package nc.impl.pcm.contract.pub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.impl.pcm.contract.util.OprContrUtil;
import nc.impl.pcm.util.ContrAlterUtil;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.data.vo.VOUpdate;
import nc.impl.pubapp.pattern.data.vo.tool.VOConcurrentTool;
import nc.itf.pcm.contract.pub.ContrOperTypeEnum;
import nc.itf.pcm.contract.pub.IContrService4Alter;
import nc.itf.pm.pvt.IUnApproveService;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContrWorksVO;
import nc.vo.pcm.contractalter.ContrAlterBillVO;
import nc.vo.pcm.contractalter.ContrAlterBodyVO;
import nc.vo.pcm.contractalter.ContrAlterHeadVO;
import nc.vo.pm.constant.BillStatusConst;
import nc.vo.pm.constant.BillTypeConst;
import nc.vo.pm.util.ArrayUtil;
import nc.vo.pm.util.StringUtil;
import nc.vo.pm.util.UFDoubleUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.pub.MathTool;

/**
 *
 * @author ligangm
 *
 */
public class ContrService4AlterImpl implements IContrService4Alter {

	@Override
	public void approveAfterAltBill(ContrAlterBillVO vo) throws BusinessException {
		ContrAlterBodyVO[] altBodyVOs = (ContrAlterBodyVO[]) vo.getChildren(ContrAlterBodyVO.class);
		// 审核通过后，要　更新合同表体的操作次数，并回写合同
		// 获取变更单表体对应的合同表体PK集合
		String[] pkWorks = this.getOprPkWorks(altBodyVOs);
		if (ArrayUtil.isNotEmpty(pkWorks)) {
			// 更新合同表体上的操作次数
			OprContrUtil.updateBodyOprNum(pkWorks, IUnApproveService.approve);
		}
		vo.getParentVO().setContr_ver_after(vo.getParentVO().getContr_ver_bef() + 1);
		vo.getParentVO().setStatus(VOStatus.UPDATED);
		// 回写合同
		ContrAlterUtil.updateContract(vo, false);
	}

	@Override
	public void approveBeforeAltBill(ContrAlterBillVO vo) throws BusinessException {
		ContrAlterHeadVO altHeadVO = vo.getParentVO();
		VOQuery<ContrHeadVO> queryHead = new VOQuery<ContrHeadVO>(ContrHeadVO.class);
		ContrHeadVO[] headVOs = queryHead.query(new String[] { altHeadVO.getPk_contr() });
		// 对合同表头加锁
		VOConcurrentTool tool = new VOConcurrentTool();
		tool.checkTSWithDB(headVOs);

		// 该合同是否生效状态
		OprContrUtil.isValidate(headVOs[0]);
		// 变更单上记录的版本号与合同当前版本号是否相同
		if (!headVOs[0].getContr_version().equals(altHeadVO.getContr_ver_bef())) {
			ExceptionUtils.wrappBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"subcontract_0", "04820003-0034")/*
													 * @res
													 * "原版本的合同已发生了变更，不能进行此操作"
													 */);
		}
		ContrAlterBodyVO[] altBodyVOs = (ContrAlterBodyVO[]) vo.getChildren(ContrAlterBodyVO.class);
		// 获取变更单表体对应的合同表体PK集合
		String[] pkWorks = this.getOprPkWorks(altBodyVOs);
		if (ArrayUtil.isNotEmpty(pkWorks)) {
			// 查询对应 合同 表体行
			Map<String, ContrWorksVO> pk_worksToVO = OprContrUtil.getpk_bodyToVO(pkWorks);
			StringBuilder msg = new StringBuilder();
			// 设置变更单表体上的操作次数
			for (int i = 0; i < altBodyVOs.length; i++) {
				ContrAlterBodyVO altBodyVO = altBodyVOs[i];
				if (StringUtil.isNotEmpty(altBodyVO.getPk_contr_works())) {
					ContrWorksVO worksVO = pk_worksToVO.get(altBodyVO.getPk_contr_works());

//					if (worksVO.getCurr_mny().add(altBodyVO.getAlt_mny()).compareTo(UFDouble.ZERO_DBL) <= 0) {
//						msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("subcontract_0",
//								"04820003-0205", null, new String[] { i + 1 + "" })/*
//																					 * @
//																					 * res
//																					 * "补充协议{0}行表体会将对应合同表体行的金额变更为0\n"
//																					 */);
//					}

					altBodyVO.setOprnum(worksVO.getOprnum() + 1);
					altBodyVO.setStatus(VOStatus.UPDATED);
				}
			}

			if (msg.length() > 0) {
				ExceptionUtils.wrappBusinessException(msg.toString());
			}
		}
		vo.setChildren(ContrAlterBodyVO.class, altBodyVOs);
	}

	@Override
	public void delAltBill(String pk_contr) throws BusinessException {
		OprContrUtil.updateHeadOprFlag(pk_contr, UFBoolean.FALSE, BillTypeConst.CONTRALTER);
	}

	@Override
	public void saveAltBill(ContrAlterBillVO altBillVO, ContrOperTypeEnum oprType) throws BusinessException {
		ContrAlterHeadVO altHeadVO = altBillVO.getParentVO();
		String pk_contr = altHeadVO.getPk_contr();
		// 查询变更单对应的合同
		VOQuery<ContrHeadVO> query = new VOQuery<ContrHeadVO>(ContrHeadVO.class);
		ContrHeadVO[] headVOs = query.query(new String[] { pk_contr });
		// 对合同加锁
		VOConcurrentTool tool = new VOConcurrentTool();
		tool.checkTSWithDB(headVOs);
		// 该合同是否已有结算单
		OprContrUtil.hasBalaBill(headVOs[0]);
		// 该合同是否生效状态 
		OprContrUtil.isValidate(headVOs[0]);
		// 校验表头的本次变更金额
		this.checkHeadAltMny(altHeadVO, headVOs[0]);
		// 校验表头的本次变更金额 与变更数量
		ContrAlterBodyVO[] altBodyVOs = (ContrAlterBodyVO[]) altBillVO.getChildren(ContrAlterBodyVO.class);
		this.checkBodyAltMnyAndNum(altBodyVOs);
		
		//校验补充协议和清单发包合同的合同基本中“物料+项目任务+cbs”不会重复。
		StringBuilder msg = new StringBuilder();
		VOQuery<ContrWorksVO> queryContrWorks = new VOQuery<ContrWorksVO>(ContrWorksVO.class);
		String condition = "where pk_contr ='"+pk_contr+"'";
		ContrWorksVO[] worksVOs = queryContrWorks.queryWithWhereKeyWord(condition,null);
		Map<String, String> worksMap = new HashMap<String, String>();
		if (ArrayUtil.isNotEmpty(worksVOs)) {
			for (int i = 0; i < worksVOs.length; i++) {
				StringBuilder keyString = new StringBuilder();
				String key = keyString.append(worksVOs[i].getPk_material_v()).
						append(worksVOs[i].getPk_wbs()).append(worksVOs[i].getPk_cbsnode()).toString();
				worksMap.put(key, worksVOs[i].getRowno());
			}
		}
		if(ArrayUtil.isNotEmpty(altBodyVOs)){
			for(int i=0;i<altBodyVOs.length; i++){
				StringBuilder keyString = new StringBuilder();
				String key = keyString.append(altBodyVOs[i].getPk_material_v()).
						append(altBodyVOs[i].getPk_wbs()).append(altBodyVOs[i].getPk_cbsnode()).toString();
				if (worksMap.get(key) != null) {
					msg.append("第"+altBodyVOs[i].getRowno()+"和合同基本"+worksMap.get(key)+"行编码+项目任务+CBS重复");
				} 
			}
		}

		if (ContrOperTypeEnum.OPER_ADD.equals(oprType)) {
			// 此合同是否出于变更中
			if (headVOs[0].getAlter_flag() != null && headVOs[0].getAlter_flag().booleanValue()) {
				ExceptionUtils.wrappBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
						"subcontract_0", "04820003-0017")/* @res "合同 " */
						+ headVOs[0].getBill_code()
						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("subcontract_0", "04820003-0031")/*
																												 * @
																												 * res
																												 * "正在变更，不能继续变更"
																												 */);
			}
			// 更新表头的操作标志位
			OprContrUtil.updateHeadOprFlag(pk_contr, UFBoolean.TRUE, BillTypeConst.CONTRALTER);
		} else {
			// 校验 合同版本是否发生变化
			if (altHeadVO.getContr_ver_bef() != altHeadVO.getContr_ver_bef()) {
				ExceptionUtils.wrappBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
						"subcontract_0", "04820003-0032")/*
														 * @res
														 * "原版本的合同已发生了变更，此操作不能保存"
														 */);
			}
		}
	}

	@Override
	public void unapproveAltBill(ContrAlterBillVO vo) throws BusinessException {
		ContrAlterHeadVO altHeadVO = (ContrAlterHeadVO) vo.getParent();
		VOQuery<ContrHeadVO> queryHead = new VOQuery<ContrHeadVO>(ContrHeadVO.class);
		ContrHeadVO[] contrHeadVOs = queryHead.query(new String[] { altHeadVO.getPk_contr() });
		// 对合同表头加锁
		VOConcurrentTool tool = new VOConcurrentTool();
		tool.checkTSWithDB(contrHeadVOs);
		// 该合同是否已有结算单
		OprContrUtil.hasBalaBill(contrHeadVOs[0]);
		// 该合同是否生效状态
		OprContrUtil.isValidate(contrHeadVOs[0]);
		// 此合同是否出于变更中
		if (contrHeadVOs[0].getAlter_flag() != null && contrHeadVOs[0].getAlter_flag().booleanValue()
				&& altHeadVO.getBill_status() == BillStatusConst.approved) {
			ExceptionUtils.wrappBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"subcontract_0", "04820003-0017")/* @res "合同 " */
					+ contrHeadVOs[0].getBill_code()
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("subcontract_0", "04820003-0031")/*
																											 * @
																											 * res
																											 * "正在变更，不能继续变更"
																											 */);
		}
		// 比较版本
		if (!contrHeadVOs[0].getContr_version().equals(altHeadVO.getContr_ver_after())) {
			ExceptionUtils.wrappBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"subcontract_0", "04820003-0033")/*
													 * @res
													 * "此合同在变更后，发生了后续业务，不能弃审"
													 */);
		}

		// 变更单操作的合同基本明细
		ContrAlterBodyVO[] altBodyVOs = (ContrAlterBodyVO[]) vo.getChildren(ContrAlterBodyVO.class);
		String[] pkWorks = this.getOprPkWorks(altBodyVOs);
		// 第一次弃审（即由　审核通过－〉审批中（或者提交）时，要更新合同表体的操作次数，并回写合同
		if (BillStatusConst.approved == altHeadVO.getBill_status()) {
			if (ArrayUtil.isNotEmpty(pkWorks)) {
				// 查询对应 合同 表体行
				Map<String, ContrWorksVO> pk_worksToVO = OprContrUtil.getpk_bodyToVO(pkWorks);
				// 校验变更单对应的合同表体明细是否有后续单据
				OprContrUtil.reverseUNApprove(altBodyVOs, pk_worksToVO);
				// 更新合同表体上的操作次数
				OprContrUtil.updateBodyOprNum(pkWorks, IUnApproveService.unapprove);
			}
			// 回写合同
			ContrAlterUtil.updateContract(vo, true);
		}
	}

	/**
	 * 方法功能描述：更新合同的变更标记
	 * <p>
	 * <b>参数说明</b>
	 *
	 * @param flag
	 *            <p>
	 * @since 6.3
	 * @author zhangyfi
	 * @time 2012-12-26 上午09:30:19
	 */
	@Override
	public void updateAlterFlag(String pk_contr, UFBoolean flag) {
		VOQuery<ContrHeadVO> query = new VOQuery<ContrHeadVO>(ContrHeadVO.class);
		ContrHeadVO[] headVOs = query.query(new String[] { pk_contr });

		headVOs[0].setAlter_flag(flag);
		headVOs[0].setStatus(VOStatus.UPDATED);

		VOUpdate<ContrHeadVO> update = new VOUpdate<ContrHeadVO>();
		update.update(new ContrHeadVO[] { headVOs[0] }, new String[] { ContrHeadVO.ALTER_FLAG });
	}

	/**
	 *
	 * @param altBodyVOs
	 * @Author:ligangm
	 * @Date:2012-5-14
	 */
	private void checkBodyAltMnyAndNum(ContrAlterBodyVO[] altBodyVOs) {
		String[] pkWorks = this.getOprPkWorks(altBodyVOs);
		if (ArrayUtil.isNotEmpty(pkWorks)) {
			StringBuffer errorRowNum4Mny = new StringBuffer();
//			StringBuffer errorRowNum4Num = new StringBuffer();
			// 查询对应 合同 表体行
			Map<String, ContrWorksVO> pk_worksToVO = OprContrUtil.getpk_bodyToVO(pkWorks);
			for (int i = 0; i < altBodyVOs.length; i++) {
				if (StringUtil.isNotEmpty(altBodyVOs[i].getPk_contr_works())) {
					ContrWorksVO worksVO = pk_worksToVO.get(altBodyVOs[i].getPk_contr_works());
					// 本次变更数量+合同数量 >= 累计完成数量比较
//					if (MathTool.compareTo(UFDoubleUtils.add(worksVO.getCurr_num(), altBodyVOs[i].getAlt_num()),
//							worksVO.getTot_comp_num()) < 0) {
//						errorRowNum4Num.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("subcontract_0","04820003-0013")/*@res "、"*/ + (i + 1));
//					}
					// 本次变更金额+合同金额 >= 累计进度款比较
					if (MathTool.compareTo(UFDoubleUtils.add(worksVO.getCurr_mny(), altBodyVOs[i].getAlt_mny()),
							worksVO.getNtot_sche_mny()) < 0) {
						errorRowNum4Mny.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("subcontract_0","04820003-0013")/*@res "、"*/ + (i + 1));
					}

				}
			}

			String errorMsy = "";
//			if (errorRowNum4Num.length() > 1) {
//				errorMsy = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("subcontract_0", "04820003-0138",
//						null, new String[] { errorRowNum4Num.substring(1).toString() })/*
//																						 * @
//																						 * res
//																						 * " 变更后第[{0}]行的合同数量小于“已完成数量”，此变更行为不能成功。\n"
//																						 */;
//			}
			if (errorRowNum4Mny.length() > 1) {
				errorMsy += nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("subcontract_0", "04820003-0154",
						null, new String[] { errorRowNum4Mny.substring(1) })/*
																			 * @
																			 * res
																			 * "变更后第[{0}]行 的合同金额小于“累计进度款”，此变更行为不能成功。"
																			 */;
			}
			if (errorMsy.length() > 1) {
				ExceptionUtils.wrappBusinessException(errorMsy);
			}
		}
	}

	/**
	 * 合同金额+本次变更金额-累计进度款-剩余预付款>=0;
	 *
	 * @param altHeadVO
	 * @param headVO
	 * @Author:ligangm
	 * @Date:2012-5-11
	 */
	private void checkHeadAltMny(ContrAlterHeadVO altHeadVO, ContrHeadVO headVO) {
		// 累计进度款
		UFDouble totSchedMny = 
			headVO.getNtot_sched_mny() == null? UFDouble.ZERO_DBL: headVO.getNtot_sched_mny();
		// 剩余预付款
		UFDouble spareMny = 
			headVO.getNspare_mny() == null? UFDouble.ZERO_DBL: headVO.getNspare_mny();
		
		UFDouble result = UFDoubleUtils.add( headVO.getNcurrent_mny(), altHeadVO.getAlt_mny())
				.sub(totSchedMny).sub(spareMny);
		if (MathTool.compareTo(result, UFDouble.ZERO_DBL) < 0) {
			ExceptionUtils.wrappBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"subcontract_0", "04820003-0035")/*
													 * @res
													 * "变更后的合同金额小于“累计进度款”+“剩余预付款”，此变更行为不能成功。"
													 */);
		}
	}

	/**
	 * 获取变更单表体对应的合同表体
	 *
	 * @return
	 */
	private String[] getOprPkWorks(ContrAlterBodyVO[] bodyVOs) {
		List<String> pkWorksList = new ArrayList<String>();
		for (int i = 0; i < bodyVOs.length; i++) {
			if (StringUtil.isNotEmpty(bodyVOs[i].getPk_contr_works())) {
				pkWorksList.add(bodyVOs[i].getPk_contr_works());
			}
		}
		return pkWorksList.toArray(new String[0]);
	}
}