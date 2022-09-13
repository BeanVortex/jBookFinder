package ir.darkdeveloper.jbookfinder.model;


public class BookModel {
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

    public BookModel() {
    }

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

    public BookModel(String id, String author, String title, String publisher, String year,
                     String pages, String language, String size, String fileFormat,
                     String imageUrl, String mirror) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.publisher = publisher;
        this.year = year;
        this.pages = pages;
        this.language = language;
        this.size = size;
        this.fileFormat = fileFormat;
        this.imageUrl = imageUrl;
        this.mirror = mirror;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMirror() {
        return mirror;
    }

    public void setMirror(String mirror) {
        this.mirror = mirror;
    }

    @Override
    public String toString() {
        return "BookModel{" +
                "id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", publisher='" + publisher + '\'' +
                ", year='" + year + '\'' +
                ", pages='" + pages + '\'' +
                ", language='" + language + '\'' +
                ", size='" + size + '\'' +
                ", fileFormat='" + fileFormat + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", mirror='" + mirror + '\'' +
                '}';
    }
}