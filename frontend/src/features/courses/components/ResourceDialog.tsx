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
import type { ResourcePayload } from "../api/coursesApi";
import { useCreateResource } from "../hooks/useCourses";

const resourceSchema = z.object({
  nom: z.string().trim().min(3, "Le nom est obligatoire"),
  url: z.url("Saisissez une URL valide"),
  type: z.string().min(1, "Le type est obligatoire"),
  telechargeable: z.boolean(),
});

type ResourceFormValues = z.infer<typeof resourceSchema>;

type ResourceDialogProps = {
  open: boolean;
  courseId: number;
  chapterId: number;
  onClose: () => void;
  onSaved: (message: string) => void;
};

export function ResourceDialog({
  open,
  courseId,
  chapterId,
  onClose,
  onSaved,
}: ResourceDialogProps) {
  const mutation = useCreateResource(courseId, chapterId);
  const {
    control,
    formState: { errors },
    handleSubmit,
    register,
    reset,
  } = useForm<ResourceFormValues>({
    resolver: zodResolver(resourceSchema),
    defaultValues: {
      nom: "",
      url: "",
      type: "PDF",
      telechargeable: true,
    },
  });

  const submit = handleSubmit(async (values) => {
    await mutation.mutateAsync(values as ResourcePayload);
    reset();
    onSaved("Ressource ajoutée avec succès");
    onClose();
  });

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle sx={{ fontWeight: 850 }}>Ajouter une ressource</DialogTitle>
      <DialogContent sx={{ display: "grid", gap: 2, pt: "12px !important" }}>
        {mutation.isError && (
          <Alert severity="error">{getApiErrorMessage(mutation.error)}</Alert>
        )}
        <TextField
          label="Nom de la ressource"
          autoFocus
          error={Boolean(errors.nom)}
          helperText={errors.nom?.message}
          {...register("nom")}
        />
        <TextField
          label="URL du fichier ou de la ressource"
          placeholder="https://.../support.pdf"
          error={Boolean(errors.url)}
          helperText={errors.url?.message ?? "Les fichiers PDF sont associés par URL."}
          {...register("url")}
        />
        <Controller
          name="type"
          control={control}
          render={({ field }) => (
            <TextField select label="Type" {...field}>
              <MenuItem value="PDF">PDF</MenuItem>
              <MenuItem value="LINK">Lien web</MenuItem>
              <MenuItem value="VIDEO">Vidéo</MenuItem>
              <MenuItem value="ZIP">Archive ZIP</MenuItem>
              <MenuItem value="OTHER">Autre</MenuItem>
            </TextField>
          )}
        />
        <Controller
          name="telechargeable"
          control={control}
          render={({ field }) => (
            <FormControlLabel
              control={
                <Switch
                  checked={field.value}
                  onChange={(_, checked) => field.onChange(checked)}
                />
              }
              label="Autoriser le téléchargement"
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
          sx={{ color: "#fff" }}
        >
          Ajouter
        </Button>
      </DialogActions>
    </Dialog>
  );
}
