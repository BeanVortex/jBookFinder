package ir.darkdeveloper.jbookfinder.config;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Configs {

    private static Configs configs;
    private static final List<ThemeObserver> observers = new ArrayList<>();

    private static String saveLocation = System.getProperty("user.home")
            + File.separator + "Downloads"
            + File.separator + "JBookFinder";

    private static final String bookCoverDirName = "book_covers";
    private static String bookCoverLocation = saveLocation
            + File.separator
            + bookCoverDirName
            + File.separator;
    private static final String configLocation = System.getProperty("user.home")
            + File.separator + "Documents"
            + File.separator + "JBookFinder"
            + File.separator;

    private static String theme = "light";
    private static final String imageBaseUrl = "http://library.lol/";

    private Configs() {
    }

    public static Configs getInstance() {
        if (configs == null)
            configs = new Configs();
        return configs;
    }


    public String getSaveLocation() {
        return saveLocation;
    }

    public void setSaveLocation(String saveLocation) {
        Configs.saveLocation = saveLocation;
        Configs.bookCoverLocation = saveLocation + File.separator + "book_covers" + File.separator;
    }

    public String getBookCoverLocation() {
        return bookCoverLocation;
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        Configs.theme = theme;
        notifyAllObservers();
    }

    public String getConfigLocation() {
        return configLocation;
    }

    public String getBookCoverDirName() {
        return bookCoverDirName;
    }

    public void addObserver(ThemeObserver newO){
        observers.add(newO);
    }

    private void notifyAllObservers(){
        observers.forEach(observer -> observer.updateTheme(getTheme()));
    }
}
