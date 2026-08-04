# CMSC — Environment Setup

This is a starter Maven web-app skeleton matching the package structure agreed on for the group project. It builds and deploys, but has no business logic yet — that's step 2 onward.

## What's included
```
cmsc-webapp/
├── pom.xml                          # dependencies: Jakarta Servlet, MySQL connector, JSTL, JUnit
├── sql/cmsc_schema_final.sql        # run this in MySQL before anything else
├── src/main/java/com/cmsc/
│   ├── controller/TestServlet.java  # sanity-check servlet — delete once FrontControllerServlet exists
│   ├── util/DBConnectionUtil.java   # shared DB connection helper — every DAO uses this
│   ├── model/                       # (empty — your entity classes go here)
│   ├── dao/                         # (empty)
│   ├── factory/                     # (empty)
│   ├── observer/                    # (empty)
│   ├── builder/                     # (empty — Member 1)
│   ├── strategy/                    # (empty — Member 3)
│   ├── adapter/                     # (empty — Member 3)
│   └── business/{user,equipment,ledger}/   # (empty — servlets go here)
├── src/main/resources/db.properties # MySQL connection string (cst8288/cst8288)
├── src/main/webapp/index.jsp        # test page
├── src/main/webapp/WEB-INF/web.xml
└── src/test/java/com/cmsc/DBConnectionTest.java   # run this first
```

## Prerequisites (install these before opening the project)
1. **JDK 21** or later — `java -version` to confirm
2. **NetBeans** (latest, with Maven support built in) or IntelliJ with Maven
3. **MySQL 8.0.40** — running locally, or update `db.properties` if it's remote
4. **Payara Server 7.2026.6** (or later) — download and register it inside NetBeans under Services → Servers

## Step-by-step setup

### 1. Create the database
```
mysql -u root -p < sql/cmsc_schema_FINAL.sql
```
This drops and recreates `cmsc_db` with all 8 tables and sample data (including working bcrypt password hashes — all 5 seed accounts use the test password `Password123!`). Re-run it anytime you want a clean slate.

Then create the `cst8288` MySQL user the assignment requires (if it doesn't already exist):
```sql
CREATE USER 'cst8288'@'localhost' IDENTIFIED BY 'cst8288';
GRANT ALL PRIVILEGES ON cmsc_db.* TO 'cst8288'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Open the project in NetBeans
File → Open Project → select the `cmsc-webapp` folder (the one with `pom.xml` in it). NetBeans will read the Maven config automatically and download dependencies — first open may take a minute.

### 3. Register Payara as your server
Services tab → Servers → right-click → Add Server → Payara Server → point it at your Payara install folder.
Then: right-click the project → Properties → Run → set Server to Payara.

### 4. Verify the database connection works
Right-click `DBConnectionTest.java` → Test File. If it passes, your `db.properties` credentials are correct and MySQL is reachable. Fix this before writing any DAO code — everything downstream depends on it.

### 5. Run the project
Right-click the project → Run. NetBeans deploys to Payara and opens a browser.
- You should see **"CMSC is running"** at the root URL.
- Click through to `/test` — you should see **"Servlet routing works."**

If both of those work, your environment is fully wired up: Maven, Payara, and MySQL are all talking to each other correctly, and you can start writing real code.

## Naming reminder before final submission
The assignment requires a specific naming convention for the delivered zip:
- NetBeans project name: `Group#_Final_Project`
- Zip file name: `Group#_Final_Project.zip`

Rename `<artifactId>` and `<finalName>` in `pom.xml` accordingly once your group number is assigned — currently placeholder values.

## What NOT to commit to Git
Add a `.gitignore` with at least:
```
target/
*.class
.nb-gradle/
nbproject/private/
```
