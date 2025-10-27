# INFO1113 - Hogwarts Archive Test Suite

This directory contains all automated tests for the **Hogwarts Archive** program.  
Each command from the specification has its own subfolder with paired `.in` and `.out` files.  
All tests are self-contained and can be run independently.

---

## Directory Overview

### Add & Import
- **add_collection/** - Tests adding spellbooks from CSV files (duplicates, dirty data, missing files)
- **add_spellbook/** - Tests adding single spellbooks (missing or already present)
- **add_student/** - Tests adding students (duplicate names, ID assignment)

### Core Commands
- **commands/** - Tests the `COMMANDS` help output
- **common/** - Finds spellbooks shared among multiple students
- **save_collection/** - Tests saving data to CSV (empty or populated)

### Listing & Queries
- **list_all/** - Tests short and long formats of spellbook listings
- **list_available/** - Filters available books after rentals
- **list_inventors/** - Lists unique inventors (case-insensitive)
- **list_types/** - Lists spellbook types (case-insensitive)
- **number_copies/** - Counts copies grouped by title and inventor
- **type/** - Filters by type keyword
- **inventor/** - Filters by inventor substring

### Students & Rentals
- **rent/** - Tests renting (conflicts, missing student)
- **relinquish/** - Tests single return, invalid cases
- **relinquish_all/** - Tests bulk returns and empty system
- **student/** - Shows individual student details
- **student_spellbooks/** - Lists currently rented books per student
- **student_history/** - Retrieves each student's full history
- **spellbook_history/** - Tracks each spellbook's rental history
- **spellbook/** - Queries a specific spellbook (short/long format)

### Miscellaneous
- **data/** - CSV files used by collection tests
- **exit/** - Ensures proper program termination
- **demo_full_showcase.in/out** - Integration test covering all major features

---

## Running Tests

```bash
javac Archive.java Student.java SpellBook.java
java Archive < tests/list_all/list_all_after_import.in
diff -u tests/list_all/list_all_after_import.out output.txt



Generated with the help of ChatGPT5.
