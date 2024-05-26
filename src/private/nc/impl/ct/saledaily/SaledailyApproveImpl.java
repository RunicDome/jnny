package nc.impl.ct.saledaily;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.trade.business.HYPubBO;
import nc.impl.ct.saledaily.action.SaledailyApproveAction;
import nc.impl.ct.saledaily.action.SaledailyFreezeAction;
import nc.impl.ct.saledaily.action.SaledailyModiDeleteAction;
import nc.impl.ct.saledaily.action.SaledailyModifySpAction;
import nc.impl.ct.saledaily.action.SaledailySendApproveAction;
import nc.impl.ct.saledaily.action.SaledailyTerminateAction;
import nc.impl.ct.saledaily.action.SaledailyUnApproveAction;
import nc.impl.ct.saledaily.action.SaledailyUnFreezeAction;
import nc.impl.ct.saledaily.action.SaledailyUnTerminateAction;
import nc.impl.ct.saledaily.action.SaledailyUnValidateAction;
import nc.impl.ct.saledaily.action.SaledailyValidateAction;
import nc.impl.ct.saledaily.action.SaledailyValidateSpAction;
import nc.impl.ct.saledaily.action.SaledailyWithDrawAction;
import nc.impl.portal.GetIntoPortalImpl;
import nc.itf.ct.saledaily.ISaledailyApprove;
import nc.itf.portal.IGetIntoPortal;
import nc.vo.bd.material.MaterialVO;
import nc.vo.ct.saledaily.entity.AggCtSaleVO;
import nc.vo.ct.saledaily.entity.CtSaleBVO;
import nc.vo.ct.saledaily.entity.CtSaleExecVO;
import nc.vo.ct.saledaily.entity.CtSaleVO;
import nc.vo.org.OrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.pub.MathTool;
import nc.vo.pubapp.pflow.PfUserObject;
import nc.vo.scmpub.util.ArrayUtil;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 销售合同
@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class SaledailyApproveImpl implements ISaledailyApprove {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表明

	public SaledailyApproveImpl() {
	}

	// 审批
	public AggCtSaleVO[] approve(AggCtSaleVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			AggCtSaleVO[] ctSaleVO = new SaledailyApproveAction().approve(vos,
					script);
			return ctSaleVO;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// TODO 单据同步OA功能
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

	private void senOaData(AggCtSaleVO[] billVOs) throws BusinessException {
		for (AggCtSaleVO temp : billVOs) {
			// 销售合同
			CtSaleVO hVO = temp.getParentVO();
			if ("4".equals(getDef2(hVO.getPk_org()))
					&& hVO.getVtrantypecode().contains("Z3")) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(),
						"Z3");
				if (oaVo != null && oaVo.getIsdr() == 0) {
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					// 获取主表数据
					JSONArray headData = getMainMap(temp.getParentVO());
					// 获取子表数据
					JSONArray bodyData = getDtaileDataMap(temp);
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(temp.getPrimaryKey());
					workFlowVO.setPkGroup(hVO.getPk_group());
					workFlowVO.setPkOrg(hVO.getPk_org());
					workFlowVO.setBillMaker(hVO.getBillmaker());
					workFlowVO.setCreator(hVO.getCreator());
					workFlowVO.setBillCode("Z3");
					// 测试临时写死，后期改为接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName("销售合同维护");
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					// WorkFlowBill的自定义项3 判断是否港华单据
					bill.setDef3("ZT");
					// 制单人身份证号
					UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
							UserVO.class, temp.getParentVO().getBillmaker());
					String idCard = (String) getHyPubBO().findColValue(
							"bd_psndoc",
							"id",
							"nvl(dr,0) = 0 and pk_psndoc='"
									+ userVO.getPk_psndoc() + "'");
					bill.setDef5(idCard);
					// 调用OA工具类同步数据至OA
					OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
				}
			}
		}
	}

	private JSONArray getMainMap(CtSaleVO parentVO) throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// 销售组织
		String orgName = (String) getHyPubBO().findColValue(
				"org_salesorg",
				"name",
				"nvl(dr,0) = 0 and pk_salesorg  = '" + parentVO.getPk_org()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", orgName));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 合同类型名称
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '"
						+ parentVO.getCtrantypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
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

		// 客户
		String customerName = (String) getHyPubBO().findColValue(
				"bd_customer",
				"name ",
				"nvl(dr,0) = 0 and  pk_customer  = '"
						+ parentVO.getPk_customer() + "'");
		list.add(OaWorkFlowUtil.listAddObj("kh", customerName));
		// 部门
		String vDeptName = (String) getHyPubBO().findColValue("org_dept_v",
				"name ",
				"nvl(dr,0) = 0 and pk_vid  = '" + parentVO.getDepid_v() + "'");
		list.add(OaWorkFlowUtil.listAddObj("bm", vDeptName));
		// 人员
		String psndocName = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name ",
				"nvl(dr,0) = 0 and  pk_psndoc = '" + parentVO.getPersonnelid()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("ry", psndocName));
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getDtaileDataMap(AggCtSaleVO temp)
			throws BusinessException {
		Map bodyMap = getBody(temp.getCtSaleBVO());
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(CtSaleBVO[] ctSaleVOs) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (CtSaleBVO temp : ctSaleVOs) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			if (null != temp.getPk_material()) {
				MaterialVO materialVO = (MaterialVO) getHyPubBO()
						.queryByPrimaryKey(MaterialVO.class,
								temp.getPk_material());
				// 物料编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"material_code", materialVO.getCode()));
				// 物料名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"material_name", materialVO.getName()));
				// 规格
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"materialspec", materialVO.getMaterialspec()));
				// 型号
				String str4 = "";
				if (null != materialVO.getMaterialtype()) {
					str4 = materialVO.getMaterialtype();
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"materialtype", str4));
			}
			// 主单位
			String zdwstr = "";
			if (null != temp.getCunitid()) {
				zdwstr = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc  = '" + temp.getCunitid()
								+ "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zdw",
					zdwstr));
			// 单位
			String dwstr = "";
			if (null != temp.getCastunitid()) {
				dwstr = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc  = '"
								+ temp.getCastunitid() + "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dw",
					dwstr));
			if (temp.getCbprojectid() != null) {
				// 项目
				ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
						.queryByPrimaryKey(ProjectHeadVO.class,
								temp.getCbprojectid());
				if (null != projectVO) {
					// 项目编码
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"bproject_code", projectVO.getProject_code()));
					// 项目名称
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"bproject_name", projectVO.getProject_name()));
				}
			}
			// 其他字段------end
			Map workflowRequestTableFieldsMap = new HashMap();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	public AggCtSaleVO[] freeze(AggCtSaleVO[] vos, AggCtSaleVO[] originBills)
			throws BusinessException {
		try {
			return new SaledailyFreezeAction().freeze(vos, originBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtSaleVO[] modiDelete(AggCtSaleVO[] vos) throws BusinessException {
		try {
			return new SaledailyModiDeleteAction().modiDelete(vos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtSaleVO[] modify(AggCtSaleVO[] vos, PfUserObject userConfirm,
			AggCtSaleVO[] originBills) throws BusinessException {
		try {
			return new SaledailyModifySpAction().modify(vos, userConfirm,
					originBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtSaleVO[] sendapprove(AggCtSaleVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			AggCtSaleVO[] nvos = new SaledailySendApproveAction().sendapprove(
					vos, script);
			// 将销售合同转换为报销单
			convertJKBXVO(nvos);
			// 提交OA
			senOaData(vos);
			return nvos;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 将销售合同转换为报销单
	public void convertJKBXVO(AggCtSaleVO[] nvos) throws BusinessException {
		String nvosPrimaryKey = null;
		IGetIntoPortal getIntoPortalImpl = NCLocator.getInstance().lookup(
				IGetIntoPortal.class);
		for (int i = 0; i < nvos.length; i++) {
			// 获取到客户档案中的def5 自定义项目 ，如果值为1 证明是工程集团，则不进入共享
			String IsGcjt = (String) getHyPubBO().findColValue(
					"bd_customer",
					"def5 ",
					"nvl(dr,0) = 0 and  pk_customer  = '"
							+ nvos[i].getParentVO().getPk_customer() + "'");
			// 获取单位的编码
			String orgCode = (String) getHyPubBO().findColValue(
					"org_salesorg",
					"code",
					"nvl(dr,0) = 0 and pk_salesorg  = '"
							+ nvos[i].getParentVO().getPk_org() + "'");

			// 对nvos中的组织进行判断 如果是工程集团下级单位不进共享限定只能由本单位的财务审批 ，不是工程集团的下级单位进入共享。
			if (orgCode.equals("512") && !"1".equals(IsGcjt)) {
				// 调用接口转为报销单
				// srcBillOrTranstype 原类型 destBillOrTranstype 转换后类型 srcBillVO
				getIntoPortalImpl.changeIntoPortal("Z3", "264X-Cxx-HFXSHT",
						nvos[i]);
				getHyPubBO().update(nvos[i].getParentVO());
			}

		}
	}

	public AggCtSaleVO[] terminate(AggCtSaleVO[] vos, AggCtSaleVO[] originBills)
			throws BusinessException {
		try {
			return new SaledailyTerminateAction().terminate(vos, originBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtSaleVO[] unapprove(AggCtSaleVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			return new SaledailyUnApproveAction().unApprove(vos, script);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtSaleVO[] unfreeze(AggCtSaleVO[] vos, AggCtSaleVO[] originBills)
			throws BusinessException {
		try {
			return new SaledailyUnFreezeAction().unfreeze(vos, originBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtSaleVO[] unterminate(AggCtSaleVO[] vos,
			AggCtSaleVO[] originBills) throws BusinessException {
		try {
			return new SaledailyUnTerminateAction().unterminate(vos,
					originBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtSaleVO[] unvalidate(AggCtSaleVO[] vos, AggCtSaleVO[] originBills)
			throws BusinessException {
		try {
			return new SaledailyUnValidateAction().unvalidate(vos, originBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtSaleVO[] validate(AggCtSaleVO[] vos, AggCtSaleVO[] originBills)
			throws BusinessException {
		try {
			List<AggCtSaleVO> firstAndCancelVersion = new ArrayList();
			List<AggCtSaleVO> firstAndCancelVersionOrig = new ArrayList();
			List<AggCtSaleVO> otherVersion = new ArrayList();
			List<AggCtSaleVO> otherVersionOrig = new ArrayList();
			AggCtSaleVO[] firstAndCancelReturnVOs = null;
			AggCtSaleVO[] otherReturnVOs = null;
			int cursor = 0;
			for (AggCtSaleVO vo : vos) {
				CtSaleExecVO[] execs = vo.getCtSaleExecVO();
				if (!ArrayUtil.isEmpty(execs)) {
					CtSaleExecVO newest = execs[0];
					for (int i = 1; i < execs.length; i++) {
						if ((execs[i].getStatus() != 2)
								&& (execs[i].getVexecdate().compareTo(
										newest.getVexecdate()) > 0)) {
							newest = execs[i];
						}
					}
					if ("取消生效".equals(newest.getVexecflow())) {
						firstAndCancelVersion.add(vo);
						firstAndCancelVersionOrig.add(originBills[cursor]);
						cursor++;
						continue;
					}
				}
				if (MathTool.equals(UFDouble.ONE_DBL, vo.getParentVO()
						.getVersion())) {
					firstAndCancelVersion.add(vo);
					firstAndCancelVersionOrig.add(originBills[cursor]);
				} else {
					otherVersion.add(vo);
					otherVersionOrig.add(originBills[cursor]);
				}
				cursor++;
			}
			if (firstAndCancelVersion.size() > 0) {
				firstAndCancelReturnVOs = new SaledailyValidateAction()
						.validate(
								(AggCtSaleVO[]) firstAndCancelVersion
										.toArray(new AggCtSaleVO[firstAndCancelVersion
												.size()]),
								(AggCtSaleVO[]) firstAndCancelVersionOrig
										.toArray(new AggCtSaleVO[firstAndCancelVersionOrig
												.size()]));
			}
			if (otherVersion.size() > 0) {
				otherReturnVOs = new SaledailyValidateSpAction().validate(
						(AggCtSaleVO[]) otherVersion
								.toArray(new AggCtSaleVO[otherVersion.size()]),
						(AggCtSaleVO[]) otherVersionOrig
								.toArray(new AggCtSaleVO[otherVersionOrig
										.size()]));
			}
			return (AggCtSaleVO[]) ArrayUtil.combinArrays(new AggCtSaleVO[][] {
					firstAndCancelReturnVOs, otherReturnVOs });
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtSaleVO[] withDraw(AggCtSaleVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			AggCtSaleVO[] nvos = new SaledailyWithDrawAction().withDraw(vos,
					script);
			unOaCommit(vos);
			return nvos;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 判断是否能收回
	private void unOaCommit(AggCtSaleVO[] aggVO) throws BusinessException {
		for (AggCtSaleVO temp : aggVO) {
			if ((temp.getParentVO().getVtrantypecode()).contains("Z3")) {
				OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			}
		}
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
}
