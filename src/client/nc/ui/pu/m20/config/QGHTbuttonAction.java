package nc.ui.pu.m20.config;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.bd.material.MaterialVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pu.m20.entity.PraybillHeaderVO;
import nc.vo.pu.m20.entity.PraybillItemVO;
import nc.vo.pu.m20.entity.PraybillVO;
import nc.vo.pub.VOStatus;
import nc.vo.sm.UserVO;
import nc.ws.intf.DCLogVO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@SuppressWarnings({ "restriction", "unused" })
public class QGHTbuttonAction extends NCAction {
	// 请购单推送电采按钮点击触发方法

	private AbstractAppModel model;
	private IEditor editor;

	private static final long serialVersionUID = -777777777771L;

	public QGHTbuttonAction() {
		System.out.println("构造--------------------------");
		super.setBtnName("推送电采");
		// PluginBeanConfigFilePath---nc/ui/pu/m20/config/praybill_config2.xml
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		String mrz = "默认";
		//String defaultValue = null; 
		if (getModel() == null || getModel().getSelectedData() == null) {
			MessageDialog.showHintDlg((Container) editor, "提示", "请先选择一行数据！");
			return;
		}
		PraybillVO qght = (PraybillVO) getModel().getSelectedData();// AggVO
		PraybillHeaderVO qghtz = (PraybillHeaderVO) qght.getParentVO();// 主表VO
		PraybillItemVO[] itemvos = (PraybillItemVO[]) qght.getChildrenVO();
		String pk_praybill = qghtz.getPrimaryKey()+ "";// pk_material档案主键
		System.out.println("请购单主键：" + pk_praybill);
		String where = " pk_bill = '" + pk_praybill + "' and success = 'Y'";
		
		String vdef8 = qghtz.getVdef8();//vdef8是否传递电采系统标识
    	if(vdef8!=null&&vdef8.equals("1")){
    		MessageDialog.showHintDlg((Container) editor, "提示", "此单据已传递电采系统！");
    		return;
    	}
    	
		/*
    	 * 请购单推送合同必须审批完成（审批状态为3审批通过）。
    	 * 请购单推送合同委外不推送，只推送不是委外的。
    	 * */
    	String bsctype = qghtz.getBsctype().toString();//bsctype委外 
    	String fbillstatus = qghtz.getFbillstatus().toString();//fbillstatus单据状态0=自由，1=提交，2=正在审批，3=审批通过，4=审批未通过，5=关闭， 
    	if(bsctype.equals("Y")){
    		MessageDialog.showHintDlg((Container) editor, "提示", "委外单据不可推送！");
    		return;
    	} 
    	if(!fbillstatus.equals("3")){
    		MessageDialog.showHintDlg((Container) editor, "提示", "单据必须审批完成！");
    		return;
    	}
		
		DCLogVO[] dcvos = (DCLogVO[]) HYPubBO_Client.queryByCondition(
				DCLogVO.class, where);
		if (dcvos == null || dcvos.length <= 0) {
			// 拼装主表数据
			JSONArray zbarrjson = new JSONArray();
			JSONObject zbjson = new JSONObject();
			zbjson.put("code", qghtz.getVbillcode());// 请购单号
			//zbjson.put("bislatest", "是");// 最新版本 默认值是
			String qglxstr = "";
			if (null != qghtz.getCtrantypeid()) {
				qglxstr = (String) HYPubBO_Client.findColValue(
						"bd_billtype",
						"billtypename",
						"nvl(dr,0) = 0 and pk_billtypeid = '"
								+ qghtz.getCtrantypeid() + "'");
			}
			zbjson.put("submitType", qglxstr);// 请购类型
			zbjson.put("submitDate", qghtz.getDbilldate().toStdString() + " 00:00:00");// 请购日期
			
			//zbjson.put("pkReqdept", qgbmstr);// 需求部门最新版本
			
			String cglxstr = "";
			if (null != qghtz.getVdef9()) {
				cglxstr = (String) HYPubBO_Client.findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + qghtz.getVdef9()
								+ "'");
			}
			zbjson.put("purchaseType", cglxstr);// 采购类型
			String kczzbmstr = (String) HYPubBO_Client.findColValue(
					"conn_group_elec", "eleccode",
					"nvl(dr,0) = 0 and pk_org = '" + qghtz.getPk_org() + "'");
			zbjson.put("stockOrgCode", kczzbmstr);// 库存组织编码
			String jhybmstr = (String) HYPubBO_Client.findColValue(
					"conn_user_elec",
					"eleccode",
					"nvl(dr,0) = 0 and pk_user = '" + qghtz.getPk_planpsn()
							+ "'");
			zbjson.put("planUserName", jhybmstr);// 计划员账号
			String jhbmstr = (String) HYPubBO_Client.findColValue(
					"conn_org_elec",
					"eleccode",
					"nvl(dr,0) = 0 and pk_org = '" + qghtz.getPk_plandept()
							+ "'");
			zbjson.put("planDepartCode", jhbmstr);// 计划部门
			String cjrbmstr = (String) HYPubBO_Client.findColValue("sm_user",
					"user_code",
					"nvl(dr,0) = 0 and cuserid = '" + qghtz.getCreator() + "'");
			System.out.println("--------" + cjrbmstr);
			String billMaker = (String) HYPubBO_Client.findColValue(
					"conn_user_elec",
					"eleccode",
					"nvl(dr,0) = 0 and user_code = '" + cjrbmstr+ "'");
			
			System.out.println("--------" + billMaker);
			zbjson.put("createBy", billMaker);// 创建人账号
			String sprbmstr = (String) HYPubBO_Client
					.findColValue(
							"sm_user",
							"user_name",
							"nvl(dr,0) = 0 and cuserid = '"
									+ qghtz.getApprover() + "'");
			zbjson.put("approver", sprbmstr);// 审批人账号
			zbjson.put("bscType", qghtz.getBsctype()+ "");// 委外 Y||N
			zbjson.put("createDate", qghtz.getCreationtime()+ "");// 创建时间
//			String zdrbmstr = (String) HYPubBO_Client.findColValue(
//					"sm_user",
//					"user_name",
//					"nvl(dr,0) = 0 and cuserid = '" + qghtz.getBillmaker()
//							+ "'");
			zbjson.put("billMaker", billMaker);// 制单人账号
			zbjson.put("dMakeDate", qghtz.getDmakedate()+ "");// 制单日期
			zbjson.put("fBillStatus", qghtz.getFbillstatus()+ "");// 单据状态
			zbjson.put("nTotalastNum", qghtz.getNtotalastnum()+ "");// 总数量
			zbjson.put("nTotalTaxmny", qghtz.getNtotaltaxmny()+ "");// 本币价税合计
			zbjson.put("tAuditTime", qghtz.getTaudittime() + "");// 审批日期

			String qglxbmstr = (String) HYPubBO_Client.findColValue(
					"bd_billtype",
					"pk_billtypecode",
					"nvl(dr,0) = 0 and pk_billtypeid = '"
							+ qghtz.getCtrantypeid() + "'");
			zbjson.put("reqTypeCode", qglxbmstr);// 请购类型编码
			zbjson.put("remark", qghtz.getVmemo()+ "");// 备注
			zbjson.put("isMakeProject", "~");// 备注

			// 明细
			JSONArray mxarrjson = new JSONArray();
			JSONObject mxjson = new JSONObject();
			for (int i = 0; i < itemvos.length; i++) {
				PraybillItemVO itemvo = itemvos[i];
				// 项目
				if (null != itemvo.getCprojectid()) {
					ProjectHeadVO projectVO = (ProjectHeadVO) HYPubBO_Client
							.queryByPrimaryKey(ProjectHeadVO.class,
									itemvo.getCprojectid());
					mxjson.put("projectName", projectVO.getProject_name());// 项目名称
					mxjson.put("projectId", projectVO.getPk_project());// 项目名称
					mxjson.put("proCode", projectVO.getProject_code());// 项目编码
					if (i == 0) {
						zbjson.put("proCode", projectVO.getProject_code());// 项目编码
						zbjson.put("contractDepartCode", "~");// 承包单位编码
						zbjson.put("infrastructureProjectType", "~");// 基建项目类型
						zbjson.put("name", projectVO.getProject_name());// 项目名称
						zbjson.put("planName", projectVO.getProject_name());// 请购名称
						zbjson.put("chProjectId", projectVO.getPk_project());// 项目ID
						String eps_code = (String) HYPubBO_Client.findColValue(
								"pm_eps",
								"eps_code",
								"nvl(dr,0) = 0 and pk_eps = '"
										+ projectVO.getPk_eps() + "'");
						zbjson.put("projectCode", eps_code);// 立项编号
						
//						String purchaseType = (String) HYPubBO_Client.findColValue(
//								"bd_billtype",
//								"billtypename",
//								"nvl(dr,0) = 0 and pk_billtypeid = '"
//										+ itemvo.getCordertrantypecode() + "'");
//						zbjson.put("purchaseType", purchaseType);// 采购类型
						
						// 需求部门
						String qgbmstr = "";
						if (null != itemvo.getPk_reqdept()) {
							qgbmstr = (String) HYPubBO_Client.findColValue(
									"conn_org_elec", "eleccode",
									"nvl(dr,0) = 0 and pk_org = '" + itemvo.getPk_reqdept() + "'");
						}
						zbjson.put("requireDepart", qgbmstr);
//						zbjson.put("requireDepart", jhbmstr);
					}
				}
				
				mxjson.put("no", itemvo.getCrowno()+ "");// 序号
				// 物料
				if (null != itemvo.getPk_material()) {
					MaterialVO wlVO = (MaterialVO) HYPubBO_Client
							.queryByPrimaryKey(MaterialVO.class,
									itemvo.getPk_material());
					mxjson.put("code", wlVO.getCode());// 物料编码
					mxjson.put("name", wlVO.getName());// 物料名称
					mxjson.put("pkMaterial", wlVO.getName());// 物料最新版本
					mxjson.put("description", mrz);// 物料描述
					if (null != wlVO.getPk_measdoc()) {
						String dwstr = (String) HYPubBO_Client.findColValue(
								"bd_measdoc", "code",
								"nvl(dr,0) = 0 and pk_measdoc  = '"
										+ wlVO.getPk_measdoc() + "'");
						mxjson.put("unit", dwstr);// 单位
					}
				}
				mxjson.put("submitDate", itemvo.getDbilldate()+ "");// 请购日期
				mxjson.put("quantity", itemvo.getNastnum()+ "");// 数量
				mxjson.put("suggestedOrderDate", itemvo.getDsuggestdate() + "");// 建议订货日期
				mxjson.put("requireDate", itemvo.getDreqdate()+ "");// 需求日期
				mxjson.put("remark", itemvo.getVbmemo()+ "");// 备注
				
				String pk_org = "";
				String cgzzbmstr = "";
				if (null != itemvo.getPk_purchaseorg()) {
					cgzzbmstr = (String) HYPubBO_Client.findColValue(
							"conn_group_elec", "eleccode",
							"nvl(dr,0) = 0 and pk_org = '" + itemvo.getPk_purchaseorg() + "'");
				}
				// --------------------------------------------------------
				mxjson.put("purchaseOrgCode", cgzzbmstr);// 采购组织编码
				//mxjson.put("pkPurchaseorg", cgzzbmstr);// 采购组织最新版本
				
				mxjson.put("purpose", mrz);// 用途
				mxjson.put("planDate", itemvo.getDsuggestdate()+"");// 计划日期
				mxjson.put("expectPrice", 0);// 参考单价
				mxjson.put("expectTotal", 0);// 参考金额
//				// 订单类型
//				String ddlxbm = "";
//				if (null != itemvo.getCordertrantypecode()) {
//					ddlxbm = (String) HYPubBO_Client.findColValue(
//							"bd_billtype",
//							"billtypename",
//							"nvl(dr,0) = 0 and pk_billtypeid = '"
//									+ itemvo.getCordertrantypecode() + "'");
//				}
//				mxjson.put("orderType", ddlxbm);
				
				// 主单位
//				String zdwbm = "";
//				if (null != itemvo.getCunitid()) {
//					zdwbm = (String) HYPubBO_Client.findColValue(
//							"bd_measdoc",
//							"name",
//							"nvl(dr,0) = 0 and pk_measdoc  = '"
//									+ itemvo.getCunitid() + "'");
//				}
//				mxjson.put("mainUnit", zdwbm);
//				mxjson.put("mainQuantity", itemvo.getNnum()+ "");// 主数量
				
				mxjson.put("pk_praybill_b", itemvo.getPk_praybill_b()+ "");// 主键

//				String mxkczzbmstr = (String) HYPubBO_Client.findColValue(
//						"conn_group_elec",
//						"eleccode",
//						"nvl(dr,0) = 0 and pk_org = '" + itemvo.getPk_org()
//								+ "'");
//				mxjson.put("purchaseOrgCode", mxkczzbmstr);
//				System.out.println("------------------------" + mxkczzbmstr);
				//zbjson.put("pkOrg", mxkczzbmstr);// 库存组织
				//zbjson.put("stockOrgCode", mxkczzbmstr);// 库存组织最新版本
				mxarrjson.add(mxjson);
			}
			zbjson.put("detailList", mxarrjson);
			zbarrjson.add(zbjson);
			// 电采物料接口url
			String dcUrl = (String) HYPubBO_Client.findColValue("sys_config",
					"config_value", "nvl(dr,0) = 0 and config_id = 52");
			// 调用接口
			
			// JSONObject rtndata = sendPost(dcUrl, zbarrjson+ "");
//			String rtndatastr = HttpClient.httpPostRaw(dcUrl, zbarrjson+ "",
//					null, "utf-8");
			System.out.println("=============================================="+qglxbmstr);
			String rtndatastr = post(dcUrl, zbarrjson+"");
			if(null == rtndatastr || rtndatastr.equals("") || rtndatastr.equals("null")) {
				MessageDialog.showHintDlg((Container) editor, "提示",
						"操作失败，电采返回空数据，请联系工程师！");
				return;
			}
			JSONObject rtndata = JSONObject.fromObject(rtndatastr);
			if (rtndata != null) {
				Boolean success = rtndata.getBoolean("success");
				DCLogVO vo = new DCLogVO();
				vo.setBilltype("请购单");
				vo.setPk_bill(qghtz.getPrimaryKey());
				vo.setPk_org(qghtz.getPk_org());
				vo.setReturn_data(rtndata+ "");
				if (success == true) {
					vo.setSuccess("Y");
				} else {
					vo.setSuccess("N");
				}
				vo.setUrl(dcUrl);
				// 当前登录人ID
				String userID = InvocationInfoProxy.getInstance().getUserId();
				UserVO userVO = (UserVO) HYPubBO_Client.queryByPrimaryKey(
						UserVO.class, userID);
				vo.setUserid(userID);
				vo.setUsername(userVO.getUser_name());
				// 组织
				String zzmc = (String) HYPubBO_Client.findColValue(
						"org_orgs",
						"name",
						"nvl(dr,0) = 0 and pk_org = '" + qghtz.getPk_org()
								+ "'");
				vo.setZzmc(zzmc);
				HYPubBO_Client.insert(vo);
				if (success == true) {
					// 成功后设置自定义项8（是否推动电采）为1：是
					/*qghtz.setVdef8("1");
					qghtz.setStatus(VOStatus.UPDATED);
					HYPubBO_Client.update(qghtz);*/
					String updsql = "UPDATE PO_PRAYBILL SET VDEF8 = '1',DR = 0 WHERE PK_PRAYBILL = '"+qghtz.getPrimaryKey()+"'";
					GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
					getDao.executeUpdate(updsql);
					MessageDialog.showHintDlg((Container) editor, "提示", rtndata
							.getString("msg")+ "");
				} else {
					String msg = rtndata.getString("msg");
					if(msg.indexOf("物料")>-1){
						msg += ", 请联系物资部湛蕾推送物料到电采平台!";
					}
					
					MessageDialog.showHintDlg((Container) editor, "提示", "推送失败，"
							+ msg + "");
				}
			} else {
				if(dcvos != null && dcvos.length > 0){
					MessageDialog.showHintDlg((Container) editor, "提示",
							"该请购单已同步至电采平台，" + "操作时间为[" + dcvos[0].getTs() + "]");
					return;
				}else {
					MessageDialog.showHintDlg((Container) editor, "提示",
							"操作失败，请联系工程师！");
					return;
				}
			}
		}

	}

	public AbstractAppModel getModel() {
		return this.model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public IEditor getEditor() {
		return this.editor;
	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}

//	private HYPubBO hyPubBO;
//
//	public HYPubBO HYPubBO_Client {
//		if (null == hyPubBO) {
//			hyPubBO = new HYPubBO();
//		}
//		return hyPubBO;
//	}
//
//	public void setHyPubBO(HYPubBO hyPubBO) {
//		this.hyPubBO = hyPubBO;
//	}

	protected boolean isActionEnable() {
		PraybillVO vo = (PraybillVO) getModel().getSelectedData();// 档案信息
		if (null == vo) {
			return false;
		}
		return true;
	}
	
	public static String sendPost(String url,String param){
        OutputStreamWriter  out = null;
        BufferedReader ins = null;
        String result = "请求失败";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-type", "application/json;charset=utf-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
             // 发送请求参数
            out.write(param);

            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            ins = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = ins.readLine()) != null) {
                result = line;
            }
        } catch (Exception e) {
//            println e
//            result=["错误":e,"json":param]
            System.out.println("[POST请求]向地址：" + url + " 发送数据：" + param + " 发生错误!");
            
        } finally {// 使用finally块来关闭输出流、输入流
            try {
                if (out != null) {
                    out.close();
                }
                if (ins != null) {
                    ins.close();
                }
            } catch (IOException ex) {
                System.out.println("关闭流异常");
            }
        }
        return result;
    }
	
	public static String post(String apiUrl, String jsondata) {
		String response = "";
		System.out.println("请求连接："+ apiUrl + " , 发送数据：" + jsondata);
		try {
			// 创建url对象
			URL url = new URL(apiUrl);
			// 打开url连接
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// 设置url请求方式 ‘get’ 或者 ‘post’
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setConnectTimeout(3000);
			connection.setDoInput(true);  
			connection.setDoOutput(true);  
			connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			 
	        if (jsondata != null && jsondata.length() > 0) {
	            OutputStream os = connection.getOutputStream();
	            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
	 
	            bufferedWriter.write(jsondata);
	            bufferedWriter.flush();
	            bufferedWriter.close();
	            os.close();
	        }
	 
	        InputStream inputStream = null;
	        if (connection != null) {
	            inputStream = connection.getInputStream();
	        }
	        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
	 
	        response = reader.readLine();
	        System.out.println("返回信息：" + response);
	        reader.close();
	 
	        if (connection != null)
	        	connection.disconnect();
	 
 
	    } catch (UnknownHostException one) {
	        System.out.println(one);
	    	return response;
	 
	    } catch (SocketException two) {
	    	System.out.println(two); 
	    	return response;
	 
	    } catch (Exception three) {
	    	System.out.println(three); 
	         return response;
	    }
		return response;
	}
}
