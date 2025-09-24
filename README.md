# 🏪 Sistema de Gestión de Tiendas Mercadona

## 📋 Descripción

API REST para gestión de tiendas, trabajadores y asignaciones por secciones, desarrollada con **Spring Boot 3.2**, **PostgreSQL** y **arquitectura hexagonal** siguiendo principios **DDD** y **TDD**.

## 🚀 Inicio Rápido

### 1️⃣ **Requisitos Previos**
- **Docker & Docker Compose** instalado
- **Java 17+** (para desarrollo local)
- **Maven 3.6+** (para desarrollo local)

### 2️⃣ **Ejecución con Docker**

```bash
# Clonar y navegar al proyecto
cd prueba-tecnica-mercadona

# Levantar todos los servicios
docker-compose up -d

# Verificar que los servicios estén corriendo
docker ps
```

**Servicios levantados:**
- 🏪 **API Principal**: http://localhost:8081
- 🗄️ **PostgreSQL**: puerto 5432
- 🌐 **API Externa Tiendas**: http://localhost:8080

### 3️⃣ **Ejecución Local**

```bash
# Levantar solo la base de datos
docker-compose up -d postgres

# Ejecutar la aplicación
mvn spring-boot:run
```

## 📊 Datos de Prueba

### **🎯 Tiendas Preconfiguradas**

| Código | Nombre | Uso Recomendado |
|--------|--------|-----------------|
| **T001** | X | ✅ Creación/POST |
| **T002** | Y | ✅ Creación/POST |
| **T003** | Z | ✅ Creación/POST |
| **T004** | LA UNION | 🔍 Consultas/GET |
| **T005** | CAMINO SAN RAFAEL | 🔍 Consultas/GET |
| **T006** | AVDA. GIORGETA | 🔍 Consultas/GET |

## 🧪 Pruebas de la API

### **📬 Postman Collection**

1. **Importar colección**: `postman/Mercadona-API.postman_collection.json`
2. **Importar environment**: `postman/Mercadona-Environment.postman_environment.json`
3. **Ejecutar carpetas**:
   - 📝 **CRUD Básico**: Crear, consultar, actualizar, eliminar
   - 📊 **Reportes**: Estado de tienda y cobertura de horas
   - 🧪 **Validaciones**: Tests de reglas de negocio

### **🌐 Swagger UI**
- **URL**: http://localhost:8081/swagger-ui.html
- **API Docs**: http://localhost:8081/api-docs

### **🔗 Endpoints Principales**

#### **🏪 Tiendas**
```http
GET    /api/tiendas              # Listar todas
POST   /api/tiendas              # Crear nueva
GET    /api/tiendas/{codigo}     # Buscar por código
PUT    /api/tiendas/{codigo}     # Actualizar
DELETE /api/tiendas/{codigo}     # Eliminar (cascada)
```

#### **👥 Trabajadores**
```http
GET    /api/trabajadores         # Listar todos
POST   /api/trabajadores         # Crear nuevo
GET    /api/trabajadores/{dni}   # Buscar por DNI
PUT    /api/trabajadores/{dni}   # Actualizar
DELETE /api/trabajadores/{dni}   # Eliminar (cascada)
```

#### **📋 Asignaciones**
```http
GET    /api/asignaciones                              # Listar todas
POST   /api/asignaciones                              # Crear nueva
PUT    /api/asignaciones/trabajador/{dni}/seccion/{seccion}  # Actualizar horas
DELETE /api/asignaciones/trabajador/{dni}/seccion/{seccion}  # Eliminar
```

#### **📊 Reportes**
```http
GET /api/reportes/tienda/{codigo}/estado      # Estado completo de tienda
GET /api/reportes/tienda/{codigo}/cobertura   # Secciones con déficit de horas
```

## 🧪 Tests

### **Ejecutar Tests**
```bash
# Todos los tests
mvn test

# Tests específicos
mvn test -Dtest=TiendaServiceImplTest
mvn test -Dtest=ReporteServiceImplTest
```

### **Cobertura de Tests**
```bash
# Generar reporte de cobertura
mvn jacoco:report

# Ver reporte en: target/site/jacoco/index.html
```

## 🔧 Validaciones Implementadas

### **⚡ Validaciones Críticas**
- ✅ **Código tienda**: Formato T001-T999
- ✅ **DNI/NIE**: Validación algoritmo oficial
- ✅ **Horas trabajador**: Entre 1-8 horas
- ✅ **Horas asignación**: Entre 1-8 horas
- ✅ **Límites sección**: Horno=8h, otras=16h
- ✅ **No exceder disponibilidad**: Trabajador no puede superar sus horas
- ✅ **Unicidad**: No duplicar asignaciones trabajador-sección
- ✅ **Eliminación cascada**: Mantener integridad referencial

### **🧪 Probar Validaciones**
Usar carpeta **"🧪 Tests de Validación"** en Postman para verificar todas las reglas de negocio.

## 🏗️ Arquitectura

### **📁 Estructura del Proyecto**
```
src/main/java/com/mercadona/
├── 🏪 tienda/           # Gestión de tiendas y secciones
├── 👥 trabajador/       # Gestión de trabajadores
├── 📋 asignacion/       # Asignaciones trabajador-sección
├── 📊 reporte/          # Informes y reportes
├── 🌐 external/         # Integración API externa
├── 🔧 config/           # Configuraciones
└── 🛡️ shared/           # Componentes compartidos
```

## 🌐 Integración API Externa

### **📍 Direcciones de Tiendas**
Los reportes incluyen direcciones obtenidas de la API externa:
- **Endpoint**: http://localhost:8080/stores
- **Búsqueda**: Por nombre de tienda
- **Fallback**: "Dirección no disponible" si falla


## 🎯 Casos de Uso Principales

### **✅ Caso 1: Crear Tienda y Asignar Trabajadores**
1. Crear tienda con código T007
2. Crear trabajadores para esa tienda
3. Asignar trabajadores a secciones
4. Consultar estado de la tienda

### **✅ Caso 2: Generar Reportes**
1. Consultar estado completo de T004 (LA UNION)
2. Consultar cobertura de horas de T005 (CAMINO SAN RAFAEL)
3. Verificar que incluyen direcciones de la API externa

### **✅ Caso 3: Validar Reglas de Negocio**
1. Intentar asignar más horas de las disponibles
2. Intentar crear códigos de tienda inválidos
3. Verificar eliminación en cascada

---

## 👨‍💻 Desarrollado

**Prueba Técnica - Sistema de Gestión Mercadona**
- ⚡ **Spring Boot 3.2** + **Java 17**
- 🗄️ **PostgreSQL 15** + **Flyway**
- 🐳 **Docker** + **Testcontainers**
- 🧪 **JUnit 5** + **Mockito**
- 📊 **JaCoCo** coverage reports
- 📝 **SpringDoc OpenAPI 3**
