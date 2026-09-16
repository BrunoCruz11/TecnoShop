# Requisitos funcionales — TecnoShop

## Gestión de usuarios
- RF01: El sistema debe permitir registrar un usuario con nombre, email, contraseña y estado (activo/inactivo).
- RF02: El sistema debe permitir consultar, modificar y dar de baja (lógica, vía `activo`) a un usuario.
- RF03: El sistema debe impedir el acceso de un usuario marcado como inactivo.

## Gestión de proveedores
- RF04: El sistema debe permitir registrar un proveedor con nombre, teléfono y email.
- RF05: El sistema debe permitir consultar, modificar y eliminar un proveedor.
- RF06: El sistema debe impedir eliminar un proveedor que tenga compras asociadas.

## Gestión de productos
- RF07: El sistema debe permitir registrar un producto con nombre, descripción, código, stock, stock mínimo, precio de compra y precio de venta.
- RF08: El sistema debe alertar (o listar) los productos cuyo stock esté por debajo del stock mínimo.
- RF09: El sistema debe permitir marcar un producto como disponible o no disponible sin eliminarlo.
- RF10: El precio de venta no puede ser menor al precio de compra.

## Gestión de compras
- RF11: El sistema debe permitir registrar una compra asociada a un usuario y a un proveedor, con fecha y estado.
- RF12: Una compra debe tener al menos un detalle de compra (no se permite una compra vacía).
- RF13: El sistema debe permitir consultar el historial de compras filtrado por usuario, por proveedor o por rango de fechas.
- RF14: El estado de una compra debe seguir un flujo válido (por ejemplo: pendiente → confirmada → cancelada), sin saltos arbitrarios.

## Gestión de detalle de compra
- RF15: El sistema debe permitir registrar, por cada producto de una compra, la cantidad y el precio unitario al momento de la compra.
- RF16: El sistema debe calcular el subtotal de cada línea (cantidad × precio unitario) y el total de la compra automáticamente.
- RF17: Al confirmarse una compra, el sistema debe incrementar el stock de cada producto involucrado según la cantidad comprada.
