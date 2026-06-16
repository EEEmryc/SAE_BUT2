import { Box, CircularProgress, Paper, Typography } from "@mui/material";
import AutoStoriesRoundedIcon from "@mui/icons-material/AutoStoriesRounded";
import FolderCopyRoundedIcon from "@mui/icons-material/FolderCopyRounded";
import GroupsRoundedIcon from "@mui/icons-material/GroupsRounded";
import type { CourseSummary } from "../services/coursesApi";
import { cardSx, LH_PRIMARY } from "../../../styles/tokens";

type SummaryCardProps = {
  summary: CourseSummary;
};

export function SummaryCard({ summary }: SummaryCardProps) {
  return (
    <Paper
      elevation={0}
      sx={{ ...cardSx, p: 2, borderRadius: 2.5 }}
    >
      <Typography sx={{ fontSize: 15.5, fontWeight: 850 }}>
        Résumé du cours
      </Typography>
      <Box
        sx={{
          mt: 1.75,
          display: "grid",
          gridTemplateColumns: "repeat(2,minmax(0,1fr))",
          gap: 1.5,
        }}
      >
        {[
          {
            icon: <GroupsRoundedIcon />,
            value: summary.students,
            label: "Étudiants inscrits",
          },
          {
            icon: <FolderCopyRoundedIcon />,
            value: summary.resources,
            label: "Ressources",
          },
          {
            icon: <AutoStoriesRoundedIcon />,
            value: summary.chapters,
            label: "Chapitres",
          },
        ].map((item) => (
          <Box
            key={item.label}
            sx={{ display: "flex", alignItems: "center", gap: 1 }}
          >
            <Box sx={{ color: LH_PRIMARY, "& svg": { fontSize: 21 } }}>
              {item.icon}
            </Box>
            <Box>
              <Typography sx={{ fontSize: 16, fontWeight: 850 }}>
                {item.value}
              </Typography>
              <Typography color="text.secondary" sx={{ fontSize: 10.5 }}>
                {item.label}
              </Typography>
            </Box>
          </Box>
        ))}
        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
          <Box sx={{ position: "relative", display: "inline-flex" }}>
            <CircularProgress
              variant="determinate"
              value={summary.averageProgress}
              size={35}
              thickness={5}
              sx={{ color: "#27ae62" }}
            />
            <Box
              sx={{
                position: "absolute",
                inset: 0,
                display: "grid",
                placeItems: "center",
                fontSize: 8,
                fontWeight: 850,
              }}
            >
              {summary.averageProgress}%
            </Box>
          </Box>
          <Box>
            <Typography sx={{ fontSize: 16, fontWeight: 850 }}>
              {summary.averageProgress}%
            </Typography>
            <Typography color="text.secondary" sx={{ fontSize: 10.5 }}>
              Progression moyenne
            </Typography>
          </Box>
        </Box>
      </Box>
    </Paper>
  );
}
