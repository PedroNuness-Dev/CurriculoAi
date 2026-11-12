import type { ReactNode } from "react";
import { Link } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import { Symbol, Wordmark } from "../brand/Brand";

export default function AuthLayout({ children }: { children: ReactNode }) {
  return (
    <div className="auth">
      <section className="auth-brand">
        <Symbol className="auth-brand-symbol" tone="white" size={900} />
        <Link to="/" aria-label="Voltar para o início">
          <Wordmark height={24} tone="white" />
        </Link>

        <div>
          <p style={{ fontWeight: 700, fontSize: 20, opacity: 0.95 }}>
            Nova identidade.
            <br />A mesma missão:
          </p>
          <h2 className="display" style={{ marginTop: 14 }}>
            Conectar
            <br />
            você ao
            <br />
            mercado.
          </h2>
          <p className="auth-brand-sub">
            Preencha sua trajetória uma vez. A IA transforma tudo em um currículo pronto para os filtros de recrutamento.
          </p>
        </div>

        <div className="auth-quote">
          <strong>88%</strong>
          <span>dos empregadores admitem que filtros automáticos descartam candidatos qualificados.</span>
        </div>
      </section>

      <section className="auth-panel">
        <Link to="/" className="btn btn-ghost btn-sm" style={{ alignSelf: "flex-start" }}>
          <ArrowLeft size={16} /> Início
        </Link>
        <div className="auth-form-wrap">{children}</div>
      </section>
    </div>
  );
}
