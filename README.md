# PersonRegistrySystem

Ez egy **RESTful API** személyeket nyilvántartó, karbantartó Spring Boot alkalmazást.
A projekthez tartozó adatbázis tartalmaz 3 táblát: személyek, címek, elérhetőségek.
Egy személynek maximum kettő címe lehet (állandó, ideiglenes), 
egy címhez több elérhetőség (email, telefon, stb.) tartozhat.

## Technológiák
* **Core:** Java 17, Spring Boot 3
* **Adatbázis:** MS SQL, H2 Database, Spring Data JPA (Hibernate)
* **Web:** Spring Web MVC
* **Eszközök:** Lombok, Maven
* **Dokumentáció:** SpringDoc OpenAPI (Swagger UI)
* **Tesztelés:** JUnit 5, Mockito, Spring Boot Test, RestAssured

## Fejlesztés 
### Indítás H2 DB-vel (opcionális)
A projekt készítése során az elején H2 adatbázis használtam a könnyebb tesztelhetőség végett. 
Ezt opcionálisan benne hagytam és az alábbi script-tel ellehet indítani az alkalmazást egy h2-es 
spring profillal és magától betölti a DB-hez szükséges scripteket: 
```bash
  mvn spring-boot:run -Dspring-boot.run.profiles=h2
```
H2-es db-t pedig ezen a linken ellehet érni futtatás után: 
http://localhost:8080/h2-console

### Alapértelmezett indítás MSSQL-el

Az alkalmazás a `http://localhost:8080` porton indul.
Alapértelmezetten elindulásakor MS SQL DB-re próbál majd rácsatlakozni 1433-as porton.
A ddl és dlm fájlok mellékelve lesznek a rootban:

[DDL](ddl.sql), [MDL](dml.sql)

projekt futtatása:
```bash
  mvn spring-boot:run 
```

### OpenApi
Projekthez készült openapi-s felület ahol fel van tüntetve hogy melyik végpont mit csinál és futtatás után tesztelni is 
lehet a végpontokat az alábbi linken:

http://localhost:8080/swagger-ui/index.html

## Végpontok (Endpoints)

| HTTP Metódus | Végpont | Leírás                               |
| :--- | :--- |:-------------------------------------|
| `GET` | `/api/persons` | Összes személy listázása             |
| `GET` | `/api/persons/{id}` | Egy személy lekérése ID alapján      |
| `POST` | `/api/persons` | Új személy létrehozása               |
| `PUT` | `/api/persons/{id}` | Meglévő személy módosítása           |
| `DELETE` | `/api/persons/{id}` | Személy és kapcsolódó adatok törlése |

## Tesztelés

A projekt átfogó tesztlefedettséggel rendelkezik a stabilitás biztosítása érdekében:

* **Unit Tesztek:** (`unittest/PersonServiceTest`) Mockito segítségével izoláltan teszteli az üzleti logikát (pl. max 2 cím szabály).
* **Integrációs Tesztek:** (`integrationtest/PersonControllerTest`) A teljes rétegződést teszteli H2 adatbázissal és `MockMvc`-vel.
* **E2E Tesztek:** (`e2etest/PersonApiTest`) Valós HTTP kérésekkel vizsgálja az alkalmazást `RestAssured` használatával.

A tesztek futtatása:
```bash 
    mvn test
```