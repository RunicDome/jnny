package nc.bs.arap.bx;

//员工借款单/差旅费借款单
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import nc.bs.businessevent.EventDispatcher;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.callouter.FipCallFacade;
import nc.bs.er.settle.ErForCmpBO;
import nc.bs.er.util.BXDataPermissionChkUtil;
import nc.bs.er.util.FipUtil;
import nc.bs.er.util.SqlUtils;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.mp.ErmMpCmpException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.data.param.CommonParam;
import nc.impl.arap.bx.ArapBXBillPrivateImp;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.ISqdlrKeyword;
import nc.itf.fi.pub.Currency;
import nc.itf.fipub.summary.ISummaryQueryService;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.jdbc.framework.ConnectionFactory;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.crossdb.CrossDBConnection;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.util.SQLHelper;
import nc.pubitf.arap.payable.IArapPayableBillPubQueryService;
import nc.pubitf.arap.payable.IArapPayableBillPubService;
import nc.pubitf.arap.receivable.IArapReceivableBillPubQueryService;
import nc.pubitf.arap.receivable.IArapReceivableBillPubService;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.billstatus.ARAPBillStatus;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.arap.payable.AggPayableBillVO;
import nc.vo.arap.receivable.AggReceivableBillVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.cmp.BusiStatus;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.ep.bx.JsConstrasVO;
import nc.vo.ep.bx.MtappfUtil;
import nc.vo.ep.bx.SqdlrVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.ep.dj.DjCondVO;
import nc.vo.er.check.VOChecker;
import nc.vo.er.check.VOStatusChecker;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.settle.SettleUtil;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.fipub.billcode.FinanceBillCodeInfo;
import nc.vo.fipub.billcode.FinanceBillCodeUtils;
import nc.vo.fipub.summary.SummaryVO;
import nc.vo.fipub.utils.KeyLock;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFTime;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.sm.UserVO;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uap.rbac.constant.INCSystemUserConst;
import nc.vo.uap.rbac.role.RoleVO;
import nc.vo.util.AuditInfoUtil;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import nc.ws.intf.oadata.util.JKBXOaUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * @author twei
 * 
 *         nc.bs.ep.bx.BXZbBO
 * 
 *         借款报销类单据表头业务类
 */
@SuppressWarnings({ "deprecation", "unused", "rawtypes", "unchecked",
		"restriction" })
public class BXZbBO {
	private JKBXDAO jkbxDAO;

	/* 员工借款单 */
	private static String RL_YGJE_001 = "263X-Cxx-YGJKD";

	/* 差旅费借款单 */
	private static String RL_CLFJK_002 = "2631";

	/* 员工费用报销单 */
	private static String RL_YGFYBX_003 = "264X-Cxx-YGFYBXD";

	/* 配套及热计量退费审 */
	private static String RL_RJLTF_004 = "264X-Cxx-PTFJRJLTF";

	/* 差旅费报销单 */
	private static String RL_CLFBX_005 = "2641";

	/* 还款单 */
	private static String RL_HKD_006 = "2647";

	/* 加班餐费报销单 */
	private static String RL_JBCFBX_007 = "264X-Cxx-JBCFBX";

	/* 采暖费退费审批单 */
	private static String RL_CNFTUSP_008 = "264X-Cxx-CNFTFSPD";

	/**
	 * 借款报销单删除
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] delete(JKBXVO[] vos) throws BusinessException {

		MessageVO[] msgVos = new MessageVO[vos.length];

		compareTs(vos);// ts校验（包含了主键锁）

		List<JKBXHeaderVO> deleteHeaders = new ArrayList<JKBXHeaderVO>();
		try {
			for (int i = 0; i < vos.length; i++) {
				msgVos[i] = new MessageVO(vos[i], ActionUtils.DELETE);
				JKBXHeaderVO parentVO = vos[i].getParentVO();

				JKBXVO vo = retriveItems(vos[i]);// 表体需要补充，因项目预算一些业务插件中需要
				fillUpMapf(vo);

				checkDelete(vo);// 校验删除

				beforeActInf(vo, MESSAGE_DELETE);
				// 删除业务信息
				getBxBusitemBO(parentVO.getDjlxbm(), parentVO.getDjdl())
						.deleteByBXVOs(new JKBXVO[] { vo });

				// 删除冲借款对照信息
				new ContrastBO().deleteByPK_bxd(new String[] { parentVO
						.getPk_jkbx() });
				// 删除报销核销 预提明细
				new BxVerifyAccruedBillBO().deleteByBxdPks(parentVO
						.getPk_jkbx());

				// 批量删除表头
				deleteHeaders.add(parentVO);

				// 暂存单据没有传结算，删除暂存的单据也应该不走结算
				boolean isTempSave = BXStatusConst.DJZT_TempSaved == parentVO
						.getDjzt().intValue() ? true : false;

				// 判断CMP产品是否启用
				boolean isCmpInstalled = SettleUtil.isCmpInstall(vo
						.getParentVO());

				// 是否 既无收款也无付款
				boolean isExistsPayOrRecv = isExistsPayOrRecv(vo.getParentVO());

				// 不是暂存单据、有收款或付款金额、结算安装、并且不是调整单
				if (!isTempSave && isExistsPayOrRecv && isCmpInstalled
						&& !vo.getParentVO().isAdjustBxd()) {
					new ErForCmpBO().invokeCmp(vo, parentVO.getDjrq(),
							BusiStatus.Deleted);
				}

				afterActInf(vo, MESSAGE_DELETE);

				// 非常用单据回退单据号，常用单据不生成单据号
				if (!vo.getParentVO().isInit()) {
					returnBillCode(vo);
					// 删除审批流
					NCLocator
							.getInstance()
							.lookup(IWorkflowMachine.class)
							.deleteCheckFlow(
									vo.getParentVO().getDjlxbm(),
									vo.getParentVO().getPrimaryKey(),
									vo,
									InvocationInfoProxy.getInstance()
											.getUserId());
				}
				// 调用东港税务系统同步单据状态
				IArapForDGSWService util = (IArapForDGSWService) NCLocator
						.getInstance().lookup(IArapForDGSWService.class);
				JSONObject res = util.sendBillByNCBill(
						parentVO.getPrimaryKey(), parentVO.getPk_org(),
						CommonParam.DELETE, parentVO.getPk_billtype());
				if (!"Y".equals(res.getString("success"))) {
					throw new BusinessException(res.getString("errinfo"));
				}
			}

			// 删除表头
			getJKBXDAO().delete(deleteHeaders.toArray(new JKBXHeaderVO[] {}));
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}

		return msgVos;
	}

	public IBXBusItemBO getBxBusitemBO(String djlxbm, String djdl)
			throws BusinessException {
		try {
			BusiTypeVO busTypeVO = BXUtil.getBusTypeVO(djlxbm, djdl);
			String busiClass = busTypeVO.getInterfaces().get(
					BusiTypeVO.IBXBusItemBO);
			return (IBXBusItemBO) Class.forName(busiClass).newInstance();
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	/**
	 * 特殊处理摘要
	 * 
	 * @param vo
	 */
	private void dealZyName(JKBXVO vo) throws BusinessException {
		String value = vo.getParentVO().getZy();
		if (value != null && value.length() == 20) {
			ISummaryQueryService service = NCLocator.getInstance().lookup(
					nc.itf.fipub.summary.ISummaryQueryService.class);
			SummaryVO[] summaryVOs = service
					.querySummaryVOByCondition(SummaryVO.PK_SUMMARY + "='"
							+ value + "'");
			if (summaryVOs != null && summaryVOs.length > 0) {
				vo.getParentVO().setZy(summaryVOs[0].getSummaryname());
			}
		}
	}

	/**
	 * 正式保存，进行业务校验，并调用需要的接口
	 * 
	 * @param vos
	 *            要保存单据的聚合VO数组
	 */
	public JKBXVO[] save(JKBXVO[] vos) throws BusinessException {
		VOChecker checker = new VOChecker();
		for (JKBXVO vo : vos) {
			// 特殊处理摘要
			dealZyName(vo);
			// 只录表头，不录表体的情况下生成表体行
			BXUtil.generateJKBXRow(vo);

			VOChecker.prepare(vo);
			// 保存校验
			checker.checkkSaveBackground(vo);

			// 检查单据是否关帐
			VOChecker.checkErmIsCloseAcc(vo);
			// XBX控制预算类别
			JKBXHeaderVO parentVO = vo.getParentVO();
			BXBusItemVO[] mxvos = vo.getBxBusItemVOS();
			String type = parentVO.getDjlxbm();
			if (mxvos != null && mxvos.length > 0) {
				for (BXBusItemVO mxvo : mxvos) {
					if (RL_CLFBX_005.equals(type) || RL_JBCFBX_007.equals(type)) {
						String pk_org = parentVO.getPk_org();
						OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(
								OrgVO.class, pk_org);
						if (orgVO.getDef8() != null
								&& "1".equals(orgVO.getDef8())) {
							if (mxvo.getSzxmid() == null
									|| "".equals(mxvo.getSzxmid())
									|| "~".equals(mxvo.getSzxmid())) {
								throw new BusinessException(
										"明细表预算类别为空，请修改明细行后保存！");
							}
						}
					}
				}
			}
			if (parentVO.getQcbz() != null && parentVO.getQcbz().booleanValue()) {
				// 期初单据保存后自动审核通过
				parentVO.setSpzt(IPfRetCheckInfo.PASSING);
				parentVO.setShrq(new UFDateTime(parentVO.getDjrq(), new UFTime(
						"00:00:00")));

				parentVO.setJsrq(parentVO.getDjrq());
				parentVO.setApprover(InvocationInfoProxy.getInstance()
						.getUserId());
				parentVO.setJsr(InvocationInfoProxy.getInstance().getUserId());

				parentVO.setPayflag(BXStatusConst.PAYFLAG_PayFinish);
				parentVO.setPayman(InvocationInfoProxy.getInstance()
						.getUserId());
				parentVO.setPaydate(parentVO.getDjrq());
			}

			// 去服务器事件作为创建时间
			if (parentVO.getCreator() == null) {
				AuditInfoUtil.addData(parentVO);
			}

			fillUpMapf(vo);
			// if("2647".equals(parentVO.getDjlxbm())){
			// parentVO.setTotal(parentVO.getHkybje());
			// }
		}
		try {
			beforeActInf(vos, MESSAGE_SAVE);
			// 保存单据
			JKBXVO[] bxvos = getJKBXDAO().save(vos);

			// 调用现金流平台结算
			ErForCmpBO erBO = new ErForCmpBO();
			for (JKBXVO vo : vos) {
				BusiStatus billStatus = SettleUtil.getBillStatus(
						vo.getParentVO(), false);
				// 是否安装结算
				boolean isInstallCmp = SettleUtil
						.isCmpInstall(vo.getParentVO());

				// 是否 既无收款也无付款
				boolean isExistsPayOrRecv = isExistsPayOrRecv(vo.getParentVO());

				// 安装了结算、不是调整单、有收款或付款金额
				if (isInstallCmp && !vo.getParentVO().isAdjustBxd()
						&& isExistsPayOrRecv) {
					erBO.invokeCmp(vo, vo.getParentVO().getDjrq(), billStatus);
				}
			}

			afterActInf(vos, MESSAGE_SAVE);

			return bxvos;
		} catch (Exception e) {
			for (JKBXVO bxvo : vos) {
				returnBillCode(bxvo);
			}
			throw ExceptionHandler.handleException(e);
		}
	}

	private void sendOa(JKBXVO temp) throws BusinessException {
		// for (JKBXVO temp : bxvos) {
		JKBXHeaderVO hvo = temp.getParentVO();
		// String bill_type = temp.getParentVO().getPk_billtype();
		String transi_type = temp.getParentVO().getPk_billtype();
		String type = temp.getParentVO().getDjlxbm();
		// String dl = temp.getParentVO().getDjdl();
		// JKVO jkVO = (JKVO)temp;
		if ("4".equals(getDef2(hvo.getPk_org()))) {
			// Map<String, String> billTypeMap = billTypeMap();
			// if (billTypeMap.containsKey(type)) {
			OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hvo.getPk_org(), type);
			// ----------------------
			if (oaVo != null && oaVo.getIsdr() == 0) {
				String workFlowId = oaVo.getFlowid();
				String tableName = oaVo.getTablename();
				// 获取主表数据
				BXBusItemVO[] mxvos = (BXBusItemVO[]) temp.getChildrenVO();
				String szxm = "";
				if (mxvos != null && mxvos.length > 0) {
					BXBusItemVO mxvo = mxvos[0];
					// 收支项目
					szxm = (String) getHyPubBO().findColValue(
							"bd_inoutbusiclass",
							"name",
							"nvl(dr,0) = 0 and pk_inoutbusiclass ='"
									+ mxvo.getSzxmid() + "'");
				}

				JSONArray headData = getNewMainMap(hvo, transi_type, szxm);
				// 获取子表数据
				JSONArray bodyData = getNewDtaileDataMap(temp, tableName,
						transi_type);
				// 构造workflow信息
				WorkFlowBill bill = getFlowBill(temp, hvo, transi_type,
						workFlowId, oaVo.getBilltypename());
				// 调用OA工具类同步数据至OA
				OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
				// }
			}
		} else if ("1".equals(getDef2(hvo.getPk_org()))) {
			if (RL_CLFJK_002.equals(type)) {
				// 差旅费借款单
				JKVO jkVO = (JKVO) temp;
				JSONArray headData = JKBXOaUtil.getMainMap(jkVO.getParentVO());
				;
				JSONArray bodyData = JKBXOaUtil.getDtaileDataMap2(jkVO);
				/*
				 * step-1 调用OA工具类同步数据至OA
				 */
				OaWorkFlowUtil.sendOaData(headData, bodyData,
						getWorkFlowBill(temp));
			}
			if (RL_YGJE_001.equals(type)) {
				JKVO jkVO = (JKVO) temp;
				// 员工借款单
				JSONArray headData = JKBXOaUtil.getMainMap(jkVO.getParentVO());
				;
				JSONArray bodyData = JKBXOaUtil.getDtaileDataMap1(jkVO);
				/*
				 * step-1 调用OA工具类同步数据至OA
				 */
				OaWorkFlowUtil.sendOaData(headData, bodyData,
						getWorkFlowBill(temp));
			}

			if (RL_YGFYBX_003.equals(type)) {
				// 员工费用报销单
				BXVO bxVO = (BXVO) temp;
				JSONArray headData = JKBXOaUtil.getMainMap3(bxVO.getParentVO());
				;
				JSONArray bodyData = JKBXOaUtil.getDtaileDataMap3(bxVO);
				/*
				 * step-1 调用OA工具类同步数据至OA
				 */
				OaWorkFlowUtil.sendOaData(headData, bodyData,
						getWorkFlowBill(temp));
			}
			if (RL_RJLTF_004.equals(type)) {
				// 配套及热计量退费审
				BXVO bxVO = (BXVO) temp;
				JSONArray headData = JKBXOaUtil.getMainMap4(bxVO.getParentVO());
				;
				JSONArray bodyData = JKBXOaUtil.getDtaileDataMap4(bxVO);
				/*
				 * step-1 调用OA工具类同步数据至OA
				 */
				OaWorkFlowUtil.sendOaData(headData, bodyData,
						getWorkFlowBill(temp));
			}
			if (RL_CLFBX_005.equals(type)) {
				// *差旅费报销单
				BXVO bxVO = (BXVO) temp;
				JSONArray headData = JKBXOaUtil.getMainMap5(bxVO.getParentVO());
				;
				JSONArray bodyData = JKBXOaUtil.getDtaileDataMap5(bxVO);
				/*
				 * step-1 调用OA工具类同步数据至OA
				 */
				OaWorkFlowUtil.sendOaData(headData, bodyData,
						getWorkFlowBill(temp));
			}
			if (RL_HKD_006.equals(type)) {
				// 还款单
				BXVO bxVO = (BXVO) temp;
				JSONArray headData = JKBXOaUtil.getMainMap6(bxVO.getParentVO());
				;
				JSONArray bodyData = JKBXOaUtil.getDtaileDataMap6(bxVO);
				/*
				 * step-1 调用OA工具类同步数据至OA
				 */
				OaWorkFlowUtil.sendOaData(headData, bodyData,
						getWorkFlowBill(temp));
			}
			if (RL_JBCFBX_007.equals(type)) {
				// 加班餐费报销单
				BXVO bxVO = (BXVO) temp;
				JSONArray headData = JKBXOaUtil.getMainMap7(bxVO.getParentVO());
				;
				JSONArray bodyData = JKBXOaUtil.getDtaileDataMap7(bxVO);
				/*
				 * step-1 调用OA工具类同步数据至OA
				 */
				OaWorkFlowUtil.sendOaData(headData, bodyData,
						getWorkFlowBill(temp));
			}
			if (RL_CNFTUSP_008.equals(type)) {
				// 采暖费退费审批单
				BXVO bxVO = (BXVO) temp;
				JSONArray headData = JKBXOaUtil.getMainMap8(temp.getParentVO());
				;
				JSONArray bodyData = JKBXOaUtil.getDtaileDataMap8(temp);
				/*
				 * step-1 调用OA工具类同步数据至OA
				 */
				OaWorkFlowUtil.sendOaData(headData, bodyData,
						getWorkFlowBill(temp));
			}
			//
		}
		// 调用东港税务系统同步单据状态
		IArapForDGSWService util = (IArapForDGSWService) NCLocator
				.getInstance().lookup(IArapForDGSWService.class);
		// 单据号，组织主键，事件类型，单据类型
		JSONObject res = util.sendBillByNCBill(hvo.getPrimaryKey(),
				hvo.getPk_org(), CommonParam.COMMIT, hvo.getPk_billtype());
		if (!"Y".equals(res.getString("success"))) {
			throw new BusinessException("提交税务系统报错：" + res.getString("errinfo"));
		}
	}

	private WorkFlowBill getWorkFlowBill(JKBXVO temp) throws BusinessException {
		String where = " nvl(dr,0) = 0 and pk_bill = '"
				+ temp.getParentVO().getPk_jkbx() + "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
				.queryByCondition(WorkFlowBill.class, where);
		WorkFlowBill workFlowBill = new WorkFlowBill();
		// String type = temp.getParentVO().getPk_billtype();
		String transi_type = temp.getParentVO().getDjlxbm();
		if (null != workFlowBills && workFlowBills.length > 1) {
			throw new BusinessException("查询到多条流程记录");
		}
		if (null != workFlowBills && workFlowBills.length == 1) {
			if (RL_YGJE_001.equals(transi_type)) {
				workFlowBill = workFlowBills[0];
				workFlowBill.setWorkflowName("员工借款单");
			}
			if (RL_CLFJK_002.equals(transi_type)) {
				workFlowBill = workFlowBills[0];
				workFlowBill.setWorkflowName("差旅费借款单");
			}
			if (RL_YGFYBX_003.equals(transi_type)) {
				workFlowBill = workFlowBills[0];
				workFlowBill.setWorkflowName("员工费用报销单");
			}
			if (RL_RJLTF_004.equals(transi_type)) {
				workFlowBill = workFlowBills[0];
				workFlowBill.setWorkflowName("配套费、室内安装费及热计量退费审批单");
			}
			if (RL_CLFBX_005.equals(transi_type)) {
				workFlowBill = workFlowBills[0];
				workFlowBill.setWorkflowName("差旅费报销单");
			}
			if (RL_HKD_006.equals(transi_type)) {
				workFlowBill = workFlowBills[0];
				workFlowBill.setWorkflowName("还款单");
			}
			if (RL_JBCFBX_007.equals(transi_type)) {
				workFlowBill = workFlowBills[0];
				workFlowBill.setWorkflowName("加班餐费报销单");
			}
			if (RL_CNFTUSP_008.equals(transi_type)) {
				workFlowBill = workFlowBills[0];
				workFlowBill.setWorkflowName("采暖费退费审批单");
			}

		} else {
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, temp.getParentVO().getCreator());
			workFlowBill.setDef5(userVO.getUser_code());
			if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
				PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(
						PsndocVO.class, userVO.getPk_psndoc());
				workFlowBill.setDef5(psndoc.getId());
			}
			workFlowBill.setPk_group(temp.getParentVO().getPk_group());
			workFlowBill.setPk_org(temp.getParentVO().getPk_org());
			workFlowBill.setPk_bill(temp.getParentVO().getPk_jkbx());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(temp.getParentVO().getCreator());
			if (RL_YGJE_001.equals(transi_type)) {
				workFlowBill.setWorkflowName("员工借款单");
				workFlowBill.setBill_code("263X");
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					workFlowBill.setWorkflowId("390");
				} else {
					workFlowBill.setWorkflowId("109");
				}
			}
			if (RL_CLFJK_002.equals(transi_type)) {
				workFlowBill.setWorkflowName("差旅费借款单");
				workFlowBill.setBill_code("263X");
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					workFlowBill.setWorkflowId("391");
				} else {
					workFlowBill.setWorkflowId("108");
				}
			}
			if (RL_YGFYBX_003.equals(transi_type)) {
				workFlowBill.setWorkflowName("员工费用报销单");
				workFlowBill.setBill_code("264X");
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					workFlowBill.setWorkflowId("382");
				} else {
					workFlowBill.setWorkflowId("11");
				}
			}
			if (RL_RJLTF_004.equals(transi_type)) {
				workFlowBill.setWorkflowName("配套费、室内安装费及热计量退费审批单");
				workFlowBill.setBill_code("264X");
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					workFlowBill.setWorkflowId("378");
				} else {
					workFlowBill.setWorkflowId("132");
				}
			}
			if (RL_CLFBX_005.equals(transi_type)) {
				workFlowBill.setWorkflowName("差旅费报销单");
				workFlowBill.setBill_code("264X");
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					workFlowBill.setWorkflowId("389");
				} else {
					workFlowBill.setWorkflowId("82");
				}
			}
			if (RL_HKD_006.equals(transi_type)) {
				workFlowBill.setWorkflowName("还款单");
				workFlowBill.setBill_code("264X");
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					workFlowBill.setWorkflowId("536");
				} else {
					workFlowBill.setWorkflowId("89");
				}
			}
			if (RL_JBCFBX_007.equals(transi_type)) {
				workFlowBill.setWorkflowName("加班餐费报销单");
				workFlowBill.setBill_code("264X");
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					workFlowBill.setWorkflowId("383");
				} else {
					workFlowBill.setWorkflowId("90");
				}
			}
			if (RL_CNFTUSP_008.equals(transi_type)) {
				workFlowBill.setWorkflowName("采暖费退费审批单");
				workFlowBill.setBill_code("264X");
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					workFlowBill.setWorkflowId("376");
				} else {
					workFlowBill.setWorkflowId("118");
				}
			}

		}
		return workFlowBill;
	}

	/*   */
	/*   */private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}

	private void checkForInitBill(JKBXVO[] vos) throws BusinessException {
		for (JKBXVO vo : vos) {
			if (vo.getParentVO().isInit()) {
				JKBXHeaderVO header = vo.getParentVO();
				String djlxbm = header.getDjlxbm();
				String pk_org = header.getPk_org();
				String pk_group = header.getPk_group();
				String pk = "ER_JKBXINIT" + djlxbm + pk_org;
				String user = KeyLock.dynamicLock(pk);

				if (user != null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("expensepub_0",
									"02011002-0060")/*
													 * @res
													 * "并发异常，保存常用单据失败，该单据类型的常用单据在当前单位已经存在！"
													 */);
				}

				DjCondVO condVO = new DjCondVO();
				UFBoolean isGroup = vo.getParentVO().getIsinitgroup();
				if (isGroup.booleanValue()) {
					condVO.defWhereSQL = " zb.djlxbm='" + djlxbm
							+ "' and zb.dr=0 and zb.isinitgroup='" + isGroup
							+ "'";
					condVO.pk_group = new String[] { pk_group };
				} else {
					condVO.defWhereSQL = " zb.djlxbm='" + djlxbm
							+ "' and zb.dr=0 and zb.isinitgroup='" + isGroup
							+ "'";
					condVO.pk_org = new String[] { pk_org };
				}
				condVO.isInit = true;
				condVO.isCHz = false;
				List<JKBXVO> svos = NCLocator.getInstance()
						.lookup(IBXBillPrivate.class).queryVOs(0, 1, condVO);

				if (svos != null && svos.size() > 0) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("expensepub_0",
									"02011002-0060")/*
													 * @res
													 * "并发异常，保存常用单据失败，该单据类型的常用单据在当前单位已经存在！"
													 */);
				}
			}
		}
	}

	/**
	 * 暂存业务处理，不进行任何业务校验，不调用其他模块接口
	 * 
	 * @param vos
	 *            要暂存单据的聚合VO数组
	 */
	public JKBXVO[] tempSave(JKBXVO[] vos) throws BusinessException {
		// 常用单据的并发校验
		checkForInitBill(vos);

		for (JKBXVO vo : vos) {
			BXUtil.generateJKBXRow(vo);
			VOChecker.prepare(vo);

			// 保存后自动提交
			vo.getParentVO().setSpzt(IBillStatus.FREE);
			vo.getParentVO().setDjzt(BXStatusConst.DJZT_TempSaved);

			new VOChecker().checkkSaveBackground(vo);
			// 特殊处理摘要
			dealZyName(vo);

			// 设置审计信息
			if (vo.getParentVO().getPrimaryKey() != null) {
				AuditInfoUtil.updateData(vo.getParentVO());
			} else {
				AuditInfoUtil.addData(vo.getParentVO());
			}
		}

		beforeActInf(vos, MESSAGE_TEMP_SAVE);

		JKBXVO[] bxvos;
		try {
			bxvos = getJKBXDAO().save(vos);
		} catch (SQLException e) {
			throw ExceptionHandler.handleException(e);
		}

		afterActInf(vos, MESSAGE_TEMP_SAVE);

		return bxvos;
	}

	public JKBXVO[] update(JKBXVO[] vos) throws BusinessException {
		// 校验时间戳
		compareTs(vos);
		for (JKBXVO vo : vos) {
			// 特殊处理摘要
			dealZyName(vo);

			// 查询修改前的vo
			if (vo.getBxoldvo() == null) {
				List<JKBXVO> oldvo = null;
				oldvo = NCLocator
						.getInstance()
						.lookup(IBXBillPrivate.class)
						.queryVOsByPrimaryKeysForNewNode(
								new String[] { vo.getParentVO().getPrimaryKey() },
								vo.getParentVO().getDjdl(),
								vo.getParentVO().isInit(), null);
				vo.setBxoldvo(oldvo.get(0));
				// 补齐children信息（因前台传过来的的只是改变的children）
				fillUpChildren(vo, oldvo.get(0));
				// 同样补齐CShareDetailVO信息
				fillUpCShareDetail(vo, oldvo.get(0));

			}
			// 只录表头，不录表体的情况下生成表体行
			BXUtil.generateJKBXRow(vo);

			VOChecker voChecker = new VOChecker();
			// 20180203 dingyma，下面的代码会造成NC端修改报错，与舒展商议后去掉
			// voChecker.checkInvalid(vo.getBxoldvo());//校验作废后单据不能提交
			VOChecker.prepare(vo);
			// 后台校验
			voChecker.checkUpdateSave(vo);

			// 补齐申请单申请记录(预算控制中用到)
			fillUpMapf(vo);
			fillUpMapf(vo.getBxoldvo());

			// 常用单据单据,暂存态单据不校验关帐
			boolean isChkCloseAcc = true;
			JKBXHeaderVO parentVO = vo.getParentVO();
			if (parentVO != null
					&& (parentVO.isInit() || BXStatusConst.DJZT_TempSaved == parentVO
							.getDjzt())) {
				isChkCloseAcc = false;
			}
			if (isChkCloseAcc) {
				VOChecker.checkErmIsCloseAcc(vo);
			}
			if (vo.getParentVO().getDjzt() != BXStatusConst.DJZT_TempSaved) {
				if (vo.getParentVO().getQcbz() != null
						&& vo.getParentVO().getQcbz().booleanValue()) {
					vo.getParentVO().setSpzt(IPfRetCheckInfo.PASSING);
					vo.getParentVO().setShrq(
							new UFDateTime(vo.getParentVO().getDjrq(),
									new UFTime("00:00:00")));
					vo.getParentVO().setJsrq(vo.getParentVO().getDjrq());
					vo.getParentVO().setApprover(
							InvocationInfoProxy.getInstance().getUserId());
					vo.getParentVO().setJsr(
							InvocationInfoProxy.getInstance().getUserId());
				}
			}

			// 取服务器事件作为修改时间
			AuditInfoUtil.updateData(vo.getParentVO());
		}

		try {
			// 事件前
			beforeActInf(vos, MESSAGE_UPDATE);

			// 报销单修改保存
			JKBXVO[] bxvos = getJKBXDAO().update(vos);

			// 调用现金流平台结算
			ErForCmpBO erForCmpBO = new ErForCmpBO();
			for (JKBXVO vo : vos) {

				// 当前结算信息状态
				BusiStatus billStatus = SettleUtil.getBillStatus(
						vo.getParentVO(), false);

				// 当前结算信息状态
				BusiStatus oldBillStatus = SettleUtil.getBillStatus(vo
						.getBxoldvo().getParentVO(), false);

				// CMP产品是否启用
				boolean isInstallCmp = SettleUtil.isCmpInstall(vo.getBxoldvo()
						.getParentVO());

				// 是否暂存单据修改
				boolean isTmpSave = BXStatusConst.DJZT_TempSaved == vo
						.getParentVO().getDjzt();

				// 是否不存在收付款
				boolean isExistsPayOrRecv = isExistsPayOrRecv(vo.getParentVO());

				if (!vo.getParentVO().isAdjustBxd()) {// 不是调整单
					if (oldBillStatus == BusiStatus.Save
							&& billStatus == BusiStatus.Deleted) {
						// 修改场景：单据修改全额冲借款，支付单位修改会造成原结算信息不能被删除，这里按oldVO进行删除
						// 资金逻辑不清楚，pk_org修改后就不能删除结算信息，明明记录了报销单的pk，不按pk进行删
						if (isInstallCmp && !isTmpSave) {
							erForCmpBO.invokeCmp(vo.getBxoldvo(), vo
									.getBxoldvo().getParentVO().getDjrq(),
									billStatus);
						}
					} else {
						if (isInstallCmp && !isTmpSave && isExistsPayOrRecv) {
							erForCmpBO.invokeCmp(vo,
									vo.getParentVO().getDjrq(), billStatus);
						}
					}
				}
			}

			afterActInf(vos, MESSAGE_UPDATE);

			// 清空补齐的数据
			for (JKBXVO vo : vos) {
				vo.setBxoldvo(null);
			}
			return bxvos;
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	/**
	 * 补充申请记录
	 * 
	 * @param bxVo
	 * @throws BusinessException
	 */
	private void fillUpMapf(JKBXVO bxVo) throws BusinessException {
		// 申请单处理
		MtapppfVO[] pfVos = MtappfUtil
				.getMaPfVosByJKBXVo(new JKBXVO[] { bxVo });
		bxVo.setMaPfVos(pfVos);

		if (bxVo instanceof BXVO) {
			MtapppfVO[] contrastPfs = MtappfUtil
					.getContrastMaPfVos(new JKBXVO[] { bxVo });
			bxVo.setContrastMaPfVos(contrastPfs);
		}
	}

	private void fillUpCShareDetail(JKBXVO vo, JKBXVO oldvo) {
		List<CShareDetailVO> result = new ArrayList<CShareDetailVO>();
		List<String> pkList = new ArrayList<String>();
		CShareDetailVO[] changedCShareVO = vo.getcShareDetailVo();
		CShareDetailVO[] oldCShareVO = oldvo.getcShareDetailVo();
		if (changedCShareVO == null || oldCShareVO == null) {
			return;
		}
		// 处理新增和修改和删除的行，只将 新增和修改的放到结果中
		for (int i = 0; i < changedCShareVO.length; i++) {
			if (changedCShareVO[i].getStatus() != VOStatus.DELETED) {
				result.add(changedCShareVO[i]);
			}
			pkList.add(changedCShareVO[i].getPrimaryKey());
		}
		for (int i = 0; i < oldCShareVO.length; i++) {
			if (!pkList.contains(oldCShareVO[i].getPrimaryKey())) {
				oldCShareVO[i].setStatus(VOStatus.UNCHANGED);
				result.add(oldCShareVO[i]);
			}
		}
		vo.setcShareDetailVo(result.toArray(new CShareDetailVO[] {}));
	}

	/**
	 * 补充业务页签
	 * 
	 * @param vo
	 * @param oldvo
	 */
	private void fillUpChildren(JKBXVO vo, JKBXVO oldvo) {
		List<BXBusItemVO> result = new ArrayList<BXBusItemVO>();
		oldvo = (JKBXVO) oldvo.clone();
		List<String> pkList = new ArrayList<String>();
		BXBusItemVO[] changedChildren = vo.getChildrenVO();
		BXBusItemVO[] oldChildren = oldvo.getChildrenVO();

		// 处理新增和修改和删除的行，只将 新增和修改的放到结果中
		for (int i = 0; i < changedChildren.length; i++) {
			if (changedChildren[i].getStatus() != VOStatus.DELETED) {
				result.add(changedChildren[i]);
			}
			pkList.add(changedChildren[i].getPrimaryKey());
		}
		for (int i = 0; i < oldChildren.length; i++) {
			if (!pkList.contains(oldChildren[i].getPrimaryKey())) {
				oldChildren[i].setStatus(VOStatus.UNCHANGED);
				result.add(oldChildren[i]);
			}

		}

		Collections.sort(result, new Comparator<BXBusItemVO>() {
			@Override
			public int compare(BXBusItemVO item1, BXBusItemVO item2) {
				if (item1.getRowno() == null && item2.getRowno() == null) {
					return 0;
				} else if (item1.getRowno() != null && item2.getRowno() == null) {
					return -1;
				} else if (item1.getRowno() == null && item2.getRowno() != null) {
					return 1;
				}
				return item1.getRowno().compareTo(item2.getRowno());
			}
		});

		vo.setChildrenVO(result.toArray(new BXBusItemVO[] {}));
	}

	public MessageVO[] unAudit(JKBXVO[] vos) throws BusinessException {

		if (vos == null || vos.length < 1)
			return null;

		MessageVO[] msgs = new MessageVO[vos.length];

		compareTs(vos);

		try {
			for (int i = 0; i < vos.length; i++) {

				try {
					unAuditBack(vos[i]);

					msgs[i] = new MessageVO(vos[i], ActionUtils.UNAUDIT);
				} catch (BusinessException e) {
					msgs[i] = new MessageVO(vos[i], ActionUtils.UNAUDIT, false,
							e.getMessage());
					ExceptionHandler.handleException(e);
				}
			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return msgs;

	}

	private void unAuditBack(JKBXVO bxvo) throws BusinessException {
		// 查表体信息
		bxvo = retriveItems(bxvo);
		JKBXHeaderVO headerVO = bxvo.getParentVO();
		VOStatusChecker.checkUnAuditStatus(headerVO);
		new VOChecker().checkUnAudit(bxvo);

		// 判断CMP产品是否启用
		BusiStatus billStatus = SettleUtil.getBillStatus(headerVO, false,
				BusiStatus.Save);
		if (billStatus.equals(BusiStatus.Deleted)) {
			// 没有结算信息的单据直接反生效
			unSettle(new JKBXVO[] { bxvo });
			// 删除凭证
			effectToFip(bxvo, MESSAGE_UNSETTLE);
		} else {
			boolean isCmpInstalled = SettleUtil.isCmpInstall(headerVO);
			UFDate shrq = headerVO.getShrq() == null ? null : headerVO
					.getShrq().getDate();
			if (!isCmpInstalled) {
				unSettle(new JKBXVO[] { bxvo });
				// 删除凭证
				effectToFip(bxvo, MESSAGE_UNSETTLE);
			} else {
				// 安装了结算的反审核
				new ErForCmpBO().invokeCmp(bxvo, shrq, billStatus);
			}
		}

		headerVO.setSxbz(BXStatusConst.SXBZ_NO);
		headerVO.setDjzt(Integer.valueOf(BXStatusConst.DJZT_Saved));

		// begin--清空结算人，结算日期
		headerVO.setJsr(null);
		headerVO.setJsrq(null);
		headerVO.shrq_show = null;
		headerVO.setPayflag(BXStatusConst.PAYFLAG_None);
		headerVO.setPayman(null);
		headerVO.setPaydate(null);
		headerVO.setVouchertag(null);

		// 工作流不配审批流时出现情况
		if (headerVO.getApprover() == null
				|| headerVO.getApprover().equals(INCSystemUserConst.NC_USER_PK)) {
			headerVO.setApprover(null);
			headerVO.setShrq(null);
		}

		try {
			beforeActInf(bxvo, MESSAGE_UNAUDIT);

			getJKBXDAO().update(
					new JKBXHeaderVO[] { headerVO },
					new String[] { JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ,
							JKBXHeaderVO.SPZT, JKBXHeaderVO.VOUCHERTAG,
							JKBXHeaderVO.APPROVER, JKBXHeaderVO.SHRQ,
							JKBXHeaderVO.JSR, JKBXHeaderVO.JSRQ,
							JKBXHeaderVO.PAYDATE, JKBXHeaderVO.PAYFLAG,
							JKBXHeaderVO.PAYMAN });

			// 重新加载冲销行表体（带出冲销行生效日期）
			if (bxvo.getContrastVO() != null && bxvo.getContrastVO().length > 0) {
				Collection<BxcontrastVO> contrasts = queryContrasts(bxvo
						.getParentVO());
				bxvo.setContrastVO(contrasts.toArray(new BxcontrastVO[] {}));
			}
			// 重新加载核销预提明细
			if (bxvo.getAccruedVerifyVO() != null
					&& bxvo.getAccruedVerifyVO().length > 0) {
				Collection<AccruedVerifyVO> accruedVerifyVOs = queryAccruedVerifyVOS(bxvo
						.getParentVO());
				bxvo.setAccruedVerifyVO(accruedVerifyVOs
						.toArray(new AccruedVerifyVO[] {}));
			}

			afterActInf(bxvo, MESSAGE_UNAUDIT);

		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	private JKBXVO retriveItems(JKBXVO bxvo) throws BusinessException {

		if ((bxvo.getChildrenVO() == null || bxvo.getChildrenVO().length == 0)) {
			JKBXVO resultBxvo = new ArapBXBillPrivateImp().retriveItems(bxvo
					.getParentVO());
			bxvo.setChildrenVO(resultBxvo.getChildrenVO());
			bxvo.setcShareDetailVo(resultBxvo.getcShareDetailVo());
			bxvo.setContrastVO(resultBxvo.getContrastVO());
			bxvo.setAccruedVerifyVO(resultBxvo.getAccruedVerifyVO());
		}

		return bxvo;
	}

	public MessageVO[] audit(JKBXVO[] vos) throws BusinessException {

		if (vos == null || vos.length < 1)
			return null;

		MessageVO[] msgs = new MessageVO[vos.length];

		compareTs(vos);

		try {
			for (int i = 0; i < vos.length; i++) {

				auditBack(vos[i]);

				msgs[i] = new MessageVO(vos[i], ActionUtils.AUDIT);
			}
		} catch (Exception e) {
			if (e instanceof nc.vo.cmp.exception.CmpAuthorizationException) {
				ErmMpCmpException cmpExcption = new ErmMpCmpException(
						e.getMessage(), e);
				cmpExcption
						.setCmpException((nc.vo.cmp.exception.CmpAuthorizationException) e);
				throw cmpExcption;
			}
			ExceptionHandler.handleException(e);
		}

		return msgs;
	}

	/**
	 * 审核，单据状态转为已审核或已签字状态
	 * 
	 * @param bxvo
	 * @throws BusinessException
	 * @throws SQLException
	 */
	private void auditBack(JKBXVO bxvo) throws BusinessException, SQLException {

		bxvo = retriveItems(bxvo);

		JKBXHeaderVO headerVO = bxvo.getParentVO();

		// 如果报销单全部用来冲借款后，审批后，支付状态应该设置为：全部冲借款
		if (headerVO.isAdjustBxd()) {
			headerVO.setPayflag(BXStatusConst.PAYFLAG_ADJUST);
		} else if (headerVO.getZfybje().doubleValue() == 0
				&& headerVO.getHkybje().doubleValue() == 0) {
			headerVO.setPayflag(BXStatusConst.ALL_CONTRAST);
		}

		if (headerVO.getApprover() == null) {// 审核人为空时，设置默认值
			headerVO.setApprover(INCSystemUserConst.NC_USER_PK);
			headerVO.setShrq(AuditInfoUtil.getCurrentTime());
		}

		VOStatusChecker.checkAuditStatus(headerVO, headerVO.getShrq());

		headerVO.setSpzt(IPfRetCheckInfo.PASSING);
		headerVO.setDjzt(Integer.valueOf(BXStatusConst.DJZT_Verified));

		beforeActInf(bxvo, MESSAGE_AUDIT);
		// 需更新字段
		String[] updateFields = new String[] { JKBXHeaderVO.SPZT,
				JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ, JKBXHeaderVO.APPROVER,
				JKBXHeaderVO.SHRQ, JKBXHeaderVO.VOUCHERTAG,
				JKBXHeaderVO.PAYFLAG };

		dealToFip(bxvo, headerVO, updateFields);

		try {
			// 如果自动结算处理
			List<JKBXHeaderVO> headerVOs = NCLocator
					.getInstance()
					.lookup(IBXBillPrivate.class)
					.queryHeadersByPrimaryKeys(
							new String[] { headerVO.getPk_jkbx() },
							headerVO.getDjdl());

			if (SettleUtil.isAutoJS(headerVO)) {// 自动结算（没有安装现金或交易类型勾选了自动结算）
				if (isExistsPayOrRecv(headerVO) && !headerVO.isAdjustBxd()
						&& SettleUtil.isCmpInstall(headerVO)) {// 是否安装现金
					headerVO.setVouchertag(headerVOs.get(0).getVouchertag());// 设置到原来的数据中，返回时设置到界面上
					headerVO.setPayflag(headerVOs.get(0).getPayflag());
					headerVO.setPaydate(headerVOs.get(0).getPaydate());
					headerVO.setPayman(headerVOs.get(0).getPayman());

					// 支付成功，表示已经自动结算，自动结算仅为手工结算，必支付完成
					if (headerVO.getPayflag() != null
							&& headerVO.getPayflag().intValue() == BXStatusConst.PAYFLAG_PayFinish) {
						updateFields = new String[] { JKBXHeaderVO.SPZT,
								JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ,
								JKBXHeaderVO.APPROVER, JKBXHeaderVO.SHRQ };
					}
				} else {
					// 没有安装现金，自动结算，则支付状态设置为支付完成
					if (headerVO.getPayflag() == null
							|| !(headerVO.getPayflag() == BXStatusConst.ALL_CONTRAST || headerVO
									.getPayflag() == BXStatusConst.PAYFLAG_ADJUST)) {
						headerVO.setPayflag(BXStatusConst.PAYFLAG_PayFinish);
						headerVO.setPaydate(new UFDate(InvocationInfoProxy
								.getInstance().getBizDateTime()));
						headerVO.setPayman(InvocationInfoProxy.getInstance()
								.getUserId());
					}

					updateFields = new String[] { JKBXHeaderVO.SPZT,
							JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ,
							JKBXHeaderVO.APPROVER, JKBXHeaderVO.SHRQ,
							JKBXHeaderVO.PAYFLAG, JKBXHeaderVO.PAYDATE,
							JKBXHeaderVO.PAYMAN, JKBXHeaderVO.VOUCHERTAG };
				}
			}

			// 生效标志（安装结算的时候，结算回调设置effectVo()
			headerVO.setSxbz(headerVOs.get(0).getSxbz());

			// 更新数据库
			getJKBXDAO().update(new JKBXHeaderVO[] { headerVO }, updateFields);

			// 重新加载冲销行表体（带出冲销行生效日期）
			if (bxvo.getContrastVO() != null && bxvo.getContrastVO().length > 0) {
				Collection<BxcontrastVO> contrasts = queryContrasts(bxvo
						.getParentVO());
				bxvo.setContrastVO(contrasts.toArray(new BxcontrastVO[] {}));
			}
			// 重新加载核销预提明细
			if (bxvo.getAccruedVerifyVO() != null
					&& bxvo.getAccruedVerifyVO().length > 0) {
				Collection<AccruedVerifyVO> accruedVerifyVOs = queryAccruedVerifyVOS(bxvo
						.getParentVO());
				bxvo.setAccruedVerifyVO(accruedVerifyVOs
						.toArray(new AccruedVerifyVO[] {}));
			}

			afterActInf(bxvo, MESSAGE_AUDIT);

		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		}
	}

	/**
	 * 处理传会计平台：需要根据现金管理的参数
	 * 
	 * @param bxvo
	 * @param headerVO
	 * @param isAutoSign
	 * @param isCmpInstalled
	 * @param updateFields
	 * @throws DAOException
	 * @throws SQLException
	 * @throws BusinessException
	 */
	private void dealToFip(JKBXVO bxvo, JKBXHeaderVO headerVO,
			String[] updateFields) throws DAOException, SQLException,
			BusinessException {

		// 全额冲销的情况和调整单特殊处理凭证标志字段
		if ((headerVO.getVouchertag() == null && (headerVO.getPayflag() != null && headerVO
				.getPayflag() == BXStatusConst.ALL_CONTRAST))
				|| headerVO.isAdjustBxd()) {
			headerVO.setVouchertag(BXStatusConst.SXFlag);
		}

		// 单据是否自动签字(受参数控制)
		boolean isAutoSign = SettleUtil.isAutoSign(headerVO);
		if (isAutoSign) {
			// 1.状态置为已审核，结算单据
			headerVO.setJsr(headerVO.getApprover());
			headerVO.setJsrq(headerVO.getShrq().getDate());
			// 自动签字
			headerVO.setDjzt(Integer.valueOf(BXStatusConst.DJZT_Sign));

			// 是否结算传会计平台
			boolean isJsToFip = SettleUtil.isJsToFip(headerVO);
			if (!isJsToFip && headerVO.getVouchertag() == null) {
				headerVO.setVouchertag(BXStatusConst.SXFlag);// 自动签字时设置该字段
			}

			getJKBXDAO().update(new JKBXHeaderVO[] { headerVO }, updateFields);
		}

		// 是否有结算信息(根据支付，还款金额判断)
		BusiStatus billStatus = SettleUtil.getBillStatus(headerVO, false,
				BusiStatus.Audit);
		if (!billStatus.equals(BusiStatus.Deleted)
				&& SettleUtil.isCmpInstall(headerVO)) {
			new ErForCmpBO().invokeCmp(bxvo, headerVO.getShrq().getDate(),
					billStatus);
		} else {// 没有结算信息的单据直接签字生效
			autoSignDeal(bxvo, headerVO);
		}
	}

	/**
	 * 不是自动签字时：没有结算信息或没有安装结算时的处理
	 * 
	 * @param bxvo
	 * @param headerVO
	 * @param param
	 * @throws BusinessException
	 */
	private void notAutoSignDeal(JKBXVO bxvo, JKBXHeaderVO headerVO,
			boolean isJsToFip) throws BusinessException {
		settle(headerVO.getApprover(), headerVO.getShrq().getDate(), bxvo);
		// 传会计平台
		if ((headerVO.getVouchertag() == null || headerVO.getVouchertag() == BXStatusConst.SXFlag)
				&& (!isJsToFip
						|| (headerVO.getPayflag() != null && headerVO
								.getPayflag() == BXStatusConst.ALL_CONTRAST) || headerVO
							.isAdjustBxd())) {
			bxvo.getParentVO().setVouchertag(BXStatusConst.SXFlag);
			effectToFip(bxvo, MESSAGE_SETTLE);
		}
	}

	/**
	 * 自动签字时：没有结算信息或没有安装结算时的处理
	 * 
	 * @param bxvo
	 * @param headerVO
	 * @param param
	 * @throws BusinessException
	 */
	private void autoSignDeal(JKBXVO bxvo, JKBXHeaderVO headerVO)
			throws BusinessException {
		settle(headerVO.getApprover(), headerVO.getShrq().getDate(), bxvo);
		// 传会计平台
		bxvo.getParentVO().setVouchertag(BXStatusConst.SXFlag);

		effectToFip(bxvo, MESSAGE_SETTLE);
	}

	/**
	 * 反生效 + 反签字（按时间顺序应该是先反生效后反签字）
	 * 
	 * @param vos
	 *            借款报销VOs
	 * @return
	 * @throws BusinessException
	 */
	public JKBXVO[] unSettle(JKBXVO[] vos) throws BusinessException {
		unEffectVos(vos);// 反生效
		unSignVo(vos);// 反签字
		return vos;
	}

	/**
	 * 后台进行反结算操作
	 * 
	 * @param head
	 * @throws BusinessException
	 * 
	 */
	public void unSettleBack(JKBXHeaderVO head) throws BusinessException {
		VOStatusChecker.checkUnSettleStatus(head);
		JKBXVO bxvo = VOFactory.createVO(head);
		unSettle(new JKBXVO[] { bxvo });
	}

	/**
	 * 签字+生效 此方法处理生效，借款控制，预算控制，传收付
	 * 
	 * @param jsr
	 * @param jsrq
	 * @param vo
	 * @throws BusinessException
	 * 
	 */
	public void settle(String jsr, UFDate jsrq, JKBXVO vo)
			throws BusinessException {
		// 委托付款时，签字时，只回写签字信息，单据并不生效
		signVo(jsr, jsrq, vo);// 签字
		effectVo(vo);// 生效
	}

	/**
	 * 单据签字
	 * 
	 * @param jsr
	 *            签字人
	 * @param jsrq
	 *            签字日期
	 * @param vo
	 * @throws BusinessException
	 * 
	 */
	public JKBXVO signVo(String jsr, UFDate jsrq, JKBXVO vo)
			throws BusinessException {
		// 校验ts
		compareTs(new JKBXVO[] { vo });

		JKBXHeaderVO head = vo.getParentVO();
		VOStatusChecker.checkSettleStatus(head, jsrq);

		head.setJsr(jsr);
		head.setJsrq(jsrq);
		head.setDjzt(BXStatusConst.DJZT_Sign);

		// 更新vo信息
		updateHeaders(new JKBXHeaderVO[] { head }, new String[] {
				JKBXHeaderVO.DJZT, JKBXHeaderVO.JSR, JKBXHeaderVO.JSRQ });
		return vo;
	}

	/**
	 * 单据生效
	 * 
	 * @param vo
	 *            借款报销VO
	 * @throws BusinessException
	 * 
	 */
	public JKBXVO effectVo(JKBXVO vo) throws BusinessException {
		// 校验ts
		compareTs(new JKBXVO[] { vo });

		JKBXHeaderVO head = vo.getParentVO();

		// 补充信息
		addBxExtralInfo(vo);
		fillUpMapf(vo);

		head.setDjzt(BXStatusConst.DJZT_Sign);
		head.setSxbz(BXStatusConst.SXBZ_VALID);

		// 处理前插件动作(预算控制)
		beforeActInf(new JKBXVO[] { vo }, MESSAGE_SETTLE);

		// 更新vo信息
		updateHeaders(new JKBXHeaderVO[] { head }, new String[] {
				JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ });

		// 核销预提明细生效处理
		new BxVerifyAccruedBillBO().effectAccruedVerifyVOs(vo);

		// 处理后插件动作
		afterActInf(new JKBXVO[] { vo }, MESSAGE_SETTLE);

		// 传收付
		transferArap(vo);

		return vo;
	}

	/**
	 * 借款单、报销单据反签字
	 * 
	 * @param vos
	 *            借款VO或报销VO
	 * @throws BusinessException
	 * 
	 * @return 更新后VOs
	 */
	public JKBXVO[] unSignVo(JKBXVO[] vos) throws BusinessException {

		List<JKBXHeaderVO> headVoList = new ArrayList<JKBXHeaderVO>();
		for (JKBXVO vo : vos) {
			// 校验ts
			compareTs(new JKBXVO[] { vo });

			JKBXHeaderVO head = vo.getParentVO();
			head.setJsr(null);
			// wangyl7 2018年1月20日14:28:38 合并取消结算时传传预算生效日期补丁
			// head.setJsrq(null);
			head.setDjzt(BXStatusConst.DJZT_Verified);

			headVoList.add(head);
		}

		// 更新vo信息
		updateHeaders(headVoList.toArray(new JKBXHeaderVO[] {}), new String[] {
				JKBXHeaderVO.DJZT, JKBXHeaderVO.JSR,
		/* JKBXHeaderVO.JSRQ */});

		return vos;
	}

	/**
	 * 反生效
	 * 
	 * @param vos
	 *            借款报销VOs
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public JKBXVO[] unEffectVos(JKBXVO[] vos) throws BusinessException {

		compareTs(vos);
		try {
			String[] keys = new String[vos.length];
			JKBXHeaderVO[] headers = new JKBXHeaderVO[vos.length];

			for (int i = 0; i < vos.length; i++) {
				JKBXHeaderVO parentVO = vos[i].getParentVO();
				keys[i] = parentVO.getPk_jkbx();

				vos[i].setBxoldvo((JKBXVO) vos[i].clone());
				headers[i] = parentVO;

				// 补充信息
				addBxExtralInfo(vos[i]);
				fillUpMapf(vos[i]);
			}

			// 校验反签字信息
			checkUnSettle(keys, headers);

			beforeActInf(vos, MESSAGE_UNSETTLE);

			for (int i = 0; i < vos.length; i++) {
				headers[i].setSxbz(BXStatusConst.SXBZ_NO);
				headers[i].setJsrq(null);
			}

			updateHeaders(headers, new String[] { JKBXHeaderVO.SXBZ,
					JKBXHeaderVO.JSRQ });

			// 取消传收付
			unTransferArap(keys);

			// 核销预提明细取消生效处理
			new BxVerifyAccruedBillBO().uneffectAccruedVerifyVOs(vos);

			afterActInf(vos, MESSAGE_UNSETTLE);

			return vos;

		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		} catch (SQLException e) {
			ExceptionHandler.handleException(e);
		}
		return null;
	}

	/**
	 * 提交
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public JKBXVO commitVO(JKBXVO vo) throws BusinessException {
		if (vo == null) {
			return null;
		}

		// 单据提交影像扫描检查
		// boolean isInstalledIamge =
		// ImageCheckUtil.isInstalledIamge(vo);//是否安装影像扫描
		// if(isInstalledIamge){
		// new VOChecker().checkImage(vo);
		// }
		// 加锁,版本校验
		compareTs(new JKBXVO[] { vo });

		// 校验
		VOStatusChecker.checkCommitStatus(vo.getParentVO());

		// 设置审批状态
		vo.getParentVO().setSpzt(IBillStatus.COMMIT);

		// 更新vo信息
		updateHeaders(new JKBXHeaderVO[] { vo.getParentVO() },
				new String[] { JKBXHeaderVO.SPZT });
		sendOa(vo);

		// 返回
		return vo;
	}

	/**
	 * 收回
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public JKBXVO recallVO(JKBXVO vo) throws BusinessException {
		if (vo == null) {
			return null;
		}

		// 加锁,版本校验
		compareTs(new JKBXVO[] { vo });

		// 校验
		VOStatusChecker.checkRecallStatus(vo.getParentVO());

		// 设置审批状态
		vo.getParentVO().setSpzt(IBillStatus.FREE);

		// 更新vo信息
		updateHeaders(new JKBXHeaderVO[] { vo.getParentVO() },
				new String[] { JKBXHeaderVO.SPZT });

		OaWorkFlowUtil.backOaWorkFlow(vo.getParentVO().getPrimaryKey());
		// 调用东港税务系统同步单据状态
		IArapForDGSWService util = (IArapForDGSWService) NCLocator
				.getInstance().lookup(IArapForDGSWService.class);
		// 单据号，组织主键，事件类型，单据类型
		JSONObject res = util.sendBillByNCBill(
				vo.getParentVO().getPrimaryKey(), vo.getParentVO().getPk_org(),
				CommonParam.RECALL, vo.getParentVO().getPk_billtype());
		if (!"Y".equals(res.getString("success"))) {
			throw new BusinessException(res.getString("errinfo"));
		}
		// 返回
		return vo;
	}

	/**
	 * 单据作废
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public JKBXVO invalidBill(JKBXVO vo) throws BusinessException {
		if (vo == null) {
			return null;
		}

		// 加锁,版本校验
		compareTs(new JKBXVO[] { vo });

		// 校验，仅保存的单据可以进行废弃
		new VOChecker().checkInvalid(vo);
		// 拉申请的报销单作废没有释放申请单预算 2017增强包 2018-01-20 add sj
		fillUpMapf(vo);

		// 处理前插件动作
		beforeActInf(new JKBXVO[] { vo }, MESSAGE_INVALID);

		// 作废状态
		vo.getParentVO().setDjzt(BXStatusConst.DJZT_Invalid);
		// 取服务器事件作为修改时间
		AuditInfoUtil.updateData(vo.getParentVO());

		// 申请单信息清除
		vo.getParentVO().setPk_item(null);

		// 更新
		updateHeaders(new JKBXHeaderVO[] { vo.getParentVO() }, new String[] {
				JKBXHeaderVO.DJZT, JKBXHeaderVO.MODIFIER,
				JKBXHeaderVO.MODIFIEDTIME, JKBXHeaderVO.PK_ITEM });

		// 表体处理
		if (vo.getChildrenVO() != null && vo.getChildrenVO().length > 0) {
			BXBusItemVO[] childrenVos = vo.getChildrenVO();
			for (BXBusItemVO item : childrenVos) {
				// 申请单处理一定要放到时间前处理事件中处理
				item.setPk_item(null);// 申请单信息清除
				item.setPk_mtapp_detail(null);
			}
			new BXBusItemBO().update(childrenVos);
		}

		// 判断CMP产品是否启用
		boolean isCmpInstalled = SettleUtil.isCmpInstall(vo.getParentVO());

		if (isCmpInstalled) {
			// 删除单据的结算信息
			new ErForCmpBO().invokeCmp(vo, vo.getParentVO().getDjrq(),
					BusiStatus.Deleted);
		}

		// 删除冲借款对照信息
		try {
			new ContrastBO().deleteByPK_bxd(new String[] { vo.getParentVO()
					.getPk_jkbx() });
			vo.setContrastVO(null);
		} catch (SQLException e) {
			ExceptionHandler.handleException(e);
		}

		// 删除报销核销 预提明细
		new BxVerifyAccruedBillBO().deleteByBxdPks(vo.getParentVO()
				.getPk_jkbx());
		vo.setAccruedVerifyVO(null);

		// 补充表体
		retriveItems(vo);

		// 处理前插件动作
		afterActInf(new JKBXVO[] { vo }, MESSAGE_INVALID);

		// 删除审批流
		NCLocator
				.getInstance()
				.lookup(IWorkflowMachine.class)
				.deleteCheckFlow(vo.getParentVO().getDjlxbm(),
						vo.getParentVO().getPrimaryKey(), vo,
						InvocationInfoProxy.getInstance().getUserId());
		return vo;
	}

	/**
	 * 
	 * 传收付 往来单据
	 */
	private void transferArap(JKBXVO vo) throws BusinessException {
		JKBXHeaderVO headVo = vo.getParentVO();
		String pk_group = headVo.getPk_group();
		if (headVo.isAdjustBxd()) {
			// 报销类型为费用调整的单据，不生成往来
			return;
		}
		// 判断是否安装应收应付产品，否则不进行转往来操作
		boolean isARused = BXUtil.isProductInstalled(pk_group,
				BXConstans.FI_AR_FUNCODE);
		boolean isAPused = BXUtil.isProductInstalled(pk_group,
				BXConstans.FI_AP_FUNCODE);

		// 进行转收付操作
		if (isARused && isAPused) {
			new ErPFUtil().doTransferArap(vo);
		}
	}

	/**
	 * 
	 * 取消传收付 往来单据
	 */
	private void unTransferArap(String[] keys) throws SQLException,
			DAOException, BusinessException {
		String sqlJS = " er_jsconstras where "
				+ SqlUtils.getInStr("er_jsconstras.pk_bxd", keys);
		Collection<JsConstrasVO> jsContrasVOs = getJKBXDAO()
				.queryJsContrastByWhereSql(sqlJS);

		if (jsContrasVOs != null) {

			// 处理生成的收付往来单据，分应收应付单独处理0表示应收 1表示应付
			List<String> vouchid1 = new ArrayList<String>();
			List<String> vouchid0 = new ArrayList<String>();
			JsConstrasVO jsconvo = null;
			for (Iterator<JsConstrasVO> iter = jsContrasVOs.iterator(); iter
					.hasNext();) {
				jsconvo = iter.next();

				String vouchid = jsconvo.getPk_jsd();
				Integer billflag = jsconvo.getBillflag();
				if (billflag.intValue() == 1) {
					vouchid1.add(vouchid);
				} else {
					vouchid0.add(vouchid);
				}
			}

			if (vouchid1 != null && vouchid1.size() != 0) {
				// 通过对应的应付单的pk找到对应的聚合VO，之后删除对应的应付单
				IArapPayableBillPubService apBillService = (IArapPayableBillPubService) NCLocator
						.getInstance().lookup(
								IArapPayableBillPubService.class.getName());
				IArapPayableBillPubQueryService apQryService = (IArapPayableBillPubQueryService) NCLocator
						.getInstance()
						.lookup(IArapPayableBillPubQueryService.class.getName());
				AggPayableBillVO[] payablevo = apQryService
						.findBillByPrimaryKey(vouchid1.toArray(new String[] {}));
				if (payablevo != null) {
					for (AggPayableBillVO ss : payablevo) {
						BaseBillVO svo = (BaseBillVO) ss.getParentVO();
						if (svo.getBillstatus().intValue() != ARAPBillStatus.TEMPSAVE.VALUE
								.intValue()
								&& svo.getBillstatus().intValue() != ARAPBillStatus.SAVE.VALUE
										.intValue()) {
							throw new BusinessRuntimeException(
									nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
											.getStrByID("expensepub_0",
													"02011002-0061")/*
																	 * @ res
																	 * "下游往来单据已经不是暂存或保存态， 不能进行反生效操作"
																	 */);
						}
					}
					apBillService.delete(payablevo);
				}
			}
			if (vouchid0 != null && vouchid0.size() != 0) {
				// 通过对应的应收单的pk找到对应的聚合VO，之后删除对应的应收单
				IArapReceivableBillPubService billBo0 = (IArapReceivableBillPubService) NCLocator
						.getInstance().lookup(
								IArapReceivableBillPubService.class.getName());
				IArapReceivableBillPubQueryService billquery0 = (IArapReceivableBillPubQueryService) NCLocator
						.getInstance().lookup(
								IArapReceivableBillPubQueryService.class
										.getName());
				AggReceivableBillVO[] recvo = billquery0
						.findBillByPrimaryKey(vouchid0.toArray(new String[] {}));
				if (recvo != null) {
					for (AggReceivableBillVO ss : recvo) {
						BaseBillVO svo = (BaseBillVO) ss.getParentVO();
						if (svo.getBillstatus().intValue() != ARAPBillStatus.TEMPSAVE.VALUE
								.intValue()
								&& svo.getBillstatus().intValue() != ARAPBillStatus.SAVE.VALUE
										.intValue()) {
							throw new BusinessRuntimeException(
									nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
											.getStrByID("expensepub_0",
													"02011002-0061")/*
																	 * @ res
																	 * "下游往来单据已经不是暂存或保存态， 不能进行反生效操作"
																	 */);
						}
					}
					billBo0.delete(recvo);
				}
			}

			getJKBXDAO().delete(jsContrasVOs.toArray(new JsConstrasVO[] {}));
		}
	}

	/**
	 * @param vo
	 * @throws BusinessException
	 * 
	 *             增加冲借款的信息和对应的借款单的信息
	 */
	private void addBxExtralInfo(JKBXVO vo) throws BusinessException {
		if (vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
			BxcontrastVO[] contrast = vo.getContrastVO();
			if (vo.getContrastVO() == null || vo.getContrastVO().length == 0) {
				// 补充冲销信息
				Collection<BxcontrastVO> contrasts = queryContrasts(vo
						.getParentVO());
				contrast = contrasts.toArray(new BxcontrastVO[] {});
				vo.setContrastVO(contrast);
			}

			if (vo.getcShareDetailVo() == null
					|| vo.getcShareDetailVo().length == 0) {
				// 补充分摊明细
				Collection<CShareDetailVO> cShares = queryCSharesVOS(new JKBXHeaderVO[] { vo
						.getParentVO() });
				vo.setcShareDetailVo(cShares.toArray(new CShareDetailVO[] {}));
			}
			if (vo.getAccruedVerifyVO() == null
					|| vo.getAccruedVerifyVO().length == 0) {
				// 补充核销预提明细
				Collection<AccruedVerifyVO> accvvos = queryAccruedVerifyVOS(vo
						.getParentVO());
				vo.setAccruedVerifyVO(accvvos.toArray(new AccruedVerifyVO[] {}));
			}

			// 补充对应的借款单信息
			List<String> jkdKeys = new ArrayList<String>();
			if (contrast != null && contrast.length != 0) {
				for (BxcontrastVO contr : contrast) {
					jkdKeys.add(contr.getPk_jkd());
				}
			}

			if (jkdKeys.size() > 0) {
				List<JKBXHeaderVO> jkds = new BXZbBO()
						.queryHeadersByPrimaryKeys(
								jkdKeys.toArray(new String[] {}),
								BXConstans.JK_DJDL);
				Map<String, JKBXHeaderVO> jkdMap = new HashMap<String, JKBXHeaderVO>();
				if (jkds != null) {
					for (JKBXHeaderVO jkd : jkds) {
						jkdMap.put(jkd.getPrimaryKey(), jkd);
					}
				}
				vo.setJkdMap(jkdMap);
			}
		}
	}

	/**
	 * 回退单据号
	 * 
	 * @param bxvo
	 * @throws BusinessException
	 */

	public void returnBillCode(JKBXVO bxvo) throws BusinessException {
		FinanceBillCodeUtils utils = new FinanceBillCodeUtils(
				new FinanceBillCodeInfo(JKBXHeaderVO.DJDL, JKBXHeaderVO.DJBH,
						JKBXHeaderVO.PK_GROUP, JKBXHeaderVO.PK_ORG, bxvo
								.getParentVO().getTableName(),
						JKBXHeaderVO.DJLXBM, bxvo.getParentVO()
								.getPk_billtype()));
		utils.returnBillCode(new AggregatedValueObject[] { bxvo });
	}

	/**
	 * @param headers
	 * @throws DAOException
	 * @throws SQLException
	 * 
	 *             由于DAO不能同时更新借款单和报销单，这里需要分开处理.
	 */
	public void updateHeaders(JKBXHeaderVO[] headers, String[] fields)
			throws BusinessException {
		Map<String, List<JKBXHeaderVO>> voMap = splitJkbx(headers);
		Collection<List<JKBXHeaderVO>> values = voMap.values();
		List<JKBXHeaderVO> list = new LinkedList<JKBXHeaderVO>();
		for (Iterator<List<JKBXHeaderVO>> iter = values.iterator(); iter
				.hasNext();) {
			List<JKBXHeaderVO> lvos = iter.next();
			list.addAll(lvos);
		}
		try {
			getJKBXDAO().update(list.toArray(new JKBXHeaderVO[list.size()]),
					fields);
		} catch (SQLException e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}

	/**
	 * @param headers
	 * @return 按单据大类分离VO数组
	 */
	private Map<String, List<JKBXHeaderVO>> splitJkbx(JKBXHeaderVO[] headers) {
		Map<String, List<JKBXHeaderVO>> map = new HashMap<String, List<JKBXHeaderVO>>();
		for (int i = 0; i < headers.length; i++) {
			JKBXHeaderVO headerVO = headers[i];
			String djdl = headerVO.getDjdl();
			if (map.containsKey(djdl)) {
				map.get(djdl).add(headerVO);
			} else {
				List<JKBXHeaderVO> list = new ArrayList<JKBXHeaderVO>();
				list.add(headerVO);
				map.put(djdl, list);
			}
		}
		return map;
	}

	/**
	 * @param keys
	 *            //所有单据主键
	 * @param jkdKeys
	 *            //借款单据主键
	 * @param headers
	 *            //借款单据表头
	 * @throws SQLException
	 * @throws DAOException
	 * @throws BusinessException
	 */
	private void checkUnSettle(String[] keys, JKBXHeaderVO[] headers)
			throws BusinessException {

		try {
			if (keys != null) {// 校验借款单是否已经进行了冲销

				String sqlJK = SqlUtils.getInStr(BxcontrastVO.PK_JKD, keys);

				Collection<BxcontrastVO> jkContrasVOs = getJKBXDAO()
						.retrieveContrastByClause(sqlJK);

				if (jkContrasVOs != null && jkContrasVOs.size() > 0) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes()
							.getStrByID("2011", "UPP2011-000384")/*
																 * @res
																 * "借款单已进行了冲销操作,无法进行反签字!"
																 */);
				}

				for (int i = 0; i < headers.length; i++) {
					JKBXHeaderVO bxHeaderVO = headers[i];
					if (BXConstans.BX_DJDL.equals(bxHeaderVO.getDjdl())) {
						CostShareVO shareVo = NCLocator
								.getInstance()
								.lookup(IErmCostShareBillQuery.class)
								.queryCShareVOByBxVoHead(bxHeaderVO,
										UFBoolean.FALSE);
						if (shareVo != null) {
							throw new BusinessException(
									nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
											.getStrByID("upp2012v575_0",
													"0upp2012V575-0063")/*
																		 * @res
																		 * "报销单已结转，无法进行反签字!"
																		 */);
						}
					}
				}

			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public static String MESSAGE_TEMP_SAVE = "bx-temp-save";

	public static String MESSAGE_SAVE = "bx-save";

	public static String MESSAGE_UPDATE = "bx-update";

	public static String MESSAGE_AUDIT = "bx-audit";

	public static String MESSAGE_UNAUDIT = "bx-unaudit";

	public static String MESSAGE_DELETE = "bx-delete";

	public static String MESSAGE_SETTLE = "bx-settle";

	public static String MESSAGE_UNSETTLE = "bx-unsettle";

	public static String MESSAGE_INVALID = "bx-invalid";

	public static int MESSAGE_NOTSEND = -1;

	/**
	 * @ 与收付以及会计平台交互 @
	 * */

	private void sendMessage(JKBXVO vo, int message) throws BusinessException {
		try {
			JKBXHeaderVO headVO = VOFactory.createHeadVO(vo.getParentVO()
					.getDjdl());
			headVO = vo.getParentVO();

			AggregatedValueObject object = createJkbxToFIPVO(vo, message);

			sendMessageToFip(headVO, vo, object, message);
		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		}
	}

	public static AggregatedValueObject createJkbxToFIPVO(JKBXVO vo, int message)
			throws BusinessException {
		AggregatedValueObject object = null;

		JKBXHeaderVO headVo = vo.getParentVO();
		if (headVo.isAdjustBxd()) {
			// 报销类型为费用调整的单据，按照报销主表+分摊明细行生成凭证
			object = (JKBXVO) vo.clone();
		} else {
			JKBXVO bxvo = ErVOUtils.prepareBxvoHeaderToItemClone(vo);

			if (message != FipMessageVO.MESSAGETYPE_DEL) {
				object = new FipUtil().addOtherInfo(bxvo);
			}
		}

		return object;
	}

	/**
	 * 传入会计平台的数据： 集团，组织，来源系统，业务日期，单据PK，单据类型 自定义项：报销管理在会计平台需要展示的项目
	 * 
	 * @param message
	 * */
	private void sendMessageToFip(JKBXHeaderVO headVO, JKBXVO bxvo,
			Object object, int message) throws BusinessException {
		FipRelationInfoVO reVO = new FipRelationInfoVO();
		// 具体设置信息应用会计平台
		reVO.setPk_group(headVO.getPk_group());

		// 63后传会计凭证按支付单位来进行处理
		reVO.setPk_org(headVO.getPk_org());
		// reVO.setRelationID(headVO.getPk());
		if (headVO.getVouchertag() == null) {// EHP2后生成凭证要根据凭证状态生成凭证
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("expensepub_0",
							"0expense-000032")/* @res "单据凭证状态不能为空，不能生成凭证！" */);
		}

		reVO.setRelationID(headVO.getPk() + "_" + headVO.getVouchertag());
		reVO.setPk_system(BXConstans.ERM_PRODUCT_CODE_Lower);

		reVO.setBusidate(headVO.getJsrq() == null ? new UFDate(
				InvocationInfoProxy.getInstance().getBizDateTime()) : headVO
				.getJsrq());
		reVO.setPk_billtype(headVO.getDjlxbm());
		reVO.setPk_operator(headVO.getOperator());
		reVO.setFreedef1(headVO.getDjbh());
		reVO.setFreedef2(headVO.getZy());
		UFDouble total = headVO.getYbje();

		if (headVO.getDjdl().equals(BXConstans.BX_DJDL)
				&& !headVO.getDjlxbm().startsWith("2647")
				&& !headVO.getDjlxbm().startsWith("264a")
				&& !headVO.getDjlxbm().startsWith("265a")) {
			total = headVO.getVat_amount();
		}

		// added by chendya 设置金额字段的精度
		total = Currency.getFormaUfValue(headVO.getBzbm(), total);

		reVO.setFreedef3(String.valueOf(total));

		FipMessageVO messageVO = new FipMessageVO();
		messageVO.setBillVO(object);
		messageVO.setMessagetype(message);
		messageVO.setMessageinfo(reVO);
		try {
			new FipCallFacade().sendMessage(messageVO);
		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);

		}
	}

	void beforeActInf(JKBXVO djzb, String message) throws BusinessException {
		beforeActInf(new JKBXVO[] { djzb }, message);
	}

	void afterActInf(JKBXVO djzb, String message) throws BusinessException {
		afterActInf(new JKBXVO[] { djzb }, message);
	}

	/**
	 * 动作后事件处理
	 * 
	 * @param vos
	 * @param message
	 * @throws BusinessException
	 */
	private void afterActInf(JKBXVO[] vos, String message)
			throws BusinessException {
		List<JKBXVO> listVOs = new ArrayList<JKBXVO>();
		for (JKBXVO vo : vos) {
			if (!vo.getParentVO().isNoOtherEffectItf()) {
				listVOs.add(vo);
			}
		}

		String eventType = null;
		if (message.equals(MESSAGE_SAVE)) {
			eventType = ErmEventType.TYPE_INSERT_AFTER;
		} else if (message.equals(MESSAGE_UPDATE)) {
			if (vos[0].getParentVO().getDjzt() == BXStatusConst.DJZT_TempSaved) {
				eventType = ErmEventType.TYPE_TEMPUPDATE_AFTER;
			} else {
				eventType = ErmEventType.TYPE_UPDATE_AFTER;
			}
		} else if (message.equals(MESSAGE_AUDIT)) {
			eventType = ErmEventType.TYPE_APPROVE_AFTER;
		} else if (message.equals(MESSAGE_UNAUDIT)) {
			eventType = ErmEventType.TYPE_UNAPPROVE_AFTER;
		} else if (message.equals(MESSAGE_DELETE)) {
			eventType = ErmEventType.TYPE_DELETE_AFTER;
		} else if (message.equals(MESSAGE_SETTLE)) {
			eventType = ErmEventType.TYPE_SIGN_AFTER;
		} else if (message.equals(MESSAGE_UNSETTLE)) {
			eventType = ErmEventType.TYPE_UNSIGN_AFTER;
		} else if (message.equals(MESSAGE_TEMP_SAVE)) {
			eventType = ErmEventType.TYPE_TEMPSAVE_AFTER;
		} else if (message.equals(MESSAGE_INVALID)) {
			eventType = ErmEventType.TYPE_INVALID_AFTER;
		} else {
			return;
		}
		// 非常用单据发送事件
		// 20221020 如果为 264X-Cxx-gcfkht 工程-付款合同，则跳过校验 XBX
		if (!vos[0].getParentVO().isInit()) {
			String djlxbm = vos[0].getParentVO().getDjlxbm();
			if (djlxbm != null && !"264X-Cxx-gcfkht".equals(djlxbm)
					&& !"264X-Cxx-HFCGHT".equals(djlxbm)
					&& !"264X-Cxx-HFXSHT".equals(djlxbm)
					&& !"264X-Cxx-gcskht".equals(djlxbm)) {
				EventDispatcher.fireEvent(new ErmBusinessEvent(
						BXConstans.ERM_MDID_BX, eventType, vos));
			}
		}

		// 生成报销业务日志
		for (JKBXVO bxvo : vos) {
			if (message.equals(MESSAGE_UPDATE)
					|| message.equals(MESSAGE_DELETE)) {
				BxBusiLogUtils.insertSmartBusiLogs(
						message.equals(MESSAGE_UPDATE) ? true : false, bxvo);
			}
		}
	}

	// @sscct@合同模块补丁合并增加--20171019--begin
	/**
	 * 动作后事件处理-new
	 * 
	 * @param vos
	 * @param message
	 * @throws BusinessException
	 */
	private void afterActInf(JKBXVO[] vos, JKBXVO[] oldvos, String message)
			throws BusinessException {
		List<JKBXVO> listVOs = new ArrayList<JKBXVO>();
		for (JKBXVO vo : vos) {
			if (!vo.getParentVO().isNoOtherEffectItf()) {
				listVOs.add(vo);
			}
		}

		String eventType = null;
		if (message.equals(MESSAGE_SAVE)) {
			eventType = ErmEventType.TYPE_INSERT_AFTER;
		} else if (message.equals(MESSAGE_UPDATE)) {
			if (vos[0].getParentVO().getDjzt() == BXStatusConst.DJZT_TempSaved) {
				eventType = ErmEventType.TYPE_TEMPUPDATE_AFTER;
			} else {
				eventType = ErmEventType.TYPE_UPDATE_AFTER;
			}
		} else if (message.equals(MESSAGE_AUDIT)) {
			eventType = ErmEventType.TYPE_APPROVE_AFTER;
		} else if (message.equals(MESSAGE_UNAUDIT)) {
			eventType = ErmEventType.TYPE_UNAPPROVE_AFTER;
		} else if (message.equals(MESSAGE_DELETE)) {
			eventType = ErmEventType.TYPE_DELETE_AFTER;
		} else if (message.equals(MESSAGE_SETTLE)) {
			eventType = ErmEventType.TYPE_SIGN_AFTER;
		} else if (message.equals(MESSAGE_UNSETTLE)) {
			eventType = ErmEventType.TYPE_UNSIGN_AFTER;
		} else if (message.equals(MESSAGE_TEMP_SAVE)) {
			eventType = ErmEventType.TYPE_TEMPSAVE_AFTER;
		} else if (message.equals(MESSAGE_INVALID)) {
			eventType = ErmEventType.TYPE_INVALID_AFTER;
		} else {
			return;
		}
		// 非常用单据发送事件
		// 20221020 如果为 264X-Cxx-gcfkht 工程-付款合同，则跳过校验 XBX
		if (!vos[0].getParentVO().isInit()) {
			String djlxbm = vos[0].getParentVO().getDjlxbm();
			if (djlxbm != null && !"264X-Cxx-gcfkht".equals(djlxbm)
					&& !"264X-Cxx-HFCGHT".equals(djlxbm)
					&& !"264X-Cxx-HFXSHT".equals(djlxbm)
					&& !"264X-Cxx-gcskht".equals(djlxbm)) {
				EventDispatcher.fireEvent(new ErmBusinessEvent(
						BXConstans.ERM_MDID_BX, eventType, vos, oldvos));
			}
		}

		// 生成报销业务日志
		for (JKBXVO bxvo : vos) {
			if (message.equals(MESSAGE_UPDATE)
					|| message.equals(MESSAGE_DELETE)) {
				BxBusiLogUtils.insertSmartBusiLogs(
						message.equals(MESSAGE_UPDATE) ? true : false, bxvo);
			}
		}
	}

	// @sscct@合同模块补丁合并增加--20171019--end

	/**
	 * 单据生效传会计平台
	 * 
	 * @throws BusinessException
	 */
	private void effectToFip(JKBXVO bxvo, String message)
			throws BusinessException {
		if (getDapMessage(bxvo.getParentVO(), message) != MESSAGE_NOTSEND) {
			sendMessage(bxvo, getDapMessage(bxvo.getParentVO(), message));
		}
	}

	/**
	 * 单据生效传会计平台
	 * 
	 * @throws BusinessException
	 */
	public void effectToFip(List<JKBXVO> listVOs, String message)
			throws BusinessException {
		// 发送会计平台
		for (Iterator<JKBXVO> iter = listVOs.iterator(); iter.hasNext();) {
			effectToFip(iter.next(), message);
		}
	}

	/**
	 * Action触发之前的动作
	 * 
	 * @param vos
	 * @param message
	 * @throws BusinessException
	 */
	private void beforeActInf(JKBXVO[] vos, String message)
			throws BusinessException {
		List<JKBXVO> listVOs = new ArrayList<JKBXVO>();
		for (JKBXVO vo : vos) {
			if (!vo.getParentVO().isNoOtherEffectItf()) {
				listVOs.add(vo);
			}
		}

		// 事件类型
		String eventType = null;
		if (message.equals(MESSAGE_SAVE)) {
			eventType = ErmEventType.TYPE_INSERT_BEFORE;
		} else if (message.equals(MESSAGE_UPDATE)) {
			if (vos[0].getParentVO().getDjzt() == BXStatusConst.DJZT_TempSaved) {
				eventType = ErmEventType.TYPE_TEMPUPDATE_BEFORE;
			} else {
				eventType = ErmEventType.TYPE_UPDATE_BEFORE;
			}
		} else if (message.equals(MESSAGE_AUDIT)) {
			eventType = ErmEventType.TYPE_APPROVE_BEFORE;
		} else if (message.equals(MESSAGE_UNAUDIT)) {
			eventType = ErmEventType.TYPE_UNAPPROVE_BEFORE;
		} else if (message.equals(MESSAGE_DELETE)) {
			eventType = ErmEventType.TYPE_DELETE_BEFORE;
		} else if (message.equals(MESSAGE_SETTLE)) {
			eventType = ErmEventType.TYPE_SIGN_BEFORE;
		} else if (message.equals(MESSAGE_UNSETTLE)) {
			eventType = ErmEventType.TYPE_UNSIGN_BEFORE;
		} else if (message.equals(MESSAGE_TEMP_SAVE)) {
			eventType = ErmEventType.TYPE_TEMPSAVE_BEFORE;
		} else if (message.equals(MESSAGE_INVALID)) {
			eventType = ErmEventType.TYPE_INVALID_BEFORE;
		} else {
			return;
		}
		// 20221020 如果为 264X-Cxx-gcfkht 工程-付款合同，则跳过校验
		for (JKBXVO vo : vos) {
			String djlxbm = vo.getParentVO().getDjlxbm();
			if (djlxbm != null && !"264X-Cxx-gcfkht".equals(djlxbm)
					&& !"264X-Cxx-HFCGHT".equals(djlxbm)
					&& !"264X-Cxx-HFXSHT".equals(djlxbm)
					&& !"264X-Cxx-gcskht".equals(djlxbm)) {
				// 非常用单据发送事件
				if (!vos[0].getParentVO().isInit()) {
					EventDispatcher.fireEvent(new ErmBusinessEvent(
							BXConstans.ERM_MDID_BX, eventType, vos));
				}
			}
		}

	}

	// 判断签字时候传会计平台
	private int getDapMessage(JKBXHeaderVO head, String message) {
		int msg = MESSAGE_NOTSEND;

		if (message.equals(MESSAGE_SETTLE)) {
			msg = FipMessageVO.MESSAGETYPE_ADD;
		} else if (message.equals(MESSAGE_UNSETTLE)) {
			msg = FipMessageVO.MESSAGETYPE_DEL;
		} else if (head.getQcbz().equals(UFBoolean.TRUE)
				&& message.equals(MESSAGE_SAVE)) {
			msg = FipMessageVO.MESSAGETYPE_ADD;
		} else if (head.getQcbz().equals(UFBoolean.TRUE)
				&& message.equals(MESSAGE_DELETE)) {
			msg = FipMessageVO.MESSAGETYPE_DEL;
		}
		return msg;
	}

	public List<JKBXHeaderVO> queryHeaders(Integer start, Integer count,
			DjCondVO condVO) throws BusinessException {
		try {
			return getJKBXDAO().queryHeaders(start, count, condVO);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public List<JKBXHeaderVO> queryHeadersByWhereSql(String sql, String djdl)
			throws BusinessException {
		try {
			return getJKBXDAO().queryHeadersByWhereSql(sql, djdl);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	private static void lockDJ(String pk) throws BusinessException {
		String lock = KeyLock.dynamicLock(pk);
		if (lock != null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000359"));
		}
	}

	public void compareTs(JKBXHeaderVO[] vos) throws BusinessException {
		Hashtable<String, String> ts = new Hashtable<String, String>();
		Hashtable<String, String> ts2 = new Hashtable<String, String>();
		Hashtable<String, String> ts3 = new Hashtable<String, String>();
		for (JKBXHeaderVO vo : vos) {
			if (vo.isInit()) {
				ts3.put(vo.getPk_jkbx(), vo.getTs() + "");
			} else if (vo.getDjdl().equals(BXConstans.BX_DJDL)) {
				ts.put(vo.getPk_jkbx(), vo.getTs() + "");
			} else {
				ts2.put(vo.getPk_jkbx(), vo.getTs() + "");
			}
		}
		if (ts.size() != 0)
			compareTS(ts, "er_bxzb", JKBXHeaderVO.PK_JKBX);
		if (ts2.size() != 0)
			compareTS(ts2, "er_jkzb", JKBXHeaderVO.PK_JKBX);
		if (ts3.size() != 0)
			compareTS(ts3, "er_jkbx_init", JKBXHeaderVO.PK_JKBX);
	}

	public void compareTs(JKBXVO[] vos) throws BusinessException {
		Hashtable<String, String> ts = new Hashtable<String, String>();
		Hashtable<String, String> ts2 = new Hashtable<String, String>();
		Hashtable<String, String> ts3 = new Hashtable<String, String>();

		for (JKBXVO vo : vos) {
			UFDateTime ts4 = vo.getParentVO().getTs();
			String pk_jkbx = vo.getParentVO().getPk_jkbx();

			if (ts4 == null || pk_jkbx == null)
				continue;

			if (vo.getParentVO().isInit()) {
				ts3.put(pk_jkbx, ts4 + "");
			} else if (vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
				ts.put(pk_jkbx, ts4 + "");
			} else {
				ts2.put(pk_jkbx, ts4 + "");
			}
		}

		if (ts.size() != 0)
			compareTS(ts, "er_bxzb", JKBXHeaderVO.PK_JKBX);
		if (ts2.size() != 0)
			compareTS(ts2, "er_jkzb", JKBXHeaderVO.PK_JKBX);
		if (ts3.size() != 0)
			compareTS(ts3, "er_jkbx_init", JKBXHeaderVO.PK_JKBX);
	}

	public static void compareTS(Hashtable<String, String> ts,
			String tableName, String pkField) throws BusinessException {
		String tname = null;
		Connection con = null;
		PreparedStatement stat = null;
		ResultSet rs = null;

		try {
			con = ConnectionFactory.getConnection();

			nc.bs.mw.sqltrans.TempTable tmptab = new nc.bs.mw.sqltrans.TempTable();

			tname = tmptab.createTempTable(con, "ErmTsTemp",
					"pk char(20),ts1 char(19)", "pk");

			String sql = "insert into " + tname + " (pk,ts1) values(?,?)";
			((CrossDBConnection) con).setAddTimeStamp(false);
			stat = con.prepareStatement(sql);

			for (Map.Entry<String, String> entry : ts.entrySet()) {
				stat.setString(1, entry.getKey());
				stat.setString(2, entry.getValue());
				stat.addBatch();
				lockDJ(entry.getKey());
			}

			stat.executeBatch();
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		} finally {
			closeDBCon(con, stat, rs);
		}

		try {
			final String sql_n = "select count(temp.pk) from " + tname
					+ " temp inner join " + tableName + " tab on temp.pk=tab."
					+ pkField + " where temp.ts1=tab.ts";
			con = ConnectionFactory.getConnection();
			stat = con.prepareStatement(sql_n);
			rs = stat.executeQuery();
			int result = 0;
			if (rs.next())
				result = rs.getInt(1);

			if (result != ts.size())
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000359")/*
																			 * @res
																			 * "并发异常，数据已经更新，请重新查询数据后操作"
																			 */);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		} finally {
			closeDBCon(con, stat, rs);
		}
	}

	private static void closeDBCon(Connection con, PreparedStatement stat,
			ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				ExceptionHandler.consume(e);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				ExceptionHandler.consume(e);
			}
		}
		if (stat != null) {
			try {
				stat.close();
			} catch (SQLException e) {
				ExceptionHandler.consume(e);
			}
		}
	}

	public Collection<JsConstrasVO> queryJsContrasts(JKBXHeaderVO header)
			throws BusinessException {

		String pk_jkbx = header.getPk_jkbx();

		Collection<JsConstrasVO> vos = null;
		try {
			vos = getJKBXDAO().retrieveJsContrastByClause(
					JsConstrasVO.PK_BXD + "='" + pk_jkbx + "'");
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
		return vos;
	}

	public Collection<BxcontrastVO> queryContrasts(JKBXHeaderVO parentVO)
			throws BusinessException {

		String pk_jkbx = parentVO.getPk_jkbx();
		String key = null;

		if (parentVO.getDjdl().equals(BXConstans.BX_DJDL)) {
			key = BxcontrastVO.PK_BXD;
		} else {
			key = BxcontrastVO.PK_JKD;
		}

		Collection<BxcontrastVO> vos = null;
		try {
			vos = getJKBXDAO().retrieveContrastByClause(
					key + "='" + pk_jkbx + "'");
			fillUpHkJe(vos);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return vos;

	}

	public Collection<BxcontrastVO> queryContrasts(JKBXHeaderVO[] parentVO,
			String djdl) throws BusinessException {
		String key = null;

		if (djdl.equals(BXConstans.BX_DJDL)) {
			key = BxcontrastVO.PK_BXD;
		} else {
			key = BxcontrastVO.PK_JKD;
		}

		Collection<BxcontrastVO> vos = null;
		try {
			vos = getJKBXDAO().retrieveContrastByClause(
					SqlUtils.getInStr(key, parentVO, JKBXHeaderVO.PK_JKBX));
			fillUpHkJe(vos);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return vos;
	}

	private void fillUpHkJe(Collection<BxcontrastVO> vos) {
		if (vos != null && vos.size() > 0) {// 冲销信息的还款金额属于计算属性，所以需要在这里补充
			for (BxcontrastVO contrastVo : vos) {
				contrastVo.setHkybje(contrastVo.getCjkybje().sub(
						contrastVo.getFyybje() == null ? UFDouble.ZERO_DBL
								: contrastVo.getFyybje()));

				UFDouble cjkbbje = contrastVo.getCjkbbje() == null ? UFDouble.ZERO_DBL
						: contrastVo.getCjkbbje();
				UFDouble fybbje = contrastVo.getFybbje() == null ? UFDouble.ZERO_DBL
						: contrastVo.getFybbje();

				contrastVo.setHkbbje(cjkbbje.sub(fybbje));
			}
		}
	}

	public List<JKBXHeaderVO> queryHeadersByPrimaryKeys(String[] keys,
			String djdl) throws BusinessException {
		List<JKBXHeaderVO> headVos = null;
		try {

			String inStr = SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, keys);
			inStr += " order by djrq desc, djbh desc ";
			headVos = getJKBXDAO().queryHeadersByWhereSql(
					" where zb.dr=0 and " + inStr, djdl);

		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
		return headVos;
	}

	public int querySize(DjCondVO condVO) throws BusinessException {
		int querySize = -99;
		try {
			querySize = getJKBXDAO().querySize(condVO);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
		return querySize;
	}

	public void updateQzzt(JKBXVO[] vos) throws BusinessException {
		JKBXHeaderVO[] heads = new JKBXHeaderVO[vos.length];
		for (int i = 0; i < heads.length; i++) {
			heads[i] = vos[i].getParentVO();
		}
		try {
			getJKBXDAO().update(heads, new String[] { JKBXHeaderVO.QZZT });
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public Collection<BxcontrastVO> queryJkContrast(JKBXVO[] selBxvos,
			boolean isBatch) throws BusinessException {
		Collection<BxcontrastVO> jkContrasVOs = null;
		try {
			String[] bxdKeys = new String[selBxvos.length];
			for (int i = 0; i < bxdKeys.length; i++) {
				bxdKeys[i] = selBxvos[i].getParentVO().getPk_jkbx();
			}
			String sqlJK = SqlUtils.getInStr(BxcontrastVO.PK_BXD, bxdKeys);
			jkContrasVOs = getJKBXDAO().retrieveContrastByClause(sqlJK);

			if (isBatch) {
				List<String> cjk_pc = new ArrayList<String>();
				for (BxcontrastVO vo : jkContrasVOs) {
					if (!StringUtils.isNullWithTrim(vo.getPk_pc())) {
						cjk_pc.add(vo.getPk_pc());
					}
				}

				Collection<BxcontrastVO> batchVos = getJKBXDAO()
						.retrieveContrastByClause(
								SqlUtils.getInStr(BxcontrastVO.PK_PC,
										cjk_pc.toArray(new String[] {})));

				jkContrasVOs.addAll(batchVos);
			}

		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		fillUpHkJe(jkContrasVOs);
		return jkContrasVOs;

	}

	/**
	 * 查询分摊信息（表体）
	 * 
	 * @param parentVO
	 * @return
	 * @throws BusinessException
	 */
	public Collection<CShareDetailVO> queryCSharesVOS(JKBXHeaderVO[] parentVO)
			throws BusinessException {

		String key = CShareDetailVO.SRC_ID;

		Collection<CShareDetailVO> vos = null;
		try {
			vos = getJKBXDAO().retrieveCShareVoByClause(
					SqlUtils.getInStr(key, parentVO, JKBXHeaderVO.PK_JKBX)
							+ " and " + CShareDetailVO.SRC_TYPE + "="
							+ IErmCostShareConst.CostShare_Bill_SCRTYPE_BX
							+ " and dr=0 ");
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return vos;
	}

	public void saveSqdlrs(List<String> roles, SqdlrVO[] sqdlrVOs)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		List<SqdlrVO> insertVos = new ArrayList<SqdlrVO>();
		List<SqdlrVO> updateVos = new ArrayList<SqdlrVO>();
		for (SqdlrVO sqdlrVO : sqdlrVOs) {
			for (String role : roles) {
				if (sqdlrVO.getPk_authorize() != null) {
					if (!role.equals(sqdlrVO.getPk_roler())) {
						addNewSqdlrVO(insertVos, sqdlrVO, role);
					} else {
						updateVos.add(sqdlrVO);
					}
				} else {
					addNewSqdlrVO(insertVos, sqdlrVO, role);
				}

			}
		}
		try {
			dao.insertVOArray(insertVos.toArray(new SqdlrVO[insertVos.size()]));
			dao.updateVOArray(updateVos.toArray(new SqdlrVO[0]));
		} catch (DAOException e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	private void addNewSqdlrVO(List<SqdlrVO> insertVos, SqdlrVO sqdlrVO,
			String role) throws BusinessException {
		if (!isAsigned(role, sqdlrVO.getPk_user(),
				(sqdlrVO.getKeyword() != null ? sqdlrVO.getKeyword()
						: ISqdlrKeyword.KEYWORD_BUSIUSER))) {
			SqdlrVO o = new SqdlrVO();
			o.setPk_roler(role);
			o.setPk_org(sqdlrVO.getPk_org());
			o.setPk_user(sqdlrVO.getPk_user());
			o.setKeyword(sqdlrVO.getKeyword() != null ? sqdlrVO.getKeyword()
					: ISqdlrKeyword.KEYWORD_BUSIUSER);
			o.setType(0);
			insertVos.add(o);
		}
	}

	public boolean isAsigned(String pk_role, String user, String keyword)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String condition = " pk_roler = '" + pk_role + "' and pk_user = '"
				+ user + "' and keyword = '" + keyword + "'";

		Collection c = null;
		try {
			c = dao.retrieveByClause(SqdlrVO.class, condition);
		} catch (DAOException e) {
			throw ExceptionHandler.handleException(e);
		}
		if (c != null && c.size() > 0)
			return true;
		return false;
	}

	public boolean isAsigned(String pk_role, String user)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String condition = " pk_roler = '" + pk_role + "' and pk_user = '"
				+ user + "'";

		Collection c = null;
		try {
			c = dao.retrieveByClause(SqdlrVO.class, condition);
		} catch (DAOException e) {
			throw ExceptionHandler.handleException(e);
		}
		if (c != null && c.size() > 0)
			return true;
		return false;
	}

	public Map<String, List<SqdlrVO>> querySqdlr(String[] pk_roles,
			String... ywy_corps) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		StringBuffer condition = null;
		try {
			condition = new StringBuffer(
					SqlUtils.getInStr("pk_roler", pk_roles));
			if (ywy_corps != null && ywy_corps.length > 0) {
				condition.append(" and ");
				condition.append(SqlUtils.getInStr("pk_corp", ywy_corps));
			}
			condition.append(" and type=0 ");

			Collection c = dao.retrieveByClause(SqdlrVO.class, condition + "");
			Map<String, List<SqdlrVO>> vos = new HashMap<String, List<SqdlrVO>>();
			if (c != null) {
				for (Object o : c) {
					if (vos.get(((SqdlrVO) o).getPk_roler()) == null) {
						vos.put(((SqdlrVO) o).getPk_roler(),
								new ArrayList<SqdlrVO>());
					}
					vos.get(((SqdlrVO) o).getPk_roler()).add((SqdlrVO) o);
				}
			}
			return vos;
		} catch (Exception e1) {
			throw ExceptionHandler.handleException(e1);
		}

	}

	public void delSqdlrs(List<String> roles, SqdlrVO[] sqdlrVOs)
			throws BusinessException {
		try {
			batchDelete(roles, sqdlrVOs);
		} catch (DAOException e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	private int batchDelete(List<String> roles, SqdlrVO[] sqdlrVOs)
			throws DAOException {
		PersistenceManager manager = null;
		int result = 0;
		try {
			manager = PersistenceManager.getInstance();
			manager.setMaxRows(100000);
			manager.setAddTimeStamp(true);
			JdbcSession session = manager.getJdbcSession();
			List<SQLParameter> listPara = new LinkedList<SQLParameter>();
			String sql = SQLHelper.getDeleteSQL(sqdlrVOs[0].getTableName(),
					new String[] { "pk_roler", "pk_user" });
			for (String role : roles) {
				for (SqdlrVO sqdlrVO : sqdlrVOs) {
					SQLParameter para = new SQLParameter();
					para.addParam(role);
					para.addParam(sqdlrVO.getPk_user());
					listPara.add(para);
				}
			}
			session.addBatch(sql,
					listPara.toArray(new SQLParameter[listPara.size()]));
			result = session.executeBatch();
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return result;
	}

	public Collection<JsConstrasVO> queryJsContrastsByJsd(String pk_jsd)
			throws BusinessException {

		Collection<JsConstrasVO> vos;
		try {
			vos = getJKBXDAO().retrieveJsContrastByClause(
					JsConstrasVO.PK_JSD + "='" + pk_jsd + "'");
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return vos;
	}

	// public void unSettleFromArap(JKBXVO[] vos) throws BusinessException {
	// unSettle(vos, false);
	// }

	// public JKBXVO[] unSettle(JKBXVO[] vos) throws BusinessException {
	// return unSettle(vos);
	// }

	public List<SqdlrVO> querySqdlr(String pk_user, String user_corp,
			String ywy_corp) throws BusinessException {
		nc.pubitf.rbac.IRolePubService service = NCLocator.getInstance()
				.lookup(nc.pubitf.rbac.IRolePubService.class);
		try {
			RoleVO[] roleVOs = service.queryRoleByUserID(pk_user, user_corp);
			List<String> roles = new ArrayList<String>();
			for (RoleVO roleVO : roleVOs) {
				roles.add(roleVO.getPk_role());
			}
			Map<String, List<SqdlrVO>> sqdlrMap = querySqdlr(
					roles.toArray(new String[roles.size()]), ywy_corp);
			List<SqdlrVO> result = new ArrayList<SqdlrVO>();

			for (Map.Entry<String, List<SqdlrVO>> entry : sqdlrMap.entrySet()) {
				result.addAll(entry.getValue());
			}
			return result;
		} catch (BusinessException e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public void savedefSqdlrs(List<String> roles, Map<String, String[]> defMap)
			throws BusinessException {
		List<SqdlrVO> vos = new ArrayList<SqdlrVO>();

		for (Map.Entry<String, String[]> entry : defMap.entrySet()) {
			if (entry.getKey() != null) {
				deldefSqdlrs(roles, entry.getKey(), entry.getValue());
				for (String def : entry.getValue()) {
					SqdlrVO vo = new SqdlrVO();
					vo.setKeyword(entry.getKey());
					vo.setPk_user(def);
					vo.setType(0);
					vos.add(vo);
				}
			} else {
				deldefSqdlrs(roles, entry.getKey(), new String[] {});
			}
		}

		saveSqdlrs(roles, vos.toArray(new SqdlrVO[vos.size()]));

	}

	private void deldefSqdlrs(List<String> roles, String key, String[] pk_defs)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		List<String> curDefls = new ArrayList<String>();
		for (String pk_def : pk_defs) {
			curDefls.add(pk_def);
		}
		List<SqdlrVO> list = new ArrayList<SqdlrVO>();
		for (String role : roles) {
			SqdlrVO[] sqdlrVOs = getAssigned(role, key);
			if (sqdlrVOs == null)
				break;
			for (SqdlrVO o : sqdlrVOs) {
				if (!curDefls.contains(o.getPk_user())) {
					list.add(o);
				}
			}
		}
		dao.deleteVOList(list);
	}

	private SqdlrVO[] getAssigned(String pk_role, String keyword) {
		BaseDAO dao = new BaseDAO();
		String condition = " pk_roler = '" + pk_role + "' and keyword = '"
				+ keyword + "'";
		Collection<?> c = null;
		try {
			c = dao.retrieveByClause(SqdlrVO.class, condition);
		} catch (DAOException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);
		}
		if (c != null && c.size() > 0)
			return c.toArray(new SqdlrVO[c.size()]);
		return null;
	}

	public JKBXHeaderVO updateHeader(JKBXHeaderVO header, String[] fields)
			throws BusinessException {
		compareTs(new JKBXHeaderVO[] { header });
		updateHeaders(new JKBXHeaderVO[] { header }, fields);
		return header;
	}

	public JKBXDAO getJKBXDAO() throws SQLException {
		if (null == jkbxDAO) {
			try {
				jkbxDAO = new JKBXDAO();
			} catch (NamingException e) {
				Log.getInstance(this.getClass()).error(e.getMessage(), e);
				throw new SQLException(e.getMessage());
			}
		}
		return jkbxDAO;
	}

	// private boolean isCmpInstall(JKBXHeaderVO parentVO) throws
	// BusinessException {
	// boolean flag = BXUtil.isProductInstalled(parentVO.getPk_group(),
	// BXConstans.TM_CMP_FUNCODE);
	// if (!flag) {
	// return false;
	// }
	// // 产品安装了现金，但支付单位没有启用现金，则按照没有装现金逻辑走
	// // 与收付保持一致
	// String periord = NCLocator.getInstance().lookup(IOrgUnitPubService.class)
	// .getOrgModulePeriodByOrgIDAndModuleID(parentVO.getPk_payorg(),
	// BXConstans.TM_CMP_FUNCODE);
	// if (periord == null) {
	// Logger.debug("当前单据支付单位未启用现金管理");
	// return false;
	// }
	// return flag;
	// }

	private static String getBSLoginUser() {
		return InvocationInfoProxy.getInstance().getUserId();
	}

	public Map<String, String> getTsByPrimaryKey(String[] key,
			String tableName, String pkfield) throws BusinessException {
		try {
			return getJKBXDAO().getTsByPrimaryKeys(key, tableName, pkfield);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(this.getClass(), e);

		}
	}

	private void checkDelete(JKBXVO vo) throws DataValidateException,
			BusinessException {
		// 校验单据状态
		VOStatusChecker.checkDeleteStatus(vo.getParentVO());

		// 非常用单据单据校验是否关帐
		if (!vo.getParentVO().isInit()
				&& vo.getParentVO().getDjzt() != BXStatusConst.DJZT_TempSaved) {
			VOChecker.checkErmIsCloseAcc(vo);
		}
	}

	private void checkDataPermission(JKBXVO[] vos) throws BusinessException {
		List<JKBXVO> bxList = new ArrayList<JKBXVO>();
		List<JKBXVO> jkList = new ArrayList<JKBXVO>();
		boolean isNCClient = false;
		for (JKBXVO vo : vos) {
			// 是否期初单据
			boolean isQc = vo.getParentVO().getQcbz().booleanValue();
			// 是否常用单据
			boolean isInit = vo.getParentVO().isInit();

			// 是否从NC客户端
			isNCClient = vo.isNCClient();
			// 期初常用单据不校验数据权限
			if (isQc || isInit) {
				continue;
			} else {
				if (vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
					bxList.add(vo);
				} else if (vo.getParentVO().getDjdl()
						.equals(BXConstans.JK_DJDL)) {
					jkList.add(vo);
				}
			}
		}
		// 验证报销权限
		if (isNCClient) {
			BXDataPermissionChkUtil.process(bxList.toArray(new JKBXVO[0]),
					BXConstans.ERMEXPRESOURCECODE, BXConstans.EXPDELOPTCODE,
					getBSLoginUser());

			// 验证借款权限
			BXDataPermissionChkUtil.process(jkList.toArray(new JKBXVO[0]),
					BXConstans.ERMLOANRESOURCECODE, BXConstans.LOANDELOPTCODE,
					getBSLoginUser());
		}
	}

	/**
	 * 查询核销预提明细
	 * 
	 * @param parentVO
	 * @return
	 * @throws BusinessException
	 */
	public Collection<AccruedVerifyVO> queryAccruedVerifyVOS(
			JKBXHeaderVO... parentVO) throws BusinessException {

		Collection<AccruedVerifyVO> vos = null;
		BaseDAO dao = new BaseDAO();
		try {
			vos = dao.retrieveByClause(AccruedVerifyVO.class, SqlUtils
					.getInStr(AccruedVerifyVO.PK_BXD, parentVO,
							JKBXHeaderVO.PK_JKBX));
			// 查询原预提单，给预提明细补ts
			if (vos != null && vos.size() > 0) {
				List<String> accruedBillPks = new ArrayList<String>();
				for (AccruedVerifyVO vo : vos) {
					accruedBillPks.add(vo.getPk_accrued_bill());
				}
				Collection<AccruedVO> accvos = dao.retrieveByClause(
						AccruedVO.class, SqlUtils.getInStr(
								AccruedVO.PK_ACCRUED_BILL, accruedBillPks
										.toArray(new String[accruedBillPks
												.size()])));
				Map<String, UFDateTime> accTsMap = new HashMap<String, UFDateTime>();
				if (accvos != null && accvos.size() > 0) {
					for (AccruedVO vo : accvos) {
						accTsMap.put(vo.getPk_accrued_bill(), vo.getTs());
					}
				}
				if (accTsMap.size() > 0) {
					for (AccruedVerifyVO vo : vos) {
						vo.setTs(accTsMap.get(vo.getPk_accrued_bill()));
					}
				}
			}
		} catch (SQLException e) {
			ExceptionHandler.handleException(e);
		}

		return vos;
	}

	/**
	 * 是否存在支付或收款金额
	 * 
	 * @param parentVo
	 * @return
	 */
	protected boolean isExistsPayOrRecv(JKBXHeaderVO parentVo) {
		return (parentVo.getZfybje() != null && parentVo.getZfybje().compareTo(
				UFDouble.ZERO_DBL) > 0)
				|| (parentVo.getHkybje() != null && parentVo.getHkybje()
						.compareTo(UFDouble.ZERO_DBL) > 0);
	}

	/**
	 * 轻量端更新
	 */
	public JKBXVO[] updateForWeb(JKBXVO[] vos) throws BusinessException {
		// 校验时间戳
		compareTs(vos);
		for (JKBXVO vo : vos) {
			// 特殊处理摘要
			dealZyName(vo);

			// 查询修改前的vo
			if (vo.getBxoldvo() == null) {
				List<JKBXVO> oldvo = null;
				oldvo = NCLocator
						.getInstance()
						.lookup(IBXBillPrivate.class)
						.queryVOsByPrimaryKeysForNewNode(
								new String[] { vo.getParentVO().getPrimaryKey() },
								vo.getParentVO().getDjdl(),
								vo.getParentVO().isInit(), null);
				vo.setBxoldvo(oldvo.get(0));
				// 补齐children信息（因前台传过来的的只是改变的children）
				// fillUpChildren(vo, oldvo.get(0));
				// 同样补齐CShareDetailVO信息
				// fillUpCShareDetail(vo, oldvo.get(0));

			}
			// 只录表头，不录表体的情况下生成表体行
			BXUtil.generateJKBXRow(vo);

			VOChecker voChecker = new VOChecker();
			VOChecker.prepare(vo);
			// 后台校验
			voChecker.checkUpdateSave(vo);

			// 补齐申请单申请记录(预算控制中用到)
			fillUpMapf(vo);
			fillUpMapf(vo.getBxoldvo());

			// 常用单据单据,暂存态单据不校验关帐
			boolean isChkCloseAcc = true;
			JKBXHeaderVO parentVO = vo.getParentVO();
			if (parentVO != null
					&& (parentVO.isInit() || BXStatusConst.DJZT_TempSaved == parentVO
							.getDjzt())) {
				isChkCloseAcc = false;
			}
			if (isChkCloseAcc) {
				VOChecker.checkErmIsCloseAcc(vo);
			}
			if (vo.getParentVO().getDjzt() != BXStatusConst.DJZT_TempSaved) {
				if (vo.getParentVO().getQcbz() != null
						&& vo.getParentVO().getQcbz().booleanValue()) {
					vo.getParentVO().setSpzt(IPfRetCheckInfo.PASSING);
					vo.getParentVO().setShrq(
							new UFDateTime(vo.getParentVO().getDjrq(),
									new UFTime("00:00:00")));
					vo.getParentVO().setJsrq(vo.getParentVO().getDjrq());
					vo.getParentVO().setApprover(
							InvocationInfoProxy.getInstance().getUserId());
					vo.getParentVO().setJsr(
							InvocationInfoProxy.getInstance().getUserId());
				}
			}

			// 取服务器事件作为修改时间
			AuditInfoUtil.updateData(vo.getParentVO());
		}

		try {
			// 事件前
			beforeActInf(vos, MESSAGE_UPDATE);

			// @sscct@合同模块补丁合并增加--20171019--begin
			List<String> pkList = new ArrayList<String>();
			if (vos != null && vos.length > 0)
				for (JKBXVO invo : vos)
					pkList.add(invo.getParentVO().getPrimaryKey());
			List<JKBXVO> oldvo = null;
			if (pkList != null && pkList.size() > 0)
				oldvo = NCLocator
						.getInstance()
						.lookup(IBXBillPrivate.class)
						.queryVOsByPrimaryKeys(
								pkList.toArray(new String[pkList.size()]),
								vos[0].getParentVO().getDjdl());
			// @sscct@合同模块补丁合并增加--20171019--end

			// 报销单修改保存
			JKBXVO[] bxvos = getJKBXDAO().update(vos);

			// 调用现金流平台结算
			ErForCmpBO erForCmpBO = new ErForCmpBO();
			for (JKBXVO vo : vos) {

				// 当前结算信息状态
				BusiStatus billStatus = SettleUtil.getBillStatus(
						vo.getParentVO(), false);

				// 当前结算信息状态
				BusiStatus oldBillStatus = SettleUtil.getBillStatus(vo
						.getBxoldvo().getParentVO(), false);

				// CMP产品是否启用
				boolean isInstallCmp = SettleUtil.isCmpInstall(vo.getBxoldvo()
						.getParentVO());

				// 是否暂存单据修改
				boolean isTmpSave = BXStatusConst.DJZT_TempSaved == vo
						.getParentVO().getDjzt();

				// 是否不存在收付款
				boolean isExistsPayOrRecv = isExistsPayOrRecv(vo.getParentVO());

				if (!vo.getParentVO().isAdjustBxd()) {// 不是调整单
					if (oldBillStatus == BusiStatus.Save
							&& billStatus == BusiStatus.Deleted) {
						// 修改场景：单据修改全额冲借款，支付单位修改会造成原结算信息不能被删除，这里按oldVO进行删除
						// 资金逻辑不清楚，pk_org修改后就不能删除结算信息，明明记录了报销单的pk，不按pk进行删
						if (isInstallCmp && !isTmpSave) {
							erForCmpBO.invokeCmp(vo.getBxoldvo(), vo
									.getBxoldvo().getParentVO().getDjrq(),
									billStatus);
						}
					} else {
						if (isInstallCmp && !isTmpSave && isExistsPayOrRecv) {
							erForCmpBO.invokeCmp(vo,
									vo.getParentVO().getDjrq(), billStatus);
						}
					}
				}
			}
			// @sscct@合同模块补丁合并增加--20171019--begin
			if (oldvo != null && oldvo.size() > 0) {
				afterActInf(vos, oldvo.toArray(new JKBXVO[oldvo.size()]),
						MESSAGE_UPDATE);
			} else {
				afterActInf(vos, MESSAGE_UPDATE);
			}
			// @sscct@合同模块补丁合并增加--20171019--end

			// 清空补齐的数据
			for (JKBXVO vo : vos) {
				vo.setBxoldvo(null);
			}
			return bxvos;
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public static String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}

	private JSONArray getNewMainMap(JKBXHeaderVO parentVO, String billType,
			String szxm) throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		if (szxm != null) {
			list.add(OaWorkFlowUtil.listAddObj("szxm", szxm));
		}
		// 组织名称
		String shortname = (String) getHyPubBO().findColValue(
				"org_purchaseorg",
				"name",
				"nvl(dr,0) = 0 and pk_purchaseorg ='" + parentVO.getPk_org()
						+ "'");
		if (shortname == null) {
			shortname = (String) getHyPubBO().findColValue(
					"org_financeorg",
					"name",
					"nvl(dr,0) = 0 and pk_financeorg ='" + parentVO.getPk_org()
							+ "'");
		}
		if (shortname == null) {
			shortname = (String) getHyPubBO().findColValue("org_orgs", "name",
					"nvl(dr,0) = 0 and pk_org ='" + parentVO.getPk_org() + "'");
		}
		list.add(OaWorkFlowUtil.listAddObj("zzmc", shortname));
		list.add(OaWorkFlowUtil.listAddObj("jkdw", shortname));
		list.add(OaWorkFlowUtil.listAddObj("bxdw", shortname));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getCreator());
		// 交易类型
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid = '"
						+ parentVO.getPk_tradetypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
		// 填单人
		String tdr = (String) getHyPubBO().findColValue("sm_user", "user_name",
				"nvl(dr,0) = 0 and cuserid ='" + parentVO.getOperator() + "'");
		list.add(OaWorkFlowUtil.listAddObj("tdr", tdr));
		// 借款报销人
		String jkbxr = (String) getHyPubBO().findColValue("bd_psndoc", "name",
				"nvl(dr,0) = 0 and pk_psndoc ='" + parentVO.getJkbxr() + "'");
		list.add(OaWorkFlowUtil.listAddObj("jkbxr", jkbxr));
		// 收款银行账户
		if (null != parentVO.getSkyhzh()) {
			String skyhzh = (String) getHyPubBO().findColValue(
					"bd_bankaccsub",
					"accnum",
					"nvl(dr,0) = 0 and pk_bankaccsub ='" + parentVO.getSkyhzh()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("skyhzh", skyhzh));
			list.add(OaWorkFlowUtil.listAddObj("jkryhzh", skyhzh));
			list.add(OaWorkFlowUtil.listAddObj("jkryxzh", skyhzh));
			list.add(OaWorkFlowUtil.listAddObj("skzh", skyhzh));
			list.add(OaWorkFlowUtil.listAddObj("gryhzh", skyhzh));
			list.add(OaWorkFlowUtil.listAddObj("gryxzh", skyhzh));
		}
		/* 收款方开户行 */
		if (null != parentVO.getZyx9()) {
			String khh = (String) getHyPubBO().findColValue(
					"bd_asslinenum",
					"name",
					"nvl(dr,0) = 0 and pk_asslinenum ='" + parentVO.getZyx9()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("skfkhh", khh));
			list.add(OaWorkFlowUtil.listAddObj("skfkhx", khh));
		}
		// 单位银行账户
		if (parentVO.getFkyhzh() != null) {
			String dwyhzh = (String) getHyPubBO().findColValue(
					"bd_bankaccsub",
					"accnum",
					"nvl(dr,0) = 0 and pk_bankaccsub ='" + parentVO.getFkyhzh()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("dwyhzh", dwyhzh));
			list.add(OaWorkFlowUtil.listAddObj("fkyhzh", dwyhzh));

		}
		if (parentVO.getCashitem() != null) {
			// 现金流量项目
			String xjllxm = (String) getHyPubBO().findColValue(
					"bd_cashflow",
					"name",
					"nvl(dr,0) = 0 and pk_cashflow ='" + parentVO.getCashitem()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xjllxm", xjllxm));
		}
		// 报销金额
		if (null != parentVO.getVat_amount()) {
			list.add(OaWorkFlowUtil.listAddObj("bxje", parentVO.getVat_amount()
					.setScale(2, UFDouble.ROUND_HALF_UP) + ""));
		}
		/* 冲借款金额 */
		if (null != parentVO.getCjkbbje()) {
			String cjkje = ((UFDouble) (parentVO.getCjkbbje() == null ? "0.00"
					: parentVO.getCjkbbje())).setScale(2,
					UFDouble.ROUND_HALF_UP)
					+ "";
			list.add(OaWorkFlowUtil.listAddObj("cjkje", cjkje));
		}
		// 支付金额
		if (null != parentVO.getZfbbje()) {
			list.add(OaWorkFlowUtil.listAddObj("zfje", parentVO.getZfbbje()
					.setScale(2, UFDouble.ROUND_HALF_UP) + ""));
		}
		// 附件张数
		if (null != parentVO.getZfbbje()) {
			list.add(OaWorkFlowUtil.listAddObj("fjzs", parentVO.getFjzs() + ""));
		}
		// 现金流量项目
		if (parentVO.getCashitem() != null) {
			String xjllxm = (String) getHyPubBO().findColValue(
					"bd_cashflow",
					"name",
					"nvl(dr,0) = 0 and pk_cashflow  ='"
							+ parentVO.getCashitem() + "'");
			list.add(OaWorkFlowUtil.listAddObj("xjllxm", xjllxm));
		}
		// 凭证号
		list.add(OaWorkFlowUtil.listAddObj("pzh", parentVO.getZyx18() + ""));
		// 收款对象
		if (parentVO.getPaytarget() != null) {
			String skdxstr = parentVO.getPaytarget() + "";
			int skdxint = Integer.parseInt(skdxstr);
			String skdx = "";
			if (skdxint == 0) {
				skdx = "员工";
			} else if (skdxint == 1) {
				skdx = "供应商";
			} else if (skdxint == 2) {
				skdx = "客户";
			}
			list.add(OaWorkFlowUtil.listAddObj("skdx", skdx));
		}
		// 客商银行账户
		if (parentVO.getCustaccount() != null) {
			String ksyhzh = (String) getHyPubBO().findColValue(
					"bd_bankaccsub",
					"accnum",
					"nvl(dr,0) = 0 and pk_bankaccsub ='"
							+ parentVO.getCustaccount() + "'");
			list.add(OaWorkFlowUtil.listAddObj("ksyhzh", ksyhzh));
		}
		// 申请人员
		if (parentVO.getJkbxr() != null) {
			String sqry = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and   pk_psndoc  ='" + parentVO.getJkbxr()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sqry", sqry));
			list.add(OaWorkFlowUtil.listAddObj("sqr", sqry));
		}
		// 固定资产项目
		if (parentVO.getZyx15() != null) {
			String gdzcxm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc  ='" + parentVO.getZyx15()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("gdzcxm", gdzcxm));
		}
		// 项目明细
		if (parentVO.getZyx11() != null) {
			String xmmx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc  ='" + parentVO.getZyx11()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xmmx", xmmx));
		}
		// 工程-合同是否载明不含税额
		if (parentVO.getZyx8() != null) {
			String sfzm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc  ='" + parentVO.getZyx8()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sfzm", sfzm));
		}
		// 工程-合同类型
		if (parentVO.getZyx87() != null) {
			String htlx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc  ='" + parentVO.getZyx87()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htlx", htlx));
		}
		// 项目明细
		if (parentVO.getZyx11() != null) {
			String xmmx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and   pk_defdoc  ='" + parentVO.getZyx11()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xmmx", xmmx));
		}
		// 费用类型
		if (parentVO.getZyx15() != null) {
			String fylx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc  ='" + parentVO.getZyx15()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fylx", fylx));
		}
		// 用印类型
		if (parentVO.getZyx5() != null) {
			String yylxbm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc  ='" + parentVO.getZyx5()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("yylxbm", yylxbm));
			String yylx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc  ='" + parentVO.getZyx5()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("yylx", yylx));
		}

		if (parentVO.getZyx15() != null && parentVO.getZyx15().length() >= 19) {
			// 代控部门主键
			list.add(OaWorkFlowUtil.listAddObj("dkbmzj", parentVO.getZyx15()));
			// 代控部门编码
			String dkbmbm = (String) getHyPubBO().findColValue("org_dept",
					"code",
					"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getZyx15() + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbm));
			// 代控部门名称
			String dkbm = (String) getHyPubBO().findColValue("org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getZyx15() + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", dkbm));
		} else {
			if (parentVO.getFydeptid() != null) {
				// 代控部门主键
				list.add(OaWorkFlowUtil.listAddObj("dkbmzj",
						parentVO.getFydeptid()));
				// 代控部门编码
				String dkbmbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"code",
						"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getFydeptid()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbm));
				// 代控部门名称
				String dkbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getFydeptid()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("dkbm", dkbm));
			}
		}

		if (null != parentVO.getZyx14()) {
			String fklx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc  ='" + parentVO.getZyx14()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fklx", fklx));
		}
		// 是否属于安全经费
		if (null != parentVO.getZyx12()) {
			String sfsyaqjf = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc  ='" + parentVO.getZyx12()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sfsyaqjf", sfsyaqjf));
		}
		// 还款单 - 还款银行
		if (parentVO.getZyx24() != null) {
			String hkyh = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getZyx24()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("hkyh", hkyh));
			list.add(OaWorkFlowUtil.listAddObj("zzyx", hkyh));// 能投转账银行
			list.add(OaWorkFlowUtil.listAddObj("zzyh", hkyh));// 能投转账银行
		}
		// 借款人
		if (parentVO.getReceiver() != null) {
			String jkr = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc ='" + parentVO.getReceiver()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("jkr", jkr));
			list.add(OaWorkFlowUtil.listAddObj("skr", jkr));
			list.add(OaWorkFlowUtil.listAddObj("hkr", jkr));
		}
		// 结算方式
		String jsfs = (String) getHyPubBO().findColValue("bd_balatype", "name",
				"nvl(dr,0) = 0 and pk_balatype ='" + parentVO.getJsfs() + "'");
		list.add(OaWorkFlowUtil.listAddObj("jsfs", jsfs));
		// 紧急
		if (parentVO.getIsexpedited() != null) {
			String jj = "否";
			if ("Y".equals(parentVO.getIsexpedited().toString())) {
				jj = "是";
			}
			list.add(OaWorkFlowUtil.listAddObj("jj", jj));
		}
		// 项目编号
		String project_code = (String) getHyPubBO().findColValue("bd_project",
				"project_code",
				"nvl(dr,0) = 0 and pk_project ='" + parentVO.getJobid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("project_code", project_code));
		// 项目名称
		String project_name = (String) getHyPubBO().findColValue("bd_project",
				"project_name",
				"nvl(dr,0) = 0 and pk_project ='" + parentVO.getJobid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("project_name", project_name));
		// 供应商名称
		if (null != parentVO.getHbbm()) {
			SupplierVO supplierVO = (SupplierVO) getHyPubBO()
					.queryByPrimaryKey(SupplierVO.class, parentVO.getHbbm());
			String name = supplierVO.getName();
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name", name));
			list.add(OaWorkFlowUtil.listAddObj("skf", name));// 收款方
		}
		// 供应商银行账户
		String gysyhzh = (String) getHyPubBO().findColValue(
				"bd_bankaccsub",
				"accnum",
				"nvl(dr,0) = 0 and pk_bankaccsub ='"
						+ parentVO.getCustaccount() + "'");
		list.add(OaWorkFlowUtil.listAddObj("gysyhzh", gysyhzh));
		// 费用分摊
		if (parentVO.getIscostshare() != null) {
			String fyft = "否";
			if ("Y".equals(parentVO.getIscostshare().toString())) {
				fyft = "是";
			}
			list.add(OaWorkFlowUtil.listAddObj("fyft", fyft));
			list.add(OaWorkFlowUtil.listAddObj("ft", fyft));
		}
		// 支付方式
		if (parentVO.getZyx2() != null) {
			String zffs = (String) getHyPubBO().findColValue(
					"bd_balatype",
					"name",
					"nvl(dr,0) = 0 and pk_balatype ='" + parentVO.getZyx2()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("zffs", zffs));
		}
		// 申请类型
		if (parentVO.getZyx7() != null) {
			String sqlx = (String) getHyPubBO()
					.findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ parentVO.getZyx7() + "'");
			list.add(OaWorkFlowUtil.listAddObj("sqlx", sqlx));
		}
		// ---固定字段
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
		// 原能投OA字段START
		if (null != userVO.getUser_name()) {
			list.add(OaWorkFlowUtil.listAddObj("zdrmc", userVO.getUser_name()));
		}
		// 身份证
		list.add(OaWorkFlowUtil.listAddObj("zdrzj", idCard));
		/* 单据号 */
		list.add(OaWorkFlowUtil.listAddObj("djh", parentVO.getDjbh()));
		/* 单据日期 */
		list.add(OaWorkFlowUtil.listAddObj("djrq", parentVO.getDjrq().getYear()
				+ "-" + parentVO.getDjrq().getStrMonth() + "-"
				+ parentVO.getDjrq().getStrDay()));
		/* 借款金额 */
		if (null != parentVO.getTotal()) {
			list.add(OaWorkFlowUtil.listAddObj("jkje", parentVO.getTotal()
					.setScale(2, UFDouble.ROUND_HALF_UP) + ""));
		}
		/* 借款金额大写 */
		/*
		 * if (null != parentVO.getTotal()) {
		 * list.add(OaWorkFlowUtil.listAddObj("jkjedx",
		 * ConvertUpMoney.toChinese(parentVO.getTotal().setScale(2,
		 * UFDouble.ROUND_HALF_UP) + ""))); }
		 */
		/* 凭证号 */
		if (null != parentVO.getZyx29()) {
			list.add(OaWorkFlowUtil.listAddObj("pzh", parentVO.getZyx29()));
		}
		/* 支付日期 */
		if (null != parentVO.getZyx21()) {
			list.add(OaWorkFlowUtil.listAddObj("zfrq", parentVO.getZyx21()));
		}
		/* CBS状态 */
		if (null != parentVO.getZyx22()) {
			list.add(OaWorkFlowUtil.listAddObj("cbszt", parentVO.getZyx22()));
		}
		/* 付款事由 */
		if (null != parentVO.getZyx30()) {
			list.add(OaWorkFlowUtil.listAddObj("fksy", parentVO.getZyx30()));
			list.add(OaWorkFlowUtil.listAddObj("ccsy", parentVO.getZyx30()));
			/* 退费说明 */
			list.add(OaWorkFlowUtil.listAddObj("tfsm", parentVO.getZyx30()));
			/* 还款事由 */
			list.add(OaWorkFlowUtil.listAddObj("hksy", parentVO.getZyx30()));
		}
		/* 退费金额大写 */
		/*
		 * if (null != parentVO.getVat_amount()) {
		 * list.add(OaWorkFlowUtil.listAddObj("tfjedx",
		 * ConvertUpMoney.toChinese(parentVO.getVat_amount() + ""))); }
		 */
		/* 附件张数 */
		if (null != parentVO.getFjzs()) {
			list.add(OaWorkFlowUtil.listAddObj("fjzs", parentVO.getFjzs() + ""));
		}
		if ("263X".equals(billType)) {// 借款单
			// 申请单位
			String sqdw = (String) getHyPubBO().findColValue(
					"org_adminorg",
					"name",
					"nvl(dr,0) = 0 and pk_adminorg ='" + parentVO.getDwbm()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sqdw", sqdw));
			list.add(OaWorkFlowUtil.listAddObj("sqrdw", sqdw));
			String fycddw = (String) getHyPubBO().findColValue("org_orgs",
					"name",
					"nvl(dr,0) = 0 and pk_org ='" + parentVO.getFydwbm() + "'");
			list.add(OaWorkFlowUtil.listAddObj("fycddw", fycddw));
			// 申请部门
			if (null != parentVO.getDeptid()) {
				String sqbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDeptid()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("sqbm", sqbm));
				list.add(OaWorkFlowUtil.listAddObj("sqrbm", sqbm));
				list.add(OaWorkFlowUtil.listAddObj("fycdbm", sqbm));
			}
			list.add(OaWorkFlowUtil.listAddObj("zj", parentVO.getDeptid()));
			list.add(OaWorkFlowUtil.listAddObj("total", parentVO.getTotal()
					.toString()));
		} else if ("264X".equals(billType)) {// 报销单
			// 申请单位
			String sqdw = (String) getHyPubBO().findColValue(
					"org_adminorg",
					"name",
					"nvl(dr,0) = 0 and pk_adminorg ='" + parentVO.getDwbm()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sqdw", sqdw));
			// 费用承担单位
			String fycddw = (String) getHyPubBO().findColValue("org_orgs",
					"name",
					"nvl(dr,0) = 0 and pk_org ='" + parentVO.getFydwbm() + "'");
			list.add(OaWorkFlowUtil.listAddObj("fycddw", fycddw));
			// 申请部门
			String sqbm = (String) getHyPubBO()
					.findColValue(
							"org_dept",
							"name",
							"nvl(dr,0) = 0 and pk_dept ='"
									+ parentVO.getDeptid() + "'");
			list.add(OaWorkFlowUtil.listAddObj("sqbm", sqbm));
			list.add(OaWorkFlowUtil.listAddObj("sqrbm", sqbm));
			// 费用承担部门
			String fycdbm = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getFydeptid()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fycdbm", fycdbm));
			// 申请单号
			if (parentVO.getPk_item() != null) {
				String sqdh = (String) getHyPubBO().findColValue(
						"er_mtapp_bill",
						"billno",
						"nvl(dr,0) = 0 and pk_mtapp_bill ='"
								+ parentVO.getPk_item() + "'");
				list.add(OaWorkFlowUtil.listAddObj("sqdh", sqdh));
			}
			list.add(OaWorkFlowUtil.listAddObj("zj", parentVO.getFydeptid()));
			/* 退费所属部门 */
			if (parentVO.getZyx15() != null) {
				String bm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getZyx15()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("tfszbm", bm));
			}
			/* 退费金额 */
			if (null != parentVO.getVat_amount()) {
				list.add(OaWorkFlowUtil.listAddObj("tfje", parentVO
						.getVat_amount().setScale(2, UFDouble.ROUND_HALF_UP)
						+ ""));
			}
			/* 收款方银行账号 */
			if (null != parentVO.getZyx14()) {
				list.add(OaWorkFlowUtil.listAddObj("skfyxzh",
						parentVO.getZyx14()));
			}
		} else if ("2647".equals(billType)) {// 还款单
			// 申请单位
			String sqdw = (String) getHyPubBO().findColValue(
					"org_adminorg",
					"name",
					"nvl(dr,0) = 0 and pk_adminorg ='" + parentVO.getDwbm()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sqdw", sqdw));
			list.add(OaWorkFlowUtil.listAddObj("sqrdw", sqdw));
			// 申请部门
			if (null != parentVO.getDeptid()) {
				String sqbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDeptid()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("sqbm", sqbm));
			}
			// 费用承担单位
			if (parentVO.getFydwbm() != null) {
				String fycddw = (String) getHyPubBO().findColValue(
						"org_orgs",
						"name",
						"nvl(dr,0) = 0 and pk_org ='" + parentVO.getFydwbm()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("fycddw", fycddw));
			}
		}
		// 事由填写规范
		if (null != parentVO.getZyx3()) {
			String sytxgf = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getZyx3()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sytxgf", sytxgf));
		}
		// 员工借款单
		if ("263X-Cxx-YGJKD".equals(parentVO.getDjlxbm())) {
			list.add(OaWorkFlowUtil.listAddObj("jklx", "1"));
		} else if ("263X-Cxx-GCDWJKD".equals(parentVO.getDjlxbm())) {
			// 单位借款单
			list.add(OaWorkFlowUtil.listAddObj("jklx", "0"));
		}
		// ---------其他字段end
		// JSONArray arr = JSONArray.fromObject(list);
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getNewDtaileDataMap(JKBXVO temp, String tableName,
			String billType) throws BusinessException {
		// 获取详细信息
		List dtlist = new ArrayList();
		Map bodyMap = null;
		String jylx = temp.getParentVO().getDjlxbm();
		if (RL_HKD_006.equals(jylx)) {
			BXVO bxVO = (BXVO) temp;
			bodyMap = getBody((BXBusItemVO[]) bxVO.getChildrenVO(), tableName);
			if (bodyMap != null) {
				dtlist.add(bodyMap);
			}
			if (bxVO.getTableVO("er_bxcontrast") != null
					&& bxVO.getTableVO("er_bxcontrast").length > 0
					&& !"264X-Cxx-JBCFBX".equals(jylx)) {
				Map bodyMap2 = getCXBody2(
						(BxcontrastVO[]) bxVO.getTableVO("er_bxcontrast"),
						tableName, billType);
				if (bodyMap2 != null) {
					dtlist.add(bodyMap2);
				}
			}
		} else {
			if (billType.contains("263X")) {
				JKVO jkVO = (JKVO) temp;
				bodyMap = getBody((BXBusItemVO[]) jkVO.getChildrenVO(),
						tableName);
			} else {
				if (billType.contains("264X")) {
					BXVO bxVO = (BXVO) temp;
					bodyMap = getBody((BXBusItemVO[]) bxVO.getChildrenVO(),
							tableName);
					if (bxVO.getTableVO("er_bxcontrast") != null
							&& bxVO.getTableVO("er_bxcontrast").length > 0
							&& !"264X-Cxx-JBCFBX".equals(jylx)) {
						Map bodyMap2 = getCXBody2(
								(BxcontrastVO[]) bxVO
										.getTableVO("er_bxcontrast"),
								tableName, billType);
						if (bodyMap2 != null) {
							dtlist.add(bodyMap2);
						}
					}
				}
			}
			if (bodyMap != null) {
				dtlist.add(bodyMap);
			}
			// 差旅费报销单
			if ("264X-Cxx-RQCL".equals(jylx)) {
				BXVO bxVO = (BXVO) temp;
				Map bodyMap4 = getBody4((BXBusItemVO[]) bxVO.getChildrenVO(),
						tableName);
				if (bodyMap4 != null) {
					dtlist.add(bodyMap4);
				}
			}
			if (temp.getTableVO("er_cshare_detail") != null
					&& temp.getTableVO("er_cshare_detail").length > 0
					&& !"264X-Cxx-JBCFBX".equals(jylx)) {
				Map bodyFtMap = getFtBody(
						(CShareDetailVO[]) temp.getTableVO("er_cshare_detail"),
						tableName);
				dtlist.add(bodyFtMap);
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(dtlist, jsonConfig);
		return dtlistString;
	}

	private Map getFtBody(CShareDetailVO[] bvo, String tableName)
			throws BusinessException {
		// TODO Auto-generated method stub
		Map dtMap = new HashMap();
		// 接口获取
		dtMap.put("tableDBName", tableName + "_dt2");
		List workflowRequestTableRecords = new ArrayList();
		for (CShareDetailVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 承担单位
			if (null != temp.getAssume_org()) {
				String cddw = (String) getHyPubBO().findColValue(
						"org_orgs",
						"name",
						"nvl(dr,0) = 0 and pk_org ='" + temp.getAssume_org()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"cddw", cddw));
			}
			// 承担部门
			if (null != temp.getAssume_dept()) {
				String cdbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept ='" + temp.getAssume_dept()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"cdbm", cdbm));
			}

			// 收支项目
			if (null != temp.getPk_iobsclass()) {
				String szxm = (String) getHyPubBO().findColValue(
						"bd_inoutbusiclass",
						"name",
						"nvl(dr,0) = 0 and pk_inoutbusiclass ='"
								+ temp.getPk_iobsclass() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"szxm", szxm));
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

	private Map getBody(BXBusItemVO[] bvo, String tableName)
			throws BusinessException {
		Map dtMap = new HashMap();
		// 接口获取
		dtMap.put("tableDBName", tableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (BXBusItemVO temp : bvo) {
			if ("arap_bxbusitem".equals(temp.getTablecode())
					|| "jk_busitem".equals(temp.getTablecode())
					|| "bx_busitem".equals(temp.getTablecode())
					|| "er_busitem".equals(temp.getTablecode())) {
				// 构造数据
				List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
						.transBean2Map(temp);
				// 其他字段------begin
				// 报销类型
				if (temp.getPk_reimtype() != null) {
					String bxlx = (String) getHyPubBO().findColValue(
							"er_reimtype",
							"name",
							"nvl(dr,0) = 0 and pk_reimtype ='"
									+ temp.getPk_reimtype() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"bxlx", bxlx));
				}
				//
				if (temp.getDefitem2() != null) {

					// 返程日期
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"fcrq", temp.getDefitem2()));
				}
				if (temp.getDefitem2() != null) {
					// 出发日期
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"defitem1", temp.getDefitem1()));
				}
				// 备注
				if (temp.getDefitem31() != null) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"bz", temp.getDefitem31()));
				}
				// 项目
				if (temp.getJobid() != null) {
					// 项目编码
					String project_code = (String) getHyPubBO().findColValue(
							"bd_project",
							"project_code",
							"nvl(dr,0) = 0 and pk_project = '"
									+ temp.getJobid() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"project_code", project_code));
					// 项目名称
					String project_name = (String) getHyPubBO().findColValue(
							"bd_project",
							"project_name",
							"nvl(dr,0) = 0 and pk_project = '"
									+ temp.getJobid() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"project_name", project_name));
				}
				// 出发地点
				if (temp.getDefitem3() != null) {
					String cfdd = (String) getHyPubBO().findColValue(
							"bd_addressdoc",
							"name",
							"nvl(dr,0) = 0 and pk_addressdoc ='"
									+ temp.getDefitem3() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"cfdd", cfdd));
				}
				// 目的地点
				if (temp.getDefitem4() != null) {
					String mddd = (String) getHyPubBO().findColValue(
							"bd_addressdoc",
							"name",
							"nvl(dr,0) = 0 and pk_addressdoc ='"
									+ temp.getDefitem4() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"mddd", mddd));
				}
				// 收款对象
				if (temp.getPaytarget() != null) {
					String skdxstr = temp.getPaytarget() + "";
					int skdxint = Integer.parseInt(skdxstr);
					String skdx = "";
					if (skdxint == 0) {
						skdx = "员工";
					} else if (skdxint == 1) {
						skdx = "供应商";
					} else if (skdxint == 2) {
						skdx = "客户";
					}
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"skdx", skdx));
				}
				/* 开户行 */
				String khh = (String) getHyPubBO().findColValue(
						"bd_asslinenum",
						"name",
						"nvl(dr,0) = 0 and pk_asslinenum ='"
								+ temp.getDefitem7() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("khh",
						khh));
				// 发票类型
				if (temp.getDefitem6() != null) {
					String fplx = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem6() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"fplx", fplx));
				}
				// 是否进成本
				if (temp.getDefitem32() != null) {
					String sfjcb = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem32() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"sfjcb", sfjcb));
				}
				// 金额合计
				if (temp.getVat_amount() != null) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"jehj", temp.getVat_amount() + ""));
				}
				// 不含税金额
				if (temp.getDefitem15() != null) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"bhsje", temp.getDefitem15() + ""));
				}
				// 税率
				if (temp.getDefitem17() != null) {
					String sl = "";
					if (temp.getDefitem17().length() > 15) {
						sl = (String) getHyPubBO().findColValue(
								"bd_defdoc",
								"name",
								"nvl(dr,0) = 0 and pk_defdoc ='"
										+ temp.getDefitem17() + "'");
					} else {
						sl = temp.getDefitem17();
					}
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"sl", sl));
				}
				if (temp.getSzxmid() != null) {
					// 收支项目
					String szxm = (String) getHyPubBO().findColValue(
							"bd_inoutbusiclass",
							"name",
							"nvl(dr,0) = 0 and pk_inoutbusiclass ='"
									+ temp.getSzxmid() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"szxm", szxm));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"yslb", szxm));
				}
				if (temp.getDefitem11() != null) {
					// 是否属于安全经费
					String sfsyaqjf = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem11() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"sfsyaqjf", sfsyaqjf));
				}
				if (temp.getCustaccount() != null) {
					// 客商银行账户
					String ksyhzh = (String) getHyPubBO().findColValue(
							"bd_bankaccbas",
							"name",
							"nvl(dr,0) = 0 and pk_bankaccbas ='"
									+ temp.getCustaccount() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"ksyhzh", ksyhzh));
				}
				if (temp.getDeptid() != null) {
					// 费用发生部门
					String fyfsbm = (String) getHyPubBO().findColValue(
							"org_dept",
							"name",
							"nvl(dr,0) = 0 and pk_dept ='" + temp.getDeptid()
									+ "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"fyfsbm", fyfsbm));
				}
				// 收款人
				if (temp.getReceiver() != null) {
					String skr = (String) getHyPubBO().findColValue(
							"bd_psndoc",
							"name",
							"nvl(dr,0) = 0 and pk_psndoc ='"
									+ temp.getReceiver() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"skr", skr));
				}
				// 个人银行账户
				if (temp.getSkyhzh() != null) {
					String gryhzh = (String) getHyPubBO().findColValue(
							"bd_bankaccsub",
							"accnum",
							"nvl(dr,0) = 0 and pk_bankaccsub ='"
									+ temp.getSkyhzh() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"gryhzh", gryhzh));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"skzh", gryhzh));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"jkryhzh", gryhzh));
				}
				// 出差区域类型
				if (temp.getDefitem13() != null) {
					String ccqylx = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem13() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"ccqylx", ccqylx));
				}
				// 客商银行账户
				if (temp.getCustaccount() != null) {
					String ksyhzh = (String) getHyPubBO().findColValue(
							"bd_bankaccsub",
							"accnum",
							"nvl(dr,0) = 0 and pk_bankaccsub ='"
									+ temp.getCustaccount() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"ksyhzh", ksyhzh));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"gysyhzh", ksyhzh));
				}
				// 供应商名称
				if (null != temp.getHbbm()) {
					SupplierVO supplierVO = (SupplierVO) getHyPubBO()
							.queryByPrimaryKey(SupplierVO.class, temp.getHbbm());
					String name = supplierVO.getName();
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"pk_supplier_name", name));
				}
				// 收款对象
				String paytarget = "";
				if (temp.getPaytarget() != null) {
					int dx = temp.getPaytarget();
					if (dx == 0) {
						paytarget = "员工";
					} else if (dx == 1) {
						paytarget = "供应商";
					} else if (dx == 2) {
						paytarget = "客户";
					}
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"paytarget", paytarget));
				// 客户
				String kh = (String) getHyPubBO().findColValue(
						"bd_customer",
						"name",
						"nvl(dr,0) = 0 and pk_customer ='" + temp.getCustomer()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("kh",
						kh));
				// 资产类别
				if (temp.getDefitem9() != null) {
					String zclb = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem9() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"zclb", zclb));
				}
				// 资金类别
				if (temp.getDefitem10() != null) {
					String zjlb = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem10() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"zjlb", zjlb));
				}
				// 计量单位
				String dwstr = "";
				if (null != temp.getDefitem12()) {
					dwstr = (String) getHyPubBO().findColValue(
							"bd_measdoc",
							"name",
							"nvl(dr,0) = 0 and pk_measdoc = '"
									+ temp.getDefitem12() + "'");
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dw",
						dwstr));

				// 申请单位
				if (temp.getDwbm() != null) {
					String sqdw = (String) getHyPubBO().findColValue(
							"org_adminorg",
							"name",
							"nvl(dr,0) = 0 and pk_adminorg ='" + temp.getDwbm()
									+ "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"sqdw", sqdw));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"fycddw", sqdw));
				}
				// 其他业务支付类型
				if (temp.getDefitem21() != null) {
					String qtywzflx = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem21() + "'");

					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"qtywzflx", qtywzflx));
				}
				// 申请部门
				String sqbm = (String) getHyPubBO()
						.findColValue(
								"org_dept",
								"name",
								"nvl(dr,0) = 0 and pk_dept ='"
										+ temp.getDeptid() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"sqbm", sqbm));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fycdbm", sqbm));
				// 交通工具
				if (temp.getDefitem5() != null) {
					String jtgjstr = temp.getDefitem5();
					String jtgj = "";
					if (jtgjstr != null && jtgjstr.length() > 15) {
						jtgj = (String) getHyPubBO().findColValue(
								"bd_defdoc",
								"name",
								"nvl(dr,0) = 0 and pk_defdoc ='"
										+ temp.getDefitem5() + "'");
					} else {
						int jtgjint = Integer.parseInt(jtgjstr);
						if (jtgjint == 0) {
							jtgj = "其他";
						} else if (jtgjint == 1) {
							jtgj = "城铁地铁";
						} else if (jtgjint == 3) {
							jtgj = "公交车";
						} else if (jtgjint == 4) {
							jtgj = "公务车";
						} else if (jtgjint == 5) {
							jtgj = "长途汽车";
						} else if (jtgjint == 6) {
							jtgj = "火车";
						} else if (jtgjint == 7) {
							jtgj = "飞机";
						}
					}
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"jtgj", jtgj));
				}
				// 能投原字段
				/* 借款事由 */
				if (null != temp.getDefitem30()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"jksy", temp.getDefitem30()));
				}
				/* 金额 */
				if (null != temp.getAmount()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"je", temp.getAmount() + ""));
				}
				/* 金额合计 */
				if (null != temp.getVat_amount()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"jehj",
							temp.getVat_amount().setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));

					/* 退费金额 */
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"tfje",
							temp.getVat_amount().setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));
				}
				/* 税额 */
				if (null != temp.getDefitem19()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"se", temp.getDefitem19()));
				}
				/* 退费类型 */
				if (null != temp.getDefitem40()) {
					String tflx = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem40() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"tflx", tflx));
				}
				/* 是否热电用户 */
				if (null != temp.getDefitem50()) {
					String sfrdyh = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem50() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"sfrdyh", sfrdyh));
				}
				/* 出差人数 */
				if (null != temp.getDefitem45()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"ccrs", temp.getDefitem45()));
				}

				/* 出发日期 */
				if (null != temp.getDefitem1()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"cfrq", temp.getDefitem1()));
				}
				/* 返程日期 */
				if (null != temp.getDefitem2()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"fcrq", temp.getDefitem2()));
				}
				/* 出差天数 */
				if (null != temp.getDefitem9()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"ccts", temp.getDefitem9()));
				}
				/* 标准 */
				if (null != temp.getDefitem46()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"bz", temp.getDefitem46() + ""));
				}
				/* 备注 */
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz1",
						temp.getDefitem31()));
				/* 还款金额 */
				if (null != temp.getCjkybje()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"hkje",
							temp.getCjkybje().setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));
				}
				/* 加班事由 */
				if (null != temp.getDefitem20()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"jbsy", temp.getDefitem20()));
				}
				/* 采暖季 */
				if (null != temp.getDefitem42()) {
					String cnj = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem42() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"cnj", cnj));
				}
				/* 应交金额 */
				if (null != temp.getDefitem43()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"yjje",
							new UFDouble(temp.getDefitem43() == null ? "0.00"
									: temp.getDefitem43()).setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));
				}

				/* 已交金额 */
				if (null != temp.getDefitem44()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"yjje1",
							new UFDouble(temp.getDefitem44() == null ? "0.00"
									: temp.getDefitem44()).setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));
				}
				// 其他字段------end
				Map workflowRequestTableFieldsMap = new HashMap();
				workflowRequestTableFieldsMap.put("recordOrder", "0");
				workflowRequestTableFieldsMap.put("workflowRequestTableFields",
						workflowRequestTableFields);
				workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
			}
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private Map getCXBody2(BxcontrastVO[] bvo, String tableName,
			String transi_type) throws BusinessException {
		Map dtMap = new HashMap();
		// 接口获取
		/*
		 * if ("264X-Cxx-RQCL".equals(transi_type)) { dtMap.put("tableDBName",
		 * tableName + "_dt3"); }else{ dtMap.put("tableDBName", tableName +
		 * "_dt2"); }
		 */
		dtMap.put("tableDBName", tableName + "_dt3");
		List workflowRequestTableRecords = new ArrayList();
		for (BxcontrastVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
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

	// 业务招待
	private Map getBody4(BXBusItemVO[] bvo, String tableName)
			throws BusinessException {
		Map dtMap = new HashMap();
		// 接口获取
		dtMap.put("tableDBName", tableName + "_dt4");
		List workflowRequestTableRecords = new ArrayList();
		for (BXBusItemVO temp : bvo) {
			if ("other".equals(temp.getTablecode())) {
				// 构造数据
				List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
						.transBean2Map(temp);
				// 其他字段------begin
				// 收支项目
				String szxm = (String) getHyPubBO().findColValue(
						"bd_inoutbusiclass",
						"name",
						"nvl(dr,0) = 0 and pk_inoutbusiclass ='"
								+ temp.getSzxmid() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"szxm", szxm));
				// 收款人
				if (null != temp.getReceiver()) {
					String skr = (String) getHyPubBO().findColValue(
							"bd_psndoc",
							"name",
							"nvl(dr,0) = 0 and pk_psndoc ='"
									+ temp.getReceiver() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"skr", skr));
				}
				// 个人银行账户
				if (null != temp.getSkyhzh()) {
					String gryhzh = (String) getHyPubBO().findColValue(
							"bd_bankaccsub",
							"accnum",
							"nvl(dr,0) = 0 and pk_bankaccsub ='"
									+ temp.getSkyhzh() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"gryhzh", gryhzh));
				}
				// 其他字段------end
				Map workflowRequestTableFieldsMap = new HashMap();
				workflowRequestTableFieldsMap.put("recordOrder", "0");
				workflowRequestTableFieldsMap.put("workflowRequestTableFields",
						workflowRequestTableFields);
				workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
			}
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private WorkFlowBill getFlowBill(JKBXVO temp, JKBXHeaderVO hvo,
			String billCode, String workFId, String flowName)
			throws BusinessException {
		OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
		workFlowVO.setPrimaryKey(hvo.getPk_jkbx());
		workFlowVO.setPkGroup(hvo.getPk_group());
		workFlowVO.setPkOrg(hvo.getPk_org());
		workFlowVO.setBillMaker(hvo.getCreator());
		workFlowVO.setCreator(hvo.getCreator());
		workFlowVO.setBillCode(billCode);
		// 接口获取
		// workFlowVO.setWorkflowId(OaWorkFlowUtil.getOAFlowID(workFId));
		workFlowVO.setWorkflowId(workFId);
		workFlowVO.setWorkflowName(flowName);
		WorkFlowBill bill = OaWorkFlowUtil.getWorkFlowBill(workFlowVO);
		bill.setDef3("ZT");
		bill.setDef4(hvo.getDjbh());// 单据编号
		// 制单人身份证号
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				temp.getParentVO().getCreator());
		PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(
				PsndocVO.class, userVO.getPk_psndoc());
		bill.setDef5(psndoc.getId());
		return bill;
	}

	/*
	 * private Map<String, String> billTypeMap() { Map<String, String> typeMap =
	 * new HashMap<String, String>();
	 * 
	 * String JH_EASQ = "261X-Cxx-RQEASQ"; typeMap.put(JH_EASQ, "EA申请单");
	 * 
	 * String JH_CEASQ = "261X-Cxx-RQCEASQ"; typeMap.put(JH_CEASQ, "CEA申请单");
	 * 
	 * String JH_QT = "261X-Cxx-RQQT"; typeMap.put(JH_QT, "其它申请单");
	 * 
	 * String JH_YGJK = "263X-Cxx-RQYGJK"; typeMap.put(JH_YGJK, "借款单-员工借款");
	 * 
	 * String JH_DWJK = "263X-Cxx-DWJKD"; typeMap.put(JH_DWJK, "借款单-单位借款");
	 * 
	 * String JH_WPFK = "263X-Cxx-RQWPFK"; typeMap.put(JH_WPFK, "借款单-无发票付款");
	 * 
	 * String JH_HKD = "2647"; typeMap.put(JH_HKD, "还款单");
	 * 
	 * String JH_CL = "264X-Cxx-RQCL"; typeMap.put(JH_CL, "差旅费报销");
	 * 
	 * String JH_YGBX = "264X-Cxx-RQYGBX"; typeMap.put(JH_YGBX, "费用报销单-员工报销");
	 * 
	 * String JH_DWZF = "264X-Cxx-RQDWZF"; typeMap.put(JH_DWZF, "费用报销单-对外支付");
	 * 
	 * String JH_YHDK = "264X-Cxx-RQYHDK"; typeMap.put(JH_YHDK, "费用报销单-银行代扣");
	 * 
	 * String JH_CLFY = "264X-Cxx-RQCLFY"; typeMap.put(JH_CLFY, "车辆费用报销单");
	 * 
	 * String JH_QTFFY = "264X-Cxx-RQQT"; typeMap.put(JH_QTFFY, "其他非费用支付单");
	 * 
	 * String JH_EABX = "264X-Cxx-RQEABX"; typeMap.put(JH_EABX, "EA报销单");
	 * 
	 * String JH_CEABX = "264X-Cxx-RQCEABX"; typeMap.put(JH_CEABX, "CEA报销单");
	 * 
	 * String JH_FYTZD = "264a"; typeMap.put(JH_FYTZD, "费用调整单");
	 * 
	 * String JH_GCFYBXD = "264X-Cxx-GCFYBXD"; typeMap.put(JH_GCFYBXD, "费用报销单");
	 * 
	 * String JH_GCDWJKD = "263X-Cxx-GCDWJKD"; typeMap.put(JH_GCDWJKD, "单位借款单");
	 * 
	 * // 城市照明新增 String JH_YGJKD = "263X-Cxx-YGJKD"; typeMap.put(JH_YGJKD,
	 * "员工借款单");
	 * 
	 * String JH_CLFJKD = "263X-Cxx-CYFJK"; typeMap.put(JH_CLFJKD, "差旅费借款单");
	 * 
	 * String JH_YGBXD = "264X-Cxx-YGFYBXD"; typeMap.put(JH_YGBXD, "员工报销单");
	 * 
	 * String JH_CLFBX = "2641"; typeMap.put(JH_CLFBX, "差旅费报销单");
	 * 
	 * String JBCFBX = "264X-Cxx-JBCFBX"; typeMap.put(JBCFBX, "加班餐费报销单");
	 * 
	 * // 港华新增 String GH_GHJKYSQ = "263X-Cxx-GHJKYSQ"; typeMap.put(GH_GHJKYSQ,
	 * "员工借款单（有事前申请）");
	 * 
	 * String GH_GHJKWSQ = "263X-Cxx-GHJKWSQ"; typeMap.put(GH_GHJKWSQ,
	 * "员工借款单（无事前申请）");
	 * 
	 * String GH_GHQT = "264X-Cxx-GHQT"; typeMap.put(GH_GHQT, "其他付款申请单");
	 * 
	 * String GH_GHCEA_C = "264X-Cxx-GHCEA-C"; typeMap.put(GH_GHCEA_C,
	 * "CEA-C报销单");
	 * 
	 * String GH_GHFYYSQ = "264X-Cxx-GHFYYSQ"; typeMap.put(GH_GHFYYSQ,
	 * "费用付款单（有事前申请）");
	 * 
	 * String GH_GHFYWSQ = "264X-Cxx-GHFYWSQ"; typeMap.put(GH_GHFYWSQ,
	 * "费用付款单（无事前申请）");
	 * 
	 * // 能源集团 String GH_NYGHBX = "264X-Cxx-NYGHBX"; typeMap.put(GH_NYGHBX,
	 * "工会报销付款审批单");
	 * 
	 * String JH_NYFKSP = "264X-Cxx-NYFKSP"; typeMap.put(JH_NYFKSP, "付款审批单");
	 * 
	 * String JH_2631 = "2631"; typeMap.put(JH_2631, "差旅费借款单");
	 * 
	 * String JH_YGFYBXDxm = "264X-Cxx-YGFYBXDxm"; typeMap.put(JH_YGFYBXDxm,
	 * "员工报销单-项目"); return typeMap; }
	 */
}
