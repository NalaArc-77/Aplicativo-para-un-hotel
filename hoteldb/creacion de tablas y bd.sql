create database hoteldb;
use hoteldb;

CREATE TABLE cliente (
    idCliente INT PRIMARY KEY AUTO_INCREMENT,
    nombres VARCHAR(100),
    apellidos VARCHAR(100),
    numDocumento VARCHAR(20),
    tipoDocumento VARCHAR(20),
    telefono VARCHAR(15)
);

CREATE TABLE habitacion (
    idHabitacion INT PRIMARY KEY AUTO_INCREMENT,
    numero VARCHAR(10),
    tipo VARCHAR(50),
    precioXNoche DOUBLE,
    estado VARCHAR(20),
    piso int
);

CREATE TABLE reserva (
    idReserva INT PRIMARY KEY AUTO_INCREMENT,
    idCliente INT,
    idHabitacion INT,
    fechaIngreso DATE,
    fechaSalida DATE,
    estado VARCHAR(20),
    FOREIGN KEY (idCliente) REFERENCES cliente(idCliente) ON DELETE CASCADE,
    FOREIGN KEY (idHabitacion) REFERENCES habitacion(idHabitacion) ON DELETE CASCADE
);

CREATE TABLE factura (
    idFactura INT PRIMARY KEY AUTO_INCREMENT,
    idReserva INT,
    totalPagar DOUBLE,
    fechaEmision DATE,
    FOREIGN KEY (idReserva) REFERENCES reserva(idReserva) ON DELETE CASCADE
);





DELETE FROM cliente WHERE idCliente = 3;
ALTER TABLE habitacion
ADD piso INT;

SELECT 
    h.idHabitacion,
    h.numero AS numeroHabitacion
FROM reserva r
JOIN cliente c ON r.idCliente = c.idCliente
JOIN habitacion h ON r.idHabitacion = h.idHabitacion;

SELECT idHabitacion FROM reserva WHERE idCliente=2;

SELECT c.nombres, c.apellidos, r.fechaIngreso, r.fechaSalida, h.tipo, h.precioXNoche FROM reserva r 
JOIN cliente c ON r.idCliente = c.idCliente 
JOIN habitacion h ON r.idHabitacion = h.idHabitacion 
WHERE r.idReserva = 24;

UPDATE reserva SET estado = 'Cancelado' WHERE idReserva = 24;

SELECT r.idHabitacion
FROM factura f
JOIN reserva r ON f.idReserva = r.idReserva
WHERE f.idFactura = 24;



