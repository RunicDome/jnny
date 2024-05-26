package nc.vo.pcm.pcmproject;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * 客开项目 
 * 创建日期:2024-4-14
 * @author developer
 * @version NCPrj
 */

public class PcmProject extends SuperVO {

	private static final long serialVersionUID = 1L;
	/**
	 * 集团
	 */
	public String pk_group;
	/**
	 * 组织
	 */
	public String pk_org;
	/**
	 * 组织版本
	 */
	public String pk_org_v;
	/**
	 * 创建人
	 */
	public String creator;
	/**
	 * 创建时间
	 */
	public UFDateTime creationtime;
	/**
	 * 修改人
	 */
	public String modifier;
	/**
	 * 修改时间
	 */
	public UFDateTime modifiedtime;
	/**
	 * 主键
	 */
	public String pk_pcmproject;
	/**
	 * 项目编码
	 */
	public String code;
	/**
	 * 项目名称
	 */
	public String name;
	/**
	 * 制单时间
	 */
	public UFDateTime maketime;
	/**
	 * 最后修改时间
	 */
	public UFDateTime lastmaketime;
	/**
	 * 开工建设日期
	 */
	public UFDateTime start_date;
	/**
	 * 完工日期
	 */
	public UFDateTime end_date;
	/**
	 * 自定义项1
	 */
	public String def1;
	/**
	 * 自定义项2
	 */
	public String def2;
	/**
	 * 自定义项3
	 */
	public String def3;
	/**
	 * 自定义项4
	 */
	public String def4;
	/**
	 * 自定义项5
	 */
	public String def5;
	/**
	 * 自定义项6
	 */
	public String def6;
	/**
	 * 自定义项7
	 */
	public String def7;
	/**
	 * 自定义项8
	 */
	public String def8;
	/**
	 * 自定义项9
	 */
	public String def9;
	/**
	 * 自定义项10
	 */
	public String def10;
	/**
	 * 自定义项11
	 */
	public String def11;
	/**
	 * 自定义项12
	 */
	public String def12;
	/**
	 * 自定义项13
	 */
	public String def13;
	/**
	 * 自定义项14
	 */
	public String def14;
	/**
	 * 自定义项15
	 */
	public String def15;
	/**
	 * 自定义项16
	 */
	public String def16;
	/**
	 * 自定义项17
	 */
	public String def17;
	/**
	 * 自定义项18
	 */
	public String def18;
	/**
	 * 自定义项19
	 */
	public String def19;
	/**
	 * 自定义项20
	 */
	public String def20;
	/**
	 * 单据日期
	 */
	public UFDate billdate;
	/**
	 * 所属组织
	 */
	public String pkorg;
	/**
	 * 业务类型
	 */
	public String busitype;
	/**
	 * 制单人
	 */
	public String billmaker;
	/**
	 * 审批人
	 */
	public String approver;
	/**
	 * 审批状态
	 */
	public Integer approvestatus;
	/**
	 * 审批批语
	 */
	public String approvenote;
	/**
	 * 审批时间
	 */
	public UFDateTime approvedate;
	/**
	 * 交易类型
	 */
	public String transtype;
	/**
	 * 单据类型
	 */
	public String billtype;
	/**
	 * 交易类型pk
	 */
	public String transtypepk;
	/**
	 * 来源单据类型
	 */
	public String srcbilltype;
	/**
	 * 来源单据id
	 */
	public String srcbillid;
	/**
	 * 修订枚举
	 */
	public Integer emendenum;
	/**
	 * 单据版本pk
	 */
	public String billversionpk;
	/**
	 * 时间戳
	 */
	public UFDateTime ts;
	
	public static final String PK_PCMROJECT = "pk_pcmproject";
	public static final String CODE = "code";
	public static final String NAME = "name";

	/**
	 * 属性 pk_group的Getter方法.属性名：集团 创建日期:2024-4-14
	 * 
	 * @return nc.vo.org.GroupVO
	 */
	public String getPk_group() {
		return this.pk_group;
	}

	/**
	 * 属性pk_group的Setter方法.属性名：集团 创建日期:2024-4-14
	 * 
	 * @param newPk_group
	 *            nc.vo.org.GroupVO
	 */
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * 属性 pk_org的Getter方法.属性名：组织 创建日期:2024-4-14
	 * 
	 * @return nc.vo.org.OrgVO
	 */
	public String getPk_org() {
		return this.pk_org;
	}

	/**
	 * 属性pk_org的Setter方法.属性名：组织 创建日期:2024-4-14
	 * 
	 * @param newPk_org
	 *            nc.vo.org.OrgVO
	 */
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	/**
	 * 属性 pk_org_v的Getter方法.属性名：组织版本 创建日期:2024-4-14
	 * 
	 * @return nc.vo.vorg.OrgVersionVO
	 */
	public String getPk_org_v() {
		return this.pk_org_v;
	}

	/**
	 * 属性pk_org_v的Setter方法.属性名：组织版本 创建日期:2024-4-14
	 * 
	 * @param newPk_org_v
	 *            nc.vo.vorg.OrgVersionVO
	 */
	public void setPk_org_v(String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}

	/**
	 * 属性 creator的Getter方法.属性名：创建人 创建日期:2024-4-14
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getCreator() {
		return this.creator;
	}

	/**
	 * 属性creator的Setter方法.属性名：创建人 创建日期:2024-4-14
	 * 
	 * @param newCreator
	 *            nc.vo.sm.UserVO
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * 属性 creationtime的Getter方法.属性名：创建时间 创建日期:2024-4-14
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getCreationtime() {
		return this.creationtime;
	}

	/**
	 * 属性creationtime的Setter方法.属性名：创建时间 创建日期:2024-4-14
	 * 
	 * @param newCreationtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	/**
	 * 属性 modifier的Getter方法.属性名：修改人 创建日期:2024-4-14
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getModifier() {
		return this.modifier;
	}

	/**
	 * 属性modifier的Setter方法.属性名：修改人 创建日期:2024-4-14
	 * 
	 * @param newModifier
	 *            nc.vo.sm.UserVO
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * 属性 modifiedtime的Getter方法.属性名：修改时间 创建日期:2024-4-14
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getModifiedtime() {
		return this.modifiedtime;
	}

	/**
	 * 属性modifiedtime的Setter方法.属性名：修改时间 创建日期:2024-4-14
	 * 
	 * @param newModifiedtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	/**
	 * 属性 pk_pcmproject的Getter方法.属性名：主键 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getPk_pcmproject() {
		return this.pk_pcmproject;
	}

	/**
	 * 属性pk_pcmproject的Setter方法.属性名：主键 创建日期:2024-4-14
	 * 
	 * @param newPk_pcmproject
	 *            java.lang.String
	 */
	public void setPk_pcmproject(String pk_pcmproject) {
		this.pk_pcmproject = pk_pcmproject;
	}

	/**
	 * 属性 code的Getter方法.属性名：项目编码 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * 属性code的Setter方法.属性名：项目编码 创建日期:2024-4-14
	 * 
	 * @param newCode
	 *            java.lang.String
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 属性 name的Getter方法.属性名：项目名称 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 属性name的Setter方法.属性名：项目名称 创建日期:2024-4-14
	 * 
	 * @param newName
	 *            java.lang.String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 属性 maketime的Getter方法.属性名：制单时间 创建日期:2024-4-14
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getMaketime() {
		return this.maketime;
	}

	/**
	 * 属性maketime的Setter方法.属性名：制单时间 创建日期:2024-4-14
	 * 
	 * @param newMaketime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setMaketime(UFDateTime maketime) {
		this.maketime = maketime;
	}

	/**
	 * 属性 lastmaketime的Getter方法.属性名：最后修改时间 创建日期:2024-4-14
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getLastmaketime() {
		return this.lastmaketime;
	}

	/**
	 * 属性lastmaketime的Setter方法.属性名：最后修改时间 创建日期:2024-4-14
	 * 
	 * @param newLastmaketime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setLastmaketime(UFDateTime lastmaketime) {
		this.lastmaketime = lastmaketime;
	}

	/**
	 * 属性 start_date的Getter方法.属性名：开工建设日期 创建日期:2024-4-14
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getStart_date() {
		return this.start_date;
	}

	/**
	 * 属性start_date的Setter方法.属性名：开工建设日期 创建日期:2024-4-14
	 * 
	 * @param newStart_date
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setStart_date(UFDateTime start_date) {
		this.start_date = start_date;
	}

	/**
	 * 属性 end_date的Getter方法.属性名：完工日期 创建日期:2024-4-14
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getEnd_date() {
		return this.end_date;
	}

	/**
	 * 属性end_date的Setter方法.属性名：完工日期 创建日期:2024-4-14
	 * 
	 * @param newEnd_date
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setEnd_date(UFDateTime end_date) {
		this.end_date = end_date;
	}

	/**
	 * 属性 def1的Getter方法.属性名：自定义项1 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef1() {
		return this.def1;
	}

	/**
	 * 属性def1的Setter方法.属性名：自定义项1 创建日期:2024-4-14
	 * 
	 * @param newDef1
	 *            java.lang.String
	 */
	public void setDef1(String def1) {
		this.def1 = def1;
	}

	/**
	 * 属性 def2的Getter方法.属性名：自定义项2 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef2() {
		return this.def2;
	}

	/**
	 * 属性def2的Setter方法.属性名：自定义项2 创建日期:2024-4-14
	 * 
	 * @param newDef2
	 *            java.lang.String
	 */
	public void setDef2(String def2) {
		this.def2 = def2;
	}

	/**
	 * 属性 def3的Getter方法.属性名：自定义项3 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef3() {
		return this.def3;
	}

	/**
	 * 属性def3的Setter方法.属性名：自定义项3 创建日期:2024-4-14
	 * 
	 * @param newDef3
	 *            java.lang.String
	 */
	public void setDef3(String def3) {
		this.def3 = def3;
	}

	/**
	 * 属性 def4的Getter方法.属性名：自定义项4 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef4() {
		return this.def4;
	}

	/**
	 * 属性def4的Setter方法.属性名：自定义项4 创建日期:2024-4-14
	 * 
	 * @param newDef4
	 *            java.lang.String
	 */
	public void setDef4(String def4) {
		this.def4 = def4;
	}

	/**
	 * 属性 def5的Getter方法.属性名：自定义项5 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef5() {
		return this.def5;
	}

	/**
	 * 属性def5的Setter方法.属性名：自定义项5 创建日期:2024-4-14
	 * 
	 * @param newDef5
	 *            java.lang.String
	 */
	public void setDef5(String def5) {
		this.def5 = def5;
	}

	/**
	 * 属性 def6的Getter方法.属性名：自定义项6 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef6() {
		return this.def6;
	}

	/**
	 * 属性def6的Setter方法.属性名：自定义项6 创建日期:2024-4-14
	 * 
	 * @param newDef6
	 *            java.lang.String
	 */
	public void setDef6(String def6) {
		this.def6 = def6;
	}

	/**
	 * 属性 def7的Getter方法.属性名：自定义项7 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef7() {
		return this.def7;
	}

	/**
	 * 属性def7的Setter方法.属性名：自定义项7 创建日期:2024-4-14
	 * 
	 * @param newDef7
	 *            java.lang.String
	 */
	public void setDef7(String def7) {
		this.def7 = def7;
	}

	/**
	 * 属性 def8的Getter方法.属性名：自定义项8 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef8() {
		return this.def8;
	}

	/**
	 * 属性def8的Setter方法.属性名：自定义项8 创建日期:2024-4-14
	 * 
	 * @param newDef8
	 *            java.lang.String
	 */
	public void setDef8(String def8) {
		this.def8 = def8;
	}

	/**
	 * 属性 def9的Getter方法.属性名：自定义项9 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef9() {
		return this.def9;
	}

	/**
	 * 属性def9的Setter方法.属性名：自定义项9 创建日期:2024-4-14
	 * 
	 * @param newDef9
	 *            java.lang.String
	 */
	public void setDef9(String def9) {
		this.def9 = def9;
	}

	/**
	 * 属性 def10的Getter方法.属性名：自定义项10 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef10() {
		return this.def10;
	}

	/**
	 * 属性def10的Setter方法.属性名：自定义项10 创建日期:2024-4-14
	 * 
	 * @param newDef10
	 *            java.lang.String
	 */
	public void setDef10(String def10) {
		this.def10 = def10;
	}

	/**
	 * 属性 def11的Getter方法.属性名：自定义项11 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef11() {
		return this.def11;
	}

	/**
	 * 属性def11的Setter方法.属性名：自定义项11 创建日期:2024-4-14
	 * 
	 * @param newDef11
	 *            java.lang.String
	 */
	public void setDef11(String def11) {
		this.def11 = def11;
	}

	/**
	 * 属性 def12的Getter方法.属性名：自定义项12 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef12() {
		return this.def12;
	}

	/**
	 * 属性def12的Setter方法.属性名：自定义项12 创建日期:2024-4-14
	 * 
	 * @param newDef12
	 *            java.lang.String
	 */
	public void setDef12(String def12) {
		this.def12 = def12;
	}

	/**
	 * 属性 def13的Getter方法.属性名：自定义项13 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef13() {
		return this.def13;
	}

	/**
	 * 属性def13的Setter方法.属性名：自定义项13 创建日期:2024-4-14
	 * 
	 * @param newDef13
	 *            java.lang.String
	 */
	public void setDef13(String def13) {
		this.def13 = def13;
	}

	/**
	 * 属性 def14的Getter方法.属性名：自定义项14 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef14() {
		return this.def14;
	}

	/**
	 * 属性def14的Setter方法.属性名：自定义项14 创建日期:2024-4-14
	 * 
	 * @param newDef14
	 *            java.lang.String
	 */
	public void setDef14(String def14) {
		this.def14 = def14;
	}

	/**
	 * 属性 def15的Getter方法.属性名：自定义项15 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef15() {
		return this.def15;
	}

	/**
	 * 属性def15的Setter方法.属性名：自定义项15 创建日期:2024-4-14
	 * 
	 * @param newDef15
	 *            java.lang.String
	 */
	public void setDef15(String def15) {
		this.def15 = def15;
	}

	/**
	 * 属性 def16的Getter方法.属性名：自定义项16 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef16() {
		return this.def16;
	}

	/**
	 * 属性def16的Setter方法.属性名：自定义项16 创建日期:2024-4-14
	 * 
	 * @param newDef16
	 *            java.lang.String
	 */
	public void setDef16(String def16) {
		this.def16 = def16;
	}

	/**
	 * 属性 def17的Getter方法.属性名：自定义项17 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef17() {
		return this.def17;
	}

	/**
	 * 属性def17的Setter方法.属性名：自定义项17 创建日期:2024-4-14
	 * 
	 * @param newDef17
	 *            java.lang.String
	 */
	public void setDef17(String def17) {
		this.def17 = def17;
	}

	/**
	 * 属性 def18的Getter方法.属性名：自定义项18 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef18() {
		return this.def18;
	}

	/**
	 * 属性def18的Setter方法.属性名：自定义项18 创建日期:2024-4-14
	 * 
	 * @param newDef18
	 *            java.lang.String
	 */
	public void setDef18(String def18) {
		this.def18 = def18;
	}

	/**
	 * 属性 def19的Getter方法.属性名：自定义项19 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef19() {
		return this.def19;
	}

	/**
	 * 属性def19的Setter方法.属性名：自定义项19 创建日期:2024-4-14
	 * 
	 * @param newDef19
	 *            java.lang.String
	 */
	public void setDef19(String def19) {
		this.def19 = def19;
	}

	/**
	 * 属性 def20的Getter方法.属性名：自定义项20 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getDef20() {
		return this.def20;
	}

	/**
	 * 属性def20的Setter方法.属性名：自定义项20 创建日期:2024-4-14
	 * 
	 * @param newDef20
	 *            java.lang.String
	 */
	public void setDef20(String def20) {
		this.def20 = def20;
	}

	/**
	 * 属性 billdate的Getter方法.属性名：单据日期 创建日期:2024-4-14
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public UFDate getBilldate() {
		return this.billdate;
	}

	/**
	 * 属性billdate的Setter方法.属性名：单据日期 创建日期:2024-4-14
	 * 
	 * @param newBilldate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setBilldate(UFDate billdate) {
		this.billdate = billdate;
	}

	/**
	 * 属性 pkorg的Getter方法.属性名：所属组织 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getPkorg() {
		return this.pkorg;
	}

	/**
	 * 属性pkorg的Setter方法.属性名：所属组织 创建日期:2024-4-14
	 * 
	 * @param newPkorg
	 *            java.lang.String
	 */
	public void setPkorg(String pkorg) {
		this.pkorg = pkorg;
	}

	/**
	 * 属性 busitype的Getter方法.属性名：业务类型 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getBusitype() {
		return this.busitype;
	}

	/**
	 * 属性busitype的Setter方法.属性名：业务类型 创建日期:2024-4-14
	 * 
	 * @param newBusitype
	 *            java.lang.String
	 */
	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}

	/**
	 * 属性 billmaker的Getter方法.属性名：制单人 创建日期:2024-4-14
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getBillmaker() {
		return this.billmaker;
	}

	/**
	 * 属性billmaker的Setter方法.属性名：制单人 创建日期:2024-4-14
	 * 
	 * @param newBillmaker
	 *            nc.vo.sm.UserVO
	 */
	public void setBillmaker(String billmaker) {
		this.billmaker = billmaker;
	}

	/**
	 * 属性 approver的Getter方法.属性名：审批人 创建日期:2024-4-14
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getApprover() {
		return this.approver;
	}

	/**
	 * 属性approver的Setter方法.属性名：审批人 创建日期:2024-4-14
	 * 
	 * @param newApprover
	 *            nc.vo.sm.UserVO
	 */
	public void setApprover(String approver) {
		this.approver = approver;
	}

	/**
	 * 属性 approvestatus的Getter方法.属性名：审批状态 创建日期:2024-4-14
	 * 
	 * @return nc.vo.pub.pf.BillStatusEnum
	 */
	public Integer getApprovestatus() {
		return this.approvestatus;
	}

	/**
	 * 属性approvestatus的Setter方法.属性名：审批状态 创建日期:2024-4-14
	 * 
	 * @param newApprovestatus
	 *            nc.vo.pub.pf.BillStatusEnum
	 */
	public void setApprovestatus(Integer approvestatus) {
		this.approvestatus = approvestatus;
	}

	/**
	 * 属性 approvenote的Getter方法.属性名：审批批语 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getApprovenote() {
		return this.approvenote;
	}

	/**
	 * 属性approvenote的Setter方法.属性名：审批批语 创建日期:2024-4-14
	 * 
	 * @param newApprovenote
	 *            java.lang.String
	 */
	public void setApprovenote(String approvenote) {
		this.approvenote = approvenote;
	}

	/**
	 * 属性 approvedate的Getter方法.属性名：审批时间 创建日期:2024-4-14
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getApprovedate() {
		return this.approvedate;
	}

	/**
	 * 属性approvedate的Setter方法.属性名：审批时间 创建日期:2024-4-14
	 * 
	 * @param newApprovedate
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setApprovedate(UFDateTime approvedate) {
		this.approvedate = approvedate;
	}

	/**
	 * 属性 transtype的Getter方法.属性名：交易类型 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getTranstype() {
		return this.transtype;
	}

	/**
	 * 属性transtype的Setter方法.属性名：交易类型 创建日期:2024-4-14
	 * 
	 * @param newTranstype
	 *            java.lang.String
	 */
	public void setTranstype(String transtype) {
		this.transtype = transtype;
	}

	/**
	 * 属性 billtype的Getter方法.属性名：单据类型 创建日期:2024-4-14
	 * 
	 * @return nc.vo.pub.billtype.BilltypeVO
	 */
	public String getBilltype() {
		return this.billtype;
	}

	/**
	 * 属性billtype的Setter方法.属性名：单据类型 创建日期:2024-4-14
	 * 
	 * @param newBilltype
	 *            nc.vo.pub.billtype.BilltypeVO
	 */
	public void setBilltype(String billtype) {
		this.billtype = billtype;
	}

	/**
	 * 属性 transtypepk的Getter方法.属性名：交易类型pk 创建日期:2024-4-14
	 * 
	 * @return nc.vo.pub.billtype.BilltypeVO
	 */
	public String getTranstypepk() {
		return this.transtypepk;
	}

	/**
	 * 属性transtypepk的Setter方法.属性名：交易类型pk 创建日期:2024-4-14
	 * 
	 * @param newTranstypepk
	 *            nc.vo.pub.billtype.BilltypeVO
	 */
	public void setTranstypepk(String transtypepk) {
		this.transtypepk = transtypepk;
	}

	/**
	 * 属性 srcbilltype的Getter方法.属性名：来源单据类型 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getSrcbilltype() {
		return this.srcbilltype;
	}

	/**
	 * 属性srcbilltype的Setter方法.属性名：来源单据类型 创建日期:2024-4-14
	 * 
	 * @param newSrcbilltype
	 *            java.lang.String
	 */
	public void setSrcbilltype(String srcbilltype) {
		this.srcbilltype = srcbilltype;
	}

	/**
	 * 属性 srcbillid的Getter方法.属性名：来源单据id 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getSrcbillid() {
		return this.srcbillid;
	}

	/**
	 * 属性srcbillid的Setter方法.属性名：来源单据id 创建日期:2024-4-14
	 * 
	 * @param newSrcbillid
	 *            java.lang.String
	 */
	public void setSrcbillid(String srcbillid) {
		this.srcbillid = srcbillid;
	}

	/**
	 * 属性 emendenum的Getter方法.属性名：修订枚举 创建日期:2024-4-14
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getEmendenum() {
		return this.emendenum;
	}

	/**
	 * 属性emendenum的Setter方法.属性名：修订枚举 创建日期:2024-4-14
	 * 
	 * @param newEmendenum
	 *            java.lang.Integer
	 */
	public void setEmendenum(Integer emendenum) {
		this.emendenum = emendenum;
	}

	/**
	 * 属性 billversionpk的Getter方法.属性名：单据版本pk 创建日期:2024-4-14
	 * 
	 * @return java.lang.String
	 */
	public String getBillversionpk() {
		return this.billversionpk;
	}

	/**
	 * 属性billversionpk的Setter方法.属性名：单据版本pk 创建日期:2024-4-14
	 * 
	 * @param newBillversionpk
	 *            java.lang.String
	 */
	public void setBillversionpk(String billversionpk) {
		this.billversionpk = billversionpk;
	}

	/**
	 * 属性 生成时间戳的Getter方法.属性名：时间戳 创建日期:2024-4-14
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * 属性生成时间戳的Setter方法.属性名：时间戳 创建日期:2024-4-14
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("pcm.pcmproject");
	}
}
