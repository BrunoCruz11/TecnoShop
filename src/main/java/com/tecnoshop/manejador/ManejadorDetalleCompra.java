package com.tecnoshop.manejador;

import java.util.ArrayList;
import java.util.List;

import com.tecnoshop.dto.DetalleCompraDTO;
import com.tecnoshop.enums.EstadoCompra;
import com.tecnoshop.model.Compra;
import com.tecnoshop.model.Detalle_compra;
import com.tecnoshop.model.Producto;
import com.tecnoshop.util.HibernateUtil;

import jakarta.persistence.EntityManager;

public class ManejadorDetalleCompra{
    private static ManejadorDetalleCompra instancia = null;

    private ManejadorDetalleCompra() {
    }

    public static synchronized ManejadorDetalleCompra getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorDetalleCompra();
        }
        return instancia;
    }


    // recibe los ids por lo mismo que agregarCompra: el detalle necesita las entidades Compra y Producto.
    // Antes de guardar la linea se "toca" la compra con un UPDATE condicional (... WHERE estado = PENDIENTE):
    // si sigue pendiente, la fila queda bloqueada hasta el commit y nadie la puede confirmar en el medio;
    // si no, no se agrega nada, porque una linea nueva en una compra confirmada nunca sumaria stock (RF17).
    // Devuelve false si no se pudo agregar.
    public boolean agregarDetalleCompra(int compraId, int productoId, int cantidad, double precioUnitario, double subtotal){
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            int filas = em.createQuery("UPDATE Compra c SET c.version = c.version + 1 WHERE c.id = :id AND c.estado = :pendiente")
                    .setParameter("id", compraId)
                    .setParameter("pendiente", EstadoCompra.PENDIENTE)
                    .executeUpdate();
            if (filas == 0) {
                em.getTransaction().commit();
                return false;
            }
            Compra compra = em.find(Compra.class, compraId);
            Producto producto = em.find(Producto.class, productoId);
            em.persist(new Detalle_compra(compra, producto, cantidad, precioUnitario, subtotal));
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }


    public DetalleCompraDTO obtenerDetalleCompraPorId(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Detalle_compra detalle = em.find(Detalle_compra.class, id);
            if (detalle == null) {
                return null;
            }
            return new DetalleCompraDTO(detalle.getId(), detalle.getCompra().getId(), detalle.getProducto().getId(), detalle.getCantidad(), detalle.getPrecioUnitario(), detalle.getSubtotal());
        } finally {
            em.close();
        }
    }


    public List<DetalleCompraDTO> obtenerDetallesDeCompra(int compraId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Detalle_compra> detalles = em.createQuery("SELECT d FROM Detalle_compra d WHERE d.compra.id = :id", Detalle_compra.class)
                    .setParameter("id", compraId)
                    .getResultList();

            List<DetalleCompraDTO> detallesDTO = new ArrayList<>();
            for (Detalle_compra detalle : detalles) {
                detallesDTO.add(new DetalleCompraDTO(detalle.getId(), detalle.getCompra().getId(), detalle.getProducto().getId(), detalle.getCantidad(), detalle.getPrecioUnitario(), detalle.getSubtotal()));
            }
            return detallesDTO;
        } finally {
            em.close();
        }
    }


}
