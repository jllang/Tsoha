package kontrollerit.tyokalut;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.DateFormatter;
import kontrollerit.tyypit.ListaAlkio;
import mallit.java.Alue;
import mallit.java.Ketju;
import mallit.java.TietokantaDAO;
import mallit.java.Yksilotyyppi;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Listaaja {

    private static final List<ListaAlkio>   TYHJA_HAKUTULOS, VIRHEELLINEN_HAKU;
    public static final String[]            ALUEOTSIKOT, KETJUOTSIKOT;

    static {
        TYHJA_HAKUTULOS     = new LinkedList<>();
        TYHJA_HAKUTULOS.add(new ListaAlkio(0, null, "(Ei hakutuloksia.)",
                null));
        VIRHEELLINEN_HAKU   = new LinkedList<>();
        VIRHEELLINEN_HAKU.add(new ListaAlkio(0, null, "<div class=\"virhe\">"
                + "(Tapahtui virhe)</div>", null));
        ALUEOTSIKOT         = new String[]{"Nimi", "Kuvaus"};
        KETJUOTSIKOT        = new String[]{"Aihe", "Aloittaja", "Aktiivinen"};
    }

    public static List<ListaAlkio> listaa(final String kohde) {
        final List<ListaAlkio> lista = kasitteleParametrit(kohde);
        if (lista == null) {
            return VIRHEELLINEN_HAKU;
        }
        if (lista.isEmpty()) {
            return TYHJA_HAKUTULOS;
        }
        return lista;
    }

    private static List<ListaAlkio> kasitteleParametrit(final String kohde) {
        final List<ListaAlkio> lista;
        switch (kohde) {
            case "tuoreet":
                lista = haeTuoreet();
                break;
            case "ketjut":
//                final int aluetunnus;
//                try {
//                    aluetunnus = Integer.parseInt(req.getParameter("tunnus"));
//                    Alue alue = (Alue) TietokantaDAO.tuo(Alue.class, aluetunnus);
//                } catch (NumberFormatException | NullPointerException e) {
//                    lista = null;
//                    break;
//                } catch (SQLException e) {
//                    Logger.getLogger(Listaaja.class.getName()).log(
//                            Level.SEVERE, null, e);
//                    lista = null;
//                    break;
//                }
//
//                lista = haeKetjut();
                lista = null;
                break;
            case "alueet":
            default:
                lista = haeAlueet();
        }
        return lista;
    }

    private static List<ListaAlkio> haeTuoreet() {
        Yksilotyyppi[] ketjut = TietokantaDAO.tuoSivu(Ketju.class, "muutettu",
                true, 10, 0);
        List<ListaAlkio> lista = new LinkedList<>();
        for (int i = 0; i < ketjut.length; i++) {
            final Ketju ketju = (Ketju) ketjut[i];
            if (ketju == null) {
                break;
            }
            // Ketjuunkin kannattaisi ehkä lisätä viestien määrän kertova kenttä
            // niin päästäisiin helposti linkittämään viimeiselle sivulle ilman
            // yhteenvetokyselyjä...
            lista.add(new ListaAlkio(i,
                    "ketju?tunnus=" + ketju.annaTunnus() + "&sivu=1",
                    ketju.listausnimi(), new String[]{
                        "<a class=\"" + ketju.annaAloittajanTaso().toString()
                                .toLowerCase() + "\" href=\"profiili?tunnus="
                                + ketju.annaAloittajaNumero() + "\">"
                                + ketju.annaAloittajanListausnimi() + "</a>",
                        DateFormat.getInstance().format(
                                new Date(ketju.annaMuutettu().getTime()))
                    }));
        }
        return lista;
    }

//    private static List<ListaAlkio> haeKetjut() {
//        Yksilotyyppi[] alueet = TietokantaDAO.tuoSivu(Ketju.class, "aihe",
//                10, 0);
//        List<ListaAlkio> lista = new LinkedList<>();
//        for (int i = 0; i < alueet.length; i++) {
//            final Yksilotyyppi ketju = alueet[i];
//            if (ketju == null) {
//                break;
//            }
//            lista.add(new ListaAlkio(i, "/ketju?tunnus=0", ketju.listausnimi(),
//                    new String[]{}));
//        }
//        return lista;
//    }

    private static List<ListaAlkio> haeAlueet() {
        Yksilotyyppi[] alueet = TietokantaDAO.tuoSivu(Alue.class, "nimi", false,
                10, 0);
        List<ListaAlkio> lista = new LinkedList<>();
        for (int i = 0; i < alueet.length; i++) {
            final Alue alue = (Alue) alueet[i];
            if (alue == null) {
                break;
            }
            lista.add(new ListaAlkio(i, "/alue?tunnus=" + alue.annaTunnus(),
                    alue.annaNimi(), new String[] {alue.annaKuvaus()}));
        }
        return lista;
    }
}
