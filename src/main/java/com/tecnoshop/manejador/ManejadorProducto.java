package com.tecnoshop.manejador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tecnoshop.dto.ProductoCatalogoDTO;
import com.tecnoshop.dto.ProductoDTO;
import com.tecnoshop.enums.EstadoCompra;
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
            return aDTO(producto, proveedoresPorProducto(em));
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

            Map<Integer, List<String>> proveedores = proveedoresPorProducto(em);
            List<ProductoDTO> productosDTO = new ArrayList<>();
            for (Producto producto : productos) {
                productosDTO.add(aDTO(producto, proveedores));
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

            Map<Integer, List<String>> proveedores = proveedoresPorProducto(em);
            List<ProductoDTO> productosDTO = new ArrayList<>();
            for (Producto producto : productos) {
                productosDTO.add(aDTO(producto, proveedores));
            }
            return productosDTO;
        } finally {
            em.close();
        }
    }

    // catalogo: solo los productos marcados como disponibles (RF09), ordenados por nombre, con el promedio de sus reseñas
    public List<ProductoCatalogoDTO> obtenerCatalogo() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Producto> productos = em.createQuery("SELECT p FROM Producto p WHERE p.disponible = true ORDER BY p.nombre", Producto.class).getResultList();

            // promedio y cantidad de reseñas de todos los productos en una sola consulta: [productoId, promedio, cantidad]
            Map<Integer, Object[]> estadisticas = new HashMap<>();
            List<Object[]> filas = em.createQuery("SELECT r.producto.id, AVG(r.puntaje), COUNT(r) FROM Resena r GROUP BY r.producto.id", Object[].class).getResultList();
            for (Object[] fila : filas) {
                estadisticas.put((Integer) fila[0], fila);
            }

            List<ProductoCatalogoDTO> catalogo = new ArrayList<>();
            for (Producto producto : productos) {
                catalogo.add(aCatalogoDTO(producto, estadisticas.get(producto.getId())));
            }
            return catalogo;
        } finally {
            em.close();
        }
    }

    // un producto del catalogo (null si no existe o no esta disponible)
    public ProductoCatalogoDTO obtenerProductoCatalogo(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Producto producto = em.find(Producto.class, id);
            if (producto == null || !producto.isDisponible()) {
                return null;
            }
            Object[] estadistica = em.createQuery("SELECT r.producto.id, AVG(r.puntaje), COUNT(r) FROM Resena r WHERE r.producto.id = :id GROUP BY r.producto.id", Object[].class)
                    .setParameter("id", id)
                    .getResultStream().findFirst().orElse(null);
            return aCatalogoDTO(producto, estadistica);
        } finally {
            em.close();
        }
    }

    private ProductoDTO aDTO(Producto producto, Map<Integer, List<String>> proveedores) {
        ProductoDTO dto = new ProductoDTO(producto.getId(), producto.getNombre(), producto.getDescripcion(), producto.isDisponible(), producto.getCodigo(), producto.getStockMinimo(), producto.getStock(), producto.getPrecioCompra(), producto.getPrecioVenta(), producto.getImagenUrl());
        dto.setProveedores(proveedores.getOrDefault(producto.getId(), new ArrayList<>()));
        return dto;
    }

    // de que proveedores se compro cada producto: se busca en los detalles de las compras confirmadas
    private Map<Integer, List<String>> proveedoresPorProducto(EntityManager em) {
        List<Object[]> filas = em.createQuery("SELECT DISTINCT d.producto.id, c.proveedor.nombre FROM Detalle_compra d JOIN d.compra c WHERE c.estado = :confirmada ORDER BY c.proveedor.nombre", Object[].class)
                .setParameter("confirmada", EstadoCompra.CONFIRMADA)
                .getResultList();
        Map<Integer, List<String>> proveedores = new HashMap<>();
        for (Object[] fila : filas) {
            proveedores.computeIfAbsent((Integer) fila[0], k -> new ArrayList<>()).add((String) fila[1]);
        }
        return proveedores;
    }

    private ProductoCatalogoDTO aCatalogoDTO(Producto producto, Object[] estadistica) {
        double promedio = estadistica == null ? 0 : ((Number) estadistica[1]).doubleValue();
        int cantidad = estadistica == null ? 0 : ((Number) estadistica[2]).intValue();
        return new ProductoCatalogoDTO(producto.getId(), producto.getNombre(), producto.getDescripcion(), producto.getCodigo(),
                producto.getPrecioVenta(), producto.getStock() > 0, producto.getImagenUrl(), promedio, cantidad);
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
    public void modificarProducto(int id, String nombre, String descripcion, String codigo, int stock, int stockMinimo, double precioCompra, double precioVenta, String imagenUrl) {
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
                producto.setImagenUrl(imagenUrl);
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
