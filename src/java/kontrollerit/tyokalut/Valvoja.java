/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kontrollerit.tyokalut;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import kontrollerit.IstuntoServlet;
import mallit.java.Jasen;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Valvoja {

    /**
     * Palauttaa tosi joss annettuun pyyntöön liittyy aktiivinen istunto.
     *
     * @param req   Pyyntö
     * @return      Liittyykö pyyntöön istunto.
     */
    public static boolean aktiivinenIstunto(final HttpServletRequest req) {
        return req.getSession().getAttribute("jasen") != null;
    }

//    public static void varmistaIstunto

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
