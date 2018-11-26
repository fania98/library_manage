import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import util.DB;

public class userServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        HttpSession session=request.getSession();
        Object ulicense=session.getAttribute("ulicense");
        Object uname=session.getAttribute("uname");
        String license=(String)ulicense;
        String name=(String)uname;
        System.out.println(name);
        PrintWriter pw=response.getWriter();
        System.out.println(request.getParameter("current"));
        if(request.getParameter("current").equals("true")){
            System.out.println("输出了当前用户");
            DB db=new DB();
            ResultSet rs=db.query("SELECT * FROM reader WHERE license="+license);
            JSONObject info = new JSONObject();
            try {
                while (rs.next()){
                    info.put("uname",name);
                    info.put("ulicense",license);
                    String temp=rs.getString("ugender");
                    info.put("ugender",temp);
                    temp=rs.getString("udepart");
                    info.put("udepart",temp);
                    temp=rs.getString("ugrade");
                    info.put("ugrade",temp);
                    temp=rs.getString("upassword");
                    info.put("upassword",temp);
                    temp=rs.getString("permission");
                    info.put("permission",temp);
                }
                pw.print(info);
                rs.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        else{


        }

    }
}
