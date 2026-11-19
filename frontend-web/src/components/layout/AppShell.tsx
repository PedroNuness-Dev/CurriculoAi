import { useEffect, useRef, useState } from "react";
import { Link, NavLink, Outlet, useLocation } from "react-router-dom";
import {
  Award,
  BookOpen,
  Briefcase,
  Database,
  FolderGit2,
  GraduationCap,
  LayoutDashboard,
  Languages as LanguagesIcon,
  Link2,
  LogOut,
  Menu,
  Sparkles,
  UserRound,
  X,
} from "lucide-react";
import { useAuth } from "../../auth/AuthContext";
import { completeness, useProfile } from "../../auth/ProfileContext";
import { firstName, initials } from "../../lib/format";
import { Wordmark } from "../brand/Brand";
import { Button } from "../ui/ui";
import "../../styles/app.css";

const PAGE_NAMES: Record<string, string> = {
  "/app": "Visão geral", "/app/perfil": "Sobre você", "/app/experiencias": "Experiências",
  "/app/formacao": "Formação", "/app/certificacoes": "Certificações", "/app/projetos": "Projetos",
  "/app/habilidades": "Habilidades", "/app/idiomas": "Idiomas", "/app/redes": "Redes e links",
  "/app/gerar": "Gerar com IA", "/app/catalogos": "Catálogos",
};

export default function AppShell() {
  const { session, logout } = useAuth();
  const { profile, loading } = useProfile();
  const [open, setOpen] = useState(false);
  const location = useLocation();
  const sidebarRef = useRef<HTMLElement>(null);
  const mainRef = useRef<HTMLElement>(null);
  const menuRef = useRef<HTMLButtonElement>(null);
  const previousPath = useRef(location.pathname);

  useEffect(() => {
    setOpen(false);
    window.scrollTo({ top: 0 });
    if (previousPath.current !== location.pathname) mainRef.current?.focus({ preventScroll: true });
    previousPath.current = location.pathname;
  }, [location.pathname]);

  useEffect(() => {
    const desktop = window.matchMedia("(min-width: 981px)");
    const closeOnDesktop = () => { if (desktop.matches) setOpen(false); };
    desktop.addEventListener("change", closeOnDesktop);
    return () => desktop.removeEventListener("change", closeOnDesktop);
  }, []);

  useEffect(() => {
    if (!open) return;
    const sidebar = sidebarRef.current;
    if (!sidebar) return;
    const overflow = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    const focusFrame = requestAnimationFrame(() => {
      (sidebar.querySelector<HTMLElement>(".nav-link.active") ?? sidebar.querySelector<HTMLElement>(".nav-link"))?.focus();
    });
    const onKey = (event: KeyboardEvent) => {
      if (event.key === "Escape") { event.preventDefault(); setOpen(false); }
      if (event.key !== "Tab") return;
      const controls = [...sidebar.querySelectorAll<HTMLElement>("a[href], button:not(:disabled)")].filter((element) => element.getClientRects().length);
      const first = controls[0], last = controls[controls.length - 1];
      if (!sidebar.contains(document.activeElement)) { event.preventDefault(); (event.shiftKey ? last : first)?.focus(); }
      else if (event.shiftKey && document.activeElement === first) { event.preventDefault(); last?.focus(); }
      else if (!event.shiftKey && document.activeElement === last) { event.preventDefault(); first?.focus(); }
    };
    document.addEventListener("keydown", onKey);
    return () => {
      document.body.style.overflow = overflow;
      cancelAnimationFrame(focusFrame);
      document.removeEventListener("keydown", onKey);
      if (sidebar.contains(document.activeElement)) menuRef.current?.focus();
    };
  }, [open]);

  useEffect(() => {
    if (loading) return;
    const preference = window.matchMedia("(prefers-reduced-motion: reduce)");
    const animations: Animation[] = [];
    const stop = () => animations.forEach((animation) => animation.cancel());
    if (!preference.matches) {
      const elements = mainRef.current?.querySelectorAll(".page-header, .hero-card, .stats-grid, .two-col > .card, .item-list > .item, .cat-grid > .item, .empty, .gen-stage, .tabs, .stack-lg > .card");
      [...(elements ?? [])].slice(0, 10).forEach((element, index) => {
        if (!element.animate) return;
        animations.push(element.animate([
          { opacity: .35, transform: `translateY(${element.classList.contains("page-header") ? 8 : 14}px)` },
          { opacity: 1, transform: "translateY(0)" },
        ], { duration: 420, delay: Math.min(index * 45, 180), easing: "cubic-bezier(.22, 1, .36, 1)", fill: "backwards" }));
      });
    }
    const onChange = () => { if (preference.matches) stop(); };
    preference.addEventListener("change", onChange);
    return () => { stop(); preference.removeEventListener("change", onChange); };
  }, [location.pathname, loading]);

  const c = completeness(profile);
  const count = (n: number | undefined) => (n ? <span className="nav-count">{n}</span> : null);
  const dot = (done: boolean) => (done ? <span className="nav-dot" aria-label="preenchido" /> : null);
  const aboutDone = !!(profile?.area && profile?.careerGoal && profile?.address && profile?.availability);

  return (
    <div className={`shell app-workspace ${open ? "nav-open" : ""}`}>
      <a className="workspace-skip" href="#workspace-main">Pular para o conteúdo</a>
      <header className="workspace-header">
        <button ref={menuRef} type="button" className="btn btn-white btn-icon workspace-menu" onClick={() => setOpen((value) => !value)} aria-label={open ? "Fechar menu" : "Abrir menu"} aria-expanded={open} aria-controls="workspace-navigation">{open ? <X size={20} /> : <Menu size={20} />}</button>
        <Link to="/app" className="workspace-logo" aria-label="CurrículoAI — visão geral"><Wordmark height={22} tone="white" /></Link>
        <span className="workspace-header-caption">Seu espaço profissional</span>
        <Link to="/app/perfil" className="workspace-account" aria-label="Meu perfil"><span className="avatar">{initials(session?.name)}</span><span>{firstName(session?.name)}</span></Link>
      </header>
      {open && <div className="scrim" onClick={() => setOpen(false)} />}

      <aside id="workspace-navigation" ref={sidebarRef} className="sidebar" aria-label="Navegação principal" role={open ? "dialog" : undefined} aria-modal={open || undefined}>
        <div className="workspace-sidebar-head"><span>Seu currículo</span><Button variant="ghost" iconOnly className="workspace-menu-close" onClick={() => setOpen(false)} aria-label="Fechar menu"><X size={19} /></Button></div>
        <div className="workspace-profile-progress">
          <div><span>{loading ? "Carregando perfil…" : "Perfil preenchido"}</span><b>{loading ? "—" : `${c.percent}%`}</b></div>
          <div className="progress on-light"><span style={{ width: `${c.percent}%` }} /></div>
        </div>
        <div className="workspace-nav-scroll">
          <nav className="nav-group">
            <NavLink to="/app" end className="nav-link">
              <LayoutDashboard size={19} /> Visão geral
            </NavLink>
            <NavLink to="/app/perfil" className="nav-link">
              <UserRound size={19} /> Sobre você {dot(aboutDone)}
            </NavLink>
          </nav>

          <div className="nav-label">Trajetória</div>
          <nav className="nav-group">
            <NavLink to="/app/experiencias" className="nav-link">
              <Briefcase size={19} /> Experiências {count(profile?.experiences.length)}
            </NavLink>
            <NavLink to="/app/formacao" className="nav-link">
              <GraduationCap size={19} /> Formação {count(profile?.formations.length)}
            </NavLink>
            <NavLink to="/app/certificacoes" className="nav-link">
              <Award size={19} /> Certificações {count(profile?.certifications.length)}
            </NavLink>
            <NavLink to="/app/projetos" className="nav-link">
              <FolderGit2 size={19} /> Projetos {count(profile?.projects.length)}
            </NavLink>
          </nav>

          <div className="nav-label">Competências</div>
          <nav className="nav-group">
            <NavLink to="/app/habilidades" className="nav-link">
              <BookOpen size={19} /> Habilidades {count(profile?.abilities.length)}
            </NavLink>
            <NavLink to="/app/idiomas" className="nav-link">
              <LanguagesIcon size={19} /> Idiomas {count(profile?.languages.length)}
            </NavLink>
            <NavLink to="/app/redes" className="nav-link">
              <Link2 size={19} /> Redes e links {count(profile?.socialNetworks.length)}
            </NavLink>
          </nav>

          <div className="nav-label">Currículo</div>
          <nav className="nav-group">
            <NavLink to="/app/analisar" className="nav-link"><BookOpen size={19} /> Analisar currículo</NavLink>
          <NavLink to="/app/github" className="nav-link"><FolderGit2 size={19} /> Importar do GitHub</NavLink>
          <NavLink to="/app/gerar" className="nav-link cta">
              <Sparkles size={19} /> Gerar com IA <span className="lime-dot" />
            </NavLink>
            <NavLink to="/app/catalogos" className="nav-link">
              <Database size={19} /> Catálogos
            </NavLink>
          </nav>

        </div>

        <div className="sidebar-user">
          <span className="avatar">{initials(session?.name)}</span>
          <div className="who">
            <b>{session?.name}</b>
            <span>
              {c.done} de {c.total} seções preenchidas
            </span>
          </div>
          <Button variant="ghost" size="sm" iconOnly onClick={logout} aria-label="Sair" title="Sair">
            <LogOut size={18} />
          </Button>
        </div>
      </aside>

      <div className="grow workspace-body" inert={open || undefined}>
        <div className="workspace-breadcrumb"><Link to="/app">Meu espaço</Link><span aria-hidden="true">/</span><span>{PAGE_NAMES[location.pathname] ?? "Currículo"}</span></div>
        <main id="workspace-main" ref={mainRef} className="main" tabIndex={-1}>
          <div className="main-inner">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
}
