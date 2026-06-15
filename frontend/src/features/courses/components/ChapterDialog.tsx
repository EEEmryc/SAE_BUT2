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
  TextField,
  Typography,
} from "@mui/material";
import AttachFileRoundedIcon from "@mui/icons-material/AttachFileRounded";
import DeleteOutlineRoundedIcon from "@mui/icons-material/DeleteOutlineRounded";
import UploadFileRoundedIcon from "@mui/icons-material/UploadFileRounded";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { getApiErrorMessage } from "../../auth/services/apiError";
import type { Chapter } from "../services/coursesApi";
import {
  useDeleteChapterMainFile,
  useSaveChapter,
  useUploadChapterMainFile,
} from "../hooks/useCourses";

const MAX_FILE_SIZE = 1024 * 1024 * 1024;
const ACCEPTED_FILES =
  ".pdf,.doc,.docx,.xls,.xlsx,.zip,.mp4,.webm,.mov,.avi";

const chapterSchema = z.object({
  titre: z.string().trim().min(3, "Le titre est obligatoire"),
  contenu: z.string().trim().min(5, "Le contenu est obligatoire"),
  ordre: z.number().int().min(1, "L'ordre doit etre superieur a zero"),
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
  const uploadMainFile = useUploadChapterMainFile(courseId);
  const deleteMainFile = useDeleteChapterMainFile(
    courseId,
    chapter?.id ?? 0,
  );
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const [mainFile, setMainFile] = useState<File | null>(null);
  const [fileError, setFileError] = useState("");
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

  const closeDialog = () => {
    setMainFile(null);
    setFileError("");
    onClose();
  };

  const submit = handleSubmit(async (values) => {
    const savedChapter = await mutation.mutateAsync(values);
    if (mainFile) {
      await uploadMainFile.mutateAsync({
        chapterId: savedChapter.id,
        file: mainFile,
      });
    }
    setMainFile(null);
    onSaved(
      chapter ? "Chapitre modifie avec succes" : "Chapitre ajoute avec succes",
    );
    onClose();
  });

  const apiError =
    mutation.error ?? uploadMainFile.error ?? deleteMainFile.error;

  return (
    <Dialog open={open} onClose={closeDialog} fullWidth maxWidth="sm">
      <DialogTitle sx={{ fontWeight: 850 }}>
        {chapter ? "Modifier le chapitre" : "Ajouter un chapitre"}
      </DialogTitle>
      <DialogContent sx={{ display: "grid", gap: 2, pt: "12px !important" }}>
        {(apiError || fileError) && (
          <Alert severity="error">
            {fileError || getApiErrorMessage(apiError)}
          </Alert>
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
            aria-label="Fichier principal du chapitre"
            accept={ACCEPTED_FILES}
            onChange={(event) => {
              const selected = event.target.files?.[0] ?? null;
              if (selected && selected.size > MAX_FILE_SIZE) {
                setFileError("Le fichier ne doit pas depasser 1 Go.");
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
                Fichier principal du chapitre
              </Typography>
              <Typography color="text.secondary" sx={{ fontSize: 12 }}>
                PDF, Word, Excel, ZIP ou video, 1 Go maximum
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
          {(mainFile || chapter?.fichierPrincipalNom) && (
            <Box
              sx={{
                mt: 1.5,
                px: 1.25,
                py: 1,
                display: "flex",
                alignItems: "center",
                gap: 1,
                bgcolor: "#fff",
                borderRadius: 1.5,
              }}
            >
              <Typography noWrap sx={{ flex: 1, fontSize: 13, fontWeight: 700 }}>
                {mainFile?.name ?? chapter?.fichierPrincipalNom}
              </Typography>
              {chapter?.fichierPrincipalNom && !mainFile && (
                <Button
                  color="error"
                  size="small"
                  startIcon={<DeleteOutlineRoundedIcon />}
                  disabled={deleteMainFile.isPending}
                  onClick={async () => {
                    await deleteMainFile.mutateAsync();
                    onSaved("Fichier du chapitre supprime");
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
          sx={{ color: "#fff" }}
        >
          {mutation.isPending || uploadMainFile.isPending
            ? "Enregistrement..."
            : "Enregistrer"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
