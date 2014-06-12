
package kontrollerit;

import java.io.IOException;
import java.sql.Timestamp;
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
        if (Valvoja.aktiivinenIstunto(req, resp, "viesti")) {
            int ketjunTunnus = 0;
            try {
                ketjunTunnus = Integer.parseInt(req.getParameter("ketju"));
            } catch (NumberFormatException e) {}
            if (ketjunTunnus == 0) {
                uusiKetju(req, resp);
            } else {
                ketjunTaydennys(req, resp, ketjunTunnus);
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
                Uudelleenohjaaja.siirra(req, resp, "/jsp/viestilomake.jsp?"
                        + req.getQueryString());
                return;
            }
            final int ketjunTunnus = TietokantaDAO.luoKetju((Jasen)
                    req.getAttribute("jasen"), aihe, sisalto, valitutAlueet);
            if (ketjunTunnus == 0) {
                req.setAttribute("virhekoodi", 2);
                req.setAttribute("aihe", aihe);
                req.setAttribute("alueet", valitutAlueet);
                req.setAttribute("sisalto", sisalto);
                req.setAttribute("epaonnistui", true);
                Uudelleenohjaaja.siirra(req, resp, "/jsp/viestilomake.jsp?"
                        + req.getQueryString());
            }
            Uudelleenohjaaja.uudelleenohjaa(req, resp, "ketju?tunnus="
                    + ketjunTunnus + "&sivu=1");
        } else {
            req.setAttribute("aihe", aihe);
            req.setAttribute("alueet", valitutAlueet);
            req.setAttribute("sisalto", sisalto);
            req.setAttribute("virhekoodi", req.getAttribute("lahetetty")
                    != null ? 1 : 0);
            Uudelleenohjaaja.siirra(req, resp, "/jsp/viestilomake.jsp?"
                        + req.getQueryString());
        }
    }

    private static void ketjunTaydennys(final HttpServletRequest req,
            final HttpServletResponse resp, final int ketjunTunnus)
            throws ServletException, IOException {
        int viestinTunnus = 0;
        try {
            viestinTunnus = Integer.parseInt(req.getParameter("viesti"));
        } catch (NumberFormatException e) {}
        if (viestinTunnus == 0) {
            uusiViesti(req, resp, ketjunTunnus);
        } else {
            viestinMuokkaus(req, resp, ketjunTunnus, viestinTunnus);
        }
    }

    private static void uusiViesti(final HttpServletRequest req,
            final HttpServletResponse resp, final int ketjunTunnus)
            throws ServletException, IOException {
        final Ketju ketju = (Ketju) TietokantaDAO.tuo(Ketju.class, ketjunTunnus);
        req.setAttribute("aihe", ketju.annaAihe());
        req.setAttribute("aluelista", ketju.annaAlueidenNimet());
        req.setAttribute("muokattavuus", "disabled=\"disabled\"");
        final String sisalto = req.getParameter("sisalto");
        if (sisalto != null && !sisalto.isEmpty()) {
            final int numero = ketju.annaViimeisenViestinNumero() + 1;
            final Timestamp aikaleima = new Timestamp(System.currentTimeMillis());
            final Jasen jasen = (Jasen) req.getSession().getAttribute("jasen");
            final Viesti viesti = Viesti.luo(ketjunTunnus, numero,
                    jasen.annaKayttajanumero(), aikaleima, sisalto);
            ketju.asetaMuutettu(aikaleima);
            jasen.kasvataViesteja();
            TietokantaDAO.vie(viesti, ketju, jasen);
            Uudelleenohjaaja.siirra(req, resp, "/jsp/ketju.jsp?tunnus="
                    + ketjunTunnus +"&sivu=1");
        } else {
            req.setAttribute("sisalto", sisalto);
            req.setAttribute("epaonnistui", req.getAttribute("lahetetty")
                    != null);
            Uudelleenohjaaja.siirra(req, resp, "/jsp/viestilomake.jsp?"
                        + req.getQueryString());
        }
    }

    private static void viestinMuokkaus(final HttpServletRequest req,
            final HttpServletResponse resp, final int ketjunTunnus,
            final int viestinTunnus) {
        if (viestinTunnus == 1) {

        } else {
            req.setAttribute("ketjunMuokattavuus", "disabled=\"disabled\"");
        }
    }
}
