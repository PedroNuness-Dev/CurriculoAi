const MONTHS = ["jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez"];

/** "2024-03-10" → "mar 2024" */
export function monthYear(iso: string | null | undefined): string {
  if (!iso) return "";
  const [y, m] = iso.split("-");
  const idx = Number(m) - 1;
  return `${MONTHS[idx] ?? m} ${y}`;
}

/** "2024-03-10" → "10/03/2024" */
export function fullDate(iso: string | null | undefined): string {
  if (!iso) return "";
  const [y, m, d] = iso.slice(0, 10).split("-");
  return `${d}/${m}/${y}`;
}

export function period(start: string | null | undefined, end: string | null | undefined, currentLabel = "atual") {
  const s = monthYear(start);
  const e = end ? monthYear(end) : currentLabel;
  return s ? `${s} — ${e}` : e;
}

/** Duração aproximada entre duas datas: "2 anos e 3 meses" */
export function duration(start: string | null | undefined, end: string | null | undefined): string {
  if (!start) return "";
  const a = new Date(start + "T00:00:00");
  const b = end ? new Date(end + "T00:00:00") : new Date();
  let months = (b.getFullYear() - a.getFullYear()) * 12 + (b.getMonth() - a.getMonth());
  if (months < 1) months = 1;
  const y = Math.floor(months / 12);
  const m = months % 12;
  const parts: string[] = [];
  if (y) parts.push(`${y} ${y === 1 ? "ano" : "anos"}`);
  if (m) parts.push(`${m} ${m === 1 ? "mês" : "meses"}`);
  return parts.join(" e ");
}

/** true se a data ainda não chegou (ex.: conclusão prevista). */
export function isFuture(iso: string | null | undefined) {
  return !!iso && iso.slice(0, 10) > todayIso();
}

export function onlyDigits(v: string) {
  return v.replace(/\D/g, "");
}

export function maskCep(v: string) {
  const d = onlyDigits(v).slice(0, 8);
  return d.length > 5 ? `${d.slice(0, 5)}-${d.slice(5)}` : d;
}

export function firstName(name: string | undefined | null) {
  return (name ?? "").trim().split(/\s+/)[0] ?? "";
}

export function initials(name: string | undefined | null) {
  const parts = (name ?? "").trim().split(/\s+/).filter(Boolean);
  if (!parts.length) return "?";
  return ((parts[0][0] ?? "") + (parts.length > 1 ? parts[parts.length - 1][0] : "")).toUpperCase();
}

export function todayIso() {
  const d = new Date();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  return `${d.getFullYear()}-${mm}-${dd}`;
}

export function prettyUrl(url: string) {
  return url.replace(/^https?:\/\//, "").replace(/^www\./, "").replace(/\/$/, "");
}

export function ensureProtocol(url: string) {
  const t = url.trim();
  if (!t) return t;
  return /^https?:\/\//i.test(t) ? t : `https://${t}`;
}
