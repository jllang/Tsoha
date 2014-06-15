
package mallit.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Mallintaa keskustelualuetta foorumilla.
 *
 * @author John Lång (jllang@cs.helsinki.fi)
 */
public final class Alue extends Yksilotyyppi {

    private static final String LISAYSLAUSE, PAIVITYSLAUSE, TUNNUSHAKU,
            NIMIHAKU, NIMIEN_MAARAN_KYSELY, NIMILISTAUS, LUKUMAARALAUSE;

    private final int   tunnus;
    private String      nimi, kuvaus;
    private Timestamp   lukittu, poistettu;

    static {
        LISAYSLAUSE     = "insert into alueet (nimi, kuvaus) values (?, ?)";
        PAIVITYSLAUSE   = "update alueet set nimi = ?, kuvaus = ?, lukittu = ?,"
                + " poistettu = ? where tunnus = ?";
        TUNNUSHAKU      = "select * from alueet where tunnus = ?";
        NIMIHAKU        = "select * from alueet where nimi = ?";
        NIMIEN_MAARAN_KYSELY = "select count(tunnus) from alueet";
        NIMILISTAUS     = "select nimi from alueet";
        LUKUMAARALAUSE  = "select count(tunnus) from alueet";
    }

    private Alue(final boolean tuore, final int tunnus, final String nimi,
            final String kuvaus, final Timestamp lukittu, final Timestamp poistettu) {
        super(tuore);
        this.tunnus     = tunnus;
        this.nimi       = nimi;
        this.kuvaus     = kuvaus;
        this.lukittu    = lukittu;
        this.poistettu  = poistettu;
    }

    public static Alue luo(final String nimi, final String kuvaus) {
        if (nimi == null || nimi.length() == 0 || nimi.length() > 128) {
            throw new IllegalArgumentException("Alueen nimen tulee olla "
                    + "epätyhjä korkeintaan 128 merkin jono.");
        }
        if (kuvaus != null && kuvaus.length() > 384) {
            throw new IllegalArgumentException("Alueen kuvaus saa olla "
                    + "korkeintaan 384 merkin jono.");
        }
        return new Alue(true, 0, nimi, kuvaus, null, null);
    }

    /**
     * Luo uuden Alue-olion annetusta ResultSet-oliosta, jonka kursori on
     * asetettu halutun monikon kohdalle. <b>Huom.</b> Tämä metodi ei kutsu
     * ResultSet:n metodia <tt>close()</tt>.
     *
     * @param rs Tietokantakyselyn palauttama ResultSet-olio.
     * @return Uusi Alue.
     */
    static Alue luo(final ResultSet rs) {
        final int       tunnus;
        final String    nimi, kuvaus;
        final Timestamp      lukittu, poistettu;
        try {
            tunnus      = rs.getInt(1);
            nimi        = rs.getString(2);
            kuvaus      = rs.getString(3);
            lukittu     = rs.getTimestamp(4);
            poistettu   = rs.getTimestamp(5);
            return new Alue(false, tunnus, nimi, kuvaus, lukittu, poistettu);
        } catch (SQLException e) {
            Logger.getLogger(Alue.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    static PreparedStatement hakukysely(final Connection yhteys,
            final int avain) throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(TUNNUSHAKU);
        kysely.setInt(1, avain);
        return kysely;
    }

    static PreparedStatement hakukysely(final Connection yhteys,
            final String avain) throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(NIMIHAKU);
        kysely.setString(1, avain);
        return kysely;
    }

    /**
     * Palauttaa kaikkien olemassaolevien alueiden nimet merkkijonolistana.
     *
     * @return Merkkijonolista alueista.
     */
    public static String[] annaNimet() {
        int maara                   = 0;
        String[] nimet              = null;
        Connection yhteys           = null;
        PreparedStatement kysely    = null;
        ResultSet vastaus           = null;
        try {
            yhteys  = TietokantaDAO.annaKertayhteys();
            kysely  = yhteys.prepareStatement(NIMIEN_MAARAN_KYSELY);
            vastaus = kysely.executeQuery();
            vastaus.next();
            maara   = vastaus.getInt(1);
            try {vastaus.close();} catch (SQLException e) {}
            try {kysely.close();} catch (SQLException e) {}
            kysely  = yhteys.prepareStatement(NIMILISTAUS);
            vastaus = kysely.executeQuery();
            nimet   = new String[maara];
            for (int i = 0; vastaus.next(); i++) {
                nimet[i] = vastaus.getString(1);
            }
        } catch (SQLException e) {
            Logger.getLogger(Alue.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            TietokantaDAO.sulje(yhteys, kysely, vastaus);
        }
        return nimet;
    }

    public static int lukumaara() {
        // Jälleen tätä toistoa kun pitää olla staattinen metodi...
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

    public List<Ketju> annaKetjut(final int sivunPituus, final int siirto)
            throws SQLException {
        final List<Ketju> paluuarvo = new LinkedList<>();
        final Connection yhteys = TietokantaDAO.annaKertayhteys();
        final PreparedStatement kysely = yhteys.prepareStatement("select "
                + "ketju.tunnus, aihe, muutettu, siirretty, moderoitu, "
                + "ketju.lukittu, ketju.poistettu from alueet inner join "
                + "ketjujen_sijainnit on alue.tunnus = alue_id inner join "
                + "ketjut on ketju_id = ketju.tunnus where alue.tunnus = ? "
                + "order by ? limit ? offset ?");
        kysely.setInt(1, tunnus);
        kysely.setString(2, "muutettu");
        kysely.setInt(3, sivunPituus);
        kysely.setInt(4, siirto);
        final ResultSet vastaus = kysely.executeQuery();
        while (vastaus.next()) {
            paluuarvo.add(Ketju.luo(vastaus));
        }
        TietokantaDAO.sulje(yhteys, kysely, vastaus);
        return paluuarvo;
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
        kysely.setString(1, nimi);
        kysely.setString(2, kuvaus);
    }

    @Override
    void valmisteleUpdate(final PreparedStatement kysely) throws SQLException {
        kysely.setString(1, nimi);
        kysely.setString(2, kuvaus);
        kysely.setTimestamp(3, lukittu);
        kysely.setTimestamp(4, poistettu);
        kysely.setInt(5, tunnus);
    }

    @Override
    public String listausnimi() {
        return nimi;
    }

    public int annaTunnus() {
        return tunnus;
    }

    public String annaNimi() {
        return nimi;
    }

    public void asetaNimi(final String nimi) {
        this.nimi = nimi;
    }

    public String annaKuvaus() {
        return kuvaus;
    }

    public void asetaKuvaus(final String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public Timestamp annaLukittu() {
        return lukittu;
    }

    public void asetaLukittu(final Timestamp lukittu) {
        this.lukittu = lukittu;
    }

    public Timestamp annaPoistettu() {
        return poistettu;
    }

    public void asetaPoistettu(final Timestamp poistettu) {
        this.poistettu = poistettu;
    }

}
