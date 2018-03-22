CREATE database if NOT EXISTS `xxl-sso` default character set utf8 collate utf8_general_ci;
use `xxl-sso`;

CREATE TABLE `xxl_sso_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;