# Notifly
The Notification and Insights System is an add-on for Contactly, offering real-time notifications, bulk email sending, and analytics for admins. It triggers notifications (e.g., promotions, reminders), provides insights on contact interactions, and integrates seamlessly with Contactly using scalable and fault-tolerant technologies.

## Features

- Spring Boot 3.3.5
- Kafka Integration for messaging
- Redis for caching and real-time data processing
- Actuator for monitoring
- Spring Security for securing endpoints
- OpenTelemetry for tracing and monitoring
- Swagger integration for API documentation

## Prerequisites

Before running the application, make sure you have the following services running locally:

### 1. Redis Server

Redis is used for caching and message brokering in the Notifly application.

### macOS:

1. Install Redis using Homebrew:
```bash
brew install redis
```
2. Start Redis:

```bash
brew services start redis
```
3. Verify Redis is running:
```bash
redis-cli ping
```
You should get a PONG response.

### Windows:
1. Download Redis from Redis for Windows.
2. Extract the Redis archive to a folder and open a command prompt in that folder.
3. Start Redis:

```bash
redis-server.exe
```
4. Verify Redis is running: Open a new command prompt and run:

```bash
redis-cli.exe ping
```
You should get a PONG response.

### 2. Kafka Server
Notifly uses Kafka for event-driven messaging. You can run Kafka using Docker:

#### **macOS**:

1. **Install Kafka** via Homebrew:
```bash
brew install kafka
```

2. Start Zookeeper (Required for Kafka to work):

```bash
zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties
```
3. Start Kafka:

```bash
kafka-server-start /usr/local/etc/kafka/server.properties
```
4. Verify Kafka is running:

```bash
kafka-topics --list --bootstrap-server localhost:9092
```

#### **Windows**

1. Download Kafka from Kafka's official website.

2. Extract the Kafka archive and navigate to the `bin/windows` folder.

3. Start Zookeeper: Open a command prompt in the Kafka `bin/windows` directory and run:

```bash
zookeeper-server-start.bat ..\..\config\zookeeper.properties
```
4. Start Kafka: In another command prompt window, navigate to `bin/windows` and run:
```bash
kafka-server-start.bat ..\..\config\server.properties
```
5. Verify Kafka is running:

```bash
kafka-topics.bat --list --bootstrap-server localhost:9092
```
### 3. Contactly Application
Contactly is another microservice that manages contacts. Notifly will consume contact data from Contactly to send notifications.

Make sure you have the Contactly application running and accessible for communication via Kafka.

### 3. Java 17
   Notifly is built with Java 17. Ensure you have Java 17 installed.

Notifly uses Maven for dependency management. Make sure Maven is installed. 

### Running the Application

Once the prerequisites are set up, you can run the application:

1. Clone the repository:

```bash
git clone https://github.com/your-username/notifly.git
```
2. Build the project:

```bash
mvn clean install
```

3. Run the application:

```bash
mvn spring-boot:run
```
The application will start and listen on port 8080.


### API Documentation
Notifly uses Swagger to provide API documentation. Once the application is running, you can access the Swagger UI at:

```bash
http://localhost:8080/swagger-ui.html
```
# API Endpoints

### 1. BatchJobController

- **Endpoint**: `/batch/trigger-email-batch`
- **Method**: `GET`
- **Summary**: Triggers a batch job for processing email notifications.
- **Authorization**: Requires a Bearer token for authentication.
- **Responses**:
  * `200 OK`: Batch job triggered successfully.
  * `400 Bad Request`: Invalid authorization token.
  * `500 Internal Server Error`: Failed to execute batch job due to an internal error.

### 2. EmailConfigController

*    **Endpoint**: `/email-config`
*    **Method**: `GET`
*    **Summary**: Fetches the current email configuration.
- **Responses**:
  *    `200 OK`: Email configuration retrieved successfully.
  *    `404 Not Found`: Email configuration not found.

*    **Endpoint**: `/email-config`
*    **Method**: `PUT`
*    **Summary**: Updates the sender email and token.
*    **Parameters**:
*    `senderEmail`: The new sender email.
*    `emailToken`: The new email token.
* 
- **Responses**:
  *    `200 OK`: Email configuration updated successfully.
  
### 3. NotificationController

*    **Endpoint**: `/api/notifications/send`

* **Method**: `POST`

* **Summary**: Sends a WebSocket notification to a specific destination with the provided message.

* **Parameters**:

* `destination`: The WebSocket destination (e.g., topic or user).
* `message`: The notification message to send.
- **Responses**:

  * `200 OK`: Notification sent successfully.
  * `400 Bad Request`: Invalid request parameters.

* **Endpoint**: `/api/notifications/send-email`

* **Method**: `POST`

* **Summary**: Sends an email notification to a contact using dynamic token-based authorization.

- **Parameters**:
  * `username`: The username for fetching the authorization token.
  * `password`: The password for fetching the authorization token.

* **Request Body**:

* **notificationEvent**: Contains the details of the notification event (recipient, message, etc.).
- **Responses**:

  * `200 OK`: Email notification sent successfully.
  * `400 Bad Request`: Invalid request or missing parameters.
  * `500 Internal Server Error`: Internal server error while sending the email.

### Dependencies
The project includes the following key dependencies:

- Spring Boot: Core framework for the application.
- Spring Kafka: 2.x For event-driven communication with Kafka.
- Spring Redis: 7.x For caching and real-time data processing with Redis.
- Hibernate Envers: For entity auditing and tracking changes.
- Spring Security: For securing endpoints.
- Lombok: For reducing boilerplate code (e.g., getters/setters).
- OpenTelemetry: For tracing and monitoring.
- Swagger: For API documentation and testing.
- H2 Database: Used for in-memory database in testing.
- Spring Cloud Circuit Breaker (Resilience4j): For resilience and fault tolerance.


### Configuration

```bash
spring.kafka.bootstrap-servers=localhost:9093
```

### Redis Configuration

```bash
spring.redis.host=localhost
spring.redis.port=6379
```

# Kafka Configuration Overview

1. **Producer Configuration:**

* The producer configuration defines the Kafka server details (`localhost:9092`) and the serializer for both the key and value (in this case, `StringSerializer` for simple string messages).
* The producer is set up using a `KafkaTemplate`, which is responsible for sending messages to Kafka topics.

2. **Consumer Configuration**:

* The consumer configuration includes details like the Kafka server and the consumer group ID (`notification-group`).
* Consumers use `StringDeserializer` to deserialize the incoming messages.

3. **Dead Letter Queue (DLQ)**:

* A `DeadLetterPublishingRecoverer` is used to handle messages that cannot be processed. If a consumer encounters an error while processing a message, the message is sent to a dead-letter queue (`.DLQ` suffixed topic).
* This mechanism allows for messages that cannot be processed immediately to be retried or analyzed separately without affecting the main processing flow.

4. **Error Handling and Retry Mechanism**:

* The `DefaultErrorHandler` is configured with a `FixedBackOff` strategy that retries failed message consumption 3 times, with a 1-second delay between attempts.
* If the message still fails after these retries, it is sent to the DLQ.
* The retry and error handling logic ensures that transient issues do not cause permanent message loss.

5. **Acknowledgment and Manual Offsets**:

* The Kafka consumer uses manual acknowledgment (`AckMode.MANUAL_IMMEDIATE`), meaning that the consumer explicitly tells Kafka when a message has been successfully processed (`acknowledge()`) or when it should be retried (`nack()`).
* This ensures that only successfully processed messages are acknowledged and that messages that fail (due to issues like invalid format or missing data) can be retried or dead-lettered.

# Consumer Behavior and Error Scenarios

1. **Message Format Validation:**

* The consumer (`EventConsumer`) first checks if the message is in the expected format (JSON) and deserializes it into a `ContactDTO`. If the message is not in valid JSON format, it throws an `InvalidMessageFormatException`, which leads to the message being negatively acknowledged (retry).
* If essential fields like `name` or `email` are missing from the `ContactDTO`, the message is rejected with a `MissingDataException`.

2. **Service Unavailability**:

* If there is an issue with service availability (such as the failure to generate an email payload), the consumer throws a `ServiceUnavailableException`. This exception also triggers a retry for the message.

3. **Logging and Monitoring:**

* Every step of the process is logged to provide visibility into message processing, including the payload, any errors, and retry attempts.
* This logging is crucial for troubleshooting and monitoring the state of the Kafka consumers and producers.

4. **Retry Mechanism and Nacking**:

* The negative acknowledgment (`nack()`) is used whenever an error occurs in processing. The retry logic will attempt to process the message again before it is eventually moved to the DLQ if it still cannot be successfully processed.

5. **Kafka Producer**:

* The producer sends events to the Kafka topic using a `KafkaTemplate`. The `EventProducer` class allows the sending of messages to a Kafka topic (`new-contacts` in this case), which is consumed by the `EventConsumer`.

## Kafka Topics

Topic Creation:

A NewTopic bean is defined for notifly-notification, with a single partition and replica, ensuring that the topic exists when the application starts.