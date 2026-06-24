UPDATE `tb_user`
SET `status` = 1,
    `update_time` = NOW()
WHERE `username` IN ('admin', 'student')
  AND `deleted` = 0;
