-- Table des utilisateurs (Héritage Single Table pour Admin, Professeur, Elève)
CREATE TABLE utilisateur (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(50),
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMINISTRATEUR', 'PROFESSEUR', 'ELEVE'))
);

-- Table des cours
CREATE TABLE cours (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(50),
    visible_catalogue BOOLEAN DEFAULT FALSE,
    responsable_id INT NOT NULL,
    FOREIGN KEY (responsable_id) REFERENCES utilisateur(id) ON DELETE CASCADE
);

-- Table des chapitres
CREATE TABLE chapitre (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titre VARCHAR(200) NOT NULL,
    contenu TEXT,
    ordre INT,
    cours_id INT NOT NULL,
    FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE
);

-- Table des ressources
CREATE TABLE ressource (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(200) NOT NULL,
    url VARCHAR(500),
    type VARCHAR(50),
    telechargeable BOOLEAN DEFAULT TRUE,
    chapitre_id INT NOT NULL,
    FOREIGN KEY (chapitre_id) REFERENCES chapitre(id) ON DELETE CASCADE
);

-- Table des inscriptions (Lien Elève - Cours)
CREATE TABLE inscription (
    id INT PRIMARY KEY AUTO_INCREMENT,
    date_inscription DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(50),
    eleve_id INT NOT NULL,
    cours_id INT NOT NULL,
    FOREIGN KEY (eleve_id) REFERENCES utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE
);

-- Table de progression
CREATE TABLE progression (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pourcentage_completion FLOAT DEFAULT 0.0,
    date_debut DATETIME,
    date_derniere_activite DATETIME,
    eleve_id INT NOT NULL,
    cours_id INT NOT NULL,
    FOREIGN KEY (eleve_id) REFERENCES utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE
);

-- Table des messages
CREATE TABLE message (
    id INT PRIMARY KEY AUTO_INCREMENT,
    contenu TEXT NOT NULL,
    date_envoi DATETIME DEFAULT CURRENT_TIMESTAMP,
    lu BOOLEAN DEFAULT FALSE,
    expediteur_id INT NOT NULL,
    destinataire_id INT NOT NULL,
    FOREIGN KEY (expediteur_id) REFERENCES utilisateur(id) ON DELETE SET NULL,
    FOREIGN KEY (destinataire_id) REFERENCES utilisateur(id) ON DELETE SET NULL
);

-- Table des signalements
CREATE TABLE signalement (
    id INT PRIMARY KEY AUTO_INCREMENT,
    description TEXT NOT NULL,
    date_signalement DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(50),
    type_probleme VARCHAR(100),
    auteur_id INT NOT NULL,
    FOREIGN KEY (auteur_id) REFERENCES utilisateur(id) ON DELETE CASCADE
);

-- Table des tokens
CREATE TABLE token (
    id INT PRIMARY KEY AUTO_INCREMENT,
    valeur VARCHAR(255) UNIQUE NOT NULL,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    date_expiration DATETIME NOT NULL,
    utilise BOOLEAN DEFAULT FALSE,
    type_token VARCHAR(50) NOT NULL,
    utilisateur_id INT NOT NULL,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE
);

-- Table des pièces jointes
CREATE TABLE message_piece_jointe (
    id INT PRIMARY KEY AUTO_INCREMENT,
    url_fichier VARCHAR(500) NOT NULL,
    message_id INT NOT NULL,
    FOREIGN KEY (message_id) REFERENCES message(id) ON DELETE CASCADE
);