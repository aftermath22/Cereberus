# Cereberus: Task Manager & LearnHub

Welcome to **Cereberus**, a project that demonstrates a powerful integration between two distinct Spring Boot applications, secured with Okta for Single Sign-On (SSO). The project consists of two main components:

  * **Task Manager (`okta-spring-boot-oidc-sso-example`)**: A web application for creating, editing, and deleting personal tasks.
  * **LearnHub (`okta-spring-boot-oidc-sso-example-2`)**: A separate web application that can securely view tasks from the Task Manager, providing a seamless user experience across different services.

This project is an excellent example of how microservices can communicate with each other while maintaining a unified authentication system.

-----

## Getting Started

To get this project up and running, you'll need a few prerequisites.

### Prerequisites

  * **Okta Developer Account**: You'll need a free developer account from [Okta](https://developer.okta.com/signup/).
  * **Java 11+**: Make sure you have a compatible Java Development Kit (JDK) installed.
  * **Maven**: The project uses Maven for dependency management.

### Okta Configuration

Before you can run the applications, you need to set up an OIDC application in your Okta Developer Console.

1.  **Create a New Application**:

      * In your Okta dashboard, go to **Applications** \> **Applications** and click **Create App Integration**.
      * Select **OIDC - OpenID Connect** as the sign-in method.
      * Choose **Web Application** as the application type.

2.  **Configure Your Application**:

      * **App integration name**: Give your application a name, like `Cereberus`.
      * **Sign-in redirect URIs**: Add the URIs for both local development and your deployed applications. These are the locations Okta will redirect to after a successful login.
        ```
        http://localhost:8080/login/oauth2/code/okta
        http://localhost:8081/login/oauth2/code/okta
        https://cereberus-task-manager.onrender.com/login/oauth2/code/okta
        https://cereberus-learnhub.onrender.com/login/oauth2/code/okta
        ```
      * **Sign-out redirect URIs**: Add the base URLs for your applications. Okta will redirect to these after a successful logout.
        ```
        http://localhost:8080
        http://localhost:8081
        https://cereberus-task-manager.onrender.com
        https://cereberus-learnhub.onrender.com
        ```
      * **Assignments**: Choose **Allow everyone in your organization to access** for simplicity, or assign specific users if you prefer.

3.  **Save Your Credentials**:

      * After saving, you will be taken to your application's page. Take note of the **Client ID** and **Client secret**.
      * You will also need your **Okta domain** (e.g., `dev-123456.okta.com`). The **Issuer URI** is typically `https://<Your-Okta-Domain>/oauth2/default`.

-----

## Running Locally

To run the applications on your local machine, you'll need to configure your Okta credentials in both projects.

1.  **Configure the Task Manager (`okta-spring-boot-oidc-sso-example`)**:

      * Open the `src/main/resources/application.properties` file.
      * Update the Okta properties with the credentials you saved earlier.
      * The base URLs should point to your local servers.

    <!-- end list -->

    ```properties
    # Okta Configuration
    okta.oauth2.issuer=https://<Your-Okta-Domain>/oauth2/default
    okta.oauth2.clientId=<Your-Client-ID>
    okta.oauth2.clientSecret=<Your-Client-Secret>

    # Application URLs
    app.base-url=http://localhost:8080
    learnhub.base-url=http://localhost:8081

    # For running behind a proxy (like Render)
    server.forward-headers-strategy=native
    ```

2.  **Configure the LearnHub (`okta-spring-boot-oidc-sso-example-2`)**:

      * Open `src/main/resources/application.properties` in the second project.
      * Use the **same** Okta credentials as the Task Manager.
      * The base URLs will be the same as well.

    <!-- end list -->

    ```properties
    # Okta Configuration
    okta.oauth2.issuer=https://<Your-Okta-Domain>/oauth2/default
    okta.oauth2.clientId=<Your-Client-ID>
    okta.oauth2.clientSecret=<Your-Client-Secret>

    # Application URLs
    app.base-url=http://localhost:8080
    learnhub.base-url=http://localhost:8081

    # For running behind a proxy (like Render)
    server.forward-headers-strategy=native
    ```

3.  **Run the Applications**:

      * Open a terminal and navigate to the `okta-spring-boot-oidc-sso-example` directory and run:
        ```bash
        ./mvnw spring-boot:run
        ```
      * Open a *second* terminal, navigate to `okta-spring-boot-oidc-sso-example-2`, and run:
        ```bash
        ./mvnw spring-boot:run
        ```
      * The Task Manager will be available at `http://localhost:8080/ia` and LearnHub at `http://localhost:8081/ib`.

-----

## Deployment to Render

This project is configured for easy deployment on **Render**.

1.  **Create a New Web Service on Render**:

      * In your Render dashboard, click **New +** \> **Web Service**.
      * Connect your GitHub repository.

2.  **Configure the Task Manager Service**:

      * **Name**: `cereberus-task-manager`
      * **Root Directory**: `okta-spring-boot-oidc-sso-example`
      * **Build Command**: `./mvnw clean install`
      * **Start Command**: `java -jar target/*.jar`

3.  **Configure the LearnHub Service**:

      * Create a *second* web service on Render.
      * **Name**: `cereberus-learnhub`
      * **Root Directory**: `okta-spring-boot-oidc-sso-example-2`
      * **Build Command**: `./mvnw clean install`
      * **Start Command**: `java -jar target/*.jar`

4.  **Set Environment Variables**:

      * For **both** services, go to the **Environment** tab and add the following environment variables.
      * **Note**: `OKTA_OAUTH2_ISSUER` has `/oauth2/default` appended to it.

| Key | Value |
| :--- | :--- |
| `OKTA_OAUTH2_ISSUER` | `https://<Your-Okta-Domain>/oauth2/default` |
| `OKTA_OAUTH2_CLIENT_ID` | `<Your-Client-ID>` |
| `OKTA_OAUTH2_CLIENT_SECRET` | `<Your-Client-Secret>` |
| `APP_BASE-URL` | `https://cereberus-task-manager.onrender.com` |
| `LEARNHUB_BASE-URL` | `https://cereberus-learnhub.onrender.com` |

-----

## Live Endpoints

Once deployed, your applications will be available at the following URLs:

  * **Task Manager**: [https://cereberus-task-manager.onrender.com/ia](https://cereberus-task-manager.onrender.com/ia)
  * **LearnHub**: [https://cereberus-learnhub.onrender.com/ib](https://cereberus-learnhub.onrender.com/ib)
