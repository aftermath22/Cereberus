
-----

# Cereberus - Spring Boot + Okta SSO Integration

This project demonstrates the integration of Okta OpenID Connect (OIDC) for Single Sign-On (SSO) with Spring Boot applications. It includes two example applications, "Task Manager" and "LearnHub," showcasing how users can authenticate with Okta and access protected resources.

## 🚀 Features

  * **Okta OIDC Integration**: Secure your Spring Boot applications using Okta as an identity provider.
  * **Single Sign-On (SSO)**: Allow users to sign in once and access multiple applications without re-authenticating.
  * **Task Manager**: A simple application to create, edit, and delete tasks.
  * **LearnHub**: A platform for users to access their courses and learning materials.
  * **Heroku Deployment**: Includes configuration for easy deployment to Heroku.

## 🛠️ Prerequisites

Before you begin, ensure you have the following installed:

  * **Java 11+**: The application is built using Java 11.
  * **Maven**: Used for building and managing the project dependencies.
  * **Okta Developer Account**: You will need a free Okta developer account to create and configure your OIDC application.

## ⚙️ Configuration

1.  **Clone the repository**:

    ```bash
    git clone https://github.com/cereberus/Cereberus-improvements.git
    cd Cereberus-improvements
    ```

2.  **Okta Application Configuration**:

      * Log in to your Okta developer account.
      * Create a new OIDC application.
      * Note the **Client ID**, **Client Secret**, and **Issuer** URI.
      * Update the `application.properties` file in each application with your Okta credentials:
        ```properties
        okta.oauth2.issuer=<your-okta-issuer-uri>
        okta.oauth2.client-id=<your-okta-client-id>
        okta.oauth2.client-secret=<your-okta-client-secret>
        ```

## 🏃‍♀️ Running the Applications

You can run each application using the provided shell script or with Maven.

### Task Manager (Port 8080)

  * **Using the script**:
    ```bash
    cd okta-spring-boot-oidc-sso-example
    ./run_app.sh --ci <client-id> --cs <client-secret> --is <issuer>
    ```
  * **Using Maven**:
    ```bash
    cd okta-spring-boot-oidc-sso-example
    mvn spring-boot:run
    ```

### LearnHub (Port 8081)

  * **Using the script**:
    ```bash
    cd okta-spring-boot-oidc-sso-example-2
    ./run_app.sh --ci <client-id> --cs <client-secret> --is <issuer> --po 8081
    ```
  * **Using Maven**:
    ```bash
    cd okta-spring-boot-oidc-sso-example-2
    mvn spring-boot:run -Dserver.port=8081
    ```

Once the applications are running, you can access them at:

  * **Task Manager**: [http://localhost:8080](https://www.google.com/search?q=http://localhost:8080)
  * **LearnHub**: [http://localhost:8081](https://www.google.com/search?q=http://localhost:8081)

## ☁️ Heroku Deployment

You can deploy these applications directly to Heroku. The project is already configured for Heroku deployment.

1.  **Create a Heroku account** if you don't have one.
2.  **Install the Heroku CLI**.
3.  **Log in to Heroku**:
    ```bash
    heroku login
    ```
4.  **Create a new Heroku application**:
    ```bash
    heroku create my-cereberus-app
    ```
5.  **Provision the Okta add-on**:
    ```bash
    heroku addons:create okta
    ```
6.  **Deploy the application**:
    ```bash
    git push heroku master
    ```

Your application will be deployed and accessible at the URL provided by Heroku.

## 📂 Project Structure

The project contains two main applications:

  * `okta-spring-boot-oidc-sso-example`: The **Task Manager** application.
  * `okta-spring-boot-oidc-sso-example-2`: The **LearnHub** application.

Each application follows a standard Spring Boot project structure.

## 🤝 Contributing

Contributions are welcome\! If you find any issues or have suggestions for improvements, please open an issue or create a pull request.

-----

I hope this `README.md` is helpful\! Let me know if you have any other questions or need further assistance with your project. Happy coding\! 😊
