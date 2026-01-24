-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 23-01-2026 a las 23:35:30
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `idiomas_db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ejercicios`
--

CREATE TABLE `ejercicios` (
  `id` bigint(20) NOT NULL,
  `audio_url` varchar(255) DEFAULT NULL,
  `enunciado` varchar(500) NOT NULL,
  `imagen_url` varchar(255) DEFAULT NULL,
  `respuesta_correcta` varchar(255) NOT NULL,
  `tipo` enum('IMAGEN','LISTENING','PRONUNCIACION','SELECCION_MULTIPLE','TRADUCCION','VERDADERO_FALSO') DEFAULT NULL,
  `ronda_id` bigint(20) DEFAULT NULL,
  `contenido` varchar(255) DEFAULT NULL,
  `opciones` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `ejercicios`
--

INSERT INTO `ejercicios` (`id`, `audio_url`, `enunciado`, `imagen_url`, `respuesta_correcta`, `tipo`, `ronda_id`, `contenido`, `opciones`) VALUES
(54, NULL, 'Traducir la frase \"The roses is red, The jazmis is violet, you is for me six and  my for you nine', NULL, 'god', 'TRADUCCION', 27, '', ''),
(55, NULL, 'Que animal es el de la imagen', NULL, 'Cat', 'IMAGEN', 28, 'f4cf0d16-9984-425a-b9cb-d590b2234737-gato.jpg', 'Cat,Dog,Fish'),
(56, NULL, 'Traducir el audio', NULL, 'Whats is causa ga', 'LISTENING', 29, 'f39f9e7d-4241-4544-b91c-4acc8d4d5622-Y2meta.app - Que pasa causa !! Gaaa ! Vas a caer chupetin (128 kbps) (1).mp3', ''),
(57, NULL, 'Hellos is similar a Hi', NULL, 'V', 'VERDADERO_FALSO', 26, '', 'V,F'),
(58, NULL, 'Good Morning', NULL, 'Buenos Dias', 'TRADUCCION', 26, '', '');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ejercicio_opciones`
--

CREATE TABLE `ejercicio_opciones` (
  `ejercicio_id` bigint(20) NOT NULL,
  `opcion` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `progresos`
--

CREATE TABLE `progresos` (
  `id` bigint(20) NOT NULL,
  `fecha_realizacion` datetime(6) DEFAULT NULL,
  `puntaje` int(11) DEFAULT NULL,
  `estudiante_id` bigint(20) DEFAULT NULL,
  `ronda_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `progresos`
--

INSERT INTO `progresos` (`id`, `fecha_realizacion`, `puntaje`, `estudiante_id`, `ronda_id`) VALUES
(25, '2026-01-23 22:31:46.000000', 100, 3, 26),
(26, '2026-01-23 21:49:59.000000', 100, 3, 28),
(27, '2026-01-23 21:50:05.000000', 100, 3, 27),
(28, '2026-01-23 21:50:21.000000', 100, 3, 29);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rondas`
--

CREATE TABLE `rondas` (
  `id` bigint(20) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `nivel` varchar(255) DEFAULT NULL,
  `titulo` varchar(255) NOT NULL,
  `creador_id` bigint(20) DEFAULT NULL,
  `activo` bit(1) NOT NULL,
  `grado` varchar(255) DEFAULT NULL,
  `seccion` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `rondas`
--

INSERT INTO `rondas` (`id`, `descripcion`, `nivel`, `titulo`, `creador_id`, `activo`, `grado`, `seccion`) VALUES
(26, '', 'A1', 'Saludos', 2, b'1', '1ro', 'A'),
(27, '', 'B1', 'Traduccion', 2, b'0', '1ro', 'A'),
(28, '', 'A1', 'Animales', 2, b'0', '1ro', 'A'),
(29, '', 'B2', 'Pronunciacion', 2, b'0', '1ro', 'A');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','DOCENTE','ESTUDIANTE') NOT NULL,
  `username` varchar(255) NOT NULL,
  `grado` varchar(255) DEFAULT NULL,
  `seccion` varchar(255) DEFAULT NULL,
  `aulas` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `email`, `enabled`, `password`, `role`, `username`, `grado`, `seccion`, `aulas`) VALUES
(1, 'admin@duolingo.com', b'1', '$2a$10$VkPqSK21KPJSm/0N5nIa1em4FNuE7qOcrFbodX68LY2NxKAV14jBK', 'ADMIN', 'admin', NULL, NULL, NULL),
(2, 'juandocente@duolingo.com', b'1', '$2a$10$dL.5JEdSi8WOP4FsMT5OuetR3dGne19MQcVfK8ChZyirnndFPTYdm', 'DOCENTE', 'Juan', '', '', '1ro-A,1ro-B,1ro-C,4to-A,4to-B,4to-C'),
(3, 'joseestudiante@duolingo.com', b'1', '$2a$10$5o5tQONB3TBa1j0bNsIDpeesA4wHdvoeX/zPxEZpKJ6lac3BRP5X6', 'ESTUDIANTE', 'Jose', '1ro', 'A', NULL),
(4, 'mariaestu@duolingo.com', b'1', '$2a$10$u3icTXjrcnlegdxMBvGbjuSTLDIZevNkpUpx5.k0K63GU38o1n7Ou', 'ESTUDIANTE', 'Maria', '2do', 'B', NULL),
(5, 'adsasdas@gmail.com', b'1', '$2a$10$y28YRfnaLhZBP5ia.sQWq.rXih9nh.3lj8dzHA/ZeJ/Te1SUgGeZi', 'ESTUDIANTE', 'Cris', '1ro', 'B', NULL),
(6, '222170@unamba.edu.pe', b'1', '$2a$10$z8CmETLNn7TSC1NYEvmSeORq0LjaQrVNfNq7WHuQ/4HtaHulTHsj6', 'DOCENTE', 'Carlos', '2do', 'A', '2do-A,2do-B,2do-C'),
(7, '221151@unamba.edu.pe', b'1', '$2a$10$qkOI9uidJTzP8gTBZp.pFOsaN8kAD/3Tm6T5EZ/RETWRxvjEOQpcu', 'ESTUDIANTE', 'Juanito', '3ro', 'A', NULL);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `ejercicios`
--
ALTER TABLE `ejercicios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKmhgf7r1pmqoamoutvmmf1udx8` (`ronda_id`);

--
-- Indices de la tabla `ejercicio_opciones`
--
ALTER TABLE `ejercicio_opciones`
  ADD KEY `FK97qa8i3l8wkf4voham8w4dtxn` (`ejercicio_id`);

--
-- Indices de la tabla `progresos`
--
ALTER TABLE `progresos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK53pii8e5cu6h2vtdw4qj5a0je` (`estudiante_id`),
  ADD KEY `FKevcylrmitifvrw2n4f8a8rofu` (`ronda_id`);

--
-- Indices de la tabla `rondas`
--
ALTER TABLE `rondas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKg6clkbtvteaw5crb5bajsk9mt` (`creador_id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKm2dvbwfge291euvmk6vkkocao` (`username`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `ejercicios`
--
ALTER TABLE `ejercicios`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=59;

--
-- AUTO_INCREMENT de la tabla `progresos`
--
ALTER TABLE `progresos`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT de la tabla `rondas`
--
ALTER TABLE `rondas`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `ejercicios`
--
ALTER TABLE `ejercicios`
  ADD CONSTRAINT `FKmhgf7r1pmqoamoutvmmf1udx8` FOREIGN KEY (`ronda_id`) REFERENCES `rondas` (`id`);

--
-- Filtros para la tabla `ejercicio_opciones`
--
ALTER TABLE `ejercicio_opciones`
  ADD CONSTRAINT `FK97qa8i3l8wkf4voham8w4dtxn` FOREIGN KEY (`ejercicio_id`) REFERENCES `ejercicios` (`id`);

--
-- Filtros para la tabla `progresos`
--
ALTER TABLE `progresos`
  ADD CONSTRAINT `FK53pii8e5cu6h2vtdw4qj5a0je` FOREIGN KEY (`estudiante_id`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `FKevcylrmitifvrw2n4f8a8rofu` FOREIGN KEY (`ronda_id`) REFERENCES `rondas` (`id`);

--
-- Filtros para la tabla `rondas`
--
ALTER TABLE `rondas`
  ADD CONSTRAINT `FKg6clkbtvteaw5crb5bajsk9mt` FOREIGN KEY (`creador_id`) REFERENCES `usuarios` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
