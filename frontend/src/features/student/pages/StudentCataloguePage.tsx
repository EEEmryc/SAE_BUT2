import { useMemo, useState } from "react";
import {
  Alert,
  Box,
  CircularProgress,
  InputAdornment,
  Paper,
  Snackbar,
  TextField,
  Typography,
} from "@mui/material";
import MenuBookRoundedIcon from "@mui/icons-material/MenuBookRounded";
import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import { useNavigate } from "react-router-dom";
import { getApiErrorMessage } from "../../auth/services/apiError";
import { StudentCourseCard } from "../components/StudentCourseCard";
import {
  useEnrollInCourse,
  useStudentCatalogue,
} from "../hooks/useStudentLearning";

export function StudentCataloguePage() {
  const navigate = useNavigate();
  const catalogueQuery = useStudentCatalogue();
  const enrollMutation = useEnrollInCourse();
  const [search, setSearch] = useState("");
  const [message, setMessage] = useState("");
  const [enrollingId, setEnrollingId] = useState<number | null>(null);

  const courses = useMemo(() => {
    const normalized = search.trim().toLocaleLowerCase("fr");
    return (catalogueQuery.data ?? []).filter((course) =>
      `${course.titre} ${course.description} ${course.profPrenom} ${course.profNom}`
        .toLocaleLowerCase("fr")
        .includes(normalized),
    );
  }, [catalogueQuery.data, search]);

  if (catalogueQuery.isPending) {
    return <LoadingState />;
  }
  if (catalogueQuery.isError) {
    return <Alert severity="error">{getApiErrorMessage(catalogueQuery.error)}</Alert>;
  }

  return (
    <Box sx={{ maxWidth: 1540, mx: "auto" }}>
      <Box sx={{ display: "flex", alignItems: "center", gap: 1.8 }}>
        <Box
          sx={{
            width: 58,
            height: 58,
            display: "grid",
            placeItems: "center",
            color: "#5364f4",
            bgcolor: "#fff",
            border: "1px solid #dce2f4",
            borderRadius: 2.5,
          }}
        >
          <MenuBookRoundedIcon sx={{ fontSize: 33 }} />
        </Box>
        <Box>
          <Typography component="h1" sx={{ fontSize: 32, fontWeight: 900 }}>
            Catalogue des cours
          </Typography>
          <Typography color="text.secondary">
            Decouvrez les cours disponibles et suivez vos demandes d'inscription.
          </Typography>
        </Box>
      </Box>

      <Paper
        elevation={0}
        sx={{ mt: 2.5, p: 2.25, border: "1px solid #e0e5f3", borderRadius: 3 }}
      >
        <TextField
          size="small"
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          placeholder="Rechercher un cours ou un professeur..."
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
      </Paper>

      <Typography sx={{ mt: 2.5, mb: 1.5, fontWeight: 800 }}>
        {courses.length} cours disponible{courses.length > 1 ? "s" : ""}
      </Typography>

      {courses.length === 0 ? (
        <Alert severity="info">
          {search.trim()
            ? "Aucun cours ne correspond a votre recherche."
            : "Aucun cours publie n'est disponible dans le catalogue pour le moment."}
        </Alert>
      ) : (
        <Box
          sx={{
            display: "grid",
            gridTemplateColumns: {
              xs: "1fr",
              sm: "repeat(2,minmax(0,1fr))",
              xl: "repeat(4,minmax(0,1fr))",
            },
            gap: 2,
          }}
        >
          {courses.map((course, index) => (
            <StudentCourseCard
              key={course.id}
              course={course}
              index={index}
              enrolling={enrollMutation.isPending && enrollingId === course.id}
              onConsult={() =>
                navigate(`/dashboard/student/courses/${course.id}`)
              }
              onEnroll={async () => {
                setEnrollingId(course.id);
                try {
                  await enrollMutation.mutateAsync(course.id);
                  setMessage("Demande d'inscription envoyee");
                } catch {
                  // L'erreur est affichee par l'alerte de la mutation.
                } finally {
                  setEnrollingId(null);
                }
              }}
            />
          ))}
        </Box>
      )}

      {enrollMutation.isError && (
        <Alert severity="error" sx={{ mt: 2 }}>
          {getApiErrorMessage(enrollMutation.error)}
        </Alert>
      )}
      <Snackbar
        open={Boolean(message)}
        autoHideDuration={3500}
        message={message}
        onClose={() => setMessage("")}
      />
    </Box>
  );
}

function LoadingState() {
  return (
    <Box sx={{ minHeight: 420, display: "grid", placeItems: "center" }}>
      <CircularProgress aria-label="Chargement du catalogue" />
    </Box>
  );
}
