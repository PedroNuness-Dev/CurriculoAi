import { useState, type FormEvent } from "react";
import { ExternalLink, Globe, Link2, Plus } from "lucide-react";
import { useProfile } from "../auth/ProfileContext";
import { userApi } from "../api/endpoints";
import { useToast } from "../components/ui/Toast";
import { Button, CatalogFeedback, CatalogSelect, Field, ListSkeleton, Modal, PageHeader } from "../components/ui/ui";
import { ensureProtocol, prettyUrl } from "../lib/format";
import "../styles/social.css";

export default function SocialLinks() {
  const { profile, loading, error, reload } = useProfile();
  const toast = useToast();
  const [open, setOpen] = useState(false);
  const [networkId, setNetworkId] = useState<number | null>(null);
  const [link, setLink] = useState("");
  const [touched, setTouched] = useState(false);
  const [saving, setSaving] = useState(false);

  const mine = profile?.socialNetworks ?? [];
  const used = mine.map((s) => s.socialNetwork.socialNetworkId);
  const valid = !!networkId && link.trim().length > 3;

  function openModal() {
    setNetworkId(null);
    setLink("");
    setTouched(false);
    setOpen(true);
  }

  async function save(e: FormEvent) {
    e.preventDefault();
    setTouched(true);
    if (!valid) return;
    setSaving(true);
    try {
      await userApi.addSocialNetworks([{ socialNetworkId: networkId!, link: ensureProtocol(link) }]);
      toast.success("Link adicionado");
      setOpen(false);
      await reload();
    } catch (err) {
      toast.error(err);
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="social-page">
      <PageHeader
        eyebrow="Competências"
        title="Redes e links"
        text="Conecte sua trajetória à sua presença digital. Reúna os perfis e canais que complementam seu currículo."
        actions={
          <Button icon={<Plus size={18} />} onClick={openModal}>
            Adicionar link
          </Button>
        }
      />

      {error && <CatalogFeedback onRetry={reload}>{error}</CatalogFeedback>}
      {loading ? (
        <div role="status" aria-label="Carregando redes e links"><ListSkeleton rows={2} /></div>
      ) : error && !profile ? null : mine.length === 0 ? (
        <section className="empty social-empty" aria-labelledby="social-empty-title">
          <div className="social-empty-main">
            <div className="social-emblem" aria-hidden="true"><Link2 size={34} /><span><Plus size={15} /></span></div>
            <span className="social-kicker">Sua presença além do currículo</span>
            <h2 id="social-empty-title">Mais caminhos para<br />conhecer seu trabalho.</h2>
            <p>Você ainda não cadastrou links. Inclua os perfis e canais que ajudam a apresentar sua trajetória profissional.</p>
            <Button icon={<Plus size={18} />} onClick={openModal}>Adicionar o primeiro link</Button>
          </div>
          <aside className="social-guide" aria-label="Como adicionar um perfil">
            <span className="social-kicker">Da sua rede para o currículo</span>
            <ol>
              <li><span>01</span><div><h3>Escolha a plataforma</h3><p>Selecione a rede ou cadastre outra pelo seletor.</p></div></li>
              <li><span>02</span><div><h3>Informe o endereço</h3><p>Inclua o link do perfil que você deseja apresentar.</p></div></li>
              <li><span>03</span><div><h3>Conecte ao seu perfil</h3><p>Salve para reunir sua presença digital no currículo.</p></div></li>
            </ol>
            <p className="social-guide-note">Os links cadastrados podem ser acessados em uma nova aba.</p>
          </aside>
        </section>
      ) : (
        <section className="social-history" aria-labelledby="social-history-title">
          <header className="social-list-header"><div><span className="social-kicker">Sua presença digital</span><h2 id="social-history-title">Conexões que complementam sua história.</h2></div><span className="social-count">{mine.length} {mine.length === 1 ? "link cadastrado" : "links cadastrados"}</span></header>
          <div className="item-list social-list">
          {mine.map((s) => (
              <article key={s.userSocialNetworkId} className="item social-record">
                <div className="social-platform-mark" aria-hidden="true">{s.socialNetwork.name.trim().slice(0, 2) || <Globe size={25} />}</div>
                <div className="item-body social-record-body">
                  <span className="social-kicker">Plataforma</span>
                  <h3 className="item-title">{s.socialNetwork.name}</h3>
                  <div className="social-url"><Link2 size={15} aria-hidden="true" /><span>{s.link.trim() ? prettyUrl(s.link) : "Endereço não informado"}</span></div>
                </div>
                {s.link.trim() && <a href={ensureProtocol(s.link)} target="_blank" rel="noreferrer" className="btn btn-secondary btn-sm" aria-label={`Abrir perfil em ${s.socialNetwork.name} em nova aba`}>
                  <ExternalLink size={15} aria-hidden="true" /> Abrir perfil
                </a>}
              </article>
          ))}
          </div>
        </section>
      )}

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        icon={<Link2 size={20} />}
        title="Adicionar link"
        width={540}
        footer={
          <>
            <Button variant="secondary" onClick={() => setOpen(false)}>
              Cancelar
            </Button>
            <Button type="submit" form="soc-form" loading={saving}>
              Salvar link
            </Button>
          </>
        }
      >
        <form id="soc-form" className="social-form" onSubmit={save} noValidate>
          <fieldset className="social-form-section"><legend><span>01</span> Plataforma</legend><p>Escolha onde está o perfil que deseja apresentar.</p>
          <Field label="Rede" htmlFor="sn-id" error={touched && !networkId ? "Escolha a rede" : null}>
            <CatalogSelect
              id="sn-id"
              catalog="socialNetworks"
              value={networkId}
              onChange={setNetworkId}
              exclude={used}
              newLabel="Cadastrar outra rede…"
              invalid={touched && !networkId}
            />
          </Field>
          </fieldset>
          <fieldset className="social-form-section"><legend><span>02</span> Endereço do perfil</legend><p>Informe o link para acessar sua página nessa plataforma.</p>
          <Field label="Link do perfil" htmlFor="sn-link" error={touched && link.trim().length <= 3 ? "Informe o link" : null}>
            <input
              id="sn-link"
              className="input"
              placeholder="linkedin.com/in/seu-perfil"
              value={link}
              onChange={(e) => setLink(e.target.value)}
              aria-invalid={touched && link.trim().length <= 3}
            />
          </Field>
          </fieldset>
        </form>
      </Modal>
    </div>
  );
}
