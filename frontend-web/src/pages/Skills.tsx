import { useEffect, useMemo, useState } from "react";
import { Check, Plus, Save, Search, Sparkles, X } from "lucide-react";
import { useIsAdmin, useProfile } from "../auth/ProfileContext";
import { useCatalog } from "../auth/CatalogContext";
import { catalogApi, userApi } from "../api/endpoints";
import { useToast } from "../components/ui/Toast";
import { Button, CatalogFeedback, PageHeader, Skeleton } from "../components/ui/ui";
import "../styles/skills.css";

export default function Skills() {
  const { reload } = useProfile();
  const catalog = useCatalog("abilities");
  const toast = useToast();
  // Só ADMIN cadastra habilidade nova no catálogo (o backend devolve 403 para os demais).
  const canCreate = useIsAdmin();
  const searchLabel = canCreate ? "Buscar ou criar habilidade" : "Buscar habilidade";

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
    if (!name || !canCreate) return;
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
    <div className="skills-page">
      <PageHeader
        eyebrow="Competências"
        title="Habilidades"
        text="Escolha as tecnologias e competências que você domina. Elas formam a seção Habilidades do currículo."
        actions={
          <Button icon={<Save size={18} />} onClick={save} loading={saving} disabled={!dirty}>
            {saved === null ? "Carregando…" : dirty ? "Salvar alterações" : "Tudo salvo"}
          </Button>
        }
      />

      <div className="stack-lg skills-workspace">
        <section className="card skills-selection" aria-labelledby="skills-selection-title">
          <div className="skills-selection-head">
            <span className="skills-kicker">Seu currículo</span>
            <div><h2 id="skills-selection-title">Habilidades selecionadas</h2><span className="skills-count" aria-label={saved === null ? "Carregando seleção" : `${selected.length} habilidades selecionadas`}>{saved === null ? "—" : selected.length}</span></div>
            <p>Revise as competências que você quer apresentar.</p>
          </div>
          <div className="skills-selection-body">
          {saved === null ? (
            <div className="row row-wrap" role="status" aria-label="Carregando suas habilidades">
              {[80, 110, 90, 70].map((w, i) => (
                <Skeleton key={i} h={38} w={w} r={99} />
              ))}
            </div>
          ) : selected.length === 0 ? (
            <div className="skills-selection-empty">
              <span className="icon-tile"><Sparkles size={24} aria-hidden="true" /></span>
              <h3>Seu repertório começa aqui.</h3>
              <p>Selecione no catálogo as habilidades que fazem parte do seu perfil profissional.</p>
              <a href="#skills-search" className="btn btn-secondary"><Search size={16} aria-hidden="true" /> Buscar habilidades</a>
            </div>
          ) : (
            <div className="chip-group skills-selected" role="group" aria-label="Habilidades selecionadas">
              {selectedItems.map((a) => (
                <button key={a.id} className="chip" aria-pressed="true" aria-label={`Remover ${a.name} da seleção`} onClick={() => toggle(a.id)}>
                  <span>{a.name}</span> <X size={14} aria-hidden="true" />
                </button>
              ))}
            </div>
          )}
          {selectedItems.length < selected.length && <p className="skills-unavailable-names">Alguns nomes não estão disponíveis no catálogo. Sua seleção foi mantida.</p>}
          {dirty && (
            <div className="skills-unsaved" role="status">
              <span aria-hidden="true" /><p>Você tem alterações não salvas. Use <b>Salvar alterações</b> para atualizar o currículo.</p>
            </div>
          )}
          {saved !== null && !dirty && selected.length > 0 && <p className="skills-saved-note"><Check size={15} aria-hidden="true" /> Sua seleção está salva.</p>}
          </div>
          <div className="skills-selection-foot"><span>01 Escolha</span><span>02 Revise</span><span>03 Salve</span></div>
        </section>

        <section className="card skills-catalog" aria-labelledby="skills-catalog-title">
          <div className="skills-catalog-head">
            <span className="skills-kicker">Suas competências, em destaque</span>
            <h2 id="skills-catalog-title">Encontre o que você sabe fazer.</h2>
            <p>Selecione uma ou várias habilidades.{canCreate && " Não encontrou? Digite o nome e cadastre uma nova."}</p>
          </div>
          <div className="skills-catalog-body">
          <label className="field-label" htmlFor="skills-search">{searchLabel}</label>
          <div className="skills-search-row">
            <div className="input-wrap">
              <Search size={18} />
              <input
                id="skills-search"
                className="input"
                aria-label={searchLabel}
                placeholder={`${searchLabel}…`}
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter" && canCreate && query.trim() && !exact) void createAbility();
                }}
              />
            </div>
            {canCreate && query.trim() && !exact && (
              <Button variant="secondary" icon={<Plus size={17} />} loading={creating} onClick={createAbility}>
                Criar “{query.trim()}”
              </Button>
            )}
          </div>
          <div className="skills-results-head"><h3>Catálogo de habilidades</h3>{!catalog.loading && !catalog.error && <span role="status">{filtered.length} {filtered.length === 1 ? "opção" : "opções"}</span>}</div>
          {catalog.error && catalog.items.length === 0 ? <CatalogFeedback onRetry={catalog.refresh}>Não foi possível carregar as habilidades.</CatalogFeedback> : catalog.loading ? (
            <div className="row row-wrap" role="status" aria-label="Carregando catálogo de habilidades">
              {Array.from({ length: 10 }).map((_, i) => (
                <Skeleton key={i} h={38} w={70 + ((i * 23) % 60)} r={99} />
              ))}
            </div>
          ) : (
            <div className="skills-options" role="group" aria-label="Catálogo de habilidades">
              {filtered.map((a) => {
                const on = selected.includes(a.id);
                return (
                  <button key={a.id} className="chip" aria-pressed={on} onClick={() => toggle(a.id)}>
                    <span className="skills-option-mark" aria-hidden="true">{on ? <Check size={15} /> : <Plus size={15} />}</span><span>{a.name}</span>
                  </button>
                );
              })}
              {filtered.length === 0 && <div className="skills-no-results"><Search size={23} aria-hidden="true" /><h3>{query.trim() ? "Nenhuma habilidade encontrada." : "O catálogo ainda está vazio."}</h3><p>{query.trim() ? `Nada encontrado para “${query}”.${canCreate ? " Você pode cadastrar essa habilidade pelo botão acima." : " Tente outro termo."}` : canCreate ? "Digite o nome de uma habilidade para cadastrá-la." : "Ainda não há habilidades cadastradas."}</p></div>}
            </div>
          )}
          </div>
        </section>
      </div>
    </div>
  );
}
