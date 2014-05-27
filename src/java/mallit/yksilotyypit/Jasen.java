/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mallit.yksilotyypit;

import java.util.Date;
import mallit.rajapinnat.Yksilotyyppi;
import mallit.tyypit.Kayttajataso;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Jasen implements Yksilotyyppi {

    private static final String     TAULUN_NIMI = "jasenet";
    private static final String[]   AVAINATTRIBUUTIT = {"kayttajatunnus"};
    private final String[]          avainarvot;

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
        this.avainarvot         = new String[]{"'" + kayttajatunnus + "'"};
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
        return luo(kayttajatunnus, new Date(), salasanatiiviste,
                sahkopostiosoite, Kayttajataso.TAVALLINEN, null, null, null);
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
