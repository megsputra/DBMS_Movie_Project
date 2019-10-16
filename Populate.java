//This program should get the names of the input files as command line parameters and
//populate them into your database. It should be executed as:
//“> java populate <filename1.dat> <filename2.dat>.....<filename.dat>”.
//Note that every time you run this program, it should remove the previous 
//data in your tables; otherwise the tables will have redundant data.
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.sql.*;

public class Populate {
    static Connection c;
    public static void main(String[] args) {
//        String[] filenames = {"movies.dat", "movie_genres.dat", "movie_countries.dat", "movie_locations.dat",
//         "tags.dat",  "movie_tags.dat"};
        try {
            System.out.println("Welcome");
            c = openConnection();
            System.out.println("Parsing...");
            for (String file: args) { //args
                parse(file);
                System.out.println(file + " is done");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Database successfully populated");
            closeConnection(c);
        }
    }
    public static void parse(String filename) {
        try {
            BufferedReader buf = new BufferedReader(new FileReader(filename));
            ArrayList<String> words = new ArrayList<>();
            String lineJustFetched = null;
            String[] wordsArray;
            buf.readLine(); //ignore file header
            int i = 0;
            while(true) {
                lineJustFetched = buf.readLine();
                if (lineJustFetched == null)
                    break;
                else {
                    wordsArray = lineJustFetched.split("\t");
                    insertIntoTable(wordsArray, filename);
                    //System.out.println(i);
                    i++;
                }
            }
            buf.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void insertIntoTable(String[] data, String filename) throws SQLException
    {

        if (filename == "movies.dat") {
            PreparedStatement stmt = c.prepareStatement("INSERT INTO Movie(mid, title, rating, num_reviews, year) VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, data[0]);
            stmt.setString(2, data[1]);
            stmt.setDouble(3, db_average(data[7], data[12], data[17]));
            stmt.setDouble(4, db_average(data[8], data[13], data[18]));
            stmt.setString(5, data[5]);
            stmt.executeUpdate();
        }
        else if (filename == "movie_genres.dat") {
           PreparedStatement stmt = c.prepareStatement("INSERT INTO Movie_Genre(mid, genre) VALUES (?, ?)");
            stmt.setString(1, data[0]);
            if (data[1] ==  null)
                stmt.setString(2, "");
            else
                stmt.setString(2, data[1]);
            stmt.executeUpdate();
        }
        else if (filename == "movie_countries.dat")	{
            PreparedStatement stmt = c.prepareStatement("INSERT INTO Movie_Countries(mid, country_origin) VALUES (?, ?)");
            for(int i = 0; i< data.length; ++i)
            {
                if(data[i] != null)
                    stmt.setString(i+1, data[i]);
                else
                    stmt.setString(i+1, "");
            }
            if (data.length ==1)
                stmt.setString(2, "");
            stmt.executeUpdate();
        }
        else if (filename == "movie_locations.dat") {
            PreparedStatement stmt = c.prepareStatement("INSERT INTO Filming_Location(mid, country) VALUES (?, ?)");
            if (data.length < 2) {
                stmt.setString(1, data[0]);
                stmt.setString(2, "");
            }
            else {
                for (int i = 0; i < 2; ++i) {
                    if (data[i] != null)
                        stmt.setString(i + 1, data[i]);
                    else
                        stmt.setString(i + 1, "");
                }
            }
            stmt.executeUpdate();
        }
        else if (filename == "movie_tags.dat") {
            PreparedStatement stmt = c.prepareStatement("INSERT INTO Tag_Weight(mid, tagid, tagWeight) VALUES (?, ?, ?)");
            stmt.setString(1, data[0]);
            stmt.setString(2, data[1]);
            stmt.setString(3, data[2]);
            stmt.executeUpdate();
        }
        else if (filename == "tags.dat") {
            PreparedStatement stmt = c.prepareStatement("INSERT INTO Movie_Tags (tagid, tagtext) VALUES (?, ?)");
            stmt.setString(1, data[0]);
            stmt.setString(2, data[1]);
            stmt.executeUpdate();
        }
    }
    private static double db_average(String allCritics, String topCritics, String audience)
    {
        if (allCritics == null || allCritics.isEmpty() || allCritics.contains("\\N"))
            allCritics = "0";
        if (topCritics == null || topCritics.isEmpty()|| topCritics.contains("\\N"))
            topCritics = "0";
        if (audience == null || audience.isEmpty() || audience.contains("\\N"))
            audience = "0";
        return (Double.parseDouble(allCritics) + Double.parseDouble(topCritics) + Double.parseDouble(audience)) / 3;
    }

    private static Connection openConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver error: " + e);
            return null;
        }
        String host = "localhost";
        String port = "3306";
        String dbName = "hw3?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC";
        String userName = "root";
        String password = "Shaquille123!";
        System.out.println("Connecting....");
        // Construct the JDBC URL
        //String dbURL = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + dbName;
        String dbURL = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
        try {
            Connection conn = DriverManager.getConnection(dbURL, userName, password);
            System.out.println("DB connection success!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Connection error: " + e);
            e.printStackTrace();
            return null;
        }
    }
    private static void closeConnection(Connection con) {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Cannot close connection: " + e.getMessage());
        }
    }
}