import org.junit.*;
import static org.junit.Assert.*;

import play.inject.guice.GuiceApplicationBuilder;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.Environment;

import play.db.jpa.*;

import play.db.Database;
import play.db.Databases;

import java.sql.*;

import java.util.Set;
import java.util.List;

import play.db.jpa.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import models.Columna;
import models.Tablero;
import models.Usuario;

public class ModeloColumnaTest {
  static Database db;
  static private Injector injector;

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

  // Crear una columna.
  @Test
  public void testCrearColumna() {
     Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
     Tablero tablero = new Tablero(usuario, "Tablero 1");
     Columna columna = new Columna(tablero, "Columna 1");

     assertEquals(tablero.getId(), columna.getTablero().getId());
     assertEquals("Tablero 1", columna.getTablero().getNombre());
     assertEquals("Columna 1", columna.getNombre());
  }



}
