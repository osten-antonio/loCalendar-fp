package localendar;

import java.util.Objects;

public class Category {
    private String name;
    private String color;
    private String textColor;
    public Category(String name, String color, String textColor){
        setName(name);
        setColor(color);
        setTextColor(textColor);
    }
    public void setName(String name){
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, textColor);
    }

    public void setColor(String color){
        this.color=color;
    }
    public void setTextColor(String textColor){
        this.textColor=textColor;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String getTextColor() {
        return textColor;
    }
}
