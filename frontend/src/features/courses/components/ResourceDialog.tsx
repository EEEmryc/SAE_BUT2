import { useMemo, useRef, useState } from "react";
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
import CloudUploadRoundedIcon from "@mui/icons-material/CloudUploadRounded";
import InsertDriveFileRoundedIcon from "@mui/icons-material/InsertDriveFileRounded";
import { getApiErrorMessage } from "../../auth/services/apiError";
import type { Chapter } from "../services/coursesApi";
import { useCreateResource } from "../hooks/useCourses";

const MAX_FILE_SIZE = 1024 * 1024 * 1024;
const ACCEPTED_EXTENSIONS = [
  ".pdf",
  ".doc",
  ".docx",
  ".xls",
  ".xlsx",
  ".zip",
  ".mp4",
  ".webm",
  ".mov",
  ".avi",
].join(",");

type ResourceDialogProps = {
  open: boolean;
  courseId: number;
  chapters: Chapter[];
  defaultChapterId?: number | null;
  onClose: () => void;
  onSaved: (message: string) => void;
};

export function ResourceDialog({
  open,
  courseId,
  chapters,
  defaultChapterId,
  onClose,
  onSaved,
}: ResourceDialogProps) {
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const initialChapterId = useMemo(
    () => defaultChapterId ?? chapters[0]?.id ?? null,
    [chapters, defaultChapterId],
  );
  const [chapterId, setChapterId] = useState<number | null>(initialChapterId);
  const [name, setName] = useState("");
  const [file, setFile] = useState<File | null>(null);
  const [downloadable, setDownloadable] = useState(true);
  const [validationError, setValidationError] = useState("");
  const mutation = useCreateResource(courseId, chapterId ?? 0);

  const selectFile = (selectedFile: File | undefined) => {
    if (!selectedFile) {
      return;
    }
    if (selectedFile.size > MAX_FILE_SIZE) {
      setValidationError("Le fichier ne doit pas dépasser 1 Go.");
      return;
    }
    setValidationError("");
    setFile(selectedFile);
    if (!name) {
      setName(selectedFile.name);
    }
  };

  const submit = async () => {
    if (!chapterId) {
      setValidationError("Sélectionnez un chapitre.");
      return;
    }
    if (!file) {
      setValidationError("Sélectionnez un fichier à uploader.");
      return;
    }

    await mutation.mutateAsync({
      file,
      nom: name,
      telechargeable: downloadable,
    });
    onSaved("Ressource uploadée avec succès");
    onClose();
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle sx={{ fontWeight: 850 }}>Ajouter une ressource</DialogTitle>
      <DialogContent sx={{ display: "grid", gap: 2, pt: "12px !important" }}>
        {(validationError || mutation.isError) && (
          <Alert severity="error">
            {validationError || getApiErrorMessage(mutation.error)}
          </Alert>
        )}
        {chapters.length === 0 ? (
          <Alert severity="warning">
            Ajoutez d’abord un chapitre avant d’associer une ressource.
          </Alert>
        ) : (
          <>
            <TextField
              select
              label="Chapitre associé"
              value={chapterId ?? ""}
              onChange={(event) => setChapterId(Number(event.target.value))}
            >
              {chapters.map((chapter) => (
                <MenuItem key={chapter.id} value={chapter.id}>
                  {chapter.ordre}. {chapter.titre}
                </MenuItem>
              ))}
            </TextField>

            <Box
              role="button"
              tabIndex={0}
              onClick={() => fileInputRef.current?.click()}
              onKeyDown={(event) => {
                if (event.key === "Enter" || event.key === " ") {
                  fileInputRef.current?.click();
                }
              }}
              sx={{
                p: 3,
                display: "grid",
                placeItems: "center",
                gap: 1,
                textAlign: "center",
                cursor: "pointer",
                border: "1.5px dashed #aeb8e7",
                borderRadius: 3,
                bgcolor: "#f8f9ff",
                "&:hover": { borderColor: "#5968f5", bgcolor: "#f3f4ff" },
              }}
            >
              <input
                ref={fileInputRef}
                hidden
                type="file"
                aria-label="Choisir un fichier"
                accept={ACCEPTED_EXTENSIONS}
                onChange={(event) => selectFile(event.target.files?.[0])}
              />
              {file ? (
                <>
                  <InsertDriveFileRoundedIcon
                    sx={{ fontSize: 38, color: "#5364f4" }}
                  />
                  <Typography sx={{ fontWeight: 800 }}>{file.name}</Typography>
                  <Typography color="text.secondary" sx={{ fontSize: 12 }}>
                    {(file.size / 1024 / 1024).toFixed(2)} Mo
                  </Typography>
                </>
              ) : (
                <>
                  <CloudUploadRoundedIcon
                    sx={{ fontSize: 42, color: "#5364f4" }}
                  />
                  <Typography sx={{ fontWeight: 800 }}>
                    Cliquez pour choisir un fichier
                  </Typography>
                  <Typography color="text.secondary" sx={{ fontSize: 12 }}>
                    PDF, Word, Excel, ZIP ou vidéo, 1 Go maximum
                  </Typography>
                </>
              )}
            </Box>

            <TextField
              label="Nom affiché"
              value={name}
              onChange={(event) => setName(event.target.value)}
              placeholder={file?.name ?? "Support du cours"}
            />
            <FormControlLabel
              control={
                <Switch
                  checked={downloadable}
                  onChange={(_, checked) => setDownloadable(checked)}
                />
              }
              label="Autoriser le téléchargement"
            />
          </>
        )}
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 3 }}>
        <Button onClick={onClose}>Annuler</Button>
        <Button
          variant="contained"
          onClick={() => void submit()}
          disabled={chapters.length === 0 || mutation.isPending}
          sx={{
            color: "#fff",
            background: "linear-gradient(110deg,#4056f4,#7458f6)",
          }}
        >
          {mutation.isPending ? "Upload..." : "Uploader"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
