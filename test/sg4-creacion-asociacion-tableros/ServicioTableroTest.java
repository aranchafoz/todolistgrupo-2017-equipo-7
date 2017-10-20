import org.junit.*;
import static org.junit.Assert.*;

import play.db.jpa.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import java.util.List;

import models.Tablero;

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

  @Before
  public void initData() throws Exception {
     JndiDatabaseTester databaseTester = new JndiDatabaseTester("DBTest");
     IDataSet initialDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("test/resources/usuarios_dataset.xml"));
     databaseTester.setDataSet(initialDataSet);
     databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
     databaseTester.onSetup();
  }

  private TableroService newTableroService() {
    return injector.instanceOf(TableroService.class);
  }

  // Test 1: allTablerosUsuarioEstanOrdenados
  @Test
  public void allTablerosAdministradosUsuarioEstanOrdenadas() {
     TableroService tableroService = newTableroService();
     List<Tablero> tableros = tableroService.allTablerosAdministradosUsuario(1000L);
     assertEquals("Tablero test 1", tableros.get(0).getNombre());
     assertEquals("Tablero test 2", tableros.get(1).getNombre());
  }
}
