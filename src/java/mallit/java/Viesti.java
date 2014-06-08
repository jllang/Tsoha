
package mallit.java;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Mallintaa viestiä ketjussa. Kyseessä on epäitsenäinen, ketjusta
 * olemassaoloriippuvainen yksilötyyppi.
 *
 * @see Ketju
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Viesti extends Yksilotyyppi {

    private static final String LISAYSPOHJA, HAKUPOHJA;

    private int         ketjunTunnus;
    private final int   kirjoittaja;
    private final Date  kirjoitettu;
    private Date        muokattu, moderoitu, poistettu;
    private String      sisalto;

    static {
//        OLIOKUMPPANI = luo(-1, "Oliokumppani", "Tätä oliota ei viedä tietokantaan.");
        LISAYSPOHJA = "insert into viestit values (?, ?, ?, ?, ?, ?, ?)";
        HAKUPOHJA   = "select * from viestit where ketju_id = ? "
                + "and kirjoitettu = ?";
    }

    private Viesti(final boolean tuore, final int ketjunTunnus,
            final int kirjoittaja, final Date kirjoitettu, final Date muokattu,
            final Date moderoitu, final Date poistettu, final String sisalto) {
        super(tuore);
        this.ketjunTunnus   = ketjunTunnus;
        this.kirjoitettu    = kirjoitettu;
        this.kirjoittaja    = kirjoittaja;
        this.muokattu       = muokattu;
        this.moderoitu      = moderoitu;
        this.poistettu      = poistettu;
        this.sisalto        = sisalto;
    }

    public static Viesti luo(final int ketjunTunnus, final int kirjoittaja,
            final Date kirjoitettu, final String sisalto) {
        if (sisalto == null || sisalto.isEmpty()) {
            throw new IllegalArgumentException("Viesti ei saa olla tyhjä.");
        }
        return new Viesti(true, ketjunTunnus, kirjoittaja, kirjoitettu, null,
                null, null, sisalto);
    }

    public static Viesti luo(final int ketjunTunnus, final int kirjoittaja,
            final String sisalto) {
        return luo(ketjunTunnus, kirjoittaja,
                new Date(System.currentTimeMillis()), sisalto);
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
        final int ketju, kirjoittaja;
        final Date kirjoitettu, muokattu, moderoitu, poistettu;
        final String sisalto;

        try {
            ketju       = rs.getInt(1);
            kirjoittaja = rs.getInt(2);
            kirjoitettu = rs.getDate(3);
            muokattu    = rs.getDate(4);
            moderoitu   = rs.getDate(5);
            poistettu   = rs.getDate(6);
            sisalto     = rs.getString(7);
            return new Viesti(false, ketju, kirjoittaja, kirjoitettu, muokattu,
                    moderoitu, poistettu, sisalto);
        } catch (SQLException e) {
            Logger.getLogger(Viesti.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    static PreparedStatement hakukysely(final Connection yhteys,
            final int ketjunTunnus, final int numero) throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(HAKUPOHJA);
        kysely.setInt(1, ketjunTunnus);
        kysely.setInt(2, numero);
        return kysely;
    }

    @Override
    PreparedStatement lisayskysely(final Connection yhteys)
            throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(LISAYSPOHJA);
        kysely.setInt(1, ketjunTunnus);
        kysely.setDate(2, kirjoitettu);
        kysely.setInt(3, kirjoittaja);
        kysely.setDate(4, muokattu);
        kysely.setDate(5, moderoitu);
        kysely.setDate(6, poistettu);
        kysely.setString(7, sisalto);
        return kysely;
    }

    @Override
    public String listausnimi() {
        throw new UnsupportedOperationException("Viestillä ei ole listausnimeä!");
    }

    public int annaKetjunTunnus() {
        return ketjunTunnus;
    }

    public void asetaKetju(final Ketju ketju) {
        this.ketjunTunnus = ketju.annaTunnus();
    }

    public int annaKirjoittaja() {
        return kirjoittaja;
    }

    public Date annaKirjoitettu() {
        return kirjoitettu;
    }

    public Date annaMuokattu() {
        return muokattu;
    }

    public void asetaMuokattu(final Date muokattu) {
        this.muokattu = muokattu;
    }

    public Date annaModeroitu() {
        return moderoitu;
    }

    public void asetaModeroitu(final Date moderoitu) {
        this.moderoitu = moderoitu;
    }

    public Date annaPoistettu() {
        return poistettu;
    }

    public void asetaPoistettu(final Date poistettu) {
        this.poistettu = poistettu;
    }

    public String annaSisalto() {
        return sisalto;
    }

    public void asetaSisalto(final String sisalto) {
        this.sisalto = sisalto;
    }

}
