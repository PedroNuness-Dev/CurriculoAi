import { useMemo, useState, type FormEvent } from "react";
import { Check, Pencil, Plus, RefreshCw, Search, X } from "lucide-react";
import { useCatalog } from "../auth/CatalogContext";
import { catalogApi, type CatalogItem, type CatalogKey } from "../api/endpoints";
import { useToast } from "../components/ui/Toast";
import { Button, PageHeader, Skeleton } from "../components/ui/ui";
import { LANGUAGE_NAMES, languageName } from "../lib/labels";

const TABS: { key: CatalogKey; label: string; endpoint: string; singular: string }[] = [
  { key: "areas", label: "Áreas", endpoint: "/api/areas", singular: "área" },
  { key: "courses", label: "Cursos", endpoint: "/api/courses", singular: "curso" },
  { key: "enterprises", label: "Empresas", endpoint: "/api/enterprises", singular: "empresa" },
  { key: "institutions", label: "Instituições", endpoint: "/api/institutions", singular: "instituição" },
  { key: "abilities", label: "Habilidades", endpoint: "/api/abilities", singular: "habilidade" },
  { key: "socialNetworks", label: "Redes sociais", endpoint: "/api/social-networks", singular: "rede" },
  { key: "languages", label: "Idiomas", endpoint: "/api/languages", singular: "idioma" },
];

export default function Catalogs() {
  const [tab, setTab] = useState<CatalogKey>("areas");
  const meta = TABS.find((t) => t.key === tab)!;

  return (
    <>
      <PageHeader
        eyebrow="Administração"
        title="Catálogos"
        text="Dados de apoio usados nos formulários. Liste, cadastre e edite os itens de cada catálogo."
      />
      <div className="tabs" role="tablist" aria-label="Catálogos">
        {TABS.map((t) => (
          <button key={t.key} role="tab" className="tab" aria-selected={tab === t.key} onClick={() => setTab(t.key)}>
            {t.label}
          </button>
        ))}
      </div>
      <div style={{ marginTop: 20 }}>
        <CatalogPanel key={tab} catalog={tab} label={meta.label} singular={meta.singular} endpoint={meta.endpoint} />
      </div>
    </>
  );
}

function CatalogPanel({ catalog, label, singular, endpoint }: { catalog: CatalogKey; label: string; singular: string; endpoint: string }) {
  const { items, loading, refresh, upsert } = useCatalog(catalog);
  const toast = useToast();
  const isLang = catalog === "languages";
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
    <div className="stack-lg">
      <section className="card card-pad">
        <div className="card-head">
          <div>
            <div className="card-title">Cadastrar {singular}</div>
            <p className="card-sub">
              <code>POST {catalog === "areas" || catalog === "languages" ? `${endpoint}/create` : endpoint}</code>
            </p>
          </div>
        </div>
        <form className="row" style={{ gap: 8 }} onSubmit={create}>
          {isLang ? (
            <select className="select grow" value={newName} onChange={(e) => setNewName(e.target.value)}>
              <option value="">{langOptions.length ? "Escolha um idioma para adicionar…" : "Todos os idiomas já estão no catálogo"}</option>
              {langOptions.map(([k, l]) => (
                <option key={k} value={k}>
                  {l}
                </option>
              ))}
            </select>
          ) : (
            <input className="input grow" placeholder={`Nome da ${singular}`} value={newName} onChange={(e) => setNewName(e.target.value)} />
          )}
          <Button type="submit" icon={<Plus size={18} />} loading={creating} disabled={!newName.trim()}>
            Cadastrar
          </Button>
        </form>
      </section>

      <section className="card card-pad">
        <div className="card-head" style={{ alignItems: "center" }}>
          <div>
            <div className="card-title">
              {label} <span className="badge">{items.length}</span>
            </div>
            <p className="card-sub">
              <code>GET {endpoint}</code> · clique no lápis para editar (<code>GET</code> + <code>PUT {endpoint}/{"{id}"}</code>)
            </p>
          </div>
          <Button variant="secondary" size="sm" icon={<RefreshCw size={15} />} loading={refreshing} onClick={doRefresh}>
            Atualizar
          </Button>
        </div>
        <div className="input-wrap" style={{ marginBottom: 16 }}>
          <Search size={18} />
          <input className="input" placeholder={`Filtrar ${label.toLowerCase()}…`} value={query} onChange={(e) => setQuery(e.target.value)} />
        </div>
        {loading ? (
          <div className="cat-grid">
            {Array.from({ length: 9 }).map((_, i) => (
              <Skeleton key={i} h={52} r={14} />
            ))}
          </div>
        ) : (
          <div className="cat-grid">
            {filtered.map((i) => (
              <CatalogRow key={i.id} catalog={catalog} item={i} display={display} onSaved={upsert} />
            ))}
            {filtered.length === 0 && <p className="muted">Nenhum item encontrado.</p>}
          </div>
        )}
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
      <form className="cat-item" style={{ borderColor: "var(--brand-300)", padding: 6, gap: 6 }} onSubmit={save}>
        {isLang ? (
          <select className="select" style={{ minHeight: 38, padding: "6px 34px 6px 10px" }} value={name} onChange={(e) => setName(e.target.value)} autoFocus>
            {Object.entries(LANGUAGE_NAMES).map(([k, l]) => (
              <option key={k} value={k}>
                {l}
              </option>
            ))}
          </select>
        ) : (
          <input
            className="input"
            style={{ minHeight: 38, padding: "6px 10px" }}
            value={name}
            autoFocus
            onChange={(e) => setName(e.target.value)}
            onKeyDown={(e) => e.key === "Escape" && setEditing(false)}
          />
        )}
        <Button type="submit" size="sm" iconOnly loading={saving} aria-label="Salvar">
          {!saving && <Check size={16} />}
        </Button>
        <Button variant="ghost" size="sm" iconOnly onClick={() => setEditing(false)} aria-label="Cancelar">
          <X size={16} />
        </Button>
      </form>
    );
  }

  return (
    <div className="cat-item">
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
