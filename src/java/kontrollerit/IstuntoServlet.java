
package kontrollerit;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jdk.nashorn.internal.ir.annotations.Ignore;
import kontrollerit.tyokalut.PasswordHash;
import kontrollerit.tyokalut.Uudelleenohjaaja;
import mallit.TietokantaDAO;
import mallit.tyypit.Kayttajataso;
import mallit.yksilotyypit.Jasen;

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
        kasittelePyynto(resp, req);
    }

    @Override
    protected void doPost(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        kasittelePyynto(resp, req);
    }

    private void kasittelePyynto(final HttpServletResponse resp, final HttpServletRequest req) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
//        final String seuraavaSivu;
        if (!aktiivinenIstunto(req)) {
//            seuraavaSivu = sisaankirjaus(req);
            sisaankirjaus(req, resp);
        } else {
//            seuraavaSivu = uloskirjaus(req);
            uloskirjaus(req, resp);
        }
//        Uudelleenohjaaja.siirra(req, resp, seuraavaSivu);
    }

    /**
     * Palauttaa tosi joss annettuun pyyntöön liittyy aktiivinen istunto.
     *
     * @param req   Pyyntö
     * @return      Liittyykö pyyntöön istunto.
     */
    public static boolean aktiivinenIstunto(final HttpServletRequest req) {
        return req.getSession().getAttribute("jasen") != null;
    }

    /**
     * Palauttaa <tt>true</tt> joss annettu käyttäjätunnus on olemassa ja sillä
     * on annettu salasana. Kaikissa virhetilanteissa palautetaan arvo false.
     *
     * @param kayttajatunnus    Autentikoitava käyttäjätunnus.
     * @param salasana          Käyttäjän antama salasana.
     * @return                  Tosi joss käyttäjätunnus ja salasana täsmäävät.
     */
    @SuppressWarnings({"TooBroadCatch", "UseSpecificCatch"})
    static boolean kelvollinen(final Jasen jasen, final String salasana) {
        try {
            return PasswordHash.validatePassword(salasana,
                    PasswordHash.PBKDF2_ITERATIONS + ":"
                    + jasen.annaSuola() + ":"
                    + jasen.annaSalasanatiiviste());
        } catch (Exception e) {
            Logger.getLogger(IstuntoServlet.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    private static void sisaankirjaus(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        final String kayttajatunnus = req.getParameter("kayttajatunnus"),
                salasana = req.getParameter("salasana");
        if (kayttajatunnus == null || salasana == null) {
//            return "jsp/sisaankirjautuminen.jsp";
            Uudelleenohjaaja.siirra(req, resp, "jsp/sisaankirjautuminen.jsp");
            return;
        }
        Jasen jasen;
        try {
            jasen = (Jasen) TietokantaDAO.tuo(Jasen.class, kayttajatunnus);
        } catch (SQLException e) {
            Logger.getLogger(IstuntoServlet.class.getName()).log(Level.SEVERE, null, e);
            jasen = null;
        }
        if (jasen == null || !kelvollinen(jasen, salasana)) {
            req.setAttribute("epaonnistui", true);
            req.setAttribute("kayttajatunnus", kayttajatunnus);
            req.setAttribute("salasana", salasana);
//            return "jsp/sisaankirjautuminen.jsp";
            Uudelleenohjaaja.siirra(req, resp, "jsp/sisaankirjautuminen.jsp");
        } else {
            req.getSession().setAttribute("jasen", jasen);
            Uudelleenohjaaja.siirra(req, resp, "jsp/etusivu.jsp");
//            return "etusivu";
        }
    }

    private static void uloskirjaus(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        req.getSession().removeAttribute("jasen");
        Uudelleenohjaaja.siirra(req, resp, "/etusivu");
//        return "etusivu";
    }

}
