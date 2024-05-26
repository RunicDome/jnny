package nc.bs.cmp.transformbill.ace.bp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.cmp.transformbill.plugin.bpplugin.TransformbillPluginPoint;
import nc.bs.cmp.transformbill.rule.TfbCheckBankAccFrozenRule;
import nc.bs.cmp.transformbill.rule.TfbFillCommitDataRule;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.cmp.bill.TransformBillAggVO;
import nc.vo.cmp.bill.TransformBillVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;

// 划账结算   --->提交
@SuppressWarnings({ "restriction", "unchecked", "rawtypes" })
public class AceTransformbillSendApproveBP {
	private static String WorkFId;// OA WorkflowId 流程ID
	// private static String TableName;// OA TableName 表名

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

	public AceTransformbillSendApproveBP() {
	}

	public TransformBillAggVO[] sendApprove(TransformBillAggVO[] clientBills,
			TransformBillAggVO[] originBills) throws BusinessException {
		UpdateBPTemplate<TransformBillAggVO> bp = new UpdateBPTemplate(
				TransformbillPluginPoint.SEND_APPROVE);

		addBeforeRule(bp.getAroundProcesser());

		addAfterRule(bp.getAroundProcesser());
		TransformBillAggVO[] returnVos = (TransformBillAggVO[]) bp.update(
				clientBills, originBills);
		// 提交OA
		sendoa(returnVos);
		return returnVos;
	}

	private void sendoa(TransformBillAggVO[] returnVos)
			throws BusinessException {
		// TODO Auto-generated method stub
		for (int i = 0; i < returnVos.length; i++) {
			TransformBillVO headvo = returnVos[i].getParentVO();
			OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
					headvo.getPk_org());
			String bill_type = headvo.getPk_billtypecode();// 单据类型
			// 中台
			if ("4".equals(orgVO.getDef2())) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
						headvo.getPk_org(), bill_type);
				if (oaVo != null && oaVo.getIsdr() == 0) {
					WorkFId = oaVo.getFlowid();
					// TableName = oaVo.getTablename();
					// 获取主表数据
					JSONArray headData = getNewMainMap(headvo);
					// 获取子表数据
					JSONArray bodyData = null;
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(headvo.getPrimaryKey());
					workFlowVO.setPkGroup(headvo.getPk_group());
					workFlowVO.setPkOrg(headvo.getPk_org());
					workFlowVO.setBillMaker(headvo.getBillmaker());
					workFlowVO.setCreator(headvo.getCreator());
					workFlowVO.setBillCode(bill_type);// 单据类型
					// 接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName(oaVo.getBilltypename());// 单据名称
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					bill.setDef3("ZT");
					bill.setDef4(headvo.getVbillno());
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
			}
		}
	}

	private JSONArray getNewMainMap(TransformBillVO parentVO)
			throws BusinessException {
		// TODO Auto-generated method stub
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
			// throw new BusinessException("制单人身份证号码未维护");
		}
		list.add(OaWorkFlowUtil.listAddObj("userid", idCard));
		// 币种
		if (null != parentVO.getPk_currtype()) {
			String bz = (String) getHyPubBO().findColValue(
					"bd_currtype",
					"name",
					"nvl(dr,0) = 0 and pk_currtype = '"
							+ parentVO.getPk_currtype() + "'");
			list.add(OaWorkFlowUtil.listAddObj("bz", bz));
		}
		// 划出银行
		if (null != parentVO.getTransformoutbank()) {
			String hcyhbm = (String) getHyPubBO().findColValue(
					"bd_bankdoc",
					"code",
					"nvl(dr,0) = 0 and pk_bankdoc = '"
							+ parentVO.getTransformoutbank() + "'");
			list.add(OaWorkFlowUtil.listAddObj("hcyhbm", hcyhbm));
			String hcyh = (String) getHyPubBO().findColValue(
					"bd_bankdoc",
					"name",
					"nvl(dr,0) = 0 and  pk_bankdoc = '"
							+ parentVO.getTransformoutbank() + "'");
			list.add(OaWorkFlowUtil.listAddObj("hcyh", hcyh));
		}
		// 划出账户
		if (null != parentVO.getTransformoutaccount()) {
			String hczh = (String) getHyPubBO().findColValue(
					"bd_bankaccsub",
					"accnum",
					"nvl(dr,0) = 0 and pk_bankaccsub = '"
							+ parentVO.getTransformoutaccount() + "'");
			list.add(OaWorkFlowUtil.listAddObj("hczh", hczh));
			;
		}
		// 划入银行
		if (null != parentVO.getTransforminbank()) {
			String hryhbm = (String) getHyPubBO().findColValue(
					"bd_bankdoc",
					"code",
					"nvl(dr,0) = 0 and pk_bankdoc = '"
							+ parentVO.getTransforminbank() + "'");
			list.add(OaWorkFlowUtil.listAddObj("hryhbm", hryhbm));
			String hryh = (String) getHyPubBO().findColValue(
					"bd_bankdoc",
					"name",
					"nvl(dr,0) = 0 and pk_bankdoc = '"
							+ parentVO.getTransforminbank() + "'");
			list.add(OaWorkFlowUtil.listAddObj("hryh", hryh));
		}
		// 划入账户
		if (null != parentVO.getTransforminaccount()) {
			String hrzh = (String) getHyPubBO().findColValue(
					"bd_bankaccsub",
					"accnum",
					"nvl(dr,0) = 0 and pk_bankaccsub = '"
							+ parentVO.getTransforminaccount() + "'");
			list.add(OaWorkFlowUtil.listAddObj("hrzh", hrzh));
		}
		// 结算方式
		if (null != parentVO.getPk_balatype()) {
			String jsfs = (String) getHyPubBO().findColValue(
					"bd_balatype",
					"name",
					"nvl(dr,0) = 0 and pk_balatype = '"
							+ parentVO.getPk_balatype() + "'");
			list.add(OaWorkFlowUtil.listAddObj("jsfs", jsfs));
		}
		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	private Object getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}

	private void addBeforeRule(
			CompareAroundProcesser<TransformBillAggVO> processer) {
		IRule<TransformBillAggVO> rule = new TfbFillCommitDataRule();
		processer.addBeforeRule(rule);
	}

	private void addAfterRule(
			CompareAroundProcesser<TransformBillAggVO> processor) {
		IRule<TransformBillAggVO> checkBankAccFrozenRule = new TfbCheckBankAccFrozenRule();
		processor.addAfterRule(checkBankAccFrozenRule);
	}
}
