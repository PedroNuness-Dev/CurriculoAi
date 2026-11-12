import { useMemo, useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ArrowRight, Eye, EyeOff, Lock, Mail, User } from "lucide-react";
import AuthLayout from "../components/layout/AuthLayout";
import { Button, Field } from "../components/ui/ui";
import { authApi } from "../api/endpoints";
import { useAuth } from "../auth/AuthContext";
import { ApiError } from "../api/client";
import { useToast } from "../components/ui/Toast";

function strength(pw: string) {
  let s = 0;
  if (pw.length >= 6) s++;
  if (pw.length >= 10) s++;
  if (/[A-Z]/.test(pw) && /[a-z]/.test(pw)) s++;
  if (/\d/.test(pw) && /[^A-Za-z0-9]/.test(pw)) s++;
  return s;
}

const COLORS = ["#e5e9f1", "#e0393e", "#f0a020", "#3a9cfd", "#109a4c"];
const LABELS = ["", "Fraca", "Razoável", "Boa", "Forte"];

export default function Register() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const toast = useToast();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [show, setShow] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [touched, setTouched] = useState(false);

  const s = useMemo(() => strength(password), [password]);
  const emailOk = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim());
  const valid = name.trim().length >= 2 && emailOk && password.length >= 6;

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setTouched(true);
    if (!valid) return;
    setError(null);
    setLoading(true);
    try {
      await authApi.register({ name: name.trim(), email: email.trim(), password });
      try {
        await login(email.trim(), password);
        toast.success(`Bem-vindo, ${name.trim().split(" ")[0]}!`);
        navigate("/app", { replace: true });
      } catch {
        navigate("/entrar", { state: { email: email.trim(), registered: true } });
      }
    } catch (err) {
      if (err instanceof ApiError && err.message === "Email inválido para cadastro") {
        setError("Este e-mail já está cadastrado. Tente entrar.");
      } else if (err instanceof ApiError && err.details.length) {
        setError(err.details.join(" · "));
      } else {
        setError(err instanceof Error ? err.message : "Não foi possível criar a conta.");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <AuthLayout>
      <div className="eyebrow">Comece agora</div>
      <h1 style={{ marginTop: 10 }}>Criar sua conta</h1>
      <p className="muted" style={{ marginTop: 8 }}>
        Leva menos de um minuto. Depois é só preencher sua trajetória.
      </p>

      {error && (
        <div className="alert error" style={{ marginTop: 22 }} role="alert">
          {error}
        </div>
      )}

      <form className="stack" style={{ marginTop: 26, gap: 18 }} onSubmit={onSubmit} noValidate>
        <Field label="Nome completo" htmlFor="name" error={touched && name.trim().length < 2 ? "Informe seu nome" : null}>
          <div className="input-wrap">
            <User size={18} />
            <input
              id="name"
              className="input"
              autoComplete="name"
              placeholder="Como você se chama?"
              value={name}
              onChange={(e) => setName(e.target.value)}
              aria-invalid={touched && name.trim().length < 2}
            />
          </div>
        </Field>
        <Field label="E-mail" htmlFor="email" error={touched && !emailOk ? "Informe um e-mail válido" : null}>
          <div className="input-wrap">
            <Mail size={18} />
            <input
              id="email"
              className="input"
              type="email"
              autoComplete="email"
              placeholder="voce@email.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              aria-invalid={touched && !emailOk}
            />
          </div>
        </Field>
        <Field
          label="Senha"
          htmlFor="password"
          error={touched && password.length < 6 ? "Use pelo menos 6 caracteres" : null}
          hint={password ? `Força: ${LABELS[s] || "Muito fraca"}` : "Mínimo de 6 caracteres"}
        >
          <div className="input-wrap">
            <Lock size={18} />
            <input
              id="password"
              className="input"
              type={show ? "text" : "password"}
              autoComplete="new-password"
              placeholder="Crie uma senha"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              aria-invalid={touched && password.length < 6}
              style={{ paddingRight: 48 }}
            />
            <Button
              variant="ghost"
              size="sm"
              iconOnly
              className="input-action"
              onClick={() => setShow((v) => !v)}
              aria-label={show ? "Ocultar senha" : "Mostrar senha"}
            >
              {show ? <EyeOff size={18} /> : <Eye size={18} />}
            </Button>
          </div>
          <div className="pw-meter" aria-hidden="true">
            {[1, 2, 3, 4].map((i) => (
              <span key={i} style={{ background: s >= i ? COLORS[s] : undefined }} />
            ))}
          </div>
        </Field>
        <Button type="submit" size="lg" block loading={loading}>
          Criar conta {!loading && <ArrowRight size={18} />}
        </Button>
      </form>

      <p className="auth-switch">
        Já tem conta? <Link to="/entrar">Entrar</Link>
      </p>
    </AuthLayout>
  );
}
