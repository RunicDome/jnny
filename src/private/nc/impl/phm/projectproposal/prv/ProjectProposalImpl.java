package nc.impl.phm.projectproposal.prv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pmpub.rule.InitCurrTypeBeforeRule;
import nc.bs.ppm.schedule.utils.TaskPushUtils;
import nc.bs.ppm.schedule.utils.TaskPushUtilsForPub;
import nc.bs.trade.business.HYPubBO;
import nc.impl.phm.rule.WriteBackToProjectAfterRule;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.ApproveAction;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.phm.projectproposal.prv.IProjectProposal;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.oa.web.NCService;
import nc.oa.web.NCServiceSoap;
import nc.ui.pcm.feebalance.action.TaskProgressDetailVO;
import nc.ui.pcm.feebalance.action.TaskProgressVO;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.beans.MessageDialog;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.cust.CustSupplierVO;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.ct.purdaily.entity.CtPuVO;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pcm.contract.ContrWorksVO;
import nc.vo.phm.projectproposal.ProjectProposalBillVO;
import nc.vo.phm.projectproposal.ProjectProposalHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.UpdateSupplier;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 勘察申请单  // 设计申请单 //刨掘申请单 //控制价委托申请单//开工单
@SuppressWarnings({ "restriction", "unused", "unchecked", "rawtypes" })
public class ProjectProposalImpl extends BillBaseImpl<ProjectProposalBillVO>
		implements IProjectProposal {

	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
	private static String RL_004 = "4D15-Cxx-004";
	private static String RL_WORKFLOW_ID_004 = "46";
	private static String RL_WORKFLOW_ID_004_TABLE_NAME = "勘察申请单";

	private static String RL_008 = "4D15-Cxx-008";
	private static String RL_WORKFLOW_ID_008 = "23";
	public static String WORKFLOWID_JNNY = "304";
	private static String RL_WORKFLOW_ID_008_TABLE_NAME = "开工单";

	private static String RL_005 = "4D15-Cxx-005";
	private static String RL_WORKFLOW_ID_005 = "50";
	private static String RL_WORKFLOW_ID_005_TABLE_NAME = "设计申请单";

	private static String RL_006 = "4D15-Cxx-006";
	private static String RL_WORKFLOW_ID_006 = "48";
	private static String RL_WORKFLOW_ID_006_TABLE_NAME = "刨掘申请单";

//	private static String RL_007 = "4D15-Cxx-007";
//	private static String RL_WORKFLOW_ID_007 = "47";
//	private static String RL_WORKFLOW_ID_007_TABLE_NAME = "控制价委托申请单";
	
	TaskPushUtilsForPub insertAndUptTaskBIll = new TaskPushUtilsForPub();

	public ProjectProposalImpl() {
	}

	public ProjectProposalBillVO[] insertProjectProposal(
			ProjectProposalBillVO[] billVOs) throws BusinessException {
		ProjectProposalHeadVO hvos = before(billVOs[0]);// 保存前
		if (hvos != null) {
			billVOs[0].setParentVO(hvos);
		}
		return (ProjectProposalBillVO[]) insert(billVOs);
	}

	// 保存前校验
	public ProjectProposalHeadVO before(ProjectProposalBillVO billVO)
			throws BusinessException {
		// TODO Auto-generated method stub
		ProjectProposalHeadVO hvo = (ProjectProposalHeadVO) billVO
				.getParentVO();
		String pk_project = hvo.getPk_project();
		String pkOrg = hvo.getPk_org();
		String transi_type = hvo.getTransi_type();// 交易类型
		// code判断当前组织是否需要推进度
		String code = (String) getHyPubBO().findColValue("bd_defdoc", "code",
				"nvl(dr,0) = 0 and name = '" + pkOrg + "'");
		// name判断当前单据是否需要推进度 name值为任务名称
		String name = getWbsName(transi_type);
		if (code != null && name != null && !"".equals(name)) {
			String pk_wbs = getWbsWork(pkOrg, pk_project, name);
			hvo.setDef10(pk_wbs);
		} else {
			hvo = null;
		}
		return hvo;
	}

	// 根据交易类型查询对应任务名称-----参数：transType==交易类型
	public String getWbsName(String transType) throws DAOException {
		String wbs_name = "";
		String wbsNameSql = "SELECT SHORTNAME FROM BD_DEFDOC WHERE PK_DEFDOCLIST = '1001A11000000023EZXV"
				+ "' AND DR = 0 AND CODE = '" + transType + "'";
		List<Object[]> wbsNameLs = getDao.query(wbsNameSql);
		if (wbsNameLs != null && wbsNameLs.size() > 0
				&& wbsNameLs.get(0)[0] != null) {
			wbs_name = wbsNameLs.get(0)[0] + "";
		}
		System.out.println("wbsNameSq==" + wbsNameSql);
		return wbs_name;
	}

	public ProjectProposalBillVO[] updateProjectProposal(
			ProjectProposalBillVO[] billVOs,
			ProjectProposalBillVO[] originBillVOs) throws BusinessException {
		ProjectProposalHeadVO hvos = before(billVOs[0]);// 保存前
		if (hvos != null) {
			billVOs[0].setParentVO(hvos);
			billVOs[0].getParentVO().setStatus(VOStatus.UPDATED);
		}
		return (ProjectProposalBillVO[]) update(billVOs, originBillVOs);
	}

	public ProjectProposalBillVO[] deleteProjectProposal(
			ProjectProposalBillVO[] billVOs) throws BusinessException {
		beforeCheck(billVOs);
		return (ProjectProposalBillVO[]) delete(billVOs);
	}

	public void beforeCheck(ProjectProposalBillVO[] billVOs)
			throws BusinessException {
		// TODO Auto-generated method stub
		for (ProjectProposalBillVO vo : billVOs) {
			String pk_bill = vo.getParentVO().getPrimaryKey();
			String strWhere = " nvl(dr,0) = 0  and pk_bill ='" + pk_bill + "'";
			WorkFlowBill[] workFlowBills = (WorkFlowBill[]) new HYPubBO()
					.queryByCondition(WorkFlowBill.class, strWhere);
			if (null != workFlowBills && workFlowBills.length == 1
					&& null == workFlowBills[0].getDef1()) {
				int flag = checkifdo();
				if (flag != 1) {
					throw new BusinessException("提交过OA的单据不允许删除！");
				}
			}
		}
	}

	public Object approveProjectProposal(ProjectProposalBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		for(ProjectProposalBillVO vo: billVOs){
			ProjectProposalHeadVO hvo = vo.getParentVO();
			String transi_type = hvo.getTransi_type();// 交易类型
			
			if(StringUtils.equals("4D15-Cxx-GYS", transi_type)){
				String pk_supplier = hvo.getDef1();// 供应商主键
				String oldname = hvo.getDef2();// 原供应商名称
				String newname = hvo.getDef3();// 变更后名称
				if(StringUtils.isEmpty(pk_supplier)){
					throw new BusinessException("未选择供应商档案");
				}
				if(StringUtils.isEmpty(oldname)){
					throw new BusinessException("原供应商名称为空，请检查！");
				}
				if(StringUtils.isEmpty(newname)){
					throw new BusinessException("变更后名称，请检查！");
				}
				Map<String,String> gysmap = new HashMap<>();
				gysmap.put("pk_supplier", pk_supplier);// 供应商主键
				gysmap.put("oldname", oldname);// 原供应商名称
				gysmap.put("newname", newname);// 变更后名称
				String result = UpdateSupplier.update(gysmap);
				if(StringUtils.isNotEmpty(result)){
					throw new BusinessException(result);
				}
			}
		}
		
		beforeAppr(billVOs);
		return approve(billVOs, pfParamVO);
	}

	// 推进度
	public void beforeAppr(ProjectProposalBillVO[] billVOs)
			throws BusinessException {
		// TODO Auto-generated method stub
		for (ProjectProposalBillVO vo : billVOs) {
			ProjectProposalHeadVO hvo = vo.getParentVO();// 表头VO
			// 自定义项10 为 PK_WBS
			if (hvo.getDef10() != null && !"null".equals(hvo.getDef10())) {
				pushTaskProcessByPro(hvo);
			}
		}
	}
	// 项目建议书->任务进度填报
	private void pushTaskProcessByPro(ProjectProposalHeadVO headVO) throws BusinessException {
		// TODO Auto-generated method stub
		TaskProgressVO taskProgressVO = new TaskProgressVO();
		taskProgressVO.setBillmaker(headVO.getBillmaker());
		taskProgressVO.setBillmaketime(headVO.getBillmaketime()); // 制单日期
		taskProgressVO.setCreator(headVO.getCreator());
		taskProgressVO.setCreationtime(headVO.getCreationtime());
		taskProgressVO.setMemo(headVO.getMemo()); // 备注
		String bm = (String) getHyPubBO().findColValue(
				"bd_project",
				"pk_duty_dept",
				"nvl(dr,0) = 0 and pk_project = '"
						+ headVO.getPk_project() + "'"); //项目责任部门
		String bmbb = (String) getHyPubBO().findColValue(
				"bd_project",
				"pk_duty_dept_v",
				"nvl(dr,0) = 0 and pk_project = '"
						+ headVO.getPk_project() + "'"); //项目责任部门版本
		taskProgressVO.setPk_filldept(bm); // 经办部门
		taskProgressVO.setPk_filldept_v(bmbb); // 经办部门多版本
		taskProgressVO.setPk_fillmaker(headVO.getBillmaker()); // 填报人
		taskProgressVO.setPk_group(headVO.getPk_group()); // 集团
		taskProgressVO.setPk_org(headVO.getPk_org()); // 组织
		taskProgressVO.setPk_org_v(headVO.getPk_org_v()); // 组织
		taskProgressVO.setBillPk(headVO.getPrimaryKey());// 单据主键
		taskProgressVO.setPk_project(headVO.getPk_project()); // 项目取表体数据

		// 项目明细
		TaskProgressDetailVO taskProgressDetailVO = new TaskProgressDetailVO();
		taskProgressDetailVO.setActu_start_date(headVO.getReq_start_date()); // 实际开始时间
		taskProgressDetailVO.setActu_finish_date(headVO.getReq_finish_date()); // 实际结束时间
		taskProgressDetailVO.setEvolve_state(headVO.getMemo()); // 进展说明
		taskProgressDetailVO.setMemo(headVO.getMemo()); // 备注
		taskProgressDetailVO.setPk_project(headVO.getPk_project()); // 项目
		taskProgressDetailVO.setPk_wbs(headVO.getDef10()); // WBS任务 	
		taskProgressDetailVO.setTtaskpercent(new UFDouble(100));// 进度
		taskProgressVO.setTaskProgressDetailVO(taskProgressDetailVO);
		
		// 执行插入
		String getwbsql = "SELECT NVL(MAX(MX.TASKPERCENT),0) AS SL FROM PM_TASKSCHEDULE JD LEFT JOIN "
				+ "PM_TASKSCHEDULE_B MX ON JD.PK_TASKSCHEDULE = MX.PK_TASKSCHEDULE WHERE JD.DR = 0 AND "
				+ "MX.DR = 0 AND MX.PK_WBS = '" + headVO.getDef10() + "'";
		List<Object[]> ls = (List<Object[]>) new BaseDAO().executeQuery(
				getwbsql, new ArrayListProcessor());
		ArrayList<UFDouble> a = (ArrayList<UFDouble>) new BaseDAO()
				.executeQuery(getwbsql, new ColumnListProcessor());
		UFDouble taskpercevt = new UFDouble(String.valueOf(a.get(0)));
		if (taskpercevt.compareTo(new UFDouble(100)) < 0) {
			try {
				insertAndUptTaskBIll.insertTaskBillVO(taskProgressVO, null);
			} catch (BusinessException e) {
				// TODO: handle exception
			}
		}
	}

	// 检查是否可以 审批 取消审批 删除
	public int checkifdo() throws BusinessException {
		int flag = 0;
		String userID = InvocationInfoProxy.getInstance().getUserId();// 获取当前系统登录用户
		Logger.error("userID==" + userID);
		String sql = "select c.cuserid, a.pk_role, a.role_name,c.user_name from sm_role a left join sm_user_role b "
				+ "on a.pk_role=b.pk_role left join sm_user c on c.cuserid=b.cuserid where a.role_code = 'DTUSER'";
		List<Object[]> ls = getDao.query(sql);
		if (ls != null && ls.size() > 0) {
			for (int i = 0; i < ls.size(); i++) {
				if (userID.equals(ls.get(i)[0])) {
					flag = 1;
				}
			}
		}
		return flag;
	}

	public ProjectProposalBillVO[] unapproveProjectProposal(
			ProjectProposalBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		beforeUNAppr(billVOs);
		return (ProjectProposalBillVO[]) unApprove(billVOs, pfParamVO);
	}

	public void beforeUNAppr(ProjectProposalBillVO[] billVOs)
			throws BusinessException {
		// TODO Auto-generated method stub
		for (ProjectProposalBillVO vo : billVOs) {
			ProjectProposalHeadVO hvo = vo.getParentVO();// 表头VO
			// 自定义项10 为 PK_WBS
			if (hvo.getDef10() != null && !"null".equals(hvo.getDef10())) {
				insertAndUptTaskBIll.deleteTaskBillVO(hvo.getPrimaryKey());
			}
		}
	}

	public ProjectProposalBillVO[] commitProjectProposal(
			ProjectProposalBillVO[] billVOs) throws BusinessException {
		ProjectProposalBillVO[] newAggVO = (ProjectProposalBillVO[]) super
				.commit(billVOs);
		sendoa(newAggVO);
		return newAggVO;
	}

	// TODO 单据同步OA功能 start create by zwh
	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}

	private void sendoa(ProjectProposalBillVO[] billVOs)
			throws BusinessException {
		for (ProjectProposalBillVO temp : billVOs) {
			ProjectProposalHeadVO headvo = temp.getParentVO();
			OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
					headvo.getPk_org());
			String transi_type = headvo.getTransi_type();// 交易类型
			// 中台
			if ("4".equals(orgVO.getDef2())) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
						headvo.getPk_org(), transi_type);
				if (oaVo != null && oaVo.getIsdr() == 0) {
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					// 2222
					// 获取主表数据
					JSONArray headData = getNewMainMap(headvo);
					// 获取子表数据
					JSONArray bodyData = null;
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(temp.getPrimaryKey());
					workFlowVO.setPkGroup(headvo.getPk_group());
					workFlowVO.setPkOrg(headvo.getPk_org());
					workFlowVO.setBillMaker(headvo.getBillmaker());
					workFlowVO.setCreator(headvo.getCreator());
					workFlowVO.setBillCode("4D15");// 单据类型
					// 接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName(oaVo.getBilltypename());// 单据名称
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					bill.setDef3("ZT");
					bill.setDef4(headvo.getBill_code());
					// 制单人身份证号
					UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
							UserVO.class, headvo.getBillmaker());
					String idCard = (String) getHyPubBO().findColValue(
							"bd_psndoc",
							"id",
							"nvl(dr,0) = 0 and pk_psndoc='"
									+ userVO.getPk_psndoc() + "'");
					bill.setDef5(idCard);
					// 调用OA工具类同步数据至OA
					OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
				}

			} else {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
						headvo.getPk_org(), transi_type);
				if (oaVo != null && oaVo.getIsdr() == 0) {
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					// 2222
					// 获取主表数据
					JSONArray headData = getNewMainMap(headvo);
					// 获取子表数据
					JSONArray bodyData = null;
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(temp.getPrimaryKey());
					workFlowVO.setPkGroup(headvo.getPk_group());
					workFlowVO.setPkOrg(headvo.getPk_org());
					workFlowVO.setBillMaker(headvo.getBillmaker());
					workFlowVO.setCreator(headvo.getCreator());
					workFlowVO.setBillCode("4D15");// 单据类型
					// 接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName(oaVo.getBilltypename());// 单据名称
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					bill.setDef3("ZT");
					bill.setDef4(headvo.getBill_code());
					// 制单人身份证号
					UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
							UserVO.class, headvo.getBillmaker());
					String idCard = (String) getHyPubBO().findColValue(
							"bd_psndoc",
							"id",
							"nvl(dr,0) = 0 and pk_psndoc='"
									+ userVO.getPk_psndoc() + "'");
					bill.setDef5(idCard);
					// 调用OA工具类同步数据至OA
					OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
				} else if ("1".equals(orgVO.getDef2())){
					// ============================
					if ("4D15".equals(temp.getParentVO().getBill_type())
							&& null != orgVO.getDef1()) {
						if (RL_004.equals(transi_type)) {
							// 勘察申请单
							JSONArray headData1 = getMainMap1(temp
									.getParentVO());

							Integer requestid = OaWorkFlowUtil.sendOaData(
									headData1, null, getWorkFlowBill(temp));
							ProjectProposalHeadVO hvo = (ProjectProposalHeadVO) temp
									.getParent();
							hvo.setStatus(VOStatus.UPDATED);

							getHyPubBO().update(hvo);
						}
						if (RL_005.equals(transi_type)) {
							// 设计申请单
							JSONArray headData2 = getMainMap2(temp
									.getParentVO());

							Integer requestid = OaWorkFlowUtil.sendOaData(
									headData2, null, getWorkFlowBill(temp));
							ProjectProposalHeadVO hvo = (ProjectProposalHeadVO) temp
									.getParent();
							hvo.setStatus(VOStatus.UPDATED);

							getHyPubBO().update(hvo);
						}
						if (RL_006.equals(transi_type)) {
							// 刨掘申请单
							JSONArray headData3 = getMainMap3(temp
									.getParentVO());

							Integer requestid = OaWorkFlowUtil.sendOaData(
									headData3, null, getWorkFlowBill(temp));
							ProjectProposalHeadVO hvo = (ProjectProposalHeadVO) temp
									.getParent();
							hvo.setStatus(VOStatus.UPDATED);

							getHyPubBO().update(hvo);
						}
//						if (RL_007.equals(transi_type)) {
//							// 控制价委托申请单
//							JSONArray headData4 = getMainMap4(temp
//									.getParentVO());
//
//							Integer requestid = OaWorkFlowUtil.sendOaData(
//									headData4, null, getWorkFlowBill(temp));
//							ProjectProposalHeadVO hvo = (ProjectProposalHeadVO) temp
//									.getParent();
//							hvo.setStatus(VOStatus.UPDATED);
//
//							getHyPubBO().update(hvo);
//						}
						if (RL_008.equals(transi_type)) {
							// 开工单
							JSONArray headData5 = getMainMap5(temp
									.getParentVO());

							OaWorkFlowUtil.sendOaData(headData5, null,
									getWorkFlowBill(temp));
						}
					}
				}
			}
		}
	}

	/*
	 * private JSONArray getNewMainMap(ProjectProposalHeadVO parentVO) throws
	 * BusinessException { JsonConfig jsonConfig = new JsonConfig();
	 * jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT); //
	 * 构造数据 List<Map<String, Object>> list =
	 * OaWorkFlowUtil.transBean2Map(parentVO); // ---------其他字段begin // 组织
	 * String stockName = (String) getHyPubBO().findColValue( "org_itemorg",
	 * "name", "nvl(dr,0) = 0 and pk_itemorg = '" + parentVO.getPk_org() + "'");
	 * list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName)); UserVO userVO =
	 * (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
	 * parentVO.getBillmaker()); // 招标类型 if(parentVO.getDef2() != null){ String
	 * zblx = (String) getHyPubBO().findColValue( "bd_defdoc", "name",
	 * "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef2() + "'");
	 * list.add(OaWorkFlowUtil.listAddObj("zblx", zblx));
	 * list.add(OaWorkFlowUtil.listAddObj("zblb", zblx)); } // 部门
	 * if(parentVO.getDef1() != null){ String sqbmName = (String)
	 * getHyPubBO().findColValue("org_dept", "name",
	 * "nvl(dr,0) = 0 and pk_dept = '" + parentVO.getDef1() + "'");
	 * list.add(OaWorkFlowUtil.listAddObj("sqbm", sqbmName)); }
	 * 
	 * // 制单人用户编码 list.add(OaWorkFlowUtil.listAddObj("usercode",
	 * userVO.getUser_code())); // 制单人名称
	 * list.add(OaWorkFlowUtil.listAddObj("username", userVO.getUser_name()));
	 * // 制单人身份证号 String idCard = (String)
	 * getHyPubBO().findColValue("bd_psndoc", "id",
	 * "nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'"); if (null
	 * == idCard) { throw new BusinessException("制单人身份证号码未维护"); }
	 * list.add(OaWorkFlowUtil.listAddObj("userid", idCard)); if (null !=
	 * custVO) { // 供应商编码 list.add(OaWorkFlowUtil.listAddObj("pk_supplier_code",
	 * custVO.getCode())); // 供应商名称
	 * list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name",
	 * custVO.getName())); } // ---------其他字段end JSONArray arr =
	 * JSONArray.fromObject(list, jsonConfig); return arr; }
	 */

	private JSONArray getNewMainMap(ProjectProposalHeadVO parentVO)
			throws BusinessException {
		// 构造数据
		List<Map<String, Object>> list = new ArrayList();
		// 构造数据
		if (!"1".equals(getDef2(parentVO.getPk_org()))) {
			list = OaWorkFlowUtil.transBean2Map(parentVO);
		}
		// 组织
		String stockName = (String) getHyPubBO()
				.findColValue(
						"org_itemorg",
						"name",
						"nvl(dr,0) = 0 and pk_itemorg = '"
								+ parentVO.getPk_org() + "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());

		// 制单人用户编码
		list.add(OaWorkFlowUtil.listAddObj("usercode", userVO.getUser_code()));
		// 制单人名称
		list.add(OaWorkFlowUtil.listAddObj("username", userVO.getUser_name()));
		// 制单人身份证号
		String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == idCard) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		list.add(OaWorkFlowUtil.listAddObj("userid", idCard));
		OrgVO yhorgvo = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				userVO.getPk_org());
		list.add(OaWorkFlowUtil.listAddObj("yhsszzzj", userVO.getPk_org()));
		list.add(OaWorkFlowUtil.listAddObj("yhsszzbm", yhorgvo.getCode()));
		list.add(OaWorkFlowUtil.listAddObj("yhsszzmc", yhorgvo.getName()));
		// 能投原字段
		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO) {
			list.add(OaWorkFlowUtil.listAddObj("szgs", orgVO.getCode()));
			// 所属组织名称
			list.add(OaWorkFlowUtil.listAddObj("sszzmc", orgVO.getName()));
		}
		String djmcNC = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				" nvl(dr,0) = 0 and pk_billtypecode = '"
						+ parentVO.getBill_type() + "'");
		list.add(OaWorkFlowUtil.listAddObj("djmc", djmcNC));

		getList(parentVO, list);

		if (parentVO.getReq_start_date() != null) {
			list.add(OaWorkFlowUtil.listAddObj("ksrq", parentVO
					.getReq_start_date().getYear()
					+ "-"
					+ parentVO.getReq_start_date().getStrMonth()
					+ "-"
					+ parentVO.getReq_start_date().getStrDay()));
		}
		String de2 = "0";
		if (null != parentVO.getDef2()) {
			String strWhere = "pk_defdoc = '" + parentVO.getDef2()
					+ "' and nvl(dr,0) = 0 ";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				de2 = defdoc[0].getName();
			}
		}
		list.add(OaWorkFlowUtil.listAddObj("sfsdlsg", de2));
		if (null != parentVO.getDef3()) {
			list.add(OaWorkFlowUtil.listAddObj("sjlx", parentVO.getDef3()));
		}
		if (null != parentVO.getDef3()) {
			list.add(OaWorkFlowUtil.listAddObj("kclx", parentVO.getDef3()));
		}
		if (null != parentVO.getMemo()) {
			list.add(OaWorkFlowUtil.listAddObj("sjxq", parentVO.getMemo()));
		}
		Map sfzh = new HashMap();
		String id = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == id) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		sfzh.put("fieldName", "sfzh");
		sfzh.put("fieldValue", id);
		list.add(sfzh);

		list.add(OaWorkFlowUtil.listAddObj("bz", parentVO.getMemo()));
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	private WorkFlowBill getWorkFlowBill(ProjectProposalBillVO temp)
			throws BusinessException {
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				temp.getParentVO().getBillmaker());
		String where = " nvl(dr,0) = 0 and pk_bill = '" + temp.getPrimaryKey()
				+ "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
				.queryByCondition(WorkFlowBill.class, where);
		WorkFlowBill workFlowBill = new WorkFlowBill();
		if (null != workFlowBills && workFlowBills.length > 1) {
			throw new BusinessException("查询到多条流程记录");
		}
		UserVO user = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				temp.getParentVO().getBillmaker());
		workFlowBill.setDef5(user.getUser_code());
		if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
			PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(
					PsndocVO.class, user.getPk_psndoc());
			workFlowBill.setDef5(psndoc.getId());
		}
		if (null != workFlowBills && workFlowBills.length == 1) {
			workFlowBill = workFlowBills[0];
			if ("4D15-Cxx-004".equals(temp.getParentVO().getTransi_type())) {
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_004_TABLE_NAME);
			}
			if ("4D15-Cxx-005".equals(temp.getParentVO().getTransi_type())) {
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_005_TABLE_NAME);
			}
			if ("4D15-Cxx-006".equals(temp.getParentVO().getTransi_type())) {
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_006_TABLE_NAME);
			}
//			if ("4D15-Cxx-007".equals(temp.getParentVO().getTransi_type())) {
//				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_007_TABLE_NAME);
//			}
			if ("4D15-Cxx-008".equals(temp.getParentVO().getTransi_type())) {
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_008_TABLE_NAME);
			}
		} else {
			workFlowBill.setPk_group(temp.getParentVO().getPk_group());
			workFlowBill.setPk_org(temp.getParentVO().getPk_org());
			workFlowBill.setPk_bill(temp.getPrimaryKey());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(temp.getParentVO().getCreator());
			workFlowBill.setBill_code(temp.getParentVO().getBill_type());
			if ("4D15-Cxx-004".equals(temp.getParentVO().getTransi_type())) {
				workFlowBill.setWorkflowId(RL_WORKFLOW_ID_004);
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_004_TABLE_NAME);
			}
			if ("4D15-Cxx-005".equals(temp.getParentVO().getTransi_type())) {
				workFlowBill.setWorkflowId(RL_WORKFLOW_ID_005);
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_005_TABLE_NAME);
			}
			if ("4D15-Cxx-006".equals(temp.getParentVO().getTransi_type())) {
				workFlowBill.setWorkflowId(RL_WORKFLOW_ID_006);
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_006_TABLE_NAME);
			}
//			if ("4D15-Cxx-007".equals(temp.getParentVO().getTransi_type())) {
//				workFlowBill.setWorkflowId(RL_WORKFLOW_ID_007);
//				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_007_TABLE_NAME);
//			}
			if ("4D15-Cxx-008".equals(temp.getParentVO().getTransi_type())) {
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_008_TABLE_NAME);
				workFlowBill.setWorkflowId(RL_WORKFLOW_ID_008);
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					workFlowBill.setWorkflowId(WORKFLOWID_JNNY);
				}
			}
		}
		return workFlowBill;
	}

	public String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}

	// 勘察申请单主表数据
	private JSONArray getMainMap1(ProjectProposalHeadVO parentVO)
			throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO) {

			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getCode());
			list.add(szgs);
			// 所属组织名称
			Map sszzmc = OaWorkFlowUtil.listAdd("sszzmc", orgVO.getName());
			list.add(sszzmc);
		}

		getList(parentVO, list);

		String djmcNC = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				" nvl(dr,0) = 0 and pk_billtypecode = '"
						+ parentVO.getBill_type() + "'");
		Map djmc = OaWorkFlowUtil.listAdd("djmc", djmcNC);
		list.add(djmc);

		String str5 = "0";
		if (null != parentVO.getReq_start_date()) {
			str5 = parentVO.getReq_start_date().getYear() + "-"
					+ parentVO.getReq_start_date().getStrMonth() + "-"
					+ parentVO.getReq_start_date().getStrDay();
		}
		Map ksrq = OaWorkFlowUtil.listAdd("ksrq", str5);
		list.add(ksrq);

		String str1 = "0";
		if (null != parentVO.getReq_finish_date()) {
			str1 = parentVO.getReq_finish_date().getYear() + "-"
					+ parentVO.getReq_finish_date().getStrMonth() + "-"
					+ parentVO.getReq_finish_date().getStrDay();
		}
		Map wcrq = OaWorkFlowUtil.listAdd("wcrq", str1);
		list.add(wcrq);

		String de2 = "0";
		if (null != parentVO.getDef2()) {
			String strWhere = "pk_defdoc = '"
					+ parentVO.getDef2()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'sf' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				de2 = defdoc[0].getName();
			}
		}
		Map sfsdlsg = OaWorkFlowUtil.listAdd("sfsdlsg", de2);
		list.add(sfsdlsg);

		Map kclx = OaWorkFlowUtil.listAdd("kclx", parentVO.getDef3());
		list.add(kclx);

		String str3 = "0";
		if (null != parentVO.getDef4()) {
			str3 = parentVO.getDef4();
		}
		Map kcqzd = OaWorkFlowUtil.listAdd("kcqzd", str3);
		list.add(kcqzd);

		String de5 = "0";
		if (null != parentVO.getDef5()) {
			String strWhere = "pk_defdoc = '"
					+ parentVO.getDef5()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'sf' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				de5 = defdoc[0].getName();
			}
		}
		Map sfxygh = OaWorkFlowUtil.listAdd("sfxygh", de5);
		list.add(sfxygh);

		Map kcxq = OaWorkFlowUtil.listAdd("kcxq", parentVO.getMemo());
		list.add(kcxq);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 设计申请单主表数据
	private JSONArray getMainMap2(ProjectProposalHeadVO parentVO)
			throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO) {

			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getCode());
			list.add(szgs);
			// 所属组织名称
			Map sszzmc = OaWorkFlowUtil.listAdd("sszzmc", orgVO.getName());
			list.add(sszzmc);
		}

		String djmcNC = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				" nvl(dr,0) = 0 and pk_billtypecode = '"
						+ parentVO.getBill_type() + "'");
		Map djmc = OaWorkFlowUtil.listAdd("djmc", djmcNC);
		list.add(djmc);

		getList(parentVO, list);

		Map ksrq = OaWorkFlowUtil.listAdd("ksrq", parentVO.getReq_start_date()
				.getYear()
				+ "-"
				+ parentVO.getReq_start_date().getStrMonth()
				+ "-" + parentVO.getReq_start_date().getStrDay());
		list.add(ksrq);

		String de2 = "0";
		if (null != parentVO.getDef2()) {
			String strWhere = "pk_defdoc = '"
					+ parentVO.getDef2()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'sf' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				de2 = defdoc[0].getName();
			}
		}
		Map sfsdlsg = OaWorkFlowUtil.listAdd("sfsdlsg", de2);
		list.add(sfsdlsg);

		Map sjlx = OaWorkFlowUtil.listAdd("sjlx", parentVO.getDef3());
		list.add(sjlx);

		Map kclx = OaWorkFlowUtil.listAdd("kclx", parentVO.getDef3());
		list.add(kclx);

		Map sjxq = OaWorkFlowUtil.listAdd("sjxq", parentVO.getMemo());
		list.add(sjxq);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 刨掘申请单主表数据
	private JSONArray getMainMap3(ProjectProposalHeadVO parentVO)
			throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO) {

			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getCode());
			list.add(szgs);
			// 所属组织名称
			Map sszzmc = OaWorkFlowUtil.listAdd("sszzmc", orgVO.getName());
			list.add(sszzmc);
		}

		getList(parentVO, list);

		String djmcNC = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				" nvl(dr,0) = 0 and pk_billtypecode = '"
						+ parentVO.getBill_type() + "'");
		Map djmc = OaWorkFlowUtil.listAdd("djmc", djmcNC);
		list.add(djmc);

		Map xmkgrq = OaWorkFlowUtil.listAdd("xmkgrq", parentVO
				.getReq_start_date().getYear()
				+ "-"
				+ parentVO.getReq_start_date().getStrMonth()
				+ "-"
				+ parentVO.getReq_start_date().getStrDay());
		list.add(xmkgrq);

		String de2 = "0";
		if (null != parentVO.getDef2()) {
			String strWhere = "pk_defdoc = '"
					+ parentVO.getDef2()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'sf' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				de2 = defdoc[0].getName();
			}
		}
		Map sfsdlsg = OaWorkFlowUtil.listAdd("sfsdlsg", de2);
		list.add(sfsdlsg);

		String de4 = "0";
		if (null != parentVO.getDef4()) {
			String strWhere = "pk_defdoc = '"
					+ parentVO.getDef4()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'sf' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				de4 = defdoc[0].getName();
			}
		}
		Map sfxkc = OaWorkFlowUtil.listAdd("sfxkc", de4);
		list.add(sfxkc);

		// if(null != parentVO.getDef3()){
		// String where = " pk_defdoc = '" + parentVO.getDef3() +
		// "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'sf' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
		// String def3 = "0";
		// if(null !=(String) getHyPubBO().findColValue("bd_defdoc", "name",
		// where)){
		// def3 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
		// where);
		// }
		//
		// Map sfhqkgd =OaWorkFlowUtil.listAdd("sfhqkgd",def3 );
		// list.add(sfhqkgd);
		// }
		String def3 = "0";
		if (null != parentVO.getDef3()) {
			def3 = parentVO.getDef3();
		}
		Map sfhqkgd = OaWorkFlowUtil.listAdd("sfhqkgd", def3);
		list.add(sfhqkgd);

		Map pjxqqzd = OaWorkFlowUtil.listAdd("pjxqqzd", parentVO.getMemo());
		list.add(pjxqqzd);

		// String de4 = "0";
		// if (null != parentVO.getDef4()) {
		// String strWhere = "pk_defdoc = '"
		// + parentVO.getDef4()
		// +
		// "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'sf' and nvl(dr,0) = 0)";
		// DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
		// DefdocVO.class, strWhere);
		// if (null != defdoc && defdoc.length == 1) {
		// de4 = defdoc[0].getName();
		// }
		// }
		// Map sfygh =OaWorkFlowUtil.listAdd("sfygh",de4 );
		// list.add(sfygh);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 控制价委托申请单主表数据
	private JSONArray getMainMap4(ProjectProposalHeadVO parentVO)
			throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO) {

			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getCode());
			list.add(szgs);
			// 所属组织名称
			Map sszzmc = OaWorkFlowUtil.listAdd("sszzmc", orgVO.getName());
			list.add(sszzmc);
		}

		getList(parentVO, list);

		String djmcNC = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				" nvl(dr,0) = 0 and pk_billtypecode = '"
						+ parentVO.getBill_type() + "'");
		Map djmc = OaWorkFlowUtil.listAdd("djmc", djmcNC);
		list.add(djmc);

		Map gcqzd = OaWorkFlowUtil.listAdd("gcqzd", parentVO.getDef2());
		list.add(gcqzd);

		Map tzgsje = OaWorkFlowUtil.listAdd("tzgsje", parentVO.getDef3());
		list.add(tzgsje);

		String de5 = "0";
		if (null != parentVO.getDef5()) {
			String strWhere = "pk_defdoc = '"
					+ parentVO.getDef5()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'sf' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				de5 = defdoc[0].getName();
			}
		}
		Map tjazsfdbkzj = OaWorkFlowUtil.listAdd("tjazsfdbkzj", de5);
		list.add(tjazsfdbkzj);

		Map tjbdhf = OaWorkFlowUtil.listAdd("tjbdhf", parentVO.getDef6());
		list.add(tjbdhf);

		Map azbdhf = OaWorkFlowUtil.listAdd("azbdhf", parentVO.getDef7());
		list.add(azbdhf);

		Map sdlsgxmhtcd = OaWorkFlowUtil.listAdd("sdlsgxmhtcd",
				parentVO.getDef8());
		list.add(sdlsgxmhtcd);

		Map wlgcazhhfqzd = OaWorkFlowUtil.listAdd("wlgcazhhfqzd",
				parentVO.getDef10());
		list.add(wlgcazhhfqzd);

		Map zbkzj = OaWorkFlowUtil.listAdd("zbkzj", parentVO.getDef11());
		list.add(zbkzj);

		String de17 = "0";
		if (null != parentVO.getDef17()) {
			String strWhere = "pk_defdoc = '"
					+ parentVO.getDef17()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'kzj' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				de17 = defdoc[0].getName();
			}
		}
		Map kzjlb = OaWorkFlowUtil.listAdd("kzjlb", de17);
		list.add(kzjlb);

		String de12 = "0";
		if (null != parentVO.getDef12()) {
			String strWhere = "pk_defdoc = '"
					+ parentVO.getDef12()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'sf' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				de12 = defdoc[0].getName();
			}
		}
		Map sfjcba = OaWorkFlowUtil.listAdd("sfjcba", de12);
		list.add(sfjcba);

		Map jgcxxsm = OaWorkFlowUtil.listAdd("jgcxxsm", parentVO.getMemo());
		list.add(jgcxxsm);

		Map tjybdqzd = OaWorkFlowUtil.listAdd("tjybdqzd", parentVO.getDef13());
		list.add(tjybdqzd);

		Map tjebdqzd = OaWorkFlowUtil.listAdd("tjebdqzd", parentVO.getDef14());
		list.add(tjebdqzd);

		Map tjsbdqzd = OaWorkFlowUtil.listAdd("tjsbdqzd", parentVO.getDef15());
		list.add(tjsbdqzd);

		Map tjsbdqzd1 = OaWorkFlowUtil
				.listAdd("tjsbdqzd1", parentVO.getDef16());
		list.add(tjsbdqzd1);

		Map azybdqzd = OaWorkFlowUtil.listAdd("azybdqzd", parentVO.getDef10());
		list.add(azybdqzd);

		Map azebdqzd = OaWorkFlowUtil.listAdd("azebdqzd", parentVO.getDef18());
		list.add(azebdqzd);

		Map tjybdkzj = OaWorkFlowUtil.listAdd("tjybdkzj", parentVO.getDef5());
		list.add(tjybdkzj);

		Map tjebdkzj = OaWorkFlowUtil.listAdd("tjebdkzj",
				parentVO.getNecessity());
		list.add(tjebdkzj);

		Map tjsbdkzj = OaWorkFlowUtil.listAdd("tjsbdkzj", parentVO.getDef11());
		list.add(tjsbdkzj);

		Map tjsbdkzj1 = OaWorkFlowUtil
				.listAdd("tjsbdkzj1", parentVO.getDef20());
		list.add(tjsbdkzj1);

		Map azybdkzj = OaWorkFlowUtil.listAdd("azybdkzj", parentVO.getDef4());
		list.add(azybdkzj);

		Map azebdkzj = OaWorkFlowUtil.listAdd("azebdkzj", parentVO.getDef19());
		list.add(azebdkzj);

		Map sgfajsjjgcxxsmqbfjbhpdftzjxgsq = OaWorkFlowUtil.listAdd("bz",
				parentVO.getMemo());
		list.add(sgfajsjjgcxxsmqbfjbhpdftzjxgsq);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 开工单主表数据
	private JSONArray getMainMap5(ProjectProposalHeadVO parentVO)
			throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO) {

			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getCode());
			list.add(szgs);
			// 所属组织名称
			Map sszzmc = OaWorkFlowUtil.listAdd("sszzmc", orgVO.getName());
			list.add(sszzmc);
		}

		getList(parentVO, list);

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		Map sfzh = new HashMap();
		String id = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == id) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		sfzh.put("fieldName", "sfzh");
		sfzh.put("fieldValue", id);
		list.add(sfzh);

		Map ksrq = OaWorkFlowUtil.listAdd("ksrq", parentVO.getReq_start_date()
				.getYear()
				+ "-"
				+ parentVO.getReq_start_date().getStrMonth()
				+ "-" + parentVO.getReq_start_date().getStrDay());
		list.add(ksrq);

		Map bz = OaWorkFlowUtil.listAdd("bz", parentVO.getMemo());
		list.add(bz);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 共有数据
	private List getList(ProjectProposalHeadVO parentVO, List list)
			throws BusinessException {

		Map djzj = OaWorkFlowUtil.listAdd("djzj",
				parentVO.getPk_projectproposal());
		list.add(djzj);
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		if (null != userVO) {

			Map zdrmc = OaWorkFlowUtil.listAdd("zdrmc", userVO.getUser_name());
			list.add(zdrmc);

			Map zdrzj = OaWorkFlowUtil.listAdd("zdrzj", userVO.getUser_code());
			list.add(zdrzj);
		}

		Map zdrq = OaWorkFlowUtil.listAdd("zdrq", parentVO.getBillmaketime()
				.getYear()
				+ "-"
				+ parentVO.getBillmaketime().getStrMonth()
				+ "-" + parentVO.getBillmaketime().getStrDay());
		list.add(zdrq);

		Map djh = OaWorkFlowUtil.listAdd("djh", parentVO.getBill_code());
		list.add(djh);

		//
		// String djmcNC = (String) getHyPubBO().findColValue("bd_billtype",
		// "billtypename", " nvl(dr,0) = 0 and pk_billtypecode = '" +
		// parentVO.getBill_type() + "'");
		// Map djmc =OaWorkFlowUtil.listAdd("djmc",djmcNC);
		// list.add(djmc);

		if (null != parentVO.getDef1()) {
			DeptVO deptVO = (DeptVO) getHyPubBO().queryByPrimaryKey(
					DeptVO.class, parentVO.getDef1());
			if (null != deptVO) {

				Map sqbmmc = OaWorkFlowUtil.listAdd("sqbmmc", deptVO.getName());
				list.add(sqbmmc);

				Map sqbmbh = OaWorkFlowUtil.listAdd("sqbmbh", deptVO.getCode());
				list.add(sqbmbh);
			}
		}

		String project_code = (String) getHyPubBO().findColValue(
				"bd_project",
				"project_code",
				"nvl(dr,0) = 0 and   pk_project  ='" + parentVO.getPk_project()
						+ "'");
		Map xmbm = OaWorkFlowUtil.listAdd("xmbm", project_code);
		list.add(xmbm);

		String project_name = (String) getHyPubBO().findColValue(
				"bd_project",
				"project_name",
				"nvl(dr,0) = 0 and   pk_project  ='" + parentVO.getPk_project()
						+ "'");
		Map xmmc = OaWorkFlowUtil.listAdd("xmmc", project_name);
		list.add(xmmc);

		String pk_projectclass = (String) getHyPubBO().findColValue(
				"bd_project",
				"pk_projectclass",
				"nvl(dr,0) = 0 and   pk_project  ='" + parentVO.getPk_project()
						+ "'");
		String type_name = (String) getHyPubBO().findColValue(
				"bd_projectclass",
				"type_name",
				"nvl(dr,0) = 0 and   pk_projectclass  ='" + pk_projectclass
						+ "'");
		Map xmlx = OaWorkFlowUtil.listAdd("xmlx", type_name);
		list.add(xmlx);

		String pk_eps = (String) getHyPubBO().findColValue(
				"bd_project",
				"pk_eps",
				"nvl(dr,0) = 0 and   pk_project  ='" + parentVO.getPk_project()
						+ "'");
		String eps_name = (String) getHyPubBO().findColValue("pm_eps",
				"eps_name", "nvl(dr,0) = 0 and   pk_eps  ='" + pk_eps + "'");
		Map eps = OaWorkFlowUtil.listAdd("eps", eps_name);
		list.add(eps);

		return list;
	}

	public ProjectProposalBillVO[] uncommitProjectProposal(
			ProjectProposalBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		ProjectProposalBillVO[] newAggVO = (ProjectProposalBillVO[]) unCommit(
				billVOs, pfParamVO);
		unOaCommit(newAggVO);
		return newAggVO;
	}

	private void unOaCommit(ProjectProposalBillVO[] aggVO)
			throws BusinessException {
		for (ProjectProposalBillVO temp : aggVO) {
			if ("4D15".equals(temp.getParentVO().getBill_type())) {
				OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
				/*
				 * String strWhere = " nvl(dr,0) = 0  and pk_bill ='" +
				 * temp.getPrimaryKey() + "'"; WorkFlowBill[] workFlowBills =
				 * (WorkFlowBill[]) new
				 * HYPubBO().queryByCondition(WorkFlowBill.class, strWhere);
				 * if(null != workFlowBills && workFlowBills.length == 1){ throw
				 * new BusinessException("已提交OA系统单据不允许收回！"); }
				 */
			}
		}
	}

	protected void initApproveAction(ApproveAction<ProjectProposalBillVO> action) {
		super.initApproveAction(action);

		IRule<ProjectProposalBillVO> rule = new WriteBackToProjectAfterRule();
		action.addAfterRule(rule);
	}

	protected void initInsertAction(InsertAction<ProjectProposalBillVO> action) {
		super.initInsertAction(action);

		action.addBeforeRule(new InitCurrTypeBeforeRule(
				new FieldsForInitCurrTypeImpl()));
	}

	protected void initUpdateAction(UpdateAction<ProjectProposalBillVO> action) {
		super.initUpdateAction(action);

		action.addBeforeRule(new InitCurrTypeBeforeRule(
				new FieldsForInitCurrTypeImpl()));
	}

	// 根据组织+项目+任务名称查询是否有该任务-----参数：pkOrg==所属组织，pk_project==项目，wbsName==WBS任务名称
	public String getWbsWork(String pkOrg, String pk_project, String wbsName)
			throws DAOException {
		String pk_wbs = "";
		String wbsWorkSql = "SELECT PK_WBS FROM PM_WBS WHERE DR = 0 AND (ENABLESTATE = 1 OR ENABLESTATE = 2) AND PK_DUTY_ORG = '"
				+ pkOrg
				+ "'"
				+ " AND PK_PROJECT = '"
				+ pk_project
				+ "' AND WBS_NAME = '" + wbsName + "'";
		List<Object[]> wbsWorkLs = getDao.query(wbsWorkSql);
		if (wbsWorkLs != null && wbsWorkLs.size() > 0
				&& wbsWorkLs.get(0)[0] != null) {
			// 如果查出多条只取第一条
			pk_wbs = wbsWorkLs.get(0)[0] + "";
		} else {
			// 根据组织+项目+任务去
			String selhaschildsql = "SELECT PK_WBS FROM PM_WBS WHERE DR = 0 AND ( ENABLESTATE = 1 OR ENABLESTATE = 2 ) AND PK_DUTY_ORG = '"
					+ pkOrg
					+ "' AND PK_PROJECT = '"
					+ pk_project
					+ "' AND WBS_NAME = '施工进度'";
			System.out.println("selhaschildsql==" + selhaschildsql);
			List<Object[]> chLs = getDao.query(selhaschildsql);
			if (chLs != null && chLs.size() > 0 && chLs.get(0)[0] != null) {
				String selchildsql = "SELECT COUNT(PK_WBS) AS SL FROM PM_WBS WHERE DR = 0 AND ( ENABLESTATE = 1 OR ENABLESTATE = 2 ) AND PK_DUTY_ORG = '"
						+ pkOrg + "' AND PK_PARENT  = '" + chLs.get(0)[0] + "'";
				System.out.println("selchildsql==" + selchildsql);
				List<Object[]> slLs = getDao.query(selchildsql);
				if (slLs != null && Integer.parseInt(slLs.get(0)[0] + "") > 0) {
					ExceptionUtils.wrappBusinessException("[项目未找到对应任务！");
				} else {
					wbsName = "施工进度";
				}

			}
		}
		return pk_wbs;
	}
}
