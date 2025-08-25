
-- BORRA SI EXISTE
DROP DATABASE IF EXISTS fidebank;

-- 1) Crear base
CREATE DATABASE fidebank
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE fidebank;

-- 2) Tablas
CREATE TABLE cuenta (
  numero     INT AUTO_INCREMENT PRIMARY KEY,
  nombre     VARCHAR(60)  NOT NULL,
  apellido   VARCHAR(60)  NOT NULL,
  pin        VARCHAR(20)  NOT NULL,
  saldo      DECIMAL(12,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB;

-- Historial par comprobantes y auditoria
CREATE TABLE movimiento (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  fecha            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tipo             VARCHAR(20) NOT NULL,   -- DEPOSITO / RETIRO / TRANSFERENCIA / CONSULTA_SALDO / APERTURA
  monto            DECIMAL(12,2) NOT NULL,
  cuenta_origen    INT NULL,
  cuenta_destino   INT NULL,
  saldo_resultante DECIMAL(12,2) NULL,
  detalle          VARCHAR(255) NULL,
  INDEX (cuenta_origen),
  INDEX (cuenta_destino)
) ENGINE=InnoDB;

-- 3) datos inisiales
INSERT INTO cuenta(nombre, apellido, pin, saldo) VALUES
('Juan','Perez','1234',100000.00),
('Maria','Gomez','4321', 25000.00);

SELECT 'Cuentas cargadas:' AS info;
SELECT numero, nombre, apellido, pin, saldo FROM cuenta;

SELECT 'Estructura movimiento:' AS info;
DESCRIBE movimiento;
