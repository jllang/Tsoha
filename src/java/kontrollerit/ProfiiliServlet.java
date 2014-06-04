package kontrollerit;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kontrollerit.tyokalut.Uudelleenohjaaja;
import mallit.TietokantaDAO;
import mallit.yksilotyypit.Jasen;


/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
//@WebServlet(name = "ProfiiliServlet", urlPatterns = {"/kayttaja"})
public class ProfiiliServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        Jasen jasen;
        try {
            jasen = (Jasen)TietokantaDAO.tuo(
                    Jasen.class, req.getParameter("jasen"));
        } catch (SQLException e) {
            Logger.getLogger(ProfiiliServlet.class.getName()).log(Level.SEVERE,
                    null, e);
            jasen = null;
        }
        Uudelleenohjaaja.uudelleenohjaa(req, resp, "/jsp/profiili.jsp");
    }
}
