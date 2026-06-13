import { Navigate, Route, Routes } from "react-router-dom";
import { UsersManagementPage } from "../features/admin/users/pages/UsersManagementPage";
import { LoginPage } from "../features/auth/pages/LoginPage";
import { ResetPasswordPage } from "../features/auth/pages/ResetPasswordPage";
import { CourseDetailPage } from "../features/courses/pages/CourseDetailPage";
import { CoursesPage } from "../features/courses/pages/CoursesPage";
import { DashboardPage } from "../features/dashboard/pages/DashboardPage";
import { MessagingPage } from "../features/messaging/pages/MessagingPage";
import { ReportsPage } from "../features/reports/pages/ReportsPage";
import { FeaturePlaceholderPage } from "../features/shared/pages/FeaturePlaceholderPage";
import { AppLayout } from "../layouts/AppLayout";
import { ProtectedRoute } from "./ProtectedRoute";
import { RoleRoute } from "./RoleRoute";

const learningRoles = ["ETUDIANT", "PROFESSEUR"] as const;

export function AppRouter() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/reset-password" element={<ResetPasswordPage />} />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <AppLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<DashboardPage />} />
        <Route
          path="courses"
          element={
            <RoleRoute allowedRoles={[...learningRoles]}>
              <CoursesPage />
            </RoleRoute>
          }
        />
        <Route
          path="courses/:courseId"
          element={
            <RoleRoute allowedRoles={["PROFESSEUR"]}>
              <CourseDetailPage />
            </RoleRoute>
          }
        />
        <Route
          path="catalogue"
          element={
            <RoleRoute allowedRoles={["ETUDIANT", "ADMIN"]}>
              <FeaturePlaceholderPage
                title="Catalogue"
                description="Découvrez les cours disponibles sur LearnHub."
              />
            </RoleRoute>
          }
        />
        <Route
          path="chapters"
          element={
            <RoleRoute allowedRoles={["PROFESSEUR"]}>
              <FeaturePlaceholderPage
                title="Chapitres"
                description="Organisez les chapitres de vos cours."
              />
            </RoleRoute>
          }
        />
        <Route
          path="resources"
          element={
            <RoleRoute allowedRoles={[...learningRoles]}>
              <FeaturePlaceholderPage
                title="Ressources"
                description="Centralisez les documents et supports pédagogiques."
              />
            </RoleRoute>
          }
        />
        <Route
          path="enrollments"
          element={
            <RoleRoute allowedRoles={[...learningRoles]}>
              <FeaturePlaceholderPage
                title="Inscriptions"
                description="Suivez les inscriptions liées à vos cours."
              />
            </RoleRoute>
          }
        />
        <Route
          path="progress"
          element={
            <RoleRoute allowedRoles={[...learningRoles]}>
              <FeaturePlaceholderPage
                title="Progression"
                description="Visualisez les avancements et objectifs pédagogiques."
              />
            </RoleRoute>
          }
        />
        <Route
          path="messages"
          element={<MessagingPage />}
        />
        <Route
          path="reports"
          element={
            <RoleRoute allowedRoles={["ADMIN"]}>
              <ReportsPage />
            </RoleRoute>
          }
        />
        <Route
          path="admin/users"
          element={
            <RoleRoute allowedRoles={["ADMIN"]}>
              <UsersManagementPage />
            </RoleRoute>
          }
        />
        <Route
          path="admin/statistics"
          element={
            <RoleRoute allowedRoles={["ADMIN"]}>
              <FeaturePlaceholderPage
                title="Statistiques"
                description="Analysez l’activité globale de la plateforme."
              />
            </RoleRoute>
          }
        />
        <Route
          path="admin/settings"
          element={
            <RoleRoute allowedRoles={["ADMIN"]}>
              <FeaturePlaceholderPage
                title="Paramètres"
                description="Configurez les paramètres d’administration."
              />
            </RoleRoute>
          }
        />
      </Route>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
