import { useMemo, type CSSProperties } from "react";
import { Check } from "lucide-react";
import "../../styles/generation-visual.css";

export type GenerationPhase = "idle" | "running" | "done" | "error";

// Linhas da folha ilustrada: "title" = título de seção, "text" = linha de conteúdo (largura em %).
const LINES: { kind: "title" | "text"; w: number }[] = [
  { kind: "title", w: 38 }, { kind: "text", w: 94 }, { kind: "text", w: 86 }, { kind: "text", w: 68 },
  { kind: "title", w: 32 }, { kind: "text", w: 96 }, { kind: "text", w: 80 }, { kind: "text", w: 88 }, { kind: "text", w: 58 },
  { kind: "title", w: 44 }, { kind: "text", w: 74 }, { kind: "text", w: 64 },
];

const CONFETTI_COLORS = ["var(--lime)", "#FFFFFF", "#55B5FF", "#B8DEFF"];

/**
 * Ilustração da folha do currículo no cartão de geração. É decorativa: não indica progresso real,
 * apenas reflete o estado da requisição (parado, gerando, pronto ou erro).
 */
export function GenerationVisual({ phase }: { phase: GenerationPhase }) {
  // Partículas do confete, espalhadas em círculo (posições fixas, calculadas uma vez).
  const confetti = useMemo(
    () =>
      Array.from({ length: 16 }, (_, i) => {
        const angle = (i / 16) * Math.PI * 2 + (i % 2 ? 0.18 : -0.12);
        const distance = 92 + (i % 3) * 22;
        return {
          x: Math.round(Math.cos(angle) * distance),
          y: Math.round(Math.sin(angle) * distance),
          color: CONFETTI_COLORS[i % CONFETTI_COLORS.length],
          rotate: (i * 47) % 360,
          round: i % 3 === 0,
        };
      }),
    [],
  );

  return (
    <div className="gen-visual" data-phase={phase} aria-hidden="true">
      <span className="gen-visual-glow" />
      <span className="gen-visual-orbit"><i /><i /><i /></span>
      <span className="gen-visual-spark s1" />
      <span className="gen-visual-spark s2" />
      <span className="gen-visual-spark s3" />

      <div className="gen-paper">
        <div className="gen-paper-head">
          <span className="gen-paper-avatar" />
          <span className="gen-paper-id"><span className="gen-paper-name" /><span className="gen-paper-role" /></span>
        </div>
        {LINES.map((line, i) => (
          <span key={i} className={`gen-paper-line ${line.kind}`} style={{ "--w": `${line.w}%`, "--i": i } as CSSProperties} />
        ))}
        <span className="gen-paper-scan" />
      </div>

      <span className="gen-visual-check"><Check size={20} strokeWidth={3} /></span>
      {phase === "done" &&
        confetti.map((p, i) => (
          <span
            key={i}
            className={`gen-confetti ${p.round ? "round" : ""}`}
            style={{ "--x": `${p.x}px`, "--y": `${p.y}px`, "--r": `${p.rotate}deg`, background: p.color, animationDelay: `${120 + (i % 4) * 30}ms` } as CSSProperties}
          />
        ))}
    </div>
  );
}
