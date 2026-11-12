import { useState, type FormEvent } from "react";
import { ExternalLink, FolderGit2, Plus } from "lucide-react";
import { useProfile } from "../auth/ProfileContext";
import { useCatalog } from "../auth/CatalogContext";
import { resumeApi } from "../api/endpoints";
import { useToast } from "../components/ui/Toast";
import { Button, EmptyState, Field, ListSkeleton, Modal, PageHeader } from "../components/ui/ui";
import { ensureProtocol, prettyUrl } from "../lib/format";

const EMPTY = { title: "", description: "", link: "", abilitiesIds: [] as number[] };

export default function Projects() {
  const { profile, loading, reload } = useProfile();
  const abilities = useCatalog("abilities");
  const toast = useToast();
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY);
  const [touched, setTouched] = useState(false);
  const [saving, setSaving] = useState(false);

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
      await resumeApi.addProjects([
        {
          title: form.title.trim(),
          description: form.description.trim(),
          link: ensureProtocol(form.link),
          abilitiesIds: form.abilitiesIds,
        },
      ]);
      toast.success("Projeto adicionado");
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

  const list = profile?.projects ?? [];
  const sortedAbilities = [...abilities.items].sort((a, b) => a.name.localeCompare(b.name, "pt-BR"));

  return (
    <>
      <PageHeader
        eyebrow="Trajetória"
        title="Projetos"
        text="Projetos pessoais, acadêmicos ou open source. Ótimos para quem está começando."
        actions={
          <Button icon={<Plus size={18} />} onClick={openModal}>
            Adicionar projeto
          </Button>
        }
      />

      {loading ? (
        <ListSkeleton />
      ) : list.length === 0 ? (
        <EmptyState
          icon={<FolderGit2 size={26} />}
          title="Nenhum projeto ainda"
          text="Mostre na prática o que você sabe fazer. Um link para o GitHub já faz diferença."
          action={
            <Button icon={<Plus size={18} />} onClick={openModal}>
              Adicionar projeto
            </Button>
          }
        />
      ) : (
        <div className="cat-grid" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(320px, 1fr))", gap: 14 }}>
          {list.map((p) => (
            <article key={p.projectId} className="item" style={{ flexDirection: "column", gap: 12 }}>
              <div className="row between">
                <span className="icon-tile grad">
                  <FolderGit2 size={20} />
                </span>
                <a href={ensureProtocol(p.link)} target="_blank" rel="noreferrer" className="btn btn-secondary btn-sm">
                  <ExternalLink size={15} /> Abrir
                </a>
              </div>
              <div>
                <div className="item-title">{p.title}</div>
                <div className="item-meta">
                  <span style={{ wordBreak: "break-all" }}>{prettyUrl(p.link)}</span>
                </div>
                <p className="item-desc">{p.description}</p>
              </div>
              {p.abilities.length > 0 && (
                <div className="chip-group" style={{ gap: 6 }}>
                  {p.abilities.map((a) => (
                    <span key={a.abilityId} className="badge neutral">
                      {a.name}
                    </span>
                  ))}
                </div>
              )}
            </article>
          ))}
        </div>
      )}

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        icon={<FolderGit2 size={20} />}
        title="Novo projeto"
        subtitle="O que você construiu e com quais tecnologias."
        width={680}
        footer={
          <>
            <Button variant="secondary" onClick={() => setOpen(false)}>
              Cancelar
            </Button>
            <Button type="submit" form="proj-form" loading={saving}>
              Salvar projeto
            </Button>
          </>
        }
      >
        <form id="proj-form" className="form-grid" onSubmit={save} noValidate>
          <Field label="Nome do projeto" htmlFor="pr-title" error={err("title")}>
            <input
              id="pr-title"
              className="input"
              placeholder="Ex.: CurrículoAI"
              value={form.title}
              onChange={(e) => set("title", e.target.value)}
              aria-invalid={!!err("title")}
            />
          </Field>
          <Field label="Link" htmlFor="pr-link" error={err("link")}>
            <input
              id="pr-link"
              className="input"
              placeholder="github.com/voce/projeto"
              value={form.link}
              onChange={(e) => set("link", e.target.value)}
              aria-invalid={!!err("link")}
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
          <Field className="span-2" label="Tecnologias usadas" optional hint={`${form.abilitiesIds.length} selecionada(s)`}>
            <div className="chip-group">
              {sortedAbilities.map((a) => (
                <button
                  key={a.id}
                  type="button"
                  className="chip"
                  aria-pressed={form.abilitiesIds.includes(a.id)}
                  onClick={() => toggle(a.id)}
                >
                  {a.name}
                </button>
              ))}
            </div>
          </Field>
        </form>
      </Modal>
    </>
  );
}
