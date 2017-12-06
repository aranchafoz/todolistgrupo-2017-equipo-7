import org.junit.*;
import static org.junit.Assert.*;

import play.db.Database;
import play.db.Databases;

import play.db.jpa.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import java.util.List;

import models.Usuario;
import models.UsuarioRepository;
import models.JPAUsuarioRepository;

import models.Tarea;
import models.TareaRepository;
import models.JPATareaRepository;

import play.inject.guice.GuiceApplicationBuilder;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.Environment;

import services.UsuarioService;
import services.UsuarioServiceException;
import services.TareaService;
import services.TareaServiceException;

import java.util.Date;

public class TareaServiceTest {
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

   private TareaService newTareaService() {
      return injector.instanceOf(TareaService.class);
   }

   // Test #19: allTareasUsuarioEstanOrdenadas
   @Test
   public void allTareasUsuarioEstanOrdenadas() {
      TareaService tareaService = newTareaService();
      List<Tarea> tareas = tareaService.allTareasUsuario(1000L);
      assertEquals("Renovar DNI", tareas.get(0).getTitulo());
      assertEquals("Práctica 1 MADS", tareas.get(1).getTitulo());
   }

   // Test #20: exceptionSiUsuarioNoExisteRecuperandoSusTareas
   @Test(expected = TareaServiceException.class)
   public void crearNuevoUsuarioLoginRepetidoLanzaExcepcion(){
      TareaService tareaService = newTareaService();
      List<Tarea> tareas = tareaService.allTareasUsuario(1011L);
   }

   // Test #21: nuevaTareaUsuario
   @Test
   public void nuevaTareaUsuario() {
      TareaService tareaService = newTareaService();
      long idUsuario = 1000L;
      long idColumna = 1000L;
      tareaService.nuevaTarea(idUsuario, "Pagar el alquiler", "", null, idColumna);
      assertEquals(3, tareaService.allTareasUsuario(1000L).size());
   }

   // Test #22: modificación de tareas
   @Test
   public void modificacionTarea() {
      TareaService tareaService = newTareaService();
      long idTarea = 1000L;
      long idColumna = 1001L;
      tareaService.modificaTarea(idTarea, "Pagar el alquiler", null, null, idColumna);
      Tarea tarea = tareaService.obtenerTarea(idTarea);
      assertEquals("Pagar el alquiler", tarea.getTitulo());
      assertEquals("Columna test 2", tarea.getColumna().getNombre());
   }

   // Test #23: borrado tarea
  @Test
  public void borradoTarea() {
     TareaService tareaService = newTareaService();
     long idTarea = 1000L;
     tareaService.borraTarea(idTarea);
     assertNull(tareaService.obtenerTarea(idTarea));
  }

   @Test
   public void tareaSinDescripcionServiceTest() {
      TareaService tareaService = newTareaService();
      long idUsuario = 1000L;
      long idColumna = 1000L;
      Tarea tarea = tareaService.nuevaTarea(idUsuario, "Pagar el alquiler", "", null, idColumna);

      assertEquals("", tarea.getDescripcion());
   }

   // SGT-7: Propiedad terminado en Tarea
   @Test
   public void tareaTerminadaServiceTest() {
     TareaService tareaService = newTareaService();
     long idUsuario = 1000L;
     long idColumna = 1000L;
     Tarea tarea = tareaService.nuevaTarea(idUsuario, "Pagar el alquiler", "", null, idColumna);
     assertEquals(3, tareaService.allTareasUsuario(1000L).size());

     Tarea t = tareaService.marcarTerminada(tarea.getId());
     assertTrue(t.getTerminada());
   }

   @Test
   public void tareasTerminadasTest() {
      TareaService tareaService = newTareaService();
      long idUsuario = 1000L;
      long idColumna = 1000L;
      Tarea tarea = tareaService.nuevaTarea(idUsuario, "Pagar el alquiler", "", null, idColumna);
      List<Tarea> tareas = tareaService.allTareasTerminadasUsuario(idUsuario);

      assertEquals(0, tareas.size());
      Tarea t = tareaService.marcarTerminada(tarea.getId());
      tareas = tareaService.allTareasTerminadasUsuario(idUsuario);
      assertEquals(1, tareas.size());
   }

   @Test
   public void testTareaEnviarPapelera() {
     TareaService tareaService = newTareaService();
     long idUsuario = 1000L;
     long idColumna = 1000L;
     Tarea tarea = tareaService.nuevaTarea(idUsuario, "Pagar el alquiler", "", null, idColumna);
     tarea = tareaService.enviarPapelera(tarea.getId());

     assertNotNull(tarea.getDeletedAt());
   }
}
