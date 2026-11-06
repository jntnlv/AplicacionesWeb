/*
  Script de creación de la base de datos de AulaLink.
  Ejecutarlo una vez, con un usuario administrador de MySQL.
*/
drop database if exists aulalink;
drop user if exists 'usuario_prueba'@'%';

create database aulalink
  default character set utf8mb4
  default collate utf8mb4_unicode_ci;

create user 'usuario_prueba'@'%' identified by 'Usuar1o_Clave.';
create user 'usuario_prueba'@'localhost' identified by 'Usuar1o_Clave.';
grant select, insert, update, delete on aulalink.* to 'usuario_prueba'@'%';
grant select, insert, update, delete on aulalink.* to 'usuario_prueba'@'localhost';
flush privileges;

use aulalink;

create table usuario (
  id_usuario int not null auto_increment,
  nombre_completo varchar(120) not null,
  correo varchar(120) not null,
  contrasena varchar(512) not null,
  rol varchar(20) not null,
  activo boolean not null default true,
  primary key (id_usuario),
  unique (correo),
  constraint ck_usuario_rol check (rol in ('ADMIN', 'TUTOR', 'ESTUDIANTE'))
) engine = InnoDB;

create table materia (
  id_materia int not null auto_increment,
  nombre varchar(80) not null,
  primary key (id_materia),
  unique (nombre)
) engine = InnoDB;

create table tutor_materia (
  id_tutor_materia int not null auto_increment,
  id_tutor int not null,
  id_materia int not null,
  primary key (id_tutor_materia),
  unique (id_tutor, id_materia),
  constraint fk_tutor_materia_tutor foreign key (id_tutor) references usuario (id_usuario),
  constraint fk_tutor_materia_materia foreign key (id_materia) references materia (id_materia)
) engine = InnoDB;

create table disponibilidad (
  id_disponibilidad int not null auto_increment,
  id_tutor int not null,
  fecha date not null,
  hora time not null,
  estado_horario varchar(20) not null default 'DISPONIBLE',
  primary key (id_disponibilidad),
  unique (id_tutor, fecha, hora),
  constraint ck_disponibilidad_estado check (estado_horario in ('DISPONIBLE', 'OCUPADO')),
  constraint fk_disponibilidad_tutor foreign key (id_tutor) references usuario (id_usuario)
) engine = InnoDB;

create table tutoria (
  id_tutoria int not null auto_increment,
  id_estudiante int not null,
  id_tutor int not null,
  id_materia int not null,
  id_disponibilidad int not null,
  estado_tutoria varchar(20) not null default 'SOLICITADA',
  calificacion int null,
  comentario text null,
  primary key (id_tutoria),
  constraint ck_tutoria_estado check (estado_tutoria in ('SOLICITADA', 'ACEPTADA', 'RECHAZADA', 'REALIZADA', 'CANCELADA')),
  constraint ck_tutoria_calificacion check (calificacion is null or calificacion between 1 and 5),
  constraint fk_tutoria_estudiante foreign key (id_estudiante) references usuario (id_usuario),
  constraint fk_tutoria_tutor foreign key (id_tutor) references usuario (id_usuario),
  constraint fk_tutoria_materia foreign key (id_materia) references materia (id_materia),
  constraint fk_tutoria_disponibilidad foreign key (id_disponibilidad) references disponibilidad (id_disponibilidad)
) engine = InnoDB;

insert into materia (nombre) values
  ('Cálculo I'),
  ('Programación I'),
  ('Bases de Datos');

insert into usuario (nombre_completo, correo, contrasena, rol, activo) values
  ('Administrador', 'admin@aulalink.com', '$2a$10$gB/8Niz2B5j89xP80nvRuOlt68uMWXeX69mG0YRRU4gRmDZeCz/6O', 'ADMIN', true);
