package nc.vo.ep.bx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.pub.ErCorpUtil;
import nc.vo.er.pub.IFYControl;
import nc.vo.er.util.StringUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BeanHelper;
import nc.vo.pub.BusinessException;
import nc.vo.pub.NullFieldException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;

/**
 * 借款报销类单据表体VO
 *
 * @author ROCKING
 * @author twei
 *
 *         nc.vo.ep.bx.BXHeaderVO
 */
public abstract class JKBXHeaderVO extends SuperVO implements IFYControl {

	private static final long serialVersionUID = -936531187472578799L;

	/**
	 * 是否加载了常用单据
	 */
	private boolean isLoadInitBill = false;

	/**
	 * 按行回写费用申请单使用，记录回写申请单的明细行pk
	 */
	private String pk_mtapp_detail = null;

	/**
	 * 回写费用申请单使用，记录业务行pk
	 */
	private String pk_busitem = null;

	/**
	 * 回写费用申请单-冲销明细数据包装使用，冲销行中的借款单明细pk
	 */
	private String jk_busitemPK;
	/**
	 * 回写费用申请单-冲销明细数据包装使用，冲销行中的报销单明细pk
	 */
	private String bx_busitemPK;

	/**
	 * 支付组织
	 */
	public static String PK_PAYORG = "pk_payorg";
	public static String PK_PAYORG_V = "pk_payorg_v";
	/**
	 * 成本中心
	 */
	public static String PK_RESACOSTCENTER = "pk_resacostcenter";

	/**
	 * 现金帐户
	 */
	public static String PK_CASHACCOUNT = "pk_cashaccount";

	/**
	 * 现金帐户v6.1新增字段，和单位银行帐号二者其一传结算
	 */
	protected String pk_cashaccount;

	/**
	 * 成本中心v6.1新增字段，预算控制纬度
	 */
	protected String pk_resacostcenter;

	/**
	 * 结算信息表头前缀
	 */
	public static String SETTLE_HEAD_PREFIX = "zb.";

	/**
	 * 结算表头VO
	 */
	protected SuperVO settleHeadVO;

	/**
	 * 审核日期显示日期，不显示时间
	 */
	public static final String SHRQ_SHOW = "shrq_show";

	/**
	 * 审核日期显示日期，不显示时间
	 */
	public UFDate shrq_show;

	//@sscct@合同模块补丁合并增加--20170901--begin
	public static final String PK_CONTRACTNO = "pk_contractno";
	private String pk_contractno;
	public String getPk_contractno() {
		return pk_contractno;
	}
	public void setPk_contractno(String pk_contractno) {
		this.pk_contractno = pk_contractno;
	}
	//@sscct@合同模块补丁合并增加--20170901--end

	protected String[] assignUsers;

	public String[] getAssignUsers() {
		return assignUsers;
	}
	public void setAssignUsers(String[] assignUsers) {
		this.assignUsers = assignUsers;
	}
	/**
	 * 不进行初始化的字段, 用于控制常用单据的加载
	 *
	 * @return
	 */
	public static String[] getFieldNotInit() {
		return new String[] { JKBXR, DJLXBM, PK_GROUP, OPERATOR, MODIFIER, MODIFIEDTIME, CREATOR, CREATIONTIME,
				PK_JKBX, DJBH, DJRQ, TS, DJZT, KJND, KJQJ, PAYMAN, PAYDATE, PAYFLAG, PK_ORG_V, FYDWBM_V, DWBM_V,
				PK_PCORG_V, DEPTID_V, FYDEPTID_V, DWBM, DEPTID, BBHL, SKYHZH, JSR, APPROVER, START_PERIOD,
				RECEIVER};
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public void setGroupbbye(UFDouble groupbbye) {
		this.groupbbye = groupbbye;
	}

	/**
	 * 不进行拷贝的字段,用于控制单据的复制功能
	 *
	 * @return
	 */
	public static String[] getFieldNotCopy() {
		return new String[] { ZHRQ, SXBZ, JSH, DJZT, SPZT, TS, DR, MODIFIER, OPERATOR, APPROVER, MODIFIEDTIME,VOUCHER,
				PK_JKBX, DJBH, DJRQ, KJND, KJQJ, JSRQ, SHRQ, JSR, CONTRASTENDDATE, PAYMAN, PAYDATE, PAYFLAG ,BBHL,GROUPBBHL,GLOBALBBHL,
				START_PERIOD,SHRQ_SHOW,VOUCHERTAG, RED_STATUS, REDBILLPK, OFFICIALPRINTUSER, OFFICIALPRINTDATE,CREATOR};
	}

	/**
	 * 金额字段, 用于进行单据的合并
	 *
	 * @return
	 */
	public static String[] getJeField() {
		return new String[] { CJKYBJE, CJKBBJE, ZFYBJE, ZFBBJE, HKYBJE, HKBBJE, YBJE, BBJE, TOTAL, YBYE, BBYE,
				GLOBALCJKBBJE, GLOBALZFBBJE, GLOBALHKBBJE, GLOBALBBJE, GLOBALBBYE, GROUPCJKBBJE, GROUPZFBBJE,
				GROUPHKBBJE, GROUPBBJE, GROUPBBYE ,TAX_AMOUNT,VAT_AMOUNT,TNI_AMOUNT,ORGTAX_AMOUNT,ORGVAT_AMOUNT,ORGTNI_AMOUNT,GROUPTAX_AMOUNT,GROUPVAT_AMOUNT,GROUPTNI_AMOUNT,GLOBALTAX_AMOUNT,GLOBALVAT_AMOUNT,GLOBALTNI_AMOUNT};
	}

	/**
	 * 返回原币金额字段
	 *
	 * @return
	 */
	public static String[] getYbjeField() {
		return new String[] { CJKYBJE, ZFYBJE, HKYBJE, YBJE, TOTAL, YBYE, YJYE ,TAX_AMOUNT,VAT_AMOUNT,TNI_AMOUNT};
	}

	/**
	 * 返回本币金额字段
	 *
	 * @return
	 */
	public static String[] getBbjeField() {
		return new String[] { CJKBBJE, ZFBBJE, HKBBJE, BBJE, BBYE, GLOBALCJKBBJE, GLOBALZFBBJE, GLOBALHKBBJE,
				GLOBALBBJE, GLOBALBBYE, GROUPCJKBBJE, GROUPZFBBJE, GROUPHKBBJE, GROUPBBJE, GROUPBBYE,
				ORGTAX_AMOUNT,ORGVAT_AMOUNT,ORGTNI_AMOUNT,
				GROUPTAX_AMOUNT,GROUPVAT_AMOUNT,GROUPTNI_AMOUNT,
				GLOBALTAX_AMOUNT,GLOBALVAT_AMOUNT,GLOBALTNI_AMOUNT};
	}

	/**
	 * 返回组织本币金额字段
	 *
	 * @return
	 */
	public static String[] getOrgBbjeField() {
		return new String[] { CJKBBJE, ZFBBJE, HKBBJE, BBJE, BBYE ,ORGTAX_AMOUNT,ORGVAT_AMOUNT,ORGTNI_AMOUNT};
	}

	/**
	 * 返回表头集团本币金额字段
	 *
	 * @author chendya
	 * @return
	 */
	public static String[] getHeadGroupBbjeField() {
		return new String[] { GROUPCJKBBJE, GROUPZFBBJE, GROUPHKBBJE, GROUPBBJE, GROUPBBYE ,GROUPTAX_AMOUNT,GROUPVAT_AMOUNT,GROUPTNI_AMOUNT};
	}

	/**
	 *
	 * 返回表头全局本币金额字段
	 *
	 * @author chendya
	 * @return
	 */
	public static String[] getHeadGlobalBbjeField() {
		return new String[] { GLOBALCJKBBJE, GLOBALZFBBJE, GLOBALHKBBJE, GLOBALBBJE, GLOBALBBYE ,GLOBALTAX_AMOUNT,GLOBALVAT_AMOUNT,GLOBALTNI_AMOUNT};
	}

	/**
	 * <p>
	 * 取得表主键.
	 * <p>
	 * 创建日期:2007-6-13
	 *
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getPKFieldName() {
		return PK_JKBX;
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2007-6-13
	 *
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getTableName() {

		if (isInit)
			return "er_jkbx_init";

		String tableName = "er_bxzb";

		if (djdl != null) {
			if (djdl.equals(BXConstans.JK_DJDL))
				tableName = "er_jkzb";
		}
		return tableName;

	}

	/**
	 * 返回数值对象的显示名称.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return java.lang.String 返回数值对象的显示名称.
	 */
	@Override
	public String getEntityName() {

		return "er_bxzb";

	}

	/**
	 * 验证对象各属性之间的数据逻辑正确性.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @exception nc.vo.pub.ValidationException
	 *                如果验证失败,抛出 ValidationException,对错误进行解释.
	 */
	@Override
	public void validate() throws ValidationException {

		validateNullField();
		validateNotNullField2();
	}

	protected void validateNotNullField2() {

		Map<String, String> map = new HashMap<String, String>();
		map.put(JKBXHeaderVO.YBJE, JKBXHeaderVO.ZPXE);

		StringBuffer message = new StringBuffer();
		message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000285")/*
																									 * @
																									 * res
																									 * "表头下列字段不能同时为空:"
																									 */);

		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (getFieldName(key) == null && null == getFieldName(value)) {
				message.append("\n");
				message.append(key + "-" + value);
			}
		}
	}

	protected String djlxmc;

	public String getDjlxmc() {
		return djlxmc;
	}

	public void setDjlxmc(String djlxmc) {
		this.djlxmc = djlxmc;
	}

	protected void validateNullField() throws NullFieldException {
		ArrayList<String> errFields = new ArrayList<String>(); // errFields
																// record those
																// null
		List<String> notNullFields = null; // errFields record those null
		// FIXME 暂时注销掉
		String[] str = { JKBXHeaderVO.DJRQ, JKBXHeaderVO.DWBM, JKBXHeaderVO.JKBXR, JKBXHeaderVO.BZBM,
				JKBXHeaderVO.BBHL, JKBXHeaderVO.YBJE, JKBXHeaderVO.BBJE, JKBXHeaderVO.YBYE, JKBXHeaderVO.BBYE,
				JKBXHeaderVO.FYDWBM, JKBXHeaderVO.PK_ORG, JKBXHeaderVO.OPERATOR, JKBXHeaderVO.PK_GROUP, };
		notNullFields = Arrays.asList(str);

		for (String field : notNullFields) {
			if (getAttributeValue(field) == null)
				errFields.add(getFieldName(field));
		}

		StringBuffer message = new StringBuffer();
		message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000286")/*
																									 * @
																									 * res
																									 * "表头下列字段不能为空:\n"
																									 */);
		if (errFields.size() > 0) {
			String[] temp = errFields.toArray(new String[0]);
			message.append(temp[0]);
			for (int i = 1; i < temp.length; i++) {
				message.append(",");
				message.append(temp[i]);
			}
			throw new NullFieldException(message.toString());
		}
	}

	public String getFieldName(String field) {
		// 注意下面组织和集团加多语
		if (field.equals(PK_ORG))
			if (djdl.equals(BXConstans.BX_DJDL))
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0131")/*
																											 * @
																											 * res
																											 * "报销单位"
																											 */;
			else
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0132")/*
																											 * @
																											 * res
																											 * "借款单位"
																											 */;
		else if (field.equals(PK_GROUP))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC001-0000072")/*
																									 * @
																									 * res
																									 * "集团"
																									 */;
		// public static final String CUSTACCOUNT = "custaccount";
		// public static final String FREECUST = "freecust";
		else if (field.equals(CUSTACCOUNT))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0133")/*
																										 * @
																										 * res
																										 * "客商银行账号"
																										 */;
		else if (field.equals(FREECUST))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0002272")/*
																									 * @
																									 * res
																									 * "散户"
																									 */;
		else if (field.equals(FYDEPTID))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000223")/*
																								 * @
																								 * res
																								 * "费用承担部门"
																								 */;
		else if (field.equals(FYDWBM))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000287")/*
																								 * @
																								 * res
																								 * "费用承担公司"
																								 */;
		else if (field.equals(DWBM))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000288")/*
																								 * @
																								 * res
																								 * "报销人公司"
																								 */;
		else if (field.equals(YBJE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000280")/*
																								 * @
																								 * res
																								 * "原币金额"
																								 */;
		else if (field.equals(BBJE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000245")/*
																								 * @
																								 * res
																								 * "本币金额"
																								 */;
		else if (field.equals(ISCHECK))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000289")/*
																								 * @
																								 * res
																								 * "支票额度"
																								 */;
		else if (field.equals(ISINITGROUP))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0134")/*
																										 * @
																										 * res
																										 * "集团常用单据"
																										 */;
		else if (field.equals(ZPXE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000290")/*
																								 * @
																								 * res
																								 * "支票限额"
																								 */;
		else if (field.equals(BZBM))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000291")/*
																								 * @
																								 * res
																								 * "币种编码"
																								 */;
		else if (field.equals(JKBXR))
			if (djdl.equals(BXConstans.BX_DJDL))
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000292")/*
																									 * @
																									 * res
																									 * "报销人"
																									 */;
			else
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000249")/*
																									 * @
																									 * res
																									 * "借款人"
																									 */;
		else if (field.equals(PJH))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000293")/*
																								 * @
																								 * res
																								 * "票据号"
																								 */;
		else if (field.equals(CHECKTYPE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0003020")/*
																									 * @
																									 * res
																									 * "票据类型"
																									 */;
		else if (field.equals(BBHL))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000294")/*
																								 * @
																								 * res
																								 * "本币汇率"
																								 */;
		else if (field.equals(SKYHZH))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000295")/*
																								 * @
																								 * res
																								 * "收款银行账号"
																								 */;
		else if (field.equals(FKYHZH))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000296")/*
																								 * @
																								 * res
																								 * "付款银行账号"
																								 */;
		else if (field.equals(JSH))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000297")/*
																								 * @
																								 * res
																								 * "结算号"
																								 */;
		else if (field.equals(JSFS))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000047")/*
																								 * @
																								 * res
																								 * "结算方式"
																								 */;
		else if (field.equals(TOTAL))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000298")/*
																								 * @
																								 * res
																								 * "合计金额"
																								 */;
		else if (field.equals(DJRQ))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000248")/*
																								 * @
																								 * res
																								 * "单据日期"
																								 */;
		else if (field.equals(KJND))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPT2011-000709")/*
																								 * @
																								 * res
																								 * "会计年度"
																								 */;
		else if (field.equals(KJQJ))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPT2011-000715")/*
																								 * @
																								 * res
																								 * "会计期间"
																								 */;

		else if (field.equals(SZXMID))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000224")/*
																								 * @
																								 * res
																								 * "收支项目"
																								 */;
		else if (field.equals(JOBID))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000221")/*
																								 * @
																								 * res
																								 * "项目"
																								 */;
		else if (field.equals(PROJECTTASK))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UCMD1-000312")/*
																								 * @
																								 * res
																								 * "项目任务"
																								 */;
		else
			return field;
	}

	public String pk_payorg;// 支付组织
	public String pk_payorg_v;// 支付组织

	public Integer payflag; // 支付标志
	public String cashproj; // 资金计划项目
	public String busitype; // 业务类型
	public String payman; // 支付人
	public UFDate paydate; // 支付日期
	public String receiver; // 收款人
	public String reimrule; // 报销规则

	public String dwbm;

	public String zyx30;

	public UFDateTime shrq;

	public String zyx4;

	public String zyx20;

	public UFDouble hkbbje;

	public String zyx14;

	public UFBoolean ischeck;

	public UFBoolean isinitgroup; // 常用单据是否集团级

	public UFDouble bbhl;

	public Integer fjzs;

	public String zyx21;

	public String zyx3;

	public String zyx15;

	public String zy;

	public String zyx16;

	public String zyx5;

	public String skyhzh;

	public String zyx25;

	public String zyx18;

	public String zyx9;

	public String zyx13;

	public String zyx24;

	public String jsh;

	public String zyx17;

	public String zyx8;

	public String cashitem;

	public Integer sxbz;

	public String bzbm;

	public UFDouble hkybje;

	public String fydwbm;

	public String zyx6;

	public String zyx11;

	public String fydeptid;

	public UFDouble zpxe;

	public String jobid;

	public String projecttask;

	public String jsfs;

	public String approver;

	public String zyx26;

	public String szxmid;

	public String zyx12;

	public String pk_item;

	public String modifier;

	public String zyx29;

	public String djlxbm;

	public String fkyhzh;

	public UFDouble cjkybje;

	public UFDate jsrq;

	public String zyx23;

	public String operator;

	public String zyx7;

	public String jkbxr;

	public String zyx2;

	public UFDouble zfbbje;

	public String zyx27;

	public String zyx22;

	public String pk_jkbx;

	public String djdl;

	public String zyx10;

	public String pjh;

	public String checktype;

	public String zyx19;

	public String hbbm; // 供应商

	public String customer; // 客户

	public UFDate djrq;

	public String deptid;

	public String zyx28;

	public String djbh;

	public Integer djzt;

	public String zyx1;

	public UFDouble cjkbbje;

	public UFDouble zfybje;

	public UFBoolean qcbz;

	public Integer spzt;

	public UFDateTime ts;

	public Integer dr;

	public UFDate zhrq;

	public UFDouble ybye;

	public UFDouble bbye;

	public UFDate contrastenddate;

	public Integer qzzt;

	public String kjnd;

	public String kjqj;

	public String jsr;

	public UFDate officialprintdate;

	public String officialprintuser;

	public String auditman;

	public UFDouble yjye; // 借款单预计余额

	public UFDouble jsybye; // 结算原币余额

	public String mngaccid;

	public UFDouble ybje;
	public UFDouble bbje;

	public UFDouble total;
	public Integer loantype;

	// v6新增
	public String pk_checkele; // 核算要素
	public String pk_pcorg; // 利润中心
	public String pk_group; // 集团
	public String pk_org; // 业务单元
	public String pk_fiorg; // 财务组织
	public String pk_org_v; // 业务单元版本

	// begin-- added by chendya@ufida.com.cn 组织和部门新增版本化信息

	/**
	 * 利润中心版本化
	 */
	public static String PK_PCORG_V = "pk_pcorg_v";
	/**
	 * (借款/报销)部门版本化
	 */
	public static String DEPTID_V = "deptid_v";
	/**
	 * (借款/报销)费用承担部门版本化
	 */
	public static String FYDEPTID_V = "fydeptid_v";
	/**
	 * (借款/报销)单位版本化
	 */
	public static String DWBM_V = "dwbm_v";
	/**
	 * (借款/报销)费用承担单位版本化
	 */
	public static String FYDWBM_V = "fydwbm_v";

	/**
	 * 利润中心版本化
	 */
	public String pk_pcorg_v;

	/**
	 * (借款/报销)部门版本化
	 */
	public String deptid_v;

	/**
	 * (借款/报销)费用承担部门版本化
	 */
	public String fydeptid_v;

	/**
	 * (借款/报销)单位版本化
	 */
	public String dwbm_v;

	/**
	 * (借款/报销)费用承担单位版本化
	 */
	public String fydwbm_v;
	// --end

	//65-ehp1新增加字段
	public UFDouble tax_amount;//税金金额
	public UFDouble vat_amount;//含税金额
	public UFDouble tni_amount;//不含税金额
	public UFDouble orgtax_amount;//组织税金本币金额
	public UFDouble orgvat_amount;//组织含税本币金额
	public UFDouble orgtni_amount;//组织不含税本币金额
	public UFDouble grouptax_amount;//集团税金本币金额
	public UFDouble groupvat_amount;//集团含税本币金额
	public UFDouble grouptni_amount;//集团不含税本币金额
	public UFDouble globaltax_amount;//全局税金本币金额
	public UFDouble globalvat_amount;//全局含税本币金额
	public UFDouble globaltni_amount;//全局不含税本币金额


	// v6新增
	public UFDouble globalcjkbbje; // 全局冲借款本币金额
	public UFDouble globalhkbbje; // 全局还款本币金额
	public UFDouble globalzfbbje; // 全局支付本币金额
	public UFDouble globalbbje; // 全局借款/报销本币金额
	public UFDouble globalbbye; // 全局本币余额。。
	public UFDouble groupbbye; // 集团本币余额。。
	public UFDouble groupcjkbbje; // 集团冲借款本币金额
	public UFDouble grouphkbbje; // 集团还款本币金额
	public UFDouble groupzfbbje; // 集团支付本币金额
	public UFDouble groupbbje; // 集团借款/报销本币金额
	public UFDouble globalbbhl; // 全局本币汇率
	public UFDouble groupbbhl; // 集团本币汇率

	public String creator; // 创建人
	public UFDateTime creationtime; // 创建时间
	public UFDateTime modifiedtime; // 修改时间
	public String custaccount; // 客商银行账号
	public String freecust; // 散户
	public String setorg;

	public UFBoolean iscostshare = UFBoolean.FALSE;// 分摊标志
	public UFBoolean isexpamt = UFBoolean.FALSE;// 摊销标志
	public String start_period;// 开始摊销期间
	public java.lang.Integer total_period;// 总摊销期

	public UFBoolean flexible_flag = UFBoolean.FALSE;// 项目预算-是否柔性控制

	public UFBoolean iscusupplier = UFBoolean.FALSE;//对公支付
	public static final String ISCUSUPPLIER = "iscusupplier";

	//v631加入
	public String pk_proline;//产品线
	public String pk_brand;//品牌

	// ehp2加入
	public Integer paytarget; // 收款对象（借款单636加入）

	public Integer vouchertag; // 凭证标志
	public UFDate  tbb_period ;//预算占用期间

	// ehp3加入
	public Integer red_status;// 红冲标志
	public String redbillpk;// 红冲单据主键
	// v633加入 CRM
	public String pk_matters;// 营销事项
	public String pk_campaign;// 营销活动

	//add 2014-07-07 V635
	public String imag_status;// 影像状态
	public UFBoolean isneedimag;// 需要影像扫描
	private java.lang.String pk_billtype;//单据类型
	public UFBoolean isexpedited;//紧急

	//add 636 by chenshuai
	public String pk_tradetypeid;//交易类型PK
	public static String PK_TRADETYPEID = "pk_tradetypeid";//交易类型

	public static final String PK_PROLINE = "pk_proline";//产品线
	public static final String PK_BRAND = "pk_brand";//品牌
	public static final String RED_STATUS = "red_status";//红冲状态
	public static final String REDBILLPK = "redbillpk";//红冲pk
	public static final String PK_MATTERS = "pk_matters";// 营销事项
	public static final String PK_CAMPAIGN = "pk_campaign";// 营销活动

	public static final String PAYTARGET = "paytarget";
	public static final String TBB_PERIOD = "tbb_period";
	public static final String VOUCHERTAG = "vouchertag";

	public static final String IMAG_STATUS = "imag_status";
	public static final String ISNEEDIMAG = "isneedimag";
	public static String PK_BILLTYPE = "pk_billtype";//单据类型
	public static String ISEXPEDITED = "isexpedited";//紧急


	//65-ehp1新增加字段
	public static final String TAX_AMOUNT = "tax_amount";
	public static final String VAT_AMOUNT = "vat_amount";
	public static final String TNI_AMOUNT = "tni_amount";
	public static final String ORGTAX_AMOUNT = "orgtax_amount";
	public static final String ORGVAT_AMOUNT = "orgvat_amount";
	public static final String ORGTNI_AMOUNT = "orgtni_amount";
	public static final String GROUPTAX_AMOUNT = "grouptax_amount";
	public static final String GROUPVAT_AMOUNT = "groupvat_amount";
	public static final String GROUPTNI_AMOUNT = "grouptni_amount";
	public static final String GLOBALTAX_AMOUNT = "globaltax_amount";
	public static final String GLOBALVAT_AMOUNT = "globalvat_amount";
	public static final String GLOBALTNI_AMOUNT = "globaltni_amount";

	// v6新增
	public static final String GLOBALCJKBBJE = "globalcjkbbje";
	public static final String GLOBALHKBBJE = "globalhkbbje";
	public static final String GLOBALZFBBJE = "globalzfbbje";
	public static final String GLOBALBBJE = "globalbbje";
	public static final String GLOBALBBYE = "globalbbye";
	public static final String GROUPBBYE = "groupbbye";
	public static final String GROUPCJKBBJE = "groupcjkbbje";
	public static final String GROUPHKBBJE = "grouphkbbje";
	public static final String GROUPZFBBJE = "groupzfbbje";
	public static final String GROUPBBJE = "groupbbje";
	public static final String GLOBALBBHL = "globalbbhl";
	public static final String GROUPBBHL = "groupbbhl";
	public static final String CUSTOMER = "customer";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String CUSTACCOUNT = "custaccount";
	public static final String FREECUST = "freecust";

	public static final String PK_CHECKELE = "pk_checkele";
	public static final String PK_PCORG = "pk_pcorg";
	public static final String PK_FIORG = "pk_fiorg";
	public static final String PK_ORG_V = "pk_org_v";

	public static final String PK_GROUP = "pk_group";

	public static final String RECEIVER = "receiver";

	public static final String AMOUNT = "amount";

	public static final String TOTAL = "total";

	public static final String OFFICIALPRINTDATE = "officialprintdate";

	public static final String OFFICIALPRINTUSER = "officialprintuser";

	public static final String KJND = "kjnd";

	public static final String KJQJ = "kjqj";

	public static final String QZZT = "qzzt";

	public static final String CONTRASTENDDATE = "contrastenddate";

	public static final String YBYE = "ybye";

	public static final String BBYE = "bbye";

	public static final String ZHRQ = "zhrq";

	public static final String SPZT = "spzt";

	public static final String TS = "ts";

	public static final String DR = "dr";

	public static final String QCBZ = "qcbz";

	public static final String DWBM = "dwbm";

	public static final String BUSITYPE = "busitype";

	public static final String ZYX30 = "zyx30";

	public static final String SHRQ = "shrq";

	public static final String ZYX4 = "zyx4";

	public static final String ZYX20 = "zyx20";

	public static final String HKBBJE = "hkbbje";

	public static final String ZYX14 = "zyx14";

	public static final String BBHL = "bbhl";

	public static final String FJZS = "fjzs";

	public static final String ZYX21 = "zyx21";

	public static final String ZYX3 = "zyx3";

	public static final String ZYX15 = "zyx15";

	public static final String ZY = "zy";

	public static final String ZYX16 = "zyx16";

	public static final String ZYX5 = "zyx5";

	public static final String SKYHZH = "skyhzh";

	public static final String ZYX25 = "zyx25";

	public static final String ZYX18 = "zyx18";

	public static final String ZYX9 = "zyx9";

	public static final String ZYX13 = "zyx13";

	public static final String YBJE = "ybje";

	public static final String ZYX24 = "zyx24";

	public static final String JSH = "jsh";

	public static final String ZYX17 = "zyx17";

	public static final String ZYX8 = "zyx8";

	public static final String CASHITEM = "cashitem";

	public static final String SXBZ = "sxbz";

	public static final String BZBM = "bzbm";

	public static final String HKYBJE = "hkybje";

	public static final String FYYBJE = "fyybje";

	public static final String FYDWBM = "fydwbm";

	public static final String PK_ORG = "pk_org";

	public static final String ZYX6 = "zyx6";

	public static final String ZYX11 = "zyx11";

	public static final String FYDEPTID = "fydeptid";

	public static final String ZPXE = "zpxe";

	public static final String JOBID = "jobid";

	public static final String PROJECTTASK = "projecttask";

	public static final String JSFS = "jsfs";

	public static final String APPROVER = "approver";

	public static final String ZYX26 = "zyx26";

	public static final String SZXMID = "szxmid";

	public static final String ZYX12 = "zyx12";

	public static final String PK_ITEM = "pk_item";

	/**
	 * 费用申请单编号
	 */
	public static final String PK_ITEM_BILLNO = "pk_item.billno";

	public static final String MODIFIER = "modifier";

	public static final String ZYX29 = "zyx29";

	public static final String DJLXBM = "djlxbm";
	public static final String DJLXMC = "djlxmc";

	public static final String FKYHZH = "fkyhzh";

	public static final String CJKYBJE = "cjkybje";

	public static final String JSRQ = "jsrq";

	public static final String ZYX23 = "zyx23";

	public static final String OPERATOR = "operator";

	public static final String ZYX7 = "zyx7";

	public static final String JKBXR = "jkbxr";

	public static final String ZYX2 = "zyx2";

	public static final String ZFBBJE = "zfbbje";

	public static final String BBJE = "bbje";

	public static final String ZYX27 = "zyx27";

	public static final String ZYX22 = "zyx22";

	public static final String PK_JKBX = "pk_jkbx";

	public static final String DJDL = "djdl";

	public static final String ZYX10 = "zyx10";

	public static final String PJH = "pjh";

	public static final String CHECKTYPE = "checktype";

	public static final String ZYX19 = "zyx19";

	public static final String HBBM = "hbbm";

	public static final String DJRQ = "djrq";

	public static final String DEPTID = "deptid";

	public static final String ZYX28 = "zyx28";

	public static final String DJBH = "djbh";

	public static final String DJZT = "djzt";

	public static final String ZYX1 = "zyx1";

	public static final String CJKBBJE = "cjkbbje";

	public static final String ZFYBJE = "zfybje";

	public static final String JSR = "jsr";

	public static final String ISCHECK = "ischeck";

	public static final String ISINITGROUP = "isinitgroup"; // 常用单据是否集团级

	public static final String CASHPROJ = "cashproj";

	public static final String JK = "jk";

	public static final String ZPJE = "zpje";

	public static final String PAYFLAG = "payflag";
	public static final String PAYDATE = "paydate";
	public static final String PAYMAN = "payman";

	public static final String MNGACCID_MC = "mngaccid_mc";
	public static final String YJYE = "yjye";
	public static final String MNGACCID = "mngaccid";
	public static final String SETORG = "setorg";

	public static final String ISCOSTSHARE = "iscostshare";
	public static final String ISEXPAMT = "isexpamt";
	public static final String START_PERIOD = "start_period";
	public static final String TOTAL_PERIOD = "total_period";
	public static final String FLEXIBLE_FLAG = "flexible_flag";
	public static final String CENTER_DEPT = "center_dept";

	/**
	 * 来源交易类型
	 */
	public static final String SRCBILLTYPE = "srcbilltype";
	/**
	 * 来源类型，默认为费用申请单
	 */
	public static final String SRCTYPE = "srctype";
	/**
	 * 是否拉单的申请单分摊，从费用申请单上拉过来
	 */
	public static final String ISMASHARE = "ismashare";

	/**
	 * 审批流起点人
	 */
	public static final String AUDITMAN = "auditman";

	/**
	 * 归口管理部门
	 */
	private java.lang.String center_dept;

	/**
	 * 来源单据类型
	 */
	private String srcbilltype;
	private String srctype;

	/**
	 * 是否申请单分摊拉单
	 */
	private UFBoolean ismashare;

	public UFDate getOfficialprintdate() {
		return officialprintdate;
	}

	public void setOfficialprintdate(UFDate officialprintdate) {
		this.officialprintdate = officialprintdate;
	}

	public String getOfficialprintuser() {
		return officialprintuser;
	}

	public void setOfficialprintuser(String officialprintuser) {
		this.officialprintuser = officialprintuser;
	}

	public String getJsr() {
		return jsr;
	}

	public void setJsr(String jsr) {
		this.jsr = jsr;
	}

	/**
	 * 属性pk_corp的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getDwbm() {
		return dwbm;
	}

	/**
	 * 属性pk_corp的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newPk_corp
	 *            String
	 */
	public void setDwbm(String dwbm) {

		this.dwbm = dwbm;
	}

	/**
	 * 属性zyx30的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx30() {
		return zyx30;
	}

	/**
	 * 属性zyx30的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx30
	 *            String
	 */
	public void setZyx30(String newZyx30) {

		zyx30 = newZyx30;
	}

	/**
	 * 属性shrq的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return UFDateTime
	 */
	public UFDateTime getShrq() {
		return shrq;
	}

	/**
	 * 属性shrq的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newShrq
	 *            UFDate
	 */
	public void setShrq(UFDateTime newShrq) {

		shrq = newShrq;
	}

	/**
	 * 属性zyx4的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx4() {
		return zyx4;
	}

	/**
	 * 属性zyx4的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx4
	 *            String
	 */
	public void setZyx4(String newZyx4) {

		zyx4 = newZyx4;
	}

	/**
	 * 属性zyx20的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx20() {
		return zyx20;
	}

	/**
	 * 属性zyx20的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx20
	 *            String
	 */
	public void setZyx20(String newZyx20) {

		zyx20 = newZyx20;
	}

	/**
	 * 属性hkbbje的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return UFDouble
	 */
	public UFDouble getHkbbje() {
		if (hkbbje == null)
			return UFDouble.ZERO_DBL;
		return hkbbje;
	}

	/**
	 * 属性hkbbje的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newHkbbje
	 *            UFDouble
	 */
	public void setHkbbje(UFDouble newHkbbje) {

		hkbbje = newHkbbje;
	}

	/**
	 * 属性zyx14的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx14() {
		return zyx14;
	}

	/**
	 * 属性zyx14的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx14
	 *            String
	 */
	public void setZyx14(String newZyx14) {

		zyx14 = newZyx14;
	}

	public UFBoolean getIscheck() {
		if (ischeck == null)
			return UFBoolean.FALSE;
		return ischeck;
	}

	public void setIscheck(UFBoolean newisCheck) {

		ischeck = newisCheck;
	}

	public UFBoolean getIsinitgroup() {
		if (isinitgroup == null)
			isinitgroup = UFBoolean.FALSE;
		return isinitgroup;
	}

	public void setIsinitgroup(UFBoolean isinitgroup) {
		this.isinitgroup = isinitgroup;
	}

	/**
	 * 属性bbhl的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return UFDouble
	 */
	public UFDouble getBbhl() {
		return bbhl;
	}

	/**
	 * 属性bbhl的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newBbhl
	 *            UFDouble
	 */
	public void setBbhl(UFDouble newBbhl) {

		bbhl = newBbhl;
	}

	/**
	 * 属性fjzs的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return Integer
	 */
	public Integer getFjzs() {
		return fjzs;
	}

	/**
	 * 属性fjzs的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newFjzs
	 *            Integer
	 */
	public void setFjzs(Integer newFjzs) {

		fjzs = newFjzs;
	}

	/**
	 * 属性zyx21的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx21() {
		return zyx21;
	}

	/**
	 * 属性zyx21的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx21
	 *            String
	 */
	public void setZyx21(String newZyx21) {

		zyx21 = newZyx21;
	}

	/**
	 * 属性zyx3的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx3() {
		return zyx3;
	}

	/**
	 * 属性zyx3的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx3
	 *            String
	 */
	public void setZyx3(String newZyx3) {

		zyx3 = newZyx3;
	}

	/**
	 * 属性zyx15的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx15() {
		return zyx15;
	}

	/**
	 * 属性zyx15的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx15
	 *            String
	 */
	public void setZyx15(String newZyx15) {

		zyx15 = newZyx15;
	}

	/**
	 * 属性zy的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZy() {
		return zy;
	}

	/**
	 * 属性zy的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZy
	 *            String
	 */
	public void setZy(String newZy) {

		zy = newZy;
	}

	/**
	 * 属性zyx16的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx16() {
		return zyx16;
	}

	/**
	 * 属性zyx16的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx16
	 *            String
	 */
	public void setZyx16(String newZyx16) {

		zyx16 = newZyx16;
	}

	/**
	 * 属性zyx5的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx5() {
		return zyx5;
	}

	/**
	 * 属性zyx5的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx5
	 *            String
	 */
	public void setZyx5(String newZyx5) {

		zyx5 = newZyx5;
	}

	/**
	 * 属性skyhzh的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getSkyhzh() {
		return skyhzh;
	}

	/**
	 * 属性skyhzh的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newSkyhzh
	 *            String
	 */
	public void setSkyhzh(String newSkyhzh) {

		skyhzh = newSkyhzh;
	}

	/**
	 * 属性zyx25的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx25() {
		return zyx25;
	}

	/**
	 * 属性zyx25的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx25
	 *            String
	 */
	public void setZyx25(String newZyx25) {

		zyx25 = newZyx25;
	}

	/**
	 * 属性zyx18的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx18() {
		return zyx18;
	}

	/**
	 * 属性zyx18的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx18
	 *            String
	 */
	public void setZyx18(String newZyx18) {

		zyx18 = newZyx18;
	}

	/**
	 * 属性zyx9的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx9() {
		return zyx9;
	}

	/**
	 * 属性zyx9的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx9
	 *            String
	 */
	public void setZyx9(String newZyx9) {

		zyx9 = newZyx9;
	}

	/**
	 * 属性zyx13的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx13() {
		return zyx13;
	}

	/**
	 * 属性zyx13的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx13
	 *            String
	 */
	public void setZyx13(String newZyx13) {

		zyx13 = newZyx13;
	}

	/**
	 * 属性zyx24的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx24() {
		return zyx24;
	}

	/**
	 * 属性zyx24的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx24
	 *            String
	 */
	public void setZyx24(String newZyx24) {

		zyx24 = newZyx24;
	}

	/**
	 * 属性jsh的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getJsh() {
		return jsh;
	}

	/**
	 * 属性jsh的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newJsh
	 *            String
	 */
	public void setJsh(String newJsh) {

		jsh = newJsh;
	}

	/**
	 * 属性zyx17的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx17() {
		return zyx17;
	}

	/**
	 * 属性zyx17的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx17
	 *            String
	 */
	public void setZyx17(String newZyx17) {

		zyx17 = newZyx17;
	}

	/**
	 * 属性zyx8的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx8() {
		return zyx8;
	}

	/**
	 * 属性zyx8的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx8
	 *            String
	 */
	public void setZyx8(String newZyx8) {

		zyx8 = newZyx8;
	}

	/**
	 * 属性cashitem的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getCashitem() {
		return cashitem;
	}

	/**
	 * 属性cashitem的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newCashitem
	 *            String
	 */
	public void setCashitem(String newCashitem) {

		cashitem = newCashitem;
	}

	/**
	 * 属性sxbz的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return Integer
	 */
	public Integer getSxbz() {
		return sxbz;
	}

	/**
	 * 属性sxbz的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newSxbz
	 *            Integer
	 */
	public void setSxbz(Integer newSxbz) {

		sxbz = newSxbz;
	}

	/**
	 * 属性bzbm的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getBzbm() {
		return bzbm;
	}

	/**
	 * 属性bzbm的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newBzbm
	 *            String
	 */
	public void setBzbm(String newBzbm) {

		bzbm = newBzbm;
	}

	/**
	 * 属性hkybje的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return UFDouble
	 */
	public UFDouble getHkybje() {
		if (hkybje == null)
			return UFDouble.ZERO_DBL;
		return hkybje;
	}

	/**
	 * 属性hkybje的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newHkybje
	 *            UFDouble
	 */
	public void setHkybje(UFDouble newHkybje) {

		hkybje = newHkybje;
	}

	/**
	 * 属性fydwbm的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getFydwbm() {
		return fydwbm;
	}

	/**
	 * 属性fydwbm的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newFydwbm
	 *            String
	 */
	public void setFydwbm(String newFydwbm) {

		fydwbm = newFydwbm;
	}

	/**
	 * 属性zyx6的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx6() {
		return zyx6;
	}

	/**
	 * 属性zyx6的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx6
	 *            String
	 */
	public void setZyx6(String newZyx6) {

		zyx6 = newZyx6;
	}

	/**
	 * 属性zyx11的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx11() {
		return zyx11;
	}

	/**
	 * 属性zyx11的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx11
	 *            String
	 */
	public void setZyx11(String newZyx11) {

		zyx11 = newZyx11;
	}

	/**
	 * 属性fydeptid的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getFydeptid() {
		return fydeptid;
	}

	/**
	 * 属性fydeptid的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newFydeptid
	 *            String
	 */
	public void setFydeptid(String newFydeptid) {

		fydeptid = newFydeptid;
	}

	/**
	 * 属性zpxe的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return UFDouble
	 */
	public UFDouble getZpxe() {
		return zpxe;
	}

	/**
	 * 属性zpxe的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZpxe
	 *            UFDouble
	 */
	public void setZpxe(UFDouble newZpxe) {

		zpxe = newZpxe;
	}

	/**
	 * 属性jobid的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getJobid() {
		return jobid;
	}

	/**
	 * 属性jobid的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newJobid
	 *            String
	 */
	public void setJobid(String newJobid) {

		jobid = newJobid;
	}

	public String getProjecttask() {
		return projecttask;
	}

	public void setProjecttask(String projecttask) {
		this.projecttask = projecttask;
	}

	/**
	 * 属性jsfs的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getJsfs() {
		return jsfs;
	}

	/**
	 * 属性jsfs的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newJsfs
	 *            String
	 */
	public void setJsfs(String newJsfs) {

		jsfs = newJsfs;
	}

	/**
	 * 属性zyx26的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx26() {
		return zyx26;
	}

	/**
	 * 属性zyx26的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx26
	 *            String
	 */
	public void setZyx26(String newZyx26) {

		zyx26 = newZyx26;
	}

	/**
	 * 属性szxmid的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getSzxmid() {
		return szxmid;
	}

	/**
	 * 属性szxmid的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newSzxmid
	 *            String
	 */
	public void setSzxmid(String newSzxmid) {

		szxmid = newSzxmid;
	}

	/**
	 * 属性zyx12的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx12() {
		return zyx12;
	}

	/**
	 * 属性zyx12的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx12
	 *            String
	 */
	public void setZyx12(String newZyx12) {

		zyx12 = newZyx12;
	}

	/**
	 * 属性pk_item的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getPk_item() {
		return pk_item;
	}

	/**
	 * 属性pk_item的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newPk_item
	 *            String
	 */
	public void setPk_item(String newPk_item) {

		pk_item = newPk_item;
	}

	/**
	 * 属性modifier的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getModifier() {
		return modifier;
	}

	/**
	 * 属性modifier的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newModifier
	 *            String
	 */
	public void setModifier(String newModifier) {

		modifier = newModifier;
	}

	/**
	 * 属性zyx29的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx29() {
		return zyx29;
	}

	/**
	 * 属性zyx29的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx29
	 *            String
	 */
	public void setZyx29(String newZyx29) {

		zyx29 = newZyx29;
	}

	/**
	 * 属性djlxbm的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getDjlxbm() {
		return djlxbm;
	}

	/**
	 * 属性djlxbm的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newDjlxbm
	 *            String
	 */
	public void setDjlxbm(String newDjlxbm) {

		djlxbm = newDjlxbm;
	}

	/**
	 * 属性fkyhzh的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getFkyhzh() {
		return fkyhzh;
	}

	/**
	 * 属性fkyhzh的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newFkyhzh
	 *            String
	 */
	public void setFkyhzh(String newFkyhzh) {

		fkyhzh = newFkyhzh;
	}

	/**
	 * 属性cjkybje的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return UFDouble
	 */
	public UFDouble getCjkybje() {
		if (cjkybje == null)
			return UFDouble.ZERO_DBL;
		return cjkybje;
	}

	/**
	 * 属性cjkybje的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newCjkybje
	 *            UFDouble
	 */
	public void setCjkybje(UFDouble newCjkybje) {

		cjkybje = newCjkybje;
	}

	/**
	 * 属性jsrq的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return UFDate
	 */
	public UFDate getJsrq() {
		return jsrq;
	}

	/**
	 * 属性jsrq的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param jsrq
	 *            UFDate
	 */
	public void setJsrq(UFDate jsrq) {

		this.jsrq = jsrq;
	}

	/**
	 * 属性zyx23的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx23() {
		return zyx23;
	}

	/**
	 * 属性zyx23的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx23
	 *            String
	 */
	public void setZyx23(String newZyx23) {

		zyx23 = newZyx23;
	}

	/**
	 * 录入人 属性operator的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * 属性operator的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newOperator
	 *            String
	 */
	public void setOperator(String newOperator) {

		operator = newOperator;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * 属性zyx7的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx7() {
		return zyx7;
	}

	/**
	 * 属性zyx7的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx7
	 *            String
	 */
	public void setZyx7(String newZyx7) {

		zyx7 = newZyx7;
	}

	/**
	 * 属性jkbxr的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getJkbxr() {
		return jkbxr;
	}

	/**
	 * 属性jkbxr的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newJkbxr
	 *            String
	 */
	public void setJkbxr(String newJkbxr) {

		jkbxr = newJkbxr;
	}

	/**
	 * 属性zyx2的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx2() {
		return zyx2;
	}

	/**
	 * 属性zyx2的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx2
	 *            String
	 */
	public void setZyx2(String newZyx2) {

		zyx2 = newZyx2;
	}

	/**
	 * 属性zfbbje的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return UFDouble
	 */
	public UFDouble getZfbbje() {
		if (zfbbje == null)
			return UFDouble.ZERO_DBL;
		return zfbbje;
	}

	/**
	 * 属性zfbbje的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZfbbje
	 *            UFDouble
	 */
	public void setZfbbje(UFDouble newZfbbje) {

		zfbbje = newZfbbje;
	}

	/**
	 * 属性zyx27的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx27() {
		return zyx27;
	}

	/**
	 * 属性zyx27的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx27
	 *            String
	 */
	public void setZyx27(String newZyx27) {

		zyx27 = newZyx27;
	}

	/**
	 * 属性zyx22的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx22() {
		return zyx22;
	}

	/**
	 * 属性zyx22的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx22
	 *            String
	 */
	public void setZyx22(String newZyx22) {

		zyx22 = newZyx22;
	}

	/**
	 * 属性pk_jkbx的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getPk_jkbx() {
		return pk_jkbx;
	}

	/**
	 * 属性pk_jkbx的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newPk_jkbx
	 *            String
	 */
	public void setPk_jkbx(String newPk_jkbx) {

		pk_jkbx = newPk_jkbx;
	}

	/**
	 * 属性djdl的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getDjdl() {
		return djdl;
	}

	/**
	 * 属性djdl的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newDjdl
	 *            String
	 */
	public void setDjdl(String newDjdl) {
		djdl = newDjdl;
	}

	/**
	 * 属性zyx10的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx10() {
		return zyx10;
	}

	/**
	 * 属性zyx10的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx10
	 *            String
	 */
	public void setZyx10(String newZyx10) {

		zyx10 = newZyx10;
	}

	/**
	 * 属性pjh的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getPjh() {
		return pjh;
	}

	/**
	 * 属性pjh的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newPjh
	 *            String
	 */
	public void setPjh(String newPjh) {

		pjh = newPjh;
	}

	/**
	 * 属性checktype的Getter方法.
	 *
	 * 创建日期:2011-05-24
	 *
	 * @return String
	 */
	public String getChecktype() {
		return checktype;
	}

	/**
	 * 属性checktype的Setter方法.
	 *
	 * 创建日期:2011-05-24
	 *
	 * @param newChecktype
	 *            String
	 */
	public void setChecktype(String checktype) {
		this.checktype = checktype;
	}

	/**
	 * 属性zyx19的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx19() {
		return zyx19;
	}

	/**
	 * 属性zyx19的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx19
	 *            String
	 */
	public void setZyx19(String newZyx19) {

		zyx19 = newZyx19;
	}

	/**
	 * 属性hbbm的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getHbbm() {
		return hbbm;
	}

	/**
	 * 属性hbbm的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newHbbm
	 *            String
	 */
	public void setHbbm(String newHbbm) {

		hbbm = newHbbm;
	}

	/**
	 * 属性djrq的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return UFDate
	 */
	public UFDate getDjrq() {
		return djrq;
	}

	/**
	 * 属性djrq的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newDjrq
	 *            UFDate
	 */
	public void setDjrq(UFDate newDjrq) {

		djrq = newDjrq;
	}

	/**
	 * 属性deptid的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getDeptid() {
		return deptid;
	}

	/**
	 * 属性deptid的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newDeptid
	 *            String
	 */
	public void setDeptid(String newDeptid) {

		deptid = newDeptid;
	}

	/**
	 * 属性zyx28的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx28() {
		return zyx28;
	}

	/**
	 * 属性zyx28的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx28
	 *            String
	 */
	public void setZyx28(String newZyx28) {

		zyx28 = newZyx28;
	}

	/**
	 * 属性djbh的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getDjbh() {
		return djbh;
	}

	/**
	 * 属性djbh的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newDjbh
	 *            String
	 */
	public void setDjbh(String newDjbh) {

		djbh = newDjbh;
	}

	/**
	 * 属性djzt的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return Integer
	 */
	public Integer getDjzt() {
		return djzt;
	}

	/**
	 * 属性djzt的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newDjzt
	 *            Integer
	 */
	public void setDjzt(Integer newDjzt) {

		djzt = newDjzt;
	}

	/**
	 * 属性zyx1的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	public String getZyx1() {
		return zyx1;
	}

	/**
	 * 属性zyx1的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZyx1
	 *            String
	 */
	public void setZyx1(String newZyx1) {

		zyx1 = newZyx1;
	}

	/**
	 * 属性cjkbbje的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return UFDouble
	 */
	public UFDouble getCjkbbje() {
		if (cjkbbje == null)
			return UFDouble.ZERO_DBL;
		return cjkbbje;
	}

	/**
	 * 属性cjkbbje的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newCjkbbje
	 *            UFDouble
	 */
	public void setCjkbbje(UFDouble newCjkbbje) {

		cjkbbje = newCjkbbje;
	}

	/**
	 * 属性zfybje的Getter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return UFDouble
	 */
	public UFDouble getZfybje() {
		if (zfybje == null)
			return UFDouble.ZERO_DBL;
		return zfybje;
	}

	/**
	 * 属性zfybje的Setter方法.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newZfybje
	 *            UFDouble
	 */
	public void setZfybje(UFDouble newZfybje) {

		zfybje = newZfybje;
	}

	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
	 * 创建日期:2007-6-13
	 *
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getParentPKFieldName() {

		return null;

	}

	/**
	 * 按照默认方式创建构造子.
	 *
	 * 创建日期:2007-6-13
	 */
	public JKBXHeaderVO() {

		super();
	}

	/**
	 * 使用主键进行初始化的构造子.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newPk_jkbx
	 *            主键值
	 */
	public JKBXHeaderVO(String newPk_jkbx) {

		// 为主键字段赋值:
		pk_jkbx = newPk_jkbx;

	}

	/**
	 * 返回对象标识,用来唯一定位对象.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @return String
	 */
	@Override
	public String getPrimaryKey() {

		return pk_jkbx;

	}

	/**
	 * 设置对象标识,用来唯一定位对象.
	 *
	 * 创建日期:2007-6-13
	 *
	 * @param newPk_jkbx
	 *            String
	 */
	@Override
	public void setPrimaryKey(String newPk_jkbx) {

		pk_jkbx = newPk_jkbx;

	}

	public UFBoolean getQcbz() {
		if (qcbz == null)
			return UFBoolean.FALSE;
		return qcbz;
	}

	public void setQcbz(UFBoolean qcbz) {
		this.qcbz = qcbz;
	}

	public Integer getSpzt() {
		if(spzt == null){
			spzt = IPfRetCheckInfo.NOSTATE;
		}
		return spzt;
	}

	public void setSpzt(Integer spzt) {
		this.spzt = spzt;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	public UFDate getZhrq() {
		return zhrq;
	}

	public void setZhrq(UFDate zhrq) {
		this.zhrq = zhrq;
	}

	public UFDouble getBbye() {
		return bbye;
	}

	public void setBbye(UFDouble bbye) {
		this.bbye = bbye;
	}

	public UFDouble getYbye() {
		return ybye;
	}

	public void setYbye(UFDouble ybye) {
		this.ybye = ybye;
	}

	/**
	 * VO增加字段, 供界面展示用, 不固化到数据库. VO增加字段, 供界面展示用, 不固化到数据库.
	 */
	public static final String SELECTED = "selected";
	protected UFBoolean selected = UFBoolean.FALSE;

	public UFBoolean getSelected() {
		return selected;
	}

	public void setSelected(UFBoolean selected) {
		this.selected = selected;
	}

	public static final String VOUCHER = "voucher";
	protected String voucher;

	public String getVoucher() {
		return voucher;
	}

	public void setVoucher(String voucher) {
		this.voucher = voucher;
	}

	/**
	 * 业务数据, 供逻辑控制使用, 不固化到数据库. 业务数据, 供逻辑控制使用, 不固化到数据库.
	 */

	protected boolean isInit; // 是否常用单据

	public boolean isInit() {
		return isInit;
	}

	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}

	/**
	 * 显示函数, 供界面展示用, 不使用于业务逻辑. 显示函数, 供界面展示用, 不使用于业务逻辑.
	 */
	protected String jkr, bxr;

	public void setBxr(String bxr) {
		this.bxr = bxr;
	}

	public void setJkr(String jkr) {
		this.jkr = jkr;
	}

	public String getJkr() {
		if (jkr != null)
			return jkr;
		if (djdl != null && djdl.equals(BXConstans.JK_DJDL))
			return getJkbxr();
		else
			return null;
	}

	public String getBxr() {
		if (bxr != null)
			return bxr;
		if (djdl != null && djdl.equals(BXConstans.BX_DJDL))
			return getJkbxr();
		else
			return null;
	}

	/**
	 * 逻辑函数, 供逻辑控制用, 不进行数据修改. 逻辑函数, 供逻辑控制用, 不进行数据修改.
	 */
	public boolean isRepayBill() { // 是否还款单据
		return getHkybje() != null && getHkybje().doubleValue() > 0;
	}

	public boolean isXeBill() { // 是否限额支票型单据
		return getIscheck().booleanValue();
	}

	protected String hyflag;

	protected boolean isFySaveControl = false; // 费用控制审核时控制

	public boolean isFySaveControl() {
		return isFySaveControl;
	}

	public void setFySaveControl(boolean isFySaveControl) {
		this.isFySaveControl = isFySaveControl;
	}

	protected boolean isunAudit;

	public boolean isIsunAudit() {
		return isunAudit;
	}

	public void setIsunAudit(boolean isunAudit) {
		this.isunAudit = isunAudit;
	}

	/**
	 * @return 是否不需要需要走其他影响接口
	 */
	public boolean isNoOtherEffectItf() {

		boolean status = false;

		if (isInit())
			status = true;
		// if (getDjzt().equals(BXStatusConst.DJZT_TempSaved))
		// status=true;

		return status;
	}

	public UFDate getContrastenddate() {
		return contrastenddate;
	}

	public void setContrastenddate(UFDate contrastEndDate) {
		this.contrastenddate = contrastEndDate;
	}

	public Integer getQzzt() {
		return qzzt;
	}

	public void setQzzt(Integer qzzt) {
		this.qzzt = qzzt;
	}

	public String getKjnd() {
		return kjnd;
	}

	public void setKjnd(String kjnd) {
		this.kjnd = kjnd;
	}

	public String getKjqj() {
		return kjqj;
	}

	public void setKjqj(String kjqj) {
		this.kjqj = kjqj;
	}

	// /////////////////事项审批控制接口需要实现的方法/////////////////

	public boolean isSaveControl() {
		return isFySaveControl();
	}

	public UFDate getOperationDate() {
		if (getDjzt() == null)
			return null;
		if (getDjzt().intValue() == BXStatusConst.DJZT_Saved) {
			return getDjrq();
			// FIXME 审核日期注销
			// }else if(getDjzt().intValue()==BXStatusConst.DJZT_Verified){
			// return getShrq();
		} else if (getDjzt().intValue() == BXStatusConst.DJZT_Sign) {
			return getJsrq();
		}
		return null;
	}

	public String getOperationUser() {
		if (getDjzt() == null)
			return null;
		if (getDjzt().intValue() == BXStatusConst.DJZT_Saved) {
			return getOperator();
		} else if (getDjzt().intValue() == BXStatusConst.DJZT_Verified) {
			return getApprover();
		} else if (getDjzt().intValue() == BXStatusConst.DJZT_Sign) {
			return getJsr();
		}
		return null;
	}

	public boolean isSSControlAble() {

		boolean status = true;

		if (getQcbz() != null && getQcbz().booleanValue())
			status = false;
		if (StringUtils.isNullWithTrim(getPk_item()))
			status = false;
		if (getDjzt().equals(BXStatusConst.DJZT_TempSaved))
			status = false;

		return status;
	}

	public boolean isYSControlAble() {

		boolean status = true;

		if (getQcbz() != null && getQcbz().booleanValue())
			status = false;
		if (getDjzt().equals(BXStatusConst.DJZT_TempSaved))
			status = false;

		return status;
	}

	public boolean isJKControlAble() {

		boolean status = true;

		if (getDjdl() == null || !getDjdl().equals(BXConstans.JK_DJDL))
			status = false;
		if (getQcbz() != null && getQcbz().booleanValue())
			status = false;
		if (getDjzt().equals(BXStatusConst.DJZT_TempSaved))
			status = false;

		return status;
	}

	public UFDouble[] getItemHl() {
		return new UFDouble[] { getGlobalbbhl(), getGroupbbhl(), getBbhl() };
	}

	public UFDouble[] getItemJe() {
		return new UFDouble[] { getGlobalbbje(), getGroupbbje(), getBbje(), getYbje() };
	}

	//预算控制
	public UFDouble[] getVatItemJe() {
		return new UFDouble[] { getGlobalvat_amount(), getGroupvat_amount(), getOrgvat_amount(), getVat_amount() };
	}
	public UFDouble[] getTniItemJe() {
		return new UFDouble[] { getGlobaltni_amount(), getGrouptni_amount(), getOrgtni_amount(), getTni_amount() };
	}
	public UFDouble[] getPayItemJe() {
		return new UFDouble[] { getGlobalzfbbje(), getGroupzfbbje(), getZfbbje(), getZfybje() };
	}
	public UFDouble[] getHkItemJe() {
		return new UFDouble[] { getGlobalhkbbje(), getGrouphkbbje(), getHkbbje(), getHkybje() };
	}

	private UFDouble[] preItemJe;

	//报销单增加按照含税，不含税，支付，还款金额预算控制
	private UFDouble[] preVAtItemJe;//含税
	private UFDouble[] preTNIItemJe;//不含税
	private UFDouble[] prePAYItemJe;//支付
	private UFDouble[] preHKItemJe;//还款



	public UFDouble[] getPreVAtItemJe() {
		return preVAtItemJe;
	}

	public void setPreVAtItemJe(UFDouble[] preVAtItemJe) {
		this.preVAtItemJe = preVAtItemJe;
	}

	public UFDouble[] getPreTNIItemJe() {
		return preTNIItemJe;
	}

	public void setPreTNIItemJe(UFDouble[] preTNIItemJe) {
		this.preTNIItemJe = preTNIItemJe;
	}

	public UFDouble[] getPrePAYItemJe() {
		return prePAYItemJe;
	}

	public void setPrePAYItemJe(UFDouble[] prePAYItemJe) {
		this.prePAYItemJe = prePAYItemJe;
	}

	public UFDouble[] getPreHKItemJe() {
		return preHKItemJe;
	}

	public void setPreHKItemJe(UFDouble[] preHKItemJe) {
		this.preHKItemJe = preHKItemJe;
	}

	@Override
	public UFDouble[] getPreItemJe() {
		return preItemJe;
	}

	public void setPreItemJe(UFDouble[] preItemJe) {
		this.preItemJe = preItemJe;
	}

	public Object getItemValue(String key) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("zrdeptid", "fydeptid");
		map.put("xmbm2", "jobid");
		map.put("ywybm", "jkbxr");
		return getAttributeValue(map.get(key) == null ? key : map.get(key));
	}

	public String getDdlx() {
		return null;
	}

	public Integer getFx() {
		return isRepayBill() ? Integer.valueOf(-1) : Integer.valueOf(1);
	}

	// /////////////////事项审批控制接口需要实现的方法/////////////////

	public String getBusitype() {
		return busitype;
	}

	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}

	/**
	 *
	 * 返回审批流起点人
	 */
	public String getAuditman() throws BusinessException {
		String result = "";
		result = ErCorpUtil.getBxCtlMan(this);
		return result;
	}

	/* 重写此方法的目的是为了clone的时候，不克隆auditman，因为getAuditman有多次远程调用 */
	@Override
	public String[] getAttributeNames() {
		List<String> retValues = new ArrayList<String>();
		final String[] names = super.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			if (AUDITMAN.equals(names[i])) {
				continue;
			}
			retValues.add(names[i]);
		}
		return retValues.toArray(new String[0]);
	}

	public void setAuditman(String man) {

	}

	public UFDouble getYjye() {
		return yjye;
	}

	public void setYjye(UFDouble yjye) {
		this.yjye = yjye;
	}

	public UFDouble getJsybye() {
		return jsybye;
	}

	public void setJsybye(UFDouble jsybye) {
		this.jsybye = jsybye;
	}

	public UFDouble getBbje() {
		return bbje;
	}

	public void setBbje(UFDouble bbje) {
		this.bbje = bbje;
	}

	public UFDouble getYbje() {
		return ybje;
	}

	public void setYbje(UFDouble ybje) {
		this.ybje = ybje;
	}

	public String getCashproj() {
		return cashproj;
	}

	public void setCashproj(String cashproj) {
		this.cashproj = cashproj;
	}

	public Integer getPayflag() {
		return payflag;
	}

	public void setPayflag(Integer payflag) {
		this.payflag = payflag;
	}

	public UFDouble getTotal() {
		return total;
	}

	public void setTotal(UFDouble total) {
		this.total = total;
	}

	public UFDate getPaydate() {
		return paydate;
	}

	public void setPaydate(UFDate paydate) {
		this.paydate = paydate;
	}

	public String getPayman() {
		return payman;
	}

	public void setPayman(String payman) {
		this.payman = payman;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Integer getLoantype() {
		return loantype;
	}

	public void setLoantype(Integer loantype) {
		this.loantype = loantype;
	}

	@Override
	public Object getAttributeValue(String key) {
		// 如果包含结算信息字段，从结算vo中取值
		if (key.startsWith(SETTLE_HEAD_PREFIX)) {
			if (getSettleHeadVO() != null) {
				String attribute = key.substring(key.indexOf(SETTLE_HEAD_PREFIX) + SETTLE_HEAD_PREFIX.length());
				return getSettleHeadVO().getAttributeValue(attribute);
			}
		}

		String name = null;
		Object result = null;
		String[] tokens = StringUtil.split(key, ".");
		if (tokens.length == 1) {
			name = key;
		}else{
			name = tokens[1];
		}
		if(BeanHelper.getMethod(this, name) != null){
			result = BeanHelper.getProperty(this, name);//用来处理借款报销中字段不一致问题
		}else{
			result = super.getAttributeValue(name);
		}

		return result;
	}

	@Override
	public void setAttributeValue(String name, Object value) {
		if (BeanHelper.getMethod(this, name) != null) {
			try {
				BeanHelper.setProperty(this, name, value);
			} catch (IllegalArgumentException e) {
				super.setAttributeValue(name, value);
			} catch (ClassCastException e) {
				super.setAttributeValue(name, value);
			}
		} else {
			super.setAttributeValue(name, value);
		}
	}

	//shiwla 手机端进行vo转换用
	public void setJsonAttributeValue(String name, Object value) {
		super.setAttributeValue(name, value);
	}
	public String getPk_org_v() {
		return pk_org_v;
	}

	public void setPk_org_v(String pkOrgV) {
		pk_org_v = pkOrgV;
	}

	public String getHyflag() {
		return hyflag;
	}

	public void setHyflag(String hyflag) {
		this.hyflag = hyflag;
	}

	public String getMngaccid() {
		return mngaccid;
	}

	public void setMngaccid(String mngaccid) {
		this.mngaccid = mngaccid;
	}

	public String getReimrule() {
		return reimrule;
	}

	public void setReimrule(String reimrule) {
		this.reimrule = reimrule;
	}

	public String getPk_group() {
		return pk_group;
	}

	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getPk_checkele() {
		return pk_checkele;
	}

	public void setPk_checkele(String pk_checkele) {
		this.pk_checkele = pk_checkele;
	}

	public String getPk_pcorg() {
		return pk_pcorg;
	}

	public void setPk_pcorg(String pk_pcorg) {
		this.pk_pcorg = pk_pcorg;
	}

	public String getPk_fiorg() {
		return pk_fiorg;
	}

	public void setPk_fiorg(String pk_fiorg) {
		this.pk_fiorg = pk_fiorg;
	}

	/**
	 * 属性globalcjkbbje的Getter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getGlobalcjkbbje() {
		return globalcjkbbje;
	}

	/**
	 * 属性globalcjkbbje的Setter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @param newGlobalcjkbbje
	 *            UFDouble
	 */
	public void setGlobalcjkbbje(UFDouble newGlobalcjkbbje) {
		this.globalcjkbbje = newGlobalcjkbbje;
	}

	/**
	 * 属性globalhkbbje的Getter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @return UFDouble
	 */
	public UFDouble getGlobalhkbbje() {
		return globalhkbbje;
	}

	/**
	 * 属性globalhkbbje的Setter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @param newGlobalhkbbje
	 *            UFDouble
	 */
	public void setGlobalhkbbje(UFDouble newGlobalhkbbje) {
		this.globalhkbbje = newGlobalhkbbje;
	}

	/**
	 * 属性globalzfbbje的Getter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @return UFDouble
	 */
	public UFDouble getGlobalzfbbje() {
		return globalzfbbje;
	}

	/**
	 * 属性globalzfbbje的Setter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @param newGlobalzfbbje
	 *            UFDouble
	 */
	public void setGlobalzfbbje(UFDouble newGlobalzfbbje) {
		this.globalzfbbje = newGlobalzfbbje;
	}

	/**
	 * 属性globalbbje的Getter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @return UFDouble
	 */
	public UFDouble getGlobalbbje() {
		return globalbbje;
	}

	/**
	 * 属性globalbbje的Setter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @param newGlobalbbje
	 *            UFDouble
	 */
	public void setGlobalbbje(UFDouble newGlobalbbje) {
		this.globalbbje = newGlobalbbje;
	}

	/**
	 * 属性globalbbye的Getter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @return UFDouble
	 */
	public UFDouble getGlobalbbye() {
		return globalbbye;
	}

	/**
	 * 属性globalbbye的Setter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @param newGlobalbbye
	 *            UFDouble
	 */
	public void setGlobalbbye(UFDouble newGlobalbbye) {
		this.globalbbye = newGlobalbbye;
	}

	/**
	 * 属性groupbbye的Getter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @return UFDouble
	 */
	public UFDouble getGroupbbye() {
		return groupbbye;
	}

	/**
	 * 属性groupbbye的Setter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @param newGroupbbye
	 *            UFDouble
	 */
	public void setgroupbbye(UFDouble newGroupbbye) {
		this.groupbbye = newGroupbbye;
	}

	/**
	 * 属性groupcjkbbje的Getter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @return UFDouble
	 */
	public UFDouble getGroupcjkbbje() {
		return groupcjkbbje;
	}

	/**
	 * 属性groupcjkbbje的Setter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @param newGroupcjkbbje
	 *            UFDouble
	 */
	public void setGroupcjkbbje(UFDouble newGroupcjkbbje) {
		this.groupcjkbbje = newGroupcjkbbje;
	}

	/**
	 * 属性grouphkbbje的Getter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @return UFDouble
	 */
	public UFDouble getGrouphkbbje() {
		return grouphkbbje;
	}

	/**
	 * 属性grouphkbbje的Setter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @param newGrouphkbbje
	 *            UFDouble
	 */
	public void setGrouphkbbje(UFDouble newGrouphkbbje) {
		this.grouphkbbje = newGrouphkbbje;
	}

	/**
	 * 属性groupzfbbje的Getter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @return UFDouble
	 */
	public UFDouble getGroupzfbbje() {
		return groupzfbbje;
	}

	/**
	 * 属性groupzfbbje的Setter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @param newGroupzfbbje
	 *            UFDouble
	 */
	public void setGroupzfbbje(UFDouble newGroupzfbbje) {
		this.groupzfbbje = newGroupzfbbje;
	}

	/**
	 * 属性groupbbje的Getter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @return UFDouble
	 */
	public UFDouble getGroupbbje() {
		return groupbbje;
	}

	/**
	 * 属性groupbbje的Setter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @param newGroupbbje
	 *            UFDouble
	 */
	public void setGroupbbje(UFDouble newGroupbbje) {
		this.groupbbje = newGroupbbje;
	}

	/**
	 * 属性globalbbhl的Getter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @return UFDouble
	 */
	public UFDouble getGlobalbbhl() {
		return globalbbhl;
	}

	/**
	 * 属性globalbbhl的Setter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @param newGlobalbbhl
	 *            UFDouble
	 */
	public void setGlobalbbhl(UFDouble newGlobalbbhl) {
		this.globalbbhl = newGlobalbbhl;
	}

	/**
	 * 属性groupbbhl的Getter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @return UFDouble
	 */
	public UFDouble getGroupbbhl() {
		return groupbbhl;
	}

	/**
	 * 属性groupbbhl的Setter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @param newGroupbbhl
	 *            UFDouble
	 */
	public void setGroupbbhl(UFDouble newGroupbbhl) {
		this.groupbbhl = newGroupbbhl;
	}

	/**
	 * 属性customer的Getter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @return java.lang.String
	 */
	public String getCustomer() {
		return customer;
	}

	/**
	 * 属性customer的Setter方法. 创建日期:2010-01-18 09:32:58
	 *
	 * @param newCustomer
	 *            java.lang.String
	 */
	public void setCustomer(String newCustomer) {
		this.customer = newCustomer;
	}

	public UFDateTime getCreationtime() {
		return creationtime;
	}

	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	public UFDateTime getModifiedtime() {
		return modifiedtime;
	}

	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	public String getCustaccount() {
		return custaccount;
	}

	public void setCustaccount(String custaccount) {
		this.custaccount = custaccount;
	}

	public String getFreecust() {
		return freecust;
	}

	public void setFreecust(String freecust) {
		this.freecust = freecust;
	}

	/**
	 * 属性setorg的Setter方法. 创建日期:2010-01-18 09:32:58 @ 实现列表界面集团，业务单元显示
	 */
	public String getSetorg() {
		return setorg;
	}

	public void setSetorg(String setorg) {
		this.setorg = setorg;
	}

	/**
	 * 借款报销多版本字段对照表
	 *
	 * @return
	 */
	public static Map<String, String> getOrgMultiVersionFieldMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(JKBXHeaderVO.PK_ORG, JKBXHeaderVO.PK_ORG_V);
		map.put(JKBXHeaderVO.FYDWBM, JKBXHeaderVO.FYDWBM_V);
		map.put(JKBXHeaderVO.DWBM, JKBXHeaderVO.DWBM_V);
		map.put(JKBXHeaderVO.PK_PCORG, JKBXHeaderVO.PK_PCORG_V);
		map.put(JKBXHeaderVO.FYDEPTID, JKBXHeaderVO.FYDEPTID_V);
		map.put(JKBXHeaderVO.DEPTID, JKBXHeaderVO.DEPTID_V);
		map.put(JKBXHeaderVO.PK_PAYORG, JKBXHeaderVO.PK_PAYORG_V);// 支付单位
		return map;
	}

	public static String getDeptFieldByVField(String vField) {
		Map<String, String> map = getDeptMultiVersionFieldMap();
		Set<Entry<String, String>> entrySet = map.entrySet();
		for (Iterator<Entry<String, String>> iterator = entrySet.iterator(); iterator.hasNext();) {
			Entry<String, String> entry = (Entry<String, String>) iterator.next();
			if (vField.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static String getDeptVFieldByField(String field) {
		return getDeptMultiVersionFieldMap().get(field);
	}

	public static String getOrgFieldByVField(String vField) {
		Map<String, String> map = getOrgMultiVersionFieldMap();
		Set<Entry<String, String>> entrySet = map.entrySet();
		for (Iterator<Entry<String, String>> iterator = entrySet.iterator(); iterator.hasNext();) {
			Entry<String, String> entry = (Entry<String, String>) iterator.next();
			if (vField.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static String getOrgVFieldByField(String field) {
		return getOrgMultiVersionFieldMap().get(field);
	}

	/**
	 * 借款报销多版本字段数组
	 *
	 * @return
	 */
	public static String[] getOrgMultiVersionFieldArray() {
		return (String[]) getOrgMultiVersionFieldMap().values().toArray(new String[0]);
	}

	/**
	 * 借款报销多版本字段列表
	 *
	 * @return
	 */
	public static List<String> getOrgMultiVersionFieldList() {
		return Arrays.asList(getOrgMultiVersionFieldArray());
	}

	/**
	 * 借款报销多版本字段对照表
	 *
	 * @return
	 */
	public static Map<String, String> getDeptMultiVersionFieldMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(JKBXHeaderVO.DEPTID, JKBXHeaderVO.DEPTID_V);
		map.put(JKBXHeaderVO.FYDEPTID, JKBXHeaderVO.FYDEPTID_V);
		return map;
	}

	/**
	 * 借款报销多版本字段数组
	 *
	 * @return
	 */
	public static String[] getDeptMultiVersionFieldArray() {
		return (String[]) getDeptMultiVersionFieldMap().values().toArray(new String[0]);
	}

	/**
	 * 借款报销多版本字段列表
	 *
	 * @return
	 */
	public static List<String> getDeptMultiVersionFieldList() {
		return Arrays.asList(getDeptMultiVersionFieldArray());
	}

	@Override
	public String getParentBillType() {
		if (BXConstans.BX_DJDL.equals(this.getDjdl())) {
			return BXConstans.BX_DJLXBM;
		} else if (BXConstans.JK_DJDL.equals(this.getDjdl())) {
			return BXConstans.JK_DJLXBM;
		}
		return null;
	}

	public UFBoolean getIscostshare() {
		if (iscostshare == null) {
			return UFBoolean.FALSE;
		}
		return iscostshare;
	}

	public void setIscostshare(UFBoolean iscostshare) {
		this.iscostshare = iscostshare;
	}

	public UFBoolean getIsexpamt() {
		if (isexpamt == null) {
			return UFBoolean.FALSE;
		}
		return isexpamt;
	}

	public void setIsexpamt(UFBoolean isexpamt) {
		this.isexpamt = isexpamt;
	}

	public UFBoolean getIscusupplier() {
		if (iscusupplier == null){
			return UFBoolean.FALSE;
		}
		return iscusupplier;
	}

	public void setIscusupplier(UFBoolean iscusupplier) {
		this.iscusupplier = iscusupplier;
	}

	public String getStart_period() {
		return start_period;
	}

	public void setStart_period(String startPeriod) {
		start_period = startPeriod;
	}

	public java.lang.Integer getTotal_period() {
		return total_period;
	}

	public void setTotal_period(java.lang.Integer totalPeriod) {
		total_period = totalPeriod;
	}

	public String getPk_payorg() {
		return pk_payorg;
	}

	public void setPk_payorg(String pk_payorg) {
		this.pk_payorg = pk_payorg;
	}

	public String getPk_payorg_v() {
		return pk_payorg_v;
	}

	public void setPk_payorg_v(String pk_payorg_v) {
		this.pk_payorg_v = pk_payorg_v;
	}

	public UFBoolean getFlexible_flag() {
		return flexible_flag;
	}

	public void setFlexible_flag(UFBoolean flexible_flag) {
		this.flexible_flag = flexible_flag;
	}

	public void setPk_cashaccount(String pkCashaccount) {
		pk_cashaccount = pkCashaccount;
	}

	public String getPk_cashaccount() {
		return pk_cashaccount;
	}

	public void setPk_resacostcenter(String pkResacostcenter) {
		pk_resacostcenter = pkResacostcenter;
	}

	public String getPk_resacostcenter() {
		return pk_resacostcenter;
	}

	public void setSettleHeadVO(SuperVO settleHeadVO) {
		this.settleHeadVO = settleHeadVO;
	}

	public SuperVO getSettleHeadVO() {
		return settleHeadVO;
	}

	/**
	 * 此方法不公开
	 *
	 * @param shrqShow
	 */
	public void setShrq_show(UFDate shrqShow) {
		shrq_show = shrqShow;
	}

	public UFDate getShrq_show() {
		if(getShrq() != null){
			shrq_show = getShrq().getDate();
		}
		return shrq_show;
	}

	public String getPk_pcorg_v() {
		return pk_pcorg_v;
	}

	public void setPk_pcorg_v(String pkPcorgV) {
		pk_pcorg_v = pkPcorgV;
	}

	public String getDeptid_v() {
		return deptid_v;
	}

	public void setDeptid_v(String deptidV) {
		deptid_v = deptidV;
	}

	public String getFydeptid_v() {
		return fydeptid_v;
	}

	public void setFydeptid_v(String fydeptidV) {
		fydeptid_v = fydeptidV;
	}

	public String getDwbm_v() {
		return dwbm_v;
	}

	public void setDwbm_v(String dwbmV) {
		dwbm_v = dwbmV;
	}

	public String getFydwbm_v() {
		return fydwbm_v;
	}

	public void setFydwbm_v(String fydwbmV) {
		fydwbm_v = fydwbmV;
	}

	public String getPk_busitem() {
		return pk_busitem;
	}

	public void setPk_busitem(String pk_busitem) {
		this.pk_busitem = pk_busitem;
	}

	@Override
	public String getPk() {
		return getPk_jkbx();
	}

	public String getJk_busitemPK() {
		return jk_busitemPK;
	}

	public void setJk_busitemPK(String jk_busitemPK) {
		this.jk_busitemPK = jk_busitemPK;
	}

	public String getBx_busitemPK() {
		return bx_busitemPK;
	}

	public void setBx_busitemPK(String bx_busitemPK) {
		this.bx_busitemPK = bx_busitemPK;
	}

	public java.lang.String getCenter_dept() {
		return center_dept;
	}

	public void setCenter_dept(java.lang.String center_dept) {
		this.center_dept = center_dept;
	}

	public String getSrcbilltype() {
		return srcbilltype;
	}

	public void setSrcbilltype(String srcbilltype) {
		this.srcbilltype = srcbilltype;
	}

	public String getSrctype() {
		return srctype;
	}

	public void setSrctype(String srctype) {
		this.srctype = srctype;
	}

	public String getPk_mtapp_detail() {
		return pk_mtapp_detail;
	}

	public void setPk_mtapp_detail(String pk_mtapp_detail) {
		this.pk_mtapp_detail = pk_mtapp_detail;
	}

	public UFBoolean getIsmashare() {
		return ismashare;
	}

	public void setIsmashare(UFBoolean ismashare) {
		this.ismashare = ismashare;
	}

	public String getPk_proline() {
		return pk_proline;
	}

	public void setPk_proline(String pkProline) {
		pk_proline = pkProline;
	}

	public String getPk_brand() {
		return pk_brand;
	}

	public void setPk_brand(String pkBrand) {
		pk_brand = pkBrand;
	}

	public java.lang.String getPk_billtype() {
		return pk_billtype;
	}

	public void setPk_billtype(java.lang.String pk_billtype) {
		this.pk_billtype = pk_billtype;
	}

	@Override
	public String getWorkFlowBillPk() {
		return getPk();
	}

	@Override
	public String getWorkFolwBillType() {
		return getDjlxbm();
	}

	public Integer getVouchertag() {
		return vouchertag;
	}

	public void setVouchertag(Integer vouchertag) {
		this.vouchertag = vouchertag;
	}

	public String getImag_status() {
		return imag_status;
	}

	public void setImag_status(String imag_status) {
		this.imag_status = imag_status;
	}

	public UFBoolean getIsneedimag() {
		return isneedimag;
	}

	public void setIsneedimag(UFBoolean isneedimag) {
		this.isneedimag = isneedimag;
	}

	public Integer getRed_status() {
		return red_status;
	}

	public void setRed_status(Integer red_status) {
		this.red_status = red_status;
	}

	public String getRedbillpk() {
		return redbillpk;
	}

	public void setRedbillpk(String redbillpk) {
		this.redbillpk = redbillpk;
	}

	public UFDate getTbb_period() {
		return tbb_period;
	}

	public void setTbb_period(UFDate tbbPeriod) {
		tbb_period = tbbPeriod;
	}

	public Integer getPaytarget() {
		return paytarget;
	}

	public void setPaytarget(Integer paytarget) {
		this.paytarget = paytarget;
	}

	public UFBoolean getIsexpedited() {
		return isexpedited;
	}

	public void setIsexpedited(UFBoolean isexpedited) {
		this.isexpedited = isexpedited;
	}

	public String getPk_matters() {
		return pk_matters;
	}

	public void setPk_matters(String pk_matters) {
		this.pk_matters = pk_matters;
	}

	public String getPk_campaign() {
		return pk_campaign;
	}

	public void setPk_campaign(String pk_campaign) {
		this.pk_campaign = pk_campaign;
	}

	public String getPk_tradetypeid() {
		return pk_tradetypeid;
	}

	public void setPk_tradetypeid(String pk_tradetypeid) {
		this.pk_tradetypeid = pk_tradetypeid;
	}

	public void combineVO(JKBXHeaderVO vo){
		if (this.pk_item == null && this.isLoadInitBill == false) {
			// 非拉单且不加载常用单据情况，根据单据模板的设置冲掉vo中的默认值
			if (!this.getIscostshare().booleanValue()) {
				setIscostshare(vo.getIscostshare());
			}
			if (!this.getIsexpamt().booleanValue()) {
				setIsexpamt(vo.getIsexpamt());
			}
		}
		String[] attributeNames = this.getAttributeNames();
		for (String attribute : attributeNames) {
			// 初始值中未设置的值，根据单据模板默认值设置
			if (this.getAttributeValue(attribute) == null) {
				Object newValue = vo.getAttributeValue(attribute);
				if (newValue != null) {
					setAttributeValue(attribute,newValue);
				}
			}
		}
	}

	public boolean isLoadInitBill() {
		return isLoadInitBill;
	}

	public void setLoadInitBill(boolean isLoadInitBill) {
		this.isLoadInitBill = isLoadInitBill;
	}



	public UFDouble getTax_amount() {
		return tax_amount;
	}

	public void setTax_amount(UFDouble tax_amount) {
		this.tax_amount = tax_amount;
	}

	public UFDouble getVat_amount() {
		return vat_amount;
	}

	public void setVat_amount(UFDouble vat_amount) {
		this.vat_amount = vat_amount;
	}

	public UFDouble getTni_amount() {
		return tni_amount;
	}

	public void setTni_amount(UFDouble tni_amount) {
		this.tni_amount = tni_amount;
	}


	public UFDouble getOrgtax_amount() {
		return orgtax_amount;
	}

	public void setOrgtax_amount(UFDouble orgtax_amount) {
		this.orgtax_amount = orgtax_amount;
	}

	public UFDouble getOrgvat_amount() {
		return orgvat_amount;
	}

	public void setOrgvat_amount(UFDouble orgvat_amount) {
		this.orgvat_amount = orgvat_amount;
	}

	public UFDouble getOrgtni_amount() {
		return orgtni_amount;
	}

	public void setOrgtni_amount(UFDouble orgtni_amount) {
		this.orgtni_amount = orgtni_amount;
	}

	public UFDouble getGrouptax_amount() {
		return grouptax_amount;
	}

	public void setGrouptax_amount(UFDouble grouptax_amount) {
		this.grouptax_amount = grouptax_amount;
	}

	public UFDouble getGroupvat_amount() {
		return groupvat_amount;
	}

	public void setGroupvat_amount(UFDouble groupvat_amount) {
		this.groupvat_amount = groupvat_amount;
	}

	public UFDouble getGrouptni_amount() {
		return grouptni_amount;
	}

	public void setGrouptni_amount(UFDouble grouptni_amount) {
		this.grouptni_amount = grouptni_amount;
	}

	public UFDouble getGlobaltax_amount() {
		return globaltax_amount;
	}

	public void setGlobaltax_amount(UFDouble globaltax_amount) {
		this.globaltax_amount = globaltax_amount;
	}

	public UFDouble getGlobalvat_amount() {
		return globalvat_amount;
	}

	public void setGlobalvat_amount(UFDouble globalvat_amount) {
		this.globalvat_amount = globalvat_amount;
	}

	public UFDouble getGlobaltni_amount() {
		return globaltni_amount;
	}

	public void setGlobaltni_amount(UFDouble globaltni_amount) {
		this.globaltni_amount = globaltni_amount;
	}

	/**
	 * 当前报销单是否是费用调整类型
	 *
	 * @return
	 */
	public boolean isAdjustBxd() {
		boolean isAdjust = false;
		try {
			if(getDjlxbm().equals(BXConstans.BILLTYPECODE_ADJUST)){
				return true;
			}
			//预制的报销单中只有264a是调整单，再有调整单只能是新增的交易类型
			if(getDjlxbm().startsWith(BXConstans.BX_DJLXBM)){
				isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(getPk_group(), getDjlxbm(),ErmDjlxConst.BXTYPE_ADJUST);
			}

		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
		return isAdjust;
	}

	//SSC新增字段
	private UFDouble tax_rate;//税率

	public static final String TAX_RATE = "tax_rate";//税率

	public UFDouble getTax_rate() {
		return tax_rate;
	}

	public void setTax_rate(UFDouble tax_rate) {
		this.tax_rate = tax_rate;
	}

	private String srcsystem;//来源系统，区分单据来源为NC或IWEB

	public String getSrcsystem() {
		return srcsystem;
	}

	public void setSrcsystem(String srcsystem) {
		this.srcsystem = srcsystem;
	}

	// 扩展自定义项begin
	public static final String ZYX31 = "zyx31";
	public static final String ZYX32 = "zyx32";
	public static final String ZYX33 = "zyx33";
	public static final String ZYX34 = "zyx34";
	public static final String ZYX35 = "zyx35";
	public static final String ZYX36 = "zyx36";
	public static final String ZYX37 = "zyx37";
	public static final String ZYX38 = "zyx38";
	public static final String ZYX39 = "zyx39";
	public static final String ZYX40 = "zyx40";
	public static final String ZYX41 = "zyx41";
	public static final String ZYX42 = "zyx42";
	public static final String ZYX43 = "zyx43";
	public static final String ZYX44 = "zyx44";
	public static final String ZYX45 = "zyx45";
	public static final String ZYX46 = "zyx46";
	public static final String ZYX47 = "zyx47";
	public static final String ZYX48 = "zyx48";
	public static final String ZYX49 = "zyx49";
	public static final String ZYX50 = "zyx50";
	public static final String ZYX51 = "zyx51";
	public static final String ZYX52 = "zyx52";
	public static final String ZYX53 = "zyx53";
	public static final String ZYX54 = "zyx54";
	public static final String ZYX55 = "zyx55";
	public static final String ZYX56 = "zyx56";
	public static final String ZYX57 = "zyx57";
	public static final String ZYX58 = "zyx58";
	public static final String ZYX59 = "zyx59";
	public static final String ZYX60 = "zyx60";

	public static final String ZYX61 = "zyx61";
	public static final String ZYX62 = "zyx62";
	public static final String ZYX63 = "zyx63";
	public static final String ZYX64 = "zyx64";
	public static final String ZYX65 = "zyx65";
	public static final String ZYX66 = "zyx66";
	public static final String ZYX67 = "zyx67";
	public static final String ZYX68 = "zyx68";
	public static final String ZYX69 = "zyx69";
	public static final String ZYX70 = "zyx70";
	public static final String ZYX71 = "ZYX71";
	public static final String ZYX72 = "ZYX72";
	public static final String ZYX73 = "ZYX73";
	public static final String ZYX74 = "ZYX74";
	public static final String ZYX75 = "ZYX75";
	public static final String ZYX76 = "ZYX76";
	public static final String ZYX77 = "ZYX77";
	public static final String ZYX78 = "ZYX78";
	public static final String ZYX79 = "ZYX79";
	public static final String ZYX80 = "zyx80";
	public static final String ZYX81 = "ZYX81";
	public static final String ZYX82 = "ZYX82";
	public static final String ZYX83 = "ZYX83";
	public static final String ZYX84 = "ZYX84";
	public static final String ZYX85 = "ZYX85";
	public static final String ZYX86 = "ZYX86";
	public static final String ZYX87 = "ZYX87";
	public static final String ZYX88 = "ZYX88";
	public static final String ZYX89 = "ZYX89";
	public static final String ZYX90 = "zyx90";
	public static final String ZYX91 = "ZYX91";
	public static final String ZYX92 = "ZYX92";
	public static final String ZYX93 = "ZYX93";
	public static final String ZYX94 = "ZYX94";
	public static final String ZYX95 = "ZYX95";
	public static final String ZYX96 = "ZYX96";
	public static final String ZYX97 = "ZYX97";
	public static final String ZYX98 = "ZYX98";
	public static final String ZYX99 = "ZYX99";
	public static final String ZYX100 = "ZYX100";
	public static final String ZYX101 = "ZYX101";
	public static final String ZYX102 = "ZYX102";
	public static final String ZYX103 = "ZYX103";
	public static final String ZYX104 = "ZYX104";
	public static final String ZYX105 = "ZYX105";
	public static final String ZYX106 = "ZYX106";
	public static final String ZYX107 = "ZYX107";
	public static final String ZYX108 = "ZYX108";
	public static final String ZYX109 = "ZYX109";
	public static final String ZYX110 = "ZYX110";
	public static final String ZYX111 = "ZYX111";
	public static final String ZYX112 = "ZYX112";
	public static final String ZYX113 = "ZYX113";
	public static final String ZYX114 = "ZYX114";
	public static final String ZYX115 = "ZYX115";
	public static final String ZYX116 = "ZYX116";
	public static final String ZYX117 = "ZYX117";
	public static final String ZYX118 = "ZYX118";
	public static final String ZYX119 = "ZYX119";
	public static final String ZYX120 = "ZYX120";
	public static final String ZYX121 = "ZYX121";
	public static final String ZYX122 = "ZYX122";
	public static final String ZYX123 = "ZYX123";
	public static final String ZYX124 = "ZYX124";
	public static final String ZYX125 = "ZYX125";
	public static final String ZYX126 = "ZYX126";
	public static final String ZYX127 = "ZYX127";
	public static final String ZYX128 = "ZYX128";
	public static final String ZYX129 = "ZYX129";
	public static final String ZYX130 = "ZYX130";
	public static final String ZYX131 = "ZYX131";
	public static final String ZYX132 = "ZYX132";
	public static final String ZYX133 = "ZYX133";
	public static final String ZYX134 = "ZYX134";
	public static final String ZYX135 = "ZYX135";
	public static final String ZYX136 = "ZYX136";
	public static final String ZYX137 = "ZYX137";
	public static final String ZYX138 = "ZYX138";
	public static final String ZYX139 = "ZYX139";
	public static final String ZYX140 = "ZYX140";
	public static final String ZYX141 = "ZYX141";
	public static final String ZYX142 = "ZYX142";
	public static final String ZYX143 = "ZYX143";
	public static final String ZYX144 = "ZYX144";
	public static final String ZYX145 = "ZYX145";
	public static final String ZYX146 = "ZYX146";
	public static final String ZYX147 = "ZYX147";
	public static final String ZYX148 = "ZYX148";
	public static final String ZYX149 = "ZYX149";
	public static final String ZYX150 = "ZYX150";
	public static final String ZYX151 = "ZYX151";
	public static final String ZYX152 = "ZYX152";
	public static final String ZYX153 = "ZYX153";
	public static final String ZYX154 = "ZYX154";
	public static final String ZYX155 = "ZYX155";
	public static final String ZYX156 = "ZYX156";
	public static final String ZYX157 = "ZYX157";
	public static final String ZYX158 = "ZYX158";
	public static final String ZYX159 = "ZYX159";
	public static final String ZYX160 = "ZYX160";
	public static final String ZYX161 = "ZYX161";
	public static final String ZYX162 = "ZYX162";
	public static final String ZYX163 = "ZYX163";
	public static final String ZYX164 = "ZYX164";
	public static final String ZYX165 = "ZYX165";
	public static final String ZYX166 = "ZYX166";
	public static final String ZYX167 = "ZYX167";
	public static final String ZYX168 = "ZYX168";
	public static final String ZYX169 = "ZYX169";
	public static final String ZYX170 = "ZYX170";
	public static final String ZYX171 = "ZYX171";
	public static final String ZYX172 = "ZYX172";
	public static final String ZYX173 = "ZYX173";
	public static final String ZYX174 = "ZYX174";
	public static final String ZYX175 = "ZYX175";
	public static final String ZYX176 = "ZYX176";
	public static final String ZYX177 = "ZYX177";
	public static final String ZYX178 = "ZYX178";
	public static final String ZYX179 = "ZYX179";
	public static final String ZYX180 = "ZYX180";
	public static final String ZYX181 = "ZYX181";
	public static final String ZYX182 = "ZYX182";
	public static final String ZYX183 = "ZYX183";
	public static final String ZYX184 = "ZYX184";
	public static final String ZYX185 = "ZYX185";
	public static final String ZYX186 = "ZYX186";
	public static final String ZYX187 = "ZYX187";
	public static final String ZYX188 = "ZYX188";
	public static final String ZYX189 = "ZYX189";
	public static final String ZYX190 = "ZYX190";

	public String getZyx91() {return zyx91;}
	public void setZyx91(String zyx91) {this.zyx91 = zyx91;}
	public String getZyx92() {return zyx92;}
	public void setZyx92(String zyx92) {this.zyx92 = zyx92;}
	public String getZyx93() {return zyx93;}
	public void setZyx93(String zyx93) {this.zyx93 = zyx93;}
	public String getZyx94() {return zyx94;}
	public void setZyx94(String zyx94) {this.zyx94 = zyx94;}
	public String getZyx95() {return zyx95;}
	public void setZyx95(String zyx95) {this.zyx95 = zyx95;}
	public String getZyx97() {return zyx97;}
	public void setZyx97(String zyx97) {this.zyx97 = zyx97;}
	public String getZyx96() {return zyx96;}
	public void setZyx96(String zyx96) {this.zyx96 = zyx96;}
	public String getZyx98() {return zyx98;}
	public void setZyx98(String zyx98) {this.zyx98 = zyx98;}
	public String getZyx99() {return zyx99;}
	public void setZyx99(String zyx99) {this.zyx99 = zyx99;}
	public String getZyx101() {return zyx101;}
	public void setZyx101(String zyx101) {this.zyx101 = zyx101;}
	public String getZyx102() {return zyx102;}
	public void setZyx102(String zyx102) {this.zyx102 = zyx102;}
	public String getZyx100() {return zyx100;}
	public void setZyx100(String zyx100) {this.zyx100 = zyx100;}
	public String getZyx103() {return zyx103;}
	public void setZyx103(String zyx103) {this.zyx103 = zyx103;}
	public String getZyx105() {return zyx105;}
	public void setZyx105(String zyx105) {this.zyx105 = zyx105;}
	public String getZyx106() {return zyx106;}
	public void setZyx106(String zyx106) {this.zyx106 = zyx106;}
	public String getZyx104() {return zyx104;}
	public void setZyx104(String zyx104) {this.zyx104 = zyx104;}
	public String getZyx107() {return zyx107;}
	public void setZyx107(String zyx107) {this.zyx107 = zyx107;}
	public String getZyx109() {return zyx109;}
	public void setZyx109(String zyx109) {this.zyx109 = zyx109;}
	public String getZyx110() {return zyx110;}
	public void setZyx110(String zyx110) {this.zyx110 = zyx110;}
	public String getZyx108() {return zyx108;}
	public void setZyx108(String zyx108) {this.zyx108 = zyx108;}
	public String zyx31;
	public String zyx32;
	public String zyx33;
	public String zyx34;
	public String zyx35;
	public String zyx36;
	public String zyx37;
	public String zyx38;
	public String zyx39;
	public String zyx40;
	public String zyx41;
	public String zyx42;
	public String zyx43;
	public String zyx44;
	public String zyx45;
	public String zyx46;
	public String zyx47;
	public String zyx48;
	public String zyx49;
	public String zyx50;
	public String zyx51;
	public String zyx52;
	public String zyx53;
	public String zyx54;
	public String zyx55;
	public String zyx56;
	public String zyx57;
	public String zyx58;
	public String zyx59;
	public String zyx60;
	public String zyx61;
	public String getZyx61() {
		return zyx61;
	}
	public void setZyx61(String zyx61) {
		this.zyx61 = zyx61;
	}
	public String getZyx62() {
		return zyx62;
	}
	public void setZyx62(String zyx62) {
		this.zyx62 = zyx62;
	}
	public String getZyx63() {
		return zyx63;
	}
	public void setZyx63(String zyx63) {
		this.zyx63 = zyx63;
	}
	public String getZyx64() {
		return zyx64;
	}
	public void setZyx64(String zyx64) {
		this.zyx64 = zyx64;
	}
	public String getZyx65() {
		return zyx65;
	}
	public void setZyx65(String zyx65) {
		this.zyx65 = zyx65;
	}
	public String getZyx66() {
		return zyx66;
	}
	public void setZyx66(String zyx66) {
		this.zyx66 = zyx66;
	}
	public String getZyx67() {
		return zyx67;
	}
	public void setZyx67(String zyx67) {
		this.zyx67 = zyx67;
	}
	public String getZyx68() {
		return zyx68;
	}
	public void setZyx68(String zyx68) {
		this.zyx68 = zyx68;
	}
	public String getZyx69() {
		return zyx69;
	}
	public void setZyx69(String zyx69) {
		this.zyx69 = zyx69;
	}
	public String getZyx70() {
		return zyx70;
	}
	public void setZyx70(String zyx70) {
		this.zyx70 = zyx70;
	}
	public String getZyx71() {
		return zyx71;
	}
	public void setZyx71(String zyx71) {
		this.zyx71 = zyx71;
	}
	public String getZyx72() {
		return zyx72;
	}
	public void setZyx72(String zyx72) {
		this.zyx72 = zyx72;
	}
	public String getZyx73() {
		return zyx73;
	}
	public void setZyx73(String zyx73) {
		this.zyx73 = zyx73;
	}
	public String getZyx74() {
		return zyx74;
	}
	public void setZyx74(String zyx74) {
		this.zyx74 = zyx74;
	}
	public String getZyx75() {
		return zyx75;
	}
	public void setZyx75(String zyx75) {
		this.zyx75 = zyx75;
	}
	public String getZyx76() {
		return zyx76;
	}
	public void setZyx76(String zyx76) {
		this.zyx76 = zyx76;
	}
	public String getZyx77() {
		return zyx77;
	}
	public void setZyx77(String zyx77) {
		this.zyx77 = zyx77;
	}
	public String getZyx78() {
		return zyx78;
	}
	public void setZyx78(String zyx78) {
		this.zyx78 = zyx78;
	}
	public String getZyx79() {
		return zyx79;
	}
	public void setZyx79(String zyx79) {
		this.zyx79 = zyx79;
	}
	public String getZyx80() {
		return zyx80;
	}
	public void setZyx80(String zyx80) {
		this.zyx80 = zyx80;
	}
	public String getZyx81() {
		return zyx81;
	}
	public void setZyx81(String zyx81) {
		this.zyx81 = zyx81;
	}
	public String getZyx82() {
		return zyx82;
	}
	public void setZyx82(String zyx82) {
		this.zyx82 = zyx82;
	}
	public String getZyx83() {
		return zyx83;
	}
	public void setZyx83(String zyx83) {
		this.zyx83 = zyx83;
	}
	public String getZyx84() {
		return zyx84;
	}
	public void setZyx84(String zyx84) {
		this.zyx84 = zyx84;
	}
	public String getZyx85() {
		return zyx85;
	}
	public void setZyx85(String zyx85) {
		this.zyx85 = zyx85;
	}
	public String getZyx86() {
		return zyx86;
	}
	public void setZyx86(String zyx86) {
		this.zyx86 = zyx86;
	}
	public String getZyx87() {
		return zyx87;
	}
	public void setZyx87(String zyx87) {
		this.zyx87 = zyx87;
	}
	public String getZyx88() {
		return zyx88;
	}
	public void setZyx88(String zyx88) {
		this.zyx88 = zyx88;
	}
	public String getZyx89() {
		return zyx89;
	}
	public void setZyx89(String zyx89) {
		this.zyx89 = zyx89;
	}
	public String getZyx90() {
		return zyx90;
	}
	public void setZyx90(String zyx90) {
		this.zyx90 = zyx90;
	}

	public String zyx62;
	public String zyx63;
	public String zyx64;
	public String zyx65;
	public String zyx66;
	public String zyx67;
	public String zyx68;
	public String zyx69;
	public String zyx70;
	public String zyx71;
	public String zyx72;
	public String zyx73;
	public String zyx74;
	public String zyx75;
	public String zyx76;
	public String zyx77;
	public String zyx78;
	public String zyx79;
	public String zyx80;
	public String zyx81;
	public String zyx82;
	public String zyx83;
	public String zyx84;
	public String zyx85;
	public String zyx86;
	public String zyx87;
	public String zyx88;
	public String zyx89;
	public String zyx90;
	public String zyx91;
	public String zyx92;
	public String zyx93;
	public String zyx94;
	public String zyx95;
	public String zyx96;
	public String zyx97;
	public String zyx98;
	public String zyx99;
	public String zyx100;
	public String zyx101;
	public String zyx102;
	public String zyx103;
	public String zyx104;
	public String zyx105;
	public String zyx106;
	public String zyx107;
	public String zyx108;
	public String zyx109;
	public String zyx110;
	public String zyx111;
	public String zyx112;
	public String zyx113;
	public String zyx114;
	public String zyx115;
	public String zyx116;
	public String zyx117;
	public String zyx118;
	public String zyx119;
	public String zyx120;
	public String zyx121;
	public String zyx122;
	public String zyx123;
	public String zyx124;
	public String zyx125;
	public String zyx126;
	public String zyx127;
	public String zyx128;
	public String zyx129;
	public String zyx130;
	public String zyx131;
	public String zyx132;
	public String zyx133;
	public String zyx134;
	public String zyx135;
	public String zyx136;
	public String zyx137;
	public String zyx138;
	public String zyx139;
	public String zyx140;
	public String zyx141;
	public String zyx142;
	public String zyx143;
	public String zyx144;
	public String zyx145;
	public String zyx146;
	public String zyx147;
	public String zyx148;
	public String zyx149;

	public String getZyx151() {
		return zyx151;
	}

	public void setZyx151(String zyx151) {
		this.zyx151 = zyx151;
	}

	public String getZyx152() {
		return zyx152;
	}

	public void setZyx152(String zyx152) {
		this.zyx152 = zyx152;
	}

	public String getZyx153() {
		return zyx153;
	}

	public void setZyx153(String zyx153) {
		this.zyx153 = zyx153;
	}

	public String getZyx154() {
		return zyx154;
	}

	public void setZyx154(String zyx154) {
		this.zyx154 = zyx154;
	}

	public String getZyx155() {
		return zyx155;
	}

	public void setZyx155(String zyx155) {
		this.zyx155 = zyx155;
	}

	public String getZyx156() {
		return zyx156;
	}

	public void setZyx156(String zyx156) {
		this.zyx156 = zyx156;
	}

	public String getZyx157() {
		return zyx157;
	}

	public void setZyx157(String zyx157) {
		this.zyx157 = zyx157;
	}

	public String getZyx158() {
		return zyx158;
	}

	public void setZyx158(String zyx158) {
		this.zyx158 = zyx158;
	}

	public String getZyx159() {
		return zyx159;
	}

	public void setZyx159(String zyx159) {
		this.zyx159 = zyx159;
	}

	public String getZyx160() {
		return zyx160;
	}

	public void setZyx160(String zyx160) {
		this.zyx160 = zyx160;
	}

	public String getZyx161() {
		return zyx161;
	}

	public void setZyx161(String zyx161) {
		this.zyx161 = zyx161;
	}

	public String getZyx162() {
		return zyx162;
	}

	public void setZyx162(String zyx162) {
		this.zyx162 = zyx162;
	}

	public String getZyx163() {
		return zyx163;
	}

	public void setZyx163(String zyx163) {
		this.zyx163 = zyx163;
	}

	public String getZyx164() {
		return zyx164;
	}

	public void setZyx164(String zyx164) {
		this.zyx164 = zyx164;
	}

	public String getZyx165() {
		return zyx165;
	}

	public void setZyx165(String zyx165) {
		this.zyx165 = zyx165;
	}

	public String getZyx166() {
		return zyx166;
	}

	public void setZyx166(String zyx166) {
		this.zyx166 = zyx166;
	}

	public String getZyx167() {
		return zyx167;
	}

	public void setZyx167(String zyx167) {
		this.zyx167 = zyx167;
	}

	public String getZyx168() {
		return zyx168;
	}

	public void setZyx168(String zyx168) {
		this.zyx168 = zyx168;
	}

	public String getZyx169() {
		return zyx169;
	}

	public void setZyx169(String zyx169) {
		this.zyx169 = zyx169;
	}

	public String getZyx170() {
		return zyx170;
	}

	public void setZyx170(String zyx170) {
		this.zyx170 = zyx170;
	}

	public String getZyx171() {
		return zyx171;
	}

	public void setZyx171(String zyx171) {
		this.zyx171 = zyx171;
	}

	public String getZyx172() {
		return zyx172;
	}

	public void setZyx172(String zyx172) {
		this.zyx172 = zyx172;
	}

	public String getZyx173() {
		return zyx173;
	}

	public void setZyx173(String zyx173) {
		this.zyx173 = zyx173;
	}

	public String getZyx174() {
		return zyx174;
	}

	public void setZyx174(String zyx174) {
		this.zyx174 = zyx174;
	}

	public String getZyx175() {
		return zyx175;
	}

	public void setZyx175(String zyx175) {
		this.zyx175 = zyx175;
	}

	public String getZyx176() {
		return zyx176;
	}

	public void setZyx176(String zyx176) {
		this.zyx176 = zyx176;
	}

	public String getZyx177() {
		return zyx177;
	}

	public void setZyx177(String zyx177) {
		this.zyx177 = zyx177;
	}

	public String getZyx178() {
		return zyx178;
	}

	public void setZyx178(String zyx178) {
		this.zyx178 = zyx178;
	}

	public String getZyx179() {
		return zyx179;
	}

	public void setZyx179(String zyx179) {
		this.zyx179 = zyx179;
	}

	public String getZyx180() {
		return zyx180;
	}

	public void setZyx180(String zyx180) {
		this.zyx180 = zyx180;
	}

	public String getZyx181() {
		return zyx181;
	}

	public void setZyx181(String zyx181) {
		this.zyx181 = zyx181;
	}

	public String getZyx182() {
		return zyx182;
	}

	public void setZyx182(String zyx182) {
		this.zyx182 = zyx182;
	}

	public String getZyx183() {
		return zyx183;
	}

	public void setZyx183(String zyx183) {
		this.zyx183 = zyx183;
	}

	public String getZyx184() {
		return zyx184;
	}

	public void setZyx184(String zyx184) {
		this.zyx184 = zyx184;
	}

	public String getZyx185() {
		return zyx185;
	}

	public void setZyx185(String zyx185) {
		this.zyx185 = zyx185;
	}

	public String getZyx186() {
		return zyx186;
	}

	public void setZyx186(String zyx186) {
		this.zyx186 = zyx186;
	}

	public String getZyx187() {
		return zyx187;
	}

	public void setZyx187(String zyx187) {
		this.zyx187 = zyx187;
	}

	public String getZyx188() {
		return zyx188;
	}

	public void setZyx188(String zyx188) {
		this.zyx188 = zyx188;
	}

	public String getZyx189() {
		return zyx189;
	}

	public void setZyx189(String zyx189) {
		this.zyx189 = zyx189;
	}

	public String getZyx190() {
		return zyx190;
	}

	public void setZyx190(String zyx190) {
		this.zyx190 = zyx190;
	}

	public String zyx150;
	public String zyx151;
	public String zyx152;
	public String zyx153;
	public String zyx154;
	public String zyx155;
	public String zyx156;
	public String zyx157;
	public String zyx158;
	public String zyx159;
	public String zyx160;
	public String zyx161;
	public String zyx162;
	public String zyx163;
	public String zyx164;
	public String zyx165;
	public String zyx166;
	public String zyx167;
	public String zyx168;
	public String zyx169;
	public String zyx170;
	public String zyx171;
	public String zyx172;
	public String zyx173;
	public String zyx174;
	public String zyx175;
	public String zyx176;
	public String zyx177;
	public String zyx178;
	public String zyx179;
	public String zyx180;
	public String zyx181;
	public String zyx182;
	public String zyx183;
	public String zyx184;
	public String zyx185;
	public String zyx186;
	public String zyx187;
	public String zyx188;
	public String zyx189;
	public String zyx190;

	public String getZyx111() {
		return zyx111;
	}

	public void setZyx111(String zyx111) {
		this.zyx111 = zyx111;
	}

	public String getZyx112() {
		return zyx112;
	}

	public void setZyx112(String zyx112) {
		this.zyx112 = zyx112;
	}

	public String getZyx113() {
		return zyx113;
	}

	public void setZyx113(String zyx113) {
		this.zyx113 = zyx113;
	}

	public String getZyx114() {
		return zyx114;
	}

	public void setZyx114(String zyx114) {
		this.zyx114 = zyx114;
	}

	public String getZyx115() {
		return zyx115;
	}

	public void setZyx115(String zyx115) {
		this.zyx115 = zyx115;
	}

	public String getZyx116() {
		return zyx116;
	}

	public void setZyx116(String zyx116) {
		this.zyx116 = zyx116;
	}

	public String getZyx117() {
		return zyx117;
	}

	public void setZyx117(String zyx117) {
		this.zyx117 = zyx117;
	}

	public String getZyx118() {
		return zyx118;
	}

	public void setZyx118(String zyx118) {
		this.zyx118 = zyx118;
	}

	public String getZyx119() {
		return zyx119;
	}

	public void setZyx119(String zyx119) {
		this.zyx119 = zyx119;
	}

	public String getZyx120() {
		return zyx120;
	}

	public void setZyx120(String zyx120) {
		this.zyx120 = zyx120;
	}

	public String getZyx121() {
		return zyx121;
	}

	public void setZyx121(String zyx121) {
		this.zyx121 = zyx121;
	}

	public String getZyx122() {
		return zyx122;
	}

	public void setZyx122(String zyx122) {
		this.zyx122 = zyx122;
	}

	public String getZyx123() {
		return zyx123;
	}

	public void setZyx123(String zyx123) {
		this.zyx123 = zyx123;
	}

	public String getZyx124() {
		return zyx124;
	}

	public void setZyx124(String zyx124) {
		this.zyx124 = zyx124;
	}

	public String getZyx125() {
		return zyx125;
	}

	public void setZyx125(String zyx125) {
		this.zyx125 = zyx125;
	}

	public String getZyx126() {
		return zyx126;
	}

	public void setZyx126(String zyx126) {
		this.zyx126 = zyx126;
	}

	public String getZyx127() {
		return zyx127;
	}

	public void setZyx127(String zyx127) {
		this.zyx127 = zyx127;
	}

	public String getZyx128() {
		return zyx128;
	}

	public void setZyx128(String zyx128) {
		this.zyx128 = zyx128;
	}

	public String getZyx129() {
		return zyx129;
	}

	public void setZyx129(String zyx129) {
		this.zyx129 = zyx129;
	}

	public String getZyx130() {
		return zyx130;
	}

	public void setZyx130(String zyx130) {
		this.zyx130 = zyx130;
	}

	public String getZyx131() {
		return zyx131;
	}

	public void setZyx131(String zyx131) {
		this.zyx131 = zyx131;
	}

	public String getZyx132() {
		return zyx132;
	}

	public void setZyx132(String zyx132) {
		this.zyx132 = zyx132;
	}

	public String getZyx133() {
		return zyx133;
	}

	public void setZyx133(String zyx133) {
		this.zyx133 = zyx133;
	}

	public String getZyx134() {
		return zyx134;
	}

	public void setZyx134(String zyx134) {
		this.zyx134 = zyx134;
	}

	public String getZyx135() {
		return zyx135;
	}

	public void setZyx135(String zyx135) {
		this.zyx135 = zyx135;
	}

	public String getZyx136() {
		return zyx136;
	}

	public void setZyx136(String zyx136) {
		this.zyx136 = zyx136;
	}

	public String getZyx137() {
		return zyx137;
	}

	public void setZyx137(String zyx137) {
		this.zyx137 = zyx137;
	}

	public String getZyx138() {
		return zyx138;
	}

	public void setZyx138(String zyx138) {
		this.zyx138 = zyx138;
	}

	public String getZyx139() {
		return zyx139;
	}

	public void setZyx139(String zyx139) {
		this.zyx139 = zyx139;
	}

	public String getZyx140() {
		return zyx140;
	}

	public void setZyx140(String zyx140) {
		this.zyx140 = zyx140;
	}

	public String getZyx141() {
		return zyx141;
	}

	public void setZyx141(String zyx141) {
		this.zyx141 = zyx141;
	}

	public String getZyx142() {
		return zyx142;
	}

	public void setZyx142(String zyx142) {
		this.zyx142 = zyx142;
	}

	public String getZyx143() {
		return zyx143;
	}

	public void setZyx143(String zyx143) {
		this.zyx143 = zyx143;
	}

	public String getZyx144() {
		return zyx144;
	}

	public void setZyx144(String zyx144) {
		this.zyx144 = zyx144;
	}

	public String getZyx145() {
		return zyx145;
	}

	public void setZyx145(String zyx145) {
		this.zyx145 = zyx145;
	}

	public String getZyx146() {
		return zyx146;
	}

	public void setZyx146(String zyx146) {
		this.zyx146 = zyx146;
	}

	public String getZyx147() {
		return zyx147;
	}

	public void setZyx147(String zyx147) {
		this.zyx147 = zyx147;
	}

	public String getZyx148() {
		return zyx148;
	}

	public void setZyx148(String zyx148) {
		this.zyx148 = zyx148;
	}

	public String getZyx149() {
		return zyx149;
	}

	public void setZyx149(String zyx149) {
		this.zyx149 = zyx149;
	}

	public String getZyx150() {
		return zyx150;
	}

	public void setZyx150(String zyx150) {
		this.zyx150 = zyx150;
	}

	public String getZyx31() {
		return zyx31;
	}
	public void setZyx31(String zyx31) {
		this.zyx31 = zyx31;
	}
	public String getZyx32() {
		return zyx32;
	}
	public void setZyx32(String zyx32) {
		this.zyx32 = zyx32;
	}
	public String getZyx33() {
		return zyx33;
	}
	public void setZyx33(String zyx33) {
		this.zyx33 = zyx33;
	}
	public String getZyx34() {
		return zyx34;
	}
	public void setZyx34(String zyx34) {
		this.zyx34 = zyx34;
	}
	public String getZyx35() {
		return zyx35;
	}
	public void setZyx35(String zyx35) {
		this.zyx35 = zyx35;
	}
	public String getZyx36() {
		return zyx36;
	}
	public void setZyx36(String zyx36) {
		this.zyx36 = zyx36;
	}
	public String getZyx37() {
		return zyx37;
	}
	public void setZyx37(String zyx37) {
		this.zyx37 = zyx37;
	}
	public String getZyx38() {
		return zyx38;
	}
	public void setZyx38(String zyx38) {
		this.zyx38 = zyx38;
	}
	public String getZyx39() {
		return zyx39;
	}
	public void setZyx39(String zyx39) {
		this.zyx39 = zyx39;
	}
	public String getZyx40() {
		return zyx40;
	}
	public void setZyx40(String zyx40) {
		this.zyx40 = zyx40;
	}
	public String getZyx41() {
		return zyx41;
	}
	public void setZyx41(String zyx41) {
		this.zyx41 = zyx41;
	}
	public String getZyx42() {
		return zyx42;
	}
	public void setZyx42(String zyx42) {
		this.zyx42 = zyx42;
	}
	public String getZyx43() {
		return zyx43;
	}
	public void setZyx43(String zyx43) {
		this.zyx43 = zyx43;
	}
	public String getZyx44() {
		return zyx44;
	}
	public void setZyx44(String zyx44) {
		this.zyx44 = zyx44;
	}
	public String getZyx45() {
		return zyx45;
	}
	public void setZyx45(String zyx45) {
		this.zyx45 = zyx45;
	}
	public String getZyx46() {
		return zyx46;
	}
	public void setZyx46(String zyx46) {
		this.zyx46 = zyx46;
	}
	public String getZyx47() {
		return zyx47;
	}
	public void setZyx47(String zyx47) {
		this.zyx47 = zyx47;
	}
	public String getZyx48() {
		return zyx48;
	}
	public void setZyx48(String zyx48) {
		this.zyx48 = zyx48;
	}
	public String getZyx49() {
		return zyx49;
	}
	public void setZyx49(String zyx49) {
		this.zyx49 = zyx49;
	}
	public String getZyx50() {
		return zyx50;
	}
	public void setZyx50(String zyx50) {
		this.zyx50 = zyx50;
	}
	public String getZyx51() {
		return zyx51;
	}
	public void setZyx51(String zyx51) {
		this.zyx51 = zyx51;
	}
	public String getZyx52() {
		return zyx52;
	}
	public void setZyx52(String zyx52) {
		this.zyx52 = zyx52;
	}
	public String getZyx53() {
		return zyx53;
	}
	public void setZyx53(String zyx53) {
		this.zyx53 = zyx53;
	}
	public String getZyx54() {
		return zyx54;
	}
	public void setZyx54(String zyx54) {
		this.zyx54 = zyx54;
	}
	public String getZyx55() {
		return zyx55;
	}
	public void setZyx55(String zyx55) {
		this.zyx55 = zyx55;
	}
	public String getZyx56() {
		return zyx56;
	}
	public void setZyx56(String zyx56) {
		this.zyx56 = zyx56;
	}
	public String getZyx57() {
		return zyx57;
	}
	public void setZyx57(String zyx57) {
		this.zyx57 = zyx57;
	}
	public String getZyx58() {
		return zyx58;
	}
	public void setZyx58(String zyx58) {
		this.zyx58 = zyx58;
	}
	public String getZyx59() {
		return zyx59;
	}
	public void setZyx59(String zyx59) {
		this.zyx59 = zyx59;
	}
	public String getZyx60() {
		return zyx60;
	}
	public void setZyx60(String zyx60) {
		this.zyx60 = zyx60;
	}

}
