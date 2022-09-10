package ir.darkdeveloper.jbookfinder.repo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DBHelper {

    static final String TABLE_NAME = "books";

    static final String COL_ID = "id";
    static final String COL_AUTHOR = "author";
    static final String COL_TITLE = "title";
    static final String COL_PUBLISHER = "publisher";
    static final String COL_YEAR = "year";
    static final String COL_PAGES = "pages";
    static final String COL_LANGUAGE = "language";
    static final String COL_SIZE = "size";
    static final String COL_FILE_FORMAT = "file_format";
    static final String COL_IMAGE = "image";
    static final String COL_FILE = "file";

    private static final Logger log = Logger.getLogger(DBHelper.class.getName());

    Connection openConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:JBookFinder.db");
    }

    void createTable() {
        var createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_AUTHOR + " VARCHAR unique ,"
                + COL_TITLE + " VARCHAR unique ,"
                + COL_PUBLISHER + " VARCHAR,"
                + COL_YEAR + " VARCHAR,"
                + COL_PAGES + " VARCHAR,"
                + COL_LANGUAGE + " VARCHAR,"
                + COL_SIZE + " VARCHAR,"
                + COL_FILE_FORMAT + " VARCHAR,"
                + COL_IMAGE + " VARCHAR,"
                + COL_FILE + " VARCHAR"
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
