/**
 * 输入具体下标，脱敏下标范围的值
 * @param input 输入值
 * @param maskIndexes 需要脱敏的下标值
 * @returns 脱敏后的字符串。
 * @example
 * ```ts
 * const value = desensitizeValue("abcdef", [1, 3]);
 *
 * // value => "a***ef"
 * ```
 */
export function desensitizeValue(input: string, maskIndexes?: number[]): string {
  if (!input) return "";
  const length = input.length;
  // 如果未提供脱敏下标数组，则默认对整个输入值进行全脱敏
  if (!maskIndexes || maskIndexes.length === 0) {
    return "*".repeat(length);
  }

  // 创建一个标记数组，用于标记需要脱敏的位置
  const shouldMask = new Array(length).fill(false);

  // 将指定的脱敏下标位置设为 true
  maskIndexes.forEach((index) => {
    if (index >= 0 && index < length) {
      shouldMask[index] = true;
    }
  });

  // 生成脱敏后的值
  let desensitizedValue = "";
  for (let i = 0; i < length; i++) {
    desensitizedValue += shouldMask[i] ? "*" : input[i];
  }

  return desensitizedValue;
}

/**
 * 手机号脱敏
 * @param value 手机号
 * @returns 脱敏后的手机号。
 * @example
 * ```ts
 * const mobile = sensitiveMobile("13812345678");
 *
 * // mobile => "138****5678"
 * ```
 */
export function sensitiveMobile(value: string) {
  if (!value) {
    return "";
  }
  value = value.toString();
  return `${value.substring(0, 3)}****${value.substring(value.length - 4)}`;
}

/**
 * 身份证号脱敏
 * @param value 证件号
 * @returns 脱敏后的证件号。
 * @example
 * ```ts
 * const card = sensitiveIdentityCard("110101199001011234");
 *
 * // card => "11************34"
 * ```
 */
export function sensitiveIdentityCard(value: string) {
  if (!value) {
    return "";
  }
  const regExp = /^(.{2})(?:\d+)(.{2})$/;
  return value.replace(regExp, "$1************$2");
}
