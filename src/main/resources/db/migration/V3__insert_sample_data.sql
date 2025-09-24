-- Migración de datos: Datos de prueba
-- Autor: Angel Mansilla Puerto
-- Fecha: 2025-09-24

INSERT INTO tiendas (codigo, nombre) VALUES 
('T004', 'Mercadona Ronda Norte'),
('T005', 'Mercadona Plaza Mayor'),
('T006', 'Mercadona Zona Industrial');

INSERT INTO trabajadores (dni, nombre, horas_disponibles, tienda_id) VALUES 
-- Tienda T004 (Ronda Norte) 
('44444444R', 'Ana López García', 8, (SELECT id FROM tiendas WHERE codigo = 'T004')),
('55555555S', 'Carlos Ruiz Martín', 8, (SELECT id FROM tiendas WHERE codigo = 'T004')),
('66666666Q', 'María Fernández López', 6, (SELECT id FROM tiendas WHERE codigo = 'T004')),
('77777777B', 'José García Sánchez', 8, (SELECT id FROM tiendas WHERE codigo = 'T004')),
('11111111H', 'Pedro Morales Vega', 8, (SELECT id FROM tiendas WHERE codigo = 'T004')),
('22222222J', 'Carmen Jiménez Torres', 7, (SELECT id FROM tiendas WHERE codigo = 'T004')),
('33333333P', 'Francisco Herrera Díaz', 8, (SELECT id FROM tiendas WHERE codigo = 'T004')),

-- Tienda T005 (Plaza Mayor)
('88888888M', 'Laura Martín Ruiz', 8, (SELECT id FROM tiendas WHERE codigo = 'T005')),
('X1234567L', 'Alessandro Rossi', 8, (SELECT id FROM tiendas WHERE codigo = 'T005')),
('Y2222222J', 'Amélie Dubois', 6, (SELECT id FROM tiendas WHERE codigo = 'T005')),
('X7654321S', 'Sophie Müller', 8, (SELECT id FROM tiendas WHERE codigo = 'T005')),
('Y1234567X', 'Ahmed Al-Rashid', 8, (SELECT id FROM tiendas WHERE codigo = 'T005')),

-- Tienda T006 (Zona Industrial)
('Z3333333N', 'Hans Mueller', 8, (SELECT id FROM tiendas WHERE codigo = 'T006')),
('X4444444G', 'Chen Wei Li', 8, (SELECT id FROM tiendas WHERE codigo = 'T006')),
('Y7654321C', 'Isabella Rosenberg', 8, (SELECT id FROM tiendas WHERE codigo = 'T006')),
('Z1234567R', 'Dmitri Volkov', 8, (SELECT id FROM tiendas WHERE codigo = 'T006')),
('Z7654321T', 'Fatima Al-Zahra', 6, (SELECT id FROM tiendas WHERE codigo = 'T006')),
('X1111111R', 'Raj Patel', 8, (SELECT id FROM tiendas WHERE codigo = 'T006'));

-- TIENDA T004 (Ronda Norte)
INSERT INTO asignaciones (trabajador_id, seccion_id, horas_asignadas) VALUES 
-- Horno
((SELECT id FROM trabajadores WHERE dni = '44444444R'), (SELECT id FROM secciones WHERE nombre = 'Horno'), 8),

-- Cajas
((SELECT id FROM trabajadores WHERE dni = '55555555S'), (SELECT id FROM secciones WHERE nombre = 'Cajas'), 8),
((SELECT id FROM trabajadores WHERE dni = '66666666Q'), (SELECT id FROM secciones WHERE nombre = 'Cajas'), 6),
((SELECT id FROM trabajadores WHERE dni = '77777777B'), (SELECT id FROM secciones WHERE nombre = 'Cajas'), 2),

-- Pescadería
((SELECT id FROM trabajadores WHERE dni = '11111111H'), (SELECT id FROM secciones WHERE nombre = 'Pescadería'), 8),
((SELECT id FROM trabajadores WHERE dni = '22222222J'), (SELECT id FROM secciones WHERE nombre = 'Pescadería'), 7),
((SELECT id FROM trabajadores WHERE dni = '77777777B'), (SELECT id FROM secciones WHERE nombre = 'Pescadería'), 1),

-- Verduras
((SELECT id FROM trabajadores WHERE dni = '33333333P'), (SELECT id FROM secciones WHERE nombre = 'Verduras'), 8),

-- Droguería
((SELECT id FROM trabajadores WHERE dni = '77777777B'), (SELECT id FROM secciones WHERE nombre = 'Droguería'), 5);

-- TIENDA T005 (Plaza Mayor)
INSERT INTO asignaciones (trabajador_id, seccion_id, horas_asignadas) VALUES 

-- Horno
((SELECT id FROM trabajadores WHERE dni = '88888888M'), (SELECT id FROM secciones WHERE nombre = 'Horno'), 8),

-- Cajas
((SELECT id FROM trabajadores WHERE dni = 'X1234567L'), (SELECT id FROM secciones WHERE nombre = 'Cajas'), 8),
((SELECT id FROM trabajadores WHERE dni = 'Y2222222J'), (SELECT id FROM secciones WHERE nombre = 'Cajas'), 6),
((SELECT id FROM trabajadores WHERE dni = 'X7654321S'), (SELECT id FROM secciones WHERE nombre = 'Cajas'), 2),

-- Pescadería
((SELECT id FROM trabajadores WHERE dni = 'Y1234567X'), (SELECT id FROM secciones WHERE nombre = 'Pescadería'), 4),

-- Verduras
((SELECT id FROM trabajadores WHERE dni = 'X7654321S'), (SELECT id FROM secciones WHERE nombre = 'Verduras'), 6),

-- Droguería  
((SELECT id FROM trabajadores WHERE dni = 'Y1234567X'), (SELECT id FROM secciones WHERE nombre = 'Droguería'), 4);

-- TIENDA T006 (Zona Industrial) 

INSERT INTO asignaciones (trabajador_id, seccion_id, horas_asignadas) VALUES 
-- Horno
((SELECT id FROM trabajadores WHERE dni = 'Z3333333N'), (SELECT id FROM secciones WHERE nombre = 'Horno'), 8),

-- Cajas
((SELECT id FROM trabajadores WHERE dni = 'X4444444G'), (SELECT id FROM secciones WHERE nombre = 'Cajas'), 8),
((SELECT id FROM trabajadores WHERE dni = 'Y7654321C'), (SELECT id FROM secciones WHERE nombre = 'Cajas'), 6),

-- Pescadería
((SELECT id FROM trabajadores WHERE dni = 'Z1234567R'), (SELECT id FROM secciones WHERE nombre = 'Pescadería'), 8),
((SELECT id FROM trabajadores WHERE dni = 'Z7654321T'), (SELECT id FROM secciones WHERE nombre = 'Pescadería'), 6),
((SELECT id FROM trabajadores WHERE dni = 'X1111111R'), (SELECT id FROM secciones WHERE nombre = 'Pescadería'), 2),

-- Verduras
((SELECT id FROM trabajadores WHERE dni = 'Y7654321C'), (SELECT id FROM secciones WHERE nombre = 'Verduras'), 2);