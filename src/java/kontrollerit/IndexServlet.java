
package kontrollerit;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kontrollerit.tyokalut.Listaaja;
import kontrollerit.tyokalut.Otsikoija;
import kontrollerit.tyokalut.Uudelleenohjaaja;

/**
 *
 * @author John Lång (jllang@cs.helsinki.fi)
 */
@WebServlet(name = "IndexServlet", urlPatterns = {"/etusivu"})
public final class IndexServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
//        resp.setContentType("text/html;charset=UTF-8");
//        resp.setCharacterEncoding("UTF-8");
//        req.setCharacterEncoding("UTF-8");
        Otsikoija.asetaOtsikko(req, "Etusivu");
        req.setAttribute("aluelista", Listaaja.listaa("alueet"));
        req.setAttribute("aluelistanOtsikot", Listaaja.ALUEOTSIKOT);
        req.setAttribute("ketjulista", Listaaja.listaa("tuoreet"));
        req.setAttribute("ketjulistanOtsikot", Listaaja.KETJUOTSIKOT);
        Uudelleenohjaaja.siirra(req, resp, "jsp/etusivu.jsp");
    }
}
