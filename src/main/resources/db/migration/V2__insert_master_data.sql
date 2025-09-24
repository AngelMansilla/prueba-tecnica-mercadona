-- Migración de datos maestros: Secciones predefinidas
-- Autor: Angel Mansilla Puerto
-- Fecha: 2025-09-24

-- Insertar las 5 secciones predefinidas del enunciado
INSERT INTO secciones (nombre, horas_necesarias) VALUES
    ('Horno', 8),
    ('Cajas', 16),
    ('Pescadería', 16),
    ('Verduras', 16),
    ('Droguería', 16);

-- Verificar que se insertaron correctamente
-- Total debe ser 5 secciones: 1 con 8 horas + 4 con 16 horas
DO $$
DECLARE
    total_secciones INTEGER;
    secciones_8h INTEGER;
    secciones_16h INTEGER;
BEGIN
    SELECT COUNT(*) INTO total_secciones FROM secciones;
    SELECT COUNT(*) INTO secciones_8h FROM secciones WHERE horas_necesarias = 8;
    SELECT COUNT(*) INTO secciones_16h FROM secciones WHERE horas_necesarias = 16;
    
    IF total_secciones != 5 THEN
        RAISE EXCEPTION 'Error: Se esperaban 5 secciones, se encontraron %', total_secciones;
    END IF;
    
    IF secciones_8h != 1 THEN
        RAISE EXCEPTION 'Error: Se esperaba 1 sección de 8h (Horno), se encontraron %', secciones_8h;
    END IF;
    
    IF secciones_16h != 4 THEN
        RAISE EXCEPTION 'Error: Se esperaban 4 secciones de 16h, se encontraron %', secciones_16h;
    END IF;
    
    RAISE NOTICE 'Datos maestros insertados correctamente: % secciones', total_secciones;
END $$;
