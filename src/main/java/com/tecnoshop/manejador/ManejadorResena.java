package com.tecnoshop.manejador;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.tecnoshop.dto.ResenaDTO;
import com.tecnoshop.model.Producto;
import com.tecnoshop.model.Resena;
import com.tecnoshop.model.Usuario;
import com.tecnoshop.util.HibernateUtil;

import jakarta.persistence.EntityManager;

public class ManejadorResena{
    private static ManejadorResena instancia = null;

    private ManejadorResena() {
    }

    public static synchronized ManejadorResena getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorResena();
        }
        return instancia;
    }


    // cada usuario tiene una sola reseña por producto: si ya tenia una se actualiza, si no se crea
    public void guardarResena(int productoId, int usuarioId, int puntaje, String comentario){
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            List<Resena> existentes = em.createQuery("SELECT r FROM Resena r WHERE r.producto.id = :producto AND r.usuario.id = :usuario", Resena.class)
                    .setParameter("producto", productoId)
                    .setParameter("usuario", usuarioId)
                    .getResultList();
            if (existentes.isEmpty()) {
                Producto producto = em.find(Producto.class, productoId);
                Usuario usuario = em.find(Usuario.class, usuarioId);
                em.persist(new Resena(producto, usuario, puntaje, comentario, LocalDateTime.now()));
            } else {
                Resena resena = existentes.get(0);
                resena.setPuntaje(puntaje);
                resena.setComentario(comentario);
                resena.setFecha(LocalDateTime.now());
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


    // las mas nuevas primero
    public List<ResenaDTO> obtenerResenasDeProducto(int productoId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Resena> resenas = em.createQuery("SELECT r FROM Resena r WHERE r.producto.id = :id ORDER BY r.fecha DESC", Resena.class)
                    .setParameter("id", productoId)
                    .getResultList();

            List<ResenaDTO> resenasDTO = new ArrayList<>();
            for (Resena resena : resenas) {
                resenasDTO.add(aDTO(resena));
            }
            return resenasDTO;
        } finally {
            em.close();
        }
    }


    public ResenaDTO obtenerResenaPorId(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Resena resena = em.find(Resena.class, id);
            return resena == null ? null : aDTO(resena);
        } finally {
            em.close();
        }
    }


    public void eliminarResena(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Resena resena = em.find(Resena.class, id);
            if (resena != null) {
                em.remove(resena);
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


    private ResenaDTO aDTO(Resena resena) {
        return new ResenaDTO(resena.getId(), resena.getProducto().getId(), resena.getUsuario().getId(), resena.getUsuario().getNombre(),
                resena.getPuntaje(), resena.getComentario(), resena.getFecha());
    }
}
