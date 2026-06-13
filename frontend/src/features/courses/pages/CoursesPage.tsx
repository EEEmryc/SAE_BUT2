import { useMemo, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  InputAdornment,
  Paper,
  Snackbar,
  TextField,
  Typography,
} from "@mui/material";
import AddRoundedIcon from "@mui/icons-material/AddRounded";
import ArrowForwardRoundedIcon from "@mui/icons-material/ArrowForwardRounded";
import MenuBookRoundedIcon from "@mui/icons-material/MenuBookRounded";
import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../../store/authStore";
import { getApiErrorMessage } from "../../auth/api/apiError";
import type { CourseStatus } from "../api/coursesApi";
import { CourseFormDialog } from "../components/CourseFormDialog";
import { useCourses } from "../hooks/useCourses";

const statusLabels: Record<CourseStatus, string> = {
  DRAFT: "Brouillon",
  PUBLISHED: "Publié",
  VALIDE: "Validé",
  ARCHIVE: "Archivé",
};

const statusColors: Record<CourseStatus, { color: string; bgcolor: string }> = {
  DRAFT: { color: "#6b7280", bgcolor: "#eef0f4" },
  PUBLISHED: { color: "#168b5b", bgcolor: "#e6f8ef" },
  VALIDE: { color: "#2f62d9", bgcolor: "#e9f0ff" },
  ARCHIVE: { color: "#a04e1c", bgcolor: "#fff0df" },
};

export function CoursesPage() {
  const navigate = useNavigate();
  const role = useAuthStore((state) => state.user?.role);
  const coursesQuery = useCourses();
  const [search, setSearch] = useState("");
  const [formOpen, setFormOpen] = useState(false);
  const [success, setSuccess] = useState("");
  const isProfessor = role === "PROFESSEUR";
  const courses = useMemo(() => {
    const normalized = search.trim().toLocaleLowerCase("fr");
    return (coursesQuery.data ?? []).filter((course) =>
      `${course.titre} ${course.description}`
        .toLocaleLowerCase("fr")
        .includes(normalized),
    );
  }, [coursesQuery.data, search]);

  if (coursesQuery.isPending) {
    return (
      <Box sx={{ minHeight: 420, display: "grid", placeItems: "center" }}>
        <CircularProgress aria-label="Chargement des cours" />
      </Box>
    );
  }

  if (coursesQuery.isError) {
    return (
      <Alert severity="error">
        Impossible de charger les cours. {getApiErrorMessage(coursesQuery.error)}
      </Alert>
    );
  }

  return (
    <Box sx={{ maxWidth: 1540, mx: "auto" }}>
      <Box
        sx={{
          display: "flex",
          flexDirection: { xs: "column", sm: "row" },
          alignItems: { sm: "center" },
          justifyContent: "space-between",
          gap: 2,
        }}
      >
        <Box>
          <Typography
            component="h1"
            sx={{
              fontSize: { xs: 30, sm: 38 },
              fontWeight: 850,
              letterSpacing: "-0.04em",
            }}
          >
            Mes cours
          </Typography>
          <Typography color="text.secondary" sx={{ mt: 0.5 }}>
            {isProfessor
              ? "Créez vos cours et organisez leurs chapitres, ressources et étudiants."
              : "Retrouvez les cours auxquels vous êtes inscrit."}
          </Typography>
        </Box>
        {isProfessor && (
          <Button
            variant="contained"
            startIcon={<AddRoundedIcon />}
            onClick={() => setFormOpen(true)}
            sx={{
              alignSelf: { xs: "stretch", sm: "center" },
              minHeight: 44,
              color: "#fff",
              background: "linear-gradient(110deg,#4056f4,#7458f6)",
            }}
          >
            Créer un cours
          </Button>
        )}
      </Box>

      <Paper
        elevation={0}
        sx={{
          mt: 3,
          p: 2,
          border: "1px solid #e3e7f3",
          borderRadius: 3,
        }}
      >
        <TextField
          fullWidth
          size="small"
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          placeholder="Rechercher un cours..."
          slotProps={{
            input: {
              startAdornment: (
                <InputAdornment position="start">
                  <SearchRoundedIcon fontSize="small" />
                </InputAdornment>
              ),
            },
            htmlInput: { "aria-label": "Rechercher un cours" },
          }}
        />
      </Paper>

      {courses.length === 0 ? (
        <Paper
          elevation={0}
          sx={{
            mt: 2.5,
            minHeight: 340,
            display: "grid",
            placeItems: "center",
            textAlign: "center",
            border: "1px solid #e3e7f3",
            borderRadius: 3.5,
          }}
        >
          <Box>
            <MenuBookRoundedIcon
              sx={{ fontSize: 58, color: "rgba(79,95,247,0.35)" }}
            />
            <Typography sx={{ mt: 1, fontSize: 20, fontWeight: 800 }}>
              Aucun cours trouvé
            </Typography>
            <Typography color="text.secondary">
              {isProfessor
                ? "Créez votre premier cours pour commencer."
                : "Aucun cours n'est encore associé à votre compte."}
            </Typography>
          </Box>
        </Paper>
      ) : (
        <Box
          sx={{
            mt: 2.5,
            display: "grid",
            gridTemplateColumns: {
              xs: "1fr",
              md: "repeat(2, minmax(0, 1fr))",
              xl: "repeat(3, minmax(0, 1fr))",
            },
            gap: 2.25,
          }}
        >
          {courses.map((course, index) => (
            <Paper
              key={course.id}
              elevation={0}
              sx={{
                overflow: "hidden",
                border: "1px solid #e3e7f3",
                borderRadius: 3.5,
                boxShadow: "0 16px 42px rgba(54,64,125,0.07)",
              }}
            >
              <Box
                sx={{
                  height: 112,
                  p: 2.5,
                  display: "flex",
                  alignItems: "flex-end",
                  color: "#fff",
                  background:
                    index % 3 === 0
                      ? "linear-gradient(135deg,#4056f4,#7659f6)"
                      : index % 3 === 1
                        ? "linear-gradient(135deg,#167db7,#42b5d7)"
                        : "linear-gradient(135deg,#6d4bd8,#a85be5)",
                }}
              >
                <MenuBookRoundedIcon sx={{ fontSize: 44, opacity: 0.9 }} />
              </Box>
              <Box sx={{ p: 2.5 }}>
                <Box
                  sx={{
                    display: "flex",
                    alignItems: "flex-start",
                    justifyContent: "space-between",
                    gap: 1,
                  }}
                >
                  <Typography sx={{ fontSize: 20, fontWeight: 850 }}>
                    {course.titre}
                  </Typography>
                  <Chip
                    size="small"
                    label={statusLabels[course.statut]}
                    sx={{
                      ...statusColors[course.statut],
                      fontWeight: 750,
                    }}
                  />
                </Box>
                <Typography
                  color="text.secondary"
                  sx={{
                    mt: 1,
                    minHeight: 42,
                    display: "-webkit-box",
                    overflow: "hidden",
                    WebkitBoxOrient: "vertical",
                    WebkitLineClamp: 2,
                  }}
                >
                  {course.description}
                </Typography>
                <Typography color="text.secondary" sx={{ mt: 2, fontSize: 13 }}>
                  {course.visibleCatalogue
                    ? "Visible dans le catalogue"
                    : "Cours privé"}
                </Typography>
                {isProfessor && (
                  <Button
                    fullWidth
                    endIcon={<ArrowForwardRoundedIcon />}
                    onClick={() => navigate(`/dashboard/courses/${course.id}`)}
                    sx={{ mt: 2, justifyContent: "space-between" }}
                  >
                    Gérer le cours
                  </Button>
                )}
              </Box>
            </Paper>
          ))}
        </Box>
      )}

      <CourseFormDialog
        open={formOpen}
        onClose={() => setFormOpen(false)}
        onSaved={setSuccess}
      />
      <Snackbar
        open={Boolean(success)}
        autoHideDuration={3500}
        onClose={() => setSuccess("")}
        message={success}
      />
    </Box>
  );
}
