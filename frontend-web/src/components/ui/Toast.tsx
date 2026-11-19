import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from "react";
import { AlertCircle, CheckCircle2, Info, X } from "lucide-react";
import { errorMessage } from "../../api/client";

type Kind = "success" | "error" | "info";
interface ToastItem {
  id: number;
  kind: Kind;
  title: string;
  details?: string[];
}

interface ToastApi {
  success: (title: string) => void;
  info: (title: string) => void;
  error: (err: unknown) => void;
}

const ToastContext = createContext<ToastApi | null>(null);

let seq = 0;

export function ToastProvider({ children }: { children: ReactNode }) {
  const [items, setItems] = useState<ToastItem[]>([]);

  const dismiss = useCallback((id: number) => setItems((xs) => xs.filter((x) => x.id !== id)), []);

  const push = useCallback(
    (t: Omit<ToastItem, "id">) => {
      const id = ++seq;
      setItems((xs) => [...xs.slice(-3), { ...t, id }]);
      window.setTimeout(() => dismiss(id), t.kind === "error" ? 7000 : 3800);
    },
    [dismiss],
  );

  const api = useMemo<ToastApi>(
    () => ({
      success: (title) => push({ kind: "success", title }),
      info: (title) => push({ kind: "info", title }),
      error: (err) => {
        const { title, details } = errorMessage(err);
        push({ kind: "error", title, details });
      },
    }),
    [push],
  );

  return (
    <ToastContext.Provider value={api}>
      {children}
      <div className="toast-stack" role="status" aria-live="polite">
        {items.map((t) => (
          <div key={t.id} className={`toast ${t.kind}`}>
            <span className="toast-icon">
              {t.kind === "success" ? <CheckCircle2 size={20} /> : t.kind === "error" ? <AlertCircle size={20} /> : <Info size={20} />}
            </span>
            <div className="grow">
              <div className="toast-title">{t.title}</div>
              {t.details && t.details.length > 0 && (
                <ul>
                  {t.details.map((d) => (
                    <li key={d}>{d}</li>
                  ))}
                </ul>
              )}
            </div>
            <button
              className="btn btn-ghost btn-sm btn-icon"
              style={{ color: "#9aa3b4", ["--h" as string]: "26px" }}
              onClick={() => dismiss(t.id)}
              aria-label="Fechar"
            >
              <X size={16} />
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error("useToast precisa estar dentro de <ToastProvider>");
  return ctx;
}
