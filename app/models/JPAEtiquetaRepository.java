package models;

import javax.inject.Inject;
import play.db.jpa.JPAApi;
import javax.persistence.EntityManager;

public class JPAEtiquetaRepository implements EtiquetaRepository {
   JPAApi jpaApi;

   @Inject
   public JPAEtiquetaRepository(JPAApi api) {
      this.jpaApi = api;
   }

   public Etiqueta add(Etiqueta etiqueta) {
      return jpaApi.withTransaction(entityManager -> {
         entityManager.persist(etiqueta);
         entityManager.flush();
         entityManager.refresh(etiqueta);
         return etiqueta;
      });
   }

   public Etiqueta findById(Long idEtiqueta) {
      return jpaApi.withTransaction(entityManager -> {
        return entityManager.find(Etiqueta.class, idEtiqueta);
      });
   }

   public Etiqueta update(Etiqueta etiqueta) {
      return jpaApi.withTransaction(entityManager -> {
         Etiqueta actualizado = entityManager.merge(etiqueta);
         return actualizado;
      });
   }

  public void delete(Long idEtiqueta) {
    jpaApi.withTransaction(() -> {
      EntityManager entityManager = jpaApi.em();
      Etiqueta etiquetaBD = entityManager.getReference(Etiqueta.class, idEtiqueta);
      entityManager.remove(etiquetaBD);
    });
  }
}
