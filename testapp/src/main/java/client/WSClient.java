package client;


import com.mycompany.eventfolder.GetUnSubEvent;

public class WSClient {

    public static void main (String[] args) {

        try {
            GetUnSubEvent.getUnSubEvent(1065830, "xxx", "yyy");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
