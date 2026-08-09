# Group3 Final Project — Campus Maker Space Co-op

This NetBeans Maven web application implements CST8288 Final Project
requirements FR01–FR06 with Jakarta Servlets/JSP, MySQL, DAO-based data
access, and the required Builder, Strategy, Simple Factory, Adapter, and
Observer patterns.

## Required software

- JDK 21
- NetBeans with Maven support
- MySQL 8.0.40
- Payara Server 7.2026.6

## Set up the database

1. Open MySQL Workbench and connect to the local MySQL server as an account
   that can create databases and users.
2. Open `sql/cmsc_db.sql`.
3. Run the complete script. It recreates `cmsc_db`, inserts the sample data,
   and creates the required application account `cst8288` / `cst8288`.
4. Refresh the Schemas panel and confirm that `cmsc_db` contains eight
   tables.

The application reads its connection settings from
`src/main/resources/db.properties`.

## Open, test, and run

1. In NetBeans, select **File > Open Project**.
2. Select the `Group3_Final_Project` folder containing `pom.xml`.
3. Right-click the project and select **Clean and Build**.
4. Confirm that all tests pass and the build ends with `BUILD SUCCESS`.
5. Right-click the project, select **Properties > Run**, and select Payara.
6. Right-click the project and select **Run**.

## Sample accounts

All sample accounts use password `Password123!`.

| Role | Email |
|---|---|
| User | `alice@algonquin.ca` |
| Trainer | `ben@algonquin.ca` |
| Shop-Tech | `carla@algonquin.ca` |

## Submission name

- NetBeans/Maven project: `Group3_Final_Project`
- Final archive: `Group3_Final_Project.zip`

Build output and NetBeans-local configuration files must not be included in
the submitted archive.
