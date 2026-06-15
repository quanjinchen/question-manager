import * as ArrayUtils from './array';
import * as DateUtils from './date';
import * as DesensitizeUtils from './desensitize';
import * as FileUtils from './file';
import * as IsUtils from './is';
import * as ObjectUtils from './object';
import * as ToolUtils from './tool';
import * as TreeUtils from './tree';
import * as StorageUtils from './storage';
import * as MessageUtils from './message';
import * as PollingUtils from './polling';
import * as EncryptUtils from './encrypt';
import * as UuidUtils from './uuid';

export * from './storage';
export * from './date';
export * from './message';
export * from './polling';
export * from './encrypt';
export * from './uuid';
export * from './array';
export * from './desensitize';
export * from './file';
export * from './is';
export * from './tool';
export * from './tree';
export * from './object';

export const Array = ArrayUtils;
export const Date = DateUtils;
export const Desensitize = DesensitizeUtils;
export const File = FileUtils;
export const Is = IsUtils;
export const Message = MessageUtils;
export const Object = ObjectUtils;
export const Polling = PollingUtils;
export const Storage = StorageUtils;
export const Tool = ToolUtils;
export const Tree = TreeUtils;
export const Encrypt = EncryptUtils;
export const Uuid = UuidUtils;

const $utils = {
  Array,
  Date,
  Desensitize,
  File,
  Is,
  Message,
  Object,
  Polling,
  Storage,
  Tool,
  Tree,
  Encrypt,
  Uuid,
};

export default $utils;
