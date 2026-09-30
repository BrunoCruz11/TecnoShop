-- Reglas que ya validan los controladores, repetidas en la base como ultima barrera:
-- aunque dos peticiones lleguen al mismo tiempo o un bug se saltee una validacion, la base no acepta datos invalidos.

-- ===== usuarios =====
ALTER TABLE usuarios
    ALTER COLUMN nombre SET NOT NULL,
    ALTER COLUMN email SET NOT NULL,
    ALTER COLUMN password SET NOT NULL;
-- el email no se puede repetir, sin importar mayusculas/minusculas
CREATE UNIQUE INDEX usuarios_email_unico ON usuarios (lower(email));

-- ===== proveedores =====
ALTER TABLE proveedores
    ALTER COLUMN nombre SET NOT NULL,
    ALTER COLUMN email SET NOT NULL;

-- ===== productos =====
ALTER TABLE productos
    ALTER COLUMN nombre SET NOT NULL,
    ALTER COLUMN codigo SET NOT NULL,
    ADD CONSTRAINT productos_codigo_unico UNIQUE (codigo),
    ADD CONSTRAINT productos_stock_check CHECK (stock >= 0),
    ADD CONSTRAINT productos_stockminimo_check CHECK (stockminimo >= 0),
    ADD CONSTRAINT productos_precios_check CHECK (preciocompra >= 0 AND precioventa >= preciocompra), -- RF10
    -- control de concurrencia optimista (@Version en Producto)
    ADD COLUMN version integer NOT NULL DEFAULT 0;

-- ===== compras =====
-- compras viejas sin fecha (de antes de RF11) quedan con la fecha de la migracion
UPDATE compras SET fecha = CURRENT_DATE WHERE fecha IS NULL;
ALTER TABLE compras
    ALTER COLUMN estado SET NOT NULL,
    ALTER COLUMN fecha SET NOT NULL,
    ALTER COLUMN usuario_id SET NOT NULL,
    ALTER COLUMN proveedor_id SET NOT NULL,
    ADD COLUMN version integer NOT NULL DEFAULT 0;
-- indices para los filtros del historial (RF13)
CREATE INDEX compras_usuario_idx ON compras (usuario_id);
CREATE INDEX compras_proveedor_idx ON compras (proveedor_id);
CREATE INDEX compras_fecha_idx ON compras (fecha);

-- ===== detalle_compras =====
ALTER TABLE detalle_compras
    ALTER COLUMN compra_id SET NOT NULL,
    ALTER COLUMN producto_id SET NOT NULL,
    ADD CONSTRAINT detalle_cantidad_check CHECK (cantidad > 0),
    ADD CONSTRAINT detalle_precio_check CHECK (preciounitario >= 0),
    -- un producto aparece una sola vez por compra
    ADD CONSTRAINT detalle_producto_unico UNIQUE (compra_id, producto_id);
CREATE INDEX detalle_producto_idx ON detalle_compras (producto_id);
