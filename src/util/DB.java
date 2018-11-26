package util;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class DB {
    Connection conn;
    Statement stmt;
    PreparedStatement pstmt;
  public DB(){
    String driver = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://192.168.32.128:3306/library";
    String username = "root";
    String password = "623672285";
    try {
        Class.forName(driver); //classLoader,加载对应驱动
        conn = (Connection) DriverManager.getConnection(url, username, password);
        System.out.println(conn);
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    } catch (SQLException e) {
        e.printStackTrace();
    }
  }
  public ResultSet query(String op){
      ResultSet rs=null;
     try{
         stmt = conn.createStatement();
         rs=stmt.executeQuery(op);
     } catch(SQLException E){
         E.printStackTrace();
     }
     return rs;
  }

  public int executeupdate(String op){
      int i=-1;
      PreparedStatement pstmt=null;
      try{
          pstmt = (PreparedStatement) conn.prepareStatement(op);
          i = pstmt.executeUpdate();
          pstmt.close();
      } catch(SQLException e){
          e.printStackTrace();
      }
      return i;
  }

  public void closestate(){
      try {
          if (stmt != null) stmt.close();
          if (pstmt != null) pstmt.close();
      } catch (SQLException e){
          e.printStackTrace();
      }
  }
    @Override
    protected void finalize(){
      try {
          conn.close();
      } catch(SQLException e){
          e.printStackTrace();
      }
    }
}
