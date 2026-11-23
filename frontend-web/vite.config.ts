import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";

// O front chama sempre "/api/..." e o Vite repassa para o backend Spring.
// Assim não há problema de CORS em desenvolvimento.
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, ".", "");
  const target = env.VITE_API_TARGET || "http://localhost:8080";

  return {
    plugins: [react()],
    server: {
      port: 5173,
      proxy: {
        "/api": { target, changeOrigin: true },
      },
    },
    preview: {
      port: 4173,
      proxy: {
        "/api": { target, changeOrigin: true },
      },
    },
  };
});
