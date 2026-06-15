import { Box, Typography } from "@mui/material";
import { AdminProfileCard } from "../components/AdminProfileCard";
import { InscriptionValidationCard } from "../components/InscriptionValidationCard";
import { RequestableRolesCard } from "../components/RequestableRolesCard";

export function AdminSettingsPage() {
  return (
    <Box sx={{ maxWidth: 1440, mx: "auto" }}>
      <Typography
        component="h1"
        sx={{
          fontSize: { xs: 29, sm: 38 },
          fontWeight: 850,
          letterSpacing: "-0.04em",
        }}
      >
        Paramètres
      </Typography>
      <Typography color="text.secondary" sx={{ mt: 0.5 }}>
        Configurez les paramètres d'administration de la plateforme.
      </Typography>

      <Box
        sx={{
          mt: 3,
          display: "grid",
          gap: 2.5,
        }}
      >
        <RequestableRolesCard />
        <InscriptionValidationCard />
        <AdminProfileCard />
      </Box>
    </Box>
  );
}
