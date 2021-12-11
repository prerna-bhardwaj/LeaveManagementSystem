package socket;

import java.util.Scanner;
import java.util.LinkedList;
import java.time.LocalDate;
import java.io.Serializable;

class Leave implements Serializable{
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
        lv_id = leaveId+1;
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
        String leftAlignFormat = "%-6s|%-3s| %-10s | %-11s | %-10s | %-10s | %-13s | %-11s | %-9s | %-20s | %-11s | %n";
        System.out.println("      +---+------------+-------------+------------+------------+---------------+-------------+-----------+----------------------+-------------+");
        System.out.format(leftAlignFormat,"","NO", "EMP. NAME", "START DATE", "END DATE", "DURATION", "TYPE OF LEAVE","APPLIED ON", "STATUS", "REMARK", "APPROVED BY");
        System.out.println("      +---+------------+-------------+------------+------------+---------------+-------------+-----------+----------------------+-------------+");
        for(int i = 0 ;i < ll.size(); i++){
            Leave temp = new Leave();
            temp = ll.get(i);
            System.out.format(leftAlignFormat,"", i+1, temp.lv_emp_name, temp.lv_start, temp.lv_end, temp.duration, temp.lv_type, temp.lv_date, temp.lv_status, temp.lv_remark, temp.lv_manager);
        }
        System.out.println("      +---+------------+-------------+------------+------------+---------------+-------------+-----------+----------------------+-------------+");

    }
}
