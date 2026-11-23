import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { onUnauthorized, readSession, writeSession, type Session } from "../api/client";
import { authApi } from "../api/endpoints";

interface AuthState {
  session: Session | null;
  login: (email: string, password: string) => Promise<Session>;
  logout: () => void;
}

const AuthContext = createContext<AuthState | null>(null);

function toEpoch(v: string | number): number {
  if (typeof v === "number") return v < 1e12 ? v * 1000 : v; // segundos ou ms
  const t = Date.parse(v);
  return Number.isNaN(t) ? Date.now() + 2 * 60 * 60 * 1000 : t;
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<Session | null>(() => readSession());

  const logout = useCallback(() => {
    writeSession(null);
    setSession(null);
  }, []);

  const login = useCallback(async (email: string, password: string) => {
    const res = await authApi.login({ email, password });
    const s: Session = {
      token: res.token,
      expiresAt: toEpoch(res.expiresAt),
      userId: res.userId,
      name: res.name,
      email: res.email,
    };
    writeSession(s);
    setSession(s);
    return s;
  }, []);

  // Token rejeitado pelo backend → encerra sessão.
  useEffect(() => onUnauthorized(logout), [logout]);

  // Expira automaticamente quando o JWT vence (2h no backend).
  useEffect(() => {
    if (!session) return;
    const ms = session.expiresAt - Date.now();
    if (ms <= 0) {
      logout();
      return;
    }
    const t = window.setTimeout(logout, Math.min(ms, 2 ** 31 - 1));
    return () => window.clearTimeout(t);
  }, [session, logout]);

  const value = useMemo(() => ({ session, login, logout }), [session, login, logout]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth precisa estar dentro de <AuthProvider>");
  return ctx;
}
