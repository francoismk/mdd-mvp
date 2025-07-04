# MDD MVP - Monde de dev

MVP (Minimum Viable Product) of the MDD application: managing an Angular front-end, a Spring Boot back-end, and a MongoDB database, orchestrated with Docker.

## 🏗️ Architecture

- **Frontend**: Angular 18.2.9 (TypeScript)
- **Backend**: Spring Boot (Java)
- **Database**: MongoDB
- **Orchestration**: Docker Compose

## 📋 Prerequisites

Before you start, make sure you have installed:

- [Node.js](https://nodejs.org/) (version 18 or higher)
- [Angular CLI](https://angular.dev/tools/cli): `npm install -g @angular/cli`
- [Java JDK](https://www.oracle.com/java/technologies/downloads/) (version 11 or higher)
- [Maven](https://maven.apache.org/download.cgi) (version 3.6 or higher)
- [Docker](https://www.docker.com/get-started) and [Docker Compose](https://docs.docker.com/compose/install/)

## 🚀 Installation and Launch

### Option 1: With Docker (Recommended)

1.  **Clone the project**
    ```bash
    git clone <repo-url>
    cd mdd-mvp
    ```

2.  **Launch the full application**
    ```bash
    docker-compose up --build
    ```

3.  **Access the application**
    -   Frontend: http://localhost
    -   Backend API: http://localhost:8080
    -   MongoDB: localhost:27017

### Option 2: Local Development

#### Backend (Spring Boot)

1.  **Navigate to the backend folder**
    ```bash
    cd mdd-backend
    ```

2.  **Install dependencies and launch**
    ```bash
    ./mvnw clean install
    ./mvnw spring-boot:run
    ```

3.  **The API will be available at**: http://localhost:8080

#### Frontend (Angular)

1.  **Navigate to the frontend folder**
    ```bash
    cd mdd-frontend
    ```

2.  **Install dependencies**
    ```bash
    npm install
    ```

3.  **Start the development server**
    ```bash
    ng serve
    ```

4.  **The application will be available at**: http://localhost:4200

#### Database

Make sure you have MongoDB running locally or use Docker:

```bash
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

## 🛠️ Useful Commands

### Frontend

```bash
cd mdd-frontend

# Development server
ng serve

# Production build
ng build

# Unit tests
ng test

# E2E tests
ng e2e

# Generate a new component
ng generate component component-name
```

### Backend

```bash
cd mdd-backend

# Run the application
./mvnw spring-boot:run

# Build the project
./mvnw clean package

# Tests
./mvnw test

# Clean the build
./mvnw clean
```

### Docker

```bash
# Start all services
docker-compose up

# Start in detached mode
docker-compose up -d

# Rebuild and start
docker-compose up --build

# Stop all services
docker-compose down

# View logs
docker-compose logs

# View logs for a specific service
docker-compose logs frontend
docker-compose logs backend
```

## 📁 Project Structure

```
mdd-mvp/
├── README.md                 # This file
├── docker-compose.yml        # Docker configuration
├── mdd-backend/              # Spring Boot API
│   ├── src/
│   │   └── main/java/com/example/mdd_backend/
│   │       └── MddBackendApplication.java
│   ├── pom.xml               # Maven dependencies
│   └── dockerfile            # Backend Docker image
├── mdd-frontend/             # Angular application
│   ├── src/
│   │   ├── app/
│   │   │   ├── features/auth/components/
│   │   │   │   └── register-from.component.ts
│   │   │   └── shared/components/navbar/
│   │   │       └── navbar.component.ts
│   │   └── index.html
│   ├── angular.json          # Angular configuration
│   └── package.json          # npm dependencies
└── .idea/                    # IntelliJ IDEA configuration
```

## 🔧 Configuration

### Environment Variables

Create a `.env` file at the root of the project to customize the configuration:

```env
# Database
MONGODB_URI=mongodb://localhost:27017/mdd
MONGODB_DATABASE=mdd

# Backend
BACKEND_PORT=8080
JWT_SECRET=your-secret-key

# Frontend
FRONTEND_PORT=4200
```

## 📝 Features

-   🔐 **Authentication**: User registration and login
-   📱 **Responsive**: Mobile and desktop friendly interface
-   🔄 **Navigation**: Navigation system with navbar
-   ✅ **Validation**: Client and server-side form validation