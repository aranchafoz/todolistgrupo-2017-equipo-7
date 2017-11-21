package controllers;

import play.mvc.*;

import views.html.*;
import javax.inject.*;

 import services.UsuarioService;
 import models.Usuario;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    @Inject UsuarioService usuarioService;

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
      String connectedUserStr = session("connected");
      if(connectedUserStr != null) {
        Long connectedUser =  Long.valueOf(connectedUserStr);
        Usuario usuario = usuarioService.findUsuarioPorId(connectedUser);
        return ok(index.render(usuario, "Your new application is ready."));
      }
      return ok(index.render(null, "Your new application is ready."));
    }

    public Result acercaDe() {
      return ok(acercaDe.render());
    }

}
