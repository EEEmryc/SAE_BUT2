# Rapport de Refactoring — LearnHub SAE BUT2

## Contexte

Ce document retrace l'ensemble du refactoring architectural réalisé sur le projet LearnHub dans le cadre de la SAE Partie 2.  
L'objectif était de corriger les conventions de nommage des packages Java, de corriger les responsabilités architecturales mal placées, et de clarifier l'organisation du frontend.

---

## 1. Backend Java — Renommage des packages

### Problème identifié

Les packages Java ne respectaient pas la convention Java standard (tout en minuscules, sans underscore ni PascalCase). Ce point est évalué dans les critères de la SAE sur la qualité du code et la lisibilité de l'architecture.

| Convention attendue | Convention utilisée (avant) |
|---|---|
| `application.auth` | `application.Auth_Service` |
| `api.controller.auth` | `api.controller.Auth_Debug_Controller` |
| `api.dto.auth` | `api.dto.Auth_DTO` |

### Fichiers impactés

**73 fichiers Java** ont été mis à jour (déclarations `package` + instructions `import`), dans `src/main/java/` et `src/test/java/`.

---

### 1.1 Couche Application — Services

| Avant | Après |
|---|---|
| `application/Admin_Service/` | `application/admin/` |
| `application/Auth_Service/` | `application/auth/` |
| `application/Chapitre_Service/` | `application/chapitre/` |
| `application/Cours_Service/` | `application/cours/` |
| `application/Inscriptions_Service/` | `application/inscription/` |
| `application/Messagerie_Service/` | `application/messagerie/` |
| `application/Progressions_Service/` | `application/progression/` |
| `application/Ressource_Service/` | `application/ressource/` |
| `application/Signalement_Service/` | `application/signalement/` |
| `application/User_Service/` | `application/user/` |

---

### 1.2 Couche API — Controllers

| Avant | Après |
|---|---|
| `api/controller/Admin/` | `api/controller/admin/` |
| `api/controller/Auth_Debug_Controller/` | `api/controller/auth/` |
| `api/controller/Chapitres/` | `api/controller/chapitre/` |
| `api/controller/Cours/` | `api/controller/cours/` |
| `api/controller/Inscriptions/` | `api/controller/inscription/` |
| `api/controller/Messagerie/` | `api/controller/messagerie/` |
| `api/controller/Progressions/` | `api/controller/progression/` |
| `api/controller/Ressources/` | `api/controller/ressource/` |
| `api/controller/Signalements/` | `api/controller/signalement/` |

---

### 1.3 Couche API — DTOs

Plusieurs packages ont été renommés, et deux packages mal organisés ont été fusionnés / éclatés.

| Avant | Après | Remarque |
|---|---|---|
| `api/dto/Auth_DTO/` | `api/dto/auth/` | Renommage |
| `api/dto/Chapitre_DTO/` | `api/dto/chapitre/` | Renommage |
| `api/dto/Cours_DTO/` | `api/dto/cours/` | Renommage |
| `api/dto/Inscriptions_DTO/` | `api/dto/inscription/` | Renommage |
| `api/dto/Messagerie_DTO/` | `api/dto/messagerie/` | Renommage |
| `api/dto/Progressions_DTO/` | `api/dto/progression/` | Renommage |
| `api/dto/Ressources_DTO/` | `api/dto/ressource/` | Renommage |
| `api/dto/Signalement_DTO/` | `api/dto/signalement/` | Renommage |
| `api/dto/User_DTO/` | `api/dto/user/` | Renommage |
| `api/dto/Register/RegisterRequest.java` | `api/dto/auth/RegisterRequest.java` | **Fusion** dans `auth` (même famille logique) |
| `api/dto/Stat_Refresh_DTO/RefreshResponse.java` | `api/dto/auth/RefreshResponse.java` | **Éclatement** vers le bon contexte métier |
| `api/dto/Stat_Refresh_DTO/StatsResponse.java` | `api/dto/admin/StatsResponse.java` | **Éclatement** (nouveau package `admin`) |
| `api/dto/Stat_Refresh_DTO/StatutRequest.java` | `api/dto/inscription/StatutRequest.java` | **Éclatement** vers le bon contexte métier |

> **Pourquoi éclater `Stat_Refresh_DTO` ?**  
> Ce package regroupait des classes sans rapport : un token de rafraîchissement (authentification), des statistiques globales (administration) et une requête de changement de statut (inscription). Chaque classe a été déplacée dans le package correspondant à son contexte métier.

---

## 2. Backend Java — Corrections architecturales

### 2.1 `CustomUserDetailsService` et `TokenBlacklistService`

**Avant :** `application/Custom_Token_Service/`  
**Après :** `infrastructure/security/`

**Pourquoi ?** Ces deux classes implémentent des interfaces Spring Security (`UserDetailsService`) et gèrent des détails techniques d'infrastructure (authentification, liste noire de tokens JWT). Elles n'ont pas de logique métier et appartiennent à la couche infrastructure, pas à la couche application.

```
Avant (incorrect)            Après (correct)
─────────────────────        ──────────────────────────
application/                 infrastructure/
  Custom_Token_Service/        security/
    CustomUserDetailsService     CustomUserDetailsService
    TokenBlacklistService        TokenBlacklistService
```

---

### 2.2 `GlobalExceptionHandler`

**Avant :** `infrastructure/config/GlobalExceptionHandler.java`  
**Après :** `api/exception/GlobalExceptionHandler.java`

**Pourquoi ?** Le `GlobalExceptionHandler` est annoté `@RestControllerAdvice` — c'est un composant de la couche API qui traduit les exceptions métier en réponses HTTP. Le placer dans `infrastructure/config` mélangeait les responsabilités.

```
Avant (incorrect)            Après (correct)
─────────────────────        ──────────────────────────
infrastructure/              api/
  config/                      exception/
    GlobalExceptionHandler         GlobalExceptionHandler
```

---

## 3. Architecture finale du backend

```
src/main/java/sae/learnhub/learnhub/
│
├── api/                          ← Couche API (HTTP)
│   ├── controller/
│   │   ├── admin/
│   │   ├── auth/
│   │   ├── chapitre/
│   │   ├── cours/
│   │   ├── inscription/
│   │   ├── messagerie/
│   │   ├── progression/
│   │   ├── ressource/
│   │   └── signalement/
│   ├── dto/
│   │   ├── admin/
│   │   ├── auth/               ← AuthResponse, LoginRequest, RegisterRequest, RefreshResponse
│   │   ├── chapitre/
│   │   ├── cours/
│   │   ├── inscription/        ← InscriptionRequest, InscriptionResponse, StatutRequest
│   │   ├── messagerie/
│   │   ├── progression/
│   │   ├── ressource/
│   │   ├── signalement/
│   │   └── user/
│   ├── exception/
│   │   └── GlobalExceptionHandler.java
│   └── mapper/
│
├── application/                  ← Couche Application (logique métier)
│   ├── admin/
│   ├── auth/
│   ├── chapitre/
│   ├── cours/
│   ├── exception/               ← Exceptions métier (AccessDeniedException, etc.)
│   ├── inscription/
│   ├── messagerie/
│   ├── port/                    ← Interfaces techniques (TokenProvider, FileStorage...)
│   ├── progression/
│   ├── ressource/
│   ├── signalement/
│   └── user/
│
├── domain/                       ← Couche Domaine (cœur métier pur)
│   ├── model/
│   └── repository/              ← Interfaces repository (IUserRepository, etc.)
│
└── infrastructure/               ← Couche Infrastructure (technique)
    ├── config/                  ← Configuration Spring (SecurityConfig, SwaggerConfig...)
    ├── filter/                  ← JwtFilter
    ├── notification/            ← SmtpAccountNotificationSender
    ├── persistence/
    │   ├── adapter/             ← Implémentations des interfaces repository
    │   ├── entity/              ← Entités JPA (@Entity)
    │   ├── mapper/              ← Mapper JPA Entity ↔ Domain Model
    │   └── repository/          ← SpringData repositories (JpaRepository)
    ├── security/                ← CustomUserDetailsService, TokenBlacklistService
    └── storage/                 ← LocalResourceFileStorage
```

---

## 4. Frontend TypeScript — Réorganisation des dossiers

### Problème identifié

Le dossier `api/` était utilisé à deux niveaux avec des significations différentes, ce qui créait une ambiguïté : s'agit-il de l'infrastructure HTTP ou des fonctions d'appel API ?

### 4.1 Dossier racine : `src/api/` → `src/http/`

**Avant :** `src/api/` (httpClient, authInterceptor, tokenManager)  
**Après :** `src/http/`

**Pourquoi ?** Ce dossier contient l'infrastructure HTTP : l'instance Axios, les intercepteurs de token, et le gestionnaire de token. Le nommer `http/` reflète exactement son rôle — ce ne sont pas des appels API, c'est la configuration du client HTTP.

| Fichier | Rôle |
|---|---|
| `http/httpClient.ts` | Instance Axios avec baseURL et timeout |
| `http/authInterceptor.ts` | Intercepteur 401 + refresh token automatique |
| `http/tokenManager.ts` | Stockage en mémoire / sessionStorage des tokens |

### 4.2 Dossiers de features : `api/` → `services/`

Dans chaque feature, le dossier `api/` contenant les fonctions d'appel REST a été renommé en `services/`.

| Avant | Après |
|---|---|
| `features/auth/api/authApi.ts` | `features/auth/services/authApi.ts` |
| `features/admin/users/api/adminUsersApi.ts` | `features/admin/users/services/adminUsersApi.ts` |
| `features/courses/api/coursesApi.ts` | `features/courses/services/coursesApi.ts` |
| `features/dashboard/api/dashboardApi.ts` | `features/dashboard/services/dashboardApi.ts` |
| `features/messaging/api/messagingApi.ts` | `features/messaging/services/messagingApi.ts` |
| `features/reports/api/reportsApi.ts` | `features/reports/services/reportsApi.ts` |
| `features/student/api/studentLearningApi.ts` | `features/student/services/studentLearningApi.ts` |

**Pourquoi `services/` ?** Les fonctions dans ces fichiers sont des *services d'accès aux données distantes* — elles encapsulent les appels HTTP et retournent des données typées. Le terme `services/` est cohérent avec le vocabulaire de la SAE (séparation UI / logique / communication réseau).

### 4.3 Structure finale du frontend

```
frontend/src/
│
├── http/                        ← Infrastructure HTTP (Axios, intercepteurs, tokens)
│   ├── httpClient.ts
│   ├── authInterceptor.ts
│   └── tokenManager.ts
│
├── app/                         ← Configuration globale (providers, env)
├── store/                       ← État global (Zustand — authStore)
├── routes/                      ← Routing (AppRouter, ProtectedRoute, RoleRoute)
├── layouts/                     ← AppLayout, navigation
├── theme/                       ← MUI theme
│
└── features/
    ├── auth/
    │   ├── services/            ← authApi.ts, apiError.ts
    │   ├── hooks/               ← useLogin, useForgotPassword, useResetPassword
    │   ├── components/          ← LoginForm, ForgotPasswordDialog
    │   ├── pages/               ← LoginPage, ResetPasswordPage
    │   └── schemas/             ← Validation Zod
    ├── courses/
    │   ├── services/            ← coursesApi.ts
    │   ├── hooks/               ← useCourses
    │   ├── components/
    │   └── pages/
    ├── admin/users/
    │   ├── services/            ← adminUsersApi.ts
    │   ├── hooks/
    │   ├── components/
    │   └── pages/
    ├── dashboard/
    ├── messaging/
    ├── reports/
    └── student/
```

---

## 5. Fichiers de tests — Alignement des répertoires

Les fichiers de test avaient leurs déclarations `package` mises à jour, mais étaient encore physiquement dans les anciens répertoires. Quatre dossiers de test ont été déplacés :

| Avant | Après |
|---|---|
| `test/.../application/Cours_Service/` | `test/.../application/cours/` |
| `test/.../application/Inscriptions_Service/` | `test/.../application/inscription/` |
| `test/.../application/Progressions_Service/` | `test/.../application/progression/` |
| `test/.../application/Ressource_Service/` | `test/.../application/ressource/` |

---

## 6. Résultat final

| Indicateur | Résultat |
|---|---|
| `mvnw clean compile` | ✅ 0 erreur |
| `mvnw test-compile` | ✅ 0 erreur |
| `tsc --noEmit` (frontend) | ✅ 0 erreur |
| Fichiers Java mis à jour | 73 fichiers |
| Fichiers TypeScript mis à jour | 69 fichiers |
| Packages backend renommés | 20 packages |
| Répertoires frontend renommés | 8 dossiers |

---

## 7. Principes respectés après refactoring

### Règle fondamentale de la SAE — Dépendances de haut en bas

```
api/ (Controllers)
  ↓ appelle
application/ (Services)
  ↓ manipule
domain/ (Entités & Interfaces Repository)
  ↑ implémente
infrastructure/ (Adapters JPA, Security, Storage)
```

La couche `domain/` ne connaît pas `infrastructure/`. C'est l'infrastructure qui implémente les interfaces définies dans le domaine (Pattern Repository).

### Pattern Repository — conforme à la SAE

```java
// Dans domain/repository/ — interface définie dans le domaine
public interface IInscriptionRepository { ... }

// Dans infrastructure/persistence/adapter/ — implémentation dans l'infra
@Repository
public class InscriptionRepositoryImpl implements IInscriptionRepository { ... }

// Dans application/inscription/ — le service dépend de l'interface, jamais de l'implémentation
@Service
@RequiredArgsConstructor
public class InscriptionService {
    private final IInscriptionRepository inscriptionRepository; // ← interface !
}
```

### Inversion de dépendance (SOLID — DIP)

Les services n'instancient jamais directement leurs dépendances (`new`). Tout est injecté via le constructeur par Spring, à travers des interfaces — permettant de tester les services sans base de données.
