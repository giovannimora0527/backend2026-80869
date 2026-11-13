# Guia de estudio 4 - Reportes y despliegue con Docker (Fase 4 y entregables)

Esta guia cubre dos temas: el modulo de reportes administrativos y contables
(Fase 4) y el despliegue del sistema completo con Docker y Docker Compose
(entregable obligatorio). Esta escrita en lenguaje claro para repasar y para
generar un video de estudio.

---

# Parte A. Modulo de reportes (Fase 4)

## 1. Vision general

El modulo de reportes consolida la informacion de la clinica en tres grupos:
financieros, de inventario y de gestion medica. Todos los reportes permiten
filtrar por rango de fechas, mostrar una vista previa en pantalla y exportarse a
PDF y a Excel.

Es un modulo de solo lectura: no crea ni modifica datos. Construye cada reporte
a partir de la informacion ya registrada por los otros modulos (facturacion,
inventario y citas), de modo que los reportes son coherentes con la operacion
real del sistema.

---

## 2. Como esta hecho (idea tecnica)

Todos los reportes usan una misma estructura de respuesta: titulo, subtitulo,
columnas, filas y totales. Esa misma estructura sirve para la vista previa
(datos en pantalla) y para generar el PDF y el Excel. Gracias a eso, la
presentacion y la exportacion son genericas: agregar un reporte nuevo no obliga
a reescribir la logica de salida.

---

## 3. Catalogo de reportes

### Financieros
- Ingresos por periodo.
- Ingresos por consultas medicas (servicios).
- Ingresos por venta de medicamentos.
- Ingresos por especialidad.
- Ingresos por veterinario.
- Facturas por estado (pagadas, pendientes, pago parcial, anuladas).
- Pagos parciales (con su saldo pendiente).
- Impuestos recaudados y descuentos aplicados.
- Resumen de ingresos por dia, mes y anio.

### Inventario
- Existencias actuales.
- Valor del inventario (a costo de adquisicion y valor potencial de venta).
- Medicamentos agotados.
- Medicamentos con stock minimo.
- Medicamentos proximos a vencer.
- Medicamentos vencidos.
- Entradas y salidas de inventario (movimientos).

### Gestion medica
- Citas por periodo.
- Citas por estado (atendidas, canceladas, pendientes).
- Consultas por veterinario.
- Consultas por especialidad.
- Mascotas atendidas por periodo.

---

## 4. La "foto del momento" (detalle importante)

Hay dos tipos de reporte segun el uso de las fechas:

- Por periodo: responden "que paso entre dos fechas" (ingresos, movimientos,
  citas). Aqui el rango de fechas SI cambia el resultado.
- Foto del momento: responden "como esta ahora" (existencias, valor, agotados,
  stock minimo, por vencer, vencidos). Estos reflejan el estado actual del
  inventario y no dependen del periodo.

El enunciado pide que todos los reportes permitan seleccionar un rango de
fechas; por eso el selector aparece siempre, pero en los reportes de foto del
momento se muestra deshabilitado con una nota, porque el resultado no cambia con
la fecha.

---

## 5. Control de acceso

El acceso a los reportes se restringe a tres perfiles: Administrador, Contable y
JEFE_BODEGA. En el frontend, el menu de reportes solo aparece para esos
perfiles; en el backend, el filtro de seguridad rechaza con un error de permisos
cualquier peticion de reportes de un rol no autorizado. La seguridad efectiva
esta en el servidor.

Perfiles nuevos de esta fase:
- Contable: enfocado en lo financiero (reportes y facturacion en consulta).
- JEFE_BODEGA: enfocado en el inventario (reportes e inventario en consulta).

---

## 6. Como usar el modulo

- Elegir la pestana (Financieros, Inventario o Gestion medica).
- Elegir el tipo de reporte en la lista.
- Ajustar el rango de fechas (viene del primer dia del mes actual hasta hoy).
- Boton "Generar": muestra la vista previa en una tabla con sus totales.
- Botones "PDF" y "Excel": descargan el mismo reporte. Lo que se ve en pantalla
  coincide con lo que se descarga.

---

## 7. Flujo de prueba sugerido (reportes)

1. En la pestana Financieros, generar "Ingresos por periodo" para un rango con
   facturas y mostrar los totales (subtotal, IVA, total).
2. Exportar ese reporte a PDF y a Excel para evidenciar ambos formatos.
3. En Inventario, generar "Valor del inventario" y mostrar que no depende de la
   fecha (foto del momento).
4. En Gestion medica, generar "Citas por estado" y mostrar el conteo.
5. Entrar con el perfil Contable y mostrar que ve reportes y facturacion, pero no
   el resto de modulos. Repetir con JEFE_BODEGA para inventario.

---

# Parte B. Despliegue con Docker y Docker Compose

## 8. Para que sirve Docker

Docker empaqueta cada parte del sistema con todo lo que necesita para funcionar,
de modo que corre igual en cualquier equipo sin instalar manualmente Java,
Maven, Node, Angular ni MySQL. Docker Compose coordina los tres servicios y los
levanta con un solo comando.

El sistema se divide en tres contenedores:
- MySQL: la base de datos.
- Backend: la API Spring Boot.
- Frontend: la aplicacion Angular servida con Nginx.

---

## 9. Como esta construido

- Cada proyecto tiene un Dockerfile de dos etapas (multi-stage): una etapa
  construye (con Maven o con Node) y otra, mas liviana, ejecuta (con el JRE de
  Java o con Nginx). Asi la imagen final no carga las herramientas de
  construccion.
- El `docker-compose.yml` declara los tres servicios, su red interna, el volumen
  de datos de MySQL y los puertos que se publican.
- El backend corre con un perfil "docker" que lee la configuracion (base de
  datos, correo, JWT) desde variables de entorno, en lugar de tenerla fija en el
  codigo.
- MySQL ejecuta automaticamente el dump de la base la primera vez que arranca,
  dejando el sistema con datos listos.

---

## 10. Como se ejecuta

Desde la carpeta del backend, con los dos repositorios como carpetas hermanas:

```bash
docker compose up --build
```

Al terminar, el sistema queda disponible en:
- Frontend: http://localhost:4200
- Backend (API): http://localhost:8080/clinica/v1
- Swagger: http://localhost:8080/clinica/v1/swagger-ui.html

Para detener: `docker compose down`. Para borrar tambien los datos y empezar de
cero: `docker compose down -v`.

El envio del codigo de verificacion (OTP) del login requiere configurar las
variables de correo en un archivo `.env` (hay una plantilla `.env.example`).

---

## 11. Problemas que se resolvieron (util para explicar)

Durante la puesta en marcha surgieron dos ajustes tipicos de este tipo de
despliegue, ya corregidos:

- Referencia circular de `JWT_SECRET`: una propiedad no puede leerse a si misma;
  se uso una variable con nombre distinto (`JWT_SECRET_ENV`).
- "Public Key Retrieval is not allowed" con MySQL 8: se agrego
  `allowPublicKeyRetrieval=true` a la URL de conexion, necesario por el metodo
  de autenticacion de MySQL 8 sin SSL.

El detalle esta en `docs/docker-despliegue.md`.

---

## 12. Flujo de prueba sugerido (Docker)

1. Mostrar los tres archivos clave: los dos Dockerfile y el docker-compose.yml.
2. Ejecutar `docker compose up --build` y mostrar como se levantan los tres
   contenedores (MySQL saludable, backend conectado, frontend sirviendo).
3. Abrir el frontend en el navegador e iniciar sesion.
4. Abrir Swagger para mostrar la documentacion de la API.
5. Explicar que todo corre en contenedores, sin instalar nada mas que Docker.

---

## 13. Glosario

- Contenedor: caja aislada con una parte del sistema y todo lo que necesita.
- Imagen: la plantilla a partir de la cual se crea un contenedor.
- Dockerfile: receta para construir una imagen.
- Docker Compose: herramienta que levanta y conecta varios contenedores.
- Multi-stage: construccion en etapas para que la imagen final sea liviana.
- Volumen: almacenamiento persistente (los datos de MySQL sobreviven a
  reinicios).
- Perfil docker: configuracion del backend para correr dentro de contenedores.
