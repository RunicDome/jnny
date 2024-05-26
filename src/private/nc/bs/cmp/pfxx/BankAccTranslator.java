/*
 * @(#)BankAccTranslator.java 2011-6-14
 * Copyright 2010 UFIDA Software CO.LTD. All rights reserved.
 */
package nc.bs.cmp.pfxx;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.pfxx.ITranslateContext;
import nc.bs.pfxx.ITranslatorStrategy;
import nc.cmp.utils.Lists;
import nc.pubitf.uapbd.IBankaccPubQueryService;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.bd.bankaccount.IBankAccConstant;
import nc.vo.cmp.util.StringUtils;
import nc.vo.pfxx.exception.PfxxException;
import nc.vo.pfxx.xxconfig.IBasicDataMatchRule;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;


/**
 * 外部交换平台，银行账户子户翻译器
 *
 * @author jiaweib
 * @version 1.0 2011-6-14
 * @since NC6.0
 */
public class BankAccTranslator implements ITranslatorStrategy {

	/*
	 * (non-Javadoc)
	 *
	 * @see nc.bs.pfxx.ITranslatorStrategy#getStrategyDescription()
	 */
	@Override
	public String getStrategyDescription() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nc.bs.pfxx.ITranslatorStrategy#translateExToNC(java.lang.String,
	 * java.lang.String, nc.bs.pfxx.ITranslateContext)
	 */
	@Override
	public String translateExToNC(String srcValue, String metaDataID, ITranslateContext translateContext)
			throws PfxxException {
		try {
			if (translateContext.getTranslateRule() == IBasicDataMatchRule.RULE_PK)
				return srcValue;

			// modified by zhufeng 2013-8-10 修改调用接口为:组织有使用权的账户接口  start
			// 查询出当前组织下所有的银行账户（包含子账户）和内部账户
//			BankAccbasVO[] bankAccs = NCLocator.getInstance().lookup(IBankaccPubQueryService.class)
//					.queryCtrlpowerAccsByOrgID(getPk_org(translateContext)); 
			BankAccbasVO[] bankAccs = NCLocator.getInstance().lookup(IBankaccPubQueryService.class)
				.queryUsableAccByOrg(getPk_org(translateContext));
			// modified by zhufeng 2013-8-10 end
			// 获取所有银行账户子户
			List<BankAccSubVO> bankSubList = getBankSubList(bankAccs);

			String returnValue = null;

			for (BankAccSubVO vo : bankSubList) {
				if (translateContext.getTranslateRule() == IBasicDataMatchRule.RULE_CODE) {
					if (vo.getCode().equals(srcValue)) {
						returnValue = vo.getPk_bankaccsub();
						break;
					}
				}
				if (translateContext.getTranslateRule() == IBasicDataMatchRule.RULE_NAME) {
					if (vo.getName().equals(srcValue)) {
						returnValue = vo.getPk_bankaccsub();
						break;
					}
				}
			}
			return returnValue;
		} catch (BusinessException e) {
			throw new PfxxException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nc.bs.pfxx.ITranslatorStrategy#translateNCToEx(java.lang.String,
	 * java.lang.String, nc.bs.pfxx.ITranslateContext)
	 */
	@Override
	public String translateNCToEx(String docPk, String metaDataID, ITranslateContext translateContext)
			throws PfxxException {
		return null;
	}

	/**
	 * 获取所有的银行子户
	 *
	 * @param vos
	 * @return
	 * @author jiaweib
	 * @since NC6.0
	 */
	private List<BankAccSubVO> getBankSubList(BankAccbasVO[] vos) {
		List<BankAccSubVO> list = Lists.newArrayList();
		for (BankAccbasVO vo : vos) {
			// 去掉内部账户
//			if (!vo.getAccstate().equals(IBankAccConstant.ACCSTATE_CLOSE) && vo.getIsinneracc().equals(UFBoolean.FALSE)) {
			if (!vo.getAccstate().equals(IBankAccConstant.ACCSTATE_CLOSE) && (vo.getIsinneracc() == null || UFBoolean.FALSE.equals(vo.getIsinneracc()))) {
				BankAccSubVO[] subVos = vo.getBankaccsub();
				if (null != subVos && subVos.length != 0) {
					for (BankAccSubVO subVo : subVos) {
						list.add(subVo);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 获取财务组织参数
	 *
	 * @param translateContext
	 * @return
	 * @throws BusinessException
	 * @author jiaweib
	 * @since NC6.0
	 */
	private String getPk_org(ITranslateContext translateContext) throws BusinessException {
		String pk_org = (String) translateContext.getTranslatorParams().get(0);
		if (StringUtils.isEmpty(pk_org)) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("3607mng_0","03607mng-0358")/*@res "未指财务组织"*/);
		}
		return pk_org;
	}
}