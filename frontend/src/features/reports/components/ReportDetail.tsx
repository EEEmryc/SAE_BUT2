import { useState } from "react";
import {
  Alert,
  Avatar,
  Box,
  Button,
  CircularProgress,
  Divider,
  Link,
  MenuItem,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import AttachFileRoundedIcon from "@mui/icons-material/AttachFileRounded";
import SaveRoundedIcon from "@mui/icons-material/SaveRounded";
import { getApiErrorMessage } from "../../auth/api/apiError";
import type { ReportStatus } from "../api/reportsApi";
import {
  useReportDetail,
  useUpdateReportStatus,
} from "../hooks/useReports";
import { categoryLabels, reportStatusLabels } from "./reportDisplay";
import { ReportStatusChip } from "./ReportStatusChip";

function formatDate(value: string) {
  return new Intl.DateTimeFormat("fr-FR", {
    dateStyle: "long",
    timeStyle: "short",
  }).format(new Date(value));
}

type ReportDetailProps = {
  reportId: number | null;
};

export function ReportDetail({ reportId }: ReportDetailProps) {
  const detail = useReportDetail(reportId);
  const updateStatus = useUpdateReportStatus();
  const [pendingStatus, setPendingStatus] = useState<ReportStatus | null>(null);

  if (reportId === null) {
    return (
      <Paper
        elevation={0}
        sx={{
          minHeight: 420,
          display: "grid",
          placeItems: "center",
          p: 3,
          textAlign: "center",
          border: "1px solid #e3e7f3",
          borderRadius: 3.5,
        }}
      >
        <Typography color="text.secondary">
          Sélectionnez un signalement pour consulter son détail.
        </Typography>
      </Paper>
    );
  }

  if (detail.isPending) {
    return (
      <Paper
        elevation={0}
        sx={{
          minHeight: 420,
          display: "grid",
          placeItems: "center",
          border: "1px solid #e3e7f3",
          borderRadius: 3.5,
        }}
      >
        <CircularProgress aria-label="Chargement du signalement" />
      </Paper>
    );
  }

  if (detail.isError || !detail.data) {
    return (
      <Alert severity="error">
        Impossible de charger le détail. {getApiErrorMessage(detail.error)}
      </Alert>
    );
  }

  const report = detail.data;
  const selectedStatus = pendingStatus ?? report.statut;
  const initials =
    `${report.auteurPrenom[0] ?? ""}${report.auteurNom[0] ?? ""}`.toUpperCase();

  return (
    <Paper
      data-testid="report-detail"
      elevation={0}
      sx={{
        p: { xs: 2.25, sm: 3 },
        border: "1px solid #e3e7f3",
        borderRadius: 3.5,
        boxShadow: "0 18px 50px rgba(54, 64, 125, 0.07)",
      }}
    >
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "flex-start",
          gap: 1.5,
        }}
      >
        <Typography
          component="h2"
          sx={{ fontSize: 19, fontWeight: 850, lineHeight: 1.35 }}
        >
          {report.sujet}
        </Typography>
        <ReportStatusChip status={report.statut} />
      </Box>

      <Divider sx={{ my: 2.5 }} />

      <Typography sx={{ fontWeight: 800, mb: 1.5 }}>
        Informations générales
      </Typography>
      <Box sx={{ display: "flex", alignItems: "center", gap: 1.25 }}>
        <Avatar
          sx={{
            width: 42,
            height: 42,
            fontSize: 13,
            fontWeight: 800,
            color: "#4f5ff7",
            bgcolor: "rgba(79,95,247,0.11)",
          }}
        >
          {initials}
        </Avatar>
        <Box sx={{ minWidth: 0 }}>
          <Typography sx={{ fontWeight: 750 }}>
            {report.auteurPrenom} {report.auteurNom}
          </Typography>
          <Typography color="text.secondary" sx={{ fontSize: 12.5 }}>
            {report.auteurEmail}
          </Typography>
        </Box>
      </Box>

      <Box
        sx={{
          mt: 2,
          display: "grid",
          gridTemplateColumns: "repeat(2, minmax(0, 1fr))",
          gap: 2,
        }}
      >
        <Box>
          <Typography color="text.secondary" sx={{ fontSize: 12 }}>
            Rôle
          </Typography>
          <Typography sx={{ mt: 0.4, fontWeight: 700 }}>
            {report.auteurRole === "ETUDIANT" ? "Étudiant" : "Professeur"}
          </Typography>
        </Box>
        <Box>
          <Typography color="text.secondary" sx={{ fontSize: 12 }}>
            Catégorie
          </Typography>
          <Typography sx={{ mt: 0.4, fontWeight: 700 }}>
            {categoryLabels[report.categorie]}
          </Typography>
        </Box>
        <Box sx={{ gridColumn: "1 / -1" }}>
          <Typography color="text.secondary" sx={{ fontSize: 12 }}>
            Date de signalement
          </Typography>
          <Typography sx={{ mt: 0.4, fontWeight: 700 }}>
            {formatDate(report.dateEnvoi)}
          </Typography>
        </Box>
      </Box>

      <Divider sx={{ my: 2.5 }} />

      <Typography sx={{ fontWeight: 800 }}>Description</Typography>
      <Typography
        color="text.secondary"
        sx={{ mt: 1, lineHeight: 1.7, whiteSpace: "pre-wrap" }}
      >
        {report.description}
      </Typography>

      {report.pieceJointeNom && report.pieceJointeUrl && (
        <>
          <Divider sx={{ my: 2.5 }} />
          <Typography sx={{ fontWeight: 800, mb: 1.25 }}>
            Pièce jointe
          </Typography>
          <Link
            href={report.pieceJointeUrl}
            target="_blank"
            rel="noreferrer"
            underline="none"
            sx={{
              p: 1.4,
              display: "flex",
              alignItems: "center",
              gap: 1,
              border: "1px solid #e0e4f1",
              borderRadius: 2,
              bgcolor: "#fafbff",
              fontWeight: 700,
            }}
          >
            <AttachFileRoundedIcon fontSize="small" />
            {report.pieceJointeNom}
          </Link>
        </>
      )}

      <Divider sx={{ my: 2.5 }} />

      <Typography sx={{ fontWeight: 800, mb: 1.25 }}>
        Traitement
      </Typography>

      {updateStatus.isSuccess && (
        <Alert severity="success" sx={{ mb: 1.5 }}>
          Statut du signalement mis à jour avec succès.
        </Alert>
      )}
      {updateStatus.isError && (
        <Alert severity="error" sx={{ mb: 1.5 }}>
          {getApiErrorMessage(updateStatus.error)}
        </Alert>
      )}

      <Box
        sx={{
          display: "grid",
          gridTemplateColumns: { xs: "1fr", sm: "minmax(0, 1fr) auto" },
          gap: 1.25,
        }}
      >
        <TextField
          select
          size="small"
          label="Changer le statut"
          value={selectedStatus}
          onChange={(event) =>
            setPendingStatus(event.target.value as ReportStatus)
          }
        >
          {Object.entries(reportStatusLabels).map(([value, label]) => (
            <MenuItem key={value} value={value}>
              {label}
            </MenuItem>
          ))}
        </TextField>
        <Button
          variant="contained"
          startIcon={<SaveRoundedIcon />}
          disabled={selectedStatus === report.statut}
          loading={updateStatus.isPending}
          onClick={() =>
            updateStatus.mutate({
              reportId: report.id,
              statut: selectedStatus,
            }, {
              onSuccess: () => setPendingStatus(null),
            })
          }
          sx={{
            minHeight: 40,
            px: 2.5,
            color: "#fff",
            background: "linear-gradient(110deg, #4056f4, #7458f6)",
          }}
        >
          Enregistrer
        </Button>
      </Box>
    </Paper>
  );
}
