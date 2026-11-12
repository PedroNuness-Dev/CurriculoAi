import { useEffect, useMemo, useState } from "react";
import { Check, Plus, Save, Search, Sparkles, X } from "lucide-react";
import { useProfile } from "../auth/ProfileContext";
import { useCatalog } from "../auth/CatalogContext";
import { catalogApi, userApi } from "../api/endpoints";
import { useToast } from "../components/ui/Toast";
import { Button, PageHeader, Skeleton } from "../components/ui/ui";

export default function Skills() {
  const { reload } = useProfile();
  const catalog = useCatalog("abilities");
  const toast = useToast();

  const [saved, setSaved] = useState<number[] | null>(null);
  const [selected, setSelected] = useState<number[]>([]);
  const [query, setQuery] = useState("");
  const [saving, setSaving] = useState(false);
  const [creating, setCreating] = useState(false);

  // GET /api/users/abilities
  useEffect(() => {
    let alive = true;
    userApi
      .abilities()
      .then((res) => {
        if (!alive) return;
        const ids = res.abilities.map((a) => a.abilityId);
        setSaved(ids);
        setSelected(ids);
      })
      .catch((err) => {
        toast.error(err);
        setSaved([]);
      });
    return () => {
      alive = false;
    };
  }, [toast]);

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    return [...catalog.items]
      .filter((a) => !q || a.name.toLowerCase().includes(q))
      .sort((a, b) => a.name.localeCompare(b.name, "pt-BR"));
  }, [catalog.items, query]);

  const dirty = saved !== null && (saved.length !== selected.length || saved.some((id) => !selected.includes(id)));
  const exact = catalog.items.some((a) => a.name.toLowerCase() === query.trim().toLowerCase());

  const toggle = (id: number) => setSelected((s) => (s.includes(id) ? s.filter((x) => x !== id) : [...s, id]));

  // POST /api/abilities
  async function createAbility() {
    const name = query.trim();
    if (!name) return;
    setCreating(true);
    try {
      const item = await catalogApi.create("abilities", name);
      catalog.upsert(item);
      setSelected((s) => [...s, item.id]);
      setQuery("");
      toast.success(`Habilidade "${item.name}" criada`);
    } catch (err) {
      toast.error(err);
    } finally {
      setCreating(false);
    }
  }

  // POST /api/users/abilities (substitui o conjunto)
  async function save() {
    if (selected.length === 0) {
      toast.info("Selecione ao menos uma habilidade.");
      return;
    }
    setSaving(true);
    try {
      const res = await userApi.setAbilities(selected);
      const ids = res.abilities.map((a) => a.abilityId);
      setSaved(ids);
      setSelected(ids);
      toast.success(`${ids.length} habilidade(s) salvas`);
      await reload();
    } catch (err) {
      toast.error(err);
    } finally {
      setSaving(false);
    }
  }

  const selectedItems = catalog.items.filter((a) => selected.includes(a.id));

  return (
    <>
      <PageHeader
        eyebrow="Competências"
        title="Habilidades"
        text="Escolha as tecnologias e competências que você domina. Elas formam a seção Habilidades do currículo."
        actions={
          <Button icon={<Save size={18} />} onClick={save} loading={saving} disabled={!dirty}>
            {dirty ? "Salvar alterações" : "Tudo salvo"}
          </Button>
        }
      />

      <div className="stack-lg">
        <section className="card card-pad">
          <div className="card-head">
            <div>
              <div className="card-title">
                <span className="icon-tile grad">
                  <Sparkles size={18} />
                </span>
                Selecionadas
              </div>
              <p className="card-sub">Clique para remover.</p>
            </div>
            <span className="badge">{selected.length}</span>
          </div>
          {saved === null ? (
            <div className="row row-wrap">
              {[80, 110, 90, 70].map((w, i) => (
                <Skeleton key={i} h={38} w={w} r={99} />
              ))}
            </div>
          ) : selectedItems.length === 0 ? (
            <p className="muted">Nenhuma habilidade selecionada ainda — escolha abaixo.</p>
          ) : (
            <div className="chip-group">
              {selectedItems.map((a) => (
                <button key={a.id} className="chip" aria-pressed="true" onClick={() => toggle(a.id)}>
                  {a.name} <X size={14} />
                </button>
              ))}
            </div>
          )}
          {dirty && (
            <div className="alert" style={{ marginTop: 18 }}>
              Você tem alterações não salvas. Clique em <b>&nbsp;Salvar alterações&nbsp;</b> para enviar.
            </div>
          )}
        </section>

        <section className="card card-pad">
          <div className="card-head">
            <div>
              <div className="card-title">Catálogo de habilidades</div>
              <p className="card-sub">Não encontrou? Digite e cadastre uma nova.</p>
            </div>
          </div>
          <div className="row" style={{ gap: 8, marginBottom: 18 }}>
            <div className="input-wrap grow">
              <Search size={18} />
              <input
                className="input"
                placeholder="Buscar ou criar habilidade…"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter" && query.trim() && !exact) void createAbility();
                }}
              />
            </div>
            {query.trim() && !exact && (
              <Button variant="secondary" icon={<Plus size={17} />} loading={creating} onClick={createAbility}>
                Criar “{query.trim()}”
              </Button>
            )}
          </div>
          {catalog.loading ? (
            <div className="row row-wrap">
              {Array.from({ length: 10 }).map((_, i) => (
                <Skeleton key={i} h={38} w={70 + ((i * 23) % 60)} r={99} />
              ))}
            </div>
          ) : (
            <div className="chip-group">
              {filtered.map((a) => {
                const on = selected.includes(a.id);
                return (
                  <button key={a.id} className="chip" aria-pressed={on} onClick={() => toggle(a.id)}>
                    {on ? <Check size={15} /> : <Plus size={15} />} {a.name}
                  </button>
                );
              })}
              {filtered.length === 0 && <p className="muted">Nada encontrado para “{query}”.</p>}
            </div>
          )}
        </section>
      </div>
    </>
  );
}
