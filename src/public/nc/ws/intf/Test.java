package nc.ws.intf;

import java.util.List;

import javax.xml.rpc.holders.StringHolder;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.tchzt.tims.webservice.ContentInfoServiceSoapBindingStub;
import com.tchzt.tims.webservice.ContentInfoService_ServiceLocator;

@SuppressWarnings("restriction")
public class Test {

	public static void main(String[] args) {
		try {
			ContentInfoService_ServiceLocator service = new ContentInfoService_ServiceLocator();
			ContentInfoServiceSoapBindingStub stub = (ContentInfoServiceSoapBindingStub) service
					.getPort(ContentInfoServiceSoapBindingStub.class);
			StringHolder arg0 = new StringHolder(new Test().getdocString("1001A1100000001244OO"));
			stub.CMForMobile_OnePicQuery(arg0);
			System.out.println(arg0.value);
			Document doc = null;
			doc = DocumentHelper.parseText(arg0.value);
			Element root = doc.getRootElement();// 指向根节点
			Element BATCH = root.element("BATCH");
			Element DOCUMENTS = BATCH.element("DOCUMENTS");
			List<Element> DOCUMENTS1 = DOCUMENTS.elements("DOCUMENT");
			Element DOCUMENT = DOCUMENTS1.get(0);
			Element FILES = DOCUMENT.element("FILES");
			List<Element> files = FILES.elements("FILE");
			if(files.size() > 0){
				for(Element temp : files){
					System.out.println(temp.elementText("URL"));
					System.out.println(temp.elementText("FILE_NAME"));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getdocString(String pk){
		Document document = DocumentHelper.createDocument();
	    //创建根节点
	    Element root = document.addElement("CMDATA");
	    //添加子节点
	    Element username = root.addElement("TRADETYPE");
	    Element password = root.addElement("CIP");
	    Element SYSTEM_CODE = root.addElement("SYSTEM_CODE");
	    Element BRANCH_NO = root.addElement("BRANCH_NO");
	    Element USER_NO = root.addElement("USER_NO");
	    Element BUSI_SERIAL_NO = root.addElement("BUSI_SERIAL_NO");
	    BUSI_SERIAL_NO.addText(pk);
	    Element BATCH = root.addElement("BATCH");
	    Element BATCHID = BATCH.addElement("BATCHID");
	    Element DOCUMENTS = BATCH.addElement("DOCUMENTS");
	    Element DOCUMENT = DOCUMENTS.addElement("DOCUMENT");
	    Element DOCNAME = DOCUMENT.addElement("DOCNAME");
	    Element DESC = DOCUMENT.addElement("DESC");
	    Element FILES = DOCUMENT.addElement("FILES");
	    Element FILE = FILES.addElement("FILE");
	    Element VERSION = FILE.addElement("VERSION");
	    Element FILE_SEQ = FILE.addElement("FILE_SEQ");
	    Element FILE_TYPE = FILE.addElement("FILE_TYPE");
	    Element FILE_NAME = FILE.addElement("FILE_NAME");
	    Element FILE_FORMAT = FILE.addElement("FILE_FORMAT");
	    Element FILE_SIZE = FILE.addElement("FILE_SIZE");
	    Element FILE_MD5 = FILE.addElement("FILE_MD5");
	    return document.asXML();
	}

	// public static void test() throws ApiException{
	// DingTalkClient client1 = new
	// DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
	// OapiGettokenRequest request = new OapiGettokenRequest();
	// request.setAppkey("dingzbqpfgkxsj5als7f");
	// request.setAppsecret("tH4KksMMcpydaqQ-KvNyg-ZttK6M6jv78scRaXJogFyyf9V1o4C4xxby1u5VnJa1");
	// request.setHttpMethod("GET");
	// OapiGettokenResponse response = client1.execute(request);
	// String access_token = response.getAccessToken();
	// client1 = new
	// DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/processinstance/listids");
	//
	// DingTalkClient client = new
	// DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/processinstance/create");
	// OapiProcessinstanceCreateRequest req = new
	// OapiProcessinstanceCreateRequest();
	// req.setAgentId(209713308L);
	// req.setProcessCode("PROC-EA20B4D3-8E08-4063-9D21-53EF52ED5BB9");
	// req.setOriginatorUserId("manager432");
	// req.setDeptId(100L);
	// req.setApprovers("manager01, manager02");
	// req.setCcList("user2,user3");
	// req.setCcPosition("START");
	// List<FormComponentValueVo> formComponentValueVoList = new
	// ArrayList<FormComponentValueVo>();
	// FormComponentValueVo formComponentValueVo = new FormComponentValueVo();
	// formComponentValueVo.setName("名称");
	// formComponentValueVo.setValue("名称1");
	// formComponentValueVoList.add(formComponentValueVo);
	// req.setFormComponentValues(formComponentValueVoList);
	// FormComponentValueVo formComponentValueVo2 = new FormComponentValueVo();
	//
	// formComponentValueVo.setName("数字");
	// formComponentValueVo.setValue("1");
	//
	// formComponentValueVoList.add(formComponentValueVo2);
	// List<ProcessInstanceApproverVo> processInstanceApproverVoList = new
	// ArrayList<ProcessInstanceApproverVo>();
	// ProcessInstanceApproverVo processInstanceApproverVo = new
	// ProcessInstanceApproverVo();
	// processInstanceApproverVoList.add(processInstanceApproverVo);
	// processInstanceApproverVo.setTaskActionType("AND");
	// processInstanceApproverVo.setUserIds(Arrays.asList(""));
	// req.setApproversV2(processInstanceApproverVoList);
	// OapiProcessinstanceCreateResponse rsp = client.execute(req,
	// access_token);
	// System.out.print(rsp);
	// }
}
