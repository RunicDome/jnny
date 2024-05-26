package nc.bs.ic.general.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nc.bs.framework.common.NCLocator;
import nc.bs.ic.pub.env.ICBSContext;
import nc.bs.logging.Logger;
import nc.bs.pfxx.ISwapContext;
import nc.bs.pfxx.plugin.AbstractPfxxPlugin;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.itf.scmpub.reference.uap.pf.PfServiceScmUtil;
import nc.itf.uap.pf.IPFBusiAction;
import nc.pubitf.scmf.ic.mbatchcode.IBatchcodePubService;
import nc.uif.pub.exception.UifException;
import nc.vo.ic.general.define.ICBillBodyVO;
import nc.vo.ic.general.define.ICBillFlag;
import nc.vo.ic.general.define.ICBillHeadVO;
import nc.vo.ic.general.define.ICBillVO;
import nc.vo.ic.general.util.InOutHelp;
import nc.vo.ic.material.define.InvBasVO;
import nc.vo.ic.material.query.InvInfoQuery;
import nc.vo.ic.org.OrgInfoQuery;
import nc.vo.ic.pub.define.ICBillTableInfo;
import nc.vo.ic.pub.util.StringUtil;
import nc.vo.ic.pub.util.ValueCheckUtil;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pfxx.auxiliary.AggxsysregisterVO;
import nc.vo.pfxx.util.PfxxPluginUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.data.ValueUtils;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.pubapp.util.VORowNoUtils;
import nc.vo.scmf.ic.mbatchcode.BatchcodeVO;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
// 材料出库单XML导入
public class GeneralDefdocPlugin extends AbstractPfxxPlugin {
	public GeneralDefdocPlugin() {
	}

	protected Object processBill(Object vo, ISwapContext swapContext,
			AggxsysregisterVO aggxsysvo) throws BusinessException {
		if (vo == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("4008001_0", "04008001-0137"));
		}

		String vopk = PfxxPluginUtils.queryBillPKBeforeSaveOrUpdate(
				swapContext.getBilltype(), swapContext.getDocID(),
				swapContext.getOrgPk());

		if ((!StringUtil.isSEmptyOrNull(vopk)) && (!canUpdate())) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("4008001_0", "04008001-0138"));
		}

		ICBillVO icbill = (ICBillVO) vo;
		icbill.getHead().setCgeneralhid(vopk);
		// 修改项目主键
		for (int i = 0; i < icbill.getChildrenVO().length; i++) {
			String pk_project = getproject(icbill.getParentVO().getPk_org(),
					icbill.getChildrenVO()[i].getCprojectid());
			icbill.getChildrenVO()[i].setCprojectid(pk_project);
		}
		ICBillVO[] icbills = null;
		if (ValueUtils.getBoolean(swapContext.getReplace())) {
			icbills = doUpdate(swapContext, icbill);
		} else {
			icbills = doSave(swapContext, icbill);
		}
		return icbills[0].getHead().getCgeneralhid();
	}

	private String getproject(String pk_org, String project_code)
			throws BusinessException {
		String pk_project = (String) new HYPubBO().findColValue("bd_project",
				"pk_project", "nvl(dr,0) = 0 and pk_duty_org = '" + pk_org
						+ "' and project_code = '" + project_code + "'");
		return pk_project;
	}

	private ICBillVO[] doSave(ISwapContext swapContext, ICBillVO icbill)
			throws BusinessException {
		checkCanInster(icbill);
		Logger.info("保存新单据前处理...");
		processBeforeSave(icbill);

		Logger.info("保存新单据...");
		IPFBusiAction service = (IPFBusiAction) NCLocator.getInstance().lookup(
				IPFBusiAction.class);
		ICBillVO[] icbills = (ICBillVO[]) service.processAction("WRITE",
				swapContext.getBilltype(), null, icbill, null, null);

		Logger.info("保存新单据完成...");

		Logger.info("保存新单据后处理...");
		processAfterSave(icbill);

		if (ValueCheckUtil.isNullORZeroLength(icbills)) {
			return null;
		}
		if (canUpdate()) {
			PfxxPluginUtils.addDocIDVsPKContrast(swapContext.getBilltype(),
					swapContext.getDocID(), swapContext.getOrgPk(), icbills[0]
							.getHead().getCgeneralhid());
		}

		return icbills;
	}

	private ICBillVO[] doUpdate(ISwapContext swapContext, ICBillVO icbill)
			throws BusinessException {
		ICBillVO bill = getOriBillVO(icbill, swapContext.getBilltype());
		if (bill != null) {
			IPFBusiAction service = (IPFBusiAction) NCLocator.getInstance()
					.lookup(IPFBusiAction.class);

			service.processAction("DELETE", swapContext.getBilltype(), null,
					bill, null, null);
		}

		return doSave(swapContext, icbill);
	}

	private ICBillVO getOriBillVO(ICBillVO icbill, String billtype) {
		if (StringUtil.isSEmptyOrNull(icbill.getHead().getVbillcode()))
			return null;
		SqlBuilder where = new SqlBuilder();
		where.append(" and ");
		where.append("vbillcode", icbill.getHead().getVbillcode());
		where.append(" and ");
		where.append("pk_group", icbill.getHead().getPk_group());

		ICBillTableInfo billinfo = ICBillTableInfo.getICBillTableInfo(InOutHelp
				.getICBillType(billtype));

		VOQuery<ICBillHeadVO> query = new VOQuery(billinfo.getHeadClass());

		ICBillHeadVO[] heads = (ICBillHeadVO[]) query.query(where.toString(),
				null);
		if (ValueCheckUtil.isNullORZeroLength(heads))
			return null;
		where = new SqlBuilder();
		where.append(" and ");
		where.append("cgeneralhid", heads[0].getCgeneralhid());
		VOQuery<ICBillBodyVO> bodyquery = new VOQuery(billinfo.getBodyClass());

		ICBillBodyVO[] bodys = (ICBillBodyVO[]) bodyquery.query(
				where.toString(), null);
		if (ValueCheckUtil.isNullORZeroLength(bodys)) {
			return null;
		}
		ICBillVO bill = (ICBillVO) billinfo.createBillVO();
		bill.setParent(heads[0]);
		bill.setChildrenVO(bodys);
		return bill;
	}

	protected void processBeforeSave(ICBillVO vo) throws BusinessException {
		if (null == vo) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("4008001_0", "04008001-0139"));
		}

		if (StringUtil.isSEmptyOrNull(vo.getParentVO().getPk_org()))
			vo.getParentVO().setPk_org(vo.getBodys()[0].getPk_org());
		if (StringUtil.isSEmptyOrNull(vo.getParentVO().getPk_org_v()))
			vo.getParentVO().setPk_org_v(vo.getBodys()[0].getPk_org_v());
		if (StringUtil.isSEmptyOrNull(vo.getParentVO().getCwarehouseid())) {
			vo.getParentVO().setCwarehouseid(
					vo.getBodys()[0].getCbodywarehouseid());
		}
		ICBSContext context = new ICBSContext();
		headVOProcess(vo.getHead(), context);
		bodyVOProcess(vo, context);
	}

	protected void processAfterSave(ICBillVO vo) throws BusinessException {
		if (null == vo) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("4008001_0", "04008001-0140"));
		}
	}

	private void headVOProcess(ICBillHeadVO vo, ICBSContext context) {
		vo.setStatus(2);

		if (StringUtil.isSEmptyOrNull(vo.getPk_group())) {
			vo.setPk_group(context.getPk_group());
		}
		if (vo.getIprintcount() == null) {
			vo.setIprintcount(Integer.valueOf(0));
		}
		if (vo.getFbillflag() == null) {
			vo.setFbillflag((Integer) ICBillFlag.FREE.value());
		}
		if (vo.getDbilldate() == null) {
			vo.setDbilldate(context.getBizDate());
		}
		if ((StringUtil.isSEmptyOrNull(vo.getCorpoid()))
				|| (StringUtil.isSEmptyOrNull(vo.getCorpvid()))) {
			vo.setCorpoid(context.getOrgInfo().getCorpIDByCalBodyID(
					vo.getPk_org()));
			vo.setCorpvid(context.getOrgInfo().getCorpVIDByCalBodyID(
					vo.getPk_org()));
		}

		if (StringUtil.isSEmptyOrNull(vo.getCtrantypeid())) {
			String vtrantypecode = vo.getVtrantypecode();
			Map<String, String> map = PfServiceScmUtil
					.getTrantypeidByCode(new String[] { vtrantypecode });

			vo.setCtrantypeid(map == null ? null : (String) map
					.get(vtrantypecode));
		}
	}

	private void bodyVOProcess(ICBillVO vo, ICBSContext context)
			throws BusinessException {
		ICBillBodyVO[] vos = vo.getBodys();
		if (ValueCheckUtil.isNullORZeroLength(vos)) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("4008001_0", "04008001-0141"));
		}

		VORowNoUtils.setVOsRowNoByRule(vos, "crowno");

		ICBillHeadVO head = vo.getHead();
		Map<String, BatchcodeVO> batchmap = getBatchcodeVO(vos);
		for (ICBillBodyVO body : vos) {
			body.setStatus(2);
			if ((StringUtil.isSEmptyOrNull(body.getCmaterialoid()))
					|| (StringUtil.isSEmptyOrNull(body.getCmaterialvid()))) {
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("4008001_0", "04008001-0142"));
			}

			body.setBbarcodeclose(UFBoolean.FALSE);
			body.setBonroadflag(UFBoolean.FALSE);
			if ((body.getNnum() != null) && (body.getNassistnum() != null)
					&& (body.getDbizdate() == null)) {
				body.setDbizdate(context.getBizDate());
			}
			if (StringUtil.isSEmptyOrNull(body.getCastunitid())) {
				body.setCastunitid(context.getInvInfo()
						.getInvBasVO(body.getCmaterialvid()).getPk_stockmeas());
			}

			if ((!StringUtils.isEmpty(body.getVbatchcode()))
					&& (StringUtils.isEmpty(body.getPk_batchcode()))) {
				BatchcodeVO batchvo = (BatchcodeVO) batchmap.get(body
						.getCmaterialvid() + body.getVbatchcode());

				if (batchvo != null) {
					body.setPk_batchcode(batchvo.getPk_batchcode());
					body.setDproducedate(batchvo.getDproducedate());
					body.setDvalidate(batchvo.getDvalidate());
				}
			}
			bodyVOCopyFromHeadVO(body, head);
		}
	}

	private Map<String, BatchcodeVO> getBatchcodeVO(ICBillBodyVO[] vos) {
		List<String> cmaterialvidList = new ArrayList();
		List<String> vbatchcodeList = new ArrayList();
		Set<String> materialbatch = new HashSet();
		for (ICBillBodyVO body : vos) {
			if ((body.getCmaterialvid() != null)
					&& (body.getVbatchcode() != null)
					&& (!materialbatch.contains(body.getCmaterialvid()
							+ body.getVbatchcode()))) {

				cmaterialvidList.add(body.getCmaterialvid());
				vbatchcodeList.add(body.getVbatchcode());
				materialbatch
						.add(body.getCmaterialvid() + body.getVbatchcode());
			}
		}
		if (materialbatch.size() == 0) {
			return new HashMap();
		}
		IBatchcodePubService batchservice = (IBatchcodePubService) NCLocator
				.getInstance().lookup(IBatchcodePubService.class);

		BatchcodeVO[] batchvos = null;
		try {
			batchvos = batchservice.queryBatchVOs(
					(String[]) cmaterialvidList.toArray(new String[0]),
					(String[]) vbatchcodeList.toArray(new String[0]));

		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}

		if ((batchvos == null) || (batchvos.length == 0)) {
			return new HashMap();
		}
		Map<String, BatchcodeVO> batchmap = new HashMap();
		for (BatchcodeVO batchvo : batchvos) {
			batchmap.put(batchvo.getCmaterialvid() + batchvo.getVbatchcode(),
					batchvo);
		}

		return batchmap;
	}

	private void bodyVOCopyFromHeadVO(ICBillBodyVO body, ICBillHeadVO head) {
		body.setPk_group(head.getPk_group());
		body.setPk_org(head.getPk_org());
		body.setPk_org_v(head.getPk_org_v());
		body.setCorpoid(head.getCorpoid());
		body.setCorpvid(head.getCorpvid());
		body.setCbodywarehouseid(head.getCwarehouseid());
		body.setCbodytranstypecode(head.getVtrantypecode());
	}

	protected boolean canUpdate() {
		return false;
	}

	protected void checkCanInster(AggregatedValueObject vo) {
		checkBillFlag(vo);
		new CheckMnyUtil().checkMny(vo);
		new CheckScaleUtil().checkScale(vo);
	}

	private void checkBillFlag(AggregatedValueObject vo) {
		if (!Integer.valueOf(ICBillFlag.getFreeFlag()).equals(
				vo.getParentVO().getAttributeValue(getBillStatusKey()))) {
			ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
					.getNCLangRes().getStrByID("4008001_0", "04008001-0816"));
		}
	}

	protected String getBillStatusKey() {
		return "fbillflag";
	}
}
