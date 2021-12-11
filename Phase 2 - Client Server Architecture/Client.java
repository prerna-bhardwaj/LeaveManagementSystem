package socket;

import java.util.*;
import java.net.*;
import java.io.*;

public class Client{

    public static void main(String[] args) throws IOException{
        try {
            System.out.println("Client Started !!!");
            Socket s = new Socket("localhost", 6666);

            Scanner sc = new Scanner(System.in);
            int ch = 0;
            ObjectOutputStream outs = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ins = new ObjectInputStream(s.getInputStream());

            do{
                System.out.println("\n 1. Employee");
                System.out.println(" 2. Manager");      // admin / employer
                System.out.println(" 3. Chatbot");
                System.out.println(" 4. Exit");
                System.out.print("\nEnter your choice : ");
                ch = sc.nextInt();
                outs.writeObject(ch);

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
                        System.out.println("\n---------------- EXITING ----------------\n");
                        break;

                    default:
                        System.out.println("Enter a number btw 1 to 3 !!");
                        break;
                }

            }while(ch != 4);
            
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }       
    }

    public static void employeeOperations(ObjectOutputStream outs, ObjectInputStream ins) throws IOException {    
        
        int chc = 0;
        String cho;
        Scanner sc = new Scanner(System.in);

        try {
            Employee emp = new Employee();
            int emp_ID;
            String srvr_response;
            do{    
                Employee e = new Employee();
                System.out.print("\n a. Signup \n b. Login \n  --> ");
                cho = sc.next();
                cho = cho.toLowerCase();
    
                if(cho.equals("a")){
                    e.Signup();
                    outs.writeObject(-1);
                }                     
                else if(cho.equals("b")){
                    e.Login();
                    outs.writeObject(0);    
                }   
                outs.writeObject(e);
                srvr_response = (String)ins.readObject();
                emp = (Employee)ins.readObject(); 
                System.out.println(srvr_response);
            
            }while(srvr_response.contains(":("));
            emp_ID = emp.emp_user_id;

            do{
                outs.reset();
                outs.writeObject(-2);
                outs.writeObject(emp_ID);
                emp = (Employee)ins.readObject();

                System.out.println("\n 1. Apply for a leave");
                System.out.println(" 2. View your Leave History");
                System.out.println(" 3. Check your Leave Balance"); 
                System.out.println(" 4. Logout");
                System.out.print("\nEnter your choice : ");
                chc = sc.nextInt();
                outs.writeObject(chc);

                switch (chc) {
                    case 1:
                        outs.reset();
                        outs.writeObject(emp_ID);
                        if(emp.emp_lv_cnt < emp.total_allowed_leaves){
                            Leave lv = emp.empApplyLeave();
                            outs.writeObject(lv);
                        }
                        else
                            System.out.println("\n --> Uh Oh !! You have exhausted all your available leaves for this month !! Can't apply for more now :(");
                        break;
                    case 2:
                        outs.reset();
                        outs.writeObject(emp_ID);
                        emp = (Employee)ins.readObject();
                        if(emp.leave.size() == 0)   System.out.println("\n  **** NO LEAVE HISTORY ****");
                        else                        emp.LeaveHistory();
                        break;
                    case 3:
                        emp.empLeaveBalance();
                        break;
                    case 4:
                        System.out.println("\n ------------ Logging out !! --------------");
                        break;
                    default:
                        System.out.println("Invalid input !! Enter a number between 1 to 4 only...");
                        break;
                }    
            }while(chc != 4);
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
            
    }

    public static void managerOperations(ObjectOutputStream outs, ObjectInputStream ins) {    
        int chc = 0;
        String cho;
        Scanner sc = new Scanner(System.in);

        try {
            Manager mng = new Manager();
            String username, srvr_response;
            do{    
                Manager m = new Manager();
                System.out.print("\n a. Signup \n b. Login \n  --> ");
                cho = sc.next();
                cho = cho.toLowerCase();
    
                if(cho.equals("a")){
                    m.Signup();
                    outs.writeObject(-1);
                }                     
                else if(cho.equals("b")){
                    m.Login();
                    outs.writeObject(0);
                }   
                outs.writeObject(m);
                
                srvr_response = (String)ins.readObject();
                mng = (Manager)ins.readObject(); 
                System.out.println(srvr_response);
            
            }while(srvr_response.contains(":("));
            username = mng.username;

            do{
                outs.writeObject(-2);
                outs.writeObject(username);
                mng = (Manager)ins.readObject();

                System.out.println("\n 1. Newly applied leaves");
                System.out.println(" 2. Manage Leaves");        
                System.out.println(" 3. View your Leave History");        // previously approved / rejected leaves
                System.out.println(" 4. View Employee Leave Report");
                System.out.println(" 5. Logout");
                System.out.print("\nEnter your choice : ");
                chc = sc.nextInt();
                outs.writeObject(chc);

                switch (chc) {
                    case 1:
                        PriorityQueue<Leave> pq = (PriorityQueue<Leave>)ins.readObject();
                        if(pq.size() == 0)      System.out.println("\n --> No new leaves present !! ");
                        else                    mng.mngrNewLeaves(pq);
                        break;
                    
                    case 2:   
                        PriorityQueue<Leave> prq = (PriorityQueue<Leave>)ins.readObject();
                        if(prq.size() == 0)   System.out.println("\n --> All leaves have been managed !!");
                        else{
                            mng.mngrNewLeaves(prq);
                            Leave lv = mng.mngrEditLeaves(prq);
                            outs.writeObject(username);
                            outs.writeObject(lv);    
                        }
                        break;
                    case 3:
                        if(mng.leave.size() == 0)   System.out.println("\n  **** NO LEAVE HISTORY ****");
                        else                        mng.LeaveHistory();
                        break;
                    case 4:
                        HashMap<String, HashMap<String, String>>hm = (HashMap<String, HashMap<String, String>>)ins.readObject();
                        displayLeaveReport(hm);
                        int id;
                        do{
                            id = viewEmployeeSeparately();
                            outs.writeObject(id);
                            if(id > -1){
                                Employee emp = (Employee)ins.readObject();
                                if(emp == null)     System.out.println("\n   *-*-*-* Requested employee doesn't exist *-*-*-*");
                                else{
                                    int size = emp.leave.size();
                                    if(size > 0)    emp.LeaveHistory();
                                    else            System.out.println("\n-----------Employee has no leave record -----------");    
                                }
                            }

                        }while(id != -1);
                        break;
                    case 5:
                        System.out.println("\n ------------ Logging out !! --------------");
                        break;
                    default:
                        System.out.println("Invalid input !! Enter a number between 1 to 5 only...");
                        break;
                } 
            }while(chc != 5);
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
           
    }

    public static void displayLeaveReport(HashMap<String, HashMap<String, String>> hm) {
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

    public static int viewEmployeeSeparately() {
        String ans;
        int index = -1;
        System.out.print("\n -> Do you wanna view any Employee's Leave Record individually ?? (YES/NO) : " );
        Scanner sc = new Scanner(System.in);
        ans = sc.next();
        ans = ans.toUpperCase();
        if(ans.equals("YES")){
            index = 0;
            System.out.print("\n -> Enter id of employee : ");
            index = sc.nextInt();
        }
        return index;
    }

    public static void chatBot(ObjectOutputStream outs, ObjectInputStream ins) throws IOException{
        try {
            String my_msg, srvr_msg;
            Scanner sc = new Scanner(System.in);
            System.out.println("\n------- Type something -------\n");

            do{
                System.out.print("Client : ");
                my_msg = sc.nextLine();
                outs.writeObject(my_msg);
                if(my_msg.equals("exit")) break;
                srvr_msg = (String)ins.readObject();
                System.out.println("Server : " + srvr_msg);
            }while(!my_msg.equals("exit"));

            System.out.println("\n----Done with chatbot----\n");
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    }

}

