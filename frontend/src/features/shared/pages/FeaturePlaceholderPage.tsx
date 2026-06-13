import { Box, Paper, Typography } from "@mui/material";
import ConstructionRoundedIcon from "@mui/icons-material/ConstructionRounded";

type FeaturePlaceholderPageProps = {
  title: string;
  description: string;
};

export function FeaturePlaceholderPage({
  title,
  description,
}: FeaturePlaceholderPageProps) {
  return (
    <Box sx={{ maxWidth: 1100, mx: "auto" }}>
      <Typography
        component="h1"
        sx={{
          fontSize: { xs: 28, md: 34 },
          fontWeight: 800,
          letterSpacing: "-0.035em",
        }}
      >
        {title}
      </Typography>
      <Typography color="text.secondary" sx={{ mt: 0.7 }}>
        {description}
      </Typography>

      <Paper
        elevation={0}
        sx={{
          mt: 3,
          minHeight: 420,
          display: "grid",
          placeItems: "center",
          textAlign: "center",
          p: 4,
          borderRadius: 3,
          border: "1px solid #e7e9f5",
          boxShadow: "0 18px 45px rgba(59, 67, 125, 0.06)",
        }}
      >
        <Box>
          <Box
            sx={{
              width: 64,
              height: 64,
              mx: "auto",
              display: "grid",
              placeItems: "center",
              borderRadius: 3,
              color: "primary.main",
              bgcolor: "rgba(79,95,247,0.1)",
            }}
          >
            <ConstructionRoundedIcon sx={{ fontSize: 34 }} />
          </Box>
          <Typography sx={{ mt: 2, fontSize: 20, fontWeight: 750 }}>
            Écran prêt à être développé
          </Typography>
          <Typography
            color="text.secondary"
            sx={{ mt: 1, maxWidth: 500 }}
          >
            Le layout, la navigation et les permissions sont déjà branchés.
            Les composants métier et les appels API seront ajoutés ici.
          </Typography>
        </Box>
      </Paper>
    </Box>
  );
}
