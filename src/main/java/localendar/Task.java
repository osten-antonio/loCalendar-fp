package localendar;

import javax.lang.model.type.NullType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Iterator;

public class Task {
    private String title;
    private String body;
    private boolean status;
    private LocalDate dueDate;
    private LocalTime dueTime;
    private Priority priority;
    private RecurrenceRule rrule;
    private Category category;

    public Task(String title, String body, boolean status, LocalDate dueDate,
         LocalTime dueTime, int priority,String rrule, Category category){
        setTitle(title);
        setBody(body);
        setStatus(status);
        setDueDate(dueDate);
        setDueTime(dueTime);
        setPriority(priority);
        setCategory(category);
        setRrule(rrule);
    }
    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Priority getPriority(){
        return priority;
    }

    public boolean isStatus() {
        return status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalTime getDueTime() {
        return dueTime;
    }

    public RecurrenceRule getRrule() {
        return rrule;
    }

    public Category getCategory() {
        return category;
    }

    public void setPriority(int integer){
        this.priority = Priority.fromInt(integer);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setDueTime(LocalTime dueTime) {
        this.dueTime = dueTime;
    }

    public void setRrule(String rrule) {
        this.rrule = parseRecurrenceRule(rrule);
    }

    public void setCategory(Category category) {
        this.category = category;
    }


    public int compare(Task task1,Task task2){
        if(task1.getPriority().getLevel() > task2.getPriority().getLevel()){
            return 1;
        }
        if(task1.getPriority().getLevel() < task2.getPriority().getLevel()){
            return -1;
        }
        if(task1.getDueDate().compareTo(task2.getDueDate()) != 0){
            return task1.getDueDate().compareTo(task2.getDueDate());
        }
        return task1.getDueTime().compareTo(task2.getDueTime());
    }


    private RecurrenceRule parseRecurrenceRule(String rrule){
        if (rrule == null || rrule.isEmpty()) {
            return new RecurrenceRule(Frequency.NONE, 0, null);
        }

        // Example: "FREQ=WEEKLY;INTERVAL=2;UNTIL=2025-12-31"
        String[] parts = rrule.split(";");
        Frequency frequency = Frequency.NONE;
        int interval = 1;
        LocalDate endDate = null;

        for (String part : parts) {
            if (part.startsWith("FREQ=")) {
                String value = part.substring(5);
                frequency = Frequency.valueOf(value); // Convert String to Enum
            } else if (part.startsWith("INTERVAL=")) {
                interval = Integer.parseInt(part.substring(9));
            } else if (part.startsWith("UNTIL=")) {
                if(!part.substring(6).isBlank()){
                    endDate = LocalDate.parse(part.substring(6));
                }
            }
        }
        return new RecurrenceRule(frequency, interval, endDate);
    }

    public Iterator<Task> iterator(LocalDate limitDate) {
        return new Iterator<Task>() {
            private final Iterator<LocalDate> recurrenceDates =
                    (rrule.getFrequency() != Frequency.NONE)
                            ? rrule.iterator()
                            : null;

            @Override
            public boolean hasNext() {
                if (recurrenceDates == null || !recurrenceDates.hasNext()) {
                    return false;
                }
                LocalDate nextDate = recurrenceDates.next();
                return (limitDate == null || !nextDate.isAfter(limitDate));
            }

            @Override
            public Task next() {
                return new Task(title, body, status, recurrenceDates.next(), dueTime, priority.getLevel(), rrule.toString(), category);
            }
        };
    }
}
