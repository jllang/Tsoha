
package mallit.yksilotyypit;

import java.util.Date;
import mallit.rajapinnat.Yksilotyyppi;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Viesti implements Yksilotyyppi {

    private static final String     TAULUN_NIMI = "viestit";
    private static final String[]   AVAINATTRIBUUTIT = {"ketju", "kirjoitettu"};
    private final String[]          avainarvot;

    private Ketju       ketju;
    private final Jasen kirjoittaja;
    private final Date  kirjoitettu;
    private Date        muokattu, moderoitu, poistettu;
    private String      sisalto;

    private Viesti(final Ketju ketju, final Jasen kirjoittaja,
            final Date kirjoitettu, final Date muokattu, final Date moderoitu,
            final Date poistettu, final String sisalto) {
        this.ketju          = ketju;
        final String ketjunTunnus = "" + ketju.annaTunnus();
        this.kirjoitettu    = kirjoitettu;
        this.avainarvot     = new String[]{ketjunTunnus,kirjoitettu.toString()};
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
        return luo(ketju, kirjoittaja, new Date(), null, null, null, sisalto);
    }

    @Override
    public String taulunNimi() {
        return TAULUN_NIMI;
    }

    @Override
    public String[] avainattribuutit() {
        return AVAINATTRIBUUTIT;
    }

    @Override
    public String[] avainarvot() {
        return avainarvot;
    }

    @Override
    public String toString() {
        StringBuilder mjr = new StringBuilder();
        mjr.append('(');
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
