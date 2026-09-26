package com.tecnoshop.manejador;

import java.util.ArrayList;
import java.util.List;

import com.tecnoshop.dto.ProveedorDTO;
import com.tecnoshop.model.Proveedor;
import com.tecnoshop.util.HibernateUtil;

import jakarta.persistence.EntityManager;

public class ManejadorProveedor{
    private static ManejadorProveedor instancia = null;

    private ManejadorProveedor() {
    }

    public static ManejadorProveedor getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorProveedor();
        }
        return instancia;
    }


    public void agregarProveedor(Proveedor proveedor){
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(proveedor);
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


    public List<ProveedorDTO> obtenerTodosLosProveedores() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Proveedor> proveedores = em.createQuery("SELECT p FROM Proveedor p", Proveedor.class).getResultList();

            List<ProveedorDTO> proveedoresDTO = new ArrayList<>();
            for (Proveedor proveedor : proveedores) {
                proveedoresDTO.add(new ProveedorDTO(proveedor.getId(), proveedor.getTelefono(), proveedor.getNombre(), proveedor.getEmail()));
            }
            return proveedoresDTO;
        } finally {
            em.close();
        }
    }


    public ProveedorDTO obtenerProveedorPorId(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Proveedor proveedor = em.find(Proveedor.class, id);
            if (proveedor == null) {
                return null;
            }
            return new ProveedorDTO(proveedor.getId(), proveedor.getTelefono(), proveedor.getNombre(), proveedor.getEmail());
        } finally {
            em.close();
        }
    }


    public void cambiarNombreProveedor(int id, String nombre) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Proveedor proveedor = em.find(Proveedor.class, id);
            if (proveedor != null) {
                proveedor.setNombre(nombre);
            }
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


    public void cambiarTelefonoProveedor(int id, int telefono) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Proveedor proveedor = em.find(Proveedor.class, id);
            if (proveedor != null) {
                proveedor.setTelefono(telefono);
            }
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


    public void cambiarEmailProveedor(int id, String email) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Proveedor proveedor = em.find(Proveedor.class, id);
            if (proveedor != null) {
                proveedor.setEmail(email);
            }
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


    public void eliminarProveedor(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Proveedor proveedor = em.find(Proveedor.class, id);
            if (proveedor != null) {
                em.remove(proveedor);
            }
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



    public boolean tieneCompras(int idProveedor) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Long cantidad = em.createQuery("SELECT COUNT(c) FROM Compra c WHERE c.proveedor.id = :id", Long.class)
            .setParameter("id", idProveedor)
            .getSingleResult();
            return cantidad > 0;
        } finally {
            em.close();
        }
    }


}
