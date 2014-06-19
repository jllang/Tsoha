
package mallit.java;

import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Mallintaa viestiketjua.
 *
 * @author John Lång (jllang@cs.helsinki.fi)
 */
public final class Ketju extends Yksilotyyppi {

    private static final String LISAYSLAUSE, PAIVITYSLAUSE, TUNNUSHAKU,
            AIHEHAKU, VIESTIHAKU, VIESTIMAARAN_HAKU, VIESTINUMERON_HAKU,
            ALUEHAKU, ALOITTAJAHAKU, KIRJOITTAJAHAKU, LUKUMAARALAUSE;

    private final int       tunnus;
    private String          aihe;
    private Timestamp       muutettu, siirretty, moderoitu, lukittu, poistettu;

    // Seuraavia kenttiä pidetään vain muistissa kyselytulvan vähentämiseksi:
    private List<Alue>      alueet;
//    private List<Viesti>    viestit;
//    private List<Jasen>     kirjoittajat;
    private String          aloittajanListausnimi;
    private int             aloittajanNumero;
    private Kayttajataso    aloittajanTaso;

    static {
        LISAYSLAUSE     = "insert into ketjut (aihe, muutettu) values (?, ?)";
        PAIVITYSLAUSE   = "update ketjut set aihe = ?, muutettu = ?, "
                + "siirretty = ?, moderoitu = ?, lukittu = ?, poistettu = ? "
                + "where tunnus = ?";
        TUNNUSHAKU      = "select * from ketjut where tunnus = ?";
        AIHEHAKU        = "select * from ketjut where aihe = ?";
        VIESTIHAKU      = "select * from viestit where ketju_id = ? and "
                + "viestit.poistettu is null order by numero asc limit ? "
                + "offset ?";
        VIESTIMAARAN_HAKU = "select count(*) from viestit where ketju_id = ? "
                + "and viestit.poistettu is null";
        VIESTINUMERON_HAKU = "select max(numero) from viestit where ketju_id = "
                + "?";
        ALUEHAKU        = "select alueet.tunnus, alueet.nimi, alueet.kuvaus, "
                + "alueet.lukittu, alueet.poistettu from ketjut join "
                + "ketjujen_sijainnit on ketjut.tunnus = ketju_id join alueet "
                + "on alueet.tunnus = alue_id where ketjut.tunnus = ?";
        ALOITTAJAHAKU   = "select jasenet.numero, nimimerkki, jasenet.tunnus, "
                + "taso from ketjut join viestit on ketjut.tunnus = ketju_id "
                + "and viestit.numero = 1 join jasenet on kirjoittaja = "
                + "jasenet.numero where ketjut.tunnus = ?";
        KIRJOITTAJAHAKU = "select jasenet.numero, jasenet.tunnus, "
                + "jasenet.rekisteroity, jasenet.tiiviste, jasenet.suola, "
                + "jasenet.sposti, jasenet.taso, jasenet.nimimerkki, "
                + "jasenet.avatar, jasenet.kuvaus, jasenet.viesteja from ketjut"
                + " join viestit on ketjut.tunnus = ketju_id  join jasenet on "
                + "kirjoittaja = jasenet.numero where ketjut.tunnus = ? order "
                + "by viestit.numero asc limit ? offset ?";
        LUKUMAARALAUSE  = "select count(tunnus) from ketjut";
    }

    private Ketju(final boolean tuore, final int tunnus, final String aihe,
            final Timestamp muutettu, final Timestamp siirretty,
            final Timestamp moderoitu, final Timestamp lukittu,
            final Timestamp poistettu) {
        super(tuore);
        this.tunnus     = tunnus;
        this.aihe       = aihe;
        this.muutettu   = muutettu;
        this.siirretty  = siirretty;
        this.moderoitu  = moderoitu;
        this.lukittu    = lukittu;
        this.poistettu  = poistettu;
        this.aloittajanNumero = 0;
        this.aloittajanListausnimi = null;
        this.aloittajanTaso = null;
        this.alueet     = null;
    }

    /**
     * Luo uuden Ketju-olion palvelimen muistiin. Ketju tulee erikseen viedä
     * tietokantaan.
     *
     * @param aihe Luotavan ketjun aihe.
     * @param muutettu Ketjulle kirjattava luomisajankohta.
     * @return Uusi ketju.
     */
    public static Ketju luo(final String aihe, final Timestamp muutettu) {
        if (aihe == null || aihe.isEmpty()) {
            throw new IllegalArgumentException("Ketjulla pitää olla aihe.");
        }
        if (muutettu == null) {
            throw new IllegalArgumentException("Aikaleima puuttuu.");
        }
        return new Ketju(true, 0, aihe, muutettu, null, null, null, null);
    }

    /**
     * Luo Ketju-olion annetusta ResultSet-oliosta, jonka kursori on asetettu
     * halutun monikon kohdalle. <b>Huom.</b> Tämä metodi ei kutsu ResultSet:n
     * metodia <tt>close()</tt>.
     *
     * @param rs Tietokantakyselyn palauttama ResultSet-olio.
     * @return Uusi Ketju.
     */
    static Ketju luo(final ResultSet rs) {
        final int       tunnus;
        final String    aihe;
        final Timestamp      muutettu, siirretty, moderoitu, lukittu, poistettu;
        try {
            tunnus      = rs.getInt(1);
            aihe        = rs.getString(2);
            muutettu    = rs.getTimestamp(3);
            siirretty   = rs.getTimestamp(4);
            moderoitu   = rs.getTimestamp(5);
            lukittu     = rs.getTimestamp(6);
            poistettu   = rs.getTimestamp(7);
            return new Ketju(false, tunnus, aihe, muutettu, siirretty,
                    moderoitu, lukittu, poistettu);
        } catch (SQLException e) {
            Logger.getLogger(Ketju.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    static PreparedStatement hakukysely(final Connection yhteys,
            final int tunnus) throws SQLException {
        PreparedStatement kysely = yhteys.prepareStatement(TUNNUSHAKU);
        kysely.setInt(1, tunnus);
        return kysely;
    }

    static PreparedStatement hakukysely(final Connection yhteys,
            final String tunnus) throws SQLException {
        PreparedStatement kysely = yhteys.prepareStatement(AIHEHAKU);
        kysely.setString(1, tunnus);
        return kysely;
    }

    public static int lukumaara() {
        Connection yhteys           = null;
        PreparedStatement kysely    = null;
        ResultSet vastaus           = null;
        int lukumaara               = 0;
        try {
            yhteys = TietokantaDAO.annaKertayhteys();
            kysely = yhteys.prepareStatement(LUKUMAARALAUSE);
            vastaus = kysely.executeQuery();
            vastaus.next();
            lukumaara = vastaus.getInt(1);
        } catch (SQLException e) {

        } finally {
            TietokantaDAO.sulje(yhteys, kysely, vastaus);
        }
        return lukumaara;
    }

    @Override
    String annaLisayslause() {
        return LISAYSLAUSE;
    }

    @Override
    String annaPaivityslause() {
        return PAIVITYSLAUSE;
    }

    @Override
    void valmisteleLisays(final PreparedStatement kysely) throws SQLException {
        kysely.setString(1, aihe);
        kysely.setTimestamp(2, muutettu);
    }

    @Override
    void valmisteleUpdate(final PreparedStatement kysely) throws SQLException {
        kysely.setString(1, aihe);
        kysely.setTimestamp(2, muutettu);
        kysely.setTimestamp(3, siirretty);
        kysely.setTimestamp(4, moderoitu);
        kysely.setTimestamp(5, lukittu);
        kysely.setTimestamp(6, poistettu);
        kysely.setInt(7, tunnus);
    }

    public void lisaaSijainti(final Alue alue) {
        try (
            final Connection yhteys = TietokantaDAO.annaKertayhteys();
            final PreparedStatement kysely = yhteys.prepareStatement("insert "
                    + "into ketjujen sijainnit values (?, ?)");
                ) {
            kysely.setInt(1, alue.annaTunnus());
            kysely.setInt(2, tunnus);
            kysely.execute();
        } catch (SQLException e) {
        }
    }

    @Override
    public String listausnimi() {
        return aihe;
    }

    public List<Alue> annaAlueet() {
        if (alueet == null) {
            Connection yhteys = null;
            PreparedStatement kysely = null;
            ResultSet vastaus = null;
            try {
                yhteys = TietokantaDAO.annaKertayhteys();
                kysely = yhteys.prepareStatement(ALUEHAKU);
                kysely.setInt(1, tunnus);
                vastaus = kysely.executeQuery();
                alueet  = new LinkedList<>();
                while (vastaus.next()) {
                    alueet.add(Alue.luo(vastaus));
                }
            } catch (SQLException e) {
                Logger.getLogger(Ketju.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                TietokantaDAO.sulje(yhteys, kysely, vastaus);
            }
        }
        return alueet;
    }

    /**
     * Palauttaa listan niistä ketjun viesteistä, joita ei ole merkitty
     * poistetuiksi.
     *
     * @param pituus
     * @param siirto
     * @return
     */
    public List<Viesti> annaViestit(final int pituus, final int siirto) {
        List<Viesti> viestit        = null;
        Connection yhteys           = null;
        PreparedStatement kysely    = null;
        ResultSet vastaus           = null;
        try {
            yhteys  = TietokantaDAO.annaKertayhteys();
            kysely  = yhteys.prepareStatement(VIESTIHAKU);
            kysely.setInt(1, tunnus);
            kysely.setInt(2, pituus);
            kysely.setInt(3, siirto);
            vastaus = kysely.executeQuery();
            viestit = new LinkedList<>();
            while (vastaus.next()) {
                viestit.add(Viesti.luo(vastaus));
            }
        } catch (SQLException e) {
            Logger.getLogger(Ketju.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            TietokantaDAO.sulje(yhteys, kysely, vastaus);
        }
        return viestit;
    }

    /**
     * Palauttaa ketjun viestien määrän. Määrään ei lasketa mukaan poistetuksi
     * merkittyjä viestejä.
     *
     * @return Ketjun viestien määrä tai -1 virhetilanteissa.
     */
    public int annaViestienMaara() {
        Connection yhteys           = null;
        PreparedStatement kysely    = null;
        ResultSet vastaus           = null;
        int viesteja                = -1;
        try {
            yhteys  = TietokantaDAO.annaKertayhteys();
            kysely  = yhteys.prepareStatement(VIESTIMAARAN_HAKU);
            kysely.setInt(1, tunnus);
            vastaus = kysely.executeQuery();
            vastaus.next();
            viesteja = vastaus.getInt(1);
        } catch (SQLException e) {
            Logger.getLogger(Ketju.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            TietokantaDAO.sulje(yhteys, kysely, vastaus);
        }
        return viesteja;
    }

    public List<Jasen> annaKirjoittajat(final int pituus, final int siirto) {
        List<Jasen> kirjoittajat    = null;
        Connection yhteys           = null;
        PreparedStatement kysely    = null;
        ResultSet vastaus           = null;
        try {
            yhteys = TietokantaDAO.annaKertayhteys();
            kysely = yhteys.prepareStatement(KIRJOITTAJAHAKU);
            kysely.setInt(1, tunnus);
            kysely.setInt(2, pituus);
            kysely.setInt(3, siirto);
            vastaus = kysely.executeQuery();
            kirjoittajat = new LinkedList<>();
            while (vastaus.next()) {
                kirjoittajat.add(Jasen.luo(vastaus));
            }
        } catch (SQLException e) {
            Logger.getLogger(Ketju.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            TietokantaDAO.sulje(yhteys, kysely, vastaus);
        }
        return kirjoittajat;
    }

    public int annaViimeisenViestinNumero() {
        int numero = 0;
        Connection yhteys           = null;
        PreparedStatement kysely    = null;
        ResultSet vastaus           = null;
        try {
            yhteys = TietokantaDAO.annaKertayhteys();
            kysely = yhteys.prepareStatement(VIESTINUMERON_HAKU);
            kysely.setInt(1, tunnus);
            vastaus = kysely.executeQuery();
            vastaus.next();
            numero = vastaus.getInt(1);
        } catch (SQLException e) {
            Logger.getLogger(Ketju.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            TietokantaDAO.sulje(yhteys, kysely, vastaus);
        }
        return numero;
    }

    public int annaAloittajaNumero() {
        if (aloittajanNumero == 0) {
            haeAloittaja();
        }
        return aloittajanNumero;
    }

    public String annaAloittajanListausnimi() {
        if (aloittajanListausnimi == null) {
            haeAloittaja();
        }
        return aloittajanListausnimi;
    }

    public Kayttajataso annaAloittajanTaso() {
        if (aloittajanTaso == null) {
            haeAloittaja();
        }
        return aloittajanTaso;
    }

    private void haeAloittaja() {
        Connection yhteys = null;
        PreparedStatement kysely = null;
        ResultSet vastaus = null;
        try {
            yhteys = TietokantaDAO.annaKertayhteys();
            kysely = yhteys.prepareStatement(ALOITTAJAHAKU);
            kysely.setInt(1, tunnus);
            vastaus = kysely.executeQuery();
            vastaus.next();
            aloittajanNumero = vastaus.getInt(1);
            aloittajanListausnimi = vastaus.getString(2);
            if (aloittajanListausnimi == null) {
                // Jos nimimerkki oli tyhjä, käytetään käyttäjätunnusta (aivan
                // kuten jäsenen listausNimi-metodissa):
                aloittajanListausnimi = vastaus.getString(3);
            }
            aloittajanTaso = Kayttajataso.valueOf(vastaus.getString(4));
        } catch (SQLException e) {
            Logger.getLogger(Ketju.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            TietokantaDAO.sulje(yhteys, kysely, vastaus);
        }

    }

    public int annaTunnus() {
        return tunnus;
    }

    public String annaAihe() {
        return aihe;
    }

    public void asetaAihe(final String aihe) {
        this.aihe = aihe;
    }

    public Timestamp annaMuutettu() {
        return muutettu;
    }

    public void asetaMuutettu(Timestamp muutettu) {
        this.muutettu = muutettu;
    }

    public Timestamp annaSiirretty() {
        return siirretty;
    }

    public void asetaSiirretty(final Timestamp siirretty) {
        this.siirretty = siirretty;
    }

    public Timestamp annaModeroitu() {
        return moderoitu;
    }

    public void asetaModeroitu(final Timestamp moderoitu) {
        this.moderoitu = moderoitu;
    }

    public Timestamp annaLukittu() {
        return lukittu;
    }

    public void asetaLukittu(final Timestamp lukittu) {
        this.lukittu = lukittu;
    }

    public Timestamp annaPoistettu() {
        return poistettu;
    }

    public void asetaPoistettu(final Timestamp poistettu) {
        this.poistettu = poistettu;
    }
}
