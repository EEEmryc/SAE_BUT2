import { useState } from "react";
import {
  Badge,
  Box,
  Button,
  IconButton,
  Menu,
  MenuItem,
  Typography,
} from "@mui/material";
import NotificationsNoneRoundedIcon from "@mui/icons-material/NotificationsNoneRounded";
import { useNavigate } from "react-router-dom";
import { usePendingEnrollmentRequests } from "../features/courses/hooks/useCourses";

export function EnrollmentNotifications({
  enabled,
}: {
  enabled: boolean;
}) {
  const navigate = useNavigate();
  const requests = usePendingEnrollmentRequests(enabled);
  const [anchor, setAnchor] = useState<HTMLElement | null>(null);
  const items = requests.data ?? [];

  return (
    <>
      <IconButton
        aria-label={
          enabled
            ? `${items.length} demande(s) d'inscription en attente`
            : "Notifications"
        }
        onClick={(event) => {
          if (enabled) setAnchor(event.currentTarget);
        }}
      >
        <Badge
          color="error"
          badgeContent={enabled ? items.length : 0}
          invisible={!enabled || items.length === 0}
        >
          <NotificationsNoneRoundedIcon />
        </Badge>
      </IconButton>

      <Menu
        anchorEl={anchor}
        open={Boolean(anchor)}
        onClose={() => setAnchor(null)}
        slotProps={{
          paper: {
            sx: { mt: 1, width: 360, maxWidth: "calc(100vw - 24px)" },
          },
        }}
      >
        <Box sx={{ px: 2, py: 1 }}>
          <Typography sx={{ fontWeight: 850 }}>
            Demandes d'inscription
          </Typography>
          <Typography color="text.secondary" sx={{ fontSize: 12 }}>
            {items.length} demande{items.length > 1 ? "s" : ""} en attente
          </Typography>
        </Box>

        {items.length === 0 ? (
          <MenuItem disabled>Aucune nouvelle demande</MenuItem>
        ) : (
          items.slice(0, 5).map((request) => (
            <MenuItem
              key={request.id}
              onClick={() => {
                setAnchor(null);
                navigate(
                  `/dashboard/enrollments?courseId=${request.coursId}`,
                );
              }}
              sx={{ alignItems: "flex-start", py: 1.2 }}
            >
              <Box sx={{ minWidth: 0 }}>
                <Typography noWrap sx={{ fontSize: 13, fontWeight: 800 }}>
                  {request.elevePrenom} {request.eleveNom}
                </Typography>
                <Typography
                  noWrap
                  color="text.secondary"
                  sx={{ fontSize: 11.5 }}
                >
                  Demande l'accès à {request.coursTitre}
                </Typography>
              </Box>
            </MenuItem>
          ))
        )}

        {items.length > 0 && (
          <Box sx={{ px: 1.5, py: 1 }}>
            <Button
              fullWidth
              onClick={() => {
                setAnchor(null);
                navigate("/dashboard/enrollments");
              }}
            >
              Gérer les demandes
            </Button>
          </Box>
        )}
      </Menu>
    </>
  );
}
