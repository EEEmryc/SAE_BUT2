import { Box, Button, Paper, Typography } from "@mui/material";
import SchoolRoundedIcon from "@mui/icons-material/SchoolRounded";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../../store/authStore";

export function DashboardPage() {
  const navigate = useNavigate();
  const clearSession = useAuthStore((state) => state.clearSession);

  return (
    <Box
      sx={{
        minHeight: "100vh",
        display: "grid",
        placeItems: "center",
        p: 3,
        bgcolor: "background.default",
      }}
    >
      <Paper sx={{ maxWidth: 560, p: 5, textAlign: "center" }}>
        <SchoolRoundedIcon color="primary" sx={{ fontSize: 58 }} />
        <Typography variant="h4" sx={{ mt: 2, fontWeight: 800 }}>
          Connexion réussie
        </Typography>
        <Typography color="text.secondary" sx={{ my: 2 }}>
          Le socle d’authentification est prêt. Cette page accueillera ensuite
          le tableau de bord adapté au rôle de l’utilisateur.
        </Typography>
        <Button
          variant="outlined"
          onClick={() => {
            clearSession();
            navigate("/login", { replace: true });
          }}
        >
          Se déconnecter
        </Button>
      </Paper>
    </Box>
  );
}
