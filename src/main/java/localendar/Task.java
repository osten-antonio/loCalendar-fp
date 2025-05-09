package localendar;

import javax.lang.model.type.NullType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

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
    public Task copy() {
        return new Task(
                this.title,
                this.body,
                this.status,
                this.dueDate,
                this.dueTime,
                this.priority.getLevel(),
                this.rrule != null ? this.rrule.toString() : null,
                this.category
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return status == task.status && Objects.equals(title, task.title) && Objects.equals(body, task.body) && Objects.equals(dueDate, task.dueDate) && Objects.equals(dueTime, task.dueTime) && priority == task.priority && Objects.equals(rrule, task.rrule) && Objects.equals(category, task.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, body, status, dueDate, dueTime, priority, rrule, category);
    }

    private RecurrenceRule parseRecurrenceRule(String rrule){
        if (rrule == null || rrule.isEmpty() || rrule.equals("FREQ=;INTERVAL=;UNTIL=")) {
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
                System.out.println(part.substring(9));
                interval = Integer.parseInt(part.substring(9));
            } else if (part.startsWith("UNTIL=")) {
                String dateStr = part.substring(6);
                if (!dateStr.isBlank()) {
                    endDate = LocalDate.parse(dateStr);
                }
            }

        }
        return new RecurrenceRule(frequency, interval, endDate);
    }

    public Iterator<Task> iterator(LocalDate limitDate) {
        if (rrule == null || rrule.getFrequency() == Frequency.NONE) {
            return new Iterator<Task>() {
                boolean hasNext = true;

                @Override
                public boolean hasNext() {
                    return hasNext;
                }

                @Override
                public Task next() {
                    hasNext = false;
                    return Task.this;
                }
            };
        }

        Iterator<LocalDate> recurrenceDates = rrule.generateInstances(dueDate).iterator();

        return new Iterator<Task>() {
            LocalDate nextDate = null;


            @Override
            public boolean hasNext() {
                while (recurrenceDates.hasNext()) {
                    LocalDate candidate = recurrenceDates.next();
                    if (limitDate != null && candidate.isAfter(limitDate)) {
                        return false;
                    }
                    nextDate = candidate;
                    return true;
                }
                return false;
            }



            @Override
            public Task next() {
                if (nextDate == null && !hasNext()) {
                    throw new IllegalStateException("No more recurrence dates.");
                }

                Task recurringInstance = new Task(
                        title, body, status, nextDate, dueTime,
                        priority.getLevel(), rrule.toString(), category
                );
                nextDate = null;
                return recurringInstance;
            }
        };
    }


}