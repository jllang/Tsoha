/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mallit.java;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Mallintaa rekisteröityyn foorumin käyttäjätunnukseen liittyviä asioita.
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Jasen extends Yksilotyyppi {

    private static final String     LISAYSPOHJA, HAKUPOHJA1, HAKUPOHJA2;
    private static final Predicate<String> KELVOLLINEN_SP;

    private final int       kayttajanumero;
    private final String    kayttajatunnus;
    private final Date      rekisteroity;
    private String          salasanatiiviste, suola, sahkopostiosoite;
    private Kayttajataso    taso;

    // Vapaaehtoiset kentät:
    private String          nimimerkki, avatar, kuvaus;
    private int             viesteja;   // Johdettu attribuutti. Mielestäni
                                        // viestien lukumäärän säilöminen omana
                                        // kenttänään on järkevää, jottei
                                        // jouduttaisi jatkuvasti tekemään
                                        // yhteenvetokyselyitä (select count(*)
                                        // from viestit where kirjoittaja...).

    static {
        LISAYSPOHJA = "insert into jasenet (numero, tunnus, rekisteroity, "
                + "tiiviste, suola, sposti, taso, nimimerkki, avatar, kuvaus,"
                + "viesteja) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        HAKUPOHJA1   = "select * from jasenet where numero = ?";
        HAKUPOHJA2  = "select * from jasenet where tunnus = ?";
        KELVOLLINEN_SP = Pattern.compile(
                "^([a-z0-9]|([a-z0-9][._-][a-z0-9]))+"
                        + "@([a-z0-9]|([a-z0-9][._-][a-z0-9]))+\\.[a-z]{2,4}$",
                Pattern.CASE_INSENSITIVE).asPredicate();
    }

    private Jasen(final boolean tuore, final int numero,
            final String kayttajatunnus, final Date rekisteroity,
            final String salasanatiiviste, final String suola,
            final String sahkopostiosoite, final Kayttajataso taso,
            final String nimimerkki, final String avatar, final String kuvaus,
            final int viesteja) {
        super(tuore);
        this.kayttajanumero     = numero;
        this.kayttajatunnus     = kayttajatunnus;
        this.rekisteroity       = rekisteroity;
        this.salasanatiiviste   = salasanatiiviste;
        this.suola              = suola;
        this.sahkopostiosoite   = sahkopostiosoite;
        this.taso               = taso;
        this.nimimerkki         = nimimerkki;
        this.avatar             = avatar;
        this.kuvaus             = kuvaus;
        this.viesteja           = viesteja;
    }

    public static Jasen luo(final String kayttajatunnus,
            final String salasanatiiviste, final String suola,
            final String sahkopostiosoite) {
        if (kayttajatunnus == null || kayttajatunnus.isEmpty()
                || kayttajatunnus.length() > 32) {
            throw new IllegalArgumentException("Käyttäjätunnuksen tulee olla "
                    + "epätyhjä korkeintaan 32 merkin jono.");
        }
        if (sahkopostiosoite == null || sahkopostiosoite.isEmpty()
                || sahkopostiosoite.length() > 64
                || !KELVOLLINEN_SP.test(sahkopostiosoite)) {
            throw new IllegalArgumentException("Sähköpostiosoitteen tulee olla"
                    + "epätyhjä --ja järkevä-- korkeintaan 64 merkin jono.");
        }
        return new Jasen(true, 0, kayttajatunnus,
                new Date(System.currentTimeMillis()), salasanatiiviste, suola,
                sahkopostiosoite, Kayttajataso.TAVALLINEN, null, null, null, 0);
    }

    /**
     * Luo Jasen-olion annetusta ResultSet-oliosta, jonka kursori on asetettu
     * halutun monikon kohdalle. <b>Huom.</b> Tämä metodi ei kutsu ResultSet:n
     * metodia <tt>close()</tt>.
     *
     * @param rs Tietokantakyselyn palauttama ResultSet-olio.
     * @return Uusi Jasen.
     */
    static Jasen luo(final ResultSet rs) {
        final int numero, viesteja;
        final String kayttajatunnus, salasanatiiviste, suola, sahkoposti,
                nimimerkki, avatar, kuvaus;
        final Date rekisteroity;
        final Kayttajataso taso;
        try {
            numero              = rs.getInt(1);
            kayttajatunnus      = rs.getString(2);
            rekisteroity        = rs.getDate(3);
            salasanatiiviste    = rs.getString(4);
            suola               = rs.getString(5);
            sahkoposti          = rs.getString(6);
            taso                = Kayttajataso.valueOf(rs.getString(7));
            nimimerkki          = rs.getString(8);
            avatar              = rs.getString(9);
            kuvaus              = rs.getString(10);
            viesteja            = rs.getInt(11);
            return new Jasen(false, numero, kayttajatunnus, rekisteroity,
                    salasanatiiviste, suola, sahkoposti, taso, nimimerkki,
                    avatar, kuvaus, viesteja);
        } catch (SQLException e) {
            Logger.getLogger(Jasen.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    static PreparedStatement hakukysely(final Connection yhteys,
            final int avain) throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(HAKUPOHJA1);
        kysely.setInt(1, avain);
        return kysely;
    }

    static PreparedStatement hakukysely(
            final Connection yhteys, final String avain) throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(HAKUPOHJA2);
        kysely.setString(1, avain);
        return kysely;
    }

    @Override
    PreparedStatement lisayskysely(final Connection yhteys)
            throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(LISAYSPOHJA);
        kysely.setString(1, kayttajatunnus);
        kysely.setDate(  2, rekisteroity);
        kysely.setString(3, salasanatiiviste);
        kysely.setString(4, suola);
        kysely.setString(5, sahkopostiosoite);
        kysely.setString(6, taso.toString());
        kysely.setString(7, nimimerkki);
        kysely.setString(8, avatar);
        kysely.setString(9, kuvaus);
        return kysely;
    }

    @Override
    public String listausnimi() {
        return (nimimerkki == null) ? kayttajatunnus : nimimerkki;
    }

    @Override
    public boolean equals(final Object verrattava) {
        return verrattava != null && verrattava.getClass() == this.getClass()
                && ((Jasen) verrattava).annaKayttajanumero() == kayttajanumero;
    }

    @Override
    public int hashCode() {
        // NetBeansin generoima metodi.
        int hash = 5;
        hash = 23 * hash + this.kayttajanumero;
        return hash;
    }

    /**
     * Onko käyttäjätunnus porttikiellossa.
     *
     * @return <tt>true</tt> joss käyttäjätunnus ei ole porttikiellossa.
     * Virhetilanteissa palautetaan <tt>false</tt>.
     */
    public boolean onPorttikiellossa() {
        try {
            final Connection yhteys = TietokantaDAO.annaYhteys();
            final PreparedStatement kysely = yhteys.prepareStatement("select "
                    + "asetettu, kesto from porttikiellot where kohde = ?");
            kysely.setInt(1, kayttajanumero);
            final ResultSet vastaus = kysely.executeQuery();

            if (!vastaus.next()) {
                TietokantaDAO.suljeYhteydet(yhteys, kysely, vastaus);
                return false;
            }
            final Date alkanut  = vastaus.getDate(1);
            final int kesto     = vastaus.getInt(2);
            if (kesto == 0) {
                // 0 merkitsee toistaiseksi voimassa olevaa porttikieltoa.
                return false;
            }
            final Date paattyy  = new Date(alkanut.getTime() + 60000 * kesto);
            final Date nykyhetki = new Date(System.currentTimeMillis());
            boolean paattynyt = paattyy.compareTo(nykyhetki) <= 0;
            if (paattynyt) {
//                poistaPorttikielto();
            }
            TietokantaDAO.suljeYhteydet(yhteys, kysely, vastaus);
            return paattynyt;
        } catch (SQLException e) {
            Logger.getLogger(Jasen.class.getName()).log(Level.SEVERE, null, e);
            return true;
        }
    }

//    public void asetaPorttikielto(final int kesto) {
////        setPorttikielto(new Date(System.currentTimeMillis()), kesto);
//        TietokantaDAO.paivita("insert into porttikiellot values ('"
//                + kayttajatunnus + "', '"
//                + new Date(System.currentTimeMillis()) + "', " + kesto + ")");
//    }

//    public void poistaPorttikielto() {
//        TietokantaDAO.paivita("delete from porttikiellot where kohde = '"
//                + kayttajatunnus + "'");
//    }

    public int annaKayttajanumero() {
        return kayttajanumero;
    }

    public String annaKayttajatunnus() {
        return kayttajatunnus;
    }

    public Date annaRekisteroity() {
        return rekisteroity;
    }

    public String annaSalasanatiiviste() {
        return salasanatiiviste;
    }

    public void asetaSalasanatiiviste(final String salasanatiiviste) {
        this.salasanatiiviste = salasanatiiviste;
    }

    public String annaSuola() {
        return suola;
    }

    public void asetaSuola(final String suola) {
        this.suola = suola;
    }

    public String annaSahkopostiosoite() {
        return sahkopostiosoite;
    }

    public void asetaSahkopostiosoite(final String sahkopostiosoite) {
        this.sahkopostiosoite = sahkopostiosoite;
    }

    public Kayttajataso annaTaso() {
        return taso;
    }

    public void asetaTaso(final Kayttajataso taso) {
        this.taso = taso;
    }

    public String annaNimimerkki() {
        return nimimerkki;
    }

    public void asetaNimimerkki(final String nimimerkki) {
        this.nimimerkki = nimimerkki;
    }

    public String annaAvatar() {
        return avatar;
    }

    public void asetaAvatar(final String avatar) {
        this.avatar = avatar;
    }

    public String annaKuvaus() {
        return kuvaus;
    }

    public void asetaKuvaus(final String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public int annaViesteja() {
        return viesteja;
    }

    public void asetaViesteja(int viesteja) {
        this.viesteja = viesteja;
    }

}
