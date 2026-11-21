import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";
import { AlertTriangle, ArrowUpRight, Check, Download, FileText, GitBranch, RefreshCw, Sparkles, X } from "lucide-react";
import { completeness, useProfile } from "../auth/ProfileContext";
import { useAuth } from "../auth/AuthContext";
import { resumeApi } from "../api/endpoints";
import { errorMessage } from "../api/client";
import { Symbol } from "../components/brand/Brand";
import { GenerationVisual, type GenerationPhase } from "../components/generate/GenerationVisual";
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
  async function study() {
    setStudying(true);
    setError(null);
    try { setInsights(await resumeApi.studyProfile()); }
    catch (err) { setError(errorMessage(err)); }
    finally { setStudying(false); }
  }
  const [phase, setPhase] = useState<Phase>("idle");
  const [elapsed, setElapsed] = useState(0);
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [error, setError] = useState<{ title: string; details: string[] } | null>(null);
  const [previewLoadedUrl, setPreviewLoadedUrl] = useState<string | null>(null);
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
    abortRef.current?.abort();
    const ctrl = new AbortController();
    abortRef.current = ctrl;
    setPhase("running");
    setElapsed(0);
    setError(null);
    try {
      const blob = await resumeApi.generatePdf(ctrl.signal, jobDescription.trim(), answers.trim());
      if (pdfUrl) URL.revokeObjectURL(pdfUrl);
      const url = URL.createObjectURL(new Blob([blob], { type: "application/pdf" }));
      setPdfUrl(url);
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

  return (
    <div className="generation-page">
      <PageHeader eyebrow="Currículo" title="Sua trajetória, pronta para o próximo passo." text="Revise seu perfil e gere um currículo A4 com projetos, habilidades e links profissionais." />
      {loading ? (
        <div className="generation-loading" role="status" aria-label="Carregando informações do perfil"><Skeleton h={320} /><Skeleton h={220} /></div>
      ) : (
        <>
          {profileError && <CatalogFeedback onRetry={reload}>{profileError}</CatalogFeedback>}
          <section className="card generation-review" aria-labelledby="generation-review-title">
            <div className="generation-review-head">
              <div className="generation-review-intro">
                <span className="generation-review-kicker">Antes de gerar</span>
                <h2 id="generation-review-title">Seu perfil, em revisão.</h2>
                <p>A geração usa os dados cadastrados. Clique em uma seção para conferir ou completar.</p>
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
          <section className="card stack-lg" aria-label="Descobertas sobre seu perfil">
            <h2>O que sua trajetória revela</h2>
            <p>A IA cruza experiências, estudos e projetos cadastrados para encontrar competências pouco exploradas. As interpretações abaixo são hipóteses: confirme sua contribuição antes de incluí-la no currículo.</p>
            <Button onClick={study} disabled={studying || phase === "running"}>{studying ? "Estudando seu perfil…" : "Investigar meu perfil"}</Button>
            {insights?.map((item, index) => <article key={index}>
              <h3>{item.skill}</h3><blockquote>{item.evidence}</blockquote>
              <p>{item.interpretation}</p><p><strong>Para aprofundar:</strong> {item.question}</p>
            </article>)}
            {insights?.length === 0 && <p>Ainda faltam evidências para novas descobertas. Descreva sua contribuição nos projetos e experiências.</p>}
            <label htmlFor="generation-answers">Contribuições e respostas confirmadas (opcional)</label>
            <textarea id="generation-answers" rows={6} maxLength={16000} value={answers} disabled={phase === "running"}
              onChange={event => setAnswers(event.target.value)} placeholder="Conte o que você fez, quais decisões tomou, dificuldades resolvidas e resultados que consegue comprovar." />
            <label htmlFor="generation-job">Descrição da vaga (opcional)</label>
            <textarea id="generation-job" rows={5} maxLength={16000} value={jobDescription} disabled={phase === "running"}
              onChange={event => setJobDescription(event.target.value)} placeholder="Sem vaga, o currículo seguirá seu objetivo profissional." />
            <p>As respostas complementam apenas esta geração. Para reutilizá-las depois, atualize também seu perfil.</p>
            {error && phase !== "error" && <p role="alert">{error.title}</p>}
          </section>
          <div className="generation-workspace">
            <div className="stack-lg generation-main">
              <section className="gen-stage generation-stage" aria-label="Geração do currículo" aria-busy={phase === "running"}>
                <div className="generation-stage-body">
                  <div className="generation-stage-top"><div className={"gen-orb " + (phase === "running" ? "busy" : "")} aria-hidden="true"><Symbol size={42} /></div><span className="badge lime"><Sparkles size={14} /> Powered by Cerebras</span></div>
                  <div className="generation-stage-grid">
                  <div className="generation-state" key={phase}>
                    <span className="generation-kicker">{phase === "running" ? "Solicitação em processamento" : phase === "done" ? "Documento disponível" : phase === "error" ? "Geração não concluída" : "Do seu perfil para o PDF"}</span>
                    <h2 role={phase === "running" || phase === "done" ? "status" : undefined}>{phase === "running" ? "Estamos gerando seu currículo." : phase === "done" ? "Seu currículo está pronto." : phase === "error" ? "Vamos tentar novamente?" : "Sua história, pronta para o mercado."}</h2>
                    <p>{phase === "running" ? "Aguardando o retorno da geração. O documento aparecerá aqui assim que estiver disponível." : phase === "done" ? "Confira a pré-visualização abaixo e baixe o mesmo arquivo para usar nas suas candidaturas." : phase === "error" ? "Confira a mensagem abaixo. Você pode tentar novamente com as informações já cadastradas." : "A IA usa os dados do seu perfil para escrever e organizar o currículo. Revise o PDF antes de baixar."}</p>
                    {phase === "running" ? <div className="generation-activity"><span className="spinner" aria-hidden="true" /><span>Gerando · {elapsed}s decorridos</span></div> : <div className="generation-format"><FileText size={17} aria-hidden="true" /><span>Documento em PDF · Currículo em português</span></div>}
                  </div>
                  <GenerationVisual key={`visual-${phase}`} phase={phase} />
                  </div>
                </div>
                <div className="generation-actions">
                  {phase === "running" ? <><p>Você pode cancelar a solicitação enquanto aguarda.</p><Button variant="secondary" icon={<X size={16} />} onClick={() => abortRef.current?.abort()}>Cancelar</Button></> : <>
                    <Button variant={phase === "done" ? "secondary" : "primary"} size="lg" icon={phase === "done" ? <RefreshCw size={19} /> : <Sparkles size={19} />} onClick={generate} disabled={studying}>{phase === "done" ? "Gerar nova versão" : phase === "error" ? "Tentar novamente" : "Gerar meu currículo"}</Button>
                    {pdfUrl && <a href={pdfUrl} download={fileName} className="btn btn-primary btn-lg"><Download size={19} /> Baixar PDF</a>}
                    {phase === "done" && <a className="generation-preview-link" href="#generation-preview-title">Ver pré-visualização <ArrowUpRight size={15} aria-hidden="true" /></a>}<p>Usa as informações atuais do seu perfil.</p>
                  </>}
                </div>
              </section>
              {weak && phase === "idle" && !profileError && <div className="alert warning generation-profile-warning"><AlertTriangle size={18} /><div>Seu perfil ainda não tem experiências nem formações. <Link to="/app/experiencias">Adicione uma experiência</Link> ou <Link to="/app/formacao">formação</Link> para dar mais contexto ao currículo. Você também pode gerar com as informações atuais.</div></div>}
              {phase === "error" && error && <div className="alert error generation-error" role="alert"><AlertTriangle size={18} /><div><b>Não foi possível gerar o currículo.</b> {error.title}{error.details.length > 0 && <div className="small">{error.details.join(" · ")}</div>}<p>Tente novamente em alguns instantes. Seus dados continuam salvos no perfil.</p></div></div>}
            </div>
          </div>
          {pdfUrl && <section className="card generation-preview" aria-labelledby="generation-preview-title">
            <div className="generation-preview-head"><div><span className="generation-review-kicker">O resultado da sua trajetória</span><h2 id="generation-preview-title" tabIndex={-1}><FileText size={21} aria-hidden="true" /> Pré-visualização do currículo</h2><p>A prévia e o download usam o mesmo arquivo PDF.</p></div><a href={pdfUrl} download={fileName} className="btn btn-secondary btn-sm"><Download size={16} /> {fileName}</a></div>
            <div className="generation-paper"><div className="generation-document-label"><span>PDF gerado</span><span>Visualização do documento</span></div><div className="generation-preview-status">{previewLoadedUrl !== pdfUrl && <p className="generation-preview-loading" role="status"><span className="spinner" aria-hidden="true" /> Carregando pré-visualização…</p>}</div><iframe key={pdfUrl} className="pdf-frame" src={pdfUrl} title="Pré-visualização do currículo em PDF" onLoad={() => setPreviewLoadedUrl(pdfUrl)} /></div>
          </section>}
        </>
      )}
    </div>
  );
}

function PlusMark() {
  return <span className="generation-missing-mark" />;
}
