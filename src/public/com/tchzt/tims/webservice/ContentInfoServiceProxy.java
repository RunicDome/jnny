package com.tchzt.tims.webservice;

public class ContentInfoServiceProxy implements com.tchzt.tims.webservice.ContentInfoService_PortType {
  private String _endpoint = null;
  private com.tchzt.tims.webservice.ContentInfoService_PortType contentInfoService_PortType = null;
  
  public ContentInfoServiceProxy() {
    _initContentInfoServiceProxy();
  }
  
  public ContentInfoServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initContentInfoServiceProxy();
  }
  
  private void _initContentInfoServiceProxy() {
    try {
      contentInfoService_PortType = (new com.tchzt.tims.webservice.ContentInfoService_ServiceLocator()).getContentInfoServiceImplPort();
      if (contentInfoService_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)contentInfoService_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)contentInfoService_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (contentInfoService_PortType != null)
      ((javax.xml.rpc.Stub)contentInfoService_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.tchzt.tims.webservice.ContentInfoService_PortType getContentInfoService_PortType() {
    if (contentInfoService_PortType == null)
      _initContentInfoServiceProxy();
    return contentInfoService_PortType;
  }
  
  public void selectState(javax.xml.rpc.holders.StringHolder arg0) throws java.rmi.RemoteException{
    if (contentInfoService_PortType == null)
      _initContentInfoServiceProxy();
    contentInfoService_PortType.selectState(arg0);
  }
  
  public void CMForMobile_OnePicQuery(javax.xml.rpc.holders.StringHolder arg0) throws java.rmi.RemoteException{
    if (contentInfoService_PortType == null)
      _initContentInfoServiceProxy();
    contentInfoService_PortType.CMForMobile_OnePicQuery(arg0);
  }
  
  public void CMForMobile_OnePicDownload(javax.xml.rpc.holders.StringHolder arg0) throws java.rmi.RemoteException{
    if (contentInfoService_PortType == null)
      _initContentInfoServiceProxy();
    contentInfoService_PortType.CMForMobile_OnePicDownload(arg0);
  }
  
  
}