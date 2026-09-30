package com.tecnoshop.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecnoshop.controlador.IProductoControlador;
import com.tecnoshop.controlador.IUsuarioControlador;
import com.tecnoshop.controlador.ProductoControlador;
import com.tecnoshop.controlador.UsuarioControlador;
import com.tecnoshop.excepciones.DatosInvalidosException;
import com.tecnoshop.excepciones.ExisteProductoException;
import com.tecnoshop.excepciones.ExisteUsuarioException;
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
            UC.registrarUsuario("Administrador", Config.ADMIN_EMAIL, Config.ADMIN_PASSWORD, true);
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

    private static int cargar(IProductoControlador PC, String nombre, String descripcion, String codigo, int stock, int stockMinimo, double precioCompra, double precioVenta) {
        try {
            PC.registrarProducto(nombre, descripcion, codigo, stock, stockMinimo, precioCompra, precioVenta);
            return 1;
        } catch (ExisteProductoException e) {
            return 0; // ya estaba cargado de una corrida anterior
        } catch (PrecioInvalidoException | DatosInvalidosException e) {
            throw new IllegalStateException("Dato de ejemplo invalido para " + codigo + ": " + e.getMessage());
        }
    }
}
