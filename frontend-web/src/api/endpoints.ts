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
  setArea: (areaId: number) => request<UserAreaResponse>(`/api/users/area/${areaId}`, { method: "POST" }),
  setAddress: (zipCode: string) => request<UserAddressResponse>(`/api/users/address/${zipCode}`, { method: "POST" }),
  abilities: () => request<UserAbilityResponse>("/api/users/abilities"),
  setAbilities: (ids: number[]) => request<UserAbilityResponse>("/api/users/abilities", { method: "POST", body: ids }),
  addLanguages: (body: UserLanguageRequest[]) =>
    request<UserLanguageResponse[]>("/api/users/languages", { method: "POST", body }),
  addSocialNetworks: (body: UserSocialNetworkRequest[]) =>
    request<UserSocialNetworkResponse[]>("/api/users/social-networks", { method: "POST", body }),
};

// ---------------------------------------------------------------------------
// Dados do currículo
// ---------------------------------------------------------------------------
export const resumeApi = {
  addExperiences: (body: ExperienceRequest[]) =>
    request<ExperienceResponse[]>("/api/experiences", { method: "POST", body }),
  addFormations: (body: FormationRequest[]) =>
    request<FormationResponse[]>("/api/formations", { method: "POST", body }),
  listFormations: () => request<FormationResponse[]>("/api/formations"),
  getFormation: (id: number) => request<FormationResponse>(`/api/formations/${id}`),
  addCertifications: (body: CertificationRequest[]) =>
    request<CertificationResponse[]>("/api/certifications", { method: "POST", body }),
  addProjects: (body: ProjectRequest[]) => request<ProjectResponse[]>("/api/projects", { method: "POST", body }),
  setAvailability: (body: AvailabilityRequest) =>
    request<AvailabilityResponse>("/api/availabilities", { method: "POST", body }),
  setCareerGoal: (body: CareerGoalRequest) =>
    request<CareerGoalResponse>("/api/career-goal", { method: "POST", body }),
  generatePdf: (signal?: AbortSignal) =>
    request<Blob>("/api/cerebras/curriculum", { method: "POST", responseType: "blob", signal }),
};

// ---------------------------------------------------------------------------
// Catálogos (GET all / GET by id / POST / PUT)
// ---------------------------------------------------------------------------
export interface CatalogItem {
  id: number;
  name: string;
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
  return { id: defs[key].idOf(raw), name: raw.name };
}

export const catalogApi = {
  list: async (key: CatalogKey): Promise<CatalogItem[]> => {
    const data = await request<any[]>(defs[key].base, { auth: false });
    return data.map((d) => normalize(key, d));
  },
  get: async (key: CatalogKey, id: number): Promise<CatalogItem> =>
    normalize(key, await request<any>(`${defs[key].base}/${id}`, { auth: false })),
  create: async (key: CatalogKey, name: string): Promise<CatalogItem> =>
    normalize(key, await request<any>(defs[key].createPath, { method: "POST", body: { name }, auth: false })),
  update: async (key: CatalogKey, id: number, name: string): Promise<CatalogItem> =>
    normalize(key, await request<any>(`${defs[key].base}/${id}`, { method: "PUT", body: { name }, auth: false })),
};
