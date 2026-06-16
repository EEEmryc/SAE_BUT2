import { useEffect, useRef, useState } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControlLabel,
  MenuItem,
  Switch,
  TextField,
  Typography,
} from "@mui/material";
import AttachFileRoundedIcon from "@mui/icons-material/AttachFileRounded";
import DeleteOutlineRoundedIcon from "@mui/icons-material/DeleteOutlineRounded";
import UploadFileRoundedIcon from "@mui/icons-material/UploadFileRounded";
import { Controller, useForm } from "react-hook-form";
import { z } from "zod";
import { getApiErrorMessage } from "../../auth/services/apiError";
import type { Course, CoursePayload } from "../services/coursesApi";
import {
  useCreateCourse,
  useDeleteCourseMainFile,
  useUpdateCourse,
  useUploadCourseMainFile,
} from "../hooks/useCourses";

const MAX_FILE_SIZE = 1024 * 1024 * 1024;
const ACCEPTED_FILES =
  ".pdf,.doc,.docx,.xls,.xlsx,.zip,.mp4,.webm,.mov,.avi";

const courseSchema = z.object({
  titre: z.string().trim().min(3, "Le titre doit contenir au moins 3 caractères"),
  description: z
    .string()
    .trim()
    .min(10, "La description doit contenir au moins 10 caractères"),
  statut: z.enum(["DRAFT", "PUBLISHED", "VALIDE", "ARCHIVE"]),
  visibleCatalogue: z.boolean(),
});

type CourseFormValues = z.infer<typeof courseSchema>;

type CourseFormDialogProps = {
  open: boolean;
  course?: Course | null;
  onClose: () => void;
  onSaved: (message: string) => void;
};

const emptyValues: CourseFormValues = {
  titre: "",
  description: "",
  statut: "PUBLISHED",
  visibleCatalogue: true,
};

export function CourseFormDialog({
  open,
  course,
  onClose,
  onSaved,
}: CourseFormDialogProps) {
  const createCourse = useCreateCourse();
  const updateCourse = useUpdateCourse(course?.id ?? 0);
  const uploadMainFile = useUploadCourseMainFile();
  const deleteMainFile = useDeleteCourseMainFile(course?.id ?? 0);
  const mutation = course ? updateCourse : createCourse;
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const [mainFile, setMainFile] = useState<File | null>(null);
  const [fileError, setFileError] = useState("");
  const {
    control,
    formState: { errors },
    handleSubmit,
    register,
    reset,
  } = useForm<CourseFormValues>({
    resolver: zodResolver(courseSchema),
    defaultValues: emptyValues,
  });

  useEffect(() => {
    reset(
      course
        ? {
            titre: course.titre,
            description: course.description,
            statut: course.statut,
            visibleCatalogue: course.visibleCatalogue,
          }
        : emptyValues,
    );
  }, [course, open, reset]);

  const closeDialog = () => {
    setMainFile(null);
    setFileError("");
    onClose();
  };

  const submit = handleSubmit(async (values) => {
    const savedCourse = await mutation.mutateAsync(values as CoursePayload);
    if (mainFile) {
      await uploadMainFile.mutateAsync({
        courseId: savedCourse.id,
        file: mainFile,
      });
    }
    setMainFile(null);
    setFileError("");
    onSaved(course ? "Cours modifié avec succès" : "Cours créé avec succès");
    onClose();
  });

  const apiError = mutation.error ?? uploadMainFile.error ?? deleteMainFile.error;

  return (
    <Dialog open={open} onClose={closeDialog} fullWidth maxWidth="sm">
      <DialogTitle sx={{ fontWeight: 850 }}>
        {course ? "Modifier le cours" : "Créer un cours"}
      </DialogTitle>
      <DialogContent sx={{ display: "grid", gap: 2, pt: "12px !important" }}>
        {(apiError || fileError) && (
          <Alert severity="error">
            {fileError || getApiErrorMessage(apiError)}
          </Alert>
        )}
        <TextField
          label="Titre du cours"
          autoFocus
          error={Boolean(errors.titre)}
          helperText={errors.titre?.message}
          {...register("titre")}
        />
        <TextField
          label="Description"
          multiline
          minRows={4}
          error={Boolean(errors.description)}
          helperText={errors.description?.message}
          {...register("description")}
        />
        <Controller
          name="statut"
          control={control}
          render={({ field }) => (
            <TextField select label="Statut" {...field}>
              <MenuItem value="DRAFT">Brouillon</MenuItem>
              <MenuItem value="PUBLISHED">Publié</MenuItem>
              <MenuItem value="VALIDE">Validé</MenuItem>
              <MenuItem value="ARCHIVE">Archivé</MenuItem>
            </TextField>
          )}
        />
        <Controller
          name="visibleCatalogue"
          control={control}
          render={({ field }) => (
            <Box>
              <FormControlLabel
                control={
                  <Switch
                    checked={field.value}
                    onChange={(_, checked) => field.onChange(checked)}
                  />
                }
                label="Visible dans le catalogue"
              />
              <Typography color="text.secondary" sx={{ fontSize: 12 }}>
                Seuls les cours publiés ou validés et visibles apparaissent dans
                le catalogue étudiant.
              </Typography>
            </Box>
          )}
        />

        <Box
          sx={{
            p: 2,
            border: "1px dashed #b8c1eb",
            borderRadius: 2.5,
            bgcolor: "#fafbff",
          }}
        >
          <input
            ref={fileInputRef}
            hidden
            type="file"
            aria-label="Fichier principal du cours"
            accept={ACCEPTED_FILES}
            onChange={(event) => {
              const selected = event.target.files?.[0] ?? null;
              if (selected && selected.size > MAX_FILE_SIZE) {
                setFileError("Le fichier ne doit pas dépasser 1 Go.");
                return;
              }
              setFileError("");
              setMainFile(selected);
            }}
          />
          <Box sx={{ display: "flex", alignItems: "center", gap: 1.25 }}>
            <AttachFileRoundedIcon sx={{ color: "#5364f4" }} />
            <Box sx={{ flex: 1, minWidth: 0 }}>
              <Typography sx={{ fontWeight: 800 }}>
                Fichier principal du cours
              </Typography>
              <Typography color="text.secondary" sx={{ fontSize: 12 }}>
                PDF, Word, Excel, ZIP ou vidéo, 1 Go maximum
              </Typography>
            </Box>
            <Button
              variant="outlined"
              startIcon={<UploadFileRoundedIcon />}
              onClick={() => fileInputRef.current?.click()}
            >
              Parcourir
            </Button>
          </Box>
          {(mainFile || course?.fichierPrincipalNom) && (
            <Box
              sx={{
                mt: 1.5,
                px: 1.25,
                py: 1,
                display: "flex",
                alignItems: "center",
                gap: 1,
                bgcolor: "var(--lh-surface)",
                borderRadius: 1.5,
              }}
            >
              <Typography noWrap sx={{ flex: 1, fontSize: 13, fontWeight: 700 }}>
                {mainFile?.name ?? course?.fichierPrincipalNom}
              </Typography>
              {course?.fichierPrincipalNom && !mainFile && (
                <Button
                  color="error"
                  size="small"
                  startIcon={<DeleteOutlineRoundedIcon />}
                  disabled={deleteMainFile.isPending}
                  onClick={async () => {
                    await deleteMainFile.mutateAsync();
                    onSaved("Fichier principal supprimé");
                  }}
                >
                  Retirer
                </Button>
              )}
            </Box>
          )}
        </Box>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 3 }}>
        <Button onClick={closeDialog}>Annuler</Button>
        <Button
          variant="contained"
          onClick={() => void submit()}
          disabled={mutation.isPending || uploadMainFile.isPending}
          sx={{
            color: "#fff",
            background: "linear-gradient(110deg,#4056f4,#7458f6)",
          }}
        >
          {mutation.isPending || uploadMainFile.isPending
            ? "Enregistrement..."
            : "Enregistrer"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
