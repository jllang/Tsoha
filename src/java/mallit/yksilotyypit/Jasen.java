/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mallit.yksilotyypit;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import mallit.TietokantaDAO;
import mallit.tyypit.Kayttajataso;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Jasen extends Yksilotyyppi {

    private static final String     LISAYSPOHJA, HAKUPOHJA;
    private static final Predicate<String> KELVOLLINEN_SP;

    private final String    kayttajatunnus;
    private final Date      rekisteroity;
    private String          salasanatiiviste, suola, sahkopostiosoite;
    private Kayttajataso    taso;

    // Vapaaehtoiset kentät:
    private String          nimimerkki, avatar, kuvaus;

    static {
        LISAYSPOHJA = "insert into jasenet values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        HAKUPOHJA   = "select * from jasenet where tunnus = ?";
        KELVOLLINEN_SP = Pattern.compile(
                "^([a-z0-9]|([a-z0-9][._-][a-z0-9]))+"
                        + "@([a-z0-9]|([a-z0-9][._-][a-z0-9]))+\\.[a-z]{2,4}$",
                Pattern.CASE_INSENSITIVE).asPredicate();
    }

    private Jasen(final boolean tuore, final String kayttajatunnus,
            final Date rekisteroity, final String salasanatiiviste,
            final String suola, final String sahkopostiosoite,
            final Kayttajataso taso, final String nimimerkki,
            final String avatar, final String kuvaus) {
        super(tuore);
        this.kayttajatunnus     = kayttajatunnus;
        this.rekisteroity       = rekisteroity;
        this.salasanatiiviste   = salasanatiiviste;
        this.suola              = suola;
        this.sahkopostiosoite   = sahkopostiosoite;
        this.taso               = taso;
        this.nimimerkki         = nimimerkki;
        this.avatar             = avatar;
        this.kuvaus             = kuvaus;
    }

    public static Jasen luo(final String kayttajatunnus,
            final String salasanatiiviste, final String suola,
            final String sahkopostiosoite) {
        if (kayttajatunnus == null || kayttajatunnus.length() == 0
                || kayttajatunnus.length() > 64) {
            throw new IllegalArgumentException("Käyttäjätunnuksen tulee olla "
                    + "epätyhjä korkeintaan 64 merkin jono.");
        }
        if (sahkopostiosoite == null || sahkopostiosoite.length() == 0
                || sahkopostiosoite.length() > 128
                || !KELVOLLINEN_SP.test(sahkopostiosoite)) {
            throw new IllegalArgumentException("Sähköpostiosoitteen tulee olla"
                    + "epätyhjä --ja järkevä-- korkeintaan 128 merkin jono.");
        }
        return new Jasen(true, kayttajatunnus,
                new Date(System.currentTimeMillis()), salasanatiiviste, suola,
                sahkopostiosoite, Kayttajataso.TAVALLINEN, null, null, null);
    }

    /**
     * Luo Jasen-olion annetusta ResultSet-oliosta, jonka kursori on asetettu
     * halutun monikon kohdalle. <b>Huom.</b> Tämä metodi ei kutsu ResultSet:n
     * metodia <tt>close()</tt>.
     *
     * @param rs Tietokantakyselyn palauttama ResultSet-olio.
     * @return Uusi Jasen.
     */
    public static Jasen luo(final ResultSet rs) {
        final String kayttajatunnus, salasanatiiviste, suola, sahkoposti,
                nimimerkki, avatar, kuvaus;
        final Date rekisteroity;
        final Kayttajataso taso;
        try {
            kayttajatunnus      = rs.getString(1);
            rekisteroity        = rs.getDate(2);
            salasanatiiviste    = rs.getString(3);
            suola               = rs.getString(4);
            sahkoposti          = rs.getString(5);
            taso                = Kayttajataso.valueOf(rs.getString(6));
            nimimerkki          = rs.getString(7);
            avatar              = rs.getString(8);
            kuvaus              = rs.getString(9);
            return new Jasen(false, kayttajatunnus, rekisteroity,
                    salasanatiiviste, suola, sahkoposti, taso, nimimerkki,
                    avatar, kuvaus);
        } catch (SQLException e) {
            Logger.getLogger(Jasen.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    public static PreparedStatement hakukysely(final Connection yhteys,
            final String avain) throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(HAKUPOHJA);
        kysely.setString(1, avain);
        return kysely;
    }

    @Override
    public PreparedStatement lisayskysely(final Connection yhteys)
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

    public boolean onPorttikiellossa() throws SQLException {
        // TODO: Fiksaa tämä käyttämään kysymysmerkkiä:
        final ResultSet vastaus = TietokantaDAO.hae("select asetettu, kesto"
                + " from porttikiellot where kohde = '" + kayttajatunnus + "'");
        if (!vastaus.next()) {
            vastaus.close();
            return false;
        }
        final Date alkanut = vastaus.getDate("asetettu");
        final Date paattyy = new Date(
                alkanut.getTime() + 60000 * vastaus.getInt("kesto"));
        final Date nykyhetki = new Date(System.currentTimeMillis());
        vastaus.close();
        boolean paattynyt = paattyy.compareTo(nykyhetki) <= 0;
        if (paattynyt) {
            poistaPorttikielto();
        }
        return paattynyt;
    }

    public void asetaPorttikielto(final int kesto) {
//        asetaPorttikielto(new Date(System.currentTimeMillis()), kesto);
        TietokantaDAO.paivita("insert into porttikiellot values ('"
                + kayttajatunnus + "', '"
                + new Date(System.currentTimeMillis()) + "', " + kesto + ")");
    }

    public void poistaPorttikielto() {
        TietokantaDAO.paivita("delete from porttikiellot where kohde = '"
                + kayttajatunnus + "'");
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
}
