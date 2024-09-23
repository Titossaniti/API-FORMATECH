# API-FORMATECH

####
Application qui gère le back-end de l'application FORMATECH.

Il s'agit donc d'une API RESTFUL en Java avec le framework Spring Boot.
Maven pour gérer les dépendances.
TOMCAT pour créer un serveur web intégré.

L'API doit permettre, selon les rôles, de :

Superadmin :
CRUD superadmin et admin
CRUD des établissements
ADD & DELETE admin des établissements

Admin :
CRUD des sessions de formation et la ADD à un établissement
CRUD des modules
CRUD des formateurs
ADD des formateurs à des modules
CRUD des élèves
ADD & DELETE des élèves à des sessions

Formateur :
CRUD Note et Commentaire pour un module animé
READ ses sessions et modules
READ la liste d’élève de ses modules
READ note et commentaire des autre module des sessions qu’il anime

Elève:
READ les modules de sa/ses sessions
READ note et commentaire de ses modules