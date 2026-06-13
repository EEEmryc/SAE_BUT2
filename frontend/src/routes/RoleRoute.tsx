import type { PropsWithChildren } from "react";
import { Navigate } from "react-router-dom";
import type { UserRole } from "../features/auth/api/authApi";
import { useAuthStore } from "../store/authStore";

type RoleRouteProps = PropsWithChildren<{
  allowedRoles: UserRole[];
}>;

export function RoleRoute({
  allowedRoles,
  children,
}: RoleRouteProps) {
  const role = useAuthStore((state) => state.user?.role);

  if (!role || !allowedRoles.includes(role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}
