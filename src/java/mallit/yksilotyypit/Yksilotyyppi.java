
package mallit.yksilotyypit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Yksilötyyppi mallintaa yhtä järjestelmän varsinaista tietokohdetta.
 * Yksilötyypin edustaja voidaan säilöä SQL-tietokannassa.
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public abstract class Yksilotyyppi {

    private boolean tuore;

    public Yksilotyyppi(final boolean tuore) {
        this.tuore = tuore;
    }

    /**
     * Ilmaisee onko yksilötyyppi jo kerran tallennettu tietokantaan.
     *
     * @return <tt>true</tt> joss yksilötyypin tietoja ei ole vielä tallennettu
     * tietokantaan.
     *
     * @see Yksilotyyppi#asetaEpatuoreeksi()
     */
    public final boolean onTuore() {
        return tuore;
    }

    /**
     * Asettaa yksilötyypin sisäisen tuoreutta ilmaisevan kentän arvoksi
     * <tt>false</tt>. Tätä metodia tulee kutsua kun yksilötyyppi on tallennettu
     * onnistuneesti tietokantaan.
     *
     * @see Yksilotyyppi#onTuore()
     */
    public final void asetaEpatuoreeksi() {
        this.tuore = false;
    }

    /**
     * Palauttaa <tt>PreparedStatement</tt>-olion, joka suorittaa SQL insert
     * -kyselyn ja johon on lisätty yksilötyypin kenttien arvot. Metodi ei sulje
     * yhteyttä.
     *
     * @param yhteys
     * @return
     * @throws SQLException
     */
    public abstract PreparedStatement lisayskysely(final Connection yhteys)
            throws SQLException;


//    /**
//     * Palauttaa <tt>PreparedStatement</tt>-olion, joka suorittaa SQL update
//     * -kyselyn ja johon on lisätty yksilötyypin (muuttuneet) attribuutit ja
//     * niiden arvot. Metodi ei sulje yhteyttä.
//     *
//     * @param yhteys
//     * @return
//     * @throws SQLException
//     */
//    public abstract PreparedStatement paivityskysely(final Connection yhteys)
//            throws SQLException;
//
//    /**
//     * Palauttaa <tt>PreparedStatement</tt>-olion, joka suorittaa SQL delete
//     * -kyselyn. Metodi ei sulje yhteyttä.
//     *
//     * @param yhteys
//     * @return
//     * @throws SQLException
//     */
//    public abstract PreparedStatement poistokysely(final Connection yhteys)
//            throws SQLException;

}
