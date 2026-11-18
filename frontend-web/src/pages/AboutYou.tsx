import { useEffect, useId, useState, type FormEvent } from "react";
import { ArrowDown, Briefcase, CalendarClock, CheckCircle2, Circle, Lock, MapPin, Search, Target } from "lucide-react";
import { useProfile } from "../auth/ProfileContext";
import { resumeApi, userApi } from "../api/endpoints";
import { useToast } from "../components/ui/Toast";
import { Button, CatalogFeedback, CatalogSelect, Field, PageHeader, Skeleton } from "../components/ui/ui";
import { GOAL_TYPES, WORK_MODELS, goalLabel, workModelLabel } from "../lib/labels";
import { fullDate, initials, maskCep, onlyDigits } from "../lib/format";
import type { GoalType, WorkModel } from "../api/types";
import "../styles/profile.css";

export default function AboutYou() {
  const { profile, loading, error, reload } = useProfile();
  const sections = [
    { id: "profile-area", label: "Área de atuação", done: !!profile?.area },
    { id: "profile-goal", label: "Objetivo", done: !!profile?.careerGoal },
    { id: "profile-address", label: "Endereço", done: !!profile?.address },
    { id: "profile-availability", label: "Disponibilidade", done: !!profile?.availability },
  ];
  const completed = sections.filter((section) => section.done).length;
  return (
    <div className="profile-page">
      <PageHeader
        eyebrow="Sobre você"
        title="Seu próximo passo começa aqui."
        text="Defina sua direção profissional e as informações que vão dar contexto ao seu currículo."
      />
      {loading ? (
        <div className="stack-lg profile-loading" role="status" aria-label="Carregando seu perfil">
          <Skeleton h={180} />
          {[0, 1].map((i) => (
            <div key={i} className="card card-pad stack">
              <Skeleton h={22} w="50%" />
              <Skeleton h={46} />
              <Skeleton h={44} w={140} />
            </div>
          ))}
        </div>
      ) : error && !profile ? (
        <CatalogFeedback onRetry={reload}>{error}</CatalogFeedback>
      ) : (
        <>
          <section className="hero-card profile-identity" aria-label="Resumo do perfil">
            <div className="profile-person">
              <span className="profile-avatar" aria-hidden="true">{initials(profile?.user.name)}</span>
              <div><span className="profile-kicker">Seu perfil profissional</span><h2>{profile?.user.name || "Sobre você"}</h2><p>Uma direção clara para a sua próxima oportunidade.</p></div>
            </div>
            <div className="profile-completion" aria-live="polite">
              <div><span>Informações desta página</span><strong>{completed}<small> / 4</small></strong></div>
              <div className="profile-track" aria-hidden="true">{sections.map((section) => <span key={section.id} className={section.done ? "is-complete" : ""} />)}</div>
              <p>{completed === 4 ? "Tudo preenchido. Seu perfil tem uma direção definida." : `${4 - completed} ${completed === 3 ? "informação para completar" : "informações para completar"}. No seu ritmo.`}</p>
            </div>
          </section>
          <nav className="profile-shortcuts" aria-label="Informações do perfil">
            {sections.map((section) => <a key={section.id} href={`#${section.id}`}><span>{section.done ? <CheckCircle2 size={16} /> : <Circle size={16} />}{section.label}<span className="sr-only">{section.done ? ": preenchido" : ": pendente"}</span></span><ArrowDown size={14} aria-hidden="true" /></a>)}
          </nav>
          {error && <CatalogFeedback onRetry={reload}>{error}</CatalogFeedback>}
          <div className="stack-lg profile-groups">
            <div className="card profile-group"><div className="profile-group-label"><span>01</span> Direção profissional</div><AreaCard /><GoalCard /></div>
            <div className="card profile-group"><div className="profile-group-label"><span>02</span> Localização e rotina</div><AddressCard /><AvailabilityCard /></div>
          </div>
          <p className="profile-footnote">Cada seção é salva separadamente. Você pode completar seu perfil aos poucos.</p>
        </>
      )}
    </div>
  );
}

function SectionCard({
  icon,
  title,
  sub,
  done,
  children,
}: {
  icon: React.ReactNode;
  title: string;
  sub: string;
  done?: boolean;
  children: React.ReactNode;
}) {
  const titleId = useId();
  const sectionIds: Record<string, string> = { "Área de atuação": "profile-area", "Endereço": "profile-address", "Objetivo de carreira": "profile-goal", "Disponibilidade": "profile-availability" };
  return (
    <section className="profile-section" id={sectionIds[title]} aria-labelledby={titleId}>
      <div className="profile-section-heading">
          <span className={`icon-tile ${done ? "grad" : ""}`}>{icon}</span>
          <div>
            <h2 className="card-title" id={titleId}>{title}</h2>
            <p className="card-sub">{sub}</p>
          </div>
        {done && (
          <span className="badge success">
            <CheckCircle2 size={14} /> Salvo
          </span>
        )}
      </div>
      <div className="profile-section-content">{children}</div>
    </section>
  );
}

// ---------------------------------------------------------------------------
// Área de atuação — POST /api/users/area/{areaId}
// ---------------------------------------------------------------------------
function AreaCard() {
  const { profile, reload } = useProfile();
  const toast = useToast();
  const [areaId, setAreaId] = useState<number | null>(profile?.area?.areaId ?? null);
  const [saving, setSaving] = useState(false);

  useEffect(() => setAreaId(profile?.area?.areaId ?? null), [profile?.area?.areaId]);

  async function save(e: FormEvent) {
    e.preventDefault();
    if (!areaId) return;
    setSaving(true);
    try {
      const res = await userApi.setArea(areaId);
      toast.success(`Área definida: ${res.area.name}`);
      await reload();
    } catch (err) {
      toast.error(err);
    } finally {
      setSaving(false);
    }
  }

  return (
    <SectionCard icon={<Briefcase size={20} />} title="Área de atuação" sub="Em qual área você atua ou quer atuar?" done={!!profile?.area}>
      <form className="stack" onSubmit={save}>
        <Field label="Área" htmlFor="area">
          <CatalogSelect id="area" catalog="areas" value={areaId} onChange={setAreaId} newLabel="Cadastrar nova área…" />
        </Field>
        <div className="row">
          <Button type="submit" loading={saving} disabled={!areaId || areaId === profile?.area?.areaId}>
            {profile?.area ? "Atualizar área" : "Salvar área"}
          </Button>
        </div>
      </form>
    </SectionCard>
  );
}

// ---------------------------------------------------------------------------
// Endereço — POST /api/users/address/{cep} (ViaCEP no backend)
// ---------------------------------------------------------------------------
function AddressCard() {
  const { profile, reload } = useProfile();
  const toast = useToast();
  const [cep, setCep] = useState(profile?.address ? maskCep(profile.address.zipCode) : "");
  const [saving, setSaving] = useState(false);
  const addr = profile?.address;
  const valid = onlyDigits(cep).length === 8;

  async function save(e: FormEvent) {
    e.preventDefault();
    if (!valid) return;
    setSaving(true);
    try {
      const res = await userApi.setAddress(onlyDigits(cep));
      toast.success(`Endereço encontrado: ${res.address.city} — ${res.address.stateAbbreviation}`);
      await reload();
    } catch (err) {
      toast.error(err);
    } finally {
      setSaving(false);
    }
  }

  return (
    <SectionCard icon={<MapPin size={20} />} title="Endereço" sub="Buscamos automaticamente pelo CEP." done={!!addr}>
      <form className="stack" onSubmit={save}>
        <Field label="CEP" htmlFor="cep" hint="Somente números — ex.: 01001-000">
          <div className="profile-cep-row">
            <input
              id="cep"
              className="input"
              inputMode="numeric"
              placeholder="00000-000"
              value={cep}
              onChange={(e) => setCep(maskCep(e.target.value))}
            />
            <Button type="submit" loading={saving} disabled={!valid} icon={<Search size={17} />}>
              Buscar
            </Button>
          </div>
        </Field>
        {addr && (
          <div className="profile-address-result">
            <div className="item-body">
              <div className="item-title" style={{ fontSize: 15 }}>
                {[addr.street, addr.neighborhood].filter(Boolean).join(", ") || "Endereço do CEP"}
              </div>
              <div className="item-meta">
                <span>
                  {addr.city} — {addr.stateAbbreviation}
                </span>
                {addr.region && <span>Região {addr.region}</span>}
                <span>CEP {addr.zipCode}</span>
              </div>
            </div>
          </div>
        )}
      </form>
    </SectionCard>
  );
}

// ---------------------------------------------------------------------------
// Objetivo de carreira — POST /api/career-goal (um por usuário)
// ---------------------------------------------------------------------------
function GoalCard() {
  const { profile, reload } = useProfile();
  const toast = useToast();
  const goal = profile?.careerGoal;
  const [goalType, setGoalType] = useState<GoalType | "">("");
  const [areaId, setAreaId] = useState<number | null>(profile?.area?.areaId ?? null);
  const [description, setDescription] = useState("");
  const [saving, setSaving] = useState(false);
  const [touched, setTouched] = useState(false);

  const valid = !!goalType && !!areaId && description.trim().length > 0;

  async function save(e: FormEvent) {
    e.preventDefault();
    setTouched(true);
    if (!valid) return;
    setSaving(true);
    try {
      await resumeApi.setCareerGoal({ goalType: goalType as GoalType, areaId: areaId!, description: description.trim() });
      toast.success("Objetivo de carreira salvo");
      await reload();
    } catch (err) {
      toast.error(err);
    } finally {
      setSaving(false);
    }
  }

  if (goal) {
    return (
      <SectionCard icon={<Target size={20} />} title="Objetivo de carreira" sub="O que você busca agora." done>
        <div className="stack" style={{ gap: 12 }}>
          <div className="row row-wrap" style={{ gap: 8 }}>
            <span className="badge">{goalLabel(goal.goalType)}</span>
            {goal.area && <span className="badge neutral">{goal.area.name}</span>}
          </div>
          <p style={{ color: "var(--ink-2)", whiteSpace: "pre-line" }}>{goal.description}</p>
          <p className="profile-readonly-note">
            <Lock size={14} /> Definido em {fullDate(goal.createdAt)} · edição indisponível no momento.
          </p>
        </div>
      </SectionCard>
    );
  }

  return (
    <SectionCard icon={<Target size={20} />} title="Objetivo de carreira" sub="O que você busca agora.">
      <form className="stack" onSubmit={save}>
        <div className="option-cards" role="radiogroup" aria-label="Tipo de objetivo">
          {GOAL_TYPES.map((g) => (
            <label key={g.value} className="option-card">
              <input
                type="radio"
                name="goalType"
                value={g.value}
                checked={goalType === g.value}
                onChange={() => setGoalType(g.value as GoalType)}
              />
              <b>{g.label}</b>
              <span>{g.hint}</span>
            </label>
          ))}
        </div>
        {touched && !goalType && <span className="field-error">Escolha um objetivo</span>}
        <Field label="Área desejada" htmlFor="goal-area" error={touched && !areaId ? "Escolha a área" : null}>
          <CatalogSelect id="goal-area" catalog="areas" value={areaId} onChange={setAreaId} invalid={touched && !areaId} />
        </Field>
        <Field
          label="Descreva seu objetivo"
          htmlFor="goal-desc"
          error={touched && !description.trim() ? "Conte um pouco sobre o que você busca" : null}
          hint={`${description.length}/1000`}
        >
          <textarea
            id="goal-desc"
            className="textarea"
            maxLength={1000}
            placeholder="Ex.: Busco minha primeira oportunidade como desenvolvedor backend, com foco em Java e Spring."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </Field>
        <div className="row">
          <Button type="submit" loading={saving}>
            Salvar objetivo
          </Button>
        </div>
      </form>
    </SectionCard>
  );
}

// ---------------------------------------------------------------------------
// Disponibilidade — POST /api/availabilities (uma por usuário)
// ---------------------------------------------------------------------------
function AvailabilityCard() {
  const { profile, reload } = useProfile();
  const toast = useToast();
  const av = profile?.availability;
  const [workModel, setWorkModel] = useState<WorkModel | "">("");
  const [start, setStart] = useState("");
  const [saving, setSaving] = useState(false);

  async function save(e: FormEvent) {
    e.preventDefault();
    if (!workModel) return;
    setSaving(true);
    try {
      await resumeApi.setAvailability({ workModel, startAvailability: start || null });
      toast.success("Disponibilidade salva");
      await reload();
    } catch (err) {
      toast.error(err);
    } finally {
      setSaving(false);
    }
  }

  if (av) {
    return (
      <SectionCard icon={<CalendarClock size={20} />} title="Disponibilidade" sub="Modelo de trabalho e início." done>
        <div className="stack" style={{ gap: 12 }}>
          <div className="row row-wrap" style={{ gap: 8 }}>
            <span className="badge">{workModelLabel(av.workModel)}</span>
            <span className="badge neutral">
              {av.startAvailability ? `A partir de ${fullDate(av.startAvailability)}` : "Início imediato"}
            </span>
          </div>
          <p className="profile-readonly-note">
            <Lock size={14} /> Disponibilidade cadastrada · edição indisponível no momento.
          </p>
        </div>
      </SectionCard>
    );
  }

  return (
    <SectionCard icon={<CalendarClock size={20} />} title="Disponibilidade" sub="Modelo de trabalho e início.">
      <form className="stack" onSubmit={save}>
        <div className="option-cards" role="radiogroup" aria-label="Modelo de trabalho">
          {WORK_MODELS.map((w) => (
            <label key={w.value} className="option-card">
              <input type="radio" name="workModel" checked={workModel === w.value} onChange={() => setWorkModel(w.value)} />
              <b>{w.label}</b>
              <span>{w.hint}</span>
            </label>
          ))}
        </div>
        <Field label="Disponível a partir de" htmlFor="av-start" optional hint="Deixe vazio para início imediato">
          <input id="av-start" type="date" className="input" value={start} onChange={(e) => setStart(e.target.value)} />
        </Field>
        <div className="row">
          <Button type="submit" loading={saving} disabled={!workModel}>
            Salvar disponibilidade
          </Button>
        </div>
      </form>
    </SectionCard>
  );
}
