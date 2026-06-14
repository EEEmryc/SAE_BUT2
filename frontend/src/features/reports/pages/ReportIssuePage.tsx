import { Box, Paper, Typography } from "@mui/material";
import { ReportIssueForm } from "../components/ReportIssueForm";

export function ReportIssuePage() {
  return (
    <Box sx={{ maxWidth: 760, mx: "auto" }}>
      <Typography
        component="h1"
        sx={{
          fontSize: { xs: 30, sm: 38 },
          fontWeight: 850,
          letterSpacing: "-0.04em",
        }}
      >
        Signaler un problème
      </Typography>
      <Typography color="text.secondary" sx={{ mt: 0.5 }}>
        Décrivez un problème rencontré sur la plateforme. Votre signalement sera
        transmis à l'équipe d'administration.
      </Typography>

      <Paper
        elevation={0}
        sx={{
          mt: 3,
          p: { xs: 2.5, sm: 3.5 },
          border: "1px solid #e3e7f3",
          borderRadius: 3.5,
          boxShadow: "0 12px 34px rgba(54,64,125,0.05)",
        }}
      >
        <ReportIssueForm />
      </Paper>
    </Box>
  );
}