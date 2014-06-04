
package kontrollerit.tyypit;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public class ListaAlkio {

    public static final String PARILLINEN, PARITON;

    public final String     parillisuus, osoite, nimi;

    static {
        PARILLINEN  = "parillinen";
        PARITON     = "pariton";
    }

    public ListaAlkio(final int numero, final String osoite, final String nimi) {
        this.parillisuus    = (numero % 2 == 0) ? PARILLINEN : PARITON;
        this.osoite         = osoite;
        this.nimi           = nimi;
    }

}
