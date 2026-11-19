import { useEffect, useRef, useState, type CSSProperties, type DragEvent, type ReactNode } from "react";
import {
  BriefcaseBusiness, CircleAlert, CircleCheck, CircleDashed, Copy, Download, FileCheck2, FileText,
  FileUp, Lightbulb, MessageCircleQuestionMark, PenLine, Quote, RotateCcw, ScanSearch,
  Sparkles, ThumbsUp, TriangleAlert, WandSparkles, X,
} from "lucide-react";
import { Button, PageHeader } from "../components/ui/ui";
import { useToast } from "../components/ui/Toast";
import { request, errorMessage } from "../api/client";
import "../styles/analyze.css";

type Priority = "HIGH" | "MEDIUM" | "LOW";
interface Improvement {
  priority: Priority;
  section: string;
  originalExcerpt: string;
  problem: string;
  suggestion: string;
  example?: string | null;
  question?: string | null;
}
interface Analysis {
  summary: string;
  strengths: string[];
  weaknesses: string[];
  suggestions: string[];
  improvements: Improvement[];
  compatibility?: { summary: string; matchedRequirements: string[]; requirementsWithoutEvidence: string[] };
}

/** Limites conferidos também pelo backend (CurriculumTextExtractor e CerebrasService). */
const MAX_FILE_BYTES = 1024 * 1024;
const MAX_TEXT = 16000;
const PRIORITY: Record<Priority, { label: string; short: string }> = {
  HIGH: { label: "Prioridade alta", short: "Alta" },
  MEDIUM: { label: "Prioridade média", short: "Média" },
  LOW: { label: "Prioridade baixa", short: "Baixa" },
};

function formatSize(bytes: number) {
  return bytes < 1024 * 1024 ? `${Math.max(1, Math.round(bytes / 1024))} KB` : `${(bytes / 1024 / 1024).toFixed(1)} MB`;
}

export default function Analyze() {
  const toast = useToast();
  const [file, setFile] = useState<File | null>(null);
  const [job, setJob] = useState("");
  const [analysis, setAnalysis] = useState<Analysis | null>(null);
  const [answers, setAnswers] = useState<Record<number, string>>({});
  const [notes, setNotes] = useState("");
  const [busy, setBusy] = useState<"analysis" | "revision" | null>(null);
  const [error, setError] = useState("");
  const [pdf, setPdf] = useState("");
  const [dragging, setDragging] = useState(false);
  const [filter, setFilter] = useState<Priority | "ALL">("ALL");
  const inputRef = useRef<HTMLInputElement>(null);
  const resultsRef = useRef<HTMLDivElement>(null);
  const revisionRef = useRef<HTMLElement>(null);
  useEffect(() => () => { if (pdf) URL.revokeObjectURL(pdf); }, [pdf]);

  const improvements = analysis?.improvements ?? [];
  const questions = improvements.filter(item => item.question);
  const answered = improvements.filter((item, i) => item.question && answers[i]?.trim()).length;
  const clarifications = improvements.flatMap((item, i) =>
    answers[i]?.trim() ? [item.question + "\n" + answers[i].trim()] : []
  ).concat(notes.trim() ? [notes.trim()] : []).join("\n\n");
  const step = pdf ? 3 : analysis ? 2 : 1;

  function reset() {
    setAnalysis(null);
    setAnswers({});
    setNotes("");
    setPdf("");
    setError("");
    setFilter("ALL");
  }

  function newAnalysis() {
    if (clarifications && !window.confirm("Começar uma nova análise? Suas respostas atuais serão descartadas.")) return;
    reset();
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function pick(candidate: File | null | undefined) {
    if (!candidate) return;
    reset();
    if (candidate.type !== "application/pdf" && !candidate.name.toLowerCase().endsWith(".pdf")) {
      setError("Envie o currículo em formato PDF."); return;
    }
    if (candidate.size > MAX_FILE_BYTES) { setError("Envie um PDF de até 1 MB."); return; }
    setFile(candidate);
  }

  function drop(e: DragEvent) {
    e.preventDefault();
    setDragging(false);
    if (!busy) pick(e.dataTransfer.files?.[0]);
  }

  function clearFile() {
    reset();
    setFile(null);
    if (inputRef.current) inputRef.current.value = "";
  }

  async function submit(revision: boolean) {
    if (!file || busy) return;
    setError("");
    if (file.size > MAX_FILE_BYTES) { setError("Envie um PDF de até 1 MB."); return; }
    if (revision && clarifications.length > MAX_TEXT) { setError("As respostas juntas devem ter até 16 mil caracteres."); return; }
    const body = new FormData();
    body.append("file", file);
    if (job.trim()) body.append("jobDescription", job.trim());
    setBusy(revision ? "revision" : "analysis");
    if (revision) setTimeout(() => revisionRef.current?.scrollIntoView({ behavior: "smooth", block: "center" }), 50);
    try {
      if (revision) {
        body.append("answers", clarifications);
        const blob = await request<Blob>("/api/cerebras/curriculum/revision", { method: "POST", body, responseType: "blob" });
        setPdf(URL.createObjectURL(blob));
        toast.success("Versão revisada pronta.");
        setTimeout(() => revisionRef.current?.scrollIntoView({ behavior: "smooth", block: "start" }), 80);
      } else {
        const result = await request<Analysis>("/api/cerebras/curriculum/analysis", { method: "POST", body });
        reset();
        setAnalysis(result);
        setTimeout(() => resultsRef.current?.scrollIntoView({ behavior: "smooth", block: "start" }), 80);
      }
    } catch (e) {
      setError(errorMessage(e).title);
    } finally {
      setBusy(null);
    }
  }

  async function copy(text: string) {
    try { await navigator.clipboard.writeText(text); toast.success("Exemplo copiado."); }
    catch { toast.error("Não foi possível copiar o exemplo."); }
  }

  return <div className="analyze-page">
    <PageHeader eyebrow="Currículo" title="Analise seu currículo com IA."
      text="Envie o PDF, receba um diagnóstico com melhorias por prioridade e gere uma versão revisada com as suas respostas." />

    <ol className="analyze-steps" aria-label="Etapas da análise">
      {["Enviar o currículo", "Revisar as melhorias", "Versão revisada"].map((label, index) => {
        const n = index + 1;
        return <li key={label} className={n < step ? "is-done" : n === step ? "is-active" : ""} aria-current={n === step ? "step" : undefined}>
          <span>{n < step ? <CircleCheck size={15} /> : n}</span>{label}
        </li>;
      })}
    </ol>

    {!analysis && <section className="card analyze-setup" aria-busy={busy === "analysis"}>
      <div className="analyze-setup-grid">
        <div className="analyze-setup-col">
          <span className="analyze-kicker">01 · Seu currículo</span>
          {!file ? <label className={"analyze-drop" + (dragging ? " is-dragging" : "")}
            onDragOver={e => { e.preventDefault(); if (!busy) setDragging(true); }}
            onDragLeave={() => setDragging(false)} onDrop={drop}>
            <input ref={inputRef} type="file" accept="application/pdf,.pdf" className="sr-only" disabled={!!busy}
              onChange={e => pick(e.target.files?.[0])} />
            <span className="analyze-drop-icon" aria-hidden="true"><FileUp size={28} /></span>
            <strong>{dragging ? "Solte o PDF aqui" : "Arraste o PDF do seu currículo"}</strong>
            <span>ou <u>clique para escolher</u> um arquivo</span>
          </label> : <div className="analyze-file">
            <span className="analyze-file-icon" aria-hidden="true"><FileText size={24} /><i>PDF</i></span>
            <div className="analyze-file-info">
              <strong>{file.name}</strong>
              <span><FileCheck2 size={14} aria-hidden="true" /> {formatSize(file.size)} · pronto para analisar</span>
            </div>
            <Button variant="ghost" size="sm" iconOnly onClick={clearFile} disabled={!!busy} aria-label="Remover arquivo"><X size={18} /></Button>
          </div>}
          <ul className="analyze-rules" aria-label="Requisitos do arquivo">
            <li>Texto selecionável</li><li>Até 20 páginas</li><li>Até 1 MB</li><li>Sem senha</li>
          </ul>
        </div>
        <div className="analyze-setup-col">
          <label className="analyze-kicker" htmlFor="analyze-job">02 · Vaga <span>(opcional)</span></label>
          <div className="analyze-job">
            <textarea id="analyze-job" className="textarea" rows={7} maxLength={MAX_TEXT} value={job} disabled={!!busy}
              placeholder="Cole aqui a descrição da vaga para ver quais requisitos o seu currículo comprova."
              onChange={e => { reset(); setJob(e.target.value); }} />
            <span className="analyze-counter">{job.length.toLocaleString("pt-BR")} / 16.000</span>
          </div>
          <p className="analyze-job-hint"><BriefcaseBusiness size={15} aria-hidden="true" />
            {job.trim() ? "Vamos comparar o currículo com os requisitos desta vaga." : "Sem vaga, a análise avalia a redação e a estrutura do currículo."}</p>
        </div>
      </div>
      <footer className="analyze-setup-foot">
        <p>A IA cita trechos reais do seu currículo e não inventa métricas. Nenhum arquivo fica salvo.</p>
        <Button size="lg" loading={busy === "analysis"} disabled={!file || !!busy} icon={<ScanSearch size={19} />}
          onClick={() => submit(false)}>Analisar currículo</Button>
      </footer>
    </section>}

    {error && <div className="alert error analyze-error" role="alert"><TriangleAlert size={18} aria-hidden="true" /><span>{error}</span></div>}

    {busy === "analysis" && <AnalyzeLoader mode="analysis" withJob={!!job.trim()} />}

    {analysis && <div className="analyze-results" ref={resultsRef}>
      <section className="analyze-source card">
        <span className="analyze-file-icon is-small" aria-hidden="true"><FileText size={18} /></span>
        <div>
          <strong>{file?.name}</strong>
          <span>{job.trim() ? "Analisado com a descrição da vaga" : "Análise da redação, sem vaga"}</span>
        </div>
        <Button variant="secondary" size="sm" icon={<RotateCcw size={15} />} disabled={!!busy} onClick={newAnalysis}>Nova análise</Button>
      </section>

      <section className="analyze-hero">
        <div className="analyze-hero-text">
          <span className="analyze-hero-kicker"><Sparkles size={14} aria-hidden="true" /> Diagnóstico</span>
          <h2>O que a IA encontrou no seu currículo</h2>
          <p>{analysis.summary}</p>
        </div>
        <dl className="analyze-stats">
          <div><dt>Pontos fortes</dt><dd>{analysis.strengths.length}</dd></div>
          <div><dt>Pontos de melhoria</dt><dd>{analysis.weaknesses.length}</dd></div>
          <div><dt>Ajustes sugeridos</dt><dd>{improvements.length}</dd></div>
          {analysis.compatibility && <div><dt>Requisitos comprovados</dt>
            <dd>{analysis.compatibility.matchedRequirements.length}
              <small>/{analysis.compatibility.matchedRequirements.length + analysis.compatibility.requirementsWithoutEvidence.length}</small></dd></div>}
        </dl>
      </section>

      {(analysis.strengths.length > 0 || analysis.weaknesses.length > 0) && <div className="analyze-columns">
        {analysis.strengths.length > 0 && <FeedbackCard tone="good" icon={<ThumbsUp size={18} />} title="Pontos fortes" items={analysis.strengths} />}
        {analysis.weaknesses.length > 0 && <FeedbackCard tone="warn" icon={<CircleAlert size={18} />} title="Pontos de melhoria" items={analysis.weaknesses} />}
      </div>}
      {analysis.suggestions.length > 0 && <FeedbackCard tone="idea" icon={<Lightbulb size={18} />} title="Sugestões gerais" items={analysis.suggestions} />}

      {analysis.compatibility && <Compatibility data={analysis.compatibility} />}

      <section className="analyze-improvements" aria-labelledby="analyze-improvements-title">
        <header className="analyze-section-head">
          <div>
            <span className="analyze-kicker">Revisar por prioridade</span>
            <h2 id="analyze-improvements-title">Ajustes no texto do currículo</h2>
            <p>Cada ajuste cita um trecho real do seu PDF. Responda às perguntas para a IA reescrever com fatos seus.</p>
          </div>
          {improvements.length > 1 && <div className="analyze-filter" role="group" aria-label="Filtrar por prioridade">
            {(["ALL", "HIGH", "MEDIUM", "LOW"] as const).map(key => {
              const count = key === "ALL" ? improvements.length : improvements.filter(item => item.priority === key).length;
              if (key !== "ALL" && !count) return null;
              return <button key={key} type="button" className={"chip" + (key !== "ALL" ? " is-" + key.toLowerCase() : "")}
                aria-pressed={filter === key} onClick={() => setFilter(key)}>
                {key === "ALL" ? "Todos" : PRIORITY[key].short} <span>{count}</span>
              </button>;
            })}
          </div>}
        </header>

        {improvements.length === 0 && <p className="card analyze-empty"><CircleCheck size={20} aria-hidden="true" />
          Nenhum ajuste específico no texto. Confira as sugestões gerais acima.</p>}

        <div className="analyze-list">
          {improvements.map((item, i) => (filter === "ALL" || item.priority === filter) &&
            <ImprovementCard key={i} item={item} index={i} total={improvements.length} answer={answers[i] ?? ""}
              disabled={!!busy} onCopy={copy}
              onAnswer={value => { setPdf(""); setAnswers(current => ({ ...current, [i]: value })); }} />)}
        </div>

        <section className="card analyze-notes">
          <div className="analyze-notes-head">
            <span className="analyze-notes-icon" aria-hidden="true"><PenLine size={18} /></span>
            <div>
              <label htmlFor="analyze-notes"><strong>Quer acrescentar algo?</strong></label>
              <p>Cursos, resultados, responsabilidades ou qualquer fato que não está no PDF. Entra na versão revisada.</p>
            </div>
          </div>
          <textarea id="analyze-notes" className="textarea" rows={3} value={notes} disabled={!!busy} maxLength={MAX_TEXT}
            placeholder="Ex.: Também liderei a migração do banco para PostgreSQL em 2025."
            onChange={e => { setPdf(""); setNotes(e.target.value); }} />
        </section>
      </section>

      {(busy === "revision" || pdf) && <section className="analyze-revision" ref={revisionRef} tabIndex={-1}
        aria-labelledby="analyze-revision-title">
        {busy === "revision" ? <AnalyzeLoader mode="revision" withJob={false} /> : <div className="card analyze-revised">
          <header className="analyze-revised-head">
            <span className="analyze-revised-icon" aria-hidden="true"><FileCheck2 size={22} /></span>
            <div>
              <span className="analyze-kicker">Versão revisada</span>
              <h2 id="analyze-revision-title">Seu currículo reescrito com as suas respostas</h2>
              <p>Leia com atenção antes de usar: confira se tudo o que está escrito é verdadeiro.</p>
            </div>
            <a className="btn btn-primary" href={pdf} download="curriculo-revisado.pdf"><Download size={17} /> Baixar PDF</a>
          </header>
          <iframe title="Currículo revisado" className="pdf-frame analyze-frame" src={pdf} />
        </div>}
      </section>}

      <div className="analyze-tray" role="region" aria-label="Gerar versão revisada">
        <div className="analyze-tray-progress">
          {questions.length > 0 && <span className="analyze-ring" aria-hidden="true" style={{ "--p": answered / questions.length } as CSSProperties}>
            <span>{answered}/{questions.length}</span>
          </span>}
          <div>
            <strong>{questions.length ? `${answered} de ${questions.length} perguntas respondidas` : "Acrescente suas informações"}</strong>
            <span>{clarifications ? "Pronto para gerar a versão revisada." : "Responda ao menos uma pergunta ou acrescente uma informação."}</span>
          </div>
        </div>
        <Button loading={busy === "revision"} disabled={!!busy || !clarifications} icon={<WandSparkles size={18} />}
          onClick={() => submit(true)}>{pdf ? "Gerar novamente" : "Gerar versão revisada"}</Button>
      </div>
    </div>}
  </div>;
}

function FeedbackCard({ tone, icon, title, items }: { tone: "good" | "warn" | "idea"; icon: ReactNode; title: string; items: string[] }) {
  return <section className={"card analyze-feedback is-" + tone}>
    <header><span className="analyze-feedback-icon" aria-hidden="true">{icon}</span><h3>{title}</h3><span className="analyze-feedback-count">{items.length}</span></header>
    <ul>{items.map((item, i) => <li key={i} style={{ "--i": i } as CSSProperties}>{item}</li>)}</ul>
  </section>;
}

function Compatibility({ data }: { data: NonNullable<Analysis["compatibility"]> }) {
  const total = data.matchedRequirements.length + data.requirementsWithoutEvidence.length;
  const percent = total ? Math.round((data.matchedRequirements.length / total) * 100) : 0;
  return <section className="card analyze-compat" aria-labelledby="analyze-compat-title">
    <header className="analyze-compat-head">
      <span className="analyze-compat-icon" aria-hidden="true"><BriefcaseBusiness size={20} /></span>
      <div>
        <span className="analyze-kicker">Compatibilidade com a vaga</span>
        <h2 id="analyze-compat-title">{total ? `${data.matchedRequirements.length} de ${total} requisitos com evidência no currículo` : "Requisitos da vaga"}</h2>
      </div>
    </header>
    {total > 0 && <div className="analyze-meter" role="img" aria-label={`${percent}% dos requisitos com evidência no currículo`}>
      <span style={{ width: percent + "%" }} />
    </div>}
    <p className="analyze-compat-summary">{data.summary}</p>
    <div className="analyze-compat-grid">
      {data.matchedRequirements.length > 0 && <div>
        <h3><CircleCheck size={16} aria-hidden="true" /> Com evidência</h3>
        <ul className="analyze-reqs is-matched">{data.matchedRequirements.map((item, i) => <li key={i}>{item}</li>)}</ul>
      </div>}
      {data.requirementsWithoutEvidence.length > 0 && <div>
        <h3><CircleDashed size={16} aria-hidden="true" /> Sem evidência no texto</h3>
        <ul className="analyze-reqs is-missing">{data.requirementsWithoutEvidence.map((item, i) => <li key={i}>{item}</li>)}</ul>
      </div>}
    </div>
    {data.requirementsWithoutEvidence.length > 0 && <p className="analyze-compat-note">
      Sem evidência não quer dizer que você não tem a habilidade: talvez ela só não esteja escrita. Se tiver, conte nas respostas abaixo.
    </p>}
  </section>;
}

function ImprovementCard({ item, index, total, answer, disabled, onAnswer, onCopy }: {
  item: Improvement; index: number; total: number; answer: string; disabled: boolean;
  onAnswer: (value: string) => void; onCopy: (text: string) => void;
}) {
  const id = "analyze-answer-" + index;
  const done = !!answer.trim();
  return <article className={"card analyze-item is-" + item.priority.toLowerCase() + (done ? " is-answered" : "")}
    style={{ "--i": index } as CSSProperties}>
    <header className="analyze-item-head">
      <span className="analyze-priority">{PRIORITY[item.priority]?.label ?? item.priority}</span>
      <span className="analyze-item-section">{item.section}</span>
      <span className="analyze-item-index">{index + 1} de {total}</span>
    </header>
    <blockquote className="analyze-quote">
      <Quote size={16} aria-hidden="true" />
      <span className="sr-only">Trecho do seu currículo: </span>{item.originalExcerpt}
    </blockquote>
    <div className="analyze-item-body">
      <div className="analyze-item-block is-problem">
        <h4><CircleAlert size={15} aria-hidden="true" /> O problema</h4><p>{item.problem}</p>
      </div>
      <div className="analyze-item-block is-suggestion">
        <h4><Lightbulb size={15} aria-hidden="true" /> Como melhorar</h4><p>{item.suggestion}</p>
      </div>
    </div>
    {item.example && <div className="analyze-example">
      <div className="analyze-example-head">
        <h4><Sparkles size={15} aria-hidden="true" /> Exemplo de reescrita</h4>
        <Button variant="ghost" size="sm" icon={<Copy size={14} />} onClick={() => onCopy(item.example!)}>Copiar</Button>
      </div>
      <p>{item.example}</p>
    </div>}
    {item.question && <div className="analyze-question">
      <label htmlFor={id}>
        <MessageCircleQuestionMark size={17} aria-hidden="true" />
        <span>{item.question}</span>
        {done && <span className="analyze-answered"><CircleCheck size={13} aria-hidden="true" /> Respondida</span>}
      </label>
      <textarea id={id} className="textarea" rows={2} disabled={disabled} value={answer} maxLength={MAX_TEXT}
        placeholder="Sua resposta (use apenas fatos reais)" onChange={e => onAnswer(e.target.value)} />
    </div>}
  </article>;
}

function AnalyzeLoader({ mode, withJob }: { mode: "analysis" | "revision"; withJob: boolean }) {
  const steps = mode === "analysis"
    ? ["Lendo o texto do PDF", "Avaliando redação e estrutura", ...(withJob ? ["Comparando com a vaga"] : []), "Conferindo os trechos citados"]
    : ["Reunindo suas respostas", "Reescrevendo o currículo", "Montando o PDF"];
  const [step, setStep] = useState(0);
  useEffect(() => {
    const timer = setInterval(() => setStep(current => Math.min(current + 1, steps.length - 1)), 2200);
    return () => clearInterval(timer);
  }, [steps.length]);

  return <section className={"card analyze-loader is-" + mode} role="status" aria-live="polite">
    <div className="analyze-paper-wrap" aria-hidden="true">
      <div className="analyze-paper">
        <span className="analyze-paper-head"><i /><span><b /><b /></span></span>
        {[86, 64, 92, 48, 78, 70, 88, 56].map((w, i) => <span key={i} className="analyze-paper-line"
          style={{ "--w": w + "%", "--d": i * 120 + "ms" } as CSSProperties} />)}
        <span className="analyze-paper-scan" />
      </div>
      <span className="analyze-paper-badge">{mode === "analysis" ? <ScanSearch size={22} /> : <WandSparkles size={22} />}</span>
    </div>
    <div className="analyze-loader-text">
      <span className="analyze-kicker">{mode === "analysis" ? "Analisando seu currículo" : "Gerando a versão revisada"}</span>
      <strong key={step}>{steps[step]}…</strong>
      <p>{mode === "analysis" ? "Costuma levar alguns segundos. Não feche esta página." : "A IA usa só o seu currículo e as suas respostas."}</p>
      <ol className="analyze-loader-steps">
        {steps.map((label, i) => <li key={label} className={i < step ? "is-done" : i === step ? "is-active" : ""}>
          <span>{i < step ? <CircleCheck size={13} /> : i + 1}</span>{label}
        </li>)}
      </ol>
    </div>
  </section>;
}

