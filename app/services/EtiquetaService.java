package services;

import javax.inject.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import models.Tarea;
import models.Tablero;
import models.Etiqueta;

import models.TareaRepository;
import models.TableroRepository;
import models.EtiquetaRepository;

public class EtiquetaService {
  EtiquetaRepository etiquetaRepository;
  TableroRepository tableroRepository;
  TareaRepository tareaRepository;

  @Inject
  public EtiquetaService(EtiquetaRepository etiquetaRepository, TableroRepository tableroRepository, TareaRepository tareaRepository) {
    this.etiquetaRepository = etiquetaRepository;
    this.tableroRepository = tableroRepository;
    this.tareaRepository = tareaRepository;
  }

  public List<Etiqueta> allEtiquetasTablero(Long idTablero) {
    Tablero tablero = tableroRepository.findById(idTablero);
    if (tablero == null) {
      throw new EtiquetaServiceException("Tablero no existente");
    }
    List<Etiqueta> etiquetas = new ArrayList<Etiqueta>();
    etiquetas.addAll(tablero.getEtiquetas());
    Collections.sort(etiquetas, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
    return etiquetas;
  }

  public List<Etiqueta> allEtiquetasTarea(Long idTarea) {
    Tarea tarea = tareaRepository.findById(idTarea);
    if (tarea == null) {
      throw new EtiquetaServiceException("Tarea no existente");
    }
    List<Etiqueta> etiquetas = new ArrayList<Etiqueta>();
    etiquetas.addAll(tarea.getEtiquetas());
    return etiquetas;
  }

  public Etiqueta nuevaEtiquetaTablero(Long idTablero, String nombre) {
    Tablero tablero = tableroRepository.findById(idTablero);
    if (tablero == null) {
      throw new EtiquetaServiceException("Tablero no existente");
    }
    Etiqueta etiqueta = new Etiqueta(tablero, nombre);
    return etiquetaRepository.add(etiqueta);
  }

  public Etiqueta modificaEtiqueta(Long idEtiqueta, String nuevoNombre) {
     Etiqueta etiqueta = etiquetaRepository.findById(idEtiqueta);
     if (etiqueta == null)
          throw new EtiquetaServiceException("No existe etiqueta");
     etiqueta.setNombre(nuevoNombre);
     etiqueta = etiquetaRepository.update(etiqueta);
     return etiqueta;
  }

  public Etiqueta obtenerEtiqueta(Long idEtiqueta) {
     return etiquetaRepository.findById(idEtiqueta);
  }

  public void borraEtiqueta(Long idEtiqueta) {
    Etiqueta etiqueta = etiquetaRepository.findById(idEtiqueta);
    if (etiqueta == null)
      throw new EtiquetaServiceException("No existe etiqueta");
    if (etiqueta.getTareas().size() > 0)
      throw new EtiquetaServiceException("No se puede borrar una etiqueta con tareas");
    etiquetaRepository.delete(idEtiqueta);
  }
}
