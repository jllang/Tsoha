
package mallit.yksilotyypit;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Viesti extends Yksilotyyppi {

//    public static final Viesti OLIOKUMPPANI;
    private static final String LISAYSPOHJA, HAKUPOHJA;

    private int             ketjunTunnus;
    private final String    kirjoittaja;
    private final Date      kirjoitettu;
    private Date            muokattu, moderoitu, poistettu;
    private String          sisalto;

    static {
//        OLIOKUMPPANI = luo(-1, "Oliokumppani", "Tätä oliota ei viedä tietokantaan.");
        LISAYSPOHJA = "insert into viestit values (?, ?, ?, ?, ?, ?, ?)";
        HAKUPOHJA   = "select * from viestit where ketju_id = ? "
                + "and kirjoitettu = ?";
    }

    private Viesti(final boolean tuore, final int ketjunTunnus,
            final String kirjoittaja, final Date kirjoitettu,
            final Date muokattu, final Date moderoitu, final Date poistettu,
            final String sisalto) {
        super(tuore);
        this.ketjunTunnus   = ketjunTunnus;
        this.kirjoitettu    = kirjoitettu;
        this.kirjoittaja    = kirjoittaja;
        this.muokattu       = muokattu;
        this.moderoitu      = moderoitu;
        this.poistettu      = poistettu;
        this.sisalto        = sisalto;
    }

//    public static Viesti luo(final boolean tuore, final int ketjunTunnus,
//            final String kirjoittaja, final Date kirjoitettu,
//            final Date muokattu, final Date moderoitu, final Date poistettu,
//            final String sisalto) {
//        return new Viesti(tuore, ketjunTunnus, kirjoittaja, kirjoitettu, muokattu,
//                moderoitu, poistettu, sisalto);
//    }

    public static Viesti luo(final int ketjunTunnus, final String kirjoittaja,
            final String sisalto) {
        return new Viesti(true, ketjunTunnus, kirjoittaja,
                new Date(System.currentTimeMillis()), null, null, null, sisalto);
    }

    /**
     * Luo Viesti-olion annetusta ResultSet-oliosta, jonka kursori on asetettu
     * halutun monikon kohdalle. <b>Huom.</b> Tämä metodi ei kutsu ResultSet:n
     * metodia <tt>close()</tt>.
     *
     * @param rs Tietokantakyselyn palauttama ResultSet-olio.
     * @return Uusi Viesti.
     */
    public static Viesti luo(final ResultSet rs) {
        final int ketju;
        final Date kirjoitettu, muokattu, moderoitu, poistettu;
        final String kirjoittaja, sisalto;

        try {
            ketju       = rs.getInt("ketju_id");
            kirjoittaja = rs.getString("kirjoittaja");
            kirjoitettu = rs.getDate("kirjoitettu");
            muokattu    = rs.getDate("muokattu");
            moderoitu   = rs.getDate("moderoitu");
            poistettu   = rs.getDate("poistettu");
            sisalto     = rs.getString("sisalto");
            return new Viesti(false, ketju, kirjoittaja, kirjoitettu, muokattu,
                    moderoitu, poistettu, sisalto);
        } catch (SQLException e) {
            Logger.getLogger(Viesti.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

//    public static Viesti hae(final String nimi) {
//        final ResultSet vastaus = TietokantaDAO.hae(
//                OLIOKUMPPANI.annaHakukysely(nimi));
//        final Viesti viesti;
//        try {
//            vastaus.next();
//            viesti = luo(vastaus);
//            vastaus.close();
//        } catch (SQLException e) {
//            Logger.getLogger(Viesti.class.getName()).log(Level.SEVERE, null, e);
//            return null;
//        }
//        return viesti;
//    }

//    @Override
//    public void tallenna() {
//        if (this != OLIOKUMPPANI) {
//            TietokantaDAO.paivita(annaLisayskysely());
//        } else {
//            System.err.println("Oliokumppani yritettiin viedä tietokantaan!");
//        }
//    }

//    @Override
    public static PreparedStatement hakukysely(final Connection yhteys,
            final int ketjunTunnus, final int numero) throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(HAKUPOHJA);
        kysely.setInt(1, ketjunTunnus);
        kysely.setInt(2, numero);
        return kysely;
    }

    @Override
    public PreparedStatement lisayskysely(final Connection yhteys)
            throws SQLException {
        final PreparedStatement kysely = yhteys.prepareStatement(LISAYSPOHJA);
        kysely.setInt(1, ketjunTunnus);
        kysely.setDate(2, kirjoitettu);
        kysely.setString(3, kirjoittaja);
        kysely.setDate(4, muokattu);
        kysely.setDate(5, moderoitu);
        kysely.setDate(6, poistettu);
        kysely.setString(7, sisalto);
        return kysely;
    }

    @Override
    public String toString() {
        StringBuilder mjr = new StringBuilder();
        mjr.append('(');
        mjr.append(ketjunTunnus);
        mjr.append(", '");
        mjr.append(kirjoitettu);
        mjr.append("', '");
        mjr.append(kirjoittaja);
        mjr.append("', ");
        mjr.append(muokattu == null ? "null, " : "'" + muokattu + "', ");
        mjr.append(moderoitu == null ? "null, " : "'" + moderoitu + "', ");
        mjr.append(poistettu == null ? "null, '" : "'" + poistettu + "', '");
        mjr.append(sisalto);
        mjr.append("'");
        mjr.append(')');
        return mjr.toString();
    }

    public int annaKetjunTunnus() {
        return ketjunTunnus;
    }

    public void asetaKetju(final Ketju ketju) {
        this.ketjunTunnus = ketju.annaTunnus();
    }

    public String annaKirjoittaja() {
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
