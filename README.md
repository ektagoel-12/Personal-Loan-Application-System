# Personal Loan Application System

A full-stack web application to manage **personal loan applications** with role-based access, automated repayment schedule generation, and support ticket management.  

This project simulates real-world loan management systems with features like **credit score validation**, **income checks**, **max loan constraints**, and **role-based workflows** for **Users** and **Admins**.  

---

## Features

### User (Viewer Role)
- Register and login with JWT authentication
- Apply for new loans (based on eligibility: credit score, income, max 3 active loans)
- View **loan application status**
- Check **repayment schedule** with EMI breakdown
- Raise **support tickets** and track responses

### Admin (Editor Role)
- Review pending loan applications
- Approve/Reject loans with remarks
- Manage repayment schedules
- Respond to support tickets
- Monitor overall system

---

## Tech Stack

- **Frontend**: Vue.js, Vue Router, Vuex, TailwindCSS  
- **Backend**: Java, Spring Boot, Spring Data JPA, JWT Auth  
- **Database**: PostgreSQL  
- **Build Tools**: Maven (Backend), Vite (Frontend)  
- **Version Control**: Git, GitHub  
- **Testing**: JUnit, Mockito, Vue Test Utils, Vitest

---
```
Project Structure

PERSONAL-LOAN-APPLICATION-SYSTEM
│
├── backend
│   └── loan-application
│       ├── .mvn
│       ├── src
│       ├── target
│       ├── .gitattributes
│       ├── .gitignore
│       ├── mvnw
│       ├── mvnw.cmd
│       ├── pom.xml
│
├── classDiagram.png
├── ERDiagram.png
│
├── frontend
│   ├── .vscode
│   ├── node_modules
│   ├── public
│   └── src
│       ├── assets
│       ├── components
│       ├── router
│       ├── store
│       ├── utils
│       ├── views
│       ├── App.vue
│       ├── main.js
│
│   ├── .gitignore
│   ├── index.html
│   ├── jsconfig.json
│   ├── package-lock.json
│   ├── package.json
│   ├── postcss.config.js
│   ├── tailwind.config.js
│   ├── vite.config.js
│   └── vitest.config.js

```

---

## Database Schema

- **Users** → User registration & roles (USER/ADMIN)  
- **LoanApplications** → Loan details & statuses  
- **RepaymentSchedules** → EMI schedules  
- **Tickets** → Support system  

---

## Testing

- **Backend**: JUnit + Mockito  
- **Frontend**: Vitest + Vue Test Utils  
- Coverage includes **controllers, services, Vue components, and store logic**.

---

## Setup & Installation

### Backend
```bash
cd backend/loan-application
mvn clean install
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

### Database
```
CREATE DATABASE loan_application;
```

---


## Team - Zeta Pioneers

- Ekta Goel [Team Lead]
- Aom Vivek Wankhede
- Maram Krishna Koushik Reddy
- Aanchal Maheshwari
- Premansu Chakraborty

