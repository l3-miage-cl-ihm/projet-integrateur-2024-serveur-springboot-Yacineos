## Bienvenue au Projet Integrateur 2024

 IKEO, une application web dédiée à l'optimisation des plannings de livraison en entrepôt et à la gestion efficace des tournées des livreurs.

![IKEO_LOGO](./IKEO_LOGO.png)

# Contributeurs:
##### - OURZIK    Jugurta
##### - OUKKAL    Yacine
##### - BOUQUETY  Andy
##### - MIEL      Nils
##### - FARINA    Alessandro
##### - MONACHON  Vivian

Niveau: Licence 3 MIAGE 
---

### Technologies & environements :

* Java/Springboot pour le back-end ( RO et server métier).
* PostgreSQL pour la base de données.
* Docker pour la conteneurisation de l'application.
* Angular et angularMaterial pour le front-end.
* Openrouteservice et data.gouv APIs pour la carte et le géocodage.
* Jest  et Junit pour les tests.

---

### Objectifs du projet:
* Développement d'un produit suivant des spécifications.
* Intégration des connaissances acquises au long de l'année universitaire 2023-2024.
* Maîtrise du Gitflow (features et branches, pull requests et  routines de revue de code ).
* Daily meetings et collaboration entre équipes.
* Mise en place de la méthodologie agile.
* Mise en place d'une organisation en pair programming.
* Exploration du DevOps.
* Compréhension de la philosophie de springboot et particulièrement le concept de l'inversion de contrôle à travers l'injection de dépendances.
* Maîtrise de l'architecture hexagonale.
* Maîtrise de la gestion d'erreurs.
* Compréhension plus approfondie de certaines notions vue en cours comme les observables en angular et les promesses.
* Maîtrise de l'architecture MVVM.
* Développement de test unitaires et de tests d'intégration.
* Mise en pratique des algorithmes vue en cours de la RO pour l'optimisations des itinéraires de livraison.

---

### Comment lancer le server
mettez vous à la racine du projet et sur la ligne de commande tapez la commande suivante:

```sh
  docker-compose -f docker/docker-compose-prod.yml up --build &
  ```
---

### Comment contribuer:

Ce que vous devez faire :

    Créer un projet (Fork).
    Cloner le repo forké localement.
    Ajouter quelques changements localement, pousser vers votre repo forké.
    Ensuite, lancez une pull request vers ce repo.

Pour plus de détails, voici les étapes à suivre.

    Tout d'abord, forker le projet sur github.

    Utilisez git pour cloner et installer votre projet forké localement.


    # après le clonage, naviguer vers le projet

    # pour synchroniser avec l'origine
    git remote add upstream <main_repo_url>
    git remote add origin <your_forked_repo_url>


    # pour configurer votre branche de développement locale
    git branch your_local_branch
    git checkout votre_branche_locale

    # pour ajouter vos modifications
    git add *
    git commit -m "+votre_branche_locale : votre commit ici"

    # pousser vers votre repo forké
    git push origin votre_branche_locale
