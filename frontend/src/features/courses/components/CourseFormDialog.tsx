import { useEffect } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControlLabel,
  MenuItem,
  Switch,
  TextField,
} from "@mui/material";
import { Controller, useForm } from "react-hook-form";
import { z } from "zod";
import { getApiErrorMessage } from "../../auth/api/apiError";
import type { Course, CoursePayload } from "../api/coursesApi";
import { useCreateCourse, useUpdateCourse } from "../hooks/useCourses";

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
  statut: "DRAFT",
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
  const mutation = course ? updateCourse : createCourse;
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

  const submit = handleSubmit(async (values) => {
    await mutation.mutateAsync(values as CoursePayload);
    onSaved(course ? "Cours modifié avec succès" : "Cours créé avec succès");
    onClose();
  });

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle sx={{ fontWeight: 850 }}>
        {course ? "Modifier le cours" : "Créer un cours"}
      </DialogTitle>
      <DialogContent sx={{ display: "grid", gap: 2, pt: "12px !important" }}>
        {mutation.isError && (
          <Alert severity="error">{getApiErrorMessage(mutation.error)}</Alert>
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
            <FormControlLabel
              control={
                <Switch
                  checked={field.value}
                  onChange={(_, checked) => field.onChange(checked)}
                />
              }
              label="Visible dans le catalogue"
            />
          )}
        />
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 3 }}>
        <Button onClick={onClose}>Annuler</Button>
        <Button
          variant="contained"
          onClick={() => void submit()}
          disabled={mutation.isPending}
          sx={{ color: "#fff", background: "linear-gradient(110deg,#4056f4,#7458f6)" }}
        >
          {mutation.isPending ? "Enregistrement..." : "Enregistrer"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
