package com.tecnoshop.manejador;

import java.util.ArrayList;
import java.util.List;

import com.tecnoshop.dto.ProductoDTO;
import com.tecnoshop.model.Producto;
import com.tecnoshop.util.HibernateUtil;

import jakarta.persistence.EntityManager;

public class ManejadorProducto{
    private static ManejadorProducto instancia = null;

    private ManejadorProducto() {
    }

    public static synchronized ManejadorProducto getInstancia() {
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

    public ProductoDTO obtenerProductoPorId(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Producto producto = em.find(Producto.class, id);
            if (producto == null) {
                return null;
            }
            return new ProductoDTO(producto.getId(), producto.getNombre(), producto.getDescripcion(), producto.isDisponible(), producto.getCodigo(), producto.getStockMinimo(), producto.getStock(), producto.getPrecioCompra(), producto.getPrecioVenta());
        } finally {
            em.close();
        }
    }

    public boolean porAcabar(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Producto producto = em.find(Producto.class, id);
            if (producto == null) {
                return false;
            }
            int stockActual = producto.getStock();
            int stockMinimo = producto.getStockMinimo();
            return stockActual <= stockMinimo;
        } finally {
            em.close();
        }
    }

    public List<ProductoDTO> obtenerTodosLosProductos() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Producto> productos = em.createQuery("SELECT p FROM Producto p", Producto.class).getResultList();

            List<ProductoDTO> productosDTO = new ArrayList<>();
            for (Producto producto : productos) {
                productosDTO.add(new ProductoDTO(producto.getId(), producto.getNombre(), producto.getDescripcion(), producto.isDisponible(), producto.getCodigo(), producto.getStockMinimo(), producto.getStock(), producto.getPrecioCompra(), producto.getPrecioVenta()));
            }
            return productosDTO;
        } finally {
            em.close();
        }
    }

    // mismo criterio que porAcabar: stock <= stockMinimo
    public List<ProductoDTO> obtenerProductosPorAcabar() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Producto> productos = em.createQuery("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo", Producto.class).getResultList();

            List<ProductoDTO> productosDTO = new ArrayList<>();
            for (Producto producto : productos) {
                productosDTO.add(new ProductoDTO(producto.getId(), producto.getNombre(), producto.getDescripcion(), producto.isDisponible(), producto.getCodigo(), producto.getStockMinimo(), producto.getStock(), producto.getPrecioCompra(), producto.getPrecioVenta()));
            }
            return productosDTO;
        } finally {
            em.close();
        }
    }

    public Producto obtenerProductoPorCodigo(String codigo) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Producto> productos = em.createQuery("SELECT p FROM Producto p WHERE p.codigo = :codigo", Producto.class)
                    .setParameter("codigo", codigo)
                    .getResultList();
            return productos.isEmpty() ? null : productos.get(0);
        } finally {
            em.close();
        }
    }


    // cambia todos los datos en una sola transaccion
    public void modificarProducto(int id, String nombre, String descripcion, String codigo, int stock, int stockMinimo, double precioCompra, double precioVenta) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, id);
            if (producto != null) {
                producto.setNombre(nombre);
                producto.setDescripcion(descripcion);
                producto.setCodigo(codigo);
                producto.setStock(stock);
                producto.setStockMinimo(stockMinimo);
                producto.setPrecioCompra(precioCompra);
                producto.setPrecioVenta(precioVenta);
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


    public void cambiarEstadoProducto(int id, boolean disponible) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, id);
            if (producto != null) {
                producto.setDisponible(disponible);
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


    public void cambiarNombreProducto(int id, String nombre) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, id);
            if (producto != null) {
                producto.setNombre(nombre);
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


    public void cambiarDescripcionProducto(int id, String descripcion) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, id);
            if (producto != null) {
                producto.setDescripcion(descripcion);
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


    public void cambiarCodigoProducto(int id, String codigo) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, id);
            if (producto != null) {
                producto.setCodigo(codigo);
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


    public void cambiarStockProducto(int id, int stock) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, id);
            if (producto != null) {
                producto.setStock(stock);
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


    public void cambiarStockMinimoProducto(int id, int stockMinimo) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, id);
            if (producto != null) {
                producto.setStockMinimo(stockMinimo);
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


    public void cambiarPrecioCompraProducto(int id, double precioCompra) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, id);
            if (producto != null) {
                producto.setPrecioCompra(precioCompra);
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


    public void cambiarPrecioVentaProducto(int id, double precioVenta) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, id);
            if (producto != null) {
                producto.setPrecioVenta(precioVenta);
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

}
