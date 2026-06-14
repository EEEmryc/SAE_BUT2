import { Box, Paper, Typography } from "@mui/material";
import SchoolRoundedIcon from "@mui/icons-material/SchoolRounded";

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
        p: { xs: 2.5, md: 3.2 },
        display: "flex",
        alignItems: "center",
        gap: 2.5,
        border: "1px solid #e2e6f4",
        borderRadius: 4,
        background:
          "radial-gradient(circle at 88% 35%,rgba(99,88,238,.14),transparent 22%),linear-gradient(135deg,#fff,#fbfbff)",
        boxShadow: "0 18px 44px rgba(62,70,130,.06)",
      }}
    >
      <Box
        sx={{
          width: { xs: 66, md: 92 },
          height: { xs: 66, md: 92 },
          display: "grid",
          placeItems: "center",
          flexShrink: 0,
          color: "#fff",
          borderRadius: "50%",
          background: "linear-gradient(135deg,#5265f5,#7554ee)",
          boxShadow: "0 16px 30px rgba(84,86,232,.28)",
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
