import { Box, Paper, Typography } from "@mui/material";
import AutoStoriesRoundedIcon from "@mui/icons-material/AutoStoriesRounded";
import CheckCircleRoundedIcon from "@mui/icons-material/CheckCircleRounded";
import PeopleAltRoundedIcon from "@mui/icons-material/PeopleAltRounded";
import TrendingUpRoundedIcon from "@mui/icons-material/TrendingUpRounded";
import WarningAmberRoundedIcon from "@mui/icons-material/WarningAmberRounded";
import type { ReactNode } from "react";
import {
  cardSx,
  iconBoxSx,
  LH_STAT_COLORS,
  LH_PRIMARY_ACCENT,
} from "../../../styles/tokens";
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
        color={LH_STAT_COLORS.blue}
      />
      <Stat
        label="Cours actifs"
        value={stats.activeCourses}
        caption="Cours présents dans la sélection"
        icon={<AutoStoriesRoundedIcon />}
        color={LH_STAT_COLORS.green}
      />
      <Stat
        label="Parcours terminés"
        value={stats.completedPaths}
        caption="Couples étudiant-cours à 100 %"
        icon={<CheckCircleRoundedIcon />}
        color={LH_STAT_COLORS.green}
      />
      <Stat
        label="Progressions à accompagner"
        value={stats.supportPaths}
        caption="De 0 à 39 %, hors cours sans chapitre"
        icon={<WarningAmberRoundedIcon />}
        color={LH_STAT_COLORS.warning}
      />
    </Box>
  );
}

function Stat({
  label,
  value,
  caption,
  icon,
  color = LH_PRIMARY_ACCENT,
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
        ...cardSx,
        p: 2.1,
        display: "flex",
        alignItems: "center",
        gap: 1.4,
      }}
    >
      <Box sx={{ ...iconBoxSx(48, color), borderRadius: 2.2 }}>
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
