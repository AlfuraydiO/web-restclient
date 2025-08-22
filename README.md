# Web REST Client

A simple, lightweight web-based REST client built with Quarkus, htmx, and the Java HTTP Client. 
This article sheds more light on its purpose https://oalfuraydi.sa/blog/building-a-modern-web-testing-platform-for-remote-teams .
## Tech Stack

*   **Backend:**
    *   [Quarkus](https://quarkus.io/) (Java Framework)
    *   Java 21
    *   [Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache)
    *   In-memory H2 Database
    *   [JAX-RS](https://jakarta.ee/specifications/restful-ws/) for REST APIs
*   **Frontend:**
    *   [Quarkus Qute](https://quarkus.io/guides/qute) (Server-side templating)
    *   [htmx](https://htmx.org/) (for AJAX-powered interactivity)
    *   Plain CSS

## Prerequisites

*   JDK 21+
*   Maven 3.8.6+
*   (Optional) Docker for containerized builds.

## Getting Started

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/AlfuraydiO/web-restclient 
    cd web-restclient
    ```

2.  **Run the application in development mode:**
    ```bash
    ./mvn quarkus:dev
    ```
      The application will be available at `http://localhost:8080`.

      You can build the application as a standard JAR or a native executable.

      *   **Build a standard JAR:**
       ```bash
       ./mvn package
       ```
       you can then run the intgration test usign 

      ```bash 
      mvn verify -DskipITs=false
      ```
      The resulting JAR will be in the `target/quarkus-app/` directory. In which you can run it using. 

      ```bash
      java -jar target/quarkus-app/quarkus-run.jar
      ```
