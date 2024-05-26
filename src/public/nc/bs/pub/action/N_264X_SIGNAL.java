package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.dao.BaseDAO;
import nc.bs.erm.util.ErFlowUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.itf.portal.IGetIntoPortal;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.fct.ap.entity.AggCtApVO;
import nc.vo.fct.ap.entity.CtApBVO;
import nc.vo.fct.ap.entity.CtApVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.uap.pf.PFBusinessException;
import nc.ws.intf.HttpClient;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONObject;

/**
 * 工作流流程驱动
 * 
 * @author chenshuaia
 * 
 */
@SuppressWarnings({ "rawtypes", "unused" })
public class N_264X_SIGNAL extends N_264X_APPROVE {
	public N_264X_SIGNAL() {
		super();
		m_keyHas = null;
	}

	/*
	 * 备注：平台编写规则类 接口执行类
	 */
	@Override
	public Object runComClass(PfParameterVO vo) throws BusinessException {
		try {
			super.m_tmpVo = vo;
			// ####本脚本必须含有返回值,返回DLG和PNL的组件不允许有返回值####
			Object retObj = null;
			// ####重要说明：生成的业务组件方法尽量不要进行修改####
			// ####该组件为单动作工作流处理结束...不能进行修改####
			// 方法说明:null
			// ##################################################
			// ####该组件为单动作工作流处理开始...不能进行修改####

			List<JKBXVO> auditVOs = new ArrayList<JKBXVO>();
			List<MessageVO> fMsgs = new ArrayList<MessageVO>();

			BXVO bxvo = (BXVO) vo.m_preValueVo;

			// begin--added by chendya@ufida.com.cn 审核操作权限
			// //是否期初单据
			// boolean isQc = bxvo.getParentVO().getQcbz().booleanValue();
			// //是否常用单据
			// boolean isInit = bxvo.getParentVO().isInit();
			// //是否从NC客户端
			// boolean isNCClient=bxvo.isNCClient();

			// 非期初、常用单据校验数据权限,65去除审核时数据权限
			// if (isNCClient) {
			// if (!isQc && !isInit) {
			// BXDataPermissionChkUtil.process(bxvo,
			// BXConstans.ERMEXPRESOURCECODE,
			// BXConstans.EXPAPPROVECODE,vo.m_operator);
			// }
			// }
			// --end
			procActionFlow(vo);

			// boolean isWorkFlow =
			// ErUtil.isUseWorkFlow(bxvo.getParentVO().getPk_org());
			boolean isWorkFlowFinalNode = ErUtil.isWorkFlowFinalNode(vo);

			String approveResult = null;
			if (vo.m_workFlow != null) {
				approveResult = vo.m_workFlow.approveresult;
			}

			// ==================================================
			if (isWorkFlowFinalNode
					&& (approveResult == null || approveResult
							.equalsIgnoreCase("Y"))) {
				// String taskId =
				// vo.m_workFlow.getTaskInfo().getTask().getTaskPK();
				// WFTask taskVo =
				// NCLocator.getInstance().lookup(IPFWorkflowQry.class).queryWFTaskByPk(taskId);
				// if (taskVo.getStatus() ==
				// WfTaskOrInstanceStatus.Finished.getIntValue()) {
				auditVOs.add(bxvo);
				// }
			} else {
				fMsgs.add(new MessageVO(bxvo, ActionUtils.AUDIT));
			}

			setParameter("billVO", auditVOs.toArray(new JKBXVO[] {}));

			retObj = runClass("nc.bs.arap.bx.BXZbBO", "audit",
					"&billVO:nc.vo.ep.bx.JKBXVO[]", vo, m_keyHas);

			if (retObj != null) {

				MessageVO[] msgs = (MessageVO[]) retObj;

				for (int i = 0; i < msgs.length; i++) {
					fMsgs.add(msgs[i]);
				}
			}

			JKBXHeaderVO headVO = bxvo.getParentVO();
			int payFlag = headVO.getPayflag();
			UFBoolean qcbz = headVO.getQcbz();
			// wangyl7 2018年1月18日15:12:09 Critical问题修改 begin
			if (0 == headVO.getSxbz().intValue()
					|| ((null == qcbz || !qcbz.booleanValue()) && payFlag != 3
							&& payFlag != 101 && payFlag != 102)) {
				// end
				InvocationInfoProxy.getInstance().setProperty("reids_event",
						"true");
				EventDispatcher
						.fireEvent(new BusinessEvent(
								"6c8584f4-21d4-4ec9-9eac-96ad472acf2f",
								"200615", bxvo));
				Logger.error("公布业务事件 201615 通知redis更新缓存");
			}
			// // 审批通过更新单据状态 addby yinzy -- start
			// if (1==bxvo.getParentVO().getSpzt()) {
			// JKBXHeaderVO headVo = bxvo.getParentVO();
			// nc.bs.dao.BaseDAO baseDAO = new nc.bs.dao.BaseDAO();
			// String pk = headVo.getPk_jkbx();
			// String sql =
			// "update er_bxzb z set z.djzt = 2 where z.pk_jkbx = '" + pk + "'";
			// baseDAO.executeUpdate(sql);
			// sql =
			// "update cmp_settlement s set s.aduitstatus = 0, s.busistatus = 1 where s.pk_busibill = '"
			// + pk + "'";
			// baseDAO.executeUpdate(sql);
			// }
			// // 审批通过更新单据状态 addby yinzy -- end

			// wyl 校验单据状态和审批状态
			ErFlowUtil.checkJKBxFlowStatus(bxvo.getParentVO().getPk_jkbx(),
					bxvo.getParentVO().getDjlxbm());
			if (vo.m_workFlow != null
					&& vo.m_workFlow.getApprovestatus() != null) {
				String approveresult = vo.m_workFlow.getApproveresult();
				String billtype = vo.m_workFlow.getPk_billtype();
				// XBX新增调用OA接口
				String checkMan = (String) new HYPubBO().findColValue(
						"sys_config", "config_value",
						"config_key='oa-token-approvers'");
				String def2 = (String) new HYPubBO().findColValue("org_orgs",
						"def2", "pk_org = '" + vo.m_workFlow.getPk_org() + "'");
				if (StringUtils.equals(def2, "1")) {
					// 查询节点
					int approvestatus = vo.m_workFlow.getApprovestatus();
					String senderman = vo.m_workFlow.getSenderman();
					String checkman = vo.m_workFlow.getCheckman();
					String cnfapprover = (String) new HYPubBO().findColValue(
							"sys_config", "config_value",
							"config_key='CNFTF_APPROVER'");
					Logger.error("billtype=" + billtype + "checkman="
							+ checkman + "cnfapprover=" + cnfapprover
							+ "senderman=" + senderman);
					if ("264X-Cxx-CNFTFSPD".equals(billtype)
							&& cnfapprover.contains(checkman)
							&& "Y".equals(approveresult)) {
						appr(vo.m_preValueVos);
					}
					if (approvestatus == 0
							&& "1001A2100000000QQF9I".equals(senderman)
							&& "1001A2100000000QQF9I".equals(checkman)) {
						if (null != vo.m_workFlow.getCheckman()
								&& checkMan.contains(vo.m_workFlow
										.getCheckman())
								&& "Y".equals(approveresult)
								&& ("2647".equals(billtype)
										|| "2641".equals(billtype)
										|| "264X-Cxx-JBCFBX".equals(billtype)
										|| "264X-Cxx-YGFYBXD".equals(billtype) || "264X-Cxx-PTFJRJLTF"
											.equals(billtype))) {
							appr(vo.m_preValueVos);
						}
					}
				}
				// 工程报销单共享驳回后删除当前共享单据，修改原单据状态为自由态，并回写驳回原因
				if ("R".equals(approveresult)
						&& ("264X-Cxx-HFCGHT".equals(billtype)
								|| "264X-Cxx-HFXSHT".equals(billtype)
								|| "264X-Cxx-gcfkht".equals(billtype) || "264X-Cxx-gcskht"
									.equals(billtype))) {
					throw new BusinessException("请点击【合同驳回】按钮驳回！");
				}
			}
			// 报销单XBX1017 264X-Cxx-gcfkht 工程—付款合同 审批完成后同步付款合同的单据
			int djzt = headVO.getDjzt();// 单据状态
			String jylx = bxvo.getParentVO().getDjlxbm();// 交易类型编码
			if (djzt == 2 || djzt == 3) {
				if ("264X-Cxx-HFCGHT".equals(jylx)
						|| "264X-Cxx-HFXSHT".equals(jylx)
						|| "264X-Cxx-gcfkht".equals(jylx)
						|| "264X-Cxx-gcskht".equals(jylx)) {
					IGetIntoPortal itf = NCLocator.getInstance().lookup(
							IGetIntoPortal.class);
					itf.approveSrcBill(jylx, bxvo);
				}
			}
			return fMsgs.toArray(new MessageVO[] {});

		} catch (Exception ex) {
			if (ex instanceof BusinessException)
				throw (BusinessException) ex;
			else
				throw new BusinessException(ex.getMessage(), ex);
		}
	}

	private void appr(AggregatedValueObject[] aggVO) throws BusinessException {
		// TODO Auto-generated method stub
		String url = (String) new HYPubBO().findColValue("sys_config",
				"config_value", "config_key='oa-url'");
		/*
		 * String zoomkeyAuthToken = (String) new HYPubBO().findColValue(
		 * "sys_config", "config_value", "config_key='zq-token'");
		 */
		for (AggregatedValueObject aggvo : aggVO) {
			BXVO jkvo = (BXVO) aggvo;
			BXHeaderVO hvo = (BXHeaderVO) jkvo.getParentVO();
			if (hvo.getDjrq().compareTo(new UFDate("2021-10-13")) > 0) {
				Logger.error("hvo.getPrimaryKey()==" + hvo.getPrimaryKey());
				WorkFlowBill[] vo = (WorkFlowBill[]) new HYPubBO()
						.queryByCondition(WorkFlowBill.class,
								"pk_bill='" + hvo.getPrimaryKey() + "'");
				if (getDef2(hvo.getPk_org()) == null) {
					return;
				}
				if (vo == null || vo.length <= 0) {
					throw new BusinessException("未获取到WorkFlowBillVO！");
				}
				// PM_WORKFLOW_BILL 自定义项2作为判断是否调用接口 XBX 20211209新增
				if (!"NO".equals(vo[0].getDef2())) {
					if (vo[0].getDef3() == null || "~".equals(vo[0].getDef3())) {
						String requestid = vo[0].getRequestid() + "";
						Map<String, String> params = new HashMap<>();
						params.put("requestid", requestid);
						Map<String, String> headers = new HashMap<>();
						headers.put("Content-Type",
								"application/x-www-form-urlencoded");
						String back = HttpClient.httpPostForm(url, params,
								headers, "utf-8");
						JSONObject res = JSONObject.fromObject(back);
						String success = res.getString("error_code");
						if (!"0".equals(success)) {
							throw new BusinessException("调用OA获取NC更新数据通知接口出错："
									+ res.getString("error_msg"));
						}
						WorkFlowBill voa = vo[0];
						voa.setDef2("NO");
						voa.setStatus(VOStatus.UPDATED);
						new HYPubBO().update(voa);
					}
				}
			}
		}
	}

	// 删除共享单据
	public void delssc(String billno) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String mes = null;// 报错信息
		String sql1 = "delete from ssc_activetask where pk_ssctask in( select pk_ssctask from ssc_ssctask where billcode='"
				+ billno + "')";
		String sql2 = "delete from ssc_persontask where pk_ssctask in( select pk_ssctask from ssc_ssctask where billcode='"
				+ billno + "')";
		String sql3 = "delete from ssc_posttask where pk_ssctask in( select pk_ssctask from ssc_ssctask where billcode='"
				+ billno + "')";
		String sql4 = "delete from ssc_ssctasklog where pk_ssctask in( select pk_ssctask from ssc_ssctask where billcode='"
				+ billno + "')";
		String sql5 = "delete from ssc_ssctask where billcode='" + billno + "'";
		try {
			dao.executeUpdate(sql1);
			dao.executeUpdate(sql2);
			dao.executeUpdate(sql3);
			dao.executeUpdate(sql4);
			dao.executeUpdate(sql5);
		} catch (Exception e) {
			// TODO: handle exception
			ExceptionUtils.marsh(e);
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
