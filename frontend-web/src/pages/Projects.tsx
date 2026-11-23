import { useState, type FormEvent } from "react";
import { Check, ExternalLink, FolderGit2, Link2, Plus, Pencil, Trash2 } from "lucide-react";
import { useProfile } from "../auth/ProfileContext";
import { useCatalog } from "../auth/CatalogContext";
import { resumeApi } from "../api/endpoints";
import type { ProjectResponse } from "../api/types";
import { useToast } from "../components/ui/Toast";
import { Button, CatalogFeedback, Field, ListSkeleton, Modal, PageHeader, Skeleton } from "../components/ui/ui";
import { ensureProtocol, initials, prettyUrl } from "../lib/format";
import "../styles/projects.css";

const EMPTY = { title: "", description: "", link: "", abilitiesIds: [] as number[] };

export default function Projects() {
  const { profile, loading, error, reload } = useProfile();
  const abilities = useCatalog("abilities");
  const toast = useToast();
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY);
  const [touched, setTouched] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [abilityQuery, setAbilityQuery] = useState("");

  const set = <K extends keyof typeof EMPTY>(k: K, v: (typeof EMPTY)[K]) => setForm((f) => ({ ...f, [k]: v }));
  const errors = {
    title: !form.title.trim() && "Dê um nome ao projeto",
    description: !form.description.trim() && "Descreva o projeto",
    link: !form.link.trim() && "Informe um link (GitHub, deploy, portfólio…)",
  };
  const valid = !Object.values(errors).some(Boolean);
  const err = (k: keyof typeof errors): string | null => (touched && typeof errors[k] === "string" ? (errors[k] as string) : null);

  const toggle = (id: number) =>
    setForm((f) => ({
      ...f,
      abilitiesIds: f.abilitiesIds.includes(id) ? f.abilitiesIds.filter((x) => x !== id) : [...f.abilitiesIds, id],
    }));

  async function save(e: FormEvent) {
    e.preventDefault();
    setTouched(true);
    if (!valid) return;
    setSaving(true);
    try {
      const body = {
          title: form.title.trim(),
          description: form.description.trim(),
          link: ensureProtocol(form.link),
          abilitiesIds: form.abilitiesIds,
        };
      if (editingId) await resumeApi.updateProject(editingId, body);
      else await resumeApi.addProjects([body]);
      toast.success(editingId ? "Projeto atualizado" : "Projeto adicionado");
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

  function edit(item: ProjectResponse) {
    setEditingId(item.projectId);
    setForm({ title: item.title, description: item.description, link: item.link,
      abilitiesIds: item.abilities.map((ability) => ability.abilityId) });
    setTouched(false);
    setOpen(true);
  }

  async function remove(item: ProjectResponse) {
    if (!window.confirm(`Excluir o projeto "${item.title}"?`)) return;
    setDeletingId(item.projectId);
    try { await resumeApi.deleteProject(item.projectId); await reload(); toast.success("Projeto excluído"); }
    catch (err) { toast.error(err); }
    finally { setDeletingId(null); }
  }

  const list = profile?.projects ?? [];
  const sortedAbilities = [...abilities.items]
    .filter((a) => form.abilitiesIds.includes(a.id) || (abilityQuery.trim() && a.name.toLowerCase().includes(abilityQuery.trim().toLowerCase())))
    .sort((a, b) => Number(form.abilitiesIds.includes(b.id)) - Number(form.abilitiesIds.includes(a.id)) || a.name.localeCompare(b.name, "pt-BR"))
    .slice(0, 40);

  return (
    <div className="projects-page">
      <PageHeader
        eyebrow="Trajetória"
        title="Projetos"
        text="Dê visibilidade ao que você construiu. Reúna projetos pessoais, acadêmicos e profissionais que mostram suas habilidades na prática."
        actions={
          <Button icon={<Plus size={18} />} onClick={openModal}>
            Adicionar projeto
          </Button>
        }
      />

      {error && <CatalogFeedback onRetry={reload}>{error}</CatalogFeedback>}
      {loading ? (
        <div role="status" aria-label="Carregando projetos"><ListSkeleton /></div>
      ) : error && !profile ? null : list.length === 0 ? (
        <section className="empty project-empty" aria-labelledby="project-empty-title">
          <div className="project-empty-main">
            <div className="project-emblem" aria-hidden="true"><FolderGit2 size={34} /><span><Plus size={15} /></span></div>
            <span className="project-kicker">Ideias que você colocou em prática</span>
            <h2 id="project-empty-title">O que você construiu<br />merece ser visto.</h2>
            <p>Você ainda não cadastrou projetos. Mostre o problema que resolveu, sua participação e as habilidades que usou para chegar ao resultado.</p>
            <Button icon={<Plus size={18} />} onClick={openModal}>Adicionar o primeiro projeto</Button>
          </div>
          <aside className="project-guide" aria-label="Como apresentar seu projeto">
            <span className="project-kicker">Do projeto para o currículo</span>
            <ol>
              <li><span>01</span><div><h3>Conte o que construiu</h3><p>Dê um nome e descreva o propósito e sua participação.</p></div></li>
              <li><span>02</span><div><h3>Mostre onde encontrar</h3><p>Inclua um link para repositório, demonstração ou portfólio.</p></div></li>
              <li><span>03</span><div><h3>Conecte suas habilidades</h3><p>Se quiser, associe as tecnologias utilizadas no projeto.</p></div></li>
            </ol>
            <p className="project-guide-note">Projetos pessoais, acadêmicos e profissionais ajudam a demonstrar o que você sabe fazer.</p>
          </aside>
        </section>
      ) : (
        <section className="project-portfolio" aria-labelledby="project-portfolio-title">
          <header className="project-list-header"><div><span className="project-kicker">Seu portfólio no currículo</span><h2 id="project-portfolio-title">Ideias que ganharam forma.</h2></div><span className="project-count">{list.length} {list.length === 1 ? "projeto cadastrado" : "projetos cadastrados"}</span></header>
          <div className="cat-grid project-grid">
          {list.map((p) => (
            <article key={p.projectId} className="item project-record">
              <div className="project-stage" aria-hidden="true"><span className="project-mark">{initials(p.title)}</span><FolderGit2 size={25} /></div>
              <div className="project-record-body">
                <span className="project-kicker">Projeto</span>
                <h3 className="item-title">{p.title}</h3>
                <p className="item-desc">{p.description}</p>
              {p.abilities.length > 0 && (
                <div className="project-stack"><span className="project-kicker">Tecnologias aplicadas</span><div className="chip-group">
                  {p.abilities.map((a) => (
                    <span key={a.abilityId} className="badge neutral">
                      {a.name}
                    </span>
                  ))}
                </div></div>
              )}
              </div>
              {p.link.trim() && <div className="project-record-foot"><span className="project-url"><Link2 size={15} aria-hidden="true" /><span>{prettyUrl(p.link)}</span></span><a href={ensureProtocol(p.link)} target="_blank" rel="noreferrer" className="btn btn-secondary btn-sm" aria-label={`Abrir projeto ${p.title} em nova aba`}><ExternalLink size={15} aria-hidden="true" /> Abrir projeto</a></div>}
              <div className="row row-wrap"><Button variant="secondary" size="sm" icon={<Pencil size={15} />} onClick={() => edit(p)}>Editar</Button><Button variant="ghost" size="sm" icon={<Trash2 size={15} />} disabled={deletingId === p.projectId} onClick={() => remove(p)}>Excluir</Button></div>
            </article>
          ))}
          </div>
        </section>
      )}

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        icon={<FolderGit2 size={20} />}
        title={editingId ? "Editar projeto" : "Novo projeto"}
        subtitle="O que você construiu e com quais tecnologias."
        width={680}
        footer={
          <>
            <Button variant="secondary" onClick={() => setOpen(false)}>
              Cancelar
            </Button>
            <Button type="submit" form="proj-form" loading={saving}>
              {editingId ? "Salvar alterações" : "Salvar projeto"}
            </Button>
          </>
        }
      >
        <form id="proj-form" className="project-form" onSubmit={save} noValidate>
          <fieldset className="project-form-section">
            <legend><span>01</span> Apresentação do projeto</legend><p>Conte o que você construiu e qual foi sua contribuição.</p>
            <div className="form-grid">
          <Field className="span-2" label="Nome do projeto" htmlFor="pr-title" error={err("title")}>
            <input
              id="pr-title"
              className="input"
              placeholder="Ex.: CurrículoAI"
              value={form.title}
              onChange={(e) => set("title", e.target.value)}
              aria-invalid={!!err("title")}
            />
          </Field>
          <Field className="span-2" label="Descrição" htmlFor="pr-desc" error={err("description")}>
            <textarea
              id="pr-desc"
              className="textarea"
              placeholder="Qual problema resolve? Qual foi sua participação?"
              value={form.description}
              onChange={(e) => set("description", e.target.value)}
              aria-invalid={!!err("description")}
            />
          </Field>
            </div>
          </fieldset>
          <fieldset className="project-form-section">
            <legend><span>02</span> Acesso ao projeto</legend><p>Use um link para repositório, demonstração ou portfólio.</p>
            <Field label="Link" htmlFor="pr-link" error={err("link")}>
              <input id="pr-link" className="input" placeholder="github.com/voce/projeto" value={form.link} onChange={(e) => set("link", e.target.value)} aria-invalid={!!err("link")} />
            </Field>
          </fieldset>
          <fieldset className="project-form-section">
            <legend><span>03</span> Habilidades aplicadas</legend><p>Associe as tecnologias que você utilizou, se desejar.</p>
          <Field label="Tecnologias usadas" optional hint={<span role="status">{form.abilitiesIds.length} selecionada(s)</span>}>
            <input className="input" type="search" aria-label="Buscar tecnologias" placeholder="Digite uma tecnologia para buscar" value={abilityQuery} onChange={(e) => setAbilityQuery(e.target.value)} />
            {abilities.error && <CatalogFeedback onRetry={abilities.refresh}>Não foi possível carregar as tecnologias.</CatalogFeedback>}
            {abilities.loading && <div className="project-technologies-loading" role="status" aria-label="Carregando tecnologias"><Skeleton h={44} w="30%" /><Skeleton h={44} w="30%" /><Skeleton h={44} w="30%" /></div>}
            {!abilities.loading && !abilities.error && sortedAbilities.length === 0 && <p className="project-technologies-empty">Nenhuma tecnologia disponível no momento.</p>}
            <div className="chip-group project-technology-options" role="group" aria-label="Tecnologias usadas">
              {sortedAbilities.map((a) => (
                <button
                  key={a.id}
                  type="button"
                  className="chip"
                  aria-pressed={form.abilitiesIds.includes(a.id)}
                  onClick={() => toggle(a.id)}
                >
                  {form.abilitiesIds.includes(a.id) && <Check size={14} aria-hidden="true" />}{a.name}
                </button>
              ))}
            </div>
          </Field>
          </fieldset>
        </form>
      </Modal>
    </div>
  );
}
