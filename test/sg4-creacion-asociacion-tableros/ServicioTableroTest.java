import org.junit.*;
import static org.junit.Assert.*;

import play.db.jpa.*;

import play.inject.guice.GuiceApplicationBuilder;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.Environment;

import services.TableroService;
// Métodos de servicio para:
// - crear un tablero y
// - para obtener el listado de tableros administrados por un usuario.
public class ServicioTableroTest {
  static private Injector injector;

  @BeforeClass
  static public void initApplication() {
     GuiceApplicationBuilder guiceApplicationBuilder =
         new GuiceApplicationBuilder().in(Environment.simple());
     injector = guiceApplicationBuilder.injector();
     // Necesario para inicializar JPA
     injector.instanceOf(JPAApi.class);
  }

  private TableroService newTableroService() {
    return injector.instanceOf(TableroService.class);
  }
}
