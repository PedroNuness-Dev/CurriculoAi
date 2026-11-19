import { useEffect, useState } from "react";
import { useAuth } from "../../auth/AuthContext";
import { Symbol, Wordmark } from "../brand/Brand";
import "../../styles/intro.css";

const STORAGE_KEY = "curriculoai.intro-seen";

// Ondas que saem de trás do símbolo; a última (gradiente) cobre a tela inteira.
const RINGS = ["#55B5FF", "#FFFFFF", "#0A72F9", "#FFFFFF", "var(--grad-diag)"];

/** Identifica o login atual: um novo login (outro vencimento do token) mostra a abertura de novo. */
function loginId(userId?: number, expiresAt?: number) {
  return userId && expiresAt ? `${userId}:${expiresAt}` : null;
}

function shouldShow(id: string | null) {
  if (!id) return false;
  if (window.matchMedia?.("(prefers-reduced-motion: reduce)").matches) return false;
  try {
    return sessionStorage.getItem(STORAGE_KEY) !== id;
  } catch {
    return false;
  }
}

/**
 * Abertura animada exibida ao entrar no app (uma vez por login).
 * Puramente decorativa: clique ou Esc pulam, e quem prefere menos movimento não a vê.
 */
export default function AppIntro() {
  const { session } = useAuth();
  const id = loginId(session?.userId, session?.expiresAt);
  const [visible, setVisible] = useState(() => shouldShow(id));
  const [leaving, setLeaving] = useState(false);

  useEffect(() => {
    if (!visible || !id) return;
    try {
      sessionStorage.setItem(STORAGE_KEY, id);
    } catch {
      /* storage indisponível: a abertura só não fica registrada */
    }
    const skip = (event: KeyboardEvent) => {
      if (event.key === "Escape" || event.key === "Enter" || event.key === " ") setLeaving(true);
    };
    // Garantia caso o evento de fim da animação não dispare (aba em segundo plano, por exemplo).
    const fallback = window.setTimeout(() => setVisible(false), 4000);
    document.addEventListener("keydown", skip);
    return () => {
      document.removeEventListener("keydown", skip);
      window.clearTimeout(fallback);
    };
  }, [visible, id]);

  if (!visible) return null;

  return (
    <div
      className={`app-intro ${leaving ? "is-leaving" : ""}`}
      aria-hidden="true"
      onClick={() => setLeaving(true)}
      onAnimationEnd={(event) => {
        if (event.target === event.currentTarget && event.animationName.startsWith("intro-exit")) setVisible(false);
      }}
    >
      {RINGS.map((color, i) => (
        <span key={i} className="app-intro-ring" style={{ background: color, ["--i" as string]: i }} />
      ))}
      <div className="app-intro-mark">
        <span className="app-intro-badge">
          <Symbol size={78} />
        </span>
        <span className="app-intro-word">
          <Wordmark height={36} tone="white" />
        </span>
        <span className="app-intro-tagline">Seu espaço profissional</span>
      </div>
    </div>
  );
}
