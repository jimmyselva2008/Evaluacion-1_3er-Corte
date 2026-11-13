--Creacion de la base de datos
CREATE DATABASE bd_laboratorio_orm;

--Creacion del Schema 
CREATE SCHEMA laboratorio;

--Creacion de la tabla Tipo_equipo
CREATE TABLE laboratorio.tipo_equipo (
    id_tipo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);


--Creacion de la tabla Equipo
CREATE TABLE laboratorio.equipo (
    id_equipo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    marca VARCHAR(100),
    estado VARCHAR(30) NOT NULL,
    cantidad INTEGER NOT NULL,
    id_tipo INTEGER NOT NULL,

    CONSTRAINT chk_cantidad
        CHECK (cantidad >= 0),

    CONSTRAINT chk_estado
        CHECK (estado IN (
            'Disponible',
            'En mantenimiento',
            'Fuera de servicio'
        )),

    CONSTRAINT fk_tipo_equipo
        FOREIGN KEY (id_tipo)
        REFERENCES laboratorio.tipo_equipo(id_tipo)
);


--Insertar los tipos de equipo existentes
INSERT INTO laboratorio.tipo_equipo
(nombre, descripcion)
VALUES
('Computadora', 'Equipos de computación'),
('Proyector', 'Equipos de proyección'),
('Impresora', 'Equipos de impresión');

--Insertar los 8 equipos
INSERT INTO laboratorio.equipo
(codigo, nombre, marca, estado, cantidad, id_tipo)
VALUES
('EQ001', 'Laptop Dell', 'Dell', 'Disponible', 5, 1),
('EQ002', 'PC HP', 'HP', 'Disponible', 10, 1),
('EQ003', 'Laptop Lenovo', 'Lenovo', 'En mantenimiento', 3, 1),
('EQ004', 'Proyector Epson', 'Epson', 'Disponible', 4, 2),
('EQ005', 'Proyector Sony', 'Sony', 'Fuera de servicio', 2, 2),
('EQ006', 'Impresora Canon', 'Canon', 'Disponible', 3, 3),
('EQ007', 'Impresora HP', 'HP', 'En mantenimiento', 2, 3),
('EQ008', 'PC Lenovo', 'Lenovo', 'Disponible', 6, 1);


--Consulta de los 8 registros
SELECT
    e.id_equipo,
    e.codigo,
    e.nombre,
    e.marca,
    e.estado,
    e.cantidad,
    t.nombre AS tipo_equipo
FROM laboratorio.equipo e
INNER JOIN laboratorio.tipo_equipo t
    ON e.id_tipo = t.id_tipo
ORDER BY e.id_equipo;

