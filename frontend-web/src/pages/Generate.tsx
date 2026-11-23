import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";
import { AlertTriangle, ArrowUpRight, Check, Download, FileText, GitBranch, RefreshCw, SearchCheck, Sparkles, X } from "lucide-react";
import { completeness, useProfile } from "../auth/ProfileContext";
import { useAuth } from "../auth/AuthContext";
import { resumeApi } from "../api/endpoints";
import { errorMessage } from "../api/client";
import type { GenerationPhase } from "../components/generate/GenerationVisual";
import { Symbol } from "../components/brand/Brand";
import { TextArea as AstryxTextArea } from "@astryxdesign/core/TextArea";
import { Theme as AstryxTheme } from "@astryxdesign/core/theme";
import { neutralTheme } from "@astryxdesign/theme-neutral";
import "@astryxdesign/core/astryx.css";
import "@astryxdesign/theme-neutral/theme.css";
import { Button, CatalogFeedback, PageHeader, Skeleton } from "../components/ui/ui";
import { useToast } from "../components/ui/Toast";
import "../styles/generate.css";

type Phase = GenerationPhase;

export default function Generate() {
  const { profile, loading, error: profileError, reload } = useProfile();
  const { session } = useAuth();
  const toast = useToast();
  const c = completeness(profile);

  const [jobDescription, setJobDescription] = useState("");
  const [answers, setAnswers] = useState("");
  const [studying, setStudying] = useState(false);
  const [insights, setInsights] = useState<Awaited<ReturnType<typeof resumeApi.studyProfile>> | null>(null);
  const [insightAnswers, setInsightAnswers] = useState<Record<string, string>>({});
  async function study() {
    setStudying(true);
    setError(null);
    try { setInsights(await resumeApi.studyProfile()); setInsightAnswers({}); if (pdfUrl) setVersionOutdated(true); }
    catch (err) { setError(errorMessage(err)); }
    finally { setStudying(false); }
  }
  const [phase, setPhase] = useState<Phase>("idle");
  const [elapsed, setElapsed] = useState(0);
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [error, setError] = useState<{ title: string; details: string[] } | null>(null);
  const [previewLoadedUrl, setPreviewLoadedUrl] = useState<string | null>(null);
  const [versionOutdated, setVersionOutdated] = useState(false);
  const abortRef = useRef<AbortController | null>(null);

  // Libera o blob ao sair da página.
  useEffect(() => () => void (pdfUrl && URL.revokeObjectURL(pdfUrl)), [pdfUrl]);
  useEffect(() => () => abortRef.current?.abort(), []);

  // Mede apenas o tempo decorrido da requisição, sem estimar progresso ou conclusão.
  useEffect(() => {
    if (phase !== "running") return;
    const t0 = Date.now();
    const id = window.setInterval(() => {
      const s = (Date.now() - t0) / 1000;
      setElapsed(Math.floor(s));
    }, 250);
    return () => window.clearInterval(id);
  }, [phase]);

  async function generate() {
    if (!profile || profileError) {
      setError({ title: "Não foi possível confirmar os dados do perfil. Atualize a página ou tente carregar o perfil novamente.", details: [] });
      setPhase("error");
      return;
    }
    abortRef.current?.abort();
    const ctrl = new AbortController();
    abortRef.current = ctrl;
    setPhase("running");
    setElapsed(0);
    setError(null);
    try {
      const confirmedAnswers = [
        ...Object.entries(insightAnswers).filter(([, value]) => value.trim()).map(([question, value]) => `${question}\nResposta: ${value.trim()}`),
        answers.trim(),
      ].filter(Boolean).join("\n\n");
      const blob = await resumeApi.generatePdf(ctrl.signal, jobDescription.trim(), confirmedAnswers);
      if (pdfUrl) URL.revokeObjectURL(pdfUrl);
      const url = URL.createObjectURL(new Blob([blob], { type: "application/pdf" }));
      setPdfUrl(url);
      setVersionOutdated(false);
      setPhase("done");
      toast.success("Currículo gerado!");
    } catch (err) {
      if ((err as Error).name === "AbortError") {
        setPhase(pdfUrl ? "done" : "idle");
        return;
      }
      const m = errorMessage(err);
      setError(m);
      setPhase("error");
    }
  }

  const fileName = `curriculo-${(session?.name ?? "candidato").toLowerCase().normalize("NFD").replace(/[̀-ͯ]/g, "").replace(/\s+/g, "-")}.pdf`;
  const weak = (profile?.experiences.length ?? 0) + (profile?.formations.length ?? 0) + (profile?.projects.length ?? 0) === 0;
  const markChanged = () => { if (pdfUrl) setVersionOutdated(true); };

  return (
    <div className="generation-page">
      <PageHeader title="Gerar currículo" />
      {loading ? (
        <div className="generation-loading" role="status" aria-label="Carregando informações do perfil"><Skeleton h={320} /><Skeleton h={220} /></div>
      ) : (
        <>
          {profileError && <CatalogFeedback onRetry={reload}>{profileError}</CatalogFeedback>}
          <AstryxTheme theme={neutralTheme}><div className="generation-primary-grid">
          <section className="gen-stage generation-stage" id="generation-create-title" aria-label="Geração do currículo" aria-busy={phase === "running"}>
            <div className="generation-stage-body">
              <div className="generation-stage-top">
                <div className={"gen-orb " + (phase === "running" ? "busy" : "")} aria-hidden="true"><Symbol size={32} /></div>
                <span className="badge lime"><Sparkles size={14} /> Powered by Cerebras</span>
              </div>
              <div className="generation-state" key={phase}>
                <span className="generation-kicker">{phase === "running" ? "GERANDO PDF" : phase === "done" ? "PDF DISPONÍVEL" : phase === "error" ? "GERAÇÃO INTERROMPIDA" : "PRONTO PARA GERAR"}</span>
                <h2 role={phase === "running" || phase === "done" ? "status" : undefined}>{phase === "running" ? "Preparando seu PDF…" : phase === "done" ? "Seu currículo está pronto" : phase === "error" ? "Não foi possível gerar" : "Seu próximo currículo começa aqui"}</h2>
                <p>{phase === "running" ? `Mantenha esta página aberta · ${elapsed}s decorridos` : phase === "done" ? "Confira a prévia ou baixe o arquivo." : phase === "error" ? "Confira o motivo abaixo e tente novamente." : "Geramos o PDF a partir dos dados do seu perfil."}</p>
                {phase === "error" && error && <div className="generation-inline-error" role="alert"><AlertTriangle size={17} /><span>{error.title}{error.details.length > 0 && ` ${error.details.join(" · ")}`}</span></div>}
              </div>
              <nav className="generation-stage-shortcuts" aria-label="Outras opções de geração">
                <span>Perfil {c.done}/{c.total}</span>
                <a href="#generation-review-title">Revisar dados</a>
                <a href="#generation-investigation-title">Investigar trajetória</a>
              </nav>
            </div>
            <div className="generation-actions">
              {phase === "running" ? <Button variant="secondary" icon={<X size={16} />} onClick={() => abortRef.current?.abort()}>Cancelar geração</Button> : <>
                <Button variant={phase === "done" ? "secondary" : "primary"} size="lg" icon={phase === "done" ? <RefreshCw size={19} /> : <Sparkles size={19} />} disabled={studying || !profile || !!profileError} onClick={generate}>{phase === "done" ? "Gerar nova versão" : phase === "error" ? "Tentar novamente" : "Gerar meu currículo"}</Button>
                {pdfUrl && <a href={pdfUrl} download={fileName} className="btn btn-primary btn-lg"><Download size={19} /> Baixar PDF</a>}
                {phase === "done" && <a className="generation-preview-link" href="#generation-preview-title">Ver prévia <ArrowUpRight size={15} aria-hidden="true" /></a>}
                {versionOutdated && <p className="generation-outdated" role="status">Há alterações novas. Gere outra versão para incluí-las no PDF.</p>}
              </>}
            </div>
          </section>
          <section className="card generation-personalize" aria-labelledby="generation-personalize-title">
            <div className="generation-personalize-head"><span className="generation-review-kicker">OPCIONAL</span><h2 id="generation-personalize-title">Personalize esta versão</h2><p>Informe uma vaga ou resultados que queira destacar no PDF.</p></div>
            <div className="investigation-fields">
              <AstryxTextArea id="generation-job" label="Descrição da vaga (opcional)" rows={3} htmlMaxLength={16000} value={jobDescription} isDisabled={phase === "running"}
                onChange={value => { setJobDescription(value); markChanged(); }} placeholder="Cole a vaga para priorizar os requisitos relevantes." />
              <AstryxTextArea id="generation-answers" label="Complementos confirmados (opcional)" rows={3} htmlMaxLength={16000} value={answers} isDisabled={phase === "running"}
                onChange={value => { setAnswers(value); markChanged(); }} placeholder="Inclua resultados e contribuições que queira destacar." />
            </div>
          </section>
          <section className="generation-investigation" aria-labelledby="generation-investigation-title">
            <header className="investigation-header"><div><span>DESCUBRA COMPETÊNCIAS · OPCIONAL</span><h2 id="generation-investigation-title">Investigue sua trajetória</h2>
              <p>A IA analisa seu perfil e propõe competências para você confirmar antes de gerar o currículo.</p></div>
              <Button variant="primary" icon={<SearchCheck size={17} />} onClick={study} disabled={studying || phase === "running" || !profile || !!profileError}>{studying ? "Investigando…" : insights ? "Investigar novamente" : "Investigar perfil"}</Button>
            </header>
            {insights && <div className="investigation-results" aria-live="polite">
              {insights.map((item, index) => <article className="investigation-insight" key={index}>
                <span className="insight-number">{String(index + 1).padStart(2, "0")}</span><div><h3>{item.skill}</h3>
                  <p>{item.interpretation}</p><blockquote>“{item.evidence}”</blockquote>
                  <label htmlFor={`insight-answer-${index}`}>{item.question}</label><textarea id={`insight-answer-${index}`} rows={2} maxLength={3000} disabled={phase === "running"} value={insightAnswers[item.question] ?? ""} onChange={event => { setInsightAnswers(current => ({ ...current, [item.question]: event.target.value })); markChanged(); }} placeholder="Responda apenas se essa experiência for sua" /></div>
              </article>)}
              {insights.length === 0 && <p>Ainda faltam evidências para novas descobertas. Complete a descrição dos projetos e experiências para dar mais contexto.</p>}
              <p className="investigation-note">As descobertas são hipóteses baseadas nos dados cadastrados. Confirme o que corresponde à sua experiência; a IA não adiciona fatos sem sua confirmação.</p>
            </div>}
            {error && phase !== "error" && <p className="investigation-error" role="alert">{error.title}</p>}
          </section>
          </div></AstryxTheme>
          {weak && phase === "idle" && !profileError && <div className="alert warning generation-profile-warning"><AlertTriangle size={18} /><div>Seu perfil ainda não tem experiência nem formação. <Link to="/app/experiencias">Adicione experiência</Link> ou <Link to="/app/formacao">formação</Link> para melhorar o PDF.</div></div>}
          {pdfUrl && <section className="card generation-preview" aria-labelledby="generation-preview-title">
            <div className="generation-preview-head"><div><span className="generation-review-kicker">O resultado da sua trajetória</span><h2 id="generation-preview-title" tabIndex={-1}><FileText size={21} aria-hidden="true" /> Pré-visualização do currículo</h2><p>A prévia e o download usam o mesmo arquivo PDF.</p></div><a href={pdfUrl} download={fileName} className="btn btn-secondary btn-sm"><Download size={16} /> {fileName}</a></div>
            <div className="generation-paper"><div className="generation-document-label"><span>PDF gerado</span><span>Visualização do documento</span></div><div className="generation-preview-status">{previewLoadedUrl !== pdfUrl && <p className="generation-preview-loading" role="status"><span className="spinner" aria-hidden="true" /> Carregando pré-visualização…</p>}</div><iframe key={pdfUrl} className="pdf-frame" src={pdfUrl} title="Pré-visualização do currículo em PDF" onLoad={() => setPreviewLoadedUrl(pdfUrl)} /></div>
          </section>}
          <section className="card generation-review" aria-labelledby="generation-review-title">
            <div className="generation-review-head">
              <div className="generation-review-intro">
                <span className="generation-review-kicker">DADOS DE ORIGEM</span>
                <h2 id="generation-review-title">Revise seu perfil</h2>
                <p>O PDF usa estas informações. Abra uma seção para corrigir ou completar os dados.</p>
              </div>
              <div className="generation-review-meta">
                <span className="generation-profile-count">{profileError && !profile ? "Dados do perfil indisponíveis" : `${c.done} de ${c.total} seções preenchidas`}</span>
                <Link to="/app/github" className="btn btn-secondary btn-sm"><GitBranch size={15} aria-hidden="true" /> Importar projetos do GitHub</Link>
              </div>
            </div>
            <div className="generation-profile-sections">
              {c.checks.map((section) => (
                <Link key={section.key} to={section.to} className={section.done ? "is-complete" : ""}>
                  <span className="generation-check" aria-hidden="true">{section.done ? <Check size={13} /> : <PlusMark />}</span>
                  {section.label}
                  <span className="sr-only">{profileError && !profile ? ": consulta indisponível" : section.done ? ": preenchido" : ": não informado"}</span>
                </Link>
              ))}
            </div>
          </section>



        </>
      )}
    </div>
  );
}

function PlusMark() {
  return <span className="generation-missing-mark" />;
}
