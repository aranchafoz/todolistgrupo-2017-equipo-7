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
import services.TareaService;
import services.TableroService;
import services.ColumnaService;
import models.Usuario;
import models.Tarea;
import models.Tablero;
import models.Columna;
import security.ActionAuthenticator;
import java.util.Date;
import java.text.SimpleDateFormat;
// Calendar
import play.libs.Json;
import play.libs.Json.*;
import play.api.libs.json.JsValue;

import static play.libs.Json.toJson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GestionTareasController extends Controller {

   @Inject FormFactory formFactory;
   @Inject UsuarioService usuarioService;
   @Inject TareaService tareaService;
   @Inject TableroService tableroService;
   @Inject ColumnaService columnaService;

   @Security.Authenticated(ActionAuthenticator.class)
   public Result seleccionaTableroParaNuevaTarea(Long idUsuario) {
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);
      Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
      if (connectedUser != idUsuario) {
         return unauthorized("Lo siento, no estás autorizado");
      } else {
        DynamicForm form = Form.form().bindFromRequest();

        if (form.get("tablero").equals("")) {
          List<Tarea> tareas = tareaService.allTareasUsuario(idUsuario);

          List<Tablero> tablerosAdministrados = tableroService.allTablerosAdministradosUsuario(idUsuario);
          List<Tablero> tablerosParticipados = tableroService.allTablerosParticipadosUsuario(idUsuario);

          List<Tablero> tableros = new ArrayList<Tablero>();
          tableros.addAll(tablerosAdministrados);
          tableros.addAll(tablerosParticipados);

          return badRequest(listaTareas.render(tareas, usuario, tableros, formFactory.form(Tarea.class), "Hay errores en el formulario"));
        }

        Long tableroId = Long.parseLong( form.get("tablero"), 10 );
        Tablero tablero = tableroService.obtenerTablero(tableroId);

        List<Columna> columnas = new ArrayList<Columna>();
        columnas.addAll(tablero.getColumnas());

        return ok(formNuevaTarea.render(usuario, formFactory.form(Tarea.class), tablero, columnas, ""));
      }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result creaNuevaTarea(Long idUsuario, Long idTablero) throws java.text.ParseException{
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);
      if (connectedUser != idUsuario) {
         return unauthorized("Lo siento, no estás autorizado");
      } else {
        DynamicForm form = Form.form().bindFromRequest();

         if (form.get("titulo").equals("") || form.get("columna").equals("") || form.get("fechaLimite").equals("")) {
            Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);

            Tablero tablero = tableroService.obtenerTablero(idTablero);
            List<Columna> columnas = new ArrayList<Columna>();
            columnas.addAll(tablero.getColumnas());

            return badRequest(formNuevaTarea.render(usuario, formFactory.form(Tarea.class), tablero, columnas, "Hay errores en el formulario"));
         }
         String titulo = form.get("titulo");

         String descripcion = form.get("descripcion");

         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
         Date fechaLimite = format.parse( form.get("fechaLimite") );

         Long columnaId = Long.parseLong( form.get("columna"), 10 );

         tareaService.nuevaTarea(idUsuario, titulo, descripcion, fechaLimite, columnaId);

         flash("aviso", "La tarea se ha grabado correctamente");
         return redirect(controllers.routes.GestionTareasController.listaTareas(idUsuario));
      }
   }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result asignaTareaUsuario(Long idUsuario, Long idTarea) {
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);
      if (connectedUser != idUsuario) {
         return unauthorized("Lo siento, no estás autorizado");
       }

        DynamicForm form = Form.form().bindFromRequest();
        Long selectedId = Long.parseLong(form.get("usuario"), 10);

        if (idUsuario != null) {
          Tarea tarea = tareaService.obtenerTarea(idTarea);
          tareaService.asignarTareaUsuario(tarea.getId(), selectedId);
        }

        flash("aviso", "La tarea se ha asignado correctamente");
        return redirect(controllers.routes.GestionTareasController.listaTareas(idUsuario));
    }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result creaNuevaTareaEnColumna(Long idUsuario, Long idTablero, Long idColumna) throws java.text.ParseException{
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);
      if (connectedUser != idUsuario) {
         return unauthorized("Lo siento, no estás autorizado");
      } else {
        DynamicForm form = Form.form().bindFromRequest();

         if (form.get("titulo").equals("")) {
            Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
            Tablero tablero = tableroService.obtenerTablero(idTablero);
            List<Usuario> participantes = new ArrayList<Usuario>();
            participantes.addAll(tablero.getParticipantes());

             List<Columna> columnas = new ArrayList<Columna>();
             columnas.addAll(tablero.getColumnas());

            return badRequest(detalleTablero.render(tablero, participantes, columnas, formFactory.form(Columna.class), usuario, false, "Hay errores en el formulario"));
         }

         tareaService.nuevaTarea(idUsuario, form.get("titulo"), null, null, idColumna);

         flash("aviso", "La tarea se ha grabado correctamente");
         return redirect(controllers.routes.GestionTablerosController.detalleTablero(idUsuario, idTablero));
      }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result listaTareas(Long idUsuario) {
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);
      if (connectedUser != idUsuario) {
         return unauthorized("Lo siento, no estás autorizado");
      } else {
         String aviso = flash("aviso");
         Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
         List<Tarea> tareas = tareaService.allTareasUsuario(idUsuario);

         List<Tablero> tablerosAdministrados = tableroService.allTablerosAdministradosUsuario(idUsuario);
         List<Tablero> tablerosParticipados = tableroService.allTablerosParticipadosUsuario(idUsuario);

         List<Tablero> tableros = new ArrayList<Tablero>();
         tableros.addAll(tablerosAdministrados);
         tableros.addAll(tablerosParticipados);

         return ok(listaTareas.render(tareas, usuario, tableros, formFactory.form(Tarea.class), aviso));
      }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result listaTareasTerminadas(Long idUsuario) {
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);
      if (connectedUser != idUsuario) {
         return unauthorized("Lo siento, no estás autorizado");
      } else {
        String aviso = flash("aviso");
        Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
        List<Tarea> tareas = tareaService.allTareasTerminadasUsuario(idUsuario);

        List<Tablero> tablerosAdministrados = tableroService.allTablerosAdministradosUsuario(idUsuario);
        List<Tablero> tablerosParticipados = tableroService.allTablerosParticipadosUsuario(idUsuario);

        List<Tablero> tableros = new ArrayList<Tablero>();
        tableros.addAll(tablerosAdministrados);
        tableros.addAll(tablerosParticipados);

        return ok(listaTareasTerminadas.render(tareas, usuario, tableros, formFactory.form(Tarea.class), aviso));
      }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result listaTareasAsignadas(Long idUsuario) {
     String connectedUserStr = session("connected");
     Long connectedUser =  Long.valueOf(connectedUserStr);
     if (connectedUser != idUsuario) {
        return unauthorized("Lo siento, no estás autorizado");
     } else {
        Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
        List<Tarea> tareas = tareaService.allTareasAsignadasUsuario(idUsuario);
        return ok(listaTareasAsignadas.render(tareas, usuario));
      }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result formularioAsignaTarea(Long idTarea) {
     Tarea tarea = tareaService.obtenerTarea(idTarea);
     if (tarea == null) {
        return notFound("Tarea no encontrada");
      } else {
        String connectedUserStr = session("connected");
        Long connectedUser =  Long.valueOf(connectedUserStr);
        if (connectedUser != tarea.getUsuario().getId()) {
           return unauthorized("Lo siento, no estás autorizado");
        } else {
          Tablero tablero = tarea.getColumna().getTablero();
          List<Usuario> usuarios = tableroService.getUsuariosParticipantes(tablero.getId());
          Usuario usuario = usuarioService.findUsuarioPorId(connectedUser);

          return ok(formularioAsignaTarea.render(tarea, usuarios, usuario));
        }
      }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result formularioEditaTarea(Long idTarea) {
      Tarea tarea = tareaService.obtenerTarea(idTarea);
      if (tarea == null) {
         return notFound("Tarea no encontrada");
      } else {
         String connectedUserStr = session("connected");
         Long connectedUser =  Long.valueOf(connectedUserStr);
         if (connectedUser != tarea.getUsuario().getId()) {
            return unauthorized("Lo siento, no estás autorizado");
         } else {

             List<Columna> columnas = new ArrayList<Columna>();
             columnas.addAll(tarea.getColumna().getTablero().getColumnas());


            return ok(formModificacionTarea.render(tarea.getUsuario(), formFactory.form(Tarea.class),
            tarea.getId(),
            tarea.getTitulo(),
            tarea.getDescripcion(),
            tarea.getFechaLimite(),
            tarea.getColumna(),
            columnas, ""));
         }
      }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result detalleTarea(Long idUsuario, Long idTarea) {
     String connectedUserStr = session("connected");
     Long connectedUser =  Long.valueOf(connectedUserStr);
     if (connectedUser != idUsuario) {
        return unauthorized("Lo siento, no estás autorizado");
     } else {
        Tarea tarea = tareaService.obtenerTarea(idTarea);
        if (tarea == null) {
          return notFound("Tarea no encontrada");
        } else {
          Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
          return ok(detalleTarea.render(usuario, tarea));
        }
      }
   }


   @Security.Authenticated(ActionAuthenticator.class)
   public Result terminaTarea(Long idTarea) {
     tareaService.marcarTerminada(idTarea);
     flash("aviso", "Tarea añadida a terminadas");
     return ok();
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result grabaTareaModificada(Long idTarea) throws java.text.ParseException{
      Tarea tarea = tareaService.obtenerTarea(idTarea);
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);

      if (connectedUser != tarea.getUsuario().getId()) {
         return unauthorized("Lo siento, no estás autorizado");
      } else {
        DynamicForm form = Form.form().bindFromRequest();

         if (form.get("titulo").equals("") || form.get("columna").equals("") || form.get("fechaLimite").equals("")) {
            Tablero tablero = tarea.getColumna().getTablero();
            List<Columna> columnas = new ArrayList<Columna>();
            columnas.addAll(tablero.getColumnas());


            return badRequest(formNuevaTarea.render(tarea.getUsuario(), formFactory.form(Tarea.class), tablero, columnas, "Hay errores en el formulario"));
         }

         String nuevoTitulo = form.get("titulo");

         String nuevaDescripcion = form.get("descripcion");

         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
         Date nuevaFechaLimite = format.parse( form.get("fechaLimite") );

         Long nuevaColumnaId = Long.parseLong( form.get("columna"), 10 );

         tareaService.modificaTarea(idTarea, nuevoTitulo, nuevaDescripcion, nuevaFechaLimite, nuevaColumnaId);
         return redirect(controllers.routes.GestionTareasController.listaTareas(tarea.getUsuario().getId()));

      }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result borraTarea(Long idTarea) {
      tareaService.borraTarea(idTarea);
      flash("aviso", "Tarea borrada correctamente");
      return ok();
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result enviarPapelera(Long idTarea) {
     tareaService.enviarPapelera(idTarea);
     flash("aviso", "Tarea enviada a papelera correctamente");
     return ok();
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result listarTareasPapelera(Long idUsuario) {
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);
      if (connectedUser != idUsuario) {
         return unauthorized("Lo siento, no estás autorizado");
      } else {
         String aviso = flash("aviso");
         Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
         List<Tarea> tareas = tareaService.allTareasPapeleraUsuario(idUsuario);
         return ok(listaTareasPapelera.render(tareas, usuario, aviso));
      }
   }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result recuperarTarea(Long idTarea) {
      tareaService.quitarDePapelera(idTarea);
      flash("aviso", "Tarea recuperada correctamente");
      return ok();
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result vistaCalendario(Long idUsuario) {
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);
      if (connectedUser != idUsuario) {
         return unauthorized("Lo siento, no estás autorizado");
      } else {
         String aviso = flash("aviso");
         Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
         List<Tarea> tareas = tareaService.allTareasUsuario(idUsuario);

         List<String> tareasString = new ArrayList<String>();
         for(Tarea t: tareas) {
           String tareaString = t.toJSON();
           tareasString.add(tareaString);
         }

         return ok(calendario.render(usuario, tareasString));
      }
    }
}
