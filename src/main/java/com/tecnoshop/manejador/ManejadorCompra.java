package com.tecnoshop.manejador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.tecnoshop.dto.CompraDTO;
import com.tecnoshop.dto.DetalleCompraDTO;
import com.tecnoshop.enums.EstadoCompra;
import com.tecnoshop.model.Compra;
import com.tecnoshop.model.Detalle_compra;
import com.tecnoshop.model.Producto;
import com.tecnoshop.model.Proveedor;
import com.tecnoshop.model.Usuario;
import com.tecnoshop.util.HibernateUtil;

import jakarta.persistence.EntityManager;

public class ManejadorCompra{
    private static ManejadorCompra instancia = null;

    private ManejadorCompra() {
    }

    public static ManejadorCompra getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorCompra();
        }
        return instancia;
    }


    // la compra y sus detalles se guardan en la misma transaccion, asi nunca queda una compra vacia (RF12)
    public int agregarCompra(LocalDate fecha, int usuarioId, int proveedorId, List<DetalleCompraDTO> detalles){
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, usuarioId);
            Proveedor proveedor = em.find(Proveedor.class, proveedorId);
            Compra compra = new Compra(EstadoCompra.PENDIENTE, fecha, usuario, proveedor);
            em.persist(compra);
            for (DetalleCompraDTO d : detalles) {
                Producto producto = em.find(Producto.class, d.getProductoId());
                em.persist(new Detalle_compra(compra, producto, d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal()));
            }
            em.getTransaction().commit();
            return compra.getId();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }


    public CompraDTO obtenerCompraPorId(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Compra compra = em.find(Compra.class, id);
            if (compra == null) {
                return null;
            }
            return aDTO(compra);
        } finally {
            em.close();
        }
    }


    public List<CompraDTO> obtenerTodasLasCompras() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Compra> compras = em.createQuery("SELECT c FROM Compra c", Compra.class).getResultList();
            return aDTOs(compras);
        } finally {
            em.close();
        }
    }


    public List<CompraDTO> obtenerComprasPorUsuario(int usuarioId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Compra> compras = em.createQuery("SELECT c FROM Compra c WHERE c.usuario.id = :id", Compra.class)
                    .setParameter("id", usuarioId)
                    .getResultList();
            return aDTOs(compras);
        } finally {
            em.close();
        }
    }


    public List<CompraDTO> obtenerComprasPorProveedor(int proveedorId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Compra> compras = em.createQuery("SELECT c FROM Compra c WHERE c.proveedor.id = :id", Compra.class)
                    .setParameter("id", proveedorId)
                    .getResultList();
            return aDTOs(compras);
        } finally {
            em.close();
        }
    }


    public List<CompraDTO> obtenerComprasPorFecha(LocalDate desde, LocalDate hasta) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Compra> compras = em.createQuery("SELECT c FROM Compra c WHERE c.fecha BETWEEN :desde AND :hasta", Compra.class)
                    .setParameter("desde", desde)
                    .setParameter("hasta", hasta)
                    .getResultList();
            return aDTOs(compras);
        } finally {
            em.close();
        }
    }


    // cambia el estado y suma al stock de cada producto en la misma transaccion (RF17)
    public void confirmarCompra(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Compra compra = em.find(Compra.class, id);
            if (compra != null) {
                compra.setEstado(EstadoCompra.CONFIRMADA);
                for (Detalle_compra detalle : compra.getDetalles()) {
                    Producto producto = detalle.getProducto();
                    producto.setStock(producto.getStock() + detalle.getCantidad());
                }
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


    // si la compra ya estaba confirmada, se descuenta el stock que se habia sumado al confirmarla
    public void cancelarCompra(int id, boolean devolverStock) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Compra compra = em.find(Compra.class, id);
            if (compra != null) {
                compra.setEstado(EstadoCompra.CANCELADA);
                if (devolverStock) {
                    for (Detalle_compra detalle : compra.getDetalles()) {
                        Producto producto = detalle.getProducto();
                        producto.setStock(producto.getStock() - detalle.getCantidad());
                    }
                }
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


    // se llama con el EntityManager abierto, porque getTotal recorre los detalles
    private CompraDTO aDTO(Compra compra) {
        return new CompraDTO(compra.getId(), compra.getEstado(), compra.getFecha(), compra.getUsuario().getId(), compra.getProveedor().getId(), compra.getTotal());
    }

    private List<CompraDTO> aDTOs(List<Compra> compras) {
        List<CompraDTO> comprasDTO = new ArrayList<>();
        for (Compra compra : compras) {
            comprasDTO.add(aDTO(compra));
        }
        return comprasDTO;
    }


}
