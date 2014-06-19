
package kontrollerit.tyypit;

import java.text.DateFormat;
import java.util.Date;
import mallit.java.Ketju;

/**
 *
 * @author John Lång (jllang@cs.helsinki.fi)
 */
public final class ListaAlkio {

    public static final String PARILLINEN, PARITON;

    public final String     parillisuus, url, nimi;
    public final String[]   lisakentat;

    static {
        PARILLINEN  = "parillinen";
        PARITON     = "pariton";
    }

    public ListaAlkio(final int numero, final String osoite, final String nimi,
            final String[] lisakentat) {
        this.parillisuus    = ((numero + 1) % 2 == 0) ? PARILLINEN : PARITON;
        this.url            = osoite;
        this.nimi           = nimi;
        this.lisakentat     = lisakentat;
    }

    /**
     * Luo ketjun listattavat tiedot sisältävän lista-alkion.
     *
     * @param listausnumero Lista-alkion järjestysnumero listauksessa.
     * @param ketju         Lista-alkion ketju.
     * @return              Uusi lista-alkio.
     */
    public static ListaAlkio luo(int listausnumero, final Ketju ketju) {
        return new ListaAlkio(listausnumero,
                "ketju?tunnus=" + ketju.annaTunnus() + "&sivu=1",
                ketju.listausnimi(), new String[]{
                    "<a class=\"" + ketju.annaAloittajanTaso().toString()
                            .toLowerCase() + "\" href=\"profiili?tunnus="
                            + ketju.annaAloittajaNumero() + "\">"
                            + ketju.annaAloittajanListausnimi() + "</a>",
                    DateFormat.getInstance().format(
                            new Date(ketju.annaMuutettu().getTime()))
                });
    }

}
