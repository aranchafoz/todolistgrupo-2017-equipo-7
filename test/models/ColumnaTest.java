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
import models.Tarea;
import models.TableroRepository;
import models.ColumnaRepository;
import models.UsuarioRepository;
import models.TareaRepository;
import models.JPAColumnaRepository;

public class ColumnaTest {
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

  private UsuarioRepository newUsuarioRepository() {
     return injector.instanceOf(UsuarioRepository.class);
  }

  private TableroRepository newTableroRepository() {
     return injector.instanceOf(TableroRepository.class);
  }

  private ColumnaRepository newColumnaRepository() {
     return injector.instanceOf(ColumnaRepository.class);
  }

  // Crear una columna.
  @Test
  public void testCrearColumna() {
     Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
     Tablero tablero = new Tablero(usuario, "Tablero 1");
     Columna columna = new Columna(tablero, "Columna 1", 1);

     assertEquals(tablero.getId(), columna.getTablero().getId());
     assertEquals("Tablero 1", columna.getTablero().getNombre());
     assertEquals("Columna 1", columna.getNombre());
  }

  @Test
  public void testObtenerColumnaRepository() {
    ColumnaRepository columnaRepository = newColumnaRepository();
    assertNotNull(columnaRepository);
  }

  // Crear funcion add() en el TableroRepository.
   @Test
   public void testAddColumnaInsertsDatabase() {
      ColumnaRepository columnaRepository = newColumnaRepository();
      TableroRepository tableroRepository = newTableroRepository();
      Tablero tablero = tableroRepository.findById(1000L);
      Columna columna = new Columna(tablero, "Columna 1", 1);
      columna = columnaRepository.add(columna);
      assertNotNull(columna.getId());
      assertEquals("Columna 1", getNombreFromColumnaDB(columna.getId()));
   }

   private String getNombreFromColumnaDB(Long columnaId) {
      String nombre = db.withConnection(connection -> {
         String selectStatement = "SELECT Nombre FROM Columna WHERE ID = ? ";
         PreparedStatement prepStmt = connection.prepareStatement(selectStatement);
         prepStmt.setLong(1, columnaId);
         ResultSet rs = prepStmt.executeQuery();
         rs.next();
         return rs.getString("Nombre");
      });
      return nombre;
   }

   // Un tablero pueda tener varias columnas
   @Test
   public void testTableroTieneVariasColumnas() {
      ColumnaRepository columnaRepository = newColumnaRepository();
      TableroRepository tableroRepository = newTableroRepository();
      Tablero tablero = tableroRepository.findById(1000L);
      Columna columna1 = new Columna(tablero, "Columna 1", 1);
      columna1 = columnaRepository.add(columna1);
      Columna columna2 = new Columna(tablero, "Columna 2", 2);
      columna2 = columnaRepository.add(columna2);
      // Recuperamos el tablero del repository
      tablero = tableroRepository.findById(tablero.getId());
      // Y comprobamos si tiene las columnas
      assertEquals(2, tablero.getColumnas().size());
   }

   @Test
   public void testModificarPosicionColumna() {
      ColumnaRepository columnaRepository = newColumnaRepository();
      Columna c = columnaRepository.findById(1000L);
      assertNotNull(c);
      assertEquals(c.getPosicion(), (Integer) 1);

      c.setPosicion(3);

      assertEquals(c.getPosicion(), (Integer) 3);
      Columna updated = columnaRepository.update(c);

      assertEquals(updated.getPosicion(), (Integer) 3);
   }

   // SGT-11: Tareas en tableros
   // Una columna pueda tener varias tareas
   @Test
   public void testColumnaTieneVariasTareas() {
      ColumnaRepository columnaRepository = newColumnaRepository();
      TableroRepository tableroRepository = newTableroRepository();
      TareaRepository tareaRepository = newTareaRepository();
      UsuarioRepository usuarioRepository = newUsuarioRepository();

      Tablero tablero = tableroRepository.findById(1000L);
      Columna columna = new Columna(tablero, "Columna Test Varias Tareas", 1);
      columna = columnaRepository.add(columna);
      // Añadimos varias tareas
      Usuario usuario = usuarioRepository.findById(1000L);
      Tarea tarea1 = new Tarea(usuario, "Tarea 1", columna);
      tarea1 = tareaRepository.add(tarea1);
      Tarea tarea2 = new Tarea(usuario, "Tarea 2", columna);
      tarea2 = tareaRepository.add(tarea2);
      Tarea tarea3 = new Tarea(usuario, "Tarea 3", columna);
      tarea3 = tareaRepository.add(tarea3);
      // Recuperamos la columna del repository
      columna = columnaRepository.findById(columna.getId());
      // Y comprobamos si tiene las tareas
      assertEquals(3, columna.getTareas().size());
   }
}
