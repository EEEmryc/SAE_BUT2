import { zodResolver } from "@hookform/resolvers/zod";
import {
  Alert,
  Box,
  Button,
  InputAdornment,
  MenuItem,
  TextField,
} from "@mui/material";
import CategoryRoundedIcon from "@mui/icons-material/CategoryRounded";
import SendRoundedIcon from "@mui/icons-material/SendRounded";
import SubjectRoundedIcon from "@mui/icons-material/SubjectRounded";
import { Controller, useForm } from "react-hook-form";
import { getApiErrorMessage } from "../../auth/services/apiError";
import { categoryLabels } from "./reportDisplay";
import { useCreateReport } from "../hooks/useCreateReport";
import {
  createReportSchema,
  reportCategories,
  type CreateReportFormValues,
} from "../schemas/createReportSchema";

const defaultValues: CreateReportFormValues = {
  sujet: "",
  categorie: "CONTENU",
  description: "",
};

export function ReportIssueForm() {
  const createReport = useCreateReport();
  const { control, handleSubmit, reset } = useForm<CreateReportFormValues>({
    resolver: zodResolver(createReportSchema),
    defaultValues,
    mode: "onTouched",
  });

  const submit = handleSubmit((values) => {
    createReport.mutate(values, {
      onSuccess: () => reset(defaultValues),
    });
  });

  return (
    <Box component="form" noValidate onSubmit={(event) => void submit(event)}>
      {createReport.isError && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {getApiErrorMessage(createReport.error)}
        </Alert>
      )}

      {createReport.isSuccess && (
        <Alert severity="success" sx={{ mb: 3 }}>
          Votre signalement a bien été envoyé. Un administrateur va le traiter.
        </Alert>
      )}

      <Box sx={{ display: "grid", gap: 2.5 }}>
        <Controller
          name="sujet"
          control={control}
          render={({ field, fieldState }) => (
            <TextField
              {...field}
              label="Sujet"
              placeholder="Ex. : Contenu inapproprié dans un chapitre"
              error={Boolean(fieldState.error)}
              helperText={fieldState.error?.message}
              slotProps={{
                input: {
                  startAdornment: (
                    <InputAdornment position="start">
                      <SubjectRoundedIcon color="action" />
                    </InputAdornment>
                  ),
                },
              }}
            />
          )}
        />

        <Controller
          name="categorie"
          control={control}
          render={({ field, fieldState }) => (
            <TextField
              {...field}
              select
              label="Catégorie"
              error={Boolean(fieldState.error)}
              helperText={fieldState.error?.message}
              slotProps={{
                input: {
                  startAdornment: (
                    <InputAdornment position="start">
                      <CategoryRoundedIcon color="action" />
                    </InputAdornment>
                  ),
                },
              }}
            >
              {reportCategories.map((categorie) => (
                <MenuItem key={categorie} value={categorie}>
                  {categoryLabels[categorie]}
                </MenuItem>
              ))}
            </TextField>
          )}
        />

        <Controller
          name="description"
          control={control}
          render={({ field, fieldState }) => (
            <TextField
              {...field}
              label="Description"
              placeholder="Décrivez le problème rencontré le plus précisément possible."
              multiline
              minRows={5}
              error={Boolean(fieldState.error)}
              helperText={fieldState.error?.message}
            />
          )}
        />
      </Box>

      <Box sx={{ mt: 3.5, display: "flex", gap: 1.5 }}>
        <Button
          type="submit"
          variant="contained"
          loading={createReport.isPending}
          startIcon={<SendRoundedIcon />}
          sx={{
            px: 3,
            color: "#fff",
            background: "linear-gradient(110deg, #4056f4 0%, #7458f6 100%)",
            boxShadow: "0 12px 28px rgba(79,95,247,0.22)",
          }}
        >
          Envoyer le signalement
        </Button>
      </Box>
    </Box>
  );
}