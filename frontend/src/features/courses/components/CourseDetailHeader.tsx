import {
  Box,
  Button,
  Chip,
  Paper,
  Typography,
} from "@mui/material";
import AddRoundedIcon from "@mui/icons-material/AddRounded";
import CalendarMonthOutlinedIcon from "@mui/icons-material/CalendarMonthOutlined";
import CodeRoundedIcon from "@mui/icons-material/CodeRounded";
import DeleteOutlineRoundedIcon from "@mui/icons-material/DeleteOutlineRounded";
import DownloadRoundedIcon from "@mui/icons-material/DownloadRounded";
import EditRoundedIcon from "@mui/icons-material/EditRounded";
import PersonOutlineRoundedIcon from "@mui/icons-material/PersonOutlineRounded";
import PublicRoundedIcon from "@mui/icons-material/PublicRounded";
import VisibilityOffOutlinedIcon from "@mui/icons-material/VisibilityOffOutlined";
import { coursesApi, type Course, type CourseStatus } from "../api/coursesApi";

const statusLabels: Record<CourseStatus, string> = {
  DRAFT: "Brouillon",
  PUBLISHED: "Publié",
  VALIDE: "Validé",
  ARCHIVE: "Archivé",
};

type CourseDetailHeaderProps = {
  course: Course;
  onEdit: () => void;
  onDelete: () => void;
  onAddResource: () => void;
};

export function CourseDetailHeader({
  course,
  onEdit,
  onDelete,
  onAddResource,
}: CourseDetailHeaderProps) {
  const published = course.statut === "PUBLISHED" || course.statut === "VALIDE";
  const metadata = [
    {
      icon: course.visibleCatalogue ? <PublicRoundedIcon /> : <VisibilityOffOutlinedIcon />,
      label: "Visibilité",
      value: course.visibleCatalogue ? "Catalogue public" : "Cours privé",
    },
    {
      icon: <PersonOutlineRoundedIcon />,
      label: "Professeur",
      value: `${course.profPrenom} ${course.profNom}`,
    },
    {
      icon: <CalendarMonthOutlinedIcon />,
      label: "Créé le",
      value: new Intl.DateTimeFormat("fr-FR").format(
        new Date(course.dateCreation),
      ),
    },
  ];

  return (
    <Paper
      elevation={0}
      sx={{
        p: { xs: 2, md: 2.75 },
        border: "1px solid #e1e6f2",
        borderRadius: 3,
        boxShadow: "0 12px 36px rgba(52,64,125,0.055)",
      }}
    >
      <Box
        sx={{
          display: "grid",
          gridTemplateColumns: { xs: "1fr", md: "116px minmax(0,1fr) auto" },
          gap: { xs: 2, md: 2.5 },
          alignItems: "center",
        }}
      >
        <Box
          sx={{
            height: 116,
            display: "grid",
            placeItems: "center",
            borderRadius: 2.5,
            color: "#fff",
            background:
              "linear-gradient(150deg,#596bf5 0%,#4d57d7 52%,#8a77e8 100%)",
            boxShadow: "inset 0 0 0 1px rgba(255,255,255,.18)",
          }}
        >
          <Box
            sx={{
              width: 72,
              height: 61,
              display: "grid",
              placeItems: "center",
              border: "5px solid rgba(255,255,255,.9)",
              borderRadius: 1.5,
              boxShadow: "0 8px 20px rgba(34,39,125,.25)",
            }}
          >
            <CodeRoundedIcon sx={{ fontSize: 40 }} />
          </Box>
        </Box>

        <Box sx={{ minWidth: 0 }}>
          <Box
            sx={{
              display: "flex",
              flexWrap: "wrap",
              alignItems: "center",
              gap: 1,
            }}
          >
            <Typography
              component="h1"
              sx={{
                fontSize: { xs: 26, md: 31 },
                lineHeight: 1.15,
                fontWeight: 850,
                letterSpacing: "-0.035em",
              }}
            >
              {course.titre}
            </Typography>
            <Chip
              size="small"
              label={statusLabels[course.statut]}
              sx={{
                height: 25,
                color: published ? "#14824d" : "#695f78",
                bgcolor: published ? "#e4f7eb" : "#f0eef4",
                fontWeight: 800,
                "&::before": {
                  content: '""',
                  width: 7,
                  height: 7,
                  ml: 0.8,
                  borderRadius: "50%",
                  bgcolor: published ? "#20ae63" : "#8d8798",
                },
              }}
            />
          </Box>
          <Typography
            color="text.secondary"
            sx={{ mt: 1, maxWidth: 720, lineHeight: 1.55 }}
          >
            {course.description}
          </Typography>
          <Box
            sx={{
              mt: 2,
              display: "flex",
              flexWrap: "wrap",
              gap: { xs: 1.75, md: 3 },
            }}
          >
            {metadata.map((item) => (
              <Box
                key={item.label}
                sx={{ display: "flex", alignItems: "center", gap: 0.75 }}
              >
                <Box sx={{ color: "#5263e8", "& svg": { fontSize: 18 } }}>
                  {item.icon}
                </Box>
                <Box>
                  <Typography color="text.secondary" sx={{ fontSize: 11 }}>
                    {item.label}
                  </Typography>
                  <Typography sx={{ fontSize: 12.5, fontWeight: 750 }}>
                    {item.value}
                  </Typography>
                </Box>
              </Box>
            ))}
          </Box>
          {course.fichierPrincipalUrl && course.fichierPrincipalNom && (
            <Button
              size="small"
              startIcon={<DownloadRoundedIcon />}
              onClick={() =>
                void coursesApi.downloadFile(
                  course.fichierPrincipalUrl!,
                  course.fichierPrincipalNom!,
                )
              }
              sx={{ mt: 1.25, px: 0 }}
            >
              {course.fichierPrincipalNom}
            </Button>
          )}
        </Box>

        <Box
          sx={{
            display: "flex",
            flexWrap: "wrap",
            justifyContent: { xs: "stretch", md: "flex-end" },
            gap: 1,
            "& .MuiButton-root": {
              minHeight: 40,
              px: 1.6,
              fontSize: 13,
            },
          }}
        >
          <Button
            variant="outlined"
            startIcon={<EditRoundedIcon />}
            onClick={onEdit}
          >
            Modifier le cours
          </Button>
          <Button
            variant="contained"
            startIcon={<AddRoundedIcon />}
            onClick={onAddResource}
            sx={{
              color: "#fff",
              background: "linear-gradient(110deg,#4257f3,#6d4ce8)",
            }}
          >
            Ajouter une ressource
          </Button>
          <Button
            variant="outlined"
            color="error"
            startIcon={<DeleteOutlineRoundedIcon />}
            onClick={onDelete}
          >
            Supprimer
          </Button>
        </Box>
      </Box>
    </Paper>
  );
}
