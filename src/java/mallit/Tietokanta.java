
package mallit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Tietokanta {

//    private static final Tietokanta ILMENTYMA;
    private static DataSource       yhteydet;

    static {
//        ILMENTYMA = new Tietokanta();
        try {
            InitialContext cxt = new InitialContext();
            yhteydet = (DataSource) cxt.lookup("java:/comp/env/jdbc/tietokanta");
        } catch (NamingException e) {
            Logger.getLogger(Tietokanta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private Tietokanta() {
    }

//    public Tietokanta luo() {
//        return ILMENTYMA;
//    }

    public static Connection annaYhteys() throws SQLException {
        return yhteydet.getConnection();
    }

}
