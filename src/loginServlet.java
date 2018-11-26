import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import util.DB;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

public class loginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        String license=request.getParameter("ulicense");
        String password=request.getParameter("password");
        System.out.print("id: "+license+" password: "+password);
        String realpassword=null;
        String name=null;
        String permission=null;
        PrintWriter pw = response.getWriter();
        DB db=new DB();
        ResultSet rs = db.query("SELECT uname,upassword,permission FROM reader WHERE license='"+license+"'");
        try{
            if(rs!=null) {
                while (rs.next()) {
                    realpassword = rs.getString("upassword");
                    System.out.println(realpassword);
                    name=rs.getString("uname");
                    permission=rs.getString("permission");
                }
                if(realpassword==null) {
                    pw.print("NOTEXIST");
                }
                else if(realpassword.equals(password)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("ulicense", license);
                    session.setAttribute("uname", name);
                    session.setAttribute("permission",permission);
                    System.out.println(session.getAttribute("uname"));
                    if(permission.equals("2"))
                        pw.print("ADMIN");
                    else pw.print("OK");
                }

                else {
                    pw.print("WRONG");
                }
            }
            System.out.println(realpassword);
            System.out.println(rs);
            rs.close();
            db.closestate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
