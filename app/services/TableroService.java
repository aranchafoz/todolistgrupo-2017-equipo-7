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

  public Tablero nuevoTablero(Long idUsuario, String nombre) {
    Usuario usuario = usuarioRepository.findById(idUsuario);
    if (usuario == null) {
      throw new TableroServiceException("Usuario no existente");
    }
    Tablero tablero = new Tablero(usuario, nombre);
    return tableroRepository.add(tablero);
  }

  public Tablero obtenerTablero(Long idTablero) {
     return tableroRepository.findById(idTablero);
  }

  public List<Tablero> allTablerosParticipadosUsuario(Long idUsuario) {
    Usuario usuario = usuarioRepository.findById(idUsuario);
    if (usuario == null) {
      throw new TableroServiceException("Usuario no existente");
    }
    List<Tablero> tableros = new ArrayList<Tablero>();
    tableros.addAll(usuario.getTableros());
    Collections.sort(tableros, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
    return tableros;
  }

  public Tablero apuntaParticipante(Long idUsuario, Long idTablero) {
    Usuario usuario = usuarioRepository.findById(idUsuario);
    if (usuario == null) {
      throw new TableroServiceException("Usuario no existente");
    }
    Tablero tablero = tableroRepository.findById(idTablero);
    if (tablero == null) {
      throw new TableroServiceException("Tablero no existente");
    }
    tablero.getParticipantes().add(usuario);
    tablero = tableroRepository.update(tablero);
    return tablero;
  }

  public List<Tablero> obtenerRestoTableros(Long idUsuario) {
    Usuario usuario = usuarioRepository.findById(idUsuario);
    if (usuario == null) {
      throw new TableroServiceException("Usuario no existente");
    }

    List<Tablero> tablerosExcluidos = new ArrayList<Tablero>();
    tablerosExcluidos.addAll(usuario.getTableros());
    tablerosExcluidos.addAll(usuario.getAdministrados());

    List<Tablero> tableros = tableroRepository.getAllTableros();

    tableros.removeAll(tablerosExcluidos);
    Collections.sort(tableros, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
    return tableros;
  }

  public Tablero cerrarTablero(Long idTablero) {
    Tablero tablero = tableroRepository.findById(idTablero);
    if (tablero == null) {
      throw new TableroServiceException("Tablero no existente");
    }
    tablero.setCerrado(true);
    tablero = tableroRepository.update(tablero);
    return tablero;
  }

  public Tablero editarTablero(Long idTablero, String newTitulo) {
    Tablero tablero = tableroRepository.findById(idTablero);
    if (tablero == null) {
      throw new TableroServiceException("Tablero no existente");
    }
    tablero.setNombre(newTitulo);
    tablero = tableroRepository.update(tablero);
    return tablero;
  }

  public List<Usuario> getUsuariosParticipantes(Long idTablero) {
    Tablero tablero = tableroRepository.findById(idTablero);
    if (tablero == null) {
      throw new TableroServiceException("Tablero no existente");
    }
    List<Usuario> usuariosTablero = new ArrayList<Usuario>();
    usuariosTablero.addAll(tablero.getParticipantes());
    Collections.sort(usuariosTablero, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);

    return usuariosTablero;
  }
}
