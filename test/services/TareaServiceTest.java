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

   @Test
   public void testListarTareasPapelera() {
     TareaService tareaService = newTareaService();
     long idUsuario = 1000L;
     long idColumna = 1000L;
     Tarea tarea = tareaService.nuevaTarea(idUsuario, "Pagar el alquiler", "", null, idColumna);
     Tarea tarea2 = tareaService.nuevaTarea(idUsuario, "Pagar la luz", "", null, idColumna);
     Tarea tarea3 = tareaService.nuevaTarea(idUsuario, "Pagar el agua", "", null, idColumna);

     tarea = tareaService.enviarPapelera(tarea.getId());

     List<Tarea> tareasBorradas = tareaService.allTareasPapeleraUsuario(idUsuario);
     assertEquals(1, tareasBorradas.size());
   }

   @Test
   public void testRecuperarTarea() {
     long idUsuario = 1000L;
     long idColumna = 1000L;
     TareaService tareaService = newTareaService();
     Tarea tarea = tareaService.nuevaTarea(idUsuario, "Pagar el alquiler", "", null, idColumna);
     tarea = tareaService.enviarPapelera(tarea.getId());

     assertNotNull(tarea.getDeletedAt());
     tarea = tareaService.quitarDePapelera(tarea.getId());
     assertNull(tarea.getDeletedAt());
   }

   // SGT-11: Tareas en tableros
   @Test(expected = TareaServiceException.class)
   public void testCrearTareaEnColumnaNoExistente() {
     TareaService tareaService = newTareaService();
     long idUsuario = 1000L;
     long idColumna = 0100L;
     Tarea tarea = tareaService.nuevaTarea(idUsuario, "Pagar el alquiler", "", null, idColumna);
   }

   @Test
   public void testCrearTareaEnColumna() {

     TareaService tareaService = newTareaService();
     long idUsuario = 1000L;
     long idColumna = 1000L;
     Tarea tarea = tareaService.nuevaTarea(idUsuario, "Pagar el alquiler", "", null, idColumna);

     assertEquals("Columna test 1", tarea.getColumna().getNombre());
   }

   @Test(expected = TareaServiceException.class)
   public void testActualizarTareaAColumnaNoExistente() {
     TareaService tareaService = newTareaService();
     Tarea tarea = tareaService.obtenerTarea(1000L);
     long idColumna = 0100L;
     tarea = tareaService.modificaTarea(1000L, "Pagar el alquiler", "", null, idColumna);
   }

   @Test
   public void testActualizarTareaADiferenteColumna() {
     TareaService tareaService = newTareaService();
     Tarea tarea = tareaService.obtenerTarea(1000L);
     long idColumna = 1001L;
     tarea = tareaService.modificaTarea(1000L, "Pagar el alquiler", "", null, idColumna);
     assertEquals("Columna test 2", tarea.getColumna().getNombre());

   }

   @Test
   public void asignarEtiquetasEnTarea() {
     TareaService tareaService = newTareaService();
     Tarea tarea = tareaService.obtenerTarea(1000L);
     long idEtiqueta = 1000L;
     tarea = tareaService.asignaEtiquetaTarea(tarea.getId(), idEtiqueta);
     assertEquals(3, tarea.getEtiquetas().size());
   }

   @Test
   public void asignarUsuariosEnTarea() {
     TareaService tareaService = newTareaService();
     Tarea tarea = tareaService.obtenerTarea(3000L);
     long idUsuario = 1000L;
     assertEquals(0, tarea.getUsuariosAsignados().size());
     tarea = tareaService.asignarTareaUsuario(tarea.getId(), idUsuario);
     assertEquals(1, tarea.getUsuariosAsignados().size());
   }

   @Test
   public void tareasAsignadasUsuario() {
     TareaService tareaService = newTareaService();
     long idColumna = 1001L;
     long idUsuario = 1000L;
     long usuarioAsignado = 1001L;
     Tarea t1 = tareaService.nuevaTarea(idUsuario, "t1", "", null, idColumna);
     Tarea t2 = tareaService.nuevaTarea(idUsuario, "t2", "", null, idColumna);
     Tarea t3 = tareaService.nuevaTarea(idUsuario, "t3", "", null, idColumna);

     List<Tarea> tareasUsuario = tareaService.allTareasAsignadasUsuario(usuarioAsignado);

     assertEquals(0, tareasUsuario.size());
     t1 = tareaService.asignarTareaUsuario(t1.getId(), usuarioAsignado);
     t2 = tareaService.asignarTareaUsuario(t2.getId(), usuarioAsignado);
     t3 = tareaService.asignarTareaUsuario(t3.getId(), usuarioAsignado);

     tareasUsuario = tareaService.allTareasAsignadasUsuario(usuarioAsignado);
     assertEquals(3, tareasUsuario.size());
   }

   @Test
   public void usuariosAsignadosTarea() {
     TareaService tareaService = newTareaService();
     long idColumna = 1001L;
     long u1 = 1000L;
     long u2 = 1001L;
     long u3 = 1002L;
     Tarea t1 = tareaService.nuevaTarea(u1, "t1", "", null, idColumna);

     List<Usuario> usuariosTarea = tareaService.allUsuariosAsignadosTarea(t1.getId());

     assertEquals(0, usuariosTarea.size());
     t1 = tareaService.asignarTareaUsuario(t1.getId(), u2);
     t1 = tareaService.asignarTareaUsuario(t1.getId(), u3);

     usuariosTarea = tareaService.allUsuariosAsignadosTarea(t1.getId());
     assertEquals(2, usuariosTarea.size());
   }
}
