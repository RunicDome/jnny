package nc.nweb.erm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.cmp.pub.util.ApplyPubProxy;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.erm.util.CacheUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.bd.psnbankacc.IPsnBankaccPubService;
import nc.itf.erm.fieldmap.IBillFieldGet;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.md.data.access.NCObject;
import nc.md.persist.framework.MDPersistenceService;
import nc.nweb.erm.pub.ERMWebConst;
import nc.pubitf.org.cache.IBasicOrgUnitPubService_C;
import nc.pubitf.para.SysInitQuery;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.bd.psn.PsnjobVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.ErmBillCalUtil;
import nc.vo.erm.BXBillVO;
import nc.vo.erm.ERMTaxCalVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.fysq.FYSQBillVO;
import nc.vo.erm.fyyt.FYYTBillVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.calculator.ConstantUtil;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.tmpub.util.StringUtil;
import nc.web.erm.bill.fysq.FYSQOrgChangedLoadDefData;
import nc.web.erm.utils.ERMBillVOUtil;
import nc.web.erm.utils.ERMRefUtil;
import nc.web.erm.utils.ERMTaxUtil;
import nc.web.erm.utils.ERMValueCheck;

import org.codehaus.jettison.json.JSONObject;

import uap.iweb.entity.DataTable;
import uap.iweb.entity.Row;
import uap.iweb.event.run.DataTableFieldEvent;
import uap.iweb.icontext.IWebViewContext;

/**
 * 值改变处理
 * 
 * @author dingyma
 *
 */
public class DataChangeLogic {

	/**
	 * 新增时赋值
	 * 
	 * @param hTable
	 * @param bTableArr
	 * @param shareTable
	 * @throws Exception
	 */
	public static void setDefaultValueForAdd(DataTable hTable, DataTable[] bTableArr, DataTable shareTable)
			throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJRQ);// 单据日期
		String bzbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.BZBM));// 币种
		String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);
		String groupCurrPK = Currency.getGroupCurrpk(pk_group);
		String globalCurrPk = Currency.getGlobalCurrPk(null);
		// if (ERMValueCheck.isEmpty(bzbm)) {// 如果表头币种字段为空,取组织本币设置为默认币种
		bzbm = orgLocalCurrPK;
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.BZBM, bzbm);
		// }
		UFDouble[] hlArr = ErmBillCalUtil.getRate(bzbm, pk_org, pk_group, djrq);// 取汇率
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.BBHL, hlArr[0]);// 设置汇率
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.GROUPBBHL, hlArr[1]);// 设置集团本币汇率
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.GLOBALBBHL, hlArr[2]);// 设置全局本币汇率
		int hlDigit = Currency.getRateDigit(pk_org, bzbm, orgLocalCurrPK);// 汇率精度
		int grouphlDigit = Currency.getGroupRateDigit(pk_org, pk_group, bzbm);// 集团汇率精度
		int globalhlDigit = Currency.getGlobalRateDigit(pk_org, bzbm);// 全局汇率精度
		FIDataTableUtil.setHeadItemDigi(hTable, IBillFieldGet.BBHL, hlDigit);// 设置汇率精度
		FIDataTableUtil.setHeadItemDigi(hTable, IBillFieldGet.GROUPBBHL, grouphlDigit);// 设置集团汇率精度
		FIDataTableUtil.setHeadItemDigi(hTable, IBillFieldGet.GLOBALBBHL, globalhlDigit);// 设置全局汇率精度
		// FIDataTableUtil.setHeadItemDigi(hTable, IBillFieldGet.TAX_RATE,
		// ERMTaxUtil.TAX_RATE_DIGIT);// 设置税率精度
		// 设置汇率字段的可编辑性
		boolean[] hlEnableArr = getHLEnableArr(pk_group, bzbm, orgLocalCurrPK, groupCurrPK, globalCurrPk);
		boolean hlEnable = hlEnableArr[0];
		boolean groupHlEnable = hlEnableArr[1];
		boolean globalHlEnable = hlEnableArr[2];
		FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.BBHL, hlEnable);
		FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.GROUPBBHL, groupHlEnable);
		FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.GLOBALBBHL, globalHlEnable);
		// 设置金额精度
		int ybDigit = Currency.getCurrDigit(bzbm);// 原币精度
		int bbDigit = Currency.getCurrDigit(orgLocalCurrPK);// 组织本币精度
		int groupbbDigit = Currency.getCurrDigit(groupCurrPK);// 集团本币精度
		int globalbbDigit = Currency.getCurrDigit(globalCurrPk);// 全局本币精度
		setHeadItemDigit(hTable, JKBXHeaderVO.getYbjeField(), ybDigit);
		setHeadItemDigit(hTable, JKBXHeaderVO.getOrgBbjeField(), bbDigit);
		setHeadItemDigit(hTable, JKBXHeaderVO.getHeadGroupBbjeField(), groupbbDigit);
		setHeadItemDigit(hTable, JKBXHeaderVO.getHeadGlobalBbjeField(), globalbbDigit);
		// 设置表头金额的默认值
		setHeadItemDefaultValue(hTable, BXHeaderVO.getJeField(), UFDouble.ZERO_DBL);

		for (DataTable bTable : bTableArr) {
			// 设置表体币种、汇率和精度
			bTable.updateMeta(Func.toString(IBillFieldGet.BZBM_BUSITEM), ERMWebConst.FIELD_DEFAULT, bzbm);// 表体币种默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.BBHL_BUSITEM), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[0]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.BBHL_BUSITEM), ERMWebConst.FIELD_PRECISION,
					String.valueOf(hlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.BBHL_BUSITEM), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_BUSITEM), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[1]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_BUSITEM), ERMWebConst.FIELD_PRECISION,
					String.valueOf(grouphlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_BUSITEM), ERMWebConst.FIELD_ENABLE,
					String.valueOf(groupHlEnable));// 表体汇率可编辑性默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_BUSITEM), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[2]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_BUSITEM), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalhlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_BUSITEM), ERMWebConst.FIELD_ENABLE,
					String.valueOf(globalHlEnable));// 表体汇率可编辑性默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.TAX_RATE_BUSITEM), ERMWebConst.FIELD_PRECISION, ""
					+ ERMTaxUtil.TAX_RATE_DIGIT);// 表体税率精度默认值
			updateMetaForMultiField(bTable, BXBusItemVO.getYbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(ybDigit));
			updateMetaForMultiField(bTable, BXBusItemVO.getBodyOrgBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(bbDigit));
			updateMetaForMultiField(bTable, BXBusItemVO.getBodyGroupBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(groupbbDigit));
			updateMetaForMultiField(bTable, BXBusItemVO.getBodyGlobalBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalbbDigit));
			updateMetaForMultiField(bTable, BXBusItemVO.getBodyJeFieldForDecimal(), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(UFDouble.ZERO_DBL));// 设置默认值为0
			// 设置金额的默认值，因为上面设置默认值的代码对表体当前行无效，需要手工设置
			setBodyItemDefaultValue(bTable.getCurrentRow(), BXBusItemVO.getBodyJeFieldForDecimal(), UFDouble.ZERO_DBL);
			for (int i = 0; i < bTable.getAllRow().length; i++) {// 这里不排除当前行，处理精度
				Row row = bTable.getAllRow()[i];
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BZBM_BUSITEM, bzbm);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BBHL_BUSITEM, hlArr[0]);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPBBHL_BUSITEM, hlArr[1]);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALBBHL_BUSITEM, hlArr[2]);
				setBodyConfig(pk_group, pk_org, djrq, row);// 表体行币种变化后处理
			}
		}
		doBodyCalculation(hTable, bTableArr);
		doBodyTotalToHead(hTable, bTableArr);

		// 处理分摊页签
		if (ERMValueCheck.isNotEmpty(shareTable)) {
			shareTable.updateMeta(Func.toString(IBillFieldGet.BZBM_SHARE), ERMWebConst.FIELD_DEFAULT, bzbm);// 表体币种默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.BBHL_SHARE), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[0]));// 表体汇率默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.BBHL_SHARE), ERMWebConst.FIELD_PRECISION,
					String.valueOf(hlDigit));// 表体汇率精度默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.BBHL_SHARE), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_SHARE), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[1]));// 表体汇率默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_SHARE), ERMWebConst.FIELD_PRECISION,
					String.valueOf(groupbbDigit));// 表体汇率精度默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_SHARE), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_SHARE), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[2]));// 表体汇率默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_SHARE), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalbbDigit));// 表体汇率精度默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_SHARE), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			updateMetaForMultiField(shareTable, CShareDetailVO.getYbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(ybDigit));
			updateMetaForMultiField(shareTable, CShareDetailVO.getBodyOrgBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(bbDigit));
			updateMetaForMultiField(shareTable, CShareDetailVO.getBodyGroupBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(groupbbDigit));
			updateMetaForMultiField(shareTable, CShareDetailVO.getBodyGlobalBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalbbDigit));
			updateMetaForMultiField(shareTable, CShareDetailVO.getBodyJeFieldForDecimal(), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(UFDouble.ZERO_DBL));// 设置默认值为0
			// 设置金额的默认值，因为上面设置默认值的代码对表体当前行无效，需要手工设置
			setBodyItemDefaultValue(shareTable.getCurrentRow(), CShareDetailVO.getBodyJeFieldForDecimal(),
					UFDouble.ZERO_DBL);
			for (int i = 0; i < shareTable.getAllRow().length; i++) {// 这里不排除当前行，处理精度
				Row row = shareTable.getAllRow()[i];
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BZBM_SHARE, bzbm);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BBHL_SHARE, hlArr[0]);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPBBHL_SHARE, hlArr[1]);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALBBHL_SHARE, hlArr[2]);
				setBodyConfig4Share(pk_group, pk_org, djrq, row);// 表体行币种变化后处理
			}
			doBodyCalculation4Share(hTable, shareTable);
		}
	}

	/**
	 * 修改时赋值
	 * 
	 * @param hTable
	 * @param bTableArr
	 * @param pk_org
	 * @throws Exception
	 */
	public static void setDefaultValueForEdit(DataTable hTable, DataTable[] bTableArr, DataTable shareTable)
			throws Exception {
		String[] defaultFieldArr = new String[]{IBillFieldGet.SZXMID, IBillFieldGet.JOBID, IBillFieldGet.SKYHZH, IBillFieldGet.JKBXR, 
				IBillFieldGet.RECEIVER, IBillFieldGet.DEPTID};
		// 获取表头币种赋给表体当前行
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJRQ);// 单据日期
		String bzbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.BZBM));// 币种
		String paytarget = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PAYTARGET));//收款对象
		String hbbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.HBBM));//供应商
		String customer = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.CUSTOMER));//客户
		String custaccount = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.CUSTACCOUNT));//客商银行账户
		String freecust = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.FREECUST));
		String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);
		String groupCurrPK = Currency.getGroupCurrpk(pk_group);
		String globalCurrPk = Currency.getGlobalCurrPk(null);
		UFDouble[] hlArr = ErmBillCalUtil.getRate(bzbm, pk_org, pk_group, djrq);// 取汇率
		int hlDigit = Currency.getRateDigit(pk_org, bzbm, orgLocalCurrPK);// 汇率精度
		int grouphlDigit = Currency.getGroupRateDigit(pk_org, pk_group, bzbm);// 集团汇率精度
		int globalhlDigit = Currency.getGlobalRateDigit(pk_org, bzbm);// 全局汇率精度
		// 设置金额精度
		int ybDigit = Currency.getCurrDigit(bzbm);// 原币精度
		int bbDigit = Currency.getCurrDigit(orgLocalCurrPK);// 组织本币精度
		int groupbbDigit = Currency.getCurrDigit(groupCurrPK);// 集团本币精度
		int globalbbDigit = Currency.getCurrDigit(globalCurrPk);// 全局本币精度
		// 设置汇率字段的可编辑性
		boolean[] hlEnableArr = getHLEnableArr(pk_group, bzbm, orgLocalCurrPK, groupCurrPK, globalCurrPk);
		boolean hlEnable = hlEnableArr[0];
		boolean groupHlEnable = hlEnableArr[1];
		boolean globalHlEnable = hlEnableArr[2];
		//设置表头汇率可编辑性
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.BBHL, hTable.getCurrentRow().getField(IBillFieldGet.BBHL).getValue());// 设置汇率
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.GROUPBBHL, hTable.getCurrentRow().getField(IBillFieldGet.GROUPBBHL).getValue());// 设置汇率
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.GLOBALBBHL, hTable.getCurrentRow().getField(IBillFieldGet.GLOBALBBHL).getValue());// 设置汇率
		FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.BBHL, hlEnable);
		FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.GROUPBBHL, groupHlEnable);
		FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.GLOBALBBHL, globalHlEnable);
		for (DataTable bTable : bTableArr) {
			// 设置币种、汇率的值
			FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(), IBillFieldGet.BZBM_BUSITEM, bzbm);
			FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(), IBillFieldGet.BBHL_BUSITEM, hlArr[0]);
			FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(), IBillFieldGet.GROUPBBHL_BUSITEM, hlArr[1]);
			FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(), IBillFieldGet.GLOBALBBHL_BUSITEM, hlArr[2]);
			// 如果该页签未保存，修改单据时，该页签字段默认取表头的值，收款对象、供应商、客户、客商银行账户、散户、散户银行账户
			Integer select = bTable.getSelect()[0];
			if(0 == select){
				bTable.updateMeta(Func.toString(IBillFieldGet.PAYTARGET_BUSITEM), ERMWebConst.FIELD_DEFAULT, paytarget);
				bTable.updateMeta(Func.toString(IBillFieldGet.HBBM_BUSITEM), ERMWebConst.FIELD_DEFAULT, hbbm);
				bTable.updateMeta(Func.toString(IBillFieldGet.CUSTOMER_BUSITEM), ERMWebConst.FIELD_DEFAULT, customer);
				bTable.updateMeta(Func.toString(IBillFieldGet.CUSTACCOUNT_BUSITEM), ERMWebConst.FIELD_DEFAULT, custaccount);
				bTable.updateMeta(Func.toString(IBillFieldGet.FREECUST_BUSITEM), ERMWebConst.FIELD_DEFAULT, freecust);
				FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(),IBillFieldGet.PAYTARGET_BUSITEM, paytarget);
				FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(),IBillFieldGet.HBBM_BUSITEM, hbbm);
				FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(),IBillFieldGet.CUSTOMER_BUSITEM, customer);
				FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(),IBillFieldGet.CUSTACCOUNT_BUSITEM, custaccount);
				FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(),IBillFieldGet.FREECUST_BUSITEM, freecust);
			}
			// 设置表体币种、汇率和精度
			bTable.updateMeta(Func.toString(IBillFieldGet.BZBM_BUSITEM), ERMWebConst.FIELD_DEFAULT, bzbm);// 表体币种默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.BBHL_BUSITEM), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[0]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.BBHL_BUSITEM), ERMWebConst.FIELD_PRECISION,
					String.valueOf(hlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.BBHL_BUSITEM), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_BUSITEM), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[1]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_BUSITEM), ERMWebConst.FIELD_PRECISION,
					String.valueOf(grouphlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_BUSITEM), ERMWebConst.FIELD_ENABLE,
					String.valueOf(groupHlEnable));// 表体汇率可编辑性默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_BUSITEM), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[2]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_BUSITEM), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalhlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_BUSITEM), ERMWebConst.FIELD_ENABLE,
					String.valueOf(globalHlEnable));// 表体汇率可编辑性默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.TAX_RATE_BUSITEM), ERMWebConst.FIELD_PRECISION, ""
					+ ERMTaxUtil.TAX_RATE_DIGIT);// 表体税率精度默认值
			updateMetaForMultiField(bTable, BXBusItemVO.getYbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(ybDigit));
			updateMetaForMultiField(bTable, BXBusItemVO.getBodyOrgBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(bbDigit));
			updateMetaForMultiField(bTable, BXBusItemVO.getBodyGroupBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(groupbbDigit));
			updateMetaForMultiField(bTable, BXBusItemVO.getBodyGlobalBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalbbDigit));
			updateMetaForMultiField(bTable, BXBusItemVO.getBodyJeFieldForDecimal(), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(UFDouble.ZERO_DBL));// 设置默认值为0
			// 设置金额的默认值，因为上面设置默认值的代码对表体当前行无效，需要手工设置
			setBodyItemDigit(bTable.getCurrentRow(), BXBusItemVO.getYbjeField(), ybDigit);
			setBodyItemDigit(bTable.getCurrentRow(), BXBusItemVO.getBodyOrgBbjeField(), bbDigit);
			setBodyItemDigit(bTable.getCurrentRow(), BXBusItemVO.getBodyGroupBbjeField(), groupbbDigit);
			setBodyItemDigit(bTable.getCurrentRow(), BXBusItemVO.getBodyGlobalBbjeField(), globalbbDigit);
			setBodyItemDigit(bTable.getCurrentRow(), new String[] { IBillFieldGet.TAX_RATE_BUSITEM },
					ERMTaxUtil.TAX_RATE_DIGIT);
			setBodyItemDefaultValue(bTable.getCurrentRow(), BXBusItemVO.getBodyJeFieldForDecimal(), UFDouble.ZERO_DBL);
			for(String defaultField : defaultFieldArr){
				bTable.updateMeta(defaultField, ERMWebConst.FIELD_DEFAULT, 
						Func.toString(ERMDataTableUtil.getHeadValue(hTable, defaultField)));
			}
		}
		// 处理分摊页签
		if (ERMValueCheck.isNotEmpty(shareTable)) {
			FIDataTableUtil.setBodyValueByRow(shareTable.getCurrentRow(), IBillFieldGet.BZBM_SHARE, bzbm);
			FIDataTableUtil.setBodyValueByRow(shareTable.getCurrentRow(), IBillFieldGet.BBHL_SHARE, hlArr[0]);
			FIDataTableUtil.setBodyValueByRow(shareTable.getCurrentRow(), IBillFieldGet.GROUPBBHL_SHARE, hlArr[1]);
			FIDataTableUtil.setBodyValueByRow(shareTable.getCurrentRow(), IBillFieldGet.GLOBALBBHL_SHARE, hlArr[2]);
			// 设置表体币种、汇率和精度
			shareTable.updateMeta(Func.toString(IBillFieldGet.BZBM_SHARE), ERMWebConst.FIELD_DEFAULT, bzbm);// 表体币种默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.BBHL_SHARE), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[0]));// 表体汇率默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.BBHL_SHARE), ERMWebConst.FIELD_PRECISION,
					String.valueOf(hlDigit));// 表体汇率精度默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.BBHL_SHARE), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_SHARE), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[1]));// 表体汇率默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_SHARE), ERMWebConst.FIELD_PRECISION,
					String.valueOf(grouphlDigit));// 表体汇率精度默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_SHARE), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_SHARE), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[2]));// 表体汇率默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_SHARE), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalhlDigit));// 表体汇率精度默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_SHARE), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			updateMetaForMultiField(shareTable, CShareDetailVO.getYbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(ybDigit));
			updateMetaForMultiField(shareTable, CShareDetailVO.getBodyOrgBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(bbDigit));
			updateMetaForMultiField(shareTable, CShareDetailVO.getBodyGroupBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(groupbbDigit));
			updateMetaForMultiField(shareTable, CShareDetailVO.getBodyGlobalBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalbbDigit));
			updateMetaForMultiField(shareTable, CShareDetailVO.getBodyJeFieldForDecimal(), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(UFDouble.ZERO_DBL));// 设置默认值为0
			// 设置金额的默认值，因为上面设置默认值的代码对表体当前行无效，需要手工设置
			setBodyItemDigit(shareTable.getCurrentRow(), CShareDetailVO.getYbjeField(), ybDigit);
			setBodyItemDigit(shareTable.getCurrentRow(), CShareDetailVO.getBodyOrgBbjeField(), bbDigit);
			setBodyItemDigit(shareTable.getCurrentRow(), CShareDetailVO.getBodyGroupBbjeField(), groupbbDigit);
			setBodyItemDigit(shareTable.getCurrentRow(), CShareDetailVO.getBodyGlobalBbjeField(), globalbbDigit);
			setBodyItemDefaultValue(shareTable.getCurrentRow(), CShareDetailVO.getBodyJeFieldForDecimal(),
					UFDouble.ZERO_DBL);
		}
	}

	
	public static void afterEditHeadPKORGFYYT(DataTable hTable, DataTable[] bTableArr, DataTable shareTable, String pk_org)
			throws Exception {
		String uistate = IWebViewContext.getEventParameter(ERMWebConst.UISTATUS);
		Map<String, String> clientAttrs = IWebViewContext.getRequest().getEnvironment().getClientAttributes();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		String pk_user = InvocationInfoProxy.getInstance().getUserId();
		String pk_tradetype = clientAttrs.get("tradetype");
		FYYTBillVO billvo = (FYYTBillVO) OrgChangedLoadDefData.dealDefValueFyyt(clientAttrs, pk_group, pk_org, pk_user,
				ERMWebConst.INIT_LOADDEF);
		CircularlyAccessibleValueObject headvo = billvo.getParentVO();
		CircularlyAccessibleValueObject bodyvo = (CircularlyAccessibleValueObject) billvo
				.getChildren(BXBusItemVO.class)[0];
		CircularlyAccessibleValueObject sharevo = (CircularlyAccessibleValueObject) billvo
				.getChildren(CShareDetailVO.class)[0];
		OrgChangedLoadDefData.dataTrans(headvo, new DataTable[] { hTable }, uistate);
		OrgChangedLoadDefData.dataTrans(bodyvo, bTableArr, uistate);
		if (ERMValueCheck.isNotEmpty(shareTable)) {// 分摊页签不为空时才处理
			OrgChangedLoadDefData.dataTrans(sharevo, new DataTable[] { shareTable }, uistate);
		}
		setPkOrg2HeadTable(hTable, bTableArr, pk_org);// 切换组织后给HeadTable赋值
		DataChangeLogic.afterEditPK_PAYORG(hTable, bTableArr);// 支付单位编辑后
		DataChangeLogic.setDefaultValueForAdd(hTable, bTableArr, shareTable);// 新增时赋值
		// 设置页面参照过滤条件（默认值无事件）
		OrgChangedLoadDefData.setDefRefParam(hTable, bTableArr, shareTable, pk_tradetype);
	}
	
	/**
	 * 表头主组织改变处理
	 * 
	 * @param dtEvent
	 * @param pk_group
	 * @param pk_tradetype
	 * @param pk_org
	 * @throws Exception
	 */
	public static void afterEditHeadPKORG(DataTable hTable, DataTable[] bTableArr, DataTable shareTable, String pk_org)
			throws Exception {
		String uistate = IWebViewContext.getEventParameter(ERMWebConst.UISTATUS);
		Map<String, String> clientAttrs = IWebViewContext.getRequest().getEnvironment().getClientAttributes();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		String pk_user = InvocationInfoProxy.getInstance().getUserId();
		String pk_tradetype = clientAttrs.get("tradetype");
		BXBillVO billvo = (BXBillVO) OrgChangedLoadDefData.dealDefValue(clientAttrs, pk_group, pk_org, pk_user,
				ERMWebConst.INIT_LOADDEF);
		CircularlyAccessibleValueObject headvo = billvo.getParentVO();
		CircularlyAccessibleValueObject bodyvo = (CircularlyAccessibleValueObject) billvo
				.getChildren(BXBusItemVO.class)[0];
		CircularlyAccessibleValueObject sharevo = (CircularlyAccessibleValueObject) billvo
				.getChildren(CShareDetailVO.class)[0];
		OrgChangedLoadDefData.dataTrans(headvo, new DataTable[] { hTable }, uistate);
		OrgChangedLoadDefData.dataTrans(bodyvo, bTableArr, uistate);
		if (ERMValueCheck.isNotEmpty(shareTable)) {// 分摊页签不为空时才处理
			OrgChangedLoadDefData.dataTrans(sharevo, new DataTable[] { shareTable }, uistate);
		}
		setPkOrg2HeadTable(hTable, bTableArr, pk_org);// 切换组织后给HeadTable赋值
		DataChangeLogic.afterEditPK_PAYORG(hTable, bTableArr);// 支付单位编辑后
		DataChangeLogic.setDefaultValueForAdd(hTable, bTableArr, shareTable);// 新增时赋值
		// 设置页面参照过滤条件（默认值无事件）
		OrgChangedLoadDefData.setDefRefParam(hTable, bTableArr, shareTable, pk_tradetype);
	}

	private static void setPkOrg2HeadTable(DataTable hTable, DataTable[] bTableArr, String pk_org) throws Exception {
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.PK_ORG, pk_org);
		hTable.updateMeta(IBillFieldGet.PK_ORG, ERMWebConst.FIELD_DEFAULT, pk_org);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.PK_FIORG, pk_org);
		hTable.updateMeta(IBillFieldGet.PK_FIORG, ERMWebConst.FIELD_DEFAULT, pk_org);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.PK_PAYORG, pk_org);
		hTable.updateMeta(IBillFieldGet.PK_PAYORG, ERMWebConst.FIELD_DEFAULT, pk_org);
		String className = MetaDataProcessUtil.getClassname(hTable.getCls());
		Object pk_org_v = MetaDataProcessUtil.getRelationItemValue(className, IBillFieldGet.PK_ORG, "pk_vid", pk_org);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.PK_ORG_V, pk_org_v);
		hTable.updateMeta(IBillFieldGet.PK_ORG_V, ERMWebConst.FIELD_DEFAULT, Func.toString(pk_org_v));
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.PK_PAYORG_V, pk_org_v);
		hTable.updateMeta(IBillFieldGet.PK_PAYORG_V, ERMWebConst.FIELD_DEFAULT, Func.toString(pk_org_v));
	}

	/**
	 * 表头单据日期变化后的处理
	 * 
	 * @param hTable
	 * @param bTableArr
	 * @throws Exception
	 */
	public static void afterEditHeadDJRQ(DataTable hTable, DataTable[] bTableArr) throws Exception {
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJRQ);// 单据日期
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		if (null != ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.ZHRQ)) {
			int days = SysInit.getParaInt(pk_org, BXParamConstant.PARAM_ER_RETURN_DAYS);// 最迟还款日期参数
			if (djrq != null && djrq.toString().length() > 0) {
				UFDate zhrq = djrq.getDateAfter(days);
				FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.ZHRQ, zhrq);// 最迟还款日期
			}
		}
		String bzbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.BZBM));// 币种
		String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);
		if (ERMValueCheck.isEmpty(bzbm)) {// 如果表头币种字段为空,取组织本币设置为默认币种
			bzbm = orgLocalCurrPK;
			FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.BZBM, bzbm);
		}
		UFDouble[] hlArr = ErmBillCalUtil.getRate(bzbm, pk_org, pk_group, djrq);// 取汇率
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.BBHL, hlArr[0]);// 设置汇率
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.GROUPBBHL, hlArr[1]);// 设置集团本币汇率
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.GLOBALBBHL, hlArr[2]);// 设置全局本币汇率
		doBodyCalculation(hTable, bTableArr);
		doBodyTotalToHead(hTable, bTableArr);
		List<DataTable> bTableList = new ArrayList<DataTable>();
		bTableList.add(hTable);
		for (DataTable bTable : bTableArr) {
			bTableList.add(bTable);
		}
		// 重新设置人员参照
		ERMRefUtil.dealPersonField(hTable, bTableList.toArray(new DataTable[bTableList.size()]), new String[] {
				IBillFieldGet.JKBXR, IBillFieldGet.RECEIVER });
	}

	/**
	 * 表头币种变化后的处理
	 * 
	 * @param dtEvent
	 * @throws Exception
	 */
	public static void afterEditHeadBZ(DataTable hTable, DataTable[] bTableArr, DataTable shareTable) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJRQ);// 单据日期
		String bzbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.BZBM));// 币种
		String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);
		String groupCurrPK = Currency.getGroupCurrpk(pk_group);
		String globalCurrPk = Currency.getGlobalCurrPk(null);
		if (ERMValueCheck.isEmpty(bzbm)) {// 如果表头币种字段为空,取组织本币设置为默认币种
			bzbm = orgLocalCurrPK;
			FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.BZBM, bzbm);
		}
		UFDouble[] hlArr = ErmBillCalUtil.getRate(bzbm, pk_org, pk_group, djrq);// 取汇率
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.BBHL, hlArr[0]);// 设置汇率
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.GROUPBBHL, hlArr[1]);// 设置集团本币汇率
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.GLOBALBBHL, hlArr[2]);// 设置全局本币汇率
		int hlDigit = Currency.getRateDigit(pk_org, bzbm, orgLocalCurrPK);// 汇率精度
		int grouphlDigit = Currency.getGroupRateDigit(pk_org, pk_group, bzbm);// 集团汇率精度
		int globalhlDigit = Currency.getGlobalRateDigit(pk_org, bzbm);// 全局汇率精度
		FIDataTableUtil.setHeadItemDigi(hTable, IBillFieldGet.BBHL, hlDigit);// 设置汇率精度
		FIDataTableUtil.setHeadItemDigi(hTable, IBillFieldGet.GROUPBBHL, grouphlDigit);// 设置集团汇率精度
		FIDataTableUtil.setHeadItemDigi(hTable, IBillFieldGet.GLOBALBBHL, globalhlDigit);// 设置全局汇率精度
		// FIDataTableUtil.setHeadItemDigi(hTable, IBillFieldGet.TAX_RATE,
		// ERMTaxUtil.TAX_RATE_DIGIT);// 设置税率精度
		// 设置汇率字段的可编辑性
		boolean[] hlEnableArr = getHLEnableArr(pk_group, bzbm, orgLocalCurrPK, groupCurrPK, globalCurrPk);
		boolean hlEnable = hlEnableArr[0];
		boolean groupHlEnable = hlEnableArr[1];
		boolean globalHlEnable = hlEnableArr[2];
		FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.BBHL, hlEnable);
		FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.GROUPBBHL, groupHlEnable);
		FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.GLOBALBBHL, globalHlEnable);
		// 设置金额精度
		int ybDigit = Currency.getCurrDigit(bzbm);// 原币精度
		int bbDigit = Currency.getCurrDigit(orgLocalCurrPK);// 组织本币精度
		int groupbbDigit = Currency.getCurrDigit(groupCurrPK);// 集团本币精度
		int globalbbDigit = Currency.getCurrDigit(globalCurrPk);// 全局本币精度
		setHeadItemDigit(hTable, JKBXHeaderVO.getYbjeField(), ybDigit);
		setHeadItemDigit(hTable, JKBXHeaderVO.getOrgBbjeField(), bbDigit);
		setHeadItemDigit(hTable, JKBXHeaderVO.getHeadGroupBbjeField(), groupbbDigit);
		setHeadItemDigit(hTable, JKBXHeaderVO.getHeadGlobalBbjeField(), globalbbDigit);
		// 设置表头金额的默认值
		setHeadItemDefaultValue(hTable, BXHeaderVO.getJeField(), UFDouble.ZERO_DBL);

		Object dwbm = FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.DWBM);
		// 清空单位银行账户
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.FKYHZH, null);
		// 币种重设单位银行账户参照
		setRefParamToFKYHZH(hTable, dwbm, bzbm);
		// 清空表头个人银行账户
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.SKYHZH, null);
		// 依据币种重设个人银行账户参照
		List<DataTable> bTableList = new ArrayList<DataTable>();
		bTableList.add(hTable);
		for (DataTable bTable : bTableArr) {
			bTableList.add(bTable);
		}
		ERMRefUtil.dealSkyhzhFiled(hTable, bTableList.toArray(new DataTable[bTableList.size()]),
				new String[] { IBillFieldGet.SKYHZH });

		for (DataTable bTable : bTableArr) {
			// 设置表体币种、汇率和精度
			bTable.updateMeta(Func.toString(IBillFieldGet.BZBM_BUSITEM), ERMWebConst.FIELD_DEFAULT, bzbm);// 表体币种默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.BBHL_BUSITEM), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[0]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.BBHL_BUSITEM), ERMWebConst.FIELD_PRECISION,
					String.valueOf(hlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.BBHL_BUSITEM), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_BUSITEM), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[1]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_BUSITEM), ERMWebConst.FIELD_PRECISION,
					String.valueOf(grouphlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_BUSITEM), ERMWebConst.FIELD_ENABLE,
					String.valueOf(groupHlEnable));// 表体汇率可编辑性默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_BUSITEM), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[2]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_BUSITEM), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalhlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_BUSITEM), ERMWebConst.FIELD_ENABLE,
					String.valueOf(globalHlEnable));// 表体汇率可编辑性默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.TAX_RATE_BUSITEM), ERMWebConst.FIELD_PRECISION, ""
					+ ERMTaxUtil.TAX_RATE_DIGIT);// 表体税率精度默认值
			updateMetaForMultiField(bTable, BXBusItemVO.getYbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(ybDigit));
			updateMetaForMultiField(bTable, BXBusItemVO.getBodyOrgBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(bbDigit));
			updateMetaForMultiField(bTable, BXBusItemVO.getBodyGroupBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(groupbbDigit));
			updateMetaForMultiField(bTable, BXBusItemVO.getBodyGlobalBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalbbDigit));
			updateMetaForMultiField(bTable, BXBusItemVO.getBodyJeFieldForDecimal(), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(UFDouble.ZERO_DBL));// 设置默认值为0
			// 设置金额的默认值，因为上面设置默认值的代码对表体当前行无效，需要手工设置
			setBodyItemDefaultValue(bTable.getCurrentRow(), BXBusItemVO.getBodyJeFieldForDecimal(), UFDouble.ZERO_DBL);
			for (int i = 0; i < bTable.getAllRow().length; i++) {// 这里不排除当前行，处理精度
				Row row = bTable.getAllRow()[i];
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BZBM_BUSITEM, bzbm);
				// 清空表体每一行的个人银行账户
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.SKYHZH_BUSITEM, null);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BBHL_BUSITEM, hlArr[0]);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPBBHL_BUSITEM, hlArr[1]);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALBBHL_BUSITEM, hlArr[2]);
				setBodyConfig(pk_group, pk_org, djrq, row);// 表体行币种变化后处理
			}
		}
		doBodyCalculation(hTable, bTableArr);
		doBodyTotalToHead(hTable, bTableArr);

		// 处理分摊页签
		if (ERMValueCheck.isNotEmpty(shareTable)) {
			shareTable.updateMeta(Func.toString(IBillFieldGet.BZBM_SHARE), ERMWebConst.FIELD_DEFAULT, bzbm);// 表体币种默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.BBHL_SHARE), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[0]));// 表体汇率默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.BBHL_SHARE), ERMWebConst.FIELD_PRECISION,
					String.valueOf(hlDigit));// 表体汇率精度默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.BBHL_SHARE), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_SHARE), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[1]));// 表体汇率默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_SHARE), ERMWebConst.FIELD_PRECISION,
					String.valueOf(groupbbDigit));// 表体汇率精度默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_SHARE), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_SHARE), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[2]));// 表体汇率默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_SHARE), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalbbDigit));// 表体汇率精度默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_SHARE), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			updateMetaForMultiField(shareTable, CShareDetailVO.getYbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(ybDigit));
			updateMetaForMultiField(shareTable, CShareDetailVO.getBodyOrgBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(bbDigit));
			updateMetaForMultiField(shareTable, CShareDetailVO.getBodyGroupBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(groupbbDigit));
			updateMetaForMultiField(shareTable, CShareDetailVO.getBodyGlobalBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalbbDigit));
			updateMetaForMultiField(shareTable, CShareDetailVO.getBodyJeFieldForDecimal(), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(UFDouble.ZERO_DBL));// 设置默认值为0
			// 设置金额的默认值，因为上面设置默认值的代码对表体当前行无效，需要手工设置
			setBodyItemDefaultValue(shareTable.getCurrentRow(), CShareDetailVO.getBodyJeFieldForDecimal(),
					UFDouble.ZERO_DBL);
			for (int i = 0; i < shareTable.getAllRow().length; i++) {// 这里不排除当前行，处理精度
				Row row = shareTable.getAllRow()[i];
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BZBM_SHARE, bzbm);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BBHL_SHARE, hlArr[0]);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPBBHL_SHARE, hlArr[1]);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALBBHL_SHARE, hlArr[2]);
				setBodyConfig4Share(pk_group, pk_org, djrq, row);// 表体行币种变化后处理
			}
			doBodyCalculation4Share(hTable, shareTable);
		}
		
		// *************** 清空客商银行账号，并处理客商银行账号参照  start ********************
		//清空客商银行账户
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.CUSTACCOUNT, null);
		String paytarget = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PAYTARGET));
		String hbbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.HBBM));
		String customer = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.CUSTOMER));
		//员工
		if("1".equals(paytarget)){
			//设置客商银行账户参照过滤条件
			ERMRefUtil.dealCustaccountFiled(hTable, IBillFieldGet.CUSTACCOUNT,hbbm);
		//客户
		}else if("2".equals(paytarget)){
			//设置客商银行账户参照过滤条件
			ERMRefUtil.dealCustaccountFiled(hTable, IBillFieldGet.CUSTACCOUNT,customer);
		}
		// *************** 清空客商银行账号，并处理客商银行账号参照  end ************************
	}

	/**
	 * 汇总表体金额字段到表头
	 * 
	 * @param hTable
	 * @param bTableArr
	 */
	private static void doBodyTotalToHead(DataTable hTable, DataTable[] bTableArr) {
		UFDouble[] totalArr = new UFDouble[JKBXHeaderVO.getJeField().length];
		for (int i = 0; i < totalArr.length; i++) {
			totalArr[i] = UFDouble.ZERO_DBL;
		}
		UFDouble amountTotal = UFDouble.ZERO_DBL;// 表体AMOUNT汇总到表头TOTAL，单独处理
		for (DataTable bTable : bTableArr) {
			for (int i = 0; i < bTable.getAllRow().length; i++) {
				if (i == bTable.getAllRow().length - 1) {// 当前行不计算
					continue;
				}
				Row row = bTable.getAllRow()[i];
				for (int j = 0; j < JKBXHeaderVO.getJeField().length; j++) {
					totalArr[j] = totalArr[j].add(null == FIDataTableUtil.getBodyValueByRow(row,
							JKBXHeaderVO.getJeField()[j]) ? UFDouble.ZERO_DBL : (UFDouble) FIDataTableUtil
							.getBodyValueByRow(row, JKBXHeaderVO.getJeField()[j]));
				}
				amountTotal = amountTotal.add(null == FIDataTableUtil.getBodyValueByRow(row,
						IBillFieldGet.AMOUNT_BUSITEM) ? UFDouble.ZERO_DBL : (UFDouble) FIDataTableUtil
						.getBodyValueByRow(row, IBillFieldGet.AMOUNT_BUSITEM));
			}
		}
		for (int i = 0; i < JKBXHeaderVO.getJeField().length; i++) {
			FIDataTableUtil.setHeadValue(hTable, JKBXHeaderVO.getJeField()[i], totalArr[i]);
		}
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.TOTAL, amountTotal);
	}

	/**
	 * 表头汇率变化后的处理
	 * 
	 * @param dtEvent
	 * @throws Exception
	 */
	public static void afterEditHeadHL(DataTable hTable, DataTable[] bTableArr) throws Exception {
		UFDouble bbhl = (UFDouble) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.BBHL);
		for (DataTable bTable : bTableArr) {
			for (int i = 0; i < bTable.getAllRow().length; i++) {
				if (i == bTable.getAllRow().length - 1 && bTable.getAllRow().length != 1) {// 当前行不计算
					continue;
				}
				Row row = bTable.getAllRow()[i];
				ERMDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BBHL_BUSITEM, bbhl);
			}
		}
		doBodyCalculation(hTable, bTableArr);
		doBodyTotalToHead(hTable, bTableArr);
	}

	public static void doBodyCalculation(DataTable hTable, DataTable[] bTableArr) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJRQ);// 单据日期
		for (DataTable bTable : bTableArr) {
			for (int i = 0; i < bTable.getAllRow().length; i++) {
				if (i == bTable.getAllRow().length - 1 && bTable.getAllRow().length != 1) {// 当前行不计算
					continue;
				}
				Row row = bTable.getAllRow()[i];
				if (row.getField(IBillFieldGet.TAX_RATE_BUSITEM) != null) {// 税率字段存在才计算税值
					calTaxOnRow(pk_group, pk_org, djrq, row, ERMTaxUtil.CAL_TYPE_FROM_SE);// 税金计算
					setAmountAndYbjeByTax(pk_org, row);// 根据参数将含税或无税金额的值赋给AMOUNT和YBJE
				}
				calBbOnRow(pk_group, pk_org, djrq, row);// 本币计算
			}
		}
	}

	public static void doBodyCalculation4Share(DataTable hTable, DataTable shareTable) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJRQ);// 单据日期
		for (int i = 0; i < shareTable.getAllRow().length; i++) {
			if (i == shareTable.getAllRow().length - 1) {// 当前行不计算
				continue;
			}
			Row row = shareTable.getAllRow()[i];
			calBbOnRow4Share(pk_group, pk_org, djrq, row);// 本币计算
		}
	}

	/**
	 * 表体币种编辑后处理
	 * 
	 * @param hTable
	 * @param row
	 * @throws Exception
	 */
	public static void afterEditBodyBZBM(DataTable bTable,DataTable hTable, Row row) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJRQ);// 单据日期
		String bzbm_busitem = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.BZBM_BUSITEM));// 表体币种
		UFDouble[] hlArr = ErmBillCalUtil.getRate(bzbm_busitem, pk_org, pk_group, djrq);
		ERMDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BBHL_BUSITEM, hlArr[0]);// 重新设置表体汇率
		ERMDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPBBHL_BUSITEM, hlArr[0]);// 重新设置表体集团汇率
		ERMDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALBBHL_BUSITEM, hlArr[0]);// 重新设置表体全局汇率
		setBodyConfig(pk_group, pk_org, djrq, row);// 设置汇率和精度
		afterEditBodyBBHL(hTable, row);
		// *************** 清空客商银行账号，并处理客商银行账号参照  start ********************
		//清空客商银行账户
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.CUSTACCOUNT_BUSITEM, null);
		String body_paytarget = Func.toString(ERMDataTableUtil.getBodyValueByRow(bTable.getCurrentRow(), IBillFieldGet.PAYTARGET_BUSITEM));
		if("1".equals(body_paytarget)){
			//设置客商银行账户参照过滤条件
			Object body_hbbm = ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.HBBM_BUSITEM);
			ERMRefUtil.dealCustaccountFiledBodyRow(bTable, row, IBillFieldGet.CUSTACCOUNT_BUSITEM,Func.toString(body_hbbm));
		//客户
		}else if("2".equals(body_paytarget)){
			Object body_customer = ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.CUSTOMER_BUSITEM);
			ERMRefUtil.dealCustaccountFiledBodyRow(bTable, row, IBillFieldGet.CUSTACCOUNT_BUSITEM,Func.toString(body_customer));
		}
		// *************** 清空客商银行账号，并处理客商银行账号参照  end ************************
	}

	/**
	 * 表体汇率编辑后处理
	 * 
	 * @param hTable
	 * @param row
	 * @throws Exception
	 */
	public static void afterEditBodyBBHL(DataTable hTable, Row row) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJRQ);// 单据日期
		if (row.getField(IBillFieldGet.TAX_RATE_BUSITEM) != null) {// 税率字段存在才计算税值
			calTaxOnRow(pk_group, pk_org, djrq, row, ERMTaxUtil.CAL_TYPE_FROM_SE);// 税金计算
			setAmountAndYbjeByTax(pk_org, row);// 根据参数将含税或无税金额的值赋给AMOUNT和YBJE
		}
		calBbOnRow(pk_group, pk_org, djrq, row);// 本币计算
	}

	/**
	 * 表体金额编辑后处理，用于借款单
	 * 
	 * @param row
	 * @throws Exception
	 */
	public static void afterEditBodyAMOUNT(DataTable hTable, Row row) throws Exception {
		UFDouble amount = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.AMOUNT_BUSITEM) == null ? UFDouble.ZERO_DBL
				: (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.AMOUNT_BUSITEM);
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.YBJE_BUSITEM, amount);
		afterEditBodyYBJE(hTable, row);
	}

	/**
	 * 表体原币金额编辑后处理，用于借款单
	 * 
	 * @param row
	 * @throws Exception
	 */
	public static void afterEditBodyYBJE(DataTable hTable, Row row) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJRQ);// 单据日期
		calBbOnRow(pk_group, pk_org, djrq, row);// 本币计算
	}

	/**
	 * 表体税率编辑后处理
	 * 
	 * @param hTable
	 * @param row
	 */
	public static void afterEditBodyTaxRate(DataTable hTable, Row row) throws Exception {
		afterEditBodyHSYBJE(hTable, row);
	}

	/**
	 * 表体含税原币金额编辑后处理
	 * 
	 * @param row
	 * @throws Exception
	 */
	public static void afterEditBodyHSYBJE(DataTable hTable, Row row) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJRQ);// 单据日期
		calTaxOnRow(pk_group, pk_org, djrq, row, ERMTaxUtil.CAL_TYPE_FROM_HS);// 税金计算
		setAmountAndYbjeByTax(pk_org, row);// 根据参数将含税或无税金额的值赋给AMOUNT和YBJE
		calBbOnRow(pk_group, pk_org, djrq, row);// 本币计算
	}

	/**
	 * 表体无税原币金额编辑后处理
	 * 
	 * @param hTable
	 * @param row
	 * @throws Exception
	 */
	public static void afterEditBodyWSYBJE(DataTable hTable, Row row) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJRQ);// 单据日期
		calTaxOnRow(pk_group, pk_org, djrq, row, ERMTaxUtil.CAL_TYPE_FROM_WS);// 税金计算
		setAmountAndYbjeByTax(pk_org, row);
		calBbOnRow(pk_group, pk_org, djrq, row);// 本币计算
	}

	public static void afterEditBodyYBSE(DataTable hTable, Row row) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJRQ);// 单据日期
		calTaxOnRow(pk_group, pk_org, djrq, row, ERMTaxUtil.CAL_TYPE_FROM_SE);// 税金计算
		setAmountAndYbjeByTax(pk_org, row);// 根据参数将含税或无税金额的值赋给AMOUNT和YBJE
		calBbOnRow(pk_group, pk_org, djrq, row);// 本币计算
	}

	public static void afterEditSHARE_ASSUME_AMOUNT(DataTable hTable, Row row) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJRQ);// 单据日期
		calBbOnRow(pk_group, pk_org, djrq, row);// 本币计算
	}

	/**
	 * 表体单行币种变化后设置精度、可编辑性和默认值等
	 * 
	 * @param dtEvent
	 * @throws Exception
	 */
	public static void setBodyConfig(String pk_group, String pk_org, UFDate djrq, Row row) throws Exception {
		String bzbm_busitem = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.BZBM_BUSITEM));// 表体币种
		String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);
		String groupCurrPK = Currency.getGroupCurrpk(pk_group);
		String globalCurrPk = Currency.getGlobalCurrPk(null);
		// 汇率精度
		int hlDigit = Currency.getRateDigit(pk_org, bzbm_busitem, Currency.getOrgLocalCurrPK(pk_org));
		int grouphlDigit = Currency.getGroupRateDigit(pk_org, pk_group, bzbm_busitem);
		int globalhlDigit = Currency.getGlobalRateDigit(pk_org, bzbm_busitem);
		// 设置表体汇率字段的可编辑性
		boolean[] hlEnableArr = getHLEnableArr(pk_group, bzbm_busitem, orgLocalCurrPK, groupCurrPK, globalCurrPk);
		boolean hlEnable = hlEnableArr[0];
		boolean groupHlEnable = hlEnableArr[1];
		boolean globalHlEnable = hlEnableArr[2];
		row.getField(IBillFieldGet.BBHL_BUSITEM).getMeta().getDescs()
				.put(ERMWebConst.FIELD_PRECISION, String.valueOf(hlDigit));// 设置汇率精度
		row.getField(IBillFieldGet.GROUPBBHL_BUSITEM).getMeta().getDescs()
				.put(ERMWebConst.FIELD_PRECISION, String.valueOf(grouphlDigit));// 设置汇率精度
		row.getField(IBillFieldGet.GLOBALBBHL_BUSITEM).getMeta().getDescs()
				.put(ERMWebConst.FIELD_PRECISION, String.valueOf(globalhlDigit));// 设置汇率精度
		row.getField(IBillFieldGet.BBHL_BUSITEM).setDesc(ERMWebConst.FIELD_ENABLE, String.valueOf(hlEnable));
		row.getField(IBillFieldGet.GROUPBBHL_BUSITEM).setDesc(ERMWebConst.FIELD_ENABLE, String.valueOf(groupHlEnable));
		row.getField(IBillFieldGet.GLOBALBBHL_BUSITEM)
				.setDesc(ERMWebConst.FIELD_ENABLE, String.valueOf(globalHlEnable));
		// 设置金额精度
		int ybDigit = Currency.getCurrDigit(bzbm_busitem);// 原币精度
		int bbDigit = Currency.getCurrDigit(orgLocalCurrPK);// 组织本币精度
		int groupbbDigit = Currency.getCurrDigit(groupCurrPK);// 集团本币精度
		int globalbbDigit = Currency.getCurrDigit(globalCurrPk);// 全局本币精度
		setBodyItemDigit(row, BXBusItemVO.getYbjeField(), ybDigit);
		setBodyItemDigit(row, BXBusItemVO.getBodyOrgBbjeField(), bbDigit);
		setBodyItemDigit(row, BXBusItemVO.getBodyGroupBbjeField(), groupbbDigit);
		setBodyItemDigit(row, BXBusItemVO.getBodyGlobalBbjeField(), globalbbDigit);
		if (row.getField(IBillFieldGet.TAX_RATE_BUSITEM) != null) {
			row.getField(IBillFieldGet.TAX_RATE_BUSITEM).getMeta().getDescs()
					.put(ERMWebConst.FIELD_PRECISION, "" + ERMTaxUtil.TAX_RATE_DIGIT);// 设置税率精度
			setBodyItemDigit(row, new String[] { IBillFieldGet.TAX_RATE_BUSITEM }, ERMTaxUtil.TAX_RATE_DIGIT);
		}
	}

	public static void setBodyConfig4Share(String pk_group, String pk_org, UFDate djrq, Row row) throws Exception {
		String bzbm_share = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.BZBM_SHARE));// 表体币种
		String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);
		String groupCurrPK = Currency.getGroupCurrpk(pk_group);
		String globalCurrPk = Currency.getGlobalCurrPk(null);
		// 汇率精度
		int hlDigit = Currency.getRateDigit(pk_org, bzbm_share, Currency.getOrgLocalCurrPK(pk_org));
		int grouphlDigit = Currency.getGroupRateDigit(pk_org, pk_group, bzbm_share);
		int globalhlDigit = Currency.getGlobalRateDigit(pk_org, bzbm_share);
		// 设置表体汇率字段的可编辑性
		boolean[] hlEnableArr = getHLEnableArr(pk_group, bzbm_share, orgLocalCurrPK, groupCurrPK, globalCurrPk);
		boolean hlEnable = hlEnableArr[0];
		boolean groupHlEnable = hlEnableArr[1];
		boolean globalHlEnable = hlEnableArr[2];
		row.getField(IBillFieldGet.BBHL_SHARE).getMeta().getDescs()
				.put(ERMWebConst.FIELD_PRECISION, String.valueOf(hlDigit));// 设置汇率精度
		row.getField(IBillFieldGet.GROUPBBHL_SHARE).getMeta().getDescs()
				.put(ERMWebConst.FIELD_PRECISION, String.valueOf(grouphlDigit));// 设置汇率精度
		row.getField(IBillFieldGet.GLOBALBBHL_SHARE).getMeta().getDescs()
				.put(ERMWebConst.FIELD_PRECISION, String.valueOf(globalhlDigit));// 设置汇率精度
		row.getField(IBillFieldGet.BBHL_SHARE).setDesc(ERMWebConst.FIELD_ENABLE, String.valueOf(hlEnable));
		row.getField(IBillFieldGet.GROUPBBHL_SHARE).setDesc(ERMWebConst.FIELD_ENABLE, String.valueOf(groupHlEnable));
		row.getField(IBillFieldGet.GLOBALBBHL_SHARE).setDesc(ERMWebConst.FIELD_ENABLE, String.valueOf(globalHlEnable));
		// 设置金额精度
		int ybDigit = Currency.getCurrDigit(bzbm_share);// 原币精度
		int bbDigit = Currency.getCurrDigit(orgLocalCurrPK);// 组织本币精度
		int groupbbDigit = Currency.getCurrDigit(groupCurrPK);// 集团本币精度
		int globalbbDigit = Currency.getCurrDigit(globalCurrPk);// 全局本币精度
		setBodyItemDigit(row, CShareDetailVO.getYbjeField(), ybDigit);
		setBodyItemDigit(row, CShareDetailVO.getBodyOrgBbjeField(), bbDigit);
		setBodyItemDigit(row, CShareDetailVO.getBodyGroupBbjeField(), groupbbDigit);
		setBodyItemDigit(row, CShareDetailVO.getBodyGlobalBbjeField(), globalbbDigit);
	}

	/**
	 * 表体单行计算，根据原币金额计算本币金额
	 * 
	 * @param dtEvent
	 * @throws Exception
	 */
	public static void calBbOnRow(String pk_group, String pk_org, UFDate djrq, Row row) throws Exception {
		String bzbm_busitem = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.BZBM_BUSITEM));// 表体币种
		// 查询表体币种对应的集团本币汇率和全局本币汇率
		UFDouble[] hlArr = ErmBillCalUtil.getRate(bzbm_busitem, pk_org, pk_group, djrq);
		// ERMDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BBHL_BUSITEM,
		// hlArr[0]);// 重新设置表体汇率
		UFDouble hl_busitem = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.BBHL_BUSITEM);// 表体汇率
		UFDouble grouphl_busitem = hlArr[1];// 集团本币汇率
		UFDouble globalhl_busitem = hlArr[2];// 全局本币汇率
		ERMDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPBBHL_BUSITEM, grouphl_busitem);// 重新设置表体集团汇率
		ERMDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALBBHL_BUSITEM, globalhl_busitem);// 重新设置表体全局汇率
		UFDouble ybje = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.YBJE_BUSITEM) == null ? UFDouble.ZERO_DBL
				: (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.YBJE_BUSITEM);// 原币金额
		UFDouble cjkybje = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.CJKYBJE_BUSITEM) == null ? UFDouble.ZERO_DBL
				: (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.CJKYBJE_BUSITEM);// 冲借款原币金额
		UFDouble hkybje = UFDouble.ZERO_DBL;// 还款原币金额
		UFDouble zfybje = UFDouble.ZERO_DBL;// 支付原币金额
		//启用了营改增liuyangs
		if(row.getField(IBillFieldGet.VAT_AMOUNT_BUSITEM) != null&&row.getField(IBillFieldGet.VAT_AMOUNT_BUSITEM).getValue()!=null) {
			UFDouble vat_amount = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.VAT_AMOUNT_BUSITEM);// 含税原币金额
			if (vat_amount.compareTo(cjkybje) > 0) {
				hkybje = UFDouble.ZERO_DBL;
				zfybje = vat_amount.sub(cjkybje);
			} else {
				hkybje = cjkybje.sub(vat_amount);
				zfybje = UFDouble.ZERO_DBL;
			}
		}else{
			if (ybje.compareTo(cjkybje) > 0) {
				hkybje = UFDouble.ZERO_DBL;
				zfybje = ybje.sub(cjkybje);
			} else {
				hkybje = cjkybje.sub(ybje);
				zfybje = UFDouble.ZERO_DBL;
			}
		}
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.HKYBJE_BUSITEM, hkybje);
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.ZFYBJE_BUSITEM, zfybje);
		// 根据原币金额计算本币金额
		UFDouble[] bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr, bzbm_busitem, ybje, null, null, null,
				hl_busitem, djrq);// 本币金额
		UFDouble[] ybggBbje = Currency.computeGroupGlobalAmount(bbje[0], bbje[2], bzbm_busitem, djrq, pk_org, pk_group,
				globalhl_busitem, grouphl_busitem);// 集团和全局本币金额
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BBJE_BUSITEM, bbje[2]);
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BBYE_BUSITEM, bbje[2]);
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPBBJE_BUSITEM, ybggBbje[0]);
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPBBYE_BUSITEM, ybggBbje[0]);
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALBBJE_BUSITEM, ybggBbje[1]);
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALBBYE_BUSITEM, ybggBbje[1]);
		UFDouble[] cjkbbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr, bzbm_busitem, cjkybje, null, null,
				null, hl_busitem, djrq);// 冲借款本币金额
		UFDouble[] cjkggBbje = Currency.computeGroupGlobalAmount(cjkbbje[0], cjkbbje[2], bzbm_busitem, djrq, pk_org,
				pk_group, globalhl_busitem, grouphl_busitem);// 集团和全局冲借款本币金额
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.CJKBBJE_BUSITEM, cjkbbje[2]);
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPCJKBBJE_BUSITEM, cjkggBbje[0]);
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALCJKBBJE_BUSITEM, cjkggBbje[1]);
		UFDouble[] hkbbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr, bzbm_busitem, hkybje, null, null, null,
				hl_busitem, djrq);// 还款本币金额
		UFDouble[] hkggBbje = Currency.computeGroupGlobalAmount(hkbbje[0], hkbbje[2], bzbm_busitem, djrq, pk_org,
				pk_group, globalhl_busitem, grouphl_busitem);// 集团和全局还款本币金额
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.HKBBJE_BUSITEM, hkbbje[2]);
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPHKBBJE_BUSITEM, hkggBbje[0]);
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALHKBBJE_BUSITEM, hkggBbje[1]);
		UFDouble[] zfbbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr, bzbm_busitem, zfybje, null, null, null,
				hl_busitem, djrq);// 支付本币金额
		UFDouble[] zfggBbje = Currency.computeGroupGlobalAmount(zfbbje[0], zfbbje[2], bzbm_busitem, djrq, pk_org,
				pk_group, globalhl_busitem, grouphl_busitem);// 集团和全局支付本币金额
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.ZFBBJE_BUSITEM, zfbbje[2]);
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPZFBBJE_BUSITEM, zfggBbje[0]);
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALZFBBJE_BUSITEM, zfggBbje[1]);
	}

	/**
	 * 表体单行计算，根据原币金额计算本币金额
	 * 
	 * @param dtEvent
	 * @throws Exception
	 */
	public static void calBbOnRow4Share(String pk_group, String pk_org, UFDate djrq, Row row) throws Exception {
		String bzbm_share = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.BZBM_SHARE));// 表体币种
		// 查询表体币种对应的集团本币汇率和全局本币汇率
		UFDouble[] hlArr = ErmBillCalUtil.getRate(bzbm_share, pk_org, pk_group, djrq);
		UFDouble hl_share = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.BBHL_SHARE);// 表体汇率
		UFDouble grouphl_share = hlArr[1];// 集团本币汇率
		UFDouble globalhl_share = hlArr[2];// 全局本币汇率
		UFDouble ybje = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.ASSUME_AMOUNT_SHARE) == null ? UFDouble.ZERO_DBL
				: (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.ASSUME_AMOUNT_SHARE);// 原币金额
		// 根据原币金额计算本币金额
		UFDouble[] bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr, bzbm_share, ybje, null, null, null,
				hl_share, djrq);// 本币金额
		UFDouble[] ybggBbje = Currency.computeGroupGlobalAmount(bbje[0], bbje[2], bzbm_share, djrq, pk_org, pk_group,
				globalhl_share, grouphl_share);// 集团和全局本币金额
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BBJE_SHARE, bbje[2]);
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPBBJE_SHARE, ybggBbje[0]);
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALBBJE_SHARE, ybggBbje[1]);
	}

	private static void calTaxOnRow(String pk_group, String pk_org, UFDate djrq, Row row, int calType) throws Exception {
		// 税金计算
		String bzbm_busitem = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.BZBM_BUSITEM));// 表体币种
		if (bzbm_busitem == null) {
			ExceptionUtils.wrappBusinessException("表体币种为空！");
		}
		UFDouble tax_rate = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.TAX_RATE_BUSITEM);// 税率
		UFDouble[] hlArr = new UFDouble[3];// 汇率从页面取
		hlArr[0] = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.BBHL_BUSITEM);// 组织本币汇率
		hlArr[1] = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.GROUPBBHL_BUSITEM);// 集团本币汇率
		hlArr[2] = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.GLOBALBBHL_BUSITEM);// 全局本币汇率
		UFDouble hsybje = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.VAT_AMOUNT_BUSITEM);
		UFDouble wsybje = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.TNI_AMOUNT_BUSITEM);
		UFDouble ybse = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.TAX_AMOUNT_BUSITEM);
		ERMTaxCalVO taxCalVO = new ERMTaxCalVO();
		if (null == tax_rate) {
			taxCalVO = ERMTaxUtil.getTaxDataNoTaxRate(pk_group, pk_org, djrq, bzbm_busitem, hsybje, wsybje, ybse,
					hlArr, calType);
		} else {
			UFDouble je = UFDouble.ZERO_DBL;
			if (calType == ERMTaxUtil.CAL_TYPE_FROM_HS) {
				je = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.VAT_AMOUNT_BUSITEM);
			} else if (calType == ERMTaxUtil.CAL_TYPE_FROM_WS) {
				je = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.TNI_AMOUNT_BUSITEM);
			} else if (calType == ERMTaxUtil.CAL_TYPE_FROM_SE) {
				je = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.TAX_AMOUNT_BUSITEM);
			}
			taxCalVO = ERMTaxUtil.getTaxData(pk_group, pk_org, djrq, bzbm_busitem, je, hsybje, ConstantUtil.outTaxtype,
					tax_rate, hlArr, ERMBillVOUtil.getDigitArr(pk_group, pk_org, bzbm_busitem), calType);
		}
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.TAX_AMOUNT_BUSITEM, taxCalVO.getTax_amount());
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.ORGTAX_AMOUNT_BUSITEM, taxCalVO.getOrgtax_amount());
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPTAX_AMOUNT_BUSITEM, taxCalVO.getGrouptax_amount());
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALTAX_AMOUNT_BUSITEM, taxCalVO.getGlobaltax_amount());
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.TNI_AMOUNT_BUSITEM, taxCalVO.getTni_amount());
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.ORGTNI_AMOUNT_BUSITEM, taxCalVO.getOrgtni_amount());
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPTNI_AMOUNT_BUSITEM, taxCalVO.getGrouptni_amount());
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALTNI_AMOUNT_BUSITEM, taxCalVO.getGlobaltni_amount());
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.VAT_AMOUNT_BUSITEM, taxCalVO.getVat_amount());
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.ORGVAT_AMOUNT_BUSITEM, taxCalVO.getOrgvat_amount());
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPVAT_AMOUNT_BUSITEM, taxCalVO.getGroupvat_amount());
		FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALVAT_AMOUNT_BUSITEM, taxCalVO.getGlobalvat_amount());
	}

	private static Boolean param_is_include_tax = null;

	private static Boolean getParam_is_include_tax(String pk_org) throws BusinessException {
		try {
			param_is_include_tax = SysInit.getParaBoolean(pk_org, BXParamConstant.PARAM_IS_INCLUDE_TAX).booleanValue();
		} catch (Exception e) {
			param_is_include_tax = Boolean.TRUE;
		}
		return param_is_include_tax;
	}

	private static void setAmountAndYbjeByTax(String pk_org, Row row) throws BusinessException {
		if (getParam_is_include_tax(pk_org)) {
			// 将含税金额赋值给amount和ybje
			FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.AMOUNT_BUSITEM,
					(UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.VAT_AMOUNT_BUSITEM));
			FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.YBJE_BUSITEM,
					(UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.VAT_AMOUNT_BUSITEM));
		} else {
			// 将无税金额赋值给amount和ybje
			FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.AMOUNT_BUSITEM,
					(UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.TNI_AMOUNT_BUSITEM));
			FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.YBJE_BUSITEM,
					(UFDouble) ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.TNI_AMOUNT_BUSITEM));
		}
	}

	private static String groupMod = null;
	private static String globalMod = null;

	/**
	 * 取汇率可编辑性
	 * 
	 * @param pk_group
	 * @param bzbm
	 * @param orgLocalCurrPK
	 * @param groupCurrPK
	 * @param globalCurrPk
	 * @return
	 * @throws BusinessException
	 */
	public static boolean[] getHLEnableArr(String pk_group, String bzbm, String orgLocalCurrPK, String groupCurrPK,
			String globalCurrPk) throws BusinessException {
		boolean hlEnable = true;
		boolean groupHlEnable = true;
		boolean globalHlEnable = true;
		if (bzbm.equals(orgLocalCurrPK)) {
			hlEnable = false;
		}
		if (null == groupMod) {
			groupMod = SysInitQuery.getParaString(pk_group, "NC001");
		}
		if (BXConstans.GROUP_DISABLE.equals(groupMod)) {
			// 不启用，则不可编辑
			groupHlEnable = false;
		} else {
			// 集团本币是否基于原币计算
			boolean isGroupByCurrtype = BXConstans.BaseOriginal.equals(groupMod);
			if (isGroupByCurrtype) {
				// 原币和集团本币相同
				if (bzbm.equals(groupCurrPK)) {
					groupHlEnable = false;
				}
			} else {
				if (orgLocalCurrPK.equals(groupCurrPK)) {
					groupHlEnable = false;
				}
			}
		}
		if (null == globalMod) {
			globalMod = SysInitQuery.getParaString("GLOBLE00000000000000", "NC002");
		}
		if (BXConstans.GLOBAL_DISABLE.equals(globalMod)) {
			// 不启用，则不可编辑
			globalHlEnable = false;
		} else {
			// 全局本币是否基于原币计算
			boolean isGlobalByCurrtype = BXConstans.BaseOriginal.equals(globalMod);
			if (isGlobalByCurrtype) {
				// 全局本币和原币相同
				if (bzbm.equals(globalCurrPk)) {
					globalHlEnable = false;
				}
			} else {
				if (orgLocalCurrPK != null && orgLocalCurrPK.equals(globalCurrPk)) {
					globalHlEnable = false;
				}
			}
		}
		return new boolean[] { hlEnable, groupHlEnable, globalHlEnable };
	}

	/**
	 * 根据表体原币金额更新其他金额字段
	 * 
	 * @param hTable
	 * @param row
	 * @param ybje
	 * @throws Exception
	 */
	// public static void changeBodyByYBJE(DataTable hTable, Row row) throws
	// Exception {
	// UFDouble ybje = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row,
	// IBillFieldGet.YBJE_BUSITEM);// 原币金额
	// String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable,
	// IBillFieldGet.PK_ORG));// 组织
	// String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable,
	// IBillFieldGet.PK_GROUP));// 集团
	// UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable,
	// IBillFieldGet.DJRQ);// 单据日期
	// String bzbm_busitem =
	// Func.toString(ERMDataTableUtil.getBodyValueByRow(row,
	// IBillFieldGet.BZBM_BUSITEM));// 表体币种
	// // 查询表体币种对应的集团本币汇率和全局本币汇率
	// UFDouble[] hlArr = ErmBillCalUtil.getRate(bzbm_busitem, pk_org, pk_group,
	// djrq);
	// UFDouble hl_busitem = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row,
	// IBillFieldGet.BBHL_BUSITEM);// 表体汇率
	// UFDouble grouphl_busitem = hlArr[1];// 集团本币汇率
	// UFDouble globalhl_busitem = hlArr[2];// 全局本币汇率
	// // 根据原币金额计算本币金额
	// UFDouble[] bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr,
	// bzbm_busitem, ybje, null, null, null,
	// hl_busitem, djrq);// 本币金额
	// UFDouble[] ybggBbje = Currency.computeGroupGlobalAmount(bbje[0], bbje[2],
	// bzbm_busitem, djrq, pk_org, pk_group,
	// globalhl_busitem, grouphl_busitem);// 集团和全局本币金额
	// FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BBJE_BUSITEM,
	// bbje[2]);
	// FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BBYE_BUSITEM,
	// bbje[2]);
	// FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPBBJE_BUSITEM,
	// ybggBbje[0]);
	// FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPBBYE_BUSITEM,
	// ybggBbje[0]);
	// FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALBBJE_BUSITEM,
	// ybggBbje[1]);
	// FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALBBYE_BUSITEM,
	// ybggBbje[1]);
	// UFDouble zfybje = ybje;// 支付原币金额
	// UFDouble[] zfbbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr,
	// bzbm_busitem, zfybje, null, null, null,
	// hl_busitem, djrq);// 支付本币金额
	// UFDouble[] zfggBbje = Currency.computeGroupGlobalAmount(zfbbje[0],
	// zfbbje[2], bzbm_busitem, djrq, pk_org,
	// pk_group, globalhl_busitem, grouphl_busitem);// 集团和全局支付本币金额
	// FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.ZFYBJE_BUSITEM,
	// zfybje);
	// FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.ZFBBJE_BUSITEM,
	// zfbbje[2]);
	// FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPZFBBJE_BUSITEM,
	// zfggBbje[0]);
	// FIDataTableUtil.setBodyValueByRow(row,
	// IBillFieldGet.GLOBALZFBBJE_BUSITEM, zfggBbje[1]);
	// }

	// public static void changeBodyByYBJE4Share(DataTable hTable, Row row)
	// throws Exception {
	// UFDouble ybje = (UFDouble)ERMDataTableUtil.getBodyValueByRow(row,
	// IBillFieldGet.ASSUME_AMOUNT_SHARE);//原币金额
	// String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable,
	// IBillFieldGet.PK_ORG));//组织
	// String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable,
	// IBillFieldGet.PK_GROUP));//集团
	// UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable,
	// IBillFieldGet.DJRQ);//单据日期
	// String bzbm_share = Func.toString(ERMDataTableUtil.getBodyValueByRow(row,
	// IBillFieldGet.BZBM_SHARE));//表体币种
	// //查询表体币种对应的集团本币汇率和全局本币汇率
	// UFDouble[] hlArr = ErmBillCalUtil.getRate(bzbm_share, pk_org,pk_group,
	// djrq);
	// UFDouble hl_share = (UFDouble)ERMDataTableUtil.getBodyValueByRow(row,
	// IBillFieldGet.BBHL_BUSITEM);//表体汇率
	// UFDouble grouphl_share = hlArr[1];//集团本币汇率
	// UFDouble globalhl_share = hlArr[2];//全局本币汇率
	// //根据原币金额计算本币金额
	// UFDouble[] bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr,
	// bzbm_share, ybje, null, null, null, hl_share, djrq);//本币金额
	// UFDouble[] ybggBbje = Currency.computeGroupGlobalAmount(bbje[0], bbje[2],
	// bzbm_share, djrq, pk_org, pk_group, globalhl_share,
	// grouphl_share);//集团和全局本币金额
	// FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BBJE_SHARE,
	// bbje[2]);
	// FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPBBJE_SHARE,
	// ybggBbje[0]);
	// FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALBBJE_SHARE,
	// ybggBbje[1]);
	// }

	/**
	 * 表头借款报销人部门（最新版本）编辑后处理
	 * 
	 * @param hTable
	 * @param bTable
	 * @throws Exception
	 */
	public static void afterEditDeptid_v(DataTable hTable, DataTable[] bTableArr, DataTable shareTable)
			throws Exception {
		String className = MetaDataProcessUtil.getClassname(hTable.getCls());
		// 借款报销单表体没有部门的版本字段，不用同步
		Object deptid_v = FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.DEPTID_V);
		// 根据deptid_v取deptid，给表头字段赋值
		Object deptid = MetaDataProcessUtil.getRelationItemValue(className, IBillFieldGet.DEPTID_V, "pk_dept.pk_dept",
				deptid_v);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.DEPTID, deptid);
		Map<String, String> clientAttrs = IWebViewContext.getRequest().getEnvironment().getClientAttributes();
		String pk_tradetype = clientAttrs.get(ERMWebConst.TRADETYPE);
		// 表体deptid字段赋值
		if (ERMValueCheck.isNotEmpty(pk_tradetype)) {
			for (DataTable bTable : bTableArr) {
				FIDataTableUtil.dealCardRelationItem(bTable, IBillFieldGet.DEPTID_BUSITEM, deptid);
			}
		}
		// 表头报销人部门修改后清空报销人
		FIDataTableUtil.setHeadValue(hTable, JKBXHeaderVO.JKBXR, null);
		afterEditJKBXR(hTable, bTableArr, shareTable);
	}

	public static void afterEditDeptid(DataTable hTable, DataTable[] bTableArr, DataTable shareTable) throws Exception {
		String className = MetaDataProcessUtil.getClassname(hTable.getCls());
		// 借款报销单表体没有部门的版本字段，不用同步
		Object deptid = FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.DEPTID);
		HashMap<String, String> newVIDSByOrgIDS = NCLocator.getInstance().lookup(IBasicOrgUnitPubService_C.class)
				.getNewVIDSByOrgIDS(new String[] { Func.toString(deptid) });
		String deptid_v = newVIDSByOrgIDS.get(deptid);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.DEPTID_V, deptid_v);
		Map<String, String> clientAttrs = IWebViewContext.getRequest().getEnvironment().getClientAttributes();
		String pk_tradetype = clientAttrs.get(ERMWebConst.TRADETYPE);
		// 表体deptid字段赋值
		if (ERMValueCheck.isNotEmpty(pk_tradetype)) {
			for (DataTable bTable : bTableArr) {
				FIDataTableUtil.dealCardRelationItem(bTable, IBillFieldGet.DEPTID_BUSITEM, deptid);
			}
		}
		// 表头报销人部门修改后清空报销人
		FIDataTableUtil.setHeadValue(hTable, JKBXHeaderVO.JKBXR, null);
		afterEditJKBXR(hTable, bTableArr, shareTable);
	}

	/**
	 * 表头费用承担部门（最新版本）编辑后处理
	 * 
	 * @param hTable
	 * @param bTable
	 * @throws Exception
	 */
	public static void afterEditFydeptid_v(DataTable hTable, DataTable[] bTableArr, DataTable shareTable)
			throws Exception {
		String className = MetaDataProcessUtil.getClassname(hTable.getCls());
		Object deptid_v = FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.FYDEPTID_V);
		// 根据deptid_v取deptid，给表头字段赋值
		Object deptid = MetaDataProcessUtil.getRelationItemValue(className, IBillFieldGet.FYDEPTID_V,
				"pk_dept.pk_dept", deptid_v);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.FYDEPTID, deptid);
		if (ERMValueCheck.isNotEmpty(shareTable)) {
			FIDataTableUtil.dealCardRelationItem(shareTable, IBillFieldGet.ASSUME_DEPT_SHARE, deptid);
		}
	}

	/**
	 * 表头费用承担部门（原始版本）编辑后处理
	 * 
	 * @param hTable
	 * @param bTable
	 * @throws Exception
	 */
	public static void afterEditFydeptid(DataTable hTable, DataTable[] bTableArr, DataTable shareTable)
			throws Exception {
		Object deptid = FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.FYDEPTID);
		HashMap<String, String> newVIDSByOrgIDS = NCLocator.getInstance().lookup(IBasicOrgUnitPubService_C.class)
				.getNewVIDSByOrgIDS(new String[] { Func.toString(deptid) });
		String deptid_v = newVIDSByOrgIDS.get(deptid);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.FYDEPTID_V, deptid_v);
		if (ERMValueCheck.isNotEmpty(shareTable)) {
			FIDataTableUtil.dealCardRelationItem(shareTable, IBillFieldGet.ASSUME_DEPT_SHARE, deptid);
		}
	}

	/**
	 * 表头借款报销人单位（最新编码）编辑后处理
	 * 
	 * @param hTable
	 * @param bTable
	 * @throws Exception
	 */
	public static void afterEditDwbm_v(DataTable hTable, DataTable[] bTableArr, DataTable shareTable) throws Exception {
		List<DataTable> allDataTableList = new ArrayList<DataTable>();
		allDataTableList.add(hTable);
		String className = MetaDataProcessUtil.getClassname(hTable.getCls());
		String dwbm_v = Func.toString(FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.DWBM_V));
		String dwbm = Func.toString(MetaDataProcessUtil.getRelationItemValue(className, IBillFieldGet.DWBM_V,
				"pk_adminorg", dwbm_v));// 借款报销人单位是行政组织
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.DWBM, dwbm);
		Map<String, String> clientAttrs = IWebViewContext.getRequest().getEnvironment().getClientAttributes();
		String pk_tradetype = clientAttrs.get(ERMWebConst.TRADETYPE);
		for (DataTable bTable : bTableArr) {
			allDataTableList.add(bTable);
			if (ERMValueCheck.isNotEmpty(pk_tradetype)) {
				FIDataTableUtil.dealCardRelationItem(bTable, IBillFieldGet.DWBM_BUSITEM, dwbm);
			}
		}
		// 借款报销人单位修改后清空相关字段
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.DEPTID_V, null);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.JKBXR, null);
		afterEditDeptid_v(hTable, bTableArr, shareTable);
		afterEditJKBXR(hTable, bTableArr, shareTable);
		// 借款报销人单位修改后设置参照
		ERMRefUtil.dealDeptField(allDataTableList.toArray(new DataTable[allDataTableList.size()]), new String[] {
				IBillFieldGet.DEPTID_V, IBillFieldGet.DEPTID });
		ERMRefUtil.dealPersonField(hTable, allDataTableList.toArray(new DataTable[allDataTableList.size()]),
				new String[] { IBillFieldGet.JKBXR, IBillFieldGet.RECEIVER });

	}

	/**
	 * 表头借款报销人单位（最新编码）编辑后处理
	 * 
	 * @param hTable
	 * @param bTable
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void afterEditDwbm(DataTable hTable, DataTable[] bTableArr, DataTable shareTable) throws Exception {
		/*
		 * 获取不带_V的dwbm，依据dwbm获取dwbm_v
		 */
		List<DataTable> allDataTableList = new ArrayList<DataTable>();
		allDataTableList.add(hTable);
		String dwbm = Func.toString(FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.DWBM));
		HashMap<String, String> newVIDSByOrgIDS = NCLocator.getInstance().lookup(IBasicOrgUnitPubService_C.class)
				.getNewVIDSByOrgIDS(new String[] { dwbm });
		String dwbm_v = newVIDSByOrgIDS.get(dwbm);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.DWBM_V, dwbm_v);
		Map<String, String> clientAttrs = IWebViewContext.getRequest().getEnvironment().getClientAttributes();
		String pk_tradetype = clientAttrs.get(ERMWebConst.TRADETYPE);
		for (DataTable bTable : bTableArr) {
			allDataTableList.add(bTable);
			if (ERMValueCheck.isNotEmpty(pk_tradetype)) {
				FIDataTableUtil.dealCardRelationItem(bTable, IBillFieldGet.DWBM_BUSITEM, dwbm);
			}
		}
		// 借款报销人单位修改后清空相关字段
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.DEPTID_V, null);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.JKBXR, null);
		afterEditDeptid_v(hTable, bTableArr, shareTable);
		afterEditJKBXR(hTable, bTableArr, shareTable);
		// 借款报销人单位修改后设置参照
		ERMRefUtil.dealDeptField(allDataTableList.toArray(new DataTable[allDataTableList.size()]), new String[] {
				IBillFieldGet.DEPTID_V, IBillFieldGet.DEPTID });
		ERMRefUtil.dealPersonField(hTable, allDataTableList.toArray(new DataTable[allDataTableList.size()]),
				new String[] { IBillFieldGet.JKBXR, IBillFieldGet.RECEIVER });

	}

	/**
	 * 表头费用承担单位（最新编码）编辑后处理
	 * 
	 * @param hTable
	 * @param bTable
	 * @throws Exception
	 */
	public static void afterEditFYDWBM_V(DataTable hTable, DataTable[] bTableArr, DataTable shareTable)
			throws Exception {
		List<DataTable> bTableList = new ArrayList<DataTable>();
		bTableList.add(hTable);
		for (DataTable bTable : bTableArr) {
			bTableList.add(bTable);
		}
		String tradetype = Func.toString(FIDataTableUtil.getHeadValue(hTable, JKBXHeaderVO.DJLXBM));
		// 处理费用承担单位
		String className = MetaDataProcessUtil.getClassname(hTable.getCls());
		Object dwbm_v = FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.FYDWBM_V);
		Object dwbm = MetaDataProcessUtil.getRelationItemValue(className, IBillFieldGet.FYDWBM_V, "pk_org", dwbm_v);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.FYDWBM, dwbm);
		if (ERMValueCheck.isNotEmpty(shareTable)) {
			FIDataTableUtil.dealCardRelationItem(shareTable, IBillFieldGet.ASSUME_ORG_SHARE, dwbm);
		}
		// 处理费用承担部门
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.FYDEPTID_V, null);// 清空表头
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.FYDEPTID, null);
		afterEditFydeptid_v(hTable, bTableArr, shareTable);// 清空表体分摊页签
		ERMRefUtil.dealDeptField(new DataTable[] { hTable }, new String[] { IBillFieldGet.FYDEPTID_V,
				IBillFieldGet.FYDEPTID });// 处理表头参照
		if (ERMValueCheck.isNotEmpty(shareTable)) {
			ERMRefUtil.dealDeptField(new DataTable[] { shareTable }, new String[] { IBillFieldGet.ASSUME_DEPT_SHARE });// 处理表体参照
		}
		// 处理收支项目
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.SZXMID, null);// 清空表头
		for (DataTable bTable : bTableArr) {
			FIDataTableUtil.dealCardRelationItem(bTable, IBillFieldGet.SZXMID, null);// 清空表体业务页签
		}
		ERMRefUtil.dealSzxmFiled(hTable, bTableList.toArray(new DataTable[bTableList.size()]),
				new String[] { IBillFieldGet.SZXMID }, tradetype, "" + dwbm);// 处理表头表体业务页签参照
		if (ERMValueCheck.isNotEmpty(shareTable)) {
			FIDataTableUtil.dealCardRelationItem(shareTable, IBillFieldGet.SZXMID, null);// 清空表体分摊页签
			ERMRefUtil.dealShareSzxmFiled(hTable, new DataTable[] { shareTable },
					new String[] { IBillFieldGet.PK_IOBSCLASS_SHARE }, tradetype, "" + dwbm);// 处理表体分摊页签参照
		}
		// 处理项目
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.JOBID, null);
		for (DataTable bTable : bTableArr) {
			FIDataTableUtil.dealCardRelationItem(bTable, IBillFieldGet.JOBID, null);// 清空表体业务页签
		}
		ERMRefUtil.dealSzxmFiled(hTable, bTableList.toArray(new DataTable[bTableList.size()]),
				new String[] { IBillFieldGet.JOBID }, tradetype, "" + dwbm);// 处理表头表体业务页签参照
		if (ERMValueCheck.isNotEmpty(shareTable)) {
			FIDataTableUtil.dealCardRelationItem(shareTable, IBillFieldGet.JOBID, null);// 清空表体分摊页签
			ERMRefUtil.dealShareSzxmFiled(hTable, new DataTable[] { shareTable },
					new String[] { IBillFieldGet.JOBID_SHARE }, tradetype, "" + dwbm);// 处理表体分摊页签参照
		}
		//处理供应商
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.HBBM, null);
		ERMRefUtil.dealHbbmFiled(hTable, IBillFieldGet.HBBM,Func.toString(dwbm));
		for (DataTable bTable : bTableArr) {
			ERMRefUtil.dealHbbmFiled(bTable, IBillFieldGet.HBBM,Func.toString(dwbm));
		}
		afterEditHeadHBBM(hTable, bTableArr, shareTable);
		//处理客户
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.CUSTOMER, null);
		ERMRefUtil.dealCustomerFiled(hTable, IBillFieldGet.CUSTOMER,Func.toString(dwbm));
		for (DataTable bTable : bTableArr) {
			ERMRefUtil.dealCustomerFiled(bTable, IBillFieldGet.CUSTOMER,Func.toString(dwbm));
		}
		afterEditHeadCUSTOMER(hTable, bTableArr, shareTable);
	}

	/**
	 * 表头费用承担单位（不带_V）编辑后处理
	 * 
	 * @param hTable
	 * @param bTableArr
	 * @param shareTable
	 * @throws Exception
	 */
	public static void afterEditFYDWBM(DataTable hTable, DataTable[] bTableArr, DataTable shareTable) throws Exception {
		List<DataTable> bTableList = new ArrayList<DataTable>();
		bTableList.add(hTable);
		for (DataTable bTable : bTableArr) {
			bTableList.add(bTable);
		}
		String tradetype = Func.toString(FIDataTableUtil.getHeadValue(hTable, JKBXHeaderVO.DJLXBM));
		// 处理费用承担单位
		String className = MetaDataProcessUtil.getClassname(hTable.getCls());
		Object dwbm = FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.FYDWBM);// 费用承担单位
		Object dwbm_v = MetaDataProcessUtil.getRelationItemValue(className, IBillFieldGet.FYDWBM, "pk_vid", dwbm);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.FYDWBM_V, dwbm_v);
		// 处理费用承担部门
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.FYDEPTID_V, null);// 清空表头
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.FYDEPTID, null);
		afterEditFydeptid(hTable, bTableArr, shareTable);// 清空表体分摊页签
		ERMRefUtil.dealDeptField(new DataTable[] { hTable }, new String[] { IBillFieldGet.FYDEPTID_V,
				IBillFieldGet.FYDEPTID });// 处理表头参照
		if (ERMValueCheck.isNotEmpty(shareTable)) {
			ERMRefUtil.dealDeptField(new DataTable[] { shareTable }, new String[] { IBillFieldGet.ASSUME_DEPT_SHARE });// 处理表体参照
		}
		// 处理收支项目
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.SZXMID, null);// 清空表头
		for (DataTable bTable : bTableArr) {
			FIDataTableUtil.dealCardRelationItem(bTable, IBillFieldGet.SZXMID, null);// 清空表体业务页签
		}
		ERMRefUtil.dealSzxmFiled(hTable, bTableList.toArray(new DataTable[bTableList.size()]),
				new String[] { IBillFieldGet.SZXMID }, tradetype, "" + dwbm);// 处理表头表体业务页签参照
		if (ERMValueCheck.isNotEmpty(shareTable)) {
			FIDataTableUtil.dealCardRelationItem(shareTable, IBillFieldGet.SZXMID, null);// 清空表体分摊页签
			ERMRefUtil.dealShareSzxmFiled(hTable, new DataTable[] { shareTable },
					new String[] { IBillFieldGet.PK_IOBSCLASS_SHARE }, tradetype, "" + dwbm);// 处理表体分摊页签参照
		}
		// 处理项目
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.JOBID, null);
		for (DataTable bTable : bTableArr) {
			FIDataTableUtil.dealCardRelationItem(bTable, IBillFieldGet.JOBID, null);// 清空表体业务页签
		}
		ERMRefUtil.dealSzxmFiled(hTable, bTableList.toArray(new DataTable[bTableList.size()]),
				new String[] { IBillFieldGet.JOBID }, tradetype, "" + dwbm);// 处理表头表体业务页签参照
		if (ERMValueCheck.isNotEmpty(shareTable)) {
			FIDataTableUtil.dealCardRelationItem(shareTable, IBillFieldGet.JOBID, null);// 清空表体分摊页签
			ERMRefUtil.dealShareSzxmFiled(hTable, new DataTable[] { shareTable },
					new String[] { IBillFieldGet.JOBID_SHARE }, tradetype, "" + dwbm);// 处理表体分摊页签参照
		}
		//处理供应商
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.HBBM, null);
		ERMRefUtil.dealHbbmFiled(hTable, IBillFieldGet.HBBM,Func.toString(dwbm));
		for (DataTable bTable : bTableArr) {
			ERMRefUtil.dealHbbmFiled(bTable, IBillFieldGet.HBBM,Func.toString(dwbm));
		}
		afterEditHeadHBBM(hTable, bTableArr, shareTable);
		//处理客户
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.CUSTOMER, null);
		ERMRefUtil.dealCustomerFiled(hTable, IBillFieldGet.CUSTOMER,Func.toString(dwbm));
		for (DataTable bTable : bTableArr) {
			ERMRefUtil.dealCustomerFiled(bTable, IBillFieldGet.CUSTOMER,Func.toString(dwbm));
		}
		afterEditHeadCUSTOMER(hTable, bTableArr, shareTable);
		
	}

	/**
	 * 表头支付单位（最新编码）编辑后处理
	 * 
	 * @param hTable
	 * @param bTable
	 * @throws Exception
	 */
	public static void afterEditPK_PAYORG_V(DataTable hTable, DataTable[] bTableArr) throws Exception {
		String className = MetaDataProcessUtil.getClassname(hTable.getCls());
		Object dwbm_v = FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_PAYORG_V);
		Object dwbm = MetaDataProcessUtil.getRelationItemValue(className, IBillFieldGet.PK_PAYORG_V, "pk_financeorg",
				dwbm_v);// 支付单位是财务组织
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.PK_PAYORG, dwbm);
		// 清空单位银行账户
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.FKYHZH, null);
		//清空资金计划项目和现金流量项目
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.CASHITEM, null);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.CASHPROJ, null);
		String bzbm = Func.toString(FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.BZBM));
		// 币种重设单位银行账户参照
		setRefParamToFKYHZH(hTable, dwbm, bzbm);
		//资金计划项目，现金流量项目重设参照
		setRefParamToCashItemProj(hTable, dwbm);
		// 支付单位联动现金账户
		JSONObject refParam = new JSONObject();
		refParam.put("pk_org", dwbm);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.PK_CASHACCOUNT, null);
		FIDataTableUtil.setDataTableRefParam(hTable, IBillFieldGet.PK_CASHACCOUNT, refParam);

	}

	/**
	 * 表头支付单位（原始版本）编辑后处理
	 * 
	 * @param hTable
	 * @param bTable
	 * @throws Exception
	 */
	public static void afterEditPK_PAYORG(DataTable hTable, DataTable[] bTableArr) throws Exception {
		Object payorg = FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_PAYORG);
		HashMap<String, String> newVIDSByOrgIDS = NCLocator.getInstance().lookup(IBasicOrgUnitPubService_C.class)
				.getNewVIDSByOrgIDS(new String[] { Func.toString(payorg) });
		String payorg_v = newVIDSByOrgIDS.get(payorg);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.PK_PAYORG_V, payorg_v);
		// 清空单位银行账户
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.FKYHZH, null);
		//清空资金计划项目和现金流量项目
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.CASHITEM, null);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.CASHPROJ, null);
		String bzbm = Func.toString(FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.BZBM));
		// 支付单位联动单位银行账户
		setRefParamToFKYHZH(hTable, payorg, bzbm);
		//资金计划项目，现金流量项目重设参照
		setRefParamToCashItemProj(hTable, payorg);
		// 支付单位联动现金账户
		JSONObject refParam = new JSONObject();
		refParam.put("pk_org", payorg);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.PK_CASHACCOUNT, null);
		FIDataTableUtil.setDataTableRefParam(hTable, IBillFieldGet.PK_CASHACCOUNT, refParam);

	}

	/**
	 * 单位银行账户重设参照
	 * 
	 * @param hTable
	 * @param dwbm
	 * @param bzbm
	 * @throws Exception
	 */
	public static void setRefParamToFKYHZH(DataTable hTable, Object dwbm, String bzbm) throws Exception {
		// 支付单位联动单位银行账户
		JSONObject refParam = new JSONObject();
		refParam.put("pk_org", dwbm);
		refParam.put("pk_currtype", bzbm);
		FIDataTableUtil.setDataTableRefParam(hTable, IBillFieldGet.FKYHZH, refParam);

	}
	
	/**
	 * 资金计划项目，现金流量项目重设参照
	 * 
	 * @param hTable
	 * @param dwbm
	 * @throws Exception
	 */
	public static void setRefParamToCashItemProj(DataTable hTable, Object dwbm) throws Exception {
		// 支付单位联动单位银行账户
		JSONObject refParam = new JSONObject();
		refParam.put("pk_org", dwbm);
		FIDataTableUtil.setDataTableRefParam(hTable, IBillFieldGet.CASHITEM, refParam);
		FIDataTableUtil.setDataTableRefParam(hTable, IBillFieldGet.CASHPROJ, refParam);

	}

	/**
	 * 表头借款报销人编辑后处理
	 * 
	 * @param hTable
	 * @param bTable
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void afterEditJKBXR(DataTable hTable, DataTable[] bTableArr, DataTable shareTable) throws Exception {
		Object jkbxr = FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.JKBXR);
		if (null != jkbxr && !"".equals(jkbxr)) {// 借款报销人不为空的时候赋值收款人
			FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.RECEIVER, jkbxr);// 表头收款人赋值
			// 收款人编辑后事件
			afterEditRECEIVER(hTable, bTableArr);
		}
		Map<String, String> clientAttrs = IWebViewContext.getRequest().getEnvironment().getClientAttributes();
		String pk_tradetype = clientAttrs.get(ERMWebConst.TRADETYPE);
		if (ERMValueCheck.isNotEmpty(pk_tradetype)) {
			for (DataTable bTable : bTableArr) {
				FIDataTableUtil.dealCardRelationItem(bTable, IBillFieldGet.JKBXR_BUSITEM, jkbxr);// 表体借款报销人赋值
			}
		}
		// 依据借款报销人查询报销人部门并赋值
		String jkbxrString = Func.toString(jkbxr);
		if(ERMValueCheck.isNotEmpty(jkbxrString)){
			String deptId = getDeptidByJkbxr(jkbxrString);
			FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.DEPTID, deptId);// 表头报销人部门
			HashMap<String, String> lastVIDSByDeptIDS = NCLocator.getInstance()
					.lookup(nc.pubitf.org.IDeptPubService.class).getLastVIDSByDeptIDS(new String[] { deptId });
			String dept_vId = lastVIDSByDeptIDS.get(deptId);
			FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.DEPTID_V, dept_vId);// 表头报销人部门
		}

		afterEditFydeptid_v(hTable, bTableArr, shareTable);
		afterEditRECEIVER(hTable, bTableArr);
	}
	
	/**
	 * 根据借款报销人取部门，先按界面上传递的任职信息进行查询
	 * @param jkbxr
	 * @return
	 * @throws Exception
	 */
	private static String getDeptidByJkbxr(String jkbxr) throws Exception{
		PsnjobVO psnjobVO = null;
		String pk_psnjob = IWebViewContext.getEventParameter("pk_psnjob");//从界面上取任职信息
		if(ERMValueCheck.isNotEmpty(pk_psnjob)){
			NCObject[] objs = MDPersistenceService.lookupPersistenceQueryService().queryBillOfNCObjectByCond(PsnjobVO.class, "pk_psnjob='"+pk_psnjob+"'", false);
			if(ERMValueCheck.isNotEmpty(objs)){
				psnjobVO = (PsnjobVO)objs[0].getContainmentObject();
			}else{
				psnjobVO = NCLocator.getInstance().lookup(IPsndocQueryService.class).queryPsnJobVOByPsnDocPK(jkbxr);
			}
		}else{
			psnjobVO = NCLocator.getInstance().lookup(IPsndocQueryService.class).queryPsnJobVOByPsnDocPK(jkbxr);
		}
		return psnjobVO.getPk_dept();
	}

	/**
	 * 表头收款人编辑后处理
	 * 
	 * @param hTable
	 * @param bTable
	 * @throws Exception
	 */
	public static void afterEditRECEIVER(DataTable hTable, DataTable[] bTableArr) throws Exception {
		/*
		 * 收款人联动： 1:刷新表体收款人
		 * 2:收款人联动个人银行账户，查找收款人对应的银行行号，如果收款人为空清空个人银行账号，如果收款人对应的银行账户为空清空个人银行账户
		 * ，否则依据收款人对应的个人银行账户赋值 3:一句空收款人重设个人银行账户参照
		 */
		Object receiver = FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.RECEIVER);
		Map<String, String> clientAttrs = IWebViewContext.getRequest().getEnvironment().getClientAttributes();
		String pk_tradetype = clientAttrs.get(ERMWebConst.TRADETYPE);
		if (ERMValueCheck.isNotEmpty(pk_tradetype)) {
			for (DataTable bTable : bTableArr) {
				FIDataTableUtil.dealCardRelationItem(bTable, IBillFieldGet.RECEIVER_BUSITEM, receiver);
			}
			changeBankSkyHZH(hTable, bTableArr);// 收款人联动个人银行账户
			afterEditSKYHZH(hTable, bTableArr, null);
		}
	}

	/**
	 * 收款人联动个人银行账户
	 * 
	 * @param hTable
	 * @param bTableArr
	 * @throws Exception
	 */
	public static void changeBankSkyHZH(DataTable hTable, DataTable[] bTableArr) throws Exception {
		Object receiver = FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.RECEIVER);
		Map<String, String> clientAttrs = IWebViewContext.getRequest().getEnvironment().getClientAttributes();
		String pk_tradetype = clientAttrs.get(ERMWebConst.TRADETYPE);
		// 表头收款人编辑后联动个人银行账户,如果收款人为空则清空个人银行账户
		if (receiver != null && !"".equals(receiver)) {
			String receiverString = Func.toString(receiver);
			BankAccbasVO bank = NCLocator.getInstance().lookup(IPsnBankaccPubService.class)
					.queryDefaultBankAccByPsnDoc(receiverString);
			if (bank != null && bank.getBankaccsub() != null && bank.getEnablestate().equals(2)) {
				String pk_bankaccsub = bank.getBankaccsub()[0].getPk_bankaccsub();
				FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.SKYHZH, pk_bankaccsub);
				if (ERMValueCheck.isNotEmpty(pk_tradetype)) {
					for (DataTable busitem : bTableArr) {
						FIDataTableUtil.dealCardRelationItem(busitem, IBillFieldGet.SKYHZH_BUSITEM, null);
					}
				}
			} else {
				FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.SKYHZH, null);
				if (ERMValueCheck.isNotEmpty(pk_tradetype)) {
					for (DataTable busitem : bTableArr) {
						FIDataTableUtil.dealCardRelationItem(busitem, IBillFieldGet.SKYHZH_BUSITEM, null);
					}
				}
			}
		} else {
			FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.SKYHZH, null);
			if (ERMValueCheck.isNotEmpty(pk_tradetype)) {
				for (DataTable busitem : bTableArr) {
					FIDataTableUtil.dealCardRelationItem(busitem, IBillFieldGet.SKYHZH_BUSITEM, null);
				}
			}
		}
		// 依据收款人重设个人银行账户参照
		List<DataTable> bTableList = new ArrayList<DataTable>();
		bTableList.add(hTable);
		for (DataTable bTable : bTableArr) {
			bTableList.add(bTable);
		}
		ERMRefUtil.dealSkyhzhFiled(hTable, bTableList.toArray(new DataTable[bTableList.size()]),
				new String[] { IBillFieldGet.SKYHZH });
	}

	/**
	 * 表头收支项目编辑后处理
	 * 
	 * @param hTable
	 * @param bTable
	 * @throws Exception
	 */
	public static void afterEditSZXM(DataTable hTable, DataTable[] bTableArr, DataTable shareTable) throws Exception {
		Object szxmid = FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.SZXMID);
		for (DataTable bTable : bTableArr) {
			FIDataTableUtil.dealCardRelationItem(bTable, IBillFieldGet.SZXMID_BUSITEM, szxmid);
		}
		if (ERMValueCheck.isNotEmpty(shareTable)) {
			FIDataTableUtil.dealCardRelationItem(shareTable, IBillFieldGet.PK_IOBSCLASS_SHARE, szxmid);
		}
	}

	/**
	 * 表头个人银行账户编辑后事件
	 * 
	 * @param hTable
	 * @param bTable
	 * @throws Exception
	 */
	public static void afterEditSKYHZH(DataTable hTable, DataTable[] bTableArr, DataTable shareTable) throws Exception {
		Object skyhzh = FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.SKYHZH);
		if (skyhzh != null) {
			for (DataTable bTable : bTableArr) {
				FIDataTableUtil.dealCardRelationItem(bTable, IBillFieldGet.SKYHZH_BUSITEM, skyhzh);
			}
			if (ERMValueCheck.isNotEmpty(shareTable) && shareTable.getMeta().containsKey(IBillFieldGet.SKYHZH_BUSITEM)) {
				FIDataTableUtil.dealCardRelationItem(shareTable, IBillFieldGet.SKYHZH_BUSITEM, skyhzh);
			}
		}

	}

	/**
	 * 表头项目编辑后事件
	 * 
	 * @param hTable
	 * @param bTableArr
	 * @param shareTable
	 * @throws Exception
	 */
	public static void afterEditJOBID(DataTable hTable, DataTable[] bTableArr, DataTable shareTable) throws Exception {
		Object jobid = FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.JOBID);// 获取表头项目，依据表头刷新表体
		for (DataTable bTable : bTableArr) {
			FIDataTableUtil.dealCardRelationItem(bTable, IBillFieldGet.JOBID, jobid);
		}
		if (ERMValueCheck.isNotEmpty(shareTable) && shareTable.getMeta().containsKey(IBillFieldGet.JOBID)) {
			FIDataTableUtil.dealCardRelationItem(shareTable, IBillFieldGet.JOBID, jobid);
		}
	}
	
	/**
	 * 表头散户编辑后事件
	 * 
	 * @param hTable
	 * @param bTableArr
	 * @param shareTable
	 * @throws Exception
	 */
	public static void afterEditFREECUST(DataTable hTable, DataTable[] bTableArr) throws Exception {
		Object freecust = FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.FREECUST);// 获取表头散户，依据表头刷新表体
		String freeAccount = getFreeCustAccount(Func.toString(freecust));
		FIDataTableUtil.setHeadValue(hTable, "freecust_bankaccount", freeAccount);
		for (DataTable bTable : bTableArr) {
			Row[] rows = bTable.getAllRow();
			for (Row row : rows) {
				if (row.getFields().containsKey(IBillFieldGet.FREECUST_BUSITEM)) {
					FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.FREECUST_BUSITEM, freecust);
					afterEditBodyFREECUST(bTable, row);
				}
			}
		}

	}
	/**
	 * 表体散户编辑后事件
	 * @param bTable
	 * @param row
	 * @throws Exception
	 */
	public static void afterEditBodyFREECUST(DataTable bTable, Row row) throws Exception {
		String freecust = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.FREECUST_BUSITEM));
		String freeAccount = getFreeCustAccount(freecust);
		FIDataTableUtil.setBodyValueByRow(row, "freecust_bankaccount", freeAccount);
	}

	/**
	 * 分摊页签表体费用承担单位编辑后事件
	 * 
	 * @param hTable
	 * @param bTableArr
	 * @param shareTable
	 * @throws Exception
	 */
	public static void afterEditSHARE_ASSUME_ORG(DataTable shareTable) throws Exception {
		// 费用承担单位修改后清空费用承担部门
		FIDataTableUtil.setBodyValueByRow(shareTable.getCurrentRow(), IBillFieldGet.ASSUME_DEPT_SHARE, null);
		// 设置参照
		ERMRefUtil.dealDeptField(new DataTable[] { shareTable }, new String[] { IBillFieldGet.ASSUME_DEPT_SHARE });
	}

	/**
	 * 获取部门参照过滤条件
	 * 
	 * @param pk_org
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getDeptRefParam(String pk_org) throws Exception {
		JSONObject deptRefParam = new JSONObject();
		deptRefParam.put("pk_org", null == pk_org ? "null" : pk_org);// 主组织
		deptRefParam.put("enablestate ", "2");// 是否可用
		// TODO vstartdate条件
		return deptRefParam;
	}

	/**
	 * 批量设置表头精度
	 * 
	 * @param hTable
	 * @param fieldArr
	 * @param digit
	 */
	private static void setHeadItemDigit(DataTable hTable, String[] fieldArr, int digit) {
		for (String field : fieldArr) {
			ERMDataTableUtil.setHeadItemDigi(hTable, field, digit);
		}
	}

	/**
	 * 批量设置表体精度
	 * 
	 * @param bTable
	 * @param bodyJeKeys
	 * @param digit
	 */
	private static void setBodyItemDigit(Row row, String[] fieldArr, int digit) {
		// 表体精度
		try {
			for (String field : fieldArr) {
				if (null != row.getField(field)) {
					row.getField(field).getMeta().getDescs().put(ERMWebConst.FIELD_PRECISION, String.valueOf(digit));
				}
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * 批量设置表头默认值
	 * 
	 * @param hTable
	 * @param fieldArr
	 * @param value
	 */
	private static void setHeadItemDefaultValue(DataTable hTable, String[] fieldArr, Object value) {
		for (String field : fieldArr) {
			if (null == ERMDataTableUtil.getHeadValue(hTable, field)) {
				ERMDataTableUtil.setHeadValue(hTable, field, value);
				;
			}
		}
	}

	/**
	 * 批量设置表体默认值
	 * 
	 * @param row
	 * @param fieldArr
	 * @param value
	 */
	private static void setBodyItemDefaultValue(Row row, String[] fieldArr, Object value) {
		try {
			for (String field : fieldArr) {
				if (null != row.getField(field) && null == row.getField(field).getValue()) {
					ERMDataTableUtil.setBodyValueByRow(row, field, value);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * 批量updateMeta
	 * 
	 * @param table
	 * @param fieldArr
	 * @param descName
	 * @param descValue
	 */
	private static void updateMetaForMultiField(DataTable table, String[] fieldArr, String descName, String descValue) {
		for (String field : fieldArr) {
			table.updateMeta(field, descName, descValue);
		}
	}

	public static void afterEditPk_org_v(DataTableFieldEvent dtEvent) {
		// TODO Auto-generated method stub

	}

	public static void afterEditOrg_v(DataTableFieldEvent dtEvent) {
		// TODO Auto-generated method stub

	}

	public static void afterEditOrgField(String pkPcorg) {
		// TODO Auto-generated method stub

	}

	public static void afterEditFydeptid() {
		// TODO Auto-generated method stub

	}

	public static void afterEditDeptid() {
		// TODO Auto-generated method stub

	}

	public static void afterEditSupplier() {
		// TODO Auto-generated method stub

	}

	public static void afterEditCustomer() {
		// TODO Auto-generated method stub

	}

	public static void afterEditPayarget(boolean b) {
		// TODO Auto-generated method stub

	}

	/**
	 * add by sunjq
	 */
	/**
	 * 修改时赋值
	 * 
	 * @param hTable
	 * @param bTableArr
	 * @param pk_org
	 * @throws Exception
	 */
	public static void setDefaultValueForEditFY(DataTable hTable, DataTable[] bTableArr) throws Exception {
		String[] defaultFieldArr = new String[]{MatterAppVO.PK_IOBSCLASS, MatterAppVO.BILLMAKER, MatterAppVO.APPLY_ORG, MatterAppVO.APPLY_DEPT};
		// 获取表头币种赋给表体当前行
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.BILLDATE);// 单据日期
		String bzbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.PK_CURRTYPE));// 币种
		String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);// 组织本币
		String groupCurrPK = Currency.getGroupCurrpk(pk_group);// 集团本币
		String globalCurrPk = Currency.getGlobalCurrPk(null);// 全局本币
		UFDouble[] hlArr = ErmBillCalUtil.getRate(bzbm, pk_org, pk_group, djrq);// 取汇率
		int hlDigit = Currency.getRateDigit(pk_org, bzbm, orgLocalCurrPK);// 汇率精度
		int grouphlDigit = Currency.getGroupRateDigit(pk_org, pk_group, bzbm);// 集团汇率精度
		int globalhlDigit = Currency.getGlobalRateDigit(pk_org, bzbm);// 全局汇率精度
		// 设置金额精度
		int ybDigit = Currency.getCurrDigit(bzbm);// 原币精度
		int bbDigit = Currency.getCurrDigit(orgLocalCurrPK);// 组织本币精度
		int groupbbDigit = Currency.getCurrDigit(groupCurrPK);// 集团本币精度
		int globalbbDigit = Currency.getCurrDigit(globalCurrPk);// 全局本币精度
		// 设置汇率字段的可编辑性
		boolean[] hlEnableArr = getHLEnableArr(pk_group, bzbm, orgLocalCurrPK, groupCurrPK, globalCurrPk);
		boolean hlEnable = hlEnableArr[0];
		boolean groupHlEnable = hlEnableArr[1];
		boolean globalHlEnable = hlEnableArr[2];
		for (DataTable bTable : bTableArr) {
			// 设置币种、汇率的值
			FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(), MtAppDetailVO.PK_CURRTYPE, bzbm);
			FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(), MtAppDetailVO.ORG_CURRINFO, hlArr[0]);
			FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(), MtAppDetailVO.GROUP_CURRINFO, hlArr[1]);
			FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(), MtAppDetailVO.GLOBAL_CURRINFO, hlArr[2]);
			// 设置表体币种、汇率和精度
			bTable.updateMeta(Func.toString(MtAppDetailVO.PK_CURRTYPE), ERMWebConst.FIELD_DEFAULT, bzbm);// 表体币种默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.ORG_CURRINFO), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[0]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.ORG_CURRINFO), ERMWebConst.FIELD_PRECISION,
					String.valueOf(hlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.ORG_CURRINFO), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GROUP_CURRINFO), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[1]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GROUP_CURRINFO), ERMWebConst.FIELD_PRECISION,
					String.valueOf(grouphlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GROUP_CURRINFO), ERMWebConst.FIELD_ENABLE,
					String.valueOf(groupHlEnable));// 表体汇率可编辑性默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GLOBAL_CURRINFO), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[2]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GLOBAL_CURRINFO), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalhlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GLOBAL_CURRINFO), ERMWebConst.FIELD_ENABLE,
					String.valueOf(globalHlEnable));// 表体汇率可编辑性默认值
			updateMetaForMultiField(bTable, AggMatterAppVO.getBodyYbAmounts(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(ybDigit));
			updateMetaForMultiField(bTable, AggMatterAppVO.getBodyOrgAmounts(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(bbDigit));
			updateMetaForMultiField(bTable, AggMatterAppVO.getBodyGroupAmounts(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(groupbbDigit));
			updateMetaForMultiField(bTable, AggMatterAppVO.getBodyGlobalAmounts(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalbbDigit));
			updateMetaForMultiField(bTable, AggMatterAppVO.getBodyAmounts(), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(UFDouble.ZERO_DBL));// 设置默认值为0
			// 设置金额的默认值，因为上面设置默认值的代码对表体当前行无效，需要手工设置
			setBodyItemDigit(bTable.getCurrentRow(), AggMatterAppVO.getBodyYbAmounts(), ybDigit);
			setBodyItemDigit(bTable.getCurrentRow(), AggMatterAppVO.getBodyOrgAmounts(), bbDigit);
			setBodyItemDigit(bTable.getCurrentRow(), AggMatterAppVO.getBodyGroupAmounts(), groupbbDigit);
			setBodyItemDigit(bTable.getCurrentRow(), AggMatterAppVO.getBodyGlobalAmounts(), globalbbDigit);
			setBodyItemDefaultValue(bTable.getCurrentRow(), AggMatterAppVO.getBodyAmounts(), UFDouble.ZERO_DBL);
			for(String defaultField : defaultFieldArr){
				bTable.updateMeta(defaultField, ERMWebConst.FIELD_DEFAULT, 
						Func.toString(ERMDataTableUtil.getHeadValue(hTable, defaultField)));
			}
		}
	}

	/**
	 * @param hTable
	 * @param bTableArr
	 * @throws Exception
	 *             表头币种变化后编辑事件
	 */
	@SuppressWarnings("rawtypes")
	public static void afterEditHeadPK_CURRTYPE(DataTable hTable, DataTable[] bTableArr) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.BILLDATE);// 单据日期
		String bzbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.PK_CURRTYPE));// 币种
		String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);// 组织本币
		String groupCurrPK = Currency.getGroupCurrpk(pk_group);// 集团本币
		String globalCurrPk = Currency.getGlobalCurrPk(null);// 全局本币
		if (ERMValueCheck.isEmpty(bzbm)) {// 如果表头币种字段为空,取组织本币设置为默认币种
			bzbm = orgLocalCurrPK;
			FIDataTableUtil.setHeadValue(hTable, MatterAppVO.PK_CURRTYPE, bzbm);// 更新表头的币种
		}
		/**
		 * 取汇率，设置汇率
		 */
		UFDouble[] hlArr = ErmBillCalUtil.getRate(bzbm, pk_org, pk_group, djrq);// 取汇率
		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.ORG_CURRINFO, hlArr[0]);// 设置本币汇率汇率
		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.GROUP_CURRINFO, hlArr[1]);// 设置集团本币汇率group_currinfo
		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.GLOBAL_CURRINFO, hlArr[2]);// 设置全局本币汇率global_currinfo
		/**
		 * 取汇率精度，设置汇率精度
		 */
		int hlDigit = Currency.getRateDigit(pk_org, bzbm, orgLocalCurrPK);// 汇率精度
		int grouphlDigit = Currency.getGroupRateDigit(pk_org, pk_group, bzbm);// 集团汇率精度
		int globalhlDigit = Currency.getGlobalRateDigit(pk_org, bzbm);// 全局汇率精度
		FIDataTableUtil.setHeadItemDigi(hTable, MatterAppVO.ORG_CURRINFO, hlDigit);// 设置汇率精度
		FIDataTableUtil.setHeadItemDigi(hTable, MatterAppVO.GROUP_CURRINFO, grouphlDigit);// 设置集团汇率精度
		FIDataTableUtil.setHeadItemDigi(hTable, MatterAppVO.GLOBAL_CURRINFO, globalhlDigit);// 设置全局汇率精度
		/**
		 * 设置汇率字段的可编辑性
		 */
		// 设置汇率字段的可编辑性
		boolean[] hlEnableArr = getHLEnableArr(pk_group, bzbm, orgLocalCurrPK, groupCurrPK, globalCurrPk);
		boolean hlEnable = hlEnableArr[0];
		boolean groupHlEnable = hlEnableArr[1];
		boolean globalHlEnable = hlEnableArr[2];
		FIDataTableUtil.setHeadItemEnabled(hTable, MatterAppVO.ORG_CURRINFO, hlEnable);// 本币汇率可编辑性org_currinfo
		FIDataTableUtil.setHeadItemEnabled(hTable, MatterAppVO.GROUP_CURRINFO, groupHlEnable);// 集团本币汇率可编辑性group_currinfo
		FIDataTableUtil.setHeadItemEnabled(hTable, MatterAppVO.GLOBAL_CURRINFO, globalHlEnable);// 全局本币汇率可编辑性global_currinfo
		// 设置金额精度
		int ybDigit = Currency.getCurrDigit(bzbm);// 原币精度
		int bbDigit = Currency.getCurrDigit(orgLocalCurrPK);// 组织本币精度
		int groupbbDigit = Currency.getCurrDigit(groupCurrPK);// 集团本币精度
		int globalbbDigit = Currency.getCurrDigit(globalCurrPk);// 全局本币精度
		setHeadItemDigit(hTable, AggMatterAppVO.getHeadYbAmounts(), ybDigit);
		setHeadItemDigit(hTable, AggMatterAppVO.getHeadOrgAmounts(), bbDigit);
		setHeadItemDigit(hTable, AggMatterAppVO.getHeadGroupAmounts(), groupbbDigit);
		setHeadItemDigit(hTable, AggMatterAppVO.getHeadGlobalAmounts(), globalbbDigit);

		// 设置表头金额的默认值
		// setHeadItemDefaultValue(hTable, AggMatterAppVO.getHeadAmounts(),
		// UFDouble.ZERO_DBL);
		// afterEditHeadHL(hTable);//表头汇率变化后的处理
		for (DataTable bTable : bTableArr) {
			// 设置表体币种、汇率和精度
			bTable.updateMeta(Func.toString(MtAppDetailVO.PK_CURRTYPE), ERMWebConst.FIELD_DEFAULT, bzbm);// 表体币种默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.ORG_CURRINFO), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[0]));// 表体组织汇率默认值org_currinfo
			bTable.updateMeta(Func.toString(MtAppDetailVO.ORG_CURRINFO), ERMWebConst.FIELD_PRECISION,
					String.valueOf(hlDigit));// 表体组织汇率精度默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.ORG_CURRINFO), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体组织汇率可编辑性默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GROUP_CURRINFO), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[1]));// 表体集团本币汇率默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GROUP_CURRINFO), ERMWebConst.FIELD_PRECISION,
					String.valueOf(grouphlDigit));// 表体集团本币汇率精度默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GROUP_CURRINFO), ERMWebConst.FIELD_ENABLE,
					String.valueOf(groupHlEnable));// 表体集团本币汇率可编辑性默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GLOBAL_CURRINFO), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[2]));// 表体全局汇率默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GLOBAL_CURRINFO), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalhlDigit));// 表体全局汇率精度默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GLOBAL_CURRINFO), ERMWebConst.FIELD_ENABLE,
					String.valueOf(globalHlEnable));// 表体全局汇率可编辑性默认值

			// TODO 处理到这里
			updateMetaForMultiField(bTable, AggMatterAppVO.getBodyYbAmounts(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(ybDigit));
			updateMetaForMultiField(bTable, AggMatterAppVO.getBodyOrgAmounts(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(bbDigit));
			updateMetaForMultiField(bTable, AggMatterAppVO.getBodyGroupAmounts(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(groupbbDigit));
			updateMetaForMultiField(bTable, AggMatterAppVO.getBodyGlobalAmounts(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalbbDigit));
			// updateMetaForMultiField(bTable,
			// BXBusItemVO.getBodyJeFieldForDecimal(),
			// ERMWebConst.FIELD_DEFAULT,
			// String.valueOf(UFDouble.ZERO_DBL));//设置默认值为0
			// 设置金额的默认值，因为上面设置默认值的代码对表体当前行无效，需要手工设置
			setBodyItemDefaultValue(bTable.getCurrentRow(), AggMatterAppVO.getBodyAmounts(), UFDouble.ZERO_DBL);
			for (int i = 0; i < bTable.getAllRow().length; i++) {// 这里不排除当前行，处理精度
				Row row = bTable.getAllRow()[i];
				FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.PK_CURRTYPE, bzbm);
				FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.ORG_CURRINFO, hlArr[0]);
				FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.GROUP_CURRINFO, hlArr[1]);
				FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.GLOBAL_CURRINFO, hlArr[2]);
				setBodyConfigFY(pk_group, pk_org, djrq, row);// 表体行币种变化后处理
				calBbOnRowFY(pk_group, pk_org, djrq, row);// 本币计算
			}
		}
		doBodyCalculationFY(hTable, bTableArr);// 执行计算
		doBodyTotalToHeadFY(hTable, bTableArr);// 汇总到表头
	}

	/**
	 * 表头汇率变化后的处理
	 * 
	 * @param dtEvent
	 * @throws Exception
	 */
	public static void afterEditHeadORG_CURRINFO(DataTable hTable, DataTable[] bTableArr) throws Exception {
		UFDouble bbhl = (UFDouble) ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.ORG_CURRINFO);
		for (DataTable bTable : bTableArr) {
			for (int i = 0; i < bTable.getAllRow().length; i++) {
				if (i == bTable.getAllRow().length - 1 && bTable.getAllRow().length != 1) {// 当前行不计算
					continue;
				}
				Row row = bTable.getAllRow()[i];
				ERMDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.ORG_CURRINFO, bbhl);
			}
		}
		doBodyCalculationFY(hTable, bTableArr);// 执行计算
		doBodyTotalToHeadFY(hTable, bTableArr);// 汇总到表头
	}

	/**
	 * 单据日期变化后编辑事件
	 * 
	 * @param hTable
	 * @param bTableArr
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void afterEditHeadBILLDATE(DataTable hTable, DataTable[] bTableArr) throws Exception {
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.BILLDATE);// 单据日期
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.PK_GROUP));// 集团
		String bzbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.PK_CURRTYPE));// 币种
		String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);
		if (ERMValueCheck.isEmpty(bzbm)) {// 如果表头币种字段为空,取组织本币设置为默认币种
			bzbm = orgLocalCurrPK;
			FIDataTableUtil.setHeadValue(hTable, MatterAppVO.ORG_CURRINFO, bzbm);
		}
		UFDouble[] hlArr = ErmBillCalUtil.getRate(bzbm, pk_org, pk_group, djrq);// 取汇率
		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.ORG_CURRINFO, hlArr[0]);// 设置汇率
		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.GROUP_CURRINFO, hlArr[1]);// 设置集团本币汇率
		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.GLOBAL_CURRINFO, hlArr[2]);// 设置全局本币汇率
		doBodyCalculationFY(hTable, bTableArr); // 执行计算
		doBodyTotalToHeadFY(hTable, bTableArr);// 汇总表体金额字段到表头
	}

	/**
	 * 执行计算
	 * 
	 * @param hTable
	 * @param bTableArr
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void doBodyCalculationFY(DataTable hTable, DataTable[] bTableArr) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.BILLDATE);// 单据日期
		for (DataTable bTable : bTableArr) {
			for (int i = 0; i < bTable.getAllRow().length; i++) {
				if (i == bTable.getAllRow().length - 1 && bTable.getAllRow().length != 1) {// 当前行不计算
					continue;
				}
				Row row = bTable.getAllRow()[i];
				calBbOnRowFY(pk_group, pk_org, djrq, row);// 本币计算
			}
		}
	}

	/**
	 * 费用单 表体单行计算，根据原币金额计算本币金额
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @param djrq
	 * @param row
	 * @throws Exception
	 */
	public static void calBbOnRowFY(String pk_group, String pk_org, UFDate djrq, Row row) throws Exception {
		String bzbm_busitem = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, MtAppDetailVO.PK_CURRTYPE));// 表体币种
		// 查询表体币种对应的集团本币汇率和全局本币汇率
		UFDouble[] hlArr = ErmBillCalUtil.getRate(bzbm_busitem, pk_org, pk_group, djrq);
		// FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.ORG_CURRINFO,
		// hlArr[0]);// 组织汇率
		UFDouble hl_busitem = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, MtAppDetailVO.ORG_CURRINFO);// 表体汇率
		UFDouble grouphl_busitem = hlArr[1];// 集团本币汇率
		UFDouble globalhl_busitem = hlArr[2];// 全局本币汇率
		FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.GROUP_CURRINFO, grouphl_busitem);// 集团本币汇率
		FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.GLOBAL_CURRINFO, globalhl_busitem);// 全局本币汇率
		UFDouble ybje = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, MtAppDetailVO.ORIG_AMOUNT) == null ? UFDouble.ZERO_DBL
				: (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, MtAppDetailVO.ORIG_AMOUNT);// 原币金额
		// 根据原币金额计算本币金额
		UFDouble[] bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr, bzbm_busitem, ybje, null, null, null,
				hl_busitem, djrq);// 本币金额
		UFDouble[] ybggBbje = Currency.computeGroupGlobalAmount(bbje[0], bbje[2], bzbm_busitem, djrq, pk_org, pk_group,
				globalhl_busitem, grouphl_busitem);// 集团和全局本币金额
		FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.ORG_AMOUNT, bbje[2]);// 组织本币金额
		FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.ORG_REST_AMOUNT, bbje[2]);// 组织本币余额
		FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.GROUP_AMOUNT, ybggBbje[0]);// 集团本币金额
		FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.GROUP_REST_AMOUNT, ybggBbje[0]);// 集团本币余额
		FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.GLOBAL_AMOUNT, ybggBbje[1]);// 全局本币金额
		FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.GLOBAL_REST_AMOUNT, ybggBbje[1]);// 全局本币余额
	}

	/**
	 * 费用 汇总表体金额字段到表头
	 * 
	 * @param hTable
	 * @param bTableArr
	 */
	@SuppressWarnings({ "rawtypes" })
	private static void doBodyTotalToHeadFY(DataTable hTable, DataTable[] bTableArr) {
		UFDouble[] totalArr = new UFDouble[AggMatterAppVO.getHeadAmounts().length];
		for (int i = 0; i < totalArr.length; i++) {
			totalArr[i] = UFDouble.ZERO_DBL;
		}
		UFDouble amountTotal = UFDouble.ZERO_DBL;// 表体AMOUNT汇总到表头TOTAL，单独处理
		for (DataTable bTable : bTableArr) {
			for (int i = 0; i < bTable.getAllRow().length; i++) {
				if (i == bTable.getAllRow().length - 1) {// 当前行不计算
					continue;
				}
				Row row = bTable.getAllRow()[i];
				for (int j = 0; j < AggMatterAppVO.getHeadAmounts().length; j++) {
					totalArr[j] = totalArr[j].add(null == FIDataTableUtil.getBodyValueByRow(row,
							AggMatterAppVO.getHeadAmounts()[j]) ? UFDouble.ZERO_DBL : (UFDouble) FIDataTableUtil
							.getBodyValueByRow(row, AggMatterAppVO.getHeadAmounts()[j]));
				}
				amountTotal = amountTotal
						.add(null == FIDataTableUtil.getBodyValueByRow(row, MtAppDetailVO.ORIG_AMOUNT) ? UFDouble.ZERO_DBL
								: (UFDouble) FIDataTableUtil.getBodyValueByRow(row, MtAppDetailVO.ORIG_AMOUNT));
			}
		}
		for (int i = 0; i < AggMatterAppVO.getHeadAmounts().length; i++) {
			FIDataTableUtil.setHeadValue(hTable, AggMatterAppVO.getHeadAmounts()[i], totalArr[i]);
		}
		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.ORIG_AMOUNT, amountTotal);// 汇总到表头的金额字段
	}

	/**
	 * 表体单行币种变化后设置精度、可编辑性和默认值等
	 * 
	 * @param dtEvent
	 * @throws Exception
	 */
	public static void setBodyConfigFY(String pk_group, String pk_org, UFDate djrq, Row row) throws Exception {
		String bzbm_busitem = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, MtAppDetailVO.PK_CURRTYPE));// 表体币种
		String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);
		String groupCurrPK = Currency.getGroupCurrpk(pk_group);
		String globalCurrPk = Currency.getGlobalCurrPk(null);
		// 汇率精度
		int hlDigit = Currency.getRateDigit(pk_org, bzbm_busitem, Currency.getOrgLocalCurrPK(pk_org));
		int grouphlDigit = Currency.getGroupRateDigit(pk_org, pk_group, bzbm_busitem);
		int globalhlDigit = Currency.getGlobalRateDigit(pk_org, bzbm_busitem);
		// 设置表体汇率字段的可编辑性
		boolean[] hlEnableArr = getHLEnableArr(pk_group, bzbm_busitem, orgLocalCurrPK, groupCurrPK, globalCurrPk);
		boolean hlEnable = hlEnableArr[0];
		boolean groupHlEnable = hlEnableArr[1];
		boolean globalHlEnable = hlEnableArr[2];
		row.getField(MtAppDetailVO.ORG_CURRINFO).getMeta().getDescs()
				.put(ERMWebConst.FIELD_PRECISION, String.valueOf(hlDigit));// 设置汇率精度
		row.getField(MtAppDetailVO.GROUP_CURRINFO).getMeta().getDescs()
				.put(ERMWebConst.FIELD_PRECISION, String.valueOf(grouphlDigit));// 设置汇率精度
		row.getField(MtAppDetailVO.GLOBAL_CURRINFO).getMeta().getDescs()
				.put(ERMWebConst.FIELD_PRECISION, String.valueOf(globalhlDigit));// 设置汇率精度
		row.getField(MtAppDetailVO.ORG_CURRINFO).setDesc(ERMWebConst.FIELD_ENABLE, String.valueOf(hlEnable));
		row.getField(MtAppDetailVO.GROUP_CURRINFO).setDesc(ERMWebConst.FIELD_ENABLE, String.valueOf(groupHlEnable));
		row.getField(MtAppDetailVO.GLOBAL_CURRINFO).setDesc(ERMWebConst.FIELD_ENABLE, String.valueOf(globalHlEnable));
		// 设置金额精度
		int ybDigit = Currency.getCurrDigit(bzbm_busitem);// 原币精度
		int bbDigit = Currency.getCurrDigit(orgLocalCurrPK);// 组织本币精度
		int groupbbDigit = Currency.getCurrDigit(groupCurrPK);// 集团本币精度
		int globalbbDigit = Currency.getCurrDigit(globalCurrPk);// 全局本币精度
		setBodyItemDigit(row, AggMatterAppVO.getBodyYbAmounts(), ybDigit);
		setBodyItemDigit(row, AggMatterAppVO.getBodyOrgAmounts(), bbDigit);
		setBodyItemDigit(row, AggMatterAppVO.getBodyGroupAmounts(), groupbbDigit);
		setBodyItemDigit(row, AggMatterAppVO.getBodyGlobalAmounts(), globalbbDigit);
	}

	/**
	 * 表体币种编辑后处理
	 * 
	 * @param hTable
	 * @param row
	 * @throws Exception
	 */
	public static void afterEditBodyPK_CURRTYPE(DataTable hTable, Row row) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.BILLDATE);// 单据日期
		String bzbm_busitem = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, MtAppDetailVO.PK_CURRTYPE));// 表体币种
		// 查询表体币种对应的集团本币汇率和全局本币汇率
		UFDouble[] hlArr = ErmBillCalUtil.getRate(bzbm_busitem, pk_org, pk_group, djrq);
		FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.ORG_CURRINFO, hlArr[0]);// 组织汇率
		FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.GROUP_CURRINFO, hlArr[1]);// 集团本币汇率
		FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.GLOBAL_CURRINFO, hlArr[2]);// 全局本币汇率
		setBodyConfigFY(pk_group, pk_org, djrq, row);// 设置汇率和精度
		calBbOnRowFY(pk_group, pk_org, djrq, row);// 本币计算
	}

	/**
	 * 表体金额编辑后处理
	 * 
	 * @param hTable
	 * @param row
	 * @throws Exception
	 */
	public static void afterEditBodyORIG_AMOUNT(DataTable hTable, Row row) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.BILLDATE);// 单据日期
		UFDouble amount = (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, MtAppDetailVO.ORIG_AMOUNT) == null ? UFDouble.ZERO_DBL
				: (UFDouble) ERMDataTableUtil.getBodyValueByRow(row, MtAppDetailVO.ORIG_AMOUNT);
		FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.REST_AMOUNT, amount);
		FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.USABLE_AMOUT, amount);

		Map<String, String> clientAttrs = IWebViewContext.getRequest().getEnvironment().getClientAttributes();
		String pkTradeType = clientAttrs.get(ERMWebConst.TRADETYPE);
		DjLXVO[] djlxvos;
		djlxvos = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, " djdl='ma' and  DJLXBM = '" + pkTradeType
				+ "' and  pk_group='" + InvocationInfoProxy.getInstance().getGroupId() + "' order by djlxbm ");

		if (null != djlxvos && djlxvos.length > 0) {
			DjLXVO djlxvoByDjlxbm = djlxvos[0];
			UFDouble bx_percentage = djlxvoByDjlxbm.getBx_percentage();// 获取允许最大报销金额的百分比
			if (null != bx_percentage && null != amount) {
				// 计算允许报销最大金额并赋值,获取的配置参数是百分制的，所以拿金额除以100再计算
				UFDouble maxAmount = bx_percentage.multiply(amount.div(new UFDouble(100)));
				// 更新当前行表体的允许报销最大金额
				FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.MAX_AMOUNT, maxAmount);
			}
		}
		calBbOnRowFY(pk_group, pk_org, djrq, row);// 本币计算
	}

	/**
	 * 表体汇率编辑后处理
	 * 
	 * @param hTable
	 * @param row
	 * @throws Exception
	 */
	public static void afterEditBodyORG_CURRINFO(DataTable hTable, Row row) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.BILLDATE);// 单据日期
		calBbOnRowFY(pk_group, pk_org, djrq, row);// 本币计算
	}

	/**
	 * 表头申请单位编辑后处理
	 * 
	 * @param hTable
	 * @param bTable
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void afterEditAppLyOrg(DataTable hTable, DataTable[] bTableArr) throws Exception {
		/*
		 * 表头申请单位编辑后需要重新设置表头申请单位的参照,并且清空表头申请人
		 */
		List<DataTable> allDataTableList = new ArrayList<DataTable>();
		allDataTableList.add(hTable);
		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.APPLY_DEPT, null);// 申请部门清空
		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.BILLMAKER, null);// 申请人清空
		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.ASSUME_DEPT, null);// 费用承担部门清空
		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.PK_IOBSCLASS, null);// 收支项目清空

		ERMRefUtil.dealDeptFieldBODY(allDataTableList.toArray(new DataTable[allDataTableList.size()]), new String[] {
				MatterAppVO.APPLY_DEPT, MatterAppVO.ASSUME_DEPT, MatterAppVO.PK_IOBSCLASS }, true);// 重新设置申请部门和费用承担部门的参照
		ERMRefUtil.dealPersonFieldFY(hTable, allDataTableList.toArray(new DataTable[allDataTableList.size()]),
				new String[] { MatterAppVO.BILLMAKER });// 重新设置申请人的参照
		/*
		 * 循环处理每个表体 判断表体上面的费用承担单位，如果为null，将表头的申请单位赋值给它
		 */
		String applyOrg = Func.toString(FIDataTableUtil.getHeadValue(hTable, MatterAppVO.APPLY_ORG));// 获取表头的申请单位
		for (DataTable bTable : bTableArr) {
			if (null == FIDataTableUtil.getBodyValue(bTable, MtAppDetailVO.ASSUME_ORG)) {
				FIDataTableUtil.dealCardRelationItem(bTable, MtAppDetailVO.ASSUME_ORG, applyOrg);
			}
		}
		afterEditApplyDept(hTable, bTableArr);// 申请部门编辑后事件
		// afterEditJKBXR(hTable, bTableArr);// 申请人编辑后事件

	}

	/**
	 * 申请部门编辑后事件
	 * 
	 * @param hTable
	 * @param bTableArr
	 * @param shareTable
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void afterEditApplyDept(DataTable hTable, DataTable[] bTableArr) throws Exception {
		List<DataTable> allDataTableList = new ArrayList<DataTable>();
		allDataTableList.add(hTable);
		// 表头的申请部门
		Object applyDept = FIDataTableUtil.getHeadValue(hTable, MatterAppVO.APPLY_DEPT);
		// 表体apply_dept申请部门字段赋值
		for (DataTable bTable : bTableArr) {
			FIDataTableUtil.dealCardRelationItem(bTable, MtAppDetailVO.APPLY_DEPT, applyDept);
			/*
			 * 表头的申请部门和表体的费用承担部门联动判断下表体的费用承担部门是否为空，为null就把表头的申请部门赋值给它
			 */
			Object assumeDept = FIDataTableUtil.getBodyValue(bTable, MtAppDetailVO.ASSUME_DEPT);
			if (assumeDept == null) {
				FIDataTableUtil.dealCardRelationItem(bTable, MatterAppVO.ASSUME_DEPT, applyDept);
			}
		}
		// 清空表头的申请人
		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.BILLMAKER, null);
	}

	/**
	 * 申请人编辑后事件
	 * 
	 * @param hTable
	 * @param bTableArr
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void afterEditBillmaker(DataTable hTable, DataTable[] bTableArr) throws Exception {
		// 获取表头的申请人
		Object billMaker = FIDataTableUtil.getHeadValue(hTable, MatterAppVO.BILLMAKER);

		// 申请人联动申请部门
//		String billMakerString = Func.toString(billMaker);
//		Map<String, List<String>> queryDeptIDByPsndocIDs = nc.itf.scmpub.reference.uap.bd.psn.PsndocPubService
//				.queryDeptIDByPsndocIDs(new String[] { billMakerString });
//		String deptId = "";
//		
//		PsnjobVO queryPsnJobVOByPsnDocPK = NCLocator.getInstance().lookup(IPsndocQueryService.class).queryPsnJobVOByPsnDocPK(billMakerString);
//		if (null != queryPsnJobVOByPsnDocPK ) {
//			deptId = queryPsnJobVOByPsnDocPK.getPk_dept();
//		}
//		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.APPLY_DEPT, deptId);
		
		String deptId = "";
		String billMakerString = Func.toString(billMaker);
		if(ERMValueCheck.isNotEmpty(billMakerString)){
			deptId = getDeptidByJkbxr(billMakerString);
			FIDataTableUtil.setHeadValue(hTable, MatterAppVO.APPLY_DEPT, deptId);// 表头报销人部门
		}
		
		
		// 表体deptid字段赋值
		for (DataTable bTable : bTableArr) {
			FIDataTableUtil.dealCardRelationItem(bTable, MtAppDetailVO.BILLMAKER, billMaker);
		}
	}

	/**
	 * 表头收支项目联动
	 * 
	 * @param hTable
	 * @param bTableArr
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void afterEditPK_IOBSCLASS(DataTable hTable, DataTable[] bTableArr) throws Exception {
		// 获取表头的收支项目
		Object pk_iobsclass = FIDataTableUtil.getHeadValue(hTable, MatterAppVO.PK_IOBSCLASS);
		// 更新表体的收支项目,表体pk_iobsclass字段赋值
		for (DataTable bTable : bTableArr) {
			FIDataTableUtil.dealCardRelationItem(bTable, MtAppDetailVO.PK_IOBSCLASS, pk_iobsclass);
		}
	}

	/**
	 * 表体费用承担单位编辑后事件
	 * 
	 * @param hTable
	 * @param row
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void afterEditBodyASSUME_ORG(DataTable bTable, Row row) throws Exception {
		List<DataTable> allDataTableList = new ArrayList<DataTable>();
		allDataTableList.add(bTable);
		FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.ASSUME_DEPT, null);// 清空费用承担部门
		FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.PK_IOBSCLASS, null);// 清空收支项目

		ERMRefUtil.dealDeptFieldBODY(allDataTableList.toArray(new DataTable[allDataTableList.size()]), new String[] {
				MtAppDetailVO.ASSUME_DEPT, MtAppDetailVO.PK_IOBSCLASS }, false);
		DataChangeLogic.afterEditAssume_org(bTable, row);
		/*
		 * 表头申请单位编辑后需要重新设置表头申请单位的参照,并且清空表头申请人
		 * 
		 * List<DataTable> allDataTableList = new ArrayList<DataTable>();
		 * allDataTableList.add(hTable); FIDataTableUtil.setHeadValue(hTable,
		 * MatterAppVO.APPLY_DEPT, null);//申请部门清空
		 * FIDataTableUtil.setHeadValue(hTable, MatterAppVO.BILLMAKER,
		 * null);//申请人清空 FIDataTableUtil.setHeadValue(hTable,
		 * MatterAppVO.ASSUME_DEPT, null);//费用承担部门清空
		 * 
		 * ERMRefUtil.dealDeptField(allDataTableList.toArray(new
		 * DataTable[allDataTableList.size()]), new String[] {
		 * MatterAppVO.APPLY_DEPT,MatterAppVO.ASSUME_DEPT});//重新设置申请部门和费用承担部门的参照
		 * ERMRefUtil.dealPersonFieldFY(hTable, allDataTableList.toArray(new
		 * DataTable[allDataTableList.size()]), new String[]
		 * {MatterAppVO.BILLMAKER});//重新设置申请人的参照
		 * 
		 * 循环处理每个表体 判断表体上面的费用承担单位，如果为null，将表头的申请单位赋值给它
		 * 
		 * String applyOrg = Func.toString(FIDataTableUtil.getHeadValue(hTable,
		 * MatterAppVO.APPLY_ORG));//获取表头的申请单位 for(DataTable bTable :
		 * bTableArr){ if(null == FIDataTableUtil.getBodyValue(bTable,
		 * MtAppDetailVO.ASSUME_ORG)){
		 * FIDataTableUtil.dealCardRelationItem(bTable,
		 * MtAppDetailVO.ASSUME_ORG, applyOrg); } } afterEditApplyDept(hTable,
		 * bTableArr);//申请部门编辑后事件 afterEditJKBXR(hTable, bTableArr);//申请人编辑后事件
		 */
	}

	/**
	 * end add
	 */

	/**
	 * 借款报销复制时处理
	 * 
	 * @param hTable
	 * @param bTableArr
	 * @param shareTable
	 * @throws Exception
	 */
	public static void setConfig4Copy(DataTable hTable, DataTable[] bTableArr, DataTable shareTable) throws Exception {
		String[] defaultFieldArr = new String[]{IBillFieldGet.SZXMID, IBillFieldGet.JOBID, IBillFieldGet.SKYHZH, IBillFieldGet.JKBXR, 
				IBillFieldGet.RECEIVER, IBillFieldGet.DEPTID};
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJRQ);// 单据日期
		String bzbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.BZBM));// 币种
		String paytarget = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PAYTARGET));//收款对象
		String hbbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.HBBM));//供应商
		String customer = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.CUSTOMER));//客户
		String custaccount = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.CUSTACCOUNT));//客商银行账户
		String freecust = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.FREECUST));
		String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);
		String groupCurrPK = Currency.getGroupCurrpk(pk_group);
		String globalCurrPk = Currency.getGlobalCurrPk(null);
		if (ERMValueCheck.isEmpty(bzbm)) {// 如果表头币种字段为空,取组织本币设置为默认币种
			bzbm = orgLocalCurrPK;
			FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.BZBM, bzbm);
		}
		UFDouble[] hlArr = ErmBillCalUtil.getRate(bzbm, pk_org, pk_group, djrq);// 取汇率
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.BBHL, hlArr[0]);// 设置汇率
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.GROUPBBHL, hlArr[1]);// 设置集团本币汇率
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.GLOBALBBHL, hlArr[2]);// 设置全局本币汇率
		int hlDigit = Currency.getRateDigit(pk_org, bzbm, orgLocalCurrPK);// 汇率精度
		int grouphlDigit = Currency.getGroupRateDigit(pk_org, pk_group, bzbm);// 集团汇率精度
		int globalhlDigit = Currency.getGlobalRateDigit(pk_org, bzbm);// 全局汇率精度
		FIDataTableUtil.setHeadItemDigi(hTable, IBillFieldGet.BBHL, hlDigit);// 设置汇率精度
		FIDataTableUtil.setHeadItemDigi(hTable, IBillFieldGet.GROUPBBHL, grouphlDigit);// 设置集团汇率精度
		FIDataTableUtil.setHeadItemDigi(hTable, IBillFieldGet.GLOBALBBHL, globalhlDigit);// 设置全局汇率精度
		// FIDataTableUtil.setHeadItemDigi(hTable, IBillFieldGet.TAX_RATE,
		// ERMTaxUtil.TAX_RATE_DIGIT);// 设置税率精度
		// 设置汇率字段的可编辑性
		boolean[] hlEnableArr = getHLEnableArr(pk_group, bzbm, orgLocalCurrPK, groupCurrPK, globalCurrPk);
		boolean hlEnable = hlEnableArr[0];
		boolean groupHlEnable = hlEnableArr[1];
		boolean globalHlEnable = hlEnableArr[2];
		FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.BBHL, hlEnable);
		FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.GROUPBBHL, groupHlEnable);
		FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.GLOBALBBHL, globalHlEnable);
		// 设置金额精度
		int ybDigit = Currency.getCurrDigit(bzbm);// 原币精度
		int bbDigit = Currency.getCurrDigit(orgLocalCurrPK);// 组织本币精度
		int groupbbDigit = Currency.getCurrDigit(groupCurrPK);// 集团本币精度
		int globalbbDigit = Currency.getCurrDigit(globalCurrPk);// 全局本币精度
		setHeadItemDigit(hTable, JKBXHeaderVO.getYbjeField(), ybDigit);
		setHeadItemDigit(hTable, JKBXHeaderVO.getOrgBbjeField(), bbDigit);
		setHeadItemDigit(hTable, JKBXHeaderVO.getHeadGroupBbjeField(), groupbbDigit);
		setHeadItemDigit(hTable, JKBXHeaderVO.getHeadGlobalBbjeField(), globalbbDigit);
		// 设置表头金额的默认值
		setHeadItemDefaultValue(hTable, BXHeaderVO.getJeField(), UFDouble.ZERO_DBL);
		for (DataTable bTable : bTableArr) {
			// 如果该页签未保存，修改单据时，该页签字段默认取表头的值，收款对象、供应商、客户、客商银行账户、散户、散户银行账户
			Integer select = bTable.getSelect()[0];
			if(0 == select){
				bTable.updateMeta(Func.toString(IBillFieldGet.PAYTARGET_BUSITEM), ERMWebConst.FIELD_DEFAULT, paytarget);
				bTable.updateMeta(Func.toString(IBillFieldGet.HBBM_BUSITEM), ERMWebConst.FIELD_DEFAULT, hbbm);
				bTable.updateMeta(Func.toString(IBillFieldGet.CUSTOMER_BUSITEM), ERMWebConst.FIELD_DEFAULT, customer);
				bTable.updateMeta(Func.toString(IBillFieldGet.CUSTACCOUNT_BUSITEM), ERMWebConst.FIELD_DEFAULT, custaccount);
				bTable.updateMeta(Func.toString(IBillFieldGet.FREECUST_BUSITEM), ERMWebConst.FIELD_DEFAULT, freecust);
				FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(),IBillFieldGet.PAYTARGET_BUSITEM, paytarget);
				FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(),IBillFieldGet.HBBM_BUSITEM, hbbm);
				FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(),IBillFieldGet.CUSTOMER_BUSITEM, customer);
				FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(),IBillFieldGet.CUSTACCOUNT_BUSITEM, custaccount);
				FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(),IBillFieldGet.FREECUST_BUSITEM, freecust);
			}
			// 设置表体币种、汇率和精度
			bTable.updateMeta(Func.toString(IBillFieldGet.BZBM_BUSITEM), ERMWebConst.FIELD_DEFAULT, bzbm);// 表体币种默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.BBHL_BUSITEM), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[0]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.BBHL_BUSITEM), ERMWebConst.FIELD_PRECISION,
					String.valueOf(hlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.BBHL_BUSITEM), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_BUSITEM), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[1]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_BUSITEM), ERMWebConst.FIELD_PRECISION,
					String.valueOf(grouphlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_BUSITEM), ERMWebConst.FIELD_ENABLE,
					String.valueOf(groupHlEnable));// 表体汇率可编辑性默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_BUSITEM), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[2]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_BUSITEM), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalhlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_BUSITEM), ERMWebConst.FIELD_ENABLE,
					String.valueOf(globalHlEnable));// 表体汇率可编辑性默认值
			bTable.updateMeta(Func.toString(IBillFieldGet.TAX_RATE_BUSITEM), ERMWebConst.FIELD_PRECISION, ""
					+ ERMTaxUtil.TAX_RATE_DIGIT);// 表体税率精度默认值
			updateMetaForMultiField(bTable, BXBusItemVO.getYbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(ybDigit));
			updateMetaForMultiField(bTable, BXBusItemVO.getBodyOrgBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(bbDigit));
			updateMetaForMultiField(bTable, BXBusItemVO.getBodyGroupBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(groupbbDigit));
			updateMetaForMultiField(bTable, BXBusItemVO.getBodyGlobalBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalbbDigit));
			updateMetaForMultiField(bTable, BXBusItemVO.getBodyJeFieldForDecimal(), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(UFDouble.ZERO_DBL));// 设置默认值为0
			for(String defaultField : defaultFieldArr){
				bTable.updateMeta(defaultField, ERMWebConst.FIELD_DEFAULT, 
						Func.toString(ERMDataTableUtil.getHeadValue(hTable, defaultField)));
				FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(), defaultField, Func.toString(FIDataTableUtil.getHeadValue(hTable, defaultField)));
			}
			// 设置金额的默认值，因为上面设置默认值的代码对表体当前行无效，需要手工设置
			setBodyItemDefaultValue(bTable.getCurrentRow(), BXBusItemVO.getBodyJeFieldForDecimal(), UFDouble.ZERO_DBL);
			for (int i = 0; i < bTable.getAllRow().length; i++) {// 这里不排除当前行，处理精度
				Row row = bTable.getAllRow()[i];
				String bzbm_busitem = Func
						.toString(ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.BZBM_BUSITEM));// 表体币种
				if (null == bzbm_busitem) {
					bzbm_busitem = bzbm;
					FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BZBM_BUSITEM, bzbm_busitem);
				}
				UFDouble[] hlBusitemArr = ErmBillCalUtil.getRate(bzbm_busitem, pk_org, pk_group, djrq);// 取汇率
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BBHL_BUSITEM, hlBusitemArr[0]);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPBBHL_BUSITEM, hlBusitemArr[1]);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALBBHL_BUSITEM, hlBusitemArr[2]);
				setBodyConfig(pk_group, pk_org, djrq, row);// 表体行币种变化后处理
			}
		}
		doBodyCalculation(hTable, bTableArr);
		doBodyTotalToHead(hTable, bTableArr);

		// 处理分摊页签
		if (ERMValueCheck.isNotEmpty(shareTable)) {
			shareTable.updateMeta(Func.toString(IBillFieldGet.BZBM_SHARE), ERMWebConst.FIELD_DEFAULT, bzbm);// 表体币种默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.BBHL_SHARE), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[0]));// 表体汇率默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.BBHL_SHARE), ERMWebConst.FIELD_PRECISION,
					String.valueOf(hlDigit));// 表体汇率精度默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.BBHL_SHARE), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_SHARE), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[1]));// 表体汇率默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_SHARE), ERMWebConst.FIELD_PRECISION,
					String.valueOf(groupbbDigit));// 表体汇率精度默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GROUPBBHL_SHARE), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_SHARE), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[2]));// 表体汇率默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_SHARE), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalbbDigit));// 表体汇率精度默认值
			shareTable.updateMeta(Func.toString(IBillFieldGet.GLOBALBBHL_SHARE), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			updateMetaForMultiField(shareTable, CShareDetailVO.getYbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(ybDigit));
			updateMetaForMultiField(shareTable, CShareDetailVO.getBodyOrgBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(bbDigit));
			updateMetaForMultiField(shareTable, CShareDetailVO.getBodyGroupBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(groupbbDigit));
			updateMetaForMultiField(shareTable, CShareDetailVO.getBodyGlobalBbjeField(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalbbDigit));
			updateMetaForMultiField(shareTable, CShareDetailVO.getBodyJeFieldForDecimal(), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(UFDouble.ZERO_DBL));// 设置默认值为0
			// 设置金额的默认值，因为上面设置默认值的代码对表体当前行无效，需要手工设置
			setBodyItemDefaultValue(shareTable.getCurrentRow(), CShareDetailVO.getBodyJeFieldForDecimal(),
					UFDouble.ZERO_DBL);
			for (int i = 0; i < shareTable.getAllRow().length; i++) {// 这里不排除当前行，处理精度
				Row row = shareTable.getAllRow()[i];
				String bzbm_share = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.BZBM_SHARE));// 表体币种
				if (null == bzbm_share) {
					bzbm_share = bzbm;
					FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BZBM_SHARE, bzbm_share);
				}
				UFDouble[] hlShareArr = ErmBillCalUtil.getRate(bzbm_share, pk_org, pk_group, djrq);// 取汇率
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BZBM_SHARE, bzbm);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.BBHL_SHARE, hlShareArr[0]);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GROUPBBHL_SHARE, hlShareArr[1]);
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.GLOBALBBHL_SHARE, hlShareArr[2]);
				setBodyConfig4Share(pk_group, pk_org, djrq, row);// 表体行币种变化后处理
			}
			doBodyCalculation4Share(hTable, shareTable);
		}
	}

	/**
	 * 费用申请复制时处理
	 * 
	 * @param hTable
	 * @param bTableArr
	 * @param shareTable
	 * @throws Exception
	 */
	public static void setConfig4CopyFY(DataTable hTable, DataTable[] bTableArr) throws Exception {
		String[] defaultFieldArr = new String[]{MatterAppVO.PK_IOBSCLASS, MatterAppVO.BILLMAKER, MatterAppVO.APPLY_ORG, MatterAppVO.APPLY_DEPT};
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.BILLDATE);// 单据日期
		String bzbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.PK_CURRTYPE));// 币种
		String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);
		String groupCurrPK = Currency.getGroupCurrpk(pk_group);
		String globalCurrPk = Currency.getGlobalCurrPk(null);
		if (ERMValueCheck.isEmpty(bzbm)) {// 如果表头币种字段为空,取组织本币设置为默认币种
			bzbm = orgLocalCurrPK;
			FIDataTableUtil.setHeadValue(hTable, MatterAppVO.PK_CURRTYPE, bzbm);
		}
		UFDouble[] hlArr = ErmBillCalUtil.getRate(bzbm, pk_org, pk_group, djrq);// 取汇率
		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.ORG_CURRINFO, hlArr[0]);// 设置汇率
		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.GROUP_CURRINFO, hlArr[1]);// 设置集团本币汇率
		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.GLOBAL_CURRINFO, hlArr[2]);// 设置全局本币汇率
		int hlDigit = Currency.getRateDigit(pk_org, bzbm, orgLocalCurrPK);// 汇率精度
		int grouphlDigit = Currency.getGroupRateDigit(pk_org, pk_group, bzbm);// 集团汇率精度
		int globalhlDigit = Currency.getGlobalRateDigit(pk_org, bzbm);// 全局汇率精度
		FIDataTableUtil.setHeadItemDigi(hTable, MatterAppVO.ORG_CURRINFO, hlDigit);// 设置汇率精度
		FIDataTableUtil.setHeadItemDigi(hTable, MatterAppVO.GROUP_CURRINFO, grouphlDigit);// 设置集团汇率精度
		FIDataTableUtil.setHeadItemDigi(hTable, MatterAppVO.GLOBAL_CURRINFO, globalhlDigit);// 设置全局汇率精度
		// 设置汇率字段的可编辑性
		boolean[] hlEnableArr = getHLEnableArr(pk_group, bzbm, orgLocalCurrPK, groupCurrPK, globalCurrPk);
		boolean hlEnable = hlEnableArr[0];
		boolean groupHlEnable = hlEnableArr[1];
		boolean globalHlEnable = hlEnableArr[2];
		FIDataTableUtil.setHeadItemEnabled(hTable, MatterAppVO.ORG_CURRINFO, hlEnable);
		FIDataTableUtil.setHeadItemEnabled(hTable, MatterAppVO.GROUP_CURRINFO, groupHlEnable);
		FIDataTableUtil.setHeadItemEnabled(hTable, MatterAppVO.GLOBAL_CURRINFO, globalHlEnable);
		// 设置金额精度
		int ybDigit = Currency.getCurrDigit(bzbm);// 原币精度
		int bbDigit = Currency.getCurrDigit(orgLocalCurrPK);// 组织本币精度
		int groupbbDigit = Currency.getCurrDigit(groupCurrPK);// 集团本币精度
		int globalbbDigit = Currency.getCurrDigit(globalCurrPk);// 全局本币精度
		setHeadItemDigit(hTable, AggMatterAppVO.getHeadYbAmounts(), ybDigit);
		setHeadItemDigit(hTable, AggMatterAppVO.getHeadOrgAmounts(), bbDigit);
		setHeadItemDigit(hTable, AggMatterAppVO.getHeadGroupAmounts(), groupbbDigit);
		setHeadItemDigit(hTable, AggMatterAppVO.getHeadGlobalAmounts(), globalbbDigit);
		// 设置表头金额的默认值
		setHeadItemDefaultValue(hTable, AggMatterAppVO.getHeadAmounts(), UFDouble.ZERO_DBL);
		for (DataTable bTable : bTableArr) {
			// 设置表体币种、汇率和精度
			bTable.updateMeta(Func.toString(MtAppDetailVO.PK_CURRTYPE), ERMWebConst.FIELD_DEFAULT, bzbm);// 表体币种默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.ORG_CURRINFO), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[0]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.ORG_CURRINFO), ERMWebConst.FIELD_PRECISION,
					String.valueOf(hlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.ORG_CURRINFO), ERMWebConst.FIELD_ENABLE,
					String.valueOf(hlEnable));// 表体汇率可编辑性默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GROUP_CURRINFO), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[1]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GROUP_CURRINFO), ERMWebConst.FIELD_PRECISION,
					String.valueOf(grouphlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GROUP_CURRINFO), ERMWebConst.FIELD_ENABLE,
					String.valueOf(groupHlEnable));// 表体汇率可编辑性默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GLOBAL_CURRINFO), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(hlArr[2]));// 表体汇率默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GLOBAL_CURRINFO), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalhlDigit));// 表体汇率精度默认值
			bTable.updateMeta(Func.toString(MtAppDetailVO.GLOBAL_CURRINFO), ERMWebConst.FIELD_ENABLE,
					String.valueOf(globalHlEnable));// 表体汇率可编辑性默认值
			updateMetaForMultiField(bTable, AggMatterAppVO.getBodyYbAmounts(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(ybDigit));
			updateMetaForMultiField(bTable, AggMatterAppVO.getBodyOrgAmounts(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(bbDigit));
			updateMetaForMultiField(bTable, AggMatterAppVO.getBodyGroupAmounts(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(groupbbDigit));
			updateMetaForMultiField(bTable, AggMatterAppVO.getBodyGlobalAmounts(), ERMWebConst.FIELD_PRECISION,
					String.valueOf(globalbbDigit));
			updateMetaForMultiField(bTable, AggMatterAppVO.getBodyAmounts(), ERMWebConst.FIELD_DEFAULT,
					String.valueOf(UFDouble.ZERO_DBL));// 设置默认值为0
			for(String defaultField : defaultFieldArr){
				bTable.updateMeta(defaultField, ERMWebConst.FIELD_DEFAULT, 
						Func.toString(ERMDataTableUtil.getHeadValue(hTable, defaultField)));
				FIDataTableUtil.setBodyValueByRow(bTable.getCurrentRow(), defaultField, Func.toString(FIDataTableUtil.getHeadValue(hTable, defaultField)));
			}
			// 设置金额的默认值，因为上面设置默认值的代码对表体当前行无效，需要手工设置
			setBodyItemDefaultValue(bTable.getCurrentRow(), AggMatterAppVO.getBodyAmounts(), UFDouble.ZERO_DBL);
			for (int i = 0; i < bTable.getAllRow().length; i++) {// 这里不排除当前行，处理精度
				Row row = bTable.getAllRow()[i];
				String bzbm_busitem = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, MtAppDetailVO.PK_CURRTYPE));// 表体币种
				if (null == bzbm_busitem) {
					bzbm_busitem = bzbm;
					FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.PK_CURRTYPE, bzbm_busitem);
				}
				UFDouble[] hlBusitemArr = ErmBillCalUtil.getRate(bzbm_busitem, pk_org, pk_group, djrq);// 取汇率
				FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.ORG_CURRINFO, hlBusitemArr[0]);
				FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.GROUP_CURRINFO, hlBusitemArr[1]);
				FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.GLOBAL_CURRINFO, hlBusitemArr[2]);
				setBodyConfigFY(pk_group, pk_org, djrq, row);// 表体行币种变化后处理
//				FIDataTableUtil.setBodyValueByRow(row, MtAppDetailVO.PK_IOBSCLASS, Func.toString(FIDataTableUtil.getBodyValueByRow(row, MtAppDetailVO.PK_IOBSCLASS)));
			}
		}
		doBodyCalculationFY(hTable, bTableArr);
		doBodyTotalToHeadFY(hTable, bTableArr);

	}

	public static void afterEditReciver_busitem(DataTable bTable, Row row) throws Exception {
		/*
		 * 收款人联动： 1:刷新表体收款人
		 * 2:收款人联动个人银行账户，查找收款人对应的银行行号，如果收款人为空清空个人银行账号，如果收款人对应的银行账户为空清空个人银行账户
		 * ，否则依据收款人对应的个人银行账户赋值 3:一句空收款人重设个人银行账户参照
		 */
		Object receiver = ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.RECEIVER_BUSITEM);
		// 表头收款人编辑后联动个人银行账户,如果收款人为空则清空个人银行账户
		if (receiver != null && !"".equals(receiver)) {
			String receiverString = Func.toString(receiver);
			BankAccbasVO bank = NCLocator.getInstance().lookup(IPsnBankaccPubService.class)
					.queryDefaultBankAccByPsnDoc(receiverString);
			if (bank != null && bank.getBankaccsub() != null) {
				String pk_bankaccsub = bank.getBankaccsub()[0].getPk_bankaccsub();
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.SKYHZH_BUSITEM, pk_bankaccsub);
			} else {
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.SKYHZH_BUSITEM, null);
			}
		} else {
			FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.SKYHZH_BUSITEM, null);
		}
		ERMRefUtil.dealSkyhzhFiledBodyRow(bTable, row, IBillFieldGet.SKYHZH_BUSITEM);

	}

	public static void afterEditAssume_org(DataTable bTable, Row row) throws Exception {
		String assume_org = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, MtAppDetailVO.ASSUME_ORG));// 组织
		JSONObject refParam = new JSONObject();
		refParam.put("pk_org", null == assume_org ? "null" : assume_org);// 主组织
		FIDataTableUtil.setDataTableRefParam(bTable, MtAppDetailVO.PK_PROJECT, refParam);
	}

	public static void afterEditHeadPKORGFY(DataTable hTable, DataTable[] bTableArr, String pk_org) throws Exception {
		String uistate = IWebViewContext.getEventParameter(ERMWebConst.UISTATUS);
		Map<String, String> clientAttrs = IWebViewContext.getRequest().getEnvironment().getClientAttributes();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		String pk_user = InvocationInfoProxy.getInstance().getUserId();
		String pk_tradetype = clientAttrs.get("tradetype");
		FYSQBillVO billvo = (FYSQBillVO) FYSQOrgChangedLoadDefData.dealDefValue(clientAttrs, pk_group, pk_org, pk_user,
				ERMWebConst.INIT_LOADDEF);
		CircularlyAccessibleValueObject headvo = billvo.getParentVO();
		CircularlyAccessibleValueObject bodyvo = billvo.getChildrenVO()[0];
		FYSQOrgChangedLoadDefData.dataTrans(headvo, new DataTable[] { hTable }, uistate);
		FYSQOrgChangedLoadDefData.dataTrans(bodyvo, bTableArr, uistate);
		setPkOrg2HeadTableFY(hTable, bTableArr, pk_org);// 切换组织后给HeadTable赋值
		setHeadPK_CURRTYPE(hTable);
		DataChangeLogic.afterEditHeadPK_CURRTYPE(hTable, bTableArr);// 触发表头币种日期编辑后事件
		// 设置页面参照过滤条件（默认值无事件）
		FYSQOrgChangedLoadDefData.setDefRefParamForFysq(hTable, bTableArr, pk_tradetype);
	}

	private static void setHeadPK_CURRTYPE(DataTable hTable) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, MatterAppVO.PK_ORG));// 组织
		String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);// 组织本币
		FIDataTableUtil.setHeadValue(hTable, MatterAppVO.PK_CURRTYPE, orgLocalCurrPK);// 更新表头的币种
	}

	private static void setPkOrg2HeadTableFY(DataTable hTable, DataTable[] bTableArr, String pk_org) throws Exception {
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.PK_ORG, pk_org);
		hTable.updateMeta(IBillFieldGet.PK_ORG, ERMWebConst.FIELD_DEFAULT, pk_org);
		String className = MetaDataProcessUtil.getClassname(hTable.getCls());
		Object pk_org_v = MetaDataProcessUtil.getRelationItemValue(className, IBillFieldGet.PK_ORG, "pk_vid", pk_org);
		FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.PK_ORG_V, pk_org_v);
		hTable.updateMeta(IBillFieldGet.PK_ORG_V, ERMWebConst.FIELD_DEFAULT, Func.toString(pk_org_v));
		for (DataTable bTable : bTableArr) {
			bTable.updateMeta(IBillFieldGet.PK_ORG, ERMWebConst.FIELD_DEFAULT, pk_org);
			Row[] rows = bTable.getAllRow();
			for (Row row : rows) {
				row.getField(IBillFieldGet.PK_ORG).setValue(pk_org);
			}
		}
	}

	public static void afterEditShareAssumeOrg(DataTable hTable, DataTable[] bTableArr, DataTable shareTable)
			throws Exception {
		Map<String, String> clientAttrs = IWebViewContext.getRequest().getEnvironment().getClientAttributes();
		String pk_tradetype = clientAttrs.get("tradetype");
		String pkOrg = Func.toString(FIDataTableUtil.getBodyValue(shareTable, IBillFieldGet.ASSUME_ORG_SHARE));
		FIDataTableUtil.dealCardRelationItem(shareTable, IBillFieldGet.ASSUME_DEPT_SHARE, null);// 清空部门
		FIDataTableUtil.dealCardRelationItem(shareTable, IBillFieldGet.PK_IOBSCLASS_SHARE, null);// 清空收支项目
		FIDataTableUtil.dealCardRelationItem(shareTable, IBillFieldGet.JOBID_SHARE, null);// 清空项目
		ERMRefUtil.dealShareSzxmFiled(hTable, new DataTable[] { shareTable }, new String[] {
				IBillFieldGet.ASSUME_DEPT_SHARE, IBillFieldGet.PK_IOBSCLASS_SHARE, IBillFieldGet.JOBID_SHARE },
				pk_tradetype, pkOrg);
	}
	
	/**
	 * 表头收款对象编辑后处理
	 * @param hTable
	 * @param bTableArr
	 * @param shareTable
	 * @throws Exception 
	 */
	public static void afterEditHeadPAYTARGET(DataTable hTable,DataTable[] bTableArr, DataTable shareTable) throws Exception {
		//收款对象
		String paytarget = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PAYTARGET));
		//供应商
		String hbbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.HBBM));
		//客户
		String customer = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.CUSTOMER));	
		//员工
		if("0".equals(paytarget)){
			//清空客商银行账户
			FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.CUSTACCOUNT, null);
			FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.CUSTACCOUNT, false);
			FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.SKYHZH, true);
		//供应商
		}else if("1".equals(paytarget)){
			//清空个人银行账户
			FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.SKYHZH, null);
			FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.CUSTACCOUNT, true);
			FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.SKYHZH, false);
			//表头供应商编辑后处理
			afterEditHeadHBBM(hTable, bTableArr, shareTable);
			
		//客户
		}else if("2".equals(paytarget)){
			//清空个人银行账户
			FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.SKYHZH, null);
			FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.CUSTACCOUNT, true);
			FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.SKYHZH, false);
			//表头客户编辑后处理
			afterEditHeadCUSTOMER(hTable, bTableArr, shareTable);
		}
		
		// 刷新表体收款对象
		for (DataTable bTable : bTableArr) {
			Row[] rows = bTable.getAllRow();
			for (Row row : rows) {
				if (row.getFields().containsKey(IBillFieldGet.PAYTARGET_BUSITEM)) {
					row.getField(IBillFieldGet.PAYTARGET_BUSITEM).setValue(paytarget);
					afterEditBodyPAYTARGET(bTable, row);
				}
			}
			bTable.updateMeta(IBillFieldGet.HBBM_BUSITEM, ERMWebConst.FIELD_DEFAULT, paytarget);
		}
		
	}
	
	/**
	 * 表体收款对象变化后处理
	 * @see 通过判断付款对象的值，为客商银行账户设置参照过滤条件
	 * @param bTable
	 * @param row
	 * @throws Exception
	 */
	public static void afterEditBodyPAYTARGET(DataTable bTable, Row row) throws Exception {
		//付款对象
		String paytarget = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.PAYTARGET_BUSITEM));
		//供应商
		String hbbm = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.HBBM_BUSITEM));
		//客户
		String customer = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.CUSTOMER_BUSITEM));
		//员工
		if("0".equals(paytarget)){
			//清空客商银行账户
			FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.CUSTACCOUNT_BUSITEM, null);
			FIDataTableUtil.setBodyItemEnabledForCur(bTable, IBillFieldGet.CUSTACCOUNT_BUSITEM, false);
			FIDataTableUtil.setBodyItemEnabledForCur(bTable, IBillFieldGet.SKYHZH_BUSITEM, true);
		//供应商
		}else if("1".equals(paytarget)){
			//清空个人银行账户
			FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.SKYHZH_BUSITEM, null);
			//设置客商银行账户项目参照条件
			afterEditBodyHBBM(bTable, row);
			FIDataTableUtil.setBodyItemEnabledForCur(bTable, IBillFieldGet.CUSTACCOUNT_BUSITEM, true);
			FIDataTableUtil.setBodyItemEnabledForCur(bTable, IBillFieldGet.SKYHZH_BUSITEM, false);
		//客户
		}else if("2".equals(paytarget)){
			//清空个人银行账户
			FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.SKYHZH_BUSITEM, null);
			//设置客商银行账户项目参照条件
			afterEditBodyCUSTOMER(bTable, row);
			FIDataTableUtil.setBodyItemEnabledForCur(bTable, IBillFieldGet.CUSTACCOUNT_BUSITEM, true);
			FIDataTableUtil.setBodyItemEnabledForCur(bTable, IBillFieldGet.SKYHZH_BUSITEM, false);
		}
	}

	/**
	 * 表头供应商编辑后处理
	 * @param hTable
	 * @param bTableArr
	 * @param shareTable
	 * @throws Exception 
	 */
	public static void afterEditHeadHBBM(DataTable hTable, DataTable[] bTableArr,DataTable shareTable) throws Exception {
		//供应商
		String hbbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.HBBM));
		String paytarget = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PAYTARGET));
		
		if("1".equals(paytarget)){
			if (hbbm != null && !"".equals(hbbm)) {
				String hbbmString = Func.toString(hbbm);
				String bzbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.BZBM));
				//获取银行账号
				String pk_bankaccsub = getDefaultCustaccount(hbbmString, "3", bzbm);
				//银行账号非空处理
				if (StringUtil.isNotNull(pk_bankaccsub)) {
					//将银行账号赋值给客商银行账户
					FIDataTableUtil.setHeadValue(hTable,IBillFieldGet.CUSTACCOUNT, pk_bankaccsub);
				} else {
					//将空赋值给客商银行账户
					FIDataTableUtil.setHeadValue(hTable,IBillFieldGet.CUSTACCOUNT, null);
				}
			} else{
				//将空赋值给客商银行账户
				FIDataTableUtil.setHeadValue(hTable,IBillFieldGet.CUSTACCOUNT, null);
			}
			//设置客商银行账户参照过滤条件
			ERMRefUtil.dealCustaccountFiled(hTable, IBillFieldGet.CUSTACCOUNT,hbbm);
		}
		//刷新表体供应商
		for (DataTable bTable : bTableArr) {
			Row[] rows = bTable.getAllRow();
			for (Row row : rows) {
				if (row.getFields().containsKey(IBillFieldGet.HBBM_BUSITEM)) {
					row.getField(IBillFieldGet.HBBM_BUSITEM).setValue(hbbm);
					afterEditBodyHBBM(bTable, row);
				}
			}
			bTable.updateMeta(IBillFieldGet.HBBM_BUSITEM, ERMWebConst.FIELD_DEFAULT, hbbm);
		}
		
		//收款对象为客商 且 选择有散户属性的客商后散户可编辑
		SupplierVO supplierVO = ApplyPubProxy.getMDQueryService().queryBillOfVOByPK(SupplierVO.class, Func.toString(hbbm), false);
		if(null != supplierVO && null != supplierVO.getIsfreecust() && supplierVO.getIsfreecust().toString().equals("Y")){
			FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.FREECUST, true);
		}else{
			FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.FREECUST, null);
			FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.FREECUST, false);
		}
		
	}
	
	/**
	 * 表体供应商变化后处理
	 * @see 设置客商银行账户项目参照条件
	 * @param bTable
	 * @param row
	 * @throws Exception
	 */
	public static void afterEditBodyHBBM(DataTable bTable, Row row) throws Exception {
		//供应商
		Object hbbm = ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.HBBM_BUSITEM);
		String paytarget = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.PAYTARGET_BUSITEM));
		 	
		
		if("1".equals(paytarget)){
			//供应商
			if (hbbm != null && !"".equals(hbbm)) {
				String hbbmString = Func.toString(hbbm);
				String bzbm = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.BZBM_BUSITEM));
				//获取银行账号
				String pk_bankaccsub = getDefaultCustaccount(hbbmString, "3", bzbm);
				//银行账号非空处理
				if (StringUtil.isNotNull(pk_bankaccsub)) {
					//将银行账号赋值给客商银行账户
					FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.CUSTACCOUNT_BUSITEM, pk_bankaccsub);
				} else {
					//将空赋值给客商银行账户
					FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.CUSTACCOUNT_BUSITEM, null);
				}
			}else{
				//将空赋值给客商银行账户
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.CUSTACCOUNT_BUSITEM, null);
			}
			//设置客商银行账户参照过滤条件
			ERMRefUtil.dealCustaccountFiledBodyRow(bTable, row, IBillFieldGet.CUSTACCOUNT_BUSITEM,Func.toString(hbbm));
		}
		//收款对象为客商 且 选择有散户属性的客商后散户可编辑
		SupplierVO supplierVO = ApplyPubProxy.getMDQueryService().queryBillOfVOByPK(SupplierVO.class, Func.toString(hbbm), false);
		if(null != supplierVO && null != supplierVO.getIsfreecust() && supplierVO.getIsfreecust().toString().equals("Y")){
			FIDataTableUtil.setBodyItemEnabledForCur(bTable, IBillFieldGet.FREECUST_BUSITEM, true);
		}else{
			//清空散户
			FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.FREECUST_BUSITEM, null);
			FIDataTableUtil.setBodyItemEnabledForCur(bTable, IBillFieldGet.FREECUST_BUSITEM, false);
		}
	}
	
	/**
	 * 表头客户编辑后处理
	 * @see 设置客商银行账户项目参照条件
	 * @param bTable
	 * @param row
	 * @throws Exception
	 */
	public static void afterEditHeadCUSTOMER(DataTable hTable, DataTable[] bTableArr,DataTable shareTable) throws Exception {
		String customer = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.CUSTOMER));
		String paytarget = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PAYTARGET));
		
		if("2".equals(paytarget)){
			//客户
			if (customer != null && !"".equals(customer)) {
				String customerString = Func.toString(customer);
				String bzbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.BZBM));
				//获取银行账号
				String pk_bankaccsub = getDefaultCustaccount(customerString, "1", bzbm);
				if (StringUtil.isNotNull(pk_bankaccsub)) {
					//将银行账号赋值给客商银行账户
					FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.CUSTACCOUNT, pk_bankaccsub);
				}else{
					//将空赋值给客商银行账户
					FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.CUSTACCOUNT, null);
				}
			}else{
				//将空赋值给客商银行账户
				FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.CUSTACCOUNT, null);
			}
			//设置客商银行账户参照过滤条件
			ERMRefUtil.dealCustaccountFiled(hTable, IBillFieldGet.CUSTACCOUNT,customer);
		}
		//刷新表体客户
		for (DataTable bTable : bTableArr) {
			Row[] rows = bTable.getAllRow();
			for (Row row : rows) {
				if (row.getFields().containsKey(IBillFieldGet.CUSTOMER_BUSITEM)) {
					row.getField(IBillFieldGet.CUSTOMER_BUSITEM).setValue(customer);
					afterEditBodyCUSTOMER(bTable, row);
				}
			}
			bTable.updateMeta(IBillFieldGet.CUSTOMER_BUSITEM, ERMWebConst.FIELD_DEFAULT, customer);
		}
		
	}
	
	/**
	 * 表体客户编辑后处理
	 * @see 设置客商银行账户项目参照条件
	 * @param bTable
	 * @param row
	 * @throws Exception
	 */
	public static void afterEditBodyCUSTOMER(DataTable bTable, Row row) throws Exception {
		Object customer = ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.CUSTOMER_BUSITEM);
		String paytarget = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.PAYTARGET_BUSITEM));
		
		
		if("2".equals(paytarget)){
			//客户
			if (customer != null && !"".equals(customer)) {
				String customerString = Func.toString(customer);
				String bzbm = Func.toString(ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.BZBM_BUSITEM));
				//获取银行账号
				String pk_bankaccsub = getDefaultCustaccount(customerString, "1", bzbm);
				if (StringUtil.isNotNull(pk_bankaccsub)) {
					//将银行账号赋值给客商银行账户
					FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.CUSTACCOUNT_BUSITEM, pk_bankaccsub);
				}else{
					//将空赋值给客商银行账户
					FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.CUSTACCOUNT_BUSITEM, null);
				}
			} else{
				//将空赋值给客商银行账户
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.CUSTACCOUNT_BUSITEM, null);
			}
			//设置客商银行账户参照过滤条件
			ERMRefUtil.dealCustaccountFiledBodyRow(bTable, row, IBillFieldGet.CUSTACCOUNT_BUSITEM,Func.toString(customer));
		}
	}
	
	/**
	 * 表头客商银行账户编辑后处理
	 * @param hTable
	 * @param bTableArr
	 * @param shareTable
	 */
	public static void afterEditHeadCUSTACCOUNT(DataTable hTable,DataTable[] bTableArr, DataTable shareTable) {
		//刷新表体客商银行账户
		String custaccount = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.CUSTACCOUNT));
		for (DataTable bTable : bTableArr) {
			FIDataTableUtil.dealCardRelationItem(bTable, IBillFieldGet.CUSTACCOUNT_BUSITEM, custaccount);
		}
	}
	/**
	 * 批量设置表体默认值 表体有值同样联动赋值
	 * 
	 * @param row
	 * @param fieldArr
	 * @param value
	 */
	private static void setBodyItemDefaultValue2(Row row, String[] fieldArr, Object value) {
		try {
			for (String field : fieldArr) {
				if (null != row.getField(field) || null == row.getField(field).getValue()) {
					ERMDataTableUtil.setBodyValueByRow(row, field, value);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}
	
	private static void setFreecustEnabled(DataTable table) throws Exception {
		String supplier = Func.toString(FIDataTableUtil.getHeadValue(table, IBillFieldGet.HBBM));
		String customer = Func.toString(FIDataTableUtil.getHeadValue(table, IBillFieldGet.CUSTOMER));
		boolean supplierIsfreecust = false;
		if(ERMValueCheck.isNotEmpty(supplier)){
			supplierIsfreecust = CheckIsFreeCust.checkcustsuppIsFreeCust("supplier", new String[]{supplier}) ; 
		}
		boolean customerIsfreecust = false;
		if(ERMValueCheck.isNotEmpty(customer)){
			customerIsfreecust = CheckIsFreeCust.checkcustsuppIsFreeCust("customer", new String[]{customer}) ; 
		}

		if(supplierIsfreecust || customerIsfreecust){
			FIDataTableUtil.setHeadItemEnabled(table, IBillFieldGet.FREECUST, true);
		}else{
			FIDataTableUtil.setHeadItemEnabled(table, IBillFieldGet.FREECUST, false);
		}
	}
	
	public static void afterEditCustSupplier(String field,String tableID,DataTable hTable, DataTable[] bTableArr)
			throws Exception {
		String newValue = IWebViewContext.getEventParameter("newValue");
		if("headform".equals(tableID)){
			String supplier = Func.toString(FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.HBBM));
			String customer = Func.toString(FIDataTableUtil.getHeadValue(hTable, IBillFieldGet.CUSTOMER));
			boolean supplierIsfreecust = false;
			if(ERMValueCheck.isNotEmpty(supplier)){
				supplierIsfreecust = CheckIsFreeCust.checkcustsuppIsFreeCust("supplier", new String[]{supplier}) ; 
			}
			boolean customerIsfreecust = false;
			if(ERMValueCheck.isNotEmpty(customer)){
				customerIsfreecust = CheckIsFreeCust.checkcustsuppIsFreeCust("customer", new String[]{customer}) ; 
			}
	
			if(supplierIsfreecust || customerIsfreecust){
				FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.FREECUST, true);
			}else{
				FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.FREECUST, false);
				FIDataTableUtil.setHeadValue(hTable, IBillFieldGet.FREECUST, null);
			}
			ERMRefUtil.dealFreecustFiled(hTable, IBillFieldGet.FREECUST, field);//参照处理
			for (DataTable bTable : bTableArr) {
				setBodyItemDefaultValue2(bTable.getCurrentRow(), new String[]{field}, newValue);
				bTable.updateMeta(field, ERMWebConst.FIELD_DEFAULT, newValue);
				if(supplierIsfreecust || customerIsfreecust){
					FIDataTableUtil.setBodyItemEnabledForAll(bTable,IBillFieldGet.FREECUST,true);
				}else{
					FIDataTableUtil.setBodyItemEnabledForAll(bTable,IBillFieldGet.FREECUST,false);
					for(Row row : bTable.getAllRow()){
						FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.FREECUST, null);
					}
				}
				ERMRefUtil.dealFreecustFiled(bTable, IBillFieldGet.FREECUST, field);//参照处理
			}
			
		}else{
			DataTable bTable = FIDataTableUtil.getDataTable(tableID);
			String supplier = Func.toString(FIDataTableUtil.getBodyValue(bTable, IBillFieldGet.HBBM));
			String customer = Func.toString(FIDataTableUtil.getBodyValue(bTable, IBillFieldGet.CUSTOMER));
			boolean supplierIsfreecust = false;
			if(ERMValueCheck.isNotEmpty(supplier)){
				supplierIsfreecust = CheckIsFreeCust.checkcustsuppIsFreeCust("supplier", new String[]{supplier}) ; 
			}
			boolean customerIsfreecust = false;
			if(ERMValueCheck.isNotEmpty(customer) ){
				customerIsfreecust = CheckIsFreeCust.checkcustsuppIsFreeCust("customer", new String[]{customer}) ; 
			}
			if(supplierIsfreecust || customerIsfreecust){
				FIDataTableUtil.setBodyItemEnabledForAll(bTable,IBillFieldGet.FREECUST,true);
			}else{
				FIDataTableUtil.setBodyItemEnabledForAll(bTable,IBillFieldGet.FREECUST,false);
			}
			for(Row row : bTable.getAllRow()){
				FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.FREECUST, null);
			}
			ERMRefUtil.dealFreecustFiled(bTable, IBillFieldGet.FREECUST, field);//参照处理
		}
		
		
	}
	
	private static String getDefaultCustaccount(String pk_cust, String accclass, String pk_currtype) throws DAOException {
		String pk_bankaccsub = null ;
		String sql  = "SELECT a.pk_bankaccsub FROM BD_CUSTBANK a,BD_BANKACCSUB b WHERE a.pk_bankACCSUB = b.PK_BANKACCSUB"
				+ " AND a.ISDEFAULT = 'Y' AND a.PK_CUST = '" + pk_cust + "' "
				+ " AND a.ACCCLASS = "+accclass+" AND b.pk_currtype = '"+pk_currtype+"' ORDER BY a.ACCCLASS ";
		BaseDAO dao = new BaseDAO();
		List<Map<String,String>> list = (List<Map<String,String>>)dao.executeQuery(sql, new MapListProcessor());
		if(null!=list&&list.size()>0){
			pk_bankaccsub = list.get(0).get("pk_bankaccsub");
		}
		return pk_bankaccsub;
		
	}
	
	private static String getFreeCustAccount(String freecust) throws DAOException{
		String sql = "SELECT bankaccount FROM bd_freecustom WHERE pk_freecustom = '"+freecust+"'";
		BaseDAO dao = new BaseDAO();
		List<Map<String,String>> list = (List<Map<String,String>>)dao.executeQuery(sql, new MapListProcessor());
		if(null!=list&&list.size()>0){
			return list.get(0).get("bankaccount");
		}else{
			return null;
		}
	}
	
	//处理对公支付相关字段的可编辑性和参照过滤
	public static void dealSomefield4PayToPublic(DataTable hTable,DataTable[] bTableArr) throws Exception{
		String paytarget = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PAYTARGET));
		String hbbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.HBBM));
		String customer = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.CUSTOMER));
		//员工
		if("0".equals(paytarget)){
			FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.CUSTACCOUNT, false);
			FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.SKYHZH, true);
		//供应商
		}else if("1".equals(paytarget)){
			FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.CUSTACCOUNT, true);
			FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.SKYHZH, false);
			//设置客商银行账户参照过滤条件
			ERMRefUtil.dealCustaccountFiled(hTable, IBillFieldGet.CUSTACCOUNT,hbbm);
		//客户
		}else if("2".equals(paytarget)){
			FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.CUSTACCOUNT, true);
			FIDataTableUtil.setHeadItemEnabled(hTable, IBillFieldGet.SKYHZH, false);
			//设置客商银行账户参照过滤条件
			ERMRefUtil.dealCustaccountFiled(hTable, IBillFieldGet.CUSTACCOUNT,customer);
		}
		//散户可编辑性处理
		setFreecustEnabled(hTable);
		
		//表体
		for (DataTable btable : bTableArr) {
			//员工
			if("0".equals(paytarget)){
				FIDataTableUtil.setBodyItemEnabledForAll(btable, IBillFieldGet.CUSTACCOUNT_BUSITEM, false);
				FIDataTableUtil.setBodyItemEnabledForAll(btable, IBillFieldGet.SKYHZH_BUSITEM, true);
			//供应商
			}else if("1".equals(paytarget)){
				FIDataTableUtil.setBodyItemEnabledForAll(btable, IBillFieldGet.CUSTACCOUNT_BUSITEM, true);
				FIDataTableUtil.setBodyItemEnabledForAll(btable, IBillFieldGet.SKYHZH_BUSITEM, false);
				for (Row row : btable.getAllRow()) {
					//设置客商银行账户参照过滤条件
					ERMRefUtil.dealCustaccountFiledBodyRow(btable, row, IBillFieldGet.CUSTACCOUNT_BUSITEM,hbbm);
				}
			//客户
			}else if("2".equals(paytarget)){
				FIDataTableUtil.setBodyItemEnabledForAll(btable, IBillFieldGet.CUSTACCOUNT_BUSITEM, true);
				FIDataTableUtil.setBodyItemEnabledForAll(btable, IBillFieldGet.SKYHZH_BUSITEM, false);
				for (Row row : btable.getAllRow()) {
					//设置客商银行账户参照过滤条件
					ERMRefUtil.dealCustaccountFiledBodyRow(btable, row, IBillFieldGet.CUSTACCOUNT_BUSITEM,customer);
				}
			}
			//散户处理
			setFreecustEnabled(btable);
			
		}
	}
	/**
	 * 报销单据表体自定义项（金额）编辑后处理
	 * 差旅费defitem7+defitem11+defitem12+defitem8
	 * 车辆费用defitem14+defitem11+defitem12+defitem4+defitem8+defitem7+defitem9+defitem15
	 * @param row
	 * @throws Exception
	 */
	public static void afterEditBodyDefitemJE(DataTable hTable, Row row) throws Exception {
		String pk_org = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_ORG));// 组织
		String pk_group = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.PK_GROUP));// 集团
		UFDate djrq = (UFDate) ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJRQ);// 单据日期
		String djlxbm = Func.toString(ERMDataTableUtil.getHeadValue(hTable, IBillFieldGet.DJLXBM));// 交易类型
		if("264X-Cxx-RQCL".equals(djlxbm)){
			// 差旅费报销 defitem7+defitem11+defitem12+defitem8
			// 自定义项7=长途交通费
			UFDouble defitem7 = new UFDouble(Double.parseDouble((String) (ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM7_BUSITEM) 
					== null ? "0.0" : ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM7_BUSITEM))));
			// 自定义项11=市内交通费
			UFDouble defitem11 = new UFDouble(Double.parseDouble((String) (ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM11_BUSITEM) 
					== null ? "0.0" : ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM11_BUSITEM))));
			// 自定义项12=住宿费
			UFDouble defitem12 = new UFDouble(Double.parseDouble((String) (ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM12_BUSITEM) 
					== null ? "0.0" : ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM12_BUSITEM))));
			// 自定义项8=出差补贴金额
			UFDouble defitem8 = new UFDouble(Double.parseDouble((String) (ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM8_BUSITEM) 
					== null ? "0.0" : ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM8_BUSITEM))));
			UFDouble totalmon = defitem7.add(defitem11.add(defitem12.add(defitem8)));
			Logger.error("合计=="+totalmon);
			// FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.AMOUNT_BUSITEM, totalmon);
			FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.VAT_AMOUNT_BUSITEM,totalmon);
		}else if("264X-Cxx-RQCLFY".equals(djlxbm)){
			// 车辆费用报销 defitem14+defitem11+defitem12+defitem4+defitem8+defitem7+defitem9+defitem15
			// 自定义项4=维修保养费
			UFDouble defitem4 = new UFDouble(Double.parseDouble((String) (ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM4_BUSITEM) 
					== null ? "0.0" : ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM4_BUSITEM))));
			// 自定义项14=停车费
			UFDouble defitem14 = new UFDouble(Double.parseDouble((String) (ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM14_BUSITEM) 
					== null ? "0.0" : ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM14_BUSITEM))));
			// 自定义项11=洗车及其它
			UFDouble defitem11 = new UFDouble(Double.parseDouble((String) (ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM11_BUSITEM) 
					== null ? "0.0" : ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM11_BUSITEM))));
			// 自定义项12=路桥费
			UFDouble defitem12 = new UFDouble(Double.parseDouble((String) (ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM12_BUSITEM) 
					== null ? "0.0" : ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM12_BUSITEM))));
			// 自定义项8=燃油费
			UFDouble defitem8 = new UFDouble(Double.parseDouble((String) (ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM8_BUSITEM) 
					== null ? "0.0" : ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM8_BUSITEM))));
			// 自定义项7=保险费
			UFDouble defitem7 = new UFDouble(Double.parseDouble((String) (ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM7_BUSITEM) 
					== null ? "0.0" : ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM7_BUSITEM))));
			// 自定义项9=车船税
			UFDouble defitem9 = new UFDouble(Double.parseDouble((String) (ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM9_BUSITEM) 
					== null ? "0.0" : ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM9_BUSITEM))));
			// 自定义项15=进项税金
			UFDouble defitem15 = new UFDouble(Double.parseDouble((String) (ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM15_BUSITEM) 
					== null ? "0.0" : ERMDataTableUtil.getBodyValueByRow(row, IBillFieldGet.DEFITEM15_BUSITEM))));
			UFDouble totalmon = defitem4.add(defitem14.add(defitem11.add(defitem12.add(defitem8.add(defitem7.add(defitem9.add(defitem15)))))));
			Logger.error("合计=="+totalmon);
			// FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.AMOUNT_BUSITEM, totalmon);
			FIDataTableUtil.setBodyValueByRow(row, IBillFieldGet.VAT_AMOUNT_BUSITEM,totalmon);
		}
		calTaxOnRow(pk_group, pk_org, djrq, row, ERMTaxUtil.CAL_TYPE_FROM_HS);// 税金计算
		setAmountAndYbjeByTax(pk_org, row);// 根据参数将含税或无税金额的值赋给AMOUNT和YBJE
		calBbOnRow(pk_group, pk_org, djrq, row);// 本币计算
	}
	
}
