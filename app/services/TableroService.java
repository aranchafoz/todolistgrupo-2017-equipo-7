package services;

import javax.inject.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import models.Usuario;
import models.Tablero;

import models.UsuarioRepository;
import models.TableroRepository;

public class TableroService {
  UsuarioRepository usuarioRepository;
  TableroRepository tableroRepository;

  @Inject
  public TableroService(UsuarioRepository usuarioRepository, TableroRepository tableroRepository) {
    this.usuarioRepository = usuarioRepository;
    this.tableroRepository = tableroRepository;
  }

  public List<Tablero> allTablerosAdministradosUsuario(Long idUsuario) {
    Usuario usuario = usuarioRepository.findById(idUsuario);
    if (usuario == null) {
      throw new TableroServiceException("Usuario no existente");
    }
    List<Tablero> tableros = new ArrayList<Tablero>();
    tableros.addAll(usuario.getAdministrados());
    Collections.sort(tableros, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
    return tableros;
  }
}
