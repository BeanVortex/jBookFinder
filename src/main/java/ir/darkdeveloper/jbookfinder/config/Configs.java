package ir.darkdeveloper.jbookfinder.config;


import javafx.application.HostServices;

import java.io.File;

public class Configs {

    public static final String version = "1.1.1";

    private static boolean backgroundDownload = false;
    private static String filterResult = "pdf,rar,epub";
    private static String resultCount = "25";


    private static String saveLocation = System.getProperty("user.home")
            + File.separator + "Downloads"
            + File.separator + "JBookFinder";

    private static final String bookCoverDirName = "book_covers";
    private static final String unrecordedDirName = "unrecorded_books";
    private static String unrecordedLocation = saveLocation
            + File.separator
            + unrecordedDirName
            + File.separator;
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
    private static final ThemeSubject themeSubject = new ThemeSubject();
    private static HostServices hostServices;


    public static String getSaveLocation() {
        return saveLocation;
    }

    public static void setSaveLocation(String saveLocation) {
        Configs.saveLocation = saveLocation;
        Configs.bookCoverLocation = saveLocation + File.separator + bookCoverDirName + File.separator;
        Configs.unrecordedLocation = saveLocation + File.separator + unrecordedDirName + File.separator;
    }

    public static String getBookCoverLocation() {
        return bookCoverLocation;
    }

    public static String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public static String getTheme() {
        return theme;
    }

    public static void setTheme(String theme) {
        Configs.theme = theme;
        themeSubject.notifyAllObservers(theme);
    }

    public static String getConfigLocation() {
        return configLocation;
    }

    public static ThemeSubject getThemeSubject() {
        return themeSubject;
    }

    public static HostServices getHostServices() {
        return hostServices;
    }

    public static String getBookCoverDirName() {
        return bookCoverDirName;
    }

    public static void setHostServices(HostServices hostServices) {
        Configs.hostServices = hostServices;
    }

    public static String getUnRecordedDirName() {
        return unrecordedDirName;
    }

    public static String getUnrecordedLocation() {
        return unrecordedLocation;
    }

    public static void setBackgroundDownload(boolean backgroundDownload) {
        Configs.backgroundDownload = backgroundDownload;
    }

    public static boolean isBackgroundDownload() {
        return backgroundDownload;
    }

    public static String getFilterResult() {
        return filterResult;
    }

    public static void setFilterResult(String filterResult) {
        Configs.filterResult = filterResult;
    }

    public static String getResultCount() {
        return resultCount;
    }

    public static void setResultCount(String resultCount) {
        Configs.resultCount = resultCount;
    }
}
