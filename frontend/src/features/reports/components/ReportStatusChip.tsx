import { Chip } from "@mui/material";
import type { ReportStatus } from "../services/reportsApi";
import { reportStatusLabels } from "./reportDisplay";
import { REPORT_STATUS_STYLES } from "../../../styles/tokens";

export function ReportStatusChip({ status }: { status: ReportStatus }) {
  const style = REPORT_STATUS_STYLES[status];

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
