
package kontrollerit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import kontrollerit.tyokalut.Valvoja;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Kontekstinkuuntelija implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Valvoja.annaIlmentyma().start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Valvoja.annaIlmentyma().interrupt();
    }

}
