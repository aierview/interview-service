# Interview Service

The **InterviewService** is responsible for managing technical interviews within the AIRVIEW platform. Its main
functionalities include:

- **Starting an interview**: initializes the interview session for a user.
- **Generating questions**: creates questions based on the user's role, level, and technology stack.
- **Generating feedback**: evaluates answers and generates feedback for each question.
- **Publishing questions**: sends the generated questions to Kafka topics to be consumed by other services.
- **WebSocket support**: streams questions and interview updates to the frontend in real-time.
- **User authentication**: handles authentication and authorization of users.

---

## Architecture Overview

The InterviewService uses a **modular monolithic architecture combined with Clean Architecture**, which provides several
strategic advantages:

### Modular Monolithic Architecture

- The modular monolithic design organizes the application into independent modules within a single codebase.
- **Justification:** This approach allows for easier **refactoring into microservices** in the future. For example, the
  authentication module can be extracted into a standalone microservice without disrupting the rest of the system.
- Modules communicate internally in a controlled manner, keeping dependencies manageable and improving maintainability.

### Clean Architecture

- Each module follows **Clean Architecture principles**, ensuring a clear separation of responsibilities between
  layers (e.g., domain, usecase and infrastructure).
- **Justification:** This separation enables:
    - Easier **unit testing** by isolating business logic from infrastructure concerns.
    - Maintainability and scalability, as changes in one layer do not impact others.
    - A decoupled design, reducing the risk of tight coupling between modules and dependencies.

---

## Key Benefits

- **Future-proofing:** Modular monolith allows the service to be split into microservices over time.
- **Testability:** Clean Architecture facilitates unit and integration testing.
- **Maintainability:** Layered separation and module boundaries reduce complexity and make refactoring safer.
- **Scalability:** Individual modules can evolve independently, preparing the service for future growth.

---

This combined architectural approach ensures that the InterviewService remains **robust, testable, and adaptable**,
supporting both current MVP requirements and planned expansions, such as microservices decomposition and enhanced
modularity.

## Technologies used

- **Spring Boot 3.5.4** → Main framework for building the backend in Java.
- **Spring Boot Data Redis** → Integration with Redis for caching and session storage.
- **Spring Boot Data JPA** → Abstraction for data persistence using JPA/Hibernate.
- **Spring Web** → Support for building RESTful APIs.
- **Spring Security** → Manages authentication and authorization.
- **Spring WebSocket** → Supports real-time communication with the frontend.
- **Spring Validation** → Validates incoming data for APIs.
- **Spring Doc** → Automatically generates API documentation (OpenAPI/Swagger).
- **PostgreSQL** → Relational database for main data storage.
- **JSON Web Token** → Token-based authentication with JWT.
- **Amazon S3** → Storage for audio files and other resources.
- **Lombok** → Reduces boilerplate code by generating getters/setters automatically.
- **H2 Database** → In-memory database for local testing.
- **Test Containers 1.19.3** → Framework for integration tests using Docker containers.
- **Test Containers PostgreSQL 1.19.3** → Integration tests with PostgreSQL in a container.
- **Test Containers Kafka 1.19.3** → Integration tests with Kafka in a container.
- **Wiremock 3.9.0** → Mocks external APIs for testing purposes.
- **JUnit 5** → Unit testing framework.
- **Spring DevTools** → Provides live reload and debugging features for development.
- **Gemini** → Artificial Intelligence used to generate interview questions and calculate feedback scores.

## Flow

## User Authentication Flow

The InterviewService handles user authentication, including **registration** and **login**.

### User Registration

Users can register in two ways:

1. **Google Account**
    - Users can sign up using their Google account.

2. **Email and Password**
    - Users can register by providing the following fields:
        - `name` (String): Full name of the user
        - `email` (String): Email address
        - `password` (String): Password for login
        - `role` (String): The role of the user (e.g., Back-end, Mobile, Front-end, Full-stack)

> Once the registration is complete, the user will be able to log in and start an interview session.

### User Sign-In

After registration, users can sign in to the platform.

1. **Email and Password**
    - If the user registered using email and password:
        - They must use the same **email** and **password** to log in.
        - The system will verify the credentials and grant access to the interview dashboard.

2. **Google Account**
    - If the user registered using their Google account:
        - They must log in using the **same Google account**.
        - The system will authenticate via Google OAuth and grant access to the interview dashboard.

> The authentication flow ensures that users can only access their own account and corresponding interview sessions.

### Start Interview Flow

After signing in, the user can begin an interview session by providing some initial information.

1. **Begin Interview**
    - The user provides:
        - **Role**: Backend, Mobile, Frontend, or Full-stack.
        - **Level**: Junior, Mid-level, or Senior.
        - **Stack**: the technology stack the user works with.

2. **Question Generation**
    - The system generates a series of technical questions based on the provided role, level, and stack.
    - Questions are displayed to the user on the frontend.

3. **Answer Submission**
    - For each question:
        - The user provides an answer via audio.
        - The backend processes the response.
        - Simultaneously, the frontend prepares and displays the next question.

4. **Feedback Generation**
    - Once all questions have been answered:
        - The system generates a detailed feedback report.
        - The feedback includes:
            - **Score per question**
            - **Total interview score**
        - The feedback can be downloaded as a PDF or viewed directly in the platform.

> This flow ensures a smooth, sequential interview experience while allowing the backend to process responses and
> evaluate performance in real-time.

# 🖥️ Instructions to Run Locally

To run this project locally, the following prerequisites must be met:

1. **Java 21** → installed on your machine.
2. **Redis** → running locally.
3. **Kafka** → running locally.
4. **Environment Variables** → Engine Environment:

    - **GOOGLE_TOKEN_INFO_URL**: provide the Google Token Info URL from your account to manage Google Account
      authentication.
    - **R2_ENDPOINT**: the endpoint URL for your Cloudflare R2 bucket.
    - **R2_BUCKET**: the name of your R2 bucket.
    - **R2_ACCESS_KEY**: access key for R2 authentication.
    - **R2_SECRET_KEY**: secret key for R2 authentication.
    - **R2_AUDIO_FILE_PATH**: directory path in the R2 bucket where TTS-generated audio files will be stored.
    - **REDIS_HOST**: host address for the Redis instance.
    - **REDIS_PORT**: port for the Redis instance.
    - **JWT_SECRET_KEY**: secret key used to sign JWT tokens.
    - **JWT_EXPIRATION_TIME**: expiration time for JWT tokens (e.g., `3600000` for 1 hour in milliseconds).
    - **KAFKA_BOOTSTRAP_SERVERS**: comma-separated list of Kafka bootstrap servers (e.g., `localhost:9092`).

> Ensure that Redis and Kafka are running and properly configured before starting the application.
> ⚠️ Important: Provide all the environment variables listed above in the `application.yaml` file of your project.

### Spring Boot Profiles

By default, the environment variables provided in `application.yaml` will be used under the **default Spring Boot
profile**.

For other environments, you can create separate profile-specific configuration files:

- **Development profile**: `application-dev.yaml` → for local or development setups. Add the same environment variables
  configured for development.
- **Homologation / Staging profile**: `application-homolog.properties` → to simulate a staging environment.
- **Production profile**: `application-prod.properties` → for production deployment with production-ready variables.

> Spring Boot will automatically pick the configuration file corresponding to the active profile set via
`spring.profiles.active`.

## Steps to run

After setting up all environment variables and selecting the desired Spring Boot profile, you can run the application.

1. **Using an IDE** (recommended: IntelliJ IDEA):
    - Open the project in IntelliJ.
    - Locate the main class `InterviewService`.
    - Run it directly via the IDE's Spring Boot run configuration.
      > The selected Spring profile (default, dev, homolog, prod) will be applied automatically based on your
      `application-<profile>.yaml`.

2. **Using Maven from the terminal**:
    - By default, Maven will use the `default` Spring profile.
    - To specify a different profile (e.g., dev, homolog, prod), run:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev

> Replace the above values with the real ones from your environment.

# 🐳 Instructions to run using Docker

To run the environment using Docker Compose, make sure you have the following repositories cloned locally:

1. **Infrastructure Repository**
    - First, clone the infrastructure repository locally:

      ```bash
      git clone https://github.com/aierview/infra.git
      ```

    - Then, start by running the Docker Compose located in the infrastructure repository.
    - This will start **Zookeeper**, **Kafka**, and **KafDrop**.

2. **Speech Service Repository**
    - First, clone the Speech Service repository locally:

      ```bash
      git clone https://github.com/aierview/speech-service.git
      ```

    - Next, run the Docker Compose located in the Speech Service repository.
    - Make sure to replace all environment variables with your actual values.

> Important: The Interview Service depends on both the infrastructure and the Speech Service, so ensure they are up and
> running before starting the Interview Service container.

> Note: When starting the infrastructure services, start each service one by one in the following order: **ZooKeeper →
Kafka → KafDrop**.
> - Wait until ZooKeeper is fully ready to accept connections before starting Kafka.
> - Wait until Kafka is fully ready to accept connections before starting KafDrop.
> - Failing to respect this order and the readiness of each service may cause some services to fail to start due to
    unmet dependencies.

> Once all infrastructure services are verified to be running, including **Redis**, navigate to the **Interview Service
** directory and run the following command to start the InterviewService container:

```bash
docker-compose up
```

## 📦 Examples / Payloads

### Input Payload (consumed by TTS)

Topic: `interview-question.text`

The TTS service consumes a payload representing the question to be converted into audio:

**Fields:**

- `questionId` (Long): the ID of the question
- `question` (String): the text of the question

**Example JSON message:**

```json
{
  "questionId": 123,
  "question": "Explain the concept of dependency injection in Java."
}
```

### Output Payload (produced by TTS)

Topic: `interview-question.audio`

After generating the audio, the TTS service produces a payload containing the audio URL:

**Fields:**

- `questionId` (Long): the ID of the question
- `audioUrl` (String): the URL to access the generated audio

**Example JSON message:**

```json
{
  "questionId": 123,
  "audioUrl": "https://r2-bucket.example.com/audio/123_question.mp3"
}
```

### Input Payload (consumed by STT)

Topic: `interview-answer.audio`

The STT service consumes a payload representing the audio file to be transcribed:

**Fields:**

- `questionId` (Long): the ID of the question associated with the audio
- `filename` (String): the name of the audio file to be transcribed

**Example JSON message:**

```json
{
  "questionId": 123,
  "filename": "123_answer.mp3"
}
```

### Output Payload (produced by STT)

Topic: `interview-answer.text`

After transcribing the audio, the STT service produces a payload containing the text of the answer:

**Fields:**

- `questionId` (Long): the ID of the question associated with the audio
- `answerText` (String): the transcribed text from the audio

**Example JSON message:**

```json
{
  "questionId": 123,
  "answerText": "This is the transcribed answer to the question."
}
```

# 🧪 Interview Service Tests

This directory is intended for **unit and integration tests** related to the InterviewService.

## Running Tests

### Using IntelliJ

You can run the tests directly from your IDE (IntelliJ) by running the test classes or the test suite.

### Using Maven in the Terminal

You can also run the tests from the terminal using Maven commands:

- `mvn test` → runs the tests and generates test reports.
- `mvn verify` → runs the tests, generates test coverage, and checks if the coverage meets the minimum threshold.
- `mvn clean install` → cleans, compiles, and installs the project, running the tests in the process.

> The test coverage report will be available in the `target` folder.  
> The minimum required test coverage is currently set to 60–80%. If coverage is below this threshold, `mvn verify` will
> return an error.

# 🤝 Contribution

This section provides guidelines for contributing to the **Speech Service**.

Repository: [https://github.com/aierview/interview-service](https://github.com/aierview/speech-service.git)

---

## How to Contribute

1. **Clone the repository**

```bash
git clone https://github.com/aierview/interview-service
cd inerview-service
```

2. **Create a new branch from main for your feature or fix:**

```bash
git checkout main
git pull
git checkout -b your-feature-branch
```

3. **Develop your feature or bug fix.**

4. **Commit your changes with clear messages:**

```bash
git add .
git commit -m "Describe your changes"
```

5. **git push origin your-feature-branch**

```bash
git push origin your-feature-branch
```

6. Open a Merge Request (MR) to the homolog branch. Your feature will be reviewed, and validated.
7. Once validated, open a Merge Request to the main branch following the normal workflow.

## ⚠️ Important Notes

- Always create a branch with a **descriptive name** that indicates the purpose of your changes:
    - Features: prefix with `feature/` → e.g., `feature/add-tts-queue`.
    - Bug fixes: prefix with `fix/` → e.g., `fix/audio-upload-bug`.
    - DevOps / CI-CD changes: prefix with `devops/` → e.g., `devops/docker-config`.
    - Documentation: prefix with `docs/` → e.g., `docs/update-readme`.
    - Tests: prefix with `test/` → e.g., `test/tts-unit-tests`.

- Make **small commits** to facilitate the review process.  
  Avoid large commits with multiple unrelated changes; this ensures that the review and approval process is smoother and
  more efficient.

> It is important that every contribution follows this branching and commit convention to maintain control and clarity
> over what changes are introduced into the project.

## 🚀 Roadmap / Future Features

- **Multi-language support**: Allow users to conduct interviews in multiple languages.  
  Currently, only Portuguese is supported. The next step is to add English and potentially other languages.

- **Kubernetes deployment**: Currently, the application is deployed directly on Fly.io.  
  The plan is to migrate to Kubernetes for a more professional, scalable, and manageable deployment workflow.

- **Authentication service migration**: Move the authentication module currently inside the Interview Service to a *
  *dedicated microservice**.

- **Microservices architecture enhancements**: Introduce a **Config Server**, **Discovery Server**, and an **API Gateway
  ** to better manage and route requests between microservices.

## 📝 License

This project is licensed under the **MIT License**.

---

MIT License

Copyright (c) 2025 AIRVIEW

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## 🔗 Useful Links

- [Spring Boot Documentation](https://spring.io/projects/spring-boot) – Framework used for building the Interview
  Service backend.
- [Redis Documentation](https://redis.io/documentation) – Used for caching and session management.
- [Google Authentication](https://developers.google.com/identity) – Documentation for integrating Google Sign-In and
  OAuth authentication.
- [Google Token Info URL](https://www.googleapis.com/oauth2/v3/tokeninfo) – Endpoint to validate Google OAuth tokens.
- [Fly.io](https://fly.io/docs/) – Platform used for deploying the AIRVIEW services.
- [Apache Kafka](https://kafka.apache.org/documentation/) – Official documentation for Kafka, used for message streaming
  in AIRVIEW.
