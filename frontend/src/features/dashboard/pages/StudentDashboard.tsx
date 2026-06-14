import { Box } from "@mui/material";
import AutoStoriesRoundedIcon from "@mui/icons-material/AutoStoriesRounded";
import CheckCircleRoundedIcon from "@mui/icons-material/CheckCircleRounded";
import FolderOpenRoundedIcon from "@mui/icons-material/FolderOpenRounded";
import ForumRoundedIcon from "@mui/icons-material/ForumRounded";
import TrendingUpRoundedIcon from "@mui/icons-material/TrendingUpRounded";
import { DashboardChartSection } from "../components/DashboardChartSection";
import { DashboardError, DashboardLoading } from "../components/DashboardState";
import { DashboardHero } from "../components/DashboardHero";
import { DashboardProgressRing } from "../components/DashboardProgressRing";
import { DashboardRecentActivity } from "../components/DashboardRecentActivity";
import { DashboardStatsCards } from "../components/DashboardStatsCards";
import { useStudentDashboard } from "../hooks/useDashboard";

export function StudentDashboard({ firstName }: { firstName: string }) {
  const dashboard = useStudentDashboard();

  if (dashboard.isPending) return <DashboardLoading />;
  if (dashboard.isError) {
    return (
      <DashboardError
        error={dashboard.error}
        onRetry={() => void dashboard.refetch()}
      />
    );
  }

  const data = dashboard.data;

  return (
    <Box>
      <DashboardHero
        firstName={firstName}
        description="Retrouvez vos cours, votre progression et vos dernières activités."
      />
      <DashboardStatsCards
        items={[
          {
            id: "courses",
            label: "Cours suivis",
            value: data.courses,
            caption: "inscriptions validées",
            icon: <AutoStoriesRoundedIcon />,
          },
          {
            id: "progress",
            label: "Progression globale",
            value: `${data.globalProgress}%`,
            caption: "tous cours confondus",
            icon: <TrendingUpRoundedIcon />,
            color: "#20a66a",
          },
          {
            id: "chapters",
            label: "Chapitres terminés",
            value: data.completedChapters,
            caption: `sur ${data.totalChapters} chapitres`,
            icon: <CheckCircleRoundedIcon />,
            color: "#4775e8",
          },
          {
            id: "messages",
            label: "Messages",
            value: data.messages,
            caption: `${data.unreadMessages} non lus`,
            icon: <ForumRoundedIcon />,
            color: "#7a56e8",
          },
          {
            id: "resources",
            label: "Ressources disponibles",
            value: data.availableResources,
            caption: "dans vos cours",
            icon: <FolderOpenRoundedIcon />,
            color: "#ec8b35",
          },
        ]}
      />

      <Box
        sx={{
          mt: 2,
          display: "grid",
          gridTemplateColumns: { xs: "1fr", lg: "1.35fr 1fr" },
          gap: 2,
        }}
      >
        <DashboardChartSection
          title="Progression par cours"
          items={data.courseMetrics}
          emptyMessage="Aucun cours valide n'est encore associé à votre compte."
        />
        <DashboardRecentActivity items={data.recentActivity} />
      </Box>

      <Box
        sx={{
          mt: 2,
          display: "grid",
          gridTemplateColumns: { xs: "1fr", lg: "1fr 420px" },
          gap: 2,
        }}
      >
        <DashboardChartSection
          title="Cours en cours"
          items={data.courseMetrics.slice(0, 4)}
          emptyMessage="Votre liste de cours apparaîtra ici."
        />
        <DashboardProgressRing
          value={data.globalProgress}
          title="Progression globale"
          description={`${data.completedChapters} chapitres terminés sur ${data.totalChapters}.`}
          actionPath="/dashboard/progress"
        />
      </Box>
    </Box>
  );
}
