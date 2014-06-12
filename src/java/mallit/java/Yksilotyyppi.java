
package mallit.java;

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

//    /**
//     * Ilmaisee onko yksilötyyppi jo kerran tallennettu tietokantaan.
//     *
//     * @return <tt>true</tt> joss yksilötyypin tietoja ei ole vielä tallennettu
//     * tietokantaan.
//     *
//     * @see Yksilotyyppi#asetaEpatuoreeksi()
//     */
//    public final boolean onTuore() {
//        return tuore;
//    }

//    /**
//     * Asettaa yksilötyypin sisäisen tuoreutta ilmaisevan kentän arvoksi
//     * <tt>false</tt>. Tätä metodia tulee kutsua kun yksilötyyppi on tallennettu
//     * onnistuneesti tietokantaan.
//     *
//     * @see Yksilotyyppi#onTuore()
//     */
//    public final void asetaEpatuoreeksi() {
//        this.tuore = false;
//    }

    /**
     * Palauttaa merkkijonomuotoisen SQL insert tai update -lauseen, jossa
     * attribuuttien arvot on korvattu kysymysmerkeillä.
     *
     * @return SQL insert tai update -kysely.
     * @see Yksilotyyppi#taydennaAttribuutit(java.sql.PreparedStatement)
     */
    public final String paivityslause() {
        if (tuore) {
            return annaLisayslause();
        } else {
            return annaPaivityslause();
        }
    }

    abstract String annaLisayslause();

    abstract String annaPaivityslause();

//    abstract String annaPoistolause();

    /**
     * Lisää annettuun PreparedStatement-olioon sen aksessoreita käyttäen
     * yksilötyypin omien attribuuttien arvot. Kyseessä voi olla SQL insert tai
     * update-lause riippuen yksilötyypin tuoreudesta (eli siitä mitä
     * yksilötyypin paivityslause-metodi on palauttanut TietokantaDAO:lle).
     *
     * @param kysely
     * @throws java.sql.SQLException
     * @see Yksilotyyppi#paivityslause()
     */
    public void valmistelePaivitys(final PreparedStatement kysely)
            throws SQLException {
        if (tuore) {
            valmisteleLisays(kysely);
            tuore = false;
        } else {
            valmisteleUpdate(kysely);
        }
    }

    abstract void valmisteleLisays(final PreparedStatement kysely)
            throws SQLException;

    abstract void valmisteleUpdate(final PreparedStatement kysely)
            throws SQLException;

//    abstract void valmistelePoisto(final PreparedStatement kysely)
//            throws SQLException;

//    /**
//     * Palauttaa <tt>PreparedStatement</tt>-olion, joka suorittaa SQL insert
//     * -kyselyn ja johon on lisätty yksilötyypin kenttien arvot. Metodi ei sulje
//     * yhteyttä.
//     *
//     * @param yhteys
//     * @return
//     * @throws SQLException
//     */
//    abstract PreparedStatement lisayskysely(final Connection yhteys)
//            throws SQLException;

    /**
     * Palauttaa uniikin merkkijonon, jota voidaan käyttää olion nimenä
     * listauksissa.
     *
     * @return Epätyhjä, uniikki merkkijono.
     * @throws UnsupportedOperationException Jos yksilötyypille ei ole
     * määritelty listausnimeä.
     */
    public abstract String listausnimi() throws UnsupportedOperationException;

}
