import java.util.*;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.SQLException;

// contains common attributes of employee and manager class
class Person implements Serializable{
    String name;
    String username;
    String password;
    // LinkedList<Leave> leave = new LinkedList<>();  

    public int Signup(){        // signup / registration
        Scanner sc = new Scanner(System.in);
        System.out.print(" -> Enter your name : ");
        name = sc.next();
        System.out.print(" -> Enter your username : ");
        username = sc.next();
        System.out.print(" -> Enter your password : ");
        password = sc.next(); 
        return 1;
    }

    public int Login(){
        Scanner sc = new Scanner(System.in);
        String user, pass;
        System.out.print(" -> Enter your username : ");
        username = sc.next();
        System.out.print(" -> Enter your password : ");
        password = sc.next();
        return 0;
    }

   void LeaveHistory(LinkedList<Leave> leave){        // prints the Leave History of the employee / manager
       Leave l = new Leave();
       l.displayLeaves(leave);
   }
}


class Employee extends Person {
    int emp_user_id;
    int emp_lv_cnt = 0;                                     // no of leaves applied
    // static int emp_ID = 0;
    final int total_allowed_leaves = 2;                     // MAX LEAVES ALLOWED = 2
    
    public Employee() {    }

    public Employee(int emp_user_id, String username, String passname, String name, int emp_lv_cnt){
        this.emp_user_id = emp_user_id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.emp_lv_cnt = emp_lv_cnt;
    }

    public Leave empApplyLeave(){
        Leave l = new Leave();
        l.addLeave(emp_lv_cnt, emp_user_id, name);
        System.out.println("\n --> YOUR LEAVE HAS BEEN SUCCESSFULLY APPLIED !!! VIEW LEAVE HISTORY FOR FURTHER DETAILS :)");
        return l;
    }

    public void empLeaveBalance() {
        System.out.println("\n# LEAVES ALLOWED / MONTH : " + total_allowed_leaves);
        System.out.println("      # LEAVES EXHAUSTED : " + emp_lv_cnt);
        System.out.println("      # AVAILABLE LEAVES : " + (total_allowed_leaves - emp_lv_cnt) + "\n");
    }

    public void addLeave(Leave lv) throws SQLException{
        Date dt;
        String sql = "insert into Leaves(lv_id, emp_id, start_date, end_date, duration, type, applied_date, priority) values(?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pdst = Server.con.prepareStatement(sql);
        pdst.setInt(1, lv.lv_id);
        pdst.setInt(2, lv.lv_emp_id);
        dt = Date.valueOf(lv.lv_start);
        pdst.setDate(3, dt);
        dt = Date.valueOf(lv.lv_end);
        pdst.setDate(4, dt);
        pdst.setInt(5, lv.duration);
        pdst.setString(6, lv.lv_type);
        dt = Date.valueOf(lv.lv_date);
        pdst.setDate(7, dt);
        pdst.setInt(8, lv.lv_priority);
        pdst.execute();
        System.out.println("Leave added successfully !!!");
        
        sql = "update Employee set lv_cnt = lv_cnt+1 where emp_user_id = " + lv.lv_emp_id;
        pdst.execute(sql);
   }

}


    class Manager extends Person {
    int mng_id;

    public Manager() {  }

    public Manager(int id, String username, String password, String name){
        this.mng_id = id;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public void mngrNewLeaves(LinkedList<Leave> ll){
        Leave l = new Leave();
        l.displayLeaves(ll);
    }

    public Leave mngrEditLeaves(LinkedList<Leave> ll){
        Scanner sc = new Scanner(System.in);
        int index;
        System.out.print("\nEnter index of leave you wanna edit : ");
        index = sc.nextInt();
        Leave req = ll.get(index - 1);

        System.out.print("\nLeave Action (APPROVE/DECLINE) : ");
        req.lv_status = sc.next();
        System.out.print("Remarks : ");
        sc.nextLine();
        req.lv_remark = sc.nextLine();
        req.lv_mng_id = mng_id;
        req.lv_manager = name;
        req.displayIndividualLeave();
        System.out.println("\n --> The selected leave has been successfully edited !! View your leave history for further details ..");
        return req;
    }   
}