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

import play.db.jpa.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import models.Usuario;
import models.UsuarioRepository;
import models.Tablero;
import models.TableroRepository;

public class ModeloRepositorioTableroTest {
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
   // Crear un tablero.
   @Test
   public void testCrearTablero() {
      Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
      Tablero tablero = new Tablero(usuario, "Tablero 1");

      assertEquals("juangutierrez", tablero.getAdministrador().getLogin());
      assertEquals("juangutierrez@gmail.com", tablero.getAdministrador().getEmail());
      assertEquals("Tablero 1", tablero.getNombre());
   }
   // Creación del JPATableroRepository y comprobación de que se obtiene correctamente un objeto TableroRepository.
   @Test
   public void testObtenerTableroRepository() {
      TableroRepository tableroRepository = injector.instanceOf(TableroRepository.class);
      assertNotNull(tableroRepository);
   }
   // Comprobar si se crea la tabla TABLERO en la base de datos
   @Test
   public void testCrearTablaTableroEnBD() throws Exception {
      Connection connection = db.getConnection();
      DatabaseMetaData meta = connection.getMetaData();
      // En la BD H2 el nombre de las tablas se define con mayúscula y en
      // MySQL con minúscula
      ResultSet resH2 = meta.getTables(null, null, "TABLERO", new String[] {"TABLE"});
      ResultSet resMySQL = meta.getTables(null, null, "Tablero", new String[] {"TABLE"});
      boolean existeTabla = resH2.next() || resMySQL.next();
   }
   // Crear funcion add() en el TableroRepository.
   @Test
   public void testAddTableroInsertsDatabase() {
      UsuarioRepository usuarioRepository = injector.instanceOf(UsuarioRepository.class);
      TableroRepository tableroRepository = injector.instanceOf(TableroRepository.class);
      Usuario administrador = new Usuario("juangutierrez", "juangutierrez@gmail.com");
      administrador = usuarioRepository.add(administrador);
      Tablero tablero = new Tablero(administrador, "Tablero 1");
      tablero = tableroRepository.add(tablero);
      assertNotNull(tablero.getId());
      assertEquals("Tablero 1", getNombreFromTableroDB(tablero.getId()));
   }

   private String getNombreFromTableroDB(Long tableroId) {
      String nombre = db.withConnection(connection -> {
         String selectStatement = "SELECT Nombre FROM Tablero WHERE ID = ? ";
         PreparedStatement prepStmt = connection.prepareStatement(selectStatement);
         prepStmt.setLong(1, tableroId);
         ResultSet rs = prepStmt.executeQuery();
         rs.next();
         return rs.getString("Nombre");
      });
      return nombre;
   }
   // Un usuario pueda administrar varios tableros
   @Test
   public void testUsuarioAdministraVariosTableros() {
      UsuarioRepository usuarioRepository = injector.instanceOf(UsuarioRepository.class);
      TableroRepository tableroRepository = injector.instanceOf(TableroRepository.class);
      Usuario administrador = new Usuario("juangutierrez", "juangutierrez@gmail.com");
      administrador = usuarioRepository.add(administrador);
      Tablero tablero1 = new Tablero(administrador, "Tablero 1");
      tableroRepository.add(tablero1);
      Tablero tablero2 = new Tablero(administrador, "Tablero 2");
      tableroRepository.add(tablero2);
      // Recuperamos el administrador del repository
      administrador = usuarioRepository.findById(administrador.getId());
      // Y comprobamos si tiene los tableros
      assertEquals(2, administrador.getAdministrados().size());
   }
   // Un usuario pueda tener varios tableros asociados
   private void initDataSet() throws Exception {
      JndiDatabaseTester databaseTester = new JndiDatabaseTester("DBTest");
      IDataSet initialDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("test/resources/usuarios_dataset.xml"));
      databaseTester.setDataSet(initialDataSet);
      databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
      databaseTester.onSetup();
   }

   @Test
   public void testUsuarioParticipaEnVariosTableros() throws Exception {
      initDataSet();
      UsuarioRepository usuarioRepository = injector.instanceOf(UsuarioRepository.class);
      TableroRepository tableroRepository = injector.instanceOf(TableroRepository.class);
      Usuario admin = usuarioRepository.findById(1000L);
      Usuario usuario = usuarioRepository.findById(1001L);
      Set<Tablero> tableros = admin.getAdministrados();
      // Tras cargar los datos del dataset el usuario2 no tiene ningún
      // tablero asociado y el usuario 1 tiene 2 tableros administrados
      assertEquals(0, usuario.getTableros().size());
      assertEquals(2, tableros.size());
      for (Tablero tablero : tableros) {
         // Actualizamos la relación en memoria, añadiendo el usuario
         // al tablero
         tablero.getParticipantes().add(usuario);
         // Actualizamos la base de datos llamando al repository
         tableroRepository.update(tablero);
      }
      // Comprobamos que se ha actualizado la relación en la BD y
      // el usuario pertenece a los dos tableros a los que le hemos añadido
      usuario = usuarioRepository.findById(1001L);
      Set<Tablero> tablerosUsuario = usuario.getTableros();
      assertEquals(2, tablerosUsuario.size());
      for (Tablero tablero: tableros) {
         assertTrue(tablerosUsuario.contains(tablero));
      }
   }

   @Test
   public void testTableroTieneVariosUsuarios() throws Exception {
      initDataSet();
      UsuarioRepository usuarioRepository = injector.instanceOf(UsuarioRepository.class);
      TableroRepository tableroRepository = injector.instanceOf(TableroRepository.class);
      // Obtenemos datos del dataset
      Tablero tablero = tableroRepository.findById(1000L);
      Usuario usuario1 = usuarioRepository.findById(1000L);
      Usuario usuario2 = usuarioRepository.findById(1001L);
      Usuario usuario3 = usuarioRepository.findById(1002L);
      assertEquals(0, tablero.getParticipantes().size());
      assertEquals(0, usuario1.getTableros().size());
      // Añadimos los 3 usuarios al tablero
      tablero.getParticipantes().add(usuario1);
      tablero.getParticipantes().add(usuario2);
      tablero.getParticipantes().add(usuario3);
      tableroRepository.update(tablero);
      // Comprobamos que los datos se han actualizado
      tablero = tableroRepository.findById(1000L);
      usuario1 = usuarioRepository.findById(1000L);
      assertEquals(3, tablero.getParticipantes().size());
      assertEquals(1, usuario1.getTableros().size());
      assertTrue(tablero.getParticipantes().contains(usuario1));
      assertTrue(usuario1.getTableros().contains(tablero));
   }
}
