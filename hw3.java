import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.Vector;
import java.util.List;

public class hw3 extends JFrame {

    private JPanel main_panel;
    private JPanel Movie;
    private JPanel Result_Screen;
    private JPanel Genre;
    private JPanel Country;
    private JPanel Location;
    private JPanel Ratings;
    private JPanel Query;
    private JPanel results;
    private JScrollPane CountryScroll;
    private JButton updateCountryButton;
    private JButton LocationButton;
    private JComboBox RatingRange;
    private JComboBox NumRatingRange;
    private JTextField RatingValue;
    private JTextField numRatingField;
    private JButton executeQueryButton;
    private JTextArea CountResult;
    private JList ResultArea;
    private JPanel SearchPanel;
    private JComboBox SearchCriteria;
    private JTextField MovieYearFrom;
    private JTextField MovieYearTo;
    private JPanel TagPanel;
    private JTextField tagValueField;
    private JComboBox TagWeightRange;
    private JTextArea tagCountArea;
    private JTextArea QueryArea;
    private JList GenreList;
    private JList CountryList;
    private JList LocationList;
    private JLabel YearLabel;
    private JScrollPane GenreScroll;
    private JTextArea DisplayArea;
    private JButton DisplayButton;
    private JList TagList;
    private JLabel QLabel;
    private JScrollPane QueryScroll;
    private JTable GenreTable;
    static Connection conn;
    static String finalGenreQuery;
    static String finalCountryQuery;
    static String finalLocationQuery;
    static String selectedMid;
    static String additionalGenres;
    static String currentCriticsQuery;
    Vector<String> res_area= new Vector<String>();
    List<String> selectedGenre;// = new List<String>();
    List<String> selectedCountry;// = new List<String>();

    public static void main(String[] args)
    {
        hw3 gui = new hw3();
//        try{
//            conn.close();
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
    }

    public hw3() {
        setTitle("Movie Application");
        setSize(1000, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Genre.setBackground(Color.YELLOW);
        Country.setBackground(Color.GREEN);
        Location.setBackground(Color.CYAN);
        Ratings.setBackground(Color.ORANGE);
        TagPanel.setBackground(Color.PINK);
        Movie.setBackground(Color.red);

        SearchCriteria.addItem("AND");
        SearchCriteria.addItem("OR");
        RatingRange.addItem("=");
        RatingRange.addItem("<");
        RatingRange.addItem(">");
        RatingRange.addItem(">=");
        RatingRange.addItem("<=");
        NumRatingRange.addItem("=");
        NumRatingRange.addItem("<");
        NumRatingRange.addItem(">");
        NumRatingRange.addItem(">=");
        NumRatingRange.addItem("<=");
        TagWeightRange.addItem("=");
        TagWeightRange.addItem("<");
        TagWeightRange.addItem(">");
        TagWeightRange.addItem(">=");
        TagWeightRange.addItem("<=");

        main_panel.setBackground(Color.BLACK);
        main_panel.add(Movie);
        main_panel.setVisible(true);
        Genre.setBorder(BorderFactory.createMatteBorder(1,1,2,1,Color.BLACK));
        Country.setBorder(BorderFactory.createMatteBorder(1,1,2,1,Color.BLACK));
        Location.setBorder(BorderFactory.createMatteBorder(1,1,2,1,Color.BLACK));
        Ratings.setBorder(BorderFactory.createMatteBorder(1,1,2,1,Color.BLACK));
        TagPanel.setBorder(BorderFactory.createMatteBorder(1,1,2,1,Color.BLACK));
        Query.setBorder(BorderFactory.createMatteBorder(1,1,2,2,Color.BLACK));
        Result_Screen.setBorder(BorderFactory.createMatteBorder(1,1,2,2,Color.BLACK));
        add(main_panel);
        setVisible(true);
        populateGenre();
        GenreList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedGenre = GenreList.getSelectedValuesList();
                CountryList.removeAll();
                LocationList.removeAll();
                additionalGenres = "";
                String genreQuery =  "";

                if (SearchCriteria.getSelectedItem() == "AND") {
                    for(int i = 0; i < selectedGenre.size(); ++i) {
                        if (i == selectedGenre.size() - 1)
                            additionalGenres = additionalGenres + "'" + selectedGenre.get(i) + "'";
                        else
                            additionalGenres = additionalGenres + "'" + selectedGenre.get(i) + "', ";
                    }
                    genreQuery = "(SELECT DISTINCT(mid) \nFROM Movie_Genre \nWHERE genre IN ("+additionalGenres+")"
                            + "\nGROUP BY mid " + "\nHAVING COUNT(genre) = " + selectedGenre.size()+ ")";
                }
                else if (SearchCriteria.getSelectedItem() == "OR") {
                    for(int i = 0; i < selectedGenre.size(); ++i) {
                        if (i == selectedGenre.size() - 1)
                            additionalGenres = additionalGenres + "genre = '" + selectedGenre.get(i) + "'";
                        else
                            additionalGenres = additionalGenres + "genre = '" + selectedGenre.get(i) + "' OR ";
                    }
                    genreQuery = "SELECT DISTINCT(mid) \nFROM Movie_Genre \n WHERE " + additionalGenres;
                }
                System.out.println(genreQuery);
                finalGenreQuery = genreQuery;
                QueryArea.setText(genreQuery);
            }
        });
        executeQueryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ResultArea.removeAll();
                    ResultArea.removeSelectionInterval(0, ResultArea.getModel().getSize() -1 );
                    CountResult.setText(null);
                    //TODO: create prepared statement query method
                    res_area.clear();
                    ResultSet rs = exeQuery(QueryArea.getText());
                    int count = 0;

                    while (rs.next()) {
                        res_area.add(rs.getString("mid") + "\n");
                        count++;
                    }
                    ResultArea.setListData(res_area);
                    CountResult.setText(String.valueOf(count));
                    if(count == 0)
                        JOptionPane.showMessageDialog(null, "No results found!");
                    Vector<String> tag_res= new Vector<String>();
                    String tagQuery = "SELECT distinct(tagtext) FROM Movie_tags M INNER JOIN(" +
                            "SELECT T.tagid FROM Tag_Weight T INNER JOIN ( " +
                            QueryArea.getText() +") AS s ON T.mid = s.mid) AS d ON M.tagid = d.tagid";

                    int tagCount = 0;
                    System.out.println(tagQuery);
                    ResultSet tag_rs = exeQuery(tagQuery);
                    while (tag_rs.next()) {
                        tagCount++;
                        tag_res.add(tag_rs.getString("tagtext") + "\n");
                    }
                    TagList.setListData(tag_res);
                    tagCountArea.setText(String.valueOf(tagCount));
                    DisplayArea.setText(null);
                }
                 catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });
        updateCountryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CountryList.removeAll(); //clear the list first
                String q = "";
                String selected_genre = "";
                String criteria = "";
                try {

                    if (SearchCriteria.getSelectedItem() == "AND") {
                        for(int i = 0; i < selectedGenre.size(); ++i)
                        {
                            if (i == selectedGenre.size() - 1) //if last elem
                                selected_genre = selected_genre + "'" + selectedGenre.get(i) + "'";
                            else
                                selected_genre = selected_genre + "'" + selectedGenre.get(i) + "', ";
                        }
                        q = "SELECT DISTINCT(country_origin) \nFROM Movie_Countries \n" +
                                "WHERE mid IN (SELECT mid FROM Movie_Genre WHERE genre IN (" + selected_genre +
                                ") \nGROUP BY mid " + "\nHAVING COUNT(genre) = " + selectedGenre.size() + ")";
                    }
                    else if (SearchCriteria.getSelectedItem() == "OR")
                    {
                        for(int i = 0; i < selectedGenre.size(); ++i)
                        {
                            if (i == selectedGenre.size() - 1) //if last elem
                                selected_genre = selected_genre + "genre = '" + selectedGenre.get(i) + "')";
                            else
                                selected_genre = selected_genre + "genre = '" + selectedGenre.get(i) + "' OR ";
                        }
                        q = "SELECT DISTINCT(country_origin) FROM Movie_Countries WHERE mid IN (" +
                                "SELECT mid FROM Movie_genre WHERE " + selected_genre;
                    }
                    System.out.println(q);
                    Vector<String> countries = new Vector<String>();
                    ResultSet rs = exeQuery(q);
                    while (rs.next()) {
                        countries.add(rs.getString("country_origin"));
                    }
                    CountryList.setListData(countries);
                }
                catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });
        CountryList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                try{
                    selectedCountry = CountryList.getSelectedValuesList();
                    String countryQuery = "";
                    String additionalCountries = "";
                    if(selectedCountry.size() > 0) {
                        if (SearchCriteria.getSelectedItem() == "AND") {
                            for (int i = 0; i < selectedCountry.size(); ++i) {
                                if (i == selectedCountry.size() - 1)
                                    additionalCountries = additionalCountries + "country_origin ='" + selectedCountry.get(i) + "'";
                                else
                                    additionalCountries = additionalCountries + "country_origin ='" + selectedCountry.get(i) + "' AND ";
                            }
                            countryQuery = "SELECT mid \nFROM Movie_Countries \nWHERE mid IN " + finalGenreQuery +
                                    " \nAND (" + additionalCountries + ")";

                        } else if (SearchCriteria.getSelectedItem() == "OR") {
                            for (int i = 0; i < selectedCountry.size(); ++i) {
                                if (i == selectedCountry.size() - 1)
                                    additionalCountries = additionalCountries + "country_origin = '" + selectedCountry.get(i) + "'";
                                else
                                    additionalCountries = additionalCountries + "country_origin = '" + selectedCountry.get(i) + "' OR ";
                            }
                            countryQuery = "SELECT mid \nFROM Movie_Countries \nWHERE mid IN (" + finalGenreQuery +
                                    ") \nAND (" + additionalCountries + ")";
                        }
                        System.out.println(countryQuery);
                        finalCountryQuery = countryQuery;
                        QueryArea.setText(countryQuery);
                    }
                }
                catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });
        LocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocationList.removeAll(); //clear the list first

                try {
                    List<String> t = GenreList.getSelectedValuesList();
                    String base_q = "SELECT DISTINCT(country) \nFROM Filming_Location F INNER JOIN (" + QueryArea.getText() +
                            ") AS Q ON Q.mid = F.mid";
                    Vector<String> countries = new Vector<String>();
                    System.out.println(base_q);
                    ResultSet rs = exeQuery(base_q);
                    while (rs.next()) {
                        //System.out.println(rs.getString("country_origin"));
                        countries.add(rs.getString("country"));
                    }
                    LocationList.setListData(countries);
                    finalLocationQuery = base_q;
                }
                catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });
        LocationList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                try{
                    List<String> selected = LocationList.getSelectedValuesList();
                    String additionalCountries = "";
                    String countryQuery = "";
                    String countryUsed = "";
                    if(CountryList.getModel().getSize() > 0)
                        countryUsed = finalCountryQuery;
                    else
                        countryUsed = finalGenreQuery;

                    if (SearchCriteria.getSelectedItem() == "AND") {
                        for (int i = 0; i < selected.size(); ++i) {
                            if (i == selected.size() - 1)
                                additionalCountries = additionalCountries + "'" + selected.get(i) + "'";
                            else
                                additionalCountries = additionalCountries + "'" + selected.get(i) + "' ,";
                        }
                        countryQuery = "SELECT DISTINCT(mid) \nFROM Filming_Location \nWHERE mid IN (" + countryUsed +
                                ") \nAND country IN (" + additionalCountries + ") GROUP BY mid HAVING count(distinct country)" +
                                " = " + selected.size();
                    }
                    else if (SearchCriteria.getSelectedItem() == "OR"){
                        for (int i = 0; i < selected.size(); ++i) {
                            if (i == selected.size() - 1)
                                additionalCountries = additionalCountries + "country ='" + selected.get(i) + "'";
                            else
                                additionalCountries = additionalCountries + "country ='" + selected.get(i) + "' OR ";
                        }
                        countryQuery = "SELECT DISTINCT(mid) \nFROM Filming_Location \nWHERE mid IN (" + countryUsed +
                                ") \nAND (" +additionalCountries + ")";
                    }
                    QueryArea.setText(countryQuery);
                }
                catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });
        ResultArea.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                List<String> selected = ResultArea.getSelectedValuesList();
                selectedMid = "";
                for (int i = 0; i < selected.size(); ++i) {
                    if (i == selected.size() - 1)
                        selectedMid = selectedMid + "" + selected.get(i) + "";
                    else
                        selectedMid = selectedMid + "" + selected.get(i) + ", ";
                }
            }
        });
        DisplayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String q1 = "SELECT title, year, rating, num_reviews FROM Movie where mid IN (" + selectedMid + ")";
                    String q2 = "SELECT genre FROM Movie_genre WHERE mid IN (" + selectedMid +
                            ") GROUP BY genre HAVING COUNT(mid) = " + ResultArea.getSelectedValuesList().size();
                    String q3 = "SELECT country_origin FROM Movie_Countries WHERE mid IN (" + selectedMid + ")";
                    String q4 = "SELECT distinct(country) FROM Filming_Location WHERE mid IN (" + selectedMid + ")";
                    System.out.println(q1);
                    System.out.println(q2);
                    System.out.println(q3);
                    System.out.println(q4);
                    ResultSet rs1 = exeQuery(q1);
                    ResultSet rs2 = exeQuery(q2);
                    ResultSet rs3 = exeQuery(q3);
                    ResultSet rs4 = exeQuery(q4);
                    System.out.println(q2);
                    System.out.println(q4);
                    while (rs1.next()){ //&& rs2.next() && rs3.next() && rs4.next()) {
                        DisplayArea.append("Title: " + rs1.getString("title") + "\n");
                        DisplayArea.append("Year: " + rs1.getString("year") + "\n");
                        DisplayArea.append("Avg Rating: " + rs1.getString("rating") + "\n");
                        DisplayArea.append("Avg Num Reviews: " + rs1.getString("num_reviews") +"\n");
                    }
                    DisplayArea.append("Genre: ");
                    while (rs2.next()){
                        DisplayArea.append(rs2.getString("genre") + " ");
                    }
                    while (rs3.next())
                        DisplayArea.append("\nCountry: " + rs3.getString("country_origin") +"\n");
                    DisplayArea.append("F_Location: ");
                    while (rs4.next())
                        DisplayArea.append(rs4.getString("country") +" ");
                    DisplayArea.append("\n");
                }
                catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });
        tagValueField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                String sign = "";
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    if(TagWeightRange.getSelectedItem() == "=")
                        sign = "=";
                    else if (TagWeightRange.getSelectedItem() == "<")
                        sign = "<";
                    else if (TagWeightRange.getSelectedItem() == ">")
                        sign = ">";
                    else if (TagWeightRange.getSelectedItem() == "<=")
                        sign = "<=";
                    else if (TagWeightRange.getSelectedItem() == ">=")
                        sign = ">=";
                    try {
                        String tags = "";
                        if (TagList.getModel().getSize() > 0)
                        {
                            for (int i = 0; i < TagList.getModel().getSize(); ++i) {
                                if (i == TagList.getModel().getSize() - 1) {//if last elem
                                    tags = tags + "'" + TagList.getModel().getElementAt(i) + "'";
                                }
                                else
                                    tags = tags + "'"+ TagList.getModel().getElementAt(i) + "', ";
                                tags = tags.replace("\n", "").replace("\r", "");
                            }
                        }
                        String q = "SELECT distinct(tagtext) FROM Movie_Tags M, Tag_Weight T\n" +
                                "WHERE M.tagid = T.tagid AND T.tagweight " + sign + " " + tagValueField.getText() +
                                " AND tagtext in (" + tags + ")";
                        System.out.println(q);
                        ResultSet rs = exeQuery(q);
                        int count = 0;
                        Vector<String> res = new Vector<String>();
                        while (rs.next()) {
                            count++;
                            res.add(rs.getString("tagtext") + "\n");
                        }
                        tagCountArea.setText(String.valueOf(count));
                        TagList.setListData(res);

                    } catch (Exception ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            }
        });
        RatingValue.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                String sign = "";
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (RatingRange.getSelectedItem() == "=")
                        sign = "=";
                    else if (RatingRange.getSelectedItem() == "<")
                        sign = "<";
                    else if (RatingRange.getSelectedItem() == ">")
                        sign = ">";
                    else if (RatingRange.getSelectedItem() == "<=")
                        sign = "<=";
                    else if (RatingRange.getSelectedItem() == ">=")
                        sign = ">=";
                    try {
                        String q = "SELECT M.mid FROM Movie M, (" + QueryArea.getText()
                                + ") AS Q WHERE M.mid = Q.mid AND M.rating " + sign + " " + RatingValue.getText();
                        System.out.println(q);
                        currentCriticsQuery = q;
                        QueryArea.setText(q);
                    } catch (Exception ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            }
        });
        numRatingField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                String sign = "";
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (NumRatingRange.getSelectedItem() == "=")
                        sign = "=";
                    else if (NumRatingRange.getSelectedItem() == "<")
                        sign = "<";
                    else if (NumRatingRange.getSelectedItem() == ">")
                        sign = ">";
                    else if (NumRatingRange.getSelectedItem() == "<=")
                        sign = "<=";
                    else if (NumRatingRange.getSelectedItem() == ">=")
                        sign = ">=";
                    String q;
                    if (RatingValue.getText().equals("")) {
                        System.out.println("EMPTY");
                        q =  "SELECT M.mid FROM Movie M INNER JOIN (" + QueryArea.getText() +") AS Q ON M.mid = Q.mid" +
                                " WHERE M.num_reviews " + sign + " " + numRatingField.getText();
                        currentCriticsQuery = q;
                    }
                    else
                        q = currentCriticsQuery + " AND M.num_reviews " + sign + " " + numRatingField.getText();
                    System.out.println(q);
                    QueryArea.setText(q);
                }
            }
        });

        MovieYearFrom.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                String q = "";
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!RatingValue.getText().equals("") ||!numRatingField.getText().equals(""))
                    { // if not empty, user just preses enter on "FROM"
                        q = currentCriticsQuery + " AND M.year >= " + MovieYearFrom.getText();
                    }
                    else {
                        q = "SELECT M.mid FROM Movie M INNER JOIN (" + QueryArea.getText() +") AS Q ON M.mid = Q.mid" +
                                " WHERE year >= " + MovieYearFrom.getText();
                    }
                    System.out.println(q);
                    QueryArea.setText(q);
                }
            }
        });
        MovieYearTo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                String q = "";
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if ((!RatingValue.getText().equals("") ||!numRatingField.getText().equals("")) && MovieYearFrom.getText().equals(""))
                    { // if already query and FROM is empty
                        q = currentCriticsQuery + " AND M.year < " + MovieYearFrom.getText();
                    }
                    else if ((!RatingValue.getText().equals("") ||!numRatingField.getText().equals("")) && !MovieYearFrom.getText().equals(""))
                    { // if already query and FROM is NOT empty
                        q = currentCriticsQuery + " AND M.year >= " + MovieYearFrom.getText() + " AND M.year < " + MovieYearTo.getText();
                    }
                    else if (RatingValue.getText().equals("") && numRatingField.getText().equals("") && MovieYearFrom.getText().equals("")){
                        // if ALL are empty, create query
                        q = "SELECT M.mid FROM Movie M INNER JOIN (" + QueryArea.getText() +") AS Q ON M.mid = Q.mid" +
                                " WHERE year < " + MovieYearTo.getText();
                    }
                    else if (RatingValue.getText().equals("") && numRatingField.getText().equals("") && !MovieYearFrom.getText().equals("")){
                        q = "SELECT M.mid FROM Movie M INNER JOIN (" + QueryArea.getText() +") AS Q ON M.mid = Q.mid" +
                                " WHERE year BETWEEN " + MovieYearFrom.getText() + " AND " + MovieYearTo.getText();
                    }
                    System.out.println(q);
                    QueryArea.setText(q);
                }

            }
        });
    }
    public void populateGenre(){
        try {
            conn = openConnection();
            String genreQuery = "SELECT DISTINCT(genre) FROM Movie_Genre";
            Vector<String> genres = new Vector<String>();
            ResultSet rs = exeQuery(genreQuery);
            while (rs.next()) {
                genres.add(rs.getString("genre"));
            }
            GenreList.setListData(genres);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    private static ResultSet exeQuery(String query) {
        ResultSet rs = null;
        try{
            Statement st = conn.createStatement();
            rs = st.executeQuery(query);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return rs;
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
        String password = "";
        System.out.println("Connecting....");
        // Construct the JDBC URL
        //String dbURL = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + dbName;
        String dbURL = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
        try {
            Connection conn = DriverManager.getConnection(dbURL, userName, password);
            System.out.println("connection success");
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
