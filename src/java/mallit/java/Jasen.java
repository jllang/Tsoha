/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mallit.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Mallintaa rekisteröityyn foorumin käyttäjätunnukseen liittyviä asioita.
 *
 * @author John Lång (jllang@cs.helsinki.fi)
 */
public final class Jasen extends Yksilotyyppi {

    private static final String     LISAYSLAUSE, PAIVITYSLAUSE,
            OLEMASSAOLOLAUSE, HAKULAUSE1, HAKULAUSE2, PORTTIKIELTOLAUSE,
            LUKUMAARALAUSE;
    private static final Predicate<String> KELVOLLINEN_SP;

    private final int       kayttajanumero;
    private final String    kayttajatunnus;
    private final Timestamp rekisteroity;
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
        LISAYSLAUSE     = "insert into jasenet (tunnus, rekisteroity, tiiviste,"
                + " suola, sposti, taso, nimimerkki, avatar, kuvaus, viesteja) "
                + "values (?, ?, ?, ?, ?, ?::Kayttajataso, ?, ?, ?, ?)";
        PAIVITYSLAUSE   = "update jasenet set tiiviste = ?, suola = ?, sposti ="
                + " ?, taso = ?::Kayttajataso, nimimerkki = ?, avatar = ?,"
                + "kuvaus = ?, viesteja = ? where numero = ?";
        OLEMASSAOLOLAUSE = "select exists(select rekisteroity from jasenet "
                + "where tunnus=?)";
        HAKULAUSE1      = "select * from jasenet where numero = ?";
        HAKULAUSE2      = "select * from jasenet where tunnus = ?";
        PORTTIKIELTOLAUSE = "select asetettu, kesto from porttikiellot where "
                + "kohde = ?";
        LUKUMAARALAUSE  = "select count(tunnus) from jasenet";
        KELVOLLINEN_SP  = Pattern.compile(
                "^([a-z0-9]|([a-z0-9][._-][a-z0-9]))+"
                        + "@([a-z0-9]|([a-z0-9][._-][a-z0-9]))+\\.[a-z]{2,4}$",
                Pattern.CASE_INSENSITIVE).asPredicate();
    }

    private Jasen(final boolean tuore, final int numero,
            final String kayttajatunnus, final Timestamp rekisteroity,
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
                new Timestamp(System.currentTimeMillis()), salasanatiiviste,
                suola, sahkopostiosoite, Kayttajataso.TAVALLINEN, null, null,
                null, 0);
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
        final Timestamp rekisteroity;
        final Kayttajataso taso;
        try {
            numero              = rs.getInt(1);
            kayttajatunnus      = rs.getString(2);
            rekisteroity        = rs.getTimestamp(3);
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

    static PreparedStatement olemassaolokysely(final Connection yhteys,
            final String avain) throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(OLEMASSAOLOLAUSE);
        kysely.setString(1, avain);
        return kysely;
    }

    static PreparedStatement hakukysely(final Connection yhteys,
            final int avain) throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(HAKULAUSE1);
        kysely.setInt(1, avain);
        return kysely;
    }

    static PreparedStatement hakukysely(final Connection yhteys,
            final String avain) throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(HAKULAUSE2);
        kysely.setString(1, avain);
        return kysely;
    }

    public static int lukumaara() {
        Connection yhteys           = null;
        PreparedStatement kysely    = null;
        ResultSet vastaus           = null;
        int lukumaara               = 0;
        try {
            yhteys = TietokantaDAO.annaKertayhteys();
            kysely = yhteys.prepareStatement(LUKUMAARALAUSE);
            vastaus = kysely.executeQuery();
            vastaus.next();
            lukumaara = vastaus.getInt(1);
        } catch (SQLException e) {

        } finally {
            TietokantaDAO.sulje(yhteys, kysely, vastaus);
        }
        return lukumaara;
    }

    @Override
    String annaLisayslause() {
        return LISAYSLAUSE;
    }

    @Override
    String annaPaivityslause() {
        return PAIVITYSLAUSE;
    }

    @Override
    void valmisteleLisays(final PreparedStatement kysely) throws SQLException {
        kysely.setString(1, kayttajatunnus);
        kysely.setTimestamp(2, rekisteroity);
        kysely.setString(3, salasanatiiviste);
        kysely.setString(4, suola);
        kysely.setString(5, sahkopostiosoite);
        kysely.setString(6, taso.name());
        kysely.setString(7, nimimerkki);
        kysely.setString(8, avatar);
        kysely.setString(9, kuvaus);
        kysely.setInt(10, viesteja);
    }

    @Override
    void valmisteleUpdate(final PreparedStatement kysely) throws SQLException {
        kysely.setString(1, salasanatiiviste);
        kysely.setString(2, suola);
        kysely.setString(3, sahkopostiosoite);
        kysely.setString(4, taso.name());
        kysely.setString(5, nimimerkki);
        kysely.setString(6, avatar);
        kysely.setString(7, kuvaus);
        kysely.setInt(8, viesteja);
        kysely.setInt(9, kayttajanumero);
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
        Connection yhteys           = null;
        PreparedStatement kysely    = null;
        ResultSet vastaus           = null;
        boolean porttikielto        = true;
        try {
            yhteys = TietokantaDAO.annaKertayhteys();
            kysely = yhteys.prepareStatement(PORTTIKIELTOLAUSE);
            kysely.setInt(1, kayttajanumero);
            vastaus = kysely.executeQuery();

            if (!vastaus.next()) {
                porttikielto = false;
                // Olisipa meillä goto...
            }
            if (porttikielto) {
                final Timestamp alkanut = vastaus.getTimestamp(1);
                final int kesto = vastaus.getInt(2);
                if (kesto != 0) {
                    // 0 merkitsee toistaiseksi voimassa olevaa porttikieltoa.
                    final Timestamp paattyy = new Timestamp(alkanut.getTime()
                            + 60000 * kesto);
                    final Timestamp nykyhetki = new Timestamp(
                            System.currentTimeMillis());
                    porttikielto = paattyy.compareTo(nykyhetki) <= 0;
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(Jasen.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            TietokantaDAO.sulje(yhteys, kysely, vastaus);
        }
        return porttikielto;
    }

    public int annaKayttajanumero() {
        return kayttajanumero;
    }

    public String annaKayttajatunnus() {
        return kayttajatunnus;
    }

    public Timestamp annaRekisteroity() {
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

    public void kasvataViesteja() {
        viesteja++;
    }

    public void vahennaViesteja() {
        viesteja--;
    }

}
