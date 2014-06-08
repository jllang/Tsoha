
package kontrollerit.tyokalut;

import javax.servlet.http.HttpServletRequest;
import mallit.java.Jasen;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Otsikoija {

    /**
     * Lisää annettuun pyyntöön (sivuvaikutuksena) luotavan html-sivun otsikon.
     * Otsikon loppuun lisätään pyydetyn sivun nimi.
     *
     * @param req       Pyyntö
     * @param sivunNimi Pyydetyn sivun nimi
     */
    public static void asetaOtsikko(final HttpServletRequest req,
            final String sivunNimi) {
        req.setAttribute("otsikko", Valvoja.aktiivinenIstunto(req)
                ? ((Jasen) req.getSession().getAttribute("jasen")).listausnimi()
                + "@Esimerkkifoorumi: " + sivunNimi
                : "Esimerkkifoorumi: " + sivunNimi);
    }
}
