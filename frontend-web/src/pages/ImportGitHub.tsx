import { useState } from "react";
import { Link } from "react-router-dom";
import { FolderGit2, Search } from "lucide-react";
import { request, errorMessage } from "../api/client";
import { useProfile } from "../auth/ProfileContext";
import { Button, Field, PageHeader } from "../components/ui/ui";
import "../styles/github.css";

interface Repository {
  name: string;
  description: string;
  url: string;
  language: string;
  fork: boolean;
  archived: boolean;
}
interface Preview {
  username: string;
  name: string;
  bio: string;
  location: string;
  profileUrl: string;
  repositories: Repository[];
  hasMore: boolean;
  page: number;
}
interface Selection { repository: string; title: string; description: string }

export default function ImportGitHub() {
  const { profile, reload } = useProfile();
  const [username, setUsername] = useState("");
  const [preview, setPreview] = useState<Preview | null>(null);
  const [selected, setSelected] = useState<Record<string, Selection>>({});
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [saved, setSaved] = useState(false);
  const choices = Object.values(selected);
  const existing = new Set(profile?.projects.map(project => project.link.toLowerCase().replace(/\/$/, "").replace(/\.git$/, "")));

  async function search(page = 1) {
    setBusy(true);
    setError("");
    setSaved(false);
    if (page === 1) { setSelected({}); setPreview(null); }
    try {
      const result = await request<Preview>("/api/github/preview?username=" +
        encodeURIComponent(page === 1 ? username : preview!.username) + "&page=" + page);
      setPreview(result);
    } catch (e) { setError(errorMessage(e).title); }
    finally { setBusy(false); }
  }

  function toggle(repository: Repository, checked: boolean) {
    setSaved(false);
    setSelected(current => {
      const next = { ...current };
      if (checked) next[repository.name] = {
        repository: repository.name, title: repository.name, description: repository.description,
      };
      else delete next[repository.name];
      return next;
    });
  }

  async function save() {
    if (!preview || !choices.length || busy) return;
    setBusy(true);
    setError("");
    try {
      await request("/api/github/import", { method: "POST", body: { username: preview.username, projects: choices } });
      await reload();
      setSelected({});
      setSaved(true);
    } catch (e) { setError(errorMessage(e).title); }
    finally { setBusy(false); }
  }

  return <div className="github-page">
    <PageHeader eyebrow="Currículo" title="Traga seus projetos do GitHub."
      text="Consulte seu perfil público, escolha os projetos e descreva sua contribuição antes de incluí-los no currículo." />
    <form className="card github-search" onSubmit={e => { e.preventDefault(); void search(); }}>
      <Field label="Usuário ou link do GitHub" htmlFor="github-profile">
        <input id="github-profile" value={username} onChange={e => setUsername(e.target.value)}
          placeholder="https://github.com/seu-usuario" required disabled={busy} maxLength={100} />
      </Field>
      <Button type="submit" loading={busy} disabled={!username.trim()} icon={<Search size={18} />}>Buscar perfil</Button>
    </form>
    {error && <div className="alert error" role="alert">{error}</div>}
    {saved && <div className="alert" role="status">Projetos salvos. <Link to="/app/gerar">Gerar currículo</Link></div>}
    {preview && <>
      <section className="card github-profile">
        <FolderGit2 size={28} aria-hidden="true" /><div>
          <h2>{preview.name || preview.username}</h2>
          <a href={preview.profileUrl} target="_blank" rel="noreferrer">@{preview.username}</a>
          {preview.bio && <p>{preview.bio}</p>}{preview.location && <p>{preview.location}</p>}
        </div>
      </section>
      <section aria-label="Repositórios públicos" className="github-repositories">
        {preview.repositories.length === 0 && <p>Nenhum repositório público nesta página.</p>}
        {preview.repositories.map(repository => {
          const imported = existing.has(repository.url.toLowerCase());
          return <article className="card github-repository" key={repository.name}>
            <label className="github-choice"><input type="checkbox" checked={!!selected[repository.name]}
              disabled={busy || imported || (!selected[repository.name] && choices.length >= 10)}
              onChange={e => toggle(repository, e.target.checked)} />
              <strong>{repository.name}</strong>{imported && <span>Já adicionado</span>}
            </label>
            <p>{repository.description || "Sem descrição pública. Preencha sua contribuição ao selecionar."}</p>
            <div className="github-meta">{repository.language && <span>{repository.language}</span>}
              {repository.fork && <span>Fork</span>}{repository.archived && <span>Arquivado</span>}
              <a href={repository.url} target="_blank" rel="noreferrer">Ver repositório</a>
            </div>
          </article>;
        })}
      </section>
      <div className="github-pagination">
        <Button variant="secondary" disabled={busy || preview.page === 1} onClick={() => search(preview.page - 1)}>Anterior</Button>
        <span>Página {preview.page}</span>
        <Button variant="secondary" disabled={busy || !preview.hasMore} onClick={() => search(preview.page + 1)}>Próxima</Button>
      </div>
    </>}
    {choices.length > 0 && <section className="card github-review">
      <h2>Revise os projetos selecionados</h2>
      <p>Inclua apenas atividades que você realizou. A linguagem detectada no repositório pode ajudar a descrever o projeto.</p>
      {choices.map(item => <fieldset key={item.repository} disabled={busy}>
        <legend>{item.repository}</legend>
        <Field label="Nome no currículo" htmlFor={"title-" + item.repository}>
          <input id={"title-" + item.repository} value={item.title} maxLength={150}
            onChange={e => setSelected({ ...selected, [item.repository]: { ...item, title: e.target.value } })} />
        </Field>
        <Field label="O que você fez neste projeto?" htmlFor={"description-" + item.repository}>
          <textarea id={"description-" + item.repository} rows={4} value={item.description} maxLength={10000}
            onChange={e => setSelected({ ...selected, [item.repository]: { ...item, description: e.target.value } })} />
        </Field>
        <Button variant="ghost" onClick={() => setSelected(current => {
          const next = { ...current }; delete next[item.repository]; return next;
        })}>Remover seleção</Button>
      </fieldset>)}
      <Button loading={busy} disabled={choices.some(item => !item.title.trim() || !item.description.trim())}
        onClick={save}>Salvar {choices.length} projeto(s) no currículo</Button>
    </section>}
  </div>;
}
