
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
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Ketju extends Yksilotyyppi {

//    public static final Ketju OLIOKUMPPANI;
    private static final String LISAYSPOHJA, HAKUPOHJA;

    private final int   tunnus;
    private String      aihe;
    private Date        siirretty, moderoitu, lukittu, poistettu;

    static {
//        OLIOKUMPPANI = luo(-1, "Oliokumppani");
        LISAYSPOHJA = "insert into ketjut values (?, ?, ?, ?, ?, ?)";
        HAKUPOHJA   = "select * from ketjut where tunnus = ?";
    }

    private Ketju(final boolean tuore, final int tunnus, final String aihe,
            final Date siirretty, final Date moderoitu, final Date lukittu,
            final Date poistettu) {
        super(tuore);
        this.tunnus     = tunnus;
        this.aihe       = aihe;
        this.siirretty  = siirretty;
        this.moderoitu  = moderoitu;
        this.lukittu    = lukittu;
        this.poistettu  = poistettu;
    }

//    private static Ketju luo(final int tunnus, final String aihe,
//            final Date siirretty, final Date moderoitu, final Date lukittu,
//            final Date poistettu) {
//        return new Ketju(true, tunnus, aihe, siirretty, moderoitu, lukittu, poistettu);
//    }

    public static Ketju luo(final String aihe) {
        return new Ketju(true, 0, aihe, new Date(System.currentTimeMillis()),
                null, null, null);
    }

    /**
     * Luo Ketju-olion annetusta ResultSet-oliosta, jonka kursori on asetettu
     * halutun monikon kohdalle. <b>Huom.</b> Tämä metodi ei kutsu ResultSet:n
     * metodia <tt>close()</tt>.
     *
     * @param rs Tietokantakyselyn palauttama ResultSet-olio.
     * @return Uusi Ketju.
     */
    public static Ketju luo(final ResultSet rs) {
        final int       tunnus;
        final String    aihe;
        final Date      siirretty, moderoitu, lukittu, poistettu;
        try {
            tunnus      = rs.getInt("tunnus");
            aihe        = rs.getString("aihe");
            siirretty   = rs.getDate("siirretty");
            moderoitu   = rs.getDate("moderoitu");
            lukittu     = rs.getDate("lukittu");
            poistettu   = rs.getDate("poistettu");
            return new Ketju(false, tunnus, aihe, siirretty, moderoitu, lukittu,
                    poistettu);
        } catch (SQLException e) {
            Logger.getLogger(Ketju.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

//    public static Ketju hae(final String nimi) {
//        final ResultSet vastaus = TietokantaDAO.hae(
//                OLIOKUMPPANI.annaHakukysely(nimi));
//        final Ketju ketju;
//        try {
//            vastaus.next();
//            ketju = luo(vastaus);
//            vastaus.close();
//        } catch (SQLException e) {
//            Logger.getLogger(Ketju.class.getName()).log(Level.SEVERE, null, e);
//            return null;
//        }
//        return ketju;
//    }

//    @Override
//    public void tallenna() {
//        if (this != OLIOKUMPPANI) {
//            TietokantaDAO.paivita(annaLisayskysely());
//        } else {
//            System.err.println("Oliokumppani yritettiin viedä tietokantaan!");
//        }
//    }

//    @Override
    public static PreparedStatement hakukysely(final Connection yhteys,
            final int tunnus) throws SQLException {
        PreparedStatement kysely = yhteys.prepareStatement(HAKUPOHJA);
        kysely.setInt(1, tunnus);
        return kysely;
    }

    @Override
    public PreparedStatement lisayskysely(final Connection yhteys)
            throws SQLException {
        final PreparedStatement kysely;
        final int lisays;
        if (tunnus == 0) {
            // Tunnus on tietokannan generoima serial-tyyppinen kokonaisluku.
            // Erikoisarvo 0 tarkottaa sitä ettei tunnus ole tiedossa koska nyt
            // ollaan suorittamassa ketjun ensimmäistä vientiä tietokantaan. On
            // syytä huomata että tunnus ei päivity tähässä oliossa, joten siltä
            // osin tiedot vanhentuvat.
            kysely = yhteys.prepareStatement("insert into ketjut "
                    + "(aihe, siirretty, moderoitu, lukittu poistettu)"
                    + "values (?, ?, ?, ?, ?)");
            lisays = 0;
        } else {
            kysely = yhteys.prepareStatement(LISAYSPOHJA);
            kysely.setInt(1, tunnus);
            lisays = 1;
        }
        kysely.setString(1 + lisays, aihe);
        kysely.setDate(2 + lisays, siirretty);
        kysely.setDate(3 + lisays, moderoitu);
        kysely.setDate(4 + lisays, lukittu);
        kysely.setDate(5 + lisays, poistettu);
        return kysely;
    }

    @Override
    public String toString() {
        StringBuilder mjr = new StringBuilder();
        mjr.append('(');
        mjr.append(tunnus);
        mjr.append(", '");
        mjr.append(aihe);
        mjr.append("', '");
        mjr.append(siirretty == null ? "NULL, " : "'" + siirretty + "', ");
        mjr.append(moderoitu == null ? "NULL, " : "'" + moderoitu + "', ");
        mjr.append(lukittu == null ? "NULL, " : "'" + lukittu + "', ");
        mjr.append(poistettu == null ? "NULL" : "'" + poistettu + "'");
        mjr.append(')');
        return mjr.toString();
    }

    public List<Alue> annaAlueet() {
        try {
            List<Alue> paluuarvo = new LinkedList<>();
            ResultSet vastaus = TietokantaDAO.hae("select alue_id from "
                    + "ketjujen_sijainnit where ketju_id = " + tunnus);
            Alue a;
            while (vastaus.next()) {
                paluuarvo.add(Alue.luo(vastaus));
            }
            return paluuarvo;
        } catch (SQLException e) {
            Logger.getLogger(Ketju.class.getName()).log(Level.SEVERE, null, e);
            return null;
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
