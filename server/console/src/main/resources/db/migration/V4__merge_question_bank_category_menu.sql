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

UPDATE `tb_sys_menu`
SET `parent_id` = 201,
    `path` = '/question/category/bank-category-save',
    `menu_type` = 'BTN',
    `order_num` = 4,
    `update_time` = NOW()
WHERE `id` = 210
  AND `deleted` = 0;

UPDATE `tb_sys_menu`
SET `parent_id` = 201,
    `path` = '/question/category/bank-category-delete',
    `menu_type` = 'BTN',
    `order_num` = 5,
    `update_time` = NOW()
WHERE `id` = 211
  AND `deleted` = 0;
