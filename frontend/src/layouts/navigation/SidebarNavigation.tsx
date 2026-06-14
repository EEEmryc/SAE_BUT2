import { Fragment, useMemo, useState } from "react";
import {
  Box,
  Badge,
  Collapse,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Tooltip,
} from "@mui/material";
import ExpandLessRoundedIcon from "@mui/icons-material/ExpandLessRounded";
import ExpandMoreRoundedIcon from "@mui/icons-material/ExpandMoreRounded";
import { useLocation, useNavigate } from "react-router-dom";
import type { UserRole } from "../../features/auth/api/authApi";
import { useUnreadCount } from "../../features/messaging/hooks/useMessaging";
import { useNewReportsCount } from "../../features/reports/hooks/useReports";
import {
  getNavigationForRole,
  type NavigationItem,
} from "./menuConfig";

type SidebarNavigationProps = {
  role: UserRole;
  expanded: boolean;
  onNavigate?: () => void;
  onRequestExpand: () => void;
};

export function SidebarNavigation({
  role,
  expanded,
  onNavigate,
  onRequestExpand,
}: SidebarNavigationProps) {
  const location = useLocation();
  const navigate = useNavigate();
  const items = useMemo(() => getNavigationForRole(role), [role]);
  const unreadCount = useUnreadCount();
  const newReportsCount = useNewReportsCount();
  const [openMenus, setOpenMenus] = useState<Record<string, boolean>>({});

  const isItemActive = (item: NavigationItem) => {
    if (item.path === "/dashboard") {
      return location.pathname === item.path;
    }

    return Boolean(
      item.path && location.pathname.startsWith(item.path),
    );
  };

  const isGroupActive = (item: NavigationItem) =>
    Boolean(item.children?.some(isItemActive));

  const handleItem = (item: NavigationItem) => {
    if (item.children?.length) {
      if (!expanded) {
        onRequestExpand();
      }
      setOpenMenus((current) => ({
        ...current,
        [item.id]: !current[item.id],
      }));
      return;
    }

    if (item.path) {
      navigate(item.path);
      onNavigate?.();
    }
  };

  return (
    <List sx={{ px: expanded ? 1.5 : 1, py: 1 }}>
      {items.map((item) => {
        const groupOpen = openMenus[item.id] ?? isGroupActive(item);
        const active = isItemActive(item) || isGroupActive(item);

        return (
          <Fragment key={item.id}>
            <Tooltip
              title={expanded ? "" : item.label}
              placement="right"
              arrow
            >
              <ListItemButton
                selected={active}
                onClick={() => handleItem(item)}
                aria-label={item.label}
                aria-expanded={
                  item.children?.length ? groupOpen : undefined
                }
                sx={{
                  minHeight: 48,
                  mb: 0.5,
                  px: expanded ? 1.5 : 1.25,
                  justifyContent: expanded ? "initial" : "center",
                  borderRadius: 2.5,
                  color: "rgba(255,255,255,0.88)",
                  "&.Mui-selected": {
                    color: "#fff",
                    backgroundColor: "rgba(255,255,255,0.16)",
                  },
                  "&.Mui-selected:hover, &:hover": {
                    backgroundColor: "rgba(255,255,255,0.13)",
                  },
                }}
              >
                <ListItemIcon
                  sx={{
                    minWidth: 0,
                    mr: expanded ? 1.5 : 0,
                    justifyContent: "center",
                    color: "inherit",
                    "& svg": { fontSize: 20 },
                  }}
                >
                  {item.id === "messaging" ? (
                    <Badge
                      color="error"
                      badgeContent={unreadCount.data ?? 0}
                      max={99}
                      invisible={!unreadCount.data}
                    >
                      {item.icon}
                    </Badge>
                  ) : item.id === "reports" ? (
                    <Badge
                      color="error"
                      badgeContent={newReportsCount.data ?? 0}
                      max={99}
                      invisible={!newReportsCount.data}
                    >
                      {item.icon}
                    </Badge>
                  ) : (
                    item.icon
                  )}
                </ListItemIcon>
                {expanded && (
                  <>
                    <ListItemText
                      primary={item.label}
                      slotProps={{
                        primary: {
                          sx: {
                            fontSize: 14,
                            fontWeight: active ? 700 : 500,
                          },
                        },
                      }}
                    />
                    {item.children?.length ? (
                      groupOpen ? (
                        <ExpandLessRoundedIcon fontSize="small" />
                      ) : (
                        <ExpandMoreRoundedIcon fontSize="small" />
                      )
                    ) : null}
                  </>
                )}
              </ListItemButton>
            </Tooltip>

            {expanded && item.children?.length ? (
              <Collapse in={groupOpen} timeout="auto" unmountOnExit>
                <List disablePadding sx={{ mb: 0.75 }}>
                  {item.children.map((child) => {
                    const childActive = isItemActive(child);
                    return (
                      <ListItemButton
                        key={child.id}
                        selected={childActive}
                        onClick={() => handleItem(child)}
                        sx={{
                          minHeight: 40,
                          pl: 5.8,
                          pr: 1.5,
                          borderRadius: 2,
                          color: "rgba(255,255,255,0.76)",
                          "&::before": {
                            content: '""',
                            width: 5,
                            height: 5,
                            mr: 1.4,
                            borderRadius: "50%",
                            backgroundColor: childActive
                              ? "#fff"
                              : "rgba(255,255,255,0.42)",
                          },
                          "&.Mui-selected": {
                            color: "#fff",
                            backgroundColor: "rgba(25, 24, 125, 0.2)",
                          },
                          "&.Mui-selected:hover, &:hover": {
                            backgroundColor: "rgba(255,255,255,0.1)",
                          },
                        }}
                      >
                        <ListItemText
                          primary={child.label}
                          slotProps={{
                            primary: {
                              sx: {
                                fontSize: 13,
                                fontWeight: childActive ? 700 : 500,
                              },
                            },
                          }}
                        />
                      </ListItemButton>
                    );
                  })}
                </List>
              </Collapse>
            ) : null}
          </Fragment>
        );
      })}
      <Box sx={{ height: 8 }} />
    </List>
  );
}