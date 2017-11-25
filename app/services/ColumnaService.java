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
}
