import { useState, type FormEvent } from "react";
import { ArrowUpRight, Briefcase, Building2, CalendarDays, CheckCircle2, Clock, Layers, Plus } from "lucide-react";
import { useProfile } from "../auth/ProfileContext";
import { resumeApi } from "../api/endpoints";
import { useToast } from "../components/ui/Toast";
import { Button, CatalogFeedback, CatalogSelect, Field, ListSkeleton, Modal, PageHeader } from "../components/ui/ui";
import { duration, period, todayIso } from "../lib/format";
import "../styles/experiences.css";

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
  const { profile, loading, error, reload } = useProfile();
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
    <div className="experiences-page">
      <PageHeader
        eyebrow="Trajetória"
        title="Experiências profissionais"
        text="Cada experiência conta uma parte da sua história. Reúna aqui os trabalhos, estágios e freelas que construíram sua trajetória."
        actions={
          <Button icon={<Plus size={18} />} onClick={openModal}>
            Adicionar experiência
          </Button>
        }
      />

      {error && <CatalogFeedback onRetry={reload}>{error}</CatalogFeedback>}
      {loading ? (
        <div role="status" aria-label="Carregando experiências"><ListSkeleton /></div>
      ) : error && !profile ? null : list.length === 0 ? (
        <section className="empty experience-empty" aria-labelledby="experience-empty-title">
          <div className="experience-empty-main">
            <div className="experience-emblem" aria-hidden="true"><Briefcase size={32} /><span><Plus size={15} /></span></div>
            <span className="experience-kicker">Sua trajetória, em palavras</span>
            <h2 id="experience-empty-title">Dê espaço à sua<br />primeira experiência.</h2>
            <p>Você ainda não cadastrou experiências. Trabalhos, estágios e projetos freelance fazem parte da sua história — mesmo os curtos contam.</p>
            <Button icon={<Plus size={18} />} onClick={openModal}>Adicionar a primeira</Button>
          </div>
          <aside className="experience-guide" aria-label="O que informar na experiência">
            <span className="experience-kicker">O que vamos reunir</span>
            <ol>
              <li><span>01</span><div><h3>Seu papel</h3><p>O cargo, a empresa e a área em que você atuou.</p></div></li>
              <li><span>02</span><div><h3>O período</h3><p>Quando começou e se esse trabalho continua hoje.</p></div></li>
              <li><span>03</span><div><h3>Sua contribuição</h3><p>As atividades e conquistas que merecem destaque.</p></div></li>
            </ol>
            <div className="experience-guide-note"><ArrowUpRight size={18} /><p>Você conta o que fez.<br /><strong>A IA ajuda a transformar em currículo.</strong></p></div>
          </aside>
        </section>
      ) : (
        <section className="experience-history" aria-labelledby="experience-history-title">
          <header className="experience-list-header"><div><span className="experience-kicker">Seu percurso profissional</span><h2 id="experience-history-title">Uma trajetória que evolui.</h2></div><span className="experience-count">{list.length} {list.length === 1 ? "experiência cadastrada" : "experiências cadastradas"}</span></header>
          <div className="item-list experience-timeline">
          {list.map((x) => (
            <article key={x.experienceId} className={`item experience-record ${x.workCurrent ? "is-current" : ""}`}>
              <div className="experience-date"><span className="experience-timeline-dot" aria-hidden="true" /><span><CalendarDays size={15} aria-hidden="true" /> Período</span><strong>{period(x.startDate, x.finishDate, "o momento")}</strong><span><Clock size={14} aria-hidden="true" /> {duration(x.startDate, x.finishDate)}</span></div>
              <div className="item-body">
                <div className="experience-record-heading">
                  <span className="icon-tile"><Building2 size={21} /></span>
                  <div><h3 className="item-title">{x.position}</h3><p className="experience-company">{x.enterprise.name}</p></div>
                  {x.workCurrent ? <span className="badge lime"><CheckCircle2 size={13} /> Trabalho atual</span> : null}
                </div>
                <div className="item-meta">
                  <span>
                    <Layers size={14} /> {x.title}
                  </span>
                  <span className="badge neutral">{x.area.name}</span>
                </div>
                <div className="experience-description"><span className="experience-kicker">Atividades e conquistas</span><p className="item-desc">{x.description}</p></div>
              </div>
            </article>
          ))}
          </div>
          <p className="experience-list-note">Trabalhos atuais primeiro. Depois, as experiências mais recentes.</p>
        </section>
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
        <form id="exp-form" className="experience-form" onSubmit={save} noValidate>
          <fieldset className="experience-form-section"><legend><span>01</span> Seu papel</legend><p>Identifique a experiência e onde ela aconteceu.</p><div className="form-grid">
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
          </div></fieldset>
          <fieldset className="experience-form-section"><legend><span>02</span> Período de atuação</legend><p>Informe as datas e indique se este é seu trabalho atual.</p><div className="form-grid">
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
          </div></fieldset>
          <fieldset className="experience-form-section"><legend><span>03</span> Sua contribuição</legend><p>Conte o que você fazia e os resultados do seu trabalho.</p>
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
          </fieldset>
        </form>
      </Modal>
    </div>
  );
}
