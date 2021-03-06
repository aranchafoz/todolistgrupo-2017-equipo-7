package models;

import javax.inject.Inject;
import play.db.jpa.JPAApi;

import java.util.List;

import javax.persistence.EntityManager;

public class JPATareaRepository implements TareaRepository {
   JPAApi jpaApi;

   @Inject
   public JPATareaRepository(JPAApi api) {
      this.jpaApi = api;
   }

   public Tarea add(Tarea tarea) {
      return jpaApi.withTransaction(entityManager -> {
         entityManager.persist(tarea);
         entityManager.flush();
         entityManager.refresh(tarea);
         return tarea;
      });
   }

   public Tarea update(Tarea tarea) {
      return jpaApi.withTransaction(entityManager -> {
         Tarea tareaBD = entityManager.find(Tarea.class, tarea.getId());
         tareaBD.setTitulo(tarea.getTitulo());
         tareaBD.setFechaLimite(tarea.getFechaLimite());
         tareaBD.setTerminada(tarea.getTerminada());
         tareaBD.setDeletedAt(tarea.getDeletedAt());
         tareaBD.setColumna(tarea.getColumna());
         tareaBD.setEtiquetas(tarea.getEtiquetas());
         tareaBD.setUsuariosAsignados(tarea.getUsuariosAsignados());
         return tareaBD;
      });
   }

   public void delete(Long idTarea) {
      jpaApi.withTransaction(() -> {
         EntityManager entityManager = jpaApi.em();
         Tarea tareaBD = entityManager.getReference(Tarea.class, idTarea);
         entityManager.remove(tareaBD);
      });
   }

   public Tarea findById(Long idTarea) {
      return jpaApi.withTransaction(entityManager -> {
         return entityManager.find(Tarea.class, idTarea);
      });
   }
}
