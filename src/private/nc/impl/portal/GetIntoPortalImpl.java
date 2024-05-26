package nc.impl.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pub.filesystem.FileExAttrDAO;
import nc.impl.pubapp.pattern.database.DBTool;
import nc.itf.arap.pub.IBXBillPublic;
import nc.itf.portal.IGetIntoPortal;
import nc.itf.pubapp.pub.smart.IBillQueryService;
import nc.itf.uap.pf.IPfExchangeService;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.jdbc.framework.exception.DbException;
import nc.ui.pcm.utils.GetDao;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.RlPmeFile;
import nc.vo.ct.saledaily.entity.AggCtSaleVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fct.ap.entity.AggCtApVO;
import nc.vo.fct.ar.entity.AggCtArVO;
import nc.vo.hrss.pub.FileNodeVO1;
import nc.vo.pmfile.documentcenter.DocumentCenterVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.filesystem.FileExAttrVO;
import nc.vo.pub.workflownote.WorkflownoteVO;

//单据转换后进共享相关
@SuppressWarnings("rawtypes")
public class GetIntoPortalImpl implements IGetIntoPortal {
	private String dsName;
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

	private String getDsName() {
		return this.dsName;
	}

	/**
	 * srcBillOrTranstype 原类型 destBillOrTranstype 转换后类型 srcBillVO 原AggVO
	 */
	@Override
	public void changeIntoPortal(String srcBillOrTranstype,
			String destBillOrTranstype, AggregatedValueObject srcBillVO)
			throws BusinessException {
		// TODO Auto-generated method stub
		String pk_primarykey = srcBillVO.getParentVO().getPrimaryKey();// 原单据主键
		// 查询是否已生成，如生成对应报销单则返回报错
		BXHeaderVO[] hvo = (BXHeaderVO[]) getHyPubBO().queryByCondition(
				BXHeaderVO.class,
				"nvl(dr,0) = 0 and pk_item = '" + pk_primarykey + "'");
		if (hvo != null && hvo.length > 0) {
			throw new BusinessException("已生成共享报销单，单据号[" + hvo[0].getDjbh()
					+ "]");
		}
		// 利用单据转换
		IPfExchangeService pf = NCLocator.getInstance().lookup(
				IPfExchangeService.class);
		// FCT1-Cxx-02 原交易类型，264X-Cxx-gcfkht 转换后交易类型
		JKBXVO bxvo = (JKBXVO) pf.runChangeData(srcBillOrTranstype,
				destBillOrTranstype, srcBillVO, null);
		JKBXVO[] vos = new JKBXVO[] { bxvo };
		IBXBillPublic bxutf = NCLocator.getInstance().lookup(
				IBXBillPublic.class);
		JKBXVO[] BXVO = bxutf.save(vos);
		System.out.println("转化后单据编号：" + BXVO[0].getParentVO().getDjbh());
		try {
			updatefile(pk_primarykey, BXVO[0].getParentVO());
			IplatFormEntry iIplatFormEntry = NCLocator.getInstance().lookup(
					IplatFormEntry.class);
			// 提交报销单
			iIplatFormEntry.processAction("START", "264X", null, BXVO[0], null,
					null);
			String sql = "update er_bxzb set pk_item = '" + pk_primarykey
					+ "' where" + " pk_jkbx = '"
					+ BXVO[0].getParentVO().getPrimaryKey() + "'";
			GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
			getDao.executeUpdate(sql);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new BusinessException("附件同步异常：" + e.getMessage());
		}
	}

	// 共享单据审核完成后，同步审核转换前单据
	@Override
	public void approveSrcBill(String bxTransiType, BXVO bxvo)
			throws BusinessException {
		// TODO Auto-generated method stub
		// 和丰采购合同进共享，生成报销单 类型为264X-Cxx-HFCGHT
		String pk_item = bxvo.getParentVO().getPk_item();// 原合同主键
		IplatFormEntry ipf = NCLocator.getInstance().lookup(
				IplatFormEntry.class);
		String adopter = (String) new HYPubBO().findColValue("sys_config",
				"config_value", " config_key='oaadopt_cuser'");
		InvocationInfoProxy.getInstance().setUserId(adopter);
		HashMap hmPfExParams = new HashMap();
		IBillQueryService billquery = NCLocator.getInstance().lookup(
				IBillQueryService.class);
		if ("264X-Cxx-HFCGHT".equals(bxTransiType)) {
			AggCtPuVO aggVO = billquery.querySingleBillByPk(AggCtPuVO.class,
					pk_item + "");
			// 审批且生效采购合同
			WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
					.getInstance().lookup(IWorkflowMachine.class))
					.checkWorkFlow("APPROVE", "Z2", aggVO, hmPfExParams);
			if (worknoteVO != null) {
				worknoteVO.setChecknote("批准");
				worknoteVO.setApproveresult("Y");
			}
			HashMap<String, Object> eParam = new HashMap<String, Object>();
			eParam.put("notechecked", "notechecked");
			// 审批
			AggCtPuVO[] appVOs = (AggCtPuVO[]) ipf.processAction("APPROVE",
					"Z2", worknoteVO, aggVO, null, eParam);
			// 生效
			ipf.processAction("VALIDATE", "Z2", worknoteVO, appVOs[0], null,
					eParam);
		} else if ("264X-Cxx-HFXSHT".equals(bxTransiType)) {
			/*销售合同*/
			AggCtSaleVO aggVO = billquery.querySingleBillByPk(
					AggCtSaleVO.class, pk_item + "");
			// 审批且生效销售合同
			WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
					.getInstance().lookup(IWorkflowMachine.class))
					.checkWorkFlow("APPROVE", "Z3", aggVO, hmPfExParams);
			if (worknoteVO != null) {
				worknoteVO.setChecknote("批准");
				worknoteVO.setApproveresult("Y");
			}
			HashMap<String, Object> eParam = new HashMap<String, Object>();
			eParam.put("notechecked", "notechecked");
			AggCtSaleVO[] appVOs = (AggCtSaleVO[]) ipf.processAction("APPROVE",
					"Z3", worknoteVO, aggVO, null, eParam);
			// 生效
			ipf.processAction("VALIDATE", "Z3", worknoteVO, appVOs[0], null,
					eParam);
		} else if ("264X-Cxx-gcfkht".equals(bxTransiType)) {
			/*付款合同*/
			AggCtApVO aggVO = billquery.querySingleBillByPk(AggCtApVO.class,
					pk_item + "");
			// 审批且生效付款合同
			WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
					.getInstance().lookup(IWorkflowMachine.class))
					.checkWorkFlow("APPROVE", "FCT1", aggVO, hmPfExParams);
			if (worknoteVO != null) {
				worknoteVO.setChecknote("批准");
				worknoteVO.setApproveresult("Y");
			}
			HashMap<String, Object> eParam = new HashMap<String, Object>();
			eParam.put("notechecked", "notechecked");
			// 审批
			AggCtApVO[] appVOs = (AggCtApVO[]) ipf.processAction("APPROVE",
					"FCT1", worknoteVO, aggVO, null, eParam);
			// 生效
			ipf.processAction("VALIDATE", "FCT1", worknoteVO, appVOs[0], null,
					eParam);
		} else if ("264X-Cxx-gcskht".equals(bxTransiType)) {
			/*收款合同*/
			AggCtArVO aggVO = billquery.querySingleBillByPk(AggCtArVO.class,
					pk_item + "");
			// 审批且生效付款合同
			WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
					.getInstance().lookup(IWorkflowMachine.class))
					.checkWorkFlow("APPROVE", "FCT2", aggVO, hmPfExParams);
			if (worknoteVO != null) {
				worknoteVO.setChecknote("批准");
				worknoteVO.setApproveresult("Y");
			}
			HashMap<String, Object> eParam = new HashMap<String, Object>();
			eParam.put("notechecked", "notechecked");
			// 审批
			AggCtArVO[] appVOs = (AggCtArVO[]) ipf.processAction("APPROVE",
					"FCT2", worknoteVO, aggVO, null, eParam);
			// 生效
			ipf.processAction("VALIDATE", "FCT2", worknoteVO, appVOs[0], null,
					eParam);
		}
	}

	// 传附件
	private String updatefile(String pk_primaryKey, JKBXHeaderVO bxhvo)
			throws BusinessException, DbException {
		FileExAttrDAO dao = new FileExAttrDAO(getDsName());
		List<String> idss = new ArrayList<String>();
		FileNodeVO1[] fnodes = (FileNodeVO1[]) getHyPubBO().queryByCondition(
				FileNodeVO1.class,
				"nvl(dr,0) = 0 and filepath like '%" + pk_primaryKey + "%' ");
		for (FileNodeVO1 fnode : fnodes) {
			RlPmeFile[] pmes = (RlPmeFile[]) getHyPubBO().queryByCondition(
					RlPmeFile.class,
					"nvl(dr,0) = 0 and file_id = '" + fnode.getPrimaryKey()
							+ "' ");
			// fnode.setPrimaryKey(null);
			fnode.setFilepath(fnode.getFilepath().replaceAll(pk_primaryKey,
					bxhvo.getPrimaryKey()));
			fnode.setAttributeValue("pk", createPk());
			fnode.setStatus(VOStatus.NEW);
			String id = getHyPubBO().insert(fnode);
			if ("n".equals(fnode.getIsfolder())) {
				idss.add(id);
			}
			// service.insert(fnode);
			// getDao.InsertVOWithPK(fnode);
			if (pmes.length > 0) {
				for (RlPmeFile pme : pmes) {
					// pme.setPk_rl_pme_file(null);
					pme.setFile_id(fnode.getPrimaryKey());
					pme.setStatus(VOStatus.NEW);
					pme.setPk_rl_pme_file(null);
					getHyPubBO().insert(pme);
					// getDao.InsertVOWithPK(pme);
				}
			}
			// FileExAttrVO
		}
		DocumentCenterVO[] dcbvos = (DocumentCenterVO[]) getHyPubBO()
				.queryByCondition(DocumentCenterVO.class,
						"nvl(dr,0) = 0 and pk_bill = '" + pk_primaryKey + "' ");
		List<String> ids = new ArrayList<String>();
		List<String> docs = new ArrayList<String>();
		if (dcbvos.length > 0) {
			for (DocumentCenterVO dcbvo : dcbvos) {
				// dcbvo.setPk_doccenter(null);
				dcbvo.setBill_code(bxhvo.getDjbh());
				dcbvo.setPk_billtype("0000Z30000000000264X");
				dcbvo.setPk_transitype(bxhvo.getDjlxbm());
				dcbvo.setPk_bill(bxhvo.getPrimaryKey());
				dcbvo.setStatus(VOStatus.NEW);
				String id = dcbvo.getPk_doccenter();
				dcbvo.setPk_doccenter(null);
				String s = getHyPubBO().insert(dcbvo);
				// getDao.InsertVOWithPK(dcbvo);
				ids.add(id);
				docs.add(s);
			}
		}
		FileExAttrVO[] fattrs = dao.queryFileExAttr(ids);
		for (int i = 0; i < fattrs.length; i++) {
			// FileExAttrVO fattr = (FileExAttrVO) temp.clone();
			fattrs[i].setPk_bill(bxhvo.getPrimaryKey());
			// fattr.setPk_fileexattr(null);
			fattrs[i].setPk_file(idss.get(i));
			fattrs[i].setPk_exattr(docs.get(i));
			// fattr.setStatus(VOStatus.NEW);
			fattrs[i].setPk_fileexattr(null);
			fattrs[i].setPk_billtypecode("264X");
			fattrs[i].setPk_fileexattr(createPk());
			// getDao.InsertVOWithPK(fattr);
			dao.insertFileExAttr(fattrs[i]);
		}
		return "";
	}

	private String createPk() {
		DBTool dbTool = new DBTool();
		String[] ids = dbTool.getOIDs(1);
		return ids[0];
	}
}
