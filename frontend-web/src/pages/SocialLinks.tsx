import { useState, type FormEvent } from "react";
import { ExternalLink, Globe, Link2, Plus } from "lucide-react";
import { useProfile } from "../auth/ProfileContext";
import { userApi } from "../api/endpoints";
import { useToast } from "../components/ui/Toast";
import { Button, CatalogSelect, EmptyState, Field, ListSkeleton, Modal, PageHeader } from "../components/ui/ui";
import { ensureProtocol, prettyUrl } from "../lib/format";

// Cor de destaque discreta por rede (apenas o monograma).
const NETWORK_TINT: Record<string, string> = {
  linkedin: "#0a66c2",
  github: "#0a0c12",
  instagram: "#d62976",
  twitter: "#0a0c12",
  behance: "#1769ff",
  dribbble: "#ea4c89",
};

export default function SocialLinks() {
  const { profile, loading, reload } = useProfile();
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
    <>
      <PageHeader
        eyebrow="Competências"
        title="Redes e links"
        text="LinkedIn, GitHub, portfólio… recrutadores sempre procuram por eles."
        actions={
          <Button icon={<Plus size={18} />} onClick={openModal}>
            Adicionar link
          </Button>
        }
      />

      {loading ? (
        <ListSkeleton rows={2} />
      ) : mine.length === 0 ? (
        <EmptyState
          icon={<Link2 size={26} />}
          title="Nenhum link cadastrado"
          text="Adicione pelo menos seu LinkedIn — é o primeiro lugar que o recrutador vai olhar."
          action={
            <Button icon={<Plus size={18} />} onClick={openModal}>
              Adicionar link
            </Button>
          }
        />
      ) : (
        <div className="item-list">
          {mine.map((s) => {
            const tint = NETWORK_TINT[s.socialNetwork.name.toLowerCase()];
            return (
              <article key={s.userSocialNetworkId} className="item" style={{ alignItems: "center" }}>
                <span
                  className="icon-tile"
                  style={tint ? { background: tint, color: "#fff", fontWeight: 800 } : undefined}
                  aria-hidden="true"
                >
                  {tint ? s.socialNetwork.name.slice(0, 2) : <Globe size={20} />}
                </span>
                <div className="item-body">
                  <div className="item-title">{s.socialNetwork.name}</div>
                  <div className="item-meta" style={{ wordBreak: "break-all" }}>
                    {prettyUrl(s.link)}
                  </div>
                </div>
                <a href={ensureProtocol(s.link)} target="_blank" rel="noreferrer" className="btn btn-secondary btn-sm">
                  <ExternalLink size={15} /> Abrir
                </a>
              </article>
            );
          })}
        </div>
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
        <form id="soc-form" className="stack" onSubmit={save} noValidate>
          <Field label="Rede" htmlFor="sn-id" error={touched && !networkId ? "Escolha a rede" : null}>
            <CatalogSelect
              id="sn-id"
              catalog="socialNetworks"
              value={networkId}
              onChange={setNetworkId}
              exclude={used}
              newLabel="Cadastrar outra rede…"
            />
          </Field>
          <Field label="Link do perfil" htmlFor="sn-link" error={touched && link.trim().length <= 3 ? "Informe o link" : null}>
            <input
              id="sn-link"
              className="input"
              placeholder="linkedin.com/in/seu-perfil"
              value={link}
              onChange={(e) => setLink(e.target.value)}
            />
          </Field>
        </form>
      </Modal>
    </>
  );
}
