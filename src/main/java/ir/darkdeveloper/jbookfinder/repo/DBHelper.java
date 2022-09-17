package ir.darkdeveloper.jbookfinder.repo;

import ir.darkdeveloper.jbookfinder.config.Configs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DBHelper {

    static final String TABLE_NAME = "books";

    static final String COL_ID = "id";
    static final String COL_BOOK_ID = "book_id";
    static final String COL_AUTHOR = "author";
    static final String COL_TITLE = "title";
    static final String COL_PUBLISHER = "publisher";
    static final String COL_YEAR = "year";
    static final String COL_PAGES = "pages";
    static final String COL_LANGUAGE = "language";
    static final String COL_SIZE = "size";
    static final String COL_FILE_FORMAT = "file_format";
    static final String COL_IMAGE_URL = "image_url";
    static final String COL_MIRROR = "mirror";
    static final String COL_IMAGE_PATH = "image_path";
    static final String COL_FILE_PATH = "file_path";

    private static final Logger log = Logger.getLogger(DBHelper.class.getName());
    private final Configs configs = Configs.getInstance();

    Connection openConnection() throws SQLException {
        var path = configs.getConfigLocation() + "JBookFinder.db";
        return DriverManager.getConnection("jdbc:sqlite:" + path);
    }

    void createTable() {
        var createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_BOOK_ID + " VARCHAR unique ,"
                + COL_AUTHOR + " VARCHAR unique ,"
                + COL_TITLE + " VARCHAR unique ,"
                + COL_PUBLISHER + " VARCHAR,"
                + COL_YEAR + " VARCHAR,"
                + COL_PAGES + " VARCHAR,"
                + COL_LANGUAGE + " VARCHAR,"
                + COL_SIZE + " VARCHAR,"
                + COL_FILE_FORMAT + " VARCHAR,"
                + COL_IMAGE_URL + " VARCHAR,"
                + COL_MIRROR + " VARCHAR,"
                + COL_IMAGE_PATH + " VARCHAR,"
                + COL_FILE_PATH + " VARCHAR"
                + ");";
        try {
            var con = openConnection();
            var stmt = con.createStatement();
            stmt.executeUpdate(createTable);
            stmt.close();
            con.close();
            log.info("created db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
