
package mallit.java;

/**
 * <p>Mallintaa käyttäjätunnukseen liittyvää käyttäjätasoa. Tämän foorumin
 * pääsynvalvontamalli perustuu tasopohjaiseen pääsynvalvontaan.
 * Käyttäjätasoille on määritelty totaalinen järjestys alkaen pienimpään
 * käyttötapausten joukkoon oikeuttavasta käyttäjätasosta seuraavasti:<br>
 * <em>VIERAILIJA</em> &lt; <em>TAVALLINEN</em> &lt; <em>MODERAATTORI</em> &lt;
 * <em>YLLAPITAJA</em>. (On syytä huomata että käyttätunnusten keskinäinen
 * järjestys käyttäjätasojen ja pääsyoikeuksien suhteen ei ole antisymmetrinen
 * eikä foorumi valvo pääsyoikeuksia yksilöpohjaisesti.)</p>
 * <p>Järjestys merkitsee tässä siis, että seuraavat väitteet tulee pitää
 * kaikkialla voimassa:</p>
 * <ol><li>Käyttäjätason "VIERAILIJA" pääsyoikeudet ovat kaikkein suppeimmat;
 * </li>
 * <li>Kaikilla saman käyttäjätason käyttäjätunnuksilla on yhtäläiset
 * pääsyoikeudet;</li>
 * <li>Jokaisen käyttäjätason pääsyoikeuksiin kuuluvat sen erityiset
 * pääsyoikeudet sekä kaikki sitä edeltävien käyttäjätasojen pääsyoikeudet;
 * ja </li>
 * <li>Jokaisen käyttäjätason pääsyoikeuksien joukko on aidosti suurempi kuin
 * sitä edeltävien käyttäjätasojen pääsyoikeuksien joukko.</li>
 * </ol>
 * <p>Pääsyoikeuksien joukolla viittaan siis niiden käyttötapausten joukkoon,
 * joissa tietyn käyttäjätason käyttäjätunnukset voivat esiintyä käyttäjän
 * roolissa.</p>
 *
 * @author John Lång (jllang@cs.helsinki.fi)
 */
public enum Kayttajataso {

    /**
     * Jokainen käyttäjä, joka ei ole aloittanut istuntoa käyttäjätunnuksella,
     * on vierailija. Tämä käyttäjätaso ei esiinny yhdenkään käyttäjätunnuksen
     * yhteydessä.
     */
    VIERAILIJA(0),

    /**
     * Jokaisen uuden jäsenen käyttäjätunnuksen taso on <tt>TAVALLINEN</tt>.
     */
    TAVALLINEN(1),

    /**
     * Moderaattoreilla on muun muassa oikeus moderoida viestejä, siirtää ja
     * lukita ketjuja sekä asettaa jäsenille porttikieltoja.
     */
    MODERAATTORI(2),

    /**
     * Ylläpitäjä on jäsen, jolla on kaikkein laajimmat pääsyoikeudet.
     */
    YLLAPITAJA(3);

    /**
     * Käytetään kahden käyttäjätasojen keskinäisen järjestyksen määrityksessä.
     */
    private final int vertailuluku;

    private Kayttajataso(final int vertailuluku) {
        this.vertailuluku = vertailuluku;
    }

    /**
     * Onko tämä käyttäjätaso vähintään moderaattori.
     *
     * @return <tt>true</tt> joss käyttäjätaso on moderaattori tai ylläpitäjä.
     */
    public boolean onModeraattori() {
        return vertailuluku >= 2;
    }

    public boolean samaTaiKorkeampiKuin(final Kayttajataso verrattava) {
        return vertailuluku >= verrattava.vertailuluku;
    }

    /**
     * Metodi mallintaa kaksipaikkaista totaalista järjestysrelaatiota.
     * Paluuarvo on <tt>true</tt> joss käyttäjätaso a on suurempi tai yhtä kuin
     * käyttäjätaso b. Ilmaisu siis viittaa käyttäjätasojen totaaliseen
     * järjestykseen.
     *
     * @param a Ensimmäinen verrattava käyttäjätaso.
     * @param b Toinen verrattava käyttäjätaso.
     * @return  Onko pari (a,b) relaatiossa "stk".
     */
    public static boolean vahintaan(final Kayttajataso a, final Kayttajataso b) {
        // Tästä voisi ehkä tehdä myös instanssimetodin...
        return a.vertailuluku >= b.vertailuluku;
    }
}