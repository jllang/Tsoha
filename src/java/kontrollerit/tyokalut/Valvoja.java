/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kontrollerit.tyokalut;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kontrollerit.IstuntoServlet;
import mallit.java.Jasen;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Valvoja {

    /**
     * Palauttaa <tt>true</tt> joss annettuun pyyntöön liittyy aktiivinen
     * istunto. Muussa tapauksessa metodi ohjaa käyttäjän kirjautumissivulle ja
     * palauttaa <tt>false</tt>, jolloin metodia kutsuneen servletin tulee
     * lopettaa suoritus.
     *
     * @param req       Pyyntö
     * @param resp      Vastaus
     * @param toiminto  Metodia kutsuneen servletin kuuntelema URL (ilman
     *                  kauttaviivaa). Istunnon aloittamisesta vastaava servlet
     *                  ohjaa käyttäjän tähän osoitteeseen kun kirjautuminen on
     *                  suoritettu.
     * @return      Liittyykö pyyntöön istunto.
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public static boolean aktiivinenIstunto(final HttpServletRequest req,
            HttpServletResponse resp, final String toiminto)
            throws ServletException, IOException {
        if (req.getSession().getAttribute("jasen") == null) {
            Uudelleenohjaaja.siirra(req, resp, "/istunto?toiminto=" + toiminto
                    + "&" + req.getQueryString());
            return false;
        }
        return true;
    }

    /**
     * Palauttaa <tt>true</tt> joss annettu käyttäjätunnus on olemassa ja sillä
     * on annettu salasana. Kaikissa virhetilanteissa palautetaan arvo false.
     *
     * @param jasen             Autentikoitava käyttäjätunnus.
     * @param salasana          Käyttäjän antama salasana.
     * @return                  Tosi joss käyttäjätunnus ja salasana täsmäävät.
     */
    @SuppressWarnings({"TooBroadCatch", "UseSpecificCatch"})
    public static boolean autentikoi(final Jasen jasen, final String salasana) {
        try {
            return !jasen.onPorttikiellossa()
                    && PasswordHash.validatePassword(salasana,
                    PasswordHash.PBKDF2_ITERATIONS + ":"
                    + jasen.annaSuola() + ":"
                    + jasen.annaSalasanatiiviste());
        } catch (Exception e) {
            Logger.getLogger(IstuntoServlet.class.getName()).log(Level.SEVERE,
                    null, e);
            return false;
        }
    }

}
