-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: gongguri
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chat_room`
--

DROP TABLE IF EXISTS `chat_room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_room` (
                             `chat_room_id` bigint NOT NULL AUTO_INCREMENT,
                             `created_at` datetime(6) DEFAULT NULL,
                             `updated_at` datetime(6) DEFAULT NULL,
                             PRIMARY KEY (`chat_room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_room`
--

LOCK TABLES `chat_room` WRITE;
/*!40000 ALTER TABLE `chat_room` DISABLE KEYS */;
INSERT INTO `chat_room` VALUES (8,'2025-05-23 00:43:50.431225','2025-05-23 00:43:50.431225'),(9,'2025-05-23 00:50:10.890327','2025-05-23 00:50:10.890327'),(10,'2025-05-23 00:52:19.092419','2025-05-23 00:52:19.092419'),(11,'2025-05-23 00:53:42.713670','2025-05-23 00:53:42.713670'),(12,'2025-05-23 00:53:56.113518','2025-05-23 00:53:56.113518'),(13,'2025-05-23 00:54:12.130081','2025-05-23 00:54:12.130081'),(14,'2025-05-23 00:54:28.609470','2025-05-23 00:54:28.609470'),(15,'2025-05-23 00:54:48.707129','2025-05-23 00:54:48.707129'),(16,'2025-05-23 00:55:38.164552','2025-05-23 00:55:38.164552'),(17,'2025-05-23 00:55:54.669843','2025-05-23 00:55:54.669843'),(18,'2025-05-23 00:56:18.653882','2025-05-23 00:56:18.653882'),(19,'2025-05-23 00:56:34.998327','2025-05-23 00:56:34.998327'),(20,'2025-05-23 00:56:57.929547','2025-05-23 00:56:57.929547'),(21,'2025-05-23 00:57:07.347600','2025-05-23 00:57:07.347600'),(22,'2025-05-23 00:57:19.107244','2025-05-23 00:57:19.107244'),(23,'2025-05-23 00:57:38.378540','2025-05-23 00:57:38.378540'),(24,'2025-05-23 00:57:55.787662','2025-05-23 00:57:55.787662'),(25,'2025-05-23 01:01:20.933741','2025-05-23 01:01:20.933741'),(26,'2025-05-23 01:02:06.533668','2025-05-23 01:02:06.533668'),(27,'2025-05-23 01:06:48.738657','2025-05-23 01:06:48.738657'),(28,'2025-05-23 01:07:42.894451','2025-05-23 01:07:42.894451'),(30,'2025-05-23 01:34:30.534228','2025-05-23 01:34:30.534228'),(31,'2025-05-23 11:09:41.514143','2025-05-23 11:09:41.514143');
/*!40000 ALTER TABLE `chat_room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_room_participation`
--

DROP TABLE IF EXISTS `chat_room_participation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_room_participation` (
                                           `chat_room_participant_id` bigint NOT NULL AUTO_INCREMENT,
                                           `chat_room_id` bigint NOT NULL,
                                           `member_id` bigint NOT NULL,
                                           `created_at` datetime(6) DEFAULT NULL,
                                           `updated_at` datetime(6) DEFAULT NULL,
                                           PRIMARY KEY (`chat_room_participant_id`),
                                           KEY `FKjk61br7mekj7wrxxmpabluwa4` (`chat_room_id`),
                                           KEY `FKk13wmrta0vcd8mrhlcljii87` (`member_id`),
                                           CONSTRAINT `FKjk61br7mekj7wrxxmpabluwa4` FOREIGN KEY (`chat_room_id`) REFERENCES `chat_room` (`chat_room_id`),
                                           CONSTRAINT `FKk13wmrta0vcd8mrhlcljii87` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_room_participation`
--

LOCK TABLES `chat_room_participation` WRITE;
/*!40000 ALTER TABLE `chat_room_participation` DISABLE KEYS */;
INSERT INTO `chat_room_participation` VALUES (9,10,1,'2025-05-23 00:52:19.097677','2025-05-23 00:52:19.097677'),(10,11,1,'2025-05-23 00:53:42.718948','2025-05-23 00:53:42.718948'),(11,12,1,'2025-05-23 00:53:56.118228','2025-05-23 00:53:56.118228'),(12,13,1,'2025-05-23 00:54:12.135376','2025-05-23 00:54:12.135376'),(13,14,1,'2025-05-23 00:54:28.614719','2025-05-23 00:54:28.614719'),(14,15,1,'2025-05-23 00:54:48.712886','2025-05-23 00:54:48.712886'),(15,16,1,'2025-05-23 00:55:38.176663','2025-05-23 00:55:38.176663'),(16,17,1,'2025-05-23 00:55:54.674573','2025-05-23 00:55:54.674573'),(17,18,1,'2025-05-23 00:56:18.659222','2025-05-23 00:56:18.659222'),(18,19,1,'2025-05-23 00:56:35.004119','2025-05-23 00:56:35.004119'),(19,20,1,'2025-05-23 00:56:57.940535','2025-05-23 00:56:57.940535'),(20,21,1,'2025-05-23 00:57:07.351801','2025-05-23 00:57:07.351801'),(21,22,1,'2025-05-23 00:57:19.112972','2025-05-23 00:57:19.112972'),(22,23,1,'2025-05-23 00:57:38.383798','2025-05-23 00:57:38.383798'),(23,24,1,'2025-05-23 00:57:55.791867','2025-05-23 00:57:55.791867'),(26,27,1,'2025-05-23 01:06:48.744464','2025-05-23 01:06:48.744464'),(27,28,1,'2025-05-23 01:07:42.899188','2025-05-23 01:07:42.899188'),(33,30,9,'2025-05-23 01:34:47.547423','2025-05-23 01:34:47.547423'),(34,30,10,'2025-05-23 01:35:39.538189','2025-05-23 01:35:39.538189'),(35,31,1,'2025-05-23 11:09:41.614656','2025-05-23 11:09:41.614656');
/*!40000 ALTER TABLE `chat_room_participation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_purchase`
--

DROP TABLE IF EXISTS `group_purchase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_purchase` (
                                  `group_id` bigint NOT NULL AUTO_INCREMENT,
                                  `account` varchar(255) NOT NULL,
                                  `bank` varchar(255) NOT NULL,
                                  `content` varchar(255) NOT NULL,
                                  `max_participants` int NOT NULL,
                                  `price` int NOT NULL,
                                  `progress_status` enum('CLOSED','COMPLETED','RECRUITING') NOT NULL,
                                  `title` varchar(255) NOT NULL,
                                  `chat_room_id` bigint NOT NULL,
                                  `member_id` bigint NOT NULL,
                                  `univ_id` bigint NOT NULL,
                                  `created_at` datetime(6) DEFAULT NULL,
                                  `updated_at` datetime(6) DEFAULT NULL,
                                  `image_url` varchar(255) DEFAULT NULL,
                                  `is_deleted` bit(1) NOT NULL,
                                  PRIMARY KEY (`group_id`),
                                  KEY `FKrna0kx7e7hf5fgvypf3gijekb` (`chat_room_id`),
                                  KEY `FKbe3xjf1igjpykc18fxt078f16` (`member_id`),
                                  KEY `FKe42gnupc0dx3uwc77923jqenh` (`univ_id`),
                                  CONSTRAINT `FKbe3xjf1igjpykc18fxt078f16` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
                                  CONSTRAINT `FKe42gnupc0dx3uwc77923jqenh` FOREIGN KEY (`univ_id`) REFERENCES `univ` (`univ_id`),
                                  CONSTRAINT `FKrna0kx7e7hf5fgvypf3gijekb` FOREIGN KEY (`chat_room_id`) REFERENCES `chat_room` (`chat_room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_purchase`
--

LOCK TABLES `group_purchase` WRITE;
/*!40000 ALTER TABLE `group_purchase` DISABLE KEYS */;
INSERT INTO `group_purchase` VALUES (10,'1111-1234-1234','우리','완료글 페이징 테스트용 데이터입니다.',2,12300,'COMPLETED','페이징 [완료글] 1',10,1,1,'2025-05-23 00:52:19.099300','2025-05-23 00:53:19.227664','/uploads/efd72b4d-1ebb-481e-812d-65b9eb4ff994_e7af66a3-7929-4c48-a81c-291ee9822996.png',_binary '\0'),(11,'12345','신한','aaaa',2,12345,'COMPLETED','페이징 [완료글] 2',11,1,1,'2025-05-23 00:53:42.721073','2025-05-23 00:53:42.721073','/uploads/22630c7e-7274-48b1-876d-639d337a0ea1_e7af66a3-7929-4c48-a81c-291ee9822996.png',_binary '\0'),(12,'22222','하나','asdf',2,12345,'COMPLETED','페이징 [완료글] 3',12,1,1,'2025-05-23 00:53:56.120366','2025-05-23 00:53:56.120366','/uploads/5837a0bf-c001-4d95-8735-5f5eb111d31c_e7af66a3-7929-4c48-a81c-291ee9822996.png',_binary '\0'),(13,'123455','국민','fff',2,12345,'COMPLETED','페이징 [완료글] 4',13,1,1,'2025-05-23 00:54:12.136959','2025-05-23 00:54:12.136959','/uploads/931410f4-36e4-40f4-8a49-b0b660fb2ec6_e7af66a3-7929-4c48-a81c-291ee9822996.png',_binary '\0'),(14,'3333333','국민','2345',2,123456,'COMPLETED','페이징 [완료글] 5',14,1,1,'2025-05-23 00:54:28.616300','2025-05-23 00:54:28.616300','/uploads/7f0c0749-c05a-4d6d-a73c-99714068082a_e7af66a3-7929-4c48-a81c-291ee9822996.png',_binary '\0'),(15,'12346','국민','asdf',2,12346,'COMPLETED','페이징 [완료글] 6',15,1,1,'2025-05-23 00:54:48.715007','2025-05-23 00:54:56.659342','/uploads/4100708d-2070-485b-9f26-23da43195467_e7af66a3-7929-4c48-a81c-291ee9822996.png',_binary '\0'),(16,'2222','국민','2222',2,12345,'COMPLETED','페이징 [완료글] 7',16,1,1,'2025-05-23 00:55:38.178779','2025-05-23 00:56:02.693271','/uploads/045d2de0-b283-4087-b3d0-248ee4c852f7_e7af66a3-7929-4c48-a81c-291ee9822996.png',_binary '\0'),(17,'2222','국민','2222',2,12345,'COMPLETED','페이징 [완료글] 8',17,1,1,'2025-05-23 00:55:54.676720','2025-05-23 00:55:54.676720','/uploads/c4e93199-1200-409d-bb99-79ca86b958b5_e7af66a3-7929-4c48-a81c-291ee9822996.png',_binary '\0'),(18,'2222','신한','2222',1,123456,'COMPLETED','페이징 [완료글] 9',18,1,1,'2025-05-23 00:56:18.662993','2025-05-23 00:56:18.662993','/uploads/0ce37880-d204-4fbe-b7d1-8bc08c84908b_e7af66a3-7929-4c48-a81c-291ee9822996.png',_binary '\0'),(19,'2222','신한','2222',2,12345,'COMPLETED','페이징 [완료글] 10',19,1,1,'2025-05-23 00:56:35.005699','2025-05-23 00:56:48.179478','/uploads/d6f537b5-9777-493a-9d30-cc59e79d3c46_e7af66a3-7929-4c48-a81c-291ee9822996.png',_binary '\0'),(20,'2222','신한','2222',1,12345,'COMPLETED','페이징 [완료글] 11',20,1,1,'2025-05-23 00:56:57.940535','2025-05-23 00:56:57.940535','/uploads/c20ec450-6f8b-4883-a796-8949ab15e157_e7af66a3-7929-4c48-a81c-291ee9822996.png',_binary '\0'),(21,'2222','신한','2222',1,12345,'COMPLETED','페이징 [완료글] 12',21,1,1,'2025-05-23 00:57:07.353355','2025-05-23 00:57:07.353355','/uploads/d51e5840-ba27-4257-b45e-2f17b6a5ba3d_e7af66a3-7929-4c48-a81c-291ee9822996.png',_binary '\0'),(22,'2222','신한','2222',1,12345,'COMPLETED','페이징 [완료글] 13',22,1,1,'2025-05-23 00:57:19.115105','2025-05-23 00:57:19.115105','/uploads/d5148ed9-a053-46a0-b255-34ff4d03e0af_e7af66a3-7929-4c48-a81c-291ee9822996.png',_binary '\0'),(23,'2222','국민','2222',1,123456,'COMPLETED','페이징 [완료글] 14',23,1,1,'2025-05-23 00:57:38.385986','2025-05-23 00:57:38.385986','/uploads/6b871f5f-21e5-422d-9915-2e02157df758_e7af66a3-7929-4c48-a81c-291ee9822996.png',_binary '\0'),(24,'2222','신한','2222',1,12345,'COMPLETED','페이징 [완료글] 15',24,1,1,'2025-05-23 00:57:55.793971','2025-05-23 00:57:55.793971','/uploads/0321b926-2281-4a33-86de-3cfcd62fdded_e7af66a3-7929-4c48-a81c-291ee9822996.png',_binary '\0'),(27,'1111-2222-3333','신한','최소인원 20명 필요합니다!\n5월 30일까지 신청 받을게요.',20,45000,'RECRUITING','학잠 공구하실분!',27,1,1,'2025-05-23 01:06:48.746069','2025-05-23 01:06:48.746069','/uploads/ef4b513e-e33c-4487-82a7-44b857bd1e5f_학잠.jpg',_binary '\0'),(28,'1111-2222-3333','신한','30롤짜리 반 나누실분 구합니다!',2,8000,'COMPLETED','휴지 15롤씩 나누실분!',28,1,1,'2025-05-23 01:07:42.900763','2025-05-23 01:19:29.286027','/uploads/8e41a477-a427-4139-ac17-710cc6f788e2_잘풀리는집.jpg',_binary '\0'),(30,'1111-2222-3333','신한','퍼실 파워젤 행사상품 1개씩 나누실 분 구합니다!',4,8000,'COMPLETED','퍼실 파워젤 3+1',30,1,1,'2025-05-23 01:34:30.541693','2025-05-23 11:12:58.439056','/uploads/a36fd2e8-61b6-4941-aece-fbca4ac7e033_세제.jpg',_binary '\0'),(31,'4444-4444-444','신한','테스트중입니다',4,12345,'RECRUITING','테스트입니다',31,1,1,'2025-05-23 11:09:41.647355','2025-05-23 11:09:41.647355','/uploads/56f19f2b-eb62-452d-8c32-491a6c74a700_5c3519fc9803776ed2bd917a87503954.jpg',_binary '\0');
/*!40000 ALTER TABLE `group_purchase` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_purchase_participant`
--

DROP TABLE IF EXISTS `group_purchase_participant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_purchase_participant` (
                                              `group_participant_id` bigint NOT NULL AUTO_INCREMENT,
                                              `participation_status` enum('CANCELLED','JOINED') NOT NULL,
                                              `group_id` bigint NOT NULL,
                                              `member_id` bigint NOT NULL,
                                              `created_at` datetime(6) DEFAULT NULL,
                                              `updated_at` datetime(6) DEFAULT NULL,
                                              `deposit` bit(1) DEFAULT NULL,
                                              PRIMARY KEY (`group_participant_id`),
                                              KEY `FK13fpc473bdtkw4fwxu00g113i` (`group_id`),
                                              KEY `FKn7ikjn0ig5otd3pobp5iqmus8` (`member_id`),
                                              CONSTRAINT `FK13fpc473bdtkw4fwxu00g113i` FOREIGN KEY (`group_id`) REFERENCES `group_purchase` (`group_id`),
                                              CONSTRAINT `FKn7ikjn0ig5otd3pobp5iqmus8` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_purchase_participant`
--

LOCK TABLES `group_purchase_participant` WRITE;
/*!40000 ALTER TABLE `group_purchase_participant` DISABLE KEYS */;
INSERT INTO `group_purchase_participant` VALUES (12,'JOINED',10,1,'2025-05-23 00:52:19.101418','2025-05-23 00:52:19.101418',_binary '\0'),(13,'JOINED',11,1,'2025-05-23 00:53:42.723720','2025-05-23 00:53:42.723720',_binary '\0'),(14,'JOINED',12,1,'2025-05-23 00:53:56.122482','2025-05-23 00:53:56.122482',_binary '\0'),(15,'JOINED',13,1,'2025-05-23 00:54:12.139629','2025-05-23 00:54:12.139629',_binary '\0'),(16,'JOINED',14,1,'2025-05-23 00:54:28.618931','2025-05-23 00:54:28.618931',_binary '\0'),(17,'JOINED',15,1,'2025-05-23 00:54:48.717123','2025-05-23 00:54:48.717123',_binary '\0'),(18,'JOINED',16,1,'2025-05-23 00:55:38.180885','2025-05-23 00:55:38.180885',_binary '\0'),(19,'JOINED',17,1,'2025-05-23 00:55:54.678815','2025-05-23 00:55:54.678815',_binary '\0'),(20,'JOINED',18,1,'2025-05-23 00:56:18.666217','2025-05-23 00:56:18.666217',_binary '\0'),(21,'JOINED',19,1,'2025-05-23 00:56:35.007801','2025-05-23 00:56:35.007801',_binary '\0'),(22,'JOINED',20,1,'2025-05-23 00:56:57.943927','2025-05-23 00:56:57.943927',_binary '\0'),(23,'JOINED',21,1,'2025-05-23 00:57:07.355461','2025-05-23 00:57:07.355461',_binary '\0'),(24,'JOINED',22,1,'2025-05-23 00:57:19.116536','2025-05-23 00:57:19.116536',_binary '\0'),(25,'JOINED',23,1,'2025-05-23 00:57:38.387567','2025-05-23 00:57:38.387567',_binary '\0'),(26,'JOINED',24,1,'2025-05-23 00:57:55.796601','2025-05-23 00:57:55.796601',_binary '\0'),(29,'JOINED',27,1,'2025-05-23 01:06:48.748632','2025-05-23 01:06:48.748632',_binary '\0'),(30,'JOINED',28,1,'2025-05-23 01:07:42.902343','2025-05-23 01:07:42.902343',_binary '\0'),(35,'JOINED',30,1,'2025-05-23 01:34:30.543839','2025-05-23 01:34:30.543839',_binary '\0'),(36,'JOINED',30,9,'2025-05-23 01:34:47.543216','2025-05-23 01:35:12.774253',_binary ''),(37,'CANCELLED',30,10,'2025-05-23 01:35:39.534011','2025-05-23 11:12:44.072640',_binary '\0'),(38,'JOINED',31,1,'2025-05-23 11:09:41.672439','2025-05-23 11:09:41.672439',_binary '\0');
/*!40000 ALTER TABLE `group_purchase_participant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member`
--

DROP TABLE IF EXISTS `member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member` (
                          `member_id` bigint NOT NULL AUTO_INCREMENT,
                          `dislike_count` int NOT NULL,
                          `email` varchar(255) NOT NULL,
                          `like_count` int NOT NULL,
                          `nickname` varchar(255) NOT NULL,
                          `password` varchar(255) NOT NULL,
                          `univ_id` bigint NOT NULL,
                          `created_at` datetime(6) DEFAULT NULL,
                          `updated_at` datetime(6) DEFAULT NULL,
                          PRIMARY KEY (`member_id`),
                          UNIQUE KEY `UKhh9kg6jti4n1eoiertn2k6qsc` (`nickname`),
                          KEY `FK1vi83o1cktv20wyplbpqxs040` (`univ_id`),
                          CONSTRAINT `FK1vi83o1cktv20wyplbpqxs040` FOREIGN KEY (`univ_id`) REFERENCES `univ` (`univ_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member`
--

LOCK TABLES `member` WRITE;
/*!40000 ALTER TABLE `member` DISABLE KEYS */;
INSERT INTO `member` VALUES (1,0,'gmh8257@gmail.com',1,'김데브','$2a$10$F84SKM2So8OvLUzh576pX.zdBsdHBNqjF.pfIKDsEsMhHytsUmazS',1,'2025-05-23 00:39:43.000000','2025-05-23 11:13:19.426502'),(9,0,'gmh82577@gmail.com',0,'이데브','$2a$10$F84SKM2So8OvLUzh576pX.zdBsdHBNqjF.pfIKDsEsMhHytsUmazS',1,'2025-05-23 00:39:43.000000','2025-05-23 00:39:44.000000'),(10,0,'gmh825777@gmail.com',0,'박데브','$2a$10$F84SKM2So8OvLUzh576pX.zdBsdHBNqjF.pfIKDsEsMhHytsUmazS',1,'2025-05-23 01:15:52.000000','2025-05-23 01:15:55.000000'),(17,0,'jmh8257@catholic.ac.kr',0,'지데브','$2a$10$BlmoFlOGgBu8o3p3gJ5Mwe2ALZMRNuqrKtfPfdgOQ7R1TGJeaKZ2C',3,'2025-06-12 16:52:22.426566','2025-06-12 16:52:22.426566');
/*!40000 ALTER TABLE `member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
                          `review_id` bigint NOT NULL AUTO_INCREMENT,
                          `liked` bit(1) NOT NULL,
                          `group_id` bigint NOT NULL,
                          `member_id` bigint NOT NULL,
                          `created_at` datetime(6) DEFAULT NULL,
                          `updated_at` datetime(6) DEFAULT NULL,
                          PRIMARY KEY (`review_id`),
                          KEY `FK4ahseun0am6dkyhp0624rwy2g` (`group_id`),
                          KEY `FKk0ccx5i4ci2wd70vegug074w1` (`member_id`),
                          CONSTRAINT `FK4ahseun0am6dkyhp0624rwy2g` FOREIGN KEY (`group_id`) REFERENCES `group_purchase` (`group_id`),
                          CONSTRAINT `FKk0ccx5i4ci2wd70vegug074w1` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `univ`
--

DROP TABLE IF EXISTS `univ`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `univ` (
                        `univ_id` bigint NOT NULL AUTO_INCREMENT,
                        `univ_name` varchar(255) NOT NULL,
                        `created_at` datetime(6) DEFAULT NULL,
                        `updated_at` datetime(6) DEFAULT NULL,
                        PRIMARY KEY (`univ_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `univ`
--

LOCK TABLES `univ` WRITE;
/*!40000 ALTER TABLE `univ` DISABLE KEYS */;
INSERT INTO `univ` VALUES (1,'데브대학',NULL,NULL),(3,'가톨릭대학교','2025-06-11 17:01:25.406203','2025-06-11 17:01:25.406203');
/*!40000 ALTER TABLE `univ` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'gongguri'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-16 14:16:44