import { Box, Paper, Typography } from "@mui/material";
import AutoStoriesRoundedIcon from "@mui/icons-material/AutoStoriesRounded";
import CheckCircleRoundedIcon from "@mui/icons-material/CheckCircleRounded";
import PeopleAltRoundedIcon from "@mui/icons-material/PeopleAltRounded";
import TrendingUpRoundedIcon from "@mui/icons-material/TrendingUpRounded";
import WarningAmberRoundedIcon from "@mui/icons-material/WarningAmberRounded";
import type { ReactNode } from "react";
import type { ProfessorStudentProgress } from "../services/progressionApi";

export function ProgressionStats({
  progressions,
}: {
  progressions: ProfessorStudentProgress[];
}) {
  const students = new Set(progressions.map((item) => item.eleveId)).size;
  const courses = new Set(progressions.map((item) => item.coursId)).size;
  const completedStudents = new Set(
    progressions
      .filter((item) => item.pourcentage === 100)
      .map((item) => item.eleveId),
  ).size;
  const lowProgressStudents = new Set(
    progressions
      .filter((item) => item.pourcentage < 40)
      .map((item) => item.eleveId),
  ).size;
  const average = progressions.length
    ? Math.round(
        progressions.reduce((sum, item) => sum + item.pourcentage, 0) /
          progressions.length,
      )
    : 0;

  return (
    <Box
      sx={{
        mt: 2.5,
        display: "grid",
        gridTemplateColumns: {
          xs: "1fr",
          sm: "repeat(2,1fr)",
          lg: "repeat(5,1fr)",
        },
        gap: 1.6,
      }}
    >
      <Stat label="Progression moyenne" value={`${average}%`} icon={<TrendingUpRoundedIcon />} />
      <Stat label="Étudiants suivis" value={students} icon={<PeopleAltRoundedIcon />} color="#4775e8" />
      <Stat label="Cours actifs" value={courses} icon={<AutoStoriesRoundedIcon />} color="#20a66a" />
      <Stat
        label="Étudiants ayant terminé"
        value={completedStudents}
        icon={<CheckCircleRoundedIcon />}
        color="#22a65f"
      />
      <Stat
        label="Étudiants à accompagner"
        value={lowProgressStudents}
        icon={<WarningAmberRoundedIcon />}
        color="#e58b25"
      />
    </Box>
  );
}

function Stat({
  label,
  value,
  icon,
  color = "#6658ef",
}: {
  label: string;
  value: ReactNode;
  icon: ReactNode;
  color?: string;
}) {
  return (
    <Paper
      elevation={0}
      sx={{
        p: 2.1,
        display: "flex",
        alignItems: "center",
        gap: 1.4,
        border: "1px solid #e2e6f4",
        borderRadius: 3,
      }}
    >
      <Box
        sx={{
          width: 48,
          height: 48,
          display: "grid",
          placeItems: "center",
          borderRadius: 2.2,
          color,
          bgcolor: `${color}14`,
        }}
      >
        {icon}
      </Box>
      <Box>
        <Typography color="text.secondary" sx={{ fontSize: 12.5 }}>
          {label}
        </Typography>
        <Typography sx={{ fontSize: 25, fontWeight: 900 }}>{value}</Typography>
      </Box>
    </Paper>
  );
}
