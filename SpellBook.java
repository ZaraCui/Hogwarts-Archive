import java.util.ArrayList;
import java.util.List;

/**
 * Represents a spellbook in the Hogwarts Archive system.
 * Each spellbook has a serial number, title, inventor, and type.
 * It tracks rental history and current availability status.
 */
public class SpellBook {
    private int serialNumber;
    private String title;
    private String inventor;
    private String type;
    private Integer rentedBy = null; // null = available
    private List<Integer> history = new ArrayList<>();

    public SpellBook(int serialNumber, String title, String inventor, String type) {
        this.serialNumber = serialNumber;
        this.title = title;
        this.inventor = inventor;
        this.type = type;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getInventor() {
        return inventor;
    }

    public String getType() {
        return type;
    }

    public Integer getRentedBy() {
        return rentedBy;
    }

    public void setRentedBy(Integer studentId) {
        this.rentedBy = studentId;
    }

    public List<Integer> getHistory() {
        return history;
    }

    public void addHistory(int studentId) {
        history.add(studentId);
    }

    /** Short: "123456: Title (Inventor)" */
    public String shortFormat() {
        return title + " (" + inventor + ")";
    }

    public String longFormat() {
        // line 1: "<serial>: <title> (<inventor>, <type>)"
        String header = serialNumber + ": " + title + " (" + inventor + ", " + type + ")";
        // line 2: availability
        String status = (rentedBy == null) ? "Currently available." : "Rented by: " + rentedBy + ".";
        return header + "\n" + status;
    }
}
