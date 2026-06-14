import {
  Box,
  Chip,
  MenuItem,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import CodeRoundedIcon from "@mui/icons-material/CodeRounded";
import type { Course } from "../api/coursesApi";

type CourseSelectorCardProps = {
  courses: Course[];
  selectedId: number;
  onChange: (courseId: number) => void;
};

export function CourseSelectorCard({
  courses,
  selectedId,
  onChange,
}: CourseSelectorCardProps) {
  const course = courses.find((item) => item.id === selectedId);

  return (
    <Paper
      elevation={0}
      sx={{
        p: 2.25,
        display: "grid",
        gridTemplateColumns: { xs: "1fr", md: "280px minmax(0,1fr)" },
        gap: 2.5,
        alignItems: "center",
        border: "1px solid #e1e6f2",
        borderRadius: 3,
      }}
    >
      <Box>
        <Typography sx={{ mb: 0.8, fontSize: 13, fontWeight: 800 }}>
          Choisir un cours
        </Typography>
        <TextField
          select
          fullWidth
          size="small"
          value={selectedId}
          onChange={(event) => onChange(Number(event.target.value))}
          slotProps={{ htmlInput: { "aria-label": "Choisir un cours" } }}
        >
          {courses.map((item) => (
            <MenuItem key={item.id} value={item.id}>
              {item.titre}
            </MenuItem>
          ))}
        </TextField>
      </Box>

      {course && (
        <Box sx={{ display: "flex", alignItems: "center", gap: 1.75 }}>
          <Box
            sx={{
              width: 70,
              height: 70,
              display: "grid",
              placeItems: "center",
              flexShrink: 0,
              color: "#fff",
              borderRadius: 2.2,
              background: "linear-gradient(145deg,#5265f5,#7658eb)",
            }}
          >
            <CodeRoundedIcon sx={{ fontSize: 38 }} />
          </Box>
          <Box sx={{ minWidth: 0 }}>
            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
              <Typography noWrap sx={{ fontSize: 18, fontWeight: 850 }}>
                {course.titre}
              </Typography>
              <Chip
                size="small"
                label={
                  course.statut === "PUBLISHED" || course.statut === "VALIDE"
                    ? "Publié"
                    : "Brouillon"
                }
                sx={{
                  color: "#16864f",
                  bgcolor: "#e5f7ec",
                  fontWeight: 750,
                }}
              />
            </Box>
            <Typography color="text.secondary" sx={{ mt: 0.5, fontSize: 12.5 }}>
              {course.visibleCatalogue ? "Catalogue public" : "Cours privé"}
              {" · "}
              Créé le{" "}
              {new Intl.DateTimeFormat("fr-FR").format(
                new Date(course.dateCreation),
              )}
              {" · "}
              {course.profPrenom} {course.profNom}
            </Typography>
          </Box>
        </Box>
      )}
    </Paper>
  );
}
