/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import kontrollerit.tyokalut.Valvoja;
import mallit.java.Alue;
import mallit.java.TietokantaDAO;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
@WebServlet(name = "AlueServlet", urlPatterns = {"/alue"})
public class AlueServlet extends HttpServlet {

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

    private void kasittelePyynto(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        if (Valvoja.aktiivinenIstunto(req, resp, "/alue")) {
            final int alueenTunnus;
            try {
                alueenTunnus = Integer.parseInt(req.getParameter("tunnus"));
            } catch (NumberFormatException e) {
                Uudelleenohjaaja.siirra(req, resp, "/jsp/virhesivu.jsp");
                return;
            }
            final Alue alue = (Alue) TietokantaDAO.tuo(Alue.class, alueenTunnus);
            if (alue == null) {
                Uudelleenohjaaja.siirra(req, resp, "/jsp/virhesivu.jsp");
                return;
            }
            int sivu = 1;
            try {
                sivu = Integer.parseInt(req.getParameter("sivu"));
            } catch (NumberFormatException e) {}
            Otsikoija.asetaOtsikko(req, alue.annaNimi());
            req.setAttribute("listanNimi", alue.annaNimi());
            req.setAttribute("listanKuvaus", alue.annaKuvaus());
            req.setAttribute("ketjulista", Listaaja.listaaKetjut(10, sivu,
                    alueenTunnus));
            req.setAttribute("ketjulistanOtsikot", Listaaja.KETJUOTSIKOT);
            req.setAttribute("sivu", sivu);
            Uudelleenohjaaja.siirra(req, resp, "/jsp/alue.jsp");
        }
    }
}
