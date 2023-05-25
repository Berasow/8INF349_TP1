<h3>Berfan TUMAR</h3>

Le but de ce projet était de réaliser une connexion TCP entre un serveur et un client grâce par le biais de sockets.
Nous avions pour cela le choix entre plusieurs langages de programmation, j'ai donc décidé de réaliser ce projet en Java.

<h2>Première partie</h2>

Dans un premier temps, je vais détailler les étapes à réaliser pour que mon code soit compilé puis exécuté.

Il faut tout d'abord compiler le code de la classe `HTTPClient.java`.
Pour cela, il faut se placer dans le dossier du projet puis dans un terminal, taper la commande suivante : 
```bash
javac HTTPClient.java
```
Ensuite, pour exécuter le code :
```bash
java HTTPClient [hôte]
```
Ici, on remplace *[hôte]* par le DNS du site que l'on souhaite, par exemple : `www.google.com`
Le code va ensuite récupérer toutes les informations de la requête HTTP.

<h2>Deuxième partie</h2>

Le deuxième exercice consistait à implémenter un serveur HTTP afin qu'il puisse écouter sur un port et autoriser les connexions des clients.

Pour compiler la classe `HTTPServer.java` :
```bash
javac HTTPServer.java
```
Ensuite, pour exécuter le code :
```bash
java HTTPServer
```

Le serveur va ensuite écouter sur le port 80, puis on peut tester les fonctionnalités en tapant `localhost:80/index.html` dans la barre de recherche d'un navigateur web.