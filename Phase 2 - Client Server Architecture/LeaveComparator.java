package socket;

import java.util.*;
import java.io.Serializable;

class LeaveComparator implements Comparator<Leave>, Serializable{

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