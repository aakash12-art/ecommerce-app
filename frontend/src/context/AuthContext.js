import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';
import { client, getStoredToken, setStoredToken } from '../api/client';

const AuthContext = createContext(null);

const USER_KEY = 'ecommerce_user';

function readStoredUser() {
  try {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

function persistUser(user) {
  if (user) localStorage.setItem(USER_KEY, JSON.stringify(user));
  else localStorage.removeItem(USER_KEY);
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser);
  const [bootstrapping, setBootstrapping] = useState(true);

  const logout = useCallback(() => {
    setStoredToken(null);
    persistUser(null);
    setUser(null);
  }, []);

  const refreshMe = useCallback(async () => {
    const token = getStoredToken();
    if (!token) {
      setUser(null);
      return;
    }
    try {
      const { data } = await client.get('/api/auth/me');
      setUser(data);
      persistUser(data);
    } catch {
      logout();
    }
  }, [logout]);

  useEffect(() => {
    (async () => {
      await refreshMe();
      setBootstrapping(false);
    })();
  }, [refreshMe]);

  const login = useCallback(async (email, password) => {
    const { data } = await client.post('/api/auth/login', { email, password });
    setStoredToken(data.accessToken);
    setUser(data.user);
    persistUser(data.user);
    return data;
  }, []);

  const register = useCallback(async (name, email, password) => {
    const { data } = await client.post('/api/auth/register', { name, email, password });
    setStoredToken(data.accessToken);
    setUser(data.user);
    persistUser(data.user);
    return data;
  }, []);

  const value = useMemo(
    () => ({
      user,
      bootstrapping,
      isAuthenticated: !!user,
      isAdmin: user?.role === 'ADMIN',
      login,
      register,
      logout,
      refreshMe,
    }),
    [user, bootstrapping, login, register, logout, refreshMe],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
