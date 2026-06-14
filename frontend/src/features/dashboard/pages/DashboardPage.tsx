import { Box } from "@mui/material";
import { useAuthStore } from "../../../store/authStore";
import { AdminDashboard } from "./AdminDashboard";
import { ProfessorDashboard } from "./ProfessorDashboard";
import { StudentDashboard } from "./StudentDashboard";

export function DashboardPage() {
  const user = useAuthStore((state) => state.user);

  if (!user) return null;

  return (
    <Box sx={{ maxWidth: 1540, mx: "auto" }}>
      {user.role === "ETUDIANT" && (
        <StudentDashboard firstName={user.prenom} />
      )}
      {user.role === "PROFESSEUR" && (
        <ProfessorDashboard firstName={user.prenom} />
      )}
      {user.role === "ADMIN" && <AdminDashboard firstName={user.prenom} />}
    </Box>
  );
}
