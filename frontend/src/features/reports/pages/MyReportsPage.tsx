import {
  Alert,
  Box,
  CircularProgress,
  Paper,
  Typography,
} from "@mui/material";
import AssignmentRoundedIcon from "@mui/icons-material/AssignmentRounded";
import { getApiErrorMessage } from "../../auth/api/apiError";
import { categoryLabels } from "../components/reportDisplay";
import { ReportStatusChip } from "../components/ReportStatusChip";
import { useMyReports } from "../hooks/useReports";

export function MyReportsPage() {
  const myReportsQuery = useMyReports();

  if (myReportsQuery.isPending) {
    return (
      <Box sx={{ minHeight: 360, display: "grid", placeItems: "center" }}>
        <Box sx={{ textAlign: "center" }}>
          <CircularProgress aria-label="Chargement de vos signalements" />
          <Typography color="text.secondary" sx={{ mt: 2 }}>
            Chargement de vos signalements...
          </Typography>
        </Box>
      </Box>
    );
  }

  if (myReportsQuery.isError) {
    return (
      <Alert severity="error">
        Impossible de charger vos signalements.{" "}
        {getApiErrorMessage(myReportsQuery.error)}
      </Alert>
    );
  }

  const reports = myReportsQuery.data;

  return (
    <Box sx={{ maxWidth: 880, mx: "auto" }}>
      <Typography
        component="h1"
        sx={{
          fontSize: { xs: 30, sm: 38 },
          fontWeight: 850,
          letterSpacing: "-0.04em",
        }}
      >
        Mes signalements
      </Typography>
      <Typography color="text.secondary" sx={{ mt: 0.5 }}>
        Retrouvez l'historique et le statut de vos signalements.
      </Typography>

      {reports.length === 0 ? (
        <Paper
          elevation={0}
          sx={{
            mt: 3,
            minHeight: 280,
            display: "grid",
            placeItems: "center",
            p: 3,
            textAlign: "center",
            border: "1px solid #e3e7f3",
            borderRadius: 3.5,
          }}
        >
          <Box>
            <AssignmentRoundedIcon
              sx={{ fontSize: 56, color: "rgba(101,83,222,0.35)" }}
            />
            <Typography sx={{ mt: 1.5, fontSize: 20, fontWeight: 800 }}>
              Aucun signalement
            </Typography>
            <Typography color="text.secondary" sx={{ mt: 0.5 }}>
              Vous n&apos;avez encore envoyé aucun signalement.
            </Typography>
          </Box>
        </Paper>
      ) : (
        <Box sx={{ mt: 3, display: "grid", gap: 2 }}>
          {reports.map((report) => (
            <Paper
              key={report.id}
              elevation={0}
              sx={{
                p: 2.5,
                border: "1px solid #e3e7f3",
                borderRadius: 3,
                boxShadow: "0 12px 34px rgba(54,64,125,0.05)",
              }}
            >
              <Box
                sx={{
                  display: "flex",
                  flexWrap: "wrap",
                  alignItems: "flex-start",
                  justifyContent: "space-between",
                  gap: 1.5,
                }}
              >
                <Box>
                  <Typography sx={{ fontWeight: 800, fontSize: 16 }}>
                    {report.sujet}
                  </Typography>
                  <Typography
                    color="text.secondary"
                    sx={{ mt: 0.4, fontSize: 13 }}
                  >
                    {categoryLabels[report.categorie]} •{" "}
                    {new Date(report.dateEnvoi).toLocaleDateString("fr-FR", {
                      day: "2-digit",
                      month: "long",
                      year: "numeric",
                    })}
                  </Typography>
                </Box>
                <ReportStatusChip status={report.statut} />
              </Box>
              <Typography
                color="text.secondary"
                sx={{
                  mt: 1.5,
                  fontSize: 14,
                  display: "-webkit-box",
                  WebkitLineClamp: 3,
                  WebkitBoxOrient: "vertical",
                  overflow: "hidden",
                }}
              >
                {report.description}
              </Typography>
            </Paper>
          ))}
        </Box>
      )}
    </Box>
  );
}