package com.tecnoshop.dto;

// Lo que ve un usuario normal en el catalogo: sin precio de compra ni stock exacto (son datos internos del negocio)
public class ProductoCatalogoDTO {
    private int id;
    private String nombre;
    private String descripcion;
    private String codigo;
    private double precioVenta;
    private boolean enStock;
    private String imagenUrl;
    private double promedioPuntaje;   // 0 si no tiene reseñas
    private int cantidadResenas;

    public ProductoCatalogoDTO(int id, String nombre, String descripcion, String codigo, double precioVenta, boolean enStock,
            String imagenUrl, double promedioPuntaje, int cantidadResenas) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.codigo = codigo;
        this.precioVenta = precioVenta;
        this.enStock = enStock;
        this.imagenUrl = imagenUrl;
        this.promedioPuntaje = promedioPuntaje;
        this.cantidadResenas = cantidadResenas;
    }

    public int getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public double getPrecioVenta() {
        return this.precioVenta;
    }

    public boolean isEnStock() {
        return this.enStock;
    }

    public String getImagenUrl() {
        return this.imagenUrl;
    }

    public double getPromedioPuntaje() {
        return this.promedioPuntaje;
    }

    public int getCantidadResenas() {
        return this.cantidadResenas;
    }
}
