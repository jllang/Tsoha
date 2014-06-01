
package mallit.tyypit;

/**
 * Mallintaa käyttäjätunnukseen liittyvää käyttäjätasoa.
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public enum Kayttajataso {

    /**
     * Jokainen käyttäjä, joka ei ole aloittanut istuntoa käyttäjätunnuksella,
     * on vierailija.
     */
    VIERAILIJA,

    /**
     * Jokaisen uuden jäsenen käyttäjätunnuksen taso on <tt>TAVALLINEN</tt>.
     */
    TAVALLINEN,

    MODERAATTORI,

    YLLAPITAJA;

//    public static Kayttajataso luo(final String merkkijono) {
//        switch (merkkijono.toUpperCase()) {
//            case "VIERAILIJA":
//                return VIERAILIJA;
//            case "TAVALLINEN":
//                return TAVALLINEN;
//            case "MODERAATTORI":
//                return MODERAATTORI;
//            case "YLLAPITAJA":
//                return YLLAPITAJA;
//            default:
//                throw new AssertionError();
//        }
//    }
}
