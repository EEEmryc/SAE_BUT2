import { Box, Paper, Typography } from "@mui/material";
import SchoolRoundedIcon from "@mui/icons-material/SchoolRounded";
import {
  LH_GRADIENT_HERO_CARD,
  LH_GRADIENT_HERO_ICON,
  LH_SHADOW_HERO,
  LH_SHADOW_ICON,
  LH_SURFACE,
  cardSx,
} from "../../../styles/tokens";

type DashboardHeroProps = {
  firstName: string;
  description: string;
};

export function DashboardHero({
  firstName,
  description,
}: DashboardHeroProps) {
  return (
    <Paper
      elevation={0}
      sx={{
        ...cardSx,
        p: { xs: 2.5, md: 3.2 },
        display: "flex",
        alignItems: "center",
        gap: 2.5,
        borderRadius: 4,
        background: LH_GRADIENT_HERO_CARD,
        boxShadow: LH_SHADOW_HERO,
      }}
    >
      <Box
        sx={{
          width: { xs: 66, md: 92 },
          height: { xs: 66, md: 92 },
          display: "grid",
          placeItems: "center",
          flexShrink: 0,
          color: LH_SURFACE,
          borderRadius: "50%",
          background: LH_GRADIENT_HERO_ICON,
          boxShadow: LH_SHADOW_ICON,
        }}
      >
        <SchoolRoundedIcon sx={{ fontSize: { xs: 36, md: 50 } }} />
      </Box>
      <Box>
        <Typography
          component="h1"
          sx={{
            fontSize: { xs: 27, md: 34 },
            fontWeight: 900,
            letterSpacing: "-.035em",
          }}
        >
          Bonjour, {firstName} !
        </Typography>
        <Typography color="text.secondary" sx={{ mt: 0.6 }}>
          {description}
        </Typography>
      </Box>
    </Paper>
  );
}
