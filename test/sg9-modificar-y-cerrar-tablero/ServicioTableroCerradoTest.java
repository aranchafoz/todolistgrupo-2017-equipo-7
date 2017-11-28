import org.junit.*;
import static org.junit.Assert.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import play.db.Database;
import play.db.Databases;
import play.db.jpa.*;

import play.Logger;

import java.sql.*;

import org.junit.*;
import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import play.inject.guice.GuiceApplicationBuilder;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.Environment;

import models.Tablero;
import models.TableroRepository;
import models.JPATableroRepository;
import models.Usuario;
import services.TableroService;

public class ServicioTableroCerradoTest {
  static Database db;
  static private Injector injector;

  // Se ejecuta sólo una vez, al principio de todos los tests
  @BeforeClass
  static public void initApplication() {
     GuiceApplicationBuilder guiceApplicationBuilder =
         new GuiceApplicationBuilder().in(Environment.simple());
     injector = guiceApplicationBuilder.injector();
     db = injector.instanceOf(Database.class);
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

  @Test
  public void cierraTableroServiceTest() {
    TableroService tableroService = newTableroService();
    Tablero tablero = tableroService.nuevoTablero(1000L, "Test 1");

    assertFalse(tablero.getCerrado());
    tablero = tableroService.cerrarTablero(tablero.getId());

    assertTrue(tablero.getCerrado());
  }

  @Test
  public void editaTableroTest() {
    TableroService tableroService = newTableroService();
    Tablero tablero = tableroService.nuevoTablero(1000L, "Test 2");

    assertEquals(tablero.getNombre(), "Test 2");
    tablero = tableroService.editarTablero(tablero.getId(), "Me abruma el tablero");

    assertEquals(tablero.getNombre(), "Me abruma el tablero");
  }

 }
