-- Migración inicial: Esquema completo de entidades
-- Autor: Angel Mansilla Puerto
-- Fecha: 2025-09-24

-- Tabla Tiendas
CREATE TABLE tiendas (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla Secciones
CREATE TABLE secciones (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    horas_necesarias INTEGER NOT NULL CHECK (horas_necesarias > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla Trabajadores
CREATE TABLE trabajadores (
    id BIGSERIAL PRIMARY KEY,
    dni VARCHAR(9) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    horas_disponibles INTEGER NOT NULL CHECK (horas_disponibles >= 0 AND horas_disponibles <= 8),
    tienda_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tienda_id) REFERENCES tiendas(id)
);

-- Tabla Asignaciones (relación trabajador-sección)
CREATE TABLE asignaciones (
    id BIGSERIAL PRIMARY KEY,
    trabajador_id BIGINT NOT NULL,
    seccion_id BIGINT NOT NULL,
    horas_asignadas INTEGER NOT NULL CHECK (horas_asignadas > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trabajador_id) REFERENCES trabajadores(id) ON DELETE CASCADE,
    FOREIGN KEY (seccion_id) REFERENCES secciones(id),
    UNIQUE(trabajador_id, seccion_id)
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_trabajadores_tienda ON trabajadores(tienda_id);
CREATE INDEX idx_trabajadores_dni ON trabajadores(dni);
CREATE INDEX idx_asignaciones_trabajador ON asignaciones(trabajador_id);
CREATE INDEX idx_asignaciones_seccion ON asignaciones(seccion_id);
CREATE INDEX idx_tiendas_codigo ON tiendas(codigo);
CREATE INDEX idx_secciones_nombre ON secciones(nombre);

-- Comentarios de documentación
COMMENT ON TABLE tiendas IS 'Registro de tiendas de la cadena Mercadona';
COMMENT ON TABLE secciones IS 'Secciones predefinidas: Horno, Cajas, Pescadería, Verduras, Droguería';
COMMENT ON TABLE trabajadores IS 'Empleados asignados a tiendas específicas';
COMMENT ON TABLE asignaciones IS 'Asignación de trabajadores a secciones con horas específicas';

COMMENT ON COLUMN trabajadores.dni IS 'DNI o NIE español válido';
COMMENT ON COLUMN trabajadores.horas_disponibles IS 'Máximo 8 horas según contrato';
COMMENT ON COLUMN secciones.horas_necesarias IS 'Horno: 8h, Resto: 16h según enunciado';
COMMENT ON COLUMN asignaciones.horas_asignadas IS 'Horas asignadas a esta sección específica';
