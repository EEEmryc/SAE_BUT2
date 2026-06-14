import {
  Box,
  Button,
  Chip,
  IconButton,
  Paper,
  Typography,
} from "@mui/material";
import AddRoundedIcon from "@mui/icons-material/AddRounded";
import AttachFileRoundedIcon from "@mui/icons-material/AttachFileRounded";
import DeleteOutlineRoundedIcon from "@mui/icons-material/DeleteOutlineRounded";
import DownloadRoundedIcon from "@mui/icons-material/DownloadRounded";
import DragIndicatorRoundedIcon from "@mui/icons-material/DragIndicatorRounded";
import EditRoundedIcon from "@mui/icons-material/EditRounded";
import type {
  Chapter,
  CourseResource,
  CourseStatus,
} from "../api/coursesApi";

type ChapterListProps = {
  chapters: Chapter[];
  resources: CourseResource[];
  courseStatus: CourseStatus;
  onAdd: () => void;
  onEdit: (chapter: Chapter) => void;
  onDelete: (chapter: Chapter) => void;
  onAddResource?: (chapter: Chapter) => void;
  onDownloadFile?: (chapter: Chapter) => void;
};

export function ChapterList({
  chapters,
  resources,
  courseStatus,
  onAdd,
  onEdit,
  onDelete,
  onAddResource,
  onDownloadFile,
}: ChapterListProps) {
  const published = courseStatus === "PUBLISHED" || courseStatus === "VALIDE";

  return (
    <Box>
      <Box
        sx={{
          mb: 1.5,
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          gap: 1,
        }}
      >
        <Typography sx={{ fontSize: 16, fontWeight: 850 }}>Chapitres</Typography>
        <Button
          variant="outlined"
          startIcon={<AddRoundedIcon />}
          onClick={onAdd}
          sx={{ minHeight: 38, px: 1.8, fontSize: 12.5 }}
        >
          Ajouter un chapitre
        </Button>
      </Box>

      {chapters.length === 0 ? (
        <Paper
          elevation={0}
          sx={{
            py: 6,
            textAlign: "center",
            border: "1px dashed #cbd2ef",
            borderRadius: 2,
            bgcolor: "#fafbff",
          }}
        >
          <Typography sx={{ fontWeight: 800 }}>Aucun chapitre</Typography>
          <Typography color="text.secondary" sx={{ mt: 0.5, fontSize: 13 }}>
            Structurez le cours en ajoutant son premier chapitre.
          </Typography>
        </Paper>
      ) : (
        <Box sx={{ display: "grid", gap: 0.75 }}>
          {chapters.map((chapter) => {
            const resourceCount = resources.filter(
              (resource) => resource.chapitreId === chapter.id,
            ).length;
            return (
              <Paper
                key={chapter.id}
                elevation={0}
                sx={{
                  minHeight: 56,
                  px: 1.25,
                  py: 0.75,
                  display: "flex",
                  alignItems: "center",
                  gap: 1,
                  border: "1px solid #e1e6f2",
                  borderRadius: 2,
                  boxShadow: "0 4px 14px rgba(50,60,120,.035)",
                }}
              >
                <DragIndicatorRoundedIcon
                  sx={{ color: "#bdc4dc", fontSize: 19 }}
                />
                <Box
                  sx={{
                    width: 34,
                    height: 34,
                    display: "grid",
                    placeItems: "center",
                    flexShrink: 0,
                    color: "#5364f4",
                    bgcolor: "#eef0ff",
                    borderRadius: "50%",
                    fontWeight: 850,
                    fontSize: 13,
                  }}
                >
                  {chapter.ordre}
                </Box>
                <Box sx={{ flex: 1, minWidth: 0 }}>
                  <Typography noWrap sx={{ fontSize: 13.5, fontWeight: 800 }}>
                    {chapter.titre}
                  </Typography>
                  <Typography color="text.secondary" sx={{ fontSize: 11.5 }}>
                    {resourceCount} ressource{resourceCount > 1 ? "s" : ""}
                    {chapter.fichierPrincipalNom
                      ? ` · ${chapter.fichierPrincipalNom}`
                      : ""}
                  </Typography>
                </Box>
                <Chip
                  size="small"
                  label={published ? "Publié" : "Brouillon"}
                  sx={{
                    display: { xs: "none", sm: "flex" },
                    height: 24,
                    color: published ? "#17864f" : "#6d6577",
                    bgcolor: published ? "#e5f7ec" : "#f0eef4",
                    fontSize: 11,
                    fontWeight: 750,
                  }}
                />
                {chapter.fichierPrincipalUrl && onDownloadFile && (
                  <IconButton
                    size="small"
                    aria-label={`Telecharger ${chapter.fichierPrincipalNom}`}
                    onClick={() => onDownloadFile(chapter)}
                    sx={{
                      color: "#5364f4",
                      border: "1px solid #dce1ef",
                      borderRadius: 1.5,
                    }}
                  >
                    <DownloadRoundedIcon sx={{ fontSize: 17 }} />
                  </IconButton>
                )}
                <IconButton
                  size="small"
                  aria-label={`Modifier ${chapter.titre}`}
                  onClick={() => onEdit(chapter)}
                  sx={{ border: "1px solid #dce1ef", borderRadius: 1.5 }}
                >
                  <EditRoundedIcon sx={{ fontSize: 17 }} />
                </IconButton>
                {onAddResource && (
                  <IconButton
                    size="small"
                    aria-label={`Ajouter une ressource à ${chapter.titre}`}
                    onClick={() => onAddResource(chapter)}
                    sx={{
                      color: "#5364f4",
                      border: "1px solid #dce1ef",
                      borderRadius: 1.5,
                    }}
                  >
                    <AttachFileRoundedIcon sx={{ fontSize: 17 }} />
                  </IconButton>
                )}
                <IconButton
                  size="small"
                  color="error"
                  aria-label={`Supprimer ${chapter.titre}`}
                  onClick={() => onDelete(chapter)}
                  sx={{ border: "1px solid #ffd7dc", borderRadius: 1.5 }}
                >
                  <DeleteOutlineRoundedIcon sx={{ fontSize: 18 }} />
                </IconButton>
              </Paper>
            );
          })}
        </Box>
      )}

    </Box>
  );
}
