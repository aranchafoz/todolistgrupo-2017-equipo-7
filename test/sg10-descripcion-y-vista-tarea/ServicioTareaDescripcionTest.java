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

import java.util.Date;

public class ServicioTareaDescripcionTest {
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

    @Test
    public void tareaSinDescripcionServiceTest() {
        TareaService tareaService = newTareaService();
        long idUsuario = 1000L;
        Tarea tarea = tareaService.nuevaTarea(idUsuario, "Pagar el alquiler", null);

        assertEquals("", t.getDescripcion());
    }

    /*
    @Test
    public void tareasTerminadasTest() {
        TareaService tareaService = newTareaService();
        long idUsuario = 1000L;
        Tarea tarea = tareaService.nuevaTarea(idUsuario, "Pagar el alquiler", null);
        List<Tarea> tareas = tareaService.allTareasTerminadasUsuario(idUsuario);

        assertEquals(0, tareas.size());
        Tarea t = tareaService.marcarTerminada(tarea.getId());
        tareas = tareaService.allTareasTerminadasUsuario(idUsuario);
        assertEquals(1, tareas.size());
    }
    */
}