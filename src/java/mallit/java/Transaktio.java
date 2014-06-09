
package mallit.java;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import kontrollerit.ViestiServlet;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Transaktio {

    public static int[] haeAlueidenTunnukset(final String[] alueidenNimet) {
        Connection yhteys = null;
        PreparedStatement kysely = null;
        ResultSet vastaus = null;
        final int alueita = alueidenNimet.length;
        final boolean useampiAlue = alueita > 1;
        final String hakulause = useampiAlue
                ? "select tunnus from alueet where nimi in ("
                : "select tunnus from alueet where nimi = ?";
        final StringBuilder mjr = new StringBuilder();
        int[] aluetunnukset = new int[alueita];
        try {
            yhteys = valmisteleYhteys();    // Riittäisiköhän tähän transaktioon
                                            // alempi eristystaso?
            if (useampiAlue) {
                mjr.append(hakulause);
                mjr.append('?');
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
            for (int i = 0; i < alueita; i++) {
                vastaus.next();
                aluetunnukset[i] = vastaus.getInt(i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(Transaktio.class.getName()).log(Level.SEVERE, null,
                    e);
        } finally {
            sitouta(yhteys, vastaus, kysely);
        }
        return aluetunnukset;
    }

    public static int luoKetju(final Jasen kirjoittaja, final String aihe,
            final String sisalto, final int[] alueet) {
        Connection yhteys = null;
        PreparedStatement ketjunLisays = null, ketjunTunnuksenHaku = null,
                viestinLisays = null, alueenLisays = null;
        ResultSet vastaus = null;
        final Date ajankohta;
        final Ketju ketju;
        final String alueenLisayslause = "insert into ketjujen_sijainnit "
                + "values (?, ?)";
        int ketjunTunnus = 0;
        final Viesti viesti;
        try {
            ajankohta = new Date(System.currentTimeMillis());
            yhteys = valmisteleYhteys();
            ketju = Ketju.luo(aihe, ajankohta);
            ketjunLisays = ketju.lisayskysely(yhteys);
            ketjunLisays.executeUpdate();
            // Ketjun tunnus luodaan tietokannan puolella joten se pitää kysyä:
            ketjunTunnuksenHaku = yhteys.prepareStatement("select tunnus from "
                    + "ketjut where aihe = ?");
            ketjunTunnuksenHaku.setString(1, aihe);
            vastaus = ketjunTunnuksenHaku.executeQuery();
            vastaus.next();
            ketjunTunnus = vastaus.getInt(1);
            alueenLisays = yhteys.prepareStatement(alueenLisayslause);
            for (int alueenTunnus : alueet) {
                alueenLisays.setInt(1, alueenTunnus);
                alueenLisays.setInt(2, ketjunTunnus);
                alueenLisays.addBatch();
            }
            alueenLisays.executeBatch();
            viesti = Viesti.luo(ketjunTunnus, 1,
                    kirjoittaja.annaKayttajanumero(), ajankohta, sisalto);
            viestinLisays = viesti.lisayskysely(yhteys);
            viestinLisays.executeUpdate();
        } catch (SQLException e) {
            Logger.getLogger(Transaktio.class.getName()).log(Level.SEVERE, null,
                    e);
        } finally {
            sitouta(yhteys, vastaus, ketjunLisays, ketjunTunnuksenHaku,
                    alueenLisays, viestinLisays);
        }
        return ketjunTunnus;
    }

    private static Connection valmisteleYhteys() throws SQLException {
        final Connection yhteys = TietokantaDAO.annaYhteys();
        yhteys.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        yhteys.setAutoCommit(false);
        return yhteys;
    }

    private static void sitouta(final Connection yhteys,
            final ResultSet vastaus, final PreparedStatement... kyselyt) {
        if (vastaus != null) {
            try {vastaus.close();} catch (SQLException e) {}
        }
        for (PreparedStatement kysely : kyselyt) {
            if (kysely != null) {
                try {kysely.close();} catch (SQLException e) {}
            }
        }
        try {yhteys.commit();} catch (SQLException e) {}
        try {yhteys.close();} catch (SQLException e) {}
    }

}
