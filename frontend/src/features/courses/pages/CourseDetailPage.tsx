import { useState } from "react";
import {
  Alert,
  Avatar,
  Box,
  Button,
  Chip,
  CircularProgress,
  Divider,
  IconButton,
  Link,
  Paper,
  Snackbar,
  Typography,
} from "@mui/material";
import AddRoundedIcon from "@mui/icons-material/AddRounded";
import ArrowBackRoundedIcon from "@mui/icons-material/ArrowBackRounded";
import DescriptionRoundedIcon from "@mui/icons-material/DescriptionRounded";
import EditRoundedIcon from "@mui/icons-material/EditRounded";
import FolderRoundedIcon from "@mui/icons-material/FolderRounded";
import GroupRoundedIcon from "@mui/icons-material/GroupRounded";
import InsertDriveFileRoundedIcon from "@mui/icons-material/InsertDriveFileRounded";
import MenuBookRoundedIcon from "@mui/icons-material/MenuBookRounded";
import OpenInNewRoundedIcon from "@mui/icons-material/OpenInNewRounded";
import { useNavigate, useParams } from "react-router-dom";
import { getApiErrorMessage } from "../../auth/api/apiError";
import type { Chapter, CourseStatus } from "../api/coursesApi";
import { ChapterDialog } from "../components/ChapterDialog";
import { CourseFormDialog } from "../components/CourseFormDialog";
import { EnrollStudentDialog } from "../components/EnrollStudentDialog";
import { ResourceDialog } from "../components/ResourceDialog";
import {
  useChapters,
  useCourse,
  useEnrollments,
  useResources,
  useStudents,
} from "../hooks/useCourses";

const statusLabels: Record<CourseStatus, string> = {
  DRAFT: "Brouillon",
  PUBLISHED: "Publié",
  VALIDE: "Validé",
  ARCHIVE: "Archivé",
};

type ChapterCardProps = {
  courseId: number;
  chapter: Chapter;
  onEdit: (chapter: Chapter) => void;
  onMessage: (message: string) => void;
};

function ChapterCard({
  courseId,
  chapter,
  onEdit,
  onMessage,
}: ChapterCardProps) {
  const resourcesQuery = useResources(courseId, chapter.id);
  const [resourceOpen, setResourceOpen] = useState(false);

  return (
    <Paper
      elevation={0}
      sx={{ p: 2, border: "1px solid #e5e8f4", borderRadius: 2.5 }}
    >
      <Box sx={{ display: "flex", alignItems: "flex-start", gap: 1.25 }}>
        <Avatar
          sx={{
            width: 36,
            height: 36,
            fontSize: 14,
            fontWeight: 850,
            color: "#4f5ff7",
            bgcolor: "#edf0ff",
          }}
        >
          {chapter.ordre}
        </Avatar>
        <Box sx={{ flex: 1, minWidth: 0 }}>
          <Typography sx={{ fontWeight: 800 }}>{chapter.titre}</Typography>
          <Typography color="text.secondary" sx={{ mt: 0.35, fontSize: 13 }}>
            {chapter.contenu}
          </Typography>
        </Box>
        <IconButton aria-label={`Modifier ${chapter.titre}`} onClick={() => onEdit(chapter)}>
          <EditRoundedIcon fontSize="small" />
        </IconButton>
      </Box>

      <Divider sx={{ my: 1.75 }} />
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          gap: 1,
        }}
      >
        <Typography sx={{ fontSize: 13, fontWeight: 800 }}>
          Ressources
        </Typography>
        <Button
          size="small"
          startIcon={<AddRoundedIcon />}
          onClick={() => setResourceOpen(true)}
        >
          Ajouter
        </Button>
      </Box>
      {resourcesQuery.isPending ? (
        <CircularProgress size={22} sx={{ mt: 1 }} />
      ) : resourcesQuery.isError ? (
        <Alert severity="error" sx={{ mt: 1 }}>
          Ressources indisponibles
        </Alert>
      ) : resourcesQuery.data.length === 0 ? (
        <Typography color="text.secondary" sx={{ mt: 1, fontSize: 13 }}>
          Aucune ressource dans ce chapitre.
        </Typography>
      ) : (
        <Box sx={{ mt: 1, display: "grid", gap: 0.75 }}>
          {resourcesQuery.data.map((resource) => (
            <Box
              key={resource.id}
              sx={{
                p: 1,
                display: "flex",
                alignItems: "center",
                gap: 1,
                bgcolor: "#f8f9fe",
                borderRadius: 1.5,
              }}
            >
              <InsertDriveFileRoundedIcon
                fontSize="small"
                sx={{ color: resource.type === "PDF" ? "#e14e59" : "#5263e8" }}
              />
              <Link
                href={resource.url}
                target="_blank"
                rel="noreferrer"
                underline="hover"
                sx={{ flex: 1, minWidth: 0, fontSize: 13, fontWeight: 700 }}
              >
                {resource.nom}
              </Link>
              <Chip size="small" label={resource.type} />
              <OpenInNewRoundedIcon sx={{ fontSize: 16, color: "text.secondary" }} />
            </Box>
          ))}
        </Box>
      )}
      <ResourceDialog
        open={resourceOpen}
        courseId={courseId}
        chapterId={chapter.id}
        onClose={() => setResourceOpen(false)}
        onSaved={onMessage}
      />
    </Paper>
  );
}

export function CourseDetailPage() {
  const navigate = useNavigate();
  const { courseId: courseIdParam } = useParams();
  const courseId = Number(courseIdParam);
  const courseQuery = useCourse(courseId);
  const chaptersQuery = useChapters(courseId);
  const enrollmentsQuery = useEnrollments(courseId);
  const studentsQuery = useStudents();
  const [courseFormOpen, setCourseFormOpen] = useState(false);
  const [chapterOpen, setChapterOpen] = useState(false);
  const [editedChapter, setEditedChapter] = useState<Chapter | null>(null);
  const [enrollOpen, setEnrollOpen] = useState(false);
  const [success, setSuccess] = useState("");

  if (!Number.isFinite(courseId)) {
    return <Alert severity="error">Identifiant de cours invalide.</Alert>;
  }

  if (
    courseQuery.isPending ||
    chaptersQuery.isPending ||
    enrollmentsQuery.isPending ||
    studentsQuery.isPending
  ) {
    return (
      <Box sx={{ minHeight: 460, display: "grid", placeItems: "center" }}>
        <CircularProgress aria-label="Chargement du cours" />
      </Box>
    );
  }

  const firstError =
    courseQuery.error ??
    chaptersQuery.error ??
    enrollmentsQuery.error ??
    studentsQuery.error;
  if (firstError) {
    return (
      <Alert severity="error">
        Impossible de charger le cours. {getApiErrorMessage(firstError)}
      </Alert>
    );
  }

  if (!courseQuery.data) {
    return <Alert severity="error">Cours introuvable.</Alert>;
  }

  const course = courseQuery.data;
  const chapters = [...(chaptersQuery.data ?? [])].sort(
    (a, b) => a.ordre - b.ordre,
  );
  const enrollments = enrollmentsQuery.data ?? [];

  return (
    <Box sx={{ maxWidth: 1600, mx: "auto" }}>
      <Button
        startIcon={<ArrowBackRoundedIcon />}
        onClick={() => navigate("/dashboard/courses")}
      >
        Mes cours
      </Button>

      <Paper
        elevation={0}
        sx={{
          mt: 1.5,
          p: { xs: 2.25, sm: 3 },
          border: "1px solid #e3e7f3",
          borderRadius: 3.5,
          boxShadow: "0 16px 44px rgba(54,64,125,0.07)",
        }}
      >
        <Box
          sx={{
            display: "flex",
            flexDirection: { xs: "column", md: "row" },
            alignItems: { md: "center" },
            gap: 2.5,
          }}
        >
          <Box
            sx={{
              width: { xs: "100%", md: 122 },
              height: 122,
              display: "grid",
              placeItems: "center",
              borderRadius: 3,
              color: "#fff",
              background: "linear-gradient(135deg,#4056f4,#7458f6)",
            }}
          >
            <MenuBookRoundedIcon sx={{ fontSize: 58 }} />
          </Box>
          <Box sx={{ flex: 1 }}>
            <Box sx={{ display: "flex", flexWrap: "wrap", alignItems: "center", gap: 1 }}>
              <Typography
                component="h1"
                sx={{ fontSize: { xs: 28, sm: 34 }, fontWeight: 850 }}
              >
                {course.titre}
              </Typography>
              <Chip
                size="small"
                label={statusLabels[course.statut]}
                color={course.statut === "PUBLISHED" ? "success" : "default"}
              />
            </Box>
            <Typography color="text.secondary" sx={{ mt: 1, maxWidth: 800 }}>
              {course.description}
            </Typography>
            <Typography color="text.secondary" sx={{ mt: 1.25, fontSize: 13 }}>
              {course.visibleCatalogue ? "Visible dans le catalogue" : "Cours privé"}
              {" · "}
              Créé le {new Intl.DateTimeFormat("fr-FR").format(new Date(course.dateCreation))}
            </Typography>
          </Box>
          <Button
            variant="outlined"
            startIcon={<EditRoundedIcon />}
            onClick={() => setCourseFormOpen(true)}
          >
            Modifier le cours
          </Button>
        </Box>
      </Paper>

      <Box
        sx={{
          mt: 2.5,
          display: "grid",
          gridTemplateColumns: { xs: "1fr", xl: "minmax(0,1.8fr) minmax(330px,0.8fr)" },
          gap: 2.5,
          alignItems: "start",
        }}
      >
        <Paper
          elevation={0}
          sx={{ p: { xs: 2, sm: 2.5 }, border: "1px solid #e3e7f3", borderRadius: 3.5 }}
        >
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
              gap: 1.5,
              mb: 2,
            }}
          >
            <Box>
              <Typography sx={{ fontSize: 20, fontWeight: 850 }}>Chapitres</Typography>
              <Typography color="text.secondary" sx={{ fontSize: 13 }}>
                {chapters.length} chapitre{chapters.length > 1 ? "s" : ""}
              </Typography>
            </Box>
            <Button
              variant="outlined"
              startIcon={<AddRoundedIcon />}
              onClick={() => {
                setEditedChapter(null);
                setChapterOpen(true);
              }}
            >
              Ajouter un chapitre
            </Button>
          </Box>

          {chapters.length === 0 ? (
            <Box sx={{ py: 6, textAlign: "center" }}>
              <DescriptionRoundedIcon sx={{ fontSize: 50, color: "rgba(79,95,247,.3)" }} />
              <Typography sx={{ mt: 1, fontWeight: 800 }}>Aucun chapitre</Typography>
              <Typography color="text.secondary">
                Commencez par structurer le contenu du cours.
              </Typography>
            </Box>
          ) : (
            <Box sx={{ display: "grid", gap: 1.5 }}>
              {chapters.map((chapter) => (
                <ChapterCard
                  key={chapter.id}
                  courseId={courseId}
                  chapter={chapter}
                  onMessage={setSuccess}
                  onEdit={(selected) => {
                    setEditedChapter(selected);
                    setChapterOpen(true);
                  }}
                />
              ))}
            </Box>
          )}
        </Paper>

        <Box sx={{ display: "grid", gap: 2.5 }}>
          <Paper
            elevation={0}
            sx={{ p: 2.5, border: "1px solid #e3e7f3", borderRadius: 3.5 }}
          >
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                gap: 1,
              }}
            >
              <Box>
                <Typography sx={{ fontSize: 19, fontWeight: 850 }}>
                  Étudiants inscrits
                </Typography>
                <Typography color="text.secondary" sx={{ fontSize: 13 }}>
                  {enrollments.length} étudiant{enrollments.length > 1 ? "s" : ""}
                </Typography>
              </Box>
              <Button
                size="small"
                startIcon={<AddRoundedIcon />}
                onClick={() => setEnrollOpen(true)}
              >
                Inscrire
              </Button>
            </Box>
            <Divider sx={{ my: 2 }} />
            {enrollments.length === 0 ? (
              <Typography color="text.secondary">Aucun étudiant inscrit.</Typography>
            ) : (
              <Box sx={{ display: "grid", gap: 1.5 }}>
                {enrollments.map((enrollment) => (
                  <Box
                    key={enrollment.id}
                    sx={{ display: "flex", alignItems: "center", gap: 1.25 }}
                  >
                    <Avatar sx={{ width: 36, height: 36, bgcolor: "#edf0ff", color: "#4f5ff7" }}>
                      {enrollment.elevePrenom[0]}{enrollment.eleveNom[0]}
                    </Avatar>
                    <Box sx={{ flex: 1, minWidth: 0 }}>
                      <Typography sx={{ fontSize: 14, fontWeight: 750 }}>
                        {enrollment.elevePrenom} {enrollment.eleveNom}
                      </Typography>
                      <Typography noWrap color="text.secondary" sx={{ fontSize: 12 }}>
                        {enrollment.eleveEmail}
                      </Typography>
                    </Box>
                    <Chip
                      size="small"
                      label={
                        enrollment.statut === "VALIDE"
                          ? "Actif"
                          : enrollment.statut === "EN_ATTENTE"
                            ? "En attente"
                            : "Refusé"
                      }
                      color={enrollment.statut === "VALIDE" ? "success" : "default"}
                    />
                  </Box>
                ))}
              </Box>
            )}
          </Paper>

          <Paper
            elevation={0}
            sx={{ p: 2.5, border: "1px solid #e3e7f3", borderRadius: 3.5 }}
          >
            <Typography sx={{ fontSize: 19, fontWeight: 850 }}>Résumé du cours</Typography>
            <Box
              sx={{
                mt: 2,
                display: "grid",
                gridTemplateColumns: "repeat(2,minmax(0,1fr))",
                gap: 1.5,
              }}
            >
              {[
                { icon: <GroupRoundedIcon />, value: enrollments.length, label: "Étudiants" },
                { icon: <MenuBookRoundedIcon />, value: chapters.length, label: "Chapitres" },
                { icon: <FolderRoundedIcon />, value: "URL", label: "Ressources" },
                { icon: <DescriptionRoundedIcon />, value: statusLabels[course.statut], label: "Statut" },
              ].map((item) => (
                <Box key={item.label} sx={{ p: 1.5, bgcolor: "#f8f9fe", borderRadius: 2 }}>
                  <Box sx={{ color: "#5263e8" }}>{item.icon}</Box>
                  <Typography sx={{ mt: 0.5, fontWeight: 850 }}>{item.value}</Typography>
                  <Typography color="text.secondary" sx={{ fontSize: 12 }}>
                    {item.label}
                  </Typography>
                </Box>
              ))}
            </Box>
          </Paper>
        </Box>
      </Box>

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
      <EnrollStudentDialog
        open={enrollOpen}
        courseId={courseId}
        students={studentsQuery.data ?? []}
        enrollments={enrollments}
        onClose={() => setEnrollOpen(false)}
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
