package ir.darkdeveloper.jbookfinder.config;

import java.util.ArrayList;
import java.util.List;

public class ThemeSubject {
    private static final List<ThemeObserver> observers = new ArrayList<>();

    public void addObserver(ThemeObserver newO) {
        observers.add(newO);
    }

    public void notifyAllObservers(String theme) {
        observers.forEach(observer -> observer.updateTheme(theme));
    }
}
