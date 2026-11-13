
package com.tuempresa.evaluacion1corte3.dao;

import com.tuempresa.evaluacion1corte3.database.JPAUtil;
import com.tuempresa.evaluacion1corte3.model.Equipo;
import com.tuempresa.evaluacion1corte3.model.TipoEquipo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class EquipoDAO {

    public void guardar(Equipo equipo) {

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            TipoEquipo tipo = em.getReference(
                    TipoEquipo.class,
                    equipo.getTipoEquipo().getIdTipo()
            );

            equipo.setTipoEquipo(tipo);

            em.persist(equipo);
            tx.commit();

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Equipo> listar() {
        return buscar("", null);
    }

    public void actualizar(Equipo equipo) {

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Equipo existente = em.find(
                    Equipo.class,
                    equipo.getIdEquipo()
            );

            if (existente == null) {
                throw new IllegalArgumentException(
                        "El equipo no existe."
                );
            }

            existente.setCodigo(equipo.getCodigo());
            existente.setNombre(equipo.getNombre());
            existente.setMarca(equipo.getMarca());
            existente.setEstado(equipo.getEstado());
            existente.setCantidad(equipo.getCantidad());

            TipoEquipo tipo = em.getReference(
                    TipoEquipo.class,
                    equipo.getTipoEquipo().getIdTipo()
            );

            existente.setTipoEquipo(tipo);

            em.merge(existente);
            tx.commit();

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void eliminar(Integer id) {

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Equipo equipo = em.find(Equipo.class, id);

            if (equipo == null) {
                throw new IllegalArgumentException(
                        "El equipo no existe."
                );
            }

            em.remove(equipo);
            tx.commit();

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean existeCodigo(
            String codigo, Integer idExcluir) {

        try (EntityManager em =
                     JPAUtil.getEntityManager()) {

            String jpql =
                    "SELECT COUNT(e) FROM Equipo e " +
                            "WHERE LOWER(e.codigo) = LOWER(:codigo)";

            if (idExcluir != null) {
                jpql += " AND e.idEquipo <> :id";
            }

            TypedQuery<Long> query =
                    em.createQuery(jpql, Long.class);

            query.setParameter("codigo", codigo);

            if (idExcluir != null) {
                query.setParameter("id", idExcluir);
            }

            return query.getSingleResult() > 0;
        }
    }

    public List<Equipo> buscar(
            String nombre, TipoEquipo tipo) {

        try (EntityManager em =
                     JPAUtil.getEntityManager()) {

            String jpql =
                    "SELECT e FROM Equipo e " +
                            "JOIN FETCH e.tipoEquipo t " +
                            "WHERE LOWER(e.nombre) LIKE :nombre";

            if (tipo != null) {
                jpql += " AND t.idTipo = :idTipo";
            }

            jpql += " ORDER BY e.idEquipo";

            TypedQuery<Equipo> query =
                    em.createQuery(jpql, Equipo.class);

            query.setParameter(
                    "nombre",
                    "%" + nombre.toLowerCase() + "%"
            );

            if (tipo != null) {
                query.setParameter(
                        "idTipo",
                        tipo.getIdTipo()
                );
            }

            return query.getResultList();
        }
    }
}
