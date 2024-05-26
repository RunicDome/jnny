package nc.impl.trn.transmng;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.filesystem.IFileSystemService;
import nc.bs.sec.esapi.NCESAPI;
import nc.bs.trade.business.HYPubBO;
import nc.bs.uif2.VersionConflictException;
import nc.bs.uif2.validation.ValidationFailure;
import nc.hr.frame.persistence.HrBatchService;
import nc.hr.utils.BillCodeHelper;
import nc.hr.utils.DataPermissionUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.hi.IBlacklistManageService;
import nc.itf.hi.IPersonRecordService;
import nc.itf.hr.frame.IHrBillCode;
import nc.itf.hr.frame.IPersistenceHome;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hr.managescope.ManagescopeFacade;
import nc.itf.hr.pf.HrPfHelper;
import nc.itf.hr.pf.IHrPf;
import nc.itf.trn.IItemSetAdapter;
import nc.itf.trn.TrnDelegator;
import nc.itf.trn.rds.IRdsManageService;
import nc.itf.trn.regmng.IRegmngQueryService;
import nc.itf.trn.transmng.ITransmngManageService;
import nc.itf.trn.transmng.ITransmngQueryService;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.md.data.access.NCObject;
import nc.md.model.IBean;
import nc.message.util.MessageCenter;
import nc.message.vo.MessageVO;
import nc.message.vo.NCMessage;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.pub.billcode.vo.BillCodeContext;
import nc.pub.tools.HiCacheUtils;
import nc.pub.tools.HiSQLHelper;
import nc.pub.tools.VOUtils;
import nc.pubitf.para.SysInitQuery;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.psn.PsnClVO;
import nc.vo.hi.blacklist.BlacklistVO;
import nc.vo.hi.entrymng.HiSendMsgHelper;
import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.pub.BillCodeRepeatBusinessException;
import nc.vo.hi.pub.RM2TRNLinkData;
import nc.vo.hi.trnstype.TrnstypeFlowVO;
import nc.vo.hi.trnstype.TrnstypeVO;
import nc.vo.hr.managescope.ManagescopeBusiregionEnum;
import nc.vo.hr.pf.PFQueryParams;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.om.job.JobTypeVO;
import nc.vo.om.joblevelsys.JobLevelVO;
import nc.vo.om.jobrank.JobRankVO;
import nc.vo.om.post.PostSeriesVO;
import nc.vo.org.AdminOrgVO;
import nc.vo.org.DeptVO;
import nc.vo.org.GroupVO;
import nc.vo.org.HROrgVO;
import nc.vo.org.JobVO;
import nc.vo.org.OrgVO;
import nc.vo.org.PostVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.filesystem.NCFileNode;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.sm.UserVO;
import nc.vo.trade.pub.HYBillVO;
import nc.vo.trn.pub.BeanUtil;
import nc.vo.trn.pub.TRNConst;
import nc.vo.trn.pub.TrnManageService;
import nc.vo.trn.transitem.TrnTransItemVO;
import nc.vo.trn.transmng.AggStapply;
import nc.vo.trn.transmng.StapplyVO;
import nc.vo.uap.pf.PFBatchExceptionInfo;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.vo.uif2.LoginContext;
import nc.vo.wfengine.definition.WorkflowTypeEnum;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

//人员调动 //离职申请单
@SuppressWarnings({ "unchecked", "rawtypes" })
public class TransmngServiceImpl extends TrnManageService implements
		ITransmngManageService, ITransmngQueryService {

	public TransmngServiceImpl() {
		this("hi_stapply");
	}

	public TransmngServiceImpl(String docName) {
		super(docName);
	}

	public AggregatedValueObject[] doCallBack(AggregatedValueObject[] vos)
			throws BusinessException {
		for (int i = 0; (vos != null) && (i < vos.length); i++) {
			deleteOldWorknote((AggStapply) vos[i]);
			OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
					(String) vos[i].getParentVO().getAttributeValue("pk_org"));
			if (null != orgVO.getDef2()) {
				OaWorkFlowUtil.backOaWorkFlow(vos[i].getParentVO().getPrimaryKey());
			}
			vos[i] = updateBill(vos[i], false);
		}
		return vos;
	}

	private void handleCtrtInfotj(StapplyVO billvo) throws BusinessException {
		boolean isCMStart = PubEnv
				.isModuleStarted(PubEnv.getPk_group(), "6011");
		if (isCMStart) {

			String cond_hasCtrt = "pk_psnorg = '"
					+ billvo.getPk_psnorg()
					+ "' and lastflag = 'Y' and conttype in (1, 2, 3) and isrefer = 'Y'";

			CtrtVO[] ctrtAllVOs = (CtrtVO[]) ((IPersistenceRetrieve) NCLocator
					.getInstance().lookup(IPersistenceRetrieve.class))
					.retrieveByClause(null, CtrtVO.class, cond_hasCtrt);

			boolean isIsreferCtrt = false;
			if (!ArrayUtils.isEmpty(ctrtAllVOs)) {
				isIsreferCtrt = true;
			}
			if (isIsreferCtrt) {
				String condition = "recordnum = 0 and isrefer = 'N' and pk_psnorg = '"
						+ billvo.getPk_psnorg() + "'";
				CtrtVO[] ctrtVOs = (CtrtVO[]) ((IPersistenceRetrieve) NCLocator
						.getInstance().lookup(IPersistenceRetrieve.class))
						.retrieveByClause(null, CtrtVO.class, condition);

				if (ArrayUtils.isEmpty(ctrtVOs)) {

					CtrtVO newCtrtVO = new CtrtVO();
					try {
						BeanUtils.copyProperties(newCtrtVO, ctrtAllVOs[0]);
					} catch (Exception e) {
						Logger.error(e.getMessage(), e);
					}
					newCtrtVO.setRecordnum(Integer.valueOf(0));
					newCtrtVO.setLastflag(UFBoolean.FALSE);
					newCtrtVO.setIsrefer(UFBoolean.FALSE);
					int conttype = billvo.getIsrelease() == UFBoolean.TRUE ? 4
							: 5;
					newCtrtVO.setConttype(Integer.valueOf(conttype));
					newCtrtVO.setSigndate(new UFLiteralDate());
					newCtrtVO
							.setTermmonth(ctrtAllVOs[0].getTermmonth() == null ? null
									: ctrtAllVOs[0].getTermmonth());
					newCtrtVO
							.setPromonth(ctrtAllVOs[0].getPromonth() == null ? null
									: ctrtAllVOs[0].getPromonth());
					newCtrtVO.setPresenter(Integer.valueOf(1));

					((IPersistenceUpdate) NCLocator.getInstance().lookup(
							IPersistenceUpdate.class)).insertVO(null,
							newCtrtVO, null);

				} else if ((ctrtVOs[0].getConttype().intValue() == 1)
						|| (ctrtVOs[0].getConttype().intValue() == 2)
						|| (ctrtVOs[0].getConttype().intValue() == 3)) {
					throw new BusinessException(ResHelper.getString("6009tran",
							"X6009tran0060"));
				}
			}
		}
	}

	public AggregatedValueObject commitBill_RequiresNew(
			AggregatedValueObject aggvo) throws BusinessException {
		StapplyVO billvo = (StapplyVO) ((AggStapply) aggvo).getParentVO();

		if (((billvo.getIsend() != null) && (billvo.getIsend().booleanValue()))
				|| ((billvo.getIsrelease() != null) && (billvo.getIsrelease()
						.booleanValue()))) {

			handleCtrtInfotj(billvo);
		}

		String errMsg = getMsg(billvo);
		if (!StringUtils.isBlank(errMsg)) {
			throw new BusinessException(ResHelper.getString("6009tran",
					"06009tran0153"));
		}

		if (!isHasFile(billvo)) {
			throw new BusinessException(ResHelper.getString("6009tran",
					"06009tran0177"));
		}

		String billmaker = (String) aggvo.getParentVO().getAttributeValue(
				"billmaker");
		if ((billmaker != null) && ("NC_USER0000000000000".equals(billmaker))) {
			aggvo.getParentVO().setAttributeValue("billmaker",
					PubEnv.getPk_user());
		}
		return getIHrPf().commitBill_RequiresNew(aggvo);
	}

	public PfProcessBatchRetObject batchCommitBill(AggregatedValueObject[] bills)
			throws BusinessException {
		PFBatchExceptionInfo exInfo = new PFBatchExceptionInfo();
		ArrayList<AggregatedValueObject> successObj = new ArrayList();
		for (int i = 0; i < bills.length; i++) {
			try {
				AggregatedValueObject aggVO = ((ITransmngManageService) NCLocator
						.getInstance().lookup(ITransmngManageService.class))
						.commitBill_RequiresNew(bills[i]);
				successObj.add(aggVO);
			} catch (Exception e) {
				if ((e instanceof VersionConflictException)) {
					exInfo.putErrorMessage(
							i,
							bills[i],
							((VersionConflictException) e).getBusiObject() == null ? ""
									: ((VersionConflictException) e)
											.getBusiObject() + "");

				} else {
					exInfo.putErrorMessage(i, bills[i], StringUtils.isBlank(e
							.getMessage()) ? "" : e.getMessage());
				}
				Logger.error(e.getMessage(), e);
			}
		}
		return new PfProcessBatchRetObject(
				successObj.toArray(new AggregatedValueObject[0]), exInfo);
	}

	public AggStapply[] batchSaveBill(AggStapply aggvo, ArrayList<String> pks,
			Integer transMode, String transType, String billType,
			LoginContext context, String[] billCodes) throws BusinessException {
		boolean isAutoGenerateBillCode = isAutoGenerateBillCode(billType,
				context.getPk_group(), context.getPk_org());
		ArrayList<AggStapply> al = new ArrayList();
		if (!isAutoGenerateBillCode) {

			BillCodeHelper
					.lockBillCodeRule("hr_auto_billcode" + billType, 100L);
		}
		try {
			InSQLCreator isc = new InSQLCreator();
			String insql = isc.getInSQL((String[]) pks.toArray(new String[0]));
			String condition = "pk_psnjob in (" + insql + ")";
			Collection<PsnJobVO> cvos = getDao().retrieveByClause(
					PsnJobVO.class, condition);
			PsnJobVO[] pvos = (PsnJobVO[]) cvos.toArray(new PsnJobVO[0]);
			HashMap<String, PsnJobVO> psnmap = new HashMap();
			for (PsnJobVO pvo : pvos) {
				psnmap.put(pvo.getPk_psnjob(), pvo);
			}
			TrnTransItemVO[] itemvos = (TrnTransItemVO[]) TrnDelegator
					.getIItemSetQueryService().queryItemSetByOrg(
							"8ce2d67d-d671-419c-9d80-ab4f135be3b8",
							context.getPk_group(), context.getPk_org(),
							transType);

			String prefix = "ZD" + billType
					+ PubEnv.getServerDate().toStdString();

			String flowCode = SQLHelper.getFlowCode(prefix, "bill_code",
					StapplyVO.class);
			StapplyVO head;
			for (int i = 0; i < pks.size(); i++) {
				AggStapply temp = clone(aggvo);
				head = (StapplyVO) temp.getParentVO();
				if ((isAutoGenerateBillCode) && (billCodes != null)
						&& (billCodes.length > 0) && (billCodes[i] != null)) {
					head.setBill_code(billCodes[i]);
				} else {
					head.setBill_code(prefix + "_" + getFlowCode(flowCode, i));
				}
				head.setStapply_mode(transMode);
				head.setPk_trnstype(transType);
				head.setApprove_state(Integer.valueOf(-1));
				head.setBillmaker(context.getPk_loginUser());

				head.setPk_billtype(billType);
				head.setPk_org(context.getPk_org());
				head.setPk_group(context.getPk_group());
				head.setFun_code(context.getNodeCode());

				PsnJobVO oldJobVO = (PsnJobVO) psnmap.get(pks.get(i));
				head.setPk_psnjob(oldJobVO.getPk_psnjob());
				head.setPk_psndoc(oldJobVO.getPk_psndoc());
				head.setPk_psnorg(oldJobVO.getPk_psnorg());
				head.setAssgid(oldJobVO.getAssgid());
				if (("6113".equals(billType)) && (3 == transMode.intValue())) {

					head.setPk_old_hi_org(HiSQLHelper.getHrorg(
							oldJobVO.getPk_psnorg(), oldJobVO.getAssgid()));
				} else {
					head.setPk_old_hi_org(context.getPk_org());
				}

				head.setPk_old_hrcm_org(HiSQLHelper.getEveryHrorg(
						oldJobVO.getPk_psnorg(), oldJobVO.getAssgid(),
						ManagescopeBusiregionEnum.psnpact));

				for (String attr : oldJobVO.getAttributeNames()) {
					head.setAttributeValue("old" + attr,
							oldJobVO.getAttributeValue(attr));
				}

				String[] flds = { "newpk_post", "newpk_postseries",
						"newpk_job", "newpk_jobgrade", "newpk_jobrank",
						"newseries" };

				for (int j = 0; (itemvos != null) && (j < itemvos.length); j++) {
					if ((!itemvos[j].getItemkey().startsWith("old"))
							&& (!ArrayUtils.contains(flds,
									itemvos[j].getItemkey()))) {

						if ((itemvos[j] != null)
								&& (itemvos[j].getIsdefault() != null)
								&& (itemvos[j].getIsdefault().booleanValue())
								&& (head.getAttributeValue(itemvos[j]
										.getItemkey()) == null)) {

							if ((("6113".equals(billType)) && (2 == transMode
									.intValue())) || ("6115".equals(billType))) {

								String[] fldsout = { "newpk_dept" };
								if (!ArrayUtils.contains(fldsout,
										itemvos[j].getItemkey())) {
									head.setAttributeValue(itemvos[j]
											.getItemkey(), oldJobVO
											.getAttributeValue(itemvos[j]
													.getItemkey().substring(3)));
								}

							} else {
								head.setAttributeValue(itemvos[j].getItemkey(),
										oldJobVO.getAttributeValue(itemvos[j]
												.getItemkey().substring(3)));
							}
						}
					}
				}

				String newPost = head.getNewpk_post();
				String newJob = head.getNewpk_job();

				if ((newPost == null) || (newJob == null)) {

					if ((newPost != null) && (newJob == null)) {

						TrnTransItemVO rankItem = getItemByItemkey(itemvos,
								"newpk_jobrank");
						if ((rankItem != null)
								&& (rankItem.getIsdefault() != null)
								&& (rankItem.getIsdefault().booleanValue())
								&& (head.getNewpk_jobrank() == null)) {

							head.setNewpk_jobrank(head.getOldpk_jobrank());
						}
					} else if ((newPost != null) || (newJob == null)) {

						if (head.getOldpk_dept().equals(head.getNewpk_dept())) {
							TrnTransItemVO postItem = getItemByItemkey(itemvos,
									"newpk_post");
							TrnTransItemVO jobItem = getItemByItemkey(itemvos,
									"newpk_job");
							if ((postItem != null)
									&& (postItem.getIsdefault() != null)
									&& (postItem.getIsdefault().booleanValue())) {

								head.setNewpk_post(head.getOldpk_post());
								head.setNewpk_postseries(head
										.getOldpk_postseries());
								head.setNewpk_job(head.getOldpk_job());
								head.setNewpk_jobrank(head.getOldpk_jobrank());
								head.setNewpk_jobgrade(head.getOldpk_jobgrade());
								head.setNewseries(head.getOldseries());
							} else if ((jobItem != null)
									&& (jobItem.getIsdefault() != null)
									&& (jobItem.getIsdefault().booleanValue())) {

								head.setNewpk_job(head.getOldpk_job());
								head.setNewpk_jobrank(head.getOldpk_jobrank());
								head.setNewpk_jobgrade(head.getOldpk_jobgrade());
								head.setNewseries(head.getOldseries());
							}
						}
					}
				}
				al.add(temp);
			}

			checkBillCodeRepeat((AggregatedValueObject[]) al
					.toArray(new AggStapply[0]));
			HrBatchService hbs = new HrBatchService("");

			AggStapply[] ret = (AggStapply[]) hbs.insert(al
					.toArray(new AggStapply[0]));
			return ret;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (!isAutoGenerateBillCode) {

				BillCodeHelper
						.unlockBillCodeRule("hr_auto_billcode" + billType);
			}
		}
	}

	private TrnTransItemVO getItemByItemkey(TrnTransItemVO[] itemvos,
			String itemKey) {
		for (int i = 0; (itemvos != null) && (i < itemvos.length); i++) {
			if (itemKey.equals(itemvos[i].getItemkey())) {
				return itemvos[i];
			}
		}
		return null;
	}

	private AggStapply clone(AggStapply src) {
		AggStapply trg = new AggStapply();
		StapplyVO head = new StapplyVO();
		trg.setParentVO(head);
		for (String attrName : src.getParentVO().getAttributeNames()) {
			trg.getParentVO().setAttributeValue(attrName,
					src.getParentVO().getAttributeValue(attrName));
		}
		return trg;
	}

	public Hashtable<String, String[]> createUserValue(
			AggregatedValueObject[] aggvos) throws BusinessException {
		String[] fieldCode = TRNConst.FIELDCODE_TRN;
		Hashtable<String, String[]> hm = new Hashtable();
		for (int i = 0; (aggvos != null) && (i < aggvos.length); i++) {
			StapplyVO bill = (StapplyVO) ((AggStapply) aggvos[i]).getParentVO();
			for (int j = 0; j < fieldCode.length; j++) {
				String value = "";
				if ("bill_code".equals(fieldCode[j])) {

					value = bill.getBill_code();
				} else if ("approve_state".equals(fieldCode[j])) {

					value = TRNConst.getBillStateName(Integer.valueOf(bill
							.getApprove_state() == null ? 102 : bill
							.getApprove_state().intValue()));
				} else if ("pk_psnjob".equals(fieldCode[j])) {

					value = VOUtils.getDocName(PsndocVO.class,
							bill.getPk_psndoc());
				} else if ("effectdate".equals(fieldCode[j])) {
					value = bill.getEffectdate() == null ? "" : bill
							.getEffectdate().toStdString();
				} else if ("oldpk_org".equals(fieldCode[j])) {

					value = VOUtils
							.getDocName(OrgVO.class, bill.getOldpk_org());
				} else if ("newpk_org".equals(fieldCode[j])) {

					value = VOUtils
							.getDocName(OrgVO.class, bill.getNewpk_org());
				} else if ("oldpk_dept".equals(fieldCode[j])) {

					value = VOUtils.getDocName(DeptVO.class,
							bill.getOldpk_dept());
				} else if ("newpk_dept".equals(fieldCode[j])) {

					value = VOUtils.getDocName(DeptVO.class,
							bill.getNewpk_dept());
				} else if ("oldpk_psncl".equals(fieldCode[j])) {

					value = VOUtils.getDocName(PsnClVO.class,
							bill.getOldpk_psncl());
				} else if ("newpk_psncl".equals(fieldCode[j])) {

					value = VOUtils.getDocName(PsnClVO.class,
							bill.getNewpk_psncl());
				} else if ("oldpk_post".equals(fieldCode[j])) {

					value = VOUtils.getDocName(PostVO.class,
							bill.getOldpk_post());
				} else if ("newpk_post".equals(fieldCode[j])) {

					value = VOUtils.getDocName(PostVO.class,
							bill.getNewpk_post());
				} else if ("oldpk_job".equals(fieldCode[j])) {

					value = VOUtils
							.getDocName(JobVO.class, bill.getOldpk_job());
				} else if ("newpk_job".equals(fieldCode[j])) {

					value = VOUtils
							.getDocName(JobVO.class, bill.getNewpk_job());
				} else {
					value = "";
				}
				hm.put(fieldCode[j] + i, new String[] { value });
			}
		}
		return hm;
	}

	private void deleteOldWorknote(AggStapply vo) throws BusinessException {
		getIHrPf().deleteWorkflowNote(vo);
	}

	public PfProcessBatchRetObject directApprove(Object[] selData,
			String approveNote, int blPassed) throws BusinessException {
		PFBatchExceptionInfo errInfo = new PFBatchExceptionInfo();
		ArrayList<AggStapply> bill = new ArrayList();

		for (int i = 0; i < selData.length; i++) {
			try {
				AggStapply retVO = ((ITransmngManageService) NCLocator
						.getInstance().lookup(ITransmngManageService.class))
						.singleDirectApprove_RequiresNew(
								(AggStapply) selData[i], PubEnv.getPk_user(),
								PubEnv.getServerTime(), approveNote, blPassed);

				bill.add(retVO);
			} catch (Exception e) {
				if ((e instanceof VersionConflictException)) {
					errInfo.putErrorMessage(
							i,
							selData[i],
							((VersionConflictException) e).getBusiObject() == null ? ""
									: ((VersionConflictException) e)
											.getBusiObject() + "");

				} else {
					errInfo.putErrorMessage(i, selData[i], StringUtils
							.isBlank(e.getMessage()) ? "" : e.getMessage());
				}
				Logger.error(e.getMessage(), e);
			}
		}

		return new PfProcessBatchRetObject(bill.toArray(new AggStapply[0]),
				errInfo);
	}

	public AggStapply singleDirectApprove_RequiresNew(AggStapply aggvo,
			String pk_user, UFDateTime approveTime, String approveNote,
			int blPassed) throws BusinessException {
		IFlowBizItf itf = (IFlowBizItf) NCObject.newInstance(aggvo)
				.getBizInterface(IFlowBizItf.class);
		StapplyVO billvo = (StapplyVO) aggvo.getParentVO();

		if (blPassed == 1) {

			if (((3 == billvo.getApprove_state().intValue()) && (1 == billvo
					.getStapply_mode().intValue()))
					|| ((2 == billvo.getApprove_state().intValue()) && ((3 == billvo
							.getStapply_mode().intValue()) || (2 == billvo
							.getStapply_mode().intValue())))) {

				if (!StringUtils.isBlank(getMsg(billvo))) {
					throw new BusinessException(ResHelper.getString("6009tran",
							"06009tran0153"));
				}
			}
		}

		if ((blPassed == 1)
				&& (3 == billvo.getApprove_state().intValue())
				&& (billvo.getPk_billtype().equals("6113"))
				&& (billvo.getStapply_mode() != null)
				&& ((3 == billvo.getStapply_mode().intValue()) || (2 == billvo
						.getStapply_mode().intValue()))) {

			String pkOrg = billvo.getStapply_mode().intValue() == 3 ? billvo
					.getPk_old_hi_org() : billvo.getPk_hi_org();

			String tempCode = "600713";
			HiSendMsgHelper.sendMessage1(tempCode, new AggStapply[] { aggvo },
					pkOrg);
		}

		if (blPassed == 1) {

			if ((3 == billvo.getApprove_state().intValue())
					&& (billvo.getStapply_mode() != null)
					&& (billvo.getPk_billtype().equals("6113"))
					&& ((3 == billvo.getStapply_mode().intValue()) || (2 == billvo
							.getStapply_mode().intValue()))) {

				writeTransApproveInfo(billvo, pk_user, approveTime,
						approveNote, Integer.valueOf(2));

			} else {
				writeTransApproveInfo(billvo, pk_user, approveTime,
						approveNote, Integer.valueOf(blPassed));
			}
		} else if (blPassed == 0) {

			writeTransApproveInfo(billvo, pk_user, approveTime, approveNote,
					Integer.valueOf(blPassed));

		} else {
			writeTransApproveInfo(billvo, null, null, approveNote,
					Integer.valueOf(blPassed));

			invalidWorkflowNote(itf, true);
		}

		writeWorknote(billvo, pk_user, approveNote, blPassed,
				billvo.getPk_billtype());

		if (((billvo.getIsend() != null) && (billvo.getIsend().booleanValue()))
				|| ((billvo.getIsrelease() != null) && (billvo.getIsrelease()
						.booleanValue()))) {

			handleCtrtInfo(billvo);
		}

		return (AggStapply) updateBill(aggvo, false);
	}

	private void handleCtrtInfo(StapplyVO billvo) throws BusinessException {
		boolean isCMStart = PubEnv
				.isModuleStarted(PubEnv.getPk_group(), "6011");
		if ((isCMStart) && (1 == billvo.getApprove_state().intValue())) {

			String cond_hasCtrt = "pk_psnorg = '"
					+ billvo.getPk_psnorg()
					+ "' and lastflag = 'Y' and conttype in (1, 2, 3) and isrefer = 'Y'";

			CtrtVO[] ctrtAllVOs = (CtrtVO[]) ((IPersistenceRetrieve) NCLocator
					.getInstance().lookup(IPersistenceRetrieve.class))
					.retrieveByClause(null, CtrtVO.class, cond_hasCtrt);

			boolean isIsreferCtrt = false;
			if (!ArrayUtils.isEmpty(ctrtAllVOs)) {
				isIsreferCtrt = true;
			}
			if (isIsreferCtrt) {
				String condition = "recordnum = 0 and isrefer = 'N' and pk_psnorg = '"
						+ billvo.getPk_psnorg() + "'";
				CtrtVO[] ctrtVOs = (CtrtVO[]) ((IPersistenceRetrieve) NCLocator
						.getInstance().lookup(IPersistenceRetrieve.class))
						.retrieveByClause(null, CtrtVO.class, condition);

				if (ArrayUtils.isEmpty(ctrtVOs)) {

					CtrtVO newCtrtVO = new CtrtVO();
					try {
						BeanUtils.copyProperties(newCtrtVO, ctrtAllVOs[0]);
					} catch (Exception e) {
						Logger.error(e.getMessage(), e);
					}
					newCtrtVO.setRecordnum(Integer.valueOf(0));
					newCtrtVO.setLastflag(UFBoolean.FALSE);
					newCtrtVO.setIsrefer(UFBoolean.FALSE);
					int conttype = billvo.getIsrelease() == UFBoolean.TRUE ? 4
							: 5;
					newCtrtVO.setConttype(Integer.valueOf(conttype));
					newCtrtVO.setSigndate(new UFLiteralDate());
					newCtrtVO
							.setTermmonth(ctrtAllVOs[0].getTermmonth() == null ? null
									: ctrtAllVOs[0].getTermmonth());
					newCtrtVO
							.setPromonth(ctrtAllVOs[0].getPromonth() == null ? null
									: ctrtAllVOs[0].getPromonth());
					newCtrtVO.setPresenter(Integer.valueOf(1));

					((IPersistenceUpdate) NCLocator.getInstance().lookup(
							IPersistenceUpdate.class)).insertVO(null,
							newCtrtVO, null);

				} else if ((ctrtVOs[0].getConttype().intValue() == 1)
						|| (ctrtVOs[0].getConttype().intValue() == 2)
						|| (ctrtVOs[0].getConttype().intValue() == 3)) {
					throw new BusinessException(ResHelper.getString("6009tran",
							"X6009tran0060"));
				}
			}
		}
	}

	public PfProcessBatchRetObject directUnApprove(AggregatedValueObject[] bills)
			throws BusinessException {
		PFBatchExceptionInfo errInfo = new PFBatchExceptionInfo();
		ArrayList<AggStapply> bill = new ArrayList();

		for (int i = 0; i < bills.length; i++) {
			try {
				AggStapply retVO = ((ITransmngManageService) NCLocator
						.getInstance().lookup(ITransmngManageService.class))
						.singleDirectUnApprove_RequiresNew((AggStapply) bills[i]);

				bill.add(retVO);
			} catch (Exception e) {
				if ((e instanceof VersionConflictException)) {
					errInfo.putErrorMessage(
							i,
							bills[i],
							((VersionConflictException) e).getBusiObject() == null ? ""
									: ((VersionConflictException) e)
											.getBusiObject() + "");

				} else {
					errInfo.putErrorMessage(i, bills[i], StringUtils.isBlank(e
							.getMessage()) ? "" : e.getMessage());
				}
				Logger.error(e.getMessage(), e);
			}
		}

		return new PfProcessBatchRetObject(bill.toArray(new AggStapply[0]),
				errInfo);
	}

	public AggStapply singleDirectUnApprove_RequiresNew(AggStapply bill)
			throws BusinessException {
		IFlowBizItf itf = (IFlowBizItf) NCObject.newInstance(bill)
				.getBizInterface(IFlowBizItf.class);
		StapplyVO head = (StapplyVO) bill.getParentVO();

		int count = getValidNoteCount(itf);

		if ((count >= 2)
				&& (head.getPk_billtype().equals("6113"))
				&& (head.getStapply_mode() != null)
				&& ((3 == head.getStapply_mode().intValue()) || (2 == head
						.getStapply_mode().intValue()))) {

			WorkflownoteVO last = getLastNote(getAllWorkflownoteVO(itf));
			writeTransApproveInfo(head, last.getSenderman(),
					PubEnv.getServerTime(), "", Integer.valueOf(2));

		} else {
			writeTransApproveInfo(head, null, null, null, Integer.valueOf(3));
		}

		invalidWorkflowNote(itf, false);
		return (AggStapply) updateBill(bill, false);
	}

	public Object doApprove(AggregatedValueObject[] vosin)
			throws BusinessException {
		AggStapply[] vos = ((ITransmngManageService) NCLocator.getInstance()
				.lookup(ITransmngManageService.class))
				.batchUpdateBill_RequiresNew(vosin);

		return execBills(vos);
	}

	public AggStapply[] batchUpdateBill_RequiresNew(
			AggregatedValueObject[] vosin) throws BusinessException {
		AggStapply[] vos = new AggStapply[vosin.length];
		for (int i = 0; i < vosin.length; i++) {
			vos[i] = ((AggStapply) vosin[i]);
		}
		validate(vos);
		for (int i = 0; i < vos.length; i++) {

			StapplyVO billvo = (StapplyVO) vos[i].getParentVO();
			if (1 == billvo.getApprove_state().intValue()) {
				if (((billvo.getIsend() != null) && (billvo.getIsend()
						.booleanValue()))
						|| ((billvo.getIsrelease() != null) && (billvo
								.getIsrelease().booleanValue()))) {

					handleCtrtInfo(billvo);
				}
			}
			vos[i] = ((AggStapply) updateBill(vos[i], false));
		}
		return vos;
	}

	Boolean isqueryctrt;

	public Boolean getIsqueryctrt() {
		return isqueryctrt;
	}

	public void setIsqueryctrt(Boolean isqueryctrt) {
		this.isqueryctrt = isqueryctrt;
	}

	public AggStapply[] execBills(AggStapply[] vos) throws BusinessException {
		ArrayList<AggStapply> al = new ArrayList();
		ArrayList<AggStapply> allvo = new ArrayList();
		for (int i = 0; i < vos.length; i++) {
			AggStapply agg = vos[i];
			Integer apprState = ((StapplyVO) agg.getParentVO())
					.getApprove_state();
			UFLiteralDate effectDate = ((StapplyVO) agg.getParentVO())
					.getEffectdate();
			if ((effectDate != null)
					&& (effectDate.compareTo(PubEnv.getServerLiteralDate()) <= 0)
					&& (apprState != null) && (apprState.intValue() == 1)) {

				al.add(agg);
			}

			allvo.add(agg);
		}

		ArrayList<AggStapply> bills = new ArrayList();

		if (al.size() > 0) {

			this.isqueryctrt = true;
			setIsqueryctrt(true);

			LoginContext tempContext = new LoginContext();
			AggStapply aggvo = (AggStapply) al.get(0);
			StapplyVO parentVO = (StapplyVO) aggvo.getParentVO();
			tempContext.setPk_group(parentVO.getPk_group());
			tempContext.setPk_org(parentVO.getPk_org());

			HashMap<String, Object> result = ((ITransmngManageService) NCLocator
					.getInstance().lookup(ITransmngManageService.class))
					.execBills((AggStapply[]) al.toArray(new AggStapply[0]),
							tempContext, false);

			bills = (ArrayList) result.get("RESULT_BILLS");
			String msg = (String) result.get("RESULT_MSG");
			if (!StringUtils.isBlank(msg)) {

				NCMessage ncMessage = new NCMessage();
				MessageVO messageVO = new MessageVO();
				messageVO.setMsgsourcetype("notice");
				messageVO.setReceiver(PubEnv.getPk_user());
				messageVO.setIsdelete(UFBoolean.FALSE);
				messageVO.setSender("NC_USER0000000000000");

				messageVO.setSendtime(PubEnv.getServerTime());
				messageVO.setDr(Integer.valueOf(0));
				messageVO.setSubject(ResHelper.getString("6007entry",
						"16007entry0015"));

				messageVO.setContent(msg);
				ncMessage.setMessage(messageVO);
				NCMessage[] message = new NCMessage[1];
				message[0] = ncMessage;
				try {
					MessageCenter.sendMessage(message);
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
			}
		}

		if ((bills != null) && (bills.size() > 0)) {
			for (int i = 0; i < allvo.size(); i++) {
				AggStapply aggivo = (AggStapply) allvo.get(i);
				for (int j = 0; j < bills.size(); j++) {
					AggStapply aggjvo = (AggStapply) allvo.get(j);
					if (aggivo.getParentVO().getPrimaryKey()
							.equals(aggjvo.getParentVO().getPrimaryKey())) {
						allvo.remove(i);
					}
				}
			}
			vos = (AggStapply[]) ArrayUtils.addAll(
					allvo.toArray(new AggStapply[0]),
					bills.toArray(new AggStapply[0]));
		}

		return vos;
	}

	public Object doCommit(AggStapply[] vos) throws BusinessException {
		String pk_org = ((StapplyVO) vos[0].getParentVO()).getPk_org();
		String billtype = ((StapplyVO) vos[0].getParentVO()).getPk_billtype();
		List<AggregatedValueObject> ls = new ArrayList();
		String functionCode = ((StapplyVO) vos[0].getParentVO()).getFun_code();
		for (int i = 0; (vos != null) && (i < vos.length); i++) {
			StapplyVO billvo = (StapplyVO) vos[i].getParentVO();

			if (!isHasFile(billvo)) {
				throw new BusinessException(ResHelper.getString("6009tran",
						"06009tran0177"));
			}

			billvo.setApprove_state(Integer.valueOf(3));
			if ("NC_USER0000000000000".equals(billvo.getBillmaker())) {
				billvo.setBillmaker(PubEnv.getPk_user());
			}
			vos[i].setParentVO(billvo);
			vos[i] = ((AggStapply) updateBill(vos[i], false));
			ls.add(vos[i]);
		}
		Integer approvetype = Integer.valueOf(1);
		try {
			approvetype = SysInitQuery.getParaInt(pk_org,
					(String) IHrPf.hashBillTypePara.get(billtype));
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		if ((approvetype != null) && (approvetype.intValue() == 0)) {

			String tempCode = null;
			if ("60090dimissionapply".equals(functionCode)) {

				tempCode = "600714";

			} else {
				tempCode = "600713";
			}
			HiSendMsgHelper.sendMessage1(tempCode, vos, pk_org);
		}
		senOaData(ls.toArray(new AggregatedValueObject[0]));
		return ls.toArray(new AggregatedValueObject[0]);
	}

	// TODO 单据同步OA功能 start create by zwh
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

	private WorkFlowBill getWorkFlowBill(AggregatedValueObject temp)
			throws BusinessException {
		String where = " nvl(dr,0) = 0 and pk_bill = '"
				+ ((StapplyVO) temp.getParentVO()).getPk_hi_stapply() + "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
				.queryByCondition(WorkFlowBill.class, where);
		WorkFlowBill workFlowBill = new WorkFlowBill();
		if (null != workFlowBills && workFlowBills.length > 1) {
			throw new BusinessException("查询到多条流程记录");
		}
		if (null != workFlowBills && workFlowBills.length == 1) {
			workFlowBill = workFlowBills[0];
			if ("60090transapply".equals((((StapplyVO) temp.getParentVO())
					.getFun_code()))) {
				workFlowBill.setWorkflowName("调配申请单");
			} else if ("60090dimissionapply".equals((((StapplyVO) temp
					.getParentVO()).getFun_code()))) {
				workFlowBill.setWorkflowName("离职申请单");
			}
		} else {
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class,
					((StapplyVO) temp.getParentVO()).getBillmaker());
			workFlowBill.setDef5(userVO.getUser_code());
			workFlowBill.setPk_group(((StapplyVO) temp.getParentVO())
					.getPk_group());
			workFlowBill
					.setPk_org(((StapplyVO) temp.getParentVO()).getPk_org());
			workFlowBill.setPk_bill(((StapplyVO) temp.getParentVO())
					.getPk_hi_stapply());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreator(((StapplyVO) temp.getParentVO())
					.getCreator());

			if ("60090transapply".equals((((StapplyVO) temp.getParentVO())
					.getFun_code()))) {
				workFlowBill.setBill_code("6113");
				workFlowBill.setWorkflowId("98");
				workFlowBill.setWorkflowName("调配申请单");
			} else if ("60090dimissionapply".equals((((StapplyVO) temp
					.getParentVO()).getFun_code()))) {
				workFlowBill.setBill_code("6115");
				workFlowBill.setWorkflowId("114");
				workFlowBill.setWorkflowName("离职申请单");
			}
		}
		return workFlowBill;
	}

	private void senOaData(AggregatedValueObject[] billVOs)
			throws BusinessException {
		for (AggregatedValueObject temp : billVOs) {
			// 调配申请
			// if(( ((StapplyVO)
			// temp.getParentVO()).getTranstype()).contains("6009")){
			if ("60090transapply".equals((((StapplyVO) temp.getParentVO())
					.getFun_code()))) {
				JSONArray headData = getMainMap((StapplyVO) temp.getParentVO());
				JSONArray bodyData = null;

				OaWorkFlowUtil.sendOaData(headData, bodyData,
						getWorkFlowBill(temp));
			} else if ("60090dimissionapply".equals((((StapplyVO) temp
					.getParentVO()).getFun_code()))) {
				// 离职申请
				JSONArray headData = getMainMap((StapplyVO) temp.getParentVO());
				JSONArray bodyData = null;

				OaWorkFlowUtil.sendOaData(headData, bodyData,
						getWorkFlowBill(temp));
			}
		}
	}

	private JSONArray getMainMap(StapplyVO parentVO) throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO) {

			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getCode());
			list.add(szgs);
		}

		Map sqrq = OaWorkFlowUtil.listAdd("sqrq", parentVO.getApply_date()
				.getYear()
				+ "-"
				+ parentVO.getApply_date().getStrMonth()
				+ "-"
				+ parentVO.getApply_date().getStrDay());
		list.add(sqrq);

		Map sqdbh = OaWorkFlowUtil.listAdd("sqdbh", parentVO.getBill_code());
		list.add(sqdbh);

		Map lclx = OaWorkFlowUtil.listAdd("lclx", parentVO.getBusiness_type());
		list.add(lclx);

		Map spzt = OaWorkFlowUtil.listAdd("spzt", parentVO.getApprove_state()
				+ "");
		list.add(spzt);

		Map sqr = OaWorkFlowUtil.listAdd("sqr",
				VOUtils.getDocName(PsndocVO.class, parentVO.getBillmaker()));
		list.add(sqr);

		Map dpr = OaWorkFlowUtil.listAdd("dpr",
				VOUtils.getDocName(PsnJobVO.class, parentVO.getPk_psnjob()));
		list.add(dpr);

		int dpfsi = parentVO.getStapply_mode();
		String dpfss = "";
		if (dpfsi == 1) {
			dpfss = "组织内调配";
		} else if (dpfsi == 2) {
			dpfss = "调出";
		} else if (dpfsi == 3) {
			dpfss = "调入";
		}
		Map dpfs = OaWorkFlowUtil.listAdd("dpfs", dpfss);
		list.add(dpfs);

		Map dplzlx = OaWorkFlowUtil
				.listAdd(
						"dplzlx",
						VOUtils.getDocName(TrnstypeVO.class,
								parentVO.getPk_trnstype()));
		list.add(dplzlx);

		Map dplzyy = OaWorkFlowUtil.listAdd("dplzyy",
				VOUtils.getDocName(DefdocVO.class, parentVO.getSreason()));
		list.add(dplzyy);

		if ("60090dimissionapply".equals(parentVO.getFun_code())) {

			String lname = (String) getHyPubBO().findColValue(
					"bd_psncl",
					"name",
					"nvl(dr,0) = 0 and  pk_psncl ='"
							+ parentVO.getNewpk_psncl() + "'");
			Map xrylb = OaWorkFlowUtil.listAdd("xrylb", lname);
			list.add(xrylb);
		} else {

			String dname = (String) getHyPubBO().findColValue(
					"bd_psncl",
					"name",
					"nvl(dr,0) = 0 and  pk_psncl ='"
							+ parentVO.getOldpk_psncl() + "'");
			Map rylb = OaWorkFlowUtil.listAdd("rylb", dname);
			list.add(rylb);

			String lname = (String) getHyPubBO().findColValue(
					"bd_psncl",
					"name",
					"nvl(dr,0) = 0 and  pk_psncl ='"
							+ parentVO.getNewpk_psncl() + "'");
			Map xrylb = OaWorkFlowUtil.listAdd("xrylb", lname);
			list.add(xrylb);
		}

		Map sxrq = OaWorkFlowUtil.listAdd("sxrq", parentVO.getEffectdate()
				.getYear()
				+ "-"
				+ parentVO.getEffectdate().getStrMonth()
				+ "-"
				+ parentVO.getEffectdate().getStrDay());
		list.add(sxrq);

		if (parentVO.getTrial_flag() == null) {
			Map sy = OaWorkFlowUtil.listAdd("sy", "0");
			list.add(sy);
		} else {
			Map sy = OaWorkFlowUtil.listAdd("sy", parentVO.getTrial_flag()
					.booleanValue() ? "是" : "否");
			list.add(sy);
		}

		Map gwsyqx = OaWorkFlowUtil.listAdd("gwsyqx", parentVO.getTrialdays()
				+ "");
		list.add(gwsyqx);

		Map gwsyqxdw = OaWorkFlowUtil.listAdd("gwsyqxdw",
				parentVO.getTrial_unit() + "");
		list.add(gwsyqxdw);

		if (parentVO.getTrialbegindate() == null) {
			Map syksrq = OaWorkFlowUtil.listAdd("syksrq", "");
			list.add(syksrq);
		} else {
			Map syksrq = OaWorkFlowUtil.listAdd("syksrq", parentVO
					.getTrialbegindate().getYear()
					+ "-"
					+ parentVO.getTrialbegindate().getStrMonth()
					+ "-"
					+ parentVO.getTrialbegindate().getStrDay());
			list.add(syksrq);
		}

		Map dplzsm = OaWorkFlowUtil.listAdd("dplzsm", parentVO.getMemo());
		list.add(dplzsm);

		if (parentVO.getTrialenddate() == null) {
			Map syjsrq = OaWorkFlowUtil.listAdd("syjsrq", "");
			list.add(syjsrq);
		} else {
			Map syjsrq = OaWorkFlowUtil.listAdd("syjsrq", parentVO
					.getTrialenddate().getYear()
					+ "-"
					+ parentVO.getTrialenddate().getStrMonth()
					+ "-"
					+ parentVO.getTrialenddate().getStrDay());
			list.add(syjsrq);
		}

		Map zzl = OaWorkFlowUtil.listAdd("zzl",
				VOUtils.getDocName(AdminOrgVO.class, parentVO.getOldpk_org()));
		list.add(zzl);

		HRDeptVO oldDept = (HRDeptVO) getHyPubBO().queryByPrimaryKey(
				HRDeptVO.class, parentVO.getOldpk_dept());
		// DefdocVO old = (DefdocVO)
		// getHyPubBO().queryByPrimaryKey(DefdocVO.class,
		// oldDept.getDeptlevel());
		// String str = "0";
		// String str1 = "0";
		// String str2 = "0";
		//
		// if (null != oldDept && null != old) {
		// str = old.getDef1();
		// str1 = old.getDef2();
		// str2 = old.getDef3();
		// }

		String oldjobglbdef1 = (String) parentVO
				.getAttributeValue("oldjobglbdef1");
		String oldjobglbdef2 = (String) parentVO
				.getAttributeValue("oldjobglbdef2");

		String yname = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and  pk_dept ='" + oldjobglbdef1 + "'");
		Map yjbm = OaWorkFlowUtil.listAdd("yjbm", yname);
		list.add(yjbm);

		Map bm = OaWorkFlowUtil.listAdd("bm", oldDept.getCode());
		list.add(bm);

		String ename = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and  pk_dept ='" + oldjobglbdef2 + "'");
		Map ejbm = OaWorkFlowUtil.listAdd("ejbm", ename);
		list.add(ejbm);

		String postname = "0";
		if (null != parentVO.getOldpk_post()) {

			PostVO gwVO = (PostVO) getHyPubBO().queryByPrimaryKey(PostVO.class,
					parentVO.getOldpk_post());
			if (gwVO != null) {
				postname = gwVO.getPostname();
			}
		}
		Map gw = OaWorkFlowUtil.listAdd("gw", postname);
		list.add(gw);

		Map gwxl = OaWorkFlowUtil.listAdd(
				"gwxl",
				VOUtils.getDocName(PostSeriesVO.class,
						parentVO.getOldpk_postseries()));
		list.add(gwxl);

		Map zw = OaWorkFlowUtil.listAdd("zw",
				VOUtils.getDocName(JobVO.class, parentVO.getOldpk_job()));
		list.add(zw);

		Map zj = OaWorkFlowUtil.listAdd(
				"zj",
				VOUtils.getDocName(JobLevelVO.class,
						parentVO.getOldpk_jobgrade()));
		list.add(zj);

		Map zd = OaWorkFlowUtil.listAdd("zd", VOUtils.getDocName(
				JobRankVO.class, parentVO.getOldpk_jobrank()));
		list.add(zd);

		Map zwlb = OaWorkFlowUtil.listAdd("zwlb",
				VOUtils.getDocName(JobTypeVO.class, parentVO.getOldseries()));
		list.add(zwlb);

		Map rzfs = OaWorkFlowUtil.listAdd("rzfs",
				VOUtils.getDocName(DefdocVO.class, parentVO.getOldjobmode()));
		list.add(rzfs);

		Map mzfs = OaWorkFlowUtil
				.listAdd(
						"mzfs",
						VOUtils.getDocName(DefdocVO.class,
								parentVO.getOlddeposemode()));
		list.add(mzfs);

		if (parentVO.getOldpoststat() == null) {
			Map sfzg = OaWorkFlowUtil.listAdd("sfzg", "0");
			list.add(sfzg);
		} else {
			Map sfzg = OaWorkFlowUtil.listAdd("sfzg", parentVO.getOldpoststat()
					.booleanValue() ? "是" : "否");
			list.add(sfzg);
		}

		Map rzlx = OaWorkFlowUtil.listAdd("rzlx", VOUtils.getDocName(
				DefdocVO.class, parentVO.getOldpk_job_type()));
		list.add(rzlx);

		Map bz = OaWorkFlowUtil.listAdd("bz", parentVO.getOldmemo());
		list.add(bz);

		Map xzz = OaWorkFlowUtil.listAdd("xzz",
				VOUtils.getDocName(AdminOrgVO.class, parentVO.getNewpk_org()));
		list.add(xzz);

		HRDeptVO newDept = (HRDeptVO) getHyPubBO().queryByPrimaryKey(
				HRDeptVO.class, parentVO.getNewpk_dept());
		// DefdocVO newd = (DefdocVO) getHyPubBO().queryByPrimaryKey(
		// DefdocVO.class, newDept.getDeptlevel());
		// if (null != oldDept && null != old) {
		// str = newd.getDef1();
		// str1 = newd.getDef2();
		// str2 = newd.getDef3();
		// }
		String newjobglbdef1 = (String) parentVO
				.getAttributeValue("newjobglbdef1");
		String newjobglbdef2 = (String) parentVO
				.getAttributeValue("newjobglbdef2");

		String yname2 = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and  pk_dept ='" + newjobglbdef1 + "'");
		Map yjbm1 = OaWorkFlowUtil.listAdd("yjbm1", yname2);
		list.add(yjbm1);

		Map xbm = OaWorkFlowUtil.listAdd("xbm", newDept.getCode());
		list.add(xbm);

		String ename2 = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and  pk_dept ='" + newjobglbdef2 + "'");
		Map ejbm2 = OaWorkFlowUtil.listAdd("ejbm2", ename2);
		list.add(ejbm2);

		Map xgw = OaWorkFlowUtil.listAdd("xgw",
				VOUtils.getDocName(PostVO.class, parentVO.getNewpk_post()));
		list.add(xgw);

		Map xgwxl = OaWorkFlowUtil.listAdd(
				"xgwxl",
				VOUtils.getDocName(PostSeriesVO.class,
						parentVO.getNewpk_postseries()));
		list.add(xgwxl);

		Map xzw = OaWorkFlowUtil.listAdd("xzw",
				VOUtils.getDocName(JobVO.class, parentVO.getNewpk_job()));
		list.add(xzw);

		Map xzj = OaWorkFlowUtil.listAdd(
				"xzj",
				VOUtils.getDocName(JobLevelVO.class,
						parentVO.getNewpk_jobgrade()));
		list.add(xzj);

		Map xzd = OaWorkFlowUtil.listAdd("xzd", VOUtils.getDocName(
				JobRankVO.class, parentVO.getNewpk_jobrank()));
		list.add(xzd);

		Map xzwlb = OaWorkFlowUtil.listAdd("xzwlb",
				VOUtils.getDocName(JobTypeVO.class, parentVO.getNewseries()));
		list.add(xzwlb);

		Map xrzfs = OaWorkFlowUtil.listAdd("xrzfs",
				VOUtils.getDocName(DefdocVO.class, parentVO.getNewjobmode()));
		list.add(xrzfs);

		Map xmzfs = OaWorkFlowUtil
				.listAdd(
						"xmzfs",
						VOUtils.getDocName(DefdocVO.class,
								parentVO.getNewdeposemode()));
		list.add(xmzfs);

		if (parentVO.getNewpoststat() == null) {
			Map xsfzg = OaWorkFlowUtil.listAdd("xsfzg", "0");
			list.add(xsfzg);
		} else {
			Map xsfzg = OaWorkFlowUtil.listAdd("xsfzg", parentVO
					.getNewpoststat().booleanValue() ? "是" : "否");
			list.add(xsfzg);
		}

		Map xrzlx = OaWorkFlowUtil.listAdd("xrzlx", VOUtils.getDocName(
				DefdocVO.class, parentVO.getNewpk_job_type()));
		list.add(xrzlx);

		Map xbz = OaWorkFlowUtil.listAdd("xbz", parentVO.getNewmemo());
		list.add(xbz);

		Map yryxxzz = OaWorkFlowUtil.listAdd("yryxxzz",
				VOUtils.getDocName(HROrgVO.class, parentVO.getPk_old_hi_org()));
		list.add(yryxxzz);

		Map xryxxzz = OaWorkFlowUtil.listAdd("xryxxzz",
				VOUtils.getDocName(HROrgVO.class, parentVO.getPk_hi_org()));
		list.add(xryxxzz);

		Map yhtglzz = OaWorkFlowUtil.listAdd("yhtglzz", VOUtils.getDocName(
				HROrgVO.class, parentVO.getPk_old_hrcm_org()));
		list.add(yhtglzz);

		Map xhtglzz = OaWorkFlowUtil.listAdd("xhtglzz",
				VOUtils.getDocName(HROrgVO.class, parentVO.getPk_hrcm_org()));
		list.add(xhtglzz);

		if (parentVO.getIsrelease() == null) {
			Map jc = OaWorkFlowUtil.listAdd("jc", "0");
			list.add(jc);
		} else {
			Map jc = OaWorkFlowUtil.listAdd("jc", parentVO.getIsrelease()
					.booleanValue() ? "是" : "否");
			list.add(jc);
		}

		if (parentVO.getIsend() == null) {
			Map zz = OaWorkFlowUtil.listAdd("zz", "0");
			list.add(zz);
		} else {
			Map zz = OaWorkFlowUtil.listAdd("zz", parentVO.getIsend()
					.booleanValue() ? "是" : "否");
			list.add(zz);
		}

		if (parentVO.getIfendpart() == null) {
			Map jsjz = OaWorkFlowUtil.listAdd("jsjz", "0");
			list.add(jsjz);
		} else {
			Map jsjz = OaWorkFlowUtil.listAdd("jsjz", parentVO.getIfendpart()
					.booleanValue() ? "是" : "否");
			list.add(jsjz);
		}

		if (parentVO.getIfsynwork() == null) {
			Map tbgzll = OaWorkFlowUtil.listAdd("tbgzll", "0");
			list.add(tbgzll);
		} else {
			Map tbgzll = OaWorkFlowUtil.listAdd("tbgzll", parentVO
					.getIfsynwork().booleanValue() ? "是" : "否");
			list.add(tbgzll);
		}

		Map spr = OaWorkFlowUtil.listAdd("spr", parentVO.getApprover());
		list.add(spr);

		if (parentVO.getApprove_time() == null) {
			Map spsj = OaWorkFlowUtil.listAdd("spsj", "");
			list.add(spsj);
		} else {
			Map spsj = OaWorkFlowUtil.listAdd("spsj", parentVO
					.getApprove_time().getYear()
					+ "-"
					+ parentVO.getApprove_time().getStrMonth()
					+ "-"
					+ parentVO.getApprove_time().getStrDay());
			list.add(spsj);
		}

		Map cjr = OaWorkFlowUtil.listAdd("cjr", parentVO.getCreator());
		list.add(cjr);

		if (parentVO.getCreationtime() == null) {
			Map cjsj = OaWorkFlowUtil.listAdd("cjsj", "");
			list.add(cjsj);
		} else {
			Map cjsj = OaWorkFlowUtil.listAdd("cjsj", parentVO
					.getCreationtime().getYear()
					+ "-"
					+ parentVO.getCreationtime().getStrMonth()
					+ "-"
					+ parentVO.getCreationtime().getStrDay());
			list.add(cjsj);
		}

		Map zhxgr = OaWorkFlowUtil.listAdd("zhxgr", parentVO.getModifier());
		list.add(zhxgr);

		if (parentVO.getModifiedtime() == null) {
			Map zhxgsj = OaWorkFlowUtil.listAdd("zhxgsj", "");
			list.add(zhxgsj);
		} else {
			Map zhxgsj = OaWorkFlowUtil.listAdd("zhxgsj", parentVO
					.getModifiedtime().getYear()
					+ "-"
					+ parentVO.getModifiedtime().getStrMonth()
					+ "-"
					+ parentVO.getModifiedtime().getStrDay());
			list.add(zhxgsj);
		}

		Map szjt = OaWorkFlowUtil.listAdd("szjt",
				VOUtils.getDocName(GroupVO.class, parentVO.getPk_group()));
		list.add(szjt);

		Map dpsqzj = OaWorkFlowUtil.listAdd("dpsqzj",
				parentVO.getPk_hi_stapply());
		list.add(dpsqzj);

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		if (null != userVO) {

			Map zdrmc = OaWorkFlowUtil.listAdd("zdrmc", userVO.getUser_name());
			list.add(zdrmc);

			Map zdrzj = OaWorkFlowUtil.listAdd("zdrzj", userVO.getUser_code());
			list.add(zdrzj);
		}

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	public AggStapply[] doDelete(AggStapply[] vos) throws BusinessException {
		List<String> strBillIdList = new ArrayList();
		String billType = "";
		for (AggStapply vo : vos) {
			billType = (String) vo.getParentVO().getAttributeValue(
					"pk_billtype");
			String pk_group = (String) vo.getParentVO().getAttributeValue(
					"pk_group");
			String pk_org = (String) vo.getParentVO().getAttributeValue(
					"pk_org");
			String bill_code = (String) vo.getParentVO().getAttributeValue(
					"bill_code");
			strBillIdList.add((String) vo.getParentVO().getAttributeValue(
					"pk_hi_stapply"));
			if (isAutoGenerateBillCode(billType, pk_group, pk_org)) {
				getIBillcodeManage().returnBillCodeOnDelete(billType, pk_group,
						pk_org, bill_code, null);
			}
			deleteOldWorknote(vo);
			deleteBill(vo);
		}

		if (!strBillIdList.isEmpty()) {
			getIHrPf().deleteOldWorkflowNote(billType,
					(String[]) strBillIdList.toArray(new String[0]));
		}
		return vos;
	}

	public Object doPush(AggStapply vo) throws BusinessException {
		return vo;
	}

	public Object doSaveBills(AggStapply[] vos) throws BusinessException {
		try {
			if ((vos == null) || (vos.length == 0)) {
				return null;
			}
			String prefix = "ZD6115" + PubEnv.getServerDate().toStdString();

			String flowCode = SQLHelper.getFlowCode(prefix, "bill_code",
					StapplyVO.class);
			for (int i = 0; i < vos.length; i++) {
				StapplyVO billvo = (StapplyVO) vos[i].getParentVO();

				billvo.setPk_billtype("6115");

				billvo.setApprove_state(Integer.valueOf(-1));
				boolean isAutoGenerateBillCode = isAutoGenerateBillCode("6115",
						billvo.getPk_group(), billvo.getPk_org());

				if (isAutoGenerateBillCode) {
					billvo.setBill_code(getIBillcodeManage()
							.getPreBillCode_RequiresNew("6115",
									billvo.getPk_group(), billvo.getPk_org()));

				} else {
					billvo.setBill_code(prefix + "_" + getFlowCode(flowCode, i));
				}

				PsnJobVO psnJobVO = (PsnJobVO) queryByPk(PsnJobVO.class,
						billvo.getPk_psnjob(), true);
				billvo.setPk_psndoc(psnJobVO.getPk_psndoc());
				billvo.setPk_psnorg(psnJobVO.getPk_psnorg());
				billvo.setAssgid(psnJobVO.getAssgid());

				for (String attr : psnJobVO.getAttributeNames()) {
					billvo.setAttributeValue("old" + attr,
							psnJobVO.getAttributeValue(attr));
				}

				billvo.setPk_group(PubEnv.getPk_group());
				billvo.setAttributeValue("newpk_org", psnJobVO.getPk_org());
				billvo.setAttributeValue("newpk_dept", psnJobVO.getPk_dept());
				billvo.setAttributeValue("newpk_psncl", psnJobVO.getPk_psncl());

				if (billvo.getPk_group() == null) {
					billvo.setPk_group(PubEnv.getPk_group());
				}

				if (billvo.getPk_org() == null) {
					billvo.setPk_org(psnJobVO.getPk_hrorg());
				}

				if (billvo.getPk_old_hi_org() == null) {
					billvo.setPk_old_hi_org(psnJobVO.getPk_hrorg());
				}

				if (billvo.getPk_hi_org() == null) {
					billvo.setPk_hi_org(psnJobVO.getPk_hrorg());
				}

				billvo.setStapply_mode(Integer.valueOf(1));
				billvo.setIshrssbill(UFBoolean.FALSE);
				billvo.setFun_code("60090dimissionapply");
				billvo.setPk_trnstype("1002Z710000000008GSX");

				if (StringUtils.isBlank(billvo.getCreator()))
					billvo.setCreator("NC_USER0000000000000");
				if (StringUtils.isBlank(billvo.getBillmaker()))
					billvo.setBillmaker("NC_USER0000000000000");
				if (billvo.getCreationtime() == null)
					billvo.setCreationtime(PubEnv.getServerTime());
				if (billvo.getApply_date() == null)
					billvo.setApply_date(PubEnv.getServerLiteralDate());
				insertBill(vos[i]);
			}
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
			throw new BusinessException(ResHelper.getString("6009tran",
					"06009tran0154"));
		}

		return vos;
	}

	public Object doUnapprove(AggregatedValueObject[] vos)
			throws BusinessException {
		List<String> stmngPKList = new ArrayList();
		for (int i = 0; i < vos.length; i++) {
			stmngPKList.add(((StapplyVO) vos[i].getParentVO())
					.getPk_hi_stapply());
		}
		if (!stmngPKList.isEmpty()) {
			InSQLCreator isc = new InSQLCreator();
			String insql = isc.getInSQL((String[]) stmngPKList
					.toArray(new String[0]));
			String strCondition = "pk_hi_stapply in (" + insql + ")";
			StapplyVO[] stApplyVOs = (StapplyVO[]) ((IPersistenceRetrieve) NCLocator
					.getInstance().lookup(IPersistenceRetrieve.class))
					.retrieveByClause(null, StapplyVO.class, strCondition);

			for (int i = 0; i < stApplyVOs.length; i++) {
				int approvestate = stApplyVOs[i].getApprove_state().intValue();

				checkPFPassingState(approvestate);

				if (approvestate == 102) {
					throw new BusinessException(ResHelper.getString("6009tran",
							"06009tran0209"));
				}
			}
		}
		for (int i = 0; i < vos.length; i++) {
			vos[i] = updateBill(vos[i], false);
		}
		return vos;
	}

	public void checkPFPassingState(int pfsate) throws BusinessException {
		if (0 == pfsate) {
			throw new BusinessException(ResHelper.getString("6007entry",
					"16007entry0014"));
		}
	}

	private WorkflownoteVO[] getAllWorkflownoteVO(IFlowBizItf itf)
			throws BusinessException {
		String transtype = StringUtils.isBlank(itf.getTranstype()) ? itf
				.getBilltype() : itf.getTranstype();
		return getIPFWorkflowQry().queryWorkitems(itf.getBillId(), transtype,
				WorkflowTypeEnum.Approveflow.getIntValue(), 0);
	}

	public String getBillIdSql(int iBillStatus, String billType)
			throws BusinessException {
		String pks = getIHrPf().getBillIdSql(iBillStatus, billType);
		String strWorkFlowWhere = "pk_hi_stapply in ("
				+ (StringUtils.isBlank(pks) ? " '1<>1' " : pks) + ") ";
		return strWorkFlowWhere;
	}

	private String getFlowCode(String code, int i) throws BusinessException {
		Integer value = Integer.valueOf(code);
		return StringUtils.leftPad(value.intValue() + i + "", 5, '0');
	}

	private IBillcodeManage getIBillcodeManage() {
		return (IBillcodeManage) NCLocator.getInstance().lookup(
				IBillcodeManage.class);
	}

	private IHrBillCode getIHrBillCode() {
		return (IHrBillCode) NCLocator.getInstance().lookup(IHrBillCode.class);
	}

	private IHrPf getIHrPf() {
		return (IHrPf) NCLocator.getInstance().lookup(IHrPf.class);
	}

	private IPersistenceUpdate getIPersistenceUpdate() {
		return (IPersistenceUpdate) NCLocator.getInstance().lookup(
				IPersistenceUpdate.class);
	}

	private IPFWorkflowQry getIPFWorkflowQry() {
		return (IPFWorkflowQry) NCLocator.getInstance().lookup(
				IPFWorkflowQry.class);
	}

	private WorkflownoteVO getLastNote(WorkflownoteVO[] workflownoteVOs) {
		for (int i = workflownoteVOs.length - 1; i >= 0; i--) {
			if (!"X".equals(workflownoteVOs[i].getIscheck())) {

				return workflownoteVOs[i];
			}
		}
		return null;
	}

	private String getMsg(StapplyVO billvo) throws BusinessException {
		SuperVO[] itemvos = TrnDelegator.getIItemSetQueryService()
				.queryItemSetByOrg("8ce2d67d-d671-419c-9d80-ab4f135be3b8",
						billvo.getPk_group(), billvo.getPk_org(),
						billvo.getPk_trnstype());

		IBean ibean = BeanUtil
				.getBeanEntity("8ce2d67d-d671-419c-9d80-ab4f135be3b8");
		List<IItemSetAdapter> iitemadpls = BeanUtil.getBizImpObjFromVo(ibean,
				IItemSetAdapter.class, itemvos);
		for (IItemSetAdapter item : iitemadpls) {
			if ((item != null) && (!item.getItemkey().startsWith("old"))) {

				if ((item.getIsnotnull().booleanValue())
						&& (isNull(billvo.getAttributeValue(item.getItemkey())))) {
					return '\n' + billvo.getBill_code();
				}
			}
		}
		return "";
	}

	private int getValidNoteCount(IFlowBizItf itf) throws BusinessException {
		WorkflownoteVO[] all = getAllWorkflownoteVO(itf);
		if ((all == null) || (all.length == 0)) {
			return 0;
		}
		int count = 0;
		for (WorkflownoteVO vo : all) {
			if (!"X".equals(vo.getIscheck())) {

				count++;
			}
		}
		return count;
	}

	private <T extends AggregatedValueObject> void checkBillCodeRepeat(
			T... billvos) throws BusinessException {
		StringBuffer errMsg = new StringBuffer();
		ArrayList<String> repeatCodes = new ArrayList();
		for (T vo : billvos) {
			IFlowBizItf itf = (IFlowBizItf) NCObject.newInstance(vo)
					.getBizInterface(IFlowBizItf.class);
			String billCode = itf.getBillNo();
			String pk_entryapply = itf.getBillId();
			String billType = itf.getBilltype();
			String whereSql = "bill_code = '" + NCESAPI.sqlEncode(billCode)
					+ "' and pk_group = '" + PubEnv.getPk_group() + "'  and "
					+ "pk_billtype" + " = '" + billType + "'";

			if (!StringUtils.isBlank(pk_entryapply)) {
				whereSql = whereSql + " and pk_hi_stapply <> '" + pk_entryapply
						+ "'";
			}
			int count = ((IPersistenceRetrieve) NCLocator.getInstance().lookup(
					IPersistenceRetrieve.class)).getCountByCondition(
					StapplyVO.getDefaultTableName(), whereSql);

			if (count > 0) {
				errMsg.append('\n'
						+ ResHelper.getString("6007entry", "06007entry0050")
						+ billCode
						+ ResHelper.getString("6007entry", "06007entry0051"));

				repeatCodes.add(billCode);
			}
		}

		if (errMsg.length() > 0) {
			BillCodeRepeatBusinessException ex = new BillCodeRepeatBusinessException(
					ResHelper.getString("6007entry", "06007entry0052") + errMsg
							+ "");

			ex.setRepeatCodes((String[]) repeatCodes.toArray(new String[0]));
			throw ex;
		}
	}

	private BlacklistVO getBlackListVO(CertVO certVO, PsndocVO psndocVO,
			String pk_org, String pk_group) {
		BlacklistVO blacklistVO = new BlacklistVO();
		blacklistVO.setId(certVO.getId());
		blacklistVO.setIdtype(certVO.getIdtype());
		blacklistVO.setPsnname(psndocVO.getName());
		blacklistVO.setPsnname2(psndocVO.getName2());
		blacklistVO.setPsnname3(psndocVO.getName3());
		blacklistVO.setPsnname4(psndocVO.getName4());
		blacklistVO.setPsnname5(psndocVO.getName5());
		blacklistVO.setPsnname6(psndocVO.getName6());
		blacklistVO.setPk_org(pk_org);
		blacklistVO.setPk_group(pk_group);
		return blacklistVO;
	}

	private void validateBlack(StapplyVO head) throws BusinessException {
		String pk_psndoc = head.getPk_psndoc();
		String pk_org = head.getNewpk_org();
		String pk_group = head.getPk_group();
		String condition = " pk_psndoc = '" + pk_psndoc + "'";
		CertVO[] certVOs = (CertVO[]) ((IPersistenceRetrieve) NCLocator
				.getInstance().lookup(IPersistenceRetrieve.class))
				.retrieveByClause(null, CertVO.class, condition);

		PsndocVO[] psndocVOs = (PsndocVO[]) ((IPersistenceRetrieve) NCLocator
				.getInstance().lookup(IPersistenceRetrieve.class))
				.retrieveByClause(null, PsndocVO.class, condition);

		if ((ArrayUtils.isEmpty(certVOs)) || (ArrayUtils.isEmpty(psndocVOs))) {
			return;
		}
		BlacklistVO blacklistVO = getBlackListVO(certVOs[0], psndocVOs[0],
				pk_org, pk_group);
		boolean blInBlacklist = ((IBlacklistManageService) NCLocator
				.getInstance().lookup(IBlacklistManageService.class))
				.isInBlacklist(blacklistVO);
		if (blInBlacklist) {
			throw new BusinessException(ResHelper.getString("6007psn",
					"06007psn0235"));
		}
	}

	public <T extends AggregatedValueObject> T updateBill(T billvo,
			boolean blChangeAuditInfo) throws BusinessException {
		T agg = null;
		StapplyVO head = (StapplyVO) billvo.getParentVO();

		validateBlack(head);

		try {
			checkBillCodeRepeat(new AggregatedValueObject[] { billvo });

			agg = super.updateBill(billvo, blChangeAuditInfo);

		} catch (Exception e) {

			if ((e instanceof BillCodeRepeatBusinessException)) {
				String[] codes = ((BillCodeRepeatBusinessException) e)
						.getRepeatCodes();
				if ((isAutoGenerateBillCode(head.getPk_billtype(),
						PubEnv.getPk_group(), head.getPk_org()))
						&& (codes != null)) {
					for (int i = 0; i < codes.length; i++) {
						try {
							((IBillcodeManage) NCLocator.getInstance().lookup(
									IBillcodeManage.class))
									.AbandonBillCode_RequiresNew(
											head.getPk_billtype(),
											PubEnv.getPk_group(),
											head.getPk_org(), codes[i]);

						} catch (Exception e2) {
							Logger.error(e2.getMessage(), e2);
						}
					}
				}
				throw ((BillCodeRepeatBusinessException) e);
			}
			throw new BusinessException(e.getMessage());
		}
		return agg;
	}

	public <T extends AggregatedValueObject> T insertBill(T billvo)
			throws BusinessException {
		T agg = null;
		StapplyVO head = (StapplyVO) billvo.getParentVO();

		validateBlack(head);

		try {
			checkBillCodeRepeat(new AggregatedValueObject[] { billvo });

			agg = super.insertBill(billvo);

			if (isAutoGenerateBillCode(head.getPk_billtype(),
					head.getPk_group(), head.getPk_org())) {
				getIHrBillCode().commitPreBillCode(head.getPk_billtype(),
						head.getPk_group(), head.getPk_org(),
						head.getBill_code());
			}

		} catch (Exception e) {
			if ((e instanceof BillCodeRepeatBusinessException)) {
				String[] codes = ((BillCodeRepeatBusinessException) e)
						.getRepeatCodes();
				if ((isAutoGenerateBillCode(head.getPk_billtype(),
						PubEnv.getPk_group(), head.getPk_org()))
						&& (codes != null)) {
					for (int i = 0; i < codes.length; i++) {
						try {
							((IBillcodeManage) NCLocator.getInstance().lookup(
									IBillcodeManage.class))
									.AbandonBillCode_RequiresNew(
											head.getPk_billtype(),
											PubEnv.getPk_group(),
											head.getPk_org(), codes[i]);

						} catch (Exception e2) {
							Logger.error(e2.getMessage(), e2);
						}
					}
				}
				throw ((BillCodeRepeatBusinessException) e);
			}

			if (isAutoGenerateBillCode(head.getPk_billtype(),
					PubEnv.getPk_group(), head.getPk_org())) {
				((IHrBillCode) NCLocator.getInstance()
						.lookup(IHrBillCode.class)).rollbackPreBillCode(
						head.getPk_billtype(), PubEnv.getPk_group(),
						head.getPk_org(), head.getBill_code());
			}

			throw new BusinessException(e.getMessage());
		}
		return agg;
	}

	private void invalidWorkflowNote(IFlowBizItf itf, boolean isDealAll)
			throws BusinessException {
		WorkflownoteVO[] vos = getAllWorkflownoteVO(itf);
		if (isDealAll) {
			for (WorkflownoteVO vo : vos) {
				vo.setIscheck("X");
				vo.setApprovestatus(Integer.valueOf(4));
			}
			getIPersistenceUpdate().updateVOArray(null, vos,
					new String[] { "ischeck", "approvestatus" }, null);
		} else {
			if ((vos == null) || (vos.length == 0)) {
				return;
			}
			WorkflownoteVO last = getLastNote(vos);
			last.setIscheck("X");
			last.setApprovestatus(Integer.valueOf(4));
			last.setMessagenote(ResHelper
					.getString("6009tran", "06009tran0155"));
			getIPersistenceUpdate().updateVO(null, last,
					new String[] { "ischeck", "approvestatus", "messagenote" },
					null);
		}
	}

	private boolean isAutoGenerateBillCode(String billType, String pk_group,
			String pk_org) throws BusinessException {
		BillCodeContext billCodeContext = HiCacheUtils.getBillCodeContext(
				billType, pk_group, pk_org);
		return billCodeContext != null;
	}

	private boolean isCheckman(IFlowBizItf itf) throws BusinessException {
		String strBillType = StringUtils.isBlank(itf.getTranstype()) ? itf
				.getBilltype() : itf.getTranstype();

		return getIPFWorkflowQry().isCheckman(itf.getBillId(), strBillType,
				PubEnv.getPk_user());
	}

	private boolean isDirectApprove(IFlowBizItf itf) throws BusinessException {
		String strBillType = StringUtils.isBlank(itf.getTranstype()) ? itf
				.getBilltype() : itf.getTranstype();
		return !getIPFWorkflowQry().isApproveFlowStartup(itf.getBillId(),
				strBillType);
	}

	private boolean isNoApprove(IFlowBizItf itf) throws BusinessException {
		WorkflownoteVO[] vo = getAllWorkflownoteVO(itf);
		return (vo == null) || (vo.length == 0);
	}

	private boolean isNull(Object o) {
		if ((o == null) || (o + "" == "") || ((o + "").trim().equals(""))) {
			return true;
		}
		return false;
	}

	public AggStapply[] queryByCondition(LoginContext context, String condition)
			throws BusinessException {
		return (AggStapply[]) queryByCondition(AggStapply.class, condition);
	}

	public AggStapply queryByPk(String pk) throws BusinessException {
		return (AggStapply) queryByPk(AggStapply.class, pk);
	}

	private boolean checkDataPermission(String operateCode,
			String mdOperateCode, String resourceCode,
			AggregatedValueObject aggVO) throws BusinessException {
		if (((StringUtils.isBlank(operateCode)) && (StringUtils
				.isBlank(mdOperateCode)))
				|| (StringUtils.isBlank(resourceCode))) {
			return true;
		}

		boolean blHasDataPermission = true;

		String resDataId = aggVO.getParentVO().getPrimaryKey();
		if (!StringUtils.isBlank(mdOperateCode)) {
			blHasDataPermission = DataPermissionUtils
					.isUserhasPermissionByMetaDataOperation(resourceCode,
							resDataId, mdOperateCode);
		} else {
			blHasDataPermission = DataPermissionUtils.isUserhasPermission(
					resourceCode, resDataId, operateCode);
		}

		return blHasDataPermission;
	}

	public PfProcessBatchRetObject transApproveValidation(AggStapply[] aggvos,
			LoginContext context, String operateCode, String mdOperateCode,
			String resourceCode) throws BusinessException {
		((IHrPf) NCLocator.getInstance().lookup(IHrPf.class))
				.validateApproveType(aggvos);

		PFBatchExceptionInfo errInfo = new PFBatchExceptionInfo();
		ArrayList<AggStapply> al = new ArrayList();
		IFlowBizItf itf = null;
		for (int i = 0; i < aggvos.length; i++) {

			AggStapply dbvo = (AggStapply) queryByPk(AggStapply.class,
					aggvos[i].getParentVO().getPrimaryKey(), true);

			if (dbvo == null) {
				errInfo.putErrorMessage(i, aggvos[i],
						ResHelper.getString("6009tran", "06009tran0156"));

			} else if (!checkDataPermission(operateCode, mdOperateCode,
					resourceCode, dbvo)) {
				errInfo.putErrorMessage(i, aggvos[i],
						ResHelper.getString("6009tran", "06009tran0157"));

			} else {

				StapplyVO head = (StapplyVO) dbvo.getParentVO();
				if (!ArrayUtils.contains(new int[] { 3, 2 }, head
						.getApprove_state().intValue())) {
					errInfo.putErrorMessage(i, aggvos[i],
							ResHelper.getString("6009tran", "06009tran0158"));

				} else {

					itf = (IFlowBizItf) NCObject.newInstance(dbvo)
							.getBizInterface(IFlowBizItf.class);

					boolean isDirectApp = isDirectApprove(itf);

					if ((!isDirectApp) && (!isCheckman(itf))) {
						errInfo.putErrorMessage(i, aggvos[i], ResHelper
								.getString("6009tran", "06009tran0159"));

					} else {

						if (isDirectApp) {
							if (3 == head.getStapply_mode().intValue()) {

								if ((3 == head.getApprove_state().intValue())
										&& (!head.getPk_hi_org().equals(
												context.getPk_org()))) {

									errInfo.putErrorMessage(i, aggvos[i],
											ResHelper.getString("6009tran",
													"06009tran0159"));

									continue;
								}
								if ((2 == head.getApprove_state().intValue())
										&& (!head.getPk_old_hi_org().equals(
												context.getPk_org()))) {

									errInfo.putErrorMessage(i, aggvos[i],
											ResHelper.getString("6009tran",
													"06009tran0159"));

									continue;
								}
							}
							if (2 == head.getStapply_mode().intValue()) {

								if ((3 == head.getApprove_state().intValue())
										&& (!head.getPk_old_hi_org().equals(
												context.getPk_org()))) {

									errInfo.putErrorMessage(i, aggvos[i],
											ResHelper.getString("6009tran",
													"06009tran0159"));

									continue;
								}
								if ((2 == head.getApprove_state().intValue())
										&& (!head.getPk_hi_org().equals(
												context.getPk_org()))) {

									errInfo.putErrorMessage(i, aggvos[i],
											ResHelper.getString("6009tran",
													"06009tran0159"));

									continue;
								}
							}
						}
						al.add(aggvos[i]);
					}
				}
			}
		}
		return new PfProcessBatchRetObject(al.toArray(new AggStapply[0]),
				errInfo);
	}

	public ValidationFailure transCallBackValidation(AggStapply[] aggvos)
			throws BusinessException {
		String errName = "";
		for (AggStapply agg : aggvos) {
			NCObject ncObj = NCObject.newInstance(agg);
			IFlowBizItf itf = (IFlowBizItf) ncObj
					.getBizInterface(IFlowBizItf.class);
			String pk = itf.getBillId();

			AggregatedValueObject dbVO = (AggregatedValueObject) queryByPk(
					agg.getClass(), pk, true);
			NCObject dbObj = NCObject.newInstance(dbVO);
			IFlowBizItf dbitf = (IFlowBizItf) dbObj
					.getBizInterface(IFlowBizItf.class);
			int iApproveStatus = dbitf.getApproveStatus().intValue();

			if (3 != iApproveStatus) {
				if ((1 != iApproveStatus) ||

				(!isNoApprove(dbitf))) {

					errName = errName + '\n' + itf.getBillNo();
				}
			}
		}
		if (!StringUtils.isBlank(errName)) {
			return new ValidationFailure(ResHelper.getString("6009tran",
					"06009tran0160") + errName);
		}

		return null;
	}

	public PfProcessBatchRetObject transUnApproveValidation(
			AggStapply[] aggvos, String operateCode, String mdOperateCode,
			String resourceCode) throws BusinessException {
		PFBatchExceptionInfo errInfo = new PFBatchExceptionInfo();
		ArrayList<AggStapply> al = new ArrayList();
		IFlowBizItf itf = null;
		for (int i = 0; i < aggvos.length; i++) {

			AggStapply dbvo = (AggStapply) queryByPk(AggStapply.class,
					aggvos[i].getParentVO().getPrimaryKey(), true);

			if (dbvo == null) {
				errInfo.putErrorMessage(i, aggvos[i],
						ResHelper.getString("6009tran", "06009tran0156"));

			} else if (!checkDataPermission(operateCode, mdOperateCode,
					resourceCode, dbvo)) {
				errInfo.putErrorMessage(i, aggvos[i],
						ResHelper.getString("6009tran", "06009tran0161"));

			} else {

				StapplyVO head = (StapplyVO) dbvo.getParentVO();

				int iApproveState = head.getApprove_state().intValue();
				if (ArrayUtils.contains(new Integer[] { Integer.valueOf(-1),
						Integer.valueOf(3) }, Integer.valueOf(iApproveState))) {
					errInfo.putErrorMessage(i, aggvos[i],
							ResHelper.getString("6009tran", "06009tran0162"));

				} else if (ArrayUtils.contains(
						new Integer[] { Integer.valueOf(0) },
						Integer.valueOf(iApproveState))) {
					errInfo.putErrorMessage(i, aggvos[i],
							ResHelper.getString("6009tran", "06009tran0163"));

				} else if (ArrayUtils.contains(
						new Integer[] { Integer.valueOf(101),
								Integer.valueOf(102) },
						Integer.valueOf(iApproveState))) {
					errInfo.putErrorMessage(i, aggvos[i],
							ResHelper.getString("6009tran", "06009tran0164"));

				} else {

					itf = (IFlowBizItf) NCObject.newInstance(dbvo)
							.getBizInterface(IFlowBizItf.class);

					if (isDirectApprove(itf)) {
						WorkflownoteVO[] workflownoteVOs = getAllWorkflownoteVO(itf);
						if ((workflownoteVOs == null)
								|| (workflownoteVOs.length == 0)) {
							errInfo.putErrorMessage(i, aggvos[i], ResHelper
									.getString("6001pf", "06001pf0058"));

							continue;
						}

						WorkflownoteVO lastVO = getLastNote(workflownoteVOs);
						if ((lastVO != null)
								&& (!PubEnv.getPk_user().equals(
										lastVO.getCheckman()))) {
							errInfo.putErrorMessage(i, aggvos[i], ResHelper
									.getString("6009tran", "06009tran0165"));

							continue;
						}
					}
					al.add(aggvos[i]);
				}
			}
		}
		return new PfProcessBatchRetObject(al.toArray(new AggStapply[0]),
				errInfo);
	}

	private void validate(AggStapply[] vos) throws BusinessException {
		String errMsg = "";
		for (AggStapply vo : vos) {
			StapplyVO billvo = (StapplyVO) vo.getParentVO();
			if (1 == billvo.getApprove_state().intValue()) {
				errMsg = errMsg + getMsg(billvo);
			}
		}
		if (!StringUtils.isBlank(errMsg)) {
			throw new BusinessException(ResHelper.getString("6009tran",
					"06009tran0153"));
		}
	}

	private void writeTransApproveInfo(StapplyVO headVO, String strApproveId,
			UFDateTime strApproveDate, String strCheckNote, Integer intAppState)
			throws BusinessException {
		if (headVO == null) {
			return;
		}
		headVO.setAttributeValue("approver", strApproveId);
		headVO.setAttributeValue("approve_time", strApproveDate);
		headVO.setAttributeValue("approve_note", strCheckNote);
		headVO.setAttributeValue("approve_state", intAppState);
	}

	private void writeWorknote(StapplyVO headVO, String strApproveId,
			String strCheckNote, int blPassed, String billtype)
			throws BusinessException {
		WorkflownoteVO worknoteVO = new WorkflownoteVO();
		worknoteVO.setBillid(headVO.getPrimaryKey());
		worknoteVO.setBillVersionPK(headVO.getPrimaryKey());
		worknoteVO.setChecknote(strCheckNote);

		worknoteVO.setSenddate(PubEnv.getServerTime());
		worknoteVO.setDealdate(PubEnv.getServerTime());

		worknoteVO.setPk_org(headVO.getPk_org());

		worknoteVO.setBillno(headVO.getBill_code());

		String sendman = headVO.getApprover() == null ? headVO.getBillmaker()
				: headVO.getApprover();
		worknoteVO.setSenderman(sendman);

		worknoteVO.setApproveresult(1 == blPassed ? "Y" : -1 == blPassed ? "R"
				: "N");
		worknoteVO.setApprovestatus(Integer.valueOf(1));
		worknoteVO.setIscheck(0 == blPassed ? "N" : 1 == blPassed ? "Y" : "X");
		worknoteVO.setActiontype("APPROVE");
		worknoteVO.setCheckman(strApproveId);

		worknoteVO.setPk_billtype(billtype);
		worknoteVO.setWorkflow_type(Integer
				.valueOf(WorkflowTypeEnum.Approveflow.getIntValue()));
		getIPersistenceUpdate().insertVO(null, worknoteVO, null);
	}

	public HashMap<String, Object> execBills(AggStapply[] bills,
			LoginContext context, boolean isRunBackgroundTask)
			throws BusinessException {
		HashMap<String, Object> result = new HashMap();
		StringBuffer sb = new StringBuffer();
		ArrayList<AggStapply> alTrans = new ArrayList();
		ArrayList<AggStapply> alDimission = new ArrayList();
		for (int i = 0; (bills != null) && (i < bills.length); i++) {
			if ("6113".equals(bills[i].getParentVO().getAttributeValue(
					"pk_billtype"))) {
				alTrans.add(bills[i]);
			} else {
				alDimission.add(bills[i]);
			}
		}
		AggregatedValueObject[] retVOs = null;
		if (alTrans.size() > 0) {
			retVOs = ((IRegmngQueryService) NCLocator.getInstance().lookup(
					IRegmngQueryService.class)).validateBudget(
					(AggregatedValueObject[]) alTrans
							.toArray(new AggStapply[0]), context);
		}

		for (int i = 0; i < alTrans.size(); i++) {
			if (!isExit(retVOs, (AggStapply) alTrans.get(i))) {

				sb.append(ResHelper
						.getString("6009tran", "06009tran0166",
								new String[] { (String) ((AggStapply) alTrans
										.get(i)).getParentVO()
										.getAttributeValue("bill_code") }));
			}
		}

		if (((retVOs == null) || (retVOs.length == 0))
				&& (alDimission.size() == 0)) {

			String msg = sb.length() == 0 ? "" : sb + "";
			result.put("RESULT_MSG",
					isRunBackgroundTask ? msg : msg.replaceAll("<br>", "\n"));
			result.put("RESULT_BILLS", null);
			return result;
		}

		ArrayList<AggStapply> transBill = new ArrayList();
		ArrayList<AggStapply> dimisBill = new ArrayList();
		AggregatedValueObject[] aggs = (AggregatedValueObject[]) ArrayUtils
				.addAll(retVOs, alDimission.toArray(new AggStapply[0]));
		for (int i = 0; i < aggs.length; i++) {
			String billtype = (String) aggs[i].getParentVO().getAttributeValue(
					"pk_billtype");

			try {
				if ("6113".equals(billtype)) {

					Object obj = getRdsService().perfromStaff_RequiresNew(
							(AggStapply) aggs[i], true);
					if (null == obj) {
						continue;
					}

					if ((obj instanceof String)) {
						transBill.add(queryByPk(aggs[i].getParentVO()
								.getPrimaryKey()));
						continue;
					}
					transBill.add(queryByPk(aggs[i].getParentVO()
							.getPrimaryKey()));

				} else {
					Object obj = getRdsService().perfromTurnOver_RequiresNew(
							(AggStapply) aggs[i], true);
					if (null == obj) {
						continue;
					}

					if ((obj instanceof String)) {
						dimisBill.add(queryByPk(aggs[i].getParentVO()
								.getPrimaryKey()));
						continue;
					}
					dimisBill.add(queryByPk(aggs[i].getParentVO()
							.getPrimaryKey()));
				}

			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
				String billcode = (String) aggs[i].getParentVO()
						.getAttributeValue("bill_code");
				if (StringUtils.isBlank(e.getMessage())) {

					sb.append(i
							+ 1
							+ ":"
							+ ResHelper.getString("6009tran", "06009tran0167",
									new String[] { billcode, e.getMessage() }));

				} else if (e.getMessage().indexOf(billcode) < 0) {

					sb.append(i
							+ 1
							+ ":"
							+ ResHelper.getString("6009tran", "06009tran0167",
									new String[] { billcode, e.getMessage() }));

				} else {

					sb.append(i + 1 + ":" + e.getMessage());
				}

				continue;
			}
			try {
				AggStapply agg = queryByPk(aggs[i].getParentVO()
						.getPrimaryKey());
				getRdsService().pushWorkflow_RequiresNew(billtype, agg);
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}
		}

		HashMap<String, ArrayList<AggStapply>> hmTrans = new HashMap();
		for (AggStapply bill : transBill) {
			String pk_org = (String) bill.getParentVO().getAttributeValue(
					"pk_hi_org");
			if (hmTrans.get(pk_org) == null) {
				hmTrans.put(pk_org, new ArrayList());
			}
			((ArrayList) hmTrans.get(pk_org)).add(bill);
		}
		for (String key : hmTrans.keySet()) {
			if ((hmTrans.get(key) != null)
					&& (((ArrayList) hmTrans.get(key)).size() > 0)) {

				String tempCode = "600705";
				HiSendMsgHelper.sendMessage1(tempCode,
						(HYBillVO[]) ((ArrayList) hmTrans.get(key))
								.toArray(new AggStapply[0]), key);
			}
		}

		HashMap<String, ArrayList<AggStapply>> hmDimis = new HashMap();
		for (AggStapply bill : dimisBill) {
			String pk_org = (String) bill.getParentVO().getAttributeValue(
					"pk_hi_org");
			if (hmDimis.get(pk_org) == null) {
				hmDimis.put(pk_org, new ArrayList());
			}
			((ArrayList) hmDimis.get(pk_org)).add(bill);
		}
		for (String key : hmDimis.keySet()) {
			if ((hmDimis.get(key) != null)
					&& (((ArrayList) hmDimis.get(key)).size() > 0)) {

				String tempCode = "600707";
				HiSendMsgHelper.sendMessage1(tempCode,
						(HYBillVO[]) ((ArrayList) hmDimis.get(key))
								.toArray(new AggStapply[0]), key);
			}
		}
		String msg = sb.length() == 0 ? "" : sb + "";
		result.put("RESULT_MSG",
				isRunBackgroundTask ? msg : msg.replaceAll("<br>", "\n"));
		transBill.addAll(dimisBill);
		result.put("RESULT_BILLS", transBill);
		return result;
	}

	private boolean isExit(AggregatedValueObject[] retVOs, AggStapply aggStapply)
			throws BusinessException {
		for (int i = 0; (retVOs != null) && (i < retVOs.length); i++) {
			if (aggStapply.getParentVO().getPrimaryKey()
					.equals(retVOs[i].getParentVO().getPrimaryKey())) {
				return true;
			}
		}
		return false;
	}

	private IRdsManageService getRdsService() {
		return (IRdsManageService) NCLocator.getInstance().lookup(
				IRdsManageService.class);
	}

	public HashMap validateApproveType(AggregatedValueObject[] aggvos)
			throws BusinessException {
		HashMap<Integer, ArrayList<AggregatedValueObject>> map = new HashMap();
		ArrayList<AggregatedValueObject> directBills = new ArrayList();
		ArrayList<AggregatedValueObject> approveBills = new ArrayList();
		map.put(Integer.valueOf(0), directBills);
		map.put(Integer.valueOf(1), approveBills);
		for (int i = 0; (aggvos != null) && (i < aggvos.length); i++) {
			IFlowBizItf itf = (IFlowBizItf) NCObject.newInstance(aggvos[i])
					.getBizInterface(IFlowBizItf.class);
			if (isDirectApprove(itf)) {
				((ArrayList) map.get(Integer.valueOf(0))).add(aggvos[i]);
			} else {
				((ArrayList) map.get(Integer.valueOf(1))).add(aggvos[i]);
			}
		}
		return map;
	}

	public PfProcessBatchRetObject dimissionApproveValidation(
			AggStapply[] aggvos, LoginContext context, String operateCode,
			String mdOperateCode, String resourceCode) throws BusinessException {
		((IHrPf) NCLocator.getInstance().lookup(IHrPf.class))
				.validateApproveType(aggvos);
		return getIHrPf().approveValidation(operateCode, mdOperateCode,
				resourceCode, aggvos);
	}

	public AggregatedValueObject[] queryBillData(LoginContext loginContext,
			String billType, String strWhere, PFQueryParams queryParams,
			String strOrderBySQL) throws BusinessException {
		if (queryParams == null) {
			throw new BusinessException(ResHelper.getString("6009tran",
					"06009tran0168"));
		}

		if (StringUtils.isBlank(strWhere)) {
			strWhere = " 1=1";
		}

		strWhere = strWhere + " and pk_billtype = '" + billType + "' ";

		String strNormalSQL = getQueryCondition(billType,
				queryParams.isApproveSite(), loginContext.getPk_org(),
				queryParams.getBillState());

		if (StringUtils.isBlank(strNormalSQL)) {
			strNormalSQL = " 1=1";
		}

		IFlowBizItf itf = HrPfHelper.getFlowBizItf(AggStapply.class);

		String strApproveDatePeriod = HrPfHelper.getApproveDatePeriod(itf,
				null, queryParams.getApproveDateParam(),
				queryParams.getBillState());

		String strSQL = strWhere + " and " + strNormalSQL;

		if (!StringUtils.isBlank(strApproveDatePeriod)) {
			strSQL = strSQL + " and " + strApproveDatePeriod;
		}

		if (!StringUtils.isBlank(strOrderBySQL)) {

			strSQL = strSQL + " and pk_org = '" + loginContext.getPk_org()
					+ "' " + "order by " + strOrderBySQL;
		}

		return (AggregatedValueObject[]) queryByCondition(AggStapply.class,
				strSQL);
	}

	private String getQueryCondition(String billType, boolean blApproveSite,
			String pk_org, int iBillStatus) throws BusinessException {
		String strQueryCondition = "";

		if (!blApproveSite) {
			strQueryCondition = MessageFormat.format(
					" {0} in ( ''{1}'',''{2}'') and {3}=''{4}'' ",
					new Object[] { "billmaker", PubEnv.getPk_user(),
							"NC_USER0000000000000", "pk_org", pk_org });

			if (100000 != iBillStatus) {
				strQueryCondition = strQueryCondition
						+ MessageFormat
								.format(" and {0}={1}",
										new Object[] { "approve_state",
												Integer.valueOf(iBillStatus) });
			}

			strQueryCondition = " (" + strQueryCondition + ")";

		} else {
			String strWorkCondition = getWorkCondition(billType, pk_org,
					iBillStatus);

			if (!StringUtils.isBlank(strWorkCondition)) {
				strQueryCondition = strWorkCondition;
			}
		}

		return strQueryCondition;
	}

	private String getWorkCondition(String strBillType, String strPk_org,
			int iBillStatus) throws BusinessException {
		String strDealWhere = "";

		if (110 == iBillStatus) {
			strDealWhere = MessageFormat.format(
					" and {0} in({1},{2})",
					new Object[] { "approve_state", Integer.valueOf(3),
							Integer.valueOf(2) });

		} else if (112 == iBillStatus) {
			strDealWhere = MessageFormat.format(" and {0} in({1})",
					new Object[] { "approve_state", Integer.valueOf(1) });
		} else if (111 == iBillStatus) {
			strDealWhere = MessageFormat.format(
					" and {0} in({1},{2},{3},{4},{5})",
					new Object[] { "approve_state", Integer.valueOf(2),
							Integer.valueOf(1), Integer.valueOf(0),
							Integer.valueOf(101), Integer.valueOf(102) });

		} else if (112 == iBillStatus) {
			strDealWhere = " and ( ( stapply_mode = 1 and pk_hi_org = '"
					+ strPk_org + "' and approve_state in (" + 1
					+ ")) or (stapply_mode = " + 2 + " and pk_hi_org = '"
					+ strPk_org + "'" + " and approve_state in (" + 1
					+ ")) or (stapply_mode = " + 3 + " and pk_hi_org = '"
					+ strPk_org + "' and approve_state in (" + 1 + "))) ";

		} else if (100000 == iBillStatus) {
			strDealWhere = MessageFormat.format(
					" and {0} in({1},{2},{3},{4},{5},{6})",
					new Object[] { "approve_state", Integer.valueOf(3),
							Integer.valueOf(2), Integer.valueOf(1),
							Integer.valueOf(0), Integer.valueOf(101),
							Integer.valueOf(102) });
		}

		String strWorkFlowWhere = ((IHrPf) NCLocator.getInstance().lookup(
				IHrPf.class)).getBillIdSql(iBillStatus, strBillType);

		if (!StringUtils.isBlank(strWorkFlowWhere)) {
			strWorkFlowWhere = "pk_hi_stapply in(" + strWorkFlowWhere + ")";
		}

		String strDirectWhere = getDirectWhere(strBillType, strPk_org,
				iBillStatus);

		String strAllWhere = "((" + strDirectWhere + ")";
		strAllWhere = strAllWhere
				+ (StringUtils.isBlank(strWorkFlowWhere) ? ")"
						: new StringBuilder().append(" or (")
								.append(strWorkFlowWhere).append("))")
								+ "");
		strAllWhere = strAllWhere + strDealWhere;

		return strAllWhere;
	}

	private String getDirectWhere(String strBillType, String strPk_org,
			int iBillStatus) {
		String strDirectWhere = MessageFormat
				.format(" {0} not in (select pub_wf_instance.billid from pub_wf_instance) ",
						new Object[] { "pk_hi_stapply" });

		if ("6113".equals(strBillType)) {
			strDirectWhere = strDirectWhere
					+ addDirectAppExtraWhereSql(strPk_org, iBillStatus);
		} else {
			strDirectWhere = strDirectWhere + " and pk_org = '" + strPk_org
					+ "' ";
		}
		return strDirectWhere;
	}

	private String addDirectAppExtraWhereSql(String strPk_org, int iBillStatus) {
		StringBuilder extraSql = new StringBuilder(" ");
		if (110 == iBillStatus) {

			extraSql.append(" and ( ( stapply_mode = 1 and pk_hi_org = '"
					+ strPk_org + "' and approve_state = " + 3
					+ " ) or ( stapply_mode = " + 2
					+ " and\t( ( pk_old_hi_org ='" + strPk_org
					+ "' and approve_state = " + 3 + " ) or (pk_hi_org = '"
					+ strPk_org + "' and approve_state = " + 2
					+ " ) ) ) or ( stapply_mode = " + 3
					+ " and\t( ( pk_hi_org = '" + strPk_org
					+ "' and approve_state = " + 3
					+ " ) or ( pk_old_hi_org = '" + strPk_org
					+ "' and approve_state = " + 2 + " ) ) ) )");

		} else if (111 == iBillStatus) {

			extraSql.append(" and ( ( stapply_mode =1 and\tpk_hi_org='"
					+ strPk_org + "' and approve_state in (" + 1 + "," + 0
					+ "," + 102 + "," + 101 + ")) or (stapply_mode = " + 2
					+ " and\t((pk_old_hi_org ='" + strPk_org
					+ "' and approve_state in (" + 1 + "," + 2 + "," + 0 + ","
					+ 101 + "," + 102 + ") ) or (pk_hi_org='" + strPk_org
					+ "' and\tapprove_state in (" + 1 + "," + 0 + "," + 102
					+ "," + 101 + ") ))) or\t(stapply_mode =" + 3
					+ " and\t( (pk_hi_org='" + strPk_org
					+ "' and\tapprove_state in (" + 1 + "," + 2 + "," + 0 + ","
					+ 101 + "," + 102 + ") ) or (pk_old_hi_org ='" + strPk_org
					+ "' and\tapprove_state in (" + 1 + "," + 0 + "," + 102
					+ "," + 101 + " ) ) ) ) ) ");

		} else if (100000 == iBillStatus) {
			extraSql.append(" and ( ( pk_old_hi_org = '" + strPk_org
					+ "' or (pk_hi_org = '" + strPk_org
					+ "' and approve_state in ( " + 1 + "," + 0 + "," + 102
					+ "," + 101 + "," + 2 + "))) and approve_state in (" + 1
					+ "," + 0 + "," + 102 + "," + 101 + "," + 3 + "," + 2
					+ " ) ) ");
		}

		return extraSql + "";
	}

	private boolean isHasFile(StapplyVO bill) throws BusinessException {
		if ((bill.getIsneedfile() == null)
				|| (!bill.getIsneedfile().booleanValue())) {

			return true;
		}

		IFileSystemService service = (IFileSystemService) NCLocator
				.getInstance().lookup(IFileSystemService.class);
		NCFileNode node = service.getNCFileNodeTreeAndCreateAsNeed(
				bill.getPk_hi_stapply(), PubEnv.getPk_user());
		return hasFile(node);
	}

	private boolean hasFile(NCFileNode node) {
		if (node.getChildCount() <= 0) {
			return false;
		}

		for (int i = 0; i < node.getChildCount(); i++) {
			if (!((NCFileNode) node.getChildAt(i)).isFolder()) {
				return true;
			}
			if (hasFile((NCFileNode) node.getChildAt(i))) {
				return true;
			}
		}
		return false;
	}

	public AggStapply createCrossInBill4RM(RM2TRNLinkData linkData)
			throws BusinessException {
		AggStapply agg = new AggStapply();
		StapplyVO bill = new StapplyVO();
		boolean isAuto = isAutoGenerateBillCode("6113", PubEnv.getPk_group(),
				linkData.getPk_org());
		try {
			if (isAuto) {
				bill.setBill_code(getIHrBillCode().getBillCode("6113",
						PubEnv.getPk_group(), linkData.getPk_org()));
			} else {
				BillCodeHelper.lockBillCodeRule("hr_auto_billcode6113", 100L);
				String prefix = "ZD6113" + PubEnv.getServerDate().toStdString();

				String flowCode = SQLHelper.getFlowCode(prefix, "bill_code",
						StapplyVO.class);
				bill.setBill_code(prefix + "_" + getFlowCode(flowCode, 0));
			}

			String[] org = ManagescopeFacade.queryHrOrgsByDeptAndBusiregion(
					linkData.getPk_dept(), ManagescopeBusiregionEnum.psndoc);
			String pk_hrorg = (org == null) || (org.length == 0) ? linkData
					.getPk_org() : org[0];

			bill.setPk_billtype("6113");
			bill.setPk_org(pk_hrorg);
			bill.setPk_group(PubEnv.getPk_group());
			bill.setPk_trnstype(linkData.getPk_trnstype());

			TrnstypeFlowVO[] flow = (TrnstypeFlowVO[]) ((IPersistenceRetrieve) NCLocator
					.getInstance().lookup(IPersistenceRetrieve.class))
					.retrieveByClause(
							null,
							TrnstypeFlowVO.class,
							" pk_group = '" + PubEnv.getPk_group()
									+ "' and pk_trnstype = '"
									+ linkData.getPk_trnstype() + "'");

			if ((flow != null) && (flow.length > 0)) {
				bill.setTranstype(flow[0].getPk_transtype());
				if (flow[0].getPk_transtype() != null) {
					BilltypeVO billtype = (BilltypeVO) ((IPersistenceRetrieve) NCLocator
							.getInstance().lookup(IPersistenceRetrieve.class))
							.retrieveByPk(null, BilltypeVO.class,
									flow[0].getPk_transtype());

					bill.setTranstype(billtype.getPk_billtypecode());
				}
			}

			bill.setApprove_state(Integer.valueOf(-1));
			bill.setBillmaker("NC_USER0000000000000");
			bill.setApply_date(PubEnv.getServerLiteralDate());
			bill.setFun_code("60090transapply");

			String pk_psndoc = linkData.getPk_psndoc();
			String pk_psnjob = ((IPersonRecordService) NCLocator.getInstance()
					.lookup(IPersonRecordService.class))
					.getPsnjobByPsndoc(pk_psndoc);
			PsnJobVO job = (PsnJobVO) queryByPk(PsnJobVO.class, pk_psnjob);
			bill.setPk_psnjob(job.getPk_psnjob());
			bill.setPk_psndoc(job.getPk_psndoc());
			bill.setPk_psnorg(job.getPk_psnorg());
			bill.setAssgid(job.getAssgid());
			bill.setPk_old_hi_org(job.getPk_hrorg());
			bill.setPk_hi_org(pk_hrorg);
			bill.setPk_old_hrcm_org(HiSQLHelper.getEveryHrorg(
					job.getPk_psnorg(), job.getAssgid(),
					ManagescopeBusiregionEnum.psnpact));
			bill.setPk_hrcm_org(pk_hrorg);
			bill.setStapply_mode(Integer.valueOf(job.getPk_hrorg().equals(
					pk_hrorg) ? 1 : 3));
			bill.setIshrssbill(UFBoolean.FALSE);
			bill.setIsneedfile(UFBoolean.FALSE);
			bill.setIfsynwork(UFBoolean.TRUE);
			bill.setIfendpart(UFBoolean.FALSE);

			for (String attr : job.getAttributeNames()) {
				bill.setAttributeValue("old" + attr,
						job.getAttributeValue(attr));
			}

			String[] flds = { "newpk_post", "newpk_postseries", "newpk_job",
					"newpk_jobgrade", "newpk_jobrank", "newseries" };

			TrnTransItemVO[] itemvos = (TrnTransItemVO[]) TrnDelegator
					.getIItemSetQueryService().queryItemSetByOrg(
							"8ce2d67d-d671-419c-9d80-ab4f135be3b8",
							PubEnv.getPk_group(), pk_hrorg,
							linkData.getPk_trnstype());

			for (int j = 0; (itemvos != null) && (j < itemvos.length); j++) {
				if ((!itemvos[j].getItemkey().startsWith("old"))
						&& (!ArrayUtils.contains(flds, itemvos[j].getItemkey()))) {

					if ((itemvos[j] != null)
							&& (itemvos[j].getIsdefault() != null)
							&& (itemvos[j].getIsdefault().booleanValue())
							&& (bill.getAttributeValue(itemvos[j].getItemkey()) == null)) {

						bill.setAttributeValue(itemvos[j].getItemkey(), job
								.getAttributeValue(itemvos[j].getItemkey()
										.substring(3)));
					}
				}
			}

			DeptVO dept = (DeptVO) ((IPersistenceRetrieve) NCLocator
					.getInstance().lookup(IPersistenceRetrieve.class))
					.retrieveByPk(null, DeptVO.class, linkData.getPk_dept());

			bill.setNewpk_org(dept.getPk_org());
			bill.setNewpk_dept(dept.getPk_dept());

			TrnTransItemVO postItem;

			if (bill.getOldpk_dept().equals(bill.getNewpk_dept())) {
				postItem = getItemByItemkey(itemvos, "newpk_post");
				TrnTransItemVO jobItem = getItemByItemkey(itemvos, "newpk_job");
				if ((postItem != null) && (postItem.getIsdefault() != null)
						&& (postItem.getIsdefault().booleanValue())) {

					bill.setNewpk_post(bill.getOldpk_post());
					bill.setNewpk_postseries(bill.getOldpk_postseries());
					bill.setNewpk_job(bill.getOldpk_job());
					bill.setNewpk_jobrank(bill.getOldpk_jobrank());
					bill.setNewpk_jobgrade(bill.getOldpk_jobgrade());
					bill.setNewseries(bill.getOldseries());
				} else if ((jobItem != null)
						&& (jobItem.getIsdefault() != null)
						&& (jobItem.getIsdefault().booleanValue())) {

					bill.setNewpk_job(bill.getOldpk_job());
					bill.setNewpk_jobrank(bill.getOldpk_jobrank());
					bill.setNewpk_jobgrade(bill.getOldpk_jobgrade());
					bill.setNewseries(bill.getOldseries());
				}
			}

			agg.setParentVO(bill);
			return (AggStapply) insertBill(agg);
		} finally {
			if (!isAuto) {
				BillCodeHelper.unlockBillCodeRule("hr_auto_billcode6113");
			}
		}
	}

	public String validateExistTrail(String[] psnjobPKs)
			throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		try {
			String cond = "  pk_psndoc in ( select pk_psndoc from hi_psndoc_trial where pk_psnorg in (select pk_psnorg from hi_psnjob where pk_psnjob in ( "
					+ isc.getInSQL(psnjobPKs) + " ) ) and endflag <> 'Y' ) ";

			PsndocVO[] vos = (PsndocVO[]) ((IPersistenceRetrieve) NCLocator
					.getInstance().lookup(IPersistenceRetrieve.class))
					.retrieveByClause(null, PsndocVO.class, cond);

			if ((vos == null) || (vos.length == 0)) {
				return null;
			}
			String name = "";
			for (PsndocVO vo : vos) {
				name = name + "," + MultiLangHelper.getName(vo);
			}
			if (StringUtils.isBlank(name)) {
				return null;
			}
			return name.substring(1);
		} finally {
			isc.clear();
		}
	}

	public HashMap<String, String> getPowerItem(String pk_psnjob,
			boolean isTransOut) throws BusinessException {
		HashMap<String, String> hm = new HashMap();
		PsnJobVO job = (PsnJobVO) queryByPk(PsnJobVO.class, pk_psnjob);

		String opCode = isTransOut ? "tansdefault" : "default";

		String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
				"60050orginfo", opCode, "org_orgs");
		powerSql = StringUtils.isBlank(powerSql) ? " 1 = 1 " : powerSql;
		int count = ((IPersistenceRetrieve) NCLocator.getInstance().lookup(
				IPersistenceRetrieve.class)).getCountByCondition("org_orgs",
				powerSql + " and org_orgs.pk_org = '" + job.getPk_org() + "' ");

		hm.put("pk_org", count <= 0 ? null : job.getPk_org());

		powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
				"60050deptinfo", opCode, "org_dept");
		powerSql = StringUtils.isBlank(powerSql) ? " 1 = 1 " : powerSql;
		count = ((IPersistenceRetrieve) NCLocator.getInstance().lookup(
				IPersistenceRetrieve.class)).getCountByCondition("org_dept",
				powerSql + " and org_dept.pk_dept = '" + job.getPk_dept()
						+ "' ");

		hm.put("pk_dept", count <= 0 ? null : job.getPk_dept());

		powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "psncl",
				opCode, "bd_psncl");
		powerSql = StringUtils.isBlank(powerSql) ? " 1 = 1 " : powerSql;
		count = ((IPersistenceRetrieve) NCLocator.getInstance().lookup(
				IPersistenceRetrieve.class)).getCountByCondition("bd_psncl",
				powerSql + " and bd_psncl.pk_psncl = '" + job.getPk_psncl()
						+ "' ");

		hm.put("pk_psncl", count <= 0 ? null : job.getPk_psncl());

		return hm;
	}

	public HashMap<String, Object> manualExecBills(AggStapply[] bills,
			LoginContext context, UFLiteralDate effectDate)
			throws BusinessException {
		if (!ArrayUtils.isEmpty(bills)) {
			for (int i = 0; i < bills.length; i++) {
				bills[i].getParentVO().setAttributeValue("effectdate",
						effectDate);
			}
		}
		HashMap<String, Object> result = execBills(bills, context, true);
		return result;
	}

	private class MapPsnProcessor extends BaseProcessor {
		private static final long serialVersionUID = 4148546217899305306L;

		private MapPsnProcessor() {
		}

		public Map<String, String> processResultSet(ResultSet rs)
				throws SQLException {
			Map<String, String> resultMap = new HashMap();
			while (rs.next()) {
				String pk_psndoc = rs.getString("pk_psndoc");
				String name = rs.getString("psnname");
				resultMap.put(pk_psndoc, name);
			}
			return resultMap;
		}

	}

	public Map<String, String> queryPsnNameByPKPsndoc(List<String> psndocPKLits)
			throws BusinessException {
		if (psndocPKLits.isEmpty()) {
			return null;
		}
		InSQLCreator isc = new InSQLCreator();
		String inSql = isc.getInSQL((String[]) psndocPKLits
				.toArray(new String[0]));
		String sql = "select pk_psndoc, "
				+ SQLHelper.getMultiLangNameColumn("name") + " psnname "
				+ "from bd_psndoc " + "where pk_psndoc in (" + inSql + ")";

		return (Map) ((IPersistenceHome) NCLocator.getInstance().lookup(
				IPersistenceHome.class)).executeQuery(sql,
				new MapPsnProcessor());
	}
}
