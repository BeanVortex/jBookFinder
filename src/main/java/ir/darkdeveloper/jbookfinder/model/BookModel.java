package ir.darkdeveloper.jbookfinder.model;

import javafx.scene.Node;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class BookModel extends Node {
    private String id;
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

    public BookModel(String id, String author, String title, String publisher, String year,
                     String pages, String language, String size, String fileFormat,
                     String mirror) {
        this.id = id;
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
}