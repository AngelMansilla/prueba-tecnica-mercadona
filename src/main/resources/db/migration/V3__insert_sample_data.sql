-- Migración de datos: Datos de prueba
-- Autor: Angel Mansilla Puerto
-- Fecha: 2025-09-24

INSERT INTO tiendas (codigo, nombre) VALUES 
('T004', 'Mercadona Ronda Norte'),
('T005', 'Mercadona Plaza Mayor'),
('T006', 'Mercadona Zona Industrial');

INSERT INTO trabajadores (dni, nombre, horas_disponibles, tienda_id) VALUES 
-- Tienda T004 (Ronda Norte) 
('71234567A', 'Ana López García', 8, (SELECT id FROM tiendas WHERE codigo = 'T004')),
('72345678B', 'Carlos Ruiz Martín', 8, (SELECT id FROM tiendas WHERE codigo = 'T004')),
('73456789C', 'María Fernández López', 6, (SELECT id FROM tiendas WHERE codigo = 'T004')),
('74567890D', 'José García Sánchez', 8, (SELECT id FROM tiendas WHERE codigo = 'T004')),
('75678901E', 'Pedro Morales Vega', 8, (SELECT id FROM tiendas WHERE codigo = 'T004')),
('76789012F', 'Carmen Jiménez Torres', 7, (SELECT id FROM tiendas WHERE codigo = 'T004')),
('77890123G', 'Francisco Herrera Díaz', 8, (SELECT id FROM tiendas WHERE codigo = 'T004')),

-- Tienda T005 (Plaza Mayor)
('78901234H', 'Laura Martín Ruiz', 8, (SELECT id FROM tiendas WHERE codigo = 'T005')),
('X7890123L', 'Alessandro Rossi', 8, (SELECT id FROM tiendas WHERE codigo = 'T005')),
('Y7890123J', 'Amélie Dubois', 6, (SELECT id FROM tiendas WHERE codigo = 'T005')),
('X6789012S', 'Sophie Müller', 8, (SELECT id FROM tiendas WHERE codigo = 'T005')),
('Y6789012X', 'Ahmed Al-Rashid', 8, (SELECT id FROM tiendas WHERE codigo = 'T005')),

-- Tienda T006 (Zona Industrial)
('Z5678901N', 'Hans Mueller', 8, (SELECT id FROM tiendas WHERE codigo = 'T006')),
('X5678901G', 'Chen Wei Li', 8, (SELECT id FROM tiendas WHERE codigo = 'T006')),
('Y5678901C', 'Isabella Rosenberg', 8, (SELECT id FROM tiendas WHERE codigo = 'T006')),
('Z4567890R', 'Dmitri Volkov', 8, (SELECT id FROM tiendas WHERE codigo = 'T006')),
('Z3456789T', 'Fatima Al-Zahra', 6, (SELECT id FROM tiendas WHERE codigo = 'T006')),
('X2345678R', 'Raj Patel', 8, (SELECT id FROM tiendas WHERE codigo = 'T006'));

-- TIENDA T004 (Ronda Norte)
INSERT INTO asignaciones (trabajador_id, seccion_id, horas_asignadas) VALUES 
-- Horno
((SELECT id FROM trabajadores WHERE dni = '71234567A'), (SELECT id FROM secciones WHERE nombre = 'Horno'), 8),

-- Cajas
((SELECT id FROM trabajadores WHERE dni = '72345678B'), (SELECT id FROM secciones WHERE nombre = 'Cajas'), 8),
((SELECT id FROM trabajadores WHERE dni = '73456789C'), (SELECT id FROM secciones WHERE nombre = 'Cajas'), 6),
((SELECT id FROM trabajadores WHERE dni = '74567890D'), (SELECT id FROM secciones WHERE nombre = 'Cajas'), 2),

-- Pescadería
((SELECT id FROM trabajadores WHERE dni = '75678901E'), (SELECT id FROM secciones WHERE nombre = 'Pescadería'), 8),
((SELECT id FROM trabajadores WHERE dni = '76789012F'), (SELECT id FROM secciones WHERE nombre = 'Pescadería'), 7),
((SELECT id FROM trabajadores WHERE dni = '74567890D'), (SELECT id FROM secciones WHERE nombre = 'Pescadería'), 1),

-- Verduras
((SELECT id FROM trabajadores WHERE dni = '77890123G'), (SELECT id FROM secciones WHERE nombre = 'Verduras'), 8),

-- Droguería
((SELECT id FROM trabajadores WHERE dni = '74567890D'), (SELECT id FROM secciones WHERE nombre = 'Droguería'), 5);

-- TIENDA T005 (Plaza Mayor)
INSERT INTO asignaciones (trabajador_id, seccion_id, horas_asignadas) VALUES 

-- Horno
((SELECT id FROM trabajadores WHERE dni = '78901234H'), (SELECT id FROM secciones WHERE nombre = 'Horno'), 8),

-- Cajas
((SELECT id FROM trabajadores WHERE dni = 'X7890123L'), (SELECT id FROM secciones WHERE nombre = 'Cajas'), 8),
((SELECT id FROM trabajadores WHERE dni = 'Y7890123J'), (SELECT id FROM secciones WHERE nombre = 'Cajas'), 6),
((SELECT id FROM trabajadores WHERE dni = 'X6789012S'), (SELECT id FROM secciones WHERE nombre = 'Cajas'), 2),

-- Pescadería
((SELECT id FROM trabajadores WHERE dni = 'Y6789012X'), (SELECT id FROM secciones WHERE nombre = 'Pescadería'), 4),

-- Verduras
((SELECT id FROM trabajadores WHERE dni = 'X6789012S'), (SELECT id FROM secciones WHERE nombre = 'Verduras'), 6),

-- Droguería  
((SELECT id FROM trabajadores WHERE dni = 'Y6789012X'), (SELECT id FROM secciones WHERE nombre = 'Droguería'), 4);

-- TIENDA T006 (Zona Industrial) 

INSERT INTO asignaciones (trabajador_id, seccion_id, horas_asignadas) VALUES 
-- Horno
((SELECT id FROM trabajadores WHERE dni = 'Z5678901N'), (SELECT id FROM secciones WHERE nombre = 'Horno'), 8),

-- Cajas
((SELECT id FROM trabajadores WHERE dni = 'X5678901G'), (SELECT id FROM secciones WHERE nombre = 'Cajas'), 8),
((SELECT id FROM trabajadores WHERE dni = 'Y5678901C'), (SELECT id FROM secciones WHERE nombre = 'Cajas'), 6),

-- Pescadería
((SELECT id FROM trabajadores WHERE dni = 'Z4567890R'), (SELECT id FROM secciones WHERE nombre = 'Pescadería'), 8),
((SELECT id FROM trabajadores WHERE dni = 'Z3456789T'), (SELECT id FROM secciones WHERE nombre = 'Pescadería'), 6),
((SELECT id FROM trabajadores WHERE dni = 'X2345678R'), (SELECT id FROM secciones WHERE nombre = 'Pescadería'), 2),

-- Verduras
((SELECT id FROM trabajadores WHERE dni = 'Y5678901C'), (SELECT id FROM secciones WHERE nombre = 'Verduras'), 2);