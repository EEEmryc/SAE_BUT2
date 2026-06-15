import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Button,
  Chip,
  LinearProgress,
  Paper,
  Typography,
} from "@mui/material";
import CheckCircleRoundedIcon from "@mui/icons-material/CheckCircleRounded";
import DownloadRoundedIcon from "@mui/icons-material/DownloadRounded";
import ExpandMoreRoundedIcon from "@mui/icons-material/ExpandMoreRounded";
import InsertDriveFileRoundedIcon from "@mui/icons-material/InsertDriveFileRounded";
import PlayCircleOutlineRoundedIcon from "@mui/icons-material/PlayCircleOutlineRounded";
import type { Chapter, CourseResource } from "../../courses/services/coursesApi";
import type { ChapterProgress } from "../services/studentLearningApi";

type StudentChapterListProps = {
  chapters: Chapter[];
  resources: CourseResource[];
  progress: ChapterProgress[];
  completingId: number | null;
  onOpen: (chapterId: number) => void;
  onComplete: (chapterId: number) => void;
  onDownload: (url: string, fileName: string) => void;
};

export function StudentChapterList({
  chapters,
  resources,
  progress,
  completingId,
  onOpen,
  onComplete,
  onDownload,
}: StudentChapterListProps) {
  if (chapters.length === 0) {
    return <Paper sx={{ p: 4, textAlign: "center" }}>Aucun chapitre disponible.</Paper>;
  }

  const progressByChapter = new Map(
    progress
      .filter((item) => item.chapitreId != null)
      .map((item) => [item.chapitreId, item]),
  );

  return (
    <Box sx={{ display: "grid", gap: 1.2 }}>
      {chapters.map((chapter) => {
        const chapterProgress = progressByChapter.get(chapter.id);
        const completed = chapterProgress?.statut === "TERMINE";
        const chapterResources = resources.filter(
          (resource) => resource.chapitreId === chapter.id,
        );

        return (
          <Accordion
            key={chapter.id}
            disableGutters
            elevation={0}
            onChange={(_, expanded) => {
              if (expanded && !chapterProgress) onOpen(chapter.id);
            }}
            sx={{
              border: "1px solid #e0e5f3",
              borderRadius: "14px !important",
              overflow: "hidden",
              "&:before": { display: "none" },
            }}
          >
            <AccordionSummary expandIcon={<ExpandMoreRoundedIcon />}>
              <Box sx={{ width: "100%", display: "flex", alignItems: "center", gap: 1.4 }}>
                <Box
                  sx={{
                    width: 36,
                    height: 36,
                    display: "grid",
                    placeItems: "center",
                    flexShrink: 0,
                    borderRadius: "50%",
                    color: completed ? "#168b5b" : "#5364f4",
                    bgcolor: completed ? "#e6f8ef" : "#eef0ff",
                    fontWeight: 850,
                  }}
                >
                  {completed ? <CheckCircleRoundedIcon /> : chapter.ordre}
                </Box>
                <Box sx={{ flex: 1, minWidth: 0 }}>
                  <Typography sx={{ fontWeight: 850 }}>{chapter.titre}</Typography>
                  <Typography color="text.secondary" sx={{ fontSize: 12 }}>
                    {chapterResources.length} ressource
                    {chapterResources.length > 1 ? "s" : ""}
                  </Typography>
                </Box>
                <Chip
                  size="small"
                  label={completed ? "Termine" : chapterProgress ? "En cours" : "A commencer"}
                  sx={{
                    mr: 1,
                    color: completed ? "#168b5b" : "#5364f4",
                    bgcolor: completed ? "#e6f8ef" : "#eef0ff",
                    fontWeight: 750,
                  }}
                />
              </Box>
            </AccordionSummary>
            <AccordionDetails sx={{ pt: 0, px: 2.5, pb: 2.5 }}>
              <Typography color="text.secondary" sx={{ whiteSpace: "pre-wrap" }}>
                {chapter.contenu}
              </Typography>

              {chapter.fichierPrincipalUrl && chapter.fichierPrincipalNom && (
                <Button
                  variant="outlined"
                  startIcon={<DownloadRoundedIcon />}
                  onClick={() =>
                    onDownload(
                      chapter.fichierPrincipalUrl!,
                      chapter.fichierPrincipalNom!,
                    )
                  }
                  sx={{ mt: 2, minHeight: 38 }}
                >
                  {chapter.fichierPrincipalNom}
                </Button>
              )}

              {chapterResources.length > 0 && (
                <Box sx={{ mt: 2, display: "grid", gap: 0.8 }}>
                  {chapterResources.map((resource) => (
                    <Box
                      key={resource.id}
                      sx={{
                        p: 1.2,
                        display: "flex",
                        alignItems: "center",
                        gap: 1,
                        bgcolor: "#f8f9ff",
                        borderRadius: 2,
                      }}
                    >
                      <InsertDriveFileRoundedIcon sx={{ color: "#5364f4" }} />
                      <Box sx={{ flex: 1, minWidth: 0 }}>
                        <Typography noWrap sx={{ fontSize: 13.5, fontWeight: 800 }}>
                          {resource.nom}
                        </Typography>
                        <Typography color="text.secondary" sx={{ fontSize: 11.5 }}>
                          {resource.type}
                        </Typography>
                      </Box>
                      {resource.telechargeable && (
                        <Button
                          size="small"
                          startIcon={<DownloadRoundedIcon />}
                          onClick={() => onDownload(resource.url, resource.nom)}
                          sx={{ minHeight: 34 }}
                        >
                          Telecharger
                        </Button>
                      )}
                    </Box>
                  ))}
                </Box>
              )}

              <Box sx={{ mt: 2.2 }}>
                <LinearProgress
                  variant="determinate"
                  value={completed ? 100 : chapterProgress?.pourcentage ?? 0}
                  sx={{ height: 7, borderRadius: 10 }}
                />
                {!completed && (
                  <Button
                    variant="contained"
                    startIcon={<PlayCircleOutlineRoundedIcon />}
                    disabled={completingId === chapter.id}
                    onClick={() => onComplete(chapter.id)}
                    sx={{
                      mt: 1.5,
                      minHeight: 40,
                      color: "#fff",
                      background: "linear-gradient(110deg,#4056f4,#7458f6)",
                    }}
                  >
                    {completingId === chapter.id
                      ? "Validation..."
                      : "Marquer comme termine"}
                  </Button>
                )}
              </Box>
            </AccordionDetails>
          </Accordion>
        );
      })}
    </Box>
  );
}
