
package kontrollerit;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kontrollerit.tyokalut.PasswordHash;
import kontrollerit.tyokalut.Uudelleenohjaaja;
import mallit.tyypit.Kayttajataso;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
@WebServlet(name = "IndexServlet", urlPatterns = {"/etusivu"})
public final class IndexServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        resp.setContentType("text/html;charset=UTF-8");
        Uudelleenohjaaja.siirra(req, resp, "jsp/etusivu.jsp");
    }
}
