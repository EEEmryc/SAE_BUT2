import { Chip } from "@mui/material";
import type { ReportStatus } from "../api/reportsApi";
import { reportStatusLabels } from "./reportDisplay";

const statusStyles: Record<
  ReportStatus,
  { color: string; bgcolor: string; dot: string }
> = {
  NOUVEAU: { color: "#c4610b", bgcolor: "#fff0df", dot: "#f08a24" },
  EN_COURS: { color: "#2167c7", bgcolor: "#e8f2ff", dot: "#3a86e8" },
  TRAITE: { color: "#6350c7", bgcolor: "#eeeaff", dot: "#7864e8" },
  RESOLU: { color: "#12834d", bgcolor: "#e5f8ed", dot: "#20b96b" },
};

export function ReportStatusChip({ status }: { status: ReportStatus }) {
  const style = statusStyles[status];

  return (
    <Chip
      size="small"
      label={reportStatusLabels[status]}
      sx={{
        color: style.color,
        bgcolor: style.bgcolor,
        borderRadius: 1.5,
        fontWeight: 750,
        "&::before": {
          content: '""',
          width: 7,
          height: 7,
          borderRadius: "50%",
          bgcolor: style.dot,
          ml: 0.8,
        },
      }}
    />
  );
}
