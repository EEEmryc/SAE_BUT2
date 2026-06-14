import { useMemo, useState } from "react";
import {
  Avatar,
  Box,
  Button,
  Chip,
  InputAdornment,
  MenuItem,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import MoreVertRoundedIcon from "@mui/icons-material/MoreVertRounded";
import PersonAddAltRoundedIcon from "@mui/icons-material/PersonAddAltRounded";
import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import type { Enrollment } from "../api/coursesApi";

type StudentsPanelProps = {
  enrollments: Enrollment[];
  onManage: () => void;
};

export function StudentsPanel({
  enrollments,
  onManage,
}: StudentsPanelProps) {
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState("TOUS");
  const visibleStudents = useMemo(() => {
    const normalized = search.trim().toLocaleLowerCase("fr");
    return enrollments.filter((enrollment) => {
      const matchesSearch =
        !normalized ||
        `${enrollment.elevePrenom} ${enrollment.eleveNom} ${enrollment.eleveEmail}`
          .toLocaleLowerCase("fr")
          .includes(normalized);
      return (
        matchesSearch &&
        (status === "TOUS" || enrollment.statut === status)
      );
    });
  }, [enrollments, search, status]);

  return (
    <Paper
      elevation={0}
      sx={{
        p: 2,
        border: "1px solid #e1e6f2",
        borderRadius: 2.5,
      }}
    >
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          gap: 1,
        }}
      >
        <Typography sx={{ fontSize: 15.5, fontWeight: 850 }}>
          Étudiants inscrits ({enrollments.length})
        </Typography>
        <Button
          variant="outlined"
          startIcon={<PersonAddAltRoundedIcon />}
          onClick={onManage}
          sx={{ minHeight: 36, px: 1.4, fontSize: 12 }}
        >
          Gérer les inscriptions
        </Button>
      </Box>

      <Box
        sx={{
          mt: 1.5,
          display: "grid",
          gridTemplateColumns: { xs: "1fr", sm: "minmax(0,1fr) 145px" },
          gap: 1,
        }}
      >
        <TextField
          size="small"
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          placeholder="Rechercher un étudiant..."
          slotProps={{
            input: {
              startAdornment: (
                <InputAdornment position="start">
                  <SearchRoundedIcon sx={{ fontSize: 18 }} />
                </InputAdornment>
              ),
            },
          }}
        />
        <TextField
          select
          size="small"
          value={status}
          onChange={(event) => setStatus(event.target.value)}
          aria-label="Filtrer les inscriptions"
        >
          <MenuItem value="TOUS">Tous les statuts</MenuItem>
          <MenuItem value="VALIDE">Actifs</MenuItem>
          <MenuItem value="EN_ATTENTE">En attente</MenuItem>
          <MenuItem value="REFUSE">Refusés</MenuItem>
        </TextField>
      </Box>

      <Box sx={{ mt: 1.2 }}>
        {visibleStudents.length === 0 ? (
          <Typography
            color="text.secondary"
            sx={{ py: 4, textAlign: "center", fontSize: 13 }}
          >
            Aucun étudiant ne correspond aux filtres.
          </Typography>
        ) : (
          visibleStudents.slice(0, 6).map((enrollment) => (
            <Box
              key={enrollment.id}
              sx={{
                py: 1,
                display: "flex",
                alignItems: "center",
                gap: 1,
                borderTop: "1px solid #edf0f7",
              }}
            >
              <Avatar
                sx={{
                  width: 34,
                  height: 34,
                  bgcolor: "#eef0ff",
                  color: "#5364f4",
                  fontSize: 12,
                  fontWeight: 800,
                }}
              >
                {enrollment.elevePrenom[0]}
                {enrollment.eleveNom[0]}
              </Avatar>
              <Box sx={{ flex: 1, minWidth: 0 }}>
                <Typography noWrap sx={{ fontSize: 12.5, fontWeight: 800 }}>
                  {enrollment.elevePrenom} {enrollment.eleveNom}
                </Typography>
                <Typography noWrap color="text.secondary" sx={{ fontSize: 10.5 }}>
                  {enrollment.eleveEmail}
                </Typography>
              </Box>
              <Chip
                size="small"
                label={
                  enrollment.statut === "VALIDE"
                    ? "Actif"
                    : enrollment.statut === "EN_ATTENTE"
                      ? "En attente"
                      : "Refusé"
                }
                sx={{
                  height: 23,
                  color: enrollment.statut === "VALIDE" ? "#16864f" : "#596783",
                  bgcolor: enrollment.statut === "VALIDE" ? "#e5f7ec" : "#eef1f7",
                  fontSize: 10.5,
                  fontWeight: 750,
                }}
              />
              <MoreVertRoundedIcon sx={{ fontSize: 17, color: "#7a849d" }} />
            </Box>
          ))
        )}
      </Box>
    </Paper>
  );
}
