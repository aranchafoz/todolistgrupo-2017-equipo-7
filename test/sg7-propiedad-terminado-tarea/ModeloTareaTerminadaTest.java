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

import models.Tarea;
import models.Usuario;
import models.TareaRepository;

public class ModeloTareaTerminadaTest {
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

  private TareaRepository newTareaRepository() {
     return injector.instanceOf(TareaRepository.class);
  }

  @Test
  public void testTareaNoTerminada() {
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    Tarea t1 = new Tarea(usuario,"tarea1");

    assertFalse(t1.getTerminada());
  }

  @Test
  public void testTareaNoTerminadaBD() {
    TareaRepository tareaRepository = newTareaRepository();
    Tarea t = tareaRepository.findById(1000L);
    assertNotNull(t);
    assertFalse(t.getTerminada());
  }

  @Test
  public void testTareaTerminada() {
    TareaRepository tareaRepository = newTareaRepository();
    Tarea t = tareaRepository.findById(1000L);
    assertNotNull(t);
    assertFalse(t.getTerminada());

    t.setTerminada(true);

    assertTrue(t.getTerminada());
    Tarea updated = tareaRepository.update(t);

    assertTrue(updated.getTerminada());
  }
}
