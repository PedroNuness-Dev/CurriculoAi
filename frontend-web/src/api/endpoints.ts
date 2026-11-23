import { request } from "./client";
import type {
  AbilityResponse,
  AreaResponse,
  AvailabilityRequest,
  AvailabilityResponse,
  CareerGoalRequest,
  CareerGoalResponse,
  CertificationRequest,
  CertificationResponse,
  CourseResponse,
  EnterpriseResponse,
  ExperienceRequest,
  ExperienceResponse,
  FormationRequest,
  FormationResponse,
  InstitutionResponse,
  LanguageResponse,
  LoginRequest,
  LoginResponse,
  ProjectRequest,
  ProjectResponse,
  SocialNetworkResponse,
  UserAbilityResponse,
  UserAddressResponse,
  UserAreaResponse,
  UserFullContent,
  UserLanguageRequest,
  UserLanguageResponse,
  UserRegisterRequest,
  UserResponse,
  UserSocialNetworkRequest,
  UserSocialNetworkResponse,
} from "./types";

// ---------------------------------------------------------------------------
// Auth & usuário
// ---------------------------------------------------------------------------
export const authApi = {
  login: (body: LoginRequest) => request<LoginResponse>("/api/auth/login", { method: "POST", body, auth: false }),
  register: (body: UserRegisterRequest) =>
    request<UserResponse>("/api/users", { method: "POST", body, auth: false }),
};

export const userApi = {
  profile: () => request<UserFullContent>("/api/users/me/profile"),
  setArea: (areaId: number, existing = false) => request<UserAreaResponse>(`/api/users/area/${areaId}`, { method: existing ? "PUT" : "POST" }),
  setAddress: (zipCode: string, existing = false) => request<UserAddressResponse>(`/api/users/address/${zipCode}`, { method: existing ? "PUT" : "POST" }),
  abilities: () => request<UserAbilityResponse>("/api/users/abilities"),
  setAbilities: async (ids: number[]): Promise<UserAbilityResponse | null> => {
    if (ids.length) return request<UserAbilityResponse>("/api/users/abilities", { method: "PUT", body: ids });
    await request<void>("/api/users/abilities", { method: "DELETE" });
    return null;
  },
  addLanguages: (body: UserLanguageRequest[]) =>
    request<UserLanguageResponse[]>("/api/users/languages", { method: "POST", body }),
  updateLanguage: (id: number, body: UserLanguageRequest) => request<UserLanguageResponse>(`/api/users/languages/${id}`, { method: "PUT", body }),
  deleteLanguage: (id: number) => request<void>(`/api/users/languages/${id}`, { method: "DELETE" }),
  addSocialNetworks: (body: UserSocialNetworkRequest[]) =>
    request<UserSocialNetworkResponse[]>("/api/users/social-networks", { method: "POST", body }),
  updateSocialNetwork: (id: number, body: UserSocialNetworkRequest) => request<UserSocialNetworkResponse>(`/api/users/social-networks/${id}`, { method: "PUT", body }),
  deleteSocialNetwork: (id: number) => request<void>(`/api/users/social-networks/${id}`, { method: "DELETE" }),
};

// ---------------------------------------------------------------------------
// Dados do currículo
// ---------------------------------------------------------------------------
export const resumeApi = {
  addExperiences: (body: ExperienceRequest[]) =>
    request<ExperienceResponse[]>("/api/experiences", { method: "POST", body }),
  updateExperience: (id: number, body: ExperienceRequest) => request<ExperienceResponse>(`/api/experiences/${id}`, { method: "PUT", body }),
  deleteExperience: (id: number) => request<void>(`/api/experiences/${id}`, { method: "DELETE" }),
  addFormations: (body: FormationRequest[]) =>
    request<FormationResponse[]>("/api/formations", { method: "POST", body }),
  updateFormation: (id: number, body: FormationRequest) => request<FormationResponse>(`/api/formations/${id}`, { method: "PUT", body }),
  deleteFormation: (id: number) => request<void>(`/api/formations/${id}`, { method: "DELETE" }),
  listFormations: () => request<FormationResponse[]>("/api/formations"),
  getFormation: (id: number) => request<FormationResponse>(`/api/formations/${id}`),
  addCertifications: (body: CertificationRequest[]) =>
    request<CertificationResponse[]>("/api/certifications", { method: "POST", body }),
  updateCertification: (id: number, body: CertificationRequest) => request<CertificationResponse>(`/api/certifications/${id}`, { method: "PUT", body }),
  deleteCertification: (id: number) => request<void>(`/api/certifications/${id}`, { method: "DELETE" }),
  addProjects: (body: ProjectRequest[]) => request<ProjectResponse[]>("/api/projects", { method: "POST", body }),
  updateProject: (id: number, body: ProjectRequest) => request<ProjectResponse>(`/api/projects/${id}`, { method: "PUT", body }),
  deleteProject: (id: number) => request<void>(`/api/projects/${id}`, { method: "DELETE" }),
  setAvailability: (body: AvailabilityRequest, existing = false) =>
    request<AvailabilityResponse>("/api/availabilities", { method: existing ? "PUT" : "POST", body }),
  setCareerGoal: (body: CareerGoalRequest, existing = false) =>
    request<CareerGoalResponse>("/api/career-goal", { method: existing ? "PUT" : "POST", body }),
  studyProfile: (signal?: AbortSignal) => request<Array<{ skill: string; evidence: string; interpretation: string; question: string }>>("/api/cerebras/profile/study", { method: "POST", signal }),
  generatePdf: (signal?: AbortSignal, jobDescription?: string, answers?: string) =>
    request<Blob>("/api/cerebras/curriculum", { method: "POST", responseType: "blob", signal, body: { jobDescription, answers } }),
};

// ---------------------------------------------------------------------------
// Catálogos (GET all / GET by id / POST / PUT)
// ---------------------------------------------------------------------------
export interface CatalogItem {
  id: number;
  name: string;
  category?: "HARD" | "SOFT";
}

interface CatalogDef<T> {
  key: CatalogKey;
  base: string;
  createPath: string;
  idOf: (item: T) => number;
}

export type CatalogKey =
  | "areas"
  | "courses"
  | "enterprises"
  | "institutions"
  | "abilities"
  | "socialNetworks"
  | "languages";

const defs: { [K in CatalogKey]: CatalogDef<any> } = {
  areas: { key: "areas", base: "/api/areas", createPath: "/api/areas/create", idOf: (i: AreaResponse) => i.areaId },
  courses: { key: "courses", base: "/api/courses", createPath: "/api/courses", idOf: (i: CourseResponse) => i.courseId },
  enterprises: {
    key: "enterprises",
    base: "/api/enterprises",
    createPath: "/api/enterprises",
    idOf: (i: EnterpriseResponse) => i.enterpriseId,
  },
  institutions: {
    key: "institutions",
    base: "/api/institutions",
    createPath: "/api/institutions",
    idOf: (i: InstitutionResponse) => i.institutionId,
  },
  abilities: {
    key: "abilities",
    base: "/api/abilities",
    createPath: "/api/abilities",
    idOf: (i: AbilityResponse) => i.abilityId,
  },
  socialNetworks: {
    key: "socialNetworks",
    base: "/api/social-networks",
    createPath: "/api/social-networks",
    idOf: (i: SocialNetworkResponse) => i.socialNetworkId,
  },
  languages: {
    key: "languages",
    base: "/api/languages",
    createPath: "/api/languages/create",
    idOf: (i: LanguageResponse) => i.languageId,
  },
};

function normalize(key: CatalogKey, raw: any): CatalogItem {
  return { id: defs[key].idOf(raw), name: raw.name, category: raw.category };
}

export const catalogApi = {
  list: async (key: CatalogKey): Promise<CatalogItem[]> => {
    const data = await request<any[]>(defs[key].base);
    return data.map((d) => normalize(key, d));
  },
  get: async (key: CatalogKey, id: number): Promise<CatalogItem> =>
    normalize(key, await request<any>(`${defs[key].base}/${id}`)),
  create: async (key: CatalogKey, name: string, category?: "HARD" | "SOFT"): Promise<CatalogItem> =>
    normalize(key, await request<any>(defs[key].createPath, { method: "POST", body: { name, ...(category ? { category } : {}) } })),
  update: async (key: CatalogKey, id: number, name: string): Promise<CatalogItem> =>
    normalize(key, await request<any>(`${defs[key].base}/${id}`, { method: "PUT", body: { name } })),
};
