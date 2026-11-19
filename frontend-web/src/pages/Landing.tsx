import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";
import { ArrowDown, ArrowRight, Check, FileText, PenLine, ScanSearch, Sparkles } from "lucide-react";
import { Symbol, SymbolOutline, Wordmark } from "../components/brand/Brand";
import { useAuth } from "../auth/AuthContext";
import navbarLogo from "../assets/brand/nav-bar.svg";
import "../styles/landing.css";

const STEPS = [
  { icon: <PenLine size={22} />, title: "Você preenche", text: "Experiências, formação, habilidades, idiomas e projetos — tudo em um só lugar." },
  { icon: <Sparkles size={22} />, title: "A IA escreve", text: "Verbos de ação, resumo profissional e a ordem certa das seções." },
  { icon: <ScanSearch size={22} />, title: "Passa no ATS", text: "Formatação pensada para os filtros automáticos de recrutamento." },
  { icon: <FileText size={22} />, title: "PDF pronto", text: "Baixe o currículo e candidate-se na hora." },
];

export default function Landing() {
  const { session } = useAuth();
  const rootRef = useRef<HTMLDivElement>(null);
  const paperRef = useRef<HTMLDivElement>(null);
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 8);
    onScroll();
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  useEffect(() => {
    const root = rootRef.current;
    if (!root) return;
    const preference = window.matchMedia("(prefers-reduced-motion: reduce)");
    const animations: Animation[] = [];
    let observer: IntersectionObserver | undefined;

    const animate = (element: Element, frames: Keyframe[], delay = 0, duration = 700) => {
      if (!element.animate) return;
      animations.push(element.animate(frames, {
        duration, delay, easing: "cubic-bezier(.22, 1, .36, 1)", fill: "backwards",
      }));
    };

    const stop = () => {
      observer?.disconnect();
      animations.forEach((animation) => animation.cancel());
      animations.length = 0;
    };

    const introduceCta = (element: Element) => {
      const delay = window.scrollY < 32 ? Number(element.getAttribute("data-cta-delay")) : 0;
      const label = element.querySelector(".lp-cta-label");
      const icon = element.querySelector(".lp-cta-spark, .lp-cta-static");
      animate(element, [{ opacity: 0.72 }, { opacity: 1 }], delay, 380);
      if (label) animate(label, [
        { transform: "translateY(8px)", opacity: 0.35 },
        { transform: "translateY(0)", opacity: 1 },
      ], delay, 560);
      if (icon) animate(icon, [
        { transform: "translateX(-7px) rotate(-12deg)", opacity: 0.2 },
        { transform: "translateX(0) rotate(0deg)", opacity: 1 },
      ], delay + 65, 550);
    };

    const introducePaper = (element: Element) => {
      const compact = window.matchMedia("(max-width: 900px)").matches;
      const delay = window.scrollY < 32 ? 160 : 0;
      animate(element, [
        { opacity: 0, transform: compact
          ? "perspective(1000px) translate3d(0, 18px, -18px) rotateX(4deg) rotateY(-4deg) scale(.98)"
          : "perspective(1000px) translate3d(18px, 28px, -45px) rotateX(9deg) rotateY(-10deg) scale(.97)" },
        { opacity: 1, transform: "perspective(1000px) translate3d(0, 0, 0) rotateX(0deg) rotateY(0deg) scale(1)" },
      ], delay, compact ? 740 : 900);
      element.querySelectorAll(".lp-paper-top, .lp-paper-name, .lp-paper-caption, .lp-paper-section, .lp-paper-footer").forEach((part, index) => {
        animate(part, [{ opacity: 0.4, transform: "translateY(5px)" }, { opacity: 1, transform: "translateY(0)" }], delay + 140 + index * 45, 420);
      });
    };

    // Content is visible by default. Motion never owns its final visibility.
    if (!preference.matches) {
      root.querySelectorAll("[data-enter]").forEach((element) => {
        const delay = Number(element.getAttribute("data-enter"));
        animate(element, [{ opacity: 0, transform: "translateY(22px)" }, { opacity: 1, transform: "translateY(0)" }], delay, 800);
      });

      if ("IntersectionObserver" in window) {
        observer = new IntersectionObserver((entries) => {
          entries.forEach((entry) => {
            if (!entry.isIntersecting) return;
            const element = entry.target;
            if (element.hasAttribute("data-paper-reveal")) {
              introducePaper(element);
            } else if (element.hasAttribute("data-cta")) {
              introduceCta(element);
            } else if (element.classList.contains("lp-process")) {
              const rail = element.querySelector(".lp-process-rail");
              if (rail) animate(rail, [{ transform: "scaleX(0)" }, { transform: "scaleX(1)" }], 0, 1100);
            } else if (element.classList.contains("lp-step")) {
              const body = element.querySelector(".lp-step-body");
              const number = element.querySelector(".lp-step-number");
              const index = Number(element.getAttribute("data-step-index"));
              const delay = window.matchMedia("(min-width: 901px)").matches ? index * 100 : 0;
              if (number) animate(number, [{ opacity: 0, transform: "scale(.8)" }, { opacity: 1, transform: "scale(1)" }], delay, 550);
              if (body) animate(body, [{ opacity: 0, transform: "translateY(26px)" }, { opacity: 1, transform: "translateY(0)" }], delay + 90);
            } else {
              animate(element, [{ opacity: 0, transform: "translateY(24px)" }, { opacity: 1, transform: "translateY(0)" }]);
            }
            observer?.unobserve(element);
          });
        }, { threshold: 0.12 });
        root.querySelectorAll("[data-reveal], .lp-step, [data-cta], [data-paper-reveal]").forEach((element) => observer?.observe(element));
      }
    }

    const onPreferenceChange = () => { if (preference.matches) stop(); };
    preference.addEventListener("change", onPreferenceChange);
    return () => { stop(); preference.removeEventListener("change", onPreferenceChange); };
  }, []);

  useEffect(() => {
    const stage = paperRef.current;
    if (!stage) return;
    const reduced = window.matchMedia("(prefers-reduced-motion: reduce)");
    const desktop = window.matchMedia("(hover: hover) and (pointer: fine) and (min-width: 901px)");
    const properties = ["rx", "ry", "x", "y", "light-x", "light-y", "shadow-x", "shadow-y"];
    let frame = 0;
    let pointerX = 0;
    let pointerY = 0;

    const reset = () => {
      cancelAnimationFrame(frame);
      frame = 0;
      stage.removeAttribute("data-paper-active");
      properties.forEach((property) => stage.style.removeProperty(`--paper-${property}`));
    };
    const followPointer = (event: PointerEvent) => {
      if (reduced.matches || !desktop.matches || event.pointerType !== "mouse") return;
      pointerX = event.clientX;
      pointerY = event.clientY;
      if (frame) return;
      frame = requestAnimationFrame(() => {
        frame = 0;
        // Measure the stable wrapper, never the tilted paper itself.
        const bounds = stage.getBoundingClientRect();
        if (!bounds.width || !bounds.height) return;
        const x = Math.max(-1, Math.min(1, (pointerX - bounds.left) / bounds.width * 2 - 1));
        const y = Math.max(-1, Math.min(1, (pointerY - bounds.top) / bounds.height * 2 - 1));
        const values = [`${-y * 3}deg`, `${x * 4}deg`, `${x * 2}px`, `${y * 1.5}px`, `${(x + 1) * 50}%`, `${(y + 1) * 50}%`, `${-x * 5}px`, `${28 - y * 3}px`];
        properties.forEach((property, index) => stage.style.setProperty(`--paper-${property}`, values[index]));
        stage.setAttribute("data-paper-active", "");
      });
    };

    stage.addEventListener("pointerenter", followPointer);
    stage.addEventListener("pointermove", followPointer);
    stage.addEventListener("pointerleave", reset);
    stage.addEventListener("pointercancel", reset);
    window.addEventListener("blur", reset);
    reduced.addEventListener("change", reset);
    desktop.addEventListener("change", reset);
    return () => {
      reset();
      stage.removeEventListener("pointerenter", followPointer);
      stage.removeEventListener("pointermove", followPointer);
      stage.removeEventListener("pointerleave", reset);
      stage.removeEventListener("pointercancel", reset);
      window.removeEventListener("blur", reset);
      reduced.removeEventListener("change", reset);
      desktop.removeEventListener("change", reset);
    };
  }, []);

  return (
    <div className="landing" ref={rootRef}>
      <a className="lp-skip" href="#conteudo">Pular para o conteúdo</a>
      <header className={`lp-nav ${scrolled ? "scrolled" : ""}`}>
        <div className="lp-nav-inner">
          <Link to="/" className="lp-logo" aria-label="CurrículoAI — início"><img src={navbarLogo} width={400} height={225} alt="" /></Link>
          <nav className="lp-nav-actions" aria-label="Acesso à conta">
            {session ? (
              <Link to="/app" className="btn btn-primary lp-cta lp-cta--account" data-cta data-cta-delay="70">
                <span className="lp-cta-label">Ir para o painel</span>
                <span className="lp-cta-static" aria-hidden="true"><ArrowRight size={17} /></span>
              </Link>
            ) : (
              <>
                <Link to="/entrar" className="btn btn-ghost lp-cta lp-cta--login" data-cta data-cta-delay="0"><span className="lp-cta-label">Entrar</span></Link>
                <Link to="/cadastro" className="btn btn-primary lp-cta lp-cta--account" data-cta data-cta-delay="70">
                  <span className="lp-cta-label">Criar conta</span>
                  <span className="lp-cta-static" aria-hidden="true"><ArrowRight size={16} /></span>
                </Link>
              </>
            )}
          </nav>
        </div>
      </header>

      <main id="conteudo">
        <section className="lp-hero" aria-labelledby="lp-title">
          <div className="lp-hero-grid">
            <div className="lp-hero-copy">
              <div className="lp-kicker" data-enter="0"><span /> Currículos gerados com IA</div>
              <h1 id="lp-title">
                <span className="lp-title-intro" data-enter="90">Seu currículo reflete o seu</span>{" "}
                <span className="lp-title-display text-grad" data-enter="180">verdadeiro <br />potencial?</span>
              </h1>
              <p className="lp-lead" data-enter="290">Se você teve que <b>pensar demais</b> sobre a resposta… a gente resolve. Você preenche seus dados, a IA monta o currículo. Simples assim.</p>
              <div className="lp-hero-cta">
                <Link to={session ? "/app" : "/cadastro"} className="btn btn-primary btn-lg lp-cta lp-cta--discover" data-cta data-cta-delay="390">
                  <span className="lp-cta-label">Conheça nosso sistema</span>
                  <span className="lp-cta-static" aria-hidden="true"><ArrowRight size={19} /></span>
                </Link>
                {!session && <Link to="/entrar" className="btn btn-secondary btn-lg lp-cta lp-cta--return" data-cta data-cta-delay="470"><span className="lp-cta-label">Já tenho conta</span></Link>}
              </div>
            </div>

            <div className="lp-visual" aria-hidden="true">
              <div className="lp-visual-backdrop" data-enter="90"><SymbolOutline className="lp-visual-outline" strokeWidth={1.4} /></div>
              <div className="lp-paper-stage" ref={paperRef} data-paper-reveal>
                <div className="lp-paper">
                  <div className="lp-paper-top"><Symbol size={28} /><span>CURRÍCULO</span><FileText size={17} /></div>
                  <div className="lp-paper-name" /><div className="lp-paper-caption" />
                  {["Resumo profissional", "Experiência", "Educação"].map((label) => (
                    <div className="lp-paper-section" key={label}><b>{label}</b><span /><span /><span /></div>
                  ))}
                  <div className="lp-paper-footer"><span /><span /><span /></div>
                </div>
              </div>
              <div className="lp-pdf-label" data-enter="630"><span><Check size={16} strokeWidth={3} /></span><div><b>Sua trajetória, em PDF.</b><small>Com a identidade de quem você é.</small></div><FileText size={22} /></div>
              <div className="lp-visual-note" data-enter="740"><Sparkles size={14} /> Você preenche. A IA escreve.</div>
            </div>
          </div>

          <div className="lp-hero-bottom" data-enter="500">
            <p className="lp-signature">Conectar você <br /><span className="text-grad">ao mercado.</span></p>
            <a className="lp-scroll-link" href="#como-funciona" aria-label="Como funciona"><span>Como funciona</span><span className="lp-scroll-icon"><ArrowDown size={18} /></span></a>
          </div>
        </section>

        <section className="lp-steps" id="como-funciona" aria-labelledby="lp-steps-title">
          <div className="lp-steps-inner">
            <div className="lp-steps-heading" data-reveal>
              <span className="eyebrow"><span className="lp-section-mark" />Como funciona</span>
              <h2 id="lp-steps-title">Do formulário ao PDF <br /><span className="lp-heading-display text-grad">em minutos.</span></h2>
              <span className="lp-section-index" aria-hidden="true">01 — 04</span>
            </div>

            <div className="lp-process" data-reveal>
              <span className="lp-process-rail" aria-hidden="true" />
              <ol className="lp-steps-grid">
                {STEPS.map((step, index) => (
                  <li className="lp-step" key={step.title} data-step-index={index}>
                    <span className="lp-step-number" aria-hidden="true">0{index + 1}</span>
                    <div className="lp-step-body"><span className="lp-step-icon" aria-hidden="true">{step.icon}</span><h3>{step.title}</h3><p>{step.text}</p></div>
                  </li>
                ))}
              </ol>
            </div>

            <div className="lp-finish">
              <span className="lp-finish-symbol" aria-hidden="true"><Symbol size={44} /></span>
              <Link to={session ? "/app/gerar" : "/cadastro"} className="btn btn-lime btn-lg lp-cta lp-cta--generate" data-cta data-cta-delay="0">
                <span className="lp-cta-spark" aria-hidden="true"><Sparkles size={19} /></span>
                <span className="lp-cta-label">Gerar meu currículo</span>
                <span className="lp-cta-static" aria-hidden="true"><ArrowRight size={18} /></span>
              </Link>
            </div>
          </div>
        </section>
      </main>

      <footer className="lp-footer"><div className="lp-footer-inner"><Wordmark height={17} /><span>Nova identidade. A mesma missão: conectar você ao mercado.</span></div></footer>
    </div>
  );
}
