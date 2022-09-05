package ir.darkdeveloper.jbookfinder.config;


public class Configs {

    private static Configs beans = null;
    private static String baseLocation = System.getProperty("user.home") + "/Downloads/JBookFinder/";
    private static final String bookCoverLocation = baseLocation + "/book_covers/";
    private static final String imageBaseUrl = "http://library.lol/";

    private Configs() {
    }

    public static Configs getInstance() {
        if (beans == null)
            beans = new Configs();
        return beans;
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
}
