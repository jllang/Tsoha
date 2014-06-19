
package mallit.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Mallintaa viestiä ketjussa. Kyseessä on epäitsenäinen, ketjusta
 * olemassaoloriippuvainen yksilötyyppi.
 *
 * @see Ketju
 * @author John Lång (jllang@cs.helsinki.fi)
 */
public final class Viesti extends Yksilotyyppi {

    private static final String LISAYSLAUSE, PAIVITYSLAUSE, HAKULAUSE,
            LUKUMAARALAUSE;

    private final int       ketjunTunnus, numero, kirjoittaja;
    private final Timestamp kirjoitettu;
    private Timestamp       muokattu, moderoitu, poistettu;
    private String          sisalto;

    static {
        LISAYSLAUSE     = "insert into viestit (ketju_id, numero, kirjoittaja,"
                + "kirjoitettu, sisalto) values (?, ?, ?, ?, ?)";
        PAIVITYSLAUSE   = "update viestit set muokattu = ?, moderoitu = ?,"
                + " poistettu = ?, sisalto = ? where ketju_id = ? and numero = "
                + "?";
        HAKULAUSE       = "select * from viestit where ketju_id = ? "
                + "and numero = ?";
        LUKUMAARALAUSE  = "select count(*) from viestit join ketjut on "
                + "viestit.ketju_id = ketjut.tunnus where ketjut.poistettu is "
                + "null and viestit.poistettu is null";
    }

    private Viesti(final boolean tuore, final int ketjunTunnus,
            final int numero, final int kirjoittaja, final Timestamp kirjoitettu,
            final Timestamp muokattu, final Timestamp moderoitu, final Timestamp poistettu,
            final String sisalto) {
        super(tuore);
        this.ketjunTunnus   = ketjunTunnus;
        this.numero         = numero;
        this.kirjoitettu    = kirjoitettu;
        this.kirjoittaja    = kirjoittaja;
        this.muokattu       = muokattu;
        this.moderoitu      = moderoitu;
        this.poistettu      = poistettu;
        this.sisalto        = sisalto;
    }

    public static Viesti luo(final int ketjunTunnus, final int numero,
            final int kirjoittaja, final Timestamp kirjoitettu,
            final String sisalto) {
        if (sisalto == null || sisalto.isEmpty()) {
            throw new IllegalArgumentException("Viesti ei saa olla tyhjä.");
        }
        return new Viesti(true, ketjunTunnus, numero, kirjoittaja, kirjoitettu,
                null, null, null, sisalto);
    }

    /**
     * Luo Viesti-olion annetusta ResultSet-oliosta, jonka kursori on asetettu
     * halutun monikon kohdalle. <b>Huom.</b> Tämä metodi ei kutsu ResultSet:n
     * metodia <tt>close()</tt>.
     *
     * @param rs Tietokantakyselyn palauttama ResultSet-olio.
     * @return Uusi Viesti.
     */
    static Viesti luo(final ResultSet rs) {
        final int ketju, numero, kirjoittaja;
        final Timestamp kirjoitettu, muokattu, moderoitu, poistettu;
        final String sisalto;

        try {
            ketju       = rs.getInt(1);
            numero      = rs.getInt(2);
            kirjoittaja = rs.getInt(3);
            kirjoitettu = rs.getTimestamp(4);
            muokattu    = rs.getTimestamp(5);
            moderoitu   = rs.getTimestamp(6);
            poistettu   = rs.getTimestamp(7);
            sisalto     = rs.getString(8);
            return new Viesti(false, ketju, numero, kirjoittaja, kirjoitettu,
                    muokattu, moderoitu, poistettu, sisalto);
        } catch (SQLException e) {
            Logger.getLogger(Viesti.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    static PreparedStatement hakukysely(final Connection yhteys,
            final int ketjunTunnus, final int numero) throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(HAKULAUSE);
        kysely.setInt(1, ketjunTunnus);
        kysely.setInt(2, numero);
        return kysely;
    }

    /**
     * Kuinka monta viestiä tietokannassa on. Mukaan ei lasketa poistetuksi
     * merkittyjä viestejä eikä viestejä poistetuksi merkityissä ketjuissa.
     *
     * @return Poistamattomien viestien lukumäärä.
     */
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
        kysely.setInt(1, ketjunTunnus);
        kysely.setInt(2, numero);
        kysely.setInt(3, kirjoittaja);
        kysely.setTimestamp(4, kirjoitettu);
        kysely.setString(5, sisalto);
    }

    @Override
    void valmisteleUpdate(final PreparedStatement kysely) throws SQLException {
        kysely.setTimestamp(1, muokattu);
        kysely.setTimestamp(2, moderoitu);
        kysely.setTimestamp(3, poistettu);
        kysely.setString(4, sisalto);
        kysely.setInt(5, ketjunTunnus);
        kysely.setInt(6, numero);
    }

    @Override
    public String listausnimi() {
        throw new UnsupportedOperationException("Viestillä ei ole listausnimeä!");
    }

    public int annaNumero() {
        return numero;
    }

    public int annaKetjunTunnus() {
        return ketjunTunnus;
    }

    public int annaKirjoittaja() {
        return kirjoittaja;
    }

    public Timestamp annaKirjoitettu() {
        return kirjoitettu;
    }

    public Timestamp annaMuokattu() {
        return muokattu;
    }

    public void asetaMuokattu(final Timestamp muokattu) {
        this.muokattu = muokattu;
    }

    public Timestamp annaModeroitu() {
        return moderoitu;
    }

    public void asetaModeroitu(final Timestamp moderoitu) {
        this.moderoitu = moderoitu;
    }

    public Timestamp annaPoistettu() {
        return poistettu;
    }

    public void asetaPoistettu(final Timestamp poistettu) {
        this.poistettu = poistettu;
    }

    public String annaSisalto() {
        return sisalto;
    }

    public void asetaSisalto(final String sisalto) {
        this.sisalto = sisalto;
    }

}
