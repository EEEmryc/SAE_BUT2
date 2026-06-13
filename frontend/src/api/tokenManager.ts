const REFRESH_TOKEN_KEY = "learnhub.refreshToken";

let accessToken: string | null = null;

export const tokenManager = {
  getAccessToken() {
    return accessToken;
  },
  setAccessToken(token: string | null) {
    accessToken = token;
  },
  getRefreshToken() {
    return sessionStorage.getItem(REFRESH_TOKEN_KEY);
  },
  setRefreshToken(token: string | null) {
    if (token) {
      sessionStorage.setItem(REFRESH_TOKEN_KEY, token);
    } else {
      sessionStorage.removeItem(REFRESH_TOKEN_KEY);
    }
  },
  clear() {
    accessToken = null;
    sessionStorage.removeItem(REFRESH_TOKEN_KEY);
  },
};
