import { useState } from "react";
import {
  Alert,
  Box,
  CircularProgress,
  Paper,
  Typography,
} from "@mui/material";
import AssignmentRoundedIcon from "@mui/icons-material/AssignmentRounded";
import CheckCircleRoundedIcon from "@mui/icons-material/CheckCircleRounded";
import NewReleasesRoundedIcon from "@mui/icons-material/NewReleasesRounded";
import PendingActionsRoundedIcon from "@mui/icons-material/PendingActionsRounded";
import { getApiErrorMessage } from "../../auth/api/apiError";
import type { ReportStatus } from "../api/reportsApi";
import { ReportDetail } from "../components/ReportDetail";
import { ReportsList } from "../components/ReportsList";
import { useReports } from "../hooks/useReports";

const statisticCards = [
  {
    key: "TOTAL",
    label: "Total des signalements",
    icon: <AssignmentRoundedIcon />,
    color: "#6553de",
    bgcolor: "#eeeaff",
  },
  {
    key: "NOUVEAU",
    label: "Nouveaux signalements",
    icon: <NewReleasesRoundedIcon />,
    color: "#db751c",
    bgcolor: "#fff0df",
  },
  {
    key: "EN_COURS",
    label: "En cours de traitement",
    icon: <PendingActionsRoundedIcon />,
    color: "#2573d7",
    bgcolor: "#e8f2ff",
  },
  {
    key: "RESOLU",
    label: "Signalements résolus",
    icon: <CheckCircleRoundedIcon />,
    color: "#159052",
    bgcolor: "#e5f8ed",
  },
] as const;

export function ReportsPage() {
  const reportsQuery = useReports();
  const [selectedId, setSelectedId] = useState<number | null>(null);

  if (reportsQuery.isPending) {
    return (
      <Box sx={{ minHeight: 460, display: "grid", placeItems: "center" }}>
        <Box sx={{ textAlign: "center" }}>
          <CircularProgress aria-label="Chargement des signalements" />
          <Typography color="text.secondary" sx={{ mt: 2 }}>
            Chargement des signalements...
          </Typography>
        </Box>
      </Box>
    );
  }

  if (reportsQuery.isError) {
    return (
      <Alert severity="error">
        Impossible de charger les signalements.{" "}
        {getApiErrorMessage(reportsQuery.error)}
      </Alert>
    );
  }

  const reports = reportsQuery.data;
  const effectiveSelectedId = reports.some(
    (report) => report.id === selectedId,
  )
    ? selectedId
    : (reports[0]?.id ?? null);

  const counts = reports.reduce<Record<ReportStatus | "TOTAL", number>>(
    (result, report) => {
      result.TOTAL += 1;
      result[report.statut] += 1;
      return result;
    },
    { TOTAL: 0, NOUVEAU: 0, EN_COURS: 0, TRAITE: 0, RESOLU: 0 },
  );

  return (
    <Box sx={{ maxWidth: 1680, mx: "auto" }}>
      <Typography
        component="h1"
        sx={{
          fontSize: { xs: 30, sm: 38 },
          fontWeight: 850,
          letterSpacing: "-0.04em",
        }}
      >
        Signalements
      </Typography>
      <Typography color="text.secondary" sx={{ mt: 0.5 }}>
        Consultez et gérez les problèmes signalés par les étudiants et les
        professeurs.
      </Typography>

      <Box
        sx={{
          mt: 3,
          display: "grid",
          gridTemplateColumns: {
            xs: "1fr",
            sm: "repeat(2, minmax(0, 1fr))",
            xl: "repeat(4, minmax(0, 1fr))",
          },
          gap: 2,
        }}
      >
        {statisticCards.map((card) => (
          <Paper
            key={card.key}
            elevation={0}
            sx={{
              p: 2.25,
              display: "flex",
              alignItems: "center",
              gap: 1.5,
              border: "1px solid #e3e7f3",
              borderRadius: 3,
              boxShadow: "0 12px 34px rgba(54,64,125,0.05)",
            }}
          >
            <Box
              sx={{
                width: 48,
                height: 48,
                display: "grid",
                placeItems: "center",
                flexShrink: 0,
                borderRadius: "50%",
                color: card.color,
                bgcolor: card.bgcolor,
              }}
            >
              {card.icon}
            </Box>
            <Box>
              <Typography sx={{ fontSize: 25, fontWeight: 850, lineHeight: 1 }}>
                {counts[card.key]}
              </Typography>
              <Typography color="text.secondary" sx={{ mt: 0.6, fontSize: 13 }}>
                {card.label}
              </Typography>
            </Box>
          </Paper>
        ))}
      </Box>

      {reports.length === 0 ? (
        <Paper
          elevation={0}
          sx={{
            mt: 3,
            minHeight: 380,
            display: "grid",
            placeItems: "center",
            p: 3,
            textAlign: "center",
            border: "1px solid #e3e7f3",
            borderRadius: 3.5,
          }}
        >
          <Box>
            <CheckCircleRoundedIcon
              sx={{ fontSize: 56, color: "rgba(32,185,107,0.45)" }}
            />
            <Typography sx={{ mt: 1.5, fontSize: 20, fontWeight: 800 }}>
              Aucun signalement
            </Typography>
            <Typography color="text.secondary" sx={{ mt: 0.5 }}>
              Aucun problème n&apos;a été signalé pour le moment.
            </Typography>
          </Box>
        </Paper>
      ) : (
        <Box
          sx={{
            mt: 3,
            display: "grid",
            gridTemplateColumns: {
              xs: "minmax(0, 1fr)",
            },
            "@media (min-width: 1400px)": {
              gridTemplateColumns: "minmax(0, 2fr) minmax(330px, 0.78fr)",
            },
            gap: 2.5,
            alignItems: "start",
          }}
        >
          <ReportsList
            reports={reports}
            selectedId={effectiveSelectedId}
            onSelect={setSelectedId}
            onRefresh={() => void reportsQuery.refetch()}
          />
          <Box sx={{ scrollMarginTop: 20 }}>
            <ReportDetail
              key={effectiveSelectedId ?? "empty"}
              reportId={effectiveSelectedId}
            />
          </Box>
        </Box>
      )}
    </Box>
  );
}
