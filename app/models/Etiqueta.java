package models;

import javax.persistence.*;

import java.util.Set;
import java.util.HashSet;

@Entity
public class Etiqueta {
     @Id
     @GeneratedValue(strategy=GenerationType.AUTO)
     private Long id;
     private String nombre;
     @ManyToOne
     @JoinColumn(name="tableroId")
     public Tablero tablero;

     @ManyToMany(mappedBy="etiquetas", fetch=FetchType.EAGER)
     public Set<Tarea> tareas = new HashSet<Tarea>();

     public Etiqueta() {}

     public Etiqueta(Tablero tablero, String nombre) {
       this.tablero = tablero;
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

     public Tablero getTablero() {
       return tablero;
     }

     public void setTablero(Tablero tablero) {
       this.tablero = tablero;
     }

     public Set<Tarea> getTareas() {
        return tareas;
     }

     public void setTareas(Set<Tarea> tareas) {
        this.tareas = tareas;
     }

     public String toString() {
        return String.format("Columna id: %s nombre: %s ",
                        id, nombre);
     }

     @Override
     public int hashCode() {
        final int prime = 31;
        int result = prime + ((nombre == null) ? 0 : nombre.hashCode());
        return result;
     }

     @Override
     public boolean equals(Object obj) {
        if (this == obj) return true;
        if (getClass() != obj.getClass()) return false;
        Etiqueta other = (Etiqueta) obj;
        // Si tenemos los ID, comparamos por ID
        if (id != null && other.id != null)
        return ((long) id == (long) other.id);
        // sino comparamos por campos obligatorios
        else {
           if (nombre == null) {
              if (other.nombre != null) return false;
           } else if (!nombre.equals(other.nombre)) return false;
           if (tablero == null) {
              if (other.tablero != null) return false;
              else if (!tablero.equals(other.tablero)) return false;
           }
        }
        return true;
     }
}
