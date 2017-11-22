
package models;

import com.google.inject.ImplementedBy;

@ImplementedBy(JPAColumnaRepository.class)
public interface ColumnaRepository {
   Columna add(Columna columna);
}
