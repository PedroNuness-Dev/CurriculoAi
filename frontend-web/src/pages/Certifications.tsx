import { useState, type FormEvent } from "react";
import { Award, CalendarDays, Plus, School } from "lucide-react";
import { useProfile } from "../auth/ProfileContext";
import { resumeApi } from "../api/endpoints";
import { useToast } from "../components/ui/Toast";
import { Button, CatalogSelect, EmptyState, Field, ListSkeleton, Modal, PageHeader } from "../components/ui/ui";
import { isFuture, monthYear } from "../lib/format";

const EMPTY = { name: "", institutionId: null as number | null, finishDate: "", inProgress: false };

export default function Certifications() {
  const { profile, loading, reload } = useProfile();
  const toast = useToast();
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY);
  const [touched, setTouched] = useState(false);
  const [saving, setSaving] = useState(false);

  const set = <K extends keyof typeof EMPTY>(k: K, v: (typeof EMPTY)[K]) => setForm((f) => ({ ...f, [k]: v }));
  const errors = {
    name: !form.name.trim() && "Informe o nome do certificado",
    institutionId: !form.institutionId && "Escolha a instituição",
    finishDate: !form.inProgress && !form.finishDate && "Informe a data ou marque “em andamento”",
  };
  const valid = !Object.values(errors).some(Boolean);
  const err = (k: keyof typeof errors): string | null => (touched && typeof errors[k] === "string" ? (errors[k] as string) : null);

  async function save(e: FormEvent) {
    e.preventDefault();
    setTouched(true);
    if (!valid) return;
    setSaving(true);
    try {
      await resumeApi.addCertifications([
        { name: form.name.trim(), institutionId: form.institutionId!, finishDate: form.inProgress ? null : form.finishDate },
      ]);
      toast.success("Certificação adicionada");
      setOpen(false);
      await reload();
    } catch (err) {
      toast.error(err);
    } finally {
      setSaving(false);
    }
  }

  function openModal() {
    setForm(EMPTY);
    setTouched(false);
    setOpen(true);
  }

  const list = (profile?.certifications ?? []).slice().sort((a, b) => (b.finishDate ?? "9999").localeCompare(a.finishDate ?? "9999"));

  return (
    <>
      <PageHeader
        eyebrow="Trajetória"
        title="Certificações e cursos"
        text="Cursos online, bootcamps e certificações profissionais que reforçam seu perfil."
        actions={
          <Button icon={<Plus size={18} />} onClick={openModal}>
            Adicionar certificação
          </Button>
        }
      />

      {loading ? (
        <ListSkeleton />
      ) : list.length === 0 ? (
        <EmptyState
          icon={<Award size={26} />}
          title="Nenhuma certificação ainda"
          text="Cursos da Alura, Udemy, DIO, Coursera… tudo conta para mostrar que você está se atualizando."
          action={
            <Button icon={<Plus size={18} />} onClick={openModal}>
              Adicionar certificação
            </Button>
          }
        />
      ) : (
        <div className="cat-grid" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(300px, 1fr))", gap: 14 }}>
          {list.map((c) => (
            <article key={c.certificationId} className="item" style={{ flexDirection: "column", gap: 14 }}>
              <div className="row between">
                <span className="icon-tile grad">
                  <Award size={20} />
                </span>
                {c.inProgress || isFuture(c.finishDate) ? <span className="badge warning">Em andamento</span> : <span className="badge success">Concluído</span>}
              </div>
              <div>
                <div className="item-title">{c.name}</div>
                <div className="item-meta">
                  <span>
                    <School size={14} /> {c.institution.name}
                  </span>
                  {c.finishDate && (
                    <span>
                      <CalendarDays size={14} /> {monthYear(c.finishDate)}
                    </span>
                  )}
                </div>
              </div>
            </article>
          ))}
        </div>
      )}

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        icon={<Award size={20} />}
        title="Nova certificação"
        subtitle="Curso, certificado ou bootcamp."
        footer={
          <>
            <Button variant="secondary" onClick={() => setOpen(false)}>
              Cancelar
            </Button>
            <Button type="submit" form="cert-form" loading={saving}>
              Salvar certificação
            </Button>
          </>
        }
      >
        <form id="cert-form" className="form-grid" onSubmit={save} noValidate>
          <Field className="span-2" label="Nome" htmlFor="ce-name" error={err("name")}>
            <input
              id="ce-name"
              className="input"
              placeholder="Ex.: Formação Java e Orientação a Objetos"
              value={form.name}
              onChange={(e) => set("name", e.target.value)}
              aria-invalid={!!err("name")}
            />
          </Field>
          <Field label="Instituição" htmlFor="ce-inst" error={err("institutionId")}>
            <CatalogSelect
              id="ce-inst"
              catalog="institutions"
              value={form.institutionId}
              onChange={(v) => set("institutionId", v)}
              newLabel="Cadastrar nova instituição…"
              invalid={!!err("institutionId")}
            />
          </Field>
          <Field label="Data de conclusão" htmlFor="ce-date" error={err("finishDate")}>
            <input
              id="ce-date"
              type="date"
              className="input"
              value={form.inProgress ? "" : form.finishDate}
              disabled={form.inProgress}
              onChange={(e) => set("finishDate", e.target.value)}
            />
          </Field>
          <label className="check span-2">
            <input type="checkbox" checked={form.inProgress} onChange={(e) => set("inProgress", e.target.checked)} />
            Ainda estou fazendo
          </label>
        </form>
      </Modal>
    </>
  );
}
