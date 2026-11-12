import { useId } from "react";
import { SYMBOL_PATH, SYMBOL_VIEWBOX, WORDMARK_PATHS, WORDMARK_VIEWBOX } from "./paths";

type Tone = "gradient" | "white" | "ink";

function GradientDef({ id, width }: { id: string; width: number }) {
  return (
    <defs>
      <linearGradient id={id} x1="0" y1="0" x2={width} y2="0" gradientUnits="userSpaceOnUse">
        <stop offset="0" stopColor="#0647C8" />
        <stop offset="0.5" stopColor="#0A72F9" />
        <stop offset="1" stopColor="#55B5FF" />
      </linearGradient>
    </defs>
  );
}

const fillFor = (tone: Tone, gid: string) =>
  tone === "gradient" ? `url(#${gid})` : tone === "white" ? "#fff" : "var(--ink)";

/** Símbolo "C" da marca. */
export function Symbol({ size = 32, tone = "gradient", className }: { size?: number; tone?: Tone; className?: string }) {
  const gid = useId().replace(/:/g, "");
  return (
    <svg
      viewBox={SYMBOL_VIEWBOX}
      width={size}
      height={(size * 477) / 610}
      className={className}
      aria-hidden="true"
      focusable="false"
    >
      {tone === "gradient" && <GradientDef id={gid} width={610} />}
      <path d={SYMBOL_PATH} fill={fillFor(tone, gid)} />
    </svg>
  );
}

/** Símbolo apenas em contorno — elemento gráfico usado no manual (posts e capa). */
export function SymbolOutline({
  className,
  strokeWidth = 6,
  style,
}: {
  className?: string;
  strokeWidth?: number;
  style?: React.CSSProperties;
}) {
  const gid = useId().replace(/:/g, "");
  return (
    <svg viewBox="-10 -10 630 497" className={className} style={style} aria-hidden="true" focusable="false">
      <defs>
        <linearGradient id={gid} x1="0" y1="0" x2="610" y2="477" gradientUnits="userSpaceOnUse">
          <stop offset="0" stopColor="#0A72F9" />
          <stop offset="0.55" stopColor="#3A9CFD" />
          <stop offset="1" stopColor="#B8DEFF" stopOpacity="0.7" />
        </linearGradient>
      </defs>
      <path d={SYMBOL_PATH} fill="none" stroke={`url(#${gid})`} strokeWidth={strokeWidth} strokeLinejoin="round" />
    </svg>
  );
}

/** Logotipo "CurrículoAI". */
export function Wordmark({ height = 22, tone = "gradient", className }: { height?: number; tone?: Tone; className?: string }) {
  const gid = useId().replace(/:/g, "");
  return (
    <svg
      viewBox={WORDMARK_VIEWBOX}
      height={height}
      width={(height * 855) / 92}
      className={className}
      role="img"
      aria-label="CurrículoAI"
    >
      {tone === "gradient" && <GradientDef id={gid} width={855} />}
      {WORDMARK_PATHS.map((d, i) => (
        <path key={i} d={d} fill={fillFor(tone, gid)} />
      ))}
    </svg>
  );
}

/** Ícone de app: símbolo branco sobre quadrado com gradiente. */
export function AppIcon({ size = 40, radius }: { size?: number; radius?: number }) {
  return (
    <span
      style={{
        width: size,
        height: size,
        borderRadius: radius ?? size * 0.28,
        background: "var(--grad-diag)",
        display: "inline-grid",
        placeItems: "center",
        flex: "none",
        boxShadow: "0 10px 22px -12px rgba(10,114,249,.9)",
      }}
    >
      <Symbol size={size * 0.6} tone="white" />
    </span>
  );
}
