import SchoolRoundedIcon from "@mui/icons-material/SchoolRounded";
import { Box, Typography } from "@mui/material";

type BrandMarkProps = {
  compact?: boolean;
};

export function BrandMark({ compact = false }: BrandMarkProps) {
  return (
    <Box
      sx={{
        display: "inline-flex",
        alignItems: "center",
        gap: compact ? 1 : 1.3,
      }}
    >
      <Box
        sx={{
          display: "grid",
          placeItems: "center",
          color: "primary.main",
          filter: "drop-shadow(0 8px 12px rgba(79, 95, 247, 0.26))",
        }}
      >
        <SchoolRoundedIcon sx={{ fontSize: compact ? 40 : 48 }} />
      </Box>
      {!compact && (
        <Typography
          component="span"
          sx={{ fontSize: 34, fontWeight: 800, letterSpacing: "-0.04em" }}
        >
          Learn
          <Box component="span" sx={{ color: "primary.main" }}>
            Hub
          </Box>
        </Typography>
      )}
    </Box>
  );
}
