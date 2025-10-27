import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Hogwarts Archive implementation.
 * This program manages students and spellbooks via command-line interaction.
 * It supports adding, listing, renting, and saving records.
 */
public class Archive {

    /**
     * Store students and spellbooks
     * The output order matches the order items were added.
     * Each Student/SpellBook is uniquely identified using LinkedHashMap.
     */
    private static Map<Integer, Student> students = new LinkedHashMap<>();
    private static Map<Integer, SpellBook> spellbooks = new LinkedHashMap<>();
    private static int nextStudentId = 100000; // student IDs start from 100000

    // Output helper function: prefix only first line in a block with "user: "
    private static boolean printedAnyBlock = false;

    /**
     * Prints formatted output blocks.
     * The first line is prefixed with "user: ", and one blank line separates
     * blocks.
     */
    private static void outBlock(String s) {
        if (printedAnyBlock) {
            System.out.println(); // one blank line between blocks
        }

        String[] lines = s.split("\\R", -1); // matches all types of line breaks, -1 flag prevents trimming
        if (lines.length == 0) {
            System.out.println("user: ");
        } else {
            // First line prefixed with "user: "
            System.out.println("user: " + lines[0]);
            // Remaining lines printed normally
            for (int i = 1; i < lines.length; i++) {
                System.out.println(lines[i]);
            }
        }
        printedAnyBlock = true;
    }

    public static void main(String[] args) {
        // reset state for each run
        students = new LinkedHashMap<>();
        spellbooks = new LinkedHashMap<>();
        nextStudentId = 100000;
        printedAnyBlock = false;

        // Read commands from standard input line by line
        // Each line represents one command
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim(); // Remove extra spaces
                if (line.isEmpty()) { // Skip empty lines
                    continue;
                }

                String upper = line.toUpperCase(); // For case-insensitive command matching

                // Exit the program
                if (upper.equals("EXIT")) {
                    outBlock("Ending Archive process.");
                    return;

                    // Output help string, show available commands
                } else if (upper.equals("COMMANDS")) {
                    printHelp();

                    // Add a new student
                } else if (upper.startsWith("ADD STUDENT ")) {
                    String name = line.substring("ADD STUDENT ".length()).trim();
                    doAddStudent(name);

                    // Add a new spellbook
                } else if (upper.startsWith("ADD SPELLBOOK ")) {
                    String rest = line.substring("ADD SPELLBOOK ".length()).trim();
                    String[] p = rest.split("\\s+"); // Split by whitespace
                    if (p.length == 2) {
                        String filename = p[0]; // Filename is first part
                        try {
                            int serial = Integer.parseInt(p[1]); // Parse serial number
                            doAddSpellbook(filename, serial); // Add the spellbook
                        } catch (NumberFormatException ignored) {
                            // Ignore invalid serial number
                        }
                    }

                    // Add a collection of spellbooks
                } else if (upper.startsWith("ADD COLLECTION ")) {
                    String filename = line.substring("ADD COLLECTION ".length()).trim();
                    if (!filename.isEmpty()) { // Check if filename is not empty
                        doAddCollection(filename); // Add the collection
                    }

                    // Save the system to a csv file
                } else if (upper.startsWith("SAVE COLLECTION ")) {
                    String filename = line.substring("SAVE COLLECTION ".length()).trim();
                    if (!filename.isEmpty()) {
                        doSaveCollection(filename); // Save the collection
                    }

                    // Output either the short or long string for all spellbooks
                } else if (upper.startsWith("LIST ALL")) {
                    boolean longForm = upper.endsWith(" LONG");
                    doListAll(longForm);

                    // Output either the short or long string for all available spellbooks
                } else if (upper.startsWith("LIST AVAILABLE")) {
                    boolean longForm = upper.endsWith(" LONG");
                    doListAvailable(longForm);

                    // Output the name of every type in the system
                } else if (upper.equals("LIST TYPES")) {
                    doListTypes();

                    // Output the name of every inventor in the system
                } else if (upper.equals("LIST INVENTORS")) {
                    doListInventors();

                    // Output the number of copies of each spellbook
                } else if (upper.equals("NUMBER COPIES")) {
                    doNumberCopies();

                    // Output the short string of every spellbook with the specified type
                } else if (upper.startsWith("TYPE ")) {
                    String type = line.substring("TYPE ".length()).trim();
                    doTypeQuery(type);

                    // Output the short string of every spellbook by the specified inventor
                } else if (upper.startsWith("INVENTOR ")) {
                    String inventor = line.substring("INVENTOR ".length()).trim();
                    doInventorQuery(inventor);

                    // Output the rental history of the specified spellbook
                } else if (upper.startsWith("SPELLBOOK HISTORY ")) {
                    String rest = line.substring("SPELLBOOK HISTORY ".length()).trim();
                    try {
                        int serial = Integer.parseInt(rest); // Parse serial number
                        doSpellbookHistory(serial); // Show rental history
                    } catch (NumberFormatException ignored) {
                    }

                    // Output either the short or long string for the specified spellbook
                } else if (upper.startsWith("SPELLBOOK ")) {
                    String rest = line.substring("SPELLBOOK ".length()).trim();
                    String[] p = rest.split("\\s+"); // Split by whitespace
                    if (p.length >= 1) {
                        try {
                            int serial = Integer.parseInt(p[0]); // Parse serial number
                            boolean longForm = (p.length >= 2 && p[1].equalsIgnoreCase("LONG"));
                            doSpellbook(serial, longForm); // Show spellbook info
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    // Output the rental history of the specified student
                } else if (upper.startsWith("STUDENT HISTORY ")) {
                    String rest = line.substring("STUDENT HISTORY ".length()).trim();
                    try {
                        int id = Integer.parseInt(rest);
                        doStudentHistory(id); // Show rental history
                    } catch (NumberFormatException ignored) {
                    }

                    // Output the spellbooks currently rented by the specified student
                } else if (upper.startsWith("STUDENT SPELLBOOKS ")) {
                    String rest = line.substring("STUDENT SPELLBOOKS ".length()).trim();
                    try {
                        int id = Integer.parseInt(rest);
                        doStudentSpellbooks(id); // Show current rentals
                    } catch (NumberFormatException ignored) {
                    }

                    // Output the information of the specified student
                } else if (upper.startsWith("STUDENT ")) {
                    String rest = line.substring("STUDENT ".length()).trim();
                    try {
                        int id = Integer.parseInt(rest);
                        doStudent(id); // Show student info
                    } catch (NumberFormatException ignored) {
                    }

                    // Loan out the specified spellbook to the given student
                } else if (upper.startsWith("RENT ")) {
                    String rest = line.substring("RENT ".length()).trim();
                    String[] p = rest.split("\\s+"); // Split by whitespace
                    if (p.length == 2) {
                        try {
                            int studentId = Integer.parseInt(p[0]); // Parse student ID
                            int serial = Integer.parseInt(p[1]); // Parse serial number
                            doRent(studentId, serial); // Rent the spellbook
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    // Return all spellbooks rented by the specified student
                } else if (upper.startsWith("RELINQUISH ALL ")) {
                    String rest = line.substring("RELINQUISH ALL ".length()).trim();
                    try {
                        int studentId = Integer.parseInt(rest); // Parse student ID
                        doRelinquishAll(studentId); // Return all spellbooks for the student
                    } catch (NumberFormatException ignored) {
                    }

                    // Return the specified spellbook from the student
                } else if (upper.startsWith("RELINQUISH ")) {
                    String rest = line.substring("RELINQUISH ".length()).trim();
                    String[] p = rest.split("\\s+"); // Split by whitespace
                    if (p.length == 2) {
                        try {
                            int studentId = Integer.parseInt(p[0]); // Parse student ID
                            int serial = Integer.parseInt(p[1]); // Parse serial number
                            doRelinquish(studentId, serial); // Return the spellbook
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    // Output the common spellbooks in students’ history
                } else if (upper.startsWith("COMMON ")) {
                    String rest = line.substring("COMMON ".length()).trim();
                    String[] p = rest.split("\\s+"); // Split by whitespace
                    if (p.length >= 2) {
                        List<Integer> ids = new ArrayList<>(); // To store parsed student IDs
                        Set<Integer> seen = new HashSet<>(); // To check for duplicates
                        boolean dup = false;
                        boolean parseFail = false;

                        for (String s : p) {
                            try {
                                int id = Integer.parseInt(s);
                                if (!seen.add(id)) {
                                    dup = true;
                                    break;
                                }
                                ids.add(id);
                            } catch (NumberFormatException ex) {
                                parseFail = true;
                            }
                        }

                        if (dup) {
                            outBlock("Duplicate students provided.");
                        } else if (parseFail) {
                            outBlock("No such student in system.");
                        } else {
                            doCommon(ids);
                        }
                    }
                } else {
                    // ignore unknown command
                }
            }
        } catch (IOException ignored) {
            // Ignore input/output errors
            // output
        }
    }

    // ---------------- Commands ----------------

    // Prints the help message listing all available commands.
    private static void printHelp() {
        // Build and output the help string for all commands.
        String help = String.join("\n",
                "EXIT ends the archive process",
                "COMMANDS outputs this help string",
                "",
                "LIST ALL [LONG] outputs either the short or long string for all spellbooks",
                "LIST AVAILABLE [LONG] outputs either the short or long string for all available spellbooks",
                "NUMBER COPIES outputs the number of copies of each spellbook",
                "LIST TYPES outputs the name of every type in the system",
                "LIST INVENTORS outputs the name of every inventor in the system",
                "",
                "TYPE <type> outputs the short string of every spellbook with the specified type",
                "INVENTOR <inventor> outputs the short string of every spellbook by the specified inventor",
                "",
                "SPELLBOOK <serialNumber> [LONG] outputs either the short or long string for the specified spellbook",
                "SPELLBOOK HISTORY <serialNumber> outputs the rental history of the specified spellbook",
                "",
                "STUDENT <studentNumber> outputs the information of the specified student",
                "STUDENT SPELLBOOKS <studentNumber> outputs the spellbooks currently rented by the specified student",
                "STUDENT HISTORY <studentNumber> outputs the rental history of the specified student",
                "",
                "RENT <studentNumber> <serialNumber> loans out the specified spellbook to the given student",
                "RELINQUISH <studentNumber> <serialNumber> returns the specified spellbook from the student",
                "RELINQUISH ALL <studentNumber> returns all spellbooks rented by the specified student",
                "",
                "ADD STUDENT <name> adds a student to the system",
                "ADD SPELLBOOK <filename> <serialNumber> adds a spellbook to the system",
                "",
                "ADD COLLECTION <filename> adds a collection of spellbooks to the system",
                "SAVE COLLECTION <filename> saves the system to a csv file",
                "",
                "COMMON <studentNumber1> <studentNumber2> ... outputs the common spellbooks in students' history");
        outBlock(help);
    }

    // Adds a new student to the system.
    private static void doAddStudent(String name) {
        // Ignore if name is empty.
        if (name.isEmpty()) {
            return;
        }
        // Create and add student, increment ID.
        Student s = new Student(nextStudentId++, name);
        students.put(s.getId(), s); // Add student to map
        outBlock("Success.");
    }

    // Adds a single spellbook from a file by serial number.
    private static void doAddSpellbook(String filename, int serial) {
        // Check for duplicate spellbook.
        if (spellbooks.containsKey(serial)) { // Duplicate serial
            outBlock("Spellbook already exists in system.");
            return;
        }
        try (BufferedReader r = new BufferedReader(new FileReader(filename))) { // Open file
            String line; // Current line
            boolean found = false; // Whether spellbook found
            // Read file line by line, look for matching serial.
            while ((line = r.readLine()) != null) {
                String[] parts = line.split(",", -1); // Split by commas, keep empty
                if (parts.length != 4) {// Invalid line
                    continue;
                }
                int fileSerial; // Serial number from file
                try {
                    fileSerial = Integer.parseInt(parts[0].trim()); // Parse serial number
                } catch (NumberFormatException e) {
                    continue; // Skip invalid lines.
                }
                if (fileSerial == serial) {
                    // If found, add spellbook to system.
                    String title = parts[1].trim();
                    String inventor = parts[2].trim();
                    String type = parts[3].trim();
                    if (title.isEmpty() || inventor.isEmpty() || type.isEmpty()) { // Invalid data
                        outBlock("No such spellbook in file.");
                        return;
                    }
                    SpellBook b = new SpellBook(fileSerial, title, inventor, type);
                    spellbooks.put(fileSerial, b); // Add to map
                    outBlock("Successfully added: " + b.shortFormat() + ".");
                    found = true; // Mark as found
                    break;
                }
            }
            // If not found, output error.
            if (!found)
                outBlock("No such spellbook in file.");
        } catch (IOException e) { // File not found or read error
            outBlock("No such file.");
        }
    }

    // Adds a collection of spellbooks from a file.
    private static void doAddCollection(String filename) {
        int added = 0;
        try (BufferedReader r = new BufferedReader(new FileReader(filename))) { // Open file
            r.readLine(); // Skip header line.
            String line;
            // Read each spellbook entry.
            while ((line = r.readLine()) != null) {
                String[] parts = line.split(",", -1); // Split by commas, keep empty
                if (parts.length != 4) {// Invalid line
                    continue;
                }
                int serial;
                try {
                    serial = Integer.parseInt(parts[0].trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                // Skip duplicates.
                if (spellbooks.containsKey(serial)) {
                    continue;
                }
                String title = parts[1].trim();
                String inventor = parts[2].trim();
                String type = parts[3].trim();
                SpellBook b = new SpellBook(serial, title, inventor, type);
                spellbooks.put(serial, b);
                added++;
            }
            // Output result based on number added.
            if (added == 0) {
                outBlock("No spellbooks have been added to the system.");
            } else if (added == 1) {
                outBlock("1 spellbook successfully added.");
            } else {
                outBlock(added + " spellbooks successfully added.");
            }
        } catch (IOException e) {
            outBlock("No such collection.");
        }
    }

    // Saves all spellbooks in the system to a CSV file.
    private static void doSaveCollection(String filename) {
        // If no spellbooks, output error.
        if (spellbooks.isEmpty()) {
            outBlock("No spellbooks in system.");
            return;
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) { // Open file for writing
            // Write header and all spellbooks.
            pw.println("serialNumber,title,inventor,type");
            List<Integer> serials = new ArrayList<>(spellbooks.keySet()); // Get all serial numbers
            Collections.sort(serials); // Sort by serial number
            for (int s : serials) {
                SpellBook b = spellbooks.get(s);
                pw.println(b.getSerialNumber() + "," + b.getTitle() + "," + b.getInventor() + "," + b.getType());
            }
            outBlock("Success.");
        } catch (IOException ignored) {
        }
    }

    // Lists all spellbooks in short or long format.
    private static void doListAll(boolean longForm) {
        if (spellbooks.isEmpty()) {
            outBlock("No spellbooks in system.");
            return;
        }
        // Build output for all spellbooks.
        List<Integer> keys = new ArrayList<>(spellbooks.keySet()); // Get all serial numbers
        Collections.sort(keys); // Sort by serial number
        StringBuilder sb = new StringBuilder(); // To build output
        boolean first = true;
        for (int k : keys) {
            SpellBook b = spellbooks.get(k);
            if (!first)
                sb.append(longForm ? "\n\n" : "\n"); // Separate entries： double newline for long, single for short
            sb.append(longForm ? b.longFormat() : b.shortFormat()); // Append formatted string： long or short
            first = false; // No longer first
        }
        outBlock(sb.toString());
    }

    // Lists all available (not rented) spellbooks in short or long format.
    private static void doListAvailable(boolean longForm) {
        if (spellbooks.isEmpty()) {
            outBlock("No spellbooks in system.");
            return;
        }
        // Build output for available spellbooks.
        List<Integer> keys = new ArrayList<>(spellbooks.keySet()); // Get all serial numbers
        Collections.sort(keys); // Sort by serial number
        StringBuilder sb = new StringBuilder(); // To build output
        boolean first = true;
        for (int k : keys) {
            SpellBook b = spellbooks.get(k);
            if (b.getRentedBy() == null) {
                if (!first)
                    sb.append(longForm ? "\n\n" : "\n"); // Separate entries： double newline for long, single for short
                sb.append(longForm ? b.longFormat() : b.shortFormat()); // Append formatted string： long or short
                first = false; // No longer first
            }
        }
        // Output result or error if none available.
        if (sb.length() == 0) { // No available spellbooks
            outBlock("No spellbooks available.");
        } else {
            outBlock(sb.toString()); // Output available spellbooks
        }
    }

    // Lists all unique spellbook types in the system.
    private static void doListTypes() {
        if (spellbooks.isEmpty()) {
            outBlock("No spellbooks in system.");
            return;
        }
        // Collect and output all types.
        TreeSet<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (SpellBook b : spellbooks.values()) // Iterate all spellbooks
            set.add(b.getType());
        outBlock(String.join("\n", set)); // Output types, one per line
    }

    // Lists all unique spellbook inventors in the system.
    private static void doListInventors() {
        if (spellbooks.isEmpty()) {
            outBlock("No spellbooks in system.");
            return;
        }
        // Collect and output all inventors.
        TreeSet<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (SpellBook b : spellbooks.values()) // Iterate all spellbooks
            set.add(b.getInventor());
        outBlock(String.join("\n", set)); // Output inventors, one per line
    }

    /**
     * Counts and displays the number of copies for each unique spellbook in the
     * system.
     * A "copy" is defined as any spellbook sharing the same title and inventor.
     * 
     * Implementation details:
     * Iterates through all SpellBook objects stored in the system.
     * Builds a case-insensitive key using the combination of title and inventor.
     * Uses a HashMap to count how many copies exist for each key.
     * Sorts all entries alphabetically by title, then by inventor using a custom
     * Comparator.
     * Prints the results in the format:
     * Title (Inventor): count
     */

    private static void doNumberCopies() {
        if (spellbooks.isEmpty()) {
            outBlock("No spellbooks in system.");
            return;
        }
        // Count copies by title and inventor.
        Map<String, Integer> count = new HashMap<>(); // key = "title||inventor", value = count
        for (SpellBook b : spellbooks.values()) {
            String key = b.getTitle().toLowerCase() + "||" + b.getInventor().toLowerCase(); // Case-insensitive
            count.put(key, count.getOrDefault(key, 0) + 1); // Increment count： default to 0 if not present
        }
        // Sort and output results.
        List<String> keys = new ArrayList<>(count.keySet());

        // Use Collections.sort with a custom Comparator
        Collections.sort(keys, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                // Split both keys into title and inventor parts
                String[] parta = a.split("\\|\\|");
                String[] partb = b.split("\\|\\|");

                // Compare by title first
                int titleCompare = parta[0].compareToIgnoreCase(partb[0]);

                // If titles are the same, compare by inventor
                if (titleCompare == 0) {
                    return parta[1].compareToIgnoreCase(partb[1]);
                } else {
                    return titleCompare;
                }
            }
        });

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i); // key = "title||inventor"
            String[] parts = k.split("\\|\\|"); // Split into title and inventor
            String title = null;
            String inventor = null; // To store original case
            for (SpellBook b : spellbooks.values()) {
                if (b.getTitle().equalsIgnoreCase(parts[0]) && b.getInventor().equalsIgnoreCase(parts[1])) { // Match
                                                                                                             // found
                    title = b.getTitle();
                    inventor = b.getInventor();
                    break;
                }
            }
            if (i > 0)
                sb.append("\n"); // Newline before each entry except first
            sb.append(title).append(" (").append(inventor).append("): ").append(count.get(k)); // Output count
        }
        outBlock(sb.toString());
    }

    // Lists all spellbooks of a given type.
    private static void doTypeQuery(String type) {
        if (spellbooks.isEmpty()) {
            outBlock("No spellbooks in system.");
            return;
        }
        // Collect all spellbooks matching type.
        List<String> out = new ArrayList<>();
        for (SpellBook b : spellbooks.values()) {
            if (b.getType().equalsIgnoreCase(type)) // Case-insensitive match
                out.add(b.shortFormat()); // Add short format to output list
        }
        // Output results or error if none found.
        if (out.isEmpty()) {
            outBlock("No spellbooks with type " + type + ".");
        } else {
            out.sort(String.CASE_INSENSITIVE_ORDER);
            outBlock(String.join("\n", out)); // Output matching spellbooks joined by newlines
        }
    }

    // Lists all spellbooks by a given inventor (supports substring match).
    private static void doInventorQuery(String inventorSubstr) {
        if (spellbooks.isEmpty()) {
            outBlock("No spellbooks in system.");
            return;
        }
        // Collect all spellbooks matching inventor substring.
        String inventorQuery = inventorSubstr.toLowerCase();
        List<String> out = new ArrayList<>(); // To store matching spellbooks
        for (SpellBook b : spellbooks.values()) {
            if (b.getInventor().toLowerCase().contains(inventorQuery)) { // Case-insensitive substring match
                out.add(b.shortFormat());
            }
        }
        // Output results or error if none found.
        if (out.isEmpty()) {
            outBlock("No spellbooks by " + inventorSubstr + ".");
        } else {
            out.sort(String.CASE_INSENSITIVE_ORDER);
            outBlock(String.join("\n", out));
        }
    }

    // Shows details of a specific spellbook (short or long format).
    private static void doSpellbook(int serial, boolean longForm) {
        if (spellbooks.isEmpty()) {
            outBlock("No spellbooks in system.");
            return;
        }
        // Find and output spellbook details.
        SpellBook b = spellbooks.get(serial);
        if (b == null) {
            outBlock("No such spellbook in system.");
            return;
        }
        outBlock(longForm ? b.longFormat() : b.shortFormat()); // Output formatted string: long or short
    }

    // Shows the rental history (student IDs) for a specific spellbook.
    private static void doSpellbookHistory(int serial) {
        SpellBook b = spellbooks.get(serial);
        if (b == null) {
            outBlock("No such spellbook in system.");
            return;
        }
        // Output rental history or error if empty.
        List<Integer> hist = b.getHistory();
        if (hist.isEmpty()) {
            outBlock("No rental history.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hist.size(); i++) { // Output each student ID
            if (i > 0) {
                sb.append("\n");
            }
            sb.append(hist.get(i));
        }
        outBlock(sb.toString());
    }

    // Shows information for a specific student.
    private static void doStudent(int id) {
        if (students.isEmpty()) {
            outBlock("No students in system.");
            return;
        }
        // Find and output student info.
        Student s = students.get(id);
        if (s == null) {
            outBlock("No such student in system.");
            return;
        }
        outBlock(s.getId() + ": " + s.getName());
    }

    // Lists all spellbooks currently rented by a specific student.
    private static void doStudentSpellbooks(int id) {
        if (students.isEmpty()) {
            outBlock("No students in system.");
            return;
        }
        // Find student and output current rentals.
        Student s = students.get(id);
        if (s == null) {
            outBlock("No such student in system.");
            return;
        }
        Set<Integer> current = s.getCurrentBooks();
        if (current.isEmpty()) {
            outBlock("Student not currently renting.");
            return;
        }
        List<Integer> serials = new ArrayList<>(current);
        Collections.sort(serials);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < serials.size(); i++) { // Output each rented spellbook
            if (i > 0) {
                sb.append("\n");
            }
            SpellBook b = spellbooks.get(serials.get(i));
            if (b != null) {
                sb.append(b.shortFormat());
            }
        }
        outBlock(sb.toString());
    }

    // Lists the rental history (spellbooks) for a specific student.
    private static void doStudentHistory(int id) {
        if (students.isEmpty()) {
            outBlock("No students in system.");
            return;
        }
        // Find student and output rental history.
        Student s = students.get(id);
        if (s == null) {
            outBlock("No such student in system.");
            return;
        }
        List<Integer> hist = s.getHistory();
        if (hist.isEmpty()) {
            outBlock("No rental history for student.");
            return;
        }
        StringBuilder sb = new StringBuilder(); // To build output
        for (int i = 0; i < hist.size(); i++) {
            if (i > 0) {
                sb.append("\n");
            }
            SpellBook b = spellbooks.get(hist.get(i)); // Get spellbook by serial number
            if (b != null) {
                sb.append(b.shortFormat()); // Append short format to output
            }
        }
        outBlock(sb.toString());
    }

    // Rents a spellbook to a student.
    private static void doRent(int studentId, int serial) {
        if (students.isEmpty()) {
            outBlock("No students in system.");
            return;
        }
        // Validate student and spellbook.
        Student s = students.get(studentId);
        if (s == null) {
            outBlock("No such student in system.");
            return;
        }
        if (spellbooks.isEmpty()) {
            outBlock("No spellbooks in system.");
            return;
        }
        SpellBook b = spellbooks.get(serial);
        if (b == null) {
            outBlock("No such spellbook in system.");
            return;
        }
        if (b.getRentedBy() != null) {
            outBlock("Spellbook is currently unavailable.");
            return;
        }
        // Rent spellbook and update records.
        b.setRentedBy(studentId);
        s.rentBook(serial);
        outBlock("Success.");
    }

    // Returns a specific spellbook from a student.
    private static void doRelinquish(int studentId, int serial) {
        if (students.isEmpty()) {
            outBlock("No students in system.");
            return;
        }
        // Validate student and spellbook.
        Student s = students.get(studentId);
        if (s == null) {
            outBlock("No such student in system.");
            return;
        }
        if (spellbooks.isEmpty()) {
            outBlock("No spellbooks in system.");
            return;
        }
        SpellBook b = spellbooks.get(serial);
        if (b == null) {
            outBlock("No such spellbook in system.");
            return;
        }
        // Check if student is renting this spellbook.
        if (!s.getCurrentBooks().contains(serial) || !Objects.equals(b.getRentedBy(), studentId)) { // Not rented by
                                                                                                    // student
            outBlock("Unable to return spellbook.");
            return;
        }
        // Return spellbook and update records.
        s.returnBook(serial);
        b.setRentedBy(null);
        b.addHistory(studentId);
        outBlock("Success.");
    }

    // Returns all spellbooks currently rented by a student.
    private static void doRelinquishAll(int studentId) {
        if (students.isEmpty()) {
            outBlock("No students in system.");
            return;
        }
        // Find student and return all current rentals.
        Student s = students.get(studentId);
        if (s == null) {
            outBlock("No such student in system.");
            return;
        }
        List<Integer> toReturn = new ArrayList<>(s.getCurrentBooks()); // Get current rentals
        Collections.sort(toReturn);
        for (int serial : toReturn) {
            SpellBook b = spellbooks.get(serial);
            if (b != null && Objects.equals(b.getRentedBy(), studentId)) {
                s.returnBook(serial);
                b.setRentedBy(null);
                b.addHistory(studentId);
            }
        }
        outBlock("Success.");
    }

    // Finds common spellbooks in the rental history of multiple students.
    private static void doCommon(List<Integer> ids) {
        if (ids.isEmpty())
            return;
        // Check for students and spellbooks existence.
        if (students.isEmpty()) {
            outBlock("No students in system.");
            return;
        }
        if (spellbooks.isEmpty()) {
            outBlock("No spellbooks in system.");
            return;
        }
        // Validate all student IDs.
        for (int id : ids) {
            if (!students.containsKey(id)) {
                outBlock("No such student in system.");
                return;
            }
        }
        // Collect rental history sets for each student.
        List<Set<String>> historySets = new ArrayList<>();
        for (int id : ids) {
            Student s = students.get(id);
            Set<String> set = new HashSet<>();
            for (int serial : s.getHistory()) {
                SpellBook b = spellbooks.get(serial);
                if (b != null) {
                    set.add(b.getTitle().toLowerCase() + "||" +
                            b.getInventor().toLowerCase()); // Case-insensitive key
                }
            }
            historySets.add(set);
        }
        // Find intersection (common spellbooks).
        Set<String> common = new HashSet<>(historySets.get(0));
        for (int i = 1; i < historySets.size(); i++) {
            common.retainAll(historySets.get(i));
        }
        // Build output list.
        List<String> out = new ArrayList<>();
        for (String key : common) {
            String[] p = key.split("\\|\\|"); // Split into title and inventor
            for (SpellBook b : spellbooks.values()) {
                if (b.getTitle().equalsIgnoreCase(p[0]) && // Compare titles
                        b.getInventor().equalsIgnoreCase(p[1])) { // Compare inventors
                    out.add(b.getTitle() + " (" + b.getInventor() + ")");
                    break;
                }
            }
        }
        // Output results or error if none found.
        if (out.isEmpty()) {
            outBlock("No common spellbooks.");
        } else {
            out.sort(String.CASE_INSENSITIVE_ORDER);
            outBlock(String.join("\n", out));
        }
    }
}
