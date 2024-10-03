import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

class Book {
    String id;
    String name;
    String title;
    String author;
    int year;
    String genre;
    Price price;
    String isbn;
    Reviews review;
    Awards awards;
    String translator;
    String format;
    String langueage;
    Publisher publisher;
    

    public Book(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return ("\n\nBook ID: " + id + "\nTitle: " + title + "\nAuthor: " + author + "\nYear: " + year +
        "\nGenre: " + genre + "\nPrice: " + price + "\nIsbn: " + isbn + "\n" + review +  "\n" + awards + "\nTranslator: " + translator + 
        "\nFormat: " + format + "\nLanguage: " + langueage + "\nPublisher " + publisher );
    }
}


class Price {
    String currency;
    double value;

    public Price(String currency, double value){
        this.currency = currency;
        this.value = value;
    }

    @Override
    public String toString() {
        return value + " " + currency;
    }
}

class Reviews {
    List<Review> review = new ArrayList<>();

    @Override
    public String toString(){
        return "Review: " + review;
    }
}


class Review {
    String user;
    double rating;
    String comment;

    public Review(String user, double rating, String comment){
        this.user = user;
        this.rating = rating;
        this.comment = comment;
    }

    @Override
    public String toString(){
        return ("\nUser: " + user + " Rating: " + rating + " Comment: " + comment ); 
    }
}

class Awards {
    List<Award> award = new ArrayList<>();

    @Override
    public String toString(){
        return "Award: " + award;
    }
}

class Award {
    String name;

    public Award(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name; 
    }
}



class Publisher {
    String name;
    Address address;

    public Publisher(String name, Address address){
        this.name = name;
        this.address = address;
    }

    @Override
    public String toString(){
        return "\nName: " + name + "\nAddress: " + address;
    }
}


class Address {
    String city;
    String country;

    public Address(String city, String country){
        this.city = city;
        this.country = country;
    }

    @Override
    public String toString(){
        return " City: " + city + " Country " + country;
    }
}


class Library {
    List<Book> books = new ArrayList<>();

    @Override
    public String toString() {
        return "Library: " + books;
    }
}


public class XML{
    public static void main(String[] args){
        String filePath = args[0];
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line); // Добавляем каждую строку в список
            }
        } catch (IOException e) {
            e.printStackTrace(); // Обработка исключений
        }
        String xml = String.join("\n", lines).replaceAll("\"", "'");
        
        Library library = Parse(xml);
        System.out.println(library);
    }

    public static Library Parse(String xml){
        Library library = new Library();
        String currentTag = null;
        Stack<String> stack = new Stack<>();
        Book currentBook = null;
        Price currentPrice = null;
        Reviews reviews = new Reviews();
        Review currentReview = null;
        Publisher currentPublisher = null;
        Address currentAddress = null;
        Awards awards = new Awards();
        Award currentAward = null;

        for (int i = 0; i < xml.length(); i++) {
            if (xml.charAt(i) == '<') {
                int closing = xml.indexOf('>', i);
                String tag = xml.substring(i + 1, closing);
                
                if (tag.startsWith("/")){
                    currentTag = stack.pop();
                    if (currentTag.split(" ")[0].equals("book")) {
                        library.books.add(currentBook);
                        currentBook = null;
                    } else if (currentTag.split(" ")[0].equals("price")){
                        currentBook.price = currentPrice;
                        currentPrice = null;
                    } else if (currentTag.equals("reviews")){
                        currentBook.review = reviews;
                        reviews = new Reviews();
                    } else if (currentTag.equals("review")) {
                        reviews.review.add(currentReview);
                        currentReview = null;
                    } else if (currentTag.equals("address")) {
                        currentPublisher.address = currentAddress;
                        currentAddress = null;
                        currentBook.publisher = currentPublisher;
                        currentPublisher = null;
                    } else if (currentTag.equals("award")){
                        awards.award.add(currentAward);
                        currentAward = null;
                    } else if (currentTag.equals("awards")){
                        currentBook.awards = awards;
                        awards = new Awards();
                    }
                } 

                else {
                    stack.push(tag);
                    currentTag = tag.split(" ")[0];

                    if (currentTag.equals("book")) {
                        String id = tag.split("'")[1];
                        currentBook = new Book(id);
                    } else if (currentTag.equals("review")) {
                        currentReview = new Review("anonim", 0.0, "null");
                    } else if (currentTag.equals("publisher")) {
                        currentPublisher = new Publisher("null",   null);
                    } else if (currentTag.equals("address")) {
                        currentAddress = new Address("null", "null");
                    } else if (currentTag.equals("price")) {
                        String currency = tag.split("'")[1];
                        currentPrice = new Price(currency, 0.0);
                    } else if (currentTag.equals("award")){
                        currentAward = new Award("null");
                    }
                }
                i = closing;

            } else if (!stack.isEmpty()){
                int closing = xml.indexOf('<', i);
                String content = xml.substring(i, closing).trim();

                if (!content.isEmpty()){
                    switch (currentTag) {
                        case "title": currentBook.title = content; break;
                        case "author": currentBook.author = content; break;
                        case "year": currentBook.year = Integer.parseInt(content); break;
                        case "genre": currentBook.genre = content; break;
                        case "price": currentPrice.value = Double.parseDouble(content); break;
                        case "isbn": currentBook.isbn = content; break;
                        case "user": currentReview.user = content; break;
                        case "rating": currentReview.rating = Double.parseDouble(content); break;
                        case "comment": currentReview.comment = content; break;
                        case "translator": currentBook.translator = content; break;
                        case "format": currentBook.format = content; break;
                        case "language": currentBook.langueage = content; break;
                        case "name": currentPublisher.name = content; break;
                        case "city": currentAddress.city = content; break;
                        case "country": currentAddress.country = content; break;
                        case "award": currentAward.name = content; break;
                    }
                }
                i = closing - 1;
            }
        }
        return library;
    }
}
