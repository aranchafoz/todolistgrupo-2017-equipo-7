package models;

import com.google.inject.ImplementedBy;

import java.util.List;

@ImplementedBy(JPATableroRepository.class)
public interface TableroRepository {
  public Tablero add(Tablero tablero);
}
