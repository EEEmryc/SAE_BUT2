import {
  InputAdornment,
  MenuItem,
  Paper,
  TextField,
} from "@mui/material";
import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import type { ProfessorStudentProgress } from "../services/progressionApi";
import type { ProgressLevel } from "./progressionLevel";

export type ProgressionFilterValues = {
  courseId: string;
  studentId: string;
  level: ProgressLevel | "TOUS";
  search: string;
};

export function ProgressionFilters({
  progressions,
  values,
  onChange,
}: {
  progressions: ProfessorStudentProgress[];
  values: ProgressionFilterValues;
  onChange: (values: ProgressionFilterValues) => void;
}) {
  const courses = Array.from(
    new Map(progressions.map((item) => [item.coursId, item.coursTitre])).entries(),
  );
  const students = Array.from(
    new Map(
      progressions.map((item) => [
        item.eleveId,
        `${item.elevePrenom} ${item.eleveNom}`,
      ]),
    ).entries(),
  );

  return (
    <Paper
      elevation={0}
      sx={{
        mt: 2,
        p: 2,
        display: "grid",
        gridTemplateColumns: { xs: "1fr", md: "minmax(220px,1.5fr) repeat(3,minmax(170px,1fr))" },
        gap: 1.5,
        border: "1px solid #e2e6f4",
        borderRadius: 3,
      }}
    >
      <TextField
        size="small"
        label="Rechercher"
        placeholder="Nom ou adresse e-mail"
        value={values.search}
        onChange={(event) => onChange({ ...values, search: event.target.value })}
        slotProps={{
          input: {
            startAdornment: (
              <InputAdornment position="start">
                <SearchRoundedIcon />
              </InputAdornment>
            ),
          },
        }}
      />
      <TextField select size="small" label="Cours" value={values.courseId} onChange={(event) => onChange({ ...values, courseId: event.target.value })}>
        <MenuItem value="TOUS">Tous les cours</MenuItem>
        {courses.map(([id, title]) => <MenuItem key={id} value={String(id)}>{title}</MenuItem>)}
      </TextField>
      <TextField select size="small" label="Étudiant" value={values.studentId} onChange={(event) => onChange({ ...values, studentId: event.target.value })}>
        <MenuItem value="TOUS">Tous les étudiants</MenuItem>
        {students.map(([id, name]) => <MenuItem key={id} value={String(id)}>{name}</MenuItem>)}
      </TextField>
      <TextField
        select
        size="small"
        label="Niveau"
        value={values.level}
        onChange={(event) =>
          onChange({ ...values, level: event.target.value as ProgressLevel | "TOUS" })
        }
      >
        <MenuItem value="TOUS">Tous les niveaux</MenuItem>
        <MenuItem value="SANS_CONTENU">Sans contenu</MenuItem>
        <MenuItem value="FAIBLE">Faible</MenuItem>
        <MenuItem value="MOYEN">Moyen</MenuItem>
        <MenuItem value="BON">Bon</MenuItem>
        <MenuItem value="TERMINE">Terminé</MenuItem>
      </TextField>
    </Paper>
  );
}
