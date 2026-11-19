import { useState, type FormEvent } from "react";
import { BookOpen, CalendarDays, CheckCircle2, Clock, ExternalLink, GraduationCap, Plus } from "lucide-react";
import { useProfile } from "../auth/ProfileContext";
import { resumeApi } from "../api/endpoints";
import type { FormationResponse } from "../api/types";
import { useToast } from "../components/ui/Toast";
import { Button, CatalogFeedback, CatalogSelect, Field, ListSkeleton, Modal, PageHeader, Skeleton } from "../components/ui/ui";
import { FORMATION_TYPES, formationLabel } from "../lib/labels";
import { duration, fullDate, isFuture, monthYear, period } from "../lib/format";
import "../styles/formations.css";

const EMPTY = {
  courseId: null as number | null,
  formationType: "",
  startDate: "",
  finishDate: "",
  inProgress: false,
};

const groups = Array.from(new Set(FORMATION_TYPES.map((f) => f.group)));

export default function Formations() {
  const { profile, loading, error, reload } = useProfile();
  const toast = useToast();
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY);
  const [touched, setTouched] = useState(false);
  const [saving, setSaving] = useState(false);
  const [detail, setDetail] = useState<FormationResponse | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);

  const set = <K extends keyof typeof EMPTY>(k: K, v: (typeof EMPTY)[K]) => setForm((f) => ({ ...f, [k]: v }));

  const errors = {
    courseId: !form.courseId && "Escolha o curso",
    formationType: !form.formationType && "Escolha o tipo",
    startDate: !form.startDate && "Informe o início",
    finishDate:
      !form.inProgress && !form.finishDate
        ? "Informe a conclusão ou marque “em andamento”"
        : !form.inProgress && form.finishDate < form.startDate
          ? "A conclusão deve ser depois do início"
          : false,
  };
  const valid = !Object.values(errors).some(Boolean);
  const err = (k: keyof typeof errors): string | null => (touched && typeof errors[k] === "string" ? (errors[k] as string) : null);

  async function save(e: FormEvent) {
    e.preventDefault();
    setTouched(true);
    if (!valid) return;
    setSaving(true);
    try {
      await resumeApi.addFormations([
        {
          courseId: form.courseId!,
          formationType: form.formationType,
          startDate: form.startDate,
          finishDate: form.inProgress ? null : form.finishDate,
        },
      ]);
      toast.success("Formação adicionada");
      setOpen(false);
      await reload();
    } catch (err) {
      toast.error(err);
    } finally {
      setSaving(false);
    }
  }

  // GET /api/formations/{id}
  async function openDetail(id: number) {
    setDetail(null);
    setDetailLoading(true);
    try {
      setDetail(await resumeApi.getFormation(id));
    } catch (err) {
      toast.error(err);
    } finally {
      setDetailLoading(false);
    }
  }

  const list = (profile?.formations ?? []).slice().sort((a, b) => b.startDate.localeCompare(a.startDate));

  function openModal() {
    setForm(EMPTY);
    setTouched(false);
    setOpen(true);
  }

  return (
    <div className="formations-page">
      <PageHeader
        eyebrow="Trajetória"
        title="Formação acadêmica"
        text="O que você aprende abre novos caminhos. Reúna sua formação, do ensino médio aos cursos que ampliam suas possibilidades."
        actions={
          <Button icon={<Plus size={18} />} onClick={openModal}>
            Adicionar formação
          </Button>
        }
      />

      {error && <CatalogFeedback onRetry={reload}>{error}</CatalogFeedback>}
      {loading ? (
        <div role="status" aria-label="Carregando formações"><ListSkeleton /></div>
      ) : error && !profile ? null : list.length === 0 ? (
        <section className="empty formation-empty" aria-labelledby="formation-empty-title">
          <div className="formation-empty-main">
            <div className="formation-emblem" aria-hidden="true"><GraduationCap size={34} /><span><Plus size={15} /></span></div>
            <span className="formation-kicker">Conhecimento que faz parte de você</span>
            <h2 id="formation-empty-title">Seu aprendizado<br />merece destaque.</h2>
            <p>Você ainda não cadastrou formações. Inclua seus estudos para compor a seção Educação do currículo, mesmo que ainda estejam em andamento.</p>
            <Button icon={<Plus size={18} />} onClick={openModal}>Adicionar a primeira formação</Button>
          </div>
          <aside className="formation-guide" aria-label="Como registrar sua formação">
            <span className="formation-kicker"><BookOpen size={15} aria-hidden="true" /> Sua trajetória de aprendizado</span>
            <ol>
              <li><span>01</span><div><h3>Curso e nível</h3><p>Selecione o curso e o tipo de formação correspondente.</p></div></li>
              <li><span>02</span><div><h3>Período de estudo</h3><p>Informe quando começou e a data de conclusão, inclusive se for prevista.</p></div></li>
              <li><span>03</span><div><h3>Formação em andamento</h3><p>Se ainda estiver cursando, indique essa condição no cadastro.</p></div></li>
            </ol>
            <p className="formation-guide-note">Ensino médio, técnico, graduação e outros níveis: cada etapa ajuda a contar sua história.</p>
          </aside>
        </section>
      ) : (
        <section className="formation-history" aria-labelledby="formation-history-title">
          <header className="formation-list-header">
            <div><span className="formation-kicker">Sua trajetória educacional</span><h2 id="formation-history-title">Conhecimento em construção.</h2></div>
            <span className="formation-count">{list.length} {list.length === 1 ? "formação cadastrada" : "formações cadastradas"}</span>
          </header>
          <div className="item-list formation-list">
          {list.map((f) => (
            <article key={f.formationId} className="item formation-record">
              <div className="item-body">
                <div className="formation-record-heading">
                  <span className="icon-tile"><GraduationCap size={23} /></span>
                  <div><span className="formation-kicker">{formationLabel(f.formationType)}</span><h3 className="item-title">{f.course.name}</h3></div>
                </div>
                <div className="formation-record-meta">
                  {f.inProgress || isFuture(f.finishDate) ? (
                    <span className="badge warning"><Clock size={13} /> Em andamento</span>
                  ) : (
                    <span className="badge success"><CheckCircle2 size={13} /> Concluído</span>
                  )}
                  <span><Clock size={14} aria-hidden="true" /> {duration(f.startDate, f.finishDate)}</span>
                </div>
              </div>
              <div className="formation-period">
                <span className="formation-kicker"><CalendarDays size={14} aria-hidden="true" /> Período de estudo</span>
                <p>{isFuture(f.finishDate)
                      ? `${monthYear(f.startDate)} — previsão ${monthYear(f.finishDate)}`
                      : period(f.startDate, f.finishDate, "em andamento")}</p>
              <Button variant="ghost" size="sm" onClick={() => openDetail(f.formationId)} icon={<ExternalLink size={15} />}>
                Detalhes
              </Button>
              </div>
            </article>
          ))}
          </div>
          <p className="formation-list-note">Formações organizadas pela data de início, da mais recente à mais antiga.</p>
        </section>
      )}

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        icon={<GraduationCap size={20} />}
        title="Nova formação"
        subtitle="Escolha o curso e o nível de ensino."
        footer={
          <>
            <Button variant="secondary" onClick={() => setOpen(false)}>
              Cancelar
            </Button>
            <Button type="submit" form="form-form" loading={saving}>
              Salvar formação
            </Button>
          </>
        }
      >
        <form id="form-form" className="formation-form" onSubmit={save} noValidate>
          <fieldset className="formation-form-section">
            <legend><span>01</span> Curso e nível</legend>
            <p>Identifique o curso e como ele se encaixa na sua formação.</p>
            <div className="form-grid">
          <Field className="span-2" label="Curso" htmlFor="fo-course" error={err("courseId")}>
            <CatalogSelect
              id="fo-course"
              catalog="courses"
              value={form.courseId}
              onChange={(v) => set("courseId", v)}
              newLabel="Cadastrar novo curso…"
              invalid={!!err("courseId")}
            />
          </Field>
          <Field className="span-2" label="Tipo de formação" htmlFor="fo-type" error={err("formationType")}>
            <select
              id="fo-type"
              className="select"
              value={form.formationType}
              onChange={(e) => set("formationType", e.target.value)}
              aria-invalid={!!err("formationType")}
            >
              <option value="">Selecione…</option>
              {groups.map((g) => (
                <optgroup key={g} label={g}>
                  {FORMATION_TYPES.filter((f) => f.group === g).map((f) => (
                    <option key={f.value} value={f.value}>
                      {f.label}
                    </option>
                  ))}
                </optgroup>
              ))}
            </select>
          </Field>
            </div>
          </fieldset>
          <fieldset className="formation-form-section">
            <legend><span>02</span> Período de estudo</legend>
            <p>Uma formação pode estar concluída, em andamento ou ter conclusão prevista.</p>
            <div className="form-grid">
          <Field label="Início" htmlFor="fo-start" error={err("startDate")}>
            <input id="fo-start" type="date" className="input" value={form.startDate} onChange={(e) => set("startDate", e.target.value)} />
          </Field>
          <Field label="Conclusão" htmlFor="fo-end" error={err("finishDate")} hint={!form.inProgress ? "Pode ser uma data prevista" : undefined}>
            <input
              id="fo-end"
              type="date"
              className="input"
              min={form.startDate || undefined}
              value={form.inProgress ? "" : form.finishDate}
              disabled={form.inProgress}
              onChange={(e) => set("finishDate", e.target.value)}
            />
          </Field>
          <label className="check span-2">
            <input type="checkbox" checked={form.inProgress} onChange={(e) => set("inProgress", e.target.checked)} />
            Ainda estou cursando
          </label>
            </div>
          </fieldset>
        </form>
      </Modal>

      <Modal
        open={detailLoading || !!detail}
        onClose={() => {
          setDetail(null);
          setDetailLoading(false);
        }}
        icon={<GraduationCap size={20} />}
        title="Detalhes da formação"
        subtitle="Informações da sua formação acadêmica."
        width={520}
      >
        {detailLoading || !detail ? (
          <div className="stack" role="status" aria-label="Carregando detalhes da formação">
            <Skeleton h={20} w="60%" />
            <Skeleton h={16} w="40%" />
            <Skeleton h={16} w="50%" />
          </div>
        ) : (
          <div className="formation-detail">
            <div className="formation-detail-heading"><span className="icon-tile"><GraduationCap size={24} /></span><span className="formation-kicker">{formationLabel(detail.formationType)}</span><h3>{detail.course.name}</h3></div>
          <dl className="formation-detail-list">
            <Detail label="Início">{fullDate(detail.startDate)}</Detail>
            <Detail label={isFuture(detail.finishDate) ? "Previsão de conclusão" : "Conclusão"}>
              {detail.finishDate ? fullDate(detail.finishDate) : "Em andamento"}
            </Detail>
            <Detail label="Situação">{detail.inProgress || isFuture(detail.finishDate) ? "Em andamento" : "Concluído"}</Detail>
            <Detail label="ID">#{detail.formationId}</Detail>
          </dl>
          </div>
        )}
      </Modal>
    </div>
  );
}

function Detail({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="formation-detail-row">
      <dt>
        {label}
      </dt>
      <dd>{children}</dd>
    </div>
  );
}
