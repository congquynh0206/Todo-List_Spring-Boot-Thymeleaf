# Todo List Manager

A **full-stack Todo List Management application** built with **Spring Boot** and **Thymeleaf** for the backend & UI rendering.  
This project is created for learning purposes and demonstrates a complete CRUD workflow with authentication, authorization, and an admin dashboard.

## Features

-  **User Authentication & Authorization**
  - Register / Login with secure password hashing
  - Role-based access: **Admin** and **User**

- **Task Management**
  - Create, read, update, delete tasks
  - Mark tasks as **current**, **finished**, or **removed**
  - Pagination and search for tasks
  - Personal profile page with avatar upload

- **Admin Dashboard**
  - User & task management
  - Statistics & charts

- **UI**
  - Built-in **Thymeleaf** templates
  - Two UI themes (toggle switch)
- **Demo**
  - **Login Page:**

<img alt="Image Login" width="450" src="https://github.com/user-attachments/assets/6f45dbf9-1f24-4d88-9f8b-7f91b698b905" />

- **Sign-up Page:**

<img alt="Image sign-up" width="450" src="https://github.com/user-attachments/assets/b766d412-3c2c-4a40-abfc-a9a504747c63" />

- **Home Page (2 styles):**

<img alt="Image home page" width="450" src="https://github.com/user-attachments/assets/d3a5d88d-58ce-422c-8007-59f03f4bd0ee" />
<img alt="Image home page" width="450" src="https://github.com/user-attachments/assets/7766a87b-40b5-41a9-bde2-81dbd4155305" />

- **Add New Todo (2 styles):**

<img alt="Image add new todo" width="450" src="https://github.com/user-attachments/assets/6e506b4a-5918-4462-a2c2-0bff1c69260c" />
<img alt="Image add new todo" width="450" src="https://github.com/user-attachments/assets/c3213d43-145b-43a0-a400-3f2b9511b82f" />

- **Detail Todo (2 styles):**

<img alt="Image detail todo" width="450" src="https://github.com/user-attachments/assets/66afd3e3-f19f-4975-9c97-ad148728337b" />
<img alt="Image detail todo" width="450" src="https://github.com/user-attachments/assets/f630e99b-2a46-44da-957e-088cbc84975c" />

- **Personal Page (2 styles):**

<img alt="Image personal page" width="450" src="https://github.com/user-attachments/assets/bfe658f3-10f0-40fc-9b5e-78f7686a5c03" />
<img alt="Image personal page" width="450" src="https://github.com/user-attachments/assets/ac50cde9-e829-484c-9407-ee77f37c71f6" />

- **Change Information (2 styles):**

<img alt="Image change infor" width="450" src="https://github.com/user-attachments/assets/4185a587-9adb-410a-8fd1-d175f5c04545" />
<img alt="Image change infor" width="450" src="https://github.com/user-attachments/assets/87fa152d-69bd-4970-ab49-c4e5cdaaeae4" />

- **Admin Page (multiple views):**

<img alt="Image admin" width="450" src="https://github.com/user-attachments/assets/ed7e7add-cc26-4894-b65a-3eb05a7287c8" />
<img alt="Image admin" width="450" src="https://github.com/user-attachments/assets/d87c5ad3-26e0-4154-8bfb-28d38b487388" />
<img alt="Image admin" width="450" src="https://github.com/user-attachments/assets/e93ff238-2737-4da7-8166-b682585076e7" />
<img alt="Image admin" width="450" src="https://github.com/user-attachments/assets/a8188010-c8f7-4927-8dba-503cc991a7ae" />
<img alt="Image admin" width="450" src="https://github.com/user-attachments/assets/341e6815-8bc5-4a30-93d0-e745d39ade23" />


## Usage

1. Open your browser and navigate to: **http://localhost:8080**
2. **Sign up** for a new account or **log in** with existing credentials.
3. Once logged in, you can:
   - Create new tasks  
   - Edit existing tasks  
   - Search and filter tasks by status (**current / finished / removed**)  
   - Update your profile information and avatar
4. **Admin users** have access to the **Admin Dashboard** to:
   - View user statistics & charts
   - Manage all users and their tasks

## Security

- All passwords are **hashed** before being stored in the database.  
- The application uses **JWT (JSON Web Tokens)** for secure authentication and session management.  
- **Role-based access control**:
  - `USER` role: basic access to personal tasks and profile
  - `ADMIN` role: extended access to dashboard, user & task management
- Sensitive operations (like user management and admin stats) are **restricted to Admin accounts**.

## Installation & Setup

### 1. Prerequisites
- **Java 17+**
- **Maven** (IDE như IntelliJ/Eclipse thường đã tích hợp)
- **SQL Server 2019+**
- (Optional) **Node.js 18+** nếu muốn phát triển ReactJS frontend

### 2. Clone the Repository
git clone https://github.com/congquynh0206/Todo-List_Spring-Boot-Thymeleaf.git
cd Todo-List_Spring-Boot-Thymeleaf

## Author
- Name: Nguyễn Công Quỳnh
- Email: congquynh0206@gmail.com

## License
- The project is built for personal study purposes.
- Currently no license is applied.
- If you want to reuse the source code, please contact the author.





  



