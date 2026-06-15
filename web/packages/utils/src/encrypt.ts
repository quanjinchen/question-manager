import bcrypt from 'bcryptjs';
import sha1 from 'crypto-js/sha1';

const CLIENT_SALT = '$2b$10$PTz3CCJvhnZM3PKMjI0Ihu';

/**
 * 对登录密码进行前端哈希处理。
 *
 * @param password 原始密码。
 * @returns 经过 sha1 和 bcrypt 固定盐处理后的密码摘要。
 * @example
 * ```ts
 * const encoded = encodePassword("123456");
 *
 * // encoded => "..." // 固定长度的密码摘要
 * ```
 */
export function encodePassword(password: string) {
  const sha1Hash = sha1(password).toString();
  return bcrypt.hashSync(sha1Hash, CLIENT_SALT).substring(CLIENT_SALT.length);
}

/**
 * 生成 bcrypt 随机盐。
 *
 * @returns bcrypt salt 字符串。
 * @example
 * ```ts
 * const salt = generateSalt();
 *
 * // salt => "$2b$10$..."
 * ```
 */
export function generateSalt() {
  return bcrypt.genSaltSync(10).toString();
}
