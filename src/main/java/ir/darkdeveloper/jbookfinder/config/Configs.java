package ir.darkdeveloper.jbookfinder.config;


import java.io.File;

public class Configs {

    private static Configs configs;

    private static String saveLocation = System.getProperty("user.home")
            + File.separator + "Downloads"
            + File.separator + "JBookFinder"
            + File.separator;
    private static final String bookCoverLocation = saveLocation
            + "book_covers"
            + File.separator;
    private static final String configLocation = saveLocation
            + "configs"
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
    }

    public String getConfigLocation() {
        return configLocation;
    }
}
