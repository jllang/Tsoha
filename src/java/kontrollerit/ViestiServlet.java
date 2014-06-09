
package kontrollerit;

import java.io.IOException;
import java.sql.Date;
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
import mallit.java.Alue;
import mallit.java.Jasen;
import mallit.java.Ketju;
import mallit.java.TietokantaDAO;
import mallit.java.Transaktio;
import mallit.java.Viesti;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
@WebServlet(name = "ViestiServlet", urlPatterns = {"/viesti"})
public final class ViestiServlet extends HttpServlet {

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
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");
        if (!Valvoja.aktiivinenIstunto(req)) {
            Uudelleenohjaaja.siirra(req, resp, "/jsp/sisaankirjaus.jsp");
        } else {
            final String toiminto = req.getAttribute("toiminto") == null
                    ? "uusi" : ((String) req.getAttribute("toiminto")).trim()
                            .toLowerCase();
            switch (toiminto) {
                case "uusi":
                    uusiKetju(req, resp);
                    break;
                default:
//                    throw new AssertionError();
                    Uudelleenohjaaja.uudelleenohjaa(req, resp,
                            "/jsp/virhesivu.jsp");
            }
        }
    }

    private static void uusiKetju(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        Otsikoija.asetaOtsikko(req, "Uusi ketju");
        if (req.getAttribute("aluelista") == null) {
            req.setAttribute("aluelista", Alue.annaNimet());
        }
        // Kyllä, tarkastetaan kahteen kertaan koska tietokantahaussa voi tulla
        // virhe:
        if (req.getAttribute("aluelista") == null) {
            Uudelleenohjaaja.uudelleenohjaa(req, resp, "/jsp/virhesivu.jsp");
            return;
        }
        final String aihe   = (String) req.getParameter("aihe"),
                sisalto     = (String) req.getParameter("sisalto");
        final String[] valitutAlueet = req.getParameterValues("alueet");
        if (aihe != null && valitutAlueet != null && sisalto != null) {
            if (aihe.isEmpty() || valitutAlueet.length == 0
                    || sisalto.isEmpty()) {
                req.setAttribute("aihe", aihe);
                req.setAttribute("alueet", valitutAlueet);
                req.setAttribute("sisalto", sisalto);
                req.setAttribute("epaonnistui", true);
                Uudelleenohjaaja.siirra(req, resp, "/jsp/uusiketju.jsp");
                return;
            }
            final int[] alueet = Transaktio.haeAlueidenTunnukset(valitutAlueet);
            Jasen kirjoittaja = (Jasen) req.getSession().getAttribute("jasen");
            final int ketjunTunnus =
                    Transaktio.luoKetju(kirjoittaja, aihe,sisalto, alueet);
            // Miksi relatiivinen URL ilman kovakoodattua Context dirriä ei
            // toimi tässä?
            Uudelleenohjaaja.uudelleenohjaa(req, resp, "/Tsoha/ketju?tunnus="
                    + ketjunTunnus + "&sivu=1");
        } else {
            req.setAttribute("aihe", aihe);
            req.setAttribute("alueet", valitutAlueet);
            req.setAttribute("sisalto", sisalto);
            req.setAttribute("epaonnistui", req.getAttribute("lahetetty")
                    != null);
            Uudelleenohjaaja.siirra(req, resp, "/jsp/uusiketju.jsp");
        }
    }
}
