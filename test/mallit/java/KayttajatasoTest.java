/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mallit.java;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public class KayttajatasoTest {

    public KayttajatasoTest() {
    }

//    @Test
//    public void testValues() {
//        System.out.println("values");
//        Kayttajataso[] expResult = null;
//        Kayttajataso[] result = Kayttajataso.values();
//        assertArrayEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testValueOf() {
//        System.out.println("valueOf");
//        String name = "";
//        Kayttajataso expResult = null;
//        Kayttajataso result = Kayttajataso.valueOf(name);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }

    @Test
    public void testSyk() {
        Kayttajataso a = Kayttajataso.VIERAILIJA;
        Kayttajataso b = Kayttajataso.VIERAILIJA;
        assertTrue(Kayttajataso.vahintaan(a, b));
        a = Kayttajataso.MODERAATTORI;
        assertTrue(Kayttajataso.vahintaan(a, b));
        b = Kayttajataso.YLLAPITAJA;
        assertFalse(Kayttajataso.vahintaan(a, b));
        assertTrue(Kayttajataso.vahintaan(Kayttajataso.MODERAATTORI,
                Kayttajataso.MODERAATTORI));
    }

}
