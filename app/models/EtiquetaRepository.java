
package models;

import com.google.inject.ImplementedBy;

@ImplementedBy(JPAEtiquetaRepository.class)
public interface EtiquetaRepository {
   public Etiqueta add(Etiqueta etiqueta);
   public Etiqueta findById(Long idEtiqueta);
   public Etiqueta update(Etiqueta etiqueta);
   public void delete(Long idEtiqueta);
}
