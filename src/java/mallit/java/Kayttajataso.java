
package mallit.java;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

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

    /**
     * Moderaattoreilla on oikeus moderoida viestejä, siirtää ja lukita ketjuja
     * sekä asettaa jäsenille porttikieltoja.
     */
    MODERAATTORI,

    /**
     * Ylläpitäjä on jäsen, jolla on kaikkein laajimmat käyttöoikeudet.
     */
    YLLAPITAJA;
}