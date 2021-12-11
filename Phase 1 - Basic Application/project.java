import java.util.*;
import java.time.LocalDate;


class Leave{
    int lv_id;                      // to uniquely identify a leave for a particuar employee
    int lv_emp_id;                  // id of employee jisne leave apply ki hai
    String lv_emp_name;
    String lv_start;    
    String lv_end;
    int duration;
    String lv_type;                 // CL / SL / PERSONAL / EMERGENCY / INTERNET ISSUES / ACADEMIC / etc ... 
    String lv_status;               // APPLIED / APPROVED / REJECTED / UNDER CORRECTION / etc...
    String lv_remark;    
    String lv_manager;
    LocalDate lv_date;              // date of application of leave
    int lv_priority;

    public void addLeave(int leaveId, int empID, String empName){
        Scanner sc = new Scanner(System.in);
        
        System.out.print("\nEnter Start Date : ");
        lv_start = sc.next();
        System.out.print("Enter End Data : ");
        lv_end = sc.next();
        System.out.print("Duration of the leave ( #days ) : ");
        duration = sc.nextInt();
        System.out.println("\n - a. Emergency\n - b. Sick Leave\n - c. Personal \n - d. Academic\n - e. Casual\n - f. Other ");
        System.out.print("Select your reason for application of leave : ");
        sc.nextLine();
        String type = sc.nextLine();
        switch (type) {
            case "a":
                lv_type = "Emergency";
                lv_priority = 1;                
                break;
            case "b":
                lv_type = "Sick Leave";
                lv_priority = 2;
                break;
            case "c":
                lv_type = "Personal";
                lv_priority = 3;
                break;
            case "d":
                lv_type = "Academic";                
                lv_priority = 4;
                break;
            case "e":
                lv_type = "Casual";
                lv_priority = 5;
                break;
            case "f":
                lv_type = "Others";
                lv_priority = 6;
                break;
            default:
                break;
        }

        // default values
        lv_id = leaveId;
        lv_emp_id = empID;
        lv_emp_name = empName;
        lv_status = "APPLIED";  // default
        lv_remark = "-";
        lv_manager = "-";
        lv_date = LocalDate.now();      // gets current date / date of application of leave 
    }

    public void displayIndividualLeave(){
        System.out.println("\n DETAILS OF THE LEAVE :- ");
        System.out.println(" - Leave NO : " + lv_id);
        System.out.println(" - Employee ID : " + lv_emp_id);
        System.out.println(" - Applied by : " + lv_emp_name);
        System.out.println(" - Start date : " + lv_start);
        System.out.println(" - End date : " + lv_end);
        System.out.println(" - Duration : " + duration);
        System.out.println(" - Type of leave : " + lv_type);        
        System.out.println(" - Applied on : " + lv_date);
        System.out.println(" - Status : " + lv_status);
        System.out.println(" - Remark : " + lv_remark);
        System.out.println(" - Approved by : " + lv_manager);
    }

    public void displayLeaves(LinkedList<Leave>ll){
        System.out.println();
        String leftAlignFormat = "%-6s|%-3s| %-10s | %-11s | %-10s | %-10s | %-13s | %-11s | %-9s | %-20s | %-12s | %n";
        System.out.println("      +---+------------+-------------+------------+------------+---------------+-------------+-----------+----------------------+--------------+");
        System.out.format(leftAlignFormat,"","NO", "EMP. NAME", "START DATE", "END DATE", "DURATION", "TYPE OF LEAVE","APPLIED ON", "STATUS", "REMARK", "APPROVED BY");
        System.out.println("      +---+------------+-------------+------------+------------+---------------+-------------+-----------+----------------------+--------------+");
        for(int i = 0 ;i < ll.size(); i++){
            Leave temp = new Leave();
            temp = ll.get(i);
            System.out.format(leftAlignFormat,"", i+1, temp.lv_emp_name, temp.lv_start, temp.lv_end, temp.duration, temp.lv_type, temp.lv_date, temp.lv_status, temp.lv_remark, temp.lv_manager);
        }
        System.out.println("      +---+------------+-------------+------------+------------+---------------+-------------+-----------+----------------------+--------------+");

    }
}

// contains common attributes of employee and manager class
class Person{
    String name;
    String username;
    String password;
    static HashSet<String> usrnm = new HashSet<>();         // collection of all usernames
    static HashSet<String> pswrd = new HashSet<>();         // collection of all passwords
    LinkedList<Leave> leave = new LinkedList<>();  

    public int Signup(){        // signup / registration
        int val = 0;
        Scanner sc = new Scanner(System.in);
        System.out.print(" -> Enter your name : ");
        name = sc.next();
        
        do{
            val = 1;
            System.out.print(" -> Enter your username : ");
            username = sc.next();
            if(usrnm.contains(username)){
                System.out.println("\n*** This username is used by another user :( Please enter another one  !!***\n");
                val = 0;
            }
        }while(val == 0);
        do{
            val = 1;
            System.out.print(" -> Enter your password : ");
            password = sc.next();
            if(pswrd.contains(password)){
                System.out.println("\n*** This password is used by another user :( Please enter another one !!***\n");
                val = 0;
            }
        }while(val == 0);    
        usrnm.add(username);
        pswrd.add(password);    
        return 1;
    }

    public int Login(){
        Scanner sc = new Scanner(System.in);
        String user, pass;
        System.out.print(" -> Enter your username : ");
        username = sc.next();
        System.out.print(" -> Enter your password : ");
        password = sc.next();
        if(usrnm.contains(username) && pswrd.contains(password))
            return 1;

        else if(usrnm.contains(username) == false){
            System.out.println("\n AUTHENTICATION FAILED .. PLEASE CHECK YOUR USERNAME :(");
            return 0;
        }
        else{
            System.out.println("\n AUTHENTICATION FAILED .. PLEASE CHECK YOUR PASSWORD:(");
            return 0;
        }
        //return 0;
    }

    void LeaveHistory(){        // prints the Leave History of the employee / manager
        Leave l = new Leave();
        l.displayLeaves(leave);
    }
}


class Employee extends Person{
    int emp_user_id;
    int emp_lv_cnt = 0;                                     // no of leaves applied
    static int emp_ID = 0;
    final int total_allowed_leaves = 2;                     // MAX LEAVES ALLOWED = 2
    
    public Leave empApplyLeave(){
        emp_lv_cnt++;
        Leave l = new Leave();
        l.addLeave(emp_lv_cnt, emp_user_id, name);
        System.out.println("\n --> YOUR LEAVE HAS BEEN SUCCESSFULLY APPLIED !!! VIEW LEAVE HISTORY FOR FURTHER DETAILS :)");
        leave.addLast(l);
        return l;
    }

    public void empLeaveBalance() {
        System.out.println("\n# LEAVES ALLOWED / MONTH : " + total_allowed_leaves);
        System.out.println("      # LEAVES EXHAUSTED : " + emp_lv_cnt);
        System.out.println("      # AVAILABLE LEAVES : " + (total_allowed_leaves - emp_lv_cnt) + "\n");
    }

}


class Manager extends Person{
    int mng_id;

    public void mngrNewLeaves(PriorityQueue<Leave> pq){
        LinkedList<Leave> ll = new LinkedList<Leave>();
        while(!pq.isEmpty()){
            Leave temp = pq.poll();
            ll.add(temp);
        }
        Leave l = new Leave();
        l.displayLeaves(ll);
    }

    public PriorityQueue<Leave> mngrEditLeaves(PriorityQueue<Leave> pq){
        Scanner sc = new Scanner(System.in);
        int index;
        System.out.print("\nEnter index of leave you wanna edit : ");
        index = sc.nextInt();
        
        PriorityQueue<Leave>update = new PriorityQueue<Leave>(new LeaveComparator());

        int cnt = 1;
        while(cnt < index){
            Leave l = pq.poll();
            update.add(l);
            cnt++;
        }
        Leave req = pq.poll();
        while(!pq.isEmpty()){
            Leave l = pq.poll();
            update.add(l);
        }
        Leave l = req;
        l.displayIndividualLeave();
        System.out.print("\nLeave Action (APPROVE/DECLINE) : ");
        req.lv_status = sc.next();
        System.out.print("Remarks : ");
        sc.nextLine();
        req.lv_remark = sc.nextLine();
        req.lv_manager = name;
        
        System.out.println("\n --> The selected leave has been successfully edited !! View your leave history for further details ..");
        leave.add(req);
        return update;
    }   
}


class LeaveComparator implements Comparator<Leave>{

    public int compare(Leave p, Leave q){

        if(p.lv_priority < q.lv_priority)           return -1;
        else if (p.lv_priority > q.lv_priority )    return 1;
        else {
            if(p.duration < q.duration)             return 1;
            else if(p.duration > q.duration)        return -1;
            else                                    return 0;
        }    
    }
}


public class project{
    
    static ArrayList<Employee> ar_emp = new ArrayList<>();
    static ArrayList<Manager> ar_mng = new ArrayList<>();
    static PriorityQueue<Leave> pq = new PriorityQueue<>(new LeaveComparator());   // max to min duration : sorts leaves in descending order of their duration...
    static HashMap<String, HashMap<String, String>>hm = new HashMap<>();

    public static void main(String[] args) {
        int ch = 0;
        do{
            Scanner sc = new Scanner(System.in);
            System.out.println("\n 1. Employee");
            System.out.println(" 2. Manager");      // admin / employer
            System.out.println(" 3. Exit");
            System.out.print("\nEnter your choice : ");
            ch = sc.nextInt();
            switch (ch) {
                case 1:
                    employeeOperations();
                    break;
                case 2:
                    managerOperations();
                    break;
                case 3:
                    System.out.println("\n---------------- EXITING ----------------\n");
                    break;

                default:
                    System.out.println("Enter a number btw 1 to 3 !!");
                    break;
            }

        }while(ch != 3);
    }

    public static void employeeOperations(){
        int val = 1, ch = 0;
        String cho;
        Scanner sc = new Scanner(System.in);
        Employee e = new Employee();

        do{    
            System.out.print("\n a. Signup \n b. Login \n  --> ");
            cho = sc.next();
            cho = cho.toLowerCase();

            if(cho.equals("a"))  {
                val = e.Signup();
                e.emp_ID++;
                e.emp_user_id = e.emp_ID;
                ar_emp.add(e);              // add employee to arraylist
            }
            else if(cho.equals("b"))
                val = e.Login();
        }while(val == 0);
        for(int i = 0; i < ar_emp.size(); i++){
            Employee temp = ar_emp.get(i);
            if(temp.username.equals(e.username)){
                e = temp;
                break;
            }
        }
        System.out.println("\n---------Congrats " + e.name + " !! You have signed in successfully !! ----------");

        do{
            System.out.println("\n 1. Apply for a leave");
            System.out.println(" 2. View your Leave History");
            System.out.println(" 3. Check your Leave Balance");
            System.out.println(" 4. Logout");
            System.out.print("\nEnter your choice : ");
            ch = sc.nextInt();
            switch (ch) {
                case 1:
                    if(e.emp_lv_cnt < e.total_allowed_leaves){
                        Leave temp = e.empApplyLeave();
                        pq.add(temp);
                    }
                    else 
                        System.out.println("\n --> Uh Oh !! You have exhausted all your available leaves for this month !! Can't apply for more now :(");
                    break;
                case 2:
                    e.LeaveHistory();
                    break;
                case 3:
                    e.empLeaveBalance();
                    break;
                case 4:
                    System.out.println("\n ------------ Logging out !! --------------");
                    return ;
                default:
                    System.out.println("Invalid input !! Enter a number between 1 to 4 only...");
                    break;
            }

        }while(ch != 4);
    }

    public static void managerOperations() {
        int val = 1, ch = 0;
        String cho;
        Scanner sc = new Scanner(System.in);
        Manager m = new Manager();

        do{    
            System.out.print("\n a. Signup \n b. Login \n  --> ");
            cho = sc.next();
            cho = cho.toLowerCase();

            if(cho.equals("a"))  {
                val = m.Signup();
                ar_mng.add(m);              // add employee to arraylist
            }
            else if(cho.equals("b"))
                val = m.Login();
        }while(val == 0);
        for(int i = 0; i < ar_mng.size(); i++){
            Manager temp = ar_mng.get(i);
            if(temp.username.equals(m.username)){
                m = temp;
                break;
            }
        }
        
        System.out.println("\n-----Congrats " + m.name + " !! You have signed in successfully !! -----");

        do{
            System.out.println("\n 1. Newly applied leaves");
            System.out.println(" 2. Manage Leaves");        
            System.out.println(" 3. View your Leave History");        // previously approved / rejected leaves
            System.out.println(" 4. View Employee Leave Report");
            System.out.println(" 5. Logout");
            System.out.print("\nEnter your choice : ");
            ch = sc.nextInt();
            switch (ch) {
                case 1:
                    PriorityQueue<Leave> temp = new PriorityQueue<Leave>(pq);
                    m.mngrNewLeaves(temp);
                    break;
                case 2:
                    PriorityQueue<Leave> tem = new PriorityQueue<Leave>(pq);
                    m.mngrNewLeaves(tem);
                    tem = new PriorityQueue<Leave>(pq);
                    pq = m.mngrEditLeaves(tem);
                    break;
                case 3:
                    m.LeaveHistory();
                    break;
                case 4:
                    generateLeaveReport();
                    displayLeaveReport();
                    viewEmployeeSeparately();
                    break;
                case 5:
                    System.out.println("\n ------------ Logging out !! --------------");
                    return ;
                default:
                    System.out.println("Invalid input !! Enter a number between 1 to 5 only...");
                    break;
            }
        }while(ch != 5);               
    }

    public static void generateLeaveReport() {
        for(int i = 0 ;i < ar_emp.size(); i++){
            HashMap<String, String>temp = new HashMap<>();
            Employee e = ar_emp.get(i);
            temp.put("Name", e.name);
            temp.put("Used", String.valueOf(e.emp_lv_cnt));
            temp.put("Available", String.valueOf((e.total_allowed_leaves - e.emp_lv_cnt)));
            temp.put("Allowed", String.valueOf(e.total_allowed_leaves));
            hm.put(e.username, temp);
        }
    }

    public static void displayLeaveReport() {
        // System.out.println("Leave record : " + hm);
        System.out.println("\n                    EMPLOYEE LEAVE REPORT");
        String leftAlignFormat = " %-6s | %-6s | %-10s | %-7s | %-7s | %-13s | %-10s |\n";
        System.out.println("        +--------+------------+---------+---------+---------------+------------+");
        System.out.format(leftAlignFormat, "","SR.NO", "USERNAME","NAME", "#USED", "#AVAILABLE", "#TOTAL");
        System.out.println("        +--------+------------+---------+---------+---------------+------------+");
        int i = 1;

        for(String key : hm.keySet()){
            System.out.format(leftAlignFormat, "",i , key ,hm.get(key).get("Name") , hm.get(key).get("Used"), hm.get(key).get("Available"), hm.get(key).get("Allowed"));
            i++;
        }
        System.out.println("        +--------+------------+---------+---------+---------------+------------+");
    }

    public static void viewEmployeeSeparately() {
        String ans;
        do{
            System.out.print("\n -> Do you wanna view any Employee's Leave Record individually ?? (YES/NO) : " );
            Scanner sc = new Scanner(System.in);
            ans = sc.next();
            ans = ans.toUpperCase();
            if(ans.equals("YES")){
                int index = 0;
                System.out.print("\n -> Enter id of employee : ");
                index = sc.nextInt();
                int size = ar_emp.get(index-1).leave.size();
                if(size > 0)     ar_emp.get(index-1).LeaveHistory();
                else    System.out.println("\n-----------Employee has no leave record -----------");
            }
        }while(ans.equals("YES"));
    }
}