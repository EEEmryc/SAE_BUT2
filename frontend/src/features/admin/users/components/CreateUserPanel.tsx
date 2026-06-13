import {
  Box,
  Divider,
  Paper,
  Typography,
} from "@mui/material";
import AdminPanelSettingsRoundedIcon from "@mui/icons-material/AdminPanelSettingsRounded";
import LightbulbOutlinedIcon from "@mui/icons-material/LightbulbOutlined";
import PersonAddAlt1RoundedIcon from "@mui/icons-material/PersonAddAlt1Rounded";
import SchoolRoundedIcon from "@mui/icons-material/SchoolRounded";
import SecurityRoundedIcon from "@mui/icons-material/SecurityRounded";
import { CreateUserForm } from "./CreateUserForm";

const tips = [
  {
    icon: <AdminPanelSettingsRoundedIcon />,
    title: "Administrateur",
    text: "accès complet à la plateforme.",
  },
  {
    icon: <SchoolRoundedIcon />,
    title: "Professeur",
    text: "crée et gère ses cours.",
  },
  {
    icon: <PersonAddAlt1RoundedIcon />,
    title: "Étudiant",
    text: "suit les cours et sa progression.",
  },
];

type CreateUserPanelProps = {
  onCancel: () => void;
};

export function CreateUserPanel({ onCancel }: CreateUserPanelProps) {
  return (
    <Box
      sx={{
        display: "grid",
        gridTemplateColumns: { xs: "1fr", lg: "minmax(0, 1fr) 300px" },
        gap: 3,
        alignItems: "start",
      }}
    >
      <Paper
        elevation={0}
        sx={{
          p: { xs: 2.25, sm: 3.5 },
          border: "1px solid #e5e8f4",
          borderRadius: 3.5,
          boxShadow: "0 18px 50px rgba(54, 64, 125, 0.08)",
        }}
      >
        <Box sx={{ display: "flex", alignItems: "center", gap: 1.5, mb: 3 }}>
          <Box
            sx={{
              width: 52,
              height: 52,
              display: "grid",
              placeItems: "center",
              borderRadius: "50%",
              color: "primary.main",
              bgcolor: "rgba(79,95,247,0.1)",
            }}
          >
            <PersonAddAlt1RoundedIcon />
          </Box>
          <Box>
            <Typography sx={{ fontSize: 19, fontWeight: 800 }}>
              Informations de l&apos;utilisateur
            </Typography>
            <Typography color="text.secondary" sx={{ fontSize: 14 }}>
              Renseignez les informations nécessaires à la création du compte.
            </Typography>
          </Box>
        </Box>
        <CreateUserForm onCancel={onCancel} />
      </Paper>

      <Paper
        component="aside"
        elevation={0}
        sx={{
          p: 3,
          border: "1px solid #e5e8f4",
          borderRadius: 3.5,
          boxShadow: "0 18px 50px rgba(54, 64, 125, 0.07)",
        }}
      >
        <Box sx={{ display: "flex", alignItems: "center", gap: 1.25 }}>
          <Box
            sx={{
              width: 42,
              height: 42,
              display: "grid",
              placeItems: "center",
              borderRadius: "50%",
              color: "#df8b13",
              bgcolor: "#fff5e3",
            }}
          >
            <LightbulbOutlinedIcon />
          </Box>
          <Typography sx={{ fontSize: 18, fontWeight: 800 }}>
            Conseils
          </Typography>
        </Box>

        <Typography color="text.secondary" sx={{ mt: 2, lineHeight: 1.65 }}>
          Vérifiez l&apos;adresse email : elle sera utilisée pour accéder à la
          plateforme et recevoir le lien de première connexion.
        </Typography>

        <Divider sx={{ my: 2.5 }} />

        {tips.map((tip) => (
          <Box
            key={tip.title}
            sx={{ display: "flex", gap: 1.4, alignItems: "flex-start", mb: 2 }}
          >
            <Box
              sx={{
                width: 38,
                height: 38,
                flex: "0 0 auto",
                display: "grid",
                placeItems: "center",
                borderRadius: "50%",
                color: "primary.main",
                bgcolor: "rgba(79,95,247,0.09)",
                "& svg": { fontSize: 20 },
              }}
            >
              {tip.icon}
            </Box>
            <Typography color="text.secondary" sx={{ lineHeight: 1.55 }}>
              <Box component="span" sx={{ color: "text.primary", fontWeight: 750 }}>
                {tip.title}
              </Box>
              {" : "}
              {tip.text}
            </Typography>
          </Box>
        ))}

        <Box sx={{ display: "flex", gap: 1.3, mt: 2.5 }}>
          <SecurityRoundedIcon color="primary" />
          <Typography color="text.secondary" sx={{ lineHeight: 1.55 }}>
            Un compte inactif ne peut pas se connecter tant qu&apos;un
            administrateur ne l&apos;active pas.
          </Typography>
        </Box>
      </Paper>
    </Box>
  );
}
