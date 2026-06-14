import { Box, Paper, Typography } from "@mui/material";
import AutoStoriesRoundedIcon from "@mui/icons-material/AutoStoriesRounded";
import CheckCircleRoundedIcon from "@mui/icons-material/CheckCircleRounded";
import ForumRoundedIcon from "@mui/icons-material/ForumRounded";
import PersonAddAltRoundedIcon from "@mui/icons-material/PersonAddAltRounded";
import ReportProblemRoundedIcon from "@mui/icons-material/ReportProblemRounded";
import type { DashboardActivity } from "../api/dashboardApi";

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
        p: 2.4,
        height: "100%",
        border: "1px solid #e2e6f4",
        borderRadius: 3.4,
        boxShadow: "0 12px 32px rgba(62,70,130,.05)",
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
                borderTop: index === 0 ? 0 : "1px solid #edf0f7",
              }}
            >
              <Box
                sx={{
                  width: 36,
                  height: 36,
                  display: "grid",
                  placeItems: "center",
                  color: "#5966ef",
                  bgcolor: "#eef0ff",
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
