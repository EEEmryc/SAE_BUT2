import { useMemo, useState } from "react";
import {
  Avatar,
  Box,
  Button,
  IconButton,
  InputAdornment,
  MenuItem,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";
import ChevronRightRoundedIcon from "@mui/icons-material/ChevronRightRounded";
import RefreshRoundedIcon from "@mui/icons-material/RefreshRounded";
import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import type {
  Report,
} from "../api/reportsApi";
import { categoryLabels } from "./reportDisplay";
import { ReportStatusChip } from "./ReportStatusChip";

const roleLabels = {
  ETUDIANT: "Étudiant",
  PROFESSEUR: "Professeur",
} as const;

function formatDate(value: string) {
  return new Intl.DateTimeFormat("fr-FR", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(new Date(value));
}

function Author({ report }: { report: Report }) {
  const initials =
    `${report.auteurPrenom[0] ?? ""}${report.auteurNom[0] ?? ""}`.toUpperCase();

  return (
    <Box sx={{ display: "flex", alignItems: "center", gap: 1.1, minWidth: 0 }}>
      <Avatar
        sx={{
          width: 36,
          height: 36,
          fontSize: 12,
          fontWeight: 800,
          color: "#4f5ff7",
          bgcolor: "rgba(79,95,247,0.11)",
        }}
      >
        {initials}
      </Avatar>
      <Box sx={{ minWidth: 0 }}>
        <Typography noWrap sx={{ fontSize: 13.5, fontWeight: 750 }}>
          {report.auteurPrenom} {report.auteurNom}
        </Typography>
        <Typography noWrap color="text.secondary" sx={{ fontSize: 11.5 }}>
          {report.auteurEmail}
        </Typography>
      </Box>
    </Box>
  );
}

type ReportsListProps = {
  reports: Report[];
  selectedId: number | null;
  onSelect: (id: number) => void;
  onRefresh: () => void;
};

export function ReportsList({
  reports,
  selectedId,
  onSelect,
  onRefresh,
}: ReportsListProps) {
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState("TOUS");
  const [role, setRole] = useState("TOUS");
  const [category, setCategory] = useState("TOUTES");
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(7);

  const filteredReports = useMemo(() => {
    const normalizedSearch = search.trim().toLocaleLowerCase("fr");

    return reports.filter((report) => {
      const searchable =
        `${report.sujet} ${report.description} ${report.auteurPrenom} `
        + `${report.auteurNom} ${report.auteurEmail}`.toLocaleLowerCase("fr");

      return (
        (!normalizedSearch || searchable.includes(normalizedSearch))
        && (status === "TOUS" || report.statut === status)
        && (role === "TOUS" || report.auteurRole === role)
        && (category === "TOUTES" || report.categorie === category)
      );
    });
  }, [category, reports, role, search, status]);

  const visibleReports = filteredReports.slice(
    page * rowsPerPage,
    page * rowsPerPage + rowsPerPage,
  );

  const resetPage = () => setPage(0);

  return (
    <Paper
      data-testid="reports-list"
      elevation={0}
      sx={{
        overflow: "hidden",
        border: "1px solid #e3e7f3",
        borderRadius: 3.5,
        boxShadow: "0 18px 50px rgba(54, 64, 125, 0.07)",
      }}
    >
      <Box
        sx={{
          p: 2,
          display: "grid",
          gridTemplateColumns: {
            xs: "1fr",
            sm: "minmax(220px, 1fr) 170px",
            xl: "minmax(240px, 1fr) 160px 160px 170px auto",
          },
          gap: 1.25,
          borderBottom: "1px solid #eceef7",
        }}
      >
        <TextField
          size="small"
          value={search}
          placeholder="Rechercher un signalement..."
          onChange={(event) => {
            setSearch(event.target.value);
            resetPage();
          }}
          slotProps={{
            htmlInput: { "aria-label": "Rechercher un signalement" },
            input: {
              startAdornment: (
                <InputAdornment position="start">
                  <SearchRoundedIcon fontSize="small" />
                </InputAdornment>
              ),
            },
          }}
        />
        <TextField
          select
          size="small"
          label="Statut"
          value={status}
          onChange={(event) => {
            setStatus(event.target.value);
            resetPage();
          }}
        >
          <MenuItem value="TOUS">Tous les statuts</MenuItem>
          <MenuItem value="NOUVEAU">Nouveaux</MenuItem>
          <MenuItem value="EN_COURS">En cours</MenuItem>
          <MenuItem value="TRAITE">Traités</MenuItem>
          <MenuItem value="RESOLU">Résolus</MenuItem>
        </TextField>
        <TextField
          select
          size="small"
          label="Rôle"
          value={role}
          onChange={(event) => {
            setRole(event.target.value);
            resetPage();
          }}
        >
          <MenuItem value="TOUS">Tous les rôles</MenuItem>
          <MenuItem value="ETUDIANT">Étudiants</MenuItem>
          <MenuItem value="PROFESSEUR">Professeurs</MenuItem>
        </TextField>
        <TextField
          select
          size="small"
          label="Catégorie"
          value={category}
          onChange={(event) => {
            setCategory(event.target.value);
            resetPage();
          }}
        >
          <MenuItem value="TOUTES">Toutes les catégories</MenuItem>
          {Object.entries(categoryLabels).map(([value, label]) => (
            <MenuItem key={value} value={value}>
              {label}
            </MenuItem>
          ))}
        </TextField>
        <Button
          variant="outlined"
          startIcon={<RefreshRoundedIcon />}
          onClick={onRefresh}
          sx={{ minHeight: 40, px: 2 }}
        >
          Actualiser
        </Button>
      </Box>

      {filteredReports.length === 0 ? (
        <Box sx={{ py: 8, px: 2, textAlign: "center" }}>
          <Typography sx={{ fontSize: 19, fontWeight: 800 }}>
            Aucun signalement trouvé
          </Typography>
          <Typography color="text.secondary" sx={{ mt: 0.75 }}>
            Aucun élément ne correspond aux filtres sélectionnés.
          </Typography>
        </Box>
      ) : (
        <>
          <TableContainer
            sx={{
              display: "none",
              "@media (min-width: 1400px)": { display: "block" },
            }}
          >
            <Table aria-label="Liste des signalements">
              <TableHead>
                <TableRow sx={{ bgcolor: "#f8f9fe" }}>
                  <TableCell>Sujet</TableCell>
                  <TableCell>Auteur</TableCell>
                  <TableCell>Rôle</TableCell>
                  <TableCell>Catégorie</TableCell>
                  <TableCell>Statut</TableCell>
                  <TableCell>Date</TableCell>
                  <TableCell padding="checkbox" />
                </TableRow>
              </TableHead>
              <TableBody>
                {visibleReports.map((report) => (
                  <TableRow
                    key={report.id}
                    hover
                    selected={report.id === selectedId}
                    onClick={() => onSelect(report.id)}
                    sx={{
                      cursor: "pointer",
                      "&.Mui-selected": { bgcolor: "rgba(79,95,247,0.06)" },
                    }}
                  >
                    <TableCell sx={{ minWidth: 210, fontWeight: 750 }}>
                      {report.sujet}
                    </TableCell>
                    <TableCell sx={{ minWidth: 220 }}>
                      <Author report={report} />
                    </TableCell>
                    <TableCell>{roleLabels[report.auteurRole]}</TableCell>
                    <TableCell>{categoryLabels[report.categorie]}</TableCell>
                    <TableCell>
                      <ReportStatusChip status={report.statut} />
                    </TableCell>
                    <TableCell sx={{ minWidth: 125 }}>
                      {formatDate(report.dateEnvoi)}
                    </TableCell>
                    <TableCell padding="checkbox">
                      <IconButton
                        aria-label={`Ouvrir ${report.sujet}`}
                        onClick={(event) => {
                          event.stopPropagation();
                          onSelect(report.id);
                        }}
                      >
                        <ChevronRightRoundedIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          <Box
            sx={{
              display: "grid",
              "@media (min-width: 1400px)": { display: "none" },
              gap: 1.25,
              p: 1.5,
              bgcolor: "#f8f9fe",
            }}
          >
            {visibleReports.map((report) => (
              <Paper
                key={report.id}
                component="button"
                type="button"
                onClick={() => onSelect(report.id)}
                elevation={0}
                sx={{
                  width: "100%",
                  p: 2,
                  textAlign: "left",
                  font: "inherit",
                  color: "inherit",
                  cursor: "pointer",
                  border: report.id === selectedId
                    ? "1px solid #7682ff"
                    : "1px solid #e3e7f3",
                  borderRadius: 2.5,
                  bgcolor: "#fff",
                }}
              >
                <Box
                  sx={{
                    display: "flex",
                    justifyContent: "space-between",
                    gap: 1,
                    alignItems: "flex-start",
                  }}
                >
                  <Typography sx={{ fontWeight: 800 }}>
                    {report.sujet}
                  </Typography>
                  <ReportStatusChip status={report.statut} />
                </Box>
                <Box sx={{ mt: 1.5 }}>
                  <Author report={report} />
                </Box>
                <Box
                  sx={{
                    mt: 1.5,
                    display: "flex",
                    justifyContent: "space-between",
                    gap: 1,
                    color: "text.secondary",
                  }}
                >
                  <Typography sx={{ fontSize: 12.5 }}>
                    {categoryLabels[report.categorie]}
                  </Typography>
                  <Typography sx={{ fontSize: 12.5 }}>
                    {formatDate(report.dateEnvoi)}
                  </Typography>
                </Box>
              </Paper>
            ))}
          </Box>

          <TablePagination
            component="div"
            count={filteredReports.length}
            page={page}
            onPageChange={(_, nextPage) => setPage(nextPage)}
            rowsPerPage={rowsPerPage}
            onRowsPerPageChange={(event) => {
              setRowsPerPage(Number(event.target.value));
              setPage(0);
            }}
            rowsPerPageOptions={[7, 10, 25]}
            labelRowsPerPage="Par page"
            labelDisplayedRows={({ from, to, count }) =>
              `${from}-${to} sur ${count}`
            }
            sx={{ borderTop: "1px solid #eceef7" }}
          />
        </>
      )}
    </Paper>
  );
}
