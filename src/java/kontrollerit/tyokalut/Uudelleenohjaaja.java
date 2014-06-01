
package kontrollerit.tyokalut;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Uudelleenohjaaja {

    public static void siirra(final HttpServletRequest pyynto,
            final HttpServletResponse vastaus, final String osoite)
            throws ServletException, IOException {
        pyynto.getRequestDispatcher(osoite).forward(pyynto, vastaus);
    }

}
