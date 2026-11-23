import { useMemo, useState, type FormEvent } from "react";
import { AlertTriangle, Check, Layers, Pencil, Plus, RefreshCw, Search, X } from "lucide-react";
import { Navigate } from "react-router-dom";
import { useCatalog } from "../auth/CatalogContext";
import { useIsAdmin, useProfile } from "../auth/ProfileContext";
import { catalogApi, type CatalogItem, type CatalogKey } from "../api/endpoints";
import { useToast } from "../components/ui/Toast";
import { Button, PageHeader, Skeleton } from "../components/ui/ui";
import { LANGUAGE_NAMES, languageName } from "../lib/labels";
import "../styles/catalogs.css";

const TABS: { key: CatalogKey; label: string; singular: string }[] = [
  { key: "areas", label: "Áreas", singular: "área" },
  { key: "courses", label: "Cursos", singular: "curso" },
  { key: "enterprises", label: "Empresas", singular: "empresa" },
  { key: "institutions", label: "Instituições", singular: "instituição" },
  { key: "abilities", label: "Habilidades", singular: "habilidade" },
  { key: "socialNetworks", label: "Redes sociais", singular: "rede" },
  { key: "languages", label: "Idiomas", singular: "idioma" },
];

const USAGE: Record<CatalogKey, string> = {
  areas: "Perfil e experiências",
  courses: "Formação acadêmica",
  enterprises: "Experiências profissionais",
  institutions: "Certificações",
  abilities: "Habilidades e projetos",
  socialNetworks: "Redes e links",
  languages: "Idiomas",
};

/** Tela exclusiva de ADMIN: os demais voltam para a visão geral (o backend também bloqueia com 403). */
export default function Catalogs() {
  const { loading } = useProfile();
  const isAdmin = useIsAdmin();
  if (loading) return null;
  if (!isAdmin) return <Navigate to="/app" replace />;
  return <CatalogsAdmin />;
}

function CatalogsAdmin() {
  const [tab, setTab] = useState<CatalogKey>("areas");
  const meta = TABS.find((t) => t.key === tab)!;

  return (
    <div className="catalogs-page">
      <PageHeader
        eyebrow="Administração"
        title="Catálogos"
        text="Dados de apoio usados nos formulários. Liste, cadastre e edite os itens de cada catálogo."
      />
      <div className="tabs catalog-tabs" role="tablist" aria-label="Catálogos">
        {TABS.map((t) => (
          <button key={t.key} id={`catalog-tab-${t.key}`} role="tab" className="tab" aria-selected={tab === t.key} aria-controls={`catalog-panel-${t.key}`} tabIndex={tab === t.key ? 0 : -1} onClick={() => setTab(t.key)} onKeyDown={(event) => {
            const index = TABS.findIndex((item) => item.key === t.key);
            const next = event.key === "ArrowRight" ? (index + 1) % TABS.length : event.key === "ArrowLeft" ? (index - 1 + TABS.length) % TABS.length : event.key === "Home" ? 0 : event.key === "End" ? TABS.length - 1 : -1;
            if (next < 0) return;
            event.preventDefault();
            setTab(TABS[next].key);
            document.getElementById(`catalog-tab-${TABS[next].key}`)?.focus();
          }}>
            {t.label}
          </button>
        ))}
      </div>
      <div className="catalog-tabpanel" id={`catalog-panel-${tab}`} role="tabpanel" aria-labelledby={`catalog-tab-${tab}`} tabIndex={0}>
        <CatalogPanel key={tab} catalog={tab} label={meta.label} singular={meta.singular} />
      </div>
    </div>
  );
}

function CatalogPanel({ catalog, label, singular }: { catalog: CatalogKey; label: string; singular: string }) {
  const { items, loading, error, refresh, upsert } = useCatalog(catalog);
  const toast = useToast();
  const isLang = catalog === "languages";
  const inputLabel = catalog === "courses" ? "Nome do curso" : `Nome da ${singular}`;
  const display = (n: string) => (isLang ? languageName(n) : n);

  const [query, setQuery] = useState("");
  const [newName, setNewName] = useState("");
  const [creating, setCreating] = useState(false);
  const [refreshing, setRefreshing] = useState(false);

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    return [...items]
      .filter((i) => !q || display(i.name).toLowerCase().includes(q))
      .sort((a, b) => a.id - b.id);
  }, [items, query]);

  const langOptions = Object.entries(LANGUAGE_NAMES)
    .filter(([k]) => !items.some((i) => i.name === k))
    .sort((a, b) => a[1].localeCompare(b[1], "pt-BR"));

  async function create(e: FormEvent) {
    e.preventDefault();
    const n = newName.trim();
    if (!n) return;
    setCreating(true);
    try {
      const item = await catalogApi.create(catalog, n);
      upsert(item);
      setNewName("");
      toast.success(`${display(item.name)} cadastrado (#${item.id})`);
    } catch (err) {
      toast.error(err);
    } finally {
      setCreating(false);
    }
  }

  async function doRefresh() {
    setRefreshing(true);
    try {
      await refresh();
    } catch (err) {
      toast.error(err);
    } finally {
      setRefreshing(false);
    }
  }

  return (
    <div className="stack-lg catalog-workspace">
      <section className="card catalog-create" aria-labelledby={`catalog-create-title-${catalog}`}>
        <div className="catalog-create-head">
          <span className="catalog-kicker">Novo registro · {label}</span>
          <h2 id={`catalog-create-title-${catalog}`}>Cadastrar {singular}</h2>
          <p>Adicione uma opção para usar nos formulários do currículo.</p>
        </div>
        <form className="catalog-create-form" onSubmit={create}>
          <label className="field-label" htmlFor={`catalog-new-${catalog}`}>{isLang ? "Idioma para cadastrar" : inputLabel}</label>
          {isLang ? (
            <select id={`catalog-new-${catalog}`} className="select" aria-label="Idioma para cadastrar" value={newName} onChange={(e) => setNewName(e.target.value)}>
              <option value="">{langOptions.length ? "Escolha um idioma para adicionar…" : "Todos os idiomas já estão no catálogo"}</option>
              {langOptions.map(([k, l]) => (
                <option key={k} value={k}>
                  {l}
                </option>
              ))}
            </select>
          ) : (
            <input id={`catalog-new-${catalog}`} className="input" aria-label={inputLabel} placeholder={inputLabel} value={newName} onChange={(e) => setNewName(e.target.value)} />
          )}
          <Button type="submit" icon={<Plus size={18} />} loading={creating} disabled={!newName.trim()}>
            Cadastrar
          </Button>
        </form>
        <div className="catalog-usage"><Layers size={18} aria-hidden="true" /><div><span className="catalog-kicker">Utilizado em</span><strong>{USAGE[catalog]}</strong><p>Os registros deste catálogo são compartilhados com esses formulários.</p></div></div>
      </section>

      <section className="card catalog-data" aria-labelledby={`catalog-data-title-${catalog}`}>
        <div className="catalog-data-head">
          <div>
            <span className="catalog-kicker">Catálogo de apoio</span>
            <h2 id={`catalog-data-title-${catalog}`}>
              {label} <span className="badge" aria-label={loading || (error && !items.length) ? "Quantidade indisponível" : `${items.length} itens`}>{loading || (error && !items.length) ? "—" : items.length}</span>
            </h2>
            <p>
              Pesquise os itens cadastrados ou use o lápis para editar.
            </p>
          </div>
          <Button variant="secondary" size="sm" icon={<RefreshCw size={15} />} loading={refreshing} onClick={doRefresh}>
            Atualizar
          </Button>
        </div>
        <div className="catalog-data-body">
        <label className="field-label" htmlFor={`catalog-search-${catalog}`}>Buscar em {label.toLowerCase()}</label>
        <div className="input-wrap catalog-search">
          <Search size={18} />
          <input id={`catalog-search-${catalog}`} className="input" aria-label={`Filtrar ${label.toLowerCase()}`} placeholder={`Filtrar ${label.toLowerCase()}…`} value={query} onChange={(e) => setQuery(e.target.value)} />
        </div>
        {error && <div className="alert error catalog-error" role="alert"><AlertTriangle size={18} /><span>Não foi possível atualizar este catálogo. Use “Atualizar” para tentar novamente.</span></div>}
        {query.trim() && !loading && (!error || items.length > 0) && <p className="catalog-result-count" role="status">{filtered.length} de {items.length} registros</p>}
        {loading ? (
          <div className="catalog-list-loading" role="status" aria-label={`Carregando ${label.toLowerCase()}`}>
            {Array.from({ length: 9 }).map((_, i) => (
              <Skeleton key={i} h={52} r={14} />
            ))}
          </div>
        ) : (
          <>
            {filtered.length > 0 && <><div className="catalog-columns" aria-hidden="true"><span>ID</span><span>Registro</span><span>Ação</span></div><ul className="catalog-record-list">{filtered.map((i) => <li key={i.id}><CatalogRow catalog={catalog} item={i} display={display} onSaved={upsert} /></li>)}</ul></>}
            {filtered.length === 0 && !error && <div className="catalog-empty"><Search size={24} aria-hidden="true" /><h3>{query.trim() ? "Nenhum registro encontrado." : "Este catálogo ainda está vazio."}</h3><p>{query.trim() ? `Não há resultados para “${query}”. Tente outro nome.` : `Use o formulário de cadastro para adicionar ${singular}.`}</p></div>}
          </>
        )}
        </div>
      </section>
    </div>
  );
}

function CatalogRow({
  catalog,
  item,
  display,
  onSaved,
}: {
  catalog: CatalogKey;
  item: CatalogItem;
  display: (n: string) => string;
  onSaved: (i: CatalogItem) => void;
}) {
  const toast = useToast();
  const [editing, setEditing] = useState(false);
  const [loadingOne, setLoadingOne] = useState(false);
  const [name, setName] = useState(item.name);
  const [saving, setSaving] = useState(false);
  const isLang = catalog === "languages";

  // GET /{id} antes de editar, para garantir o valor mais recente.
  async function startEdit() {
    setLoadingOne(true);
    try {
      const fresh = await catalogApi.get(catalog, item.id);
      setName(fresh.name);
      setEditing(true);
    } catch (err) {
      toast.error(err);
    } finally {
      setLoadingOne(false);
    }
  }

  async function save(e?: FormEvent) {
    e?.preventDefault();
    const n = name.trim();
    if (!n || n === item.name) {
      setEditing(false);
      return;
    }
    setSaving(true);
    try {
      const updated = await catalogApi.update(catalog, item.id, n);
      onSaved(updated);
      toast.success(`Atualizado para "${display(updated.name)}"`);
      setEditing(false);
    } catch (err) {
      toast.error(err);
    } finally {
      setSaving(false);
    }
  }

  if (editing) {
    return (
      <form className="cat-item catalog-edit" onSubmit={save}>
        <div className="catalog-edit-context"><span className="id">#{item.id}</span><label htmlFor={`catalog-edit-${catalog}-${item.id}`}>Editando {display(item.name)}</label></div>
        <div className="catalog-edit-controls">
        {isLang ? (
          <select id={`catalog-edit-${catalog}-${item.id}`} className="select" aria-label="Editar idioma" value={name} onChange={(e) => setName(e.target.value)} autoFocus>
            {Object.entries(LANGUAGE_NAMES).map(([k, l]) => (
              <option key={k} value={k}>
                {l}
              </option>
            ))}
          </select>
        ) : (
          <input
            className="input"
            id={`catalog-edit-${catalog}-${item.id}`}
            aria-label={`Editar ${display(item.name)}`}
            value={name}
            autoFocus
            onChange={(e) => setName(e.target.value)}
            onKeyDown={(e) => e.key === "Escape" && setEditing(false)}
          />
        )}
        <div className="catalog-edit-actions">
        <Button type="submit" size="sm" iconOnly loading={saving} aria-label="Salvar">
          {!saving && <Check size={16} />}
        </Button>
        <Button variant="ghost" size="sm" iconOnly onClick={() => setEditing(false)} aria-label="Cancelar">
          <X size={16} />
        </Button>
        </div>
        </div>
      </form>
    );
  }

  return (
    <div className="cat-item catalog-row">
      <span className="id">#{item.id}</span>
      <span className="nm" title={display(item.name)}>
        {display(item.name)}
      </span>
      <Button variant="ghost" size="sm" iconOnly onClick={startEdit} loading={loadingOne} aria-label={`Editar ${display(item.name)}`}>
        {!loadingOne && <Pencil size={15} />}
      </Button>
    </div>
  );
}
