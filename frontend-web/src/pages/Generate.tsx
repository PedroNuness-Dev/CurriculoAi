import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";
import { AlertTriangle, Check, Download, FileText, RefreshCw, Sparkles, X } from "lucide-react";
import { completeness, useProfile } from "../auth/ProfileContext";
import { useAuth } from "../auth/AuthContext";
import { resumeApi } from "../api/endpoints";
import { errorMessage } from "../api/client";
import { Symbol } from "../components/brand/Brand";
import { Button, PageHeader } from "../components/ui/ui";
import { useToast } from "../components/ui/Toast";

const STEPS = [
  "Lendo seu perfil completo",
  "Escrevendo o resumo profissional",
  "Reescrevendo experiências com verbos de ação",
  "Organizando seções na ordem ideal para ATS",
  "Montando o PDF final",
];

type Phase = "idle" | "running" | "done" | "error";

export default function Generate() {
  const { profile } = useProfile();
  const { session } = useAuth();
  const toast = useToast();
  const c = completeness(profile);

  const [phase, setPhase] = useState<Phase>("idle");
  const [step, setStep] = useState(0);
  const [elapsed, setElapsed] = useState(0);
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [error, setError] = useState<{ title: string; details: string[] } | null>(null);
  const abortRef = useRef<AbortController | null>(null);

  // Libera o blob ao sair da página.
  useEffect(() => () => void (pdfUrl && URL.revokeObjectURL(pdfUrl)), [pdfUrl]);
  useEffect(() => () => abortRef.current?.abort(), []);

  // Avança as etapas visuais enquanto a requisição roda.
  useEffect(() => {
    if (phase !== "running") return;
    const t0 = Date.now();
    const id = window.setInterval(() => {
      const s = (Date.now() - t0) / 1000;
      setElapsed(Math.floor(s));
      setStep(Math.min(STEPS.length - 1, Math.floor(s / 4)));
    }, 250);
    return () => window.clearInterval(id);
  }, [phase]);

  async function generate() {
    abortRef.current?.abort();
    const ctrl = new AbortController();
    abortRef.current = ctrl;
    setPhase("running");
    setStep(0);
    setElapsed(0);
    setError(null);
    try {
      const blob = await resumeApi.generatePdf(ctrl.signal);
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
  const weak = (profile?.experiences.length ?? 0) + (profile?.formations.length ?? 0) === 0;

  return (
    <>
      <PageHeader
        eyebrow="Currículo"
        title="Gerar com IA"
        text="A IA lê tudo o que você cadastrou, escreve o currículo e devolve um PDF pronto para enviar."
      />

      <div className="stack-lg">
        <section className="gen-stage">
          <div className="row row-wrap" style={{ gap: 32, alignItems: "center" }}>
            <div className={`gen-orb ${phase === "running" ? "busy" : ""}`}>
              <Symbol size={64} tone="white" />
            </div>
            <div className="grow" style={{ minWidth: 260 }}>
              {phase === "running" ? (
                <>
                  <span className="badge lime">
                    <span className="spinner" style={{ width: 12, height: 12, borderWidth: 2 }} /> Gerando · {elapsed}s
                  </span>
                  <h2 style={{ marginTop: 14 }}>A IA está escrevendo seu currículo…</h2>
                  <div className="gen-steps">
                    {STEPS.map((s, i) => (
                      <div key={s} className={`gen-step ${i === step ? "on" : i < step ? "done" : ""}`}>
                        <span className="dot">{i < step ? <Check size={13} strokeWidth={3} /> : i + 1}</span>
                        {s}
                      </div>
                    ))}
                  </div>
                  <div style={{ marginTop: 22 }}>
                    <Button variant="ghost" size="sm" style={{ color: "#c7cede" }} icon={<X size={16} />} onClick={() => abortRef.current?.abort()}>
                      Cancelar
                    </Button>
                  </div>
                </>
              ) : (
                <>
                  <span className="badge lime">
                    <Sparkles size={14} /> Powered by Cerebras
                  </span>
                  <h2 style={{ marginTop: 14 }}>
                    {phase === "done" ? "Pronto! Seu currículo está aqui." : "Seu currículo, escrito por IA."}
                  </h2>
                  <p style={{ marginTop: 10, color: "rgba(255,255,255,.75)", fontWeight: 550, maxWidth: 560 }}>
                    Resumo → Habilidades → Experiência → Educação → Certificações → Idiomas. Tudo em português, com verbos de
                    ação e formatação amigável a ATS.
                  </p>
                  <div className="row row-wrap" style={{ marginTop: 24 }}>
                    <Button variant="lime" size="lg" icon={phase === "done" ? <RefreshCw size={19} /> : <Sparkles size={19} />} onClick={generate}>
                      {phase === "done" ? "Gerar nova versão" : "Gerar meu currículo"}
                    </Button>
                    {pdfUrl && (
                      <a href={pdfUrl} download={fileName} className="btn btn-white btn-lg">
                        <Download size={19} /> Baixar PDF
                      </a>
                    )}
                  </div>
                  <p className="small" style={{ marginTop: 14, color: "rgba(255,255,255,.55)" }}>
                    Perfil {c.percent}% completo · pode levar alguns segundos.
                  </p>
                </>
              )}
            </div>
          </div>
        </section>

        {weak && phase === "idle" && (
          <div className="alert warning">
            <AlertTriangle size={18} />
            <div>
              Seu perfil ainda não tem experiências nem formações. A IA vai gerar algo bem genérico —{" "}
              <Link to="/app/experiencias">adicione uma experiência</Link> ou <Link to="/app/formacao">formação</Link> antes.
            </div>
          </div>
        )}

        {phase === "error" && error && (
          <div className="alert error" role="alert">
            <AlertTriangle size={18} />
            <div>
              <b>Não foi possível gerar o currículo.</b> {error.title}
              {error.details.length > 0 && <div className="small">{error.details.join(" · ")}</div>}
              <div className="small" style={{ marginTop: 4 }}>
                Verifique se a variável <code>CEREBRAS_API_KEY</code> do backend está configurada.
              </div>
            </div>
          </div>
        )}

        {pdfUrl && (
          <section className="card card-pad">
            <div className="card-head">
              <div className="card-title">
                <span className="icon-tile grad">
                  <FileText size={18} />
                </span>
                Pré-visualização
              </div>
              <a href={pdfUrl} download={fileName} className="btn btn-secondary btn-sm">
                <Download size={16} /> {fileName}
              </a>
            </div>
            <iframe className="pdf-frame" src={pdfUrl} title="Pré-visualização do currículo em PDF" />
          </section>
        )}
      </div>
    </>
  );
}
