package com.tecnoshop.util;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecnoshop.controlador.CompraControlador;
import com.tecnoshop.controlador.ICompraControlador;
import com.tecnoshop.controlador.IProductoControlador;
import com.tecnoshop.controlador.IProveedorControlador;
import com.tecnoshop.controlador.ProveedorControlador;
import com.tecnoshop.dto.DetalleCompraDTO;
import com.tecnoshop.dto.ProductoDTO;
import com.tecnoshop.dto.ProveedorDTO;
import com.tecnoshop.dto.UsuarioDTO;
import com.tecnoshop.controlador.IUsuarioControlador;
import com.tecnoshop.controlador.ProductoControlador;
import com.tecnoshop.controlador.UsuarioControlador;
import com.tecnoshop.enums.EstadoCompra;
import com.tecnoshop.enums.Rol;
import com.tecnoshop.excepciones.DatosInvalidosException;
import com.tecnoshop.excepciones.ExisteProductoException;
import com.tecnoshop.excepciones.ExisteUsuarioException;
import com.tecnoshop.excepciones.NoExisteProductoException;
import com.tecnoshop.excepciones.PrecioInvalidoException;

/**
 * Datos iniciales. Usa los mismos controladores que la API, asi pasan por las mismas validaciones.
 * Se puede correr varias veces: lo que ya existe se saltea.
 */
public final class CargaDatos {

    private static final Logger LOG = LoggerFactory.getLogger(CargaDatos.class);

    private CargaDatos() {
    }

    // Como no hay registro publico, el primer usuario sale de ADMIN_EMAIL / ADMIN_PASSWORD.
    // Solo se crea si la tabla de usuarios esta vacia; despues los usuarios se crean desde la app.
    public static void crearAdministradorSiNoHayUsuarios() {
        IUsuarioControlador UC = UsuarioControlador.get();
        if (!UC.obtenerTodosLosUsuarios().isEmpty()) {
            return;
        }
        if (Config.ADMIN_EMAIL.isEmpty() || Config.ADMIN_PASSWORD.isEmpty()) {
            LOG.warn("No hay usuarios y no se definio ADMIN_EMAIL / ADMIN_PASSWORD: nadie va a poder iniciar sesion");
            return;
        }
        try {
            UC.registrarUsuario("Administrador", Config.ADMIN_EMAIL, Config.ADMIN_PASSWORD, true, Rol.ADMIN);
            LOG.info("Usuario inicial creado: {}", Config.ADMIN_EMAIL);
        } catch (ExisteUsuarioException e) {
            // otra instancia lo creo al mismo tiempo
        } catch (DatosInvalidosException e) {
            throw new IllegalStateException("ADMIN_EMAIL o ADMIN_PASSWORD invalidos: " + e.getMessage());
        }
    }

    public static void cargarProductos() {
        IProductoControlador PC = ProductoControlador.get();
        int cargados = 0;

        //                  nombre                              descripcion                                           codigo     stock min  compra   venta
        cargados += cargar(PC, "Mouse inalambrico Logitech M185",  "Mouse optico inalambrico con receptor USB",          "MOU-001",  25,   5,   350,    590);
        cargados += cargar(PC, "Teclado mecanico Redragon Kumara", "Teclado mecanico switches Outemu Blue, retroiluminado", "TEC-001",  12,   4,  1400,   2190);
        cargados += cargar(PC, "Monitor Samsung 24\" FHD",          "Monitor LED 24 pulgadas 1920x1080, 75Hz",            "MON-001",   6,   2,  5200,   7490);
        cargados += cargar(PC, "Auriculares HyperX Cloud Stinger", "Auriculares gamer con microfono, jack 3.5mm",        "AUR-001",   3,   5,  1100,   1790);
        cargados += cargar(PC, "Pendrive Kingston 64GB",           "Memoria USB 3.2 de 64GB",                            "USB-064",  40,  10,   180,    320);
        cargados += cargar(PC, "Disco SSD Kingston A400 480GB",    "Unidad de estado solido SATA 2.5\"",                 "SSD-480",   8,   3,  1300,   1990);
        cargados += cargar(PC, "Memoria RAM Kingston 8GB DDR4",    "Modulo DDR4 3200MHz para PC de escritorio",          "RAM-008",   2,   4,   900,   1450);
        cargados += cargar(PC, "Webcam Logitech C270",             "Camara web HD 720p con microfono",                   "CAM-001",  10,   3,   950,   1490);
        cargados += cargar(PC, "Router TP-Link Archer C6",         "Router WiFi doble banda AC1200",                     "RED-001",   5,   2,  1600,   2390);
        cargados += cargar(PC, "Cable HDMI 2m",                    "Cable HDMI 2.0 4K de 2 metros",                      "CAB-HD2",  60,  15,    90,    190);
        cargados += cargar(PC, "Parlantes Genius SP-HF180",        "Parlantes USB 2.0 de escritorio",                    "PAR-001",   1,   3,   450,    750);
        cargados += cargar(PC, "Mousepad XL",                      "Alfombrilla de 80x30cm con base antideslizante",     "MPD-XL",   18,   5,   250,    450);

        LOG.info("CargaDatos: {} productos nuevos cargados", cargados);
    }

    public static void cargarProveedores() {
        IProveedorControlador PRC = ProveedorControlador.get();
        // el proveedor no tiene un campo unico, asi que se saltea el que ya tenga el mismo email
        Set<String> emails = PRC.obtenerTodosLosProveedores().stream()
                .map(ProveedorDTO::getEmail)
                .collect(Collectors.toSet());
        int cargados = 0;

        //                              telefono   nombre                           email
        cargados += cargar(PRC, emails, 24001234, "Distribuidora TecnoSur",       "ventas@tecnosur.example.com");
        cargados += cargar(PRC, emails, 29005678, "Importadora Digital del Plata", "compras@digitalplata.example.com");
        cargados += cargar(PRC, emails, 26221190, "Mayorista InfoRed",            "contacto@infored.example.com");
        cargados += cargar(PRC, emails, 24873300, "Perifericos Uruguay",          "pedidos@perifericos.example.com");
        cargados += cargar(PRC, emails, 27104455, "Redes y Cables SRL",           "info@redesycables.example.com");

        LOG.info("CargaDatos: {} proveedores nuevos cargados", cargados);
    }

    // compras de ejemplo, para que cada producto quede conectado con los proveedores que lo venden.
    // Solo se cargan si la base no tiene ninguna compra (confirmar suma stock, no se puede repetir).
    public static void cargarCompras() {
        ICompraControlador CC = CompraControlador.get();
        if (!CC.obtenerTodasLasCompras().isEmpty()) {
            return;
        }
        UsuarioDTO admin = UsuarioControlador.get().obtenerTodosLosUsuarios().stream()
                .filter(u -> u.getRol() == Rol.ADMIN)
                .findFirst().orElse(null);
        if (admin == null) {
            return;
        }
        Map<String, Integer> productos = ProductoControlador.get().obtenerTodosLosProductos().stream()
                .collect(Collectors.toMap(ProductoDTO::getCodigo, ProductoDTO::getId));
        Map<String, Integer> proveedores = ProveedorControlador.get().obtenerTodosLosProveedores().stream()
                .collect(Collectors.toMap(ProveedorDTO::getEmail, ProveedorDTO::getId, (a, b) -> a));
        LocalDate hoy = LocalDate.now();
        int cargadas = 0;

        cargadas += cargar(CC, admin.getId(), proveedores.get("ventas@tecnosur.example.com"), hoy.minusDays(40), EstadoCompra.CONFIRMADA,
                detalle(productos, "MOU-001", 10, 350), detalle(productos, "TEC-001", 5, 1400));
        cargadas += cargar(CC, admin.getId(), proveedores.get("pedidos@perifericos.example.com"), hoy.minusDays(32), EstadoCompra.CONFIRMADA,
                detalle(productos, "CAM-001", 5, 950), detalle(productos, "MPD-XL", 10, 250), detalle(productos, "MOU-001", 5, 340));
        cargadas += cargar(CC, admin.getId(), proveedores.get("contacto@infored.example.com"), hoy.minusDays(25), EstadoCompra.CONFIRMADA,
                detalle(productos, "SSD-480", 4, 1300), detalle(productos, "USB-064", 20, 180), detalle(productos, "MON-001", 2, 5200));
        cargadas += cargar(CC, admin.getId(), proveedores.get("info@redesycables.example.com"), hoy.minusDays(18), EstadoCompra.CONFIRMADA,
                detalle(productos, "RED-001", 3, 1600), detalle(productos, "CAB-HD2", 20, 90));
        cargadas += cargar(CC, admin.getId(), proveedores.get("compras@digitalplata.example.com"), hoy.minusDays(10), EstadoCompra.CONFIRMADA,
                detalle(productos, "SSD-480", 3, 1280), detalle(productos, "MON-001", 1, 5100));
        // pendiente: los auriculares y la RAM siguen con stock bajo hasta que se confirme
        cargadas += cargar(CC, admin.getId(), proveedores.get("compras@digitalplata.example.com"), hoy.minusDays(2), EstadoCompra.PENDIENTE,
                detalle(productos, "AUR-001", 6, 1100), detalle(productos, "RAM-008", 6, 900));
        cargadas += cargar(CC, admin.getId(), proveedores.get("ventas@tecnosur.example.com"), hoy.minusDays(5), EstadoCompra.CANCELADA,
                detalle(productos, "PAR-001", 4, 450));

        LOG.info("CargaDatos: {} compras de ejemplo cargadas", cargadas);
    }

    private static DetalleCompraDTO detalle(Map<String, Integer> productos, String codigo, int cantidad, double precioUnitario) {
        return new DetalleCompraDTO(productos.getOrDefault(codigo, -1), cantidad, precioUnitario);
    }

    private static int cargar(ICompraControlador CC, int usuarioId, Integer proveedorId, LocalDate fecha, EstadoCompra estado, DetalleCompraDTO... detalles) {
        if (proveedorId == null) {
            return 0; // el proveedor de ejemplo no esta (lo borraron o cambiaron el email)
        }
        try {
            int id = CC.registrarCompra(usuarioId, proveedorId, fecha, List.of(detalles));
            if (estado == EstadoCompra.CONFIRMADA) {
                CC.confirmarCompra(id);
            } else if (estado == EstadoCompra.CANCELADA) {
                CC.cancelarCompra(id);
            }
            return 1;
        } catch (NoExisteProductoException e) {
            return 0; // algun producto de ejemplo no esta
        } catch (Exception e) {
            throw new IllegalStateException("Compra de ejemplo invalida: " + e.getMessage());
        }
    }

    private static int cargar(IProveedorControlador PRC, Set<String> emails, int telefono, String nombre, String email) {
        if (emails.contains(email)) {
            return 0; // ya estaba cargado de una corrida anterior
        }
        try {
            PRC.registrarProveedor(telefono, nombre, email);
            return 1;
        } catch (DatosInvalidosException e) {
            throw new IllegalStateException("Dato de ejemplo invalido para " + nombre + ": " + e.getMessage());
        }
    }

    private static int cargar(IProductoControlador PC, String nombre, String descripcion, String codigo, int stock, int stockMinimo, double precioCompra, double precioVenta) {
        try {
            // las ilustraciones de los productos de ejemplo vienen con el front, en img/productos/
            String imagen = "img/productos/" + codigo.toLowerCase() + ".svg";
            PC.registrarProducto(nombre, descripcion, codigo, stock, stockMinimo, precioCompra, precioVenta, imagen);
            return 1;
        } catch (ExisteProductoException e) {
            return 0; // ya estaba cargado de una corrida anterior
        } catch (PrecioInvalidoException | DatosInvalidosException e) {
            throw new IllegalStateException("Dato de ejemplo invalido para " + codigo + ": " + e.getMessage());
        }
    }
}
