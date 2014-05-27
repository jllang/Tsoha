
package mallit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import mallit.rajapinnat.Yksilotyyppi;

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

    public static void vie(final Yksilotyyppi tietokohde) {
        try {
            final Connection yhteys         = annaYhteys();
            final PreparedStatement kysely  = yhteys.prepareStatement(
                    "insert into " + tietokohde.taulunNimi() + " values "
                            + tietokohde.toString());
            kysely.execute();
            kysely.close();
            yhteys.close();
        } catch (SQLException e) {
            Logger.getLogger(TietokantaDAO.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static<T extends Yksilotyyppi> T tuo(final String avain,
            T... viiteViitteeseen) {
        if (viiteViitteeseen.length != 1) {
            throw new IllegalArgumentException("Viitteitä viitteeseen tulee olla "
                    + "täsmälleen 1!");
        }
        switch (viiteViitteeseen.getClass().getSimpleName()) {
            case "Alue[]":
                break;
            case "Jasen[]":
                break;
            case "Ketju[]":
                break;
            case "Viesti[]":
                break;
            default:
                throw new AssertionError();
        }

        return viiteViitteeseen[0];
    }
}
