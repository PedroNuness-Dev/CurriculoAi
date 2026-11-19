import { useEffect, useRef, useState, type CSSProperties } from "react";
import { Link } from "react-router-dom";
import {
  ArrowRight, ArrowUpRight, Check, CircleAlert, FolderGit2, GitBranch, GitCommitHorizontal, GitFork,
  ListChecks, MapPin, PenLine, Plus, Search, ShieldCheck, Star, Trash2, X,
} from "lucide-react";
import { request, errorMessage } from "../api/client";
import { useProfile } from "../auth/ProfileContext";
import { Button, Field, Modal, PageHeader } from "../components/ui/ui";
import { useToast } from "../components/ui/Toast";
import { githubUsername } from "../lib/format";
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
interface Selection { repository: string; title: string; description: string; language: string; source: string }

/** Tempo mínimo do carregamento: evita que a animação apareça e suma num piscar de olhos. */
const MIN_LOADING_MS = 1600;
const MAX_SELECTION = 10;
const wait = (ms: number) => new Promise(resolve => setTimeout(resolve, Math.max(0, ms)));

/** Cores usadas pelo próprio GitHub para as linguagens mais comuns. */
const LANGUAGE_COLORS: Record<string, string> = {
  Java: "#b07219", Kotlin: "#a97bff", TypeScript: "#3178c6", JavaScript: "#f1e05a", Python: "#3572a5",
  HTML: "#e34c26", CSS: "#563d7c", SCSS: "#c6538c", "C#": "#178600", "C++": "#f34b7d", C: "#555555",
  Go: "#00add8", Rust: "#dea584", PHP: "#4f5d95", Ruby: "#701516", Dart: "#00b4ab", Swift: "#f05138",
  Shell: "#89e051", Vue: "#41b883", "Jupyter Notebook": "#da5b0b", Dockerfile: "#384d54",
};

export default function ImportGitHub() {
  const { profile, reload } = useProfile();
  const toast = useToast();
  const [username, setUsername] = useState("");
  const [typed, setTyped] = useState(false);
  const [preview, setPreview] = useState<Preview | null>(null);
  const [selected, setSelected] = useState<Record<string, Selection>>({});
  const [loading, setLoading] = useState<{ username: string; page: number } | null>(null);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [saved, setSaved] = useState(false);
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [focusRepo, setFocusRepo] = useState<string | null>(null);
  const [attempted, setAttempted] = useState(false);
  const requestId = useRef(0);
  const resultsRef = useRef<HTMLDivElement>(null);
  const busy = !!loading || saving;
  const choices = Object.values(selected);
  const pending = choices.filter(item => !item.title.trim() || !item.description.trim()).length;
  const existing = new Set(profile?.projects.map(project => project.link.toLowerCase().replace(/\/$/, "").replace(/\.git$/, "")));
  // GitHub cadastrado em "Redes e links" (procura pelo nome, não pelo id, que depende do seed).
  const suggested = githubUsername(profile?.socialNetworks
    .find(item => item.socialNetwork.name.trim().toLowerCase() === "github")?.link);

  // Preenche com o GitHub sugerido assim que o perfil carrega, sem sobrescrever o que o usuário digitou.
  useEffect(() => {
    if (suggested && !typed) setUsername(suggested);
  }, [suggested, typed]);

  // Ao selecionar um repositório, o painel abre já no campo de descrição dele.
  useEffect(() => {
    if (!drawerOpen || !focusRepo) return;
    document.getElementById("description-" + focusRepo)?.focus();
    setFocusRepo(null);
  }, [drawerOpen, focusRepo]);

  async function search(page = 1, override?: string) {
    const target = override ?? (page === 1 ? username.trim() : preview!.username);
    const id = ++requestId.current;
    setLoading({ username: githubUsername(target) ?? target, page });
    setError("");
    setSaved(false);
    if (page === 1) { setSelected({}); setPreview(null); setAttempted(false); }
    else resultsRef.current?.scrollIntoView({ behavior: "smooth", block: "start" });
    const started = Date.now();
    let result: Preview | null = null;
    let failure = "";
    try {
      result = await request<Preview>("/api/github/preview?username=" + encodeURIComponent(target) + "&page=" + page);
    } catch (e) { failure = errorMessage(e).title; }
    await wait(MIN_LOADING_MS - (Date.now() - started));
    if (id !== requestId.current) return; // uma busca mais nova já começou
    if (result) setPreview(result);
    setError(failure);
    setLoading(null);
  }

  function select(repository: Repository) {
    if (selected[repository.name]) {
      setSelected(current => { const next = { ...current }; delete next[repository.name]; return next; });
      return;
    }
    if (choices.length >= MAX_SELECTION) return;
    setSaved(false);
    setSelected(current => ({
      ...current,
      [repository.name]: {
        repository: repository.name, title: repository.name, description: "",
        language: repository.language, source: repository.description,
      },
    }));
    setFocusRepo(repository.name);
    setDrawerOpen(true);
  }

  function update(name: string, patch: Partial<Selection>) {
    setSelected(current => current[name] ? { ...current, [name]: { ...current[name], ...patch } } : current);
  }

  function remove(name: string) {
    setSelected(current => { const next = { ...current }; delete next[name]; return next; });
    if (choices.length <= 1) setDrawerOpen(false);
  }

  async function save() {
    if (!preview || !choices.length || saving) return;
    setAttempted(true);
    const invalid = choices.find(item => !item.title.trim() || !item.description.trim());
    if (invalid) {
      document.getElementById((invalid.title.trim() ? "description-" : "title-") + invalid.repository)?.focus();
      return;
    }
    setSaving(true);
    setError("");
    try {
      const projects = choices.map(({ repository, title, description }) => ({ repository, title, description }));
      await request("/api/github/import", { method: "POST", body: { username: preview.username, projects } });
      await reload();
      toast.success(choices.length === 1 ? "Projeto adicionado ao currículo." : `${choices.length} projetos adicionados ao currículo.`);
      setSelected({});
      setAttempted(false);
      setDrawerOpen(false);
      setSaved(true);
    } catch (e) { setError(errorMessage(e).title); setDrawerOpen(false); }
    finally { setSaving(false); }
  }

  const idle = !preview && !loading;
  const searchForm = <form className="github-search-row" onSubmit={e => { e.preventDefault(); void search(); }}>
    <Field label="Usuário ou link do GitHub" htmlFor="github-profile">
      <div className="input-wrap">
        <GitHubMark size={18} />
        <input id="github-profile" className="input" value={username} onChange={e => { setTyped(true); setUsername(e.target.value); }}
          placeholder="seu-usuario ou https://github.com/seu-usuario" required disabled={busy} maxLength={100}
          aria-describedby="github-suggestion" autoComplete="off" spellCheck={false} />
      </div>
    </Field>
    <Button type="submit" loading={loading?.page === 1} disabled={busy || !username.trim()}
      icon={<Search size={18} />}>Buscar perfil</Button>
  </form>;

  return <div className="github-page">
    <PageHeader eyebrow="Currículo" title="Traga seus projetos do GitHub."
      text="Consulte seu perfil público, escolha os projetos e descreva sua contribuição antes de incluí-los no currículo." />

    {idle ? <section className="card github-hero">
      <div className="github-hero-main">
        <div className="github-emblem" aria-hidden="true"><GitHubMark size={34} /><span><Plus size={15} /></span></div>
        <span className="github-kicker">Importação do GitHub</span>
        <h2>Seus repositórios contam a sua experiência.</h2>
        <p>Busque seu perfil público, escolha até {MAX_SELECTION} projetos e conte o que você fez em cada um.
          É isso que a IA usa para escrever o currículo.</p>
        {searchForm}
        {suggested ? <button type="button" className="github-suggest-card" onClick={() => { setUsername(suggested); void search(1, suggested); }}>
          <span className="github-suggest-avatar" aria-hidden="true">
            <img src={`https://github.com/${suggested}.png?size=96`} alt="" onError={e => { e.currentTarget.style.display = "none"; }} />
            <GitHubMark size={20} />
          </span>
          <span className="github-suggest-text">
            <span>Seu GitHub cadastrado em Redes e links</span>
            <strong>@{suggested}</strong>
          </span>
          <span className="github-suggest-go">Buscar agora <ArrowRight size={16} aria-hidden="true" /></span>
        </button> : <p className="github-suggestion" id="github-suggestion">
          Dica: cadastre seu GitHub em <Link to="/app/redes">Redes e links</Link> e ele aparece aqui pronto para buscar.
        </p>}
      </div>
      <aside className="github-guide" aria-label="Como funciona a importação">
        <span className="github-kicker">Como funciona</span>
        <ol>
          <li><span aria-hidden="true"><Search size={16} /></span><div><h3>Busque seu perfil</h3><p>Informe seu usuário ou o link do perfil público.</p></div></li>
          <li><span aria-hidden="true"><ListChecks size={16} /></span><div><h3>Escolha os repositórios</h3><p>Selecione até {MAX_SELECTION} projetos por importação.</p></div></li>
          <li><span aria-hidden="true"><PenLine size={16} /></span><div><h3>Descreva sua contribuição</h3><p>Conte o que você fez em cada projeto. Esse passo é obrigatório.</p></div></li>
        </ol>
        <p className="github-guide-note"><ShieldCheck size={16} aria-hidden="true" /> Só lemos dados públicos. Nada é alterado na sua conta do GitHub.</p>
      </aside>
    </section> : <section className="card github-search">
      {searchForm}
      <p className="github-suggestion" id="github-suggestion">
        {!suggested && <>Cadastre seu GitHub em <Link to="/app/redes">Redes e links</Link> para ele aparecer aqui.</>}
        {suggested && username.trim() === suggested && <>Sugerido a partir do GitHub cadastrado em Redes e links.</>}
        {suggested && username.trim() !== suggested && <>Seu GitHub cadastrado:{" "}
          <button type="button" className="github-suggestion-use" disabled={busy}
            onClick={() => setUsername(suggested)}>usar @{suggested}</button></>}
      </p>
    </section>}

    {error && <div className="alert error" role="alert">{error}</div>}
    {saved && <div className="alert" role="status">Projetos salvos no seu currículo. <Link to="/app/gerar">Gerar currículo</Link></div>}

    {loading?.page === 1 && <GitHubLoader username={loading.username} page={1} />}

    {preview && loading?.page !== 1 && <>
      <ProfileCard key={preview.username} preview={preview} />
      <div className="github-results" ref={resultsRef}>
        <header className="github-results-head">
          <div>
            <span className="github-kicker">Repositórios públicos</span>
            <h2>Escolha o que entra no currículo</h2>
          </div>
          <span className="github-count">{choices.length} de {MAX_SELECTION} selecionados</span>
        </header>
        {loading ? <GitHubLoader username={loading.username} page={loading.page} compact /> : <>
          {preview.repositories.length === 0 && <p className="card github-empty">Nenhum repositório público nesta página.</p>}
          <section aria-label="Repositórios públicos" className="github-repositories">
            {preview.repositories.map((repository, index) => <RepositoryCard key={repository.name} index={index}
              repository={repository} selected={!!selected[repository.name]}
              imported={existing.has(repository.url.toLowerCase())}
              full={!selected[repository.name] && choices.length >= MAX_SELECTION}
              disabled={busy} onSelect={() => select(repository)} />)}
          </section>
        </>}
        <nav className="github-pagination" aria-label="Páginas de repositórios">
          <Button variant="secondary" size="sm" disabled={busy || preview.page === 1} onClick={() => search(preview.page - 1)}>Anterior</Button>
          <span>Página {loading?.page ?? preview.page}</span>
          <Button variant="secondary" size="sm" disabled={busy || !preview.hasMore} onClick={() => search(preview.page + 1)}>Próxima</Button>
        </nav>
      </div>
    </>}

    {choices.length > 0 && !drawerOpen && <div className="github-tray" role="region" aria-label="Projetos selecionados">
      <div className="github-tray-icon" aria-hidden="true"><FolderGit2 size={20} /><span>{choices.length}</span></div>
      <div className="github-tray-text">
        <strong>{choices.length} {choices.length === 1 ? "projeto selecionado" : "projetos selecionados"}</strong>
        <span>{pending ? `${pending} aguardando a descrição do que você fez` : "Tudo pronto para salvar"}</span>
      </div>
      <Button onClick={() => setDrawerOpen(true)}>Revisar e salvar</Button>
    </div>}

    <Modal open={drawerOpen} onClose={() => { if (!saving) setDrawerOpen(false); }} variant="drawer" icon={<FolderGit2 size={20} />}
      title="Projetos selecionados" subtitle="Descreva sua contribuição em cada projeto antes de salvar."
      footer={<>
        <span className="github-drawer-status">{pending ? `${choices.length - pending} de ${choices.length} prontos` : "Tudo pronto"}</span>
        <Button variant="secondary" disabled={saving} onClick={() => setDrawerOpen(false)}>Continuar selecionando</Button>
        <Button loading={saving} onClick={save}>Salvar {choices.length} projeto(s)</Button>
      </>}>
      <div className="github-drawer">
        <p className="github-required-note"><CircleAlert size={17} aria-hidden="true" />
          <span>Descrever o que você fez é <strong>obrigatório</strong>. Inclua só atividades que você realizou de verdade:
            é isso que a IA usa para escrever o currículo.</span></p>
        {choices.map(item => {
          const titleError = attempted && !item.title.trim() ? "Informe o nome do projeto." : null;
          const descriptionError = attempted && !item.description.trim()
            ? "Campo obrigatório: conte o que você fez neste projeto para poder salvá-lo." : null;
          return <fieldset key={item.repository} className="github-selection" disabled={saving}>
            <legend className="github-selection-head">
              <span className="github-selection-icon" aria-hidden="true"><FolderGit2 size={18} /></span>
              <span className="github-selection-name">{item.repository}</span>
              {item.language && <Language name={item.language} />}
            </legend>
            <Field label="Nome no currículo" htmlFor={"title-" + item.repository} error={titleError}>
              <input id={"title-" + item.repository} className="input" value={item.title} maxLength={150} aria-invalid={!!titleError}
                onChange={e => update(item.repository, { title: e.target.value })} />
            </Field>
            <Field label={<>O que você fez neste projeto? <span className="github-required" aria-hidden="true">*</span></>}
              htmlFor={"description-" + item.repository} error={descriptionError}
              hint="Obrigatório. Ex.: o que você desenvolveu, as tecnologias que usou e o resultado alcançado.">
              <textarea id={"description-" + item.repository} className="textarea" rows={5} value={item.description} maxLength={10000}
                required aria-required="true" aria-invalid={!!descriptionError}
                placeholder="Ex.: Desenvolvi a API REST em Spring Boot com autenticação JWT e escrevi os testes com JUnit e Mockito."
                onChange={e => update(item.repository, { description: e.target.value })} />
            </Field>
            {item.source && <div className="github-source">
              <span><strong>Descrição no GitHub:</strong> {item.source}</span>
              {!item.description.trim() && <Button variant="ghost" size="sm"
                onClick={() => update(item.repository, { description: item.source })}>Usar como ponto de partida</Button>}
            </div>}
            <Button variant="ghost" size="sm" className="github-selection-remove" icon={<Trash2 size={15} />}
              onClick={() => remove(item.repository)}>Remover seleção</Button>
          </fieldset>;
        })}
      </div>
    </Modal>
  </div>;
}

/** Marca do GitHub (desenho do Simple Icons, CC0). */
function GitHubMark({ size = 24 }: { size?: number }) {
  return <svg width={size} height={size} viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
    <path d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12" />
  </svg>;
}

function GitHubLoader({ username, page, compact }: { username: string; page: number; compact?: boolean }) {
  const steps = page === 1
    ? ["Conectando ao GitHub", "Lendo o perfil público", "Organizando os repositórios"]
    : [`Buscando a página ${page}`, "Lendo os repositórios", "Organizando os cards"];
  const [step, setStep] = useState(0);
  useEffect(() => {
    const timer = setInterval(() => setStep(current => Math.min(current + 1, 2)), 600);
    return () => clearInterval(timer);
  }, []);

  return <section className={"card github-loader" + (compact ? " is-compact" : "")} role="status" aria-live="polite">
    <div className="github-loader-visual" aria-hidden="true">
      <span className="github-loader-ring" /><span className="github-loader-ring" /><span className="github-loader-ring" />
      <span className="github-loader-orbit">
        <span><GitBranch size={14} /></span><span><GitCommitHorizontal size={14} /></span><span><Star size={14} /></span>
      </span>
      <span className="github-loader-mark"><GitHubMark size={compact ? 30 : 40} /></span>
    </div>
    <div className="github-loader-text">
      <span className="github-kicker">Buscando @{username || "perfil"}</span>
      <strong key={step}>{steps[step]}…</strong>
      <ol className="github-loader-steps">
        {steps.map((label, index) => <li key={label} className={index < step ? "is-done" : index === step ? "is-active" : ""}>
          <span>{index < step ? <Check size={12} /> : index + 1}</span>{label}
        </li>)}
      </ol>
    </div>
  </section>;
}

function ProfileCard({ preview }: { preview: Preview }) {
  const [avatarFailed, setAvatarFailed] = useState(false);
  const display = preview.name || preview.username;
  return <section className="card github-profile">
    <div className="github-profile-banner" aria-hidden="true"><GitHubMark size={140} /></div>
    <div className="github-profile-body">
      <div className="github-avatar">
        {avatarFailed ? <span>{display.slice(0, 2).toUpperCase()}</span>
          : <img src={`https://github.com/${preview.username}.png?size=160`} alt="" onError={() => setAvatarFailed(true)} />}
      </div>
      <div className="github-profile-info">
        <h2>{display}</h2>
        <span className="github-profile-handle">@{preview.username}</span>
        {preview.bio && <p>{preview.bio}</p>}
        {preview.location && <span className="github-profile-location"><MapPin size={14} aria-hidden="true" /> {preview.location}</span>}
      </div>
      <a className="btn btn-secondary btn-sm github-profile-link" href={preview.profileUrl} target="_blank" rel="noreferrer">
        <GitHubMark size={16} /> Abrir no GitHub
      </a>
    </div>
  </section>;
}

function Language({ name }: { name: string }) {
  return <span className="github-lang">
    <i style={{ background: LANGUAGE_COLORS[name] ?? "var(--subtle)" }} aria-hidden="true" />{name}
  </span>;
}

function RepositoryCard({ repository, index, selected, imported, full, disabled, onSelect }: {
  repository: Repository; index: number; selected: boolean; imported: boolean; full: boolean; disabled: boolean; onSelect: () => void;
}) {
  const locked = disabled || imported || full;
  const className = ["card github-repo", selected && "is-selected", imported && "is-imported", !locked && "is-toggleable"]
    .filter(Boolean).join(" ");
  // O card inteiro alterna a seleção com o mouse; teclado e leitor de tela usam o botão "Selecionar".
  return <article className={className} style={{ "--i": index } as CSSProperties}
    onClick={e => { if (!locked && !(e.target as HTMLElement).closest("a, button")) onSelect(); }}>
    <header className="github-repo-head">
      <span className="github-repo-icon" aria-hidden="true">{repository.fork ? <GitFork size={19} /> : <FolderGit2 size={19} />}</span>
      <div className="github-repo-titles">
        <h3>{repository.name}</h3>
        <div className="github-repo-tags">
          {repository.fork && <span>Fork</span>}
          {repository.archived && <span>Arquivado</span>}
          {imported && <span className="is-success">Já no currículo</span>}
        </div>
      </div>
      {!imported && <span className="github-repo-check" aria-hidden="true">{selected && <Check size={15} />}</span>}
    </header>
    <p className={"github-repo-desc" + (repository.description ? "" : " is-empty")}>
      {repository.description || "Sem descrição pública no GitHub."}
    </p>
    <footer className="github-repo-foot">
      {repository.language && <Language name={repository.language} />}
      <a className="github-repo-link" href={repository.url} target="_blank" rel="noreferrer"
        aria-label={`Ver ${repository.name} no GitHub em nova aba`}>Ver no GitHub <ArrowUpRight size={14} /></a>
      <button type="button" className="github-repo-select" aria-pressed={selected} onClick={onSelect}
        disabled={locked} title={full ? `Máximo de ${MAX_SELECTION} projetos por importação` : selected ? "Clique para remover da seleção" : undefined}>
        {imported ? <><Check size={15} /> Adicionado</>
          : selected ? <><span className="github-select-on"><Check size={15} /> Selecionado</span>
            <span className="github-select-off" aria-hidden="true"><X size={15} /> Remover</span></>
          : <><Plus size={15} /> Selecionar</>}
      </button>
    </footer>
  </article>;
}
