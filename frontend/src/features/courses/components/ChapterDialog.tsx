import { useEffect } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from "@mui/material";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { getApiErrorMessage } from "../../auth/api/apiError";
import type { Chapter } from "../api/coursesApi";
import { useSaveChapter } from "../hooks/useCourses";

const chapterSchema = z.object({
  titre: z.string().trim().min(3, "Le titre est obligatoire"),
  contenu: z.string().trim().min(5, "Le contenu est obligatoire"),
  ordre: z.number().int().min(1, "L'ordre doit être supérieur à zéro"),
});

type ChapterFormValues = z.infer<typeof chapterSchema>;

type ChapterDialogProps = {
  open: boolean;
  courseId: number;
  chapter?: Chapter | null;
  nextOrder: number;
  onClose: () => void;
  onSaved: (message: string) => void;
};

export function ChapterDialog({
  open,
  courseId,
  chapter,
  nextOrder,
  onClose,
  onSaved,
}: ChapterDialogProps) {
  const mutation = useSaveChapter(courseId, chapter?.id);
  const {
    formState: { errors },
    handleSubmit,
    register,
    reset,
  } = useForm<ChapterFormValues>({
    resolver: zodResolver(chapterSchema),
    defaultValues: { titre: "", contenu: "", ordre: nextOrder },
  });

  useEffect(() => {
    reset(
      chapter
        ? {
            titre: chapter.titre,
            contenu: chapter.contenu,
            ordre: chapter.ordre,
          }
        : { titre: "", contenu: "", ordre: nextOrder },
    );
  }, [chapter, nextOrder, open, reset]);

  const submit = handleSubmit(async (values) => {
    await mutation.mutateAsync(values);
    onSaved(chapter ? "Chapitre modifié avec succès" : "Chapitre ajouté avec succès");
    onClose();
  });

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle sx={{ fontWeight: 850 }}>
        {chapter ? "Modifier le chapitre" : "Ajouter un chapitre"}
      </DialogTitle>
      <DialogContent sx={{ display: "grid", gap: 2, pt: "12px !important" }}>
        {mutation.isError && (
          <Alert severity="error">{getApiErrorMessage(mutation.error)}</Alert>
        )}
        <TextField
          label="Titre"
          autoFocus
          error={Boolean(errors.titre)}
          helperText={errors.titre?.message}
          {...register("titre")}
        />
        <TextField
          label="Contenu"
          multiline
          minRows={5}
          error={Boolean(errors.contenu)}
          helperText={errors.contenu?.message}
          {...register("contenu")}
        />
        <TextField
          label="Ordre"
          type="number"
          error={Boolean(errors.ordre)}
          helperText={errors.ordre?.message}
          {...register("ordre", { valueAsNumber: true })}
        />
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 3 }}>
        <Button onClick={onClose}>Annuler</Button>
        <Button
          variant="contained"
          onClick={() => void submit()}
          disabled={mutation.isPending}
          sx={{ color: "#fff" }}
        >
          Enregistrer
        </Button>
      </DialogActions>
    </Dialog>
  );
}
