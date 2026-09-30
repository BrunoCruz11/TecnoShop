-- Roles: ADMIN gestiona todo, USUARIO solo ve el catalogo.
-- Los usuarios que ya existian quedan como USUARIO, salvo el mas antiguo, que queda como ADMIN
-- (asi siempre hay alguien que pueda administrar).
ALTER TABLE usuarios
    ADD COLUMN rol varchar(20) NOT NULL DEFAULT 'USUARIO',
    ADD CONSTRAINT usuarios_rol_check CHECK (rol IN ('ADMIN', 'USUARIO'));

UPDATE usuarios SET rol = 'ADMIN' WHERE id = (SELECT min(id) FROM usuarios);
