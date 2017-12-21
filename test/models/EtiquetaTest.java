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
import models.Etiqueta;
import models.Tablero;
import models.Usuario;
import models.Tarea;
import models.TableroRepository;
import models.EtiquetaRepository;
import models.UsuarioRepository;
import models.TareaRepository;
import models.ColumnaRepository;
import models.JPAColumnaRepository;
import models.EtiquetaRepository;
import models.JPAEtiquetaRepository;

public class EtiquetaTest {
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

  private EtiquetaRepository newEtiquetaRepository() {
     return injector.instanceOf(EtiquetaRepository.class);
  }

  private ColumnaRepository newColumnaRepository() {
     return injector.instanceOf(ColumnaRepository.class);
  }

  // Crear una etiqueta.
  @Test
  public void testCrearEtiqueta() {
     Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
     Tablero tablero = new Tablero(usuario, "Tablero 1");
     Etiqueta etiqueta = new Etiqueta(tablero, "Etiqueta 1");

     assertEquals(tablero.getId(), etiqueta.getTablero().getId());
     assertEquals("Tablero 1", etiqueta.getTablero().getNombre());
     assertEquals("Etiqueta 1", etiqueta.getNombre());
  }

  @Test
  public void testObtenerEtiquetaRepository() {
    EtiquetaRepository etiquetaRepository = newEtiquetaRepository();
    assertNotNull(etiquetaRepository);
  }

  // Crear funcion add() en el TableroRepository.
   @Test
   public void testAddEtiquetaInsertsDatabase() {
      EtiquetaRepository etiquetaRepository = newEtiquetaRepository();
      TableroRepository tableroRepository = newTableroRepository();
      Tablero tablero = tableroRepository.findById(1000L);
      Etiqueta etiqueta = new Etiqueta(tablero, "Etiqueta 1");
      etiqueta = etiquetaRepository.add(etiqueta);
      assertNotNull(etiqueta.getId());
      assertEquals("Etiqueta 1", getNombreFromEtiquetaDB(etiqueta.getId()));
   }

   private String getNombreFromEtiquetaDB(Long etiquetaId) {
      String nombre = db.withConnection(connection -> {
         String selectStatement = "SELECT Nombre FROM Etiqueta WHERE ID = ? ";
         PreparedStatement prepStmt = connection.prepareStatement(selectStatement);
         prepStmt.setLong(1, etiquetaId);
         ResultSet rs = prepStmt.executeQuery();
         rs.next();
         return rs.getString("Nombre");
      });
      return nombre;
   }

   // Un tablero pueda tener varias etiquetas
   @Test
   public void testTableroTieneVariasEtiquetas() {
      EtiquetaRepository etiquetaRepository = newEtiquetaRepository();
      TableroRepository tableroRepository = newTableroRepository();
      Tablero tablero = tableroRepository.findById(1000L);
      Etiqueta etiqueta1 = new Etiqueta(tablero, "Etiqueta 1");
      etiqueta1 = etiquetaRepository.add(etiqueta1);
      Etiqueta etiqueta2 = new Etiqueta(tablero, "Etiqueta 2");
      etiqueta2 = etiquetaRepository.add(etiqueta2);
      // Recuperamos el tablero del repository
      tablero = tableroRepository.findById(tablero.getId());
      // Y comprobamos si tiene las etiquetas
      assertEquals(2, tablero.getEtiquetas().size());
   }

   // SGT-13: Etiquetas en tareas
   // Una tarea puede tener varias etiquetas
   @Test
   public void testTareaTieneVariasEtiquetas() {
      ColumnaRepository columnaRepository = newColumnaRepository();
      EtiquetaRepository etiquetaRepository = newEtiquetaRepository();
      TableroRepository tableroRepository = newTableroRepository();
      TareaRepository tareaRepository = newTareaRepository();
      UsuarioRepository usuarioRepository = newUsuarioRepository();

      Tablero tablero = tableroRepository.findById(1000L);
      Columna columna = new Columna(tablero, "Columna Test Varias Tareas", 1);
      columna = columnaRepository.add(columna);
      Usuario usuario = usuarioRepository.findById(1000L);

      Tarea tarea = new Tarea(usuario, "Tarea 1", columna);
      tarea = tareaRepository.add(tarea);

      Etiqueta etiqueta1 = new Etiqueta(tablero, "Etiqueta 1");
      etiqueta1 = etiquetaRepository.add(etiqueta1);
      Etiqueta etiqueta2 = new Etiqueta(tablero, "Etiqueta 2");
      etiqueta2 = etiquetaRepository.add(etiqueta2);

      // Recuperamos la tarea del repository
      tarea = tareaRepository.findById(tarea.getId());
      tarea.getEtiquetas().add(etiqueta1);
      tarea.getEtiquetas().add(etiqueta2);

      //tarea.setEtiquetas(etiqueta);

      // Y comprobamos si tiene la etiqueta
      assertEquals(2, tarea.getEtiquetas().size()); // FALLA AQUÍ
   }

}
