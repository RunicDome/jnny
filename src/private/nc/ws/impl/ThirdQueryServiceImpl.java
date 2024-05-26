package nc.ws.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pub.filesystem.FileExAttrDAO;
import nc.impl.pubapp.pattern.database.DBTool;
import nc.itf.uap.pf.IPfExchangeService;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.pubitf.pmr.pm.IPmFeebalanceCtMaintain;
import nc.uif.pub.exception.UifException;
import nc.vo.ct.purdaily.entity.RlPmeFile;
import nc.vo.hrss.pub.FileNodeVO1;
import nc.vo.pcm.feebalance.FeeBalanceBillVO;
import nc.vo.pcm.feebalance.FeeBalanceBodyVO;
import nc.vo.pcm.feebalance.FeeBalanceHeadVO;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pm.feebalance.PmFeebalanceBVO;
import nc.vo.pm.feebalance.PmFeebalanceHVO;
import nc.vo.pmfile.documentcenter.DocumentCenterVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.filesystem.FileExAttrVO;
import nc.ws.intf.IOaWorkFlowService;
import nc.ws.intf.IThirdQueryService;
import nc.ws.intf.OALogVO;
import nc.ws.intf.Result;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ThirdQueryServiceImpl implements IThirdQueryService {

	private HYPubBO hyPubBO;

	@Override
	public String queryService(String data) {
		try {
			JSONObject jsonObj = new JSONObject(data);
			// funCode为操作类型 select:查询;save:保存; update;更新;delete:删除
			String funCode = (String) jsonObj.get("funCode");
			// billType为单据类型 SaleOut:销售出库单
			String billType = (String) jsonObj.get("billType");
			String pk_org = null;// 组织
			if (jsonObj.has("pkOrg")) {
				pk_org = (String) jsonObj.get("pkOrg");
			}
			Map<String, String> where = new HashMap<String, String>();
			if ("select".equals(funCode)) {
				String json = jsonObj.getString("where");
				where = mainToMap(json);
				return select(billType, where);
			} else if ("notice".equals(funCode)) {
				Integer requestid = jsonObj.getInt("requestid");
				if (!"adopt".equals(billType) && !"return".equals(billType)) {
					return Result.error("type传值错误");
				}
				String mes = NCLocator.getInstance().lookup(IOaWorkFlowService.class)
						.oaCallBack(billType, requestid, pk_org);
				return mes;
			} else if ("selectAll".equals(funCode)) {
				String bill_code = jsonObj.getString("bill_code");// 合同编码
				return createAggPmFeebalance(bill_code);
			} else if ("file".equals(funCode)) {
				return updateAggPmFeebalance("1001A1100000001ELER0");
			} else {
				return billService(data);
			}
		} catch (Exception e) {
			try {
				JSONObject jsonObj = new JSONObject(data);
				String billType = (String) jsonObj.get("billType");
				Integer requestid = jsonObj.getInt("requestid");
				OALogVO vo = new OALogVO();
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
				vo.setCreationtime(df.format(new Date()));// 创建时间
				vo.setRequestid(requestid +"");
				vo.setTransi_type(billType);
				vo.setSend_data(data);
				vo.setDef1("请求失败" + e.getMessage());// 错误记录
				new HYPubBO().insert(vo);
			} catch (JSONException | UifException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				return "{\"result\":500,\"msg\":\"请求失败" + e2.getMessage() + "\"}";
			}
			// funCode为操作类型 select:查询;save:保存; update;更新;delete:删除
			
			return "{\"result\":500,\"msg\":\"请求失败" + e.getMessage() + "\"}";
		}
	}

	private String dsName;

	private String getDsName() {

		return this.dsName;
	}

	private String createAggPmFeebalance(String bill_code) throws BusinessException {
		HYPubBO hyPubBO = new HYPubBO();
		IPfExchangeService exchangeService = NCLocator.getInstance().lookup(
				IPfExchangeService.class);
		IPmFeebalanceCtMaintain ipfm = NCLocator.getInstance().lookup(
				IPmFeebalanceCtMaintain.class);
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");
		FeeBalanceHeadVO[] hvos = (FeeBalanceHeadVO[]) hyPubBO
				.queryByCondition(
						FeeBalanceHeadVO.class,
						"nvl(dr,0) = 0 and transi_type = '4D83-Cxx-02' AND def2 = '"+bill_code+"'");

		for (FeeBalanceHeadVO hvo : hvos) {
			String flag = (String) hyPubBO.findColValue("pm_feebalance_ct_b",
					"csourcebillhid", " nvl(dr,0) = 0 and csourcebillhid='"
							+ hvo.getPk_feebalance() + "'");
			if (null == flag) {
				FeeBalanceBodyVO[] bvo = (FeeBalanceBodyVO[]) hyPubBO
						.queryByCondition(
								FeeBalanceBodyVO.class,
								"nvl(dr,0) = 0 and pk_feebalance = '"
										+ hvo.getPk_feebalance() + "'");
				FeeBalanceBillVO agg = new FeeBalanceBillVO();
				agg.setParent(hvo);
				agg.setChildrenVO(bvo);
				List<FeeBalanceBillVO> oldAggvo = new ArrayList<FeeBalanceBillVO>();
				oldAggvo.add(agg);
				AggPmFeebalance[] aggvos = (AggPmFeebalance[]) exchangeService
						.runChangeDataAry("4D83", "4Z01",
								oldAggvo.toArray(new FeeBalanceBillVO[0]), null);
				for (AggPmFeebalance agg1 : aggvos) {
					PmFeebalanceBVO[] bvos = agg1.getChildrenVO();
					int i = 10;
					for (PmFeebalanceBVO bvo1 : bvos) {
						bvo1.setRowno(i + "");
						i = i + 10;
					}
					InvocationInfoProxy.getInstance().setUserId(
							hvo.getBillmaker());
					InvocationInfoProxy.getInstance().setBizDateTime(
							hvo.getBillmaketime().getMillis());
					agg1.getParent().setFstatusflag(1);
					agg1.getParent().setBill_type("4Z01");
					agg1.getParent().setPk_bill_type("0001ZZ1000000006DOCH");
					AggPmFeebalance[] newAgg = { agg1 };
					ipfm.insert(newAgg, null);
					try {
						updateAggPmFeebalance(hvo.getPrimaryKey());
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return "";
	}

	// 传附件
	private String updateAggPmFeebalance(String pk_feebalance) throws BusinessException,
			DbException {
		HYPubBO hyPubBO = new HYPubBO();
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");
		PmFeebalanceHVO[] hvos = (PmFeebalanceHVO[]) hyPubBO.queryByCondition(
				PmFeebalanceHVO.class,
				"nvl(dr,0) = 0 and vdef1 = '"+pk_feebalance+"'");
		FileExAttrDAO dao = new FileExAttrDAO(getDsName());
		List<String> idss = new ArrayList<String>();
		for (PmFeebalanceHVO hvo : hvos) {
			FileNodeVO1[] fnodes = (FileNodeVO1[]) hyPubBO.queryByCondition(
					FileNodeVO1.class, "nvl(dr,0) = 0 and filepath like '%"
							+ hvo.getDef1() + "%' ");
			for (FileNodeVO1 fnode : fnodes) {
				RlPmeFile[] pmes = (RlPmeFile[]) hyPubBO.queryByCondition(
						RlPmeFile.class, "nvl(dr,0) = 0 and file_id = '"
								+ fnode.getPrimaryKey() + "' ");
				// fnode.setPrimaryKey(null);
				fnode.setFilepath(fnode.getFilepath().replaceAll(
						hvo.getDef1(), hvo.getPrimaryKey()));
				fnode.setAttributeValue("pk", createPk());
				fnode.setStatus(VOStatus.NEW);
				String id = hyPubBO.insert(fnode);
				if ("n".equals(fnode.getIsfolder())) {
					idss.add(id);
				}
				// service.insert(fnode);
				// getDao.InsertVOWithPK(fnode);
				if (pmes.length > 0) {
					for (RlPmeFile pme : pmes) {
						// pme.setPk_rl_pme_file(null);
						pme.setFile_id(fnode.getPrimaryKey());
						pme.setStatus(VOStatus.NEW);
						pme.setPk_rl_pme_file(null);
						hyPubBO.insert(pme);
						// getDao.InsertVOWithPK(pme);
					}
				}
				// FileExAttrVO
			}
			DocumentCenterVO[] dcbvos = (DocumentCenterVO[]) hyPubBO
					.queryByCondition(DocumentCenterVO.class,
							"nvl(dr,0) = 0 and pk_bill = '" + hvo.getDef1()
									+ "' ");
			List<String> ids = new ArrayList<String>();
			List<String> docs = new ArrayList<String>();
			if (dcbvos.length > 0) {
				for (DocumentCenterVO dcbvo : dcbvos) {
					// dcbvo.setPk_doccenter(null);
					dcbvo.setBill_code(hvo.getBill_code());
					dcbvo.setPk_billtype("0001ZZ1000000006DOCH");
					dcbvo.setPk_transitype("0001ZZ1000000006DOCH");
					dcbvo.setPk_bill(hvo.getPrimaryKey());
					dcbvo.setStatus(VOStatus.NEW);
					String id = dcbvo.getPk_doccenter();
					dcbvo.setPk_doccenter(null);
					String s = hyPubBO.insert(dcbvo);
					// getDao.InsertVOWithPK(dcbvo);
					ids.add(id);
					docs.add(s);
				}
			}
			FileExAttrVO[] fattrs = dao.queryFileExAttr(ids);
			for (int i = 0; i < fattrs.length; i++) {
				// FileExAttrVO fattr = (FileExAttrVO) temp.clone();
				fattrs[i].setPk_bill(hvo.getPrimaryKey());
				// fattr.setPk_fileexattr(null);
				fattrs[i].setPk_file(idss.get(i));
				fattrs[i].setPk_exattr(docs.get(i));
				// fattr.setStatus(VOStatus.NEW);
				fattrs[i].setPk_fileexattr(null);
				fattrs[i].setPk_billtypecode("4Z01");
				fattrs[i].setPk_fileexattr(createPk());
				// getDao.InsertVOWithPK(fattr);
				dao.insertFileExAttr(fattrs[i]);
			}
		}
		return "";
	}

	private String createPk() {
		DBTool dbTool = new DBTool();
		String[] ids = dbTool.getOIDs(1);
		return ids[0];
	}

	@SuppressWarnings("unchecked")
	public String select(String billType, Map<String, String> where) {
		ArrayList<Object> res = new ArrayList<Object>();
		try {
			String sql = "select * from " + billType;
			if (null != where) {
				int i = 1;
				for (Entry<String, String> entry : where.entrySet()) {
					String mapKey = entry.getKey();
					String mapValue = entry.getValue();
					if (mapKey != null && mapValue != null) {
						if (i == 1) {
							sql += " where " + mapKey + " = '" + mapValue + "'";
							i++;
						} else {
							sql += " and " + mapKey + " = '" + mapValue + "'";
						}
					}
				}
			}
			res = (ArrayList<Object>) new BaseDAO().executeQuery(sql,
					new ArrayListProcessor());
			ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
			for (int i = 0; i < res.size(); i++) {
				Object[] data1 = (Object[]) res.get(i);
				Map<String, String> temp = new HashMap<String, String>();
				for (int j = 0; j < data1.length; j++) {
					String value = "";
					if (null != data1[j]) {
						value = data1[j].toString();
					}
					temp.put("A" + j, value);
				}
				data.add(temp);
			}
			return Result.success(data);
		} catch (DAOException e) {
			return Result.error(e.getMessage());
		}
	}

	public Map<String, String> mainToMap(String json) {
		Map<String, String> map = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			map = mapper.readValue(json,
					new TypeReference<HashMap<String, String>>() {
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public String billService(String data) throws BusinessException {
		data = charSetConvert(data);
		String obj = "";
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(data);
			// funCode为操作类型 select:查询;save:保存; update;更新;delete:删除
			String funCode = (String) jsonObj.get("funCode");
			// billType为单据类型 FinProdIn:产成品入库单
			String billType = (String) jsonObj.get("billType");
			String pkGroup = (String) getHyPubBO().findColValue("org_group",
					"pk_group ",
					" code = '" + IThirdQueryService.GROUP_CODE + "'");
			InvocationInfoProxy.getInstance().setGroupId(pkGroup);// 设置默认的集团主键
			String userId = (String) getHyPubBO().findColValue("sm_user",
					"cuserid",
					" user_code = '" + IThirdQueryService.USER_CODE + "'");
			InvocationInfoProxy.getInstance().setUserId(userId);// 设置默认操作员主键
			// InvocationInfoProxy.getInstance().setUserDataSource("sjkyncc");
			Class<?> clazz = Class.forName("nc.ws.impl.service." + billType
					+ "Service");
			Method method = clazz.getMethod(funCode, String.class);
			obj = (String) method.invoke(clazz.newInstance(), data);
		} catch (IllegalAccessException e) {
			throw new BusinessException(e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new BusinessException(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new BusinessException(e.getMessage());
		} catch (InstantiationException e) {
			throw new BusinessException(e.getMessage());
		} catch (InvocationTargetException e) {
			throw new BusinessException(e.getTargetException().getMessage());
		} catch (BusinessException e) {
			throw new BusinessException(e.getMessage());
		} catch (JSONException e) {
			throw new BusinessException(e.getMessage());
		}
		return obj;
	}

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	// 中文字符处理
	public String charSetConvert(String xmlRequest) {
		String charSet = getEncoding(xmlRequest);
		try {
			byte[] b = xmlRequest.getBytes(charSet);
			xmlRequest = new String(b, "UTF-8");
		} catch (Exception e) {
		}
		return xmlRequest;

	}

	public static String getEncoding(String str) {
		String encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { // 判断是不是GB2312
				String s = encode;
				return s; // 是的话，返回GB2312，以下代码同理
			}
		} catch (Exception e) {
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { // 判断是不是ISO-8859-1
				String s1 = encode;
				return s1;
			}
		} catch (Exception e) {
		}
		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { // 判断是不是UTF-8编码
				String s2 = encode;
				return s2;
			}
		} catch (Exception e) {
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { // 判断是不是GBK
				String s3 = encode;
				return s3;
			}
		} catch (Exception e) {
		}
		return ""; // 到这一步，你就应该检查是不是其他编码啦 }
	}
}
