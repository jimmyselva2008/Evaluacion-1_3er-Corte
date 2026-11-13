
package com.tuempresa.evaluacion1corte3.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {

    private static EntityManagerFactory emf;

    private JPAUtil() {
    }

    public static synchronized EntityManagerFactory
    getEntityManagerFactory() {

        if (emf == null || !emf.isOpen()) {

            String password = System.getenv("DB_PASSWORD");

            if (password == null || password.isBlank()) {
                throw new IllegalStateException(
                        "Configura la variable de entorno DB_PASSWORD"
                );
            }

            Map<String, Object> propiedades = new HashMap<>();

            propiedades.put(
                    "jakarta.persistence.jdbc.password",
                    password
            );

            emf = Persistence.createEntityManagerFactory(
                    "laboratorioPU",
                    propiedades
            );
        }

        return emf;
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory()
                .createEntityManager();
    }

    public static synchronized void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
