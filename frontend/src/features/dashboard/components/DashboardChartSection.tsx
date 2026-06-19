import { Box, LinearProgress, Paper, Typography } from "@mui/material";
import BarChartRoundedIcon from "@mui/icons-material/BarChartRounded";
import type { DashboardCourseMetric } from "../services/dashboardApi";
import { dashboardCardSx, LH_PROGRESS_TRACK } from "../../../styles/tokens";

type DashboardChartSectionProps = {
  title: string;
  items: DashboardCourseMetric[];
  mode?: "percent" | "value";
  emptyMessage?: string;
};

export function DashboardChartSection({
  title,
  items,
  mode = "percent",
  emptyMessage = "Aucune donnée disponible pour le moment.",
}: DashboardChartSectionProps) {
  const max = Math.max(1, ...items.map((item) => item.value));

  return (
    <Paper
      elevation={0}
      sx={{
        ...dashboardCardSx,
        p: 2.4,
        height: "100%",
      }}
    >
      <Typography sx={{ display: "flex", alignItems: "center", gap: 1, fontWeight: 900 }}>
        <BarChartRoundedIcon color="primary" />
        {title}
      </Typography>

      {items.length === 0 ? (
        <Typography color="text.secondary" sx={{ mt: 3, textAlign: "center" }}>
          {emptyMessage}
        </Typography>
      ) : (
        <Box sx={{ mt: 2, display: "grid", gap: 1.45 }}>
          {items.slice(0, 6).map((item) => {
            const progress =
              mode === "percent" ? item.value : Math.round((item.value * 100) / max);
            return (
              <Box key={item.id}>
                <Box
                  sx={{
                    mb: 0.55,
                    display: "flex",
                    justifyContent: "space-between",
                    gap: 2,
                  }}
                >
                  <Box sx={{ minWidth: 0 }}>
                    <Typography noWrap sx={{ fontSize: 13, fontWeight: 800 }}>
                      {item.title}
                    </Typography>
                    <Typography color="text.secondary" sx={{ fontSize: 10.8 }}>
                      {item.detail}
                    </Typography>
                  </Box>
                  <Typography sx={{ fontSize: 13, fontWeight: 900 }}>
                    {item.value}{mode === "percent" ? "%" : ""}
                  </Typography>
                </Box>
                <LinearProgress
                  variant="determinate"
                  value={Math.min(100, progress)}
                  sx={{ height: 8, borderRadius: 10, bgcolor: LH_PROGRESS_TRACK }}
                />
              </Box>
            );
          })}
        </Box>
      )}
    </Paper>
  );
}
