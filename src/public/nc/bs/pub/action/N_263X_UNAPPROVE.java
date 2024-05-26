package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.er.util.BXDataPermissionChkUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Logger;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.imag.pub.util.ImageServiceUtil;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.change.PublicHeadVO;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uap.pf.PFBusinessException;

/**
 * 备注：借款单的审核 单据动作执行中的动态执行类的动态执行类。
 * 
 * 创建日期：(2007-7-9)
 * 
 * @author 平台脚本生成
 */
public class N_263X_UNAPPROVE extends AbstractCompiler2 {

	public N_263X_UNAPPROVE() {
		super();
		// m_methodReturnHas = new Hashtable<String, Object>();
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

			List<JKBXVO> auditVOs = new ArrayList<JKBXVO>();

			List<MessageVO> fMsgs = new ArrayList<MessageVO>();

			JKVO bxvo = (JKVO) vo.m_preValueVo;

			// begin--added by chendya@ufida.com.cn 借款单反审核操作权限

			/**
			 * XBX判断是否推送拜特资金，状态为2可取消审批，其他不可取消审批
			 */
//			if(bxvo.getParentVO().getZyx28() != null && "Y".equals(bxvo.getParentVO().getZyx28())){
//				if(!(bxvo.getParentVO().getZyx29() != null && "-2".equals(bxvo.getParentVO().getZyx29()))){
//					throw new BusinessException("已推送拜特资金系统，当前结果{"+bxvo.getParentVO().getZyx29() +"}");
//				}
//			}
			// 是否期初单据
			boolean isQc = bxvo.getParentVO().getQcbz().booleanValue();

			// 是否常用单据
			boolean isInit = bxvo.getParentVO().isInit();
			// 是否从NC客户端
			boolean isNCClient = bxvo.isNCClient();

			// 非期初、常用单据校验数据权限
			if (isNCClient) {
				if (!isQc && !isInit) {
					BXDataPermissionChkUtil.process(bxvo,
							BXConstans.ERMLOANRESOURCECODE,
							BXConstans.LOANUNAPPROVECODE, vo.m_operator);
				}
			}
			// --end
			int spStatus = bxvo.getParentVO().getSpzt();
			boolean bflag = procUnApproveFlow(vo);

			boolean isWorkFlow = ErUtil.isUseWorkFlow(bxvo.getParentVO()
					.getPk_org());
			boolean isWorkFlowFinalNode = ErUtil.isWorkFlowFinalNode(vo);

			// 取消审批影像扫描校验
			if (isWorkFlow) {
				checkImage(bxvo);
			}

			if (isWorkFlow && isWorkFlowFinalNode) {// 工作流时?
													// 当工作流中审批不通过，反审是不是会出现问题？
				auditVOs.add(bxvo);
			} else {
				if (bflag && spStatus != IBillStatus.NOPASS) {
					if (vo.m_workFlow == null) {
						bxvo.getParentVO().setSpzt(IBillStatus.COMMIT);
					}
					auditVOs.add(bxvo);
				} else {
					fMsgs.add(new MessageVO(bxvo, ActionUtils.UNAUDIT));
				}
			}

			setParameter("billVO", auditVOs.toArray(new JKBXVO[] {}));
			retObj = runClass("nc.bs.arap.bx.BXZbBO", "unAudit",
					"&billVO:nc.vo.ep.bx.JKBXVO[]", vo, m_keyHas);

			if (retObj != null) {
				// m_methodReturnHas.put("unAuditBill", retObj);

				MessageVO[] msgs = (MessageVO[]) retObj;

				for (int i = 0; i < msgs.length; i++) {
					fMsgs.add(msgs[i]);
				}
			}
			InvocationInfoProxy.getInstance().setProperty("reids_event", "true");
			EventDispatcher.fireEvent(new BusinessEvent("6c8584f4-21d4-4ec9-9eac-96ad472acf2f", "200615", bxvo));
			Logger.error("公布业务事件 201615 通知redis更新缓存");
			return fMsgs.toArray(new MessageVO[] {});

		} catch (Exception ex) {
			if (ex instanceof BusinessException)
				throw (BusinessException) ex;
			else
				throw new PFBusinessException(ex.getMessage(), ex);
		}
	}

	@Override
	public String getCodeRemark() {
		return "erm action script not allowed modify, all rights reserved!;";
	}

	/*
	 * 备注：设置脚本变量的HAS
	 */
	protected void setParameter(String key, Object val) {
		if (m_keyHas == null) {
			m_keyHas = new Hashtable<String, Object>();
		}
		if (val != null) {
			m_keyHas.put(key, val);
		}
	}

	// protected java.util.Hashtable<String, Object> m_methodReturnHas = new
	// java.util.Hashtable<String, Object>();

	protected Hashtable<String, Object> m_keyHas = null;

	protected void getPfVO(PfParameterVO paraVo, JKBXVO bxvo) {

		PublicHeadVO standHeadVo = new PublicHeadVO();

		getHeadInfo(standHeadVo, bxvo, bxvo.getParentVO().getDjlxbm());

		paraVo.m_billType = standHeadVo.billType;
		paraVo.m_billNo = standHeadVo.billNo;
		paraVo.m_billId = standHeadVo.pkBillId;
		paraVo.m_pkOrg = standHeadVo.pkOrg;
		paraVo.m_makeBillOperator = standHeadVo.operatorId;
		paraVo.m_preValueVo = bxvo;
		paraVo.m_preValueVos = new JKBXVO[] { bxvo };
		paraVo.m_standHeadVo = standHeadVo;
	}

	private void getHeadInfo(nc.vo.pub.change.PublicHeadVO headvo,
			AggregatedValueObject vo, String billtype) {
		headvo.billType = billtype;
	}

	private void checkImage(JKBXVO bxvo) throws BusinessException {
		String pk_billid = "";
		if (bxvo != null && bxvo.getParentVO() != null
				&&bxvo.getParentVO().getPk_jkbx() != null) {
			pk_billid = bxvo.getParentVO().getPk_jkbx();
		}
		ImageServiceUtil.recallCheckWithImage(pk_billid);
//		if (bxvo == null || bxvo.getParentVO() == null
//				|| bxvo.getParentVO().getPk_jkbx() == null) {
//
//		}
//
//		boolean isWfOnImage = false;
//		boolean isInstalled = ImageCheckUtil.isInstalledIamge(bxvo);// 是否安装影像模块
//
//		if (!isInstalled) {
//			return;
//		}
//
//		try {
//			isWfOnImage = ((IImagUtil) NCLocator.getInstance().lookup(
//					IImagUtil.class.getName())).isWFOnImageActivity(bxvo
//					.getParentVO().getPk_jkbx());
//		} catch (Exception e) {// 影像服务无法判断是否安装，这里抛出异常就认为没有安装
//			ExceptionHandler.handleException(e);
//		}
//
//		if (isWfOnImage) {
//			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
//					.getNCLangRes().getStrByID("expensepub_0",
//							"0expense-000006")/*
//											 * @ res "当前有影像扫描活动正在进行，无法反审单据"
//											 */);
//		}
	}
}
