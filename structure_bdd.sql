-- =============================================================================
-- NOM DU SCRIPT : schema.sql
-- DESCRIPTION : Création des tables, relations et contraintes pour LearnHub
-- VERSION : 2.0 (PostgreSQL Optimized)
-- =============================================================================

-- Extension pour la gestion des UUID si nécessaire (optionnel)
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. UTILISATEURS
-- Utilisation du rôle pour gérer les accès (Admin, Prof, Eleve)
CREATE TABLE utilisateur (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    date_creation TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(50) DEFAULT 'ACTIF',
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMINISTRATEUR', 'PROFESSEUR', 'ELEVE'))
);

-- 2. CATALOGUE DE COURS
CREATE TABLE cours (
    id SERIAL PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    date_creation TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(50) DEFAULT 'BROUILLON',
    visible_catalogue BOOLEAN DEFAULT FALSE,
    responsable_id INT NOT NULL,
    CONSTRAINT fk_cours_responsable FOREIGN KEY (responsable_id) 
        REFERENCES utilisateur(id) ON DELETE CASCADE
);

-- 3. STRUCTURE DU COURS (Chapitres)
CREATE TABLE chapitre (
    id SERIAL PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    contenu TEXT,
    ordre INT DEFAULT 0,
    cours_id INT NOT NULL,
    CONSTRAINT fk_chapitre_cours FOREIGN KEY (cours_id) 
        REFERENCES cours(id) ON DELETE CASCADE
);

-- 4. RESSOURCES PÉDAGOGIQUES
CREATE TABLE ressource (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    url VARCHAR(500) NOT NULL,
    type VARCHAR(50), -- pdf, video, link
    telechargeable BOOLEAN DEFAULT TRUE,
    chapitre_id INT NOT NULL,
    CONSTRAINT fk_ressource_chapitre FOREIGN KEY (chapitre_id) 
        REFERENCES chapitre(id) ON DELETE CASCADE
);

-- 5. SUIVI ET INSCRIPTIONS
CREATE TABLE inscription (
    id SERIAL PRIMARY KEY,
    date_inscription TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(50) DEFAULT 'EN_ATTENTE', -- EN_ATTENTE, VALIDE, REFUSE
    eleve_id INT NOT NULL,
    cours_id INT NOT NULL,
    UNIQUE(eleve_id, cours_id), -- Un élève ne s'inscrit qu'une fois par cours
    CONSTRAINT fk_inscr_eleve FOREIGN KEY (eleve_id) REFERENCES utilisateur(id) ON DELETE CASCADE,
    CONSTRAINT fk_inscr_cours FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE
);

CREATE TABLE progression (
    id SERIAL PRIMARY KEY,
    pourcentage_completion FLOAT DEFAULT 0.0 CHECK (pourcentage_completion BETWEEN 0 AND 100),
    date_debut TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    date_derniere_activite TIMESTAMP WITH TIME ZONE,
    eleve_id INT NOT NULL,
    cours_id INT NOT NULL,
    CONSTRAINT fk_prog_eleve FOREIGN KEY (eleve_id) REFERENCES utilisateur(id) ON DELETE CASCADE,
    CONSTRAINT fk_prog_cours FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE
);

-- 6. COMMUNICATION (Messages et Signalements)
CREATE TABLE message (
    id SERIAL PRIMARY KEY,
    contenu TEXT NOT NULL,
    date_envoi TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    lu BOOLEAN DEFAULT FALSE,
    expediteur_id INT NOT NULL,
    destinataire_id INT NOT NULL,
    CONSTRAINT fk_msg_exp FOREIGN KEY (expediteur_id) REFERENCES utilisateur(id) ON DELETE SET NULL,
    CONSTRAINT fk_msg_dest FOREIGN KEY (destinataire_id) REFERENCES utilisateur(id) ON DELETE SET NULL
);

CREATE TABLE message_piece_jointe (
    id SERIAL PRIMARY KEY,
    url_fichier VARCHAR(500) NOT NULL,
    message_id INT NOT NULL,
    CONSTRAINT fk_pj_message FOREIGN KEY (message_id) REFERENCES message(id) ON DELETE CASCADE
);

CREATE TABLE signalement (
    id SERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    date_signalement TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(50) DEFAULT 'OUVERT',
    type_probleme VARCHAR(100),
    auteur_id INT NOT NULL,
    CONSTRAINT fk_sig_auteur FOREIGN KEY (auteur_id) REFERENCES utilisateur(id) ON DELETE CASCADE
);

-- 7. SÉCURITÉ (Tokens JWT / Reset Password)
CREATE TABLE token (
    id SERIAL PRIMARY KEY,
    valeur VARCHAR(512) UNIQUE NOT NULL,
    date_creation TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    date_expiration TIMESTAMP WITH TIME ZONE NOT NULL,
    utilise BOOLEAN DEFAULT FALSE,
    type_token VARCHAR(50) NOT NULL, -- REFRESH, RESET_PASSWORD
    utilisateur_id INT NOT NULL,
    CONSTRAINT fk_token_user FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE
);

-- 8. INDEXATION (Pour les performances de recherche)
CREATE INDEX idx_user_email ON utilisateur(email);
CREATE INDEX idx_cours_responsable ON cours(responsable_id);
CREATE INDEX idx_inscription_eleve ON inscription(eleve_id);