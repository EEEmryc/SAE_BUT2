import { useEffect, useRef, useState } from "react";
import {
  Avatar,
  Box,
  Divider,
  Drawer,
  IconButton,
  InputAdornment,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  TextField,
  Tooltip,
  Typography,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import MenuRoundedIcon from "@mui/icons-material/MenuRounded";
import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import LogoutRoundedIcon from "@mui/icons-material/LogoutRounded";
import SchoolRoundedIcon from "@mui/icons-material/SchoolRounded";
import KeyboardArrowDownRoundedIcon from "@mui/icons-material/KeyboardArrowDownRounded";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";
import { roleLabels } from "./navigation/menuConfig";
import { SidebarNavigation } from "./navigation/SidebarNavigation";
import { EnrollmentNotifications } from "./EnrollmentNotifications";
import { AdminAccountRequestNotifications } from "./AdminAccountRequestNotifications";

const expandedWidth = 272;
const collapsedWidth = 76;

export function AppLayout() {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("md"));
  const location = useLocation();
  const navigate = useNavigate();
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);
  const [desktopExpanded, setDesktopExpanded] = useState(true);
  const [mobileOpen, setMobileOpen] = useState(false);
  const mainScrollRef = useRef<HTMLElement | null>(null);

  useEffect(() => {
    if (mainScrollRef.current) {
      mainScrollRef.current.scrollTop = 0;
    }
  }, [location.pathname]);

  if (!user) {
    return null;
  }

  const initials =
    `${user.prenom?.[0] ?? ""}${user.nom?.[0] ?? ""}`.toUpperCase();
  const drawerExpanded = isMobile ? true : desktopExpanded;

  const drawerContent = (
    <Box
      sx={{
        height: "100%",
        display: "flex",
        flexDirection: "column",
        color: "#fff",
        background:
          "linear-gradient(165deg, #5364f4 0%, #554bd8 58%, #4438bd 100%)",
      }}
    >
      <Box
        sx={{
          height: 72,
          px: drawerExpanded ? 2.1 : 1,
          display: "flex",
          alignItems: "center",
          justifyContent: drawerExpanded ? "space-between" : "center",
        }}
      >
        {drawerExpanded && (
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <SchoolRoundedIcon sx={{ fontSize: 30 }} />
            <Typography sx={{ fontWeight: 800, fontSize: 20 }}>
              LearnHub
            </Typography>
          </Box>
        )}
        <IconButton
          aria-label={drawerExpanded ? "Fermer le menu" : "Ouvrir le menu"}
          onClick={() => {
            if (isMobile) {
              setMobileOpen(false);
            } else {
              setDesktopExpanded((open) => !open);
            }
          }}
          sx={{
            color: "#fff",
            borderRadius: 2,
            backgroundColor: "rgba(255,255,255,0.1)",
            "&:hover": { backgroundColor: "rgba(255,255,255,0.18)" },
          }}
        >
          <MenuRoundedIcon />
        </IconButton>
      </Box>

      <SidebarNavigation
        role={user.role}
        expanded={drawerExpanded}
        onRequestExpand={() => setDesktopExpanded(true)}
        onNavigate={() => {
          if (isMobile) {
            setMobileOpen(false);
          }
        }}
      />

      <Box sx={{ mt: "auto", px: drawerExpanded ? 1.5 : 1, pb: 2 }}>
        <Divider sx={{ mb: 1.25, borderColor: "rgba(255,255,255,0.15)" }} />
        <Tooltip
          title={drawerExpanded ? "" : "Déconnexion"}
          placement="right"
          arrow
        >
          <List disablePadding>
            <ListItemButton
              aria-label="Déconnexion"
              onClick={async () => {
                await logout();
                navigate("/login", { replace: true });
              }}
              sx={{
                minHeight: 48,
                px: drawerExpanded ? 1.5 : 1.25,
                justifyContent: drawerExpanded ? "initial" : "center",
                borderRadius: 2.5,
                color: "rgba(255,255,255,0.9)",
                "&:hover": { backgroundColor: "rgba(255,255,255,0.12)" },
              }}
            >
              <ListItemIcon
                sx={{
                  minWidth: 0,
                  mr: drawerExpanded ? 1.5 : 0,
                  justifyContent: "center",
                  color: "inherit",
                }}
              >
                <LogoutRoundedIcon fontSize="small" />
              </ListItemIcon>
              {drawerExpanded && (
                <ListItemText
                  primary="Déconnexion"
                  slotProps={{
                    primary: {
                      sx: { fontSize: 14, fontWeight: 600 },
                    },
                  }}
                />
              )}
            </ListItemButton>
          </List>
        </Tooltip>
      </Box>
    </Box>
  );

  return (
    <Box
      sx={{
        height: "100dvh",
        display: "flex",
        overflow: "hidden",
        bgcolor: "#f7f8fe",
        backgroundImage:
          "radial-gradient(circle at 85% 10%, rgba(126, 117, 255, 0.08), transparent 30%)",
      }}
    >
      {isMobile ? (
        <Drawer
          open={mobileOpen}
          onClose={() => setMobileOpen(false)}
          ModalProps={{ keepMounted: true }}
          slotProps={{
            paper: {
              sx: {
                width: expandedWidth,
                border: 0,
                borderRadius: "0 22px 22px 0",
                overflow: "hidden",
              },
            },
          }}
        >
          {drawerContent}
        </Drawer>
      ) : (
        <Drawer
          variant="permanent"
          open
          slotProps={{
            paper: {
              sx: {
                position: "relative",
                width: desktopExpanded ? expandedWidth : collapsedWidth,
                height: "100dvh",
                flexShrink: 0,
                border: 0,
                overflowX: "hidden",
                transition: theme.transitions.create("width", {
                  easing: theme.transitions.easing.sharp,
                  duration: theme.transitions.duration.standard,
                }),
                boxShadow: "8px 0 34px rgba(60, 58, 148, 0.12)",
              },
            },
          }}
          sx={{
            width: desktopExpanded ? expandedWidth : collapsedWidth,
            height: "100dvh",
            flexShrink: 0,
            transition: theme.transitions.create("width", {
              easing: theme.transitions.easing.sharp,
              duration: theme.transitions.duration.standard,
            }),
          }}
        >
          {drawerContent}
        </Drawer>
      )}

      <Box
        sx={{
          minWidth: 0,
          height: "100dvh",
          flex: 1,
          display: "flex",
          flexDirection: "column",
          overflow: "hidden",
        }}
      >
        <Box
          component="header"
          sx={{
            height: 72,
            px: { xs: 2, sm: 3 },
            display: "flex",
            alignItems: "center",
            gap: 2,
            bgcolor: "rgba(255,255,255,0.88)",
            borderBottom: "1px solid #e9ebf6",
            backdropFilter: "blur(16px)",
            position: "sticky",
            top: 0,
            zIndex: 10,
            flexShrink: 0,
          }}
        >
          {isMobile && (
            <IconButton
              aria-label="Ouvrir le menu"
              onClick={() => setMobileOpen(true)}
              sx={{
                color: "primary.main",
                bgcolor: "rgba(79,95,247,0.08)",
              }}
            >
              <MenuRoundedIcon />
            </IconButton>
          )}

          <TextField
            size="small"
            placeholder="Rechercher..."
            aria-label="Rechercher"
            slotProps={{
              input: {
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchRoundedIcon fontSize="small" />
                  </InputAdornment>
                ),
              },
            }}
            sx={{
              ml: "auto",
              maxWidth: 300,
              display: { xs: "none", sm: "block" },
              "& .MuiOutlinedInput-root": {
                bgcolor: "#fafbff",
              },
            }}
          />

          {user.role === "ADMIN" ? (
            <AdminAccountRequestNotifications />
          ) : (
            <EnrollmentNotifications
              enabled={user.role === "PROFESSEUR"}
              professorEmail={user.email}
            />
          )}

          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              gap: 1.15,
              minWidth: 0,
            }}
          >
            <Avatar
              sx={{
                width: 38,
                height: 38,
                fontSize: 14,
                fontWeight: 800,
                color: "#fff",
                background:
                  "linear-gradient(135deg, #5263f4, #8a58f8)",
              }}
            >
              {initials}
            </Avatar>
            <Box sx={{ display: { xs: "none", sm: "block" }, minWidth: 0 }}>
              <Typography
                noWrap
                sx={{ fontSize: 13, fontWeight: 750, lineHeight: 1.25 }}
              >
                {user.prenom} {user.nom}
              </Typography>
              <Typography
                color="text.secondary"
                sx={{ fontSize: 11, lineHeight: 1.25 }}
              >
                {roleLabels[user.role]}
              </Typography>
            </Box>
            <KeyboardArrowDownRoundedIcon
              sx={{
                display: { xs: "none", sm: "block" },
                color: "text.secondary",
                fontSize: 19,
              }}
            />
          </Box>
        </Box>

        <Box
          component="main"
          ref={mainScrollRef}
          data-testid="app-main-scroll"
          sx={{
            minHeight: 0,
            flex: 1,
            overflowY: "auto",
            overscrollBehavior: "contain",
            p: { xs: 2, sm: 3, lg: 4 },
          }}
        >
          <Outlet />
        </Box>
      </Box>
    </Box>
  );
}
