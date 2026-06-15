import { useMemo, useState } from "react";
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  LinearProgress,
  Paper,
  Snackbar,
  Typography,
} from "@mui/material";
import ArrowBackRoundedIcon from "@mui/icons-material/ArrowBackRounded";
import AutoStoriesRoundedIcon from "@mui/icons-material/AutoStoriesRounded";
import DownloadRoundedIcon from "@mui/icons-material/DownloadRounded";
import FolderOpenRoundedIcon from "@mui/icons-material/FolderOpenRounded";
import PersonOutlineRoundedIcon from "@mui/icons-material/PersonOutlineRounded";
import { useNavigate, useParams } from "react-router-dom";
import { getApiErrorMessage } from "../../auth/services/apiError";
import { StudentChapterList } from "../components/StudentChapterList";
import { studentLearningApi } from "../services/studentLearningApi";
import {
  useCompleteChapter,
  useCourseProgress,
  useStartChapter,
  useStudentChapters,
  useStudentCourse,
  useStudentResources,
} from "../hooks/useStudentLearning";

export function StudentCourseDetailPage() {
  const navigate = useNavigate();
  const { courseId: courseIdParam } = useParams();
  const courseId = Number(courseIdParam);
  const courseQuery = useStudentCourse(courseId);
  const chaptersQuery = useStudentChapters(courseId);
  const resourcesQuery = useStudentResources(courseId);
  const progressQuery = useCourseProgress(courseId);
  const startChapter = useStartChapter(courseId);
  const completeChapter = useCompleteChapter(courseId);
  const [completingId, setCompletingId] = useState<number | null>(null);
  const [message, setMessage] = useState("");

  const chapters = useMemo(
    () => [...(chaptersQuery.data ?? [])].sort((a, b) => a.ordre - b.ordre),
    [chaptersQuery.data],
  );

  const queries = [courseQuery, chaptersQuery, resourcesQuery, progressQuery];
  if (!Number.isFinite(courseId)) {
    return <Alert severity="error">Identifiant de cours invalide.</Alert>;
  }
  if (queries.some((query) => query.isPending)) {
    return (
      <Box sx={{ minHeight: 500, display: "grid", placeItems: "center" }}>
        <CircularProgress aria-label="Chargement du cours" />
      </Box>
    );
  }
  const error = queries.find((query) => query.error)?.error;
  if (error || !courseQuery.data || !progressQuery.data) {
    return (
      <Alert severity="error">
        Impossible de consulter ce cours. {error ? getApiErrorMessage(error) : ""}
      </Alert>
    );
  }

  const course = courseQuery.data;
  const progress = progressQuery.data;

  return (
    <Box sx={{ maxWidth: 1460, mx: "auto" }}>
      <Button
        startIcon={<ArrowBackRoundedIcon />}
        onClick={() => navigate("/dashboard/courses")}
        sx={{ minHeight: 36, mb: 1 }}
      >
        Mes cours
      </Button>

      <Paper
        elevation={0}
        sx={{
          p: { xs: 2.2, md: 3 },
          border: "1px solid #e0e5f3",
          borderRadius: 3.5,
          boxShadow: "0 16px 42px rgba(54,64,125,.06)",
        }}
      >
        <Box
          sx={{
            display: "grid",
            gridTemplateColumns: { xs: "1fr", md: "auto 1fr 260px" },
            gap: 2.5,
            alignItems: "center",
          }}
        >
          <Box
            sx={{
              width: 108,
              height: 108,
              display: "grid",
              placeItems: "center",
              color: "#fff",
              borderRadius: 3,
              background: "linear-gradient(135deg,#4056f4,#7659f6)",
            }}
          >
            <AutoStoriesRoundedIcon sx={{ fontSize: 56 }} />
          </Box>
          <Box>
            <Typography component="h1" sx={{ fontSize: 30, fontWeight: 900 }}>
              {course.titre}
            </Typography>
            <Typography color="text.secondary" sx={{ mt: 0.6, maxWidth: 760 }}>
              {course.description}
            </Typography>
            <Box sx={{ mt: 1.5, display: "flex", flexWrap: "wrap", gap: 2 }}>
              <Typography color="text.secondary" sx={{ fontSize: 13 }}>
                <PersonOutlineRoundedIcon sx={{ mr: 0.5, fontSize: 17, verticalAlign: -4 }} />
                {course.profPrenom} {course.profNom}
              </Typography>
              <Typography color="text.secondary" sx={{ fontSize: 13 }}>
                <AutoStoriesRoundedIcon sx={{ mr: 0.5, fontSize: 17, verticalAlign: -4 }} />
                {progress.totalChapitres} chapitres
              </Typography>
              <Typography color="text.secondary" sx={{ fontSize: 13 }}>
                <FolderOpenRoundedIcon sx={{ mr: 0.5, fontSize: 17, verticalAlign: -4 }} />
                {progress.totalRessources} ressources
              </Typography>
            </Box>
            {course.fichierPrincipalUrl && course.fichierPrincipalNom && (
              <Button
                variant="outlined"
                startIcon={<DownloadRoundedIcon />}
                onClick={() =>
                  void studentLearningApi.download(
                    course.fichierPrincipalUrl!,
                    course.fichierPrincipalNom!,
                  )
                }
                sx={{ mt: 1.5, minHeight: 38 }}
              >
                Support principal
              </Button>
            )}
          </Box>
          <Box sx={{ p: 2, bgcolor: "#f6f7ff", borderRadius: 2.5 }}>
            <Typography sx={{ fontSize: 30, fontWeight: 900, color: "#5364f4" }}>
              {progress.pourcentageGlobal}%
            </Typography>
            <Typography color="text.secondary" sx={{ fontSize: 12.5 }}>
              Progression du cours
            </Typography>
            <LinearProgress
              variant="determinate"
              value={progress.pourcentageGlobal}
              sx={{ mt: 1.2, height: 9, borderRadius: 10 }}
            />
            <Typography sx={{ mt: 1, fontSize: 12.5, fontWeight: 750 }}>
              {progress.chapitresTermines} / {progress.totalChapitres} chapitres termines
            </Typography>
          </Box>
        </Box>
      </Paper>

      <Box sx={{ mt: 2.5 }}>
        <Typography sx={{ mb: 1.4, fontSize: 20, fontWeight: 900 }}>
          Contenu du cours
        </Typography>
        <StudentChapterList
          chapters={chapters}
          resources={resourcesQuery.data ?? []}
          progress={progress.details}
          completingId={completingId}
          onOpen={(chapterId) => startChapter.mutate(chapterId)}
          onComplete={async (chapterId) => {
            setCompletingId(chapterId);
            try {
              await completeChapter.mutateAsync(chapterId);
              setMessage("Chapitre marque comme termine");
            } catch {
              // L'erreur est affichee sous la liste des chapitres.
            } finally {
              setCompletingId(null);
            }
          }}
          onDownload={(url, fileName) =>
            void studentLearningApi.download(url, fileName)
          }
        />
      </Box>

      {(startChapter.isError || completeChapter.isError) && (
        <Alert severity="error" sx={{ mt: 2 }}>
          {getApiErrorMessage(startChapter.error ?? completeChapter.error)}
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
