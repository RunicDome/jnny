package nc.bs.bd.pfxx.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pfxx.ISwapContext;
import nc.bs.pfxx.plugin.AbstractPfxxPlugin;
import nc.bs.trade.business.HYPubBO;
import nc.itf.bd.material.baseinfo.IMaterialBaseInfoService;
import nc.vo.bd.errorlog.ErrLogReturnValue;
import nc.vo.bd.errorlog.ErrorMsgVO;
import nc.vo.bd.material.MaterialConvertVO;
import nc.vo.bd.material.MaterialTaxTypeVO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.material.cost.MaterialCostVO;
import nc.vo.bd.material.cost.MaterialCostmodeVO;
import nc.vo.bd.material.stock.MaterialStockVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pfxx.auxiliary.AggxsysregisterVO;
import nc.vo.pfxx.util.PfxxPluginUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

import org.apache.commons.lang.StringUtils;

/**
 * 物料XML导入
 * 
 * @author XBX
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MaterialPfxxPlugin extends AbstractPfxxPlugin {
	private BaseDAO baseDAO = null;

	private IMaterialBaseInfoService service = null;

	public MaterialPfxxPlugin() {
	}

	protected Object processBill(Object vo, ISwapContext swapContext,
			AggxsysregisterVO aggxsysvo) throws BusinessException {
		String pk = null;
		try {
			MaterialVO materialVO = (MaterialVO) vo;
			pk = PfxxPluginUtils.queryBillPKBeforeSaveOrUpdate(
					swapContext.getBilltype(), swapContext.getDocID());

			if (StringUtils.isBlank(pk)) {
				materialVO = insertMaterialVO(materialVO);
				// 对接二维码XBX 集团级 默认分配512,409
				if (StringUtils.equals("NYGC", materialVO.getDef2())) {
					MaterialCostVO[] cbvos = (MaterialCostVO[]) new HYPubBO()
							.queryByCondition(
									MaterialCostVO.class,
									" pk_material = '"
											+ materialVO.getPrimaryKey() + "'");// 成本信息
					for (int cbi = 0; cbi < cbvos.length; cbi++) {
						MaterialCostVO cbvo = cbvos[cbi];
						MaterialCostmodeVO[] jjfsvo = (MaterialCostmodeVO[]) new HYPubBO()
								.queryByCondition(
										MaterialCostmodeVO.class,
										" pk_materialcost = '"
												+ cbvo.getPrimaryKey() + "'");// 计价方式信息
						// MaterialCostmodeVO[] jjfsvo =
						// cbvo.getMaterialcostmode();// 成本信息 ->计价方式信息
						for (int cbjji = 0; cbjji < jjfsvo.length; cbjji++) {
							jjfsvo[cbjji].setBatchcost(UFBoolean.TRUE);// 批次核算-Y
							new HYPubBO().update(jjfsvo[cbjji]);
						}
					}
					materialVO.setMaterialcost(cbvos);
					MaterialStockVO[] kcvos = (MaterialStockVO[]) new HYPubBO()
							.queryByCondition(
									MaterialStockVO.class,
									" pk_material = '"
											+ materialVO.getPrimaryKey() + "'"); // 库存信息
					for (int kci = 0; kci < kcvos.length; kci++) {
						kcvos[kci].setWholemanaflag(UFBoolean.TRUE);// 批次管理-Y
						new HYPubBO().update(kcvos[kci]);
					}
				}
				PfxxPluginUtils.addDocIDVsPKContrast(swapContext.getBilltype(),
						swapContext.getDocID(), materialVO.getPrimaryKey());
				return materialVO.getPrimaryKey();
			}
			getService().updateMaterial(getUpdateVO(materialVO, pk));
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex.getCause());
			throw new BusinessException(ex.getMessage(), ex.getCause());
		}
		return pk;
	}

	private BaseDAO getBaseDAO() {
		if (this.baseDAO == null) {
			this.baseDAO = new BaseDAO();
		}
		return this.baseDAO;
	}

	private MaterialVO getInsertVO(MaterialVO materialVO) throws DAOException {
		materialVO.setEnablestate(Integer.valueOf(2));
		materialVO.setStatus(2);
		materialVO.setMaterialconvert(getMaterialConvertVOs(materialVO));
		materialVO.setMaterialtaxtype(getMaterialTaxTypeVOs(materialVO));
		return materialVO;
	}

	private MaterialConvertVO[] getMaterialConvertVOs(MaterialVO MaterialVO)
			throws DAOException {
		List<MaterialConvertVO> newConverts = new ArrayList();
		if (StringUtils.isNotBlank(MaterialVO.getPrimaryKey())) {
			Collection<MaterialConvertVO> oldAgentStores = getBaseDAO()
					.retrieveByClause(
							MaterialConvertVO.class,
							"pk_material = '" + MaterialVO.getPrimaryKey()
									+ "'",
							new String[] { "pk_materialconvert" });

			for (MaterialConvertVO agentstore : oldAgentStores) {
				agentstore.setStatus(3);
				newConverts.add(agentstore);
			}
		}
		if ((MaterialVO.getMaterialconvert() != null)
				&& (MaterialVO.getMaterialconvert().length > 0)) {
			for (MaterialConvertVO agentstore : MaterialVO.getMaterialconvert()) {
				agentstore.setStatus(2);
				newConverts.add(agentstore);
			}
		}
		return (MaterialConvertVO[]) newConverts
				.toArray(new MaterialConvertVO[0]);
	}

	private MaterialTaxTypeVO[] getMaterialTaxTypeVOs(MaterialVO MaterialVO)
			throws DAOException {
		List<MaterialTaxTypeVO> newConverts = new ArrayList();
		if (StringUtils.isNotBlank(MaterialVO.getPrimaryKey())) {
			Collection<MaterialTaxTypeVO> oldAgentStores = getBaseDAO()
					.retrieveByClause(
							MaterialTaxTypeVO.class,
							"pk_material = '" + MaterialVO.getPrimaryKey()
									+ "'",
							new String[] { "pk_materialtaxtype" });

			for (MaterialTaxTypeVO agentstore : oldAgentStores) {
				agentstore.setStatus(3);
				newConverts.add(agentstore);
			}
		}
		if ((MaterialVO.getMaterialtaxtype() != null)
				&& (MaterialVO.getMaterialtaxtype().length > 0)) {
			for (MaterialTaxTypeVO agentstore : MaterialVO.getMaterialtaxtype()) {
				agentstore.setStatus(2);
				newConverts.add(agentstore);
			}
		}
		return (MaterialTaxTypeVO[]) newConverts
				.toArray(new MaterialTaxTypeVO[0]);
	}

	private IMaterialBaseInfoService getService() {
		if (this.service == null) {
			this.service = ((IMaterialBaseInfoService) NCLocator.getInstance()
					.lookup(IMaterialBaseInfoService.class));
		}
		return this.service;
	}

	private MaterialVO getUpdateVO(MaterialVO materialVO, String pk)
			throws BusinessException {
		MaterialVO oldVO = (MaterialVO) getBaseDAO().retrieveByPK(
				MaterialVO.class,
				pk,
				new String[] { "creator", "creationtime", "pk_source",
						"version", "latest", "enablestate" });

		if (oldVO == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("bdpub", "0bdpub0057"));
		}

		materialVO.setPrimaryKey(pk);
		materialVO.setCreator(oldVO.getCreator());
		materialVO.setCreationtime(oldVO.getCreationtime());
		materialVO.setPk_source(oldVO.getPk_source());
		materialVO.setVersion(oldVO.getVersion());
		materialVO.setLatest(oldVO.getLatest());
		materialVO.setEnablestate(oldVO.getEnablestate());
		materialVO.setStatus(1);
		materialVO.setMaterialconvert(getMaterialConvertVOs(materialVO));
		materialVO.setMaterialtaxtype(getMaterialTaxTypeVOs(materialVO));
		return materialVO;
	}

	private MaterialVO insertMaterialVO(MaterialVO materialVO)
			throws BusinessException, DAOException {
		if (StringUtils.isBlank(materialVO.getPk_source())) {
			materialVO = getService().insertMaterial(getInsertVO(materialVO));
		} else {
			ErrLogReturnValue value = getService().createMaterialVersion(
					materialVO, materialVO.getPk_source());

			if ((value.getReturnValue() == null)
					|| (!value.getReturnValue().getClass().isArray())) {
				return materialVO;
			}
			materialVO = (MaterialVO) ((Object[]) (Object[]) value
					.getReturnValue())[0];
			LogErrorMessage(value);
		}
		return materialVO;
	}

	private void LogErrorMessage(ErrLogReturnValue value) {
		ErrorMsgVO[] vos = value.getErrLogResult().getErrorMsgs();
		if ((vos != null) && (vos.length > 0)) {
			String message = NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"10140mag",
					"010140mag0200",
					null,
					new String[] {
							Integer.toString(value.getTotalNum()),
							Integer.toString(value.getErrLogResult()
									.getErrorMessagegNum()) })
					+ "\n";

			for (int i = 0; i < vos.length; i++) {
				message = message + vos[i].getErrormsg() + "\n";
			}
			Logger.debug(message);
		}
	}
}
