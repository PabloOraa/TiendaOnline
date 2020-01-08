-- phpMyAdmin SQL Dump
-- version 4.9.2
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 18-12-2019 a las 22:15:52
-- Versión del servidor: 10.4.10-MariaDB
-- Versión de PHP: 7.3.12

/*ELIMINACION DE TABLAS*/
DROP TABLE IF EXISTS COMPRA;
DROP TABLE IF EXISTS TIQUET;
DROP TABLE IF EXISTS USUARIO;
DROP TABLE IF EXISTS PRODUCTO;

/*ELIMINAMOS LA BASE DE DATOS*/
DROP DATABASE IF EXISTS ACCESODATOS;

/*CREAMOS LA BASE DE DATOS*/
CREATE DATABASE ACCESODATOS;

/*CREACION DE TABLAS*/
CREATE TABLE USUARIO
(
	IdUser INTEGER PRIMARY KEY,
	Username VARCHAR(30) UNIQUE,
	Password VARCHAR(30)
);
 
CREATE TABLE PRODUCTO
(
	IdProduct INTEGER PRIMARY KEY,
	Description VARCHAR(50),
	Price DECIMAL(60,30)
);

/*INSERCION DE DATOS BASE*/
INSERT INTO USUARIO(IdUser) VALUES(0);
INSERT INTO PRODUCTO(IdProduct) VALUES(0);

/*AÑADIMOS EL AUTO_INCREMENT y NOT NULL*/
ALTER TABLE USUARIO
CHANGE IdUser IdUser INTEGER AUTO_INCREMENT;

ALTER TABLE USUARIO
MODIFY Password VARCHAR(30) NOT NULL;

ALTER TABLE USUARIO
MODIFY Username VARCHAR(30) NOT NULL;

ALTER TABLE PRODUCTO
CHANGE IdProduct IdProduct INTEGER AUTO_INCREMENT;

ALTER TABLE PRODUCTO 
MODIFY Description VARCHAR(50) NOT NULL;

CREATE TABLE TIQUET
(
	FechaHora DATETIME, /*yyyy-MM-dd hh:mm:ss*/
	IdShopping INTEGER PRIMARY KEY,
	IdUser INTEGER NOT NULL,
	TOTAL DECIMAL (60,2),

	CONSTRAINT FK_USER FOREIGN KEY (IdUser) REFERENCES USUARIO (IdUser)
);

INSERT INTO TIQUET
VALUES (NOW(),0,0,0);

ALTER TABLE TIQUET
CHANGE IdShopping IdShopping INTEGER AUTO_INCREMENT;

CREATE TABLE COMPRA
(
	IdShopping INTEGER,
	IdProduct INTEGER,
	Quantity	INTEGER,
	Total	DECIMAL(60,2),
	
	PRIMARY KEY(IdShopping, IdProduct),
	CONSTRAINT FK_PRODUCT FOREIGN KEY (IdProduct) REFERENCES PRODUCTO (IdProduct),
	CONSTRAINT FK_TIQUET FOREIGN KEY (IdShopping) REFERENCES TIQUET (IdShopping)
);

/*INSERCION DE DATOS*/
INSERT INTO COMPRA(IdShopping) VALUES(0);
INSERT INTO USUARIO(USERNAME,PASSWORD) VALUES('admin', 'admin');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto0', '61');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto1', '81');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto2', '56');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto3', '74');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto4', '25');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto5', '32');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto6', '14');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto7', '42');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto8', '71');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto9', '78');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto10', '83');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto11', '47');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto12', '77');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto13', '28');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto14', '22');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto15', '21');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto16', '75');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto17', '51');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto18', '41');
INSERT INTO PRODUCTO(DESCRIPTION,PRICE) VALUES('Producto19', '60');

