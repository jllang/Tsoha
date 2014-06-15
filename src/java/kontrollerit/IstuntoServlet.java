package kontrollerit;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import kontrollerit.tyokalut.Otsikoija;
import kontrollerit.tyokalut.Uudelleenohjaaja;
import kontrollerit.tyokalut.Valvoja;
import mallit.java.Jasen;
import mallit.java.TietokantaDAO;

/**
 *
 * @author John Lång (jllang@cs.helsinki.fi)
 */
@WebServlet(name = "IstuntoServlet", urlPatterns = {"/istunto"})
public final class IstuntoServlet extends HttpServlet {

    // Virhekoodit ovat:
    // (0 - Ei virhettä)
    // 1 -  Käyttäjätunnusta tai salasanaa ei annettu
    // 2 -  Väärä käyttäjätunnus tai salasana, mahdollinen porttikielto taikka
    //      käyttäjä on jo kirjautunut

    private static final int EI_VIRHETTA, PUUTTUVIA_KENTTIA, AUTENTIKOINTIVIRHE;

    static {
        EI_VIRHETTA         = 0;
        PUUTTUVIA_KENTTIA   = 1;
        AUTENTIKOINTIVIRHE  = 2;
    }

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
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        if (req.getSession().getAttribute("jasen") == null) {
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
        if (puutteita(kayttajatunnus, salasana, req)) {
            Uudelleenohjaaja.siirra(req, resp, "jsp/sisaankirjaus.jsp"
                    + (req.getQueryString() == null ? ""
                            : "?" + req.getQueryString()));
            return;
        }
        // Tämähän on vähän niinkuin malloc:
        Jasen jasen = (Jasen) TietokantaDAO.tuo(Jasen.class, kayttajatunnus);
        if (jasen != null && !Valvoja.onKirjautunut(jasen)
                && Valvoja.autentikoi(jasen, salasana)) {
            final String[] parametrit = req.getQueryString().split("&");
            String pyynto = jasennaPyynto(parametrit);
            HttpSession istunto = req.getSession();
            istunto.setAttribute("jasen", jasen);
            Valvoja.lisaaIstunto(jasen, istunto);
            Uudelleenohjaaja.uudelleenohjaa(req, resp, pyynto);
        } else {
            req.setAttribute("virhekoodi", AUTENTIKOINTIVIRHE);
            req.setAttribute("kayttajatunnus", kayttajatunnus);
            req.setAttribute("salasana", salasana);
            Uudelleenohjaaja.siirra(req, resp, "jsp/sisaankirjaus.jsp"
                    + (req.getQueryString() == null ? ""
                            : "?" + req.getQueryString()));
        }
    }

    private static boolean puutteita(final String kayttajatunnus,
            final String salasana, final HttpServletRequest req)
            throws IOException, ServletException {
        if (kayttajatunnus == null || salasana == null) {
            return true;
        }
        if (kayttajatunnus.isEmpty() || salasana.isEmpty()) {
            req.setAttribute("kayttajatunnus", kayttajatunnus);
            req.setAttribute("salasana", salasana);
            if (req.getParameter("lahetetty") != null) {
                req.setAttribute("virhekoodi", PUUTTUVIA_KENTTIA);
            }
            return true;
        }
        return false;
    }

    private static String jasennaPyynto(final String[] parametrit) {
        if (parametrit.length >= 2) {
            StringBuilder mjr = new StringBuilder();
            mjr.append(parametrit[0].split("=")[1]);
            mjr.append('?');
            for (int i = 1; i < parametrit.length - 1; i++) {
                mjr.append(parametrit[i]);
                mjr.append('&');
            }
            mjr.append(parametrit[parametrit.length - 1]);
            return mjr.toString();
        } else if (parametrit.length == 1) {
            return parametrit[0];
        } else {
            return "etusivu";
        }
    }

    private static void uloskirjaus(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        Valvoja.suljeIstunto((Jasen) req.getSession().getAttribute("jasen"));
        Uudelleenohjaaja.uudelleenohjaa(req, resp, "etusivu");
    }

}
