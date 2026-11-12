import { useState, type FormEvent } from "react";
import { CalendarDays, ExternalLink, GraduationCap, Plus } from "lucide-react";
import { useProfile } from "../auth/ProfileContext";
import { resumeApi } from "../api/endpoints";
import type { FormationResponse } from "../api/types";
import { useToast } from "../components/ui/Toast";
import { Button, CatalogSelect, EmptyState, Field, ListSkeleton, Modal, PageHeader, Skeleton } from "../components/ui/ui";
import { FORMATION_TYPES, formationLabel } from "../lib/labels";
import { duration, fullDate, isFuture, monthYear, period } from "../lib/format";

const EMPTY = {
  courseId: null as number | null,
  formationType: "",
  startDate: "",
  finishDate: "",
  inProgress: false,
};

const groups = Array.from(new Set(FORMATION_TYPES.map((f) => f.group)));

export default function Formations() {
  const { profile, loading, reload } = useProfile();
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
    <>
      <PageHeader
        eyebrow="Trajetória"
        title="Formação acadêmica"
        text="Do ensino médio ao doutorado — inclua também cursos técnicos e livres."
        actions={
          <Button icon={<Plus size={18} />} onClick={openModal}>
            Adicionar formação
          </Button>
        }
      />

      {loading ? (
        <ListSkeleton />
      ) : list.length === 0 ? (
        <EmptyState
          icon={<GraduationCap size={26} />}
          title="Nenhuma formação cadastrada"
          text="Adicione sua formação para que ela apareça na seção Educação do currículo."
          action={
            <Button icon={<Plus size={18} />} onClick={openModal}>
              Adicionar formação
            </Button>
          }
        />
      ) : (
        <div className="item-list">
          {list.map((f) => (
            <article key={f.formationId} className="item">
              <span className="icon-tile grad">
                <GraduationCap size={20} />
              </span>
              <div className="item-body">
                <div className="row between row-wrap" style={{ gap: 8 }}>
                  <div className="item-title">{f.course.name}</div>
                  {f.inProgress || isFuture(f.finishDate) ? (
                    <span className="badge warning">Em andamento</span>
                  ) : (
                    <span className="badge success">Concluído</span>
                  )}
                </div>
                <div className="item-meta">
                  <span className="badge">{formationLabel(f.formationType)}</span>
                  <span>
                    <CalendarDays size={14} />{" "}
                    {isFuture(f.finishDate)
                      ? `${monthYear(f.startDate)} — previsão ${monthYear(f.finishDate)}`
                      : period(f.startDate, f.finishDate, "em andamento")}
                  </span>
                  <span>{duration(f.startDate, f.finishDate)}</span>
                </div>
              </div>
              <Button variant="ghost" size="sm" onClick={() => openDetail(f.formationId)} icon={<ExternalLink size={15} />}>
                Detalhes
              </Button>
            </article>
          ))}
        </div>
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
        <form id="form-form" className="form-grid" onSubmit={save} noValidate>
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
        subtitle="Consulta individual — GET /api/formations/{id}"
        width={520}
      >
        {detailLoading || !detail ? (
          <div className="stack">
            <Skeleton h={20} w="60%" />
            <Skeleton h={16} w="40%" />
            <Skeleton h={16} w="50%" />
          </div>
        ) : (
          <dl className="stack" style={{ gap: 12, margin: 0 }}>
            <Detail label="Curso">{detail.course.name}</Detail>
            <Detail label="Tipo">{formationLabel(detail.formationType)}</Detail>
            <Detail label="Início">{fullDate(detail.startDate)}</Detail>
            <Detail label={isFuture(detail.finishDate) ? "Previsão de conclusão" : "Conclusão"}>
              {detail.finishDate ? fullDate(detail.finishDate) : "Em andamento"}
            </Detail>
            <Detail label="Situação">{detail.inProgress || isFuture(detail.finishDate) ? "Em andamento" : "Concluído"}</Detail>
            <Detail label="ID">#{detail.formationId}</Detail>
          </dl>
        )}
      </Modal>
    </>
  );
}

function Detail({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="row between" style={{ borderBottom: "1px solid var(--line)", paddingBottom: 10 }}>
      <dt className="muted" style={{ fontWeight: 650 }}>
        {label}
      </dt>
      <dd style={{ margin: 0, fontWeight: 700, textAlign: "right" }}>{children}</dd>
    </div>
  );
}
