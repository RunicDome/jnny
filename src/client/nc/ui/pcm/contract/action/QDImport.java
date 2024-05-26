package nc.ui.pcm.contract.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import nc.bp.impl.uap.oid.OidBaseAlgorithm;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.bs.uap.oid.OidGenerator;
import nc.cmp.utils.BillcodeGenerater;
import nc.itf.pcm.contract.pvt.IContractImport;
import nc.jdbc.framework.MockDataSource;
import nc.jdbc.framework.generator.IdGenerator;
import nc.jdbc.framework.generator.SequenceGenerator;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.NCAction;
import nc.vo.bd.material.MaterialVersionVO;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContrWorksVO;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@SuppressWarnings({ "restriction", "unused" })
public class QDImport extends NCAction {
	// 清单合同Excel导入
	public QDImport() {
		// TODO Auto-generated constructor stub
		setCode("importQDHT");
		setBtnName("Excel导入");
	}

	private static final long serialVersionUID = -1261891773744296055L;
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
	IContractImport htdr = NCLocator.getInstance()
			.lookup(IContractImport.class);

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		if (isimport()) {
			JFileChooser chooser = new JFileChooser();// 文件选择对话框
			File file = null;
			chooser.setFileFilter(new FileNameExtensionFilter("Excel文件", "xlsx"));// 设置只过滤扩展名为.xls的Excel文件
			int returnValue = chooser.showOpenDialog(null);// 打开窗口
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();
			} else {
				// MessageDialog.showOkCancelDlg(null, "提示", "未选择文件！");
				throw new BusinessException("未选择文件！");
			}
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
			XSSFSheet sheet = workbook.getSheetAt(0);
			List<List<String>> list = new ArrayList<List<String>>();
			int rownum = sheet.getPhysicalNumberOfRows();// 获取最大行数
			for (int i = 1; i < rownum; i++) {
				Row row = sheet.getRow(i);// 获取每一行
				List<String> rowList = new ArrayList<String>();
				int colnum = row.getPhysicalNumberOfCells();// 获取最大列数
				if (row != null) {
					for (int j = 0; j < colnum; j++) {
						String cellData = (String) getCellFormatValue(row
								.getCell(j));
						rowList.add(cellData);
					}
				}

				if (rowList != null)
					list.add(rowList);
			}
			// 解析Excel
			ContractBillVO[] qdvos = new ContractBillVO[list.size()];
			for (int n = 0; n < qdvos.length; n++) {
				List<String> ls = list.get(n);// 每一行
				ContractBillVO qdvo = null;
				String fname = file.getName();
				if(fname.contains("GH")){
					qdvo = setGHBillVO(ls);
				}else if(fname.contains("JH")){
					qdvo = setJHBillVO(ls);
				}else if(fname.contains("RD")){
					qdvo = setRDBillVO(ls);
				}
				qdvos[n] = qdvo;
			}
			htdr.insertBillVO(qdvos);
			MessageDialog.showHintDlg(null, "提示信息", "成功导入[" + qdvos.length
					+ "]条记录");
		} else {
			throw new BusinessException("无权限导入！");
		}
	}

	// 判断是否有权限导入（通过 QDHTDR 角色判断）
	public boolean isimport() throws DAOException {
		String fg = "0";
		String user = InvocationInfoProxy.getInstance().getUserId();// 当前用户主键
		String querySql = "select c.cuserid, a.pk_role, a.role_name,c.user_name from sm_role a left join sm_user_role b  on a.pk_role=b.pk_role "
				+ " left join sm_user c on c.cuserid=b.cuserid  where a.role_code = 'QDHTDR'";
		List<Object[]> resultList = getDao.query(querySql);
		//System.out.println("sql===" + querySql);
		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				Object[] item = resultList.get(i);
				if (user.equals(item[0])) {
					fg = "1";
				}
			}
		}
		//System.out.println("当前登录用户主键：" + user);
		// 暂时屏蔽
		/*
		 * if ("1".equals(fg)) { return true; } else { return false; }
		 */

		return true;
	}

	public ContractBillVO setBillVO(List<String> ls) throws Exception {
		ContractBillVO billvo = new ContractBillVO();// 聚合VO
		ContrHeadVO headvo = new ContrHeadVO();// 表头VO
		IBillcodeManage iBillcodeManage = (IBillcodeManage) NCLocator
				.getInstance().lookup(IBillcodeManage.class.getName());

		String pk_contr = generate(1)[0];// 合同主键
//		System.out.println("pk_contr===" + pk_contr);
		// *****表头VO*****
		headvo.setPk_contr(pk_contr);// 合同主键
		headvo.setStatus(VOStatus.NEW);
		String bill_code = new BillcodeGenerater().getBillCode("4D42",
				"0001A1100000000001QS", "0001A110000000000HYQ", null, null);// 单据类型
		headvo.setBill_code(bill_code);// 合同编码
		headvo.setBill_name(ls.get(0));// 合同名称
		headvo.setBill_status(-1);// 合同状态 -1=自由态
		headvo.setBalaflag(1);// 结算状态 1=未结算,2=结算中,3=已结算
		headvo.setBill_type("4D42");// 单据类型
		headvo.setBillmaker("1001A110000000000HV8");// 制单人 暂定 OA审批
		headvo.setBillmaketime(new UFDate());// 制单时间
		headvo.setBusi_type("1001A21000000005UIH1");// 业务流程 拉清单发包合同流程
		headvo.setContr_version(1);// 版本号
		headvo.setCreationtime(new UFDateTime());// 创建时间
		headvo.setCreator("1001A110000000000HV8");// 创建人 暂定 OA审批
		headvo.setFlexible_flag(new UFBoolean(false)); // 预算柔性标记 false
		headvo.setMemo(ls.get(18));// 备注
		headvo.setNcurrent_mny(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 合同金额
		headvo.setNexchangerate(new UFDouble(1));// 签约折本汇率 默认1
		headvo.setNtot_sld_mny(new UFDouble(0));// 预付款累计冲销
		headvo.setNtot_sched_mny(new UFDouble(0));// 累计进度款
		headvo.setNtot_sld_mny(new UFDouble(0));// 累计应付
		headvo.setOrig_current_mny(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 合同金额（原币）
		// 合同类型
		String htlxmc = ls.get(1);// 合同类型名称
		String pk_contracttype = getPrimarykey("pm_contracttype", "type_name",
				htlxmc, "pk_contracttype", "0001A110000000000HYQ");
		headvo.setPk_contracttype(pk_contracttype);// 合同类型
		headvo.setPk_currtype("1002Z0100000000001K1");// 币种 默认人民币
		headvo.setPk_currtype_org("1002Z0100000000001K1");// 组织币种 默认人民币
		headvo.setPk_financeorg("0001A110000000000HYQ");// 应付财务组织 01热力
		headvo.setPk_financeorg_v("0001A110000000000HYQ");// 应付财务组织多版本 01热力
		headvo.setPk_group("0001A1100000000001QS");// 集团 热力集团
		headvo.setPk_org("0001A110000000000HYQ");// 组织 热力集团
		headvo.setPk_org_v("0001A110000000000HYP");// 组织多版本 热力集团
		// 签约部门
		String qybmmc = ls.get(7);// 签约部门名称
		String pk_promise_dept = getPrimarykey("org_dept", "name", qybmmc,
				"pk_dept", "0001A110000000000HYQ");
		headvo.setPk_promise_dept(pk_promise_dept);// 签约部门
		// 签约部门多版本
		String pk_pro_dept_v = getPrimarykey("org_dept", "pk_dept",
				pk_promise_dept, "pk_vid", "0001A110000000000HYQ");
		headvo.setPk_pro_dept_v(pk_pro_dept_v);// 签约部门多版本
		// 签约人
		String qyr = ls.get(9);// 签约人
		String pk_promisepsn = getPrimarykey("bd_psndoc", "name", qyr,
				"pk_psndoc", "0001A110000000000HYQ");
		headvo.setPk_promisepsn(pk_promisepsn);// 签约人
		// 项目
		String xmbm = ls.get(4);// 项目编码
		String pk_project = getPrimarykey("BD_PROJECT", "PROJECT_CODE", xmbm,
				"PK_PROJECT", "0001A110000000000HYQ");
		headvo.setPk_project(pk_project);// 项目
		// 供应商
		String gys = ls.get(3);// 供应商
		String pk_supplier = getPrimarykey("bd_supplier", "name", gys,
				"pk_supplier", "0001A110000000000HYQ");
		headvo.setPk_supplier(pk_supplier);// 供应商
		headvo.setPk_transitype("0001A21000000000PG0R");// 交易类型 - 清单发包合同
		headvo.setPlan_validate_time(new UFDate(ls.get(10)));// 开始日期(计划生效日期)
		headvo.setPlan_teminate_time(new UFDate(ls.get(11)));// 结算日期(计划终止日期)
		headvo.setPromisetime(new UFDate(ls.get(6)));// 签约日期
		headvo.setQual_time(1);// 质保期(月) 1
		headvo.setServicebalrule(100);// 进度款控制比例(%) 默认 100
		headvo.setServicebalrule(5);// 5=合同价格+结算日期价格
		headvo.setTransi_type("4D42-01");// 交易类型编码 清单发包合同
		// 自定义项1 = 代控部门分管领导名称
		/*
		 * String dkfgldmc = ls.get(14);// 代控部门分管领导名称 String hdef1 =
		 * getPrimarykey("BD_DEFDOC", "NAME", dkfgldmc, "PK_DEFDOC");
		 */
		headvo.setHdef1("1001A210000000072TSY");// 代控部门分管领导名称（自定义档案） 默认 王思合
		headvo.setHdef21("1001A2100000000B68C3");// 是否特殊合同
		headvo.setHdef22("1001A2100000000B68C3");// 是否模板合同
		headvo.setHdef26(ls.get(14));// 原合同金额
		headvo.setHdef27(ls.get(16));// 已付款金额
		headvo.setHdef28(ls.get(17));// 审定值
		headvo.setHdef30(ls.get(15));// 合同折扣金额
		// 自定义项31 = 代控部门
		String dkbm = ls.get(8);// 代控部门
		String hdef31 = getPrimarykey("ORG_DEPT", "NAME", dkbm, "PK_ORG",
				"0001A110000000000HYQ");
		headvo.setHdef31(hdef31);// 代控部门
		// *****表体VO*****
		ContrWorksVO itemvo = new ContrWorksVO();// 表体VO

		itemvo.setPk_contr(pk_contr);// 合同主键

		itemvo.setPk_project(pk_project);// 项目编码
		ProjectHeadVO ProjectVO = (ProjectHeadVO) HYPubBO_Client
				.queryByPrimaryKey(ProjectHeadVO.class, pk_project); // 项目VO
		itemvo.setNprom_price(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 单价
		itemvo.setCurr_num(new UFDouble(1));// 数量 默认1
		itemvo.setCurr_mny(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 金额
		itemvo.setOrig_curr_mny(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 金额（原币）
		// CBS
		String cbsname = ls.get(2);// CBS名称
		String pk_cbsnode = getCBSPrimarykey(cbsname, pk_project,
				ProjectVO.getPk_projectclass(), "0001A110000000000HYQ");
		itemvo.setPk_cbsnode(pk_cbsnode);// CBS名称
		itemvo.setPk_contr(headvo.getPrimaryKey());// 主表主键
		itemvo.setPk_group("0001A1100000000001QS");// 集团 热力集团
		// 物料
		String wlmc = ls.get(2);// 物料名称（CBS名称）
		String pk_material = getPrimarykey("BD_MATERIAL_V", "NAME", wlmc,
				"PK_SOURCE", "0001A110000000000HYQ");
		itemvo.setPk_material(pk_material);// 物料
		// 物料（多版本）
		String pk_material_v = getPrimarykey("BD_MATERIAL_V", "NAME", wlmc,
				"PK_MATERIAL", "0001A110000000000HYQ");
		itemvo.setPk_material_v(pk_material_v);// 物料（多版本）
		// 单位
		MaterialVersionVO materialVO = (MaterialVersionVO) HYPubBO_Client
				.queryByPrimaryKey(MaterialVersionVO.class, pk_material + "");
		if (materialVO != null) {
			itemvo.setPk_measdoc(materialVO.getPk_measdoc());// 单位
		} else {
			throw new BusinessException("未查找到相应物料信息！");
		}
		itemvo.setPk_org("0001A110000000000HYQ");// 组织 热力
		itemvo.setPk_org_v("0001A110000000000HYP");// 组织多版本 热力

		itemvo.setStatus(VOStatus.NEW);
		itemvo.setRowno("10");// 行号
		itemvo.setService_prc_ratio(new UFDouble(1));// 服务价格系数

		billvo.setParentVO(headvo);
		ContrWorksVO[] itemvos = new ContrWorksVO[] { itemvo };// 表体VO
		billvo.setChildrenVO(itemvos);
		// System.out.println("headvo=="+headvo);
		// System.out.println("itemvo=="+itemvo);
		return billvo;
	}

	// 0001A21000000001ZBF8 03 山东济华燃气有限公司 组织版本：0001A21000000001ZBF7
	public ContractBillVO setJHBillVO(List<String> ls) throws Exception {
		ContractBillVO billvo = new ContractBillVO();// 聚合VO
		ContrHeadVO headvo = new ContrHeadVO();// 表头VO
		IBillcodeManage iBillcodeManage = (IBillcodeManage) NCLocator
				.getInstance().lookup(IBillcodeManage.class.getName());

		String pk_contr = generate(1)[0];// 合同主键
//		System.out.println("pk_contr===" + pk_contr);
		// *****表头VO*****
		headvo.setPk_contr(pk_contr);// 合同主键
		headvo.setStatus(VOStatus.NEW);
		String bill_code = new BillcodeGenerater().getBillCode("4D42",
				"0001A1100000000001QS", "0001A21000000001ZBF8", null, null);// 单据类型
		headvo.setBill_code(bill_code);// 合同编码
		headvo.setBill_name(ls.get(0));// 合同名称
		headvo.setBill_status(-1);// 合同状态 -1=自由态
		headvo.setBalaflag(1);// 结算状态 1=未结算,2=结算中,3=已结算
		headvo.setBill_type("4D42");// 单据类型
		headvo.setBillmaker("1001A110000000000HV8");// 制单人 暂定 OA审批
		headvo.setBillmaketime(new UFDate());// 制单时间
		headvo.setBusi_type("1001A21000000005UIH1");// 业务流程 拉清单发包合同流程
		headvo.setContr_version(1);// 版本号
		headvo.setCreationtime(new UFDateTime());// 创建时间
		headvo.setCreator("1001A110000000000HV8");// 创建人 暂定 OA审批
		headvo.setFlexible_flag(new UFBoolean(false)); // 预算柔性标记 false
		// headvo.setMemo(ls.get(18));// 备注
		headvo.setNcurrent_mny(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 合同金额
		headvo.setNexchangerate(new UFDouble(1));// 签约折本汇率 默认1
		headvo.setNtot_sld_mny(new UFDouble(0));// 预付款累计冲销
		headvo.setNtot_sched_mny(new UFDouble(0));// 累计进度款
		headvo.setNtot_sld_mny(new UFDouble(0));// 累计应付
		headvo.setOrig_current_mny(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 合同金额（原币）
		// 合同类型
		String htlxmc = ls.get(1);// 合同类型名称
		String pk_contracttype = getPrimarykey("pm_contracttype", "type_name",
				htlxmc, "pk_contracttype", "0001A21000000001ZBF8");
		headvo.setPk_contracttype(pk_contracttype);// 合同类型
		headvo.setPk_currtype("1002Z0100000000001K1");// 币种 默认人民币
		headvo.setPk_currtype_org("1002Z0100000000001K1");// 组织币种 默认人民币
		headvo.setPk_financeorg("0001A21000000001ZBF8");// 应付财务组织 01热力
		headvo.setPk_financeorg_v("0001A21000000001ZBF7");// 应付财务组织多版本 01热力
		headvo.setPk_group("0001A1100000000001QS");// 集团 热力集团
		headvo.setPk_org("0001A21000000001ZBF8");// 组织 热力集团
		headvo.setPk_org_v("0001A21000000001ZBF7");// 组织多版本 热力集团
		// 签约部门
		String qybmmc = ls.get(7);// 签约部门名称
		String pk_promise_dept = getPrimarykey("org_dept", "name", qybmmc,
				"pk_dept", "0001A21000000001ZBF8");
		headvo.setPk_promise_dept(pk_promise_dept);// 签约部门
		// 签约部门多版本
		String pk_pro_dept_v = getPrimarykey("org_dept", "pk_dept",
				pk_promise_dept, "pk_vid", "0001A21000000001ZBF8");
		headvo.setPk_pro_dept_v(pk_pro_dept_v);// 签约部门多版本
		// 签约人
		String qyr = ls.get(9);// 签约人
		if (qyr != null && !"".equals(qyr)) {
			String pk_promisepsn = getPrimarykey("bd_psndoc", "name", qyr,
					"pk_psndoc", "0001A21000000001ZBF8");
			headvo.setPk_promisepsn(pk_promisepsn);// 签约人
		}

		// 项目
		String xmbm = ls.get(4);// 项目编码
		String pk_project = getPrimarykey("BD_PROJECT", "PROJECT_CODE", xmbm,
				"PK_PROJECT", "0001A21000000001ZBF8");
		headvo.setPk_project(pk_project);// 项目
		// 供应商
		String gys = ls.get(3);// 供应商
		String pk_supplier = getPrimarykey("bd_supplier", "name", gys,
				"pk_supplier", "0001A21000000001ZBF8");
		headvo.setPk_supplier(pk_supplier);// 供应商
		headvo.setPk_transitype("0001A21000000000PG0R");// 交易类型 - 清单发包合同
		headvo.setPlan_validate_time(new UFDate(ls.get(10)));// 开始日期(计划生效日期)
		headvo.setPlan_teminate_time(new UFDate(ls.get(11)));// 结算日期(计划终止日期)
		headvo.setPromisetime(new UFDate(ls.get(6)));// 签约日期
		headvo.setQual_time(1);// 质保期(月) 1
		headvo.setServicebalrule(100);// 进度款控制比例(%) 默认 100
		headvo.setServicebalrule(5);// 5=合同价格+结算日期价格
		headvo.setTransi_type("4D42-01");// 交易类型编码 清单发包合同
		// 自定义项1 = 代控部门分管领导名称
		/*
		 * String dkfgldmc = ls.get(14);// 代控部门分管领导名称 String hdef1 =
		 * getPrimarykey("BD_DEFDOC", "NAME", dkfgldmc, "PK_DEFDOC");
		 */
		headvo.setHdef1("1001A210000000072TSY");// 代控部门分管领导名称（自定义档案） 默认 王思合
		headvo.setHdef21("1001A2100000000B68C3");// 是否特殊合同
		headvo.setHdef22("1001A2100000000B68C3");// 是否模板合同
		headvo.setHdef26(ls.get(14));// 原合同金额
		headvo.setHdef27(ls.get(16));// 已付款金额
		// headvo.setHdef28(ls.get(17));// 审定值
		headvo.setHdef30(ls.get(15));// 合同折扣金额
		// 自定义项31 = 代控部门
		String dkbm = ls.get(8);// 代控部门
		String hdef31 = getPrimarykey("ORG_DEPT", "NAME", dkbm, "PK_ORG",
				"0001A21000000001ZBF8");
		headvo.setHdef31(hdef31);// 代控部门
		// *****表体VO*****
		ContrWorksVO itemvo = new ContrWorksVO();// 表体VO

		itemvo.setPk_contr(pk_contr);// 合同主键

		itemvo.setPk_project(pk_project);// 项目编码
		ProjectHeadVO ProjectVO = (ProjectHeadVO) HYPubBO_Client
				.queryByPrimaryKey(ProjectHeadVO.class, pk_project); // 项目VO
		itemvo.setNprom_price(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 单价
		itemvo.setCurr_num(new UFDouble(1));// 数量 默认1
		itemvo.setCurr_mny(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 金额
		itemvo.setOrig_curr_mny(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 金额（原币）
		// CBS
		String cbsname = ls.get(2);// CBS名称
		String pk_cbsnode = getCBSPrimarykey(cbsname, pk_project,
				ProjectVO.getPk_projectclass(), "0001A21000000001ZBF8");
		itemvo.setPk_cbsnode(pk_cbsnode);// CBS名称
		itemvo.setPk_contr(headvo.getPrimaryKey());// 主表主键
		itemvo.setPk_group("0001A1100000000001QS");// 集团 热力集团
		// 物料
		String wlmc = ls.get(2);// 物料名称（CBS名称）
		String pk_material = getPrimarykey("BD_MATERIAL_V", "NAME", wlmc,
				"PK_SOURCE", "0001A21000000001ZBF8");
		itemvo.setPk_material(pk_material);// 物料
		// 物料（多版本）
		String pk_material_v = getPrimarykey("BD_MATERIAL_V", "NAME", wlmc,
				"PK_MATERIAL", "0001A21000000001ZBF8");
		itemvo.setPk_material_v(pk_material_v);// 物料（多版本）
		// 单位
		MaterialVersionVO materialVO = (MaterialVersionVO) HYPubBO_Client
				.queryByPrimaryKey(MaterialVersionVO.class, pk_material + "");
		if (materialVO != null) {
			itemvo.setPk_measdoc(materialVO.getPk_measdoc());// 单位
		} else {
			throw new BusinessException("未查找到相应物料信息！");
		}
		itemvo.setPk_org("0001A21000000001ZBF8");// 组织 热力
		itemvo.setPk_org_v("0001A21000000001ZBF7");// 组织多版本 热力

		itemvo.setStatus(VOStatus.NEW);
		itemvo.setRowno("10");// 行号
		itemvo.setService_prc_ratio(new UFDouble(1));// 服务价格系数

		billvo.setParentVO(headvo);
		ContrWorksVO[] itemvos = new ContrWorksVO[] { itemvo };// 表体VO
		billvo.setChildrenVO(itemvos);
		// System.out.println("headvo=="+headvo);
		// System.out.println("itemvo=="+itemvo);
		return billvo;
	}

	// 1 0001A21000000001ZBF5 05 济南热电集团有限公司 组织版本：0001A21000000001ZBF4
	public ContractBillVO setRDBillVO(List<String> ls) throws Exception {
		ContractBillVO billvo = new ContractBillVO();// 聚合VO
		ContrHeadVO headvo = new ContrHeadVO();// 表头VO
		IBillcodeManage iBillcodeManage = (IBillcodeManage) NCLocator
				.getInstance().lookup(IBillcodeManage.class.getName());

		String pk_contr = generate(1)[0];// 合同主键
//		System.out.println("pk_contr===" + pk_contr);
		// *****表头VO*****
		headvo.setPk_contr(pk_contr);// 合同主键
		headvo.setStatus(VOStatus.NEW);
		String bill_code = new BillcodeGenerater().getBillCode("4D42",
				"0001A1100000000001QS", "0001A21000000001ZBF5", null, null);// 单据类型
		headvo.setBill_code(bill_code);// 合同编码
		headvo.setBill_name(ls.get(0));// 合同名称
		headvo.setBill_status(-1);// 合同状态 -1=自由态
		headvo.setBalaflag(1);// 结算状态 1=未结算,2=结算中,3=已结算
		headvo.setBill_type("4D42");// 单据类型
		headvo.setBillmaker("1001A110000000000HV8");// 制单人 暂定 OA审批
		headvo.setBillmaketime(new UFDate());// 制单时间
		headvo.setBusi_type("1001A21000000005UIH1");// 业务流程 拉清单发包合同流程
		headvo.setContr_version(1);// 版本号
		headvo.setCreationtime(new UFDateTime());// 创建时间
		headvo.setCreator("1001A110000000000HV8");// 创建人 暂定 OA审批
		headvo.setFlexible_flag(new UFBoolean(false)); // 预算柔性标记 false
		// headvo.setMemo(ls.get(18));// 备注
		headvo.setNcurrent_mny(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 合同金额
		headvo.setNexchangerate(new UFDouble(1));// 签约折本汇率 默认1
		headvo.setNtot_sld_mny(new UFDouble(0));// 预付款累计冲销
		headvo.setNtot_sched_mny(new UFDouble(0));// 累计进度款
		headvo.setNtot_sld_mny(new UFDouble(0));// 累计应付
		headvo.setOrig_current_mny(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 合同金额（原币）
		// 合同类型
		String htlxmc = ls.get(1);// 合同类型名称
		String pk_contracttype = getPrimarykey("pm_contracttype", "type_name",
				htlxmc, "pk_contracttype", "0001A21000000001ZBF5");
		headvo.setPk_contracttype(pk_contracttype);// 合同类型
		headvo.setPk_currtype("1002Z0100000000001K1");// 币种 默认人民币
		headvo.setPk_currtype_org("1002Z0100000000001K1");// 组织币种 默认人民币
		headvo.setPk_financeorg("0001A21000000001ZBF5");// 应付财务组织 01热力
		headvo.setPk_financeorg_v("0001A21000000001ZBF4");// 应付财务组织多版本 01热力
		headvo.setPk_group("0001A1100000000001QS");// 集团 热力集团
		headvo.setPk_org("0001A21000000001ZBF5");// 组织 热力集团
		headvo.setPk_org_v("0001A21000000001ZBF4");// 组织多版本 热力集团
		// 签约部门
		String qybmmc = ls.get(7);// 签约部门名称
		String pk_promise_dept = getPrimarykey("org_dept", "name", qybmmc,
				"pk_dept", "0001A21000000001ZBF5");
		headvo.setPk_promise_dept(pk_promise_dept);// 签约部门
		// 签约部门多版本
		String pk_pro_dept_v = getPrimarykey("org_dept", "pk_dept",
				pk_promise_dept, "pk_vid", "0001A21000000001ZBF5");
		headvo.setPk_pro_dept_v(pk_pro_dept_v);// 签约部门多版本
		// 签约人
		String qyr = ls.get(9);// 签约人
		if (qyr != null && !"".equals(qyr) && !"null".equals(qyr)) {
			String pk_promisepsn = getPrimarykey("bd_psndoc", "name", qyr,
					"pk_psndoc", "0001A21000000001ZBF5");
			headvo.setPk_promisepsn(pk_promisepsn);// 签约人
		}

		// 项目
		String xmbm = ls.get(4);// 项目编码
		String pk_project = getPrimarykey("BD_PROJECT", "PROJECT_CODE", xmbm,
				"PK_PROJECT", "0001A21000000001ZBF5");
		headvo.setPk_project(pk_project);// 项目
		// 供应商
		String gys = ls.get(3);// 供应商
		String pk_supplier = getPrimarykey("bd_supplier", "name", gys,
				"pk_supplier", "0001A21000000001ZBF5");
		headvo.setPk_supplier(pk_supplier);// 供应商
		headvo.setPk_transitype("0001A21000000000PG0R");// 交易类型 - 清单发包合同
		headvo.setPlan_validate_time(new UFDate(ls.get(10)));// 开始日期(计划生效日期)
		headvo.setPlan_teminate_time(new UFDate(ls.get(11)));// 结算日期(计划终止日期)
		headvo.setPromisetime(new UFDate(ls.get(6)));// 签约日期
		headvo.setQual_time(1);// 质保期(月) 1
		headvo.setServicebalrule(100);// 进度款控制比例(%) 默认 100
		headvo.setServicebalrule(5);// 5=合同价格+结算日期价格
		headvo.setTransi_type("4D42-01");// 交易类型编码 清单发包合同
		// 自定义项1 = 代控部门分管领导名称
		/*
		 * String dkfgldmc = ls.get(14);// 代控部门分管领导名称 String hdef1 =
		 * getPrimarykey("BD_DEFDOC", "NAME", dkfgldmc, "PK_DEFDOC");
		 */
		headvo.setHdef1("1001A210000000072TSY");// 代控部门分管领导名称（自定义档案） 默认 王思合
		headvo.setHdef21("1001A2100000000B68C3");// 是否特殊合同
		headvo.setHdef22("1001A2100000000B68C3");// 是否模板合同
		headvo.setHdef26(ls.get(14));// 原合同金额
		headvo.setHdef27(ls.get(16));// 已付款金额
		// headvo.setHdef28(ls.get(17));// 审定值
		headvo.setHdef30(ls.get(15));// 合同折扣金额
		// 自定义项31 = 代控部门
		String dkbm = ls.get(8);// 代控部门
		String hdef31 = getPrimarykey("ORG_DEPT", "NAME", dkbm, "PK_ORG",
				"0001A21000000001ZBF5");
		headvo.setHdef31(hdef31);// 代控部门
		// *****表体VO*****
		ContrWorksVO itemvo = new ContrWorksVO();// 表体VO

		itemvo.setPk_contr(pk_contr);// 合同主键

		itemvo.setPk_project(pk_project);// 项目编码
		ProjectHeadVO ProjectVO = (ProjectHeadVO) HYPubBO_Client
				.queryByPrimaryKey(ProjectHeadVO.class, pk_project); // 项目VO
		itemvo.setNprom_price(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 单价
		itemvo.setCurr_num(new UFDouble(1));// 数量 默认1
		itemvo.setCurr_mny(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 金额
		itemvo.setOrig_curr_mny(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 金额（原币）
		// CBS
		String cbsname = ls.get(2);// CBS名称
		String pk_cbsnode = getCBSPrimarykey(cbsname, pk_project,
				ProjectVO.getPk_projectclass(), "0001A21000000001ZBF5");
		itemvo.setPk_cbsnode(pk_cbsnode);// CBS名称
		itemvo.setPk_contr(headvo.getPrimaryKey());// 主表主键
		itemvo.setPk_group("0001A1100000000001QS");// 集团 热力集团
		// 物料
		String wlmc = ls.get(2);// 物料名称（CBS名称）
		String pk_material = getPrimarykey("BD_MATERIAL_V", "NAME", wlmc,
				"PK_SOURCE", "0001A21000000001ZBF8");
		itemvo.setPk_material(pk_material);// 物料
		// 物料（多版本）
		String pk_material_v = getPrimarykey("BD_MATERIAL_V", "NAME", wlmc,
				"PK_MATERIAL", "0001A21000000001ZBF5");
		itemvo.setPk_material_v(pk_material_v);// 物料（多版本）
		// 单位
		MaterialVersionVO materialVO = (MaterialVersionVO) HYPubBO_Client
				.queryByPrimaryKey(MaterialVersionVO.class, pk_material + "");
		if (materialVO != null) {
			itemvo.setPk_measdoc(materialVO.getPk_measdoc());// 单位
		} else {
			throw new BusinessException("未查找到相应物料信息！");
		}
		itemvo.setPk_org("0001A21000000001ZBF5");// 组织 热力
		itemvo.setPk_org_v("0001A21000000001ZBF4");// 组织多版本 热力

		itemvo.setStatus(VOStatus.NEW);
		itemvo.setRowno("10");// 行号
		itemvo.setService_prc_ratio(new UFDouble(1));// 服务价格系数

		billvo.setParentVO(headvo);
		ContrWorksVO[] itemvos = new ContrWorksVO[] { itemvo };// 表体VO
		billvo.setChildrenVO(itemvos);
		// System.out.println("headvo=="+headvo);
		// System.out.println("itemvo=="+itemvo);
		return billvo;
	}

	// 1 0001A21000000001ZBFB 04 济南港华燃气有限公司 组织版本：0001A21000000001ZBFA
	public ContractBillVO setGHBillVO(List<String> ls) throws Exception {
		ContractBillVO billvo = new ContractBillVO();// 聚合VO
		ContrHeadVO headvo = new ContrHeadVO();// 表头VO
		IBillcodeManage iBillcodeManage = (IBillcodeManage) NCLocator
				.getInstance().lookup(IBillcodeManage.class.getName());

		String pk_contr = generate(1)[0];// 合同主键
//		System.out.println("pk_contr===" + pk_contr);
		// *****表头VO*****
		headvo.setPk_contr(pk_contr);// 合同主键
		headvo.setStatus(VOStatus.NEW);
		String bill_code = new BillcodeGenerater().getBillCode("4D42",
				"0001A1100000000001QS", "0001A21000000001ZBFB", null, null);// 单据类型
		headvo.setBill_code(bill_code);// 合同编码
		headvo.setBill_name(ls.get(0));// 合同名称
		headvo.setBill_status(-1);// 合同状态 -1=自由态
		headvo.setBalaflag(1);// 结算状态 1=未结算,2=结算中,3=已结算
		headvo.setBill_type("4D42");// 单据类型
		headvo.setBillmaker("1001A110000000000HV8");// 制单人 暂定 OA审批
		headvo.setBillmaketime(new UFDate());// 制单时间
		headvo.setBusi_type("1001A21000000005UIH1");// 业务流程 拉清单发包合同流程
		headvo.setContr_version(1);// 版本号
		headvo.setCreationtime(new UFDateTime());// 创建时间
		headvo.setCreator("1001A110000000000HV8");// 创建人 暂定 OA审批
		headvo.setFlexible_flag(new UFBoolean(false)); // 预算柔性标记 false
		// headvo.setMemo(ls.get(18));// 备注
		headvo.setNcurrent_mny(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 合同金额
		headvo.setNexchangerate(new UFDouble(1));// 签约折本汇率 默认1
		headvo.setNtot_sld_mny(new UFDouble(0));// 预付款累计冲销
		headvo.setNtot_sched_mny(new UFDouble(0));// 累计进度款
		headvo.setNtot_sld_mny(new UFDouble(0));// 累计应付
		headvo.setOrig_current_mny(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 合同金额（原币）
		// 合同类型
		String htlxmc = ls.get(1);// 合同类型名称
		String pk_contracttype = getPrimarykey("pm_contracttype", "type_name",
				htlxmc, "pk_contracttype", "0001A21000000001ZBFB");
		headvo.setPk_contracttype(pk_contracttype);// 合同类型
		headvo.setPk_currtype("1002Z0100000000001K1");// 币种 默认人民币
		headvo.setPk_currtype_org("1002Z0100000000001K1");// 组织币种 默认人民币
		headvo.setPk_financeorg("0001A21000000001ZBFB");// 应付财务组织 01热力
		headvo.setPk_financeorg_v("0001A21000000001ZBFA");// 应付财务组织多版本 01热力
		headvo.setPk_group("0001A1100000000001QS");// 集团 热力集团
		headvo.setPk_org("0001A21000000001ZBFB");// 组织 热力集团
		headvo.setPk_org_v("0001A21000000001ZBFA");// 组织多版本 热力集团
		// 签约部门
		String qybmmc = ls.get(7);// 签约部门名称
		String pk_promise_dept = getPrimarykey("org_dept", "name", qybmmc,
				"pk_dept", "0001A21000000001ZBFB");
		headvo.setPk_promise_dept(pk_promise_dept);// 签约部门
		// 签约部门多版本
		String pk_pro_dept_v = getPrimarykey("org_dept", "pk_dept",
				pk_promise_dept, "pk_vid", "0001A21000000001ZBFB");
		headvo.setPk_pro_dept_v(pk_pro_dept_v);// 签约部门多版本
		// 签约人
		String qyr = ls.get(9);// 签约人
		if (qyr != null && !"".equals(qyr) && !"null".equals(qyr)) {
//			System.out.println("111" + !"".equals(qyr));
			String pk_promisepsn = getPrimarykey("bd_psndoc", "name", qyr,
					"pk_psndoc", "0001A21000000001ZBFB");
			headvo.setPk_promisepsn(pk_promisepsn);// 签约人
		}

		// 项目
		String xmbm = ls.get(4);// 项目编码
		String pk_project = getPrimarykey("BD_PROJECT", "PROJECT_CODE", xmbm,
				"PK_PROJECT", "0001A21000000001ZBFB");
		headvo.setPk_project(pk_project);// 项目
		// 供应商
		String gys = ls.get(3);// 供应商
		String pk_supplier = getPrimarykey("bd_supplier", "name", gys,
				"pk_supplier", "0001A21000000001ZBFB");
		headvo.setPk_supplier(pk_supplier);// 供应商
		headvo.setPk_transitype("0001A21000000000PG0R");// 交易类型 - 清单发包合同
		headvo.setPlan_validate_time(new UFDate(ls.get(10)));// 开始日期(计划生效日期)
		headvo.setPlan_teminate_time(new UFDate(ls.get(11)));// 结算日期(计划终止日期)
		headvo.setPromisetime(new UFDate(ls.get(6)));// 签约日期
		headvo.setQual_time(1);// 质保期(月) 1
		headvo.setServicebalrule(100);// 进度款控制比例(%) 默认 100
		headvo.setServicebalrule(5);// 5=合同价格+结算日期价格
		headvo.setTransi_type("4D42-01");// 交易类型编码 清单发包合同
		// 自定义项1 = 代控部门分管领导名称
		/*
		 * String dkfgldmc = ls.get(14);// 代控部门分管领导名称 String hdef1 =
		 * getPrimarykey("BD_DEFDOC", "NAME", dkfgldmc, "PK_DEFDOC");
		 */
		headvo.setHdef1("1001A210000000072TSY");// 代控部门分管领导名称（自定义档案） 默认 王思合
		headvo.setHdef21("1001A2100000000B68C3");// 是否特殊合同
		headvo.setHdef22("1001A2100000000B68C3");// 是否模板合同
		headvo.setHdef26(ls.get(14));// 原合同金额
		headvo.setHdef27(ls.get(15));// 已付款金额
		// headvo.setHdef28(ls.get(17));// 审定值
		headvo.setHdef30("0");// 合同折扣金额
		// 自定义项31 = 代控部门
		String dkbm = ls.get(8);// 代控部门
		String hdef31 = getPrimarykey("ORG_DEPT", "NAME", dkbm, "PK_ORG",
				"0001A21000000001ZBFB");
		headvo.setHdef31(hdef31);// 代控部门
		// *****表体VO*****
		ContrWorksVO itemvo = new ContrWorksVO();// 表体VO

		itemvo.setPk_contr(pk_contr);// 合同主键

		itemvo.setPk_project(pk_project);// 项目编码
		ProjectHeadVO ProjectVO = (ProjectHeadVO) HYPubBO_Client
				.queryByPrimaryKey(ProjectHeadVO.class, pk_project); // 项目VO
		itemvo.setNprom_price(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 单价
		itemvo.setCurr_num(new UFDouble(1));// 数量 默认1
		itemvo.setCurr_mny(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 金额
		itemvo.setOrig_curr_mny(new UFDouble(ls.get(12)).setScale(2, UFDouble.ROUND_HALF_UP));// 金额（原币）
		// CBS
		String cbsname = ls.get(2);// CBS名称
		String pk_cbsnode = getCBSPrimarykey(cbsname, pk_project,
				ProjectVO.getPk_projectclass(), "0001A21000000001ZBFB");
		itemvo.setPk_cbsnode(pk_cbsnode);// CBS名称
		itemvo.setPk_contr(headvo.getPrimaryKey());// 主表主键
		itemvo.setPk_group("0001A1100000000001QS");// 集团 热力集团
		// 物料
		String wlmc = ls.get(2);// 物料名称（CBS名称）
		String pk_material = getPrimarykey("BD_MATERIAL_V", "NAME", wlmc,
				"PK_SOURCE", "0001A21000000001ZBFB");
		itemvo.setPk_material(pk_material);// 物料
		// 物料（多版本）
		String pk_material_v = getPrimarykey("BD_MATERIAL_V", "NAME", wlmc,
				"PK_MATERIAL", "0001A21000000001ZBFB");
		itemvo.setPk_material_v(pk_material_v);// 物料（多版本）
		// 单位
		MaterialVersionVO materialVO = (MaterialVersionVO) HYPubBO_Client
				.queryByPrimaryKey(MaterialVersionVO.class, pk_material + "");
		if (materialVO != null) {
			itemvo.setPk_measdoc(materialVO.getPk_measdoc());// 单位
		} else {
			throw new BusinessException("未查找到相应物料信息！");
		}
		itemvo.setPk_org("0001A21000000001ZBFB");// 组织 港华
		itemvo.setPk_org_v("0001A21000000001ZBFA");// 组织多版本 热力

		itemvo.setStatus(VOStatus.NEW);
		itemvo.setRowno("10");// 行号
		itemvo.setService_prc_ratio(new UFDouble(1));// 服务价格系数

		billvo.setParentVO(headvo);
		ContrWorksVO[] itemvos = new ContrWorksVO[] { itemvo };// 表体VO
		billvo.setChildrenVO(itemvos);
		// System.out.println("headvo=="+headvo);
		// System.out.println("itemvo=="+itemvo);
		return billvo;
	}

	// 获取对应主键
	public String getPrimarykey(String tbname, String valfield, String value,
			String pkfield, String pk_org) throws BusinessException {
		String pk = "";// 对应表的主键
		String sql = "SELECT DISTINCT " + pkfield + " FROM " + tbname
				+ " WHERE " + valfield + " = '" + value
				+ "' AND DR = 0 AND PK_ORG = '" + pk_org + "'";
		if ("pm_contracttype".equals(tbname) || "bd_supplier".equals(tbname)
				|| "BD_PROJECT".equals(tbname)
				|| "BD_MATERIAL_V".equals(tbname)) {
			sql = "SELECT DISTINCT " + pkfield + " FROM " + tbname + " WHERE "
					+ valfield + " = '" + value
					+ "' AND DR = 0 AND PK_ORG IN ( '0001A1100000000001QS','"
					+ pk_org + "')";
		} else if ("org_dept".equals(tbname)) {
			sql = "SELECT DISTINCT " + pkfield + " FROM " + tbname + " WHERE "
					+ valfield + " = '" + value + "' AND DR = 0 AND PK_ORG = '"
					+ pk_org + "' AND PK_FATHERORG = '~'";
		}
		try {
			List<Object[]> resultList = getDao.query(sql);
			if("bd_supplier".equals(tbname)){
				System.out.println("根据名称查PK：" + sql);
			}
			if (resultList != null && resultList.size() == 1) {
				pk = resultList.get(0)[0] + "";
			} else {
				throw new BusinessException("根据名称未查询到对应主键或查询到多条记录！");
			}
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pk;
	}

	// 获取CBS主键
	public String getCBSPrimarykey(String cbsName, String pk_project,
			String pk_projectClass, String pk_org) throws BusinessException {
		String pk = "";// CBS主键
		String sql = "SELECT DISTINCT PK_CBSNODE FROM BD_CBSNODE WHERE NAME = '"
				+ cbsName
				+ "' AND DR = 0 AND (PK_ORG = '"
				+ pk_org
				+ "' OR PK_ORG = '~') AND (PK_PROJECT = '"
				+ pk_project
				+ "' OR PK_PROJECTTYPE = '" + pk_projectClass + "')";
		try {
			List<Object[]> resultList = getDao.query(sql);
			// System.out.println("根据名称查PK：" + sql);
			if (resultList != null && resultList.size() == 1) {
				pk = resultList.get(0)[0] + "";
			} else {
				throw new BusinessException("根据CBS名称["+sql+"]未查询到对应主键或查询到多条记录！");
			}
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pk;
	}

	// 获取Excel列值
	public static Object getCellFormatValue(Cell cell) {
		Object cellValue = null;
		if (cell != null) {
			// 判断cell类型
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC: {
				cellValue = String.valueOf(cell.getNumericCellValue());
				break;
			}
			case Cell.CELL_TYPE_FORMULA: {
				// 判断cell是否为日期格式
				if (DateUtil.isCellDateFormatted(cell)) {
					// 转换为日期格式YYYY-mm-dd
					cellValue = cell.getDateCellValue();
				} else {
					// 数字
					cellValue = String.valueOf(cell.getNumericCellValue());
				}
				break;
			}
			case Cell.CELL_TYPE_STRING: {
				cellValue = cell.getRichStringCellValue().getString();
				break;
			}
			default:
				cellValue = "";
			}
		} else {
			cellValue = "";
		}
		return cellValue;
	}

	private String[] generate(int amount) {
		long OID_BASE_INITIAL_VAL = 19000000000000L;
		IdGenerator ss = new SequenceGenerator();
		String[] newOids = new String[amount];
		if (MockDataSource.isMockDataBase()) {
			for (int i = 0; i < amount; i++) {
				OID_BASE_INITIAL_VAL += 1L;
				newOids[i] = OidBaseAlgorithm.getInstance(
						String.valueOf(OID_BASE_INITIAL_VAL)).nextOidBase();
			}
			return newOids;
		}
		String groupNumber = InvocationInfoProxy.getInstance().getGroupNumber();
		if ((groupNumber == null) || (groupNumber.isEmpty())) {
			groupNumber = "001";
		}
		String ds = InvocationInfoProxy.getInstance().getUserDataSource();
		for (int i = 0; i < amount; i++) {
			newOids[i] = OidGenerator.getInstance().nextOid(ds, groupNumber);
		}
		return newOids;
	}
}
