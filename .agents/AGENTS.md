# Workspace Rules

These rules apply to the Rello chat application project and its future development.

## General Project Guidelines
- **Core Domain:** Group chat and 1-on-1 user chat application.
- **Technology Stack:** Java, Spring Boot, WebSocket with SockJS (text queue), and Database persistence.
- **Primary Goals:** High security, reliable message delivery, and robust user list management.

## Future Considerations
- **Scalability:** The architecture must be prepared to scale out. Avoid relying solely on local memory for critical state (prepare for external message brokers like Redis/RabbitMQ for WebSocket scaling).
- **Security Enhancements:** Emphasize end-user security in all future developments (e.g., secure connection negotiation, strict payload validation, and rate limiting).
- **Reliability:** Implement fault-tolerant features to handle reconnections, offline queuing, and message redelivery.

## Implementation Instructions
- Always prioritize security when adding new chat endpoints or message handlers.
- Maintain the user list strictly in the database and ensure accurate synchronization with connected WebSocket clients.
- Validate all incoming text queue messages to prevent injection attacks and ensure data integrity.
