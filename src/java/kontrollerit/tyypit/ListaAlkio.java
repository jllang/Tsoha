
package kontrollerit.tyypit;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
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

}
