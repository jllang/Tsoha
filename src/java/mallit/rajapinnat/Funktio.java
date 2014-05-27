
package mallit.rajapinnat;

/**
 * Mallintaa matemaattista funktiota eli kuvausta X -> Y.
 *
 * @param <X> Kuvauksen lähtöjoukko (eli tyyppi, jolle kuvaus määritellään).
 * @param <Y> Kuvauksen maalijoukko (eli tyyppi, jonka ilmentymä liitetään
 * maalijoukon ilmentymään).
 * @author John Lång <jllang@cs.helsinki.fi>
 */
//@FunctionalInterface
public interface Funktio<X, Y> {

    /**
     * Liittää annetun tyypin X ilmentymään x deterministisesti yhden tyypin Y
     * ilmentymän y.
     *
     * @param x Jokin X.
     * @return Jokin Y.
     */
    Y kuvaa(final X x);
}
