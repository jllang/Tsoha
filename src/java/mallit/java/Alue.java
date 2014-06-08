
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
 * Mallintaa keskustelualuetta foorumilla.
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Alue extends Yksilotyyppi {

    private static final String LISAYSPOHJA, HAKUPOHJA1, HAKUPOHJA2;

    private final int   tunnus;
    private String      nimi, kuvaus;
    private Date        lukittu, poistettu;

    static {
        LISAYSPOHJA = "insert into alueet values (?, ?, ?, ?, ?)";
        HAKUPOHJA1  = "select * from alueet where tunnus = ?";
        HAKUPOHJA2  = "select * from alueet where nimi = ?";
    }

    private Alue(final boolean tuore, final int tunnus, final String nimi,
            final String kuvaus, final Date lukittu, final Date poistettu) {
        super(tuore);
        this.tunnus     = tunnus;
        this.nimi       = nimi;
        this.kuvaus     = kuvaus;
        this.lukittu    = lukittu;
        this.poistettu  = poistettu;
    }

    public static Alue luo(final String nimi, final String kuvaus) {
        if (nimi == null || nimi.length() == 0 || nimi.length() > 128) {
            throw new IllegalArgumentException("Alueen nimen tulee olla "
                    + "epätyhjä korkeintaan 128 merkin jono.");
        }
        if (kuvaus != null && kuvaus.length() > 384) {
            throw new IllegalArgumentException("Alueen kuvaus saa olla "
                    + "korkeintaan 384 merkin jono.");
        }
        return new Alue(true, 0, nimi, kuvaus, null, null);
    }

    /**
     * Luo uuden Alue-olion annetusta ResultSet-oliosta, jonka kursori on
     * asetettu halutun monikon kohdalle. <b>Huom.</b> Tämä metodi ei kutsu
     * ResultSet:n metodia <tt>close()</tt>.
     *
     * @param rs Tietokantakyselyn palauttama ResultSet-olio.
     * @return Uusi Alue.
     */
    static Alue luo(final ResultSet rs) {
        final int       tunnus;
        final String    nimi, kuvaus;
        final Date      lukittu, poistettu;
        try {
            tunnus      = rs.getInt(1);
            nimi        = rs.getString(2);
            kuvaus      = rs.getString(3);
            lukittu     = rs.getDate(4);
            poistettu   = rs.getDate(5);
            return new Alue(false, tunnus, nimi, kuvaus, lukittu, poistettu);
        } catch (SQLException e) {
            Logger.getLogger(Alue.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    static PreparedStatement hakukysely(final Connection yhteys,
            final int avain) throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(HAKUPOHJA1);
        kysely.setInt(1, avain);
        return kysely;
    }

    static PreparedStatement hakukysely(final Connection yhteys,
            final String avain) throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(HAKUPOHJA2);
        kysely.setString(1, avain);
        return kysely;
    }

    public static List<String> annaNimet() {
        try (
            final Connection yhteys = TietokantaDAO.annaYhteys();
            final PreparedStatement kysely = yhteys.prepareStatement("select "
                    + "nimi from alueet");
            final ResultSet vastaus = kysely.executeQuery();
                ) {
            List<String> alueidenNimet = new LinkedList<>();
            while (vastaus.next()) {
                alueidenNimet.add(vastaus.getString(1));
            }
            return alueidenNimet;
        } catch (SQLException e) {
            Logger.getLogger(Alue.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    public List<Ketju> annaKetjut(final int sivunPituus, final int siirto)
            throws SQLException {
        final List<Ketju> paluuarvo = new LinkedList<>();
        final Connection yhteys = TietokantaDAO.annaYhteys();
        final PreparedStatement kysely = yhteys.prepareStatement("select "
                + "ketju.tunnus, aihe, muutettu, siirretty, moderoitu, "
                + "ketju.lukittu, ketju.poistettu from alueet inner join "
                + "ketjujen_sijainnit on alue.tunnus = alue_id inner join "
                + "ketjut on ketju_id = ketju.tunnus where alue.tunnus = ? "
                + "order by ? limit ? offset ?");
        kysely.setInt(1, tunnus);
        kysely.setString(2, "muutettu");
        kysely.setInt(3, sivunPituus);
        kysely.setInt(4, siirto);
        final ResultSet vastaus = kysely.executeQuery();
        while (vastaus.next()) {
            paluuarvo.add(Ketju.luo(vastaus));
        }
        TietokantaDAO.suljeYhteydet(yhteys, kysely, vastaus);
        return paluuarvo;
    }

//    public void lisaaKetju(final Ketju ketju) {
//        final Connection yhteys = TietokantaDAO.annaYhteys();
//        final PreparedStatement kysely = yhteys.prepareStatement("insert "
//                + "into ketjujen_sijainnit values (?, ?)");
//        kysely.setInt(1, tunnus);
//        kysely.setInt(2, ketju.annaTunnus());
//        final ResultSet vastaus = kysely.executeQuery();
//    }

    @Override
    PreparedStatement lisayskysely(final Connection yhteys)
            throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(LISAYSPOHJA);
        kysely.setString(1, nimi);
        kysely.setString(2, kuvaus);
        kysely.setDate(3, lukittu);
        kysely.setDate(4, poistettu);
        return kysely;
    }

    @Override
    public String listausnimi() {
        return nimi;
    }

    public int annaTunnus() {
        return tunnus;
    }

    public String annaNimi() {
        return nimi;
    }

    public void asetaNimi(final String nimi) {
        this.nimi = nimi;
    }

    public String annaKuvaus() {
        return kuvaus;
    }

    public void asetaKuvaus(final String kuvaus) {
        this.kuvaus = kuvaus;
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
