
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
import mallit.java.Ketju;
import mallit.java.TietokantaDAO;
import mallit.java.Viesti;

/**
 * Tämä servlet käsittelee viestien lisäämiseen, muokkaamiseen ja poistoon
 * liittyvää logiikkaa. Lisäksi servletin vastuulla on myös ketjujen luonti ja
 * poisto ketjujen ja viestien läheisten käsitteellisten yhteyksien vuoksi.
 *
 * @author John Lång (jllang@cs.helsinki.fi)
 */
@WebServlet(name = "ViestiServlet", urlPatterns = {"/muokkaus", "/poisto"})
public final class MuokkausServlet extends HttpServlet {

    // Taisi mennä aika karsean näköiseksi sotkuksi tämä luokka. Pahoittelut
    // siitä, mutta aikaa ei ole enää refaktorointiin.

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
        int ketjunTunnus = 0;
        try {
            ketjunTunnus = Integer.parseInt(req.getParameter("ketju"));
        } catch (NumberFormatException e) {}
        if (req.getRequestURI().equals(MUOKKAUS)) {
            if (Valvoja.aktiivinenIstunto(req, resp, "muokkaus")) {
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
                if (req.getParameter("viesti") == null) {
                    ketjunPoisto(req, resp, ketjunTunnus);
                } else {
                    try {
                        final int viestinTunnus = Integer.parseInt(
                                req.getParameter("viesti"));
                        if (viestinTunnus != 1) {
                            viestinPoisto(req, resp, ketjunTunnus, viestinTunnus);
                        } else {
                            ketjunPoisto(req, resp, ketjunTunnus);
                        }
                    } catch (NumberFormatException e) {
                        Uudelleenohjaaja.siirra(req, resp, "/jsp/virhesivu.jsp");
                        return;
                    }
                }
            }
        } else {
            Uudelleenohjaaja.siirra(req, resp, "/jsp/virhesivu.jsp");
        }
    }

    private static void siirryLomakkeeseen(final HttpServletRequest req,
            final HttpServletResponse resp, final String aihe,
            final String[] alueet, final String sisalto, final int virhekoodi)
            throws IOException, ServletException {
        req.setAttribute("aihe", aihe);
//        req.setAttribute("aluetaulu", alueet);
        req.setAttribute("sisalto", sisalto);
        req.setAttribute("virhekoodi", virhekoodi);
        Uudelleenohjaaja.siirra(req, resp, "/jsp/viestilomake.jsp?"
                + req.getQueryString());
    }

    private static void uusiKetju(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        Otsikoija.asetaOtsikko(req, "Uusi ketju");
        if (req.getAttribute("aluetaulu") == null) {
            req.setAttribute("aluetaulu", Alue.annaNimet());
        }
        // Kyllä, tarkastetaan kahteen kertaan koska tietokantahaussa voi tulla
        // virhe:
        if (req.getAttribute("aluetaulu") == null) {
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
                viestinMuokkaus(req, resp, ketjunTunnus, viestinTunnus);
                break;
            default:
                req.setAttribute("lomakkeenNimi", "Viestin muokkaus");
                req.setAttribute("muokattavuus", "disabled=\"disabled\"");
                Otsikoija.asetaOtsikko(req, "Viestin muokkaus");
                viestinMuokkaus(req, resp, ketjunTunnus, viestinTunnus);
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

    private static void viestinMuokkaus(final HttpServletRequest req,
            final HttpServletResponse resp, final int ketjunTunnus,
            final int viestinTunnus) throws ServletException, IOException {
        final Ketju ketju = (Ketju) TietokantaDAO.tuo(Ketju.class, ketjunTunnus);
        final Viesti viesti = (Viesti) TietokantaDAO.tuo(Viesti.class,
                ketjunTunnus, viestinTunnus);
        if (ketju == null || viesti == null) {
            Uudelleenohjaaja.siirra(req, resp, "/jsp/virhesivu.jsp");
            return;
        }
        final Jasen kirjoittaja = (Jasen) TietokantaDAO.tuo(Jasen.class,
                viesti.annaKirjoittaja()),
                muokkaaja = (Jasen) req.getSession().getAttribute("jasen");
        if (!muokkaaja.equals(kirjoittaja)
                && !(muokkaaja.annaTaso().onModeraattori()
                && muokkaaja.annaTaso().samaTaiKorkeampiKuin(
                        kirjoittaja.annaTaso()))
                ) {
            // Jos muokkaaja ei ole sama kuin alkuperäinen kirjoittaja, ja jos
            // muokkaaja ei ole moderaattori, tulee pääsy muokkauslomakkeeseen
            // estää. Lisäksi modderaattorit eivät saa moderoida ylläpitäjien
            // viestejä.
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
        if (muokkaaja.annaTaso().onModeraattori()) {
            viesti.asetaModeroitu(aikaleima);
        } else {
            viesti.asetaMuokattu(aikaleima);
        }
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

    private static void ketjunPoisto(final HttpServletRequest req,
            final HttpServletResponse resp, final int ketjunTunnus)
            throws ServletException, IOException {
        final Ketju ketju = (Ketju) TietokantaDAO.tuo(Ketju.class, ketjunTunnus);
        // Entä jos ketju == null?
        final Jasen poistaja = (Jasen) req.getSession().getAttribute("jasen");
        if (!poistaja.annaTaso().onModeraattori()
                && (poistaja.annaKayttajanumero() != ketju.annaAloittajaNumero()
                || ketju.annaViestienMaara() > 1)) {
            // Ketjun voi poistaa yleisessä tapauksessa vain moderaattori. Jos
            // ketjussa on vain yksi viesti, sen voi poistaa myös ketjun
            // aloittaja.
            Uudelleenohjaaja.siirra(req, resp, "/jsp/virhesivu.jsp");
            return;
        }
        if (req.getParameter("vahvistettu") == null) {
            req.setAttribute("toiminnonKuvaus", "poistaa valitun ketjun");
            Uudelleenohjaaja.siirra(req, resp, "/jsp/vahvistus.jsp?toiminto="
                    + "poisto&ketju=" + ketjunTunnus + "&viesti=" + 1);
            return;
        }
        ketju.asetaPoistettu(new Timestamp(System.currentTimeMillis()));
        if (TietokantaDAO.vie(ketju)) {
            // Pitäisiköhän ohjata etusivulle vai jollekin alueelle...
            Uudelleenohjaaja.uudelleenohjaa(req, resp, "etusivu");
        } else {
            Uudelleenohjaaja.siirra(req, resp, "/jsp/virhesivu.jsp");
        }
    }

    private static void viestinPoisto(final HttpServletRequest req,
            final HttpServletResponse resp, final int ketjunTunnus,
            final int viestinTunnus) throws ServletException, IOException {
        final Viesti viesti = (Viesti) TietokantaDAO.tuo(
                Viesti.class, ketjunTunnus, viestinTunnus);
        final Jasen poistaja = (Jasen) req.getSession().getAttribute("jasen");
        // Huom. Tässä pitäisi myös tarkastaa ettei moderaattori yritä poistaa
        // ylläpitäjän viestiä. Linkkiähän tämä ei näe mutta URL on helposti
        // arvattavissa. (Sama koskee tietysti myös moderointia.)
        if (!poistaja.annaTaso().onModeraattori()
                && poistaja.annaKayttajanumero() != viesti.annaKirjoittaja()) {
            // Muiden viestejä voivat poistaa vain operaattorit.
            Uudelleenohjaaja.siirra(req, resp, "/jsp/virhesivu.jsp");
            return;
        }
        if (req.getParameter("vahvistettu") == null) {
            req.setAttribute("toiminnonKuvaus", "poistaa valitun viestin");
            Uudelleenohjaaja.siirra(req, resp, "/jsp/vahvistus.jsp?toiminto="
                    + "poisto&ketju=" + ketjunTunnus + "&viesti="
                    + viestinTunnus);
            return;
        }
        viesti.asetaPoistettu(new Timestamp(System.currentTimeMillis()));
        if (TietokantaDAO.vie(viesti)) {
            Uudelleenohjaaja.uudelleenohjaa(req, resp, "ketju?tunnus="
                    + ketjunTunnus + "&sivu=1");
        } else {
            Uudelleenohjaaja.siirra(req, resp, "/jsp/virhesivu.jsp");
        }
    }
}
