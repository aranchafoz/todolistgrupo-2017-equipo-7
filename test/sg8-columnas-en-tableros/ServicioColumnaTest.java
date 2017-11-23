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
}
