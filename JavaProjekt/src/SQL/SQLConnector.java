package SQL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLConnector {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/test";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "";

    private SQLResult GetSQLResult(String query, QueryType queryType){
        Connection con = null;
        Statement stmt = null;

        SQLResult r = new SQLResult();
        try{
            //Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //Open a connection
            con = DriverManager.getConnection(DB_URL, USER, PASS);

            //Execute a query
            stmt = con.createStatement();

            String sql = query;
            System.out.println(sql);
            //set default return state
            r.status = SQL_Status.QueryPass;


            if(queryType == QueryType.Select) {
                ResultSet rs = stmt.executeQuery(sql);

                //init return object list
                r.resultList = new ArrayList<List<String>>();


                while (rs.next()) {
                    r.resultList.add(new ArrayList<>());
                    for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                        String value = rs.getString(i + 1);
                        //System.out.println(value);
                        r.resultList.get(rs.getRow() - 1).add(value);
                    }
                    r.status = SQL_Status.QueryPass;
                }
                rs.close();
            }
            else if(queryType == QueryType.Insert){
                // insert the data
                int b = stmt.executeUpdate(sql);
                System.out.println(b);
                r.status = SQL_Status.QueryPass;
            }
            else if(queryType == QueryType.Delete){
                // Delete the data
                int b = stmt.executeUpdate(sql);
                System.out.println(b);
                r.status = SQL_Status.QueryPass;
            }


            stmt.close();
            con.close();
            return  r;


        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
            r.status = SQL_Status.ConnectionError;
            //return r;
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
            r.status = SQL_Status.ConnectionError;
            //return r;
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(con!=null)
                    con.close();
            }catch(SQLException se){
                se.printStackTrace();
            }

            return r;
        }
    }

    public boolean CheckIfUserExist(String nick, String password) {
        String sql = String.format("SELECT * FROM users WHERE NICK= '%s' and PASSWORD= '%s';", nick, password);
        SQLResult rs = GetSQLResult(sql, QueryType.Select);
        return rs.status == SQL_Status.QueryPass;
    }

    public String GetRoomName(Integer roomId){
        String sql = String.format("SELECT name FROM rooms WHERE ID_ROOM= '%s';", roomId.toString());
        SQLResult rs = GetSQLResult(sql, QueryType.Select);

        //get 1st element of 1st row
        String name = rs.resultList.get(0).get(0);
        return name;
    }
    public SQLResult GetUser(Integer userid){
        String sql = String.format("SELECT NICK, AGE FROM users WHERE ID_USER= '%s';", userid.toString());
        SQLResult rs = GetSQLResult(sql, QueryType.Select);

        return rs;
    }
    public SQLResult GetRoomMembers(Integer roomId){
        String sql = String.format("SELECT * FROM roommembers WHERE ID_ROOMMEMBER= '%s';", roomId.toString());
        SQLResult rs = GetSQLResult(sql, QueryType.Select);

        //get 1st element of 1st row
        String name = rs.resultList.get(0).get(0);
        return rs;
    }
    public boolean AddNewRoomMember(Integer roomId, Integer userID){
        String sql = String.format("INSERT INTO roommembers (ID_ROOM, ID_USER) VALUES (%s, %s);", roomId.toString(), userID.toString());
        SQLResult rs = GetSQLResult(sql, QueryType.Insert);

        return rs.status == SQL_Status.QueryPass;
    }
}