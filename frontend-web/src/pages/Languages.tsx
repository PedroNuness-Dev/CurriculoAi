import { useState, type FormEvent } from "react";
import { Languages as LanguagesIcon, Plus } from "lucide-react";
import { useProfile } from "../auth/ProfileContext";
import { useCatalog } from "../auth/CatalogContext";
import { userApi } from "../api/endpoints";
import { useToast } from "../components/ui/Toast";
import { Button, EmptyState, Field, ListSkeleton, Modal, PageHeader } from "../components/ui/ui";
import { LANGUAGE_LEVELS, languageName, levelLabel, levelRank } from "../lib/labels";

export default function Languages() {
  const { profile, loading, reload } = useProfile();
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
    <>
      <PageHeader
        eyebrow="Competências"
        title="Idiomas"
        text="Informe os idiomas que você fala e seu nível em cada um."
        actions={
          <Button icon={<Plus size={18} />} onClick={openModal}>
            Adicionar idioma
          </Button>
        }
      />

      {loading ? (
        <ListSkeleton rows={2} />
      ) : sorted.length === 0 ? (
        <EmptyState
          icon={<LanguagesIcon size={26} />}
          title="Nenhum idioma cadastrado"
          text="Comece pelo português (nativo) e adicione os outros que você conhece."
          action={
            <Button icon={<Plus size={18} />} onClick={openModal}>
              Adicionar idioma
            </Button>
          }
        />
      ) : (
        <div className="cat-grid" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(260px, 1fr))", gap: 14 }}>
          {sorted.map((l) => {
            const r = levelRank(l.level);
            return (
              <article key={l.userLanguageId} className="item" style={{ flexDirection: "column", gap: 14 }}>
                <div className="row between">
                  <div className="item-title" style={{ fontSize: 18 }}>
                    {languageName(l.language.name)}
                  </div>
                  <span className="badge">{levelLabel(l.level)}</span>
                </div>
                <div className="row" style={{ gap: 6 }} aria-label={`Nível ${r} de 5`}>
                  {[1, 2, 3, 4, 5].map((i) => (
                    <span
                      key={i}
                      style={{
                        flex: 1,
                        height: 8,
                        borderRadius: 99,
                        background: i <= r ? "var(--grad)" : "var(--brand-50)",
                      }}
                    />
                  ))}
                </div>
              </article>
            );
          })}
        </div>
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
        <form id="lang-form" className="stack" onSubmit={save} noValidate>
          <Field label="Idioma" htmlFor="lg-id" error={touched && !languageId ? "Escolha o idioma" : null}>
            <select
              id="lg-id"
              className="select"
              value={languageId}
              disabled={catalog.loading}
              onChange={(e) => setLanguageId(e.target.value)}
            >
              <option value="">{catalog.loading ? "Carregando…" : available.length ? "Selecione…" : "Você já adicionou todos os idiomas"}</option>
              {available.map((l) => (
                <option key={l.id} value={l.id}>
                  {languageName(l.name)}
                </option>
              ))}
            </select>
          </Field>
          <Field label="Nível" error={touched && !level ? "Escolha o nível" : null}>
            <div className="option-cards" role="radiogroup" aria-label="Nível" style={{ gridTemplateColumns: "repeat(auto-fit, minmax(104px, 1fr))" }}>
              {LANGUAGE_LEVELS.map((l) => (
                <label key={l.value} className="option-card" style={{ alignItems: "center", textAlign: "center", padding: "14px 8px" }}>
                  <input type="radio" name="level" checked={level === l.value} onChange={() => setLevel(l.value)} />
                  <b>{l.label}</b>
                </label>
              ))}
            </div>
          </Field>
        </form>
      </Modal>
    </>
  );
}
