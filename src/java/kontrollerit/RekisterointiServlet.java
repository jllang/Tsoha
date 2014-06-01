/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kontrollerit;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kontrollerit.tyokalut.PasswordHash;
import kontrollerit.tyokalut.Uudelleenohjaaja;
import mallit.TietokantaDAO;
import mallit.yksilotyypit.Jasen;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
@WebServlet(name = "RekisterointiServlet", urlPatterns = {"/rekisterointi"})
public final class RekisterointiServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        final String tunnus, salasana, tiiviste, suola, sposti;
        tunnus = req.getParameter("kayttajatunnus");
        try {
            if (TietokantaDAO.tuo(Jasen.class, tunnus) != null) {
                kasitteleEpaonnistuminen(req, resp,
                        "Käyttäjätunnus on jo olemassa");
                return;
            }
        } catch (SQLException e) {
//            Logger.getLogger(RekisterointiServlet.class.getName()).log(Level.SEVERE, null, e);
            // Parempi olla speksaamatta tarkemmin random selailijoille:
            kasitteleEpaonnistuminen(req, resp, "Palvelimella tapahtui virhe");
            return;
        }
        salasana = req.getParameter("salasana");
        if (!salasana.equals(req.getParameter("salasana2"))) {
            kasitteleEpaonnistuminen(req, resp, "Salasanat eivät täsmänneet");
            return;
        }
        sposti = req.getParameter("sahkoposti");
        try {
            final String[] raakatiiviste = PasswordHash.createHash(salasana)
                    .split(":");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RekisterointiServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(RekisterointiServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void kasitteleEpaonnistuminen(final HttpServletRequest req,
            final HttpServletResponse resp, final String viesti)
            throws ServletException, IOException {
        req.setAttribute("epaonnistui", viesti);
        req.setAttribute("kayttajatunnus", req.getParameter("kayttajatunnus"));
        req.setAttribute("sahkoposti", req.getParameter("sahkoposti"));
        req.setAttribute("salasana", req.getParameter("salasana"));
//        req.setAttribute("salasana2", req.getParameter("salasana2"));
        Uudelleenohjaaja.siirra(req, resp, viesti);
    }

}
