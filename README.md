# Hackathon #1: Oreo Insight Factory 🍪📈

## Descripción General

La fábrica de **Oreo** está por lanzar un piloto con UTEC para transformar sus datos de ventas en **insights accionables**. Tu misión es construir un **backend** sencillo y sólido que permita registrar ventas y, a partir de esos datos, **generar resúmenes automáticos** en lenguaje natural usando **GitHub Models** (vía GitHub Marketplace).

El énfasis no está en pantallas ni frontends, sino en la **calidad del contrato de API**, autenticación básica, persistencia, pruebas mínimas y un endpoint de **insights** que consulte un LLM. La validación se hará ejecutando un **Postman Flow** end-to-end sobre tu backend. 🥛🤖

## Duración y Formato

- **Tiempo**: 2 horas
- **Equipos**: Grupos de 4 o 5 estudiantes  
- **Recursos**: Uso de IA permitido, documentación y material de Internet

## Contexto del Negocio

Oreo quiere dejar de "mojar la galleta a ciegas" y empezar a **entender qué pasa en cada sucursal**: qué SKU lidera, cuándo hay picos de demanda y cómo evoluciona el ticket promedio. Para ello, busca un backend que reciba ventas, consolide métricas y pida a un **LLM** un **resumen corto y claro** que cualquier analista pueda leer en segundos. 🍪🥛

Tu servicio será el motor de insights: **seguro** (JWT), **consistente** (JPA) y **probado** (testing mínimo). Si el Postman Flow "se la come" completa —login, seed de ventas, consultas y /summary—, ¡estás listo para producción… o al menos para un vaso grande de leche! 🚀

## Requerimientos Técnicos

### Tecnologías Obligatorias

- Java 17+
- Spring Boot 3.x
- Spring Security con JWT
- Spring Data JPA
- H2 o PostgreSQL (a elección)
- Cliente HTTP o SDK para GitHub Models API

### Variables de Entorno Requeridas

```properties
GITHUB_TOKEN=<tu_token_de_GitHub>
GITHUB_MODELS_URL=<endpoint_REST_de_GitHub_Models>
MODEL_ID=<id_del_modelo_del_Marketplace>
JWT_SECRET=<clave_para_firmar_JWT>
# Si usas PostgreSQL:
DB_URL=<jdbc_url>
DB_USER=<usuario_db>
DB_PASS=<password_db>
```

## Roles y Seguridad

Implementar JWT para autenticación y los siguientes roles con sus respectivos permisos:

1. **ROLE_CENTRAL**: Oficina central de Oreo - Acceso completo a todas las ventas de todas las sucursales, reportes globales y gestión de usuarios
2. **ROLE_BRANCH**: Usuario de sucursal - Solo puede ver y crear ventas de su propia sucursal asignada

Cada usuario con `ROLE_BRANCH` debe tener una sucursal asignada al momento del registro.

## Funcionalidades Requeridas

### 1. Autenticación JWT

| Método | Endpoint | Descripción | Roles Permitidos | Request Body | Response |
|--------|----------|-------------|-----------------|--------------|----------|
| POST | `/auth/register` | Crear nuevo usuario | Público | `{"username": "oreo.admin", "email": "admin@oreo.com", "password": "Oreo1234", "role": "CENTRAL"}` o `{"username": "miraflores.user", "email": "mira@oreo.com", "password": "Oreo1234", "role": "BRANCH", "branch": "Miraflores"}` | 201: `{"id": "u_01J...", "username": "oreo.admin", "email": "admin@oreo.com", "role": "CENTRAL", "branch": null, "createdAt": "2025-09-12T18:10:00Z"}` |
| POST | `/auth/login` | Autenticar y obtener JWT | Público | `{"username": "oreo.admin", "password": "Oreo1234"}` | 200: `{"token": "eyJhbGci...", "expiresIn": 3600, "role": "CENTRAL", "branch": null}` |

**Reglas de validación**:
- Username: 3-30 caracteres, alfanumérico + `_` y `.`
- Email: formato válido
- Password: mínimo 8 caracteres
- Role: debe ser uno de ["CENTRAL", "BRANCH"]
- Branch: obligatorio si role es "BRANCH", null si es "CENTRAL"

### 2. Gestión de Ventas

| Método | Endpoint | Descripción | Roles Permitidos | Request Body | Response |
|--------|----------|-------------|-----------------|--------------|----------|
| POST | `/sales` | Crear nueva venta | CENTRAL (cualquier branch), BRANCH (solo su branch) | Ver ejemplo abajo | 201: Venta creada |
| GET | `/sales/{id}` | Obtener detalle de venta | CENTRAL (todas), BRANCH (solo de su branch) | - | 200: Detalle completo |
| GET | `/sales` | Listar ventas con filtros | CENTRAL (todas), BRANCH (solo su branch) | Query params: `from`, `to`, `branch`, `page`, `size` | 200: Lista paginada |
| PUT | `/sales/{id}` | Actualizar venta | CENTRAL (todas), BRANCH (solo de su branch) | Ver ejemplo abajo | 200: Venta actualizada |
| DELETE | `/sales/{id}` | Eliminar venta | CENTRAL | - | 204: No Content |

**Ejemplo de creación de venta**:
```json
{
  "sku": "OREO_CLASSIC_12",
  "units": 25,
  "price": 1.99,
  "branch": "Miraflores",
  "soldAt": "2025-09-12T16:30:00Z"
}
```

**Nota**: Los usuarios BRANCH solo pueden crear ventas para su sucursal asignada. Si intentan crear para otra sucursal, devolver 403.

**Response esperado** (201):
```json
{
  "id": "s_01K...",
  "sku": "OREO_CLASSIC_12",
  "units": 25,
  "price": 1.99,
  "branch": "Miraflores",
  "soldAt": "2025-09-12T16:30:00Z",
  "createdBy": "miraflores.user"
}
```

### 3. Resumen Semanal con LLM

| Método | Endpoint | Descripción | Roles Permitidos |
|--------|----------|-------------|-----------------|
| POST | `/sales/summary/weekly` | Generar resumen semanal usando GitHub Models | CENTRAL (cualquier branch), BRANCH (solo su branch) |
| GET | `/sales/summary/history` | Ver historial de resúmenes generados | CENTRAL |

**Request (opcional)** para `/sales/summary/weekly`:
```json
{
  "from": "2025-09-01",
  "to": "2025-09-07",
  "branch": "Miraflores"
}
```
*Si no se envía body, calcular automáticamente la última semana.*
*Usuarios BRANCH solo pueden generar resúmenes de su propia sucursal.*

**Response esperado** (200):
```json
{
  "summaryText": "Esta semana vendimos 1,250 unidades con ingresos de $4,800.50. El SKU estrella fue OREO_DOUBLE con 420 unidades. La sucursal Miraflores lideró las ventas.",
  "range": {
    "from": "2025-09-01",
    "to": "2025-09-07",
    "branch": "Miraflores"
  },
  "aggregates": {
    "totalUnits": 1250,
    "totalRevenue": 4800.50,
    "topSku": "OREO_DOUBLE",
    "topBranch": "Miraflores"
  }
}
```

### 4. Gestión de Usuarios (Solo CENTRAL)

| Método | Endpoint | Descripción | Roles Permitidos |
|--------|----------|-------------|-----------------|
| GET | `/users` | Listar todos los usuarios | CENTRAL |
| GET | `/users/{id}` | Ver detalle de usuario | CENTRAL |
| DELETE | `/users/{id}` | Eliminar usuario | CENTRAL |

## 🎯 RETO EXTRA: Carta Mágica de Puntos Bonus 🪄

**¡Para los valientes que quieran puntos extra!** 🏆

### El Desafío

¿Qué pasaría si los gerentes de Oreo no solo quieren leer el resumen, sino también **descargarlo como PDF** con **gráficos incluidos**? 📊📄

### Endpoints Bonus

| Método | Endpoint | Descripción | Roles Permitidos |
|--------|----------|-------------|-----------------|
| POST | `/sales/report/pdf` | Generar reporte PDF con resumen y gráficos | CENTRAL, BRANCH |
| POST | `/sales/charts/generate` | Generar gráficos de ventas (bar, pie, line) | CENTRAL, BRANCH |

### Pistas para el Éxito 🕵️‍♂️

- **Pista #1**: No todo tiene que hacerse en Java... ¿qué tal si llamas a otra API especializada en generar gráficos? 🤔
- **Pista #2**: Servicios como **QuickChart.io**, **Chart.js Image**, o **Google Charts API** pueden ser tus mejores amigos
- **Pista #3**: Para PDFs, bibliotecas como **iText**, **Apache PDFBox** o servicios externos como **PDFShift** pueden salvarte
- **Pista #4**: El LLM puede generar no solo texto, sino también **código** para los gráficos (piensa en Chart.js config o SVG)
- **Pista #5**: Recuerda que puedes pedirle al LLM que te devuelva los datos en formato JSON estructurado para facilitar la generación de gráficos

### Ejemplo de Response para PDF

```json
{
  "reportId": "r_01K...",
  "downloadUrl": "/reports/r_01K.pdf",
  "expiresIn": 3600,
  "preview": {
    "summaryText": "Esta semana fue histórica...",
    "chartsIncluded": ["sales_by_sku", "revenue_trend", "branch_comparison"],
    "pages": 3
  }
}
```

### Criterios de Evaluación del Reto

- **+5 puntos**: Generar PDF básico con el resumen
- **+10 puntos**: Incluir al menos un gráfico en el PDF
- **+15 puntos**: PDF completo con múltiples gráficos, tabla de datos y formato profesional

**Nota**: Este reto es OPCIONAL. Los equipos que lo intenten y fallen no serán penalizados. ¡Es puro upside! 🚀

## Integración con GitHub Models

### Proceso Requerido

1. **Calcular agregados** de las ventas:
   - totalUnits
   - totalRevenue  
   - topSku (el más vendido por unidades)
   - topBranch (sucursal con más ventas)

2. **Construir prompt** para el LLM:
```json
{
  "model": "${MODEL_ID}",
  "messages": [
    {"role": "system", "content": "Eres un analista que escribe resúmenes breves y claros."},
    {"role": "user", "content": "Con estos datos: totalUnits=1250, totalRevenue=4800.50, topSku=OREO_DOUBLE, topBranch=Miraflores. Devuelve un resumen ≤120 palabras."}
  ],
  "max_tokens": 200
}
```

3. **Validaciones del resumen**:
   - Máximo 120 palabras
   - Debe mencionar al menos uno: unidades totales, SKU top, sucursal top, o total recaudado
   - En español, claro y sin alucinaciones

## Manejo de Errores

Formato estándar para todos los errores:
```json
{
  "error": "BAD_REQUEST",
  "message": "Detalle claro del problema",
  "timestamp": "2025-09-12T18:10:00Z",
  "path": "/sales"
}
```

Códigos HTTP esperados:
- 201: Recurso creado
- 200: OK
- 204: Sin contenido (cuando no hay ventas en el rango)
- 400: Validación fallida
- 401: No autenticado
- 403: Sin permisos (intentando acceder a datos de otra sucursal)
- 404: Recurso no encontrado
- 409: Conflicto (username/email ya existe)
- 503: Servicio no disponible (LLM caído)

## Validación con Postman Flow

La colección ejecutará esta secuencia:

1. **Register CENTRAL** → Assert 201, guardar userId
2. **Login CENTRAL** → Assert 200, guardar {{centralToken}}
3. **Register BRANCH (Miraflores)** → Assert 201
4. **Login BRANCH** → Assert 200, guardar {{branchToken}}
5. **Crear 5 ventas (con CENTRAL)** → Assert 201 cada una (diferentes sucursales)
6. **Listar todas las ventas (con CENTRAL)** → Assert 200, lista con todas
7. **Listar ventas (con BRANCH)** → Assert 200, solo ventas de Miraflores
8. **Generar resumen LLM (con BRANCH)** → Assert 200, solo datos de Miraflores
9. **Intentar crear venta otra sucursal (con BRANCH)** → Assert 403 Forbidden
10. **Eliminar venta (con CENTRAL)** → Assert 204

### Datos de Prueba (Seeds)
```json
[
  {"sku": "OREO_CLASSIC_12", "units": 25, "price": 1.99, "branch": "Miraflores", "soldAt": "2025-09-01T10:30:00Z"},
  {"sku": "OREO_DOUBLE", "units": 40, "price": 2.49, "branch": "Miraflores", "soldAt": "2025-09-02T15:10:00Z"},
  {"sku": "OREO_THINS", "units": 32, "price": 2.19, "branch": "San Isidro", "soldAt": "2025-09-03T11:05:00Z"},
  {"sku": "OREO_DOUBLE", "units": 55, "price": 2.49, "branch": "San Isidro", "soldAt": "2025-09-04T18:50:00Z"},
  {"sku": "OREO_CLASSIC_12", "units": 20, "price": 1.99, "branch": "Miraflores", "soldAt": "2025-09-05T09:40:00Z"}
]
```

## Entregables

1. **Código fuente** completo en un repositorio público de GitHub
2. **Postman Collection** (archivo .json) en el root del repositorio
3. **README.md** con:
   - Instrucciones para ejecutar el proyecto
   - Variables de entorno necesarias
   - Cómo importar y ejecutar la colección de Postman
   - (Si intentaste el reto) Documentación de los endpoints bonus

## Criterios de Evaluación

| Criterio | Peso | Descripción |
|----------|------|-------------|
| **Funcionalidad** | 60% | Auth con roles (15%), CRUD ventas con permisos por sucursal (25%), Resumen LLM operativo (20%) |
| **Calidad de Código** | 20% | Capas claras, DTOs, validaciones, manejo de errores |
| **Testing** | 10% | Tests unitarios mínimos |
| **Postman Collection** | 10% | Collection funcional que valide todo el flow con diferentes roles |
| **Reto Bonus** | +15% máx | Generación de PDF y/o gráficos (opcional) |

## Observaciones Adicionales

- El prompt al LLM debe ser **corto y explícito** con los números agregados
- Si usas H2, activa la consola en modo dev para debugging
- **NUNCA** subas tokens o secretos al repositorio
- El resumen debe reflejar los datos reales (no inventar información)
- Maneja las fallas del LLM con 503 y mensaje claro al usuario
- Los usuarios BRANCH solo ven/modifican datos de su sucursal asignada
- El reto bonus es completamente opcional - ¡pero los puntos extra pueden hacer la diferencia! 🎯

¡Que la galleta esté de tu lado! 🍪✨

Con mucho cariño desde California,

**Gabriel Romero**  
❤️
