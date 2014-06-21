/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kontrollerit;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kontrollerit.tyokalut.PasswordHash;
import kontrollerit.tyokalut.Uudelleenohjaaja;
import mallit.java.Jasen;
import mallit.java.TietokantaDAO;

/**
 *
 * @author John Lång (jllang@cs.helsinki.fi)
 */
@WebServlet(name = "RekisterointiServlet", urlPatterns = {"/rekisterointi"})
public final class RekisterointiServlet extends HttpServlet {

    private static final int EI_VIRHEITA, PUUTTUVIA_KENTTIA, LIIAN_PITKA_SYOTE,
            VIRHEELLINEN_SYOTE, TUNNUS_ON_VARATTU, SALASANAVIRHE,
            SISAINEN_VIRHE;

    static {
        // Taikakoodit ovat:
        EI_VIRHEITA         = 0;
        PUUTTUVIA_KENTTIA   = 1;
        LIIAN_PITKA_SYOTE   = 2;
        VIRHEELLINEN_SYOTE  = 3;    // "Muu" virhe.
        TUNNUS_ON_VARATTU   = 4;
        SALASANAVIRHE       = 5;
        SISAINEN_VIRHE      = 6;    // Tietokanta tai tiivisteen laskenta
                                    // reistailee.
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

    private void kasittelePyynto(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        resp.setContentType("text/html;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");
        if (req.getSession().getAttribute("jasen") != null) {
            // Jo rekisteröityneet eivät saa rekisteröidä uutta tunnusta
            // (ainakaan jos ovat jo kirjautuneet sisään).
            Uudelleenohjaaja.siirra(req, resp, "/jsp/virhesivu.jsp");
            return;
        }
        if (req.getParameter("lahetetty") == null) {
            req.setAttribute("virhekoodi", EI_VIRHEITA);
            Uudelleenohjaaja.siirra(req, resp, "/jsp/rekisterointilomake.jsp");
            return;
        }
        final String tunnus, salasana1, salasana2, tiiviste, suola, sposti,
                nimimerkki, kuvaus;
        tunnus      = req.getParameter("kayttajatunnus");
        salasana1   = req.getParameter("salasana1");
        salasana2   = req.getParameter("salasana2");
        sposti      = req.getParameter("sahkoposti");
        if (tunnus == null || tunnus.isEmpty() || salasana1 == null
                || salasana1.isEmpty() || salasana2 == null
                || salasana2.isEmpty() || sposti == null || sposti.isEmpty()) {
            ohjaaLomakkeeseen(req, resp, PUUTTUVIA_KENTTIA);
            return;
        }
        if (!salasana1.equals(req.getParameter("salasana2"))) {
            ohjaaLomakkeeseen(req, resp, SALASANAVIRHE);
            return;
        }
        nimimerkki  = req.getParameter("nimimerkki");
        kuvaus      = req.getParameter("kuvaus");
        if (tunnus.length() > 32 || nimimerkki.length() > 32
                || sposti.length() > 64 || kuvaus.length() > 512) {
            ohjaaLomakkeeseen(req, resp, LIIAN_PITKA_SYOTE);
            return;
        }
        // Sovelletaanpa vähän SQL-tyylistä kolmiarvologiikkaa:
        final Boolean tunnusOnVarattu = TietokantaDAO.onOlemassa(Jasen.class,
                tunnus);
        if (tunnusOnVarattu == null) {
            // Tietokantavirhe.
            ohjaaLomakkeeseen(req, resp, SISAINEN_VIRHE);
            return;
        }
        if (tunnusOnVarattu) {
            ohjaaLomakkeeseen(req, resp, TUNNUS_ON_VARATTU);
            return;
        }
        try {
            final String[] raakatiiviste = PasswordHash.createHash(salasana1)
                    .split(":");
            tiiviste = raakatiiviste[2];
            suola = raakatiiviste[1];
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Logger.getLogger(RekisterointiServlet.class.getName()).log(
                    Level.SEVERE, null, e);
            ohjaaLomakkeeseen(req, resp, SISAINEN_VIRHE);
            return;
        }
        final Jasen uusi;
        try {
            uusi = Jasen.luo(tunnus, tiiviste, suola, sposti);
        } catch (IllegalArgumentException e) {
            ohjaaLomakkeeseen(req, resp, VIRHEELLINEN_SYOTE);
            return;
        }
        if (TietokantaDAO.vie(uusi)) {
            // En jaksa tehdä mitään erillistä sivua joka ilmoittaisi
            // rekisteröitymisen onnistumisesta...
            Uudelleenohjaaja.uudelleenohjaa(req, resp, "istunto");
        } else {
            ohjaaLomakkeeseen(req, resp, SISAINEN_VIRHE);
        }
    }

    private void ohjaaLomakkeeseen(final HttpServletRequest req,
            final HttpServletResponse resp, final int virhekoodi)
            throws ServletException, IOException {
        req.setAttribute("virhekoodi", virhekoodi);
        req.setAttribute("kayttajatunnus", req.getParameter("kayttajatunnus"));
        req.setAttribute("sahkoposti", req.getParameter("sahkoposti"));
        req.setAttribute("salasana1", req.getParameter("salasana1"));
        req.setAttribute("salasana2", req.getParameter("salasana2"));
        req.setAttribute("nimimerkki", req.getParameter("nimimerkki"));
        req.setAttribute("avatar", req.getParameter("avatar"));
        req.setAttribute("kuvaus", req.getParameter("kuvaus"));
        Uudelleenohjaaja.siirra(req, resp, "/jsp/rekisterointilomake.jsp");
    }

}
