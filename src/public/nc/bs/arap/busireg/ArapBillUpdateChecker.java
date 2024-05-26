package nc.bs.arap.busireg;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nc.bs.arap.util.ArapVOUtils;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.basebill.BaseItemVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.arap.pub.BillEnumCollection;
import nc.vo.arap.utils.StringUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

/**
 * 收付单据修改-收付基础校验类
 * 
 * @author lvhj
 *
 */
public class ArapBillUpdateChecker implements BillUpdateChecker {
	
	// 进出口单据
	final List<String> jckBillTypes = Arrays.asList(new String[] {
			"5801",
			"5818"
			});
	
	//项目管理模块
	final String STUFF_SETTLE_BILL = "4D39"; //4D39（材料结算单推应付单）
	final String EXPENSES_SETTLE_BILL = "4D83"; //4D83（费用结算单推应付单）
	final String PLAN_MONEY_BILL = "4D48"; // 4D48（进度款单推应付单）
	final String PROJECT_SETTLE_BILL = "4D50"; // 4D50（项目结算单推应付单）
	final String SELL_CONTRACT_BILL = "4D60"; // 4D60（销售合同推应收单）
	final String GATHERING_PLAN_BILL = "4D62"; // 4D62（收款计划推收款单）
	final String PREPAY_BILL = "4D46"; // 预付款单4D46 到 付款单

	final String SELL_EXPENSES_BILL = "35"; // 35 销售费用单
	
	final List<String> immutableRows = Arrays.asList(new String[] {
			"5790",// 出口发票
			"5760",// 出口调整发票
			"5820",// 调账申请
			"6090",// 转口发票
			"5818",// 代理进口发票
			"4A2F",
			"4A3F",
			"4A3H",
			"4A77",
			"4A87", // 周转材租入租赁结算单
			"21",
			"2201",
			"36J3",
			"4D46",
			"264X",
			SELL_EXPENSES_BILL,
			STUFF_SETTLE_BILL,
			EXPENSES_SETTLE_BILL,
			PLAN_MONEY_BILL,
			PROJECT_SETTLE_BILL,
			SELL_CONTRACT_BILL,
			GATHERING_PLAN_BILL,// 35 销售费用单 2013-04-16 @wangdongd
			PREPAY_BILL });
	
	final List<String> addAllowDelNotAllowRows = Arrays.asList(new String[] { "F0", "F1", "F2", "F3" });
	final List<String> delAllowAddNotAllowRows = Arrays.asList(new String[] { "36D1" });
	
	// 金额等第一个重要字段
	final List<String> firstEditableFields = Arrays.asList(new String[] {"36D1"});
	// 往来字段等第二重要字段
	final List<String> secondEditableFields = Arrays.asList(new String[] { "264X", "264x" });
	
	
	@Override
	public boolean isMatch(BaseAggVO vo) {
		return true;
	}

	@Override
	public boolean canUpdateBillWithoutCtrl(BaseAggVO vo) {
		BaseItemVO[] vosNew = (BaseItemVO[]) vo.getChildrenVO();
		String top_billtype = vosNew[0].getTop_billtype();
		String billtype = vosNew[0].getPk_billtype();
		if("5720".equals(top_billtype)||"4B36".equals(top_billtype)||"F1".equals(billtype)){
			// 出口合同、资产工单，不校验
			return true;
		}
		return false;
	}

	@Override
	public String canAddLine(String top_billtype,List<BaseItemVO> addlines) {
		if (top_billtype != null) {
			if (immutableRows.contains(top_billtype)||delAllowAddNotAllowRows.contains(top_billtype)) {
				return NCLangRes4VoTransl.getNCLangRes().getStrByID("2006arappub0316_0", "02006arappub0316-0027", null,
						new String[] { PfDataCache.getBillType(top_billtype).getBilltypenameOfCurrLang() })/*@res 来自{0}的收付单据不能增加行*/;
			} 
		}
		return null;
	}

	@Override
	public String canDeleteLine(String top_billtype,List<BaseItemVO> deletelines) {
		boolean flag = true;
		if (top_billtype != null) {
			if (immutableRows.contains(top_billtype)) {
				flag =  false;
			} else if (addAllowDelNotAllowRows.contains(top_billtype)) {
				for (BaseItemVO baseItemVO : deletelines) {
					if (!StringUtil.isEmpty(baseItemVO.getTop_itemid())) {
						flag = false;
						break;
					}
				}
			}
		}
		if(!flag){
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("2006arappub0316_0", "02006arappub0316-0026", null,
					new String[] { PfDataCache.getBillType(top_billtype).getBilltypenameOfCurrLang() })/*@res 来自{0}的收付单据不能删除行*/;
		}
		return null;
	}

	@Override
	public String canUpdateBodyFieldValue(Map<String,Map<String, Object[]>> fieldvalue,BaseAggVO aggvo,BaseAggVO oldaggvo) {
		BaseBillVO billvo = (BaseBillVO) aggvo.getParentVO();
		BaseItemVO[] vosNew = (BaseItemVO[]) aggvo.getChildrenVO();
		
		boolean otherSystemPushBill = ArapVOUtils.isOtherSystemPushBill(billvo, vosNew[0].getTop_billtype());
		UFBoolean sddreversalflag = UFBoolean.FALSE;
		if (billvo instanceof PayBillVO) {
			sddreversalflag = ((PayBillVO) billvo).getSddreversalflag();
			if (null == sddreversalflag) {
				sddreversalflag = UFBoolean.FALSE;
			}
		}
		if (!otherSystemPushBill && !sddreversalflag.booleanValue())
			return null;

		String errmsg = null;

		for (BaseItemVO vo : vosNew) {
			Map<String, Object[]> valuechangeMap = fieldvalue.get(vo.getPrimaryKey());
			if (vo.getPrimaryKey() == null|| valuechangeMap == null || valuechangeMap.isEmpty()) {
				continue;
			}

			String topBilltype = vo.getTop_billtype();
			if (firstEditableFields.contains(topBilltype)) {
				break;
			}
			String[] s1 = new String[] {
					IBillFieldGet.OBJTYPE,
					IBillFieldGet.PK_CURRTYPE,
					IBillFieldGet.MONEY_DE,
					IBillFieldGet.MONEY_CR,
					IBillFieldGet.PAYACCOUNT,
					IBillFieldGet.RECACCOUNT,
					IBillFieldGet.MATCUSTCODE };
			String[] s2 = new String[] {
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001935")/* @res "往来对象" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001755")/* @res "币种" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0004112")/* @res "金额" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0004112")/* @res "金额" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006v61008_0", "02006v61008-0301")/* @res "付款银行账户" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006v61008_0", "02006v61008-0302")/* @res "收款银行账户" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("receivablebill", "2receive-000119") /* @res "客户物料码" */

			};
			if ("Z2".equals(vosNew[0].getTop_billtype())
					|| "4D42".equals(vosNew[0].getTop_billtype())
					|| "FCT1".equals(vosNew[0].getTop_billtype())) {
				s1 = new String[] { IBillFieldGet.OBJTYPE,
						IBillFieldGet.PK_CURRTYPE, IBillFieldGet.MONEY_DE,
						IBillFieldGet.MONEY_CR,
						// IBillFieldGet.PAYACCOUNT, IBillFieldGet.RECACCOUNT,
						IBillFieldGet.MATCUSTCODE };
				s2 = new String[] {
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"common", "UC000-0001935")/* @res "往来对象" */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"common", "UC000-0001755")/* @res "币种" */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"common", "UC000-0004112")/* @res "金额" */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"common", "UC000-0004112")/* @res "金额" */,
						// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006v61008_0",
						// "02006v61008-0301")/* @res "付款银行账户" */,
						// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006v61008_0",
						// "02006v61008-0302")/* @res "收款银行账户" */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"receivablebill", "2receive-000119") /*
																	 * @res
																	 * "客户物料码"
																	 */
				};
			}
			int t = -1;
			for (String s : s1) {
				t++;
				Object[] values = valuechangeMap.get(s);
				if(values == null ){
					continue;
				}
				Object newvalue = values[0];
				Object oldvalue = values[1];
				
				// 允许补录信息 ,只允许一次修改
				if (s.equals(IBillFieldGet.PAYACCOUNT) || s.equals(IBillFieldGet.RECACCOUNT)) {
					if (oldvalue == null || oldvalue.toString().length() != 20) {
						continue;
					}
				}
				if (s.equals(IBillFieldGet.MATCUSTCODE)) {
					// 销售出库--暂估应收:客户物料码携带到应收单后可编辑
					if ("434C".equals(topBilltype) && "23E0".equals(vo.getPk_billtype())) {
						continue;
					}
				}
				if (oldvalue == null && newvalue != null) {
					continue;
				}
				if((vo.getAttributeValue(s) instanceof UFDouble)){
					// 全额拒付不检查金额
					if (vo.getCommpaytype() != null
//							&& (vo.getAttributeValue(s) instanceof UFDouble)
							&& (BillEnumCollection.CommissionPayType.RefuseCommPay.VALUE.equals(vo.getCommpaytype()) || BillEnumCollection.CommissionPayType.CommPayPartly.VALUE
									.equals(vo.getCommpaytype()))) {
						continue;
					}else if(jckBillTypes.contains(topBilltype)){
						// 进出口单据不检查金额
						continue;
					}
				}

				errmsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0432", null, new String[] { s2[t] })/* @res "来源于外系统的单据不能进行修改的操作！" */;
				break;
			}
			if(!StringUtil.isEmpty(errmsg)){
				break;
			}
			if (secondEditableFields.contains(topBilltype)) {
				break;
			}

			String[] s3 = new String[] { IBillFieldGet.CUSTOMER, IBillFieldGet.SUPPLIER, IBillFieldGet.PK_DEPTID, IBillFieldGet.PK_PSNDOC };

			String[] s4 = new String[] {
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001589")/* @res "客户" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0000275")/* @res "供应商" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0004064")/* @res "部门" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0340")
			/* @res "业务员" */};
			int[] s5 = new int[] {
					BillEnumCollection.ObjType.CUSTOMER.VALUE.intValue(),
					BillEnumCollection.ObjType.SUPPLIER.VALUE.intValue(),
					BillEnumCollection.ObjType.DEP.VALUE.intValue(),
					BillEnumCollection.ObjType.PERSON.VALUE.intValue() };

			int m = -1;
			for (int s : s5) {
				m++;
				
				if (!(vo.getObjtype().intValue() == s)) {
					continue;
				}
				
				Object[] values = valuechangeMap.get(s3[m]);
				if(values == null ){
					continue;
				}
				Object oldvalue = values[1];

				// 允许补录信息 ,只允许一次修改
				if (oldvalue == null || oldvalue.toString().length() != 20) {
					continue;
				}

				errmsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0432", null, new String[] { s4[m] })/* @res "来源于外系统的单据不能进行修改的操作！" */;
				break;
			}
			if(!StringUtil.isEmpty(errmsg)){
				break;
			}

			List<String> ctrlPROJECT = Arrays.asList(new String[] {
					STUFF_SETTLE_BILL,
					EXPENSES_SETTLE_BILL,
					PLAN_MONEY_BILL,
					PROJECT_SETTLE_BILL,
					SELL_CONTRACT_BILL,
					GATHERING_PLAN_BILL,
					PREPAY_BILL });
			
			List<String> ctrlCONTRACTNO = Arrays.asList(new String[] { 
					STUFF_SETTLE_BILL, 
					GATHERING_PLAN_BILL, 
					PLAN_MONEY_BILL, 
					PROJECT_SETTLE_BILL, });
			
			List<String> ctrlMATERIAL = Arrays.asList(new String[] {  
					PROJECT_SETTLE_BILL, 
					PLAN_MONEY_BILL,
					EXPENSES_SETTLE_BILL });
			
			List<String> ctrlPROJECT_TASK = Arrays.asList(new String[] { 
					EXPENSES_SETTLE_BILL, 
					PREPAY_BILL, 
					PLAN_MONEY_BILL,
					PROJECT_SETTLE_BILL,
					STUFF_SETTLE_BILL});
			if (ctrlPROJECT.contains(topBilltype)) {// 项目管理单据
				
				Object[] values = valuechangeMap.get(IBillFieldGet.PROJECT);
				if(values != null ){
					String projectLang = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-001021"); /* @res "项目" */
					errmsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0432", null, new String[] { projectLang })/* @res "来源于外系统的单据不能进行修改的操作！"*/;
					break;
				}
				values = valuechangeMap.get("CBS");
				if(values != null ){
					errmsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0432", null, new String[] { "CBS" })/* @res "来源于外系统的单据不能进行修改的操作！"*/;
					break;
				}
				
				// 合同号
				if (ctrlCONTRACTNO.contains(topBilltype)) {
					values = valuechangeMap.get(IBillFieldGet.CONTRACTNO);
					if(values != null ){
						String contractnoLang = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000234");/* @res "合同号" */
						errmsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0432", null,
								new String[] { contractnoLang })/* @res "来源于外系统的单据不能进行修改的操作！" */;
						break;
					}
				}
				
				// 物料
				if (ctrlMATERIAL.contains(topBilltype)) {
					values = valuechangeMap.get(IBillFieldGet.MATERIAL);
					if(values != null ){
						String materialLang = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000594");/* @res "物料" */
						errmsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("2006pub_0", "02006pub-0432", null, new String[] { materialLang })/* @res "来源于外系统的单据不能进行修改的操作！" */;
						break;
						
					}
				}
				
				// 项目任务
				if (ctrlPROJECT_TASK.contains(topBilltype)) {
					values = valuechangeMap.get(IBillFieldGet.PROJECT_TASK);
					if(values != null ){
						String project_taskLang = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-001023");/* @res "项目任务" */
						errmsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0432", null,
								new String[] { project_taskLang })/* @res "来源于外系统的单据不能进行修改的操作！" */;
						break;
					}
				}
				// 4D50 需要控制的 付款协议和 起算时间
				if (PROJECT_SETTLE_BILL.equals(topBilltype)) {// pk_payterm busidate 4D50
					values = valuechangeMap.get(IBillFieldGet.PK_PAYTERM);
					if(values != null ){
						String pk_paytermLang = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000055");/* @res "付款协议" */
						errmsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0432", null,
								new String[] { pk_paytermLang })/* @res "来源于外系统的单据不能进行修改的操作！" */;
						break;
						
					}
					values = valuechangeMap.get(IBillFieldGet.BUSIDATE);
					if(values != null ){
						String busidateLang = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000946");/* @res "起算时间" */
						errmsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0432", null,
								new String[] { busidateLang })/* @res "来源于外系统的单据不能进行修改的操作！" */;
						break;
					}
				}
				//  4D46 付款标志
				if (PREPAY_BILL.equals(topBilltype)) {// 4D46 prepay
					values = valuechangeMap.get(IBillFieldGet.PREPAY);
					if(values != null ){
						String scommentLang = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("paybill", "2paybill-000011"); /* @res "付款性质" */
						errmsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0432", null,
								new String[] { scommentLang })/* @res "来源于外系统的单据不能进行修改的操作！" */;
						break;
					}
				}
				
				// 4D60 摘要 
				if (SELL_CONTRACT_BILL.equals(topBilltype)) {// somment 4D60
					values = valuechangeMap.get(IBillFieldGet.SCOMMENT);
					if(values != null ){
						String scommentLang = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000426"); /* @res "摘要" */
						errmsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("2006pub_0", "02006pub-0432", null, new String[] { scommentLang })/* @res "来源于外系统的单据不能进行修改的操作！" */;
						break;
						
					}
				}
			}

			// 对直接借记退回生成的付款单部分表体字段的校验
			if (!sddreversalflag.booleanValue()) {
				continue;
			}
			String[] s6 = new String[] {
					IBillFieldGet.RATE,
					IBillFieldGet.GROUPRATE,
					IBillFieldGet.GLOBALRATE,
					IBillFieldGet.LOCAL_MONEY_DE,
					IBillFieldGet.GROUPDEBIT,
					IBillFieldGet.GLOBALDEBIT,
					IBillFieldGet.SETT_ORG,
					IBillFieldGet.PK_ORG,
					IBillFieldGet.PK_BALATYPE,
					IBillFieldGet.INVOICENO };

			String[] s7 = new String[] {
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000699")/* @res "组织本币汇率" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-001016")/* @res "集团本币汇率" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000119")/* @res "全局本币汇率" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("paybill", "2paybill-000013")/* @res "组织本币金额（借方）" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("paybill", "2paybill-000007")/* @res "集团本币金额（借方）" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("paybill", "2paybill-000005")/* @res "全局本币金额（借方）" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000715")/* @res "结算财务组织" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("paybill", "2paybill-000028")/* @res "付款财务组织" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000712")/* @res "结算方式" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000220") /* @res "发票号" */
			};

			int n = -1;
			for (String s : s6) {
				n++;
				Object[] values = valuechangeMap.get(s);
				if(values != null ){
					errmsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0432", null, new String[] { s7[n] })/* @res "来源于外系统的单据不能进行修改的操作！" */;
					break;
				}
			}
			if(!StringUtil.isEmpty(errmsg)){
				break;
			}
		}
		return errmsg;
	}

	@Override
	public String canUpdateHeadFieldValue(Map<String, Object[]> fieldvalue,BaseAggVO vo,BaseAggVO oldvo) {
		BaseBillVO billvo = (BaseBillVO) vo.getParentVO();
		BaseItemVO[] vosNew = (BaseItemVO[]) vo.getChildrenVO();
		
		boolean otherSystemPushBill = ArapVOUtils.isOtherSystemPushBill(billvo, vosNew[0].getTop_billtype());
		UFBoolean sddreversalflag = UFBoolean.FALSE;
		if (billvo instanceof PayBillVO) {
			sddreversalflag = ((PayBillVO) billvo).getSddreversalflag();
			if (null == sddreversalflag) {
				sddreversalflag = UFBoolean.FALSE;
			}
		}
		if (!otherSystemPushBill && !sddreversalflag.booleanValue())
			return null;
		// 对直接借记退回生成的付款单表头字段的校验
		// 部分表头字段是由表体自动刷表头，另一部分表头字段在单据上已经设为不可编辑，对这些表头字段无需进行校验
		String errmsg = null;
		if (sddreversalflag.booleanValue()) {
			String[] s1 = new String[] { IBillFieldGet.EXPECTDEALDATE
			// IBillFieldGet.RECACCOUNT, IBillFieldGet.CONSIGNAGREEMENT,
			// IBillFieldGet.PK_BALATYPE, IBillFieldGet.SDDREVERSALFLAG, IBillFieldGet.REVERSALREASON,
			// IBillFieldGet.BILLMAKER, IBillFieldGet.PK_ORG, IBillFieldGet.PK_GROUP, IBillFieldGet.SETT_ORG,
			// IBillFieldGet.OBJTYPE, IBillFieldGet.CUSTOMER, IBillFieldGet.PK_CURRTYPE
			};

			String[] s2 = new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("paybill", "2paybill-000108") /* @res "期望处理日期" */
			// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006v61008_0","02006v61008-0302")/* @res "收款银行账户" */,
			// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("paybill", "2paybill-000109")/* @res "托收协议号" */,
			// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000712")/* @res "结算方式" */,
			// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("paybill","2paybill-000111")/*@res "直接借记退回标志"*/,
			// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("paybill","2paybill-000112")/*@res "退回原因"*/,
			// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000165")/* @res "制单人" */,
			// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("paybill", "2paybill-000028")/* @res "付款财务组织" */,
			// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000367")/* @res "所属集团" */,
			// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "2UC000-000715")/* @res "结算财务组织" */,
			// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001935")/* @res "往来对象" */,
			// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001589")/* @res "客户" */,
			// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001755")/* @res "币种" */
			};

			int n = -1;
			for (String s : s1) {
				n++;
				Object[] values = fieldvalue.get(s);
				if(values != null){
					errmsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0432", null, new String[] { s2[n] })/* @res "来源于外系统的单据不能进行修改的操作！" */;
					break;
				}
			}
		}
		return errmsg;
	}

	@Override
	public boolean excuteDefaultCheck() {
		return false;
	}

}
