package fr.epita.dao;

import fr.epita.datamodel.User;
import fr.epita.services.Configuration;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles CRUD operations for User
 * @author shubham
 * @author manish
 */
public class UserDAO {

    public static final Logger logger = Logger.getLogger("Logger.UserDAO");

    private static final String INSERT_QUERY = "INSERT INTO USER (NAME) VALUES (?)";
    private static final String SEARCH_QUERY = "SELECT * FROM USER WHERE NAME = ?";

    /**
     * Function to add a new user to the database
     * @param user an User object
     * @return "Success" if the user does not exist and
     * "User already exists!" if the user is already in the database.
     */
    public static String addUser(User user) {
        String status = "";
        User exists = search(user.getName());
        if(exists == null) {
            try (Connection connection = getConnection(); PreparedStatement insertStatement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
                insertStatement.setString(1,user.getName());
                insertStatement.execute();
                status = "Success";
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }else {
            status = "User already exists!";
        }

        return status;
    }

    /***
     * Search for the user in the database
     * @return null if user does not exists, user object if the user is found
     * @param  name  of the User
     */
    public static User search (String name){
        User foundUser = new User();
        try (Connection connection = getConnection();
             PreparedStatement searchStatement = connection.prepareStatement(SEARCH_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            searchStatement.setString(1, name);
            ResultSet resultSet = searchStatement.executeQuery();
            if(resultSet.next()){
                foundUser.setName(name);
                return foundUser;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }


        /***
         * creates the database connection
         * @return Connection
         * @throws SQLException,ClassNotFoundException
         */
        public static Connection getConnection () throws SQLException, ClassNotFoundException {

            Class.forName(Configuration.getValueFromKey("jdbc.driver"));
            String url = Configuration.getValueFromKey("jdbc.url");
            String username = Configuration.getValueFromKey("jdbc.username");
            String password = Configuration.getValueFromKey("jdbc.password");

            return DriverManager.getConnection(url, username, password);
        }

}

