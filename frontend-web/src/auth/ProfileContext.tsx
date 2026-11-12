import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { userApi } from "../api/endpoints";
import type { UserFullContent } from "../api/types";
import { errorMessage } from "../api/client";

interface ProfileState {
  profile: UserFullContent | null;
  loading: boolean;
  error: string | null;
  reload: () => Promise<void>;
}

const ProfileContext = createContext<ProfileState | null>(null);

/** Carrega GET /api/users/me/profile — a fonte única dos dados do currículo. */
export function ProfileProvider({ children }: { children: ReactNode }) {
  const [profile, setProfile] = useState<UserFullContent | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const reload = useCallback(async () => {
    try {
      const data = await userApi.profile();
      setProfile(data);
      setError(null);
    } catch (err) {
      setError(errorMessage(err).title);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void reload();
  }, [reload]);

  const value = useMemo(() => ({ profile, loading, error, reload }), [profile, loading, error, reload]);
  return <ProfileContext.Provider value={value}>{children}</ProfileContext.Provider>;
}

export function useProfile() {
  const ctx = useContext(ProfileContext);
  if (!ctx) throw new Error("useProfile precisa estar dentro de <ProfileProvider>");
  return ctx;
}

/** Calcula o quanto do perfil já foi preenchido. */
export function completeness(p: UserFullContent | null) {
  const checks = [
    { key: "area", label: "Área de atuação", done: !!p?.area, to: "/app/perfil" },
    { key: "goal", label: "Objetivo de carreira", done: !!p?.careerGoal, to: "/app/perfil" },
    { key: "address", label: "Endereço", done: !!p?.address, to: "/app/perfil" },
    { key: "availability", label: "Disponibilidade", done: !!p?.availability, to: "/app/perfil" },
    { key: "experiences", label: "Experiências", done: (p?.experiences.length ?? 0) > 0, to: "/app/experiencias" },
    { key: "formations", label: "Formação", done: (p?.formations.length ?? 0) > 0, to: "/app/formacao" },
    { key: "abilities", label: "Habilidades", done: (p?.abilities.length ?? 0) > 0, to: "/app/habilidades" },
    { key: "languages", label: "Idiomas", done: (p?.languages.length ?? 0) > 0, to: "/app/idiomas" },
    { key: "certifications", label: "Certificações", done: (p?.certifications.length ?? 0) > 0, to: "/app/certificacoes" },
    { key: "projects", label: "Projetos", done: (p?.projects.length ?? 0) > 0, to: "/app/projetos" },
    { key: "social", label: "Redes e links", done: (p?.socialNetworks.length ?? 0) > 0, to: "/app/redes" },
  ];
  const done = checks.filter((c) => c.done).length;
  return { checks, done, total: checks.length, percent: Math.round((done / checks.length) * 100) };
}
