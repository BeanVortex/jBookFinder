package ir.darkdeveloper.jbookfinder.config;


//import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.application.HostServices;

import java.io.File;

public class Configs {

    private static Configs configs;

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

//    private static FXTrayIcon fxTray;

    private Configs() {
    }

    public static Configs getInstance() {
        if (configs == null)
            configs = new Configs();
        return configs;
    }

//    public  FXTrayIcon getFxTray() {
//        return fxTray;
//    }

//    public void setFxTray(FXTrayIcon fxTray) {
//        Configs.fxTray = fxTray;
//    }


    public String getSaveLocation() {
        return saveLocation;
    }

    public void setSaveLocation(String saveLocation) {
        Configs.saveLocation = saveLocation;
        Configs.bookCoverLocation = saveLocation + File.separator + bookCoverDirName + File.separator;
        Configs.unrecordedLocation = saveLocation + File.separator + unrecordedDirName + File.separator;
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
        themeSubject.notifyAllObservers(theme);
    }

    public String getConfigLocation() {
        return configLocation;
    }

    public ThemeSubject getThemeSubject() {
        return themeSubject;
    }

    public HostServices getHostServices() {
        return hostServices;
    }

    public String getBookCoverDirName() {
        return bookCoverDirName;
    }

    public void setHostServices(HostServices hostServices) {
        Configs.hostServices = hostServices;
    }

    public String getUnRecordedDirName() {
        return unrecordedDirName;
    }

    public String getUnrecordedLocation(){
        return unrecordedLocation;
    }
}
