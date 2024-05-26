package nc.vo.aim.equipinsp;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此处简要描述此类功能 </b>
 * <p>
 * 此处添加累的描述信息
 * </p>
 * 创建日期:2024-1-30
 * 
 * @author yonyouBQ
 * @version NCPrj ??
 */

public class Equipinspb extends SuperVO {

	/**
	 * 
	 */
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
	 * 子表主键
	 */
	public String pk_equipinspb;
	/**
	 * 来源单据表体行主键
	 */
	public String csourcebillbid;
	/**
	 * 来源单据表头主键
	 */
	public String csourcebillhid;
	/**
	 * 来源单据交易类型
	 */
	public String csourcetranstype;
	/**
	 * 来源单据类型
	 */
	public String csourcetype;
	/**
	 * 来源单据行号
	 */
	public String vsourcerowno;
	/**
	 * 行号
	 */
	public Integer rowno;
	/**
	 * 设备
	 */
	public String pk_equip;
	/**
	 * 巡检原因
	 */
	public String inspreason;
	/**
	 * 巡检日期
	 */
	public UFDate inspdate;
	/**
	 * 部门
	 */
	public String pk_dept;
	/**
	 * 部门版本
	 */
	public String pk_dept_v;
	/**
	 * 业务员
	 */
	public String pk_psndoc;
	/**
	 * 上层单据主键
	 */
	public String pk_equipinsp;
	/**
	 * 时间戳
	 */
	public UFDateTime ts;

	/**
	 * 属性 pk_group的Getter方法.属性名：集团 创建日期:2024-1-30
	 * 
	 * @return nc.vo.org.GroupVO
	 */
	public String getPk_group() {
		return this.pk_group;
	}

	/**
	 * 属性pk_group的Setter方法.属性名：集团 创建日期:2024-1-30
	 * 
	 * @param newPk_group
	 *            nc.vo.org.GroupVO
	 */
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * 属性 pk_org的Getter方法.属性名：组织 创建日期:2024-1-30
	 * 
	 * @return nc.vo.org.AssetOrgVO
	 */
	public String getPk_org() {
		return this.pk_org;
	}

	/**
	 * 属性pk_org的Setter方法.属性名：组织 创建日期:2024-1-30
	 * 
	 * @param newPk_org
	 *            nc.vo.org.AssetOrgVO
	 */
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	/**
	 * 属性 pk_org_v的Getter方法.属性名：组织版本 创建日期:2024-1-30
	 * 
	 * @return nc.vo.vorg.OrgVersionVO
	 */
	public String getPk_org_v() {
		return this.pk_org_v;
	}

	/**
	 * 属性pk_org_v的Setter方法.属性名：组织版本 创建日期:2024-1-30
	 * 
	 * @param newPk_org_v
	 *            nc.vo.vorg.OrgVersionVO
	 */
	public void setPk_org_v(String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}

	/**
	 * 属性 pk_equipinspb的Getter方法.属性名：子表主键 创建日期:2024-1-30
	 * 
	 * @return java.lang.String
	 */
	public String getPk_equipinspb() {
		return this.pk_equipinspb;
	}

	/**
	 * 属性pk_equipinspb的Setter方法.属性名：子表主键 创建日期:2024-1-30
	 * 
	 * @param newPk_equipinspb
	 *            java.lang.String
	 */
	public void setPk_equipinspb(String pk_equipinspb) {
		this.pk_equipinspb = pk_equipinspb;
	}

	/**
	 * 属性 csourcebillbid的Getter方法.属性名：来源单据表体行主键 创建日期:2024-1-30
	 * 
	 * @return java.lang.String
	 */
	public String getCsourcebillbid() {
		return this.csourcebillbid;
	}

	/**
	 * 属性csourcebillbid的Setter方法.属性名：来源单据表体行主键 创建日期:2024-1-30
	 * 
	 * @param newCsourcebillbid
	 *            java.lang.String
	 */
	public void setCsourcebillbid(String csourcebillbid) {
		this.csourcebillbid = csourcebillbid;
	}

	/**
	 * 属性 csourcebillhid的Getter方法.属性名：来源单据表头主键 创建日期:2024-1-30
	 * 
	 * @return java.lang.String
	 */
	public String getCsourcebillhid() {
		return this.csourcebillhid;
	}

	/**
	 * 属性csourcebillhid的Setter方法.属性名：来源单据表头主键 创建日期:2024-1-30
	 * 
	 * @param newCsourcebillhid
	 *            java.lang.String
	 */
	public void setCsourcebillhid(String csourcebillhid) {
		this.csourcebillhid = csourcebillhid;
	}

	/**
	 * 属性 csourcetranstype的Getter方法.属性名：来源单据交易类型 创建日期:2024-1-30
	 * 
	 * @return java.lang.String
	 */
	public String getCsourcetranstype() {
		return this.csourcetranstype;
	}

	/**
	 * 属性csourcetranstype的Setter方法.属性名：来源单据交易类型 创建日期:2024-1-30
	 * 
	 * @param newCsourcetranstype
	 *            java.lang.String
	 */
	public void setCsourcetranstype(String csourcetranstype) {
		this.csourcetranstype = csourcetranstype;
	}

	/**
	 * 属性 csourcetype的Getter方法.属性名：来源单据类型 创建日期:2024-1-30
	 * 
	 * @return java.lang.String
	 */
	public String getCsourcetype() {
		return this.csourcetype;
	}

	/**
	 * 属性csourcetype的Setter方法.属性名：来源单据类型 创建日期:2024-1-30
	 * 
	 * @param newCsourcetype
	 *            java.lang.String
	 */
	public void setCsourcetype(String csourcetype) {
		this.csourcetype = csourcetype;
	}

	/**
	 * 属性 vsourcerowno的Getter方法.属性名：来源单据行号 创建日期:2024-1-30
	 * 
	 * @return java.lang.String
	 */
	public String getVsourcerowno() {
		return this.vsourcerowno;
	}

	/**
	 * 属性vsourcerowno的Setter方法.属性名：来源单据行号 创建日期:2024-1-30
	 * 
	 * @param newVsourcerowno
	 *            java.lang.String
	 */
	public void setVsourcerowno(String vsourcerowno) {
		this.vsourcerowno = vsourcerowno;
	}

	/**
	 * 属性 rowno的Getter方法.属性名：行号 创建日期:2024-1-30
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getRowno() {
		return this.rowno;
	}

	/**
	 * 属性rowno的Setter方法.属性名：行号 创建日期:2024-1-30
	 * 
	 * @param newRowno
	 *            java.lang.Integer
	 */
	public void setRowno(Integer rowno) {
		this.rowno = rowno;
	}

	/**
	 * 属性 pk_equip的Getter方法.属性名：设备 创建日期:2024-1-30
	 * 
	 * @return nc.vo.aim.equip.EquipHeadVO
	 */
	public String getPk_equip() {
		return this.pk_equip;
	}

	/**
	 * 属性pk_equip的Setter方法.属性名：设备 创建日期:2024-1-30
	 * 
	 * @param newPk_equip
	 *            nc.vo.aim.equip.EquipHeadVO
	 */
	public void setPk_equip(String pk_equip) {
		this.pk_equip = pk_equip;
	}

	/**
	 * 属性 inspreason的Getter方法.属性名：巡检原因 创建日期:2024-1-30
	 * 
	 * @return java.lang.String
	 */
	public String getInspreason() {
		return this.inspreason;
	}

	/**
	 * 属性inspreason的Setter方法.属性名：巡检原因 创建日期:2024-1-30
	 * 
	 * @param newInspreason
	 *            java.lang.String
	 */
	public void setInspreason(String inspreason) {
		this.inspreason = inspreason;
	}

	/**
	 * 属性 inspdate的Getter方法.属性名：巡检日期 创建日期:2024-1-30
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public UFDate getInspdate() {
		return this.inspdate;
	}

	/**
	 * 属性inspdate的Setter方法.属性名：巡检日期 创建日期:2024-1-30
	 * 
	 * @param newInspdate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setInspdate(UFDate inspdate) {
		this.inspdate = inspdate;
	}

	/**
	 * 属性 pk_dept的Getter方法.属性名：部门 创建日期:2024-1-30
	 * 
	 * @return nc.vo.org.DeptVO
	 */
	public String getPk_dept() {
		return this.pk_dept;
	}

	/**
	 * 属性pk_dept的Setter方法.属性名：部门 创建日期:2024-1-30
	 * 
	 * @param newPk_dept
	 *            nc.vo.org.DeptVO
	 */
	public void setPk_dept(String pk_dept) {
		this.pk_dept = pk_dept;
	}

	/**
	 * 属性 pk_dept_v的Getter方法.属性名：部门版本 创建日期:2024-1-30
	 * 
	 * @return nc.vo.vorg.DeptVersionVO
	 */
	public String getPk_dept_v() {
		return this.pk_dept_v;
	}

	/**
	 * 属性pk_dept_v的Setter方法.属性名：部门版本 创建日期:2024-1-30
	 * 
	 * @param newPk_dept_v
	 *            nc.vo.vorg.DeptVersionVO
	 */
	public void setPk_dept_v(String pk_dept_v) {
		this.pk_dept_v = pk_dept_v;
	}

	/**
	 * 属性 pk_psndoc的Getter方法.属性名：业务员 创建日期:2024-1-30
	 * 
	 * @return nc.vo.bd.psn.PsndocVO
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * 属性pk_psndoc的Setter方法.属性名：业务员 创建日期:2024-1-30
	 * 
	 * @param newPk_psndoc
	 *            nc.vo.bd.psn.PsndocVO
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * 属性 生成上层主键的Getter方法.属性名：上层主键 创建日期:2024-1-30
	 * 
	 * @return String
	 */
	public String getPk_equipinsp() {
		return this.pk_equipinsp;
	}

	/**
	 * 属性生成上层主键的Setter方法.属性名：上层主键 创建日期:2024-1-30
	 * 
	 * @param newPk_equipinsp
	 *            String
	 */
	public void setPk_equipinsp(String pk_equipinsp) {
		this.pk_equipinsp = pk_equipinsp;
	}

	/**
	 * 属性 生成时间戳的Getter方法.属性名：时间戳 创建日期:2024-1-30
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * 属性生成时间戳的Setter方法.属性名：时间戳 创建日期:2024-1-30
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("aim.equipinspb");
	}
}
