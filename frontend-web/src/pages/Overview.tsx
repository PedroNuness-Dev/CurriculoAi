import { Link } from "react-router-dom";
import {
  ArrowRight,
  Award,
  Briefcase,
  CalendarClock,
  Check,
  ChevronRight,
  FolderGit2,
  GraduationCap,
  MapPin,
  RefreshCw,
  Sparkles,
  Target,
} from "lucide-react";
import { completeness, useProfile } from "../auth/ProfileContext";
import { useAuth } from "../auth/AuthContext";
import { Symbol } from "../components/brand/Brand";
import { Button, Skeleton } from "../components/ui/ui";
import { firstName, fullDate, period } from "../lib/format";
import { goalLabel, workModelLabel } from "../lib/labels";

export default function Overview() {
  const { session } = useAuth();
  const { profile, loading, error, reload } = useProfile();
  const c = completeness(profile);

  if (error && !profile) {
    return (
      <div className="card card-pad stack" style={{ alignItems: "flex-start" }}>
        <h2>Não foi possível carregar seu perfil</h2>
        <p className="muted">{error}</p>
        <Button variant="secondary" icon={<RefreshCw size={16} />} onClick={() => reload()}>
          Tentar novamente
        </Button>
      </div>
    );
  }

  const latestExp = profile?.experiences.slice().sort((a, b) => (b.startDate ?? "").localeCompare(a.startDate ?? ""))[0];
  const ready = c.percent >= 45 && (profile?.experiences.length || profile?.formations.length);

  return (
    <div className="stack-lg">
      <section className="hero-card">
        <Symbol className="hero-symbol" tone="white" size={440} />
        <div className="hero-grid">
          <div>
            <div className="eyebrow" style={{ color: "rgba(255,255,255,.85)" }}>
              Painel do candidato
            </div>
            <h2 style={{ marginTop: 10 }}>Olá, {firstName(session?.name)}! Vamos montar seu currículo?</h2>
            <p style={{ marginTop: 12, opacity: 0.9, fontWeight: 600, maxWidth: 520 }}>
              Quanto mais completo seu perfil, melhor o currículo que a IA escreve para você.
            </p>
            <div className="row row-wrap" style={{ marginTop: 22 }}>
              <Link to="/app/gerar" className="btn btn-lime btn-lg">
                <Sparkles size={19} /> Gerar currículo com IA
              </Link>
              <Link to={c.checks.find((x) => !x.done)?.to ?? "/app/perfil"} className="btn btn-white btn-lg">
                {c.done === c.total ? "Revisar perfil" : "Completar perfil"}
              </Link>
            </div>
          </div>
          <div>
            {loading ? (
              <Skeleton h={104} w={200} />
            ) : (
              <div className="hero-pct">{c.percent}%</div>
            )}
            <p style={{ fontWeight: 700, marginTop: 8 }}>
              do perfil preenchido · {c.done} de {c.total} seções
            </p>
            <div className="progress" style={{ marginTop: 14 }}>
              <span style={{ width: `${c.percent}%` }} />
            </div>
            {ready ? (
              <p className="small" style={{ marginTop: 10, opacity: 0.85 }}>
                Você já tem o suficiente para gerar uma primeira versão.
              </p>
            ) : (
              <p className="small" style={{ marginTop: 10, opacity: 0.85 }}>
                Adicione ao menos uma experiência ou formação para começar.
              </p>
            )}
          </div>
        </div>
      </section>

      <section className="stats-grid">
        <StatTile to="/app/experiencias" icon={<Briefcase size={20} />} n={profile?.experiences.length} label="Experiências" loading={loading} />
        <StatTile to="/app/formacao" icon={<GraduationCap size={20} />} n={profile?.formations.length} label="Formações" loading={loading} />
        <StatTile to="/app/certificacoes" icon={<Award size={20} />} n={profile?.certifications.length} label="Certificações" loading={loading} />
        <StatTile to="/app/projetos" icon={<FolderGit2 size={20} />} n={profile?.projects.length} label="Projetos" loading={loading} />
      </section>

      <section className="two-col">
        <div className="card card-pad">
          <div className="card-head">
            <div>
              <div className="card-title">Checklist do perfil</div>
              <p className="card-sub">Cada item vira uma seção do seu currículo.</p>
            </div>
            <span className="badge">
              {c.done}/{c.total}
            </span>
          </div>
          <div className="checklist">
            {c.checks.map((x) => (
              <Link key={x.key} to={x.to} className={`check-row ${x.done ? "done" : ""}`}>
                <span className="tick">{x.done && <Check size={14} strokeWidth={3} />}</span>
                {x.label}
                <ChevronRight size={16} className="go" />
              </Link>
            ))}
          </div>
        </div>

        <div className="card card-pad">
          <div className="card-head">
            <div>
              <div className="card-title">Resumo</div>
              <p className="card-sub">O que a IA já sabe sobre você.</p>
            </div>
            <Link to="/app/perfil" className="btn btn-secondary btn-sm">
              Editar
            </Link>
          </div>
          {loading ? (
            <div className="stack">
              <Skeleton h={18} w="70%" />
              <Skeleton h={18} w="55%" />
              <Skeleton h={18} w="62%" />
            </div>
          ) : (
            <div className="stack" style={{ gap: 14 }}>
              <SummaryRow icon={<Target size={18} />} label="Objetivo">
                {profile?.careerGoal ? (
                  <>
                    {goalLabel(profile.careerGoal.goalType)}
                    {profile.careerGoal.area ? ` em ${profile.careerGoal.area.name}` : ""}
                  </>
                ) : null}
              </SummaryRow>
              <SummaryRow icon={<Briefcase size={18} />} label="Área">
                {profile?.area?.name}
              </SummaryRow>
              <SummaryRow icon={<MapPin size={18} />} label="Local">
                {profile?.address ? `${profile.address.city} — ${profile.address.stateAbbreviation}` : null}
              </SummaryRow>
              <SummaryRow icon={<CalendarClock size={18} />} label="Disponibilidade">
                {profile?.availability
                  ? `${workModelLabel(profile.availability.workModel)}${
                      profile.availability.startAvailability ? ` · a partir de ${fullDate(profile.availability.startAvailability)}` : ""
                    }`
                  : null}
              </SummaryRow>
              {latestExp && (
                <SummaryRow icon={<Briefcase size={18} />} label="Último cargo">
                  {latestExp.position} · {latestExp.enterprise.name}{" "}
                  <span className="muted small">({period(latestExp.startDate, latestExp.finishDate)})</span>
                </SummaryRow>
              )}
              {profile && profile.abilities.length > 0 && (
                <div className="chip-group" style={{ marginTop: 4 }}>
                  {profile.abilities.slice(0, 8).map((a) => (
                    <span key={a.abilityId} className="badge neutral">
                      {a.name}
                    </span>
                  ))}
                  {profile.abilities.length > 8 && <span className="badge">+{profile.abilities.length - 8}</span>}
                </div>
              )}
            </div>
          )}
        </div>
      </section>
    </div>
  );
}

function StatTile({
  to,
  icon,
  n,
  label,
  loading,
}: {
  to: string;
  icon: React.ReactNode;
  n: number | undefined;
  label: string;
  loading: boolean;
}) {
  return (
    <Link to={to} className="stat-tile">
      <div className="row between">
        <span className="icon-tile">{icon}</span>
        <ArrowRight size={18} className="muted" />
      </div>
      <div>
        {loading ? <Skeleton h={34} w={44} /> : <div className="num">{n ?? 0}</div>}
        <div className="lbl">{label}</div>
      </div>
    </Link>
  );
}

function SummaryRow({ icon, label, children }: { icon: React.ReactNode; label: string; children: React.ReactNode }) {
  return (
    <div className="row" style={{ alignItems: "flex-start", gap: 12 }}>
      <span className="muted" style={{ marginTop: 2 }}>
        {icon}
      </span>
      <div className="grow">
        <div className="small muted" style={{ fontWeight: 700 }}>
          {label}
        </div>
        <div style={{ fontWeight: 650 }}>{children || <span className="muted">Não informado</span>}</div>
      </div>
    </div>
  );
}
