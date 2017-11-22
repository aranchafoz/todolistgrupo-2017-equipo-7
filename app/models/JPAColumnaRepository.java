package models;

import javax.inject.Inject;
import play.db.jpa.JPAApi;

public class JPAColumnaRepository implements ColumnaRepository {
   JPAApi jpaApi;

   @Inject
   public JPAColumnaRepository(JPAApi api) {
      this.jpaApi = api;
   }

   public Columna add(Columna columna) {
      return jpaApi.withTransaction(entityManager -> {
         entityManager.persist(columna);
         entityManager.flush();
         entityManager.refresh(columna);
         return columna;
      });
   }

   public Columna findById(Long idColumna) {
      return jpaApi.withTransaction(entityManager -> {
        return entityManager.find(Columna.class, idColumna);
      });
   }
}
