-- Fix BCrypt password hashes using proper format
UPDATE utilisateur SET mot_de_passe = '$2a$10$N.zmdr9k7shNzBsNqEqTLOHrZ8tUUDQW1Gq3k1FeJfUxqF5wjT5X.' WHERE email = 'admin@learnhub.com';
UPDATE utilisateur SET mot_de_passe = '$2a$10$N.zmdr9k7shNzBsNqEqTLOHrZ8tUUDQW1Gq3k1FeJfUxqF5wjT5X.' WHERE email = 'prof@learnhub.com';
UPDATE utilisateur SET mot_de_passe = '$2a$10$N.zmdr9k7shNzBsNqEqTLOHrZ8tUUDQW1Gq3k1FeJfUxqF5wjT5X.' WHERE email = 'student@learnhub.com';