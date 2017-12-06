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

import models.Usuario;
import models.UsuarioRepository;
import models.JPAUsuarioRepository;

import models.TareaRepository;
import models.JPATareaRepository;

import services.TareaService;
import services.TareaServiceException;

public class Practica2Test {
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

   private UsuarioRepository newUsuarioRepository() {
      return injector.instanceOf(UsuarioRepository.class);
   }

   private TareaService newTareaService() {
      return injector.instanceOf(TareaService.class);
   }

   // Test 1: testFindUsuarioPorIdNoExists
   @Test
   public void testFindUsuarioPorIdNoExists() {
      UsuarioRepository repository = newUsuarioRepository();
      Usuario usuario = repository.findById(1500L);
      assertEquals(null, usuario);
   }

   // Test 2: nuevaTareaUsuarioNoExistente
   @Test(expected = TareaServiceException.class)
   public void nuevaTareaUsuarioNoExistente() {
      TareaService tareaService = newTareaService();
      long idUsuario = 1500L;
      tareaService.nuevaTarea(idUsuario, "Pagar el alquiler", "", null, null);
   }

   // Test 3: modificar tarea que no existe
   @Test(expected = TareaServiceException.class)
   public void modificarTareaNoExistente() {
      TareaService tareaService = newTareaService();
      long idTarea = 1500L;
      tareaService.modificaTarea(idTarea, "nuevoTitulo", null, null, null);
   }

   // Test 4: borrado tarea que no existe
   @Test(expected = TareaServiceException.class)
   public void borradoTareaNoExistente() {
      TareaService tareaService = newTareaService();
      long idTarea = 1500L;
      tareaService.borraTarea(idTarea);
   }

}
