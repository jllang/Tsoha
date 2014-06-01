
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
        resp.setContentType("text/html;charset=UTF-8");
        final String seuraavaSivu;
        if (!aktiivinenIstunto(req)) {
            seuraavaSivu = sisaankirjaus(req);
        } else {
            seuraavaSivu = uloskirjaus(req);
        }
        Uudelleenohjaaja.siirra(req, resp, seuraavaSivu);
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
    static boolean kelvollinen(final String kayttajatunnus,
            final String salasana) {
        try {
            final Jasen jasen =
                    (Jasen) TietokantaDAO.tuo(Jasen.class, kayttajatunnus);
            return PasswordHash.validatePassword(salasana,
                    PasswordHash.PBKDF2_ITERATIONS + ":"
                    + jasen.annaSuola() + ":"
                    + jasen.annaSalasanatiiviste());
        } catch (Exception e) {
            Logger.getLogger(IstuntoServlet.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    private static String sisaankirjaus(final HttpServletRequest req)
            throws ServletException, IOException {
        final String kayttajatunnus = req.getParameter("kayttajatunnus"),
                salasana = req.getParameter("salasana");
        if (!kelvollinen(kayttajatunnus, salasana)) {
            req.setAttribute("epaonnistui", true);
            req.setAttribute("kayttajatunnus", kayttajatunnus);
            req.setAttribute("salasana", salasana);
            return "istunto";
        } else {
            req.getSession().setAttribute("jasen", kayttajatunnus);
            return "etusivu";
        }
    }

    private static String uloskirjaus(final HttpServletRequest req) {
        req.getSession().removeAttribute("kayttajatunnus");
        return "etusivu";
    }

}
