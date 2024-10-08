# [Forum Project]()

**Description**: The Forum Project is an online platform where users can create and discuss various topics.
It supports features such as user authentication, real-time communication using **WebSockets**, and the ability to create topics with banners and attachments.
Additionally, the application utilizes **SMTP** for sending emails, providing users with essential email verification.

![Forum Screenshot](screenshot.png)

---

## <span style="color: #6b5b9a;">Frontend Repository</span>
Find the frontend part of this project [here](https://github.com/Vezem1r/SpringForumFrontend).

---

## <span style="color: #6b5b9a;">Roles and Permissions</span>

This forum application includes various roles, each with different permissions:

- **Admin** (Not implemented yet)
  - 🛠️ Full access to the forum.
  - 👥 Can manage users, topics, comments, categories, and tags.

- **User**
  - 📝 Can create topics and comments with multipart attachments.
  - 💬 Can reply to comments.
  - 👍👎 Can upvote and downvote topics and comments.
  - 👤 Has a personal profile displaying their personal information (email is hidden to other users), topics, and overall rating.
  - ✏️ Can change their username, avatar, and password with email verification.
  - 📩 Can send messages to different users and receive messages.
  - 🔔 Has personal notifications for:
    - 🆕 New comments on their topics.
    - 📊 Changes in the rating of their topics or comments.
    - ✉️ Personal messages.

- **Guest**
  - 👁️ Can view users' profiles, topics, and comments.
  - 🔍 Can search for topics based on various criteria.
  - 🆕 Can log in or register with email verification.
  - 🚫 No permissions to interact with posts or comments.

---

## <span style="color: #6b5b9a;">Technologies Used</span>

This project is built with the following technologies:

- **Backend**:
  - ☕ Java 21
  - 🚀 Spring Boot 3.3.4 (Data JPA, Security, Web, Mail, WebSocket)
  - 🗄️ MariaDB (as the database)
  - 🔑 JWT for authentication
  - 🛠️ Lombok (for reducing boilerplate code)

- **Frontend**:
  - ⚛️ React
  - 🎨 Tailwind CSS
  - 🌐 WebSocket
  - 🍞 React Toastify
  - 📦 React Icons

---

## <span style="color: #6b5b9a;">How to Build the Project</span>

To set up and run the backend project locally, follow these steps:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Vezem1r/SpringForum.git
