import { useEffect, useMemo, useRef, useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ArrowLeft, ArrowRight, CircleAlert, Eye, EyeOff, FileText, Lock, Mail, PenLine, Sparkles, User } from "lucide-react";
import { Symbol, SymbolOutline, Wordmark } from "../components/brand/Brand";
import { Button, Field } from "../components/ui/ui";
import { authApi } from "../api/endpoints";
import { useAuth } from "../auth/AuthContext";
import { ApiError } from "../api/client";
import { useToast } from "../components/ui/Toast";
import "../styles/login.css";
import "../styles/register.css";

function strength(pw: string) {
  let s = 0;
  if (pw.length >= 6) s++;
  if (pw.length >= 10) s++;
  if (/[A-Z]/.test(pw) && /[a-z]/.test(pw)) s++;
  if (/\d/.test(pw) && /[^A-Za-z0-9]/.test(pw)) s++;
  return s;
}

const COLORS = ["var(--line)", "var(--danger)", "var(--warning)", "var(--brand-400)", "var(--success)"];
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
  const rootRef = useRef<HTMLDivElement>(null);

  const s = useMemo(() => strength(password), [password]);
  const emailOk = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim());
  const valid = name.trim().length >= 2 && emailOk && password.length >= 6;

  useEffect(() => {
    const root = rootRef.current;
    if (!root) return;
    const preference = window.matchMedia("(prefers-reduced-motion: reduce)");
    const animations: Animation[] = [];
    if (!preference.matches) {
      const compact = window.matchMedia("(max-width: 800px)").matches;
      root.querySelectorAll<HTMLElement>("[data-register-enter]").forEach((element) => {
        if (!element.animate || !element.getClientRects().length) return;
        const step = element.classList.contains("register-step");
        animations.push(element.animate([
          { opacity: 0, transform: step ? "translate3d(-16px, 8px, 0)" : `translateY(${compact ? 10 : 18}px)` },
          { opacity: 1, transform: "translate3d(0, 0, 0)" },
        ], {
          duration: step ? 700 : 600,
          delay: Number(element.dataset.registerEnter),
          easing: "cubic-bezier(.22, 1, .36, 1)",
          fill: "backwards",
        }));
      });
      const rail = root.querySelector(".register-path-rail");
      if (rail?.animate && rail.getClientRects().length) animations.push(rail.animate([
        { transform: "scaleY(0)" }, { transform: "scaleY(1)" },
      ], { duration: 950, delay: 160, easing: "cubic-bezier(.22, 1, .36, 1)", fill: "backwards" }));
    }
    const stop = () => animations.forEach((animation) => animation.cancel());
    const onChange = () => { if (preference.matches) stop(); };
    preference.addEventListener("change", onChange);
    return () => { stop(); preference.removeEventListener("change", onChange); };
  }, []);

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
    <div className="login-page register-page" ref={rootRef}>
      <a className="login-skip" href="#register-form">Pular para o formulário</a>
      <header className="login-topbar">
        <div className="login-topbar-inner">
          <Link to="/" className="login-logo" aria-label="CurrículoAI — início"><Wordmark height={22} tone="white" /></Link>
          <Link to="/" className="login-back"><ArrowLeft size={17} aria-hidden="true" /><span>Voltar ao início</span></Link>
        </div>
      </header>
      <main className="login-main register-main">
        <section className="login-story register-story" aria-labelledby="register-story-title">
          <SymbolOutline className="register-story-outline" strokeWidth={1.1} />
          <div data-register-enter="40">
            <p className="login-eyebrow"><span /> Conectar você ao mercado</p>
            <h2 id="register-story-title">Sua história.<span>Novas<br />possibilidades.</span></h2>
            <p className="register-story-intro">Uma conta para reunir sua trajetória e dar forma ao seu próximo currículo.</p>
          </div>
          <div className="register-path-group">
            <span className="register-path-rail" aria-hidden="true" />
            <ol className="register-path">
              <li className="register-step" data-register-enter="160"><span className="register-step-icon"><PenLine size={21} aria-hidden="true" /></span><div><span className="register-step-index">01 / SUA TRAJETÓRIA</span><h3>Você preenche.</h3><p>Experiências, formação e habilidades em um só lugar.</p></div></li>
              <li className="register-step" data-register-enter="250"><span className="register-step-icon"><Sparkles size={21} aria-hidden="true" /></span><div><span className="register-step-index">02 / INTELIGÊNCIA ARTIFICIAL</span><h3>A IA escreve.</h3><p>Suas informações se transformam em um currículo profissional.</p></div></li>
              <li className="register-step" data-register-enter="340"><span className="register-step-icon"><FileText size={21} aria-hidden="true" /></span><div><span className="register-step-index">03 / SEU PRÓXIMO PASSO</span><h3>Seu currículo, em PDF.</h3><p>Baixe o documento e candidate-se.</p></div></li>
            </ol>
          </div>
          <div className="login-story-bottom"><span>Nova identidade. A mesma missão.</span><Symbol size={22} /></div>
        </section>
        <section className="login-panel register-panel" aria-labelledby="register-title">
          <div data-register-enter="70">
            <p className="login-eyebrow"><span /> Comece agora</p>
            <h1 id="register-title">Criar sua conta.</h1>
            <p className="login-intro">Leva menos de um minuto. Depois é só preencher sua trajetória.</p>
          </div>
          <div className="login-feedback register-feedback">
            {error && <div className="login-message login-message--error" role="alert"><CircleAlert size={18} aria-hidden="true" /><p>{error}</p></div>}
          </div>
          <form id="register-form" className="login-form register-form" onSubmit={onSubmit} noValidate aria-busy={loading} aria-labelledby="register-title" tabIndex={-1}>
            <div data-register-enter="150">
              <Field label="Nome completo" htmlFor="name" className="login-field">
                <div className="input-wrap">
                  <User size={18} aria-hidden="true" />
                  <input
                    id="name"
                    className="input"
                    autoComplete="name"
                    placeholder="Como você se chama?"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    aria-invalid={touched && name.trim().length < 2}
                    aria-describedby="register-name-feedback"
                  />
                </div>
                <span id="register-name-feedback" className="register-field-feedback" aria-live="polite">{touched && name.trim().length < 2 ? "Informe seu nome" : ""}</span>
              </Field>
            </div>
            <div data-register-enter="210">
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
                    aria-invalid={touched && !emailOk}
                    aria-describedby="register-email-feedback"
                  />
                </div>
                <span id="register-email-feedback" className="register-field-feedback" aria-live="polite">{touched && !emailOk ? "Informe um e-mail válido" : ""}</span>
              </Field>
            </div>
            <div data-register-enter="270">
              <Field
                label="Senha"
                htmlFor="password"
                className="login-field"
              >
                <div className="input-wrap">
                  <Lock size={18} aria-hidden="true" />
                  <input
                    id="password"
                    className="input"
                    type={show ? "text" : "password"}
                    autoComplete="new-password"
                    placeholder="Crie uma senha"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    aria-invalid={touched && password.length < 6}
                    aria-describedby="register-password-feedback"
                  />
                  <Button
                    variant="ghost"
                    size="sm"
                    iconOnly
                    className="input-action"
                    onClick={() => setShow((v) => !v)}
                    aria-label={show ? "Ocultar senha" : "Mostrar senha"}
                    aria-pressed={show}
                    aria-controls="password"
                  >
                    <span className="login-eye" key={show ? "visible" : "hidden"} aria-hidden="true">{show ? <EyeOff size={18} /> : <Eye size={18} />}</span>
                  </Button>
                </div>
                <div className="pw-meter register-strength" aria-hidden="true">
                  {[1, 2, 3, 4].map((i) => (
                    <span key={i} style={{ background: s >= i ? COLORS[s] : undefined }} />
                  ))}
                </div>
                <span id="register-password-feedback" className={`register-field-feedback ${touched && password.length < 6 ? "" : "register-password-hint"}`} aria-live="polite">{touched && password.length < 6 ? "Use pelo menos 6 caracteres" : password ? `Força: ${LABELS[s] || "Muito fraca"}` : "Mínimo de 6 caracteres"}</span>
              </Field>
            </div>
            <div data-register-enter="330">
              <Button type="submit" className="login-submit register-submit" size="lg" block loading={loading}>
                <span className="login-submit-label">{loading ? "Criando conta…" : "Criar conta"}</span>
                {!loading && <ArrowRight size={19} aria-hidden="true" />}
              </Button>
            </div>
          </form>
          <div className="login-register" data-register-enter="390"><p>Já tem conta?</p><Link to="/entrar"><span>Entrar</span><ArrowRight size={16} aria-hidden="true" /></Link></div>
        </section>
      </main>
    </div>
  );
}
