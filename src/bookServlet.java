import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.*;

import util.DB;
public class bookServlet extends HttpServlet {
    Map<String, String> attributes;
    DB db;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(request.getRequestURI());
        System.out.println(request.getParameter("bname"));
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("执行servlet!");
        response.setContentType("text/html; charset=UTF-8");
        //request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter pw=response.getWriter();
        attributes = new HashMap<String, String>();
        db = new DB();
        //System.out.println(request);
        int active =1;
        String temp=request.getParameter("active");
        if(temp!=null&&temp!="")
            active=Integer.parseInt(temp);
        int length=10;
        temp=request.getParameter("length");
        if(temp!=null&&temp!="")
        length=Integer.parseInt(temp);
        int pagenum=1;
        String bname = request.getParameter("bname");
        if (bname != null&&bname!="")
            attributes.put("bname", bname);
        String author = request.getParameter("author");
        if (author != null&&author!="")
            attributes.put("author", author);
        String isbn = request.getParameter("isbn");
        if (isbn != null&&isbn!="")
            attributes.put("isbn", isbn);
        String cbs = request.getParameter("cbs");
        if (cbs != null&&cbs!="")
            attributes.put("cbs", cbs);
        String year = request.getParameter("year");
        if (year != null&&year!="")
            attributes.put("year", year);
        String ssh = request.getParameter("ssh");
        if (ssh != null&&ssh!="")
            attributes.put("ssh", ssh);
        String num = request.getParameter("num");
        if (num != null&&num!="")
            attributes.put("num", num);
        System.out.println(attributes);
        //System.out.println(request.getParameter("add"));
        //System.out.println(request.getParameter("query").equals("true"));
        if (request.getParameter("query")!=null){
            //System.out.println("querry");
            JSONObject result= doquery(active,length);
            //System.out.println(result);

            //System.out.println(response.getStatus());
            pw.print(result);

        }
        else if(request.getParameter("add")!=null){
            //System.out.println("adddd");
            int result=doadd();
            response.setStatus(200);
            pw.print(result);
        }
        else if(request.getParameter("delete")!=null){
            String[] ids=request.getParameterValues("delete_lines[]");
            System.out.println(ids[0]);
            //System.out.println(ids[1]);
            int result=dodelete(ids);
            pw.print(result);
        }
        //System.out.println(request.getParameter("add").equals("true"));
        db.closestate();
        //response.setStatus(200);

    }

    protected JSONObject doquery(int active,int length){
        List jsonlist=new ArrayList<JSONObject>();
        JSONObject result=new JSONObject();
        int pagenum=1;
        int size = attributes.size();
        ResultSet rs;
        ResultSet count;
        if (size == 0) {
            rs = db.query("SELECT * FROM book ORDER BY bookid desc limit "+ String.valueOf((active-1)*length)+","+String.valueOf(length));
            count=db.query("SELECT count(*) from book");
            System.out.println("qqqqq");
        } else {
            String s = "SELECT * FROM book where";
            String s1="SELECT count(*) FROM book where";
            int i = 0;
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                i++;
                if (i < size) {
                    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                    s = s + " " + entry.getKey() + "='" + entry.getValue() + "' AND";
                    s1 = s1 + " " + entry.getKey() + "='" + entry.getValue() + "' AND";
                }
                else {
                    s = s + " " + entry.getKey() + "='" + entry.getValue() + "'";
                    s1 = s1 + " " + entry.getKey() + "='" + entry.getValue() + "'";
                }
            }
            s=s+" ORDER BY bookid desc limit "+ String.valueOf((active-1)*length)+","+String.valueOf(length);
            //System.out.println(s);
            //System.out.println(s1);
            rs = db.query(s);
            count=db.query(s1);

        }

        try {
            if (rs != null) {
                while (rs.next()) {
                    JSONObject json = new JSONObject();
                    String id=rs.getString("bookid");
                    json.put("id",id);
                    String name = rs.getString("bname");
                    json.put("bname", name);
                    String aut = rs.getString("author");
                    json.put("author", aut);
                    String isb = rs.getString("isbn");
                    json.put("isbn", isb);
                    String cb = rs.getString("cbs");
                    json.put("cbs", cb);
                    String yea = rs.getString("year");
                    json.put("year", yea);
                    String ss = rs.getString("ssh");
                    json.put("ssh", ss);
                    String nm = rs.getString("num");
                    json.put("num", nm);
                    jsonlist.add(json);
                }
            }
            if(count!=null){
                while(count.next()){
                    pagenum=count.getInt(1);
                }
            }
            rs.close();
            //PrintWriter pw = response.getWriter();
            String jsonstring=jsonlist.toString();
            result.put("data",jsonlist);
            //System.out.println(pagenum);
            result.put("page_num",Math.ceil((double)pagenum/length));
            return result;
            //pw.print(result);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException j) {
            j.printStackTrace();
        }
        return result;
    }

    protected int doadd(){
        String s="INSERT INTO book(bname,author,isbn,cbs,year,ssh,num) VALUES('"+attributes.get("bname")+"','"
                +attributes.get("author")+"','"+attributes.get("isbn")+"','"+attributes.get("cbs")+"','"
                +attributes.get("year")+"','"+attributes.get("ssh")+"','"+attributes.get("num")+"')";
        int result=db.executeupdate(s);
        System.out.println(result);
        return result;
    }

    protected int dodelete(String[] ids){
        int k=0;
        int length=ids.length;
        for (int i=0;i<length;i++){
            String s="DELETE from book where bookid='"+ids[i]+"'";
            System.out.println(s);
            k=db.executeupdate(s);
        }
        return k;
    }
}

