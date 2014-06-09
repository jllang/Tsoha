package kontrollerit;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
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
import mallit.java.Ketju;
import mallit.java.TietokantaDAO;
import mallit.java.Viesti;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
@WebServlet(name = "KetjuServlet", urlPatterns = {"/ketju"})
public final class KetjuServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
//        resp.setContentType("text/html;charset=UTF-8");
//        resp.setCharacterEncoding("UTF-8");
//        req.setCharacterEncoding("UTF-8");
        if (!Valvoja.aktiivinenIstunto(req)) {
            Uudelleenohjaaja.siirra(req, resp, "/jsp/sisaankirjaus.jsp");
        } else {
            final Ketju ketju;
            final int sivu;
            try {
                final int tunnus = Integer.parseInt(req.getParameter("tunnus"));
                sivu = Integer.parseInt(req.getParameter("sivu"));
                ketju = (Ketju) TietokantaDAO.tuo(Ketju.class, tunnus);
            } catch (NumberFormatException e) {
                Uudelleenohjaaja.siirra(req, resp, "/jsp/virhesivu.jsp");
                return;
            } catch (SQLException e) {
                Logger.getLogger(KetjuServlet.class.getName())
                        .log(Level.SEVERE, null, e);
                Uudelleenohjaaja.siirra(req, resp, "/jsp/virhesivu.jsp");
                return;
            }
            final List<Viesti> viestit = ketju.annaViestit(10, (sivu - 1 * 10));
            Otsikoija.asetaOtsikko(req, ketju.annaAihe());
            // TODO: pistä viestien kirjoittajien nimet johonkin tauluun, jotta
            // niitä pääsee iteroimaan ketju.jsp:ssä viestien rinnalla.
            req.setAttribute("aihe", ketju.annaAihe());
            req.setAttribute("alueet", ketju.annaAlueidenNimet());
            req.setAttribute("viestit", viestit);
            Uudelleenohjaaja.siirra(req, resp, "/jsp/ketju.jsp");
        }
    }
}
