package localendar;

import java.util.Objects;

public class Category {
    private String name;
    private String color;
    private String textColor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


    public Category(String name, String color, String textColor){
        setName(name);
        setColor(color);
        setTextColor(textColor);
    }
    public void setName(String name){
        this.name = name;
    }
    public void setColor( String color){
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
