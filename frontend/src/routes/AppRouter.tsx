import { Navigate, Route, Routes } from "react-router-dom";
import { UsersManagementPage } from "../features/admin/users/pages/UsersManagementPage";
import { LoginPage } from "../features/auth/pages/LoginPage";
import { ResetPasswordPage } from "../features/auth/pages/ResetPasswordPage";
import { ChaptersManagementPage } from "../features/courses/pages/ChaptersManagementPage";
import { CourseDetailPage } from "../features/courses/pages/CourseDetailPage";
import { CoursesPage } from "../features/courses/pages/CoursesPage";
import { EnrollmentsManagementPage } from "../features/courses/pages/EnrollmentsManagementPage";
import { ResourcesManagementPage } from "../features/courses/pages/ResourcesManagementPage";
import { DashboardPage } from "../features/dashboard/pages/DashboardPage";
import { MessagingPage } from "../features/messaging/pages/MessagingPage";
import { MyReportsPage } from "../features/reports/pages/MyReportsPage";
import { ReportIssuePage } from "../features/reports/pages/ReportIssuePage";
import { ReportsPage } from "../features/reports/pages/ReportsPage";
import { FeaturePlaceholderPage } from "../features/shared/pages/FeaturePlaceholderPage";
import { ProgressionPage } from "../features/progression/pages/ProgressionPage";
import { AppLayout } from "../layouts/AppLayout";
import { ProtectedRoute } from "./ProtectedRoute";
import { RoleRoute } from "./RoleRoute";
import { SettingsPage } from "../features/settings/pages/SettingsPage";
import { AccountRequestPage } from "../features/accountRequests/pages/AccountRequestPage";
import { AdminAccountRequestsPage } from "../features/accountRequests/pages/AdminAccountRequestsPage";

const learningRoles = ["ETUDIANT", "PROFESSEUR"] as const;

export function AppRouter() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<AccountRequestPage />} />
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
              <ChaptersManagementPage />
            </RoleRoute>
          }
        />

        <Route
          path="resources"
          element={
            <RoleRoute allowedRoles={["PROFESSEUR"]}>
              <ResourcesManagementPage />
            </RoleRoute>
          }
        />
        <Route
          path="enrollments"
          element={
            <RoleRoute allowedRoles={["PROFESSEUR"]}>
              <EnrollmentsManagementPage />
            </RoleRoute>
          }
        />
        <Route
          path="progress"
          element={
            <RoleRoute allowedRoles={[...learningRoles]}>
              <ProgressionPage />
            </RoleRoute>
          }
        />
        <Route path="messages" element={<MessagingPage />} />
        <Route
          path="report-issue"
          element={
            <RoleRoute allowedRoles={[...learningRoles]}>
              <ReportIssuePage />
            </RoleRoute>
          }
        />
        <Route
          path="my-reports"
          element={
            <RoleRoute allowedRoles={[...learningRoles]}>
              <MyReportsPage />
            </RoleRoute>
          }
        />
        <Route
          path="reports"
          element={
            <RoleRoute allowedRoles={["ADMIN"]}>
              <ReportsPage />
            </RoleRoute>
          }
        />
        <Route path="settings" element={<SettingsPage />} />
        <Route
          path="admin/account-requests"
          element={
            <RoleRoute allowedRoles={["ADMIN"]}>
              <AdminAccountRequestsPage />
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
      </Route>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}