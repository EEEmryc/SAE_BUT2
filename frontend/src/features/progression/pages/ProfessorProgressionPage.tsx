import { Alert, Box, CircularProgress, Typography } from "@mui/material";
import { useMemo, useState } from "react";
import { getApiErrorMessage } from "../../auth/services/apiError";
import {
  ProgressionFilters,
  type ProgressionFilterValues,
} from "../components/ProgressionFilters";
import { ProgressionTable } from "../components/ProgressionTable";
import { getProgressLevel } from "../components/progressionLevel";
import { ProgressionStats } from "../components/ProgressionStats";
import { useProfessorProgress } from "../hooks/useProfessorProgress";

const initialFilters: ProgressionFilterValues = {
  courseId: "TOUS",
  studentId: "TOUS",
  level: "TOUS",
  search: "",
};

export function ProfessorProgressionPage() {
  const query = useProfessorProgress();
  const [filters, setFilters] = useState(initialFilters);
  const filtered = useMemo(() => {
    const search = filters.search.trim().toLocaleLowerCase("fr");
    return (query.data ?? []).filter((item) => {
      const identity =
        `${item.elevePrenom} ${item.eleveNom} ${item.eleveEmail}`
          .toLocaleLowerCase("fr");
      return (
        (!search || identity.includes(search)) &&
        (filters.courseId === "TOUS" ||
          String(item.coursId) === filters.courseId) &&
        (filters.studentId === "TOUS" ||
          String(item.eleveId) === filters.studentId) &&
        (filters.level === "TOUS" ||
          getProgressLevel(item.pourcentage) === filters.level)
      );
    });
  }, [filters, query.data]);

  if (query.isPending) {
    return (
      <Box sx={{ minHeight: 460, display: "grid", placeItems: "center" }}>
        <CircularProgress aria-label="Chargement des progressions" />
      </Box>
    );
  }
  if (query.isError) {
    return <Alert severity="error">{getApiErrorMessage(query.error)}</Alert>;
  }

  const progressions = query.data ?? [];
  return (
    <Box sx={{ maxWidth: 1500, mx: "auto" }}>
      <Typography component="h1" sx={{ fontSize: { xs: 30, md: 38 }, fontWeight: 900 }}>
        Progression des étudiants
      </Typography>
      <Typography color="text.secondary" sx={{ mt: 0.5 }}>
        Suivez l’avancement des étudiants inscrits à vos cours.
      </Typography>
      <ProgressionStats progressions={progressions} />
      <ProgressionFilters
        progressions={progressions}
        values={filters}
        onChange={setFilters}
      />
      <ProgressionTable progressions={filtered} />
    </Box>
  );
}
