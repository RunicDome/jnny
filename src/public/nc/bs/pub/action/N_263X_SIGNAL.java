package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.erm.util.ErFlowUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKHeaderVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
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
public class N_263X_SIGNAL extends N_263X_APPROVE {
	public N_263X_SIGNAL() {
		super();
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
			JKVO bxvo = (JKVO) vo.m_preValueVo;

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
				auditVOs.add(bxvo);
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

			if ("N".equals(headVO.getSxbz())
					|| ((null == qcbz || !qcbz.booleanValue()) && payFlag != 3
							&& payFlag != 101 && payFlag != 102)) {
				InvocationInfoProxy.getInstance().setProperty("reids_event",
						"true");
				EventDispatcher
						.fireEvent(new BusinessEvent(
								"6c8584f4-21d4-4ec9-9eac-96ad472acf2f",
								"200615", bxvo));
				Logger.error("公布业务事件 201615 通知redis更新缓存");
			}

			// wyl 校验单据状态和审批状态
			ErFlowUtil.checkJKBxFlowStatus(bxvo.getParentVO().getPk_jkbx(),
					bxvo.getParentVO().getDjlxbm());
			if (vo.m_workFlow != null
					&& vo.m_workFlow.getApprovestatus() != null) {
				// XBX新增调用OA接口
				String checkMan = (String) new HYPubBO().findColValue(
						"sys_config", "config_value",
						"config_key='oa-token-approvers'");
				String def2 = (String) new HYPubBO().findColValue("org_orgs",
						"def2", "pk_org = '" + vo.m_workFlow.getPk_org()
								+ "'");
				if (StringUtils.equals(def2, "1")) {
					String billtype = vo.m_workFlow.getPk_billtype();
					// 查询节点
					int approvestatus = vo.m_workFlow.getApprovestatus();
					String senderman = vo.m_workFlow.getSenderman();
					String checkman = vo.m_workFlow.getCheckman();
					String approveresult = vo.m_workFlow.getApproveresult();
					if (approvestatus == 0
							&& "1001A2100000000QQF9I".equals(senderman)
							&& "1001A2100000000QQF9I".equals(checkman)
							&& "Y".equals(approveresult)) {
						if (null != vo.m_workFlow.getCheckman()
								&& checkMan.contains(vo.m_workFlow.getCheckman())
								&& ("2631".equals(billtype) || "263X-Cxx-YGJKD"
										.equals(billtype))) {
							appr(vo.m_preValueVos);
						}
					}
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
			JKVO jkvo = (JKVO) aggvo;
			JKHeaderVO hvo = (JKHeaderVO) jkvo.getParentVO();
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
					if(vo[0].getDef3() == null || "~".equals(vo[0].getDef3())){	
						String requestid = vo[0].getRequestid() + "";
						Map<String, String> params = new HashMap<>();
						params.put("requestid", requestid);
						Map<String, String> headers = new HashMap<>();
						headers.put("Content-Type",
								"application/x-www-form-urlencoded");
						String back = HttpClient.httpPostForm(url, params, headers,
								"utf-8");
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
