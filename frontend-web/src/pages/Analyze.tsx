import { useEffect, useState } from "react";
import { Button, PageHeader } from "../components/ui/ui";
import { request, errorMessage } from "../api/client";

interface Improvement {
  priority: string;
  section: string;
  originalExcerpt: string;
  problem: string;
  suggestion: string;
  example?: string;
  question?: string;
}
interface Analysis {
  summary: string;
  strengths: string[];
  weaknesses: string[];
  suggestions: string[];
  improvements: Improvement[];
  compatibility?: { summary: string; matchedRequirements: string[]; requirementsWithoutEvidence: string[] };
}

function FeedbackList({ title, items }: { title: string; items: string[] }) {
  return items.length > 0 && <div><h3>{title}</h3><ul>{items.map((item, i) => <li key={i}>{item}</li>)}</ul></div>;
}

export default function Analyze() {
  const [file, setFile] = useState<File | null>(null);
  const [job, setJob] = useState("");
  const [analysis, setAnalysis] = useState<Analysis | null>(null);
  const [answers, setAnswers] = useState<Record<number, string>>({});
  const [notes, setNotes] = useState("");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [pdf, setPdf] = useState("");
  useEffect(() => () => { if (pdf) URL.revokeObjectURL(pdf); }, [pdf]);

  function reset() {
    setAnalysis(null);
    setAnswers({});
    setNotes("");
    setPdf("");
    setError("");
  }

  const clarifications = (analysis?.improvements ?? []).flatMap((item, i) =>
    answers[i]?.trim() ? [item.question + "\n" + answers[i].trim()] : []
  ).concat(notes.trim() ? [notes.trim()] : []).join("\n\n");

  async function submit(revision: boolean) {
    if (!file || busy) return;
    setError("");
    if (file.size > 1024 * 1024) { setError("Envie um PDF de até 1 MiB."); return; }
    if (revision && clarifications.length > 16000) { setError("As respostas juntas devem ter até 16 mil caracteres."); return; }
    const body = new FormData();
    body.append("file", file);
    if (job.trim()) body.append("jobDescription", job.trim());
    setBusy(true);
    try {
      if (revision) {
        body.append("answers", clarifications);
        const blob = await request<Blob>("/api/cerebras/curriculum/revision", { method: "POST", body, responseType: "blob" });
        setPdf(URL.createObjectURL(blob));
      } else {
        const result = await request<Analysis>("/api/cerebras/curriculum/analysis", { method: "POST", body });
        reset();
        setAnalysis(result);
      }
    } catch (e) {
      setError(errorMessage(e).title);
    } finally {
      setBusy(false);
    }
  }

  return <>
    <PageHeader eyebrow="Currículo" title="Analisar currículo" text="Envie seu PDF para identificar melhorias e revisar o conteúdo com suas respostas." />
    <div className="stack-lg">
      <section className="card card-pad stack-lg">
        <label className="field">Currículo em PDF
          <input type="file" accept="application/pdf,.pdf" disabled={busy} onChange={e => { reset(); setFile(e.target.files?.[0] ?? null); }} />
          <small>Texto selecionável, até 20 páginas e 1 MiB.</small>
        </label>
        <label className="field">Descrição da vaga (opcional)
          <textarea rows={4} maxLength={16000} value={job} disabled={busy} onChange={e => { reset(); setJob(e.target.value); }} />
        </label>
        <div><Button disabled={!file || busy} onClick={() => submit(false)}>Analisar currículo</Button></div>
      </section>
      {busy && <p role="status">Processando seu currículo…</p>}
      {error && <div className="alert error" role="alert">{error}</div>}
      {analysis && <>
        <section className="card card-pad stack-lg"><h2>Diagnóstico</h2><p>{analysis.summary}</p>
          <FeedbackList title="Pontos fortes" items={analysis.strengths} />
          <FeedbackList title="Pontos de melhoria" items={analysis.weaknesses} />
          <FeedbackList title="Sugestões" items={analysis.suggestions} />
        </section>
        {analysis.compatibility && <section className="card card-pad stack-lg">
          <h2>Compatibilidade com a vaga</h2><p>{analysis.compatibility.summary}</p>
          <FeedbackList title="Requisitos atendidos" items={analysis.compatibility.matchedRequirements} />
          <FeedbackList title="Requisitos sem evidência" items={analysis.compatibility.requirementsWithoutEvidence} />
        </section>}
        <section className="card card-pad stack-lg"><h2>Revisar por prioridade</h2>
          {analysis.improvements.map((item, i) => <article key={i} className="stack-lg">
            <h3>{item.section} · Prioridade {({HIGH:"alta", MEDIUM:"média", LOW:"baixa"})[item.priority as "HIGH"|"MEDIUM"|"LOW"]}</h3>
            <blockquote>{item.originalExcerpt}</blockquote><p>{item.problem}</p><p>{item.suggestion}</p>
            {item.example && <p><strong>Exemplo de reescrita</strong><br />{item.example}</p>}
            {item.question && <label className="field">{item.question}
              <textarea rows={2} disabled={busy} value={answers[i] ?? ""} onChange={e => {setPdf(""); setAnswers({...answers, [i]: e.target.value});}} />
            </label>}
          </article>)}
          <label className="field">Informações que deseja acrescentar
            <textarea rows={3} value={notes} disabled={busy} onChange={e => {setPdf(""); setNotes(e.target.value);}} />
          </label>
          <div className="row row-wrap"><Button disabled={busy || !clarifications} onClick={() => submit(true)}>Gerar versão revisada</Button>
            {pdf && <a className="btn btn-secondary" href={pdf} download="curriculo-revisado.pdf">Baixar PDF revisado</a>}
          </div>
          {pdf && <iframe title="Currículo revisado" className="pdf-frame" src={pdf} />}
        </section>
      </>}
    </div>
  </>;
}
