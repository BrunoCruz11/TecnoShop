package com.tecnoshop.controlador;

import java.util.List;

import com.tecnoshop.dto.ProveedorDTO;
import com.tecnoshop.excepciones.DatosInvalidosException;
import com.tecnoshop.excepciones.NoExisteProveedorException;
import com.tecnoshop.excepciones.ProveedorConComprasException;
import com.tecnoshop.manejador.ManejadorProveedor;
import com.tecnoshop.model.Proveedor;
import com.tecnoshop.util.Validar;

public class ProveedorControlador implements IProveedorControlador {
    private static ProveedorControlador instancia= null;

    private ProveedorControlador(){}

    public static synchronized ProveedorControlador get(){
        if(instancia == null){
            instancia = new ProveedorControlador();
        }
        return instancia;
    }

    public void registrarProveedor(int telefono, String nombre, String email) throws DatosInvalidosException{
        Validar.positivo(telefono, "telefono");
        nombre = Validar.texto(nombre, "nombre", 255);
        email = Validar.email(email);
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

    public void cambiarNombreProveedor(int id, String nombre) throws NoExisteProveedorException, DatosInvalidosException{
        nombre = Validar.texto(nombre, "nombre", 255);
        buscar(id).cambiarNombreProveedor(id, nombre);
    }

    public void cambiarTelefonoProveedor(int id, int telefono) throws NoExisteProveedorException, DatosInvalidosException{
        Validar.positivo(telefono, "telefono");
        buscar(id).cambiarTelefonoProveedor(id, telefono);
    }

    public void cambiarEmailProveedor(int id, String email) throws NoExisteProveedorException, DatosInvalidosException{
        email = Validar.email(email);
        buscar(id).cambiarEmailProveedor(id, email);
    }

    public void eliminarProveedor(int id) throws NoExisteProveedorException, ProveedorConComprasException{
        ManejadorProveedor MP = buscar(id);
        if(MP.tieneCompras(id)){
            throw new ProveedorConComprasException("No se puede eliminar un proveedor con compras asociadas");
        }
        MP.eliminarProveedor(id);
    }

    // devuelve el manejador si el proveedor existe, asi cada metodo no repite el mismo if
    private ManejadorProveedor buscar(int id) throws NoExisteProveedorException{
        ManejadorProveedor MP = ManejadorProveedor.getInstancia();
        if(MP.obtenerProveedorPorId(id) == null){
            throw new NoExisteProveedorException("No existe un proveedor con el id dado");
        }
        return MP;
    }

}
