import {
  Box,
  Button,
  Chip,
  IconButton,
  Paper,
  Typography,
} from "@mui/material";
import AddRoundedIcon from "@mui/icons-material/AddRounded";
import DeleteOutlineRoundedIcon from "@mui/icons-material/DeleteOutlineRounded";
import DescriptionRoundedIcon from "@mui/icons-material/DescriptionRounded";
import DownloadRoundedIcon from "@mui/icons-material/DownloadRounded";
import FolderZipRoundedIcon from "@mui/icons-material/FolderZipRounded";
import GridOnRoundedIcon from "@mui/icons-material/GridOnRounded";
import MovieRoundedIcon from "@mui/icons-material/MovieRounded";
import TextSnippetRoundedIcon from "@mui/icons-material/TextSnippetRounded";
import type { CourseResource } from "../services/coursesApi";
import { coursesApi } from "../services/coursesApi";

type ResourceListProps = {
  resources: CourseResource[];
  title?: string;
  limit?: number;
  onAdd?: () => void;
  onDelete?: (resource: CourseResource) => void;
};

function resourceIcon(type: string) {
  const normalized = type.toUpperCase();
  if (normalized === "PDF") {
    return <DescriptionRoundedIcon />;
  }
  if (normalized === "EXCEL") {
    return <GridOnRoundedIcon />;
  }
  if (normalized === "ZIP") {
    return <FolderZipRoundedIcon />;
  }
  if (normalized === "VIDEO") {
    return <MovieRoundedIcon />;
  }
  return <TextSnippetRoundedIcon />;
}

function resourceColor(type: string) {
  return {
    PDF: { color: "#e54a55", bgcolor: "#fff0f1" },
    EXCEL: { color: "#1da35c", bgcolor: "#eaf8f0" },
    ZIP: { color: "#eea31b", bgcolor: "#fff6df" },
    VIDEO: { color: "#5866f2", bgcolor: "#edf0ff" },
    WORD: { color: "#3476dc", bgcolor: "#eaf2ff" },
  }[type.toUpperCase()] ?? { color: "#596783", bgcolor: "#eff1f6" };
}

function formatSize(size: number | null) {
  if (!size) {
    return null;
  }
  return size >= 1024 * 1024
    ? `${(size / 1024 / 1024).toFixed(1)} Mo`
    : `${Math.ceil(size / 1024)} Ko`;
}

export function ResourceList({
  resources,
  title = "Ressources récentes",
  limit,
  onAdd,
  onDelete,
}: ResourceListProps) {
  const visibleResources = limit ? resources.slice(0, limit) : resources;

  return (
    <Paper
      elevation={0}
      sx={{
        border: "1px solid #e1e6f2",
        borderRadius: 2.5,
        overflow: "hidden",
      }}
    >
      <Box
        sx={{
          px: 2,
          py: 1.5,
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          gap: 1,
        }}
      >
        <Typography sx={{ fontSize: 15, fontWeight: 850 }}>{title}</Typography>
        {onAdd && (
          <Button
            startIcon={<AddRoundedIcon />}
            onClick={onAdd}
            sx={{ minHeight: 34, fontSize: 12 }}
          >
            Ajouter
          </Button>
        )}
      </Box>
      {visibleResources.length === 0 ? (
        <Box sx={{ px: 2, py: 4, textAlign: "center", bgcolor: "#fafbff" }}>
          <Typography color="text.secondary" sx={{ fontSize: 13 }}>
            Aucune ressource ajoutée pour le moment.
          </Typography>
        </Box>
      ) : (
        visibleResources.map((resource) => {
          const colors = resourceColor(resource.type);
          return (
            <Box
              key={resource.id}
              sx={{
                px: 1.75,
                py: 1.1,
                display: "flex",
                alignItems: "center",
                gap: 1.25,
                borderTop: "1px solid #edf0f7",
              }}
            >
              <Box
                sx={{
                  width: 34,
                  height: 34,
                  display: "grid",
                  placeItems: "center",
                  flexShrink: 0,
                  borderRadius: 1.5,
                  ...colors,
                  "& svg": { fontSize: 20 },
                }}
              >
                {resourceIcon(resource.type)}
              </Box>
              <Box sx={{ flex: 1, minWidth: 0 }}>
                <Typography noWrap sx={{ fontSize: 13, fontWeight: 800 }}>
                  {resource.nom}
                </Typography>
                <Typography color="text.secondary" sx={{ fontSize: 11.5 }}>
                  {[resource.type, formatSize(resource.tailleOctets), resource.chapitreTitre]
                    .filter(Boolean)
                    .join(" · ")}
                </Typography>
              </Box>
              <Chip
                size="small"
                label={new Intl.DateTimeFormat("fr-FR").format(
                  new Date(resource.dateCreation),
                )}
                sx={{ display: { xs: "none", sm: "flex" }, fontSize: 10.5 }}
              />
              <IconButton
                size="small"
                aria-label={`Télécharger ${resource.nom}`}
                onClick={() => void coursesApi.downloadResource(resource)}
              >
                <DownloadRoundedIcon sx={{ fontSize: 18 }} />
              </IconButton>
              {onDelete && (
                <IconButton
                  size="small"
                  color="error"
                  aria-label={`Supprimer ${resource.nom}`}
                  onClick={() => onDelete(resource)}
                >
                  <DeleteOutlineRoundedIcon sx={{ fontSize: 18 }} />
                </IconButton>
              )}
            </Box>
          );
        })
      )}
    </Paper>
  );
}
