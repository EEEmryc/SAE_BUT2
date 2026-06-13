# Recommandation Front-End

## Choix final

Je recommande une **SPA responsive React + TypeScript**, construite avec :

- **Vite** pour le développement et le build ;
- **Material UI** pour éviter la majorité du HTML/CSS manuel ;
- **React Router** pour la navigation ;
- **TanStack Query** pour les appels API, le cache et les états de chargement ;
- **Axios** pour l’authentification et les interceptors ;
- **React Hook Form + Zod** pour les formulaires ;
- **Vitest + React Testing Library** pour les tests ;
- **Playwright** pour quelques parcours fonctionnels complets.

Cette solution respecte le client léger demandé dans la SAE : application web
responsive consommant une API REST, sans client desktop ni application mobile
native.

## Comparaison

| Solution | Avantages | Inconvénients | Avis |
| --- | --- | --- | --- |
| HTML/CSS/JS | Très léger, aucune abstraction | Devient rapidement difficile à maintenir | Déconseillé |
| Vue 3 + PrimeVue | Simple, productif, composants très complets | Écosystème IA légèrement moins riche | Excellente alternative |
| React + Material UI | Écosystème mature, TypeScript, IA, composants, tests | Demande quelques conventions d’équipe | **Recommandé** |
| Angular | Architecture très structurée | Plus lourd et plus long à apprendre | Surdimensionné |
| Next.js | Routage et rendu serveur puissants | SSR inutile avec votre backend Spring séparé | Complexité inutile |
| React + Tailwind/shadcn | Très personnalisable, compatible avec v0 | Davantage de CSS et d’assemblage manuel | Alternative design |

## Architecture

Adoptez une organisation **par fonctionnalité**, comparable aux modules métier
du backend :

```text
frontend/
├── src/
│   ├── app/                 # Router, providers, configuration
│   ├── api/
│   │   ├── generated/       # Client généré depuis OpenAPI
│   │   ├── httpClient.ts
│   │   └── authInterceptor.ts
│   ├── features/
│   │   ├── auth/
│   │   ├── courses/
│   │   ├── chapters/
│   │   ├── enrollments/
│   │   ├── progress/
│   │   ├── messaging/
│   │   └── admin/
│   ├── components/         # Composants réellement partagés
│   ├── layouts/            # StudentLayout, TeacherLayout, AdminLayout
│   ├── routes/             # Routes publiques et protégées
│   ├── theme/              # Couleurs, typographie, thème MUI
│   └── tests/
├── .env
└── Dockerfile
```

Évitez une séparation globale `pages/services/components` trop rigide. Chaque
fonctionnalité devrait contenir ses pages, composants, hooks et tests.

## Intégration API

Votre Swagger expose normalement le contrat sur :

```text
http://localhost:8081/v3/api-docs
```

Utilisez **OpenAPI Generator** avec `typescript-axios` pour générer les types et
méthodes HTTP. Cela évite de recopier manuellement les DTO Java.

Configuration recommandée :

```env
VITE_API_URL=http://localhost:8081
```

L’instance Axios devra :

- ajouter `Authorization: Bearer <token>` ;
- intercepter les réponses `401` ;
- appeler `/api/auth/refresh` avec `X-Refresh-Token` ;
- rejouer une seule fois la requête initiale ;
- normaliser les erreurs du `GlobalExceptionHandler`.

Les protections de routes React améliorent l’interface, mais **Spring Security
doit rester l’autorité réelle** pour les permissions.

## JWT

Actuellement, les deux jetons sont accessibles au JavaScript et
`allowCredentials(false)` interdit l’authentification par cookie.

Pour la première version :

- access token conservé en mémoire ;
- refresh token dans `sessionStorage` ;
- renouvellement au rechargement de l’application ;
- suppression complète lors de la déconnexion.

Pour une version plus sécurisée, faites évoluer le backend afin de placer le
refresh token dans un cookie `HttpOnly`, `Secure` et `SameSite`.

## Accélération UI

### Figma

Utilisez Figma pour les wireframes, les parcours, le responsive et les
composants réutilisables. Définissez d’abord boutons, champs, cartes, tableaux,
dialogues et navigation. N’utilisez pas le code Figma généré comme architecture
finale : il doit servir de référence visuelle.

### Material UI

C’est le principal accélérateur recommandé : formulaires, tableaux, menus,
cartes, modales, notifications, skeletons et responsive sont déjà disponibles.
Un thème MUI central évitera la duplication de CSS.

### IA

- v0 pour produire des idées de dashboards et des prototypes React ;
- GitHub Copilot ou Codex pour les composants, hooks API et tests ;
- Figma AI pour explorer rapidement plusieurs interfaces.

Le code généré doit ensuite être adapté à votre structure, à Material UI et au
contrat OpenAPI. Pour la soutenance, vous devez pouvoir expliquer chaque
élément conservé.

## Tests Front-End

Conservez une stratégie courte mais crédible :

- tests unitaires des hooks d’authentification et de permissions ;
- tests des formulaires importants ;
- tests des états chargement, erreur et liste vide ;
- trois scénarios Playwright : connexion étudiant, création de cours
  professeur, administration utilisateur.

## Déploiement

Construisez le front en fichiers statiques et servez-le avec **Nginx dans
Docker**. En production, faites rediriger `/api` vers Spring Boot. Cela
simplifie CORS et permet de lancer toute la démonstration avec Docker Compose.

## Verdict

La meilleure solution pour votre SAE est :

> **React + TypeScript + Vite + Material UI + TanStack Query + Axios + OpenAPI Generator**

Elle est suffisamment professionnelle pour la soutenance, rapide à développer,
responsive, testable et cohérente avec votre architecture backend. Vue 3 +
PrimeVue reste le meilleur second choix si votre équipe maîtrise déjà Vue.

## Sources

- [React](https://react.dev/learn)
- [Vite](https://vite.dev/guide/)
- [Material UI](https://mui.com/material-ui/getting-started/)
- [TanStack Query](https://tanstack.com/query/latest/docs/framework/react/overview)
- [OpenAPI Generator TypeScript Axios](https://openapi-generator.tech/docs/generators/typescript-axios/)
- [Figma Dev Mode](https://help.figma.com/hc/en-us/articles/15023124644247-Guide-to-Dev-Mode)
- [Playwright](https://playwright.dev/docs/intro)
