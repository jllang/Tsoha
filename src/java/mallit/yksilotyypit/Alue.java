
package mallit.yksilotyypit;

import java.sql.Date;
import mallit.rajapinnat.Yksilotyyppi;

/**
 *
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Alue implements Yksilotyyppi {

    private static final String     TAULUN_NIMI = "alueet";
    private static final String[]   AVAINATTRIBUUTIT = {"nimi"};
    private final String[]          avainarvot;

    private String  nimi, kuvaus;
    private Date    lukittu, poistettu;

    private Alue(final String nimi, final String kuvaus, final Date lukittu,
            final Date poistettu) {
        this.nimi       = nimi;
        this.avainarvot = new String[]{nimi};
        this.kuvaus     = kuvaus;
        this.lukittu    = lukittu;
        this.poistettu  = poistettu;
    }

    public Alue luo(final String nimi, final String kuvaus, final Date lukittu,
            final Date poistettu) {
        return new Alue(nimi, kuvaus, lukittu, poistettu);
    }

    public Alue luo(final String nimi, final String kuvaus) {
        return luo(nimi, kuvaus, null, null);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        StringBuilder mjr = new StringBuilder();
        mjr.append("('");
        mjr.append(nimi);
        mjr.append("', ");
        mjr.append(kuvaus == null ? "NULL, " : "'" + kuvaus + "', ");
        mjr.append(lukittu == null ? "NULL, " : "'" + lukittu + "', ");
        mjr.append(poistettu == null ? "NULL" : "'" + poistettu + "'");
        mjr.append(')');
        return mjr.toString();
    }

    public String annaNimi() {
        return nimi;
    }

    public void aasetaaNimi(String nimi) {
        this.nimi = nimi;
    }

    public String annaKuvaus() {
        return kuvaus;
    }

    public void asetaKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
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
