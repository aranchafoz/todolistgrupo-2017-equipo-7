/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

ALTER TABLE `Tarea` ADD
  `deleted_at` date DEFAULT NULL;
  
CREATE TABLE `Etiqueta` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `tableroId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqhvnrs00mpblhwhp2ondwu2vo` (`tableroId`),
  CONSTRAINT `FKqhvnrs00mpblhwhp2ondwu2vo` FOREIGN KEY (`tableroId`) REFERENCES `Tablero` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `Tarea_Etiqueta` (
  `tareas_id` bigint(20) NOT NULL,
  `etiquetas_id` bigint(20) NOT NULL,
  PRIMARY KEY (`tareas_id`,`etiquetas_id`),
  KEY `FKlkb4xdc8vxt63t1ihylcc36s6` (`etiquetas_id`),
  CONSTRAINT `FKlkb4xdc8vxt63t1ihylcc36s6` FOREIGN KEY (`etiquetas_id`) REFERENCES `Etiqueta` (`id`),
  CONSTRAINT `FKqjbjoqqylxa92bunkrndg0nx7` FOREIGN KEY (`tareas_id`) REFERENCES `Tarea` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
