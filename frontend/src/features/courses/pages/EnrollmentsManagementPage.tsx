import { useMemo, useState } from "react";
import {
  Alert,
  Avatar,
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
import CheckCircleOutlineRoundedIcon from "@mui/icons-material/CheckCircleOutlineRounded";
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import GroupAddRoundedIcon from "@mui/icons-material/GroupAddRounded";
import HourglassTopRoundedIcon from "@mui/icons-material/HourglassTopRounded";
import PersonRemoveOutlinedIcon from "@mui/icons-material/PersonRemoveOutlined";
import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import { useSearchParams } from "react-router-dom";
import { getApiErrorMessage } from "../../auth/api/apiError";
import type { Enrollment, Student } from "../api/coursesApi";
import { CourseSelectorCard } from "../components/CourseSelectorCard";
import {
  useCourses,
  useEnrollments,
  useEnrollStudent,
  useRemoveEnrollment,
  useStudents,
  useUpdateEnrollmentStatus,
} from "../hooks/useCourses";

export function EnrollmentsManagementPage() {
  const coursesQuery = useCourses();
  const [searchParams] = useSearchParams();
  const [chosenCourseId, setChosenCourseId] = useState<number | null>(null);
  const requestedCourseId = Number(searchParams.get("courseId"));
  const requestedCourseExists = coursesQuery.data?.some(
    (course) => course.id === requestedCourseId,
  );
  const selectedId =
    chosenCourseId ??
    (requestedCourseExists ? requestedCourseId : coursesQuery.data?.[0]?.id) ??
    Number.NaN;
  const enrollmentsQuery = useEnrollments(selectedId);
  const studentsQuery = useStudents();
  const enrollStudent = useEnrollStudent(selectedId);
  const removeEnrollment = useRemoveEnrollment(selectedId);
  const updateStatus = useUpdateEnrollmentStatus(selectedId);
  const [availableSearch, setAvailableSearch] = useState("");
  const [enrolledSearch, setEnrolledSearch] = useState("");
  const [message, setMessage] = useState("");

  const enrollments = useMemo(
    () => enrollmentsQuery.data ?? [],
    [enrollmentsQuery.data],
  );
  const enrolledIds = useMemo(
    () => new Set(enrollments.map((item) => item.eleveId)),
    [enrollments],
  );
  const availableStudents = useMemo(() => {
    const search = availableSearch.trim().toLocaleLowerCase("fr");
    return (studentsQuery.data ?? []).filter((student) => {
      const role = student.role.replace("ROLE_", "");
      const active = student.statut.trim().toUpperCase() !== "INACTIF";
      const matches =
        !search ||
        `${student.prenom} ${student.nom} ${student.email}`
          .toLocaleLowerCase("fr")
          .includes(search);
      return role === "ETUDIANT" && active && !enrolledIds.has(student.id) && matches;
    });
  }, [availableSearch, enrolledIds, studentsQuery.data]);
  const visibleEnrollments = useMemo(() => {
    const search = enrolledSearch.trim().toLocaleLowerCase("fr");
    return enrollments.filter(
      (item) =>
        !search ||
        `${item.elevePrenom} ${item.eleveNom} ${item.eleveEmail}`
          .toLocaleLowerCase("fr")
          .includes(search),
    );
  }, [enrolledSearch, enrollments]);

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

  const error =
    enrollmentsQuery.error ??
    studentsQuery.error ??
    enrollStudent.error ??
    removeEnrollment.error ??
    updateStatus.error;

  return (
    <Box sx={{ maxWidth: 1540, mx: "auto" }}>
      <Typography component="h1" sx={{ fontSize: 32, fontWeight: 850 }}>
        Inscriptions
      </Typography>
      <Typography color="text.secondary" sx={{ mt: 0.4 }}>
        Choisissez un cours puis gérez les étudiants qui y ont accès.
      </Typography>

      <Box sx={{ mt: 2.5 }}>
        <CourseSelectorCard
          courses={courses}
          selectedId={selectedId}
          onChange={setChosenCourseId}
        />
      </Box>

      {enrollmentsQuery.isPending || studentsQuery.isPending ? (
        <LoadingState />
      ) : (
        <>
          {error && (
            <Alert severity="error" sx={{ mt: 2 }}>
              {getApiErrorMessage(error)}
            </Alert>
          )}
          <Box
            sx={{
              mt: 2.25,
              display: "grid",
              gridTemplateColumns: {
                xs: "1fr",
                sm: "repeat(3,minmax(0,1fr))",
              },
              gap: 1.5,
            }}
          >
            <StatCard
              icon={<GroupAddRoundedIcon />}
              value={availableStudents.length}
              label="Étudiants disponibles"
              color="#5364f4"
              background="#eef0ff"
            />
            <StatCard
              icon={<CheckCircleOutlineRoundedIcon />}
              value={enrollments.filter((item) => item.statut === "VALIDE").length}
              label="Inscrits actifs"
              color="#17925a"
              background="#e8f8ef"
            />
            <StatCard
              icon={<HourglassTopRoundedIcon />}
              value={
                enrollments.filter((item) => item.statut === "EN_ATTENTE").length
              }
              label="En attente"
              color="#d98a13"
              background="#fff4df"
            />
          </Box>

          <Box
            sx={{
              mt: 2.25,
              display: "grid",
              gridTemplateColumns: { xs: "1fr", lg: "1fr 1fr" },
              gap: 2.25,
            }}
          >
            <StudentPanel
              title={`Étudiants disponibles (${availableStudents.length})`}
              search={availableSearch}
              onSearch={setAvailableSearch}
            >
              {availableStudents.length === 0 ? (
                <EmptyState text="Aucun étudiant disponible." />
              ) : (
                availableStudents.map((student) => (
                  <AvailableStudentRow
                    key={student.id}
                    student={student}
                    pending={enrollStudent.isPending}
                    onEnroll={async () => {
                      await enrollStudent.mutateAsync(student.id);
                      setMessage("Étudiant inscrit avec succès");
                    }}
                  />
                ))
              )}
            </StudentPanel>

            <StudentPanel
              title={`Étudiants inscrits (${enrollments.length})`}
              search={enrolledSearch}
              onSearch={setEnrolledSearch}
            >
              {visibleEnrollments.length === 0 ? (
                <EmptyState text="Aucun étudiant inscrit." />
              ) : (
                visibleEnrollments.map((enrollment) => (
                  <EnrolledStudentRow
                    key={enrollment.id}
                    enrollment={enrollment}
                    pending={
                      removeEnrollment.isPending || updateStatus.isPending
                    }
                    onAccept={async () => {
                      await updateStatus.mutateAsync({
                        enrollmentId: enrollment.id,
                        statut: "VALIDE",
                      });
                      setMessage("Demande d'inscription acceptée");
                    }}
                    onReject={async () => {
                      await updateStatus.mutateAsync({
                        enrollmentId: enrollment.id,
                        statut: "REFUSE",
                      });
                      setMessage("Demande d'inscription refusée");
                    }}
                    onRemove={async () => {
                      await removeEnrollment.mutateAsync(enrollment.id);
                      setMessage("Étudiant retiré du cours");
                    }}
                  />
                ))
              )}
            </StudentPanel>
          </Box>
        </>
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

function StudentPanel({
  title,
  search,
  onSearch,
  children,
}: {
  title: string;
  search: string;
  onSearch: (value: string) => void;
  children: React.ReactNode;
}) {
  return (
    <Paper
      elevation={0}
      sx={{ p: 2.25, border: "1px solid #e1e6f2", borderRadius: 3 }}
    >
      <Typography sx={{ fontSize: 17, fontWeight: 850 }}>{title}</Typography>
      <TextField
        fullWidth
        size="small"
        value={search}
        onChange={(event) => onSearch(event.target.value)}
        placeholder="Rechercher un étudiant..."
        sx={{ mt: 1.5 }}
        slotProps={{
          input: {
            startAdornment: (
              <InputAdornment position="start">
                <SearchRoundedIcon fontSize="small" />
              </InputAdornment>
            ),
          },
        }}
      />
      <Box sx={{ mt: 1.25, display: "grid", gap: 0.75 }}>{children}</Box>
    </Paper>
  );
}

function AvailableStudentRow({
  student,
  pending,
  onEnroll,
}: {
  student: Student;
  pending: boolean;
  onEnroll: () => Promise<void>;
}) {
  return (
    <StudentRow
      initials={`${student.prenom[0]}${student.nom[0]}`}
      name={`${student.prenom} ${student.nom}`}
      email={student.email}
      action={
        <Button
          size="small"
          variant="outlined"
          disabled={pending}
          onClick={() => void onEnroll()}
        >
          Inscrire
        </Button>
      }
    />
  );
}

function EnrolledStudentRow({
  enrollment,
  pending,
  onAccept,
  onReject,
  onRemove,
}: {
  enrollment: Enrollment;
  pending: boolean;
  onAccept: () => Promise<void>;
  onReject: () => Promise<void>;
  onRemove: () => Promise<void>;
}) {
  return (
    <StudentRow
      initials={`${enrollment.elevePrenom[0]}${enrollment.eleveNom[0]}`}
      name={`${enrollment.elevePrenom} ${enrollment.eleveNom}`}
      email={enrollment.eleveEmail}
      action={
        enrollment.statut === "EN_ATTENTE" ? (
          <>
            <Button
              size="small"
              color="success"
              startIcon={<CheckCircleOutlineRoundedIcon />}
              disabled={pending}
              onClick={() => void onAccept()}
            >
              Accepter
            </Button>
            <Button
              size="small"
              color="error"
              startIcon={<CloseRoundedIcon />}
              disabled={pending}
              onClick={() => void onReject()}
            >
              Refuser
            </Button>
          </>
        ) : (
          <>
          <Chip
            size="small"
            label={enrollment.statut === "VALIDE" ? "Actif" : "Refusé"}
            sx={{
              color: enrollment.statut === "VALIDE" ? "#16864f" : "#b23c48",
              bgcolor:
                enrollment.statut === "VALIDE" ? "#e5f7ec" : "#fdecef",
            }}
          />
          <Button
            size="small"
            color="error"
            startIcon={<PersonRemoveOutlinedIcon />}
            disabled={pending}
            onClick={() => void onRemove()}
          >
            Retirer
          </Button>
          </>
        )
      }
    />
  );
}

function StudentRow({
  initials,
  name,
  email,
  action,
}: {
  initials: string;
  name: string;
  email: string;
  action: React.ReactNode;
}) {
  return (
    <Box
      sx={{
        p: 1.2,
        display: "flex",
        alignItems: "center",
        gap: 1.15,
        border: "1px solid #e8ebf5",
        borderRadius: 2,
      }}
    >
      <Avatar
        sx={{
          width: 38,
          height: 38,
          bgcolor: "#eef0ff",
          color: "#5364f4",
          fontSize: 12,
          fontWeight: 850,
        }}
      >
        {initials.toUpperCase()}
      </Avatar>
      <Box sx={{ flex: 1, minWidth: 0 }}>
        <Typography noWrap sx={{ fontSize: 13.5, fontWeight: 800 }}>
          {name}
        </Typography>
        <Typography noWrap color="text.secondary" sx={{ fontSize: 11.5 }}>
          {email}
        </Typography>
      </Box>
      <Box sx={{ display: "flex", alignItems: "center", gap: 0.75 }}>
        {action}
      </Box>
    </Box>
  );
}

function StatCard({
  icon,
  value,
  label,
  color,
  background,
}: {
  icon: React.ReactNode;
  value: number;
  label: string;
  color: string;
  background: string;
}) {
  return (
    <Paper
      elevation={0}
      sx={{
        p: 2,
        display: "flex",
        alignItems: "center",
        gap: 1.25,
        border: "1px solid #e1e6f2",
        borderRadius: 2.5,
      }}
    >
      <Box
        sx={{
          width: 44,
          height: 44,
          display: "grid",
          placeItems: "center",
          color,
          bgcolor: background,
          borderRadius: "50%",
        }}
      >
        {icon}
      </Box>
      <Box>
        <Typography sx={{ fontSize: 22, fontWeight: 850 }}>{value}</Typography>
        <Typography color="text.secondary" sx={{ fontSize: 12 }}>
          {label}
        </Typography>
      </Box>
    </Paper>
  );
}

function EmptyState({ text }: { text: string }) {
  return (
    <Typography
      color="text.secondary"
      sx={{ py: 5, textAlign: "center", fontSize: 13 }}
    >
      {text}
    </Typography>
  );
}

function LoadingState() {
  return (
    <Box sx={{ minHeight: 300, display: "grid", placeItems: "center" }}>
      <CircularProgress aria-label="Chargement des inscriptions" />
    </Box>
  );
}
