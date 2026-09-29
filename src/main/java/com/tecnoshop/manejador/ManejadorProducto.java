package com.tecnoshop.manejador;

import java.util.List;

import com.tecnoshop.model.Producto;
import com.tecnoshop.util.HibernateUtil;

import jakarta.persistence.EntityManager;

public class ManejadorProducto{
    private static ManejadorProducto instancia = null;

    private ManejadorProducto() {
    }

    public static ManejadorProducto getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorProducto();
        }
        return instancia;
    }


    public void agregarProducto(Producto producto){
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(producto);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }


    public boolean existeProducto(String codigo) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Producto> productos = em.createQuery("SELECT p FROM Producto p WHERE p.codigo = :codigo", Producto.class)
                    .setParameter("codigo", codigo)
                    .getResultList();
            return !productos.isEmpty();
        } finally {
            em.close();
        }
    }


}
