package controllers;

import play.mvc.*;

import views.html.*;
import javax.inject.*;
import play.data.Form;
import play.data.FormFactory;
import play.data.DynamicForm;
import play.Logger;

import java.util.List;

import services.UsuarioService;
import services.TareaService;
import models.Usuario;
import models.Tarea;
import security.ActionAuthenticator;
import java.util.Date;
import java.text.SimpleDateFormat;

public class GestionTareasController extends Controller {

   @Inject FormFactory formFactory;
   @Inject UsuarioService usuarioService;
   @Inject TareaService tareaService;

   // Comprobamos si hay alguien logeado con @Security.Authenticated(ActionAuthenticator.class)
   // https://alexgaribay.com/2014/06/15/authentication-in-play-framework-using-java/
   @Security.Authenticated(ActionAuthenticator.class)
   public Result formularioNuevaTarea(Long idUsuario) {
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);
      if (connectedUser != idUsuario) {
         return unauthorized("Lo siento, no estás autorizado");
      } else {
         Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
         return ok(formNuevaTarea.render(usuario, formFactory.form(Tarea.class),""));
      }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result creaNuevaTarea(Long idUsuario) throws java.text.ParseException{
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);
      if (connectedUser != idUsuario) {
         return unauthorized("Lo siento, no estás autorizado");
      } else {
         Form<Tarea> tareaForm = formFactory.form(Tarea.class).bindFromRequest();
         if (tareaForm.hasErrors()) {
            Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
            return badRequest(formNuevaTarea.render(usuario, formFactory.form(Tarea.class), "Hay errores en el formulario"));
         }

         Tarea tarea = tareaForm.get();

         tareaService.nuevaTarea(idUsuario, tarea.getTitulo(), tarea.getDescripcion(), tarea.getFechaLimite(), tarea.getColumna().getId());

         flash("aviso", "La tarea se ha grabado correctamente");
         return redirect(controllers.routes.GestionTareasController.listaTareas(idUsuario));
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
         return ok(listaTareas.render(tareas, usuario, aviso));
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
         return ok(listaTareasTerminadas.render(tareas, usuario, aviso));
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
            return ok(formModificacionTarea.render(tarea.getUsuario(), formFactory.form(Tarea.class),
            tarea.getId(),
            tarea.getTitulo(),
            tarea.getDescripcion(),
            tarea.getFechaLimite(),
            tarea.getColumna(), ""));
         }
      }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result detalleTarea(Long idTarea) {
      Tarea tarea = tareaService.obtenerTarea(idTarea);
      if (tarea == null) {
         return notFound("Tarea no encontrada");
      } else {
         String connectedUserStr = session("connected");
         Long connectedUser =  Long.valueOf(connectedUserStr);
         if (connectedUser != tarea.getUsuario().getId()) {
            return unauthorized("Lo siento, no estás autorizado");
         } else {
            return ok(detalleTarea.render(tarea));
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
         Form<Tarea> tareaForm = formFactory.form(Tarea.class).bindFromRequest();
         if (tareaForm.hasErrors()) {
            return badRequest(formNuevaTarea.render(tarea.getUsuario(), formFactory.form(Tarea.class), "Hay errores en el formulario"));
         }
         Tarea tareaInput = tareaForm.get();

         String nuevoTitulo = tareaInput.getTitulo();
         Date nuevaFechaLimite = tareaInput.getFechaLimite();
         String nuevaDescripcion = tareaInput.getDescripcion();
         Long nuevaColumna = tareaInput.getColumna().getId();

         tareaService.modificaTarea(idTarea, nuevoTitulo, nuevaDescripcion, nuevaFechaLimite, nuevaColumna);
         return redirect(controllers.routes.GestionTareasController.listaTareas(tarea.getUsuario().getId()));
      }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result borraTarea(Long idTarea) {
      tareaService.borraTarea(idTarea);
      flash("aviso", "Tarea borrada correctamente");
      return ok();
   }
}
