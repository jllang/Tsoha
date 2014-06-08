package mallit.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Tämä luokka vastaa pääosin yksilötyyppien tuonnista ja viennistä tietokantaan.
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

    static Connection annaYhteys() throws SQLException {
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

//    public static void paivita(final String kysely, final String... argumentit) {
//
//    }

    /**
     * Suorittaa annetun SQL select-kyselyn. Metodi ei sulje yhteyksiä.
     *
     * @param hakukysely
     * @return Kyselyn vastaus. <b>Huom.</b> Ennen ensimmäisen rivin lukemista
     * tulee kutsua ResultSet-olion metodia <tt>next()</tt> ResultSet-olion
     * metodia <tt>close() </tt> tulee kutsua kun sen sisältämä data on
     * käsitelty.
     */
    static ResultSet hae(final PreparedStatement hakukysely) {
        try {
            final ResultSet vastaus = hakukysely.executeQuery();
            return vastaus;
        } catch (SQLException e) {
            Logger.getLogger(TietokantaDAO.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    public static Yksilotyyppi[] tuoSivu(final Class luokka,
            final String jarjestys, final int pituus, final int siirto)
            throws SQLException {
        final ResultSet vastaus;
        final Connection yhteys;
        final PreparedStatement kysely;
        final Yksilotyyppi[] kohteet = new Yksilotyyppi[pituus];
        int i = 0;
        switch (luokka.getSimpleName()) {
            case "Alue":
                yhteys = annaYhteys();
                kysely = yhteys.prepareStatement("select * from alueet order by"
                        + " ? limit ? offset ?;");
                kysely.setString(1, jarjestys);
                kysely.setInt(2, pituus);
                kysely.setInt(3, siirto);
                vastaus = kysely.executeQuery();
                while (vastaus.next()) {
                    kohteet[i++] = Alue.luo(vastaus);
                }
                break;
            case "Jasen":
                yhteys = annaYhteys();
                kysely = yhteys.prepareStatement("select * from jasenet order "
                        + "by ? limit ? offset ?;");
                kysely.setString(1, jarjestys);
                kysely.setInt(2, pituus);
                kysely.setInt(3, siirto);
                vastaus = kysely.executeQuery();
                while (vastaus.next()) {
                    kohteet[i++] = Jasen.luo(vastaus);
                }
                break;
            case "Ketju":
                yhteys = annaYhteys();
                kysely = yhteys.prepareStatement("select * from ketjut order "
                        + "by ? limit ? offset ?;");
                kysely.setString(1, jarjestys);
                kysely.setInt(2, pituus);
                kysely.setInt(3, siirto);
                vastaus = kysely.executeQuery();
                while (vastaus.next()) {
                    kohteet[i++] = Ketju.luo(vastaus);
                }
                break;
            case "Viesti":
                yhteys = annaYhteys();
                kysely = yhteys.prepareStatement("select * from viestit order "
                        + "by ? limit ? offset ?;");
                kysely.setString(1, jarjestys);
                kysely.setInt(2, pituus);
                kysely.setInt(3, siirto);
                vastaus = kysely.executeQuery();
                while (vastaus.next()) {
                    kohteet[i++] = Viesti.luo(vastaus);
                }
                break;
            default:
                throw new AssertionError();
        }
        suljeYhteydet(yhteys, kysely, vastaus);
        return kohteet;
    }

    /**
     * Luo ensimmäisenä parametrina annetun luokan ilmentymän. Olion tila
     * haetaan tietokannasta annetun avaimen perusteella luokkaa vastaavasta
     * relaatiosta.
     *
     * @param <T>
     * @param luokka Haettavan olion luokka.
     * @param avain Oliota vastaavan monikon avain.
     * @return Uusi
     * @throws SQLException
     * @throws AssertionError Jos syötteeksi annetaan jokin tuntematon luokka.
     */
    public static Yksilotyyppi tuo(final Class luokka, final int... avain)
            throws SQLException {
        final ResultSet vastaus;
        final Connection yhteys;
        final PreparedStatement kysely;
        final Yksilotyyppi olio;
        // Tämä koodi on aika ruman näköistä koska Javassa luokat eivät ole
        // first class -arvoja:
        switch (luokka.getSimpleName()) {
            case "Alue":
                yhteys = annaYhteys();
                kysely = Alue.hakukysely(yhteys, avain[0]);
                vastaus = kysely.executeQuery();
                vastaus.next();
                olio = Alue.luo(vastaus);
                break;
            case "Jasen":
                yhteys = annaYhteys();
                kysely = Jasen.hakukysely(yhteys, avain[0]);
                vastaus = kysely.executeQuery();
                vastaus.next();
                olio = Jasen.luo(vastaus);
                break;
            case "Ketju":
                yhteys = annaYhteys();
                kysely = Ketju.hakukysely(yhteys, avain[0]);
                vastaus = kysely.executeQuery();
                vastaus.next();
                olio = Ketju.luo(vastaus);
                break;
            case "Viesti":
                yhteys = annaYhteys();
                kysely = Viesti.hakukysely(yhteys, avain[0], avain[1]);
                vastaus = kysely.executeQuery();
                vastaus.next();
                olio = Viesti.luo(vastaus);
                break;
            default:
                System.err.println("Tietokannasta yritettiin tuoda tuntematon "
                        + "yksilötyyppi!");
                throw new AssertionError();
        }
        suljeYhteydet(yhteys, kysely, vastaus);
        return olio;
    }
    /**
     * Luo ensimmäisenä parametrina annetun luokan ilmentymän. Olion tila
     * haetaan tietokannasta annetun avaimen perusteella luokkaa vastaavasta
     * relaatiosta.
     *
     * @param <T>
     * @param luokka Haettavan olion luokka.
     * @param avain Oliota vastaavan monikon avain.
     * @return Uusi
     * @throws SQLException
     * @throws AssertionError Jos syötteeksi annetaan jokin tuntematon luokka.
     */
    public static Yksilotyyppi tuo(final Class luokka, final String... avain)
            throws SQLException {
        final ResultSet vastaus;
        final Connection yhteys;
        final PreparedStatement kysely;
        final Yksilotyyppi olio;
        // Tämä koodi on aika ruman näköistä koska Javassa luokat eivät ole
        // first class -arvoja:
        switch (luokka.getSimpleName()) {
            case "Alue":
                yhteys = annaYhteys();
                kysely = Alue.hakukysely(yhteys, avain[0]);
                vastaus = kysely.executeQuery();
                vastaus.next();
                olio = Alue.luo(vastaus);
                break;
            case "Jasen":
                yhteys = annaYhteys();
                kysely = Jasen.hakukysely(yhteys, avain[0]);
                vastaus = kysely.executeQuery();
                vastaus.next();
                olio = Jasen.luo(vastaus);
                break;
            case "Ketju":
                yhteys = annaYhteys();
                kysely = Ketju.hakukysely(yhteys, avain[0]);
                vastaus = kysely.executeQuery();
                vastaus.next();
                olio = Ketju.luo(vastaus);
                break;
            case "Viesti":
                System.err.println("Viestillä ei ole toissijaista hakukyselyä.");
            default:
                System.err.println("Tietokannasta yritettiin tuoda tuntematon "
                        + "yksilötyyppi!");
                throw new AssertionError();
        }
        suljeYhteydet(yhteys, kysely, vastaus);
        return olio;
    }

//    /**
//     * Tämä metodi hakee tietokannasta annettua käyttäjätunnusta vastaavan
//     * jäsenen.
//     *
//     * @param kayttajatunnus
//     * @return Käyttäjätunnusta vastaava Jäsen tai <tt>null</tt> jos jäsentä ei
//     * löytynyt tai tapahtui jokin virhe.
//     */
//    public static Jasen tuoJasen(final String kayttajatunnus) {
//        try (
//                final Connection yhteys = annaYhteys();
//                final PreparedStatement kysely =
//                        Jasen.toissijainenHakukysely(yhteys, kayttajatunnus);
//                final ResultSet vastaus = kysely.executeQuery();
//                ) {
//            vastaus.next();
//            return Jasen.luo(vastaus);
//        } catch (SQLException e) {
//            return null;
//        }
//    }

    /**
     * Sulkee kaikki annettujen olioiden tietokantayhteydet.
     *
     * @param yhteys
     * @param kysely
     * @param vastaus
     */
    static void suljeYhteydet(final Connection yhteys,
            final PreparedStatement kysely, final ResultSet vastaus) {
        try { vastaus.close(); } catch (SQLException e) {}
        try { kysely.close(); } catch (SQLException e) {}
        try { yhteys.close(); } catch (SQLException e) {}
    }
}
