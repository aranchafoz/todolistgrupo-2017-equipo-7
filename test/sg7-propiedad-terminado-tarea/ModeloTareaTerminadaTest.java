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

public class ModeloTareaTerminadaTest {

  @Test
  public void testTareaNoTerminada() {
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    Tarea t1 = new Tarea(usuario,"tarea1");

    assertFalse(t1.getTerminada());
  }
}
