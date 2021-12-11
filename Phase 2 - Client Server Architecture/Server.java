package socket;

import java.util.*;
import java.net.*;
import java.io.*;

public class Server{
    
    static ArrayList<Employee> ar_emp = new ArrayList<>();
    static ArrayList<Manager> ar_mng = new ArrayList<>();
    static PriorityQueue<Leave> pq = new PriorityQueue<>(new LeaveComparator());   // max to min duration : sorts leaves in descending order of their duration...
    static HashMap<String, HashMap<String, String>>hm = new HashMap<>();
    static HashSet<String> usrnm = new HashSet<>();         // collection of all usernames
    static HashSet<String> pswrd = new HashSet<>();         // collection of all passwords

    public static void main(String[] args) throws IOException {
        try {
            ServerSocket ss = new ServerSocket(6666);
            System.out.println("starting Server ... ");
            while(true){
                Socket s = ss.accept();
                System.out.println("Client is connected !!!\n");
                ObjectOutputStream outs = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream ins = new ObjectInputStream(s.getInputStream());
                
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
            }
                    
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
        
    }

    public static void employeeOperations(ObjectOutputStream outs, ObjectInputStream ins) throws IOException{
        
        try {
            int ch;
            do{
                ch = (int)ins.readObject();
                String username, password, srvr_response;
                Employee emp = new Employee();
                switch (ch) {
                    case -1:
                        emp = (Employee)ins.readObject();
                        username = emp.username;
                        password = emp.password;
                        if(usrnm.contains(username))
                            srvr_response = "\n*** This username is used by another user :( Please enter another one !!***\n";
                        
                        else if(pswrd.contains(password))
                            srvr_response = "\n*** This password is used by another user :( Please enter another one !!***\n";
                            
                        else{
                            usrnm.add(username);
                            pswrd.add(password); 
                            emp.emp_ID++;
                            emp.emp_user_id = emp.emp_ID;
                            System.out.println(emp.name + " has signed in !!!");
                            ar_emp.add(emp);
                            srvr_response = "\n---------Congrats " + emp.name + " !! You have signed in successfully !! ----------";
                            
                        }
                        outs.reset();
                        outs.writeObject(srvr_response);
                        outs.writeObject(emp);
                        break;
                    
                    case 0:
                        emp = (Employee)ins.readObject();
                        username = emp.username;
                        password = emp.password;

                        if(usrnm.contains(username) && pswrd.contains(password)){
                            int user_id;
                            
                            srvr_response = "\n---------Congrats !! You have logged in successfully !! ----------";
                            for(int i = 0; i < ar_emp.size(); i++){
                                Employee temp = ar_emp.get(i);
                                if(temp.username.equals(username)){
                                    emp = temp;
                                    break;
                                }
                            }
                        }    
                        else{
                            if(usrnm.contains(username) == false)
                                srvr_response = "\n AUTHENTICATION FAILED .. PLEASE CHECK YOUR USERNAME :(";                           
                            else
                                srvr_response = "\n AUTHENTICATION FAILED .. PLEASE CHECK YOUR PASSWORD:(";
                        } 
                        outs.reset();
                        outs.writeObject(srvr_response);
                        outs.writeObject(emp);
                        break;
                    
                    
                    case -2:            // get employee object from database
                        int eid = (int)ins.readObject();
                        Employee e = getEmployeeObject(eid);
                        outs.reset();
                        outs.writeObject(e);
                        break;
                    case 1:
                        int id = (int)ins.readObject();
                        emp = getEmployeeObject(id);
                        if(emp.emp_lv_cnt < emp.total_allowed_leaves){
                            Leave lv = (Leave)ins.readObject();
                            emp.addLeave(lv);
                            pq.add(lv);
                        }
                        break;
                    case 2:
                        int emID = (int)ins.readObject();
                        Employee em = ar_emp.get(emID-1);
                        outs.reset();
                        outs.writeObject(em);
                        // System.out.println("Leave history ...");
                        break;
                    case 3:
                        // System.out.println("Leave Balance ...");
                        break;
                    case 4:
                        // System.out.println(emp.name + " has logged out !!");
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

    public static void managerOperations(ObjectOutputStream outs, ObjectInputStream ins) throws IOException{
       
        try {
            int ch;
            do{
                Manager mng = new Manager();
                ch = (int)ins.readObject();
                String username, password, srvr_response;
                switch (ch) {
                    case -1:
                        mng = (Manager)ins.readObject();
                        username = mng.username;
                        password = mng.password;
                        
                        if(usrnm.contains(username))
                            srvr_response = "\n*** This username is used by another user :( Please enter another one !!***\n";
                            
                        else if(pswrd.contains(password))
                            srvr_response = "\n*** This password is used by another user :( Please enter another one !!***\n";
                        else{
                            usrnm.add(username);
                            pswrd.add(password); 
                            System.out.println(mng.name + " has signed in !!");
                            ar_mng.add(mng);
                            srvr_response = "\n---------Congrats " + mng.name + " !! You have signed in successfully !! ----------";
                        }
                        outs.reset();
                        outs.writeObject(srvr_response);
                        outs.writeObject(mng);
                        break;
                    
                    case 0:
                        mng = (Manager)ins.readObject();
                        username = mng.username;
                        password = mng.password;

                        if(usrnm.contains(username) && pswrd.contains(password)){
                            srvr_response = "\n---------Congrats " + mng.name + "!! You have logged in successfully !! ----------";
                            mng = getManagerObject(mng.username);
                        }    
                        else{
                            if(usrnm.contains(username) == false)
                                srvr_response = "\n AUTHENTICATION FAILED .. PLEASE CHECK YOUR USERNAME :(";                           
                            else
                                srvr_response = "\n AUTHENTICATION FAILED .. PLEASE CHECK YOUR PASSWORD:(";
                            
                        } 
                        outs.reset();
                        outs.writeObject(srvr_response);
                        outs.writeObject(mng);
                        break;
                    
                    case -2:
                        username = (String)ins.readObject();
                        Manager mngr = getManagerObject(username);
                        outs.reset();
                        outs.writeObject(mngr);
                        break;
                    case 1:
                        outs.reset();
                        outs.writeObject(pq);
                        break;
                    case 2:
                        outs.reset();
                        outs.writeObject(pq);
                        if(pq.size() > 0){
                            username = (String)ins.readObject();
                            Leave lv = (Leave)ins.readObject();
                            updateEmployee(lv);
                            updatePriorityQueue(lv);
                            updateManager(username, lv);
                        }   
                        break;
                    case 3:
                        // System.out.println("Leave history");
                        break;
                    case 4:
                        generateLeaveReport();
                        outs.reset();
                        outs.writeObject(hm);
                        int id;
                        do{
                            id = (int)ins.readObject();
                            if(id > -1){
                                try {
                                    outs.writeObject(ar_emp.get(id-1));  
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

    public static void updatePriorityQueue(Leave lv) {
        PriorityQueue<Leave>update = new PriorityQueue<>(new LeaveComparator());
        while(pq.size() != 0){
            Leave temp = pq.poll();
            if(temp.lv_id == lv.lv_id && temp.lv_emp_id == lv.lv_emp_id)    continue;
            update.add(temp);
        }
        pq = update;
    }

    public static void updateEmployee(Leave lv){
        int emp_id = lv.lv_emp_id;
        Employee emp = new Employee();
        for(int i = 0; i < ar_emp.size() ; i++){
            Employee temp = ar_emp.get(i);
            if(temp.emp_user_id == emp_id){
                emp = temp;
                break;
            }
        }

        LinkedList<Leave>l = emp.leave;
        for(int i = 0;i < l.size(); i++){
            Leave obj = l.get(i);
            if(obj.lv_id == lv.lv_id){
                l.set(i, lv);
                break;
            }
        }
    }

    public static void updateManager(String username, Leave lv){
        for(int i = 0 ; i < ar_mng.size(); i++){
            Manager temp = ar_mng.get(i);
            if(temp.username.equals(username)){
                temp.leave.add(lv);
                ar_mng.set(i, temp);
                break;
            }
        } 
    }

    public static Employee getEmployeeObject(int id) {
        Employee emp = new Employee();
        for(int i = 0 ;i < ar_emp.size(); i++){
            Employee temp = ar_emp.get(i);
            if(temp.emp_user_id == id){
                emp = temp;
                break;
            }
        }
        return emp;
    }

    public static Manager getManagerObject(String username){
        Manager mng = new Manager();
        for(int i = 0 ; i < ar_mng.size(); i++){
            Manager temp = ar_mng.get(i);
            if(temp.username.equals(username)){
                mng = temp;
                break;
            }
        }
        return mng;
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

    public static void chatBot(ObjectOutputStream outs, ObjectInputStream ins) throws IOException{
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
