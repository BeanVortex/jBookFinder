package ir.darkdeveloper.jbookfinder.config;


public class Configs {

    private static Configs configs;
    private static String baseLocation = System.getProperty("user.home") + "/Downloads/JBookFinder/";
    private static String theme = "light";

    private static final String bookCoverLocation = baseLocation + "/book_covers/";
    private static final String imageBaseUrl = "http://library.lol/";

    private Configs() {
    }

    public static Configs getInstance() {
        if (configs == null)
            configs = new Configs();
        return configs;
    }


    public String getSaveLocation() {
        return baseLocation;
    }

    public static void setBaseLocation(String baseLocation) {
        Configs.baseLocation = baseLocation;
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
}
