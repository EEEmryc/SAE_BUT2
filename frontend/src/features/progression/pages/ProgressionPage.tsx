import { Navigate } from "react-router-dom";
import { useAuthStore } from "../../../store/authStore";
import { StudentProgressPage } from "../../student/pages/StudentProgressPage";
import { ProfessorProgressionPage } from "./ProfessorProgressionPage";

export function ProgressionPage() {
  const role = useAuthStore((state) => state.user?.role);

  if (role === "PROFESSEUR") return <ProfessorProgressionPage />;
  if (role === "ETUDIANT") return <StudentProgressPage />;
  return <Navigate to="/dashboard" replace />;
}
