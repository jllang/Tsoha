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
import mallit.java.Kayttajataso;
import mallit.java.TietokantaDAO;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
@WebServlet(name = "ProfiiliServlet", urlPatterns = {"/profiili"})
public final class ProfiiliServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
//        resp.setContentType("text/html;charset=UTF-8");
//        resp.setCharacterEncoding("UTF-8");
//        req.setCharacterEncoding("UTF-8");
        if (!Valvoja.aktiivinenIstunto(req)) {
            // Tarkastetaan aivan aluksi onko käyttäjä kirjautunut sisään,
            // jottei jouduta tekemään turhia tietokantakyselyjä:
            Uudelleenohjaaja.siirra(req, resp, "/jsp/sisaankirjaus.jsp");
            return;
        }
        Jasen kohde, katselija;
        try {
            kohde = (Jasen)TietokantaDAO.tuo(
                    Jasen.class, Integer.parseInt(req.getParameter("tunnus")));
        } catch (SQLException e) {
            Logger.getLogger(ProfiiliServlet.class.getName()).log(Level.SEVERE,
                    null, e);
            kohde = null;
        } catch (NumberFormatException e) {
            // Käyttäjä heitti urliin jotain roskaa.
            kohde = null;
        }
        if (kohde == null) {
            Uudelleenohjaaja.siirra(req, resp, "/jsp/virhesivu.jsp");
            return;
        }
        katselija = (Jasen) req.getSession().getAttribute("jasen");
        final boolean omaProfiili = kohde.equals(katselija);
        if (katselija.annaTaso() == Kayttajataso.YLLAPITAJA
                || omaProfiili) {
            req.setAttribute("profiiliotsikko", "Käyttäjän "
                    + kohde.annaKayttajatunnus() + " profiili");
        } else {
            req.setAttribute("profiiliotsikko", "Profiili");
        }
        req.setAttribute("nimimerkki", kohde.listausnimi());
        req.setAttribute("taso", kohde.annaTaso().toString().toLowerCase());
        req.setAttribute("rekisteroity", kohde.annaRekisteroity());
        req.setAttribute("viesteja", 0);
        req.setAttribute("kuvaus", kohde.annaKuvaus());
        Otsikoija.asetaOtsikko(req, omaProfiili ? "Oma sivu" : "Profiilisivu");
        Uudelleenohjaaja.siirra(req, resp, "/jsp/profiili.jsp");
    }
}
