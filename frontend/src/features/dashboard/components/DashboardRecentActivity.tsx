import { Box, Paper, Typography } from "@mui/material";
import AutoStoriesRoundedIcon from "@mui/icons-material/AutoStoriesRounded";
import CheckCircleRoundedIcon from "@mui/icons-material/CheckCircleRounded";
import ForumRoundedIcon from "@mui/icons-material/ForumRounded";
import PersonAddAltRoundedIcon from "@mui/icons-material/PersonAddAltRounded";
import ReportProblemRoundedIcon from "@mui/icons-material/ReportProblemRounded";
import type { DashboardActivity } from "../services/dashboardApi";
import {
  dashboardCardSx,
  iconBoxSx,
  LH_BORDER_LIGHT,
  LH_PRIMARY_ACCENT,
} from "../../../styles/tokens";

const activityIcons = {
  course: <AutoStoriesRoundedIcon />,
  message: <ForumRoundedIcon />,
  progress: <CheckCircleRoundedIcon />,
  report: <ReportProblemRoundedIcon />,
  user: <PersonAddAltRoundedIcon />,
};

function formatDate(value: string) {
  if (!value) return "Date indisponible";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "Date indisponible";

  return new Intl.DateTimeFormat("fr-FR", {
    day: "2-digit",
    month: "short",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
}

export function DashboardRecentActivity({
  items,
}: {
  items: DashboardActivity[];
}) {
  return (
    <Paper
      elevation={0}
      sx={{
        ...dashboardCardSx,
        p: 2.4,
        height: "100%",
      }}
    >
      <Typography sx={{ fontWeight: 900 }}>Activité récente</Typography>
      {items.length === 0 ? (
        <Typography color="text.secondary" sx={{ mt: 3, textAlign: "center" }}>
          Aucune activité récente.
        </Typography>
      ) : (
        <Box sx={{ mt: 1.3, display: "grid" }}>
          {items.map((item, index) => (
            <Box
              key={item.id}
              sx={{
                py: 1.25,
                display: "grid",
                gridTemplateColumns: "38px minmax(0,1fr) auto",
                alignItems: "center",
                gap: 1.1,
                borderTop: index === 0 ? 0 : `1px solid ${LH_BORDER_LIGHT}`,
              }}
            >
              <Box
                sx={{
                  ...iconBoxSx(36, LH_PRIMARY_ACCENT),
                  borderRadius: "50%",
                  "& svg": { fontSize: 19 },
                }}
              >
                {activityIcons[item.kind]}
              </Box>
              <Box sx={{ minWidth: 0 }}>
                <Typography noWrap sx={{ fontSize: 13, fontWeight: 850 }}>
                  {item.title}
                </Typography>
                <Typography noWrap color="text.secondary" sx={{ fontSize: 11.5 }}>
                  {item.description}
                </Typography>
              </Box>
              <Typography color="text.secondary" sx={{ fontSize: 10.5 }}>
                {formatDate(item.date)}
              </Typography>
            </Box>
          ))}
        </Box>
      )}
    </Paper>
  );
}
