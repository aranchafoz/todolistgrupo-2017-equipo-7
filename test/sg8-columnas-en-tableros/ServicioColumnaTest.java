import org.junit.*;
import static org.junit.Assert.*;

import play.db.jpa.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import java.util.List;

import models.Columna;

import play.inject.guice.GuiceApplicationBuilder;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.Environment;


import services.ColumnaService;
import services.ColumnaServiceException;

public class ServicioColumnaTest {
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

  private ColumnaService newColumnaService() {
    return injector.instanceOf(ColumnaService.class);
  }

  // Test 1: allColumnasTableroEstanOrdenadas
  @Test
  public void allColumnasTableroEstanOrdenadas() {
     ColumnaService columnaService = newColumnaService();
     List<Columna> columnas = columnaService.allColumnasTablero(1001L);
     assertEquals("Columna test 1", columnas.get(0).getNombre());
     assertEquals("Columna test 2", columnas.get(1).getNombre());
  }

  // Test 3: nuevaColumnaService
  @Test
  public void nuevaColumnaService() {
    ColumnaService columnaService = newColumnaService();
    long idTablero = 2000L;
    columnaService.nuevaColumna(idTablero, "Nueva columna test 1", 1);
    assertEquals(3, columnaService.allColumnasTablero(2000L).size());
  }

  // Test 4: exceptionSiTableroNoExisteRecuperandoSusColumnas
  @Test(expected = ColumnaServiceException.class)
  public void crearNuevoTableroNoExistenteLanzaExcepcion(){
    ColumnaService columnaService = newColumnaService();
     List<Columna> columnas = columnaService.allColumnasTablero(1011L);
  }
}
