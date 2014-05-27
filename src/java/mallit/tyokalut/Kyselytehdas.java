package mallit.tyokalut;

import mallit.rajapinnat.Funktio;
import mallit.yksilotyypit.Jasen;

/**
 * Tämä luokka rakentaa lambdalausekkeita jotka tuottavat yksilötyypeille
 * merkkijonomuotoisia SQL-lisäyskyselyjä.
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Kyselytehdas {

    private static Funktio<Jasen, String> luoLisayskysely() {
        return (final Jasen jasen) -> {
            final StringBuilder mjr = new StringBuilder();
            mjr.append("insert into jasenet values ('");
            mjr.append(jasen.annaKayttajatunnus());
            mjr.append("', '");
            mjr.append(jasen.annaRekisteroity());
            mjr.append("', '");
            mjr.append(jasen.annaSalasanatiiviste());
            mjr.append("', '");
            mjr.append(jasen.annaSahkopostiosoite());
            mjr.append("', '");
            mjr.append(jasen.annaTaso());
            mjr.append("', ");
            mjr.append(jasen.annaNimimerkki() == null
                    ? "NULL, " : "'" + jasen.annaNimimerkki() + "', ");
            mjr.append(jasen.annaAvatar() == null
                    ? "NULL, " : "'" + jasen.annaAvatar() + "', ");
            mjr.append(jasen.annaKuvaus() == null
                    ? "NULL)" : "'" + jasen.annaKuvaus() + ")");
            return mjr.toString();
        };
    }

}
