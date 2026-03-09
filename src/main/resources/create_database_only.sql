-- Script de création de la base de données "elearning"
CREATE DATABASE elearning
    WITH 
    OWNER = postgres
    CONNECTION LIMIT = -1;
  -- Donner les droits 
 GRANT ALL PRIVILEGES ON DATABASE elearning TO postgres;

 -- Connecytion à la Base de données
 \c elearning;
