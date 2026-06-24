import {
  getSessionStorage,
  getStorage,
  removeSessionStorage,
  setSessionStorage,
  setStorage
} from '@vue-scaffold/utils';

export type CachedAuthIdentity = {
  fullName: string;
};

export type CachedAuthAccount = {
  fullName: string;
  token?: string;
  userId?: number | string;
  username?: string;
};

const AUTH_IDENTITY_STORAGE_KEY = 'question-manager:h5:identity';
const AUTH_ACCOUNT_SESSION_KEY = 'question-manager:h5:account';
let currentAuthAccount: CachedAuthAccount | null = null;

export function getCachedAuthIdentity() {
  const identity = getStorage<CachedAuthIdentity>(AUTH_IDENTITY_STORAGE_KEY);
  if (!identity?.fullName) {
    return null;
  }
  return identity;
}

export function setCachedAuthIdentity(identity: CachedAuthIdentity) {
  setStorage(AUTH_IDENTITY_STORAGE_KEY, {
    fullName: identity.fullName
  });
}

export function getCurrentAuthAccount() {
  currentAuthAccount = currentAuthAccount
    ?? getSessionStorage<CachedAuthAccount>(AUTH_ACCOUNT_SESSION_KEY);
  return currentAuthAccount || null;
}

export function setCurrentAuthAccount(account: CachedAuthAccount) {
  currentAuthAccount = account;
  setSessionStorage(AUTH_ACCOUNT_SESSION_KEY, account);
}

export function clearCurrentAuthAccount() {
  currentAuthAccount = null;
  removeSessionStorage(AUTH_ACCOUNT_SESSION_KEY);
}

export function getCurrentAuthToken() {
  return getCurrentAuthAccount()?.token || '';
}
