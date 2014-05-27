
package mallit.yksilotyypit;

import java.sql.Date;
import java.sql.ResultSet;
import mallit.rajapinnat.Yksilotyyppi;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Viesti implements Yksilotyyppi {

    private Ketju       ketju;
    private final Jasen kirjoittaja;
    private final Date  kirjoitettu;
    private Date        muokattu, moderoitu, poistettu;
    private String      sisalto;

    private Viesti(final Ketju ketju, final Jasen kirjoittaja,
            final Date kirjoitettu, final Date muokattu, final Date moderoitu,
            final Date poistettu, final String sisalto) {
        this.ketju          = ketju;
        this.kirjoitettu    = kirjoitettu;
        this.kirjoittaja    = kirjoittaja;
        this.muokattu       = muokattu;
        this.moderoitu      = moderoitu;
        this.poistettu      = poistettu;
        this.sisalto        = sisalto;
    }

    public static Viesti luo(final Ketju ketju, final Jasen kirjoittaja,
            final Date kirjoitettu, final Date muokattu, final Date moderoitu,
            final Date poistettu, final String sisalto) {
        return new Viesti(ketju, kirjoittaja, kirjoitettu, muokattu, moderoitu,
                poistettu, sisalto);
    }

    public static Viesti luo(final Ketju ketju, final Jasen kirjoittaja,
            final String sisalto) {
        return luo(ketju, kirjoittaja, new Date(System.currentTimeMillis()),
                null, null, null, sisalto);
    }
    
    public static Viesti luo(final ResultSet rs) {
        final Ketju ketju;
        final Jasen kirjoittaja;
        final Date kirjoitettu, muokattu, moderoitu, poistettu;
        final String sisalto;
        
        
        return null;
    }

    @Override
    public String annaLisayskysely() {
        return "insert into viestit values " + toString();
    }

    @Override
    public String annaHakukysely(String... avain) {
        return "select * from viestit where ketju_id = " + avain[0] + " and "
                + "kirjoitettu = '" + avain[1] + "'";
    }

    @Override
    public String toString() {
        StringBuilder mjr = new StringBuilder();
        mjr.append('(');
        mjr.append(ketju.annaTunnus());
        mjr.append(", '");
        mjr.append(kirjoitettu);
        mjr.append("', '");
        mjr.append(kirjoittaja);
        mjr.append("', ");
        mjr.append(muokattu == null ? "NULL, " : "'" + muokattu + "', ");
        mjr.append(moderoitu == null ? "NULL, " : "'" + moderoitu + "', ");
        mjr.append(poistettu == null ? "NULL, '" : "'" + poistettu + "', '");
        mjr.append(sisalto);
        mjr.append("'");
        mjr.append(')');
        return mjr.toString();
    }

    public Ketju annaKetju() {
        return ketju;
    }

    public void asetaKetju(Ketju ketju) {
        this.ketju = ketju;
    }

    public Jasen annaKirjoittaja() {
        return kirjoittaja;
    }

    public Date annaKirjoitettu() {
        return kirjoitettu;
    }

    public Date annaMuokattu() {
        return muokattu;
    }

    public void asetaMuokattu(Date muokattu) {
        this.muokattu = muokattu;
    }

    public Date annaModeroitu() {
        return moderoitu;
    }

    public void asetaModeroitu(Date moderoitu) {
        this.moderoitu = moderoitu;
    }

    public Date annaPoistettu() {
        return poistettu;
    }

    public void asetaPoistettu(Date poistettu) {
        this.poistettu = poistettu;
    }

    public String annaSisalto() {
        return sisalto;
    }

    public void asetaSisalto(String sisalto) {
        this.sisalto = sisalto;
    }

}
