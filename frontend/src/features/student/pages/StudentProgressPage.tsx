import {
  Alert,
  Box,
  Button,
  CircularProgress,
  LinearProgress,
  Paper,
  Typography,
} from "@mui/material";
import AutoStoriesRoundedIcon from "@mui/icons-material/AutoStoriesRounded";
import CheckCircleRoundedIcon from "@mui/icons-material/CheckCircleRounded";
import FolderOpenRoundedIcon from "@mui/icons-material/FolderOpenRounded";
import PersonOutlineRoundedIcon from "@mui/icons-material/PersonOutlineRounded";
import TrendingUpRoundedIcon from "@mui/icons-material/TrendingUpRounded";
import { useNavigate } from "react-router-dom";
import { getApiErrorMessage } from "../../auth/api/apiError";
import { useAuthStore } from "../../../store/authStore";
import { useAllStudentProgress } from "../hooks/useStudentLearning";

export function StudentProgressPage() {
  const navigate = useNavigate();
  const user = useAuthStore((state) => state.user);
  const progressQuery = useAllStudentProgress();

  if (progressQuery.isPending) {
    return (
      <Box sx={{ minHeight: 440, display: "grid", placeItems: "center" }}>
        <CircularProgress aria-label="Chargement de la progression" />
      </Box>
    );
  }
  if (progressQuery.isError) {
    return <Alert severity="error">{getApiErrorMessage(progressQuery.error)}</Alert>;
  }

  const courses = progressQuery.data ?? [];
  const totalChapters = courses.reduce((sum, item) => sum + item.totalChapitres, 0);
  const completedChapters = courses.reduce(
    (sum, item) => sum + item.chapitresTermines,
    0,
  );
  const totalResources = courses.reduce(
    (sum, item) => sum + item.totalRessources,
    0,
  );
  const average = courses.length
    ? Math.round(
        courses.reduce((sum, item) => sum + item.pourcentageGlobal, 0) /
          courses.length,
      )
    : 0;

  return (
    <Box sx={{ maxWidth: 1500, mx: "auto" }}>
      <Paper
        elevation={0}
        sx={{
          p: { xs: 2.5, md: 3.2 },
          display: "grid",
          gridTemplateColumns: { xs: "1fr", md: "auto 1fr auto" },
          alignItems: "center",
          gap: 2.5,
          border: "1px solid #e0e5f3",
          borderRadius: 3.5,
        }}
      >
        <Box
          sx={{
            width: 92,
            height: 92,
            display: "grid",
            placeItems: "center",
            color: "#fff",
            borderRadius: 3,
            background: "linear-gradient(135deg,#4056f4,#7659f6)",
          }}
        >
          <TrendingUpRoundedIcon sx={{ fontSize: 50 }} />
        </Box>
        <Box>
          <Typography component="h1" sx={{ fontSize: 34, fontWeight: 900 }}>
            Ma progression
          </Typography>
          <Typography color="text.secondary">
            Suivez votre avancement et reprenez facilement vos cours.
          </Typography>
        </Box>
        <Box sx={{ position: "relative", display: "inline-flex", mx: "auto" }}>
          <CircularProgress
            variant="determinate"
            value={average}
            size={112}
            thickness={5}
          />
          <Box
            sx={{
              position: "absolute",
              inset: 0,
              display: "grid",
              placeItems: "center",
              textAlign: "center",
            }}
          >
            <Box>
              <Typography sx={{ fontSize: 26, fontWeight: 900 }}>{average}%</Typography>
              <Typography color="text.secondary" sx={{ fontSize: 10.5 }}>
                moyenne
              </Typography>
            </Box>
          </Box>
        </Box>
      </Paper>

      <Box
        sx={{
          mt: 2.2,
          display: "grid",
          gridTemplateColumns: { xs: "1fr", sm: "repeat(2,1fr)", xl: "repeat(4,1fr)" },
          gap: 1.6,
        }}
      >
        <StatCard icon={<AutoStoriesRoundedIcon />} label="Cours inscrits" value={courses.length} />
        <StatCard
          icon={<CheckCircleRoundedIcon />}
          label="Chapitres termines"
          value={`${completedChapters}/${totalChapters}`}
          color="#21a35f"
        />
        <StatCard
          icon={<FolderOpenRoundedIcon />}
          label="Ressources disponibles"
          value={totalResources}
          color="#4169e1"
        />
        <StatCard
          icon={<TrendingUpRoundedIcon />}
          label="Progression moyenne"
          value={`${average}%`}
          color="#e9a21b"
        />
      </Box>

      <Paper
        elevation={0}
        sx={{ mt: 2.2, p: 2.5, border: "1px solid #e0e5f3", borderRadius: 3.5 }}
      >
        <Box sx={{ display: "flex", justifyContent: "space-between", gap: 2 }}>
          <Box>
            <Typography sx={{ fontSize: 21, fontWeight: 900 }}>Mes cours</Typography>
            <Typography color="text.secondary" sx={{ fontSize: 13 }}>
              Bravo {user?.prenom ?? ""}, continuez a avancer a votre rythme.
            </Typography>
          </Box>
        </Box>

        {courses.length === 0 ? (
          <Alert severity="info" sx={{ mt: 2 }}>
            Aucun cours valide n'est encore associe a votre compte.
          </Alert>
        ) : (
          <Box sx={{ mt: 1.5, display: "grid" }}>
            {courses.map((course, index) => (
              <Box
                key={course.coursId}
                sx={{
                  py: 1.8,
                  display: "grid",
                  gridTemplateColumns: {
                    xs: "1fr",
                    md: "minmax(220px,1.3fr) minmax(180px,1fr) 100px 100px 130px",
                  },
                  alignItems: "center",
                  gap: 2,
                  borderTop: index === 0 ? 0 : "1px solid #edf0f7",
                }}
              >
                <Box>
                  <Typography sx={{ fontWeight: 850 }}>{course.coursTitre}</Typography>
                  <Typography color="text.secondary" sx={{ fontSize: 12 }}>
                    <PersonOutlineRoundedIcon
                      sx={{ mr: 0.4, fontSize: 15, verticalAlign: -3 }}
                    />
                    Prof. {course.profPrenom} {course.profNom}
                  </Typography>
                </Box>
                <Box>
                  <Box sx={{ display: "flex", justifyContent: "space-between" }}>
                    <Typography sx={{ fontSize: 12, fontWeight: 800 }}>
                      {course.pourcentageGlobal}%
                    </Typography>
                  </Box>
                  <LinearProgress
                    variant="determinate"
                    value={course.pourcentageGlobal}
                    sx={{ mt: 0.6, height: 8, borderRadius: 10 }}
                  />
                </Box>
                <Box>
                  <Typography sx={{ fontWeight: 850 }}>{course.totalChapitres}</Typography>
                  <Typography color="text.secondary" sx={{ fontSize: 11 }}>
                    Chapitres
                  </Typography>
                </Box>
                <Box>
                  <Typography sx={{ fontWeight: 850 }}>{course.chapitresTermines}</Typography>
                  <Typography color="text.secondary" sx={{ fontSize: 11 }}>
                    Termines
                  </Typography>
                </Box>
                <Button
                  variant="outlined"
                  onClick={() =>
                    navigate(`/dashboard/student/courses/${course.coursId}`)
                  }
                  sx={{ minHeight: 38 }}
                >
                  Voir le cours
                </Button>
              </Box>
            ))}
          </Box>
        )}
      </Paper>
    </Box>
  );
}

function StatCard({
  icon,
  label,
  value,
  color = "#6658ef",
}: {
  icon: React.ReactNode;
  label: string;
  value: React.ReactNode;
  color?: string;
}) {
  return (
    <Paper
      elevation={0}
      sx={{
        p: 2.2,
        display: "flex",
        alignItems: "center",
        gap: 1.5,
        border: "1px solid #e0e5f3",
        borderRadius: 3,
      }}
    >
      <Box
        sx={{
          width: 48,
          height: 48,
          display: "grid",
          placeItems: "center",
          color,
          bgcolor: `${color}14`,
          borderRadius: 2,
        }}
      >
        {icon}
      </Box>
      <Box>
        <Typography color="text.secondary" sx={{ fontSize: 12.5 }}>
          {label}
        </Typography>
        <Typography sx={{ fontSize: 24, fontWeight: 900 }}>{value}</Typography>
      </Box>
    </Paper>
  );
}
