import {
  Box,
  Divider,
  Paper,
  Typography,
} from "@mui/material";
import PersonAddAlt1RoundedIcon from "@mui/icons-material/PersonAddAlt1Rounded";
import { CreateUserForm } from "./CreateUserForm";
import type { CreateUserFormValues } from "../schemas/createUserSchema";

type CreateUserPanelProps = {
  onCancel: () => void;
  initialValues?: CreateUserFormValues;
  sourceRequestId?: number;
};

export function CreateUserPanel({
  onCancel,
  initialValues,
  sourceRequestId,
}: CreateUserPanelProps) {
  return (
    <Box
      sx={{
        display: "grid",
        gridTemplateColumns: { xs: "1fr", lg: "minmax(0, 1fr) 300px" },
        gap: 3,
        alignItems: "start",
      }}
    >
      <Paper
        elevation={0}
        sx={{
          p: { xs: 2.25, sm: 3.5 },
          border: "1px solid #e5e8f4",
          borderRadius: 3.5,
          boxShadow: "0 18px 50px rgba(54, 64, 125, 0.08)",
        }}
      >
        <Box sx={{ display: "flex", alignItems: "center", gap: 1.5, mb: 3 }}>
          <Box
            sx={{
              width: 52,
              height: 52,
              display: "grid",
              placeItems: "center",
              borderRadius: "50%",
              color: "primary.main",
              bgcolor: "rgba(79,95,247,0.1)",
            }}
          >
            <PersonAddAlt1RoundedIcon />
          </Box>
          <Box>
            <Typography sx={{ fontSize: 19, fontWeight: 800 }}>
              Informations de l&apos;utilisateur
            </Typography>
            <Typography color="text.secondary" sx={{ fontSize: 14 }}>
              Renseignez les informations nécessaires à la création du compte.
            </Typography>
          </Box>
        </Box>
        <CreateUserForm
          onCancel={onCancel}
          initialValues={initialValues}
          sourceRequestId={sourceRequestId}
        />
      </Paper>
    </Box>
  );
}
