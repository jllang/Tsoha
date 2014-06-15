/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kontrollerit.tyokalut;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import kontrollerit.IstuntoServlet;
import mallit.java.Jasen;

/**
 * Tämä luokka sisältää yleisiä pääsynvalvontaan liittyviä toimintoja.
 *
 * @author John Lång (jllang@cs.helsinki.fi)
 */
public final class Valvoja {

    private static final Map<Jasen, HttpSession> KIRJAUTUNEET;

    static {
        KIRJAUTUNEET = new HashMap<>();
    }

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
     * Palauttaa metodin kutsuhetkellä aktiivisiin istuntoihin liittyvät
     * käyttäjätunnukset.
     *
     * @return Sisäänkirjautuneet jäsenet.
     */
    public static Set<Jasen> annaKirjautuneet() {
        return KIRJAUTUNEET.keySet();
    }

    /**
     * Asettaa annetulle jäsenelle annetun istunnon.
     *
     * @param jasen
     * @param istunto
     */
    public static void lisaaIstunto(final Jasen jasen,
            final HttpSession istunto) {
        KIRJAUTUNEET.put(jasen, istunto);
    }

    /**
     * Ilmoittaa onko annetun jäsenen käyttäjätunnuksella aktiivinen istunto.
     *
     * @param jasen
     * @return <tt>true</tt> joss jäsen on kirjautunut.
     */
    public static boolean onKirjautunut(final Jasen jasen) {
        return KIRJAUTUNEET.containsKey(jasen);
    }

    /**
     * Käytetään porttikiellon ja uloskirjautumisen toimeenpanossa.
     *
     * @param kohde Käyttäjätunnus, jonka istunto suljetaan. Sulkeminen näkyy
     * käyttäjälle tämän pyytäessä seuraavan istuntoa edellyttävän sivun.
     */
    // ...Näin ainakin luulisin.
    public static void suljeIstunto(final Jasen kohde) {
        if (kohde == null) {
            return;
        }
        // Pitäisi tutkiskella joskus voisiko porttikiellon jotenkin helposti
        // saada aiheuttamaan välitön uudelleenohjaus esimerkiksi virhesivulle.
        final HttpSession istunto = KIRJAUTUNEET.get(kohde);
        istunto.removeAttribute("jasen");
        KIRJAUTUNEET.remove(kohde);
    }

    /**
     * Palauttaa <tt>true</tt> joss annettu käyttäjätunnus on olemassa, sillä on
     * annettu salasana, eikä se ole porttikiellossa. Kaikissa virhetilanteissa
     * palautetaan arvo false.
     *
     * @param jasen             Autentikoitava käyttäjätunnus.
     * @param salasana          Käyttäjän antama salasana.
     * @return                  Tosi joss käyttäjätunnus ja salasana täsmäävät.
     */
    @SuppressWarnings({"TooBroadCatch", "UseSpecificCatch"})
    public static boolean autentikoi(final Jasen jasen, final String salasana) {
        try {
            // Nukutaan hetki, jottei brute force arvailu / sanakirjahyökkäys
            // toimi liian nopeasti:
            Thread.sleep(1000);
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
