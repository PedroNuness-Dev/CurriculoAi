import { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState, type ReactNode } from "react";
import { catalogApi, type CatalogItem, type CatalogKey } from "../api/endpoints";

type Store = Partial<Record<CatalogKey, CatalogItem[]>>;

interface CatalogState {
  store: Store;
  ensure: (key: CatalogKey) => void;
  refresh: (key: CatalogKey) => Promise<CatalogItem[]>;
  upsert: (key: CatalogKey, item: CatalogItem) => void;
}

const CatalogContext = createContext<CatalogState | null>(null);

/** Cache simples dos catálogos públicos (áreas, cursos, empresas...). */
export function CatalogProvider({ children }: { children: ReactNode }) {
  const [store, setStore] = useState<Store>({});
  const inflight = useRef(new Map<CatalogKey, Promise<CatalogItem[]>>());

  const refresh = useCallback(async (key: CatalogKey) => {
    const p = catalogApi.list(key);
    inflight.current.set(key, p);
    try {
      const items = await p;
      setStore((s) => ({ ...s, [key]: items }));
      return items;
    } finally {
      inflight.current.delete(key);
    }
  }, []);

  const loaded = useRef(new Set<CatalogKey>());
  const ensure = useCallback(
    (key: CatalogKey) => {
      if (loaded.current.has(key) || inflight.current.has(key)) return;
      loaded.current.add(key);
      refresh(key).catch(() => loaded.current.delete(key));
    },
    [refresh],
  );

  const upsert = useCallback((key: CatalogKey, item: CatalogItem) => {
    setStore((s) => {
      const list = s[key] ?? [];
      const exists = list.some((x) => x.id === item.id);
      return { ...s, [key]: exists ? list.map((x) => (x.id === item.id ? item : x)) : [...list, item] };
    });
  }, []);

  const value = useMemo(() => ({ store, ensure, refresh, upsert }), [store, ensure, refresh, upsert]);
  return <CatalogContext.Provider value={value}>{children}</CatalogContext.Provider>;
}

export function useCatalog(key: CatalogKey) {
  const ctx = useContext(CatalogContext);
  if (!ctx) throw new Error("useCatalog precisa estar dentro de <CatalogProvider>");
  const { store, ensure, refresh, upsert } = ctx;
  useEffect(() => ensure(key), [ensure, key]);
  const items = store[key];
  return {
    items: items ?? [],
    loading: items === undefined,
    refresh: () => refresh(key),
    upsert: (item: CatalogItem) => upsert(key, item),
  };
}
