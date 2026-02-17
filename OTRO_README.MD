### Cómo ejecutar el proyecto localmente
* IDE compatible con proyectos MAVEN o tener instalado MAVEN en su equipo
* Java 11 (JDK11)
* Por default se usa la base de datos embedded h2. Si desea usar PostgreSQL
* Despues de clonar el proyecto, ir a su carpeta personal `/home/username`, abrir terminal y ejecutar lo siguiente:
```shell
 $ mkdir .m2

$ cd .m2
```
* Cuando estemos en la carpeta .m2, se debe crear un archivo vacío llamado `settings.xml` y pegar lo siguiente dentro del archivo:

```xml
  <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>true</enabled></snapshots>
        </repository>
        
        <repository>
          <id>github</id>
          <name>GitHub jokoframework Apache Maven Packages</name>
          <url>https://maven.pkg.github.com/jokoframework/security</url>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>USERNAME</username>
      <password>PERSONAL ACCESS TOKEN</password>
    </server>
  </servers>
</settings>
```
* Despues en el propertie puse esta opcion para ingresar a H2 en la web: `spring.h2.console.enabled=true`

### Cómo ejecutar migraciones/levantar BD (si aplica)
* En este caso el caso el proyecto ya levanta las migraciones que estan en db-changelog.xml

### Ejemplos de uso de API (curl o equivalente)
estos son los ejemplos
crear un nuevo pais:
```shell
curl request POST 'localhost:8080/api/countries' \
  --header 'X-JOKO-AUTH: string' \
  --header 'Content-Type: application/json' \
  --header 'Accept: application/json' \
  --body '{
  "description": "China",
  "id": "chi"
}'
```

actualizar un pais:
```shell
curl request PUT 'localhost:8080/api/countries' \
  --header 'X-JOKO-AUTH: string' \
  --header 'Content-Type: application/json' \
  --header 'Accept: application/json' \
  --body '{
  "description": "Chinas",
  "id": "chi"
}'
```
eliminar un pais:
```shell
curl request DELETE 'localhost:8080/api/countries/chi'
```
listar auditorias:
```shell
postman request 'localhost:8080/api/secure/audit-events/list?orderAsc=false&page=0&resourceId=chi&resourceType=COUNTRY&size=20&to=2026-02-16T23%3A59%3A59Z&action=USER_DELETED&from=2026-02-01T00%3A00%3A00Z' \
  --header 'X-JOKO-AUTH: string' \
  --header 'X-JOKO-STARTER-KIT-VERSION: string' \
  --header 'Accept: application/json'
```
### Supuestos realizados
* Persistencia de auditoria: logrado
* Generación automática de eventos: logrado
* API de consulta de auditoría: logrado(falta mejorar)
* Seguridad y permisos: no logrado(por falta de cuenta)
*  Trazabilidad mínima: logrado(se genera en el back)
* Logs útiles: logrado

### Decisiones técnicas relevantes
* Se implementó un filtro CorrelationFilter que toma X-Correlation-Id o genera uno si no existe, lo propaga en la respuesta y lo guarda en MDC para logueo.
* Se creó RequestContext usando ThreadLocal para almacenar el correlationId durante el ciclo de vida del request. Se limpia en finally

### Qué se dejó fuera por tiempo y qué se mejoraría con más horas
* Se podria haber configurado para que solo sea visible por endpoint seguros, una mejor integracion y tal vez un flujo mas limpio.
* Mejores validaciones en la API de auditoria, manejo de errores y paginacion.
* Implementar seguridad y permisos de acceso a la API de auditoría.
* Mejorar la estructura de los eventos de auditoría y la metadata asociada.

## Interacciones con el asistente de IA (consultas técnicas)

El asistente de IA fue utilizado principalmente como herramienta de apoyo técnico y validación de decisiones durante el desarrollo. Las consultas se enfocaron en problemas específicos del proyecto y no en la generación automática completa del sistema.

### Consultas sobre arquitectura y diseño
- Validación del uso de CorrelationId para trazabilidad en sistemas backend
- Recomendaciones sobre manejo de metadata (old/new) en eventos de auditoría
- Buenas prácticas para estructurar DTO, Entity y Repository en Spring Boot

### Consultas sobre debugging y resolución de errores
- Diagnóstico de errores 401 en Spring Security y configuración de rutas protegidas
- Interpretación de logs de Spring Boot y configuración de logging en Windows

### Consultas sobre integración con el starter kit
- Adaptación del código a la arquitectura existente del proyecto (Manager/Service/Repository)
- Uso correcto de `AuditEventService` dentro de la lógica de negocio
- Manejo del SecurityContext para obtener el actor autenticado
- Implementación consistente con las convenciones del framework Joko

### Consultas sobre base de datos y configuración
- Conexión y uso de la base de datos H2 embebida
- Interpretación de propiedades de configuración de Spring
- Manejo de scripts de inicialización y schema

### Consultas sobre buenas prácticas backend
- Uso de filtros para manejo de contexto por request
- Implementación de logging con MDC
- Estrategias de trazabilidad y observabilidad en APIs REST
