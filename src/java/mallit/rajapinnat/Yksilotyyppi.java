
package mallit.rajapinnat;

import java.sql.ResultSet;

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
     * Palauttaa SQL insert-kyselyn, jolla olion tila viedään
     * tietokantaan.
     * 
     * @return Merkkijonomuotoinen SQL-kysely.
     */
    String annaLisayskysely();
    
    /**
     * Palauttaa SQL select-kyselyn, jolla haetaan annettua avainta
     * vastaava monikko tietokannasta.
     * 
     * @param avain Relaation avain SQL-literaaleina.
     * @return Merkkijonomuotoinen SQL-kysely.
     */
    String annaHakukysely(final String... avain);

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
