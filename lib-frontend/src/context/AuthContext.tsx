import React, { createContext, useContext, useState } from 'react';
import type { ReactNode } from 'react';
import type { User, UserRole } from '../types';
import { getStoredToken, setStoredToken, removeStoredToken, decodeJWT, authApi } from '../services/api';

interface AuthContextType {
  token: string | null;
  user: User | null;
  login: (username: string, password: string) => Promise<void>;
  register: (data: Record<string, unknown>) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [token, setToken] = useState<string | null>(getStoredToken());
  const [user, setUser] = useState<User | null>(() => {
    const savedToken = getStoredToken();
    if (savedToken) {
      const decoded = decodeJWT(savedToken);
      if (decoded && decoded.username) {
        return {
          username: decoded.username,
          role: (decoded.role as UserRole) || 'BORROWER',
          id: decoded.id || 1,
          fullName: decoded.username,
        };
      }
    }
    return null;
  });

  const login = async (username: string, password: string) => {
    // Calling real Spring Boot Auth API Gateway (/api/v1/auth/login)
    const res = await authApi.login({ username, password });
    const tokenStr = res?.accessToken || res?.token;
    if (tokenStr) {
      setToken(tokenStr);
      setStoredToken(tokenStr);
      const decoded = decodeJWT(tokenStr);
      const loggedUser: User = res.user || {
        username: decoded?.username || username,
        role: (decoded?.role as UserRole) || 'BORROWER',
        fullName: username,
      };
      setUser(loggedUser);
    } else {
      throw new Error('Máy chủ Backend không trả về Access Token hợp lệ');
    }
  };

  const register = async (data: Record<string, unknown>) => {
    // Calling real Spring Boot Auth API Gateway (/api/v1/auth/sign-up)
    const res = await authApi.register(data);
    const tokenStr = res?.accessToken || res?.token;
    if (tokenStr) {
      setToken(tokenStr);
      setStoredToken(tokenStr);
      const decoded = decodeJWT(tokenStr);
      setUser(
        res.user || {
          username: String(data.username || decoded?.username),
          role: (decoded?.role as UserRole) || (data.role as UserRole) || 'BORROWER',
          fullName: String(data.fullName || data.username),
        }
      );
    }
  };

  const logout = () => {
    removeStoredToken();
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ token, user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
