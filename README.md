# Biblioteca Escolar

Sistema de gestión de biblioteca escolar desarrollado en Java, con interfaz gráfica Swing y conexión a MySQL.

Proyecto de Evaluación Final Transversal de Desarrollo Orientado a Objetos II, Semana 9.

## Autor

Daniel González

## Funcionalidades

- Autenticación con roles de bibliotecario y estudiante.
- Gestión de libros, categorías y estudiantes.
- Creación de cuentas de acceso para estudiantes.
- Registro de préstamos con plazo de siete días.
- Devoluciones y actualización del stock.
- Detección de atrasos y registro de la fecha real de devolución.
- Reportes de libros más prestados, historial por estudiante y préstamos pendientes.

El bibliotecario administra el sistema. El estudiante puede consultar libros y gestionar sus propios préstamos.

## Tecnologías

- Java.
- Swing y JFrame.
- MySQL y JDBC.
- Maven.
- IntelliJ IDEA.

## Diseño del sistema

- **MVC:** separación de modelos, vistas y controladores.
- **DAO:** consultas y operaciones de base de datos.
- **Singleton:** instancia única de DatabaseConnection.
- **SwingWorker:** operaciones en segundo plano para evitar bloquear la interfaz.
- **Sincronización y transacciones:** control del stock y consistencia de los préstamos.

También se aplican herencia, polimorfismo, una clase abstracta e interfaces.

## Estructura

| Paquete | Responsabilidad |
| --- | --- |
| model | Entidades del sistema |
| dao | Consultas SQL y persistencia |
| interfaces | Contratos de operaciones |
| controller | Eventos y coordinación de acciones |
| vista | Ventanas, paneles y formularios |
| config | Conexión a MySQL |
| ui | Clase Main |

## Configuración

1. Crear la base de datos ejecutando el script de creación de tablas.
2. Ejecutar el script de poblado.
3. Ejecutar el script de actualización para agregar fecha_devolucion_real.
4. Configurar el usuario, contraseña y puerto de MySQL.
5. Abrir el proyecto en IntelliJ, sincronizar Maven y ejecutar ui.Main.
