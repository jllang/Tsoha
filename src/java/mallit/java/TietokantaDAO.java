package mallit.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.transaction.TransactionRolledbackException;

/**
 * Tämä luokka vastaa pääosin yksilötyyppien tuonnista ja viennistä tietokantaan.
 * Lisäksi luokka käsittelee monimutkaisemmat usean taulun transaktiot.
 *
 * @author John Lång (jllang@cs.helsinki.fi)
 */
public final class TietokantaDAO {

    private static final String ALUE_KETJU_LIITOS, KETJUN_TUNNUKSEN_HAKU,
            ALUETUNNUSHAKU1, ALUETUNNUSHAKU2;

    private static DataSource yhteydet;

    static {
        ALUE_KETJU_LIITOS       = "insert into ketjujen_sijainnit values (?, ?)";
        KETJUN_TUNNUKSEN_HAKU   = "select tunnus from ketjut where aihe = ?";
        ALUETUNNUSHAKU1         = "select tunnus from alueet where nimi in (?";
        ALUETUNNUSHAKU2         = "select tunnus from alueet where nimi = ?";
        try {
            InitialContext cxt = new InitialContext();
            yhteydet = (DataSource) cxt.lookup("java:/comp/env/jdbc/tietokanta");
        } catch (NamingException e) {
            Logger.getLogger(TietokantaDAO.class.getName()).log(Level.SEVERE,
                    null, e);
        }
    }

    static Connection annaKertayhteys() throws SQLException {
        return yhteydet.getConnection();
    }

    /**
     * Palauttaa Connection-olion, jolta on poistettu autocommit käytöstä.
     *
     * @return Uusi yhteys
     * @throws SQLException
     */
    static Connection annaTransaktioyhteys() throws SQLException {
        final Connection yhteys = TietokantaDAO.annaKertayhteys();
        yhteys.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        yhteys.setAutoCommit(false);
        return yhteys;
    }

   /**
     * Tallentaa annettujen olioiden tilan tietokantaan yhtenä transaktiona. Jos
     * jonkin olion vienti tietokantaan epäonnistuu, transaktio perutaan.
     * Tietokanta jää eheään tilaan kun tämä metodi on suoritettu.
     *
     * @param tietokohteet Tietokohteet, joiden kenttien arvot viedään
     * tietokantaan.
     * @return <tt>true</tt> joss kaikki annetut tietokohteet saatiin viedyksi
     * tietokantaan.
     */
    public static boolean vie(final Yksilotyyppi... tietokohteet) {
        Connection yhteys           = null;
        PreparedStatement kysely    = null;
        boolean onnistui            = true;
        try {
            yhteys = annaTransaktioyhteys();
            // Kyselyitä ei voida tehdä batchissa koska tietokohteet eivät ole
            // välttämättä samaa tyyppiä (eri luokilla kun on erilaiset kyselyt).
            // Joka tapauksessa tässä nautitaan transaktion tarjoamasta ACID:sta.
            for (Yksilotyyppi tietokohde : tietokohteet) {
                kysely = yhteys.prepareStatement(tietokohde.paivityslause());
                tietokohde.valmistelePaivitys(kysely);
                if (kysely.executeUpdate() == 0) {
                    onnistui = false;
                    break;
                }
            }
            if (onnistui) {
                yhteys.commit();
            } else {
                yhteys.rollback();
            }
        } catch (SQLException | NullPointerException e) {
            Logger.getLogger(TietokantaDAO.class.getName())
                    .log(Level.SEVERE, null, e);
        } finally {
            sulje(yhteys, kysely, null);
        }
        return onnistui;
    }

    /**
     * Suorittaa annetun SQL select-kyselyn. Metodi ei sulje yhteyksiä.
     *
     * @param hakukysely
     * @return Kyselyn vastaus. <b>Huom.</b> Ennen ensimmäisen rivin lukemista
     * tulee kutsua ResultSet-olion metodia <tt>next()</tt> ResultSet-olion
     * metodia <tt>close() </tt> tulee kutsua kun sen sisältämä data on
     * käsitelty.
     */
    static ResultSet hae(final PreparedStatement hakukysely) {
        try {
            final ResultSet vastaus = hakukysely.executeQuery();
            return vastaus;
        } catch (SQLException e) {
            Logger.getLogger(TietokantaDAO.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    public static Yksilotyyppi[] tuoSivu(final Class luokka,
            final String jarjestys, final boolean laskeva, final int pituus,
            final int siirto) {
        ResultSet vastaus           = null;
        Connection yhteys           = null;
        PreparedStatement kysely    = null;
        Yksilotyyppi[] kohteet      = null;
        int i = 0;
        try {
            switch (luokka.getSimpleName()) {
                case "Alue":
                    kohteet = new Alue[pituus];
                    yhteys = annaKertayhteys();
                    kysely = yhteys.prepareStatement("select * from alueet "
                            + "order by ? " + (laskeva ? "desc" : "asc")
                            + " limit ? offset ?;");
                    kysely.setString(1, jarjestys);
                    kysely.setInt(2, pituus);
                    kysely.setInt(3, siirto);
                    vastaus = kysely.executeQuery();
                    while (vastaus.next()) {
                        kohteet[i++] = Alue.luo(vastaus);
                    }
                    break;
                case "Jasen":
                    kohteet = new Jasen[pituus];
                    yhteys = annaKertayhteys();
                    kysely = yhteys.prepareStatement("select * from jasenet "
                            + "order by ? limit ? offset ?;");
                    kysely.setString(1, jarjestys);
                    kysely.setInt(2, pituus);
                    kysely.setInt(3, siirto);
                    vastaus = kysely.executeQuery();
                    while (vastaus.next()) {
                        kohteet[i++] = Jasen.luo(vastaus);
                    }
                    break;
                case "Ketju":
                    kohteet = new Ketju[pituus];
                    yhteys = annaKertayhteys();
                    kysely = yhteys.prepareStatement("select * from ketjut "
                            + "order by ? limit ? offset ?;");
                    kysely.setString(1, jarjestys);
                    kysely.setInt(2, pituus);
                    kysely.setInt(3, siirto);
                    vastaus = kysely.executeQuery();
                    while (vastaus.next()) {
                        kohteet[i++] = Ketju.luo(vastaus);
                    }
                    break;
                case "Viesti":
                    kohteet = new Viesti[pituus];
                    yhteys = annaKertayhteys();
                    kysely = yhteys.prepareStatement("select * from viestit "
                            + "order by ? limit ? offset ?;");
                    kysely.setString(1, jarjestys);
                    kysely.setInt(2, pituus);
                    kysely.setInt(3, siirto);
                    vastaus = kysely.executeQuery();
                    while (vastaus.next()) {
                        kohteet[i++] = Viesti.luo(vastaus);
                    }
                    break;
                default:
                    throw new AssertionError();
            }
        } catch (SQLException | AssertionError e) {
            Logger.getLogger(TietokantaDAO.class.getName())
                    .log(Level.SEVERE, null, e);
        } finally {
            sulje(yhteys, kysely, vastaus);
        }
        return kohteet;
    }

    /**
     * Luo ensimmäisenä parametrina annetun luokan ilmentymän. Olion tila
     * haetaan tietokannasta annetun avaimen perusteella luokkaa vastaavasta
     * relaatiosta.
     *
     * @param luokka Haettavan olion luokka.
     * @param avain Oliota vastaavan monikon avain.
     * @return Uusi
     * @throws AssertionError Jos syötteeksi annetaan jokin tuntematon luokka.
     */
    public static Yksilotyyppi tuo(final Class luokka, final int... avain) {
        ResultSet vastaus           = null;
        Connection yhteys           = null;
        PreparedStatement kysely    = null;
        Yksilotyyppi olio           = null;
        // Tämä koodi on aika ruman näköistä koska Javassa luokat eivät ole
        // first class -arvoja:
        try {
            switch (luokka.getSimpleName()) {
                case "Alue":
                    yhteys = annaKertayhteys();
                    kysely = Alue.hakukysely(yhteys, avain[0]);
                    vastaus = kysely.executeQuery();
                    vastaus.next();
                    olio = Alue.luo(vastaus);
                    break;
                case "Jasen":
                    yhteys = annaKertayhteys();
                    kysely = Jasen.hakukysely(yhteys, avain[0]);
                    vastaus = kysely.executeQuery();
                    vastaus.next();
                    olio = Jasen.luo(vastaus);
                    break;
                case "Ketju":
                    yhteys = annaKertayhteys();
                    kysely = Ketju.hakukysely(yhteys, avain[0]);
                    vastaus = kysely.executeQuery();
                    vastaus.next();
                    olio = Ketju.luo(vastaus);
                    break;
                case "Viesti":
                    yhteys = annaKertayhteys();
                    kysely = Viesti.hakukysely(yhteys, avain[0], avain[1]);
                    vastaus = kysely.executeQuery();
                    vastaus.next();
                    olio = Viesti.luo(vastaus);
                    break;
                default:
                    System.err.println("Tietokannasta yritettiin tuoda tuntematon "
                            + "yksilötyyppi!");
                    throw new AssertionError();
            }
        } catch (SQLException | AssertionError e) {
            Logger.getLogger(TietokantaDAO.class.getName())
                    .log(Level.SEVERE, null, e);
        } finally {
            sulje(yhteys, kysely, vastaus);
        }
        return olio;
    }

    /**
     * Luo ensimmäisenä parametrina annetun luokan ilmentymän. Olion tila
     * haetaan tietokannasta annetun avaimen perusteella luokkaa vastaavasta
     * relaatiosta.
     *
     * @param luokka Haettavan olion luokka.
     * @param avain Oliota vastaavan monikon avain.
     * @return Uusi
     * @throws AssertionError Jos syötteeksi annetaan jokin tuntematon luokka.
     */
    public static Yksilotyyppi tuo(final Class luokka, final String avain) {
        ResultSet vastaus           = null;
        Connection yhteys           = null;
        PreparedStatement kysely    = null;
        Yksilotyyppi olio           = null;
        // Tämä koodi on aika ruman näköistä koska Javassa luokat eivät ole
        // first class -arvoja:
        try {
            switch (luokka.getSimpleName()) {
                case "Alue":
                    yhteys = annaKertayhteys();
                    kysely = Alue.hakukysely(yhteys, avain);
                    vastaus = kysely.executeQuery();
                    vastaus.next();
                    olio = Alue.luo(vastaus);
                    break;
                case "Jasen":
                    yhteys = annaKertayhteys();
                    kysely = Jasen.hakukysely(yhteys, avain);
                    vastaus = kysely.executeQuery();
                    vastaus.next();
                    olio = Jasen.luo(vastaus);
                    break;
                case "Ketju":
                    yhteys = annaKertayhteys();
                    kysely = Ketju.hakukysely(yhteys, avain);
                    vastaus = kysely.executeQuery();
                    vastaus.next();
                    olio = Ketju.luo(vastaus);
                    break;
                case "Viesti":
                    System.err.println("Viestillä ei ole toissijaista hakukyselyä.");
                default:
                    System.err.println("Tietokannasta yritettiin tuoda tuntematon "
                            + "yksilötyyppi!");
                    throw new AssertionError();
            }
        } catch (SQLException | AssertionError e) {
            Logger.getLogger(TietokantaDAO.class.getName())
                    .log(Level.SEVERE, null, e);
        } finally {
            sulje(yhteys, kysely, vastaus);
        }
        return olio;
    }

    /**
     * Luo uuden ketjun ja suorittaa siihen tarvittavat tietokantakyselyt.
     * Kyseessä on transaktio, joten tietokanta jää koherenttiin tilaan metodin
     * suorituksen päätyttyä. (Ketjun luonti tosin ei ole taattu.)
     *
     * @param kirjoittaja   Ensimmäisen viestin kirjoittaja.
     * @param aihe          Ensimmäisen viestin aihe.
     * @param sisalto       Ensimmäisen viestin sisältö.
     * @param alueidenNimet Ketjun alueiden nimet.
     * @return              Luodun ketjun tunnus tai 0 jos ketjun luonti
     *                      epäonnistui.
     */
    public static int luoKetju(final Jasen kirjoittaja, final String aihe,
            final String sisalto, final String[] alueidenNimet) {
        // Tämän transaktion koodi näyttää aika hirveältä ja perustuu
        // sivuvaikutuksiin, mutta ainakin sen pitäisi toimia...
        Connection yhteys = null;
        PreparedStatement ketjunLisays = null, ketjunTunnuksenHaku = null,
                alueidenTunnustenHaku = null, viestinLisays = null, alueenLisays
                = null, viestienKasvatus = null;
        ResultSet ketjunTunnusRS = null, alueidenTunnuksetRS = null;
        boolean onnistui = true;
        final Timestamp ajankohta;
        int ketjunTunnus = 0;
        final int[] alueidenTunnukset;
        try {
            ajankohta = new Timestamp(System.currentTimeMillis());
            yhteys = TietokantaDAO.annaTransaktioyhteys();
            // Ketjun tunnus luodaan tietokannan puolella joten se pitää kysyä:
            if (lisaaKetju(yhteys, ketjunLisays, aihe, ajankohta) != 1) {
                throw new TransactionRolledbackException("Ketjun luonti "
                        + "epäonnistui.");
            }
            ketjunTunnus = haeKetjunTunnus(yhteys, ketjunTunnuksenHaku,
                    ketjunTunnusRS, aihe);
            if (ketjunTunnus == 0) {
                throw new TransactionRolledbackException("Ketjun tunnuksen haku"
                        + " epäonnistui.");
            }
            alueidenTunnukset = haeAlueidenTunnukset(yhteys,
                    alueidenTunnustenHaku, alueidenTunnuksetRS, alueidenNimet);
            if (alueidenTunnukset == null) {
                throw new TransactionRolledbackException("Alueiden tunnusten "
                        + "haku epäonnistui.");
            }
            if (!lisaaAlueet(yhteys, alueenLisays, alueidenTunnukset,
                    ketjunTunnus)) {
                throw new TransactionRolledbackException("Ketjun sijaintien "
                        + "lisäys epäonnistui.");
            }
            if (lisaaViesti(yhteys, viestinLisays, ketjunTunnus, kirjoittaja,
                    ajankohta, sisalto) != 1) {
                throw new TransactionRolledbackException("Viestin luonti "
                        + "epäonnistui.");
            }
            if (kasvataViesteja(yhteys, viestienKasvatus, kirjoittaja) != 1) {
                throw new TransactionRolledbackException("Jäsenen tietojen "
                        + "päivitys epäonnistui.");
            }
        } catch (SQLException | TransactionRolledbackException e) {
            Logger.getLogger(TietokantaDAO.class.getName()).log(Level.SEVERE,
                    null, e);
                onnistui = false;
        } finally {
            if (ketjunTunnusRS != null) {
                try {ketjunTunnusRS.close();} catch (SQLException e) {}
            }
            if (alueidenTunnuksetRS != null) {
                try {alueidenTunnuksetRS.close();} catch (SQLException e) {}
            }
            viimeistele(onnistui, yhteys, ketjunLisays, ketjunTunnuksenHaku,
                    alueidenTunnustenHaku, alueenLisays, viestinLisays);
        }
        return ketjunTunnus;
    }

    private static int lisaaKetju(final Connection yhteys,
            PreparedStatement kysely, final String aihe,
            final Timestamp aikaleima) throws SQLException {
        final Ketju ketju = Ketju.luo(aihe, aikaleima);
        kysely = yhteys.prepareStatement(ketju.paivityslause());
        ketju.valmistelePaivitys(kysely);
        return kysely.executeUpdate();
    }

    private static int haeKetjunTunnus(final Connection yhteys,
            PreparedStatement kysely, ResultSet vastaus, final String aihe)
            throws SQLException {
        kysely = yhteys.prepareStatement(KETJUN_TUNNUKSEN_HAKU);
        kysely.setString(1, aihe);
        vastaus = kysely.executeQuery();
        vastaus.next();
        return vastaus.getInt(1);
    }

    private static int[] haeAlueidenTunnukset(final Connection yhteys,
            PreparedStatement kysely, ResultSet vastaus,
            final String[] alueidenNimet) throws SQLException {
        int[] aluetunnukset         = null;
        final int alueita           = alueidenNimet.length;
        final boolean useampiAlue   = alueita > 1;
        final String hakulause      = useampiAlue
                ? ALUETUNNUSHAKU1 : ALUETUNNUSHAKU2;
        final StringBuilder mjr     = new StringBuilder();
        if (useampiAlue) {
            mjr.append(hakulause);
            for (int i = 1; i < alueita; i++) {
                mjr.append(',');
                mjr.append('?');
            }
            mjr.append(')');
            kysely = yhteys.prepareStatement(mjr.toString());
            for (int i = 0; i < alueita; i++) {
                kysely.setString(i + 1, alueidenNimet[i]);
            }
        } else {
            kysely = yhteys.prepareStatement(hakulause);
            kysely.setString(1, alueidenNimet[0]);
        }
        vastaus = kysely.executeQuery();
        aluetunnukset = new int[alueita];
        for (int i = 0; i < alueita; i++) {
            vastaus.next();
            aluetunnukset[i] = vastaus.getInt(1);
        }
        return aluetunnukset;
    }

    private static boolean lisaaAlueet(final Connection yhteys,
            PreparedStatement kysely, final int[] alueidenTunnukset,
            final int ketjunTunnus) throws SQLException {
        final int[] tulokset;
        kysely = yhteys.prepareStatement(ALUE_KETJU_LIITOS);
        for (int alueenTunnus : alueidenTunnukset) {
            kysely.setInt(1, alueenTunnus);
            kysely.setInt(2, ketjunTunnus);
            kysely.addBatch();
        }
        tulokset = kysely.executeBatch();
        for (int tulos : tulokset) {
            if (tulos != 1) {
                return false;
            }
        }
        return true;
    }

    private static int lisaaViesti(final Connection yhteys,
            PreparedStatement kysely, final int ketjunTunnus,
            final Jasen kirjoittaja, final Timestamp ajankohta,
            final String sisalto) throws SQLException {
        Viesti viesti;
        viesti = Viesti.luo(ketjunTunnus, 1, kirjoittaja.annaKayttajanumero(),
                ajankohta, sisalto);
        kysely = yhteys.prepareStatement(viesti.paivityslause());
        viesti.valmistelePaivitys(kysely);
        return kysely.executeUpdate();
    }

    private static int kasvataViesteja(final Connection yhteys,
            PreparedStatement kysely, final Jasen kirjoittaja)
            throws SQLException {;
        kirjoittaja.kasvataViesteja();
        kysely = yhteys.prepareStatement(kirjoittaja.paivityslause());
        kirjoittaja.valmistelePaivitys(kysely);
        return kysely.executeUpdate();
    }

    /**
     * Sulkee kaikki annettujen olioiden tietokantayhteydet. Tämä metodi ei
     * kutsu Connection-olion metodia close.
     *
     * @param yhteys
     * @param kysely
     * @param vastaus
     * @see TietokantaDAO#sitouta(java.sql.Connection, java.sql.ResultSet, java.sql.PreparedStatement...)
     */
    static void sulje(final Connection yhteys,
            final PreparedStatement kysely, final ResultSet vastaus) {
        if (vastaus != null) {
            try { vastaus.close(); } catch (SQLException e) {}
        }
        if (kysely != null) {
            try { kysely.close(); } catch (SQLException e) {}
        }
        if (yhteys != null) {
            try { yhteys.close(); } catch (SQLException e) {}
        }
    }

    private static void viimeistele(final boolean onnistui,
            final Connection yhteys, final PreparedStatement... kyselyt) {
        for (PreparedStatement kysely : kyselyt) {
            if (kysely != null) {
                try {kysely.close();} catch (SQLException e) {}
            }
        }
        if (onnistui) {
            try {yhteys.commit();} catch (SQLException e) {}
        } else {
            try {yhteys.rollback();} catch (SQLException e) {};
        }
        try {yhteys.close();} catch (SQLException e) {}
    }
}
