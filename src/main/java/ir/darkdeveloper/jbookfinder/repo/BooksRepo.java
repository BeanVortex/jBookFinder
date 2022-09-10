package ir.darkdeveloper.jbookfinder.repo;

import ir.darkdeveloper.jbookfinder.model.BookModel;

import java.sql.SQLException;

import static ir.darkdeveloper.jbookfinder.repo.DBHelper.*;

public class BooksRepo {

    private static BooksRepo booksRepo;

    private BooksRepo() {

    }

    public static BooksRepo getInstance() {
        if (booksRepo == null)
            booksRepo = new BooksRepo();
        return booksRepo;
    }

    private static final DBHelper dbHelper = new DBHelper();

    public void createTable() {
        dbHelper.createTable();
    }

    public void insertBook(BookModel book, String imagePath, String filePath) {
        var sql = "INSERT INTO " + TABLE_NAME + " (" +
                COL_AUTHOR + "," +
                COL_TITLE + "," +
                COL_PUBLISHER + "," +
                COL_YEAR + "," +
                COL_PAGES + "," +
                COL_LANGUAGE + "," +
                COL_SIZE + "," +
                COL_FILE_FORMAT + "," +
                COL_IMAGE + "," +
                COL_FILE +
                ") VALUES(\"" +
                book.getAuthor() + "\",\"" +
                book.getTitle() + "\",\"" +
                book.getPublisher() + "\",\"" +
                book.getYear() + "\",\"" +
                book.getPages() + "\",\"" +
                book.getLanguage() + "\",\"" +
                book.getSize() + "\",\"" +
                book.getFileFormat() + "\",\"" +
                imagePath + "\",\"" +
                filePath +
                "\");";
        try {
            var con = dbHelper.openConnection();
            var stmt = con.createStatement();
            stmt.executeUpdate(sql);
            con.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public BookModel findByTitle(String cleanedTitle) {
        var sql = "SELECT * FROM " + TABLE_NAME + " WHERE title=\"" + cleanedTitle + "\";";
        try {
            var con = dbHelper.openConnection();
            var stmt = con.createStatement();
            var rs = stmt.executeQuery(sql);
            if (rs.next()) {
                var id = rs.getInt(COL_ID);
                var author = rs.getString(COL_AUTHOR);
                var title = rs.getString(COL_TITLE);
                var publisher = rs.getString(COL_PUBLISHER);
                var year = rs.getString(COL_YEAR);
                var pages = rs.getString(COL_PAGES);
                var language = rs.getString(COL_LANGUAGE);
                var size = rs.getString(COL_SIZE);
                var fileFormat = rs.getString(COL_FILE_FORMAT);
                var imagePath = rs.getString(COL_IMAGE);
                var filePath = rs.getString(COL_FILE);
                var book = new BookModel(String.valueOf(id), author, title, publisher, year, pages, language, size,
                        fileFormat, imagePath, filePath);
                rs.close();
                stmt.close();
                con.close();
                return book;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteBook(Integer id){
        var sql = "DELETE FROM " + TABLE_NAME + " WHERE id=" + id +";";
        try {
            var con = dbHelper.openConnection();
            var stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
