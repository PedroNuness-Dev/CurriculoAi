import { useState, type FormEvent } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { ArrowRight, Eye, EyeOff, Lock, Mail } from "lucide-react";
import AuthLayout from "../components/layout/AuthLayout";
import { Button, Field } from "../components/ui/ui";
import { useAuth } from "../auth/AuthContext";
import { ApiError } from "../api/client";

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const state = location.state as { from?: string; email?: string; registered?: boolean } | null;

  const [email, setEmail] = useState(state?.email ?? "");
  const [password, setPassword] = useState("");
  const [show, setShow] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await login(email.trim(), password);
      navigate(state?.from && state.from.startsWith("/app") ? state.from : "/app", { replace: true });
    } catch (err) {
      // O backend devolve 500 para credenciais inválidas; tratamos como login incorreto.
      if (err instanceof ApiError && err.status !== 0 && err.status !== 502 && err.status !== 504) {
        setError(err.status === 400 ? err.details[0] ?? err.message : "E-mail ou senha incorretos.");
      } else {
        setError(err instanceof Error ? err.message : "Não foi possível entrar.");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <AuthLayout>
      <div className="eyebrow">Bem-vindo de volta</div>
      <h1 style={{ marginTop: 10 }}>Entrar na sua conta</h1>
      <p className="muted" style={{ marginTop: 8 }}>
        Continue de onde parou e gere seu currículo com IA.
      </p>

      {state?.registered && !error && (
        <div className="alert" style={{ marginTop: 22 }}>
          Conta criada! Agora é só entrar com sua senha.
        </div>
      )}
      {error && (
        <div className="alert error" style={{ marginTop: 22 }} role="alert">
          {error}
        </div>
      )}

      <form className="stack" style={{ marginTop: 26, gap: 18 }} onSubmit={onSubmit} noValidate>
        <Field label="E-mail" htmlFor="email">
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
              required
            />
          </div>
        </Field>
        <Field label="Senha" htmlFor="password">
          <div className="input-wrap">
            <Lock size={18} />
            <input
              id="password"
              className="input"
              type={show ? "text" : "password"}
              autoComplete="current-password"
              placeholder="Sua senha"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              style={{ paddingRight: 48 }}
            />
            <Button
              variant="ghost"
              size="sm"
              iconOnly
              className="input-action"
              onClick={() => setShow((s) => !s)}
              aria-label={show ? "Ocultar senha" : "Mostrar senha"}
            >
              {show ? <EyeOff size={18} /> : <Eye size={18} />}
            </Button>
          </div>
        </Field>
        <Button type="submit" size="lg" block loading={loading} disabled={!email || !password}>
          Entrar {!loading && <ArrowRight size={18} />}
        </Button>
      </form>

      <p className="auth-switch">
        Ainda não tem conta? <Link to="/cadastro">Criar conta grátis</Link>
      </p>
    </AuthLayout>
  );
}
