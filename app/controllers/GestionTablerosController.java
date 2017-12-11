package controllers;

import play.mvc.*;

import views.html.*;
import javax.inject.*;
import play.data.Form;
import play.data.FormFactory;
import play.data.DynamicForm;
import play.Logger;

import java.util.List;
import java.util.ArrayList;

import services.UsuarioService;
import services.TableroService;
import services.ColumnaService;
import services.EtiquetaService;
import models.Usuario;
import models.Tablero;
import models.Columna;
import models.Etiqueta;
import security.ActionAuthenticator;

public class GestionTablerosController extends Controller {

   @Inject FormFactory formFactory;
   @Inject UsuarioService usuarioService;
   @Inject TableroService tableroService;
   @Inject ColumnaService columnaService;
   @Inject EtiquetaService etiquetaService;


  // Comprobamos si hay alguien logeado con @Security.Authenticated(ActionAuthenticator.class)
  // https://alexgaribay.com/2014/06/15/authentication-in-play-framework-using-java/
  @Security.Authenticated(ActionAuthenticator.class)
  public Result formularioNuevoTablero(Long idUsuario) {
     String connectedUserStr = session("connected");
     Long connectedUser =  Long.valueOf(connectedUserStr);
     if (connectedUser != idUsuario) {
        return unauthorized("Lo siento, no estás autorizado");
     } else {
        Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
        return ok(formNuevoTablero.render(usuario, formFactory.form(Tablero.class),""));
     }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result creaNuevoTablero(Long idUsuario) {
     String connectedUserStr = session("connected");
     Long connectedUser =  Long.valueOf(connectedUserStr);
     if (connectedUser != idUsuario) {
        return unauthorized("Lo siento, no estás autorizado");
     } else {
        Form<Tablero> tableroForm = formFactory.form(Tablero.class).bindFromRequest();
        if (tableroForm.hasErrors()) {
           Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
           return badRequest(formNuevoTablero.render(usuario, formFactory.form(Tablero.class), "Hay errores en el formulario"));
        }
        Tablero tablero = tableroForm.get();
        tableroService.nuevoTablero(idUsuario, tablero.getNombre());
        flash("aviso", "El tablero se ha grabado correctamente");
        return redirect(controllers.routes.GestionTablerosController.listaTableros(idUsuario));
     }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result listaTableros(Long idUsuario) {
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    if (connectedUser != idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      String aviso = flash("aviso");
      Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
      List<Tablero> tablerosAdministrados = tableroService.allTablerosAdministradosUsuario(idUsuario);
      List<Tablero> tablerosParticipados = tableroService.allTablerosParticipadosUsuario(idUsuario);
      List<Tablero> restoTableros = tableroService.obtenerRestoTableros(idUsuario);
      return ok(listaTableros.render(tablerosAdministrados, tablerosParticipados, restoTableros, usuario, aviso));
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result apuntarse(Long idUsuario, Long idTablero) {
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    if (connectedUser != idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      tableroService.apuntaParticipante(idUsuario, idTablero);

      flash("aviso", "El usuario se ha apuntado correctamente");
      return redirect(controllers.routes.GestionTablerosController.listaTableros(idUsuario));
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result detalleTablero(Long idUsuario, Long idTablero) {
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    if (connectedUser != idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      Tablero tablero = tableroService.obtenerTablero(idTablero);
      if (tablero == null) {
         return notFound("Tablero no encontrado");
      } else {
        List<Usuario> participantes = new ArrayList<Usuario>();
        participantes.addAll(tablero.getParticipantes());
        List<Columna> columnas = columnaService.allColumnasTablero(idTablero);
        List<Etiqueta> etiquetas = etiquetaService.allEtiquetasTablero(idTablero);
        Usuario usuario = usuarioService.findUsuarioPorId(connectedUser);
        return ok(detalleTablero.render(tablero, participantes, columnas, formFactory.form(Columna.class), usuario, false, etiquetas, formFactory.form(Etiqueta.class)));
      }
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result cerrarTablero(Long idUsuario, Long idTablero) {
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    if (connectedUser != idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      Tablero tablero = tableroService.obtenerTablero(idTablero);
      if (tablero == null) {
         return notFound("Tablero no encontrado");
      } else {
        tableroService.cerrarTablero(idTablero);
        flash("aviso", "El tablero se ha cerrado correctamente");
        return redirect(controllers.routes.GestionTablerosController.listaTableros(idUsuario));
      }
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result editarTablero(Long idUsuario, Long idTablero) {
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    if (connectedUser != idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      DynamicForm requestData = formFactory.form().bindFromRequest();
      String nuevoNombre = requestData.get("nombre");

      Tablero tablero = tableroService.obtenerTablero(idTablero);
      tablero = tableroService.editarTablero(tablero.getId(), nuevoNombre);

      return redirect(controllers.routes.GestionTablerosController.detalleTablero(idUsuario, idTablero));
    }
  }
 }
