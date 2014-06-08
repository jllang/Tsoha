
package kontrollerit;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kontrollerit.tyokalut.Otsikoija;
import kontrollerit.tyokalut.Uudelleenohjaaja;
import kontrollerit.tyokalut.Valvoja;
import mallit.java.Jasen;
import mallit.java.TietokantaDAO;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
@WebServlet(name = "IstuntoServlet", urlPatterns = {"/istunto"})
public final class IstuntoServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        kasittelePyynto(req, resp);
    }

    @Override
    protected void doPost(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        kasittelePyynto(req, resp);
    }

    private static void kasittelePyynto(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        resp.setContentType("text/html;charset=UTF-8");
        if (!Valvoja.aktiivinenIstunto(req)) {
            Otsikoija.asetaOtsikko(req, "Sisäänkirjautuminen");
            sisaankirjaus(req, resp);
        } else {
            uloskirjaus(req, resp);
        }
    }

    private static void sisaankirjaus(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        final String kayttajatunnus = req.getParameter("kayttajatunnus"),
                salasana = req.getParameter("salasana");
        if (kayttajatunnus == null || salasana == null) {
            Uudelleenohjaaja.siirra(req, resp, "jsp/sisaankirjaus.jsp");
            return;
        }
        if (kayttajatunnus.isEmpty() || salasana.isEmpty()) {
            req.setAttribute("kayttajatunnus", kayttajatunnus);
            req.setAttribute("salasana", salasana);
            if (req.getParameter("lahetetty") != null) {
                req.setAttribute("virhekoodi", 2);
            }
            Uudelleenohjaaja.siirra(req, resp, "jsp/sisaankirjaus.jsp");
            return;
        }
        Jasen jasen;
        try {
            jasen = (Jasen) TietokantaDAO.tuo(Jasen.class,
                    kayttajatunnus);
        } catch (SQLException e) {
            Logger.getLogger(IstuntoServlet.class.getName()).log(Level.SEVERE,
                    null, e);
            jasen = null;
        }
        if (jasen != null && Valvoja.autentikoi(jasen, salasana)) {
            req.getSession().setAttribute("jasen", jasen);
            Uudelleenohjaaja.uudelleenohjaa(req, resp, "etusivu");
        } else {
            req.setAttribute("virhekoodi", 1);
            req.setAttribute("kayttajatunnus", kayttajatunnus);
            req.setAttribute("salasana", salasana);
            Uudelleenohjaaja.siirra(req, resp, "jsp/sisaankirjaus.jsp");
        }
    }

    private static void uloskirjaus(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        req.getSession().removeAttribute("jasen");
        Uudelleenohjaaja.uudelleenohjaa(req, resp, "etusivu");
    }

}
