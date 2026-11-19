import { useEffect, useId, useRef, useState, type ButtonHTMLAttributes, type ReactNode } from "react";
import { Check, Plus, X } from "lucide-react";
import { catalogApi, type CatalogKey } from "../../api/endpoints";
import { useCatalog } from "../../auth/CatalogContext";
import { useIsAdmin } from "../../auth/ProfileContext";
import { useToast } from "./Toast";

// ---------------------------------------------------------------------------
// Button
// ---------------------------------------------------------------------------
type Variant = "primary" | "secondary" | "ghost" | "lime" | "white" | "danger";
interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant;
  size?: "sm" | "md" | "lg";
  loading?: boolean;
  block?: boolean;
  icon?: ReactNode;
  iconOnly?: boolean;
}

export function Button({
  variant = "primary",
  size = "md",
  loading,
  block,
  icon,
  iconOnly,
  className = "",
  children,
  disabled,
  type = "button",
  ...rest
}: ButtonProps) {
  const cls = [
    "btn",
    `btn-${variant}`,
    size !== "md" ? `btn-${size}` : "",
    block ? "btn-block" : "",
    iconOnly ? "btn-icon" : "",
    className,
  ]
    .filter(Boolean)
    .join(" ");
  return (
    <button type={type} className={cls} disabled={disabled || loading} aria-busy={loading || undefined} {...rest}>
      {loading ? <span className="spinner" /> : icon}
      {children}
    </button>
  );
}

// ---------------------------------------------------------------------------
// Field wrapper
// ---------------------------------------------------------------------------
export function Field({
  label,
  hint,
  error,
  optional,
  className = "",
  children,
  htmlFor,
}: {
  label: ReactNode;
  hint?: ReactNode;
  error?: string | null;
  optional?: boolean;
  className?: string;
  children: ReactNode;
  htmlFor?: string;
}) {
  return (
    <div className={`field ${className}`}>
      <label className="field-label" htmlFor={htmlFor}>
        {label}
        {optional && <span className="opt">(opcional)</span>}
      </label>
      {children}
      {error ? <span className="field-error">{error}</span> : hint ? <span className="field-hint">{hint}</span> : null}
    </div>
  );
}

// ---------------------------------------------------------------------------
// Modal
// ---------------------------------------------------------------------------
export function Modal({
  open,
  onClose,
  title,
  subtitle,
  icon,
  children,
  footer,
  width,
}: {
  open: boolean;
  onClose: () => void;
  title: ReactNode;
  subtitle?: ReactNode;
  icon?: ReactNode;
  children: ReactNode;
  footer?: ReactNode;
  width?: number;
}) {
  const titleId = useId();
  const dialogRef = useRef<HTMLDivElement>(null);
  const closeRef = useRef(onClose);
  useEffect(() => { closeRef.current = onClose; }, [onClose]);
  useEffect(() => {
    if (!open) return;
    const dialog = dialogRef.current;
    const workspace = !!dialog?.closest(".app-workspace");
    const previousFocus = document.activeElement instanceof HTMLElement ? document.activeElement : null;
    if (workspace) {
      const firstField = dialog?.querySelector<HTMLElement>(".modal-body input:not(:disabled), .modal-body select:not(:disabled), .modal-body textarea:not(:disabled), .modal-body button:not(:disabled)");
      (firstField ?? dialog)?.focus();
    }
    const onKey = (e: KeyboardEvent) => {
      if (e.defaultPrevented) return;
      if (e.key === "Escape") { e.preventDefault(); closeRef.current(); }
      if (!workspace || e.key !== "Tab" || !dialog) return;
      const controls = [...dialog.querySelectorAll<HTMLElement>("a[href], button:not(:disabled), input:not(:disabled), select:not(:disabled), textarea:not(:disabled), [tabindex='0']")].filter((element) => element.getClientRects().length);
      const first = controls[0], last = controls[controls.length - 1];
      if (!first) { e.preventDefault(); dialog.focus(); }
      else if (e.shiftKey && (document.activeElement === first || document.activeElement === dialog)) { e.preventDefault(); last?.focus(); }
      else if (!e.shiftKey && document.activeElement === last) { e.preventDefault(); first.focus(); }
    };
    document.addEventListener("keydown", onKey);
    const prev = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    return () => {
      document.removeEventListener("keydown", onKey);
      document.body.style.overflow = prev;
      if (workspace && previousFocus?.isConnected && (document.activeElement === document.body || dialog?.contains(document.activeElement))) previousFocus.focus();
    };
  }, [open]);

  if (!open) return null;
  return (
    <div className="modal-backdrop" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <div ref={dialogRef} className="modal" role="dialog" aria-modal="true" aria-labelledby={titleId} tabIndex={-1} style={width ? { width: `min(${width}px, 100%)` } : undefined}>
        <div className="modal-head">
          <div className="row" style={{ alignItems: "flex-start", gap: 14 }}>
            {icon && <span className="icon-tile grad">{icon}</span>}
            <div>
              <h2 id={titleId} style={{ fontSize: 21 }}>
                {title}
              </h2>
              {subtitle && <p className="card-sub">{subtitle}</p>}
            </div>
          </div>
          <Button variant="ghost" size="sm" iconOnly onClick={onClose} aria-label="Fechar">
            <X size={18} />
          </Button>
        </div>
        <div className="modal-body">{children}</div>
        {footer && <div className="modal-foot">{footer}</div>}
      </div>
    </div>
  );
}

// ---------------------------------------------------------------------------
// Estado vazio
// ---------------------------------------------------------------------------
export function EmptyState({
  icon,
  title,
  text,
  action,
}: {
  icon: ReactNode;
  title: string;
  text: string;
  action?: ReactNode;
}) {
  return (
    <div className="empty">
      <div className="empty-icon">{icon}</div>
      <h3>{title}</h3>
      <p>{text}</p>
      {action && <div style={{ marginTop: 8 }}>{action}</div>}
    </div>
  );
}

// ---------------------------------------------------------------------------
// Cabeçalho de página
// ---------------------------------------------------------------------------
export function PageHeader({
  eyebrow,
  title,
  text,
  actions,
}: {
  eyebrow?: ReactNode;
  title: ReactNode;
  text?: ReactNode;
  actions?: ReactNode;
}) {
  return (
    <header className="page-header">
      <div className="grow">
        {eyebrow && <div className="eyebrow">{eyebrow}</div>}
        <h1>{title}</h1>
        {text && <p className="page-lead">{text}</p>}
      </div>
      {actions && <div className="row row-wrap">{actions}</div>}
    </header>
  );
}

export function Skeleton({ h = 18, w = "100%", r }: { h?: number; w?: number | string; r?: number }) {
  return <div className="skeleton" style={{ height: h, width: w, borderRadius: r }} />;
}

export function ListSkeleton({ rows = 3 }: { rows?: number }) {
  return (
    <div className="item-list">
      {Array.from({ length: rows }).map((_, i) => (
        <div key={i} className="item">
          <Skeleton h={40} w={40} r={12} />
          <div className="grow stack" style={{ gap: 8 }}>
            <Skeleton h={16} w="45%" />
            <Skeleton h={12} w="70%" />
          </div>
        </div>
      ))}
    </div>
  );
}

// ---------------------------------------------------------------------------
// Select de catálogo com opção de cadastrar um novo item (POST no catálogo)
// ---------------------------------------------------------------------------
const NEW = "__new__";

export function CatalogSelect({
  catalog,
  value,
  onChange,
  placeholder = "Selecione…",
  newLabel = "Cadastrar novo…",
  id,
  invalid,
  exclude = [],
  format = (n: string) => n,
}: {
  exclude?: number[];
  format?: (name: string) => string;
  catalog: CatalogKey;
  value: number | null;
  onChange: (id: number | null) => void;
  placeholder?: string;
  newLabel?: string;
  id?: string;
  invalid?: boolean;
}) {
  const { items, loading, error, refresh, upsert } = useCatalog(catalog);
  const toast = useToast();
  // Só ADMIN pode cadastrar item de catálogo (o backend devolve 403 para os demais).
  const canCreate = useIsAdmin();
  const [creating, setCreating] = useState(false);
  const [name, setName] = useState("");
  const [saving, setSaving] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (creating) inputRef.current?.focus();
  }, [creating]);

  const sorted = items
    .filter((i) => !exclude.includes(i.id) || i.id === value)
    .sort((a, b) => format(a.name).localeCompare(format(b.name), "pt-BR"));

  async function create() {
    const n = name.trim();
    if (!n) return;
    const existing = items.find((i) => i.name.toLowerCase() === n.toLowerCase());
    if (existing) {
      onChange(existing.id);
      setCreating(false);
      setName("");
      return;
    }
    setSaving(true);
    try {
      const item = await catalogApi.create(catalog, n);
      upsert(item);
      onChange(item.id);
      toast.success(`"${item.name}" cadastrado`);
      setCreating(false);
      setName("");
    } catch (err) {
      toast.error(err);
    } finally {
      setSaving(false);
    }
  }

  if (creating && canCreate) {
    return (
      <div className="row" style={{ gap: 8 }}>
        <input
          ref={inputRef}
          className="input"
          id={id}
          value={name}
          placeholder="Digite o nome"
          onChange={(e) => setName(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              e.preventDefault();
              void create();
            }
            if (e.key === "Escape") {
              e.stopPropagation();
              setCreating(false);
            }
          }}
        />
        <Button variant="primary" iconOnly onClick={create} loading={saving} aria-label="Salvar">
          {!saving && <Check size={18} />}
        </Button>
        <Button variant="secondary" iconOnly onClick={() => setCreating(false)} aria-label="Cancelar">
          <X size={18} />
        </Button>
      </div>
    );
  }

  if (error && items.length === 0) {
    return (
      <div className="stack" style={{ gap: 10 }}>
        <select id={id} className="select" disabled><option>Opções indisponíveis no momento</option></select>
        <CatalogFeedback onRetry={refresh}>Não foi possível carregar as opções.</CatalogFeedback>
      </div>
    );
  }

  return (
    <select
      id={id}
      className="select"
      aria-invalid={invalid || undefined}
      value={value ?? ""}
      disabled={loading}
      onChange={(e) => {
        if (e.target.value === NEW) setCreating(true);
        else onChange(e.target.value ? Number(e.target.value) : null);
      }}
    >
      <option value="">{loading ? "Carregando…" : placeholder}</option>
      {sorted.map((i) => (
        <option key={i.id} value={i.id}>
          {format(i.name)}
        </option>
      ))}
      {canCreate && <option value={NEW}>＋ {newLabel}</option>}
    </select>
  );
}

export function AddButton({ children, onClick }: { children: ReactNode; onClick: () => void }) {
  return (
    <Button icon={<Plus size={18} />} onClick={onClick}>
      {children}
    </Button>
  );
}

/** Retrying a failed catalog keeps the existing request and prevents double clicks. */
export function CatalogFeedback({ children, onRetry }: { children: ReactNode; onRetry: () => Promise<unknown> }) {
  const toast = useToast();
  const [retrying, setRetrying] = useState(false);
  async function retry() {
    setRetrying(true);
    try { await onRetry(); }
    catch (error) { toast.error(error); }
    finally { setRetrying(false); }
  }
  return (
    <div className="alert error" role="alert">
      <div className="grow">{children}</div>
      <Button variant="secondary" size="sm" loading={retrying} onClick={retry}>Tentar novamente</Button>
    </div>
  );
}
