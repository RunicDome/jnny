package nc.bs.erm.bx.outer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import nc.bs.dao.BaseDAO;
import nc.bs.er.outer.ArapItemSsVO;
import nc.bs.er.util.BXBsUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.pfxx.ISwapContext;
import nc.bs.pfxx.plugin.AbstractPfxxPlugin;
import nc.cmp.utils.CmpUtils;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IBXBillPublic;
import nc.itf.erm.prv.IArapCommonPrivate;
import nc.itf.uap.IVOPersistence;
import nc.itf.uap.pf.IPFBusiAction;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.itf.uap.pfxx.IPFxxEJBService;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.org.IDeptPubService;
import nc.pubitf.org.IOrgUnitPubService;
import nc.vo.arap.payable.AggPayableBillVO;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.cmp.settlement.SettlementAggVO;
import nc.vo.cmp.settlement.SettlementBodyVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.reimtype.ReimTypeHeaderVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.billcode.FinanceBillCodeInfo;
import nc.vo.fipub.billcode.FinanceBillCodeUtils;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pfxx.auxiliary.AggxsysregisterVO;
import nc.vo.pfxx.idcontrast.IDContrastVO;
import nc.vo.pfxx.util.PfxxPluginUtils;
import nc.vo.pfxx.util.PfxxUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.workflownote.WorkflownoteVO;

// XBX 修改报销单XML插件类 
@SuppressWarnings({ "unused", "rawtypes", "restriction" })
public class ErmExpPfxxPlugin extends AbstractPfxxPlugin {
	public ErmExpPfxxPlugin() {
	}

	protected Object processBill(Object vo, ISwapContext swapContext,
			AggxsysregisterVO aggxsysvo) throws BusinessException {
		JKBXVO bxvo = (JKBXVO) vo;
		dealSpecialField(bxvo);
		JKBXHeaderVO header = bxvo.getParentVO();

		if (("bx".equals(bxvo.getParentVO().getDjdl()))
				&& (bxvo.getChildrenVO() != null)) {
			for (int i = 0; i < bxvo.getChildrenVO().length; i++) {
				bxvo.getChildrenVO()[i].setAttributeValue("tablecode",
						"arap_bxbusitem");
			}
		}

		if (bxvo.getContrastVO() != null) {
			if ((bxvo.getChildrenVO().length == 1)
					&& (bxvo.getChildrenVO()[0].getCjkybje()
							.equals(UFDouble.ZERO_DBL))) {

				bxvo.setContrastVO(null);
			} else {
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011v61013_0", "02011v61013-0067"));
			}
		}

		boolean isCheck = true;
		if (header.getDjzt() == null) {
			header.setDjzt(Integer.valueOf(1));
			isCheck = false;
		}

		if ((isCheck) && (header.getDjzt() != null)
				&& (header.getDjzt().intValue() > 1)) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011v61013_0", "02011v61013-0068"));
		}

		if (header.djdl == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2006pub_0", "02006pub-0316"));
		}

		if (header.djlxbm == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2006pub_0", "02006pub-0316"));
		}

		if (header.getPk_group() == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2006pub_0", "02006pub-0317"));
		}

		if (header.getPk_org() == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2006pub_0", "02006pub-0318"));
		}

		if (header.getFydwbm() == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011v61013_0", "02011v61013-0029"));
		}

		if (header.getDwbm() == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011v61013_0", "02011v61013-0030"));
		}

		if (header.getDeptid() == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011v61013_0", "02011v61013-0031"));
		}

		if (header.getFydeptid() == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011v61013_0", "02011v61013-0032"));
		}

		if (header.getQcbz().booleanValue()) {
			String yearMonth = ((IOrgUnitPubService) NCLocator.getInstance()
					.lookup(IOrgUnitPubService.class))
					.getOrgModulePeriodByOrgIDAndModuleID(header.getPk_org(),
							"2011");

			if ((yearMonth != null) && (yearMonth.length() != 0)
					&& (yearMonth != null) && (yearMonth.length() != 0)) {
				String year = yearMonth.substring(0, 4);
				String month = yearMonth.substring(5, 7);
				if ((year != null) && (month != null)) {
					AccountCalendar calendar = AccountCalendar
							.getInstanceByPk_org(header.getPk_org());

					if (calendar == null) {
						throw new BusinessException(NCLangRes4VoTransl
								.getNCLangRes().getStrByID("2011v61013_0",
										"02011v61013-0021"));
					}

					calendar.set(year, month);
					if (calendar.getMonthVO() == null) {
						throw new BusinessException(NCLangRes4VoTransl
								.getNCLangRes().getStrByID("2011v61013_0",
										"02011v61013-0022"));
					}

					header.setDjrq(calendar.getMonthVO().getBegindate()
							.getDateBefore(1));
				}
			}
		}

		JKBXVO tempvo = null;
		IBXBillPrivate billPri = (IBXBillPrivate) NCLocator.getInstance()
				.lookup(IBXBillPrivate.class);

		String billtype = swapContext.getBilltype();
		String docid = swapContext.getDocID();
		String pk_org = null;
		String oldPk = PfxxPluginUtils.queryBillPKBeforeSaveOrUpdate(billtype,
				docid, pk_org);

		if (oldPk != null) {
			if (swapContext.getReplace().equalsIgnoreCase("N")) {
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("expensepub_0", "02011002-0072"));
			}

			header.setPrimaryKey(oldPk);

			List<JKBXVO> vos = billPri.queryVOsByPrimaryKeys(
					new String[] { oldPk }, header.getDjdl());

			if ((vos != null) && (vos.size() != 0)) {
				tempvo = (JKBXVO) vos.get(0);
			}
			if (tempvo == null) {
				((IPFxxEJBService) NCLocator.getInstance().lookup(
						IPFxxEJBService.class)).deleteIDvsPKByDocPK(oldPk);
			}

			if ((tempvo != null) && (tempvo.getParentVO() != null)) {
				if (tempvo.getParentVO().getDjzt() == null) {
					throw new BusinessException(NCLangRes4VoTransl
							.getNCLangRes().getStrByID("expensepub_0",
									"02011002-0073"));
				}

				if (tempvo.getParentVO().getDjzt().intValue() > 1) {
					throw new BusinessException(NCLangRes4VoTransl
							.getNCLangRes().getStrByID("expensepub_0",
									"02011002-0074")
							+ tempvo.getParentVO().getDjzt());
				}

				header.setPrimaryKey(oldPk);
				header.setDjbh(tempvo.getParentVO().getDjbh());
			}
		}

		if (bxvo.getSettlevo() != null) {
			CmpUtils.addModifyFlag((SettlementBodyVO[]) bxvo.getSettlevo()
					.getChildrenVO());
		}

		int newbillstat = header.getDjzt().intValue();
		UFBoolean isqc = header.getQcbz();

		if (isqc.booleanValue()) {
			header.setDjzt(Integer.valueOf(3));
			header.setSxbz(Integer.valueOf(1));
			header.setSpzt(Integer.valueOf(1));
		} else {
			if (newbillstat <= 1) {
				header.setDjzt(Integer.valueOf(1));
			}
			header.setSxbz(Integer.valueOf(0));
			header.setSpzt(Integer.valueOf(-1));
		}

		// header.setZy(NCLangRes4VoTransl.getNCLangRes().getStrByID(
		// "expensepub_0", "02011002-0076"));

		setDeptOrgVersion(header);

		if (oldPk == null) {
			try {
				JKBXVO[] bxvos = ((IBXBillPublic) NCLocator.getInstance()
						.lookup(IBXBillPublic.class))
						.save(new JKBXVO[] { bxvo });

				String pk = bxvos[0].getParentVO().getPk_jkbx();
				PfxxPluginUtils.addDocIDVsPKContrast(swapContext.getBilltype(),
						swapContext.getDocID(), swapContext.getOrgPk(), pk);
				// XBX提交报销单
				if (bxvo != null) {
					HashMap<String, Object> eParam = new HashMap<String, Object>();
					eParam.put("silently", "silently");
					WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
							.getInstance().lookup(IWorkflowMachine.class))
							.checkWorkFlow("START", bxvo.getParentVO()
									.getDjlxbm(), bxvo, eParam);
					IplatFormEntry iIplatFormEntry = NCLocator.getInstance()
							.lookup(IplatFormEntry.class);
					JKBXVO bxcommitvo = (JKBXVO) iIplatFormEntry
							.processAction("START", bxvo.getParentVO()
									.getDjlxbm(), worknoteVO, bxvo, null,
									eParam);
					// 自动审批
					if ("1001A1100000002E7H8E".equals(bxvo.getParentVO()
							.getOperator())) {
						HashMap hmPfExParams = new HashMap();
						WorkflownoteVO spnoteVO = ((IWorkflowMachine) NCLocator
								.getInstance().lookup(IWorkflowMachine.class))
								.checkWorkFlow("SIGNAL", bxvo.getParentVO()
										.getDjlxbm(), bxcommitvo,
										hmPfExParams);
						if (spnoteVO != null) {
							spnoteVO.setChecknote("批准");
							spnoteVO.setApproveresult("Y");
						}
						// I8DJ提交单据，自动审批
						iIplatFormEntry.processAction("SIGNAL", bxvo
								.getParentVO().getDjlxbm(), spnoteVO,
								bxcommitvo, null, eParam);
					}
				}
				Map<String, String> res = new HashMap<>();
				res.put("pk", bxvos[0].getParentVO().getPrimaryKey());
				res.put("billNo", bxvos[0].getParentVO().getDjbh());
				return res;
			} catch (Exception e) {
				ExceptionHandler.handleException(e);
			}
		} else {
			bxvo.setBxoldvo(tempvo);
			try {
				IPFBusiAction pfbo = (IPFBusiAction) NCLocator.getInstance()
						.lookup(IPFBusiAction.class);
				Object object = pfbo.processAction("EDIT", header.getDjlxbm(),
						null, bxvo, null, null);
			} catch (Exception e) {
				Object object;
				ExceptionHandler.handleException(e);
				throw new BusinessException(e.getMessage());
			}
		}

		if ((!isqc.booleanValue()) && (newbillstat > 1)) {
			header.setDjzt(Integer.valueOf(1));
			billPri.audit(new JKBXVO[] { bxvo });
		}
		if (oldPk == null) {
			IDContrastVO refVO = new IDContrastVO();
			refVO.setPk_idcontra(swapContext.getDocID());
			refVO.setPk_bill(bxvo.getParentVO().getPrimaryKey());
			refVO.setBill_type(swapContext.getBilltype());
			PfxxUtils.lookUpPFxxEJBService().insertIDvsPK(refVO);
		}
		WorkflownoteVO noteVO = new WorkflownoteVO();
		noteVO.setActiontype("Biz");
		noteVO.setBillid(bxvo.getParentVO().getPrimaryKey());
		noteVO.setBillno(header.getDjbh());
		noteVO.setCheckman(header.getOperator());
		noteVO.setChecknote(NCLangRes4VoTransl.getNCLangRes().getStrByID(
				"expensepub_0", "02011002-0076"));

		noteVO.setIscheck("N");
		noteVO.setMessagenote(NCLangRes4VoTransl.getNCLangRes().getStrByID(
				"expensepub_0", "02011002-0076"));

		noteVO.setPk_billtype(header.getDjlxbm());

		noteVO.setPk_org(header.getPk_org());
		noteVO.setReceivedeleteflag(UFBoolean.FALSE);
		noteVO.setSenddate(new UFDateTime(System.currentTimeMillis()));
		noteVO.setSenderman(header.getOperator());
		IVOPersistence vop = (IVOPersistence) NCLocator.getInstance().lookup(
				IVOPersistence.class.getName());

		vop.insertVO(noteVO);
		Map<String, String> res = new HashMap<>();
		res.put("pk", header.getPrimaryKey());
		res.put("billNo", header.getDjbh());
		return res;
	}

	private void setDeptOrgVersion(JKBXHeaderVO header)
			throws BusinessException {
		String[] depts = { header.getFydeptid(), header.getDeptid() };

		String[] orgs = { header.getPk_org(), header.getDwbm(),
				header.getFydwbm(), header.getPk_pcorg(), header.getPk_payorg() };

		Map<String, String> deptMap = ((IDeptPubService) NCLocator
				.getInstance().lookup(IDeptPubService.class))
				.getLastVIDSByDeptIDS(depts);

		Map<String, String> orgMap = ((IOrgUnitPubService) NCLocator
				.getInstance().lookup(IOrgUnitPubService.class))
				.getNewVIDSByOrgIDSAndDate(
						orgs,
						header.getDjrq() == null ? new UFDate() : header
								.getDjrq());

		if (deptMap != null) {
			header.setFydeptid_v((String) deptMap.get(header.getFydeptid()));
			header.setDeptid_v((String) deptMap.get(header.getDeptid()));
		}

		if (orgMap != null) {
			header.setPk_org_v((String) orgMap.get(header.getPk_org()));
			header.setDwbm_v((String) orgMap.get(header.getDwbm()));
			header.setFydwbm_v((String) orgMap.get(header.getFydwbm()));
			header.setPk_pcorg_v((String) orgMap.get(header.getPk_pcorg()));
			header.setPk_payorg_v((String) orgMap.get(header.getPk_payorg()));
		}
	}

	private void getBillNo(JKBXVO bxvo) throws BusinessException {
		JKBXHeaderVO parent = bxvo.getParentVO();
		FinanceBillCodeInfo info = new FinanceBillCodeInfo("djdl", "djbh",
				"pk_group", "pk_org", parent.getTableName(), "djlxbm",
				parent.getPk_billtype());

		FinanceBillCodeUtils util = new FinanceBillCodeUtils(info);
		util.createBillCode(new AggregatedValueObject[] { bxvo });
	}

	private void dealSpecialField(JKBXVO bxvo) throws BusinessException {
		dealOtherField(bxvo);

		getBillNo(bxvo);
	}

	private void dealPKItem(JKBXVO djvo) {
		String djbh = djvo.getParentVO().getPk_item();
		if (djbh != null) {
			String pk_item = null;
			try {
				Collection collection = new BaseDAO().retrieveByClause(
						ArapItemSsVO.class, " dr=0 and djbh='" + djbh + "'");

				if ((collection != null) && (collection.size() > 0)) {
					pk_item = ((ArapItemSsVO) collection.iterator().next())
							.getVouchid();
				}
			} catch (Exception e) {
				ExceptionHandler.consume(e);
				throw new BusinessRuntimeException(NCLangRes4VoTransl
						.getNCLangRes().getStrByID("expensepub_0",
								"02011002-0077")
						+ e.getMessage());
			}

			djvo.getParentVO().setPk_item(pk_item);
		}
	}

	private void dealOtherField(JKBXVO djvo) {
		JKBXHeaderVO header = djvo.getParentVO();
		header.setDr(Integer.valueOf(0));
		if ((header.getBbhl() == null)
				|| (header.getBbhl().toDouble().doubleValue() == 0.0D)) {
			header.setBbhl(new UFDouble(1.0D));
		}
		if ((header.getGroupbbhl() == null)
				|| (header.getGroupbbhl().toDouble().doubleValue() == 0.0D)) {
			header.setGroupbbhl(new UFDouble(1.0D));
		}
		if ((header.getGlobalbbhl() == null)
				|| (header.getGlobalbbhl().toDouble().doubleValue() == 0.0D)) {
			header.setGlobalbbhl(new UFDouble(1.0D));
		}
		if ((header.getPk_group() == null)
				|| (header.getPk_group().length() == 0)) {
			header.setPk_group(BXBsUtil.getPK_group());
		}
		if (header.getPk_payorg_v() == null) {
			header.setPk_payorg_v(header.getPk_payorg());
		}

		if (header.getDjzt().equals(Integer.valueOf(3))) {
			header.setSxbz(Integer.valueOf(1));
		} else {
			header.setSxbz(Integer.valueOf(0));
		}
	}

	private void dealReimtype(JKBXVO djvo) throws BusinessException {
		try {
			BXBusItemVO[] bxBusItemVOS = djvo.getBxBusItemVOS();
			if ((bxBusItemVOS != null) && (bxBusItemVOS.length != 0)) {
				Collection<SuperVO> reimType = ((IArapCommonPrivate) NCLocator
						.getInstance().lookup(IArapCommonPrivate.class))
						.getVOs(ReimTypeHeaderVO.class, "", false);

				Map<String, SuperVO> reimtypeCodeMap = VOUtils
						.changeCollectionToMap(reimType, ReimTypeHeaderVO.CODE);

				Map<String, SuperVO> reimtypeNameMap = VOUtils
						.changeCollectionToMap(reimType, ReimTypeHeaderVO.CODE);

				for (BXBusItemVO item : bxBusItemVOS) {
					if (item.getPk_reimtype() != null) {
						SuperVO codeVO = (SuperVO) reimtypeCodeMap.get(item
								.getPk_reimtype());

						SuperVO nameVO = (SuperVO) reimtypeNameMap.get(item
								.getPk_reimtype());

						if (codeVO != null) {
							item.setPk_reimtype(codeVO.getPrimaryKey());
						} else if (nameVO != null)
							item.setPk_reimtype(nameVO.getPrimaryKey());
					}
				}
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}
}
