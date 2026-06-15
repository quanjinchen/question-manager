/*
SQLyog Community v13.3.1 (64 bit)
MySQL - 8.1.0 : Database - spring-scaffold
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`db_small_auth_platform` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `db_small_auth_platform`;

/*Table structure for table `tb_app` */

DROP TABLE IF EXISTS `tb_app`;

CREATE TABLE `tb_app` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `app_name` varchar(128) NOT NULL COMMENT '应用名称',
  `app_code` varchar(64) NOT NULL COMMENT '应用编码',
  `client_id` varchar(128) NOT NULL COMMENT '客户端 ID',
  `client_secret` varchar(255) NOT NULL COMMENT '客户端密钥',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_by` bigint DEFAULT NULL COMMENT '创建人 ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人 ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间并在更新时自动刷新',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记，0 正常，1 删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用表';

/*Data for the table `tb_app` */


DROP TABLE IF EXISTS `tb_face_auth_log`;

CREATE TABLE `tb_face_auth_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `auth_api_type` tinyint NOT NULL DEFAULT '1' COMMENT '认证接口类型，1 表示 1:1，2 表示 1:N',
  `ip` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求 IP',
  `app_id` bigint DEFAULT NULL COMMENT '应用 ID',
  `app_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '应用名称',
  `auth_full_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '认证人员姓名',
  `auth_user_id` bigint DEFAULT NULL COMMENT '认证人员 ID',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态，0 失败 1 成功',
  `errmsg` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '失败原因',
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='人脸认证日志表';



/*Table structure for table `tb_file_record` */

DROP TABLE IF EXISTS `tb_file_record`;

CREATE TABLE `tb_file_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `file_id` varchar(100) DEFAULT NULL COMMENT '文件业务 ID',
  `file_name` varchar(100) DEFAULT NULL COMMENT '文件名称',
  `file_category` varchar(32) NOT NULL DEFAULT 'COMMON' COMMENT '文件分类',
  `object_name` varchar(200) DEFAULT NULL COMMENT '对象存储中的对象名',
  `content_type` varchar(100) DEFAULT NULL COMMENT '文件内容类型',
  `file_suffix` varchar(100) DEFAULT NULL COMMENT '文件后缀',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小，单位字节',
  `create_by` bigint DEFAULT NULL COMMENT '创建人 ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人 ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间并在更新时自动刷新',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记，0 正常，1 删除',
  PRIMARY KEY (`id`),
  KEY `idx_tb_file_record_file_id_deleted` (`file_id`,`deleted`),
  KEY `idx_tb_file_record_object_name_deleted` (`object_name`,`deleted`),
  KEY `idx_tb_file_record_create_time_deleted` (`create_time`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件记录表';



DROP TABLE IF EXISTS `tb_operation_log`;

CREATE TABLE `tb_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `module_name` varchar(64) NOT NULL COMMENT '模块名称',
  `action_name` varchar(64) NOT NULL COMMENT '操作名称',
  `operator_name` varchar(64) DEFAULT NULL COMMENT '操作人名称',
  `request_path` varchar(255) DEFAULT NULL COMMENT '请求路径',
  `success_flag` tinyint NOT NULL DEFAULT '1' COMMENT '是否成功，1 成功，0 失败',
  `request_time` datetime NOT NULL COMMENT '请求时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人 ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人 ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间并在更新时自动刷新',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记，0 正常，1 删除',
  PRIMARY KEY (`id`),
  KEY `idx_tb_operation_log_request_time_deleted` (`request_time`,`deleted`),
  KEY `idx_tb_operation_log_module_name_deleted` (`module_name`,`deleted`),
  KEY `idx_tb_operation_log_operator_name_deleted` (`operator_name`,`deleted`),
  KEY `idx_tb_operation_log_success_flag_deleted` (`success_flag`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=170 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';



/*Table structure for table `tb_org` */

DROP TABLE IF EXISTS `tb_org`;

CREATE TABLE `tb_org` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父组织 ID，0 表示根节点',
  `org_code` varchar(64) NOT NULL COMMENT '组织编码',
  `name` varchar(128) NOT NULL COMMENT '组织名称',
  `leader_name` varchar(64) DEFAULT NULL COMMENT '负责人名称',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序值',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态，1 启用，0 禁用',
  `create_by` bigint DEFAULT NULL COMMENT '创建人 ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人 ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间并在更新时自动刷新',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记，0 正常，1 删除',
  PRIMARY KEY (`id`),
  KEY `idx_tb_org_parent_id_deleted` (`parent_id`,`deleted`),
  KEY `idx_tb_org_org_code_deleted` (`org_code`,`deleted`),
  KEY `idx_tb_org_status_deleted` (`status`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组织表';


/*Table structure for table `tb_org_user` */

DROP TABLE IF EXISTS `tb_org_user`;

CREATE TABLE `tb_org_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `org_id` bigint NOT NULL COMMENT '组织 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建人 ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人 ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间并在更新时自动刷新',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记，0 正常，1 删除',
  PRIMARY KEY (`id`),
  KEY `idx_tb_org_user_org_id_deleted` (`org_id`,`deleted`),
  KEY `idx_tb_org_user_user_id_deleted` (`user_id`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组织用户关联表';



/*Table structure for table `tb_sys_menu` */

DROP TABLE IF EXISTS `tb_sys_menu`;

CREATE TABLE `tb_sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父菜单 ID，0 表示根节点',
  `menu_name` varchar(128) NOT NULL COMMENT '菜单名称',
  `path` varchar(255) DEFAULT NULL COMMENT '菜单路由路径',
  `menu_type` varchar(32) NOT NULL COMMENT '菜单类型',
  `menu_code` varchar(128) DEFAULT NULL COMMENT '权限编码',
  `order_num` int NOT NULL DEFAULT '0' COMMENT '排序值',
  `visible` tinyint NOT NULL DEFAULT '1' COMMENT '是否可见，1 可见，0 不可见',
  `create_by` bigint DEFAULT NULL COMMENT '创建人 ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人 ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间并在更新时自动刷新',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记，0 正常，1 删除',
  `icon` varchar(64) DEFAULT NULL COMMENT '图标',
  PRIMARY KEY (`id`),
  KEY `idx_tb_sys_menu_parent_id_deleted` (`parent_id`,`deleted`),
  KEY `idx_tb_sys_menu_permission_code_deleted` (`menu_code`,`deleted`),
  KEY `idx_tb_sys_menu_menu_type_deleted` (`menu_type`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=125 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

/*Data for the table `tb_sys_menu` */

insert  into `tb_sys_menu`(`id`,`parent_id`,`menu_name`,`path`,`menu_type`,`menu_code`,`order_num`,`visible`,`create_by`,`create_time`,`update_by`,`update_time`,`deleted`,`icon`) values 
(1,0,'系统管理','/system','MENU','1',5,1,1,'2026-04-16 14:50:35',1,'2026-04-16 14:50:35',0,'Setting'),
(2,1,'菜单管理','/system/menu','MENU','system:menu:query',10,1,1,'2026-04-16 14:50:35',1,'2026-04-16 14:50:35',0,NULL),
(3,0,'用户管理','/user','MENU','system:user:query',2,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:45:04',0,'UserFilled'),
(4,1,'角色管理','/system/role','MENU','system:role:query',30,1,1,'2026-04-16 14:50:35',1,'2026-04-16 14:50:35',0,NULL),
(5,1,'组织管理','/organization','MENU','system:org:query',40,1,1,'2026-04-16 14:50:35',1,'2026-05-14 12:29:36',1,NULL),
(6,124,'操作日志','/system/operation-log','MENU','system:operationLog:query',2,1,1,'2026-04-16 14:50:35',1,'2026-04-16 14:50:35',0,''),
(7,1,'文件上传','/system/file/upload','BTN','system:file:upload',60,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:44:28',0,NULL),
(8,1,'文件下载','/system/file/download','BTN','system:file:download',70,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:44:28',0,NULL),
(9,1,'文件删除','/system/file/delete','BTN','system:file:delete',80,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:44:28',0,NULL),
(10,3,'用户保存','/system/user/save','BTN','system:user:update',90,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:44:28',0,NULL),
(11,3,'重置密码','/system/user/reset-password','BTN','system:user:resetPassword',100,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:44:28',0,NULL),
(12,4,'角色保存','/system/role/save','BTN','system:role:update',110,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:44:28',0,NULL),
(13,5,'组织保存','/system/org/save','BTN','system:org:update',120,1,1,'2026-04-16 14:50:35',1,'2026-05-14 12:29:29',1,NULL),
(14,2,'菜单保存','/system/menu/save','BTN','system:menu:update',130,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:44:28',0,NULL),
(100,0,'首页','/index/baseInfo','MENU','system:index:baseInfo',1,1,1,'2026-04-16 14:50:35',1,'2026-04-16 14:50:35',0,'HomeFilled'),
(101,100,'基础信息','/index/baseInfo','PAGE','system:index:baseInfo',10,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:41:24',1,''),
(102,101,'用户总数','/index/baseInfo/userNum','BTN','system:index:userNum',11,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:44:28',1,NULL),
(103,101,'活跃用户','/index/baseInfo/userActive','BTN','system:index:userActive',12,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:44:28',1,NULL),
(104,101,'应用排行','/index/baseInfo/appRank','BTN','system:index:appRank',13,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:44:28',1,NULL),
(105,101,'设备统计','/index/baseInfo/userDevice','MENU','system:index:userDevice',14,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:41:01',1,'BrushFilled'),
(106,1,'用户新增','/system/user/create','BTN','system:user:add',21,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:44:28',0,NULL),
(107,3,'用户删除','/system/user/delete','BTN','system:user:delete',22,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:44:28',0,NULL),
(108,5,'组织新增','/system/org/create','BTN','system:org:add',41,1,1,'2026-04-16 14:50:35',1,'2026-05-14 12:29:34',1,NULL),
(109,5,'组织删除','/system/org/delete','BTN','system:org:delete',42,1,1,'2026-04-16 14:50:35',1,'2026-05-14 12:29:32',1,NULL),
(118,2,'菜单删除','/system/menu/delete','BTN','system:menu:delete',121,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:44:28',0,NULL),
(119,4,'角色删除','/system/role/delete','BTN','system:role:delete',111,1,1,'2026-04-16 14:50:35',1,'2026-05-12 16:44:28',0,NULL),
(120,0,'应用管理','/application','MENU','system:app:query',3,0,1,'2026-05-12 17:34:00',1,'2026-05-12 19:09:22',0,'Grid'),
(121,120,'应用保存','/application/save','BTN','system:app:update',10,0,1,'2026-05-12 19:09:22',1,'2026-05-12 19:09:22',0,''),
(122,120,'应用删除','/application/delete','BTN','system:app:delete',20,0,1,'2026-05-12 19:09:22',1,'2026-05-12 19:09:22',0,''),
(123,124,'认证日志','/system/face-auth-log','MENU','system:operationLog:query',1,1,1,'2026-05-13 16:53:42',1,'2026-05-13 16:53:42',0,''),
(124,0,'日志管理','/log','DIR','log',4,1,1,'2026-05-13 17:49:40',1,'2026-05-13 17:49:40',0,'Document');

/*Table structure for table `tb_sys_role` */

DROP TABLE IF EXISTS `tb_sys_role`;

CREATE TABLE `tb_sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `code` varchar(64) NOT NULL COMMENT '角色编码',
  `name` varchar(128) NOT NULL COMMENT '角色名称',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态，1 启用，0 禁用',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_by` bigint DEFAULT NULL COMMENT '创建人 ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人 ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间并在更新时自动刷新',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记，0 正常，1 删除',
  PRIMARY KEY (`id`),
  KEY `idx_tb_sys_role_code_deleted` (`code`,`deleted`),
  KEY `idx_tb_sys_role_name_deleted` (`name`,`deleted`),
  KEY `idx_tb_sys_role_status_deleted` (`status`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

/*Data for the table `tb_sys_role` */

insert  into `tb_sys_role`(`id`,`code`,`name`,`status`,`remark`,`create_by`,`create_time`,`update_by`,`update_time`,`deleted`) values 
(1,'ADMIN','系统管理员',1,'系统初始化管理员角色',1,'2026-04-16 14:50:35',1,'2026-04-16 14:50:35',0),
(2,'SUPER_ADMIN','超级管理员',1,'系统超级管理员角色',1,'2026-04-16 14:50:35',1,'2026-04-16 14:50:35',0);

/*Table structure for table `tb_sys_role_menu` */

DROP TABLE IF EXISTS `tb_sys_role_menu`;

CREATE TABLE `tb_sys_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `role_id` bigint NOT NULL COMMENT '角色 ID',
  `menu_id` bigint NOT NULL COMMENT '菜单 ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建人 ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人 ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间并在更新时自动刷新',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记，0 正常，1 删除',
  PRIMARY KEY (`id`),
  KEY `idx_tb_sys_role_menu_role_id_deleted` (`role_id`,`deleted`),
  KEY `idx_tb_sys_role_menu_menu_id_deleted` (`menu_id`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=138 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

/*Data for the table `tb_sys_role_menu` */

insert  into `tb_sys_role_menu`(`id`,`role_id`,`menu_id`,`create_by`,`create_time`,`update_by`,`update_time`,`deleted`) values 
(1,1,1,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:07',1),
(2,1,2,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:07',1),
(3,1,3,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:07',1),
(4,1,4,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:07',1),
(5,1,5,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:07',1),
(6,1,6,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:07',1),
(7,1,7,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:07',1),
(8,1,8,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:07',1),
(9,1,9,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:07',1),
(10,1,10,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:07',1),
(11,1,11,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:07',1),
(12,1,12,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:07',1),
(13,1,13,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:07',1),
(14,1,14,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:07',1),
(15,1,118,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:07',1),
(16,1,119,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:07',1),
(17,2,1,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(18,2,2,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(19,2,3,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(20,2,4,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(21,2,5,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(22,2,6,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(23,2,7,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(24,2,8,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(25,2,9,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(26,2,10,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(27,2,11,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(28,2,12,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(29,2,13,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(30,2,14,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(31,2,100,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(32,2,101,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(33,2,102,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(34,2,103,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(35,2,104,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(36,2,105,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(37,2,106,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(38,2,107,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(39,2,108,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(40,2,109,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(41,2,118,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(42,2,119,1,'2026-04-16 14:50:35',1,'2026-04-16 15:16:12',1),
(43,1,1,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(44,1,2,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(45,1,118,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(46,1,14,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(47,1,3,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(48,1,107,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(49,1,10,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(50,1,11,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(51,1,106,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(52,1,4,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(53,1,12,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(54,1,119,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(55,1,5,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(56,1,108,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(57,1,109,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(58,1,13,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(59,1,6,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(60,1,7,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(61,1,8,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(62,1,9,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(63,1,100,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(64,1,101,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(65,1,102,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(66,1,103,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(67,1,104,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(68,1,105,1,'2026-04-16 15:16:07',1,'2026-05-12 19:18:22',1),
(69,2,1,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(70,2,2,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(71,2,118,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(72,2,14,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(73,2,3,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(74,2,107,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(75,2,10,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(76,2,11,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(77,2,106,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(78,2,4,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(79,2,12,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(80,2,119,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(81,2,5,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(82,2,108,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(83,2,109,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(84,2,13,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(85,2,6,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(86,2,7,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(87,2,8,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(88,2,9,1,'2026-04-16 15:16:12',1,'2026-04-16 15:16:12',0),
(89,1,100,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(90,1,1,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(91,1,2,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(92,1,118,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(93,1,14,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(94,1,3,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(95,1,107,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(96,1,10,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(97,1,11,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(98,1,106,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(99,1,4,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(100,1,12,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(101,1,119,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(102,1,5,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(103,1,108,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(104,1,109,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(105,1,13,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(106,1,6,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(107,1,7,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(108,1,8,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(109,1,9,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(110,1,120,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(111,1,121,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(112,1,122,1,'2026-05-12 19:18:22',1,'2026-05-13 16:54:21',1),
(113,1,100,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(114,1,1,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(115,1,123,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(116,1,2,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(117,1,118,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(118,1,14,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(119,1,3,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(120,1,107,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(121,1,10,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(122,1,11,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(123,1,106,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(124,1,4,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(125,1,12,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(126,1,119,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(127,1,5,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(128,1,108,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(129,1,109,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(130,1,13,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(131,1,6,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(132,1,7,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(133,1,8,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(134,1,9,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(135,1,120,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(136,1,121,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0),
(137,1,122,1,'2026-05-13 16:54:21',1,'2026-05-13 16:54:21',0);

/*Table structure for table `tb_sys_role_user` */

DROP TABLE IF EXISTS `tb_sys_role_user`;

CREATE TABLE `tb_sys_role_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `role_id` bigint NOT NULL COMMENT '角色 ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建人 ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人 ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间并在更新时自动刷新',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记，0 正常，1 删除',
  PRIMARY KEY (`id`),
  KEY `idx_tb_sys_role_user_user_id_deleted` (`user_id`,`deleted`),
  KEY `idx_tb_sys_role_user_role_id_deleted` (`role_id`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

/*Data for the table `tb_sys_role_user` */

insert  into `tb_sys_role_user`(`id`,`user_id`,`role_id`,`create_by`,`create_time`,`update_by`,`update_time`,`deleted`) values 
(1,1,1,1,'2026-04-16 14:50:35',1,'2026-04-16 14:50:35',0),
(2,1,2,1,'2026-04-16 14:50:35',1,'2026-04-16 14:50:35',0);


DROP TABLE IF EXISTS `tb_user`;

CREATE TABLE `tb_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `full_name` varchar(64) DEFAULT NULL COMMENT '姓名',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(32) DEFAULT NULL COMMENT '手机号',
  `id_card` varchar(128) DEFAULT NULL COMMENT '身份证号',
  `face_file_id` varchar(100) DEFAULT NULL COMMENT '人脸文件 ID',
  `face_feature` text COMMENT '人脸特征值',
  `org_id` bigint DEFAULT NULL COMMENT '所属组织 ID',
  `password` varchar(255) DEFAULT NULL COMMENT '密码密文',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态，1 启用，0 禁用',
  `create_by` bigint DEFAULT NULL COMMENT '创建人 ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人 ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间并在更新时自动刷新',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记，0 正常，1 删除',
  PRIMARY KEY (`id`),
  KEY `idx_tb_user_username_deleted` (`username`,`deleted`),
  KEY `idx_tb_user_email_deleted` (`email`,`deleted`),
  KEY `idx_tb_user_phone_deleted` (`phone`,`deleted`),
  KEY `idx_tb_user_org_id_status_deleted` (`org_id`,`status`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

/*Data for the table `tb_user` */

insert  into `tb_user`(`id`,`username`,`full_name`,`email`,`phone`,`id_card`,`face_file_id`,`face_feature`,`org_id`,`password`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`deleted`) values 
(1,'admin','超级管理员','admin@local.test','Mg+bprE7ppiO85EAHpPqrw==',NULL,NULL,NULL,1,'$2a$10$5jEE.2xAuijvcRpFVjhXt.ZODYlK/bsYzAxn6EPnmh0tOw0B5ArjG',0,1,'2026-04-16 14:50:35',1,'2026-05-12 19:15:25',0);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
