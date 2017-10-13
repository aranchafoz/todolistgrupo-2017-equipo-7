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

import models.Usuario;
import models.UsuarioRepository;
import models.JPAUsuarioRepository;

import models.TareaRepository;
import models.JPATareaRepository;

import services.TareaService;
import services.TareaServiceException;

public class Practica2Test {
  static Database db;
  static JPAApi jpaApi;

  @BeforeClass
  static public void initDatabase() {
    // Inicializamos la BD en memoria y su nombre JNDI
    db = Databases.inMemoryWith("jndiName", "DBTest");
    // Se activa la compatibilidad MySQL en la BD H2
    db.withConnection(connection -> {
       connection.createStatement().execute("SET MODE MySQL;");
    });
    // Activamos en JPA la unidad de persistencia "memoryPersistenceUnit"
    // declarada en META-INF/persistence.xml y obtenemos el objeto
    // JPAApi
    jpaApi = JPA.createFor("memoryPersistenceUnit");
  }

  @Before
   public void initData() throws Exception {
      JndiDatabaseTester databaseTester = new JndiDatabaseTester("DBTest");
      IDataSet initialDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("test/resources/usuarios_dataset.xml"));
      databaseTester.setDataSet(initialDataSet);
      databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
      databaseTester.onSetup();
   }

   private TareaService newTareaService() {
      UsuarioRepository usuarioRepository = new JPAUsuarioRepository(jpaApi);
      TareaRepository tareaRepository = new JPATareaRepository(jpaApi);
      return new TareaService(usuarioRepository, tareaRepository);
   }

   // Test 1: testFindUsuarioPorIdNoExists
   @Test
   public void testFindUsuarioPorIdNoExists() {
      UsuarioRepository repository = new JPAUsuarioRepository(jpaApi);
      Usuario usuario = repository.findById(1500L);
      assertEquals(null, usuario);
   }

   // Test 2: nuevaTareaUsuarioNoExistente
   @Test(expected = TareaServiceException.class)
   public void nuevaTareaUsuarioNoExistente() {
      TareaService tareaService = newTareaService();
      long idUsuario = 1500L;
      tareaService.nuevaTarea(idUsuario, "Pagar el alquiler");
   }

   // Test 3: modificar tarea que no existe
   @Test(expected = TareaServiceException.class)
   public void modificarTareaNoExistente() {
      TareaService tareaService = newTareaService();
      long idTarea = 1500L;
      tareaService.modificaTarea(idTarea, "nuevoTitulo");
   }

   // Test 4: borrado tarea que no existe
   @Test(expected = TareaServiceException.class)
   public void borradoTareaNoExistente() {
      TareaService tareaService = newTareaService();
      long idTarea = 1500L;
      tareaService.borraTarea(idTarea);
   }

}
