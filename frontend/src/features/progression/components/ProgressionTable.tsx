import {
  Avatar,
  Box,
  Chip,
  LinearProgress,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import type { ProfessorStudentProgress } from "../services/progressionApi";
import { getProgressLevel } from "./progressionLevel";

const styles = {
  FAIBLE: { label: "Faible", color: "#c23e4b", background: "#fdecef" },
  MOYEN: { label: "Moyen", color: "#a96308", background: "#fff1d9" },
  BON: { label: "Bon", color: "#16834f", background: "#e4f7ec" },
  TERMINE: { label: "Terminé", color: "#147545", background: "#dff5e8" },
};

export function ProgressionTable({
  progressions,
}: {
  progressions: ProfessorStudentProgress[];
}) {
  if (!progressions.length) {
    return (
      <Paper
        elevation={0}
        sx={{ mt: 2, py: 8, textAlign: "center", border: "1px solid #e2e6f4", borderRadius: 3 }}
      >
        <Typography sx={{ fontWeight: 850 }}>
          Aucune progression ne correspond aux filtres
        </Typography>
        <Typography color="text.secondary" sx={{ mt: 0.5 }}>
          Les inscriptions validées de vos cours apparaîtront ici.
        </Typography>
      </Paper>
    );
  }

  return (
    <TableContainer
      component={Paper}
      elevation={0}
      sx={{ mt: 2, border: "1px solid #e2e6f4", borderRadius: 3 }}
    >
      <Table sx={{ minWidth: 960 }}>
        <TableHead>
          <TableRow sx={{ bgcolor: "#f8f9ff" }}>
            <TableCell>Étudiant</TableCell>
            <TableCell>Cours concerné</TableCell>
            <TableCell align="center">Chapitres</TableCell>
            <TableCell sx={{ minWidth: 210 }}>Progression</TableCell>
            <TableCell>Dernière activité</TableCell>
            <TableCell>Statut</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {progressions.map((item) => {
            const level = getProgressLevel(item.pourcentage);
            const style = styles[level];
            return (
              <TableRow key={item.inscriptionId} hover>
                <TableCell>
                  <Box sx={{ display: "flex", alignItems: "center", gap: 1.2 }}>
                    <Avatar sx={{ width: 38, height: 38, bgcolor: "rgba(79,95,247,.12)", color: "primary.main", fontSize: 13, fontWeight: 850 }}>
                      {item.elevePrenom[0]}{item.eleveNom[0]}
                    </Avatar>
                    <Box>
                      <Typography sx={{ fontWeight: 800 }}>
                        {item.elevePrenom} {item.eleveNom}
                      </Typography>
                      <Typography color="text.secondary" sx={{ fontSize: 12 }}>
                        {item.eleveEmail}
                      </Typography>
                    </Box>
                  </Box>
                </TableCell>
                <TableCell>{item.coursTitre}</TableCell>
                <TableCell align="center">
                  <Typography sx={{ fontWeight: 800 }}>
                    {item.chapitresTermines}/{item.totalChapitres}
                  </Typography>
                </TableCell>
                <TableCell>
                  <Box sx={{ display: "flex", alignItems: "center", gap: 1.2 }}>
                    <LinearProgress
                      variant="determinate"
                      value={item.pourcentage}
                      color={level === "FAIBLE" ? "error" : level === "MOYEN" ? "warning" : "success"}
                      sx={{ flex: 1, height: 8, borderRadius: 8 }}
                    />
                    <Typography sx={{ minWidth: 38, fontWeight: 850 }}>
                      {item.pourcentage}%
                    </Typography>
                  </Box>
                </TableCell>
                <TableCell>{formatActivity(item.derniereActivite)}</TableCell>
                <TableCell>
                  <Chip
                    size="small"
                    label={style.label}
                    sx={{ color: style.color, bgcolor: style.background, fontWeight: 800 }}
                  />
                </TableCell>
              </TableRow>
            );
          })}
        </TableBody>
      </Table>
    </TableContainer>
  );
}

function formatActivity(value: string | null) {
  if (!value) return "Aucune activité";
  return new Intl.DateTimeFormat("fr-FR", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(value));
}
