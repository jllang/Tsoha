package kontrollerit.tyokalut;

import java.util.LinkedList;
import java.util.List;
import kontrollerit.tyypit.ListaAlkio;
import mallit.java.Alue;
import mallit.java.Ketju;
import mallit.java.TietokantaDAO;
import mallit.java.Yksilotyyppi;

/**
 *
 * @author John Lång (jllang@cs.helsinki.fi)
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

    public static List<ListaAlkio> tyhjaLista() {
        // Tämä metodi on testausta varten
        return TYHJA_HAKUTULOS;
    }

    public static List<ListaAlkio> listaaAlueet() {
        return tarkastaLista(haeAlueet());
    }

    public static List<ListaAlkio> listaaKetjut(final int sivunPituus,
            final int siirto, final int alueenTunnus) {
        return tarkastaLista(haeKetjut(sivunPituus, siirto, alueenTunnus));
    }

    public static List<ListaAlkio> listaaTuoreet() {
        return tarkastaLista(haeTuoreet());
    }

//    public static List<ListaAlkio> listaaPorttikiellot() {
//        return tarkastaLista(haePorttikiellot());
//    }

    private static List<ListaAlkio> tarkastaLista(final List<ListaAlkio> lista) {
        if (lista == null) {
            return VIRHEELLINEN_HAKU;
        }
        if (lista.isEmpty()) {
            return TYHJA_HAKUTULOS;
        }
        return lista;
    }

    private static List<ListaAlkio> haeTuoreet() {
        Yksilotyyppi[] ketjut = TietokantaDAO.tuoSivu(Ketju.class, "muutettu",
                true, 10, 0);
        List<ListaAlkio> lista = new LinkedList<>();
        int naytettyja = 0;
        for (int i = 0; i < ketjut.length; i++) {
            final Ketju ketju = (Ketju) ketjut[i];
            if (ketju == null) {
                break;
            }
            if (ketju.annaPoistettu() != null) {
                // Poistetut ketjut yksinkertaisesti jätetään listaamatta.
                continue;
            }
            lista.add(ListaAlkio.luo(naytettyja, ketju));
            naytettyja++;
        }
        return lista;
    }

    private static List<ListaAlkio> haeKetjut(final int sivunPituus,
            final int siirto, final int alueenTunnus) {
        final Ketju[] ketjut = Alue.annaKetjut(sivunPituus, siirto, alueenTunnus);
        final List<ListaAlkio> lista = new LinkedList<>();
        int naytettyja = 0;
        for (final Ketju ketju : ketjut) {
            if (ketju == null) {
                break;
            }
            if (ketju.annaPoistettu() != null) {
                continue;
            }
            lista.add(ListaAlkio.luo(naytettyja, ketju));
            naytettyja++;
        }
        return lista;
    }

    private static List<ListaAlkio> haeAlueet() {
        Yksilotyyppi[] alueet = TietokantaDAO.tuoSivu(Alue.class, "nimi", false,
                10, 0);
        List<ListaAlkio> lista = new LinkedList<>();
        int naytettyja = 0;
        for (int i = 0; i < alueet.length; i++) {
            final Alue alue = (Alue) alueet[i];
            if (alue == null) {
                break;
            }
            if (alue.annaPoistettu() != null) {
                continue;
            }
            lista.add(new ListaAlkio(naytettyja, "alue?tunnus="
                    + alue.annaTunnus() + "&sivu=1", alue.annaNimi(),
                    new String[] {alue.annaKuvaus()}));
            naytettyja++;
        }
        return lista;
    }

//    private static List<ListaAlkio> haePorttikiellot() {
//
//    }
}
