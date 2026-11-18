import { useState, type FormEvent } from "react";
import { Languages as LanguagesIcon, Plus } from "lucide-react";
import { useProfile } from "../auth/ProfileContext";
import { useCatalog } from "../auth/CatalogContext";
import { userApi } from "../api/endpoints";
import { useToast } from "../components/ui/Toast";
import { Button, CatalogFeedback, Field, ListSkeleton, Modal, PageHeader } from "../components/ui/ui";
import { LANGUAGE_LEVELS, languageName, levelLabel, levelRank } from "../lib/labels";
import "../styles/languages.css";

export default function Languages() {
  const { profile, loading, error, reload } = useProfile();
  const catalog = useCatalog("languages");
  const toast = useToast();
  const [open, setOpen] = useState(false);
  const [languageId, setLanguageId] = useState<string>("");
  const [level, setLevel] = useState("");
  const [saving, setSaving] = useState(false);
  const [touched, setTouched] = useState(false);

  const mine = profile?.languages ?? [];
  const usedIds = mine.map((l) => l.language.languageId);
  const available = catalog.items
    .filter((l) => !usedIds.includes(l.id))
    .sort((a, b) => languageName(a.name).localeCompare(languageName(b.name), "pt-BR"));
  const valid = !!level && !!languageId;

  function openModal() {
    setLanguageId("");
    setLevel("");
    setTouched(false);
    setOpen(true);
  }

  async function save(e: FormEvent) {
    e.preventDefault();
    setTouched(true);
    if (!valid) return;
    setSaving(true);
    try {
      // POST /api/users/languages
      await userApi.addLanguages([{ languageId: Number(languageId), languageLevel: level }]);
      toast.success("Idioma adicionado");
      setOpen(false);
      await reload();
    } catch (err) {
      toast.error(err);
    } finally {
      setSaving(false);
    }
  }

  const sorted = mine.slice().sort((a, b) => levelRank(b.level) - levelRank(a.level));

  return (
    <div className="languages-page">
      <PageHeader
        eyebrow="Competências"
        title="Idiomas"
        text="Mostre como você se comunica. Reúna os idiomas que conhece e informe seu nível em cada um."
        actions={
          <Button icon={<Plus size={18} />} onClick={openModal}>
            Adicionar idioma
          </Button>
        }
      />

      {error && <CatalogFeedback onRetry={reload}>{error}</CatalogFeedback>}
      {loading ? (
        <div role="status" aria-label="Carregando idiomas"><ListSkeleton rows={2} /></div>
      ) : error && !profile ? null : sorted.length === 0 ? (
        <section className="empty language-empty" aria-labelledby="language-empty-title">
          <div className="language-empty-main">
            <div className="language-emblem" aria-hidden="true"><LanguagesIcon size={34} /><span><Plus size={15} /></span></div>
            <span className="language-kicker">Sua forma de se conectar</span>
            <h2 id="language-empty-title">Comunicação que<br />abre novos caminhos.</h2>
            <p>Você ainda não cadastrou idiomas. Inclua os que conhece para apresentar essa competência no seu currículo.</p>
            <Button icon={<Plus size={18} />} onClick={openModal}>Adicionar o primeiro idioma</Button>
          </div>
          <aside className="language-guide" aria-label="Como registrar seus idiomas">
            <span className="language-kicker">Do conhecimento para o currículo</span>
            <ol>
              <li><span>01</span><div><h3>Escolha o idioma</h3><p>Selecione entre as opções disponíveis no catálogo.</p></div></li>
              <li><span>02</span><div><h3>Informe seu nível</h3><p>Escolha a classificação que representa seu conhecimento.</p></div></li>
              <li><span>03</span><div><h3>Adicione ao seu perfil</h3><p>Salve o idioma para incluí-lo entre suas competências.</p></div></li>
            </ol>
            <p className="language-guide-note">O nível de proficiência é uma informação declarada por você.</p>
          </aside>
        </section>
      ) : (
        <section className="language-history" aria-labelledby="language-history-title">
          <header className="language-list-header"><div><span className="language-kicker">Suas competências linguísticas</span><h2 id="language-history-title">Conexões em diferentes idiomas.</h2></div><span className="language-count">{sorted.length} {sorted.length === 1 ? "idioma cadastrado" : "idiomas cadastrados"}</span></header>
          <div className="cat-grid language-grid">
          {sorted.map((l) => (
              <article key={l.userLanguageId} className="item language-record">
                <div className="language-record-heading"><span className="icon-tile"><LanguagesIcon size={24} /></span><div><span className="language-kicker">Idioma</span><h3 className="item-title">{languageName(l.language.name)}</h3></div></div>
                <div className="language-proficiency"><span className="language-kicker">Proficiência declarada</span><p>{levelLabel(l.level)}</p></div>
              </article>
          ))}
          </div>
          <p className="language-list-note">Níveis de conhecimento informados por você.</p>
        </section>
      )}

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        icon={<LanguagesIcon size={20} />}
        title="Adicionar idioma"
        width={560}
        footer={
          <>
            <Button variant="secondary" onClick={() => setOpen(false)}>
              Cancelar
            </Button>
            <Button type="submit" form="lang-form" loading={saving}>
              Salvar idioma
            </Button>
          </>
        }
      >
        <form id="lang-form" className="language-form" onSubmit={save} noValidate>
          <fieldset className="language-form-section"><legend><span>01</span> Idioma</legend><p>Escolha o idioma que deseja incluir no seu perfil.</p>
          <Field label="Idioma" htmlFor="lg-id" error={touched && !languageId ? "Escolha o idioma" : null}>
            <select
              id="lg-id"
              className="select"
              value={languageId}
              disabled={catalog.loading || (!!catalog.error && catalog.items.length === 0)}
              onChange={(e) => setLanguageId(e.target.value)}
            >
              <option value="">{catalog.error && catalog.items.length === 0 ? "Idiomas indisponíveis no momento" : catalog.loading ? "Carregando…" : available.length ? "Selecione…" : "Você já adicionou todos os idiomas"}</option>
              {available.map((l) => (
                <option key={l.id} value={l.id}>
                  {languageName(l.name)}
                </option>
              ))}
            </select>
            {catalog.error && <CatalogFeedback onRetry={catalog.refresh}>Não foi possível carregar os idiomas.</CatalogFeedback>}
          </Field>
          </fieldset>
          <fieldset className="language-form-section"><legend><span>02</span> Proficiência declarada</legend><p>Selecione o nível que representa seu conhecimento.</p>
          <Field label="Nível" error={touched && !level ? "Escolha o nível" : null}>
            <div className="option-cards language-level-options" role="radiogroup" aria-label="Nível">
              {LANGUAGE_LEVELS.map((l) => (
                <label key={l.value} className="option-card">
                  <input type="radio" name="level" checked={level === l.value} onChange={() => setLevel(l.value)} />
                  <b>{l.label}</b>
                </label>
              ))}
            </div>
          </Field>
          <p className="language-level-note" role="status">{level ? `Nível selecionado: ${levelLabel(level)}.` : "Nenhum nível selecionado."}</p>
          </fieldset>
        </form>
      </Modal>
    </div>
  );
}
