
package com.tuempresa.evaluacion1corte3.dao;

import com.tuempresa.evaluacion1corte3.database.JPAUtil;
import com.tuempresa.evaluacion1corte3.model.TipoEquipo;

import jakarta.persistence.EntityManager;
import java.util.List;

public class TipoEquipoDAO {

    public List<TipoEquipo> listar() {

        try (EntityManager em =
                     JPAUtil.getEntityManager()) {

            return em.createQuery(
                    "SELECT t FROM TipoEquipo t ORDER BY t.nombre",
                    TipoEquipo.class
            ).getResultList();
        }
    }
}
