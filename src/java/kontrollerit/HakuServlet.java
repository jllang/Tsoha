package kontrollerit;

import java.io.IOException;
import java.util.LinkedList;
import javax.print.attribute.standard.MediaSize;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kontrollerit.tyokalut.Listaaja;
import kontrollerit.tyokalut.Otsikoija;
import kontrollerit.tyokalut.Uudelleenohjaaja;
import kontrollerit.tyokalut.Valvoja;
import kontrollerit.tyypit.ListaAlkio;

/**
 *
 * @author John Lång (jllang@cs.helsinki.fi)
 */
@WebServlet(name = "HakuServlet", urlPatterns = {"/haku"})
public final class HakuServlet extends HttpServlet {

    private static final String[] OTSIKOT;
    private static final int EI_VIRHEITA;

    static {
        OTSIKOT     = new String[] {"Kohde", "Hakutulos"};
        EI_VIRHEITA = 0;
    }

    @Override
    protected void doPost(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        kasittelePyynto(req, resp);
    }

    @Override
    protected void doGet(final HttpServletRequest req,
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
        Otsikoija.asetaOtsikko(req, "Hakusivu");
        if (Valvoja.aktiivinenIstunto(req, resp, "haku")) {
            if (req.getParameter("lahetetty") == null) {
                req.setAttribute("tuloslista", Listaaja.tyhjaLista());
                req.setAttribute("tulosotsikot", OTSIKOT);
                ohjaaLomakkeeseen(req, resp, EI_VIRHEITA);
                return;
            }
        }
    }

    private static void ohjaaLomakkeeseen(final HttpServletRequest req,
            final HttpServletResponse resp, final int virhekoodi)
            throws ServletException, IOException {
        req.setAttribute("virhekoodi", virhekoodi);
        Uudelleenohjaaja.siirra(req, resp, "/jsp/hakulomake.jsp");
    }

}
