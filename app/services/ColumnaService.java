package services;

import javax.inject.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import models.Columna;
import models.Tablero;

import models.ColumnaRepository;
import models.TableroRepository;

public class ColumnaService {
  ColumnaRepository columnaRepository;
  TableroRepository tableroRepository;

  @Inject
  public ColumnaService(ColumnaRepository columnaRepository, TableroRepository tableroRepository) {
    this.columnaRepository = columnaRepository;
    this.tableroRepository = tableroRepository;
  }

  public List<Columna> allColumnasTablero(Long idTablero) {
    Tablero tablero = tableroRepository.findById(idTablero);
    if (tablero == null) {
      throw new ColumnaServiceException("Tablero no existente");
    }
    List<Columna> columnas = new ArrayList<Columna>();
    columnas.addAll(tablero.getColumnas());
    Collections.sort(columnas, (a, b) -> a.getPosicion() < b.getPosicion() ? -1 : a.getPosicion() == b.getPosicion() ? 0 : 1);
    return columnas;
  }

  public Columna nuevaColumna(Long idTablero, String nombre, Integer posicion) {
    Tablero tablero = tableroRepository.findById(idTablero);
    if (tablero == null) {
      throw new ColumnaServiceException("Tablero no existente");
    }
    Columna columna = new Columna(tablero, nombre, posicion);
    return columnaRepository.add(columna);
  }

  public Columna modificaColumna(Long idColumna, String nuevoNombre) {
     Columna columna = columnaRepository.findById(idColumna);
     if (columna == null)
          throw new ColumnaServiceException("No existe columna");
     columna.setNombre(nuevoNombre);
     columna = columnaRepository.update(columna);
     return columna;
  }

  public Columna moverColumna(Long idColumna, Integer nuevaPosicion) {
     Columna columna = columnaRepository.findById(idColumna);
     if (columna == null)
          throw new ColumnaServiceException("No existe columna");
     columna.setPosicion(nuevaPosicion);
     columna = columnaRepository.update(columna);
     return columna;
  }

  public Columna obtenerColumna(Long idColumna) {
     return columnaRepository.findById(idColumna);
  }

  public void borraColumna(Long idColumna) {
    Columna columna = columnaRepository.findById(idColumna);
    if (columna == null)
      throw new ColumnaServiceException("No existe columna");
    if (columna.getTareas().size() > 0)
      throw new ColumnaServiceException("No se puede borrar una columna con tareas");
    columnaRepository.delete(idColumna);
  }
}
