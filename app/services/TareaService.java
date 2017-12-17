package services;

import javax.inject.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.util.Date;
import java.text.DateFormat;
import play.data.format.*;
import java.util.Set;
import java.util.HashSet;

import models.Usuario;
import models.UsuarioRepository;
import models.Tarea;
import models.TareaRepository;
import models.Columna;
import models.ColumnaRepository;
import java.util.Date;
import models.Etiqueta;
import models.EtiquetaRepository;


public class TareaService {
   UsuarioRepository usuarioRepository;
   TareaRepository tareaRepository;
   ColumnaRepository columnaRepository;
   EtiquetaRepository etiquetaRepository;

   @Inject
   public TareaService(UsuarioRepository usuarioRepository, TareaRepository tareaRepository, ColumnaRepository columnaRepository, EtiquetaRepository etiquetaRepository) {
      this.usuarioRepository = usuarioRepository;
      this.tareaRepository = tareaRepository;
      this.columnaRepository = columnaRepository;
      this.etiquetaRepository = etiquetaRepository;
   }

   // Devuelve la lista de tareas de un usuario, ordenadas por su id
   // (equivalente al orden de creación)
   public List<Tarea> allTareasUsuario(Long idUsuario) {
     Usuario usuario = usuarioRepository.findById(idUsuario);
     if (usuario == null) {
         throw new TareaServiceException("Usuario no existente");
      }
      List<Tarea> tareas = new ArrayList<Tarea>();
      tareas.addAll(usuario.getTareas());
      Collections.sort(tareas, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
      return tareas;
   }

   public List<Tarea> allTareasTerminadasUsuario(Long idUsuario) {
     Usuario usuario = usuarioRepository.findById(idUsuario);
     if (usuario == null) {
         throw new TareaServiceException("Usuario no existente");
      }

      List <Tarea> tareas = new ArrayList<Tarea>();
      tareas.addAll(usuario.getTareas());
      List <Tarea> definitivas = new ArrayList<Tarea>();

      for(Tarea t: tareas) {
        if (t.getTerminada()) {
          definitivas.add(t);
        }
      }
      Collections.sort(definitivas, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
      return definitivas;
   }

   public List<Tarea> allTareasPapeleraUsuario(Long idUsuario) {
     Usuario usuario = usuarioRepository.findById(idUsuario);
     if (usuario == null) {
         throw new TareaServiceException("Usuario no existente");
      }

      List <Tarea> tareas = new ArrayList<Tarea>();
      tareas.addAll(usuario.getTareas());
      List <Tarea> definitivas = new ArrayList<Tarea>();

      for(Tarea t: tareas) {
        if (t.getDeletedAt() != null) {
          definitivas.add(t);
        }
      }
      Collections.sort(definitivas, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
      return definitivas;
   }

   public List<Tarea> allTareasAsignadasUsuario(Long idUsuario) {
     Usuario usuario = usuarioRepository.findById(idUsuario);
     if (usuario == null) {
         throw new TareaServiceException("Usuario no existente");
      }
      List <Tarea> tareas = new ArrayList<Tarea>();
      tareas.addAll(usuario.getTareasAsignadas());
      Collections.sort(tareas, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);

      return tareas;
   }

   public Tarea nuevaTarea(Long idUsuario, String titulo, String descripcion, Date fechaLimite, Long idColumna) {
      Usuario usuario = usuarioRepository.findById(idUsuario);
      if (usuario == null) {
         throw new TareaServiceException("Usuario no existente");
      }
      Columna columna = columnaRepository.findById(idColumna);
      if (columna == null) {
         throw new TareaServiceException("Columna no existente");
      }
      Tarea tarea = new Tarea(usuario, titulo, columna);
      tarea.setDescripcion(descripcion);
      tarea.setFechaLimite(fechaLimite);
      return tareaRepository.add(tarea);
   }

   public Tarea obtenerTarea(Long idTarea) {
      return tareaRepository.findById(idTarea);
   }

   public Tarea modificaTarea(Long idTarea, String nuevoTitulo, String descripcion, Date fechaLimite, Long idColumna) {
      Tarea tarea = tareaRepository.findById(idTarea);
      if (tarea == null)
           throw new TareaServiceException("No existe tarea");
      Columna columna = columnaRepository.findById(idColumna);
      if (columna == null) {
         throw new TareaServiceException("No existe columna");
      }
      tarea.setTitulo(nuevoTitulo);
      tarea.setFechaLimite(fechaLimite);
      tarea.setDescripcion(descripcion);
      tarea.setColumna(columna);
      tarea = tareaRepository.update(tarea);
      return tarea;
   }

   public void borraTarea(Long idTarea) {
     Tarea tarea = tareaRepository.findById(idTarea);
     if (tarea == null)
          throw new TareaServiceException("No existe tarea");
     tareaRepository.delete(idTarea);
  }

  public Tarea marcarTerminada(Long idTarea) {
    Tarea tarea = tareaRepository.findById(idTarea);
    if (tarea == null) throw new TareaServiceException("No existe tarea");
    tarea.setTerminada(true);
    tarea = tareaRepository.update(tarea);
    return tarea;
  }

  public Tarea enviarPapelera(Long idTarea) {
    Tarea tarea = tareaRepository.findById(idTarea);
    if (tarea == null) throw new TareaServiceException("No existe tarea");
    tarea.setDeletedAt(new Date());
    tarea = tareaRepository.update(tarea);
    return tarea;
  }

  public Tarea quitarDePapelera(Long idTarea) {
    Tarea tarea = tareaRepository.findById(idTarea);
    if (tarea == null) throw new TareaServiceException("No existe tarea");
    if (tarea.getDeletedAt() != null) {
        tarea.setDeletedAt(null);
        tarea = tareaRepository.update(tarea);
    }

    return tarea;
  }

  public Tarea asignarTareaUsuario(Long idTarea, Long idUsuario) {
    Tarea tarea = tareaRepository.findById(idTarea);
    Usuario usuario = usuarioRepository.findById(idUsuario);
    if (tarea == null || usuario == null) {
      throw new TareaServiceException("Tarea o Usuario no existentes");
    }
    List<Usuario> usuariosAsignados = new ArrayList<Usuario>();
    usuariosAsignados.addAll(tarea.getUsuariosAsignados());
    usuariosAsignados.add(usuario);

    tarea.setUsuariosAsignados(new HashSet<Usuario>(usuariosAsignados));
    tarea = tareaRepository.update(tarea);

    return tarea;
  }

  public Tarea asignaEtiquetaTarea(Long idTarea, Long idEtiqueta)
  {
    Tarea tarea = tareaRepository.findById(idTarea);
    if (tarea == null) {
      throw new TareaServiceException("Tarea no existente");
    }

    Etiqueta etiqueta = etiquetaRepository.findById(idTarea);
    if (etiqueta == null) {
      throw new TareaServiceException("Etiqueta no existente");
    }
    List<Etiqueta> etiquetas = new ArrayList<Etiqueta>();
    etiquetas.addAll(tarea.getEtiquetas());
    etiquetas.add(etiqueta);

    tarea.setEtiquetas(new HashSet<Etiqueta> (etiquetas));
    tarea = tareaRepository.update(tarea);

    return tarea;
  }
}
