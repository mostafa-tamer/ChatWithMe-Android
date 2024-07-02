# Chat With Me Android-Spring Boot Chat Application

This project is an Android-Spring Boot chat application that provides authentication, user management, friendship, chats, and group functionalities. It integrates REST API and Web Sockets for real-time interactions and utilizes FCM for background push notifications.

## Backend Repository

[ChatWithMe Backend Repository](https://github.com/mostafa-tamer/ChatWithMe.git)

## Technologies

- **Jetpack Compose**
- **Clean Architecture (MVVM, UseCase, Repository)**
- **Real-time Communication (Simple Text Oriented Messaging Protocol)**
- **REST API (Retrofit)**
- **Dependency Injection (Dagger Hilt)**
- **Firebase Cloud Messaging**
- **Pagination**
- **Shared Preferences**
- **Notifications**

## Functional Specification

### Login

1. User enters username and password:
   - If correct, it authenticates; otherwise, an error message is shown.
2. User can sign up if no account is already registered.
3. User credentials are saved if the login succeeds.

### Sign Up

1. User enters nickname, username, password, and confirms the password.
2. Error message appears if the password fields do not match.
3. User can log in if they already have an account.

### Friendship Hub (Chats of Friends)

1. Friends' chats are loaded with:
   - The last message
   - The time of the last message
   - A missing messages indicator
2. Observes new chats added due to friend request acceptance.
3. Observes messages received from friends.
4. Removes chat if a friend removes the user.
5. User can log out.

### Friendship Chat

1. Chat messages are loaded.
2. Observes for new messages received.
3. User can send messages to friends.
4. User can remove friends, with a confirmation dialog, and automatic navigation up.

### Group Hub (Chats of Joined Groups)

1. Groups' chats are loaded with:
   - The last message
   - The time of the last message
   - A missing messages indicator
2. Observes new group chats added by friends.
3. Observes messages received from group chats.
4. User can create groups.

### Group Chat

1. Chat messages are loaded (pagination is applied).
2. Observes for new messages received by group users.
3. User can send messages to group chats.
4. User can leave groups with a confirmation dialog.
5. User can add friends to groups.

### Friendship Management

1. User can send friend requests by writing the username and a message:
   - Error message if the user sends the request to themselves.
   - Error message if the user sends the request to someone already a friend.
2. Friend requests are loaded.
3. User can accept or reject friend requests.

### Notifications

1. Message received from private or group chats.
2. User accepts a friend request.
3. User sends a friend request.
4. User added to a group.
