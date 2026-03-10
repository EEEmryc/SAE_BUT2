-- Fixed database creation with proper BCrypt hashes
\c elearning;

-- Drop existing tables
DROP TABLE IF EXISTS ressource CASCADE;
DROP TABLE IF EXISTS chapitre CASCADE; 
DROP TABLE IF EXISTS inscription CASCADE;
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS cours CASCADE;
DROP TABLE IF EXISTS utilisateur CASCADE;

-- Create tables
CREATE TABLE utilisateur (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'PROFESSEUR', 'ETUDIANT')),
    statut VARCHAR(50) DEFAULT 'ACTIF',
    date_creation TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    reset_token VARCHAR(255),
    reset_token_expiration TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE cours (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    date_creation TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(50) DEFAULT 'DRAFT',
    visible_catalogue BOOLEAN DEFAULT TRUE,
    prof_id BIGINT NOT NULL
);

CREATE TABLE chapitre (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    contenu TEXT,
    ordre INTEGER,
    date_creation TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    cours_id BIGINT NOT NULL
);

CREATE TABLE ressource (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    url VARCHAR(500),
    type VARCHAR(50),
    telechargeable BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    chapitre_id BIGINT NOT NULL
);

CREATE TABLE inscription (
    id BIGSERIAL PRIMARY KEY,
    date_inscription TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(50) DEFAULT 'EN_ATTENTE' CHECK (statut IN ('EN_ATTENTE', 'VALIDE', 'REFUSE')),
    eleve_id BIGINT NOT NULL,
    cours_id BIGINT NOT NULL
);

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(500) UNIQUE NOT NULL,
    email VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    revoked BOOLEAN DEFAULT FALSE
);

-- Foreign Keys
ALTER TABLE cours ADD CONSTRAINT fk_cours_prof FOREIGN KEY (prof_id) REFERENCES utilisateur(id) ON DELETE CASCADE;
ALTER TABLE chapitre ADD CONSTRAINT fk_chapitre_cours FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE;
ALTER TABLE ressource ADD CONSTRAINT fk_ressource_chapitre FOREIGN KEY (chapitre_id) REFERENCES chapitre(id) ON DELETE CASCADE;
ALTER TABLE inscription ADD CONSTRAINT fk_inscription_eleve FOREIGN KEY (eleve_id) REFERENCES utilisateur(id) ON DELETE CASCADE;
ALTER TABLE inscription ADD CONSTRAINT fk_inscription_cours FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE;
ALTER TABLE inscription ADD CONSTRAINT uk_inscription_eleve_cours UNIQUE (eleve_id, cours_id);

-- Insert users with proper BCrypt hashes using dollar-quoting
-- admin123 hash:
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, statut) VALUES 
('Admin', 'System', 'admin@learnhub.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/tn5k2S', 'ADMIN', 'ACTIF');

-- prof123 hash:
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, statut) VALUES 
('Dupont', 'Jean', 'prof@learnhub.com', '$2a$10$eImiTXuWVxfM37uY4JAOjexgQOvPGvBOBDNKN.3Tm/zMJcbwb6t/u', 'PROFESSEUR', 'ACTIF');

-- student123 hash:
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, statut) VALUES 
('Martin', 'Sophie', 'student@learnhub.com', '$2a$10$pKBZMJy.vmTx0HmrFQC8lOaNzJGz2RNHT1mfXBJjxtFaEpbCB.zku', 'ETUDIANT', 'ACTIF');

-- Sample course
INSERT INTO cours (titre, description, statut, visible_catalogue, prof_id) VALUES 
('Introduction à Java', 'Cours de base pour apprendre la programmation Java', 'DRAFT', true, 2);

-- Sample chapter
INSERT INTO chapitre (titre, contenu, ordre, cours_id) VALUES 
('Les bases de Java', 'Dans ce chapitre, nous allons découvrir les concepts fondamentaux de Java...', 1, 1);

-- Sample resource
INSERT INTO ressource (nom, url, type, telechargeable, chapitre_id) VALUES 
('Guide Java pour débutants', 'https://docs.oracle.com/javase/tutorial/', 'LINK', false, 1);