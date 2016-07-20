CREATE TABLE `Configuration` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `apiPassword` varchar(100) DEFAULT NULL,
  `apiURL` varchar(200) DEFAULT NULL,
  `apiUser` varchar(100) DEFAULT NULL,
  `managerBinLocation` varchar(100) DEFAULT NULL,
  `managerCommand` varchar(100) DEFAULT NULL,
  `managerIp` varchar(50) DEFAULT NULL,
  `managerKeyLocation` varchar(100) DEFAULT NULL,
  `managerUser` varchar(100) DEFAULT NULL,
  `trustStore` varchar(255) DEFAULT NULL,
  `trustStorePassword` varchar(20) DEFAULT NULL,
  `validateCertificate` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
CREATE TABLE `DatabaseConnection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `creation_date` datetime NOT NULL,
  `modified_date` datetime NOT NULL,
  `destination_connection` varchar(20) NOT NULL,
  `origin_connection` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `DatabaseIQN` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `creation_date` datetime NOT NULL,
  `modified_date` datetime NOT NULL,
  `destination_iqn` varchar(255) NOT NULL,
  `origin_iqn` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `DisasterRecoveryOperation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `creation_date` datetime NOT NULL,
  `modified_date` datetime NOT NULL,
  `status` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `OperationLog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` datetime DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `operation_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKF9745C7D67F8D0C0` (`operation_id`),
  CONSTRAINT `FKF9745C7D67F8D0C0` FOREIGN KEY (`operation_id`) REFERENCES `DisasterRecoveryOperation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `UserRole` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` int(11) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
CREATE TABLE `RemoteHost` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `creation_date` datetime NOT NULL,
  `modified_date` datetime NOT NULL,
  `host_name` varchar(255) NOT NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `User` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `creation_date` datetime NOT NULL,
  `modified_date` datetime NOT NULL,
  `firstName` varchar(45) NOT NULL,
  `last_access` datetime DEFAULT NULL,
  `last_activity` datetime DEFAULT NULL,
  `lastName` varchar(45) NOT NULL,
  `needsPasswordReset` bit(1) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(45) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `FK285FEBDDA99B89` (`role_id`),
  CONSTRAINT `FK285FEBDDA99B89` FOREIGN KEY (`role_id`) REFERENCES `UserRole` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
INSERT INTO `UserRole` (`id`, `code`, `description`, `name`) VALUES (1,0,'Administrator User','Administrator'),(2,1,'Only perform DR process','Operator');
INSERT INTO `User` (`id`, `active`, `creation_date`, `modified_date`, `firstName`, `last_access`, `last_activity`, `lastName`, `needsPasswordReset`, `password`, `username`, `role_id`) VALUES (1,'','2016-04-27 17:00:04','2016-04-27 23:04:20','Master','2016-04-27 21:28:28','2016-04-27 23:04:20','Admin','\0','579f9df5aa41734eb64e6a8b2e2004dd','admin',1);
INSERT INTO `Configuration` (`id`, `apiPassword`, `apiURL`, `apiUser`, `managerBinLocation`, `managerCommand`, `managerIp`, `managerKeyLocation`, `managerUser`, `trustStore`, `trustStorePassword`, `validateCertificate`) VALUES (1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'\0');
