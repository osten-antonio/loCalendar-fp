package localendar;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public class RecurrenceRule implements Iterable<LocalDate> {
    private Frequency freq;
    private Optional<LocalDate> dateEnd;
    private int interval;
    private LocalDate startDate;

    RecurrenceRule(Frequency freq, int interval, LocalDate dateEnd){
        this.freq=freq;
        this.interval=interval;
        this.dateEnd=Optional.ofNullable(dateEnd);
    }
    public Frequency getFrequency() { return freq; }

    public int getInterval() { return interval; }
    public Optional<LocalDate> getEndDate() { return dateEnd; }


    public Iterable<LocalDate> generateInstances(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    @Override
    public String toString(){
        return String.format("FREQ=%s;INTERVAL=%d;UNTIL=%s",freq.toString(),
                interval,dateEnd.map(LocalDate::toString).orElse(""));
    }

    //https://stackoverflow.com/questions/47066644/how-to-create-a-custom-iterator-in-java
    @Override
    public Iterator<LocalDate> iterator() {
        return new Iterator<LocalDate>() {
            private LocalDate currentDate = startDate;

            @Override
            public boolean hasNext() {
                if (freq == Frequency.NONE) return false;
                return dateEnd.map(end -> !currentDate.isAfter(end)).orElse(true);
            }


            @Override
            public LocalDate next() {
                if (!hasNext()) throw new IllegalStateException("No more recurrence dates available.");

                LocalDate nextDate = currentDate;

                switch (freq) {
                    case DAILY:
                        currentDate = currentDate.plusDays(interval);
                        break;
                    case WEEKLY:
                        currentDate = currentDate.plusWeeks(interval);
                        break;
                    case MONTHLY:
                        currentDate = currentDate.plusMonths(interval);
                        break;
                    case YEARLY:
                        currentDate = currentDate.plusYears(interval);
                        break;
                }

                return nextDate;
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecurrenceRule that = (RecurrenceRule) o;
        return interval == that.interval && freq == that.freq && Objects.equals(dateEnd, that.dateEnd) && Objects.equals(startDate, that.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(freq, dateEnd, interval, startDate);
    }
}
