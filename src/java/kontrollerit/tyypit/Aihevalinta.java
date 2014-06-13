/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kontrollerit.tyypit;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public class Aihevalinta {

    public final String     aihe;
    public final boolean    valittu;
    public final int        tunnus;

    public Aihevalinta(final String aihe, final boolean valittu,
            final int tunnus) {
        this.aihe = aihe;
        this.valittu = valittu;
        this.tunnus = tunnus;
    }

}
