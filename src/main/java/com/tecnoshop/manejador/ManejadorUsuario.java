package com.tecnoshop.manejador;

import java.util.ArrayList;
import java.util.List;

import com.tecnoshop.dto.UsuarioDTO;
import com.tecnoshop.model.Usuario;
import com.tecnoshop.util.HibernateUtil;

import jakarta.persistence.EntityManager;

public class ManejadorUsuario{
    private static ManejadorUsuario instancia = null;

    private ManejadorUsuario() {
    }

    public static ManejadorUsuario getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorUsuario();
        }
        return instancia;
    }


    public void agregarUsuario(Usuario user){
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
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

    
    public UsuarioDTO obtenerUsuarioPorId(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Usuario usuario = em.find(Usuario.class, id);
            if (usuario == null) {
                return null;
            }
            return new UsuarioDTO(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.isActivo());
        } finally {
            em.close();
        }
    }

    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Usuario> usuarios = em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();

            List<UsuarioDTO> usuariosDTO = new ArrayList<>();
            for (Usuario usuario : usuarios) {
                usuariosDTO.add(new UsuarioDTO(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.isActivo()));
            }
            return usuariosDTO;
        } finally {
            em.close();
        }
    }



    public void eliminarUsuario(int id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, id);
            if (usuario != null) {
                em.remove(usuario);
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



    public void actualizarUsuario(Usuario user) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(user);
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



    public Usuario obtenerUsuarioPorEmail(String email){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            List<Usuario> usuarios = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                    .setParameter("email", email)
                    .getResultList();
            return usuarios.isEmpty() ? null : usuarios.get(0);
        }finally{
            em.close();
        }
    }

    
    public boolean existeUsuario (String email){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            List<Usuario> usuarioABuscar = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                    .setParameter("email", email)
                    .getResultList();
            return !usuarioABuscar.isEmpty();
        }finally{
            em.close();
        }
    }


    public void cambiarEstadoUsuario(int id, boolean activo){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, id);
            if(usuario != null){
                usuario.setActivo(activo);
            }
            em.getTransaction().commit();

        }catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }


    public void cambiarNombreUsuario(int id, String nombre){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, id);
            if(usuario != null){
                 usuario.setNombre(nombre);
            }
            em.getTransaction().commit();
        }catch (RuntimeException e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
      
    }



        public void cambiarEmailUsuario(int id, String email){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, id);
            if(usuario != null){
                usuario.setEmail(email);
            }
            em.getTransaction().commit();

        }catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }


    public void cambiarPasswordUsuario(int id, String password){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, id);
            if(usuario != null){
                usuario.setPassword(password);
            }
            em.getTransaction().commit();

        }catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }


}
