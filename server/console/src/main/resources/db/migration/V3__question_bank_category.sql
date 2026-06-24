CREATE TABLE IF NOT EXISTS `tb_question_bank_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `category_name` varchar(128) NOT NULL COMMENT '题库分类名称',
  `description` varchar(500) DEFAULT NULL COMMENT '题库分类描述',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序值',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态，1 启用，0 禁用',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除，0 未删除，1 已删除',
  PRIMARY KEY (`id`),
  KEY `idx_tb_question_bank_category_name_deleted` (`category_name`,`deleted`),
  KEY `idx_tb_question_bank_category_status_deleted` (`status`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题库分类表';

INSERT INTO `tb_question_bank_category`(`id`,`category_name`,`description`,`sort_order`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`deleted`)
SELECT 1,'默认分类','系统默认题库分类',1,1,1,NOW(),1,NOW(),0
WHERE NOT EXISTS (SELECT 1 FROM `tb_question_bank_category` WHERE `id` = 1);

ALTER TABLE `tb_question_category`
  ADD COLUMN `bank_category_id` bigint NOT NULL DEFAULT '1' COMMENT '题库分类 ID' AFTER `id`,
  ADD KEY `idx_tb_question_category_bank_category_deleted` (`bank_category_id`,`deleted`);

UPDATE `tb_question_category`
SET `bank_category_id` = 1
WHERE `bank_category_id` IS NULL
   OR `bank_category_id` = 0;

UPDATE `tb_sys_menu`
SET `menu_name` = '题库',
    `path` = '/question/category',
    `menu_code` = 'question:category:query',
    `icon` = 'Collection',
    `update_time` = NOW()
WHERE `id` = 201
  AND `deleted` = 0;

INSERT INTO `tb_sys_menu`(`id`,`parent_id`,`menu_name`,`path`,`menu_type`,`menu_code`,`order_num`,`visible`,`create_by`,`create_time`,`update_by`,`update_time`,`deleted`,`icon`)
SELECT 209,201,'题库分类查询','/question/category/bank-category-query','BTN','question:bankCategory:query',3,1,1,NOW(),1,NOW(),0,NULL
WHERE NOT EXISTS (SELECT 1 FROM `tb_sys_menu` WHERE `id` = 209);

UPDATE `tb_sys_menu`
SET `parent_id` = 201,
    `menu_name` = '题库分类查询',
    `path` = '/question/category/bank-category-query',
    `menu_type` = 'BTN',
    `menu_code` = 'question:bankCategory:query',
    `order_num` = 3,
    `icon` = NULL,
    `update_time` = NOW()
WHERE `id` = 209
  AND `deleted` = 0;

INSERT INTO `tb_sys_menu`(`id`,`parent_id`,`menu_name`,`path`,`menu_type`,`menu_code`,`order_num`,`visible`,`create_by`,`create_time`,`update_by`,`update_time`,`deleted`,`icon`)
SELECT 210,201,'题库分类保存','/question/category/bank-category-save','BTN','question:bankCategory:update',4,1,1,NOW(),1,NOW(),0,NULL
WHERE NOT EXISTS (SELECT 1 FROM `tb_sys_menu` WHERE `id` = 210);

UPDATE `tb_sys_menu`
SET `parent_id` = 201,
    `path` = '/question/category/bank-category-save',
    `menu_type` = 'BTN',
    `order_num` = 4,
    `update_time` = NOW()
WHERE `id` = 210
  AND `deleted` = 0;

INSERT INTO `tb_sys_menu`(`id`,`parent_id`,`menu_name`,`path`,`menu_type`,`menu_code`,`order_num`,`visible`,`create_by`,`create_time`,`update_by`,`update_time`,`deleted`,`icon`)
SELECT 211,201,'题库分类删除','/question/category/bank-category-delete','BTN','question:bankCategory:delete',5,1,1,NOW(),1,NOW(),0,NULL
WHERE NOT EXISTS (SELECT 1 FROM `tb_sys_menu` WHERE `id` = 211);

UPDATE `tb_sys_menu`
SET `parent_id` = 201,
    `path` = '/question/category/bank-category-delete',
    `menu_type` = 'BTN',
    `order_num` = 5,
    `update_time` = NOW()
WHERE `id` = 211
  AND `deleted` = 0;

UPDATE `tb_sys_menu`
SET `order_num` = 2,
    `update_time` = NOW()
WHERE `id` = 201
  AND `deleted` = 0;

UPDATE `tb_sys_menu`
SET `order_num` = 3,
    `update_time` = NOW()
WHERE `id` = 205
  AND `deleted` = 0;

UPDATE `tb_sys_menu`
SET `order_num` = 4,
    `update_time` = NOW()
WHERE `id` = 208
  AND `deleted` = 0;

INSERT INTO `tb_sys_role_menu`(`id`,`role_id`,`menu_id`,`create_by`,`create_time`,`update_by`,`update_time`,`deleted`)
SELECT 218,1,209,1,NOW(),1,NOW(),0
WHERE NOT EXISTS (SELECT 1 FROM `tb_sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 209 AND `deleted` = 0);

INSERT INTO `tb_sys_role_menu`(`id`,`role_id`,`menu_id`,`create_by`,`create_time`,`update_by`,`update_time`,`deleted`)
SELECT 219,1,210,1,NOW(),1,NOW(),0
WHERE NOT EXISTS (SELECT 1 FROM `tb_sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 210 AND `deleted` = 0);

INSERT INTO `tb_sys_role_menu`(`id`,`role_id`,`menu_id`,`create_by`,`create_time`,`update_by`,`update_time`,`deleted`)
SELECT 220,1,211,1,NOW(),1,NOW(),0
WHERE NOT EXISTS (SELECT 1 FROM `tb_sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 211 AND `deleted` = 0);
