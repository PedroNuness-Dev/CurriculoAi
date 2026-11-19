// Rótulos amigáveis para os enums do backend.

export const FORMATION_TYPES: { value: string; label: string; group: string }[] = [
  { value: "ENSINO_FUNDAMENTAL", label: "Ensino Fundamental", group: "Ensino básico" },
  { value: "ENSINO_MEDIO", label: "Ensino Médio", group: "Ensino básico" },
  { value: "ENSINO_MEDIO_TECNICO", label: "Ensino Médio Técnico", group: "Ensino básico" },
  { value: "TECNOLOGO", label: "Tecnólogo", group: "Ensino superior" },
  { value: "GRADUAÇÃO", label: "Graduação", group: "Ensino superior" },
  { value: "BACHARELADO", label: "Bacharelado", group: "Ensino superior" },
  { value: "LICENCIATURA", label: "Licenciatura", group: "Ensino superior" },
  { value: "ESPECIALIZAÇÃO", label: "Especialização", group: "Pós-graduação" },
  { value: "MBA", label: "MBA", group: "Pós-graduação" },
  { value: "RESIDENCIA", label: "Residência", group: "Pós-graduação" },
  { value: "POS_GRADUAÇÃO", label: "Pós-Graduação", group: "Pós-graduação" },
  { value: "MESTRADO", label: "Mestrado", group: "Stricto sensu" },
  { value: "MESTRADO_PROFISSIONAL", label: "Mestrado Profissional", group: "Stricto sensu" },
  { value: "DOUTORADO", label: "Doutorado", group: "Stricto sensu" },
  { value: "POS_DOUTORADO", label: "Pós-Doutorado", group: "Stricto sensu" },
  { value: "CERTIFICACAO_TECNICA", label: "Certificação Técnica", group: "Outros" },
  { value: "CURSO_LIVRE", label: "Curso Livre", group: "Outros" },
  { value: "AUTODIDATA", label: "Autodidata", group: "Outros" },
  { value: "OUTRO", label: "Outro", group: "Outros" },
];

export const formationLabel = (v: string) => FORMATION_TYPES.find((f) => f.value === v)?.label ?? v;

export const LANGUAGE_LEVELS: { value: string; label: string; rank: number }[] = [
  { value: "BASICO", label: "Básico", rank: 1 },
  { value: "INTERMEDIARIO", label: "Intermediário", rank: 2 },
  { value: "AVANCADO", label: "Avançado", rank: 3 },
  { value: "FLUENTE", label: "Fluente", rank: 4 },
  { value: "NATIVO", label: "Nativo", rank: 5 },
];

export const levelLabel = (v: string) => LANGUAGE_LEVELS.find((l) => l.value === v)?.label ?? v;
export const levelRank = (v: string) => LANGUAGE_LEVELS.find((l) => l.value === v)?.rank ?? 0;

export const LANGUAGE_NAMES: Record<string, string> = {
  INGLES: "Inglês",
  MANDARIM: "Mandarim",
  HINDI: "Hindi",
  ESPANHOL: "Espanhol",
  FRANCES: "Francês",
  ARABE: "Árabe",
  BENGALI: "Bengali",
  PORTUGUES: "Português",
  RUSSO: "Russo",
  URDU: "Urdu",
  INDONESIO: "Indonésio",
  ALEMAO: "Alemão",
  JAPONES: "Japonês",
  SUAILI: "Suaíli",
  MARATA: "Marata",
  TELUGU: "Telugu",
  TURCO: "Turco",
  TAMIL: "Tâmil",
  VIETNAMITA: "Vietnamita",
  COREANO: "Coreano",
  PUNJABI: "Punjabi",
  JAVANES: "Javanês",
  ITALIANO: "Italiano",
  PERSA: "Persa",
  GUJARATI: "Gujarati",
  BOJPURI: "Bhojpuri",
  MIN_NAN: "Min Nan",
  HAUCA: "Hauçá",
  CANARES: "Canarês",
  MALAYALAM: "Malaiala",
  ODIA: "Odia",
  BIRMANES: "Birmanês",
  POLONES: "Polonês",
  UCRANIANO: "Ucraniano",
  HOLANDES: "Holandês",
  TAILANDES: "Tailandês",
  TAGALO: "Tagalo",
  IORUBA: "Iorubá",
  AMARICO: "Amárico",
  ROMENO: "Romeno",
  UZBEQUE: "Uzbeque",
  SINDHI: "Sindi",
  IGBO: "Igbo",
  NEPALI: "Nepalês",
  CEBUANO: "Cebuano",
  SERVO_CROATA: "Servo-croata",
  MALGAXE: "Malgaxe",
  SOMALI: "Somali",
  CINGALES: "Cingalês",
};

export const languageName = (v: string) => LANGUAGE_NAMES[v] ?? v;

export const WORK_MODELS: { value: "PRESENCIAL" | "REMOTO" | "HIBRIDO"; label: string; hint: string }[] = [
  { value: "REMOTO", label: "Remoto", hint: "De qualquer lugar" },
  { value: "HIBRIDO", label: "Híbrido", hint: "Parte no escritório" },
  { value: "PRESENCIAL", label: "Presencial", hint: "No escritório" },
];

export const workModelLabel = (v: string) => WORK_MODELS.find((w) => w.value === v)?.label ?? v;

export const GOAL_TYPES: { value: string; label: string; hint: string }[] = [
  { value: "PRIMEIRO_EMPREGO", label: "Primeiro emprego", hint: "Começar a carreira" },
  { value: "ESTAGIO", label: "Estágio", hint: "Ganhar experiência" },
  { value: "PROGRESSAO_CARREIRA", label: "Progressão de carreira", hint: "Subir de nível" },
  { value: "MUDANCA_DE_AREA", label: "Mudança de área", hint: "Recomeçar em outra área" },
  { value: "PREPARACAO_MERCADO", label: "Preparação para o mercado", hint: "Ficar pronto" },
];

export const goalLabel = (v: string) => GOAL_TYPES.find((g) => g.value === v)?.label ?? v;
