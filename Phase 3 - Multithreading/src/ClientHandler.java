import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;


public class ClientHandler implements Runnable{
	Socket s;
	Statement st;
	ResultSet rs;
	PreparedStatement pdst;
	ObjectOutputStream outs;
	ObjectInputStream ins;
	
	
	public ClientHandler(Socket s)throws IOException, SQLException{
		this.s = s;
		outs = new ObjectOutputStream(s.getOutputStream());
		ins = new ObjectInputStream(s.getInputStream());
		this.st = Server.con.createStatement();			
	}
	
	public void run() {
		try {
			
			int ch = 0;
            do{
                ch = (int)ins.readObject();
                switch (ch) {
                    case 1:
                        employeeOperations(outs, ins);
                        break;
                    case 2:
                        managerOperations(outs, ins);
                        break;
                    case 3:
                        chatBot(outs, ins);
                        break;
                    case 4:
                        System.out.println("\n--------CLIENT IS EXITING----------\n");
                        break;
                    default:
                        System.out.println("------- INVALID ENTRY --------");
                        break;
                }
            }while(ch != 4);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	

    public void employeeOperations(ObjectOutputStream outs, ObjectInputStream ins) throws IOException{
        
        try {
            int ch;
            do{
                ch = (int)ins.readObject();
                int user_cnt, pass_cnt, eid;
                String username, password, srvr_response, sql;
                Employee emp = new Employee();
                
                switch (ch) {
                    case -1:												// SIGNUP
                        emp = (Employee)ins.readObject();
                        username = emp.username;
                        password = emp.password;
                        rs = st.executeQuery("select count(*) from Credentials where username = " + "'" + username + "'");
                        rs.next();
                        user_cnt = rs.getInt(1);
                        rs = st.executeQuery("select count(*) from Credentials where password = " + "'" + password + "'");
                        rs.next();
                        pass_cnt = rs.getInt(1);
                        
                        if(user_cnt > 0)
                            srvr_response = "\n*** This username is used by another user :( Please enter another one !!***\n";
                        
                        else if(pass_cnt > 0)
                            srvr_response = "\n*** This password is used by another user :( Please enter another one !!***\n";
                            
                        else{
                        	sql = "insert into Credentials values(?, ?)";
                        	pdst = Server.con.prepareStatement(sql);
                        	pdst.setString(1, username);
                        	pdst.setString(2, password);
                            pdst.execute();
                            
                            sql = "insert into Employee(username, password, name) values(?, ?, ?)";
                        	pdst = Server.con.prepareStatement(sql);
                        	pdst.setString(1, username);
                        	pdst.setString(2, password);
                        	pdst.setString(3, emp.name);
                        	pdst.execute();
                            rs = st.executeQuery("select emp_user_id from Employee where username = " + "'" + username + "'");
                            rs.next();
                            emp.emp_user_id = rs.getInt(1);
                            System.out.println(emp.name + " has signed in !!!");
                            srvr_response = "\n---------Congrats " + emp.name + " !! You have signed in successfully !! ----------";
                            
                        }
                        outs.reset();
                        outs.writeObject(srvr_response);
                        outs.writeObject(emp);
                        break;
                    
                    case 0:													// LOGIN
                        emp = (Employee)ins.readObject();
                        username = emp.username;
                        password = emp.password;
                        
                        rs = st.executeQuery("select count(*) from Credentials where username = " + "'" + username + "'");
                        rs.next();
                        user_cnt = rs.getInt(1);
                        rs = st.executeQuery("select count(*) from Credentials where password = " + "'" + password + "'");
                        rs.next();
                        pass_cnt = rs.getInt(1);

                        if(user_cnt > 0 && pass_cnt > 0){
                            
                            srvr_response = "\n---------Congrats !! You have logged in successfully !! ----------";
                            rs = st.executeQuery("select emp_user_id from Employee where username = " + "'" + username + "'");
                            rs.next();
                            eid = rs.getInt(1);
                            emp = getEmployeeObject(eid);
                        }    
                        else{
                            if(user_cnt == 0)
                                srvr_response = "\n AUTHENTICATION FAILED .. PLEASE CHECK YOUR USERNAME :(";                           
                            else
                                srvr_response = "\n AUTHENTICATION FAILED .. PLEASE CHECK YOUR PASSWORD:(";
                        } 
                        outs.reset();
                        outs.writeObject(srvr_response);
                        outs.writeObject(emp);
                        break;
                    
                    case -2:            									// GET EMPLOYEE OBJECT FROM DATABASE
                        eid = (int)ins.readObject();
                        emp = getEmployeeObject(eid);
                        outs.reset();
                        outs.writeObject(emp);
                        break;

                    case 1:													// APPLY FOR A LEAVE
                        eid = (int)ins.readObject();
                        emp = getEmployeeObject(eid);
                        if(emp.emp_lv_cnt < emp.total_allowed_leaves){
                            Leave lv = (Leave)ins.readObject();
                           // System.out.println("lv id = " + lv.lv_id + "  emp_id = " + lv.lv_emp_id);
                            emp.addLeave(lv);
                        }
                        break;
                    case 2:													// VIEW LEAVE HISTORY
                        eid = (int)ins.readObject();
                        outs.reset();
                        sql = "select a.name, lv_id, emp_id, start_date, end_date, duration, type, applied_date, status, remark, if(mng_id is NULL, '-', (select name from Manager where mng_id = mng_user_id)) as mng_name from Leaves join Employee as a where emp_id = emp_user_id and emp_id = " + eid;
                        rs = st.executeQuery(sql);
                        LinkedList<Leave>ll = getLeavesList();
                        outs.writeObject(ll);
                        break;	
                    case 3:													// VIEW LEAVE BALANCE
                        //System.out.println("Leave Balance ...");
                        break;
                    case 4:
                        //System.out.println("Employee has logged out !!");
                        return;
                    default:
                        System.out.println("Invalid Choice !!!");
                        break;
                }
            }while(ch!=4);
                
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }

    }

    public void managerOperations(ObjectOutputStream outs, ObjectInputStream ins) throws IOException{
        
        try {
            int ch;
            do{
                ch = (int)ins.readObject();
                int user_cnt, pass_cnt, mID;
                Manager mng = new Manager();
                String username, password, srvr_response, sql;
                LinkedList<Leave>ll = new LinkedList<>();
                
                switch (ch) {
                    case -1:                                                // SIGNUP
                        mng = (Manager)ins.readObject();
                        username = mng.username;
                        password = mng.password;
                        rs = st.executeQuery("select count(*) from Credentials where username = " + "'" + username + "'");
                        rs.next();
                        user_cnt = rs.getInt(1);
                        rs = st.executeQuery("select count(*) from Credentials where password = " + "'" + password + "'");
                        rs.next();
                        pass_cnt = rs.getInt(1);
                        
                        if(user_cnt > 0)
                            srvr_response = "\n*** This username is used by another user :( Please enter another one !!***\n";
                            
                        else if(pass_cnt > 0)
                            srvr_response = "\n*** This password is used by another user :( Please enter another one !!***\n";
                        else{
                            sql = "insert into Credentials values(?, ?)";
                        	pdst = Server.con.prepareStatement(sql);
                        	pdst.setString(1, username);
                        	pdst.setString(2, password);
                            pdst.execute();

                            sql = "insert into Manager(username, password, name) values(?, ?, ?)";
                        	pdst = Server.con.prepareStatement(sql);
                        	pdst.setString(1, username);
                        	pdst.setString(2, password);
                        	pdst.setString(3, mng.name);
                        	pdst.execute();
                            rs = st.executeQuery("select mng_user_id from Manager where username = " + "'" + username + "'");
                            rs.next();
                            mng.mng_id = rs.getInt(1);
                            System.out.println(mng.name + " has signed in !!");
                            srvr_response = "\n---------Congrats " + mng.name + " !! You have signed in successfully !! ----------";
                        }
                        outs.reset();
                        outs.writeObject(srvr_response);
                        outs.writeObject(mng);
                        break;
                    
                    case 0:                                                 // LOGIN
                        mng = (Manager)ins.readObject();
                        username = mng.username;
                        password = mng.password;
                        rs = st.executeQuery("select count(*) from Credentials where username = " + "'" + username + "'");
                        rs.next();
                        user_cnt = rs.getInt(1);
                        rs = st.executeQuery("select count(*) from Credentials where password = " + "'" + password + "'");
                        rs.next();
                        pass_cnt = rs.getInt(1);

                        if(user_cnt > 0 && pass_cnt > 0){
                            srvr_response = "\n---------Congrats !! You have logged in successfully !! ----------";
                            rs = st.executeQuery("select mng_user_id from Manager where username = " + "'" + username + "'");
                            rs.next();
                            mID = rs.getInt(1);
                            mng = getManagerObject(mID);
                        }    
                        else{
                            if(user_cnt > 0)
                                srvr_response = "\n AUTHENTICATION FAILED .. PLEASE CHECK YOUR USERNAME :(";                           
                            else
                                srvr_response = "\n AUTHENTICATION FAILED .. PLEASE CHECK YOUR PASSWORD:(";
                        } 
                        outs.reset();
                        outs.writeObject(srvr_response);
                        outs.writeObject(mng);
                        break;
                    
                    case -2:                                                // GET MANAGER OBJECT FROM DATABASE
                        mID = (int)ins.readObject();
                        Manager mngr = getManagerObject(mID);
                        outs.reset();
                        outs.writeObject(mngr);
                        break;
                    case 1:                                                 // VIEW NEWLY APPLIED LEAVES
                        outs.reset();
                        sql = "select a.name, lv_id, emp_id, start_date, end_date, duration, type, applied_date, status, remark, if(mng_id is NULL, '-', (select name from Manager where mng_id = mng_user_id)) as mng_name from Leaves join Employee as a where emp_id = emp_user_id and addressed = 0 order by priority, duration";
                        rs = st.executeQuery(sql);
                        ll = getLeavesList();
                        outs.writeObject(ll);
                        break;
                    case 2:                                                 // MANAGE LEAVES
                        outs.reset();
                        sql = "select a.name, lv_id, emp_id, start_date, end_date, duration, type, applied_date, status, remark, if(mng_id is NULL, '-', (select name from Manager where mng_id = mng_user_id)) as mng_name from Leaves join Employee as a where emp_id = emp_user_id and addressed = 0 order by priority, duration";
                        rs = st.executeQuery(sql);
                        ll = getLeavesList();
                        outs.writeObject(ll);
                        if(ll.size() > 0){
                            Leave lv = (Leave)ins.readObject();
                            updateLeavesDatabase(lv);
                        }   
                        break;
                    case 3:                                                 // LEAVE HISTORY    
                        mID = (int)ins.readObject();
                        outs.reset();
                        sql = "select a.name, lv_id, emp_id, start_date, end_date, duration, type, applied_date, status, remark, b.name as mng_name from Leaves join Employee as a join Manager as b where emp_id = emp_user_id and mng_id = mng_user_id and mng_id = " + mID;
                        rs = st.executeQuery(sql);
                        ll = getLeavesList();
                        outs.writeObject(ll);
                        // System.out.println("Leave history");
                        break;
                    case 4:                                                 // EMPLOYEE LEAVE REPORT
                    	HashMap<String, HashMap<String, String>>hm = generateLeaveReport();
                        outs.reset();
                        outs.writeObject(hm);
                        int id;
                        do{
                            id = (int)ins.readObject();
                            if(id > -1){
                                try {
                                    sql = "select a.name, lv_id, emp_id, start_date, end_date, duration, type, applied_date, status, remark, if(mng_id is NULL, '-', (select name from Manager where mng_id = mng_user_id)) as mng_name from Leaves join Employee as a where emp_id = emp_user_id and emp_id = " + id;
                                    rs = st.executeQuery(sql);
                                    ll = getLeavesList();
                                    outs.writeObject(ll);
                          
                                } catch (Exception e) {
                                    //TODO: handle exception
                                    outs.writeObject(null);
                                }
                            }     
                        }while(id != -1);
                        break;

                    case 5:
                        return;
                    default:
                        System.out.println("Invalid Choice !!");
                        break;
                }
            }while(ch!=5);

        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
        
    }

    public Employee getEmployeeObject(int id) throws SQLException{
        rs = st.executeQuery("select * from Employee where emp_user_id = " + id);
        rs.next();
        Employee emp = new Employee(rs.getInt("emp_user_id"), rs.getString("username"), rs.getString("password"), rs.getString("name"), rs.getInt("lv_cnt"));
        return emp;
    }

    public Manager getManagerObject(int id) throws SQLException{
        rs = st.executeQuery("select * from Manager where mng_user_id = " + id);
        rs.next();
        Manager mng = new Manager(rs.getInt("mng_user_id"), rs.getString("username"), rs.getString("password"), rs.getString("name"));
        return mng;
    }

    public LinkedList<Leave>getLeavesList()throws SQLException{
        LinkedList<Leave>ll = new LinkedList<>();
        while(rs.next()){
            Leave l = new Leave();
            l.lv_id = rs.getInt("lv_id");
            l.lv_emp_id = rs.getInt("emp_id");
            l.lv_emp_name = rs.getString("name");
            l.lv_start = rs.getDate("start_date").toString();
            l.lv_end = rs.getDate("end_date").toString();
            l.duration = rs.getInt("duration");
            l.lv_type = rs.getString("type");
            l.lv_date = rs.getDate("applied_date").toLocalDate();
            l.lv_status = rs.getString("status");
            l.lv_remark = rs.getString("remark");
            l.lv_manager = rs.getString("mng_name");
            ll.add(l);
        }
        return ll;
    }

    public void updateLeavesDatabase(Leave lv) throws SQLException {
    	String sql = "update Leaves set status = ?, remark = ?, mng_id = ?, addressed = 1 where lv_id = ? and emp_id = ?";
       	pdst = Server.con.prepareStatement(sql);
    	pdst.setString(1, lv.lv_status);
    	pdst.setString(2, lv.lv_remark);
    	pdst.setInt(3, lv.lv_mng_id);
    	pdst.setInt(4, lv.lv_id);
    	pdst.setInt(5, lv.lv_emp_id);
        pdst.execute();    	
    }
    
    public HashMap<String, HashMap<String, String>> generateLeaveReport() throws SQLException{
        rs = st.executeQuery("select * from Employee order by emp_user_id");
        HashMap<String, HashMap<String, String>>hm = new HashMap<>();
        while(rs.next()){
            HashMap<String, String>temp = new HashMap<>();
            temp.put("ID", String.valueOf(rs.getInt("emp_user_id")));
            temp.put("Name", rs.getString("name"));
            temp.put("Used", String.valueOf(rs.getInt("lv_cnt")));
            temp.put("Available", String.valueOf(rs.getInt("total_allowed_leaves") - rs.getInt("lv_cnt")));
            temp.put("Allowed", String.valueOf(rs.getInt("total_allowed_leaves")));
            hm.put(rs.getString("username"), temp);
        }
        return hm;
    }

    public void chatBot(ObjectOutputStream outs, ObjectInputStream ins) throws IOException{
        try {
            String my_msg, clnt_msg;
            Scanner sc = new Scanner(System.in);
            System.out.println("Initiating Chatbot ...\n");

            do{
                clnt_msg = (String)ins.readObject();
                System.out.println("Client : " + clnt_msg);
                if(clnt_msg.equals("exit")) break;
                System.out.print("Server : ");
                my_msg = sc.nextLine();
                outs.writeObject(my_msg);
            }while(!clnt_msg.equals("exit"));

            System.out.println("\n----Done with chatbot----\n");
            
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    
    }
	
}