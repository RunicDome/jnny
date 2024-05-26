package nc.bs.pub.action;

import java.lang.reflect.Method;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.core.util.ObjectCreator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.pub.compiler.IWorkFlowRet;
import nc.bs.pub.filesystem.FSOption;
import nc.bs.pub.filesystem.IFileSystemService;
import nc.itf.bd.supplier.assign.ISupplierAssignService;
import nc.itf.bd.supplier.baseinfo.ISupplierBaseInfoService;
import nc.itf.bd.supplier.pf.ISupplierPFService;
import nc.vo.bd.cust.CustbankVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.bd.supplier.pf.AggSupplierPfVO;
import nc.vo.bd.supplier.pf.SupplierPfVO;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BeanHelper;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.uap.pf.PFBusinessException;
import org.apache.commons.lang.StringUtils;

public class N_10GY_APPROVE extends AbstractCompiler2 {
	public N_10GY_APPROVE() {
	}

	public Object runComClass(PfParameterVO vo) throws BusinessException {
		try {
			this.m_tmpVo = vo;
			IWorkFlowRet woekFlowRet = (IWorkFlowRet) procActionFlow(vo);
			ISupplierPFService pfService = (ISupplierPFService) NCLocator
					.getInstance().lookup(ISupplierPFService.class);
			if (woekFlowRet == null) {
				SupplierPfVO pfvo = (SupplierPfVO) ((AggSupplierPfVO) getVo())
						.getParentVO();
				/*SupplierVO supplier = (SupplierVO) pfvo.getBsupbaseinfo();
				supplier.setPk_supplier_pf(pfvo.getPrimaryKey());
				supplier.setPk_billtypecode(pfvo.getPk_billtype());
				try {
					CustbankVO[] vos = supplier.getSupbankacc();
					supplier.setSupbankacc(null);
					supplier = ((ISupplierBaseInfoService) NCLocator
							.getInstance().lookup(
									ISupplierBaseInfoService.class))
							.insertSupplierVO(supplier, false);

					supplier.setSupbankacc(vos);
					dealSupBankaccPf(pfvo, supplier.getPrimaryKey());

					String rootPath = "uapbd/d6be4596-55a6-4476-9b1d-cb770c03bfdd/"
							+ pfvo.getPrimaryKey();

					String destDirPath = "uapbd/720dcc7c-ff19-48f4-b9c5-b90906682f45/"
							+ supplier.getPrimaryKey();

					IFileSystemService service = (IFileSystemService) NCLocator
							.getInstance().lookup(IFileSystemService.class);

					service.copyTo(rootPath, destDirPath,
							FSOption.WHEN_DEST_EXIST_OVERWRITE, false);
				} catch (BusinessException e) {
					String msg = NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"10140custsup", "010140custsup0060", null,
							new String[] { e.getMessage() });

					pfService.writeSupErrMsgToMemo_RequiresNew(
							pfvo.getPrimaryKey(), msg);
					throw e;
				}*/
				// SupplierVO fullSupplierVO = (SupplierVO) pfvo.getBsupbaseinfo();
				// pfvo.setBsupbaseinfo(fullSupplierVO);
				pfvo.setMeno(null);
				pfvo = pfService.updateSupplierPfVOForAction("approve", pfvo);

				// assignSupplierToOrg(pfvo, supplier);
				getVo().setParentVO(pfvo);

				return getVo();
			}
			SupplierPfVO pfvo = (SupplierPfVO) ((AggSupplierPfVO) woekFlowRet.m_inVo)
					.getParentVO();
			pfvo = pfService.updateSupplierPfVOForAction("approve", pfvo);
			((AggSupplierPfVO) woekFlowRet.m_inVo).setParentVO(pfvo);
			return woekFlowRet;
		} catch (Exception ex) {
			if ((ex instanceof BusinessException)) {
				throw ((BusinessException) ex);
			}
			throw new PFBusinessException(ex.getMessage(), ex);
		}
	}

	private void assignSupplierToOrg(SupplierPfVO pfvo, SupplierVO vo)
			throws BusinessException {
		if (isOrgData(vo)) {
			getAssignService().assignSupplierToSelfOrg(vo);
		} else {
			getAssignService().assignSupplierForPf(vo.getPk_supplier(),
					pfvo.getPk_org(), null);
		}
	}

	private boolean isOrgData(SupplierVO vo) {
		if ((vo.getPk_group() == vo.getPk_org())
				|| ("GLOBLE00000000000000".equals(vo.getPk_org()))) {
			return false;
		}
		return true;
	}

	private ISupplierAssignService getAssignService() {
		return (ISupplierAssignService) NCLocator.getInstance().lookup(
				ISupplierAssignService.class);
	}

	public String getCodeRemark() {
		return "\tIWorkFlowRet woekFlowRet = (IWorkFlowRet) procActionFlow(vo);\nISupplierPFService pfService = NCLocator.getInstance().lookup(\n\t\tISupplierPFService.class);\nif (woekFlowRet == null) {\n\tSupplierPfVO pfvo = (SupplierPfVO) ((AggSupplierPfVO) getVo())\n\t\t\t.getParentVO();\n\tSupplierVO supplier = (SupplierVO) pfvo.getBsupbaseinfo();\n\tsupplier.setPk_supplier_pf(pfvo.getPrimaryKey());\n\tsupplier.setPk_billtypecode(pfvo.getPk_billtype());\n\ttry {\n\t\tsupplier = NCLocator.getInstance()\n\t\t\t\t.lookup(ISupplierBaseInfoService.class)\n\t\t\t\t.insertSupplierVO(supplier, true);\n\t} catch (BusinessException e) {\n\t\tString msg = NCLangRes4VoTransl.getNCLangRes().getStrByID(\n\t\t\t\t\"10140custsup\", \"010140custsup0060\", null,\n\t\t\t\tnew String[] { e.getMessage() })\n\t\t\t\t;\n\t\tpfService.writeSupErrMsgToMemo_RequiresNew(\n\t\t\t\tpfvo.getPrimaryKey(), msg);\n\t\tthrow e;\n\t}\n\tpfvo.setBsupbaseinfo(supplier);\n\tpfvo.setMeno(null);\n\tpfvo = pfService.updateSupplierPfVO(pfvo);\n\tgetVo().setParentVO(pfvo);\n\treturn getVo();\n} else {\n\tSupplierPfVO pfvo = (SupplierPfVO) ((AggSupplierPfVO) woekFlowRet.m_inVo)\n\t\t\t.getParentVO();\n\tpfvo = pfService.updateSupplierPfVO(pfvo);\n\t((AggSupplierPfVO) woekFlowRet.m_inVo).setParentVO(pfvo);\n\treturn woekFlowRet;\n}\n";
	}

	private void dealSupBankaccPf(SupplierPfVO pfvo, String pk_supplier)
			throws Exception {
		Object[] args = { pfvo, pk_supplier };
		Object newInstance = ObjectCreator.newInstance("uapbd",
				"nc.bs.bd.bankacc.cust.SupBankaccSaveServiceForPf", args);

		BeanHelper.setProperty(newInstance, "supplierPfVO", pfvo);
		BeanHelper.setProperty(newInstance, "pk_supplier", pk_supplier);
		Method method = newInstance.getClass().getMethod(
				"saveSupBankUnionVOBySupplierPfVO", new Class[0]);
		String errormsg = (String) method.invoke(newInstance, new Object[0]);
		if (!StringUtils.isEmpty(errormsg)) {
			throw new BusinessException(errormsg);
		}
	}
}