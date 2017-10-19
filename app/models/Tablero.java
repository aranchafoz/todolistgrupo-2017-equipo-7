package models;

import javax.persistence.*;

@Entity
public class Tablero {

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
  private String nombre;
  // Relación muchos-a-uno entre tareas y usuario
  @ManyToOne
  // Nombre de la columna en la BD que guarda físicamente
  // el ID del usuario con el que está asociado un tablero
  @JoinColumn(name="usuarioId")
  public Usuario administrador;

  public Tablero() {}

  public Tablero(Usuario administrador, String nombre) {
    this.administrador = administrador;
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public Usuario getAdministrador() {
    return administrador;
  }
}
