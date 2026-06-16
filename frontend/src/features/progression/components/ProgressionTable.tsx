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
import {
  cardSx,
  LH_PRIMARY_SOFT,
  PROGRESSION_STATUS_STYLES,
} from "../../../styles/tokens";
import type { ProfessorStudentProgress } from "../services/progressionApi";
import { getProgressLevel } from "./progressionLevel";

export function ProgressionTable({
  progressions,
}: {
  progressions: ProfessorStudentProgress[];
}) {
  if (!progressions.length) {
    return (
      <Paper
        elevation={0}
        sx={{ ...cardSx, mt: 2, py: 8, textAlign: "center" }}
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
      sx={{ ...cardSx, mt: 2 }}
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
            const level = getProgressLevel(
              item.pourcentage,
              item.totalChapitres,
            );
            const style = PROGRESSION_STATUS_STYLES[level];
            return (
              <TableRow key={item.inscriptionId} hover>
                <TableCell>
                  <Box sx={{ display: "flex", alignItems: "center", gap: 1.2 }}>
                    <Avatar sx={{ width: 38, height: 38, bgcolor: LH_PRIMARY_SOFT, color: "primary.main", fontSize: 13, fontWeight: 850 }}>
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
                    sx={{ color: style.color, bgcolor: style.bgcolor, fontWeight: 800 }}
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
