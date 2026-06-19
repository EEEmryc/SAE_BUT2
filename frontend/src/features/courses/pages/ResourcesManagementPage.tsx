import { useMemo, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Paper,
  Snackbar,
  Typography,
} from "@mui/material";
import AddRoundedIcon from "@mui/icons-material/AddRounded";
import CloudUploadRoundedIcon from "@mui/icons-material/CloudUploadRounded";
import { getApiErrorMessage } from "../../auth/services/apiError";
import type { CourseResource } from "../services/coursesApi";
import { CourseSelectorCard } from "../components/CourseSelectorCard";
import { ResourceDialog } from "../components/ResourceDialog";
import { ResourceList } from "../components/ResourceList";
import {
  useChapters,
  useCourseResources,
  useCourses,
  useDeleteResource,
} from "../hooks/useCourses";

export function ResourcesManagementPage() {
  const coursesQuery = useCourses();
  const [chosenCourseId, setChosenCourseId] = useState<number | null>(null);
  const selectedId = chosenCourseId ?? coursesQuery.data?.[0]?.id ?? Number.NaN;
  const chaptersQuery = useChapters(selectedId);
  const resourcesQuery = useCourseResources(selectedId);
  const deleteResource = useDeleteResource(selectedId);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [message, setMessage] = useState("");

  const resources = useMemo(
    () =>
      [...(resourcesQuery.data ?? [])].sort(
        (first, second) =>
          new Date(second.dateCreation).getTime() -
          new Date(first.dateCreation).getTime(),
      ),
    [resourcesQuery.data],
  );

  if (coursesQuery.isPending) {
    return <LoadingState />;
  }
  if (coursesQuery.isError) {
    return <Alert severity="error">{getApiErrorMessage(coursesQuery.error)}</Alert>;
  }
  const courses = coursesQuery.data ?? [];
  if (courses.length === 0) {
    return <Alert severity="info">Créez d’abord un cours.</Alert>;
  }

  const error = chaptersQuery.error ?? resourcesQuery.error;

  return (
    <Box sx={{ maxWidth: 1540, mx: "auto" }}>
      <Box
        sx={{
          display: "flex",
          flexDirection: { xs: "column", sm: "row" },
          justifyContent: "space-between",
          gap: 2,
        }}
      >
        <Box>
          <Typography component="h1" sx={{ fontSize: 32, fontWeight: 850 }}>
            Gestion des ressources
          </Typography>
          <Typography color="text.secondary">
            Choisissez un cours puis partagez ses documents pédagogiques.
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<AddRoundedIcon />}
          disabled={(chaptersQuery.data?.length ?? 0) === 0}
          onClick={() => setDialogOpen(true)}
          sx={{ color: "#fff", alignSelf: { sm: "center" } }}
        >
          Ajouter une ressource
        </Button>
      </Box>

      <Box sx={{ mt: 2.5 }}>
        <CourseSelectorCard
          courses={courses}
          selectedId={selectedId}
          onChange={setChosenCourseId}
        />
      </Box>

      {chaptersQuery.isPending || resourcesQuery.isPending ? (
        <LoadingState />
      ) : error ? (
        <Alert severity="error" sx={{ mt: 2 }}>
          {getApiErrorMessage(error)}
        </Alert>
      ) : (
        <Box
          sx={{
            mt: 2.5,
            display: "grid",
            gridTemplateColumns: { xs: "1fr", lg: "minmax(0,1.7fr) 360px" },
            gap: 2.25,
            alignItems: "start",
          }}
        >
          <ResourceList
            title={`Ressources du cours (${resources.length})`}
            resources={resources}
            onDelete={async (resource: CourseResource) => {
              await deleteResource.mutateAsync({
                chapterId: resource.chapitreId,
                resourceId: resource.id,
              });
              setMessage("Ressource supprimée avec succès");
            }}
          />
          <Paper
            elevation={0}
            sx={{
              p: 2.5,
              border: "1px solid #e1e6f2",
              borderRadius: 3,
            }}
          >
            <CloudUploadRoundedIcon sx={{ fontSize: 45, color: "#5364f4" }} />
            <Typography sx={{ mt: 1, fontSize: 18, fontWeight: 850 }}>
              Fichiers pris en charge
            </Typography>
            <Typography color="text.secondary" sx={{ mt: 0.5, lineHeight: 1.6 }}>
              Chaque fichier est associé à un chapitre du cours sélectionné et
              devient disponible aux étudiants inscrits.
            </Typography>
            <Box sx={{ mt: 2, display: "flex", flexWrap: "wrap", gap: 0.75 }}>
              {["PDF", "Excel", "Word", "ZIP", "Vidéo"].map((type) => (
                <Chip key={type} label={type} />
              ))}
            </Box>
            <Typography color="text.secondary" sx={{ mt: 2, fontSize: 12 }}>
              Taille maximale : 1 Go par fichier.
            </Typography>
          </Paper>
        </Box>
      )}

      <ResourceDialog
        key={`${dialogOpen}-${selectedId}`}
        open={dialogOpen}
        courseId={selectedId}
        chapters={chaptersQuery.data ?? []}
        onClose={() => setDialogOpen(false)}
        onSaved={setMessage}
      />
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
    <Box sx={{ minHeight: 280, display: "grid", placeItems: "center" }}>
      <CircularProgress aria-label="Chargement des ressources" />
    </Box>
  );
}
