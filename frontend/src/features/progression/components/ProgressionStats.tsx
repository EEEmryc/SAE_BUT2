import { Box, Paper, Typography } from "@mui/material";
import AutoStoriesRoundedIcon from "@mui/icons-material/AutoStoriesRounded";
import CheckCircleRoundedIcon from "@mui/icons-material/CheckCircleRounded";
import PeopleAltRoundedIcon from "@mui/icons-material/PeopleAltRounded";
import TrendingUpRoundedIcon from "@mui/icons-material/TrendingUpRounded";
import WarningAmberRoundedIcon from "@mui/icons-material/WarningAmberRounded";
import type { ReactNode } from "react";
import type { ProfessorStudentProgress } from "../services/progressionApi";
import { calculateProgressionStats } from "./progressionStatsCalculator";

export function ProgressionStats({
  progressions,
}: {
  progressions: ProfessorStudentProgress[];
}) {
  const stats = calculateProgressionStats(progressions);

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
      <Stat
        label="Progression moyenne"
        value={`${stats.averageProgress}%`}
        caption={`${stats.completedChapters}/${stats.totalChapters} chapitres terminés`}
        icon={<TrendingUpRoundedIcon />}
      />
      <Stat
        label="Étudiants suivis"
        value={stats.trackedStudents}
        caption="Étudiants uniques affichés"
        icon={<PeopleAltRoundedIcon />}
        color="#4775e8"
      />
      <Stat
        label="Cours actifs"
        value={stats.activeCourses}
        caption="Cours présents dans la sélection"
        icon={<AutoStoriesRoundedIcon />}
        color="#20a66a"
      />
      <Stat
        label="Parcours terminés"
        value={stats.completedPaths}
        caption="Couples étudiant-cours à 100 %"
        icon={<CheckCircleRoundedIcon />}
        color="#22a65f"
      />
      <Stat
        label="Progressions à accompagner"
        value={stats.supportPaths}
        caption="De 0 à 39 %, hors cours sans chapitre"
        icon={<WarningAmberRoundedIcon />}
        color="#e58b25"
      />
    </Box>
  );
}

function Stat({
  label,
  value,
  caption,
  icon,
  color = "#6658ef",
}: {
  label: string;
  value: ReactNode;
  caption: string;
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
        <Typography color="text.secondary" sx={{ mt: 0.15, fontSize: 10.5 }}>
          {caption}
        </Typography>
      </Box>
    </Paper>
  );
}
