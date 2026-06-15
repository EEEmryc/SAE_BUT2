import { useMemo, useState } from "react";
import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  TextField,
  Typography,
} from "@mui/material";
import { getApiErrorMessage } from "../../auth/services/apiError";
import type { Enrollment, Student } from "../services/coursesApi";
import { useEnrollStudent } from "../hooks/useCourses";

type EnrollStudentDialogProps = {
  open: boolean;
  courseId: number;
  students: Student[];
  enrollments: Enrollment[];
  onClose: () => void;
  onSaved: (message: string) => void;
};

export function EnrollStudentDialog({
  open,
  courseId,
  students,
  enrollments,
  onClose,
  onSaved,
}: EnrollStudentDialogProps) {
  const [studentId, setStudentId] = useState("");
  const mutation = useEnrollStudent(courseId);
  const candidates = useMemo(() => {
    const enrolledIds = new Set(enrollments.map((item) => item.eleveId));
    return students.filter(
      (student) =>
        student.role.replace("ROLE_", "") === "ETUDIANT" &&
        student.statut.trim().toUpperCase() !== "INACTIF" &&
        !enrolledIds.has(student.id),
    );
  }, [enrollments, students]);

  const submit = async () => {
    if (!studentId) {
      return;
    }
    await mutation.mutateAsync(Number(studentId));
    setStudentId("");
    onSaved("Étudiant inscrit avec succès");
    onClose();
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle sx={{ fontWeight: 850 }}>Inscrire un étudiant</DialogTitle>
      <DialogContent sx={{ pt: "12px !important" }}>
        {mutation.isError && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {getApiErrorMessage(mutation.error)}
          </Alert>
        )}
        {candidates.length === 0 ? (
          <Typography color="text.secondary">
            Tous les étudiants actifs sont déjà inscrits à ce cours.
          </Typography>
        ) : (
          <TextField
            select
            fullWidth
            label="Étudiant"
            value={studentId}
            onChange={(event) => setStudentId(event.target.value)}
          >
            {candidates.map((student) => (
              <MenuItem key={student.id} value={student.id}>
                {student.prenom} {student.nom} - {student.email}
              </MenuItem>
            ))}
          </TextField>
        )}
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 3 }}>
        <Button onClick={onClose}>Annuler</Button>
        <Button
          variant="contained"
          disabled={!studentId || mutation.isPending}
          onClick={() => void submit()}
          sx={{ color: "#fff" }}
        >
          Inscrire
        </Button>
      </DialogActions>
    </Dialog>
  );
}
