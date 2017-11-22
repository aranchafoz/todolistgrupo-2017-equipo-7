
package models;

import com.google.inject.ImplementedBy;

@ImplementedBy(JPAColumnaRepository.class)
public interface ColumnaRepository {
   public Columna add(Columna columna);
   public Columna findById(Long idColumna);
}
