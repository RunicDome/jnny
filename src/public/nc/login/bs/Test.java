//package nc.login.bs;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import com.dingtalk.api.DefaultDingTalkClient;
//import com.dingtalk.api.DingTalkClient;
//import com.dingtalk.api.request.OapiGettokenRequest;
//import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
//import com.dingtalk.api.request.OapiV2DepartmentListsubidRequest;
//import com.dingtalk.api.request.OapiV2UserListRequest;
//import com.dingtalk.api.request.OapiProcessinstanceCreateRequest.FormComponentValueVo;
//import com.dingtalk.api.request.OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo;
//import com.dingtalk.api.response.OapiGettokenResponse;
//import com.dingtalk.api.response.OapiProcessinstanceCreateResponse;
//import com.dingtalk.api.response.OapiV2DepartmentListsubidResponse;
//import com.dingtalk.api.response.OapiV2UserListResponse;
//import com.taobao.api.ApiException;
//
//public class Test {
//	
//
//	public static void test() throws ApiException{
//		
//		
//		DingTalkClient client1 = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
//		OapiGettokenRequest request = new OapiGettokenRequest();
//		request.setAppkey("dingzbqpfgkxsj5als7f");
//		request.setAppsecret("tH4KksMMcpydaqQ-KvNyg-ZttK6M6jv78scRaXJogFyyf9V1o4C4xxby1u5VnJa1");
//		request.setHttpMethod("GET");
//		OapiGettokenResponse response = client1.execute(request);
//		String access_token = response.getAccessToken();
//		client1 = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/processinstance/listids");
//
////		getUserList(access_token);
////		getUserList(access_token);
//		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/processinstance/create");
//		OapiProcessinstanceCreateRequest req = new OapiProcessinstanceCreateRequest();
//		req.setAgentId(209713308L);
//		req.setProcessCode("PROC-EA20B4D3-8E08-4063-9D21-53EF52ED5BB9");
//		req.setOriginatorUserId("0545034359677724");
//		req.setDeptId(92135838L);
//		req.setApprovers("0363075950845983");
////		req.setCcList("user2,user3");
////		req.setCcPosition("START");
//		List<FormComponentValueVo> formComponentValueVoList = new ArrayList<FormComponentValueVo>();
//		FormComponentValueVo formComponentValueVo = new FormComponentValueVo();
//		formComponentValueVo.setName("名称");
//		formComponentValueVo.setValue("名称999991");
//		formComponentValueVoList.add(formComponentValueVo);
//		FormComponentValueVo formComponentValueVo2 = new FormComponentValueVo();
//		formComponentValueVo2.setName("数字");
//		formComponentValueVo2.setValue("199999");
//		formComponentValueVoList.add(formComponentValueVo2);
//		FormComponentValueVo formComponentValueVo3 = new FormComponentValueVo();
//		formComponentValueVo3.setName("单选框");
//		formComponentValueVo3.setValue("1");
//		formComponentValueVoList.add(formComponentValueVo3);
//		req.setFormComponentValues(formComponentValueVoList);
//		List<ProcessInstanceApproverVo> processInstanceApproverVoList = new ArrayList<ProcessInstanceApproverVo>();
//		ProcessInstanceApproverVo processInstanceApproverVo = new ProcessInstanceApproverVo();
//		processInstanceApproverVoList.add(processInstanceApproverVo);
//		processInstanceApproverVo.setTaskActionType("AND");
//		processInstanceApproverVo.setUserIds(Arrays.asList(""));
//		req.setApproversV2(processInstanceApproverVoList);  
//		OapiProcessinstanceCreateResponse rsp = client.execute(req, access_token);
//		System.out.print(rsp);
//	}
//
//	private static void getUserList(String access_token) throws ApiException {
//		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/list");
//		OapiV2UserListRequest req = new OapiV2UserListRequest();
//		req.setDeptId(1L);
//		req.setCursor(0L);
//		req.setSize(10L);
//		req.setOrderField("modify_desc");
//		req.setContainAccessLimit(false);
//		req.setLanguage("zh_CN");
//		OapiV2UserListResponse rsp = client.execute(req, access_token);
//		System.out.println(rsp.getBody());
//	}
//
//	private static void getDeptList(String access_token) throws ApiException {
//		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/listsubid");
//		OapiV2DepartmentListsubidRequest req = new OapiV2DepartmentListsubidRequest();
//		req.setDeptId(1L);
//		OapiV2DepartmentListsubidResponse rsp = client.execute(req, access_token);
//		System.out.println(rsp.getBody());
//	}
//
//	public static void main(String[] args) {
//		try {
//			test();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//	}
//}