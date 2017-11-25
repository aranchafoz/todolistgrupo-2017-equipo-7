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
 }
