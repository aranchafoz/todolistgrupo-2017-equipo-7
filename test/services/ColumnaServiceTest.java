import org.junit.*;
import static org.junit.Assert.*;

import play.db.jpa.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import java.util.List;

import models.Columna;
import models.Tarea;

import play.inject.guice.GuiceApplicationBuilder;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.Environment;


import services.ColumnaService;
import services.ColumnaServiceException;
import services.TareaService;


public class ColumnaServiceTest {
  static private Injector injector;

  @BeforeClass
  static public void initApplication() {
     GuiceApplicationBuilder guiceApplicationBuilder =
         new GuiceApplicationBuilder().in(Environment.simple());
     injector = guiceApplicationBuilder.injector();
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

  private ColumnaService newColumnaService() {
    return injector.instanceOf(ColumnaService.class);
  }

  private TareaService newTareaService() {
     return injector.instanceOf(TareaService.class);
  }

  // Test 1: allColumnasTableroEstanOrdenadas
  @Test
  public void allColumnasTableroEstanOrdenadas() {
     ColumnaService columnaService = newColumnaService();
     List<Columna> columnas = columnaService.allColumnasTablero(1001L);
     assertEquals("Columna test 1", columnas.get(0).getNombre());
     assertEquals("Columna test 2", columnas.get(1).getNombre());
  }

  // Test 3: nuevaColumnaService
  @Test
  public void nuevaColumnaService() {
    ColumnaService columnaService = newColumnaService();
    long idTablero = 2000L;
    columnaService.nuevaColumna(idTablero, "Nueva columna test 1", 1);
    assertEquals(5, columnaService.allColumnasTablero(2000L).size());
  }

  // Test 4: exceptionSiTableroNoExisteRecuperandoSusColumnas
  @Test(expected = ColumnaServiceException.class)
  public void crearNuevaColumnaNoExistenteLanzaExcepcion(){
    ColumnaService columnaService = newColumnaService();
     List<Columna> columnas = columnaService.allColumnasTablero(1011L);
  }

  @Test
  public void modificarColumna() {
    ColumnaService columnaService = newColumnaService();
    long idColumna = 1000L;
    columnaService.modificaColumna(idColumna, "Pendiente");
    Columna columna = columnaService.obtenerColumna(idColumna);
    assertEquals("Pendiente", columna.getNombre());
  }

  @Test
  public void moverColumna() {
    ColumnaService columnaService = newColumnaService();
    long idColumna = 1000L;
    columnaService.moverColumna(idColumna, 3);
    Columna columna = columnaService.obtenerColumna(idColumna);
    assertEquals((Integer) 3, columna.getPosicion());
  }

  // Test #23: borrado columna
  @Test
  public void borradoColumna() {
    ColumnaService columnaService = newColumnaService();
     long idColumna = 3000L;
     columnaService.borraColumna(idColumna);
     assertNull(columnaService.obtenerColumna(idColumna));
  }

  // SGT-11: Tareas en tableros
  @Test(expected = ColumnaServiceException.class)
  public void testBorradoColumnaConTareas() {
    ColumnaService columnaService = newColumnaService();
     long idColumna = 3001L;
     columnaService.borraColumna(idColumna);
  }

  @Test
  public void testCrearTareasEnColumna() {
    TareaService tareaService = newTareaService();
    ColumnaService columnaService = newColumnaService();
    long idUsuario = 1000L;
    long idColumna = 2000L;
    Tarea tarea1 = tareaService.nuevaTarea(idUsuario, "Tarea test crear varias 1", "", null, idColumna);
    Tarea tarea2 = tareaService.nuevaTarea(idUsuario, "Tarea test crear varias 2", "", null, idColumna);
    Tarea tarea3 = tareaService.nuevaTarea(idUsuario, "Tarea test crear varias 3", "", null, idColumna);
    Tarea tarea4 = tareaService.nuevaTarea(idUsuario, "Tarea test crear varias 4", "", null, idColumna);
    Columna columna = columnaService.obtenerColumna(idColumna);
    assertEquals("Columna test crear 1", tarea1.getColumna().getNombre());
    assertEquals("Columna test crear 1", tarea2.getColumna().getNombre());
    assertEquals("Columna test crear 1", tarea3.getColumna().getNombre());
    assertEquals("Columna test crear 1", tarea4.getColumna().getNombre());
    assertEquals(4, columna.getTareas().size());
  }

  @Test
  public void testBorradoColumnaConTareasBorradas() {
    TareaService tareaService = newTareaService();
    ColumnaService columnaService = newColumnaService();
    long idUsuario = 1000L;
    long idColumna = 2000L;
    Tarea tarea1 = tareaService.nuevaTarea(idUsuario, "Tarea test crear varias 1", "", null, idColumna);
    Tarea tarea2 = tareaService.nuevaTarea(idUsuario, "Tarea test crear varias 2", "", null, idColumna);
    Tarea tarea3 = tareaService.nuevaTarea(idUsuario, "Tarea test crear varias 3", "", null, idColumna);
    Tarea tarea4 = tareaService.nuevaTarea(idUsuario, "Tarea test crear varias 4", "", null, idColumna);

    tareaService.borraTarea(tarea1.getId());
    tareaService.borraTarea(tarea2.getId());
    tareaService.borraTarea(tarea3.getId());
    tareaService.borraTarea(tarea4.getId());
    columnaService.borraColumna(idColumna);
    
    assertNull(columnaService.obtenerColumna(idColumna));
  }
}
