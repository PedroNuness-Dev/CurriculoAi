import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import type { ReactNode } from "react";
import { useAuth } from "./auth/AuthContext";
import { ProfileProvider } from "./auth/ProfileContext";
import AppShell from "./components/layout/AppShell";
import Landing from "./pages/Landing";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Overview from "./pages/Overview";
import AboutYou from "./pages/AboutYou";
import Experiences from "./pages/Experiences";
import Formations from "./pages/Formations";
import Certifications from "./pages/Certifications";
import Projects from "./pages/Projects";
import Skills from "./pages/Skills";
import Languages from "./pages/Languages";
import SocialLinks from "./pages/SocialLinks";
import Generate from "./pages/Generate";
import Catalogs from "./pages/Catalogs";

function RequireAuth({ children }: { children: ReactNode }) {
  const { session } = useAuth();
  const location = useLocation();
  if (!session) return <Navigate to="/entrar" replace state={{ from: location.pathname }} />;
  return <>{children}</>;
}

function GuestOnly({ children }: { children: ReactNode }) {
  const { session } = useAuth();
  if (session) return <Navigate to="/app" replace />;
  return <>{children}</>;
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Landing />} />
      <Route
        path="/entrar"
        element={
          <GuestOnly>
            <Login />
          </GuestOnly>
        }
      />
      <Route
        path="/cadastro"
        element={
          <GuestOnly>
            <Register />
          </GuestOnly>
        }
      />
      <Route
        path="/app"
        element={
          <RequireAuth>
            <ProfileProvider>
              <AppShell />
            </ProfileProvider>
          </RequireAuth>
        }
      >
        <Route index element={<Overview />} />
        <Route path="perfil" element={<AboutYou />} />
        <Route path="experiencias" element={<Experiences />} />
        <Route path="formacao" element={<Formations />} />
        <Route path="certificacoes" element={<Certifications />} />
        <Route path="projetos" element={<Projects />} />
        <Route path="habilidades" element={<Skills />} />
        <Route path="idiomas" element={<Languages />} />
        <Route path="redes" element={<SocialLinks />} />
        <Route path="gerar" element={<Generate />} />
        <Route path="catalogos" element={<Catalogs />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
