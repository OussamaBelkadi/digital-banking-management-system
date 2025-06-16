# 🏦 Digital Banking Management System

## 📋 Description du Projet

Application complète de gestion bancaire digitale développée avec Spring Boot (Backend) et Angular (Frontend). Le système permet de gérer les clients, comptes bancaires, et opérations financières avec un système d'authentification sécurisé basé sur JWT.

## ⚡ Fonctionnalités Principales

### 🎯 Gestion des Comptes
- ✅ Création de comptes courants et comptes épargne
- ✅ Consultation des soldes et historiques
- ✅ Opérations de débit et crédit
- ✅ Virements entre comptes
- ✅ Calcul automatique des découverts et intérêts

### 👥 Gestion des Clients
- ✅ Création, modification, suppression de clients
- ✅ Recherche avancée de clients
- ✅ Consultation des comptes par client
- ✅ Historique des opérations par client

### 📊 Dashboard et Statistiques
- ✅ Graphiques des transactions (Chart.js)
- ✅ Statistiques financières en temps réel
- ✅ Rapports mensuels et annuels
- ✅ Indicateurs de performance

### 🔐 Sécurité et Authentification
- ✅ Authentification JWT (JSON Web Token)
- ✅ Autorisation basée sur les rôles (ADMIN, EMPLOYEE)
- ✅ Audit trail des opérations
- ✅ Gestion des mots de passe sécurisée

## 🛠️ Technologies Utilisées

### Backend
- **Java 17** - Langage de programmation
- **Spring Boot 3.2** - Framework principal
- **Spring Data JPA** - Persistance des données
- **Spring Security** - Sécurité et authentification
- **MySQL/H2** - Base de données
- **JWT** - Authentification stateless
- **Swagger/OpenAPI** - Documentation API


### DevOps & Outils
- **Maven** - Gestion des dépendances
- **Docker** - Containerisation
- **Git** - Contrôle de version

## 🚀 Installation et Lancement

### Prérequis
- Java 17+
- Node.js 16+
- MySQL 8.0+ (optionnel, H2 inclus pour dev)
- Maven 3.6+

### Backend (Port 8080)
```bash
# Cloner le repository
git clone https://github.com/votre-username/digital-banking-management-system.git
cd digital-banking-management-system

# Lancer le backend
cd backend
mvn clean install
mvn spring-boot:run
