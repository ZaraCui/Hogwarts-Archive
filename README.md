# 🪄 Hogwarts Archive

An object-oriented Java application developed for the **INFO1113 (Object-Oriented Programming)** course at the University of Sydney.

This program manages Hogwarts’ magical archive of students and spellbooks. It allows adding, listing, renting, and returning spellbooks while maintaining accurate rental histories and enforcing strict data consistency and formatting rules.

---

## 📘 Overview

The **Hogwarts Archive** system simulates a record-keeping program that supports command-line interaction.  
It manages:
- Student registration and rental history tracking.
- Spellbook cataloguing, availability, and type/inventor classification.
- File-based input/output for loading and saving spellbook collections.

Your program reads from **standard input** and writes to **standard output**, matching exact formatting required by the course’s automated testing.

---

## 🧩 Object-Oriented Design

The system is built around three main classes:

| Class | Responsibility |
|--------|----------------|
| **Archive** | Acts as the controller; parses commands, coordinates actions between students and spellbooks. |
| **Student** | Stores student data, current rentals, and past rental history. |
| **SpellBook** | Represents an individual spellbook with metadata (serial number, title, inventor, type) and availability. |

Each class follows the **Single Responsibility Principle**, ensuring clear separation of logic and easy extensibility.  
For example, adding new commands like `LIST POPULAR` would only require modifying `Archive`.

---

## ⚙️ Key Features

- **ADD STUDENT** – Register new students with auto-assigned IDs starting from 100000.  
- **ADD SPELLBOOK / ADD COLLECTION** – Load single or multiple spellbooks from CSV files.  
- **LIST / TYPE / INVENTOR** – Display spellbooks filtered or grouped by category.  
- **RENT / RELINQUISH / RELINQUISH ALL** – Manage borrowing and returning spellbooks.  
- **SAVE COLLECTION** – Export the current archive state to CSV.  
- **COMMON** – Find spellbooks shared in multiple students’ rental histories.  
- **HISTORY commands** – Output chronological records of rentals for both students and spellbooks.  

All commands are **case-insensitive** and formatted according to the official INFO1113 specification.

---

## 🧠 Design Highlights

- **Encapsulation:** Internal data (e.g., rental history) is private and accessed through dedicated methods.  
- **Abstraction:** Each command is handled via small, cohesive methods like `doRent()` or `doRelinquish()`.  
- **Formatting Consistency:** The `outBlock()` method ensures every output follows `"user: "` prefixes and exact spacing rules for Ed testing.  
- **Defensive Programming:** Error cases (e.g., invalid IDs, duplicates) are handled gracefully without crashes.  
- **Extensibility:** Designed for easy extension with future inheritance or additional features.

---

## 🧪 Testing

Automated test cases are organized by command (e.g., `/tests/add_student/`, `/tests/list_all/`), each containing paired `.in` and `.out` files.  
You can run tests manually via:

```bash
javac *.java
java Archive < tests/add_student/input.in > output.txt
diff output.txt tests/add_student/expected.out
````

---

## 📁 Project Structure

```
├── Archive.java           # Main driver class (command parser and controller)
├── Student.java           # Handles student data and rental management
├── SpellBook.java         # Represents spellbook data and availability
├── tests/                 # Organized input/output test directories
├── README.md              # Documentation file
└── HogwartsArchive_DesignReport_JCui.docx  # Design explanation and UML diagram
```

---

## 🏗️ How to Run

Compile all source files:

```bash
javac *.java
```

Run the archive system with standard input:

```bash
java Archive
```

Example session:

```
user: ADD STUDENT Hermione Granger
Success.

user: ADD COLLECTION spellbooks.csv
17 spellbooks successfully added.

user: LIST TYPES
Charm
Curse
Healing
...
```

---

## 👩‍💻 Author

**Jiajie (Zara) Cui**
Bachelor of Advanced Computing, University of Sydney
🗓️ Semester 2, 2025

---

## 📚 References

* [INFO1113 Assignment Specification](https://edstem.org/au/courses/25103/lessons/91887/slides/628470)
* Google Java Style Guide
* HogwartsArchive_DesignReport_JCui.docx (internal design documentation)

---

```
