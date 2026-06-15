import {
  getSessionStorage,
  getStorage,
  removeSessionStorage,
  setSessionStorage,
  setStorage
} from '@vue-scaffold/utils';

export type CachedAuthIdentity = {
  fullName: string;
  idCard: string;
};

export type CachedAuthAccount = {
  certToken: string;
  fullName: string;
  idCard: string;
  token?: string;
  appInfo?: {
    appName?: string;
    clientId?: string;
  } | null;
};

const AUTH_IDENTITY_STORAGE_KEY = 'small-auth:h5:identity';
const AUTH_ACCOUNT_SESSION_KEY = 'small-auth:h5:account';
let currentAuthAccount: CachedAuthAccount | null = null;

export function getCachedAuthIdentity() {
  const identity = getStorage<CachedAuthIdentity>(AUTH_IDENTITY_STORAGE_KEY);
  if (!identity?.fullName || !identity?.idCard) {
    return null;
  }
  return identity;
}

export function setCachedAuthIdentity(identity: CachedAuthIdentity) {
  setStorage(AUTH_IDENTITY_STORAGE_KEY, {
    fullName: identity.fullName,
    idCard: identity.idCard
  });
}

export function getCurrentAuthAccount(certToken?: string) {
  currentAuthAccount = currentAuthAccount
    ?? getSessionStorage<CachedAuthAccount>(AUTH_ACCOUNT_SESSION_KEY);
  if (!currentAuthAccount) {
    return null;
  }
  if (certToken && currentAuthAccount.certToken !== certToken) {
    return null;
  }
  return currentAuthAccount;
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
