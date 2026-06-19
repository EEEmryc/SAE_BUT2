import { Box, Paper, Typography } from "@mui/material";
import type { ReactNode } from "react";
import { cardSx, iconBoxSx } from "../../../styles/tokens";

export type DashboardStat = {
  id: string;
  label: string;
  value: ReactNode;
  caption: string;
  icon: ReactNode;
  color?: string;
};

export function DashboardStatsCards({ items }: { items: DashboardStat[] }) {
  return (
    <Box
      sx={{
        mt: 2,
        alignSelf: "start",
        display: "grid",
        gridTemplateColumns: {
          xs: "1fr",
          sm: "repeat(2,minmax(0,1fr))",
          lg: `repeat(${Math.min(items.length, 5)},minmax(0,1fr))`,
        },
        gap: 1.6,
      }}
    >
      {items.map((item) => {
        const color = item.color ?? "#5966ef";
        return (
          <Paper
            key={item.id}
            elevation={0}
            sx={{
              ...cardSx,
              p: 2.1,
              minHeight: 118,
              display: "flex",
              alignItems: "center",
              gap: 1.5,
              borderRadius: 3.2,
            }}
          >
            <Box sx={iconBoxSx(50, color)}>
              {item.icon}
            </Box>
            <Box sx={{ minWidth: 0 }}>
              <Typography sx={{ fontSize: 25, fontWeight: 900, lineHeight: 1.1 }}>
                {item.value}
              </Typography>
              <Typography sx={{ mt: 0.45, fontSize: 13, fontWeight: 750 }}>
                {item.label}
              </Typography>
              <Typography color="text.secondary" sx={{ mt: 0.25, fontSize: 11.5 }}>
                {item.caption}
              </Typography>
            </Box>
          </Paper>
        );
      })}
    </Box>
  );
}
