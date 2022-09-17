package ir.darkdeveloper.jbookfinder.repo;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.model.BookModel;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ir.darkdeveloper.jbookfinder.repo.DBHelper.*;

public class BooksRepo {

    private static BooksRepo booksRepo;
    private final Configs configs = Configs.getInstance();

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

    public void insertBook(BookModel book) {
        var sql = "INSERT INTO " + TABLE_NAME + " (" +
                COL_BOOK_ID + "," +
                COL_AUTHOR + "," +
                COL_TITLE + "," +
                COL_PUBLISHER + "," +
                COL_YEAR + "," +
                COL_PAGES + "," +
                COL_LANGUAGE + "," +
                COL_SIZE + "," +
                COL_FILE_FORMAT + "," +
                COL_IMAGE_URL + "," +
                COL_MIRROR + "," +
                COL_IMAGE_PATH + "," +
                COL_FILE_PATH +
                ") VALUES(\"" +
                book.getBookId() + "\",\"" +
                book.getAuthor() + "\",\"" +
                book.getTitle() + "\",\"" +
                book.getPublisher() + "\",\"" +
                book.getYear() + "\",\"" +
                book.getPages() + "\",\"" +
                book.getLanguage() + "\",\"" +
                book.getSize() + "\",\"" +
                book.getFileFormat() + "\",\"" +
                book.getImageUrl() + "\",\"" +
                book.getMirror() + "\",\"" +
                book.getImagePath() + "\",\"" +
                book.getFilePath() +
                "\");";
        try (var con = dbHelper.openConnection();
             var stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public BookModel findByBookId(String bookId) {
        var sql = "SELECT * FROM " + TABLE_NAME + " WHERE book_id=\"" + bookId + "\";";
        try (var con = dbHelper.openConnection();
             var stmt = con.createStatement();
             var rs = stmt.executeQuery(sql)) {

            if (rs.next())
                return createBook(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteBook(Integer id) {
        var sql = "DELETE FROM " + TABLE_NAME + " WHERE id=" + id + ";";
        try (var con = dbHelper.openConnection();
             var stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<BookModel> getBooks() {
        var sql = "SELECT * FROM " + TABLE_NAME + ";";
        try (var con = dbHelper.openConnection();
             var stmt = con.createStatement();
             var rs = stmt.executeQuery(sql)) {
            var list = new ArrayList<BookModel>();
            while (rs.next())
                list.add(createBook(rs));
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private BookModel createBook(ResultSet rs) throws SQLException {
        var id = rs.getInt(COL_ID);
        var book_id = rs.getString(COL_BOOK_ID);
        var author = rs.getString(COL_AUTHOR);
        var title = rs.getString(COL_TITLE);
        var publisher = rs.getString(COL_PUBLISHER);
        var year = rs.getString(COL_YEAR);
        var pages = rs.getString(COL_PAGES);
        var language = rs.getString(COL_LANGUAGE);
        var size = rs.getString(COL_SIZE);
        var fileFormat = rs.getString(COL_FILE_FORMAT);
        var imageUrl = rs.getString(COL_IMAGE_URL);
        var mirror = rs.getString(COL_MIRROR);
        var imagePath = rs.getString(COL_IMAGE_PATH);
        var filePath = rs.getString(COL_FILE_PATH);
        return new BookModel(String.valueOf(id),book_id, author, title, publisher, year, pages, language, size,
                fileFormat, imageUrl, mirror, imagePath, filePath);
    }

    public void updateBookExistenceRecords() {
        var books = booksRepo.getBooks();
        books.forEach(book -> {
            var file = new File(book.getFilePath());
            if (!file.exists())
                booksRepo.deleteBook(Integer.valueOf(book.getId()));
        });
    }

    public void updateBooksPath(BookModel book) {
        var sql = "UPDATE " + TABLE_NAME + " SET " + COL_FILE_PATH + "=\"" + book.getFilePath() + "\","
                + COL_IMAGE_PATH + "=\"" + book.getImagePath()
                + "\" WHERE id=" + book.getId() + ";";
        try (var con = dbHelper.openConnection();
             var stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBooksPath(String newPath) {
        var books = getBooks();
        books.forEach(book -> {
            var filePathWithName = book.getFilePath();
            var imagePathWithName = book.getImagePath();
            var fileName = filePathWithName.substring(filePathWithName.lastIndexOf(File.separatorChar) + 1);
            var imageName = imagePathWithName.substring(imagePathWithName.lastIndexOf(File.separatorChar) + 1);

            var filePath = newPath + File.separator + fileName;
            var imagePath = newPath + File.separator + configs.getBookCoverDirName() + File.separator + imageName;
            book.setFilePath(filePath);
            book.setImagePath(imagePath);
            updateBooksPath(book);
        });
    }
}
