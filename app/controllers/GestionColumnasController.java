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
import models.Usuario;
import models.Tablero;
import models.Columna;
import models.Etiqueta;
import security.ActionAuthenticator;


public class GestionColumnasController extends Controller {

   @Inject FormFactory formFactory;
   @Inject UsuarioService usuarioService;
   @Inject TableroService tableroService;
   @Inject ColumnaService columnaService;

   @Security.Authenticated(ActionAuthenticator.class)
   public Result creaNuevaColumna(Long idUsuario, Long idTablero) {
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);
      if (connectedUser != idUsuario) {
         return unauthorized("Lo siento, no estás autorizado");
      } else {
         Form<Columna> columnaForm = formFactory.form(Columna.class).bindFromRequest();
         if (columnaForm.hasErrors()) {
            return redirect(controllers.routes.GestionTablerosController.detalleTablero(idUsuario, idTablero));
         }
         Tablero tablero = tableroService.obtenerTablero(idTablero);
         int posicion = tablero.getColumnas().size();
         Columna columna = columnaForm.get();
         columnaService.nuevaColumna(idTablero, columna.getNombre(), posicion);
         flash("aviso", "La columna se ha grabado correctamente");
         return redirect(controllers.routes.GestionTablerosController.detalleTablero(idUsuario, idTablero));
      }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result grabaNuevaPosicionColumna(Long idUsuario, Long idTablero) throws java.text.ParseException {
     String connectedUserStr = session("connected");
     Long connectedUser =  Long.valueOf(connectedUserStr);
     if (connectedUser != idUsuario) {
        return unauthorized("Lo siento, no estás autorizado");
     } else {

       DynamicForm requestData = formFactory.form().bindFromRequest();

       List<Columna> columnas = columnaService.allColumnasTablero(idTablero);

       for(int i = 0; i < columnas.size(); i++) {
         String name = "columna" + columnas.get(i).getId();
         Integer nuevaPosicion = Integer.parseInt(requestData.get(name));
         columnaService.moverColumna(columnas.get(i).getId(), nuevaPosicion);
       }

       return redirect(controllers.routes.GestionTablerosController.detalleTablero(idUsuario, idTablero));
     }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result grabaNombreColumnaModificado(Long idUsuario, Long idTablero, Long idColumna) throws java.text.ParseException {
     String connectedUserStr = session("connected");
     Long connectedUser =  Long.valueOf(connectedUserStr);
     if (connectedUser != idUsuario) {
        return unauthorized("Lo siento, no estás autorizado");
     } else {
       DynamicForm requestData = formFactory.form().bindFromRequest();
       String nuevoNombre = requestData.get("nombre");

       Columna columna = columnaService.obtenerColumna(idColumna);
       columnaService.modificaColumna(columna.getId(), nuevoNombre);

       return redirect(controllers.routes.GestionTablerosController.detalleTablero(idUsuario, idTablero));
     }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result listaColumnasDesplazables(Long idUsuario, Long idTablero) throws java.text.ParseException {
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
         Usuario usuario = usuarioService.findUsuarioPorId(connectedUser);
         boolean editable = true;
         List<Etiqueta> etiquetas = new ArrayList<Etiqueta>();
         return ok(detalleTablero.render(tablero, participantes, columnas, formFactory.form(Columna.class), usuario, editable, etiquetas, formFactory.form(Etiqueta.class), ""));
       }
     }
   }

   @Security.Authenticated(ActionAuthenticator.class)
   public Result borraColumna(Long idUsuario, Long idTablero, Long idColumna) {
     String connectedUserStr = session("connected");
     Long connectedUser =  Long.valueOf(connectedUserStr);
     if (connectedUser != idUsuario) {
        return unauthorized("Lo siento, no estás autorizado");
     } else {
       Tablero tablero = tableroService.obtenerTablero(idTablero);
       if (tablero == null) {
          return notFound("Tablero no encontrado");
       } else {
          columnaService.borraColumna(idColumna);
          flash("aviso", "Columna borrada correctamente");
          return ok();
       }
     }
   }
 }
