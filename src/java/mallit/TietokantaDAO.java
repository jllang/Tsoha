
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
import mallit.yksilotyypit.Alue;

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

    public static Connection annaYhteys() throws SQLException {
        return yhteydet.getConnection();
    }

    /**
     * Tallentaa olion tilan tietokantaan.
     * 
     * @param tietokohde Tietokohde, jonka kenttien arvot viedään tietokantaan.
     */
    public static void vie(final Yksilotyyppi tietokohde) {
        try {
            final Connection yhteys         = annaYhteys();
            final PreparedStatement kysely  = yhteys.prepareStatement(
                    tietokohde.annaLisayskysely());
            kysely.execute();
            kysely.close();
            yhteys.close();
        } catch (SQLException e) {
            Logger.getLogger(TietokantaDAO.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public static Yksilotyyppi tuo(final String... hakusana) {
        return null;
    }
}
