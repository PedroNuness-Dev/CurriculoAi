import type { ApiErrorBody } from "./types";

// Em dev o Vite faz proxy de /api → http://localhost:8080 (ver vite.config.ts).
const BASE_URL = "";

const SESSION_KEY = "curriculoai.session";

export interface Session {
  token: string;
  expiresAt: number; // epoch ms
  userId: number;
  name: string;
  email: string;
}

export function readSession(): Session | null {
  try {
    const raw = localStorage.getItem(SESSION_KEY);
    if (!raw) return null;
    const session = JSON.parse(raw) as Session;
    if (!session.token || Date.now() >= session.expiresAt) {
      localStorage.removeItem(SESSION_KEY);
      return null;
    }
    return session;
  } catch {
    return null;
  }
}

export function writeSession(session: Session | null) {
  try {
    if (session) localStorage.setItem(SESSION_KEY, JSON.stringify(session));
    else localStorage.removeItem(SESSION_KEY);
  } catch {
    /* storage indisponível: sessão fica só em memória */
  }
}

export class ApiError extends Error {
  status: number;
  details: string[];

  constructor(status: number, message: string, details: string[] = []) {
    super(message);
    this.status = status;
    this.details = details;
  }
}

// Listeners para quando o token expira / é rejeitado.
type Listener = () => void;
const unauthorizedListeners = new Set<Listener>();
export function onUnauthorized(fn: Listener) {
  unauthorizedListeners.add(fn);
  return () => {
    unauthorizedListeners.delete(fn);
  };
}

interface RequestOptions {
  method?: "GET" | "POST" | "PUT" | "DELETE";
  body?: unknown;
  auth?: boolean;
  responseType?: "json" | "blob";
  signal?: AbortSignal;
}

export async function request<T>(path: string, opts: RequestOptions = {}): Promise<T> {
  const { method = "GET", body, auth = true, responseType = "json", signal } = opts;
  const headers: Record<string, string> = { Accept: responseType === "blob" ? "application/pdf" : "application/json" };

  if (body !== undefined) headers["Content-Type"] = "application/json";

  if (auth) {
    const session = readSession();
    if (!session) {
      unauthorizedListeners.forEach((fn) => fn());
      throw new ApiError(401, "Sua sessão expirou. Entre novamente.");
    }
    headers.Authorization = `Bearer ${session.token}`;
  }

  let res: Response;
  try {
    res = await fetch(BASE_URL + path, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
      signal,
    });
  } catch (err) {
    if ((err as Error).name === "AbortError") throw err;
    throw new ApiError(0, "Não foi possível conectar ao servidor. Verifique se o backend está rodando em localhost:8080.");
  }

  if (!res.ok) {
    let parsed: ApiErrorBody | null = null;
    try {
      parsed = (await res.json()) as ApiErrorBody;
    } catch {
      /* corpo vazio */
    }
    if (res.status === 401 || res.status === 403) {
      if (auth) unauthorizedListeners.forEach((fn) => fn());
      throw new ApiError(res.status, auth ? "Sua sessão expirou. Entre novamente." : "E-mail ou senha incorretos.");
    }
    if (res.status === 502 || res.status === 504) {
      throw new ApiError(res.status, "O backend não respondeu. Ele está rodando em localhost:8080?");
    }
    throw new ApiError(
      res.status,
      parsed?.message || `Erro ${res.status} ao chamar ${path}`,
      parsed?.errors ?? [],
    );
  }

  if (responseType === "blob") return (await res.blob()) as T;
  if (res.status === 204) return undefined as T;
  const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

export function errorMessage(err: unknown): { title: string; details: string[] } {
  if (err instanceof ApiError) return { title: err.message, details: err.details };
  if (err instanceof Error) return { title: err.message, details: [] };
  return { title: "Algo deu errado.", details: [] };
}
