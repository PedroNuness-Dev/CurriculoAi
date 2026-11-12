import { useEffect, useState, type FormEvent } from "react";
import { Briefcase, CalendarClock, CheckCircle2, Lock, MapPin, Search, Target } from "lucide-react";
import { useProfile } from "../auth/ProfileContext";
import { resumeApi, userApi } from "../api/endpoints";
import { useToast } from "../components/ui/Toast";
import { Button, CatalogSelect, Field, PageHeader, Skeleton } from "../components/ui/ui";
import { GOAL_TYPES, WORK_MODELS, goalLabel, workModelLabel } from "../lib/labels";
import { fullDate, maskCep, onlyDigits } from "../lib/format";
import type { GoalType, WorkModel } from "../api/types";

export default function AboutYou() {
  const { loading } = useProfile();
  return (
    <>
      <PageHeader
        eyebrow="Sobre você"
        title="Quem é você no mercado?"
        text="Essas informações orientam o tom e o foco do currículo que a IA vai escrever."
      />
      {loading ? (
        <div className="two-col">
          {[0, 1, 2, 3].map((i) => (
            <div key={i} className="card card-pad stack">
              <Skeleton h={22} w="50%" />
              <Skeleton h={46} />
              <Skeleton h={44} w={140} />
            </div>
          ))}
        </div>
      ) : (
        <div className="two-col">
          <AreaCard />
          <AddressCard />
          <GoalCard />
          <AvailabilityCard />
        </div>
      )}
    </>
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
  return (
    <section className="card card-pad">
      <div className="card-head">
        <div className="row" style={{ alignItems: "flex-start", gap: 14 }}>
          <span className={`icon-tile ${done ? "grad" : ""}`}>{icon}</span>
          <div>
            <div className="card-title">{title}</div>
            <p className="card-sub">{sub}</p>
          </div>
        </div>
        {done && (
          <span className="badge success">
            <CheckCircle2 size={14} /> Salvo
          </span>
        )}
      </div>
      {children}
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
          <div className="row" style={{ gap: 8 }}>
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
          <div className="item" style={{ padding: 14, background: "var(--surface-2)" }}>
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
          <p className="small muted row" style={{ gap: 6 }}>
            <Lock size={14} /> Definido em {fullDate(goal.createdAt)} · o backend ainda não permite editar.
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
          <p className="small muted row" style={{ gap: 6 }}>
            <Lock size={14} /> O backend ainda não permite editar a disponibilidade.
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
