
package mallit.yksilotyypit;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mallit.TietokantaDAO;

/**
 *
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Alue extends Yksilotyyppi {

//    public static final Alue OLIOKUMPPANI;
    private static final String LISAYSPOHJA, HAKUPOHJA;

    private final int   tunnus;
    private String      nimi, kuvaus;
    private Date        lukittu, poistettu;

    static {
//        OLIOKUMPPANI = luo("Oliokumppani", "Tätä oliota ei viedä tietokantaan.");
        LISAYSPOHJA = "insert into alueet values (?, ?, ?, ?)";
        HAKUPOHJA   = "select * from alueet where tunnus = ?";
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

//    private static Alue luo(final String nimi, final String kuvaus, final Date lukittu,
//            final Date poistettu) {
//        return new Alue(false, nimi, kuvaus, lukittu, poistettu);
//    }

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
    public static Alue luo(final ResultSet rs) {
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

//    public static Alue hae(final String nimi) {
//        final ResultSet vastaus = TietokantaDAO.hae(
//                OLIOKUMPPANI.annaHakukysely(nimi));
//        final Alue alue;
//        try {
//            vastaus.next();
//            alue = luo(vastaus);
//            vastaus.close();
//        } catch (SQLException e) {
//            Logger.getLogger(Alue.class.getName()).log(Level.SEVERE, null, e);
//            return null;
//        }
//        return alue;
//    }

    public static PreparedStatement hakukysely(final Connection yhteys,
            final int avain) throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(HAKUPOHJA);
        kysely.setInt(1, avain);
        return kysely;
    }

//    @Override
//    public void tallenna() {
//        if (this != OLIOKUMPPANI) {
//            TietokantaDAO.paivita(annaLisayskysely());
//        } else {
//            System.err.println("Oliokumppani yritettiin viedä tietokantaan!");
//        }
//    }
    @Override
    public PreparedStatement lisayskysely(final Connection yhteys)
            throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(LISAYSPOHJA);
        kysely.setString(1, nimi);
        kysely.setString(2, kuvaus);
        kysely.setDate(3, lukittu);
        kysely.setDate(4, poistettu);
        return kysely;
    }

    public List<Ketju> annaKetjut() {
        try {
            List<Ketju> paluuarvo = new LinkedList<>();
            ResultSet vastaus = TietokantaDAO.hae("select ketju_id from "
                    + "ketjujen_sijainnit where alue_id = '" + nimi + "'");
            Alue a;
            while (vastaus.next()) {
                paluuarvo.add(Ketju.luo(vastaus));
            }
            return paluuarvo;
        } catch (SQLException e) {
            Logger.getLogger(Alue.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
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
