import java.util.ArrayList;
import java.util.Scanner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        library.menu();
    }
}

class Book{
    private int id;
    private String name;
    private String author;
    private int year;
    private int quantity;
    private String genre;

    public Book(String name, String author, int year, int quantity, String genre) {
        this.name = name;
        this.author = author;
        this.year = year;
        this.quantity = quantity;
        this.genre = genre;
    }

    public Book(int id, String name, String genre, String author, int year, int quantity) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.author = author;
        this.year = year;
        this.quantity = quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void printBook(){
        System.out.println("Название: " + this.name);
        System.out.println("Жанр: " + this.genre);
        System.out.println("Автор: " + this.author);
        System.out.println("Год: " + this.year);
        System.out.println("Количество: " + this.quantity);
    }
}

class User{
    private int id;
    private String name;
    private String group;
    private ArrayList<Book> borrowedBooks = new ArrayList<>();

    public User(String name, String group) {
        this.name = name;
        this.group = group;
    }

    public User(int id, String name, String group) {
        this.id = id;
        this.name = name;
        this.group = group;
    }

    public ArrayList<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(ArrayList<Book> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }
    public String getName() {
        return name;
    }
    public void printUser(){
        System.out.println("ID: " + this.id);
        System.out.println("Имя: " + this.name);
        System.out.println("Группа: " + this.group);
        System.out.print("Книги: ");
        for (Book borrowedBook : borrowedBooks) {
            System.out.println(borrowedBook.getName());
        }
    }

    public int getId() {
        return id;
    }
}

class Library{
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/library";
    static final String USER = "postgres";
    static final String PASS = "123456";
    private Connection connection;
    private Scanner in = new Scanner(System.in);
    ArrayList<User> users;
    ArrayList<Book> books;

    public Library() {
        users = new ArrayList<>();
        books = new ArrayList<>();
        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users")) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("first_name");
                String group = rs.getString("group");
                users.add(new User(id,name,group));

                try (PreparedStatement pst = connection.prepareStatement("SELECT b.id, b.name, b.author, b.year, b.quantity, b.genre \n" +
                        "FROM books b\n" +
                        "LEFT JOIN user_book ub\n" +
                        "    ON b.Id = ub.book_id\n" +
                        "LEFT JOIN users u\n" +
                        "    ON ub.user_id = u.Id\n" +
                        "WHERE\n" +
                        "    u.id =  ?;")) {
                    pst.setInt(1, id);
                    ResultSet res = preparedStatement.executeQuery();
                    ArrayList<Book> lst = new ArrayList<>();
                    while (res.next()) {
                        int id1 = rs.getInt("id");
                        String name1 = rs.getString("name");
                        String author = rs.getString("author");
                        int year = rs.getInt("year");
                        int quantity = rs.getInt("quantity");
                        String genre = rs.getString("genre");
                        lst.add(new Book(id1,name1,genre, author,year,quantity));
                    }
                    users.get(users.size()-1).setBorrowedBooks(lst);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM books")) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String author = rs.getString("author");
                String genre = rs.getString("genre");
                int year = rs.getInt("year");
                int quantity = rs.getInt("quantity");
                books.add(new Book(id,name,genre,author,year,quantity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void usersMenu(){
        System.out.println();
        for(int i = 0; i < users.size(); i++) {
            System.out.println(i+1 + "." + users.get(i).getName());
        }
        System.out.println("0.Вернуться в меню");
        int n = in.nextInt() -1 ;
        if(n+1 == 0|| n >= users.size() || n < 0){
            menu();
        } else {
            users.get(n).printUser();
            menu();
        }
    }
    public void booksMenu(){
        System.out.println();
        for(int i = 0; i < books.size(); i++) {
            System.out.println(i+1 + "." + books.get(i).getName());
        }
        System.out.println("0.Вернуться в меню");
        int n = in.nextInt()-1;
        if(n+1 == 0 || n >= users.size() || n < 0){
            menu();
        } else {
            books.get(n).printBook();
            menu();
        }
    }
    public void addBookToUser(){
        Book bookie;
        System.out.println();
        System.out.println("Выберите книгу: ");
        for(int i = 0; i < books.size(); i++) {
            System.out.println(i+1 + "." + books.get(i).getName());
        }
        System.out.println("0.Вернуться в меню");
        int m = in.nextInt()-1;
        if(m+1 == 0 || m > books.size() || m < 0){
            menu();
        } else {
            bookie = books.get(m);
            System.out.println("Выберите студента: ");
            for(int i = 0; i < users.size(); i++) {
                System.out.println(i+1 + "." + users.get(i).getName());
            }
            System.out.println("0.Вернуться в меню");
            int n = in.nextInt()-1;
            if(n+1 == 0 || n >= users.size() || n < 0){
                menu();
            } else {
                User us = users.get(n);
                books.get(m).setQuantity(bookie.getQuantity()-1);
                if(books.get(m).getQuantity() == 0){
                    books.remove(m);
                }
                ArrayList<Book> arr = us.getBorrowedBooks();
                arr.add(bookie);
                us.setBorrowedBooks(arr);
                menu();
            }
        }
    }
    public void returnBookFromUser(){
        User us;
        System.out.println();
        System.out.println("Выберите студента: ");
        for(int i = 0; i < users.size(); i++) {
            System.out.println(i+1 + "." + users.get(i).getName());
        }
        System.out.println("0.Вернуться в меню");
        int m = in.nextInt()-1;
        if(m+1 == 0 || m >= users.size() || m < 0){
            menu();
        } else {
            us = users.get(m);
            System.out.println("Выберите книгу: ");
            for(int i = 0; i < us.getBorrowedBooks().size(); i++) {
                System.out.println(i+1 + "." + us.getBorrowedBooks().get(i).getName());
            }
            System.out.println("0.Вернуться в меню");
            int n = in.nextInt()-1;
            if(n+1 == 0 || n >= us.getBorrowedBooks().size() || n < 0){
                menu();
            } else {
                Book bookie = us.getBorrowedBooks().get(n);
                ArrayList<Book> arr = us.getBorrowedBooks();
                arr.remove(bookie);
                us.setBorrowedBooks(arr);
                if(!books.contains(bookie)){
                    books.add(bookie);
                }else{
                    books.get(books.indexOf(bookie)).setQuantity(books.get(books.indexOf(bookie)).getQuantity()+1);
                }
                menu();
            }
        }
    }
    public void addUser(){
        System.out.println();
        System.out.print("Имя: ");
        in.nextLine();
        String name = in.nextLine();
        System.out.print("Группа: ");
        String group = in.nextLine();
        User us = new User(name,group);
        users.add(us);
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users(first_name,group) VALUES(?, ?)")) {
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,group);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        menu();
    }

    public void addBook(){
        System.out.println();
        System.out.print("Название: ");
        in.nextLine();
        String title = in.nextLine();
        System.out.print("Жанр: ");
        String genre = in.nextLine();
        System.out.print("Автор: ");
        String author = in.nextLine();
        System.out.print("Год: ");
        int year = in.nextInt();
        System.out.print("Количество: ");
        int quantity = in.nextInt();
        Book bk = new Book(title,author,year,quantity,genre);
        books.add(bk);
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO books(name,author,year,quantity,genre) VALUES(?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1,title);
            preparedStatement.setString(2,author);
            preparedStatement.setInt(3,year);
            preparedStatement.setInt(4,quantity);
            preparedStatement.setString(5,genre);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        menu();
    }
    public void menu(){
        System.out.println();
        System.out.println("1.Показать все книги\n2.Показать всех студентов\n3.Дать книгу студенту\n4.Забрать книгу у студента\n5.Добавить студента\n6.Добавить книгу\n0.Выход");
        int n = in.nextInt();
        switch (n){
            case 1:
                booksMenu();
            case 2:
                usersMenu();
            case 3:
                addBookToUser();
            case 4:
                returnBookFromUser();
            case 5:
                addUser();
            case 6:
                addBook();
            case 0:
                System.exit(0);
            default:
                System.exit(0);
        }
    }

}