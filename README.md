# Book Store Service 📚

**Java Fundamentals Course Project | EPAM**

A monolithic Spring Boot MVC web application simulating a bookstore. Developed as a final task to demonstrate Java and Spring skills with Thymeleaf templates. Supports role-based access for Customers and Employees.

## 🚀 Key Features

**Architecture & Security**  
- Monolithic Spring Boot MVC application.  
- Server-side rendered pages using **Thymeleaf**.  
- Role-Based Access Control via **Spring Security**.  
- Data validation and error handling.  
- DTOs for structured data transfer between layers.

**Functional Roles**  

👤 **Customer**  
- Browse books, view details, manage basket.  
- Place orders and view order history.  
- Edit personal information and profile.  

👔 **Employee**  
- CRUD operations for books.  
- Confirm or cancel customer orders.  
- Manage customer accounts (block/unblock).  
- View client lists and details.  

## 🛠 Technology Stack  
- **Backend:** Java, Spring Boot, Spring MVC, Spring Security, Spring Data JPA, MySQL, DTOs, JUnit  
- **Frontend:** Thymeleaf, HTML5, CSS3, JavaScript  
- **Testing:** Unit tests for services and controllers using JUnit  

## ⚙️ Setup & Run  
1. Clone the repository.  
2. Configure **MySQL database** connection in `application.properties`.  
3. Run the application:  
```bash
./mvnw spring-boot:run
