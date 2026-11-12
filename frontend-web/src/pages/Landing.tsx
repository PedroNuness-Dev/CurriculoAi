import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ArrowRight, FileText, PenLine, ScanSearch, Sparkles } from "lucide-react";
import { Symbol, SymbolOutline, Wordmark } from "../components/brand/Brand";
import { useAuth } from "../auth/AuthContext";

const STEPS = [
  { icon: <PenLine size={20} />, title: "Você preenche", text: "Experiências, formação, habilidades, idiomas e projetos — tudo em um só lugar." },
  { icon: <Sparkles size={20} />, title: "A IA escreve", text: "Verbos de ação, resumo profissional e a ordem certa das seções." },
  { icon: <ScanSearch size={20} />, title: "Passa no ATS", text: "Formatação pensada para os filtros automáticos de recrutamento." },
  { icon: <FileText size={20} />, title: "PDF pronto", text: "Baixe o currículo e candidate-se na hora." },
];

export default function Landing() {
  const { session } = useAuth();
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 8);
    onScroll();
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  return (
    <div className="landing">
      <header className={`lp-nav ${scrolled ? "scrolled" : ""}`}>
        <Link to="/" aria-label="CurrículoAI — início">
          <Wordmark height={22} />
        </Link>
        <div className="row" style={{ gap: 8 }}>
          {session ? (
            <Link to="/app" className="btn btn-primary">
              Ir para o painel <ArrowRight size={18} />
            </Link>
          ) : (
            <>
              <Link to="/entrar" className="btn btn-ghost">
                Entrar
              </Link>
              <Link to="/cadastro" className="btn btn-primary">
                Criar conta
              </Link>
            </>
          )}
        </div>
      </header>

      <section className="lp-hero">
        <SymbolOutline className="lp-hero-outline" strokeWidth={3} />
        <span className="badge lime">
          <Sparkles size={14} /> Currículos gerados com IA
        </span>
        <h1>
          Seu currículo reflete o seu <span className="text-grad">verdadeiro potencial?</span>
        </h1>
        <p className="lead">
          Se você teve que <b>pensar demais</b> sobre a resposta… a gente resolve. Você preenche seus dados, a IA monta o
          currículo. Simples assim.
        </p>
        <div className="lp-hero-cta">
          <Link to={session ? "/app" : "/cadastro"} className="btn btn-primary btn-lg">
            Conheça nosso sistema <ArrowRight size={19} />
          </Link>
          {!session && (
            <Link to="/entrar" className="btn btn-secondary btn-lg">
              Já tenho conta
            </Link>
          )}
        </div>

        <div className="display lp-slogan text-grad" aria-label="Conectar você ao mercado.">
          Conectar você
          <br />
          ao mercado.
        </div>
      </section>

      <section className="lp-band">
        <div className="lp-band-inner">
          <div style={{ display: "flex", justifyContent: "center" }}>
            <Wordmark height={30} tone="white" />
          </div>
          <div className="lp-bento">
            <div className="bento-card">
              <h3>
                Tecnologia
                <br />
                que te
                <br />
                <span className="text-grad">
                  ingressa
                  <br />
                  na área.
                </span>
              </h3>
            </div>
            <div className="bento-card photo" style={{ backgroundImage: "url(/brand/foto-escritorio.jpg)" }}>
              <div className="bento-symbol">
                <Symbol size={120} tone="white" />
              </div>
            </div>
            <div className="bento-card">
              <h3>
                Destaque-se
                <br />
                <span className="text-grad">Empregue-se</span>
                <br />
                Progrida.
              </h3>
            </div>
            <div className="bento-card photo" style={{ backgroundImage: "url(/brand/foto-equipe.jpg)" }} />
            <div className="bento-card">
              <div className="bento-stat text-grad">88%</div>
              <p>
                dos empregadores reconhecem que seus processos automatizados de recrutamento podem descartar candidatos
                qualificados antes mesmo de uma avaliação humana.
              </p>
            </div>
            <div className="bento-card photo" style={{ backgroundImage: "url(/brand/foto-home-office.jpg)" }} />
          </div>
        </div>
      </section>

      <section className="lp-steps">
        <div className="eyebrow">Como funciona</div>
        <h2 style={{ fontSize: "clamp(30px, 4vw, 46px)", marginTop: 10, letterSpacing: "-0.04em" }}>
          Do formulário ao PDF em <span className="text-grad">minutos.</span>
        </h2>
        <div className="lp-steps-grid">
          {STEPS.map((s, i) => (
            <div className="step-card" key={s.title}>
              <div className="row between">
                <span className="step-num text-grad">0{i + 1}</span>
                <span className="icon-tile">{s.icon}</span>
              </div>
              <h3>{s.title}</h3>
              <p>{s.text}</p>
            </div>
          ))}
        </div>
        <div style={{ marginTop: 40, display: "flex", justifyContent: "center" }}>
          <Link to={session ? "/app/gerar" : "/cadastro"} className="btn btn-lime btn-lg">
            <Sparkles size={19} /> Gerar meu currículo
          </Link>
        </div>
      </section>

      <footer className="lp-footer">
        <Wordmark height={16} />
        <span>Nova identidade. A mesma missão: conectar você ao mercado.</span>
      </footer>
    </div>
  );
}
