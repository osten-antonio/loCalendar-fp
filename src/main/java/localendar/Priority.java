package localendar;

public enum Priority {
    LOW(1,"#ff4c4c"), MEDIUM(2,"#ffa600"), HIGH(3,"#32cd32");

    private final int level;
    private final String color;

    Priority(int level, String color) {
        this.level = level;
        this.color = color;
    }

    public int getLevel() {
        return level;
    }

    public String getColor() {
        return color;
    }

    public static Priority fromInt(int level) {
        for (Priority p : values()) {
            if (p.getLevel() == level) {
                return p;
            }
        }
        throw new IllegalArgumentException("Invalid priority level: " + level);
    }
}
