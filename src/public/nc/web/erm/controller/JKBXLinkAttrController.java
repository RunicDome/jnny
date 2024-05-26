package nc.web.erm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.itf.erm.fieldmap.IBillFieldGet;
import nc.nweb.erm.pub.ERMWebConst;
import nc.nweb.erm.util.BodyDataChangeLogic;
import nc.nweb.erm.util.DataChangeLogic;
import nc.nweb.erm.util.ERMDataTableUtil;
import nc.nweb.erm.util.FIDataTableUtil;
import nc.nweb.erm.util.Func;
import nc.ui.pub.beans.MessageDialog;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.web.erm.utils.CrossRulePubUtil;
import nc.web.erm.utils.ERMReimResultVO;
import nc.web.erm.utils.ERMReimRuleUtil;
import nc.web.erm.utils.ERMValueCheck;

import org.springframework.stereotype.Component;

import uap.iweb.entity.DataTable;
import uap.iweb.entity.Row;
import uap.iweb.event.EventResponse;
import uap.iweb.event.run.DataTableFieldEvent;
import uap.iweb.icontext.IWebViewContext;
import uap.web.util.JsonUtil;

// 报销单据加表头表体编辑事件
@Component("JKBXLinkAttrController")
public class JKBXLinkAttrController {

	/**
	 * 冲借款回写报销单业务行后，根据原币计算本币
	 * 
	 * @param dtEvent
	 * @throws Exception
	 */
	public void afterCJK() throws Exception {
		DataTable hTable = FIDataTableUtil.getDataTable(ERMWebConst.ERM_HEAD_DT);
		DataTable[] bTableArr = ERMDataTableUtil.getBusitemDataTableArr();
		DataChangeLogic.doBodyCalculation(hTable, bTableArr);
	}

	/**
	 * 行保存后操作，目前是做借款报销标准控制
	 * 
	 * @param dtEvent
	 * @throws Exception
	 */
	public void rowSaved() throws Exception {
		String pk_billtemplet = IWebViewContext.getEventParameter("pk_billtemplet");// 模板
		String tableID = IWebViewContext.getEventParameter("tableID");// 页签
		
		DataTable hTable = FIDataTableUtil.getDataTable(ERMWebConst.ERM_HEAD_DT);
		JKBXHeaderVO headVO = (JKBXHeaderVO) hTable.toBean(hTable.getCurrentRow());
		
		new ERMReimRuleUtil().doBodyReimSingleTabCurRow(tableID, pk_billtemplet, headVO);
	}

	/**
	 * 计算分摊页签的承担金额，区分币种
	 * 
	 * @param dtEvent
	 * @throws Exception
	 */
	public void calAssumeAmount() throws Exception {
		try {
			DataTable hTable = FIDataTableUtil.getDataTable(ERMWebConst.ERM_HEAD_DT);
			DataTable[] bTableArr = ERMDataTableUtil.getBusitemDataTableArr();// 业务页签
			DataTable shareTable = FIDataTableUtil.getDataTable(ERMWebConst.ERM_BX_BODY_CSHARE_DT);// 分摊页签
			Map<String, UFDouble> bzbmAmountMap = new HashMap<String, UFDouble>();
			for (DataTable bTable : bTableArr) {
				for (Row row : bTable.getAllRow()) {
					String bzbm = Func.toString(FIDataTableUtil.getBodyValueByRow(row, IBillFieldGet.BZBM_BUSITEM));
					if (ERMValueCheck.isNotEmpty(bzbm)) {
						UFDouble amount = null == FIDataTableUtil.getBodyValueByRow(row, IBillFieldGet.AMOUNT_BUSITEM) ? UFDouble.ZERO_DBL
								: (UFDouble) FIDataTableUtil.getBodyValueByRow(row, IBillFieldGet.AMOUNT_BUSITEM);
						if (bzbmAmountMap.containsKey(bzbm)) {
							bzbmAmountMap.put(bzbm, bzbmAmountMap.get(bzbm).add(amount));
						} else {
							bzbmAmountMap.put(bzbm, amount);
						}
					}
				}
			}
			String shareBzbm = Func.toString(FIDataTableUtil.getBodyValueByRow(shareTable.getCurrentRow(),
					IBillFieldGet.BZBM_BUSITEM));
			UFDouble sharedAmount = UFDouble.ZERO_DBL;
			for (int i = 0; i < shareTable.getAllRow().length; i++) {
				if (i == shareTable.getAllRow().length - 1)
					continue;
				UFDouble eachSharedAmount = null == FIDataTableUtil.getBodyValueByRow(shareTable.getAllRow()[i],
						IBillFieldGet.ASSUME_AMOUNT_SHARE) ? UFDouble.ZERO_DBL : (UFDouble) FIDataTableUtil
						.getBodyValueByRow(shareTable.getAllRow()[i], IBillFieldGet.ASSUME_AMOUNT_SHARE);
				sharedAmount = sharedAmount.add(eachSharedAmount);
			}
			UFDouble currAssumeAmount = UFDouble.ZERO_DBL;
			if (bzbmAmountMap.containsKey(shareBzbm) && bzbmAmountMap.get(shareBzbm).compareTo(sharedAmount) > 0) {
				currAssumeAmount = bzbmAmountMap.get(shareBzbm).sub(sharedAmount);
			}
			FIDataTableUtil.setBodyValueByRow(shareTable.getCurrentRow(), IBillFieldGet.ASSUME_AMOUNT_SHARE,
					currAssumeAmount);
			DataChangeLogic.afterEditSHARE_ASSUME_AMOUNT(hTable, shareTable.getCurrentRow());
		} catch (Exception e) {
			// TODO Auto-generated catch block

            Logger.debug(e.getMessage());

			//e.printStackTrace();
		}
	}

	public void getReimDimList() throws Exception {
		DataTable hTable = FIDataTableUtil.getDataTable(ERMWebConst.ERM_HEAD_DT);
		JKBXHeaderVO headVO = (JKBXHeaderVO) hTable.toBean(hTable.getCurrentRow());
		String djlxbm = headVO.getDjlxbm();
		String pk_org = headVO.getPk_org();
		List<String> reimDimList = new ERMReimRuleUtil().getReimDimList(djlxbm, pk_org);
		EventResponse response = IWebViewContext.getResponse();
		response.getWriter().write(JsonUtil.toJson(reimDimList.toArray(new String[reimDimList.size()])));
	}

	public void handleReim(DataTableFieldEvent dtEvent) throws Exception {
		String tableID = dtEvent.getDataTable();
		String pk_billtemplet = IWebViewContext.getEventParameter("pk_billtemplet");
		if (ERMDataTableUtil.getBusitemDataTableIdList().contains(tableID)) {
			DataTable hTable = FIDataTableUtil.getDataTable(ERMWebConst.ERM_HEAD_DT);
			DataTable bTable = FIDataTableUtil.getDataTable(tableID);
			JKBXHeaderVO headVO = (JKBXHeaderVO) hTable.toBean(hTable.getCurrentRow());
			BXBusItemVO currentBusitemVO = (BXBusItemVO) bTable.toBean(bTable.getCurrentRow());// 只处理当前行的标准控制
			currentBusitemVO.setTablecode(tableID.substring(6));// 这里需要给tablecode赋值，否则报销标准会取不到值
			String djlxbm = headVO.getDjlxbm();
			JKBXVO jkbxvo = null;
			if (djlxbm.startsWith("263")) {
				jkbxvo = new JKVO(headVO, new BXBusItemVO[] { currentBusitemVO });
			} else if (djlxbm.startsWith("264")) {
				jkbxvo = new BXVO(headVO, new BXBusItemVO[] { currentBusitemVO });
			}
			ERMReimResultVO reimResultVO = new ERMReimRuleUtil().doBodyReimAction(pk_billtemplet, jkbxvo);// 借款报销标准控制
			FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(), reimResultVO.getShowItemKey(),
					reimResultVO.getJkbxbz());
		}
	}

	public void valueChanged() throws Exception {
		DataTable hTable = FIDataTableUtil.getDataTable(ERMWebConst.ERM_HEAD_DT);
		DataTable[] bTableArr = ERMDataTableUtil.getBusitemDataTableArr();
		List<String> bTableIdList = ERMDataTableUtil.getBusitemDataTableIdList();
		DataTable shareTable = FIDataTableUtil.getDataTable(ERMWebConst.ERM_BX_BODY_CSHARE_DT);
		String tableID = IWebViewContext.getEventParameter("tableID");
		String field = IWebViewContext.getEventParameter("field");
		String newValue = IWebViewContext.getEventParameter("newValue");
		// String oldValue = IWebViewContext.getEventParameter("oldValue");
		DataTable editTable = FIDataTableUtil.getDataTable(tableID);//编辑的页签
		try {
			// throw new BusinessException("测试："+field);
			if (ERMWebConst.ERM_MIANORG_DT.equals(tableID) && IBillFieldGet.PK_ORG.equals(field)) {// 组织面板组织编辑事件
				String pk_org = newValue;// 组织面板上面的PK_ORG
				DataChangeLogic.afterEditHeadPKORG(hTable, bTableArr, shareTable, pk_org);
			}
			if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && IBillFieldGet.PK_ORG.equals(field)) {// 表头主组织编辑事件
				String pk_org = newValue;// 表头上的的PK_ORG
				DataChangeLogic.afterEditHeadPKORG(hTable, bTableArr, shareTable, pk_org);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(IBillFieldGet.DJRQ)) {// 表头单据日期编辑后事件
				DataChangeLogic.afterEditHeadDJRQ(hTable, bTableArr);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(IBillFieldGet.BZBM)) {// 表头币种编辑后事件
				DataChangeLogic.afterEditHeadBZ(hTable, bTableArr, shareTable);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(IBillFieldGet.BBHL)) {// 表头汇率编辑后事件
				DataChangeLogic.afterEditHeadHL(hTable, bTableArr);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.DWBM_V)) {// 表头借款报销人单位编辑后事件
				DataChangeLogic.afterEditDwbm_v(hTable, bTableArr, shareTable);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.DWBM)) {// 表头借款报销人单位编辑后事件
				DataChangeLogic.afterEditDwbm(hTable, bTableArr, shareTable);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.FYDWBM_V)) {// 表头费用承担单位编辑后事件
				DataChangeLogic.afterEditFYDWBM_V(hTable, bTableArr, shareTable);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.FYDWBM)) {// 表头费用承担单位编辑后事件不带_V
				DataChangeLogic.afterEditFYDWBM(hTable, bTableArr, shareTable);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.PK_PAYORG_V)) {// 表头支付单位编辑后事件
				DataChangeLogic.afterEditPK_PAYORG_V(hTable, bTableArr);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.PK_PAYORG)) {// 表头支付单位编辑后事件
				DataChangeLogic.afterEditPK_PAYORG(hTable, bTableArr);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.DEPTID_V)) {// 表头报销人部门编辑后事件
				DataChangeLogic.afterEditDeptid_v(hTable, bTableArr, shareTable);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.DEPTID)) {// 表头报销人部门编辑后事件
				DataChangeLogic.afterEditDeptid(hTable, bTableArr, shareTable);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.FYDEPTID_V)) {// 表头费用承担部门编辑后事件
				DataChangeLogic.afterEditFydeptid_v(hTable, bTableArr, shareTable);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.FYDEPTID)) {// 表头费用承担部门编辑后事件
				DataChangeLogic.afterEditFydeptid(hTable, bTableArr, shareTable);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.JKBXR)) {// 表头借款报销人
				DataChangeLogic.afterEditJKBXR(hTable, bTableArr, shareTable);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.RECEIVER)) {// 表头收款人
				DataChangeLogic.afterEditRECEIVER(hTable, bTableArr);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.SZXMID)) {// 表头收支项目
				DataChangeLogic.afterEditSZXM(hTable, bTableArr, shareTable);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.SKYHZH)) {// 表头个人银行账户
				DataChangeLogic.afterEditSKYHZH(hTable, bTableArr, shareTable);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.JOBID)) {// 表头项目
				DataChangeLogic.afterEditJOBID(hTable, bTableArr, shareTable);
			} else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.FREECUST)) {// 散户
				DataChangeLogic.afterEditFREECUST(hTable, bTableArr);
			}else if (ERMWebConst.ERM_HEAD_DT.equals(tableID) && field.equals(JKBXHeaderVO.CUSTACCOUNT)) {// 表头客商银行账户
				DataChangeLogic.afterEditHeadCUSTACCOUNT(hTable, bTableArr, shareTable);
			} else if (ERMWebConst.ERM_BX_BODY_CSHARE_DT.equals(tableID)
					&& field.equals(IBillFieldGet.ASSUME_ORG_SHARE)) {// 分摊页签费用承担单位
				DataChangeLogic.afterEditShareAssumeOrg(hTable, bTableArr, shareTable);
			} else if (bTableIdList.contains(tableID) && field.equals(IBillFieldGet.FREECUST_BUSITEM)) {// 表体散户
				DataChangeLogic.afterEditBodyFREECUST(editTable, editTable.getCurrentRow());
			}  else if (bTableIdList.contains(tableID) && field.equals(IBillFieldGet.RECEIVER_BUSITEM)) {// 表体收款人
				DataChangeLogic.afterEditReciver_busitem(editTable, editTable.getCurrentRow());
			} else if (bTableIdList.contains(tableID) && field.equals(IBillFieldGet.AMOUNT_BUSITEM)) {// 表体报销金额
				DataChangeLogic.afterEditBodyAMOUNT(hTable, editTable.getCurrentRow());
			} else if (bTableIdList.contains(tableID) && field.equals(IBillFieldGet.YBJE_BUSITEM)) {// 表体原币金额
				DataChangeLogic.afterEditBodyYBJE(hTable, editTable.getCurrentRow());
			} else if (bTableIdList.contains(tableID) && field.equals(IBillFieldGet.TAX_RATE_BUSITEM)) {// 表体税率
				DataChangeLogic.afterEditBodyTaxRate(hTable, editTable.getCurrentRow());
			} else if (bTableIdList.contains(tableID) && field.equals(IBillFieldGet.VAT_AMOUNT_BUSITEM)) {// 表体含税原币金额
				DataChangeLogic.afterEditBodyHSYBJE(hTable, editTable.getCurrentRow());
			} else if (bTableIdList.contains(tableID) && field.equals(IBillFieldGet.TNI_AMOUNT_BUSITEM)) {// 表体无税原币金额
				DataChangeLogic.afterEditBodyWSYBJE(hTable, editTable.getCurrentRow());
			} else if (bTableIdList.contains(tableID) && field.equals(IBillFieldGet.TAX_AMOUNT_BUSITEM)) {// 表体原币税额
				DataChangeLogic.afterEditBodyYBSE(hTable, editTable.getCurrentRow());
			} else if (bTableIdList.contains(tableID) && field.equals(IBillFieldGet.BZBM_BUSITEM)) {// 表体币种编码
				DataChangeLogic.afterEditBodyBZBM(editTable,hTable, editTable.getCurrentRow());
			} else if (bTableIdList.contains(tableID) && field.equals(IBillFieldGet.BBHL_BUSITEM)) {// 表体汇率
				DataChangeLogic.afterEditBodyBBHL(hTable, editTable.getCurrentRow());
			} else if (ERMWebConst.ERM_HEAD_DT.contains(tableID) && field.equals(JKBXHeaderVO.PAYTARGET)) {// 表头收款对象
				DataChangeLogic.afterEditHeadPAYTARGET(hTable, bTableArr, shareTable);
			} else if (ERMWebConst.ERM_HEAD_DT.contains(tableID) && field.equals(JKBXHeaderVO.HBBM)) {// 表头供应商
				DataChangeLogic.afterEditHeadHBBM(hTable, bTableArr, shareTable);
				DataChangeLogic.afterEditCustSupplier(field,tableID,hTable,bTableArr);
			} else if (ERMWebConst.ERM_HEAD_DT.contains(tableID) && field.equals(JKBXHeaderVO.CUSTOMER)) {// 表头客户
				DataChangeLogic.afterEditHeadCUSTOMER(hTable, bTableArr, shareTable);	
				DataChangeLogic.afterEditCustSupplier(field,tableID,hTable,bTableArr);
			} else if (bTableIdList.contains(tableID) && field.equals(IBillFieldGet.PAYTARGET_BUSITEM)) {// 表体收款对象
				DataChangeLogic.afterEditBodyPAYTARGET(editTable, editTable.getCurrentRow());
			} else if (bTableIdList.contains(tableID) && field.equals(IBillFieldGet.HBBM_BUSITEM)) {// 表体供应商
				DataChangeLogic.afterEditBodyHBBM(editTable, editTable.getCurrentRow());
				DataChangeLogic.afterEditCustSupplier(field,tableID,hTable,bTableArr);
			} else if (bTableIdList.contains(tableID) && field.equals(IBillFieldGet.CUSTOMER_BUSITEM)) {// 表体客户
				DataChangeLogic.afterEditBodyCUSTOMER(editTable, editTable.getCurrentRow());
				DataChangeLogic.afterEditCustSupplier(field,tableID,hTable,bTableArr);
			} else if (field.equals(IBillFieldGet.JKBXR_BUSITEM)) {
				BodyDataChangeLogic.afterEditJKBXR(editTable, editTable.getCurrentRow());
			} else if (field.equals(IBillFieldGet.DWBM)) {
				BodyDataChangeLogic.afterEditDWBM(hTable, editTable, editTable.getCurrentRow());
			} else if (ERMWebConst.ERM_BX_BODY_CSHARE_DT.equals(tableID)
					&& field.equals(IBillFieldGet.ASSUME_ORG_SHARE)) {// 分摊页签表体承担单位编辑后事件
				DataChangeLogic.afterEditSHARE_ASSUME_ORG(shareTable);
			} else if (ERMWebConst.ERM_BX_BODY_CSHARE_DT.equals(tableID)
					&& field.equals(IBillFieldGet.ASSUME_AMOUNT_SHARE)) {// 分摊页签表体承担金额编辑后事件
				DataChangeLogic.afterEditSHARE_ASSUME_AMOUNT(hTable, shareTable.getCurrentRow());
			}else if ((ERMWebConst.ERM_HEAD_DT.equals(tableID) && (field.equals(IBillFieldGet.HBBM)
					||field.equals(IBillFieldGet.CUSTOMER)))
					 || (bTableIdList.contains(tableID) && (field.equals(IBillFieldGet.HBBM)
								||field.equals(IBillFieldGet.CUSTOMER)))) {// 表头/表体供应商客户编辑后事件
				DataChangeLogic.afterEditCustSupplier(field,tableID,hTable,bTableArr);
			}else if (bTableIdList.contains(tableID) && field.contains("defitem")) {// XBX
				DataChangeLogic.afterEditBodyDefitemJE(hTable, editTable.getCurrentRow());
			}
			
			/***处理配置了交叉校验规则的参照start****/
			List<DataTable> dataTableList = new ArrayList<DataTable>();
			dataTableList.add(hTable);
			if(bTableArr!=null&&bTableArr.length>0){
				for(DataTable dataTable :bTableArr){
					dataTableList.add(dataTable);
				}
			}
			if(shareTable != null){
				dataTableList.add(shareTable);
			}
			String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
			new CrossRulePubUtil().reSetOtherRefPropertyDatas(editTable,field,newValue,pk_org,dataTableList.toArray(new DataTable[0]));
			/***处理配置了交叉校验规则的参照end****/
			
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
	}

	
	/**
	 * 重新计算表体行原币本币金额
	 * 将amount赋值给ybje，然后重新计算
	 * @param dtEvent
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void calBbOnRow() throws Exception {
		
		String tableID = IWebViewContext.getEventParameter("tableID");// 页签
		if (ERMDataTableUtil.getBusitemDataTableIdList().contains(tableID)) {
			DataTable hTable = FIDataTableUtil.getDataTable(ERMWebConst.ERM_HEAD_DT);
			DataTable bTable = FIDataTableUtil.getDataTable(tableID);
//			Row currentRow = bTable.getCurrentRow();
			
//			String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
//			String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
//			UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJRQ);// 单据日期
//			
//			FIDataTableUtil.setBodyValueByRow(currentRow, IBillFieldGet.YBJE_BUSITEM, ERMDataTableUtil.getBodyValueByRow(currentRow, IBillFieldGet.AMOUNT_BUSITEM));
//			
//			DataChangeLogic.calBbOnRow(pk_group,pk_org,djrq,currentRow);
			
			String djdl = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJDL));
			if("bx".equals(djdl)){
				DataChangeLogic.afterEditBodyHSYBJE(hTable, bTable.getCurrentRow());
			}else if("jk".equals(djdl)){
				DataChangeLogic.afterEditBodyAMOUNT(hTable, bTable.getCurrentRow());
			}
			
		}
		
	}
}
