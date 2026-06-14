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
import ArrowBackRoundedIcon from "@mui/icons-material/ArrowBackRounded";
import AutoStoriesRoundedIcon from "@mui/icons-material/AutoStoriesRounded";
import ForumOutlinedIcon from "@mui/icons-material/ForumOutlined";
import GroupsOutlinedIcon from "@mui/icons-material/GroupsOutlined";
import { useNavigate, useParams } from "react-router-dom";
import { getApiErrorMessage } from "../../auth/api/apiError";
import {
  coursesApi,
  type Chapter,
  type CourseSummary,
} from "../api/coursesApi";
import { ChapterDialog } from "../components/ChapterDialog";
import { ChapterList } from "../components/ChapterList";
import { CourseDetailHeader } from "../components/CourseDetailHeader";
import {
  CourseDetailTabs,
  type CourseDetailTab,
} from "../components/CourseDetailTabs";
import { CourseFormDialog } from "../components/CourseFormDialog";
import { EnrollStudentDialog } from "../components/EnrollStudentDialog";
import { ResourceDialog } from "../components/ResourceDialog";
import { ResourceList } from "../components/ResourceList";
import { StudentsPanel } from "../components/StudentsPanel";
import { SummaryCard } from "../components/SummaryCard";
import {
  useChapters,
  useCourse,
  useCourseResources,
  useCourseSummary,
  useDeleteChapter,
  useDeleteCourse,
  useDeleteResource,
  useEnrollments,
  useStudents,
} from "../hooks/useCourses";

type DeleteTarget =
  | { type: "course"; label: string }
  | { type: "chapter"; label: string; chapterId: number }
  | {
      type: "resource";
      label: string;
      chapterId: number;
      resourceId: number;
    };

export function CourseDetailPage() {
  const navigate = useNavigate();
  const { courseId: courseIdParam } = useParams();
  const courseId = Number(courseIdParam);
  const courseQuery = useCourse(courseId);
  const chaptersQuery = useChapters(courseId);
  const resourcesQuery = useCourseResources(courseId);
  const enrollmentsQuery = useEnrollments(courseId);
  const studentsQuery = useStudents();
  const summaryQuery = useCourseSummary(courseId);
  const deleteCourse = useDeleteCourse();
  const deleteChapter = useDeleteChapter(courseId);
  const deleteResource = useDeleteResource(courseId);

  const [activeTab, setActiveTab] = useState<CourseDetailTab>("chapters");
  const [courseFormOpen, setCourseFormOpen] = useState(false);
  const [chapterOpen, setChapterOpen] = useState(false);
  const [editedChapter, setEditedChapter] = useState<Chapter | null>(null);
  const [resourceOpen, setResourceOpen] = useState(false);
  const [resourceChapterId, setResourceChapterId] = useState<number | null>(null);
  const [enrollOpen, setEnrollOpen] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<DeleteTarget | null>(null);
  const [success, setSuccess] = useState("");
  const [actionError, setActionError] = useState("");

  const chapters = useMemo(
    () =>
      [...(chaptersQuery.data ?? [])].sort((first, second) => first.ordre - second.ordre),
    [chaptersQuery.data],
  );
  const resources = useMemo(
    () =>
      [...(resourcesQuery.data ?? [])].sort(
        (first, second) =>
          new Date(second.dateCreation).getTime() -
          new Date(first.dateCreation).getTime(),
      ),
    [resourcesQuery.data],
  );
  const enrollments = enrollmentsQuery.data ?? [];

  if (!Number.isFinite(courseId)) {
    return <Alert severity="error">Identifiant de cours invalide.</Alert>;
  }

  const mainQueries = [
    courseQuery,
    chaptersQuery,
    resourcesQuery,
    enrollmentsQuery,
    summaryQuery,
  ];
  if (mainQueries.some((query) => query.isPending)) {
    return (
      <Box sx={{ minHeight: 500, display: "grid", placeItems: "center" }}>
        <CircularProgress aria-label="Chargement du cours" />
      </Box>
    );
  }

  const firstError = mainQueries.find((query) => query.error)?.error;
  if (firstError) {
    return (
      <Alert severity="error">
        Impossible de charger le cours. {getApiErrorMessage(firstError)}
      </Alert>
    );
  }

  const course = courseQuery.data;
  if (!course) {
    return <Alert severity="error">Cours introuvable.</Alert>;
  }

  const summary: CourseSummary = summaryQuery.data ?? {
    students: enrollments.filter((item) => item.statut === "VALIDE").length,
    chapters: chapters.length,
    resources: resources.length,
    averageProgress: 0,
  };

  const openResourceDialog = (chapterId?: number) => {
    setResourceChapterId(chapterId ?? chapters[0]?.id ?? null);
    setResourceOpen(true);
  };

  const confirmDelete = async () => {
    if (!deleteTarget) {
      return;
    }
    setActionError("");
    try {
      if (deleteTarget.type === "course") {
        await deleteCourse.mutateAsync(courseId);
        navigate("/dashboard/courses", { replace: true });
        return;
      }
      if (deleteTarget.type === "chapter") {
        await deleteChapter.mutateAsync(deleteTarget.chapterId);
        setSuccess("Chapitre supprimé avec succès");
      } else {
        await deleteResource.mutateAsync({
          chapterId: deleteTarget.chapterId,
          resourceId: deleteTarget.resourceId,
        });
        setSuccess("Ressource supprimée avec succès");
      }
      setDeleteTarget(null);
    } catch (error) {
      setActionError(getApiErrorMessage(error));
    }
  };

  const renderMainContent = () => {
    if (activeTab === "chapters") {
      return (
        <Box sx={{ display: "grid", gap: 2 }}>
          <ChapterList
            chapters={chapters}
            resources={resources}
            courseStatus={course.statut}
            onAdd={() => {
              setEditedChapter(null);
              setChapterOpen(true);
            }}
            onEdit={(chapter) => {
              setEditedChapter(chapter);
              setChapterOpen(true);
            }}
            onDelete={(chapter) =>
              setDeleteTarget({
                type: "chapter",
                chapterId: chapter.id,
                label: chapter.titre,
              })
            }
            onDownloadFile={(chapter) => {
              if (chapter.fichierPrincipalUrl && chapter.fichierPrincipalNom) {
                void coursesApi.downloadFile(
                  chapter.fichierPrincipalUrl,
                  chapter.fichierPrincipalNom,
                );
              }
            }}
          />
          <ResourceList
            resources={resources}
            limit={3}
            onDelete={(resource) =>
              setDeleteTarget({
                type: "resource",
                resourceId: resource.id,
                chapterId: resource.chapitreId,
                label: resource.nom,
              })
            }
          />
        </Box>
      );
    }

    if (activeTab === "resources") {
      return (
        <ResourceList
          title={`Toutes les ressources (${resources.length})`}
          resources={resources}
          onDelete={(resource) =>
            setDeleteTarget({
              type: "resource",
              resourceId: resource.id,
              chapterId: resource.chapitreId,
              label: resource.nom,
            })
          }
        />
      );
    }

    if (activeTab === "students") {
      return (
        <SectionCard
          icon={<GroupsOutlinedIcon />}
          title="Gestion des étudiants"
          description="Inscrivez les étudiants existants et suivez leur accès au cours."
          actionLabel="Gérer les inscriptions"
          onAction={() => setEnrollOpen(true)}
        />
      );
    }

    if (activeTab === "messaging") {
      return (
        <SectionCard
          icon={<ForumOutlinedIcon />}
          title="Messagerie du cours"
          description="Échangez avec les étudiants inscrits depuis la messagerie LearnHub."
          actionLabel="Ouvrir la messagerie"
          onAction={() => navigate("/dashboard/messages")}
        />
      );
    }

    return (
      <Box sx={{ display: "grid", gap: 2 }}>
        <SectionCard
          icon={<AutoStoriesRoundedIcon />}
          title="Aperçu pédagogique"
          description={course.description}
          actionLabel="Modifier le cours"
          onAction={() => setCourseFormOpen(true)}
        />
        <ResourceList
          resources={resources}
          limit={3}
        />
      </Box>
    );
  };

  return (
    <Box sx={{ maxWidth: 1600, mx: "auto" }}>
      <Box
        sx={{
          mb: 1.6,
          display: "flex",
          alignItems: "center",
          gap: 0.75,
          color: "text.secondary",
        }}
      >
        <Button
          size="small"
          startIcon={<ArrowBackRoundedIcon />}
          onClick={() => navigate("/dashboard/courses")}
          sx={{ px: 0.5 }}
        >
          Cours
        </Button>
        <Typography sx={{ fontSize: 12 }}>›</Typography>
        <Typography color="primary.main" sx={{ fontSize: 12.5, fontWeight: 750 }}>
          {course.titre}
        </Typography>
        <Typography sx={{ fontSize: 12 }}>›</Typography>
        <Typography sx={{ fontSize: 12.5 }}>Détails du cours</Typography>
      </Box>

      <CourseDetailHeader
        course={course}
        onEdit={() => setCourseFormOpen(true)}
        onDelete={() =>
          setDeleteTarget({ type: "course", label: course.titre })
        }
        onAddResource={() => openResourceDialog()}
      />

      <Paper
        elevation={0}
        sx={{
          mt: 2.25,
          border: "1px solid #e1e6f2",
          borderRadius: 3,
          overflow: "hidden",
          boxShadow: "0 12px 36px rgba(52,64,125,0.045)",
        }}
      >
        <CourseDetailTabs
          value={activeTab}
          studentsCount={enrollments.length}
          onChange={setActiveTab}
        />
        <Box
          sx={{
            display: "grid",
            gridTemplateColumns: {
              xs: "1fr",
              lg: "minmax(0,1.65fr) minmax(340px,.95fr)",
            },
          }}
        >
          <Box sx={{ p: { xs: 1.5, sm: 2.25 }, minWidth: 0 }}>
            {renderMainContent()}
          </Box>
          <Box
            sx={{
              p: { xs: 1.5, sm: 2.25 },
              display: "grid",
              alignContent: "start",
              gap: 1.5,
              borderTop: { xs: "1px solid #e5e8f3", lg: 0 },
              borderLeft: { xs: 0, lg: "1px solid #e5e8f3" },
              bgcolor: "rgba(250,251,255,.52)",
            }}
          >
            <StudentsPanel
              enrollments={enrollments}
              onManage={() => setEnrollOpen(true)}
            />
            <SummaryCard summary={summary} />
          </Box>
        </Box>
      </Paper>

      <CourseFormDialog
        open={courseFormOpen}
        course={course}
        onClose={() => setCourseFormOpen(false)}
        onSaved={setSuccess}
      />
      <ChapterDialog
        open={chapterOpen}
        courseId={courseId}
        chapter={editedChapter}
        nextOrder={chapters.length + 1}
        onClose={() => setChapterOpen(false)}
        onSaved={setSuccess}
      />
      <ResourceDialog
        key={`${resourceOpen}-${resourceChapterId ?? "none"}`}
        open={resourceOpen}
        courseId={courseId}
        chapters={chapters}
        defaultChapterId={resourceChapterId}
        onClose={() => setResourceOpen(false)}
        onSaved={setSuccess}
      />
      <EnrollStudentDialog
        open={enrollOpen}
        courseId={courseId}
        students={studentsQuery.data ?? []}
        enrollments={enrollments}
        onClose={() => setEnrollOpen(false)}
        onSaved={setSuccess}
      />

      <Dialog
        open={Boolean(deleteTarget)}
        onClose={() => {
          setDeleteTarget(null);
          setActionError("");
        }}
        fullWidth
        maxWidth="xs"
      >
        <DialogTitle sx={{ fontWeight: 850 }}>Confirmer la suppression</DialogTitle>
        <DialogContent>
          {actionError && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {actionError}
            </Alert>
          )}
          <Typography>
            Voulez-vous vraiment supprimer « {deleteTarget?.label} » ? Cette
            action est définitive.
          </Typography>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 3 }}>
          <Button onClick={() => setDeleteTarget(null)}>Annuler</Button>
          <Button
            variant="contained"
            color="error"
            onClick={() => void confirmDelete()}
            disabled={
              deleteCourse.isPending ||
              deleteChapter.isPending ||
              deleteResource.isPending
            }
          >
            Supprimer
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={Boolean(success)}
        autoHideDuration={3500}
        onClose={() => setSuccess("")}
        message={success}
      />
    </Box>
  );
}

type SectionCardProps = {
  icon: React.ReactNode;
  title: string;
  description: string;
  actionLabel: string;
  onAction: () => void;
};

function SectionCard({
  icon,
  title,
  description,
  actionLabel,
  onAction,
}: SectionCardProps) {
  return (
    <Paper
      elevation={0}
      sx={{
        p: { xs: 2.5, sm: 3.5 },
        minHeight: 220,
        display: "grid",
        placeItems: "center",
        textAlign: "center",
        border: "1px solid #e1e6f2",
        borderRadius: 2.5,
        background:
          "radial-gradient(circle at 50% 15%, rgba(93,103,246,.12), transparent 42%), #fff",
      }}
    >
      <Box>
        <Box
          sx={{
            width: 58,
            height: 58,
            mx: "auto",
            display: "grid",
            placeItems: "center",
            color: "#5364f4",
            bgcolor: "#eef0ff",
            borderRadius: 2.5,
            "& svg": { fontSize: 30 },
          }}
        >
          {icon}
        </Box>
        <Typography sx={{ mt: 1.5, fontSize: 19, fontWeight: 850 }}>
          {title}
        </Typography>
        <Typography
          color="text.secondary"
          sx={{ mt: 0.75, mx: "auto", maxWidth: 580, lineHeight: 1.6 }}
        >
          {description}
        </Typography>
        <Button variant="contained" onClick={onAction} sx={{ mt: 2, color: "#fff" }}>
          {actionLabel}
        </Button>
      </Box>
    </Paper>
  );
}
