package com.tecnoshop.controlador;

import java.util.List;

import com.tecnoshop.dto.ProveedorDTO;
import com.tecnoshop.excepciones.NoExisteProveedorException;
import com.tecnoshop.excepciones.ProveedorConComprasException;
import com.tecnoshop.manejador.ManejadorProveedor;
import com.tecnoshop.model.Proveedor;

public class ProveedorControlador implements IProveedorControlador {
    private static ProveedorControlador instancia= null;

    private ProveedorControlador(){}

    public static synchronized ProveedorControlador get(){
        if(instancia == null){
            instancia = new ProveedorControlador();
        }
        return instancia;
    }

    public void registrarProveedor(int telefono, String nombre, String email){
        ManejadorProveedor MP = ManejadorProveedor.getInstancia();
        Proveedor P = new Proveedor(telefono, nombre, email);
        MP.agregarProveedor(P);
    }

    public ProveedorDTO obtenerProveedorPorId(int id){
        ManejadorProveedor MP = ManejadorProveedor.getInstancia();
        return MP.obtenerProveedorPorId(id);
    }

    public List<ProveedorDTO> obtenerTodosLosProveedores(){
        ManejadorProveedor MP = ManejadorProveedor.getInstancia();
        return MP.obtenerTodosLosProveedores();
    }

    public void cambiarNombreProveedor(int id, String nombre){
        ManejadorProveedor MP = ManejadorProveedor.getInstancia();
        MP.cambiarNombreProveedor(id, nombre);
    }

    public void cambiarTelefonoProveedor(int id, int telefono){
        ManejadorProveedor MP = ManejadorProveedor.getInstancia();
        MP.cambiarTelefonoProveedor(id, telefono);
    }

    public void cambiarEmailProveedor(int id, String email){
        ManejadorProveedor MP = ManejadorProveedor.getInstancia();
        MP.cambiarEmailProveedor(id, email);
    }

    public void eliminarProveedor(int id) throws NoExisteProveedorException, ProveedorConComprasException{
        ManejadorProveedor MP = ManejadorProveedor.getInstancia();
        if(MP.obtenerProveedorPorId(id) == null){
            throw new NoExisteProveedorException("No existe un proveedor con el id dado");
        }
        if(MP.tieneCompras(id)){
            throw new ProveedorConComprasException("No se puede eliminar un proveedor con compras asociadas");
        }
        MP.eliminarProveedor(id);
    }

}
