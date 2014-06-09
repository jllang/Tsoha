
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

    private static final String LISAYSLAUSE, PAIVITYSLAUSE, HAKULAUSE;

    private final int   ketjunTunnus, numero, kirjoittaja;
    private final Date  kirjoitettu;
    private Date        muokattu, moderoitu, poistettu;
    private String      sisalto;

    static {
        LISAYSLAUSE     = "insert into viestit (ketju_id, numero, kirjoittaja,"
                + "kirjoitettu, sisalto) values (?, ?, ?, ?, ?)";
        PAIVITYSLAUSE   = "update viestit set muokattu = ?, moderoitu = ?,"
                + " poistettu = ?, sisalto = ? where ketju_id = ? and numero = "
                + "?";
        HAKULAUSE       = "select * from viestit where ketju_id = ? "
                + "and kirjoitettu = ?";
    }

    private Viesti(final boolean tuore, final int ketjunTunnus,
            final int numero, final int kirjoittaja, final Date kirjoitettu,
            final Date muokattu, final Date moderoitu, final Date poistettu,
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
            final int kirjoittaja, final Date kirjoitettu,
            final String sisalto) {
        if (sisalto == null || sisalto.isEmpty()) {
            throw new IllegalArgumentException("Viesti ei saa olla tyhjä.");
        }
        return new Viesti(true, ketjunTunnus, numero, kirjoittaja, kirjoitettu,
                null, null, null, sisalto);
    }

    public static Viesti luo(final int ketjunTunnus, final int numero,
            final int kirjoittaja, final String sisalto) {
        return luo(ketjunTunnus, numero, kirjoittaja,
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
        final int ketju, numero, kirjoittaja;
        final Date kirjoitettu, muokattu, moderoitu, poistettu;
        final String sisalto;

        try {
            ketju       = rs.getInt(1);
            numero      = rs.getInt(2);
            kirjoittaja = rs.getInt(3);
            kirjoitettu = rs.getDate(4);
            muokattu    = rs.getDate(5);
            moderoitu   = rs.getDate(6);
            poistettu   = rs.getDate(7);
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

    @Override
    PreparedStatement lisayskysely(final Connection yhteys)
            throws SQLException {
        final PreparedStatement kysely;
        if (onTuore()) {
            kysely = yhteys.prepareStatement(LISAYSLAUSE);
            kysely.setInt(1, ketjunTunnus);
            kysely.setInt(2, numero);
            kysely.setInt(3, kirjoittaja);
            kysely.setDate(4, kirjoitettu);
            kysely.setString(5, sisalto);
        } else {
            kysely = yhteys.prepareStatement(PAIVITYSLAUSE);
            kysely.setDate(1, muokattu);
            kysely.setDate(2, moderoitu);
            kysely.setDate(3, poistettu);
            kysely.setString(4, sisalto);
            kysely.setInt(5, ketjunTunnus);
            kysely.setInt(6, numero);
        }
        return kysely;
    }

    @Override
    public String listausnimi() {
        throw new UnsupportedOperationException("Viestillä ei ole listausnimeä!");
    }

    public int annaKetjunTunnus() {
        return ketjunTunnus;
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
