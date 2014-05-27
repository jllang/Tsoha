
package mallit.yksilotyypit;

import java.util.Date;
import mallit.rajapinnat.Yksilotyyppi;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Ketju implements Yksilotyyppi {

    private static final String     TAULUN_NIMI = "ketjut";
    private static final String[]   AVAINATTRIBUUTIT = {"tunnus"};
    private final String[]          avainarvot;

    private final int   tunnus;
    private String      aihe;
    private Date        siirretty, moderoitu, lukittu, poistettu;

    private Ketju(final int tunnus, final String aihe, final Date siirretty,
            final Date moderoitu, final Date lukittu, final Date poistettu) {
        this.tunnus     = tunnus;
        this.avainarvot = new String[]{"" + tunnus};
        this.aihe       = aihe;
        this.siirretty  = siirretty;
        this.moderoitu  = moderoitu;
        this.lukittu    = lukittu;
        this.poistettu  = poistettu;
    }

    public static Ketju luo(final int tunnus, final String aihe,
            final Date siirretty, final Date moderoitu, final Date lukittu,
            final Date poistettu) {
        return new Ketju(tunnus, aihe, siirretty, moderoitu, lukittu, poistettu);
    }

    public static Ketju luo(final int tunnus, final String aihe) {
        return luo(tunnus, aihe, new Date(), null, null, null);
    }

    @Override
    public String taulunNimi() {
        return "ketjut";
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
        mjr.append(tunnus);
        mjr.append(", '");
        mjr.append(aihe);
        mjr.append("', '");
        mjr.append(siirretty == null ? "NULL, " : "'" + siirretty + "', ");
        mjr.append(moderoitu == null ? "NULL, " : "'" + moderoitu + "', ");
        mjr.append(lukittu == null ? "NULL, " : "'" + lukittu + "', ");
        mjr.append(poistettu == null ? "NULL" : "'" + poistettu + "'");
        mjr.append(')');
        return mjr.toString();
    }

    public int annaTunnus() {
        return tunnus;
    }

    public String annaAihe() {
        return aihe;
    }

    public void asetaAihe(String aihe) {
        this.aihe = aihe;
    }

    public Date annaSiirretty() {
        return siirretty;
    }

    public void asetaSiirretty(Date siirretty) {
        this.siirretty = siirretty;
    }

    public Date annaModeroitu() {
        return moderoitu;
    }

    public void asetaModeroitu(Date moderoitu) {
        this.moderoitu = moderoitu;
    }

    public Date annaLukittu() {
        return lukittu;
    }

    public void asetaLukittu(Date lukittu) {
        this.lukittu = lukittu;
    }

    public Date annaPoistettu() {
        return poistettu;
    }

    public void asetaPoistettu(Date poistettu) {
        this.poistettu = poistettu;
    }
}
