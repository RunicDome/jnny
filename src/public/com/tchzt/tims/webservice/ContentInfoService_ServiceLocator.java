/**
 * ContentInfoService_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tchzt.tims.webservice;

public class ContentInfoService_ServiceLocator extends org.apache.axis.client.Service implements com.tchzt.tims.webservice.ContentInfoService_Service {

    public ContentInfoService_ServiceLocator() {
    }


    public ContentInfoService_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ContentInfoService_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ContentInfoServiceImplPort
    private java.lang.String ContentInfoServiceImplPort_address = "http://172.18.128.94:2333/services/ContentInfoService";
 // private java.lang.String ContentInfoServiceImplPort_address = "http://172.18.130.232:2333/services/ContentInfoService";

    public java.lang.String getContentInfoServiceImplPortAddress() {
        return ContentInfoServiceImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ContentInfoServiceImplPortWSDDServiceName = "ContentInfoServiceImplPort";

    public java.lang.String getContentInfoServiceImplPortWSDDServiceName() {
        return ContentInfoServiceImplPortWSDDServiceName;
    }

    public void setContentInfoServiceImplPortWSDDServiceName(java.lang.String name) {
        ContentInfoServiceImplPortWSDDServiceName = name;
    }

    public com.tchzt.tims.webservice.ContentInfoService_PortType getContentInfoServiceImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ContentInfoServiceImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getContentInfoServiceImplPort(endpoint);
    }

    public com.tchzt.tims.webservice.ContentInfoService_PortType getContentInfoServiceImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.tchzt.tims.webservice.ContentInfoServiceSoapBindingStub _stub = new com.tchzt.tims.webservice.ContentInfoServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getContentInfoServiceImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setContentInfoServiceImplPortEndpointAddress(java.lang.String address) {
        ContentInfoServiceImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.tchzt.tims.webservice.ContentInfoService_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.tchzt.tims.webservice.ContentInfoServiceSoapBindingStub _stub = new com.tchzt.tims.webservice.ContentInfoServiceSoapBindingStub(new java.net.URL(ContentInfoServiceImplPort_address), this);
                _stub.setPortName(getContentInfoServiceImplPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("ContentInfoServiceImplPort".equals(inputPortName)) {
            return getContentInfoServiceImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://webservice.tims.tchzt.com/", "ContentInfoService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://webservice.tims.tchzt.com/", "ContentInfoServiceImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ContentInfoServiceImplPort".equals(portName)) {
            setContentInfoServiceImplPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
