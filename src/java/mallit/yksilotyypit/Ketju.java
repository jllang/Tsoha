
package mallit.yksilotyypit;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mallit.rajapinnat.Yksilotyyppi;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Ketju implements Yksilotyyppi {
    
    private final int   tunnus;
    private String      aihe;
    private Date        siirretty, moderoitu, lukittu, poistettu;

    private Ketju(final int tunnus, final String aihe, final Date siirretty,
            final Date moderoitu, final Date lukittu, final Date poistettu) {
        this.tunnus     = tunnus;
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
        return luo(tunnus, aihe, new Date(System.currentTimeMillis()), null,
                null, null);
    }
    
    public static Ketju luo(final ResultSet rs) {
        final int       tunnus;
        final String    aihe;
        final Date      siirretty, moderoitu, lukittu, poistettu;
        try {
            tunnus      = rs.getInt("tunnus");
            aihe        = rs.getString("aihe");
            siirretty   = rs.getDate("siirretty");
            moderoitu   = rs.getDate("moderoitu");
            lukittu     = rs.getDate("lukittu");
            poistettu   = rs.getDate("poistettu");
            return luo(tunnus, aihe, siirretty, moderoitu, lukittu, poistettu);
        } catch (SQLException e) {
            Logger.getLogger(Jasen.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public String annaLisayskysely() {
        return "insert into ketjut values " + toString();
    }

    @Override
    public String annaHakukysely(String... avain) {
        return "select * from ketjtut where tunnus = " + avain[0];
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
