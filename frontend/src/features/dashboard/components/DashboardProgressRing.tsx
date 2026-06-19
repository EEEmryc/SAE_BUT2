import { Box, Button, Paper, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import {
  dashboardCardSx,
  LH_PRIMARY_ACCENT,
  LH_PROGRESS_TRACK,
} from "../../../styles/tokens";

type DashboardProgressRingProps = {
  value: number;
  title: string;
  description: string;
  actionPath?: string;
};

export function DashboardProgressRing({
  value,
  title,
  description,
  actionPath,
}: DashboardProgressRingProps) {
  const navigate = useNavigate();
  const normalizedValue = Math.min(100, Math.max(0, Math.round(value)));

  return (
    <Paper
      elevation={0}
      sx={{
        ...dashboardCardSx,
        p: 2.4,
        height: "100%",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        gap: 2.4,
        flexWrap: "wrap",
      }}
    >
      <Box
        sx={{
          width: 126,
          height: 126,
          display: "grid",
          placeItems: "center",
          flexShrink: 0,
          borderRadius: "50%",
          background: `conic-gradient(${LH_PRIMARY_ACCENT} ${normalizedValue * 3.6}deg,${LH_PROGRESS_TRACK} 0deg)`,
          position: "relative",
          "&::after": {
            content: '""',
            position: "absolute",
            inset: 14,
            bgcolor: "var(--lh-surface)",
            border: "1px solid var(--lh-border-light)",
            borderRadius: "50%",
          },
        }}
      >
        <Typography
          sx={{
            zIndex: 1,
            color: "var(--lh-text-primary)",
            fontSize: 26,
            fontWeight: 900,
          }}
        >
          {normalizedValue}%
        </Typography>
      </Box>
      <Box sx={{ maxWidth: 220 }}>
        <Typography sx={{ fontWeight: 900 }}>{title}</Typography>
        <Typography color="text.secondary" sx={{ mt: 0.6, fontSize: 12.5 }}>
          {description}
        </Typography>
        {actionPath && (
          <Button
            variant="outlined"
            onClick={() => navigate(actionPath)}
            sx={{ mt: 1.4, minHeight: 36 }}
          >
            Voir le détail
          </Button>
        )}
      </Box>
    </Paper>
  );
}
