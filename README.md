# 📂 Favorite File Manager

## 📝 Project Overview
This project is a **Favorite File Manager** tool that allows users to:
- Mark files as favorites  
- Update favorite files  
- List all favorite files  
- Search files by **author, title, or tag**  
- Display file properties  

The application provides a **graphical user interface (JavaFX)** and uses an **Oracle relational database** (hosted in Docker) to store file metadata.

---
## 👥 Authors
- **Mohamed Khalil Nafati**  
- [@MoKhalilNafati](https://github.com/MoKhalilNafati)

- **Ahmed Khatmi**  
- [@AhMeD-KhaTmi](https://github.com/AhMeD-KhaTmi)
---

## 🛠️ Technical Environment
- **Language**: Java (JavaFX for GUI)  
- **Database**: Oracle XE (Docker image `gvenzl/oracle-xe:21-slim`)  
- **Database Tool**: SQL Developer  
- **Architecture**: MVC (Model - View - Controller)  

### 📊 Database Schema
- **FICHIERS**: stores file metadata (ID, path, title, author, summary, comments, added date)  
- **TAGS**: stores tags (ID, name)  
- **FICHIER_TAGS**: many-to-many relation between files and tags (file_id, tag_id)  

---

## 💻 Development Approach
1. Designed the database inside a Docker container  
2. Implemented file and tag management methods using **JDBC**  
3. Built the user interface with **JavaFX**  
4. Implemented **CRUD operations** (Create, Read, Update, Delete)  
5. Added search functionality (by author, title, tag)  
6. Added a feature to display file properties  

---

## ✨ Implemented Features
- ✅ **Add favorite files** with metadata (author, title, tags, summary, comments)  
- ✅ **Update & delete favorite files**  
- ✅ **List all favorite files** in a table view  
- ✅ **Search files** by author, title, or tag  
- ✅ **Display properties**: total number of files, list of authors, tags, files per tag  

---

## 🎨 User Interface
The JavaFX interface includes:  
- **Form**: author, title, tags, summary, comments  
- **Buttons**: Add, Update, Delete, Search (by Title, Tag, Author), Show Properties  
- **Table**: displays favorite files with ID, Title, Author, Path  
- **Properties window**: shows statistics (total files, authors, tags, files per tag)  

---

## 🛠️ Installation & Execution

### 1️⃣ Prerequisites
- Eclipse IDE
- Java JDK 11 or higher  
- Docker  
- SQL Developer (or any Oracle client)  
- Git  

---

### 2️⃣ Clone the Project
bash:
git clone https://github.com/username/java-file-manager.git
cd java-file-manager

### 3️⃣ Run Oracle Database with Docker
docker run -d --name filemanager-db \
  -p 1521:1521 -e ORACLE_PASSWORD=admin \
  gvenzl/oracle-xe:21-slim

  <img width="1019" height="576" alt="image" src="https://github.com/user-attachments/assets/50d4086e-e994-4174-bec9-1a6fc2e7b90f" />


### 4️⃣ Configure Database

Connection name: FILEMGR_DOCKER

Database: XEPDB1

Use SQL Developer to test the connection

<img width="1023" height="605" alt="image" src="https://github.com/user-attachments/assets/2885cff1-950a-45ec-b461-5113896a03bd" />


5️⃣ Run the Java Application

Open the project in your IDE (preferred Eclipse)

Compile and run the main class FileManagerView.java

The GUI will open, ready to manage your favorite files
