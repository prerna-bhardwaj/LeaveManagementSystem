package socket;

import java.util.*;
import java.io.Serializable;


// contains common attributes of employee and manager class
class Person implements Serializable{
    String name;
    String username;
    String password;
    LinkedList<Leave> leave = new LinkedList<>();  

    public int Signup(){        // signup / registration
        int val = 0;
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

    void LeaveHistory(){        // prints the Leave History of the employee / manager
        Leave l = new Leave();
        l.displayLeaves(leave);
    }
}


class Employee extends Person {
    int emp_user_id;
    int emp_lv_cnt = 0;                                     // no of leaves applied
    static int emp_ID = 0;
    final int total_allowed_leaves = 2;                     // MAX LEAVES ALLOWED = 2
    
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

    public void addLeave(Leave lv){
        leave.add(lv);
        emp_lv_cnt++;
    }

}


class Manager extends Person {
    int mng_id;

    public void mngrNewLeaves(PriorityQueue<Leave> pq){
        PriorityQueue<Leave>prq = new PriorityQueue<>(pq);
        LinkedList<Leave> ll = new LinkedList<Leave>();
        while(!prq.isEmpty()){
            Leave temp = prq.poll();
            ll.add(temp);
        }
        Leave l = new Leave();
        l.displayLeaves(ll);
    }

    public Leave mngrEditLeaves(PriorityQueue<Leave> pq){
        Scanner sc = new Scanner(System.in);
        int index;
        System.out.print("\nEnter index of leave you wanna edit : ");
        index = sc.nextInt();
        
        int cnt = 1;
        while(cnt < index){
            pq.poll();
            cnt++;
        }
        Leave req = pq.poll();

        System.out.print("\nLeave Action (APPROVE/DECLINE) : ");
        req.lv_status = sc.next();
        System.out.print("Remarks : ");
        sc.nextLine();
        req.lv_remark = sc.nextLine();
        req.lv_manager = name;
        req.displayIndividualLeave();
        System.out.println("\n --> The selected leave has been successfully edited !! View your leave history for further details ..");
        return req;
    }   
}