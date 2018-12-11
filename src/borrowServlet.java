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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;
import util.DB;

public class borrowServlet extends HttpServlet {
    private DB db;
    Map<String, String> attributes;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        db = new DB();
        HttpSession session = request.getSession();
        PrintWriter pw = response.getWriter();
        System.out.println(request.getParameter("current"));
        attributes = new HashMap<String, String>();
        int active =1;
        String ulicense;
        String uname;
        if (((String)session.getAttribute("permission")).equals("1")) {
            Object license = session.getAttribute("ulicense");
            Object name = session.getAttribute("uname");
            ulicense = (String) license;
            uname = (String) name;
        }
        else{
            ulicense=request.getParameter("ulicense");
            uname = request.getParameter("uname");
        }
        String temp=request.getParameter("active");
        if(temp!=null&&temp!="")
            active=Integer.parseInt(temp);
        int length=10;
        temp=request.getParameter("length");
        if(temp!=null&&temp!="")
            length=Integer.parseInt(temp);
        int pagenum=1;
        if (ulicense != null&&ulicense!="")
            attributes.put("ulicense", ulicense);
        String bookid = request.getParameter("bookid");
        if (bookid != null&&bookid!="")
            attributes.put("bookid", bookid);
        String bdate = request.getParameter("bdate");
        if (bdate != null&&bdate!="")
            attributes.put("bdate", bdate);
        String duedate = request.getParameter("duedate");
        if (duedate != null&&duedate!="")
            attributes.put("duedate", duedate);
        String rdate = request.getParameter("rdate");
        if (rdate != null&&rdate!="")
            attributes.put("rdate", rdate);
        String is_return = request.getParameter("is_return");
        if (is_return != null&&is_return!="")
            attributes.put("is_return", is_return);
        System.out.println(attributes);
        //System.out.println(request.getParameter("add"));
        //System.out.println(request.getParameter("query").equals("true"));
        String op=request.getParameter("operation");

        if (op.equals("query")){
            //System.out.println("querry");
            JSONObject result= doQuery(active,length);
            pw.print(result);

        }
        else if(op.equals("add")){
            //System.out.println("adddd");
            String result=doAdd();
            response.setStatus(200);
            pw.print(result);
        }
        else if(op.equals("delete")){
            String ids[]=request.getParameterValues("delete_lines");
            int result=doDelete(ids);
            pw.print(result);
        }
        else if(op.equals("return")){
            String id=request.getParameter("return_line");
            long result=doReturn(id);
            pw.print(result);
        }
        else if(op.equals("update")){
            String id=request.getParameter("id");
            int result=doUpdate(id);
            pw.print(result);
        }
    }
    public String updatePass(String license,String originalPass,String newPass){
        ResultSet rs=db.query("SELECT upassword FROM reader where license= '"+license+"'");
        String realPass=null;
        int re=0;
        if (rs!=null){
            try{
                while(rs.next()){
                    realPass=rs.getString("upassword");
                }
                if(realPass!=null&&realPass.equals(originalPass)){
                    re=db.executeupdate("UPDATE reader SET upassword= '"+newPass+"' WHERE license='"+license+"'");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(re==1){
            return "OK";
        }
        else{
            return "Fail";
        }
    }

    protected JSONObject doQuery(int active,int length){
        List jsonlist=new ArrayList<JSONObject>();
        JSONObject result=new JSONObject();
        int pagenum=1;
        int size = attributes.size();
        ResultSet rs;
        ResultSet count;
        if (size == 0) {
            rs = db.query("SELECT * FROM borrow ORDER BY ulicense desc limit "+ String.valueOf((active-1)*length)+","+String.valueOf(length));
            count=db.query("SELECT count(*) from borrow");
            System.out.println("qqqqq");
        } else {
            String s = "SELECT * FROM borrow where";
            String s1="SELECT count(*) FROM borrow where";
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
            s=s+" ORDER BY ulicense desc limit "+ String.valueOf((active-1)*length)+","+String.valueOf(length);
            //System.out.println(s);
            //System.out.println(s1);
            rs = db.query(s);
            count=db.query(s1);
        }

        try {
            if (rs != null) {
                while (rs.next()) {
                    JSONObject json = new JSONObject();
                    String ulicense=rs.getString("ulicense");
                    String uname=null;
                    ResultSet rsname=db.query("SElECT uname FROM reader WHERE license='"+ulicense+"'");
                    while(rsname.next()){
                       uname=rsname.getString("uname");
                    }
                    String bookid=rs.getString("bookid");
                    ResultSet rsbname=db.query("SELECT bname FROM books WHERE bookid='"+bookid+"'");
                    String bname=null;
                    while(rsbname.next()){
                        bname=rsbname.getString("bname");
                    }
                    String id=rs.getString("id");
                    String bdate=rs.getString("bdate");
                    String duedate=rs.getString("duedate");
                    String rdate=rs.getString("rdate");
                    String is_return=rs.getString("is_return");
                    json.put("id",id);
                    json.put("ulicense",ulicense);
                    json.put("uname", uname);
                    json.put("bookid", bookid);
                    json.put("bname", bname);
                    json.put("bdate", bdate);
                    json.put("duedate", duedate);
                    json.put("rdate", rdate);
                    json.put("is_return", is_return);

                    jsonlist.add(json);
                }
                rs.close();

            }
            if(count!=null){
                while(count.next()){
                    pagenum=count.getInt(1);
                }
            }

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

    protected String doAdd(){
        String result=null;
        try{
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
            String currentDate=df.format(new Date());// new Date()为获取当前系统时间
            String overDue="SELECT count(*)from borrow WHERE ulicense='"+attributes.get("ulicense")+"' and is_return=0 and duedate<'"+currentDate+"'";
            System.out.println(overDue);
            ResultSet overduedb=db.query(overDue);
            int numofOverdue=0;
            while(overduedb.next()){
                numofOverdue=overduedb.getInt(1);
            }
            if(numofOverdue>0){
                result="有超期书未还，不能借书！";
            }
            else{
                String numOfBookBorrowed="SELECT count(*) from borrow WHERE bookid='"+attributes.get("bookid")+"'&& is_return=0";
                String numOfBooks="SELECT num from books where bookid='"+attributes.get("bookid")+"'";
                ResultSet borrowed=db.query(numOfBookBorrowed);
                ResultSet num=db.query(numOfBooks);
                int numOfBorrowed=0;
                int total=0;
                while(borrowed.next()){
                    numOfBorrowed=borrowed.getInt(1);
                }
                while(num.next()){
                    total=Integer.valueOf(num.getString("num"));
                }
                if(numOfBorrowed>=total){
                    result="本书无剩余库存！";
                }
                else{
                    String s="INSERT INTO borrow(ulicense,bookid,bdate,duedate) VALUES('"+attributes.get("ulicense")+"','"
                            +attributes.get("bookid")+"','"+attributes.get("bdate")+"','"+attributes.get("duedate")+"')";
                    System.out.println(s);
                    int n=db.executeupdate(s);
                    if(n==1){
                        result="借书成功！";
                    }
                }
            }
            System.out.println(result);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    protected int doDelete(String[] ids){
        int k=0;
        int len=ids.length;
        for(int i=0;i<len;i++){
            String s="DELETE from borrow where id='"+ids[i]+"'";

            int l=db.executeupdate(s);
            if(l!=-1)
                k+=l;
        }
        return k;
    }
    protected int doUpdate(String id){
        String s="UPDATE borrow SET";
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
        s+=" where id='"+id+"'";
        System.out.println(s);
        k=db.executeupdate(s);
        return k;
    };
    long doReturn(String id){
        int k=0;
        long time=-1;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String currentDate=df.format(new Date());// new Date()为获取当前系统时间
        try {
            String s = "Update borrow SET is_return='" + String.valueOf(1) + "',rdate='" + currentDate + "' where id='" + id + "'";
            System.out.println(s);
            k = db.executeupdate(s);
            String overdue = "SELECT duedate,rdate from borrow where id='" + id + "'";
            ResultSet is_overdue = db.query(overdue);
            while (is_overdue.next()) {
                String due=is_overdue.getString("duedate");
                String r=is_overdue.getString("rdate");
                Date duedate=df.parse(due);
                Date rdate=df.parse(r);
                time=rdate.getTime()-duedate.getTime();
                if(time>0){
                    time=time/1000/60/60/24;
                }
                else{
                    time=0;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        catch (ParseException e){
            e.printStackTrace();
        }


        return time;
    }
}

