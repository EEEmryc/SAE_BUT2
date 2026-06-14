import {
  Box,
  Button,
  Chip,
  Paper,
  Typography,
} from "@mui/material";
import ArrowForwardRoundedIcon from "@mui/icons-material/ArrowForwardRounded";
import AutoStoriesRoundedIcon from "@mui/icons-material/AutoStoriesRounded";
import FolderOpenRoundedIcon from "@mui/icons-material/FolderOpenRounded";
import PersonOutlineRoundedIcon from "@mui/icons-material/PersonOutlineRounded";
import type { CatalogCourse } from "../api/studentLearningApi";

type StudentCourseCardProps = {
  course: CatalogCourse;
  index: number;
  enrolling: boolean;
  onConsult: () => void;
  onEnroll: () => void;
};

const gradients = [
  "linear-gradient(135deg,#3049a2,#5265e8)",
  "linear-gradient(135deg,#6557dc,#9386ff)",
  "linear-gradient(135deg,#2ba36d,#79d6a0)",
  "linear-gradient(135deg,#e28b2e,#ffc46f)",
];

export function StudentCourseCard({
  course,
  index,
  enrolling,
  onConsult,
  onEnroll,
}: StudentCourseCardProps) {
  const enrolled = course.statutInscription === "VALIDE";
  const pending = course.statutInscription === "EN_ATTENTE";

  return (
    <Paper
      elevation={0}
      sx={{
        overflow: "hidden",
        display: "flex",
        flexDirection: "column",
        border: "1px solid #e0e5f3",
        borderRadius: 3,
        boxShadow: "0 14px 34px rgba(49,61,125,.07)",
      }}
    >
      <Box
        sx={{
          height: 88,
          px: 2.25,
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          color: "#fff",
          background: gradients[index % gradients.length],
        }}
      >
        <AutoStoriesRoundedIcon sx={{ fontSize: 42, opacity: 0.92 }} />
        <Chip
          size="small"
          label={enrolled ? "Inscrit" : pending ? "En attente" : "Ouvert"}
          sx={{
            color: enrolled ? "#14794a" : pending ? "#a35d0a" : "#4556df",
            bgcolor: enrolled ? "#e3f7eb" : pending ? "#fff0d7" : "#eef0ff",
            fontWeight: 800,
          }}
        />
      </Box>
      <Box sx={{ p: 2.1, display: "flex", flex: 1, flexDirection: "column" }}>
        <Typography sx={{ fontSize: 18, fontWeight: 850, lineHeight: 1.2 }}>
          {course.titre}
        </Typography>
        <Typography
          color="text.secondary"
          sx={{
            mt: 0.75,
            minHeight: 42,
            fontSize: 13,
            display: "-webkit-box",
            overflow: "hidden",
            WebkitBoxOrient: "vertical",
            WebkitLineClamp: 2,
          }}
        >
          {course.description}
        </Typography>
        <Box sx={{ mt: 1.5, display: "flex", alignItems: "center", gap: 0.6 }}>
          <PersonOutlineRoundedIcon sx={{ fontSize: 17, color: "#67728d" }} />
          <Typography color="text.secondary" sx={{ fontSize: 12.5 }}>
            {course.profPrenom} {course.profNom}
          </Typography>
        </Box>
        <Box sx={{ mt: 1.25, display: "flex", gap: 2 }}>
          <Typography color="text.secondary" sx={{ fontSize: 12 }}>
            <AutoStoriesRoundedIcon sx={{ mr: 0.5, fontSize: 15, verticalAlign: -3 }} />
            {course.nombreChapitres} chapitres
          </Typography>
          <Typography color="text.secondary" sx={{ fontSize: 12 }}>
            <FolderOpenRoundedIcon sx={{ mr: 0.5, fontSize: 15, verticalAlign: -3 }} />
            {course.nombreRessources} ressources
          </Typography>
        </Box>
        <Button
          fullWidth
          variant={enrolled ? "outlined" : "contained"}
          endIcon={<ArrowForwardRoundedIcon />}
          disabled={pending || enrolling}
          onClick={enrolled ? onConsult : onEnroll}
          sx={{
            mt: 2,
            minHeight: 40,
            color: enrolled ? undefined : "#fff",
            background: enrolled
              ? undefined
              : "linear-gradient(110deg,#4056f4,#7458f6)",
          }}
        >
          {enrolled
            ? "Consulter le cours"
            : pending
              ? "Demande en attente"
              : enrolling
                ? "Inscription..."
                : "Demander l'inscription"}
        </Button>
      </Box>
    </Paper>
  );
}
