
package mallit.java;

import java.sql.Connection;
import java.sql.Date;
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
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Ketju extends Yksilotyyppi {

    private static final String LISAYSPOHJA, HAKUPOHJA1, HAKUPOHJA2, VIESTIHAKU;

    private final int   tunnus;
    private String      aihe;
    private Date        muutettu, siirretty, moderoitu, lukittu, poistettu;

    static {
//        OLIOKUMPPANI = luo(-1, "Oliokumppani");
        LISAYSPOHJA = "insert into ketjut values (?, ?, ?, ?, ?, ?, ?)";
        HAKUPOHJA1  = "select * from ketjut where tunnus = ?";
        HAKUPOHJA2  = "select * from ketjut where aihe = ?";
        VIESTIHAKU  = "select * from viestit where ketju_id = ?";
    }

    private Ketju(final boolean tuore, final int tunnus, final String aihe,
            final Date muutettu, final Date siirretty, final Date moderoitu,
            final Date lukittu, final Date poistettu) {
        super(tuore);
        this.tunnus     = tunnus;
        this.aihe       = aihe;
        this.muutettu   = muutettu;
        this.siirretty  = siirretty;
        this.moderoitu  = moderoitu;
        this.lukittu    = lukittu;
        this.poistettu  = poistettu;
    }

    /**
     * Luo uuden Ketju-olion palvelimen muistiin. Ketju tulee erikseen viedä
     * tietokantaan.
     *
     * @param aihe Luotavan ketjun aihe.
     * @return Uusi ketju.
     * @see TietokantaDAO#vie(mallit.java.Yksilotyyppi)
     */
    public static Ketju luo(final String aihe, final Date muutettu) {
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
        final Date      muutettu, siirretty, moderoitu, lukittu, poistettu;
        try {
            tunnus      = rs.getInt(1);
            aihe        = rs.getString(2);
            muutettu    = rs.getDate(3);
            siirretty   = rs.getDate(4);
            moderoitu   = rs.getDate(5);
            lukittu     = rs.getDate(6);
            poistettu   = rs.getDate(7);
            return new Ketju(false, tunnus, aihe, muutettu, siirretty,
                    moderoitu, lukittu, poistettu);
        } catch (SQLException e) {
            Logger.getLogger(Ketju.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    static PreparedStatement hakukysely(final Connection yhteys,
            final int tunnus) throws SQLException {
        PreparedStatement kysely = yhteys.prepareStatement(HAKUPOHJA1);
        kysely.setInt(1, tunnus);
        return kysely;
    }

    static PreparedStatement hakukysely(final Connection yhteys,
            final String tunnus) throws SQLException {
        PreparedStatement kysely = yhteys.prepareStatement(HAKUPOHJA2);
        kysely.setString(1, tunnus);
        return kysely;
    }

    @Override
    PreparedStatement lisayskysely(final Connection yhteys)
            throws SQLException {
        final PreparedStatement kysely;
        if (tunnus == 0) {
            // Tunnus on tietokannan generoima serial-tyyppinen kokonaisluku.
            // Erikoisarvo 0 tarkottaa sitä ettei tunnus ole tiedossa koska nyt
            // ollaan suorittamassa ketjun ensimmäistä vientiä tietokantaan. On
            // syytä huomata että tunnus ei päivity tässä oliossa, joten siltä
            // osin tiedot vanhentuvat.
            kysely = yhteys.prepareStatement("insert into ketjut "
                    + "(aihe, muutettu) values (?, ?)");
            kysely.setString(1, aihe);
            kysely.setDate(2, muutettu);
            return kysely;
        } else {
            kysely = yhteys.prepareStatement(LISAYSPOHJA);
            kysely.setInt(1, tunnus);
            kysely.setString(2, aihe);
            kysely.setDate(3, muutettu);
            kysely.setDate(4, siirretty);
            kysely.setDate(5, moderoitu);
            kysely.setDate(6, lukittu);
            kysely.setDate(7, poistettu);
        }
        return kysely;
    }

    public void lisaaSijainti(final Alue alue) {
        try (
            final Connection yhteys = TietokantaDAO.annaYhteys();
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

//    public static List<Ketju> tuoreimmat() throws SQLException {
//        final List<Ketju> tuoreet = new LinkedList<>();
//        final Connection yhteys = TietokantaDAO.annaYhteys();
//
//        return tuoreet;
//    }

//    public List<Alue> annaAlueet() {
//        try {
//            final List<Alue> paluuarvo = new LinkedList<>();
//            final ResultSet vastaus = TietokantaDAO.hae("select alue_id from "
//                    + "ketjujen_sijainnit where ketju_id = " + tunnus);
//            while (vastaus.next()) {
//                paluuarvo.add(Alue.luo(vastaus));
//            }
//            return paluuarvo;
//        } catch (SQLException e) {
//            Logger.getLogger(Ketju.class.getName()).log(Level.SEVERE, null, e);
//            return null;
//        }
//    }

    public List<Viesti> annaViestit() {
        List<Viesti> viestit = new LinkedList<>();
        Connection yhteys           = null;
        PreparedStatement kysely    = null;
        ResultSet vastaus           = null;
        try {
            yhteys  = TietokantaDAO.annaYhteys();
            kysely  = yhteys.prepareStatement(VIESTIHAKU);
            kysely.setInt(1, tunnus);
            vastaus = kysely.executeQuery();
            while (vastaus.next()) {
                viestit.add(Viesti.luo(vastaus));
            }
        } catch (SQLException e) {
            Logger.getLogger(Ketju.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            TietokantaDAO.suljeYhteydet(yhteys, kysely, vastaus);
        }

        return viestit;
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

    public Date annaMuutettu() {
        return muutettu;
    }

    public void asetaMuutettu(Date muutettu) {
        this.muutettu = muutettu;
    }

    public Date annaSiirretty() {
        return siirretty;
    }

    public void asetaSiirretty(final Date siirretty) {
        this.siirretty = siirretty;
    }

    public Date annaModeroitu() {
        return moderoitu;
    }

    public void asetaModeroitu(final Date moderoitu) {
        this.moderoitu = moderoitu;
    }

    public Date annaLukittu() {
        return lukittu;
    }

    public void asetaLukittu(final Date lukittu) {
        this.lukittu = lukittu;
    }

    public Date annaPoistettu() {
        return poistettu;
    }

    public void asetaPoistettu(final Date poistettu) {
        this.poistettu = poistettu;
    }
}
