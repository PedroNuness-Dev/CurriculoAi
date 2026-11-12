import { useState, type FormEvent } from "react";
import { Briefcase, Building2, CalendarDays, Clock, Layers, Plus } from "lucide-react";
import { useProfile } from "../auth/ProfileContext";
import { resumeApi } from "../api/endpoints";
import { useToast } from "../components/ui/Toast";
import { Button, CatalogSelect, EmptyState, Field, ListSkeleton, Modal, PageHeader } from "../components/ui/ui";
import { duration, period, todayIso } from "../lib/format";

const EMPTY = {
  title: "",
  position: "",
  enterpriseId: null as number | null,
  areaId: null as number | null,
  startDate: "",
  finishDate: "",
  current: false,
  description: "",
};

export default function Experiences() {
  const { profile, loading, reload } = useProfile();
  const toast = useToast();
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY);
  const [touched, setTouched] = useState(false);
  const [saving, setSaving] = useState(false);

  const set = <K extends keyof typeof EMPTY>(k: K, v: (typeof EMPTY)[K]) => setForm((f) => ({ ...f, [k]: v }));

  const errors = {
    title: !form.title.trim() && "Informe um título",
    position: !form.position.trim() && "Informe o cargo",
    enterpriseId: !form.enterpriseId && "Escolha a empresa",
    areaId: !form.areaId && "Escolha a área",
    startDate: !form.startDate && "Informe a data de início",
    finishDate:
      !form.current && !form.finishDate
        ? "Informe a data de término ou marque “trabalho atual”"
        : !form.current && form.finishDate < form.startDate
          ? "O término deve ser depois do início"
          : false,
    description: !form.description.trim() && "Descreva suas atividades",
  };
  const valid = !Object.values(errors).some(Boolean);

  function openModal() {
    setForm({ ...EMPTY, areaId: profile?.area?.areaId ?? null });
    setTouched(false);
    setOpen(true);
  }

  async function save(e: FormEvent) {
    e.preventDefault();
    setTouched(true);
    if (!valid) return;
    setSaving(true);
    try {
      await resumeApi.addExperiences([
        {
          title: form.title.trim(),
          position: form.position.trim(),
          enterpriseId: form.enterpriseId!,
          areaId: form.areaId!,
          startDate: form.startDate,
          finishDate: form.current ? null : form.finishDate,
          workCurrent: form.current,
          description: form.description.trim(),
        },
      ]);
      toast.success("Experiência adicionada");
      setOpen(false);
      await reload();
    } catch (err) {
      toast.error(err);
    } finally {
      setSaving(false);
    }
  }

  const list = (profile?.experiences ?? [])
    .slice()
    .sort((a, b) => (b.workCurrent ? 1 : 0) - (a.workCurrent ? 1 : 0) || b.startDate.localeCompare(a.startDate));

  const err = (k: keyof typeof errors): string | null => (touched && typeof errors[k] === "string" ? (errors[k] as string) : null);

  return (
    <>
      <PageHeader
        eyebrow="Trajetória"
        title="Experiências profissionais"
        text="Empregos, estágios e freelas. A IA reescreve suas atividades com verbos de ação."
        actions={
          <Button icon={<Plus size={18} />} onClick={openModal}>
            Adicionar experiência
          </Button>
        }
      />

      {loading ? (
        <ListSkeleton />
      ) : list.length === 0 ? (
        <EmptyState
          icon={<Briefcase size={26} />}
          title="Nenhuma experiência ainda"
          text="Adicione seus trabalhos, estágios ou projetos freelance — mesmo os curtos contam."
          action={
            <Button icon={<Plus size={18} />} onClick={openModal}>
              Adicionar a primeira
            </Button>
          }
        />
      ) : (
        <div className="item-list">
          {list.map((x) => (
            <article key={x.experienceId} className="item">
              <span className="icon-tile grad">
                <Building2 size={20} />
              </span>
              <div className="item-body">
                <div className="row between row-wrap" style={{ gap: 8 }}>
                  <div className="item-title">
                    {x.position} <span className="muted">· {x.enterprise.name}</span>
                  </div>
                  {x.workCurrent ? <span className="badge lime">Atual</span> : null}
                </div>
                <div className="item-meta">
                  <span>
                    <Layers size={14} /> {x.title}
                  </span>
                  <span>
                    <CalendarDays size={14} /> {period(x.startDate, x.finishDate, "o momento")}
                  </span>
                  <span>
                    <Clock size={14} /> {duration(x.startDate, x.finishDate)}
                  </span>
                  <span className="badge neutral">{x.area.name}</span>
                </div>
                <p className="item-desc">{x.description}</p>
              </div>
            </article>
          ))}
        </div>
      )}

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        icon={<Briefcase size={20} />}
        title="Nova experiência"
        subtitle="Conte o que você fez — a IA cuida da redação."
        width={720}
        footer={
          <>
            <Button variant="secondary" onClick={() => setOpen(false)}>
              Cancelar
            </Button>
            <Button type="submit" form="exp-form" loading={saving}>
              Salvar experiência
            </Button>
          </>
        }
      >
        <form id="exp-form" className="form-grid" onSubmit={save} noValidate>
          <Field label="Cargo" htmlFor="ex-pos" error={err("position")}>
            <input
              id="ex-pos"
              className="input"
              placeholder="Ex.: Desenvolvedor Backend Jr."
              value={form.position}
              onChange={(e) => set("position", e.target.value)}
              aria-invalid={!!err("position")}
            />
          </Field>
          <Field label="Título da experiência" htmlFor="ex-title" error={err("title")} hint="Um resumo curto do que foi">
            <input
              id="ex-title"
              className="input"
              placeholder="Ex.: Estágio em desenvolvimento"
              value={form.title}
              onChange={(e) => set("title", e.target.value)}
              aria-invalid={!!err("title")}
            />
          </Field>
          <Field label="Empresa" htmlFor="ex-ent" error={err("enterpriseId")}>
            <CatalogSelect
              id="ex-ent"
              catalog="enterprises"
              value={form.enterpriseId}
              onChange={(v) => set("enterpriseId", v)}
              newLabel="Cadastrar nova empresa…"
              invalid={!!err("enterpriseId")}
            />
          </Field>
          <Field label="Área" htmlFor="ex-area" error={err("areaId")}>
            <CatalogSelect
              id="ex-area"
              catalog="areas"
              value={form.areaId}
              onChange={(v) => set("areaId", v)}
              newLabel="Cadastrar nova área…"
              invalid={!!err("areaId")}
            />
          </Field>
          <Field label="Início" htmlFor="ex-start" error={err("startDate")}>
            <input
              id="ex-start"
              type="date"
              className="input"
              max={todayIso()}
              value={form.startDate}
              onChange={(e) => set("startDate", e.target.value)}
              aria-invalid={!!err("startDate")}
            />
          </Field>
          <Field label="Término" htmlFor="ex-end" error={err("finishDate")}>
            <input
              id="ex-end"
              type="date"
              className="input"
              min={form.startDate || undefined}
              value={form.current ? "" : form.finishDate}
              disabled={form.current}
              onChange={(e) => set("finishDate", e.target.value)}
              aria-invalid={!!err("finishDate")}
            />
          </Field>
          <label className="check span-2">
            <input type="checkbox" checked={form.current} onChange={(e) => set("current", e.target.checked)} />
            Trabalho aqui atualmente
          </label>
          <Field
            className="span-2"
            label="Atividades e conquistas"
            htmlFor="ex-desc"
            error={err("description")}
            hint="Liste o que você fazia, tecnologias usadas e resultados."
          >
            <textarea
              id="ex-desc"
              className="textarea"
              placeholder="Ex.: Desenvolvi APIs REST com Spring Boot, reduzi o tempo de resposta em 30%…"
              value={form.description}
              onChange={(e) => set("description", e.target.value)}
              aria-invalid={!!err("description")}
            />
          </Field>
        </form>
      </Modal>
    </>
  );
}
