import { useEffect, useRef, useState, type FormEvent } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { ArrowLeft, ArrowRight, Check, CircleAlert, Eye, EyeOff, FileText, Lock, Mail, Sparkles } from "lucide-react";
import { Symbol, SymbolOutline, Wordmark } from "../components/brand/Brand";
import { Button, Field } from "../components/ui/ui";
import { useAuth } from "../auth/AuthContext";
import { ApiError } from "../api/client";
import "../styles/login.css";

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
  const rootRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const root = rootRef.current;
    if (!root) return;
    const preference = window.matchMedia("(prefers-reduced-motion: reduce)");
    const animations: Animation[] = [];
    const stop = () => animations.forEach((animation) => animation.cancel());
    if (!preference.matches) {
      const compact = window.matchMedia("(max-width: 800px)").matches;
      root.querySelectorAll<HTMLElement>("[data-login-enter]").forEach((element) => {
        if (!element.animate || !element.getClientRects().length) return;
        const paper = element.classList.contains("login-document");
        animations.push(element.animate(paper ? [
          { opacity: 0, transform: "perspective(900px) translate3d(15px, 24px, -35px) rotateX(8deg) rotate(-1deg)" },
          { opacity: 1, transform: "perspective(900px) translate3d(0, 0, 0) rotateX(0deg) rotate(-5deg)" },
        ] : [
          { opacity: 0, transform: `translateY(${compact ? 10 : 18}px)` },
          { opacity: 1, transform: "translateY(0)" },
        ], {
          duration: paper ? 850 : 600,
          delay: Number(element.dataset.loginEnter),
          easing: "cubic-bezier(.22, 1, .36, 1)",
          fill: "backwards",
        }));
      });
    }
    const onChange = () => { if (preference.matches) stop(); };
    preference.addEventListener("change", onChange);
    return () => { stop(); preference.removeEventListener("change", onChange); };
  }, []);

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
    <div className="login-page" ref={rootRef}>
      <a className="login-skip" href="#login-form">Pular para o formulário</a>
      <header className="login-topbar">
        <div className="login-topbar-inner">
          <Link to="/" className="login-logo" aria-label="CurrículoAI — início"><Wordmark height={22} tone="white" /></Link>
          <Link to="/" className="login-back"><ArrowLeft size={17} aria-hidden="true" /><span>Voltar ao início</span></Link>
        </div>
      </header>
      <main className="login-main">
        <section className="login-story" aria-labelledby="login-story-title">
          <SymbolOutline className="login-story-outline" strokeWidth={1.1} />
          <div className="login-story-copy" data-login-enter="40">
            <p className="login-eyebrow"><span /> Sua trajetória, em destaque</p>
            <h2 id="login-story-title">Conectar você <span>ao mercado.</span></h2>
          </div>
          <div className="login-illustration" aria-hidden="true">
            <div className="login-document" data-login-enter="180">
              <div className="login-document-head"><Symbol size={25} /><span>CURRÍCULO</span><FileText size={17} /></div>
              <div className="login-document-name" /><div className="login-document-caption" />
              {["Resumo profissional", "Experiência", "Educação"].map((label) => (
                <div className="login-document-section" key={label}><b>{label}</b><i /><i /><i /></div>
              ))}
            </div>
            <div className="login-document-seal" data-login-enter="360"><Check size={19} strokeWidth={3} /><span>Sua trajetória,<br /><b>em PDF.</b></span></div>
            <div className="login-illustration-note" data-login-enter="300"><Sparkles size={16} />Você preenche.<br />A IA escreve.</div>
          </div>
          <p className="login-story-description" data-login-enter="260">Preencha sua trajetória uma vez. A IA transforma tudo em um currículo pronto para os filtros de recrutamento.</p>
          <div className="login-story-bottom"><span>Nova identidade. A mesma missão.</span><Symbol size={22} /></div>
        </section>

        <section className="login-panel" aria-labelledby="login-title">
          <div className="login-panel-heading" data-login-enter="70">
            <p className="login-eyebrow"><span /> Bem-vindo de volta</p>
            <h1 id="login-title">Entrar na sua conta.</h1>
            <p className="login-intro">Continue de onde parou e gere seu currículo com IA.</p>
          </div>
          {/* Reserve one feedback row so common error/success messages do not move the fields. */}
          <div className="login-feedback">
            {error ? <div id="login-feedback" className="login-message login-message--error" role="alert"><CircleAlert size={18} aria-hidden="true" /><p>{error}</p></div>
              : state?.registered ? <div id="login-feedback" className="login-message" role="status"><Check size={18} aria-hidden="true" /><p>Conta criada! Agora é só entrar com sua senha.</p></div> : null}
          </div>
          <form id="login-form" className="login-form" onSubmit={onSubmit} noValidate aria-busy={loading} aria-labelledby="login-title" tabIndex={-1}>
            <div data-login-enter="160">
              <Field label="E-mail" htmlFor="email" className="login-field">
                <div className="input-wrap">
                  <Mail size={18} aria-hidden="true" />
                  <input
                    id="email"
                    className="input"
                    type="email"
                    autoComplete="email"
                    placeholder="voce@email.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    aria-describedby={error ? "login-feedback" : undefined}
                  />
                </div>
              </Field>
            </div>
            <div data-login-enter="220">
              <Field label="Senha" htmlFor="password" className="login-field">
                <div className="input-wrap">
                  <Lock size={18} aria-hidden="true" />
                  <input
                    id="password"
                    className="input"
                    type={show ? "text" : "password"}
                    autoComplete="current-password"
                    placeholder="Sua senha"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    aria-describedby={error ? "login-feedback" : undefined}
                  />
                  <Button
                    variant="ghost"
                    size="sm"
                    iconOnly
                    className="input-action"
                    onClick={() => setShow((s) => !s)}
                    aria-label={show ? "Ocultar senha" : "Mostrar senha"}
                    aria-pressed={show}
                    aria-controls="password"
                  >
                    <span className="login-eye" key={show ? "visible" : "hidden"} aria-hidden="true">{show ? <EyeOff size={18} /> : <Eye size={18} />}</span>
                  </Button>
                </div>
              </Field>
            </div>
            <div data-login-enter="300">
              <Button type="submit" className="login-submit" size="lg" block loading={loading} disabled={!email || !password}>
                <span className="login-submit-label">{loading ? "Entrando…" : "Entrar"}</span>
                {!loading && <ArrowRight size={19} aria-hidden="true" />}
              </Button>
            </div>
          </form>
          <div className="login-register" data-login-enter="360"><p>Ainda não tem conta?</p><Link to="/cadastro"><span>Criar conta grátis</span><ArrowRight size={16} aria-hidden="true" /></Link></div>
        </section>
      </main>
    </div>
  );
}
