import {
  Box,
  Paper,
  Typography,
} from "@mui/material";
import AutoStoriesRoundedIcon from "@mui/icons-material/AutoStoriesRounded";
import TrendingUpRoundedIcon from "@mui/icons-material/TrendingUpRounded";
import ForumRoundedIcon from "@mui/icons-material/ForumRounded";
import PeopleAltRoundedIcon from "@mui/icons-material/PeopleAltRounded";
import BarChartRoundedIcon from "@mui/icons-material/BarChartRounded";
import AdminPanelSettingsRoundedIcon from "@mui/icons-material/AdminPanelSettingsRounded";
import type { ReactNode } from "react";
import { useAuthStore } from "../../../store/authStore";
import type { UserRole } from "../../auth/api/authApi";

type PreviewCard = {
  title: string;
  description: string;
  icon: ReactNode;
};

const cardsByRole: Record<UserRole, PreviewCard[]> = {
  ETUDIANT: [
    {
      title: "Mes cours",
      description: "Retrouvez les cours auxquels vous êtes inscrit.",
      icon: <AutoStoriesRoundedIcon />,
    },
    {
      title: "Ma progression",
      description: "Suivez votre avancement chapitre par chapitre.",
      icon: <TrendingUpRoundedIcon />,
    },
    {
      title: "Messagerie",
      description: "Échangez avec vos enseignants.",
      icon: <ForumRoundedIcon />,
    },
  ],
  PROFESSEUR: [
    {
      title: "Mes cours",
      description: "Créez et organisez vos contenus pédagogiques.",
      icon: <AutoStoriesRoundedIcon />,
    },
    {
      title: "Mes étudiants",
      description: "Consultez les inscriptions et les progressions.",
      icon: <PeopleAltRoundedIcon />,
    },
    {
      title: "Messagerie",
      description: "Répondez aux questions de vos étudiants.",
      icon: <ForumRoundedIcon />,
    },
  ],
  ADMIN: [
    {
      title: "Utilisateurs",
      description: "Gérez les comptes et leurs autorisations.",
      icon: <PeopleAltRoundedIcon />,
    },
    {
      title: "Statistiques",
      description: "Préparez les futurs indicateurs de la plateforme.",
      icon: <BarChartRoundedIcon />,
    },
    {
      title: "Administration",
      description: "Supervisez la configuration de LearnHub.",
      icon: <AdminPanelSettingsRoundedIcon />,
    },
  ],
};

export function DashboardPage() {
  const user = useAuthStore((state) => state.user);

  if (!user) {
    return null;
  }

  return (
    <Box sx={{ maxWidth: 1240, mx: "auto" }}>
      <Box sx={{ mb: 3.5 }}>
        <Typography
          component="h1"
          sx={{
            fontSize: { xs: 28, md: 34 },
            fontWeight: 800,
            letterSpacing: "-0.035em",
          }}
        >
          Bonjour, {user.prenom} !
        </Typography>
        <Typography color="text.secondary" sx={{ mt: 0.7 }}>
          Voici votre espace LearnHub. Les indicateurs détaillés seront ajoutés
          dans une prochaine étape.
        </Typography>
      </Box>

      <Box
        sx={{
          display: "grid",
          gridTemplateColumns: {
            xs: "1fr",
            sm: "repeat(2, minmax(0, 1fr))",
            lg: "repeat(3, minmax(0, 1fr))",
          },
          gap: 2,
        }}
      >
        {cardsByRole[user.role].map((card, index) => (
          <Paper
            key={card.title}
            elevation={0}
            sx={{
              p: 2.5,
              border: "1px solid #e8eaf5",
              borderRadius: 3,
              background:
                index === 0
                  ? "linear-gradient(135deg, #ffffff, #f4f5ff)"
                  : "#fff",
              boxShadow: "0 12px 35px rgba(59, 67, 125, 0.06)",
            }}
          >
            <Box
              sx={{
                width: 44,
                height: 44,
                display: "grid",
                placeItems: "center",
                mb: 2,
                borderRadius: 2.5,
                color: "primary.main",
                bgcolor: "rgba(79,95,247,0.1)",
              }}
            >
              {card.icon}
            </Box>
            <Typography sx={{ fontWeight: 750, fontSize: 17 }}>
              {card.title}
            </Typography>
            <Typography
              color="text.secondary"
              sx={{ mt: 0.7, fontSize: 14, lineHeight: 1.55 }}
            >
              {card.description}
            </Typography>
          </Paper>
        ))}
      </Box>

      <Paper
        elevation={0}
        sx={{
          mt: 2.5,
          minHeight: 300,
          p: { xs: 2.5, md: 3.5 },
          display: "grid",
          placeItems: "center",
          textAlign: "center",
          border: "1px dashed #cfd4ef",
          borderRadius: 3,
          background:
            "linear-gradient(135deg, rgba(255,255,255,0.95), rgba(242,244,255,0.88))",
        }}
      >
        <Box>
          <Typography sx={{ fontSize: 20, fontWeight: 750 }}>
            Tableau de bord en préparation
          </Typography>
          <Typography
            color="text.secondary"
            sx={{ mt: 1, maxWidth: 520 }}
          >
            Cette zone accueillera les statistiques, graphiques et activités
            récentes propres à votre rôle.
          </Typography>
        </Box>
      </Paper>
    </Box>
  );
}
