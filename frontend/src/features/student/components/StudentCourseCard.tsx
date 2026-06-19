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
import type { CatalogCourse } from "../services/studentLearningApi";
import {
  cardLgSx,
  ENROLLMENT_STATUS_STYLES,
  gradientBtnSx,
  LH_TEXT_SECONDARY,
  STUDENT_COURSE_GRADIENTS,
} from "../../../styles/tokens";

type StudentCourseCardProps = {
  course: CatalogCourse;
  index: number;
  enrolling: boolean;
  onConsult: () => void;
  onEnroll: () => void;
};

export function StudentCourseCard({
  course,
  index,
  enrolling,
  onConsult,
  onEnroll,
}: StudentCourseCardProps) {
  const enrolled = course.statutInscription === "VALIDE";
  const pending = course.statutInscription === "EN_ATTENTE";
  const rejected = course.statutInscription === "REFUSE";

  const enrollmentKey = enrolled
    ? "VALIDE"
    : pending
      ? "EN_ATTENTE"
      : rejected
        ? "REFUSE"
        : "OUVERT";
  const statusStyle = ENROLLMENT_STATUS_STYLES[enrollmentKey];

  return (
    <Paper
      elevation={0}
      sx={{
        ...cardLgSx,
        overflow: "hidden",
        display: "flex",
        flexDirection: "column",
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
          background: STUDENT_COURSE_GRADIENTS[index % STUDENT_COURSE_GRADIENTS.length],
        }}
      >
        <AutoStoriesRoundedIcon sx={{ fontSize: 42, opacity: 0.92 }} />
        <Chip
          size="small"
          label={
            enrolled
              ? "Inscrit"
              : pending
                ? "En attente"
                : rejected
                  ? "Refusée"
                  : "Ouvert"
          }
          sx={{
            color: statusStyle.color,
            bgcolor: statusStyle.bgcolor,
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
          <PersonOutlineRoundedIcon sx={{ fontSize: 17, color: LH_TEXT_SECONDARY }} />
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
          disabled={pending || rejected || enrolling}
          onClick={enrolled ? onConsult : onEnroll}
          sx={{
            mt: 2,
            minHeight: 40,
            ...(enrolled ? {} : gradientBtnSx),
          }}
        >
          {enrolled
            ? "Consulter le cours"
            : pending
              ? "Demande en attente"
              : rejected
                ? "Demande refusée"
              : enrolling
                ? "Inscription..."
                : "Demander l'inscription"}
        </Button>
      </Box>
    </Paper>
  );
}
