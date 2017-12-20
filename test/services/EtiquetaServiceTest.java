import org.junit.*;
import static org.junit.Assert.*;

import play.db.jpa.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import java.util.List;

import models.Etiqueta;
import models.Tarea;

import play.inject.guice.GuiceApplicationBuilder;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.Environment;


import services.EtiquetaService;
import services.EtiquetaServiceException;
import services.TareaService;


public class EtiquetaServiceTest {
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

  private EtiquetaService newEtiquetaService() {
    return injector.instanceOf(EtiquetaService.class);
  }

  private TareaService newTareaService() {
     return injector.instanceOf(TareaService.class);
  }

  @Test
  public void nuevaEtiquetaEnTablero() {
    EtiquetaService etiquetaService = newEtiquetaService();
    long idTablero = 2000L;
    etiquetaService.nuevaEtiquetaTablero(idTablero, "Nueva etiqueta test 1");
    assertEquals(5, etiquetaService.allEtiquetasTablero(2000L).size());
  }

  @Test(expected = EtiquetaServiceException.class)
  public void crearNuevaEtiquetaNoExistenteLanzaExcepcion(){
    EtiquetaService etiquetaService = newEtiquetaService();
     List<Etiqueta> etiquetas = etiquetaService.allEtiquetasTablero(1011L);
  }

  @Test
  public void modificarEtiqueta() {
    EtiquetaService etiquetaService = newEtiquetaService();
    long idEtiqueta = 1000L;
    etiquetaService.modificaEtiqueta(idEtiqueta, "Etiqueta Modificada");
    Etiqueta etiqueta = etiquetaService.obtenerEtiqueta(idEtiqueta);
    assertEquals("Etiqueta Modificada", etiqueta.getNombre());
  }

  @Test
  public void borradoEtiqueta() {
    EtiquetaService etiquetaService = newEtiquetaService();
     long idEtiqueta = 3000L;
     etiquetaService.borraEtiqueta(idEtiqueta);
     assertNull(etiquetaService.obtenerEtiqueta(idEtiqueta));
  }

}
