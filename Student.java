import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Student entity:
 * - id, name
 * - currentBooks: serial numbers of books currently rented
 * - history: serial numbers of books that have been returned (completed
 * rentals)
 */
public class Student {
    private final int id;
    private final String name;
    private final LinkedHashSet<Integer> currentBooks = new LinkedHashSet<>();
    private final ArrayList<Integer> history = new ArrayList<>();

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters for student attributes and records
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Integer> getCurrentBooks() {
        return Collections.unmodifiableSet(currentBooks);
    }

    public List<Integer> getHistory() {
        return Collections.unmodifiableList(history);
    }

    // Rent a book (add to current)
    public void rentBook(int serial) {
        currentBooks.add(serial);
    }

    // Return a book (move from current to history)
    public void returnBook(int serial) {
        if (currentBooks.remove(serial)) {
            history.add(serial); // append in order of completion
        }
    }
}
