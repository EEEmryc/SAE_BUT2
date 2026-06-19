# Configuration des e-mails LearnHub

LearnHub utilise `JavaMailSender` et peut envoyer les messages vers un véritable
serveur SMTP. Mailpit reste disponible uniquement comme outil de développement.

## E-mails actuellement envoyés

- invitation après création d'un utilisateur ;
- réinitialisation du mot de passe ;
- confirmation de réception d'une demande de compte ;
- refus d'une demande de compte.

Le destinataire SMTP est toujours l'adresse associée à l'action concernée. Les
échecs d'envoi sont journalisés et renvoyés à l'interface avec les indicateurs
`invitationEmailSent` ou `confirmationEmailSent`.

## Configuration Gmail

1. Activez la validation en deux étapes sur le compte Google expéditeur.
2. Créez un mot de passe d'application Google.
3. Copiez `Docker/.env.example` vers `Docker/.env`.
4. Renseignez au minimum :

```dotenv
MAIL_ENABLED=true
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your.account@gmail.com
MAIL_PASSWORD=the_16_character_app_password
MAIL_FROM=your.account@gmail.com
MAIL_FROM_NAME=LearnHub
MAIL_REPLY_TO=your.account@gmail.com
MAIL_SMTP_AUTH=true
MAIL_STARTTLS=true
MAIL_STARTTLS_REQUIRED=true
MAIL_SSL_ENABLE=false
MAIL_SMTP_TRUST=smtp.gmail.com
```

Le mot de passe principal du compte Google ne doit jamais être utilisé. Le
fichier `Docker/.env` est ignoré par Git et ne doit pas être partagé.

Références officielles :

- [Mots de passe d'application Google](https://support.google.com/accounts/answer/185833)
- [Support e-mail de Spring Boot](https://docs.spring.io/spring-boot/reference/io/email.html)

Ensuite, reconstruisez l'API :

```powershell
docker compose -f Docker/docker-compose.yml up -d --build learnhub-api
```

## Mode Mailpit

Mailpit n'est plus lancé par défaut. Pour le développement local :

1. utilisez les valeurs de `Docker/.env.mailpit.example` dans `Docker/.env` ;
2. démarrez le profil dédié :

```powershell
docker compose -f Docker/docker-compose.yml --profile dev-mail up -d --build
```

L'interface de test reste alors disponible sur `http://localhost:8025`.

## Autres fournisseurs SMTP

Les mêmes variables fonctionnent avec Brevo, Mailgun, SendGrid SMTP, Outlook ou
un serveur universitaire. Adaptez l'hôte, le port, TLS/SSL et les identifiants
aux paramètres du fournisseur. Pour une mise en production, préférez un domaine
expéditeur configuré avec SPF, DKIM et DMARC.
