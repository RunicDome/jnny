package nc.impl.arap.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import nc.bs.arap.bill.ArapBillPubUtil;
import nc.bs.arap.util.ArapBillVOUtils;
import nc.bs.arap.util.ArapFlowUtil;
import nc.bs.arap.util.CheckException;
import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.bs.pub.pf.PfMessageUtil;
import nc.fipub.framework.base.FIArrayUtil;
import nc.fipub.framework.base.FIStringUtil;
import nc.fipub.framework.db.FISqlBuilder;
import nc.imag.pub.util.ImageServiceUtil;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.itf.arap.web.IArapWebRequestService;
import nc.itf.arap.web.verify.IFIWebVerifyRelation;
import nc.itf.cmp.IApplyService;
import nc.itf.iv.invoice.IInvoiceQueryService;
import nc.itf.iv.web.IInvoiceWebPubService;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.jdbc.framework.SQLParameter;
import nc.nweb.arap.pub.ArapWebConst;
import nc.nweb.arap.util.EncodeForSQLUtil;
import nc.nweb.arap.util.WorkFlowCheck;
import nc.pubitf.arap.pay.IArapPayBillPubQueryService;
import nc.ssc.fiweb.pub.RequestParam;
import nc.ui.pub.bill.IBillItem;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.basebill.BaseItemVO;
import nc.vo.arap.billstatus.ARAPBillStatus;
import nc.vo.arap.event.IArapBSEventType;
import nc.vo.arap.exception.ArapTbbException;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.arap.payable.PayableBillItemVO;
import nc.vo.arap.payable.PayableBillVO;
import nc.vo.arap.pub.ArapConstant;
import nc.vo.arap.pub.BillEnumCollection;
import nc.vo.cmp.apply.ApplyVO;
import nc.vo.fipub.billcode.FinanceBillCodeUtils;
import nc.vo.fiweb.verify.VerifyRelationVO;
import nc.vo.iv.invoice.InvoiceAssociateVO;
import nc.vo.iv.invoice.InvoiceHeadVO;
import nc.vo.pf.change.PfUtilBaseTools;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.bill.BillOperaterEnvVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.AssignableInfo;
import nc.vo.pub.pf.workflow.IPFActionName;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.util.NCPfServiceUtils;
import nc.vo.sm.UserVO;
import nc.vo.uap.pf.OrganizeUnit;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.web.arap.bill.pub.IArapBillPubQueryService;
import nc.web.arap.bill.pub.WebBillTypeFactory;
import nc.web.arap.environment.EnvironmentInit;
import nc.web.arap.utils.ArapBillUtil;
import nc.web.arap.utils.ArapPubProxy;
import nc.web.arap.utils.ArapTemplateQueryUtil;
import nc.web.datatrans.itf.ITranslateDataService;
import nc.web.datatrans.itf.ITranslateVODataService;
import nc.web.pub.utils.WebPubUtils;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.OracleCodec;

import uap.iweb.pub.bill.BillItem;
import uap.iweb.pub.bill.util.BillTemplateWebUtil;
import uap.iweb.ref.pub.BillEnumCollection.ApproveStatus;
import uap.web.util.Coder;

/**
 * 应收应付web端实现类
 * 
 * @author liyjf
 * 
 */
@SuppressWarnings("unchecked")
public class ArapWebRequestServiceImpl implements IArapWebRequestService {

	@SuppressWarnings("deprecation")
	public String sendBillSave(RequestParam request) throws Exception {
		boolean compression = "true"
				.equals(request.getParameter("compression"));
		String compressType = request.getParameter("compressType");
		String jsnObj = request.getParameter("bill");
		if (compression) {
			jsnObj = Coder.decode(jsnObj, compressType);
		}

		String isAlarmPassed = request.getParameter("isAlarmPassed");
		JSONObject json = new JSONObject();
		String tradetype = request.getParameter("tradetype");
		String state = request.getParameter("state");
		String pk_billtemplet = request.getParameter("pk_billtemplet");
		String billtypeCode = WebBillTypeFactory.getWebBillType(tradetype)
				.getBilltypeCode();
		List<BillItem> billItems = uap.iweb.pub.bill.util.BillTemplateWebUtil
				.getBillItems(null, pk_billtemplet);
		String msg_pk_bill = request.getParameter("msg_pk_bill");
		String accessorybillid = request.getParameter("accessorybillid");
		// 发票信息解析
		String ajsnObj = request.getParameter("associatevo");
		String ivjsnObj = request.getParameter("invoicevo");
		String deletejsnObj = request.getParameter("deleteidlist");
		String fromssc = request.getParameter("fromssc");//单据来源，如果是ssc则需要更新共享单据列表的金额
		JSONArray associateArray = new JSONArray();

		// 应付关联发票保存与redis缓存不再同一事物中，所以保存最终完成前redis暂时不更新，保存成功后再处理 20160629 胡斌
		if ("F1".equals(billtypeCode)) {
			InvocationInfoProxy.getInstance().setProperty("reids_event",
					"false");
		}

		if (billtypeCode.equals("F1")
				&& (FIStringUtil.isNotEmpty(ajsnObj) || FIStringUtil
						.isNotEmpty(deletejsnObj))) {
			// 启用压缩的情况
			if (compression) {
				if (FIStringUtil.isNotEmpty(ajsnObj)) {
					ajsnObj = Coder.decode(ajsnObj, compressType);
					associateArray = new JSONArray(ajsnObj);
				}
				if (FIStringUtil.isNotEmpty(ivjsnObj)) {
					ivjsnObj = Coder.decode(ivjsnObj, compressType);
				}
				if (FIStringUtil.isNotEmpty(deletejsnObj)) {
					deletejsnObj = Coder.decode(deletejsnObj, compressType);
				}
			} else {
				// 不启用压缩，就不用重新编码了
				if (FIStringUtil.isNotEmpty(ajsnObj)) {
					associateArray = new JSONArray(ajsnObj);
				}
			}
		}
		try {

			Object resultObject = NCLocator
					.getInstance()
					.lookup(ITranslateDataService.class)
					.translateJsonToAggvo(
							WebBillTypeFactory.getWebBillType(tradetype)
									.getAggvoClass(), jsnObj);
			AggregatedValueObject aggvo = (AggregatedValueObject) (resultObject);
			if (aggvo == null || aggvo.getChildrenVO() == null
					|| aggvo.getChildrenVO().length == 0) {
				throw new Exception("单据表体不能为空");
			}
			
			//校验收付款协议和核销关联关系，如果当前单据已核销关联，则不可关联收付款协议
			checkPaytermAndVerifyRelation(aggvo);
			
			// 初始化用户名密码
			String userid = InvocationInfoProxy.getInstance().getUserId();
			EnvironmentInit.initEvn(userid);
			Object returnvos = null;
			BaseAggVO[] savebills = new BaseAggVO[] { (BaseAggVO) aggvo };
			if (isAlarmPassed != null) {
				for (BaseAggVO billvo : savebills) {
					billvo.setAlarmPassed(isAlarmPassed.equals("true") ? true
							: false);
				}
			}
			// 协同生成的应收单默认制单人是NC系统用户，修改保存时将制单人修改为当前登录用户。
			for (BaseAggVO billvo : savebills) {
				CircularlyAccessibleValueObject parentVO = billvo.getParentVO();
				String billmaker = FIStringUtil.coverToString(parentVO
						.getAttributeValue(BaseBillVO.BILLMAKER));
				String nc_user = "NC_USER0000000000000";
				if (billmaker != null && nc_user.equals(billmaker)) {
					parentVO.setAttributeValue(BaseBillVO.BILLMAKER, userid);
				}
			}
			try {
				// 处理sql注入
				for (AggregatedValueObject bill : savebills) {
					EncodeForSQLUtil.encode(new SuperVO[] { (SuperVO) bill
							.getParentVO() });
					EncodeForSQLUtil.encode((SuperVO[]) bill.getChildrenVO());
				}
			} catch (Exception e) {
				Log.getInstance(this.getClass()).error(
						"处理sql注入异常：" + e.getMessage(), e);
			}
			for (AggregatedValueObject bill : savebills) {
				String pk_billtype = FIStringUtil.coverToString(bill
						.getParentVO()
						.getAttributeValue(BaseBillVO.PK_BILLTYPE));
                //预收款预付款单据不校验老版本的合同号是否一致，因为拉单的上游单据本身表头没有合同号
				String pk_tradetype = FIStringUtil.coverToString(bill
						.getParentVO()
						.getAttributeValue(BaseBillVO.PK_TRADETYPE));
				if (!pk_billtype.equals("F1") || (pk_tradetype!=null &&  (pk_tradetype.endsWith("YFK") || pk_tradetype.endsWith("YSK") ))) {
					continue;
				}
				String contractnoValue = (String) savebills[0].getParentVO()
						.getAttributeValue("contractno");
				CircularlyAccessibleValueObject[] childrenVO = bill
						.getChildrenVO();
				for (CircularlyAccessibleValueObject child : childrenVO) {
					String childcontractnoValue = (String) child
							.getAttributeValue(IBillFieldGet.CONTRACTNO);
					if (!FIStringUtil.isEmpty(contractnoValue)) {
						if (FIStringUtil.isEmpty(childcontractnoValue)
								|| !contractnoValue
										.equals(childcontractnoValue)) {
							throw new Exception("表体行合同号与表头合同号不一致，请修改！");
						}
					} else {
						if (!FIStringUtil.isEmpty(childcontractnoValue)) {
							throw new Exception("表体行合同号与表头合同号不一致，请修改！");
						}
					}
					UFDouble occupationmny = (UFDouble) child.getAttributeValue("occupationmny");
					if(occupationmny==null||occupationmny.compareTo(UFDouble.ZERO_DBL)==0){
						child.setAttributeValue("occupationmny", (UFDouble)child.getAttributeValue("money_bal"));
					}
				}
				//@sscct@合同模块补丁合并增加--20171013--begin
				String pk_contractno = (String) savebills[0].getParentVO()
						.getAttributeValue("pk_contractno");
				//获取发票信息的合同号
				JSONArray invoiceArray = new JSONArray();
				if (ivjsnObj != null) {
					invoiceArray = new JSONArray(ivjsnObj);
				}
				if(invoiceArray!=null && invoiceArray.length()>0)
				{
					for(int i=0;i<invoiceArray.length();i++)
					if(invoiceArray.getJSONObject(i)!=null && invoiceArray.getJSONObject(i).getJSONObject("head")!=null )
					{
						String pk_contractno_invoice =null;
						if(invoiceArray.getJSONObject(i).getJSONObject("head").has("pk_contractno"))
					    pk_contractno_invoice = (String) invoiceArray.getJSONObject(i).getJSONObject("head").get("pk_contractno");
					    String invoicecode = (String) invoiceArray.getJSONObject(i).getJSONObject("head").get("invoicecode");
						//校验
					    if(!FIStringUtil.isEmpty(pk_contractno))
					    {
							if(!pk_contractno.equals(pk_contractno_invoice)){
								
								throw new Exception("发票号：'"+invoicecode+"'关联行合同与表头合同不一致，请修改！");
							}
					    }else if(!FIStringUtil.isEmpty(pk_contractno_invoice)){
					    	throw new Exception("发票号：'"+invoicecode+"'关联行合同与表头合同不一致，请修改！");
					    }
					}
				}
				
				if(associateArray!=null && associateArray.length()>0)
				{
					List<String> pk_invoice_list = new ArrayList<String>();
					  for(int i=0;i<associateArray.length();i++)
					  {
						  JSONObject assocJSONObject = associateArray.getJSONObject(i);
						  String pk_invoice = (String) assocJSONObject.get("pk_invoice");
						  if(pk_invoice!=null && pk_invoice.length()>0)
						  pk_invoice_list.add(pk_invoice);
					  }
					//查询发票信息
					InvoiceHeadVO[] invoiceHeadVOS = NCLocator.getInstance().lookup(IInvoiceQueryService.class).queryInvoiceByPKs(pk_invoice_list.toArray(new String[pk_invoice_list.size()]));
			        if(invoiceHeadVOS!=null && invoiceHeadVOS.length>0)
					for(InvoiceHeadVO invoiceHeadVO :invoiceHeadVOS){
						String pk_contractno_in =  invoiceHeadVO.getPk_contractno();
						String invoicecode =  invoiceHeadVO.getInvoicecode();
						//校验
					    if(!FIStringUtil.isEmpty(pk_contractno))
					    {
							if(!pk_contractno.equals(pk_contractno_in)){
								
								throw new Exception("发票号：'"+invoicecode+"'关联行合同与表头合同不一致，请修改！");
							}
					    }else if(!FIStringUtil.isEmpty(pk_contractno_in)){
					    	throw new Exception("发票号：'"+invoicecode+"'关联行合同与表头合同不一致，请修改！");
					    }
					}
				}
				//@sscct@合同模块补丁合并增加--20171013--end
				
			}
			CircularlyAccessibleValueObject[] bodys = savebills[0].getChildrenVO();
			Set<String> infoset = new HashSet<String>();
			for(int i=0;i<(bodys==null?0:bodys.length);i++){
				UFDouble money_cr = (UFDouble) bodys[i].getAttributeValue("money_cr")==null?UFDouble.ZERO_DBL:(UFDouble) bodys[i].getAttributeValue("money_cr");
				UFDouble money_de = (UFDouble) bodys[i].getAttributeValue("money_de")==null?UFDouble.ZERO_DBL:(UFDouble) bodys[i].getAttributeValue("money_de");
				String pk_tradetype = (String)bodys[i].getAttributeValue("pk_tradetype");
				String top_tradetype = (String)bodys[i].getAttributeValue("top_tradetype");
				String pk_billtype = (String)bodys[i].getAttributeValue("pk_billtype");
				String top_billtype = (String)bodys[i].getAttributeValue("top_billtype");
				if(money_cr.add(money_de).compareTo(UFDouble.ZERO_DBL)>0&&pk_tradetype.equals(top_tradetype)&&pk_billtype.equals(top_billtype)){
					infoset.add("+");
				}
				else if(money_cr.add(money_de).compareTo(UFDouble.ZERO_DBL)<0&&pk_tradetype.equals(top_tradetype)&&pk_billtype.equals(top_billtype)){
					infoset.add("-");
				}
			}
			if(infoset.size()>1){
				throw new BusinessException("红冲单据表体行的金额方向必须相同！");
			}
			if ("edit".equals(state)) {
				BaseAggVO billvo = (BaseAggVO) savebills[0];
				checkMustInput(savebills,tradetype);
				// 修改保存
				ArapBillUtil.setClientDateAndUser(savebills, VOStatus.UPDATED);
				for (AggregatedValueObject bill : savebills) {
				
					CircularlyAccessibleValueObject[] childrenVO = bill
							.getChildrenVO();
					String primaryKey = bill.getParentVO().getPrimaryKey();
					for (CircularlyAccessibleValueObject child : childrenVO) {
						child.setAttributeValue(
								((SuperVO) child).getParentPKFieldName(),
								primaryKey);
						if (child.getPrimaryKey() == null
								|| child.getPrimaryKey().length() == 0) {
							child.setStatus(VOStatus.NEW);
						} else {
							child.setStatus(VOStatus.UPDATED);
						}
					}

				}
				if (billvo.getHeadVO().getBillstatus().intValue() == ARAPBillStatus.TEMPSAVE.VALUE
						.intValue()) {
					billvo.getHeadVO().setBillstatus(ARAPBillStatus.SAVE.VALUE);
				}

				billvo.getParentVO().setStatus(VOStatus.UPDATED);
				returnvos = ArapPubProxy.getLoginQueryService().processAction(
						WebPubUtils.SAVEBASE, tradetype, new WorkflownoteVO(),
						billvo, null, null);
				if("true".equals(fromssc)){
					String sql = "update SSC_SSCTASK set def2='"+billvo.getParentVO().getAttributeValue("money")+"' where BILLCODE='"+billvo.getParentVO().getAttributeValue("billno")+"'";
					try {
						BaseDAO dao = new BaseDAO();
						dao.executeUpdate(sql);
					} catch (Exception e) {
						Logger.error(e.getMessage(), e);
						throw new BusinessException(e.getMessage());
					}
				}
			} else {
				// 新增保存
				checkMustInput(savebills,tradetype);
				
				ArapBillUtil.setClientDateAndUser(savebills, VOStatus.NEW);
				String pk_org = savebills[0].getHeadVO().getPk_org();

				String primaryKey = savebills[0].getHeadVO().getPrimaryKey();
				if (primaryKey == null) {
					primaryKey = "";
				}
				if(!StringUtils.isEmpty(accessorybillid)){
					savebills[0].getParentVO().setPrimaryKey(accessorybillid);
				}else if (!StringUtils.isEmpty(msg_pk_bill)
						&& StringUtils.isEmpty(primaryKey)) {
					savebills[0].getParentVO().setPrimaryKey(msg_pk_bill);
				}

				ArapBillVOUtils.dealVoScaleAndHtb(savebills[0]);

				returnvos = ArapPubProxy.getLoginQueryService().processAction(
						WebPubUtils.SAVEBASE, tradetype, new WorkflownoteVO(),
						savebills[0], null, null);

				if (billtypeCode.equals("F1")) {
					int scantype = ImageServiceUtil.getImageScanType(pk_org,
							tradetype);
					BaseAggVO billvo = null;
					if (returnvos instanceof AggregatedValueObject[]) {
						AggregatedValueObject[] returnObjects = (AggregatedValueObject[]) returnvos;
						if (returnObjects.length > 0
								&& returnObjects[0] != null) {
							billvo = (BaseAggVO) returnObjects[0];
						}
					} else if (returnvos instanceof Object[]) {
						Object[] returnObjects = (Object[]) returnvos;
						if (returnObjects.length > 0
								&& returnObjects[0] != null
								&& (returnObjects[0] instanceof BaseAggVO[])) {
							Object returnObject = returnObjects[0];
							billvo = ((BaseAggVO[]) returnObject)[0];
						}
					}
					if (returnvos instanceof AggregatedValueObject) {
						billvo = (BaseAggVO) returnvos;
					}
					if (billvo == null) {
						ExceptionUtils.wrappBusinessException("未找到应付单信息!");
					}
					if (scantype == 1 || scantype == 2) {// 必须扫描的时候才压任务
						// NCLocator.getInstance().lookup(IImageService.class).addScanTask(billvo,
						// scantype, billtypeCode);
						ImageServiceUtil.addScanTask(billvo, scantype,
								billtypeCode, pk_org);
						// NCLocator.getInstance().lookup(IImageWSService.class)
						// .updateImageStateVO(billvo.getPrimaryKey(),
						// "nullflow");
					}
				}
			}

			BaseAggVO billvo = null;
			if (returnvos instanceof AggregatedValueObject[]) {
				AggregatedValueObject[] returnObjects = (AggregatedValueObject[]) returnvos;
				if (returnObjects.length > 0 && returnObjects[0] != null) {
					billvo = (BaseAggVO) returnObjects[0];
				}
			} else if (returnvos instanceof Object[]) {
				Object[] returnObjects = (Object[]) returnvos;
				if (returnObjects.length > 0 && returnObjects[0] != null
						&& (returnObjects[0] instanceof BaseAggVO[])) {
					Object returnObject = returnObjects[0];
					billvo = ((BaseAggVO[]) returnObject)[0];
				}
			}
			if (returnvos instanceof AggregatedValueObject) {
				billvo = (BaseAggVO) returnvos;
			}
			if (billvo == null) {
				ExceptionUtils.wrappBusinessException("未找到应付单信息!");
			}

			// 发票关联保存
			JSONArray deleteidArray = new JSONArray();
			JSONArray associatevos = null;
			if (deletejsnObj != null) {
				deleteidArray = new JSONArray(deletejsnObj);
			}
			if (associateArray != null && associateArray.length() > 0
					|| (deleteidArray != null && deleteidArray.length() > 0)) {
				JSONArray invoiceArray = new JSONArray();
				if (ivjsnObj != null) {
					invoiceArray = new JSONArray(ivjsnObj);
				}

				String pk_billtemplet2 = request
						.getParameter("pk_billtemplet2");

				// 关联发票
				associatevos = NCLocator
						.getInstance()
						.lookup(IInvoiceWebPubService.class)
						.associateForBill(associateArray, invoiceArray,
								deleteidArray, billvo, pk_billtemplet2);

			}
			
			//wangyl7 2018年1月26日15:09:37 上游生效状态校验，暂时portal只校验付款单 begin
			if ("F1".equals(billtypeCode)) {
				String top_billtype = null;
				String top_billid = null;
				PayableBillItemVO[] childs = (PayableBillItemVO[])billvo.getChildrenVO();
				for(PayableBillItemVO itemvo:childs){
					if(itemvo.getTop_billtype()!= null){
						top_billtype = itemvo.getTop_billtype();
						top_billid = itemvo.getTop_billid();
					}
				}
				if(top_billtype != null && "F3".equals(top_billtype)){
					AggPayBillVO[] payaggvos = (AggPayBillVO[]) NCLocator.getInstance().lookup(
							IArapPayBillPubQueryService.class).findBillByPrimaryKey(new String[]{top_billid});
					if(payaggvos!=null && payaggvos.length > 0){
						PayBillVO hvo = (PayBillVO)payaggvos[0].getParentVO();
						if(hvo.getEffectstatus() != 10){
							ExceptionUtils
							.wrappBusinessException("上游付款单据"+hvo.getBillno()+"未生效，无法保存");
						}
					}
				}
			}
			//end
			
			// 检查应付单供应商
			FISqlBuilder sql = new FISqlBuilder();
			sql.select();
			sql.append("pi." + PayableBillItemVO.SUPPLIER);
			sql.from(PayableBillItemVO.getDefaultTable() + " pi ");
			sql.innerjoin(InvoiceAssociateVO.getDefaultTableName() + " ia ");
			sql.on("pi", PayableBillItemVO.PK_PAYABLEBILL, "ia",
					InvoiceAssociateVO.BILLID);
			sql.and();
			sql.append("pi." + PayableBillItemVO.PK_PAYABLEBILL, billvo
					.getHeadVO().getPrimaryKey());
			sql.innerjoin(InvoiceHeadVO.getDefaultTableName() + " iv ");
			sql.on("iv", InvoiceHeadVO.PK_INVOICE, "ia",
					InvoiceAssociateVO.PK_INVOICE);
			sql.where();
			sql.append("pi." + PayableBillItemVO.SUPPLIER + " <> iv."
					+ InvoiceHeadVO.SUPPLIERID);
			sql.and();
			sql.appendDr("ia");
			sql.and();
			sql.appendDr("pi");
			DataAccessUtils util = new DataAccessUtils();
			IRowSet rows = util.query(sql.toString());

			if (rows.next()) {
				ExceptionUtils
						.wrappBusinessException("应付单表体与所关联发票的供应商不一致，不能保存，请修改应付单或者关联发票信息！");
			}

			returnvos = WebBillTypeFactory
					.getWebBillType(tradetype)
					.getIArapBillPubQueryService()
					.findBillByPrimaryKey(
							new String[] { savebills[0].getHeadVO()
									.getPrimaryKey() });

			ITranslateVODataService service = NCLocator.getInstance().lookup(
					ITranslateVODataService.class);
			if (returnvos instanceof AggregatedValueObject[]) {
				AggregatedValueObject[] returnObjects = (AggregatedValueObject[]) returnvos;
				if (returnObjects.length > 0 && returnObjects[0] != null) {
					AggregatedValueObject returnObject = returnObjects[0];
					ArapBillVOUtils.dealVoScaleAndHtb((BaseAggVO) returnObject);

					if (billtypeCode.equals("F1")) {
						PayableBillVO head = (PayableBillVO) returnObject
								.getParentVO();
						if (head.getLinkmoney() != null) {
							head.setLinkmoney(head.getLinkmoney().setScale(2,
									UFDouble.ROUND_HALF_UP));
						}
					}

					if (billItems == null) {
						json = service.transAggvoToJSON(returnObject);
					} else {
						json = service
								.transAggvoToJSON(returnObject, billItems);
					}
				}
			} else if (returnvos instanceof Object[]) {
				Object[] returnObjects = (Object[]) returnvos;
				if (returnObjects.length > 0 && returnObjects[0] != null
						&& (returnObjects[0] instanceof BaseAggVO[])) {
					Object returnObject = returnObjects[0];
					BaseAggVO[] object = (BaseAggVO[]) returnObject;
					if (object != null && object.length > 0) {
						ArapBillVOUtils.dealVoScaleAndHtb(object[0]);
						if (billItems == null) {
							json = service.transAggvoToJSON(object[0]);
						} else {
							json = service.transAggvoToJSON(object[0],
									billItems);
						}
					}
				}
			}
			InvocationInfoProxy.getInstance()
					.setProperty("reids_event", "true");
			EventDispatcher.fireEvent(new BusinessEvent(
					ArapConstant.ARAP_MDID_PAYABLEBILL,
					IArapBSEventType.TYPE_APIVADD_AFTER, returnvos));
			Logger.error("应付单保存公布业务事件 201615 通知redis更新缓存");
			if (associatevos != null) {
				json.put("associatevos", associatevos);
			}
			json.put("success", "true");
			return json.toString();

		} catch (Exception e) {
			if (e.getCause() != null
					&& e.getCause() instanceof ArapTbbException) {
				json.put("type", "warning");
				Logger.error(e.getMessage(), e);
				json.put("success", "false");
				json.put("message", e.getMessage());
			} else {
				ExceptionUtils.wrappException(e);
			}
		}
		return json.toString();
	}

	/**
	 * @param aggvo
	 * @throws BusinessException
	 * @throws Exception
	 */
	private void checkPaytermAndVerifyRelation(AggregatedValueObject aggvo)
			throws BusinessException, Exception {
		if(null != aggvo.getParentVO()){
			String billprimarykey = aggvo.getParentVO().getPrimaryKey();
			if( null != billprimarykey){
				boolean hasPayterm = false;
				for (CircularlyAccessibleValueObject childvo : aggvo.getChildrenVO()) {
					String pk_payterm = (String)childvo.getAttributeValue("pk_payterm");
					if(null != pk_payterm && pk_payterm.trim().length()>0){
						hasPayterm = true;
						break;
					}
				}
				if(hasPayterm){
					IFIWebVerifyRelation fIWebVerifyRelation = NCLocator.getInstance().lookup(IFIWebVerifyRelation.class);
					List<VerifyRelationVO> list = fIWebVerifyRelation.getVerifyRelationsByTBillId(billprimarykey);
					if(null!=list && list.size()>0){
						throw new Exception("保存失败，该单据已建立核销关联关系，表体不可关联收付款协议。");
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public String sendredBillSave(RequestParam request) throws Exception {
		boolean compression = "true"
				.equals(request.getParameter("compression"));
		String compressType = request.getParameter("compressType");
		String jsnObj = request.getParameter("bill");
		if (compression) {
			jsnObj = Coder.decode(jsnObj, compressType);
		}

		String isAlarmPassed = request.getParameter("isAlarmPassed");
		JSONObject json = new JSONObject();
		String tradetype = request.getParameter("tradetype");
		String state = request.getParameter("state");
		String pk_billtemplet = request.getParameter("pk_billtemplet");
		String billtypeCode = WebBillTypeFactory.getWebBillType(tradetype)
				.getBilltypeCode();
		List<BillItem> billItems = uap.iweb.pub.bill.util.BillTemplateWebUtil
				.getBillItems(null, pk_billtemplet);
		String msg_pk_bill = request.getParameter("msg_pk_bill");
		String accessorybillid = request.getParameter("accessorybillid");
		// 发票信息解析
		String ajsnObj = request.getParameter("associatevo");
		String ivjsnObj = request.getParameter("invoicevo");
		String deletejsnObj = request.getParameter("deleteidlist");
		JSONArray associateArray = new JSONArray();

		// 应付关联发票保存与redis缓存不再同一事物中，所以保存最终完成前redis暂时不更新，保存成功后再处理 20160629 胡斌
		if ("F1".equals(billtypeCode)) {
			InvocationInfoProxy.getInstance().setProperty("reids_event",
					"false");
		}

		if (billtypeCode.equals("F1")
				&& (FIStringUtil.isNotEmpty(ajsnObj) || FIStringUtil
						.isNotEmpty(deletejsnObj))) {
			// 启用压缩的情况
			if (compression) {
				if (FIStringUtil.isNotEmpty(ajsnObj)) {
					ajsnObj = Coder.decode(ajsnObj, compressType);
					associateArray = new JSONArray(ajsnObj);
				}
				if (FIStringUtil.isNotEmpty(ivjsnObj)) {
					ivjsnObj = Coder.decode(ivjsnObj, compressType);
				}
				if (FIStringUtil.isNotEmpty(deletejsnObj)) {
					deletejsnObj = Coder.decode(deletejsnObj, compressType);
				}
			} else {
				// 不启用压缩，就不用重新编码了
				if (FIStringUtil.isNotEmpty(ajsnObj)) {
					associateArray = new JSONArray(ajsnObj);
				}
			}
		}
		try {

			Object resultObject = NCLocator
					.getInstance()
					.lookup(ITranslateDataService.class)
					.translateJsonToAggvo(
							WebBillTypeFactory.getWebBillType(tradetype)
									.getAggvoClass(), jsnObj);
			AggregatedValueObject aggvo = (AggregatedValueObject) (resultObject);
			if (aggvo == null || aggvo.getChildrenVO() == null
					|| aggvo.getChildrenVO().length == 0) {
				throw new Exception("单据表体不能为空");
			}
			// 初始化用户名密码
			String userid = InvocationInfoProxy.getInstance().getUserId();
			EnvironmentInit.initEvn(userid);
			Object returnvos = null;
			BaseAggVO[] savebills = new BaseAggVO[] { (BaseAggVO) aggvo };
			if (isAlarmPassed != null) {
				for (BaseAggVO billvo : savebills) {
					billvo.setAlarmPassed(isAlarmPassed.equals("true") ? true
							: false);
				}
			}
			// 协同生成的应收单默认制单人是NC系统用户，修改保存时将制单人修改为当前登录用户。
			for (BaseAggVO billvo : savebills) {
				CircularlyAccessibleValueObject parentVO = billvo.getParentVO();
				String billmaker = FIStringUtil.coverToString(parentVO
						.getAttributeValue(BaseBillVO.BILLMAKER));
				String nc_user = "NC_USER0000000000000";
				if (billmaker != null && nc_user.equals(billmaker)) {
					parentVO.setAttributeValue(BaseBillVO.BILLMAKER, userid);
				}
			}
			try {
				// 处理sql注入
				for (AggregatedValueObject bill : savebills) {
					EncodeForSQLUtil.encode(new SuperVO[] { (SuperVO) bill
							.getParentVO() });
					EncodeForSQLUtil.encode((SuperVO[]) bill.getChildrenVO());
				}
			} catch (Exception e) {
				Log.getInstance(this.getClass()).error(
						"处理sql注入异常：" + e.getMessage(), e);
			}
			for (AggregatedValueObject bill : savebills) {
				String pk_billtype = FIStringUtil.coverToString(bill
						.getParentVO()
						.getAttributeValue(BaseBillVO.PK_BILLTYPE));
                //预收款预付款单据不校验老版本的合同号是否一致，因为拉单的上游单据本身表头没有合同号
				String pk_tradetype = FIStringUtil.coverToString(bill
						.getParentVO()
						.getAttributeValue(BaseBillVO.PK_TRADETYPE));
				if (!pk_billtype.equals("F1") || (pk_tradetype!=null &&  (pk_tradetype.endsWith("YFK") || pk_tradetype.endsWith("YSK") ))) {
					continue;
				}
				String contractnoValue = (String) savebills[0].getParentVO()
						.getAttributeValue("contractno");
				CircularlyAccessibleValueObject[] childrenVO = bill
						.getChildrenVO();
				for (CircularlyAccessibleValueObject child : childrenVO) {
					String childcontractnoValue = (String) child
							.getAttributeValue(IBillFieldGet.CONTRACTNO);
					if (!FIStringUtil.isEmpty(contractnoValue)) {
						if (FIStringUtil.isEmpty(childcontractnoValue)
								|| !contractnoValue
										.equals(childcontractnoValue)) {
							throw new Exception("表体行合同号与表头合同号不一致，请修改！");
						}
					} else {
						if (!FIStringUtil.isEmpty(childcontractnoValue)) {
							throw new Exception("表体行合同号与表头合同号不一致，请修改！");
						}
					}

				}
			}
			CircularlyAccessibleValueObject[] bodys = savebills[0].getChildrenVO();
			Set<String> infoset = new HashSet<String>();
			for(int i=0;i<(bodys==null?0:bodys.length);i++){
				UFDouble money_cr = (UFDouble) bodys[i].getAttributeValue("money_cr")==null?UFDouble.ZERO_DBL:(UFDouble) bodys[i].getAttributeValue("money_cr");
				UFDouble money_de = (UFDouble) bodys[i].getAttributeValue("money_de")==null?UFDouble.ZERO_DBL:(UFDouble) bodys[i].getAttributeValue("money_de");
				if(money_cr.add(money_de).compareTo(UFDouble.ZERO_DBL)>0){
					infoset.add("+");
				}
				else if(money_cr.add(money_de).compareTo(UFDouble.ZERO_DBL)<0){
					infoset.add("-");
				}
			}
			if(infoset.size()>1){
				throw new BusinessException("红冲单据表体行的金额方向必须相同！");
			}
			if ("edit".equals(state)) {
				BaseAggVO billvo = (BaseAggVO) savebills[0];
				checkMustInput(savebills,tradetype);
				// 修改保存
				ArapBillUtil.setClientDateAndUser(savebills, VOStatus.UPDATED);
				for (AggregatedValueObject bill : savebills) {
				
					CircularlyAccessibleValueObject[] childrenVO = bill
							.getChildrenVO();
					String primaryKey = bill.getParentVO().getPrimaryKey();
					for (CircularlyAccessibleValueObject child : childrenVO) {
						child.setAttributeValue(
								((SuperVO) child).getParentPKFieldName(),
								primaryKey);
						if (child.getPrimaryKey() == null
								|| child.getPrimaryKey().length() == 0) {
							child.setStatus(VOStatus.NEW);
						} else {
							child.setStatus(VOStatus.UPDATED);
						}
					}

				}
				if (billvo.getHeadVO().getBillstatus().intValue() == ARAPBillStatus.TEMPSAVE.VALUE
						.intValue()) {
					billvo.getHeadVO().setBillstatus(ARAPBillStatus.SAVE.VALUE);
				}

				billvo.getParentVO().setStatus(VOStatus.UPDATED);
				returnvos = ArapPubProxy.getLoginQueryService().processAction(
						WebPubUtils.SAVEBASE, tradetype, new WorkflownoteVO(),
						billvo, null, null);
			} else {
				// 新增保存
				checkMustInput(savebills,tradetype);
				
				//ArapBillUtil.setClientDateAndUser(savebills, VOStatus.NEW);

				savebills[0].getParentVO().setAttributeValue(IBillFieldGet.BILLMAKER, AppContext.getInstance().getPkUser());
				savebills[0].getParentVO().setAttributeValue(IBillFieldGet.CREATOR, AppContext.getInstance().getPkUser());
				if (savebills[0].getParentVO().getAttributeValue(IBillFieldGet.CREATIONTIME) == null) {
					savebills[0].getParentVO().setAttributeValue(IBillFieldGet.CREATIONTIME, AppContext.getInstance().getBusiDate());
				}
				savebills[0].getParentVO().setAttributeValue(IBillFieldGet.APPROVESTATUS, -1);
				savebills[0].getParentVO().setStatus(VOStatus.NEW);
				String pk_org = savebills[0].getHeadVO().getPk_org();

				String primaryKey = savebills[0].getHeadVO().getPrimaryKey();
				if (primaryKey == null) {
					primaryKey = "";
				}
				if(!StringUtils.isEmpty(accessorybillid)){
					savebills[0].getParentVO().setPrimaryKey(accessorybillid);
				}else if (!StringUtils.isEmpty(msg_pk_bill)
						&& StringUtils.isEmpty(primaryKey)) {
					savebills[0].getParentVO().setPrimaryKey(msg_pk_bill);
				}

				ArapBillVOUtils.dealVoScaleAndHtb(savebills[0]);

				returnvos = ArapPubProxy.getLoginQueryService().processAction(
						WebPubUtils.SAVEBASE, tradetype, new WorkflownoteVO(),
						savebills[0], null, null);
			}

			BaseAggVO billvo = null;
			if (returnvos instanceof AggregatedValueObject[]) {
				AggregatedValueObject[] returnObjects = (AggregatedValueObject[]) returnvos;
				if (returnObjects.length > 0 && returnObjects[0] != null) {
					billvo = (BaseAggVO) returnObjects[0];
				}
			} else if (returnvos instanceof Object[]) {
				Object[] returnObjects = (Object[]) returnvos;
				if (returnObjects.length > 0 && returnObjects[0] != null
						&& (returnObjects[0] instanceof BaseAggVO[])) {
					Object returnObject = returnObjects[0];
					billvo = ((BaseAggVO[]) returnObject)[0];
				}
			}
			if (returnvos instanceof AggregatedValueObject) {
				billvo = (BaseAggVO) returnvos;
			}
			if (billvo == null) {
				ExceptionUtils.wrappBusinessException("未找到应付单信息!");
			}

			returnvos = WebBillTypeFactory
					.getWebBillType(tradetype)
					.getIArapBillPubQueryService()
					.findBillByPrimaryKey(
							new String[] { savebills[0].getHeadVO()
									.getPrimaryKey() });

			ITranslateVODataService service = NCLocator.getInstance().lookup(
					ITranslateVODataService.class);
			if (returnvos instanceof AggregatedValueObject[]) {
				AggregatedValueObject[] returnObjects = (AggregatedValueObject[]) returnvos;
				if (returnObjects.length > 0 && returnObjects[0] != null) {
					AggregatedValueObject returnObject = returnObjects[0];
					ArapBillVOUtils.dealVoScaleAndHtb((BaseAggVO) returnObject);

					if (billtypeCode.equals("F1")) {
						PayableBillVO head = (PayableBillVO) returnObject
								.getParentVO();
						if (head.getLinkmoney() != null) {
							head.setLinkmoney(head.getLinkmoney().setScale(2,
									UFDouble.ROUND_HALF_UP));
						}
					}

					if (billItems == null) {
						json = service.transAggvoToJSON(returnObject);
					} else {
						json = service
								.transAggvoToJSON(returnObject, billItems);
					}
				}
			} else if (returnvos instanceof Object[]) {
				Object[] returnObjects = (Object[]) returnvos;
				if (returnObjects.length > 0 && returnObjects[0] != null
						&& (returnObjects[0] instanceof BaseAggVO[])) {
					Object returnObject = returnObjects[0];
					BaseAggVO[] object = (BaseAggVO[]) returnObject;
					if (object != null && object.length > 0) {
						ArapBillVOUtils.dealVoScaleAndHtb(object[0]);
						if (billItems == null) {
							json = service.transAggvoToJSON(object[0]);
						} else {
							json = service.transAggvoToJSON(object[0],
									billItems);
						}
					}
				}
			}
			json.put("success", "true");
			return json.toString();

		} catch (Exception e) {
			if (e.getCause() != null
					&& e.getCause() instanceof ArapTbbException) {
				json.put("type", "warning");
				Logger.error(e.getMessage(), e);
				json.put("success", "false");
				json.put("message", e.getMessage());
			} else {
				ExceptionUtils.wrappException(e);
			}
		}
		return json.toString();
	}
	
	public static void checkMustInput(BaseAggVO[]  baseAggVOs,String tradetype)
			throws BusinessException {
		for(BaseAggVO baseAggVO:baseAggVOs)
		{
			// 匹配模板
			BillOperaterEnvVO envvo = new BillOperaterEnvVO();
			envvo.setBilltype(WebBillTypeFactory.getWebBillType(tradetype)
					.getNodeCode());
			envvo.setNodekey(tradetype);
			envvo.setOperator(AppContext.getInstance().getPkUser());
	
			envvo.setCorp(AppContext.getInstance().getPkGroup());
			BillTempletVO billTempletVO = ArapTemplateQueryUtil
					.findBillTempletDatas(envvo);
	
			if (billTempletVO == null)
				throw new BusinessException("查询模板失败");
			Map<String, BillTempletBodyVO> headMap = new HashMap<String, BillTempletBodyVO>();
			Map<String, BillTempletBodyVO> bodyMap = new HashMap<String, BillTempletBodyVO>();
			BillTempletBodyVO[] billtempbodyVOs = billTempletVO.getBodyVO();
			
			//表体跳过校验的字段
			Set<String> bodyExcludeItems = new HashSet<>();
			bodyExcludeItems.add("taxcodeid");		//税码
			
			for (BillTempletBodyVO vo : billtempbodyVOs) {
				int position = vo.getPos();
				if (position == IBillItem.HEAD && vo.getNullflag().booleanValue()) {
					// 表头的
					// headSet.add(vo.getItemkey());
					headMap.put(vo.getItemkey(), vo);
				} else if (position == IBillItem.BODY && vo.getNullflag().booleanValue()
						&& !bodyExcludeItems.contains(vo.getItemkey())) {
					// 表体的
					bodyMap.put(vo.getItemkey(), vo);
				}
			}
			StringBuilder sb = new StringBuilder();
			BaseBillVO applyvo = (BaseBillVO) baseAggVO.getParent();
			for (String key : applyvo.getAttributeNames()) {
				if (headMap.containsKey(key)
						&& applyvo.getAttributeValue(key) == null) {
					String label = headMap.get(key).getDefaultshowname();
					if (label == null) {
						label = applyvo.getMetaData().getAttribute(key).getColumn()
								.getLabel();
					}
					String value = label;
					sb.append(value).append(",");
				}
			}
			if (sb.length() > 0) {
				sb = new StringBuilder("<br>表头信息：<br>").append(sb.substring(0,
						sb.length() - 1));
			}
			StringBuilder sb2 = new StringBuilder();
			CircularlyAccessibleValueObject[] applybvos = baseAggVO
					.getChildrenVO();
			Set<String> infoset = new HashSet<String>();
			for (CircularlyAccessibleValueObject bvo : applybvos) {
				for (String key : bvo.getAttributeNames()) {
					if (bodyMap.containsKey(key)
							&& bvo.getAttributeValue(key) == null) {
						String label = bodyMap.get(key).getDefaultshowname();
						if (label == null) {
							label = ((BaseItemVO ) bvo).getMetaData()
									.getAttribute(key).getColumn().getLabel();
						}
						String value = label;
						if (infoset.contains(value)) {
							continue;
						}
						infoset.add(value);
						sb2.append(value).append(",");
					}
				}
			}
			if (sb2.length() > 0) {
				sb2 = new StringBuilder("表体信息：<br>").append(sb2.substring(0,
						sb2.length() - 1));
			}
			if (sb.length() > 0 || sb2.length() > 0) {
				sb = new StringBuilder("数据不完整，请编辑完善以下信息：").append(sb)
						.append("<br>").append(sb2);
			}
			if (sb.length() > 0) {
				throw new BusinessException(sb.toString());
			}
		}
	}

	
	/**
	 * 生成单据号
	 * 
	 * @param bills
	 * @throws BusinessException
	 */
	void setBillCode(AggregatedValueObject[] bills) throws BusinessException {
		FinanceBillCodeUtils util = ArapBillPubUtil.getBillCodeUtil(bills[0]);
		List<AggregatedValueObject> lastVo = new ArrayList<AggregatedValueObject>();
		for (AggregatedValueObject bill : bills) {
			if (!util.isPrecode(bill))
				lastVo.add(bill);
		}
		if (lastVo.size() > 0)
			util.createBillCode(lastVo.toArray(new AggregatedValueObject[lastVo
					.size()]));
	}

	// private void checkImag(String billid, String pk_org, String tradetype)
	// throws BusinessException {
	// int scantype = ImageServiceUtil.getImageScanType(pk_org, tradetype);
	// if (scantype == 1 || scantype == 2) {
	// if (!ImageServiceUtil.ifImageHasScaned(billid)) {
	// ExceptionUtils
	// .wrappBusinessException("应付单未扫描影像，不能提交单据，请先处理扫描任务!");
	// }
	// }
	//
	// NCLocator.getInstance().lookup(IInvoiceForPubService.class)
	// .commitCheckWithImage(pk_org, billid);
	// }

	public String commit(RequestParam request) throws Exception {
		String isAlarmPassed = request.getParameter("isAlarmPassed");
		String tradetype = request.getParameter("tradetype");
		String pk_group = ESAPI.encoder().encodeForSQL(new OracleCodec(),
				request.getParameter("pk_group"));
		String billType = WebPubUtils.getbilltype(tradetype, pk_group);
		String openbillid = ESAPI.encoder().encodeForSQL(new OracleCodec(),
				request.getParameter("pk_bill"));

		String pk_billtemplet = ESAPI.encoder().encodeForSQL(new OracleCodec(),
				request.getParameter("pk_billtemplet"));
		List<BillItem> billItems = getBillItemBypkTemplet(pk_billtemplet);

		// String billtypeCode = WebBillTypeFactory.getWebBillType(tradetype)
		// .getBilltypeCode();

		String userId = InvocationInfoProxy.getInstance().getUserId();
		String creator = null;
		JSONObject json = new JSONObject();
		Object processBatch = null;
		String errorMessage = "";
		String[] pks = { openbillid };
		BaseAggVO[] billvo = WebBillTypeFactory.getWebBillType(tradetype)
				.getIArapBillPubQueryService().findBillByPrimaryKey(pks);
		//wangyl7 2018年1月17日21:49:09 begin
		if (billvo == null
				|| billvo.length == 0
				|| (Integer.valueOf(1)).equals(billvo[0].getParentVO()
						.getAttributeValue("dr"))) {
		//end
			throw new BusinessException("单据状态已改变,请刷新界面！");
			
		}
		
		creator = billvo[0].getHeadVO().getBillmaker();

		if (!(userId.equals(creator))) {
			throw new BusinessException("当前用户不是制单人，不能提交！");
		}
		BaseBillVO headVO = billvo[0].getHeadVO();
		Integer approveStatus = headVO.getApprovestatus();
		if (!(approveStatus)
				.equals(BillEnumCollection.ApproveStatus.NOSTATE.VALUE)) {
			throw new BusinessException("非自由态单据，不能提交！");
		}
		if (billvo == null || billvo.length == 0 || billvo[0] == null) {
			throw new BusinessException("单据不存在或已被其他人删除，请刷新后再试");
		}

		// 提交时判断无票付款选项
		FISqlBuilder sql = new FISqlBuilder();
		sql.select();
		sql.append(" count(1) ");
		sql.from(InvoiceAssociateVO.getDefaultTableName());
		sql.where();
		sql.append(InvoiceAssociateVO.BILLID, billvo[0].getPrimaryKey());
		sql.appendDr();
		IRowSet rows = new DataAccessUtils().query(sql.toString());
		rows.next();
		if (rows.getInt(0) > 0) {
			billvo[0].getHeadVO().setAttributeValue(PayableBillVO.NOINVOICE,
					UFBoolean.FALSE);
		} else {
			billvo[0].getHeadVO().setAttributeValue(PayableBillVO.NOINVOICE,
					UFBoolean.TRUE);
		}

		if (billvo[0].getHeadVO().getBillstatus().intValue() == ARAPBillStatus.TEMPSAVE.VALUE
				.intValue()) {
			billvo[0].getHeadVO().setBillstatus(ARAPBillStatus.SAVE.VALUE);
		}

		tradetype = tradetype == null ? billType : tradetype;
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(PfUtilBaseTools.PARAM_SILENTLY,
				PfUtilBaseTools.PARAM_SILENTLY);

		billvo[0].getParentVO().setAttributeValue(IBillFieldGet.APPROVESTATUS,
				BillEnumCollection.ApproveStatus.COMMIT.VALUE);
		String[] assingUsers = request.getParameters("assingUsers[]");
		if (isAlarmPassed != null && billvo!=null && billvo.length>0) {
			for (BaseAggVO vo : billvo) {
				vo.setAlarmPassed(isAlarmPassed.equals("true") ? true
						: false);
			}
		}
		try{
			if (IPFActionName.START.equals(ArapFlowUtil.getCommitActionCode(
					headVO.getPk_org(), tradetype))) {
				WorkflownoteVO noteVO = WebPubUtils.checkWorkFlow(
						IPFActionName.SIGNAL, tradetype, billvo[0], null);

				if (noteVO != null) {
					if(FIArrayUtil.isNotEmpty(assingUsers)){
						setAssign(noteVO,assingUsers);
					}
					processBatch = ArapPubProxy.getLoginQueryService()
							.processBatch(WebPubUtils.SIGNAL, billType, noteVO,
									billvo, null, paramMap);
				} else {
					noteVO = WebPubUtils.checkWorkFlow(
							IPFActionName.START, tradetype, billvo[0], null);
					if(FIArrayUtil.isNotEmpty(assingUsers)){
						setAssign(noteVO,assingUsers);
					}
					processBatch = ArapPubProxy.getLoginQueryService()
							.processBatch(WebPubUtils.START, tradetype,
									noteVO, billvo, null, paramMap);
				}
			} else {
				if (WorkFlowCheck.isWorkFlowStartup(billvo[0].getHeadVO()
						.getPrimaryKey(), tradetype)) {
					throw new Exception(
							"流程类型组织参数已经切换为审批流，已经启动工作流的单据不能修改。请修改参数或终止工作流。");
				}
				WorkflownoteVO noteVO = new WorkflownoteVO();
				if(FIArrayUtil.isNotEmpty(assingUsers)){
					setAssign(noteVO,assingUsers);
				}
				processBatch = ArapPubProxy.getLoginQueryService().processBatch(
						WebPubUtils.SAVE, tradetype, noteVO, billvo,
						null, paramMap);
			}
		} catch (Exception e) {
			if (e.getCause() != null
					&& e.getCause() instanceof ArapTbbException) {
				json.put("type", "warning");
				Logger.error(e.getMessage(), e);
				json.put("success", "false");
				json.put("message", e.getMessage());
			} else {
				ExceptionUtils.wrappException(e);
			}
		}

		PfProcessBatchRetObject retObject = (PfProcessBatchRetObject) processBatch;
		if (retObject.getRetObj() == null) {
			errorMessage += retObject.getExceptionInfo().getErrorMessage();

			// 对errorMessage进行处理，去除重复的单据号显示问题
			errorMessage = proErrorMessage(errorMessage);
		} else {
			ArapBillVOUtils
					.dealVoScaleAndHtb((BaseAggVO) retObject.getRetObj()[0]);
			if (billItems == null) {
				json = ArapPubProxy
						.getTranslateVODataService()
						.transAggvoToJSON(
								(AggregatedValueObject) retObject.getRetObj()[0]);
			} else {
				json = ArapPubProxy
						.getTranslateVODataService()
						.transAggvoToJSON(
								(AggregatedValueObject) retObject.getRetObj()[0],
								billItems);
			}
		}

		if (errorMessage.length() > 0) {
			json.put("message", errorMessage);
			json.put("success", "false");
			//刚醒控制 百分比预警 提交单据预警控制不
			if(errorMessage.contains("预警型控制")){
			   json.put("type", "warning");
			}
		} else {
			InvocationInfoProxy.getInstance().setProperty("reids_event", "true");
			EventDispatcher.fireEvent(new BusinessEvent(
					ArapConstant.ARAP_MDID_PAYABLEBILL,
					IArapBSEventType.TYPE_APIVADD_AFTER,
					(AggregatedValueObject) retObject.getRetObj()[0]));
			Logger.error("应付单保存公布业务事件 201615 通知redis更新缓存");
			json.put("success", "true");
		}

		return json.toString();
	}

	public String recall(RequestParam request) throws Exception {
		String tradetype = request.getParameter("tradetype");
		String billtypeCode = WebBillTypeFactory.getWebBillType(tradetype)
				.getBilltypeCode();
		String pk_group = ESAPI.encoder().encodeForSQL(new OracleCodec(),
				request.getParameter("pk_group"));
		String billType = WebPubUtils.getbilltype(tradetype, pk_group);
		String openbillid = ESAPI.encoder().encodeForSQL(new OracleCodec(),
				request.getParameter("pk_bill"));

		String pk_billtemplet = ESAPI.encoder().encodeForSQL(new OracleCodec(),
				request.getParameter("pk_billtemplet"));
		List<BillItem> billItems = getBillItemBypkTemplet(pk_billtemplet);

		JSONObject json = new JSONObject();
		if (StringUtils.isNotBlank(openbillid)) {
			String[] pks = { openbillid };
			BaseAggVO[] billvo = WebBillTypeFactory.getWebBillType(tradetype)
					.getIArapBillPubQueryService().findBillByPrimaryKey(pks);
			//wangyl7 2018年1月17日21:50:38 Critical问题修改  begin
			if (billvo == null
					|| billvo.length == 0
					|| (Integer.valueOf(1)).equals(billvo[0].getParentVO().getAttributeValue("dr"))
					|| !(Integer.valueOf(3)).equals(billvo[0].getParentVO().getAttributeValue("approvestatus"))) {
			//end
				throw new BusinessException("单据状态已改变,请刷新界面！");
			}
			if (billvo == null || billvo.length == 0 || billvo[0] == null) {
				throw new BusinessException("单据不存在或已被其他人删除，请刷新后再试");
			}

			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put(PfUtilBaseTools.PARAM_SILENTLY,
					PfUtilBaseTools.PARAM_SILENTLY);

			BaseBillVO headVO = billvo[0].getHeadVO();
			if (!billtypeCode.equals("F1")) {
				// 收回时删除影像
				ImageServiceUtil.recallCheckWithImage(headVO.getPrimaryKey());
			}
			for (BaseAggVO vo : billvo) {
				vo.setAlarmPassed(true);
			}
			Object returnVo = ArapPubProxy.getLoginQueryService()
					.processAction(
							WebPubUtils.getRecallActionName(billvo[0]
									.getHeadVO().getPrimaryKey(), tradetype),
							billType, new WorkflownoteVO(), billvo[0], null,
							paramMap);

			ArapBillVOUtils.dealVoScaleAndHtb(((BaseAggVO[]) returnVo)[0]);

			if (billvo[0].getChildrenVO() == null) {
				BaseItemVO[] lCircularlyAccessibleValueObject = {};
				billvo[0].setChildrenVO(lCircularlyAccessibleValueObject);
			}

			if (billItems == null) {
				json = ArapPubProxy.getTranslateVODataService()
						.transAggvoToJSON(((BaseAggVO[]) returnVo)[0]);
			} else {
				json = ArapPubProxy.getTranslateVODataService()
						.transAggvoToJSON(((BaseAggVO[]) returnVo)[0],
								billItems);
			}
			json.put("success", "true");
		}

		return json.toString();
	}

	private List<BillItem> getBillItemBypkTemplet(String pk_billtemplet) {
		return BillTemplateWebUtil.getBillItems(null, pk_billtemplet);
	}

	/**
	 * 对errorMessage进行处理，去除重复的单据号显示问题
	 * 
	 * @param errorMessage
	 * @return
	 */
	public static String proErrorMessage(String errorMessage) {

		String stra = "=";
		String strb = ">>";
		String billno = "";
		String message1 = "";
		String message2 = "";

		message1 = errorMessage
				.substring(0, errorMessage.indexOf(strb, 10) + 2);
		message2 = errorMessage.substring(errorMessage.indexOf(strb, 10) + 2);

		int indexstra = message1.indexOf(stra);
		billno = message1.substring(indexstra + 1, message1.length() - 2);
		if (message2.contains(billno)) {
			message2 = message2.replace(billno, "|||");
			message2 = message2.substring(message2.indexOf("|||") + 3);
			errorMessage = message1 + message2;
		}
		return errorMessage;
	}

	public String sendBillDelete(RequestParam request) throws Exception {
		String ts = request.getParameter("ts");
		String openBillId = request.getParameter("openbillid");
		String userId = InvocationInfoProxy.getInstance().getUserId();
		String creator = "";
		String tradetype = (String) request.getParameter("tradetype");
		IArapBillPubQueryService service = null;
		// boolean messageChangeFlag = true;
		try {
			service = WebBillTypeFactory.getWebBillType(tradetype)
					.getIArapBillPubQueryService();
		} catch (nc.vo.pub.BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		JSONObject json = new JSONObject();
		if (StringUtils.isNotBlank(openBillId)) {
			String[] pks = { openBillId };
			BaseAggVO[] billvo = null;
			if (null != service) {
				billvo = service.findBillByPrimaryKey(pks);
			}
			//wangyl7 2018年1月17日21:51:36 Critical问题修改  begin 
			if (billvo == null
					|| billvo.length == 0
					|| (Integer.valueOf(1)).equals(billvo[0].getParentVO()
							.getAttributeValue("dr"))) {
			//end	
				throw new Exception("单据状态已改变,请刷新界面！");
			}
			creator = (String) billvo[0].getParentVO().getAttributeValue(
					ApplyVO.BILLMAKER);
			if (!(userId.equals(creator))) {
				throw new Exception("当前用户不是制单人，无法删除!");
			}
			// checkPermission(ArapConstant.DELETE, "删除", billvo[0]);
			UserVO uservo = NCLocator.getInstance()
					.lookup(IUserManageQuery.class).getUser(userId);
			checkTopBilltype(Arrays.copyOf(billvo, billvo.length,
					BaseAggVO[].class));// 来自轻量级的付款申请不可删除
			this.checkOtherSystemBill(billvo);
			EnvironmentInit.initGroup(uservo == null ? null : uservo
					.getPk_group());
			BaseBillVO vo = (BaseBillVO) billvo[0].getParent();
			//删除不做预算控制
			billvo[0].setAlarmPassed(true);
			if (ts != null) {
				vo.setTs(new UFDateTime(ts));
			}
			ArapBillUtil.setClientDateAndUser(billvo, VOStatus.DELETED);
			if (!ApproveStatus.NOSTATE.VALUE.equals(vo.getApprovestatus())) {
				throw new Exception("单据已进入审批流或无工作流，无法删除!");
			}
			NCPfServiceUtils.processBatch(ArapBillUtil.DELETE, tradetype,
					billvo, new Object[] { uservo }, new WorkflownoteVO());

			String sqlCond = "billversionpk=? and pk_billtype=? and workflow_type in(4,5)";
			SQLParameter param = new SQLParameter();
			param.addParam(openBillId);
			param.addParam(tradetype);
			BaseDAO dao = new BaseDAO();
			Collection<WorkflownoteVO> colWorknote = dao.retrieveByClause(
					WorkflownoteVO.class, sqlCond, param);
			PfMessageUtil
					.deleteMessagesOfWorknote((WorkflownoteVO[]) colWorknote
							.toArray(new WorkflownoteVO[0]));
		}
		json.put("success", "true");
		return json.toString();
	}

	private void checkTopBilltype(BaseAggVO[] bills)
			throws nc.vo.pub.BusinessException {
		for (AggregatedValueObject bill : bills) {
			CircularlyAccessibleValueObject[] childrenVOs = bill
					.getChildrenVO();
			for (CircularlyAccessibleValueObject childrenVO : childrenVOs) {
				Object top_billtype = childrenVO
						.getAttributeValue(IBillFieldGet.TOP_BILLTYPE);
				if (null != top_billtype && top_billtype.equals("36D1")) {
					String[] top_billID = { FIStringUtil
							.coverToString(childrenVO
									.getAttributeValue(IBillFieldGet.TOP_BILLID)) };
					IApplyService applyService = NCLocator.getInstance()
							.lookup(IApplyService.class);
					if (applyService.isHasOpsrctype(top_billID)) {
						throw new BusinessException("来自付款申请轻量级的单据不可删除!");
					}
				}
			}
		}
		for (BaseAggVO bill : bills) {
			Object top_syscode = bill.getParentVO().getAttributeValue(
					IBillFieldGet.SRC_SYSCODE);
			Object top_billtype = bill.getChildrenVO()[0]
					.getAttributeValue(IBillFieldGet.TOP_BILLTYPE);
			Object billtype = bill.getParentVO().getAttributeValue(
					IBillFieldGet.PK_BILLTYPE);

			if (null == top_billtype)
				continue;
			if (null != top_billtype
					&& ArapWebConst.special_billtype.contains(top_billtype))
				continue;
			if (null != top_syscode) {
				CheckException.checkArgument(
						ArapWebConst.top_syscodes.contains(top_syscode
								.toString()),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"2006pub_0", "02006pub-0041")/*
															 * @ res
															 * "来源于外系统的单据不允许直接删除!"
															 */);
			}
			if (IBillFieldGet.F0.equals(billtype)
					|| IBillFieldGet.F1.equals(billtype)) {
				String jckSyscode = "113";// 进出口来源系统
				CheckException.checkArgument(
						jckSyscode.equals(top_syscode.toString()),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"2006pub_0", "02006pub-0041")/*
															 * @ res
															 * "来源于外系统的单据不允许直接删除!"
															 */);
			}
			if ("36U5".equals(top_billtype) || "36UA".equals(top_billtype)
					|| "5795".equals(top_billtype)) {
				CheckException.checkArgument(true, nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes()
						.getStrByID("2006pub_0", "02006pub-0041")/*
																 * @ res
																 * "来源于外系统的单据不允许直接删除!"
																 */);
			}
		}
	}

	private void checkOtherSystemBill(AggregatedValueObject[] bills) {
		try {
			// Integer syscode =
			// ((BaseBillVO)bills[0].getParentVO()).getSyscode();
			// List<BillDeleteChecker> pluginChecks =
			// ArapBusiPluginCenter.getAllBillDeleteCheckPlugins(syscode);

			for (AggregatedValueObject bill : bills) {
				// 先处理扩展校验
				// for (BillDeleteChecker billDeleteChecker : pluginChecks) {
				// if(billDeleteChecker.isMatch((BaseAggVO)bill)){
				// boolean candelete =
				// billDeleteChecker.canDelete((BaseAggVO)bill);
				// if(!candelete){
				// throw new
				// BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0",
				// "02006pub-0042")/* @res "来源于外系统的单据不能进行删除操作！" */);
				// }
				// }
				// }
				BaseBillVO billvo = (BaseBillVO) bill.getParentVO();
				Integer srcSyscode = billvo.getSrc_syscode();
				UFBoolean isflowbill = billvo.getIsflowbill();
				if (((BaseAggVO) bill).getIsOtherModuleOriginate()
						.booleanValue()) {
					continue;
				}
				if (isflowbill.booleanValue()) {
					continue;
				}
				if (srcSyscode.intValue() == BillEnumCollection.FromSystem.AR.VALUE
						.intValue()
						|| srcSyscode.intValue() == BillEnumCollection.FromSystem.AP.VALUE
								.intValue()
						|| srcSyscode.intValue() == BillEnumCollection.FromSystem.CMP.VALUE
								.intValue()
						|| srcSyscode.intValue() == BillEnumCollection.FromSystem.WBJHPT.VALUE
								.intValue()
						|| srcSyscode.intValue() == BillEnumCollection.FromSystem.XTDJ.VALUE
								.intValue()) {
					continue;
				}
				Object top_billtype = bill.getChildrenVO()[0]
						.getAttributeValue(IBillFieldGet.TOP_BILLTYPE);

				if (null == top_billtype
						|| ArapWebConst.special_billtype.contains(top_billtype)) {
					continue;
				}
				if (top_billtype != null) {
					if ("FCT1".equals(top_billtype.toString().trim())
							|| "FCT2".equals(top_billtype.toString().trim())) {
						continue;
					}
				}
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes()
						.getStrByID("2006pub_0", "02006pub-0042")/*
																 * @res
																 * "来源于外系统的单据不能进行删除操作！"
																 */);
			}
		} catch (BusinessException e) {
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * 指派信息或审批不起作用
	 * 
	 * @param wfvo
	 * @param pk_users
	 * @param userObject
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void setAssign(WorkflownoteVO wfvo, String[] pk_users) {
		if (wfvo == null)
			return;
		// 设置指派信息
		if (pk_users != null) {
			Vector<AssignableInfo> assInfos = wfvo.getTaskInfo()
					.getAssignableInfos();
			for (Iterator<AssignableInfo> assit = assInfos.iterator(); assit
					.hasNext();) {
				AssignableInfo assInfo = assit.next();
				// if(key.equals(assInfo.getActivityDefId())){
				// 先清除
				assInfo.getAssignedOperatorPKs().clear();
				assInfo.getOuAssignedUsers().clear();
				Map<String, OrganizeUnit> tmpMap = new HashMap<String, OrganizeUnit>();
				String[] oper = assInfo.getOperatorPKs().toArray(new String[0]);
				OrganizeUnit[] tmpOU = assInfo.getOuUsers().toArray(
						new OrganizeUnit[0]);
				for (int k = 0; k < oper.length; k++) {
					tmpMap.put(oper[k], tmpOU[k]);
				}
				String[] oper2 = assInfo.getOperatorPKs().toArray(
						new String[0]);
				for (String pk_user : pk_users) {
					for (int m = 0; m < oper2.length; m++) {
						if (pk_user.equals(oper2[m])) {
							assInfo.getAssignedOperatorPKs().add(pk_user);
							assInfo.getOuAssignedUsers().add(
									tmpMap.get(pk_user));
						}
					}
				}
			}
		}
	}
}
