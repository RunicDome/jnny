package com.ant.common.utils;




import javax.xml.rpc.ServiceException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

public class WebServiceUtil {

	public static Call getCall() throws ServiceException {
		return (Call) new Service().createCall(); 
	}
}
