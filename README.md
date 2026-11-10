# Gestion Cinéma — CineFX

> Bonjour,
>
> Je suis Safia Tirar, étudiante en 5e année IA-DATA, groupe 05.
>
> S'il vous plaît, pourriez-vous m'ajouter au groupe du Centre de carrière ?
>
> Merci d'avance.

## Présentation

CineFX est une application de bureau permettant de gérer un cinéma : films,
salles, séances, réservations, tarifs et suivi des recettes.

## Technologies

- Java 17
- JavaFX 21
- Maven
- Hibernate ORM
- MySQL
- JUnit 5

## Prérequis

- JDK 17 ou version ultérieure
- Maven 3.9 ou version ultérieure
- MySQL 8

## Installation

1. Cloner le dépôt :

   ```bash
   git clone https://github.com/SafiaTirar/Gestion_cinema.git
   cd Gestion_cinema
   ```

2. Créer une base de données MySQL nommée `CineFX_project`.

3. Adapter si nécessaire les paramètres de connexion dans
   `src/main/resources/hibernate.cfg.xml`.

4. Compiler et exécuter les tests :

   ```bash
   mvn clean test
   ```

5. Lancer l'application :

   ```bash
   mvn javafx:run
   ```

## Structure

```text
src/
├── main/
│   ├── java/          # Modèles, DAO, services et contrôleurs
│   └── resources/     # Vues FXML et configuration Hibernate
└── test/
    └── java/          # Tests unitaires
```

## Auteure

Safia Tirar — 5e année IA-DATA, groupe 05
