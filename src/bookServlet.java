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
        String bookid=request.getParameter("bookid");
        if (bookid != null&&bookid!="")
            attributes.put("bookid", bookid);
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
            JSONObject result= doQuery(active,length);
            //System.out.println(result);

            //System.out.println(response.getStatus());
            pw.print(result);

        }
        else if(request.getParameter("add")!=null){
            //System.out.println("adddd");
            int result=doAdd();
            response.setStatus(200);
            pw.print(result);
        }
        else if(request.getParameter("delete")!=null){
            String id=request.getParameter("delete_line");
            System.out.println(id);
            //System.out.println(ids[1]);
            String result=doDelete(id);
            pw.print(result);
        }
        else if(request.getParameter("update")!=null){
            String id=request.getParameter("id");
            int result=doUpdate(id);
            pw.print(result);
        }
        //System.out.println(request.getParameter("add").equals("true"));
        db.closestate();
        //response.setStatus(200);

    }

    protected JSONObject doQuery(int active,int length){
        List jsonlist=new ArrayList<JSONObject>();
        JSONObject result=new JSONObject();
        int pagenum=1;
        int size = attributes.size();
        ResultSet rs;
        ResultSet count;
        if (size == 0) {
            rs = db.query("SELECT * FROM books ORDER BY bookid desc limit "+ String.valueOf((active-1)*length)+","+String.valueOf(length));
            count=db.query("SELECT count(*) from books");
            System.out.println("qqqqq");
        } else {
            String s = "SELECT * FROM books where";
            String s1="SELECT count(*) FROM books where";
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
                    String name = rs.getString("bname");
                    String aut = rs.getString("author");
                    String isb = rs.getString("isbn");
                    String cb = rs.getString("cbs");
                    String yea = rs.getString("year");
                    String ss = rs.getString("ssh");
                    String nm = rs.getString("num");
                    int numOfBorrows=0;
                    ResultSet borrows=db.query("SELECT count(*) from borrow where bookid= "+id);
                    if(borrows!=null){
                        while(borrows.next()){
                            numOfBorrows=borrows.getInt(1);
                        }
                    }
                    json.put("id",id);
                    json.put("bname", name);
                    json.put("author", aut);
                    json.put("isbn", isb);
                    json.put("cbs", cb);
                    json.put("year", yea);
                    json.put("ssh", ss);
                    json.put("num", nm);
                    json.put("available",Integer.valueOf(nm)-numOfBorrows);
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

    protected int doAdd(){
        String s="INSERT INTO books(bname,author,isbn,cbs,year,ssh,num) VALUES('"+attributes.get("bname")+"','"
                +attributes.get("author")+"','"+attributes.get("isbn")+"','"+attributes.get("cbs")+"','"
                +attributes.get("year")+"','"+attributes.get("ssh")+"','"+attributes.get("num")+"')";
        int result=db.executeupdate(s);
        System.out.println(result);
        return result;
    }

    protected String doDelete(String id){
        String result="fail";
        //int length=ids.length;
        int numofBorrow=0;
        try{
            String is_deletable="SELECT count(*) from borrow where bookid='"+id+"'and is_return=0";
            ResultSet borrows=db.query(is_deletable);
            while (borrows.next()){
                numofBorrow=borrows.getInt(1);
            }
            if (numofBorrow>0){
                result="该书有读者未还，不能删除";
            }
            else{
                String s="DELETE from books where bookid='"+id+"'";
                int k=db.executeupdate(s);
                if(k==1){
                    result="OK";
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return result;
    }
    protected int doUpdate(String id){
        String s="UPDATE books SET";
        int i=0;
        int size=attributes.size();
        int k=0;
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            i++;
            if (i < size) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                s = s + " " + entry.getKey() + "='" + entry.getValue() + "',";
            }
            else {
                s = s + " " + entry.getKey() + "='" + entry.getValue() + "'";
            }
        }
        s+=" where bookid='"+id+"'";
        System.out.println(s);
        k=db.executeupdate(s);
        return k;
    }
}

