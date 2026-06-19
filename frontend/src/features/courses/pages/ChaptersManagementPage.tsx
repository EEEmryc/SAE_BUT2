import { useMemo, useState } from "react";
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Paper,
  Snackbar,
  Typography,
} from "@mui/material";
import OpenInNewRoundedIcon from "@mui/icons-material/OpenInNewRounded";
import { useNavigate } from "react-router-dom";
import { getApiErrorMessage } from "../../auth/services/apiError";
import { coursesApi, type Chapter } from "../services/coursesApi";
import { ChapterDialog } from "../components/ChapterDialog";
import { ChapterList } from "../components/ChapterList";
import { CourseSelectorCard } from "../components/CourseSelectorCard";
import { ResourceDialog } from "../components/ResourceDialog";
import { SummaryCard } from "../components/SummaryCard";
import {
  useChapters,
  useCourseResources,
  useCourseSummary,
  useCourses,
  useDeleteChapter,
} from "../hooks/useCourses";

export function ChaptersManagementPage() {
  const navigate = useNavigate();
  const coursesQuery = useCourses();
  const [chosenCourseId, setChosenCourseId] = useState<number | null>(null);
  const selectedId = chosenCourseId ?? coursesQuery.data?.[0]?.id ?? Number.NaN;
  const chaptersQuery = useChapters(selectedId);
  const resourcesQuery = useCourseResources(selectedId);
  const summaryQuery = useCourseSummary(selectedId);
  const deleteChapter = useDeleteChapter(selectedId);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editedChapter, setEditedChapter] = useState<Chapter | null>(null);
  const [chapterToDelete, setChapterToDelete] = useState<Chapter | null>(null);
  const [resourceChapter, setResourceChapter] = useState<Chapter | null>(null);
  const [message, setMessage] = useState("");

  const chapters = useMemo(
    () =>
      [...(chaptersQuery.data ?? [])].sort(
        (first, second) => first.ordre - second.ordre,
      ),
    [chaptersQuery.data],
  );

  if (coursesQuery.isPending) {
    return <LoadingState label="Chargement des cours" />;
  }
  if (coursesQuery.isError) {
    return <Alert severity="error">{getApiErrorMessage(coursesQuery.error)}</Alert>;
  }
  const courses = coursesQuery.data ?? [];
  if (courses.length === 0) {
    return <Alert severity="info">Créez d’abord un cours.</Alert>;
  }

  const selectedCourse = courses.find((course) => course.id === selectedId);
  const loading =
    chaptersQuery.isPending || resourcesQuery.isPending || summaryQuery.isPending;
  const error =
    chaptersQuery.error ?? resourcesQuery.error ?? summaryQuery.error;

  return (
    <Box sx={{ maxWidth: 1540, mx: "auto" }}>
      <Typography component="h1" sx={{ fontSize: 32, fontWeight: 850 }}>
        Gestion des chapitres
      </Typography>
      <Typography color="text.secondary" sx={{ mt: 0.4 }}>
        Choisissez un cours puis organisez son contenu pédagogique.
      </Typography>

      <Box sx={{ mt: 2.5 }}>
        <CourseSelectorCard
          courses={courses}
          selectedId={selectedId}
          onChange={setChosenCourseId}
        />
      </Box>

      {loading ? (
        <LoadingState label="Chargement des chapitres" />
      ) : error ? (
        <Alert severity="error" sx={{ mt: 2 }}>
          {getApiErrorMessage(error)}
        </Alert>
      ) : (
        <Box
          sx={{
            mt: 2.5,
            display: "grid",
            gridTemplateColumns: { xs: "1fr", lg: "minmax(0,2fr) 340px" },
            gap: 2.25,
            alignItems: "start",
          }}
        >
          <Paper
            elevation={0}
            sx={{ p: 2.5, border: "1px solid #e1e6f2", borderRadius: 3 }}
          >
            <ChapterList
              chapters={chapters}
              resources={resourcesQuery.data ?? []}
              courseStatus={selectedCourse?.statut ?? "DRAFT"}
              onAdd={() => {
                setEditedChapter(null);
                setDialogOpen(true);
              }}
              onEdit={(chapter) => {
                setEditedChapter(chapter);
                setDialogOpen(true);
              }}
              onDelete={setChapterToDelete}
              onAddResource={setResourceChapter}
              onDownloadFile={(chapter) => {
                if (chapter.fichierPrincipalUrl && chapter.fichierPrincipalNom) {
                  void coursesApi.downloadFile(
                    chapter.fichierPrincipalUrl,
                    chapter.fichierPrincipalNom,
                  );
                }
              }}
            />
          </Paper>
          <Box sx={{ display: "grid", gap: 1.5 }}>
            <SummaryCard
              summary={
                summaryQuery.data ?? {
                  students: 0,
                  chapters: chapters.length,
                  resources: resourcesQuery.data?.length ?? 0,
                  averageProgress: 0,
                }
              }
            />
            <Button
              variant="outlined"
              endIcon={<OpenInNewRoundedIcon />}
              onClick={() => navigate(`/dashboard/courses/${selectedId}`)}
            >
              Voir le cours
            </Button>
          </Box>
        </Box>
      )}

      <ChapterDialog
        open={dialogOpen}
        courseId={selectedId}
        chapter={editedChapter}
        nextOrder={chapters.length + 1}
        onClose={() => setDialogOpen(false)}
        onSaved={setMessage}
      />
      <ResourceDialog
        key={`${Boolean(resourceChapter)}-${resourceChapter?.id ?? "none"}`}
        open={Boolean(resourceChapter)}
        courseId={selectedId}
        chapters={chapters}
        defaultChapterId={resourceChapter?.id}
        onClose={() => setResourceChapter(null)}
        onSaved={setMessage}
      />
      <Dialog
        open={Boolean(chapterToDelete)}
        onClose={() => setChapterToDelete(null)}
      >
        <DialogTitle sx={{ fontWeight: 850 }}>Supprimer le chapitre</DialogTitle>
        <DialogContent>
          Cette action supprimera aussi les ressources associées à «{" "}
          {chapterToDelete?.titre} ».
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setChapterToDelete(null)}>Annuler</Button>
          <Button
            color="error"
            variant="contained"
            onClick={async () => {
              if (!chapterToDelete) return;
              await deleteChapter.mutateAsync(chapterToDelete.id);
              setChapterToDelete(null);
              setMessage("Chapitre supprimé avec succès");
            }}
          >
            Supprimer
          </Button>
        </DialogActions>
      </Dialog>
      <Snackbar
        open={Boolean(message)}
        autoHideDuration={3500}
        message={message}
        onClose={() => setMessage("")}
      />
    </Box>
  );
}

function LoadingState({ label }: { label: string }) {
  return (
    <Box sx={{ minHeight: 280, display: "grid", placeItems: "center" }}>
      <CircularProgress aria-label={label} />
    </Box>
  );
}
