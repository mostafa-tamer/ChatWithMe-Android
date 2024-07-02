Android-Spring Boot chat app with Authentication, User Management, Friendship, Chats, and Groups.
Integrates REST API and Web Sockets for real-time interactions.Utilizes FCM for background push notifications.

Backend Repository: https://github.com/mostafa-tamer/ChatWithMe.git

@ Technologies
  1) Jetpack Compose
  2) Clean architecture (MVVM, UseCase, Repository)
  3) Realtime Communication (Simple Text Oriented Messaging Protocol)
  4) Rest Api (Retrofit)
  5) Dependency Injection (Dagger Hilt)
  6) Firebase Cloud Messaging
  7) Pagination
  8) Shared Preferences
  9) Notifications

@ Functional Specification
  - Login
      1) User enters his username and password
          * If it is correct it authenticates else show an error message
      2) User can sign up if no account is already registered
      3) User credentials saved if the login succeed     

  - Sign Up
      1) Enters the nickname and username and password and confirms the password
      2) Error message appears when the password in the password field does not match with password in the confirm password field
      3) User can login if he already has an account

  - Friendship hub (Chats of friends)
      1) Friends' chats are loaded with:
          * The last message
          * The time of the last message
          * A missing messages indicator
      2) Observe if a new chat is added due to an acceptance of a friend request
      3) Observe if a message was received from a friend
      4) If a friend removed the user, the chat of this friend is removed
      5) User can logout

  - Friendship chat
      1) Chat messages is loaded
      2) Observe for new message to receive
      3) Send message to the friend
      4) User can remove the friend and automatic navigate up (a confirmation dialog appears)

  - Group hub (Chats of the joined groups)
      1) Groups' chats are loaded with:
          * The last message
          * The time of the last message
          * A missing messages indicator
      2) Observe if a new group chat is added due to an addition by a friend
      3) Observe if a message was received from a group chat
      4) User can create a group

  - Group chat
      1) Chat messages is loaded (pagination is applied)
      2) Observe for new message to receive by users in the group
      3) Send message to the group chat
      4) User can leave the group (a confirmation dialog appears)
      5) User can add his friends
      
  - Friendship management
      1) User can send a friend request by writing the username of a person he wishes to be a friend and a message
          * If the user sends the friend request to himself an error message appears
          * If the user sends the friend request to someone he already friend with, and error message appears
      2) Friend friend requests are loaded
      3) User can accept or reject the friend requests

  - Notifications
      1) A message received from a private chat or a group chat
      2) User accepts the friend request
      3) User sends a friend request
      4) User added to a group
