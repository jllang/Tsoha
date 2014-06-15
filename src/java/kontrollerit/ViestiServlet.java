
package kontrollerit;

import java.io.IOException;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kontrollerit.tyokalut.Otsikoija;
import kontrollerit.tyokalut.Uudelleenohjaaja;
import kontrollerit.tyokalut.Valvoja;
import mallit.java.Alue;
import mallit.java.Jasen;
import mallit.java.Kayttajataso;
import mallit.java.Ketju;
import mallit.java.TietokantaDAO;
import mallit.java.Viesti;

/**
 * Tämä servlet käsittelee viestien lisäämiseen, muokkaamiseen ja poistoon
 * liittyvää logiikkaa.
 *
 * @author John Lång (jllang@cs.helsinki.fi)
 */
@WebServlet(name = "ViestiServlet", urlPatterns = {"/muokkaus", "/poisto"})
public final class ViestiServlet extends HttpServlet {

    // Virhekoodit ovat:
    // 0 - Ei virhettä
    // 1 - Lomakkeessa oli puuttuvia kenttiä
    // 2 - Transaktio epäonnistui palvelimella

    private static final int EI_VIRHETTA, PUUTTUVIA_KENTTIA, TIETOKANTAVIRHE;

    private static String MUOKKAUS, POISTO;

    static {
        EI_VIRHETTA         = 0;
        PUUTTUVIA_KENTTIA   = 1;
        TIETOKANTAVIRHE     = 2;
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

    private static void kasittelePyynto(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        if (MUOKKAUS == null) {
            MUOKKAUS    = req.getContextPath() + "/muokkaus";
            POISTO      = req.getContextPath() + "/poisto";
        }
        if (req.getRequestURI().equals(MUOKKAUS)) {
            if (Valvoja.aktiivinenIstunto(req, resp, "muokkaus")) {
                int ketjunTunnus = 0;
                try {
                    ketjunTunnus = Integer.parseInt(req.getParameter("ketju"));
                } catch (NumberFormatException e) {
                }
                if (ketjunTunnus == 0) {
                    req.setAttribute("lomakkeenNimi", "Uusi ketju");
                    Otsikoija.asetaOtsikko(req, "Uusi ketju");
                    uusiKetju(req, resp);
                } else {
                    ketjunTaydennys(req, resp, ketjunTunnus);
                }
            }
        } else if (req.getRequestURI().equals(POISTO)) {
            if (Valvoja.aktiivinenIstunto(req, resp, "poisto")) {

            }
        } else {
            Uudelleenohjaaja.siirra(req, resp, "/jsp/virhesivu.jsp");
            return;
        }
    }

    private static void siirryLomakkeeseen(final HttpServletRequest req,
            final HttpServletResponse resp, final String aihe,
            final String[] alueet, final String sisalto, final int virhekoodi)
            throws IOException, ServletException {
        req.setAttribute("aihe", aihe);
        req.setAttribute("aluelista", alueet);
        req.setAttribute("sisalto", sisalto);
        req.setAttribute("virhekoodi", virhekoodi);
        Uudelleenohjaaja.siirra(req, resp, "/jsp/viestilomake.jsp?"
                + req.getQueryString());
    }

    private static void uusiKetju(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        Otsikoija.asetaOtsikko(req, "Uusi ketju");
        if (req.getAttribute("aluelista") == null) {
            req.setAttribute("aluelista", Alue.annaNimet());
        }
        // Kyllä, tarkastetaan kahteen kertaan koska tietokantahaussa voi tulla
        // virhe:
        if (req.getAttribute("aluelista") == null) {
            Uudelleenohjaaja.siirra(req, resp, "/jsp/virhesivu.jsp");
            return;
        }
        final String aihe   = (String) req.getParameter("aihe"),
                sisalto     = (String) req.getParameter("sisalto");
        final String[] valitutAlueet = req.getParameterValues("alueet");
        if (aihe != null && valitutAlueet != null && sisalto != null) {
            if (aihe.isEmpty() || valitutAlueet.length == 0
                    || sisalto.isEmpty()) {
                siirryLomakkeeseen(req, resp, aihe, valitutAlueet, sisalto,
                        PUUTTUVIA_KENTTIA);
                return;
            }
            final int ketjunTunnus = TietokantaDAO.luoKetju((Jasen)
                    req.getSession().getAttribute("jasen"), aihe, sisalto,
                    valitutAlueet);
            if (ketjunTunnus == 0) {
                siirryLomakkeeseen(req, resp, aihe, valitutAlueet, sisalto,
                        TIETOKANTAVIRHE);
                return;
            }
            Uudelleenohjaaja.uudelleenohjaa(req, resp, "ketju?tunnus="
                    + ketjunTunnus + "&sivu=1");
        } else {
            siirryLomakkeeseen(req, resp, aihe, valitutAlueet, sisalto,
                    req.getAttribute("lahetetty") != null
                            ? PUUTTUVIA_KENTTIA : EI_VIRHETTA);
        }
    }
    private static void ketjunTaydennys(final HttpServletRequest req,
            final HttpServletResponse resp, final int ketjunTunnus)
            throws ServletException, IOException {
        int viestinTunnus = 0;
        try {
            viestinTunnus = Integer.parseInt(req.getParameter("viesti"));
        } catch (NumberFormatException e) {}
        switch (viestinTunnus) {
            case 0:
                req.setAttribute("lomakkeenNimi", "Uusi viesti");
                req.setAttribute("muokattavuus", "disabled=\"disabled\"");
                Otsikoija.asetaOtsikko(req, "Uusi viesti");
                uusiViesti(req, resp, ketjunTunnus);
                break;
            case 1:
                req.setAttribute("lomakkeenNimi", "Viestin muokkaus");
                Otsikoija.asetaOtsikko(req, "Viestin muokkaus");
                muokkaaViestia(req, resp, ketjunTunnus, viestinTunnus);
                break;
            default:
                req.setAttribute("lomakkeenNimi", "Viestin muokkaus");
                req.setAttribute("muokattavuus", "disabled=\"disabled\"");
                Otsikoija.asetaOtsikko(req, "Viestin muokkaus");
                muokkaaViestia(req, resp, ketjunTunnus, viestinTunnus);
        }
    }

    private static void uusiViesti(final HttpServletRequest req,
            final HttpServletResponse resp, final int ketjunTunnus)
            throws ServletException, IOException {
        // Mitä jos tapahtuu tietokantavirhe tässä kohdassa?
        final Ketju ketju = (Ketju) TietokantaDAO.tuo(Ketju.class, ketjunTunnus);
        final String aihe = ketju.annaAihe(),
                sisalto = req.getParameter("sisalto");
        final String[] valitutAlueet = req.getParameterValues("alueet");
        if (sisalto != null && !sisalto.isEmpty()) {
            final int numero = ketju.annaViimeisenViestinNumero() + 1;
            final Timestamp aikaleima = new Timestamp(System.currentTimeMillis());
            final Jasen jasen = (Jasen) req.getSession().getAttribute("jasen");
            final Viesti viesti = Viesti.luo(ketjunTunnus, numero,
                    jasen.annaKayttajanumero(), aikaleima, sisalto);
            ketju.asetaMuutettu(aikaleima);
            jasen.kasvataViesteja();
            if (TietokantaDAO.vie(viesti, ketju, jasen)) {
                Uudelleenohjaaja.siirra(req, resp, "/jsp/ketju.jsp?tunnus="
                        + ketjunTunnus +"&sivu=1");
            } else {
                siirryLomakkeeseen(req, resp, aihe, valitutAlueet, sisalto,
                        TIETOKANTAVIRHE);
            }
        } else {
            siirryLomakkeeseen(req, resp, aihe, valitutAlueet, sisalto,
                    req.getAttribute("lahetetty") != null
                            ? PUUTTUVIA_KENTTIA : EI_VIRHETTA);
        }
    }

    private static void muokkaaViestia(final HttpServletRequest req,
            final HttpServletResponse resp, final int ketjunTunnus,
            final int viestinTunnus) throws ServletException, IOException {
        final Ketju ketju = (Ketju) TietokantaDAO.tuo(Ketju.class, ketjunTunnus);
        final Viesti viesti = (Viesti) TietokantaDAO.tuo(Viesti.class,
                ketjunTunnus, viestinTunnus);
        final Jasen kirjoittaja = (Jasen) TietokantaDAO.tuo(Jasen.class,
                viesti.annaKirjoittaja()),
                muokkaaja = (Jasen) req.getSession().getAttribute("jasen");
        if (muokkaaja.annaKayttajanumero() != kirjoittaja.annaKayttajanumero()
                && !Kayttajataso.vahintaan(muokkaaja.annaTaso(),
                        Kayttajataso.MODERAATTORI)) {
            // Jos muokkaaja ei ole sama kuin alkuperäinen kirjoittaja ja jos
            // muokkaaja ei ole moderaattori, tulee pääsy muokkauslomakkeeseen
            // estää:
            Uudelleenohjaaja.uudelleenohjaa(req, resp, "virhesivu");
        }
        if (ketju == null || viesti == null) {
            Uudelleenohjaaja.siirra(req, resp, "/jsp/virhesivu.jsp");
            return;
        }
        final String aihe = ketju.annaAihe();
        String sisalto = req.getParameter("sisalto");
        final String[] valitutAlueet = Alue.annaNimet();
        if (sisalto == null || sisalto.isEmpty() || valitutAlueet == null
                || valitutAlueet.length == 0) {
            sisalto = viesti.annaSisalto();
            siirryLomakkeeseen(req, resp, aihe, valitutAlueet, sisalto,
                    req.getAttribute("lahetetty") != null
                            ? PUUTTUVIA_KENTTIA : EI_VIRHETTA);
            return;
        }
        final Timestamp aikaleima = new Timestamp(System.currentTimeMillis());
        viesti.asetaSisalto(sisalto);
        viesti.asetaMuokattu(aikaleima);
//        if (viestinTunnus == 1) {
//            //Hoida ketjujen_sijainnit ajan tasalle.
//            throw new UnsupportedOperationException("Ketjun siirtoa ei ole "
//                    + "vielä toteutettu");
//        }
        ketju.asetaMuutettu(aikaleima);
        if (TietokantaDAO.vie(viesti, ketju)) {
            Uudelleenohjaaja.uudelleenohjaa(req, resp, "ketju?tunnus="
                    + ketjunTunnus +"&sivu=1");
        } else {
            siirryLomakkeeseen(req, resp, aihe, valitutAlueet, sisalto,
                    TIETOKANTAVIRHE);
        }
    }
}
