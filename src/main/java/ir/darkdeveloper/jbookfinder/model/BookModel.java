package ir.darkdeveloper.jbookfinder.model;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookModel {
    private Integer id;
    private String bookId;
    private String author;
    private String title;
    private String publisher;
    private String year;
    private String pages;
    private String language;
    private String size;
    private String fileFormat;
    private String imageUrl;
    private String mirror;
    private String imagePath;
    private String filePath;
    private boolean resumable;
    private long fileSize;

    public BookModel(String bookId, String author, String title, String publisher, String year,
                     String pages, String language, String size, String fileFormat,
                     String mirror) {
        this.bookId = bookId;
        this.author = author;
        this.title = title;
        this.publisher = publisher;
        this.year = year;
        this.pages = pages;
        this.language = language;
        this.size = size;
        this.fileFormat = fileFormat;
        this.mirror = mirror;
    }

    public BookModel(int id, String bookId, String author, String title,
                     String publisher, String year, String pages, String language,
                     String size, String fileFormat, String imageUrl, String mirror,
                     String imagePath, String filePath) {
        this.id = id;
        this.bookId = bookId;
        this.author = author;
        this.title = title;
        this.publisher = publisher;
        this.year = year;
        this.pages = pages;
        this.language = language;
        this.size = size;
        this.fileFormat = fileFormat;
        this.mirror = mirror;
        this.imageUrl = imageUrl;
        this.imagePath = imagePath;
        this.filePath = filePath;
    }
}