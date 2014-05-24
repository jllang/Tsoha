/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mallit.relaatiot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import mallit.Tietokanta;
import mallit.tyypit.Kayttajataso;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Jasen {

    public final String     kayttajatunnus;
    public final Date       rekisteroity;
    private String          salasanatiiviste, sahkopostiosoite;
    private Kayttajataso    taso;

    // Vapaaehtoiaseta kentät:
    private String          nimimerkki, avatar, kuvaus;

    private Jasen(final String kayttajatunnus, final String salasanatiiviste,
            final String sahkopostiosoite, final Kayttajataso taso) {
        this.kayttajatunnus     = kayttajatunnus;
        this.rekisteroity       = new Date();
        this.salasanatiiviste   = salasanatiiviste;
        this.sahkopostiosoite   = sahkopostiosoite;
        this.taso               = taso;
    }

    /**
     * Tämä tehdasmetodi luo uuden jäsenolion.
     *
     * @param kayttajatunnus
     * @param salasanatiiviste
     * @param sahkopostiosoite
     * @param taso
     * @return
     */
    public static Jasen luo(final String kayttajatunnus, final String salasanatiiviste,
            final String sahkopostiosoite, final Kayttajataso taso) {
        return new Jasen(kayttajatunnus, salasanatiiviste, sahkopostiosoite, taso);
    }

    public static void vie(final Jasen jasen) {

    }

    public static Jasen[] haeKaikki() throws SQLException {
        Connection yhteys = Tietokanta.annaYhteys();
        PreparedStatement kysely = yhteys.prepareStatement("select * from jasenet");
        ResultSet vastaus = kysely.executeQuery();

        while (vastaus.next()) {
            final String tunnus     = vastaus.getString("tunnus");
            final String tiiviste   = vastaus.getString("tiiviste");
            final Date rekisteroity = vastaus.getDate("rekisteroity");
            final String sahkoposti = vastaus.getString("sposti");
            final Kayttajataso taso = vastaus.getObject("taso", Kayttajataso.class);
        }

        vastaus.close();
        kysely.close();
        yhteys.close();
        return null;
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
