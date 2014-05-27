
package mallit.yksilotyypit;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mallit.rajapinnat.Yksilotyyppi;

/**
 *
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Alue implements Yksilotyyppi {

    private String  nimi, kuvaus;
    private Date    lukittu, poistettu;

    private Alue(final String nimi, final String kuvaus, final Date lukittu,
            final Date poistettu) {
        this.nimi       = nimi;
        this.kuvaus     = kuvaus;
        this.lukittu    = lukittu;
        this.poistettu  = poistettu;
    }

    public static Alue luo(final String nimi, final String kuvaus, final Date lukittu,
            final Date poistettu) {
        return new Alue(nimi, kuvaus, lukittu, poistettu);
    }

    public static Alue luo(final String nimi, final String kuvaus) {
        return luo(nimi, kuvaus, null, null);
    }
    
    public static Alue luo(final ResultSet rs) {
        final String nimi, kuvaus;
        final Date lukittu, poistettu;
        try {
            nimi        = rs.getString("nimi");
            kuvaus      = rs.getString("kuvaus");
            lukittu     = rs.getDate("lukittu");
            poistettu   = rs.getDate("poistettu");
            return luo(nimi, kuvaus, lukittu, poistettu);
        } catch (SQLException e) {
            Logger.getLogger(Alue.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public String annaLisayskysely() {
        return "insert into alueet values " + toString(); 
    }

    @Override
    public String annaHakukysely(String... avain) {
        return "select * from alueet where nimi = '" + avain[0] + "'";
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
