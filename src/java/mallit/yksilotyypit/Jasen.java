/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mallit.yksilotyypit;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mallit.rajapinnat.Yksilotyyppi;
import mallit.tyypit.Kayttajataso;
import static mallit.yksilotyypit.Alue.luo;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Jasen implements Yksilotyyppi {

    private final String    kayttajatunnus;
    private final Date      rekisteroity;
    private String          salasanatiiviste, sahkopostiosoite;
    private Kayttajataso    taso;

    // Vapaaehtoiaset kentät:
    private String          nimimerkki, avatar, kuvaus;

    private Jasen(final String kayttajatunnus, final Date rekisteroity,
            final String salasanatiiviste, final String sahkopostiosoite,
            final Kayttajataso taso, final String nimimerkki,
            final String avatar, final String kuvaus) {
        this.kayttajatunnus     = kayttajatunnus;
        this.rekisteroity       = rekisteroity;
        this.salasanatiiviste   = salasanatiiviste;
        this.sahkopostiosoite   = sahkopostiosoite;
        this.taso               = taso;
        this.nimimerkki         = nimimerkki;
        this.avatar             = avatar;
        this.kuvaus             = kuvaus;
    }

    public static Jasen luo(final String kayttajatunnus, final Date rekisteroity,
            final String salasanatiiviste, final String sahkopostiosoite,
            final Kayttajataso taso, final String nimimerkki,
            final String avatar, final String kuvaus) {
        return new Jasen(kayttajatunnus, rekisteroity, salasanatiiviste,
                sahkopostiosoite, taso, nimimerkki, avatar, kuvaus);
    }

    public static Jasen luo(final String kayttajatunnus,
            final String salasanatiiviste,
            final String sahkopostiosoite) {
        return luo(kayttajatunnus, new Date(System.currentTimeMillis()),
                salasanatiiviste, sahkopostiosoite, Kayttajataso.TAVALLINEN,
                null, null, null);
    }
    
    public static Jasen luo(final ResultSet rs) {
        final String kayttajatunnus, salasanatiiviste, sahkoposti, nimimerkki, avatar,
                kuvaus;
        final Date rekisteroity;
        final Kayttajataso taso;
        try {
            kayttajatunnus      = rs.getString("tunnus");
            rekisteroity        = rs.getDate("rekisteroity");
            salasanatiiviste    = rs.getString("tiiviste");
            sahkoposti          = rs.getString("sposti");
            taso                = rs.getObject("taso", Kayttajataso.class);
            nimimerkki          = rs.getString("nimimerkki");
            avatar              = rs.getString("avatar");
            kuvaus              = rs.getString("kuvaus");
            return luo(kayttajatunnus, rekisteroity, salasanatiiviste, sahkoposti,
                    taso, nimimerkki, avatar, kuvaus);
        } catch (SQLException e) {
            Logger.getLogger(Jasen.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public String annaLisayskysely() {
        return "insert into jasenet values " + toString();
    }

    @Override
    public String annaHakukysely(String... avain) {
        return "select * from jasenet where kayttajatunnus = '" + avain[0] + "'";
    }

    @Override
    public String toString() {
        final StringBuilder mjr = new StringBuilder();
        mjr.append("('");
        mjr.append(kayttajatunnus);
        mjr.append("', '");
        mjr.append(rekisteroity);
        mjr.append("', '");
        mjr.append(salasanatiiviste);
        mjr.append("', '");
        mjr.append(sahkopostiosoite);
        mjr.append("', '");
        mjr.append(taso);
        mjr.append("', ");
        mjr.append(nimimerkki == null ?
                "NULL, " : "'" + nimimerkki + "', ");
        mjr.append(avatar == null ?
                "NULL, " : "'" + avatar + "', ");
        mjr.append(kuvaus == null ?
                "NULL)" : "'" + kuvaus + ")");
        return mjr.toString();
    }

    public String annaKayttajatunnus() {
        return kayttajatunnus;
    }

    public Date annaRekisteroity() {
        return rekisteroity;
    }

    public String annaSalasanatiiviste() {
        return salasanatiiviste;
    }

    public void asetaSalasanatiiviste(final String salasanatiiviste) {
        this.salasanatiiviste = salasanatiiviste;
    }

    public String annaSahkopostiosoite() {
        return sahkopostiosoite;
    }

    public void asetaSahkopostiosoite(final String sahkopostiosoite) {
        this.sahkopostiosoite = sahkopostiosoite;
    }

    public Kayttajataso annaTaso() {
        return taso;
    }

    public void asetaTaso(final Kayttajataso taso) {
        this.taso = taso;
    }

    public String annaNimimerkki() {
        return nimimerkki;
    }

    public void asetaNimimerkki(final String nimimerkki) {
        this.nimimerkki = nimimerkki;
    }

    public String annaAvatar() {
        return avatar;
    }

    public void asetaAvatar(final String avatar) {
        this.avatar = avatar;
    }

    public String annaKuvaus() {
        return kuvaus;
    }

    public void asetaKuvaus(final String kuvaus) {
        this.kuvaus = kuvaus;
    }
}
