import { Box, Paper, Typography } from "@mui/material";
import { Navigate } from "react-router-dom";
import { useAuthStore } from "../../../store/authStore";
import { BrandMark } from "../components/BrandMark";
import { LoginForm } from "../components/LoginForm";

export function LoginPage() {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <Box className="login-page">
      <Box className="decorative-dots decorative-dots--top" aria-hidden />
      <Box className="decorative-dots decorative-dots--bottom" aria-hidden />

      <Box component="aside" className="login-intro">
        <BrandMark />
        <Typography
          component="h1"
          sx={{
            mt: 6,
            fontSize: { md: 36, xl: 42 },
            lineHeight: 1.25,
            fontWeight: 800,
            letterSpacing: "-0.035em",
          }}
        >
          Apprenez.
          <br />
          Progressez.
          <br />
          <Box component="span" sx={{ color: "primary.main" }}>
            Réalisez vos objectifs.
          </Box>
        </Typography>
        <Typography
          color="text.secondary"
          sx={{ mt: 3, maxWidth: 410, fontSize: 18, lineHeight: 1.65 }}
        >
          Accédez à vos cours, suivez votre progression et développez vos
          compétences.
        </Typography>
      </Box>

      <Paper
        component="main"
        elevation={0}
        className="login-card"
        aria-labelledby="login-title"
      >
        <BrandMark compact />
        <Typography
          id="login-title"
          component="h2"
          sx={{
            mt: 2,
            fontSize: { xs: 30, sm: 34 },
            fontWeight: 800,
            letterSpacing: "-0.035em",
          }}
        >
          Bienvenue !
        </Typography>
        <Typography color="text.secondary" sx={{ mt: 0.5, fontSize: 17 }}>
          Connectez-vous à votre compte
        </Typography>
        <LoginForm />
      </Paper>

      <Typography component="footer" className="login-footer">
        © {new Date().getFullYear()}{" "}
        <Box component="span" sx={{ color: "primary.main", fontWeight: 700 }}>
          LearnHub
        </Box>{" "}
        - Tous droits réservés
      </Typography>
    </Box>
  );
}
