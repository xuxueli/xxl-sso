CREATE database if NOT EXISTS `xxl-sso` default character set utf8 collate utf8_general_ci;
use `xxl-sso`;


CREATE TABLE `xxl_sso_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(50) NOT NULL COMMENT '密码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `xxl_sso_user` VALUES ('1', 'user', '123456');
COMMIT;
