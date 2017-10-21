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
import services.TableroServiceException;
// Métodos de servicio para:
// - crear un tablero y
// - obtener el listado de:
//    - tableros administrados por un usuario.
//    - tableros en los que participa el usuario.
//    - resto de tableros (en los que el usuario ni participa ni es administrador).
// - apuntarse a un tablero (como participante)
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

  // Issue #2
  // Test 1: Crear TableroService
  @Test
  public void crearTableroService() {
    TableroService tableroService = injector.instanceOf(TableroService.class);
    assertNotNull(tableroService);
  }

  // Test 2: allTablerosUsuarioEstanOrdenados
  @Test
  public void allTablerosAdministradosUsuarioEstanOrdenadas() {
     TableroService tableroService = newTableroService();
     List<Tablero> tableros = tableroService.allTablerosAdministradosUsuario(1000L);
     assertEquals("Tablero test 1", tableros.get(0).getNombre());
     assertEquals("Tablero test 2", tableros.get(1).getNombre());
  }

  // Test 3: nuevoTableroService
  @Test
  public void nuevoTableroService() {
    TableroService tableroService = newTableroService();
    long idUsuario = 1000L;
    tableroService.nuevoTablero(idUsuario, "Nuevo tablero test 1");
    assertEquals(3, tableroService.allTablerosAdministradosUsuario(1000L).size());
  }

  // Test 4: exceptionSiUsuarioNoExisteRecuperandoSusTableros
  @Test(expected = TableroServiceException.class)
  public void crearNuevoUsuarioLoginNoExistenteLanzaExcepcion(){
     TableroService tableroService = newTableroService();
     List<Tablero> tableros = tableroService.allTablerosAdministradosUsuario(1011L);
  }

  // Issue #4
  // Test 5: obtenerTablero
  @Test
  public void obtenerTablero() {
    TableroService tableroService = newTableroService();
    Tablero tablero = tableroService.obtenerTablero(1000L);
    assertNotNull(tablero);
    assertEquals("Tablero test 1", tablero.getNombre());
  }
}
