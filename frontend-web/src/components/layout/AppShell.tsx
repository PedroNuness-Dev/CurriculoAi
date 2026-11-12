import { useEffect, useState } from "react";
import { NavLink, Outlet, useLocation } from "react-router-dom";
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
} from "lucide-react";
import { useAuth } from "../../auth/AuthContext";
import { completeness, useProfile } from "../../auth/ProfileContext";
import { initials } from "../../lib/format";
import { AppIcon, Wordmark } from "../brand/Brand";
import { Button } from "../ui/ui";

export default function AppShell() {
  const { session, logout } = useAuth();
  const { profile } = useProfile();
  const [open, setOpen] = useState(false);
  const location = useLocation();

  useEffect(() => {
    setOpen(false);
    window.scrollTo({ top: 0 });
  }, [location.pathname]);

  const c = completeness(profile);
  const count = (n: number | undefined) => (n ? <span className="nav-count">{n}</span> : null);
  const dot = (done: boolean) => (done ? <span className="nav-dot" aria-label="preenchido" /> : null);
  const aboutDone = !!(profile?.area && profile?.careerGoal && profile?.address && profile?.availability);

  return (
    <div className={`shell ${open ? "nav-open" : ""}`}>
      {open && <div className="scrim" onClick={() => setOpen(false)} />}

      <aside className="sidebar" aria-label="Navegação principal">
        <div className="sidebar-logo">
          <NavLink to="/app" aria-label="Início">
            <Wordmark height={21} />
          </NavLink>
        </div>

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
          <NavLink to="/app/gerar" className="nav-link cta">
            <Sparkles size={19} /> Gerar com IA <span className="lime-dot" />
          </NavLink>
          <NavLink to="/app/catalogos" className="nav-link">
            <Database size={19} /> Catálogos
          </NavLink>
        </nav>

        <div className="sidebar-user">
          <span className="avatar">{initials(session?.name)}</span>
          <div className="who">
            <b>{session?.name}</b>
            <span>
              {c.percent}% do perfil · {session?.email}
            </span>
          </div>
          <Button variant="ghost" size="sm" iconOnly onClick={logout} aria-label="Sair" title="Sair">
            <LogOut size={18} />
          </Button>
        </div>
      </aside>

      <div className="grow">
        <div className="topbar">
          <Button variant="secondary" size="sm" iconOnly onClick={() => setOpen(true)} aria-label="Abrir menu">
            <Menu size={18} />
          </Button>
          <Wordmark height={17} />
          <AppIcon size={34} />
        </div>
        <main className="main">
          <div className="main-inner">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
}
