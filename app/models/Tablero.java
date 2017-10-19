package models;

import javax.persistence.*;

@Entity
public class Tablero {

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
  private String nombre;

  @ManyToOne
  @JoinColumn(name="administradorId")
  public Usuario administrador;

  public Tablero() {}

  public Tablero(Usuario administrador, String nombre) {
    this.administrador = administrador;
    this.nombre = nombre;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Usuario getAdministrador() {
    return administrador;
  }

  public void setAdministrador(Usuario usuario) {
    this.administrador = administrador;
  }
}
