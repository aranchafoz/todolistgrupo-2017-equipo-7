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
import services.EtiquetaService;
import models.Usuario;
import models.Tablero;
import models.Etiqueta;
import security.ActionAuthenticator;


public class GestionEtiquetasController extends Controller {

   @Inject FormFactory formFactory;
   @Inject UsuarioService usuarioService;
   @Inject TableroService tableroService;
   @Inject EtiquetaService etiquetaService;

   @Security.Authenticated(ActionAuthenticator.class)
   public Result creaNuevaEtiqueta(Long idUsuario, Long idTablero) {
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);
      if (connectedUser != idUsuario) {
         return unauthorized("Lo siento, no estás autorizado");
      } else {
         Form<Etiqueta> etiquetaForm = formFactory.form(Etiqueta.class).bindFromRequest();
         if (etiquetaForm.hasErrors()) {
            return redirect(controllers.routes.GestionTablerosController.detalleTablero(idUsuario, idTablero));
         }
         Tablero tablero = tableroService.obtenerTablero(idTablero);
         Etiqueta etiqueta = etiquetaForm.get();
         etiquetaService.nuevaEtiquetaTablero(idTablero, etiqueta.getNombre());
         flash("aviso", "La etiqueta se ha grabado correctamente");
         return redirect(controllers.routes.GestionTablerosController.detalleTablero(idUsuario, idTablero));
      }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result grabaNombreEtiquetaModificado(Long idUsuario, Long idTablero, Long idEtiqueta) throws java.text.ParseException {
     String connectedUserStr = session("connected");
     Long connectedUser =  Long.valueOf(connectedUserStr);
     if (connectedUser != idUsuario) {
        return unauthorized("Lo siento, no estás autorizado");
     } else {
       DynamicForm requestData = formFactory.form().bindFromRequest();
       String nuevoNombre = requestData.get("nombre");

       Etiqueta etiqueta = etiquetaService.obtenerEtiqueta(idEtiqueta);
       etiquetaService.modificaEtiqueta(etiqueta.getId(), nuevoNombre);

       return redirect(controllers.routes.GestionTablerosController.detalleTablero(idUsuario, idTablero));
     }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result borraEtiqueta(Long idUsuario, Long idTablero, Long idEtiqueta) {
     String connectedUserStr = session("connected");
     Long connectedUser =  Long.valueOf(connectedUserStr);
     if (connectedUser != idUsuario) {
        return unauthorized("Lo siento, no estás autorizado");
     } else {
       Tablero tablero = tableroService.obtenerTablero(idTablero);
       if (tablero == null) {
          return notFound("Tablero no encontrado");
       } else {
          etiquetaService.borraEtiqueta(idEtiqueta);
          flash("aviso", "Etiqueta borrada correctamente");
          return ok();
       }
     }
   }
 }
