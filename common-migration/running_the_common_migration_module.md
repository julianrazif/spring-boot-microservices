### Goal
Run the migrations in the common-migration module with the Flyway Maven Plugin (instead of booting the Spring application), using your existing migration scripts under src\main\resources\db\migration.

---

### What I found in your project
- Module common-migration currently has:
    - flyway-core dependency (managed by parent to 8.5.13)
    - PostgreSQL driver dependency
    - Spring Boot app with application.properties holding datasource and spring.flyway.* keys
    - No Flyway Maven Plugin configured
- Parent pom manages flyway-core to 8.5.13 but also declares flyway-database-postgresql at 11.7.2. Mixing Flyway 8.x with 11.x add-ons can lead to compatibility issues.

---

### Recommended approach
1) Add the Flyway Maven Plugin to common-migration\pom.xml and parameterize with Maven properties so you can pass DB credentials at runtime.
2) Align Flyway versions (plugin + core) to the same major/minor version to avoid classpath/plugin mismatches. I recommend Flyway 11.7.2 since you already reference flyway-database-postgresql 11.7.2.
3) Run the plugin from the module with -pl common-migration, passing DB params via -D system properties.

Note: The Flyway Maven Plugin does not read Spring’s spring.datasource.* properties, so either add a flyway.conf or pass -Dflyway.* parameters (simplest), or set properties in the pom via a profile.

---

### Option A — Minimal change (keep current parent, add plugin in the module)
This works, but you may hit version mismatch between core 8.5.13 and plugin 11.x if you jump plugin version. If you stay with plugin 8.5.13, remove any 11.x references. If you stay with parent’s 8.5.13, configure the plugin as 8.5.13:

Add to common-migration/pom.xml:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.flywaydb</groupId>
      <artifactId>flyway-maven-plugin</artifactId>
      <version>${flyway.version}</version>
      <configuration>
        <locations>
          <location>classpath:db/migration</location>
        </locations>
        <url>${flyway.url}</url>
        <user>${flyway.user}</user>
        <password>${flyway.password}</password>
      </configuration>
    </plugin>

    <!-- existing spring-boot-maven-plugin here ... -->
  </plugins>
</build>
```

Then run:

- Migrate:
    - Powershell (from repo root):
        - mvn -pl common-migration -am flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/db_ecommerce -Dflyway.user=postgres -Dflyway.password=P@ssw0rd

- Validate/Repair when needed:
    - mvn -pl common-migration flyway:validate -Dflyway.url=... -Dflyway.user=... -Dflyway.password=...
    - mvn -pl common-migration flyway:repair -Dflyway.url=... -Dflyway.user=... -Dflyway.password=...

This will pick your migrations at classpath:db/migration (src\main\resources\db\migration).

If you get version conflicts (because parent also references flyway-database-postgresql 11.7.2), see Option B.

---

### Option B — Recommended: align everything to Flyway 11.7.2
1) In the parent pom.xml, set:
    - <flyway.version>11.7.2</flyway.version>
    - Ensure flyway-core and the flyway-maven-plugin use that same version.
    - Keep or remove flyway-database-postgresql at 11.7.2 (for 11.x, this remains correct).

Example parent properties section:
```xml
<properties>
  ...
  <flyway.version>11.7.2</flyway.version>
</properties>
```

Ensure dependencyManagement has:
```xml
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
  <version>${flyway.version}</version>
</dependency>
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-database-postgresql</artifactId>
  <version>${flyway.version}</version>
</dependency>
```

2) In common-migration/pom.xml add the plugin tied to the same version:
```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.flywaydb</groupId>
      <artifactId>flyway-maven-plugin</artifactId>
      <version>${flyway.version}</version>
      <configuration>
        <locations>
          <location>classpath:db/migration</location>
        </locations>
        <url>${flyway.url}</url>
        <user>${flyway.user}</user>
        <password>${flyway.password}</password>
      </configuration>
    </plugin>
    <!-- existing spring-boot-maven-plugin ... -->
  </plugins>
</build>
```

3) Run the plugin (same commands as Option A):
- mvn -pl common-migration -am flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/db_ecommerce -Dflyway.user=postgres -Dflyway.password=P@ssw0rd

---

### Optional quality-of-life improvements
- Use Maven profiles to avoid typing secrets on the command line:
```xml
<profiles>
  <profile>
    <id>local</id>
    <properties>
      <flyway.url>jdbc:postgresql://localhost:5432/db_ecommerce</flyway.url>
      <flyway.user>postgres</flyway.user>
      <flyway.password>P@ssw0rd</flyway.password>
    </properties>
  </profile>
</profiles>
```
Then run:
- mvn -pl common-migration -P local flyway:migrate

- Alternatively, use a flyway.conf (or flyway.properties) file at common-migration\src\main\resources\flyway.conf for local dev:
```
flyway.url=jdbc:postgresql://localhost:5432/db_ecommerce
flyway.user=postgres
flyway.password=********
flyway.locations=classpath:db/migration
```
Run with:
- mvn -pl common-migration flyway:migrate -Dflyway.configFiles=src/main/resources/flyway.conf

- Baseline an existing DB if schema_history already exists and isn’t empty:
    - mvn -pl common-migration flyway:baseline -Dflyway.url=... -Dflyway.user=... -Dflyway.password=...

- Use validate in CI before migrate:
    - mvn -pl common-migration flyway:validate -Dflyway.url=... -Dflyway.user=... -Dflyway.password=...

- Keep application.properties for Spring Boot, but don’t expect the plugin to read spring.datasource.* automatically.

---

### Quick commands you can run now (no pom changes, using inline config)
From the repository root in PowerShell:
- mvn -pl common-migration -am org.flywaydb:flyway-maven-plugin:8.5.13:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/db_ecommerce -Dflyway.user=postgres -Dflyway.password=P@ssw0rd -Dflyway.locations=classpath:db/migration

If you update to Flyway 11.7.2 (recommended):
- mvn -pl common-migration -am org.flywaydb:flyway-maven-plugin:11.7.2:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/db_ecommerce -Dflyway.user=postgres -Dflyway.password=P@ssw0rd -Dflyway.locations=classpath:db/migration

This will execute your V20251101072939__create_product.sql against the configured database.

---

### Summary
- Add/configure Flyway Maven Plugin in common-migration.
- Align all Flyway artifacts to the same version (prefer 11.7.2 to match your flyway-database-postgresql).
- Run migrations via: mvn -pl common-migration flyway:migrate with -Dflyway.* parameters or a profile/flyway.conf.
- You no longer need to run the Spring Boot Main class just to perform DB migrations.