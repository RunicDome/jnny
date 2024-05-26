package nc.bs.pub.action;

import java.util.Hashtable;
import java.util.List;

import nc.bs.arap.payablebp.PayableBO;
import nc.bs.arap.util.ArapFlowCheckUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.trade.business.HYPubBO;
import nc.pubitf.credit.creditcheck.IIgnoreCreditCheck;
import nc.ui.pcm.utils.GetDao;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.payable.AggPayableBillVO;
import nc.vo.arap.payable.PayableBillItemVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.pflow.PfUserObject;
import nc.vo.scmpub.res.BusinessCheck;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

// 应付单保存
@SuppressWarnings("restriction")
public class N_F1_SAVEBASE extends AbstractCompiler2 {
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

	private Hashtable<String, Object> m_keyHas;

	public Object runComClass(PfParameterVO paraVo)
			throws nc.vo.pub.BusinessException {
		try {
			super.m_tmpVo = paraVo;
			Object obj = null;
			setParameter("context", paraVo.m_preValueVos);

			String primaryKey = paraVo.m_preValueVos[0].getParentVO()
					.getPrimaryKey();

			if (hasBill(primaryKey)) {
				obj = runClass(
						"nc.bs.arap.actions.PayablebillEditBatchBSAction",
						"updateVOs",
						"&context:nc.vo.pub.AggregatedValueObject[]", paraVo,
						m_keyHas);
			} else {
				obj = runClass(
						"nc.bs.arap.actions.PayablebillSaveBatchBSAction",
						"insertVOs",
						"&context:nc.vo.pub.AggregatedValueObject[]", paraVo,
						m_keyHas);
			}

			// if(obj != null)
			// m_methodReturnHas.put("insertVOs", obj);
			ignorecheckCredit();
			// luozhw3 校验单据状态和审批状态
			for (AggregatedValueObject aggvo : paraVo.m_preValueVos) {
				AggPayableBillVO bxvo = (AggPayableBillVO) aggvo;
				// XBX付款校验
				beforeSave(bxvo);
				ArapFlowCheckUtil.checkArapbillFlowStatus(bxvo.getParentVO()
						.getPrimaryKey(),
						bxvo.getParentVO().getAttributeValue("pk_billtype")
								.toString());
			}
			return obj;
		} catch (Exception exception) {
			throw ExceptionHandler.handleException(this.getClass(), exception);
		}
	}

	// 保存校验
	private void beforeSave(AggPayableBillVO yfvo) throws BusinessException {
		// TODO Auto-generated method stub
		BaseBillVO headVO = yfvo.getHeadVO();
		String jylx = headVO.getPk_tradetype();// 交易类型
		String zz = headVO.getPk_org();// 组织主键
		PayableBillItemVO[] childrenVOs = (PayableBillItemVO[]) yfvo
				.getChildrenVO();// 付款明细VO
		// 验证合同
		Object conforg = (Object) getHyPubBO().findColValue(
				"bd_defdoc",
				"code",
				"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
						+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
						+ " and code = '" + zz + "'");
		String htsave_jylx = (String) new HYPubBO().findColValue("sys_config",
				"config_value", "config_key='FKHTSave_JYLX'");
		if (conforg != null && htsave_jylx.contains(jylx)) {
			if (headVO.getCreationtime().afterDate(
					new UFDate("2023-05-22 00:00:00"))) {
				for (PayableBillItemVO conWorkVO : childrenVOs) {
					if (StringUtils.isEmpty(conWorkVO.getTop_billid())) {
						throw new BusinessException("行号："
								+ conWorkVO.getRowno() + "不允许自制！");
					}
				}
			}
		}
		for (int i = 0; i < childrenVOs.length; i++) {
			PayableBillItemVO itemVO = childrenVOs[i];

			GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
			/**
			 * 验证 付款单号[]的单据未审批通过 2021-11-01 liuli 修改组织、交易类型、 修改人 ：XBX
			 */
			String cfsave_org = (String) new HYPubBO()
					.findColValue("sys_config", "config_value",
							"config_key='FKHTSaveCF_ORG'");
			String cfsave_jylx = (String) new HYPubBO().findColValue(
					"sys_config", "config_value",
					"config_key='FKHTSaveCF_JYLX'");
			String mhtno = itemVO.getContractno() + "";// 合同号
			String mgyspk = itemVO.getSupplier() + "";// 供应商
			if (cfsave_org.contains(zz) && cfsave_jylx.contains(jylx)) {
				String fksql = "SELECT DISTINCT ZB.BILLNO,MX.SUPPLIER,ZB.APPROVESTATUS FROM AP_PAYABLEBILL ZB "
						+ "LEFT JOIN AP_PAYABLEITEM MX ON ZB.PK_PAYABLEBILL = MX.PK_PAYABLEBILL WHERE ZB.DR = 0 AND MX.DR = 0 "
						+ "AND MX.CONTRACTNO = '"
						+ mhtno
						+ "' AND MX.SUPPLIER = '"
						+ mgyspk
						+ "' AND ZB.PK_TRADETYPE IN ('D1','F1-Cxx-01') ";
				if (headVO.getPrimaryKey() != null
						&& !"".equals(headVO.getPrimaryKey())) {
					fksql += "AND ZB.PK_PAYABLEBILL <> '"
							+ headVO.getPrimaryKey() + "'";
				}
				System.out.println("查询应付SQL：" + fksql);
				List<Object[]> fkls = getDao.query(fksql);
				if (fkls != null && fkls.size() > 0 && fkls.get(0) != null) {
					for (int fki = 0; fki < fkls.size(); fki++) {
						String spzt = fkls.get(fki)[2] + "";// 单据状态
						if ("2".equals(spzt) || "3".equals(spzt)) {
							throw new BusinessException("应付单号["
									+ fkls.get(fki)[0] + "" + "]的单据未审批通过！");
						}
					}
				}
				// 查询付款单
				String yfsql = "SELECT DISTINCT ZB.BILLNO,MX.SUPPLIER,ZB.APPROVESTATUS FROM AP_PAYBILL ZB "
						+ "LEFT JOIN AP_PAYITEM MX ON ZB.PK_PAYBILL = MX.PK_PAYBILL WHERE ZB.DR = 0 AND MX.DR = 0 "
						+ "AND MX.CONTRACTNO = '"
						+ mhtno
						+ "' AND MX.SUPPLIER = '"
						+ mgyspk
						+ "' AND ZB.PK_TRADETYPE IN ('D3','F3-Cxx-01')";
				System.out.println("查询付款SQL：" + yfsql);
				List<Object[]> yfls = getDao.query(yfsql);
				if (yfls != null && yfls.size() > 0 && yfls.get(0) != null) {
					for (int fki = 0; fki < yfls.size(); fki++) {
						String spzt = yfls.get(fki)[2] + "";// 单据状态
						if (!"1".equals(spzt)) {
							throw new BusinessException("付款单号["
									+ yfls.get(fki)[0] + "" + "]的单据未审批通过！");
						}
					}
				}
			}
		}
	}

	private boolean hasBill(String primaryKey) throws BusinessException {
		boolean hasBill = false;
		if (primaryKey != null) {
			AggPayableBillVO[] bvos = null;
			try {
				PayableBO payableBO = new PayableBO();
				bvos = payableBO
						.findBillByPrimaryKey(new String[] { primaryKey });
			} catch (Exception e) {
			}
			if (bvos != null && bvos.length != 0 && bvos[0] != null) {
				hasBill = true;
			}
		}
		return hasBill;
	}

	private void ignorecheckCredit() throws BusinessException {
		PfUserObject userObj = null;

		if (this.getUserObj() instanceof PfUserObject) {
			userObj = (PfUserObject) this.getUserObj();
		} else if (this.getUserObj() instanceof PfUserObject[]) {
			userObj = ArrayUtils.isEmpty((PfUserObject[]) this.getUserObj()) ? null
					: ((PfUserObject[]) this.getUserObj())[0];
		}

		// 判断是否做信用检查，在session中设置标识
		if (userObj != null
				&& userObj.getBusinessCheckMap().get(
						BusinessCheck.CreditCheck.getCheckCode()) != null
				&& !userObj.getBusinessCheckMap()
						.get(BusinessCheck.CreditCheck.getCheckCode())
						.booleanValue()) {
			// 不做信用检查
			IIgnoreCreditCheck service = NCLocator.getInstance().lookup(
					IIgnoreCreditCheck.class);
			service.ignoreCreditCheck();
		}
	}

	protected void setParameter(String key, Object val) {
		if (m_keyHas == null) {
			m_keyHas = new Hashtable<String, Object>();
		}
		if (val != null) {
			m_keyHas.put(key, val);
		}
	}
}
