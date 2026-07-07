---
name: chat_system_guidelines
description: Prompts and instructions for developing the group chat and user chat features using Java, SockJS, and a database. Focuses on security, scale, and reliability.
---

# Chat System Development Guidelines

When requested to work on or create chat features (group chat, user chat) for this Java project, follow these specific instructions and consider the following prompts.

## 1. WebSocket & SockJS Configuration
- Always utilize Spring's WebSocket support configured with SockJS fallback options to ensure broad client compatibility.
- Messages should be routed using a text-based STOMP queue.
- Secure the endpoints: verify that only authenticated users can subscribe to or publish messages on destination channels.

## 2. User Management
- The user list must be reliably managed and persisted within the relational database.
- Database operations that fetch, update, or search the user list should be optimized (e.g., indexed appropriately) to reduce latency and handle growth.

## 3. Security Prompts
Before submitting any new endpoint or message handler, review these points:
- **Sanitization:** Is this payload sanitized against XSS or malicious input?
- **Authentication/Authorization:** Can an unauthorized user spoof this message or subscribe to a private queue?
- **Injection Protection:** Are database queries protected against injection? Use JPA/Hibernate parameterized queries.

## 4. Scalability & Reliability (Future-Proofing)
- **State Management:** Avoid storing critical, long-term state directly in local WebSocket session attributes if possible. 
- **Message Delivery:** Design the architecture so that it can eventually transition to a distributed environment (e.g., using a full external message broker like RabbitMQ or Redis for scaling WebSockets across multiple server nodes).
- **Error Handling:** Implement robust error handling for message delivery failures and connection drops to improve reliability for the end user.
