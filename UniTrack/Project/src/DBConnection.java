
package pack1;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/unitrack", "root", "watchmeshine");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
