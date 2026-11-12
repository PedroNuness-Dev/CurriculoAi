// Tipos espelhando os DTOs do backend Spring (br.com.CurriculoAi.dto.*)

export type ISODate = string; // "YYYY-MM-DD"

export interface ApiErrorBody {
  status: number;
  message: string;
  path?: string;
  timestamp?: string;
  errors?: string[];
}

// ---------- Auth / usuário ----------
export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  expiresAt: string | number;
  userId: number;
  name: string;
  email: string;
}

export interface UserRegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface RoleResponse {
  type: "ROLE_CANDIDATO" | "ROLE_RECRUTADOR";
}

export interface UserResponse {
  userId: number;
  name: string;
  email: string;
  roles: RoleResponse[];
}

// ---------- Catálogos ----------
export interface AreaResponse {
  areaId: number;
  name: string;
}
export interface CourseResponse {
  courseId: number;
  name: string;
}
export interface EnterpriseResponse {
  enterpriseId: number;
  name: string;
}
export interface InstitutionResponse {
  institutionId: number;
  name: string;
}
export interface AbilityResponse {
  abilityId: number;
  name: string;
}
export interface SocialNetworkResponse {
  socialNetworkId: number;
  name: string;
}
export interface LanguageResponse {
  languageId: number;
  name: string; // enum LanguageEnum (ex.: "PORTUGUES")
}

// ---------- Endereço ----------
export interface AddressEntityResponse {
  addressId: number;
  zipCode: string;
  street: string | null;
  complement: string | null;
  unit: string | null;
  neighborhood: string | null;
  city: string | null;
  stateAbbreviation: string | null;
  state: string | null;
  region: string | null;
}

export interface UserAddressResponse {
  userId: number;
  name: string;
  email: string;
  address: AddressEntityResponse;
}

export interface UserAreaResponse {
  userId: number;
  name: string;
  email: string;
  area: AreaResponse;
}

// ---------- Objetivo / disponibilidade ----------
export type GoalType =
  | "PRIMEIRO_EMPREGO"
  | "ESTAGIO"
  | "PROGRESSAO_CARREIRA"
  | "MUDANCA_DE_AREA"
  | "PREPARACAO_MERCADO";

export interface CareerGoalRequest {
  goalType: GoalType;
  areaId: number;
  description: string;
}

export interface CareerGoalResponse {
  careerGoalId: number;
  goalType: GoalType;
  area: AreaResponse | null;
  description: string;
  createdAt: string;
  updatedAt: string;
}

export type WorkModel = "PRESENCIAL" | "REMOTO" | "HIBRIDO";

export interface AvailabilityRequest {
  startAvailability: ISODate | null;
  workModel: WorkModel;
}

export interface AvailabilityResponse {
  availabilityId: number;
  startAvailability: ISODate | null;
  workModel: WorkModel;
}

// ---------- Formação ----------
export interface FormationRequest {
  courseId: number;
  formationType: string;
  startDate: ISODate;
  finishDate: ISODate | null;
}

export interface FormationResponse {
  formationId: number;
  formationType: string;
  startDate: ISODate;
  finishDate: ISODate | null;
  inProgress: boolean;
  course: CourseResponse;
}

// ---------- Experiência ----------
export interface ExperienceRequest {
  title: string;
  description: string;
  areaId: number;
  enterpriseId: number;
  startDate: ISODate;
  position: string;
  finishDate: ISODate | null;
  workCurrent: boolean;
}

export interface ExperienceResponse {
  experienceId: number;
  title: string;
  description: string;
  position: string;
  startDate: ISODate;
  finishDate: ISODate | null;
  workCurrent: boolean;
  area: AreaResponse;
  enterprise: EnterpriseResponse;
}

// ---------- Certificação ----------
export interface CertificationRequest {
  name: string;
  institutionId: number;
  finishDate: ISODate | null;
}

export interface CertificationResponse {
  certificationId: number;
  name: string;
  finishDate: ISODate | null;
  inProgress: boolean;
  institution: InstitutionResponse;
}

// ---------- Projeto ----------
export interface ProjectRequest {
  title: string;
  description: string;
  link: string;
  abilitiesIds: number[];
}

export interface ProjectResponse {
  projectId: number;
  title: string;
  description: string;
  link: string;
  abilities: AbilityResponse[];
}

// ---------- Idiomas / redes / habilidades do usuário ----------
export interface UserLanguageRequest {
  languageId: number;
  languageLevel: string;
}

export interface UserLanguageResponse {
  userLanguageId: number;
  level: string;
  language: LanguageResponse;
}

export interface UserSocialNetworkRequest {
  socialNetworkId: number;
  link: string;
}

export interface UserSocialNetworkResponse {
  userSocialNetworkId: number;
  link: string;
  socialNetwork: SocialNetworkResponse;
}

export interface UserAbilityResponse {
  userId: number;
  name: string;
  email: string;
  abilities: AbilityResponse[];
}

// ---------- Perfil completo ----------
export interface UserFullContent {
  user: UserResponse;
  area: AreaResponse | null;
  address: AddressEntityResponse | null;
  careerGoal: CareerGoalResponse | null;
  availability: AvailabilityResponse | null;
  formations: FormationResponse[];
  experiences: ExperienceResponse[];
  certifications: CertificationResponse[];
  languages: UserLanguageResponse[];
  projects: ProjectResponse[];
  socialNetworks: UserSocialNetworkResponse[];
  abilities: AbilityResponse[];
}
