import { useState, type FormEvent } from "react";
import { Award, CalendarDays, Clock, Plus, School, Pencil, Trash2 } from "lucide-react";
import { useProfile } from "../auth/ProfileContext";
import { resumeApi } from "../api/endpoints";
import type { CertificationResponse } from "../api/types";
import { useToast } from "../components/ui/Toast";
import { Button, CatalogFeedback, CatalogSelect, Field, ListSkeleton, Modal, PageHeader } from "../components/ui/ui";
import { isFuture, monthYear } from "../lib/format";
import "../styles/certifications.css";

const EMPTY = { name: "", institutionId: null as number | null, finishDate: "", inProgress: false };

export default function Certifications() {
  const { profile, loading, error, reload } = useProfile();
  const toast = useToast();
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY);
  const [touched, setTouched] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);

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
      const body = { name: form.name.trim(), institutionId: form.institutionId!, finishDate: form.inProgress ? null : form.finishDate };
      if (editingId) await resumeApi.updateCertification(editingId, body);
      else await resumeApi.addCertifications([body]);
      toast.success(editingId ? "Certificação atualizada" : "Certificação adicionada");
      setOpen(false);
      await reload();
    } catch (err) {
      toast.error(err);
    } finally {
      setSaving(false);
    }
  }

  function openModal() {
    setEditingId(null);
    setForm(EMPTY);
    setTouched(false);
    setOpen(true);
  }

  function edit(item: CertificationResponse) {
    setEditingId(item.certificationId);
    setForm({ name: item.name, institutionId: item.institution.institutionId,
      finishDate: item.finishDate ?? "", inProgress: item.inProgress });
    setTouched(false);
    setOpen(true);
  }

  async function remove(item: CertificationResponse) {
    if (!window.confirm(`Excluir a certificação "${item.name}"?`)) return;
    setDeletingId(item.certificationId);
    try { await resumeApi.deleteCertification(item.certificationId); await reload(); toast.success("Certificação excluída"); }
    catch (err) { toast.error(err); }
    finally { setDeletingId(null); }
  }

  const list = (profile?.certifications ?? []).slice().sort((a, b) => (b.finishDate ?? "9999").localeCompare(a.finishDate ?? "9999"));

  return (
    <div className="certifications-page">
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

      {error && <CatalogFeedback onRetry={reload}>{error}</CatalogFeedback>}
      {loading ? (
        <div role="status" aria-label="Carregando certificações"><ListSkeleton /></div>
      ) : error && !profile ? null : list.length === 0 ? (
        <section className="empty certification-empty" aria-labelledby="certification-empty-title">
          <div className="certification-empty-main">
            <div className="certification-emblem" aria-hidden="true"><Award size={34} /><span><Plus size={15} /></span></div>
            <span className="certification-kicker">Seu desenvolvimento profissional</span>
            <h2 id="certification-empty-title">Cada conquista<br />merece seu espaço.</h2>
            <p>Você ainda não cadastrou certificações. Reúna os cursos, bootcamps e qualificações que reforçam seu perfil, inclusive os que estão em andamento.</p>
            <Button icon={<Plus size={18} />} onClick={openModal}>Adicionar a primeira certificação</Button>
          </div>
          <aside className="certification-guide" aria-label="Como registrar uma certificação">
            <span className="certification-kicker">O que vamos reunir</span>
            <ol>
              <li><span>01</span><div><h3>Certificação ou curso</h3><p>O nome que identifica essa etapa do seu aprendizado.</p></div></li>
              <li><span>02</span><div><h3>Instituição responsável</h3><p>A organização associada à sua certificação ou ao seu curso.</p></div></li>
              <li><span>03</span><div><h3>Conclusão ou andamento</h3><p>A data de conclusão, que pode ser prevista, ou a indicação de que você ainda está fazendo.</p></div></li>
            </ol>
            <p className="certification-guide-note">Organize suas qualificações para que elas também façam parte da sua história no currículo.</p>
          </aside>
        </section>
      ) : (
        <section className="certification-history" aria-labelledby="certification-history-title">
          <header className="certification-list-header">
            <div><span className="certification-kicker">Suas qualificações</span><h2 id="certification-history-title">Aprendizado que agrega valor.</h2></div>
            <span className="certification-count">{list.length} {list.length === 1 ? "certificação cadastrada" : "certificações cadastradas"}</span>
          </header>
          <div className="cat-grid certification-grid">
          {list.map((c) => (
            <article key={c.certificationId} className="item certification-record">
              <div className="certification-record-top">
                <span className="icon-tile"><Award size={23} /></span>
                {c.inProgress || isFuture(c.finishDate) ? <span className="badge warning">Em andamento</span> : <span className="badge success">Concluído</span>}
              </div>
              <div className="certification-record-content">
                <span className="certification-kicker">Certificação ou curso</span>
                <h3 className="item-title">{c.name}</h3>
                <p className="certification-institution"><School size={16} aria-hidden="true" /><span>{c.institution.name}</span></p>
              </div>
              <div className="certification-record-foot">
                {c.finishDate ? <><CalendarDays size={17} aria-hidden="true" /><div><span className="certification-kicker">{isFuture(c.finishDate) ? "Conclusão prevista" : "Conclusão"}</span><p>{monthYear(c.finishDate)}</p></div></> : <><Clock size={17} aria-hidden="true" /><div><span className="certification-kicker">Conclusão</span><p>Data não informada</p></div></>}
              </div>
              <div className="row row-wrap"><Button variant="secondary" size="sm" icon={<Pencil size={15} />} onClick={() => edit(c)}>Editar</Button><Button variant="ghost" size="sm" icon={<Trash2 size={15} />} disabled={deletingId === c.certificationId} onClick={() => remove(c)}>Excluir</Button></div>
            </article>
          ))}
          </div>
        </section>
      )}

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        icon={<Award size={20} />}
        title={editingId ? "Editar certificação" : "Nova certificação"}
        subtitle="Curso, certificado ou bootcamp."
        footer={
          <>
            <Button variant="secondary" onClick={() => setOpen(false)}>
              Cancelar
            </Button>
            <Button type="submit" form="cert-form" loading={saving}>
              {editingId ? "Salvar alterações" : "Salvar certificação"}
            </Button>
          </>
        }
      >
        <form id="cert-form" className="certification-form" onSubmit={save} noValidate>
          <fieldset className="certification-form-section">
            <legend><span>01</span> Identificação da certificação</legend>
            <p>Informe a qualificação e a instituição responsável.</p>
            <div className="form-grid">
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
          <Field className="span-2" label="Instituição" htmlFor="ce-inst" error={err("institutionId")}>
            <CatalogSelect
              id="ce-inst"
              catalog="institutions"
              value={form.institutionId}
              onChange={(v) => set("institutionId", v)}
              newLabel="Cadastrar nova instituição…"
              invalid={!!err("institutionId")}
            />
          </Field>
            </div>
          </fieldset>
          <fieldset className="certification-form-section">
            <legend><span>02</span> Conclusão</legend>
            <p>Informe a data ou indique que você ainda está fazendo.</p>
            <div className="form-grid">
          <Field className="span-2" label="Data de conclusão" htmlFor="ce-date" error={err("finishDate")}>
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
            </div>
          </fieldset>
        </form>
      </Modal>
    </div>
  );
}
