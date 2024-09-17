# Customer Management CRUD Application

## Project Overview

This is a Customer Management CRUD (Create, Read, Update, Delete) application built using Spring Boot for the backend, MySQL for database management, and plain HTML/CSS/JavaScript for the frontend. It allows users to manage customer information efficiently, with features including registration, authentication.

## Features

- **User Registration and Authentication:** Users can register and authenticate using JWT-based authentication.
- **Customer Management:** Create, read, update, and delete customer records.
- **Customer Synchronization:** Synchronize customer data with an external API.

## Technologies Used

- **Backend:** Spring Boot
- **Database:** MySQL
- **Frontend:** HTML, CSS, JavaScript
- **Authentication:** JWT (JSON Web Tokens)
- **External API Integration:** Fetch data from an external API and synchronize with the local database.

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- MySQL
- Node.js (if building frontend separately)

### Setting Up the Backend

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/yourusername/your-repo.git
   cd your-repo

2. **Configure the Application:**

    Open src/main/resources/application.properties.
    Set up the database connection properties:

    spring.datasource.url=jdbc:mysql://localhost:3306/yourdatabase
    spring.datasource.username=yourusername
    spring.datasource.password=yourpassword

3. **Build and Run the Application:**

    mvn clean install
    mvn spring-boot:run

4. **Access the Application:**    

    Open a browser and go to http://localhost:8088.


### API Endpoints

## Authentication:

- POST /api/auth: Authenticate a user and get a JWT token.
- POST /api/register: Register a new user.

## Customer Management:

- POST /customers/create: Create a new customer.
- PUT /customers/update/{id}: Update an existing customer.
- GET /customers/get: Get a list of customers.
- GET /customers/{id}: Get a customer by ID.
- DELETE /customers/{id}: Delete a customer.

