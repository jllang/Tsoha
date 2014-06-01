package mallit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import mallit.yksilotyypit.Yksilotyyppi;
import mallit.yksilotyypit.Alue;
import mallit.yksilotyypit.Jasen;
import mallit.yksilotyypit.Ketju;
import mallit.yksilotyypit.Viesti;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class TietokantaDAO {

    private static DataSource yhteydet;

    static {
        try {
            InitialContext cxt = new InitialContext();
            yhteydet = (DataSource) cxt.lookup("java:/comp/env/jdbc/tietokanta");
        } catch (NamingException e) {
            Logger.getLogger(TietokantaDAO.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static Connection annaYhteys() throws SQLException {
        return yhteydet.getConnection();
    }

    /**
     * Tallentaa olion tilan tietokantaan.
     *
     * @param tietokohde Tietokohde, jonka kenttien arvot viedään tietokantaan.
     * @throws java.sql.SQLException
     */
    public static void vie(final Yksilotyyppi tietokohde) throws SQLException {
        final Connection yhteys = annaYhteys();
        final PreparedStatement kysely;
        if (tietokohde.onTuore()) {
            kysely = tietokohde.lisayskysely(yhteys);
        } else {
//            kysely = tietokohde.paivitysKysely(yhteys);
            kysely = null;
        }
        kysely.executeUpdate();
        kysely.close();
        yhteys.close();
        tietokohde.asetaEpatuoreeksi();
    }

    /**
     * Suorittaa annetun SQL select-kyselyn.
     *
     * @param hakukysely Merkkijonomuotoinen kysely.
     * @return Kyselyn vastaus. <b>Huom.</b> Ennen ensimmäisen rivin lukemista
     * tulee kutsua ResultSet-olion metodia <tt>next()</tt> ResultSet-olion
     * metodia <tt>close() </tt> tulee kutsua kun sen sisältämä data on
     * käsitelty.
     */
    public static ResultSet hae(final String hakukysely) {
//        if (!sqlKysely.substring(0, 6).toLowerCase().equals("select")) {
//            System.err.println("Mahdollinen SQL-injektio havaittu!");
//            throw new IllegalArgumentException("Odotettiin select-kyselyä!");
//        }
        try {
            final Connection yhteys = annaYhteys();
            final PreparedStatement kysely = yhteys.prepareStatement(hakukysely);
            final ResultSet vastaus = kysely.executeQuery();
            kysely.close();
            yhteys.close();
            return vastaus;
        } catch (SQLException e) {
            Logger.getLogger(TietokantaDAO.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    /**
     * Suorittaa annetun SQL-muokkauslyselyn.
     *
     * @param poistokysely Merkkijonomuotoinen SQL <tt>insert</tt>,
     * <tt>update</tt> tai <tt>delete</tt> -kysely.
     */
    public static void paivita(final String poistokysely) {
        try {
            final Connection yhteys = annaYhteys();
            final PreparedStatement kysely = yhteys.prepareStatement(poistokysely);
            kysely.executeUpdate();
            kysely.close();
            yhteys.close();
        } catch (SQLException e) {
            Logger.getLogger(TietokantaDAO.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Luo ensimmäisenä parametrina annetun luokan ilmentymän. Olion tila
     * haetaan tietokannasta annetun avaimen perusteella luokkaa vastaavasta
     * relaatiosta.
     *
     * @param luokka Haettavan olion luokka.
     * @param avain Oliota vastaavan monikon avain.
     * @return Uusi
     * @throws SQLException
     * @throws AssertionError Jos syötteeksi annetaan jokin tuntematon luokka.
     */
    public static <T> Yksilotyyppi tuo(final Class luokka, final T... avain)
            throws SQLException {
        final ResultSet vastaus;
        final Connection yhteys = annaYhteys();
        final PreparedStatement kysely;
        final Yksilotyyppi olio;
        // Tämä koodi on aika ruman näköistä koska Javassa luokat eivät ole
        // first class -arvoja:
        switch (luokka.getSimpleName()) {
            case "Alue":
                kysely = Alue.hakukysely(yhteys, (Integer) avain[0]);
                vastaus = kysely.executeQuery();
                olio = Alue.luo(vastaus);
                break;
            case "Jasen":
                kysely = Jasen.hakukysely(yhteys, (String) avain[0]);
                vastaus = kysely.executeQuery();
                olio = Jasen.luo(vastaus);
                break;
            case "Ketju":
                kysely = Ketju.hakukysely(yhteys, (Integer) avain[0]);
                vastaus = kysely.executeQuery();
                olio = Ketju.luo(vastaus);
                break;
            case "Viesti":
                kysely = Viesti.hakukysely(yhteys, (Integer) avain[0],
                        (Integer) avain[1]);
                vastaus = kysely.executeQuery();
                olio = Viesti.luo(vastaus);
                break;
            default:
                System.err.println("Tietokannasta yritettiin tuoda tuntematon "
                        + "yksilötyyppi!");
                throw new AssertionError();
        }
        vastaus.close();
        kysely.close();
        yhteys.close();
        return olio;
    }
}
