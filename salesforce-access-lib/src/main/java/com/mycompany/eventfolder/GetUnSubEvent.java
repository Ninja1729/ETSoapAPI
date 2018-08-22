package com.mycompany.eventfolder;

import com.exacttarget.wsdl.partnerapi.ClientID;
import com.exacttarget.wsdl.partnerapi.ComplexFilterPart;
import com.exacttarget.wsdl.partnerapi.LogicalOperators;
import com.exacttarget.wsdl.partnerapi.PartnerAPI;
import com.exacttarget.wsdl.partnerapi.RetrieveRequest;
import com.exacttarget.wsdl.partnerapi.RetrieveRequestMsg;
import com.exacttarget.wsdl.partnerapi.RetrieveResponseMsg;
import com.exacttarget.wsdl.partnerapi.SimpleFilterPart;
import com.exacttarget.wsdl.partnerapi.SimpleOperators;
import com.exacttarget.wsdl.partnerapi.Soap;
import com.exacttarget.wsdl.partnerapi.UnsubEvent;
import com.mycompany.util.ClientPasswordCallback;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class GetUnSubEvent {

    /**
     * Activate SOAP calls by providing necessary login credentials
     * @param businessUnitId - BU/MemberID of Exact Target instance.
     * @param username - Exact Target username
     * @param password - Exact Target password
     */
    public static void getUnSubEvent(int businessUnitId, String username, String password) {
        //Create PartnerAPI stub.
        PartnerAPI service = new PartnerAPI();
        Soap stub = service.getSoap();

        Client client = org.apache.cxf.frontend.ClientProxy.getClient(stub);
        Endpoint cxfEndpoint = client.getEndpoint();
        Map<String, Object> outProps = new HashMap<String, Object>();
        outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        outProps.put(WSHandlerConstants.USER, username);
        outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        ClientPasswordCallback cbc = new ClientPasswordCallback();
        cbc.setPassword(password);
        outProps.put(WSHandlerConstants.PW_CALLBACK_REF, cbc);
        WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
        cxfEndpoint.getOutInterceptors().add(wssOut);

        ClientID clientID = new ClientID();
        clientID.setID(businessUnitId);
        getUnsubEventResponse(stub, clientID);
    }

    /**
     *  @param stub - JAX-WS endpoint to make SOAP calls
     *  @param clientID - ClientID of business unit to create the folder in
     */
    private static void getUnsubEventResponse(Soap stub, ClientID clientID) {


        try {
            RetrieveRequest req = new RetrieveRequest();
            req.getClientIDs().add(clientID);
            req.setObjectType("UnsubEvent");
            req.getProperties().add("SendID");
            req.getProperties().add("EventDate");
            req.getProperties().add("SubscriberKey");
            req.getProperties().add("List.ID");

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            GregorianCalendar cal = new GregorianCalendar();

            Date date = format.parse("2017-11-24 11:15:00");
            cal.setTime(date);
            XMLGregorianCalendar xmlDate1 =  DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

            date = format.parse("2017-11-28 11:15:00");
            cal.setTime(date);
            XMLGregorianCalendar xmlDate2 =  DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

            SimpleFilterPart sfp = new SimpleFilterPart();
            sfp.setProperty("EventDate");
            sfp.setSimpleOperator(SimpleOperators.GREATER_THAN_OR_EQUAL);
            sfp.getDateValue().add(xmlDate1);
            SimpleFilterPart sfp2 = new SimpleFilterPart();
            sfp2.setProperty("EventDate");
            sfp2.setSimpleOperator(SimpleOperators.LESS_THAN_OR_EQUAL);
            sfp2.getDateValue().add(xmlDate2);
            ComplexFilterPart cfp = new ComplexFilterPart();
            cfp.setLeftOperand(sfp);
            cfp.setLogicalOperator(LogicalOperators.AND);
            cfp.setRightOperand(sfp2);
            req.setFilter(cfp);

            RetrieveRequestMsg rrMsg = new RetrieveRequestMsg();
            rrMsg.setRetrieveRequest(req);

            RetrieveResponseMsg response = stub.retrieve(rrMsg);

            if ("OK".equals(response.getOverallStatus())) {
                System.out.println("SUCCESS!!!");
            }
            System.out.println("Size of the response"+response.getResults().size());

            //print the first ten response
            for (int i = 0; i< 10; i ++) {
                UnsubEvent e =(UnsubEvent) response.getResults().get(i);
                System.out.println(e.getSubscriberKey());
                System.out.println("______________");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

