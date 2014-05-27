
package mallit.rajapinnat;

import java.sql.SQLException;

/**
 * Yksilötyyppi mallintaa yhtä järjestelmän varsinaista tietokohdetta.
 * Yksilötyypin edustaja voidaan säilöä SQL-tietokannassa. Säilömisen
 * helpottamiseksi olion tulee palauttaa avainattribuuttiensa arvot kelvollisina
 * SQL-literaaleina <tt>avainarvot</tt>-metodia kutsuttaessa. Lisäksi
 * yksilötyypin <tt>toString</tt>-metodin tulee palauttaa SQL insert-lauseeseen
 * sopiva merkkijonomuotoinen monikko.
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public interface Yksilotyyppi {

    /**
     * Palauttaa yksilötyyppiä vastaavan relaation nimen.
     *
     * @return
     */
    String taulunNimi();

    /**
     * Palauttaa yksilötyypin avaimen attribuuttien nimen.
     *
     * @return Attribuutin nimi merkkijonotaulukkona.
     * @see Yksilotyyppi#avainarvot()
     */
    String[] avainattribuutit();

    /**
     * Palauttaa yksilötyypin avaimen (eli minimaalisen yliavaimen)
     * attribuuttien arvot.
     *
     * @return Avaimen arvo merkkijonotaulukkona. Arvojen tulee olla kelvollisia
     * SQL-tietotyyppien literaaleja. Lisäksi niiden tulee olla avainten nimiä
     * vastaavassa järjestyksessä.
     * @see Yksilotyyppi#avainattribuutit()
     */
    String[] avainarvot();

    /**
     * Palauttaa olion tilaa kuvaavan merkkijonomuotoisen monikon. <em>n</em>
     * attribuutin monikko tulee koodata merkkijonoksi seuraavassa muodossa:
     * <p><em>"(" &lt;attribuutti 1&gt; ["," (&lt;attribuutti 2&gt; | "NULL")","
     * (&lt;attribuutti 3&gt; | "NULL")"," ... "," (&lt;attribuutti n&gt; |
     * "NULL")] ")"</em></p>.
     *
     * @return Merkkijonoksi koodattu monikko.
     */
    @Override
    String toString();
}
