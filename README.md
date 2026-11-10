# CineFX — Cinema Management System

**Cinema operations delivered through a modern JavaFX desktop application.**

CineFX combines a JavaFX interface, a layered Java architecture, Hibernate ORM,
and MySQL to manage the day-to-day operations of a cinema from one application.

**Start here:** follow the [Quick run](https://github.com/SafiaTirar/Gestion_cinema#quick-run)
guide to configure the database and launch the application.

## Why it matters

Cinema teams need a reliable way to coordinate films, screening rooms, schedules,
reservations, pricing, and revenue. CineFX centralizes these operations in a single
desktop workspace and keeps business logic separate from persistence and interface
code, making the application easier to understand, test, and extend.

## What the product delivers

**Cinema operations**

1. **Film management** for creating, updating, listing, and removing films.
2. **Room and screening management** for organizing capacities and schedules.
3. **Reservation and pricing workflows** for managing bookings and fares.

**Business visibility**

1. **Planning view** for reviewing scheduled screenings.
2. **Search tools** for quickly locating cinema information.
3. **Revenue reports** for monitoring receipts by film and screening.

## Application architecture

CineFX uses a layered architecture to keep responsibilities clear:

- **JavaFX views and controllers** provide the desktop user experience.
- **Services** apply validation rules and coordinate business operations.
- **DAO classes** isolate database access and Hibernate transactions.
- **JPA entities** model films, rooms, screenings, reservations, and fares.
- **Hibernate** maps the domain model to a MySQL database.

Database-generated files, Maven build output, IDE settings, and local environment
files are excluded from Git and are not included in this public repository.

## Tech stack

- **Language:** Java 17
- **Desktop UI:** JavaFX 21, FXML
- **Persistence:** Hibernate ORM 6, Jakarta Persistence
- **Database:** MySQL 8
- **Build:** Maven
- **Tests:** JUnit 5, Maven Surefire

## Repo structure

```text
Gestion_cinema/
├── src/
│   ├── main/
│   │   ├── java/ma/cinefx/
│   │   │   ├── Controller/    # JavaFX screen controllers
│   │   │   ├── Dao/           # Hibernate data-access layer
│   │   │   ├── Dto/           # Reporting data-transfer objects
│   │   │   ├── Exception/     # Domain and persistence exceptions
│   │   │   ├── Model/         # JPA entities
│   │   │   ├── Service/       # Business logic and validation
│   │   │   ├── util/          # Hibernate SessionFactory setup
│   │   │   └── App.java       # JavaFX application entry point
│   │   └── resources/
│   │       ├── View/fxml/      # JavaFX interface definitions
│   │       └── hibernate.cfg.xml
│   └── test/java/ma/cinefx/    # JUnit test suite
├── .gitignore
├── pom.xml
└── README.md
```

## Quick run

### Prerequisites

- JDK 17 or later
- Maven 3.9 or later
- MySQL 8

### 1. Clone the repository

```bash
git clone https://github.com/SafiaTirar/Gestion_cinema.git
cd Gestion_cinema
```

### 2. Create the database

Connect to MySQL and create the application database:

```sql
CREATE DATABASE CineFX_project
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

### 3. Configure Hibernate

Open `src/main/resources/hibernate.cfg.xml` and adapt these values to your local
MySQL installation:

```xml
<property name="hibernate.connection.url">
    jdbc:mysql://localhost:3306/CineFX_project?useSSL=false&amp;serverTimezone=UTC
</property>
<property name="hibernate.connection.username">root</property>
<property name="hibernate.connection.password">your-password</property>
```

Do not commit real database credentials to a public repository.

### 4. Build and test

```bash
mvn clean test
```

### 5. Start the application

```bash
mvn javafx:run
```

Hibernate creates or updates the required database tables when the application
connects successfully.

## Development and tests

Run the complete test suite:

```bash
mvn clean test
```

Create the application JAR:

```bash
mvn clean package
```

Run the JavaFX application during development:

```bash
mvn javafx:run
```

## Security notes

- Use a dedicated MySQL user with only the permissions required by CineFX.
- Never commit production usernames, passwords, `.env` files, or database backups.
- Keep MySQL inaccessible from the public Internet.
- Back up the database regularly before application or schema upgrades.
- Review `hibernate.hbm2ddl.auto` before production deployment; automatic schema
  updates are convenient for development but should be controlled in production.

## Managed data

- Films
- Screening rooms
- Screenings and schedules
- Reservations
- Fares
- Revenue reports

## Author

Safia Tirar — 5th-year IA-DATA student, Group 05
