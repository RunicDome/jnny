package nc.impl.invp.action.maintain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.db2.jcc.am.q;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.invp.plan.InvpLogUtil;
import nc.bs.trade.business.HYPubBO;
import nc.itf.ewm.pub.IWorkOrderForInv;
import nc.itf.invp.result.IQueryMaterialPlanMap;
import nc.itf.scmpub.reference.uap.bd.accesor.MaterialAccessor;
import nc.itf.scmpub.reference.uap.bd.accesor.StockOrgAccessor;
import nc.itf.scmpub.reference.uap.bd.material.MaterialStockClassPubService;
import nc.itf.scmpub.reference.uap.group.SysInitGroupQuery;
import nc.itf.scmpub.reference.uap.org.OrgUnitPubService;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.pubitf.ic.atp.IAtpQuery;
import nc.pubitf.ic.onhand.IOnhandQry;
import nc.pubitf.pu.m422x.invp.inv9.IStorereqQueryForInv9;
import nc.pubitf.scmf.sourcing.sour4mm.ISourceMMService;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.accessor.IBDData;
import nc.vo.bd.material.stock.MaterialStockVO;
import nc.vo.ic.atp.entity.AtpVO;
import nc.vo.ic.atp.pub.AtpQryParamVO;
import nc.vo.ic.onhand.entity.OnhandVO;
import nc.vo.invp.balance.entity.AggBalanceRuleVO;
import nc.vo.invp.balance.entity.BalanceRuleBodyVO;
import nc.vo.invp.balance.entity.BalanceRuleHeadVO;
import nc.vo.invp.pub.enm.ReqTypeEnum;
import nc.vo.invp.pub.util.BalanceUtil;
import nc.vo.invp.pub.util.InvpBalanceSysParamUtil;
import nc.vo.invp.query.enm.PlanQueryMaterTypeEnum;
import nc.vo.invp.result.entity.AggBalanceResultVO;
import nc.vo.invp.result.entity.BalanceResultVO;
import nc.vo.invp.result.entity.BalanceTreeParamVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.pub.MathTool;

// 需求汇总平衡需新增定向平衡库存功能
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class BuildBalanceTreeAction {
	private String rootname = "root";

	private BalanceRuleBodyVO[] rulebodys;

	private BalanceRuleHeadVO rulehead;

	private Map<String, String> vstockorgsMap = new HashMap();

	Map<String, UFDouble> canuseNumMap = new HashMap();

	public BuildBalanceTreeAction() {
	}

	public BalanceTreeParamVO buildBalanceTree(Object[] vos)
			throws BusinessException {
		List<BalanceResultVO> treevoList = new ArrayList();
		List<String> orgList = new ArrayList();
		List<String> materialList = new ArrayList();
		List<String> vmaterialList = new ArrayList();
		AggBalanceResultVO vo = (AggBalanceResultVO) vos[0];

		UFBoolean flag = getParaValue(vo.getParentVO().getPk_org());
		if (UFBoolean.FALSE.equals(flag)) {
			return null;
		}

		String[] pk_vids = new String[this.rulebodys.length];
		for (int i = 0; i < this.rulebodys.length; i++) {
			pk_vids[i] = this.rulebodys[i].getPk_stockorg_v();
		}

		Map<String, String> orgMap = OrgUnitPubService.getOrgIDSByVIDS(pk_vids);

		List<String> parentVMaterials = new ArrayList();
		Map<String, String> parentMaterialMap = new HashMap();

		String orgstr = null;
		List<String> orgMaterialList = new ArrayList();

		for (Object obj : vos) {
			AggBalanceResultVO aggvo = (AggBalanceResultVO) obj;
			BalanceResultVO treeparentvo = aggvo.getParentVO();
			if (null == orgstr) {
				orgstr = treeparentvo.getPk_org();
			}
			treeparentvo.setPk_parentid(this.rootname);
			treevoList.add(treeparentvo);

			String okey = treeparentvo.getPk_org()
					+ treeparentvo.getCmaterialoid();
			if (!orgMaterialList.contains(okey)) {
				orgList.add(treeparentvo.getPk_org());
				materialList.add(treeparentvo.getCmaterialoid());
				vmaterialList.add(treeparentvo.getCmaterialvid());
				parentMaterialMap.put(treeparentvo.getCmaterialvid(),
						treeparentvo.getCmaterialoid());

				parentVMaterials.add(treeparentvo.getCmaterialvid());
				orgMaterialList.add(okey);
			}
			if ((null != this.rulebodys) && (this.rulebodys.length != 0)) {

				List<String> ids = BalanceUtil
						.createRandKey(this.rulebodys.length);

				for (int i = 0; i < this.rulebodys.length; i++) {
					BalanceRuleBodyVO rulebodyvo = this.rulebodys[i];
					if ((null != rulebodyvo.getPk_stockorg_v())
							&& (!rulebodyvo.getPk_stockorg_v().isEmpty())) {

						if (!ReqTypeEnum.BEFOREBALREQ.integerValue().equals(
								treeparentvo.getFreqtype())) {

							if ((!StringUtil.isEmpty(rulebodyvo
									.getPk_stockorg_v()))
									&& (!rulebodyvo.getPk_stockorg_v().equals(
											treeparentvo.getPk_org_v()))) {

								if ((!ReqTypeEnum.AFTERBALREQ.integerValue()
										.equals(treeparentvo.getFreqtype()))
										|| (!treeparentvo.getPk_org_req()
												.equals(treeparentvo
														.getPk_org()))) {

									BalanceResultVO bodytreevo = cloneBodyVO(
											treeparentvo,
											(String) orgMap.get(rulebodyvo
													.getPk_stockorg_v()),
											rulebodyvo.getPk_stockorg_v(),
											(String) ids.get(i));

									treevoList.add(bodytreevo);
									orgList.add(bodytreevo.getPk_org());
									materialList.add(bodytreevo
											.getCmaterialoid());
									vmaterialList.add(bodytreevo
											.getCmaterialvid());
								}
							}
						}
					}
				}
			}
		}
		Map<String, String> nextOrgMap = getMaterialStockInfo(
				(String[]) parentVMaterials.toArray(new String[0]),
				parentMaterialMap, orgstr);

		filterSname(orgList, materialList, vmaterialList);
		String[] orgs = (String[]) orgList.toArray(new String[0]);
		String[] materials = (String[]) materialList.toArray(new String[0]);
		String[] vmaterials = (String[]) vmaterialList.toArray(new String[0]);

		Map<String, AtpVO> atpMap = new HashMap();
		if ((UFBoolean.TRUE.equals(this.rulehead.getBpo()))
				|| (UFBoolean.TRUE.equals(this.rulehead.getBpr()))
				|| (UFBoolean.TRUE.equals(this.rulehead.getBreqin()))
				|| (UFBoolean.TRUE.equals(this.rulehead.getBmmplan()))
				|| (UFBoolean.TRUE.equals(this.rulehead.getBreqout()))) {

			atpMap = queryATPs(orgs, materials);
		}

		Map<String, UFDouble> safeMap = new HashMap();
		if (UFBoolean.TRUE.equals(this.rulehead.getBsafestock())) {
			InvpLogUtil.logout();
			IQueryMaterialPlanMap safeService = (IQueryMaterialPlanMap) NCLocator
					.getInstance().lookup(IQueryMaterialPlanMap.class);

			safeMap = safeService
					.queryMaterialPlanMapInfoByPks(orgs, materials);
			InvpLogUtil.logout();
		}

		Map<String, UFDouble> reqMap = new HashMap();
		if (UFBoolean.TRUE.equals(this.rulehead.getBmdapply())) {
			reqMap = queryStockReqPlanout(orgs, vmaterials);
		}

		Map<String, UFDouble> wkMap = new HashMap();
		if (UFBoolean.TRUE.equals(this.rulehead.getBworkorder())) {
			wkMap = queryWorkOrderPlanout(orgs, vmaterials);
		}

		Map<String, UFDouble> onhandMap = queryOnhandNum(orgs, materials);

		BalanceResultVO[] treeDatas = (BalanceResultVO[]) treevoList
				.toArray(new BalanceResultVO[0]);

		setAtpValue(treeDatas, atpMap, reqMap, wkMap, safeMap, nextOrgMap,
				onhandMap);

		BalanceTreeParamVO param = new BalanceTreeParamVO();
		param.setCanusenummap(this.canuseNumMap);
		param.setTreeVO(treeDatas);
		return param;
	}

	private void calNum(BalanceResultVO vo) {
		UFDouble ncanusenum = UFDouble.ZERO_DBL;

		UFDouble nin = MathTool.add(vo.getNpraynum(), vo.getNpurchasenum());
		nin = MathTool.add(nin, vo.getNtransinnum());

		UFDouble nout = MathTool.add(vo.getNreqnum(), vo.getNworknum());
		nout = MathTool.add(nout, vo.getNstockpreparenum());
		nout = MathTool.add(nout, vo.getNsafestocknum());
		nout = MathTool.add(nout, vo.getNtransoutnum());
		String key = vo.getPk_org() + vo.getCmaterialoid();
		if ((null != this.canuseNumMap) && (null != this.canuseNumMap.get(key))) {
			ncanusenum = (UFDouble) this.canuseNumMap.get(key);
		} else {
			ncanusenum = vo.getNstocknum().add(nin).sub(nout);
		}

		if (MathTool.compareTo(ncanusenum, UFDouble.ZERO_DBL) < 0) {
			vo.setNsupplynum(UFDouble.ZERO_DBL);

		} else if (MathTool.compareTo(ncanusenum, vo.getNnum()) >= 0) {
			vo.setNsupplynum(vo.getNnum());
			ncanusenum = ncanusenum.sub(vo.getNnum());
		} else {
			vo.setNsupplynum(ncanusenum);
			ncanusenum = UFDouble.ZERO_DBL;
		}

		vo.setNlacknum(vo.getNnum().sub(vo.getNsupplynum()));

		vo.setNsugpunum(vo.getNlacknum());

		this.canuseNumMap.put(key, ncanusenum);
	}

	private BalanceResultVO cloneBodyVO(BalanceResultVO treeparentvo,
			String pk_stockorg, String pk_stockorg_v, String id) {
		BalanceResultVO treebodyvo = new BalanceResultVO();
		treebodyvo.setPk_balance_result(id);
		treebodyvo.setPk_org(pk_stockorg);
		treebodyvo.setPk_org_v(pk_stockorg_v);
		treebodyvo.setCmaterialvid(treeparentvo.getCmaterialvid());
		treebodyvo.setCmaterialoid(treeparentvo.getCmaterialoid());
		treebodyvo.setCbilltypecode(treeparentvo.getCbilltypecode());
		treebodyvo.setCreqbillid(treeparentvo.getCreqbillid());
		treebodyvo.setCreqrowid(treeparentvo.getCreqrowid());
		treebodyvo.setVreqbillcode(treeparentvo.getVreqbillcode());
		treebodyvo.setVreqrowno(treeparentvo.getVreqrowno());
		treebodyvo.setCunitid(treeparentvo.getCunitid());
		treebodyvo.setDreqdate(treeparentvo.getDreqdate());
		treebodyvo.setNnum(treeparentvo.getNnum());
		treebodyvo.setVbatchcode(treeparentvo.getVbatchcode());
		treebodyvo.setCprojecttask(treeparentvo.getCprojecttask());
		treebodyvo.setPk_project(treeparentvo.getPk_project());
		treebodyvo.setPk_supplier(treeparentvo.getPk_supplier());
		treebodyvo.setPk_productor(treeparentvo.getPk_productor());
		treebodyvo.setBistransflag(treeparentvo.getBistransflag());
		treebodyvo.setPk_reqstordoc(treeparentvo.getPk_reqstordoc());
		treebodyvo.setPk_org_originalreq(treeparentvo.getPk_org_originalreq());
		treebodyvo.setPk_org_originalreq_v(treeparentvo
				.getPk_org_originalreq_v());
		treebodyvo.setPk_org_req(treeparentvo.getPk_org_req());
		treebodyvo.setPk_org_req_v(treeparentvo.getPk_org_req_v());
		treebodyvo.setPk_org_maintain(treeparentvo.getPk_org_maintain());
		treebodyvo.setPk_org_maintain_v(treeparentvo.getPk_org_maintain_v());
		treebodyvo.setPk_org_stockin(treeparentvo.getPk_org_stockin());
		treebodyvo.setPk_org_stockin_v(treeparentvo.getPk_org_stockin_v());
		treebodyvo.setPk_org_next(treeparentvo.getPk_org_next());
		treebodyvo.setPk_org_next_v(treeparentvo.getPk_org_next_v());
		treebodyvo.setFreqtype(treeparentvo.getFreqtype());
		treebodyvo.setPk_departmen_req(treeparentvo.getPk_departmen_req());
		treebodyvo.setPk_person_req(treeparentvo.getPk_person_req());
		treebodyvo.setDapplydate(treeparentvo.getDapplydate());
		treebodyvo.setPk_group(treeparentvo.getPk_group());
		treebodyvo.setBisurgent(treeparentvo.getBisurgent());
		treebodyvo.setVmemo(treeparentvo.getVmemo());
		treebodyvo.setVmaterialspec(treeparentvo.getVmaterialspec());
		treebodyvo.setVmaterialtype(treeparentvo.getVmaterialtype());
		treebodyvo.setPk_parentid(treeparentvo.getPk_balance_result());
		treebodyvo.setVfree1(treeparentvo.getVfree1());
		treebodyvo.setVfree1(treeparentvo.getVfree2());
		treebodyvo.setVfree1(treeparentvo.getVfree3());
		treebodyvo.setVfree1(treeparentvo.getVfree4());
		treebodyvo.setVfree1(treeparentvo.getVfree5());
		treebodyvo.setVfree1(treeparentvo.getVfree6());
		treebodyvo.setVfree1(treeparentvo.getVfree7());
		treebodyvo.setVfree1(treeparentvo.getVfree8());
		treebodyvo.setVfree1(treeparentvo.getVfree9());
		treebodyvo.setVfree1(treeparentvo.getVfree10());
		treebodyvo.setVdef1(treeparentvo.getVdef1());
		treebodyvo.setVdef1(treeparentvo.getVdef2());
		treebodyvo.setVdef1(treeparentvo.getVdef3());
		treebodyvo.setVdef1(treeparentvo.getVdef4());
		treebodyvo.setVdef1(treeparentvo.getVdef5());
		treebodyvo.setVdef1(treeparentvo.getVdef6());
		treebodyvo.setVdef1(treeparentvo.getVdef7());
		treebodyvo.setVdef1(treeparentvo.getVdef8());
		treebodyvo.setVdef1(treeparentvo.getVdef9());
		treebodyvo.setVdef1(treeparentvo.getVdef10());
		treebodyvo.setVdef1(treeparentvo.getVdef11());
		treebodyvo.setVdef1(treeparentvo.getVdef12());
		treebodyvo.setVdef1(treeparentvo.getVdef13());
		treebodyvo.setVdef1(treeparentvo.getVdef14());
		treebodyvo.setVdef1(treeparentvo.getVdef15());
		treebodyvo.setVdef1(treeparentvo.getVdef16());
		treebodyvo.setVdef1(treeparentvo.getVdef17());
		treebodyvo.setVdef1(treeparentvo.getVdef18());
		treebodyvo.setVdef1(treeparentvo.getVdef19());
		treebodyvo.setVdef1(treeparentvo.getVdef20());
		treebodyvo.setVbdef1(treeparentvo.getVbdef1());
		treebodyvo.setVbdef1(treeparentvo.getVbdef2());
		treebodyvo.setVbdef1(treeparentvo.getVbdef3());
		treebodyvo.setVbdef1(treeparentvo.getVbdef4());
		treebodyvo.setVbdef1(treeparentvo.getVbdef5());
		treebodyvo.setVbdef1(treeparentvo.getVbdef6());
		treebodyvo.setVbdef1(treeparentvo.getVbdef7());
		treebodyvo.setVbdef1(treeparentvo.getVbdef8());
		treebodyvo.setVbdef1(treeparentvo.getVbdef9());
		treebodyvo.setVbdef1(treeparentvo.getVbdef10());
		treebodyvo.setVbdef1(treeparentvo.getVbdef11());
		treebodyvo.setVbdef1(treeparentvo.getVbdef12());
		treebodyvo.setVbdef1(treeparentvo.getVbdef13());
		treebodyvo.setVbdef1(treeparentvo.getVbdef14());
		treebodyvo.setVbdef1(treeparentvo.getVbdef15());
		treebodyvo.setVbdef1(treeparentvo.getVbdef16());
		treebodyvo.setVbdef1(treeparentvo.getVbdef17());
		treebodyvo.setVbdef1(treeparentvo.getVbdef18());
		treebodyvo.setVbdef1(treeparentvo.getVbdef19());
		treebodyvo.setVbdef1(treeparentvo.getVbdef20());
		treebodyvo.setHts(treeparentvo.getHts());
		treebodyvo.setBts(treeparentvo.getBts());
		return treebodyvo;
	}

	private void filterSname(List<String> orgList, List<String> materialList,
			List<String> vmaterialList) {
		if (orgList != null) {
			int length = -1;
			int size = orgList.size();
			if ((size == 1) || (size == 0)) {
				return;
			}

			length = size;

			Set<String> set = new HashSet();
			for (int i = 0; i < length; i++) {
				String key = (String) orgList.get(i)
						+ (String) materialList.get(i);
				if (set.contains(key)) {
					orgList.remove(i);
					materialList.remove(i);
					vmaterialList.remove(i);
					i--;
					length--;
				} else {
					set.add(key);
				}
			}
		}
	}

	private Map<String, String> getMaterialStockInfo(String[] vmaterials,
			Map<String, String> materialMap, String stockorg) {
		Map<String, String> nextOrgMap = new HashMap();
		List<String> pk_materials = new ArrayList();

		Map<String, MaterialStockVO> stockMap = MaterialStockClassPubService
				.queryMaterialStockInfoByPks(vmaterials, stockorg,
						new String[] { "martype" });

		Set<String> notAllotMaterial = new HashSet();
		for (String material : vmaterials) {
			MaterialStockVO vo = (MaterialStockVO) stockMap.get(material);
			if ((null == vo) || (null == vo.getMartype())) {
				notAllotMaterial.add(material);

			} else if ((!PlanQueryMaterTypeEnum.MATERTYPE_MR.code().equals(
					vo.getMartype()))
					&& (!PlanQueryMaterTypeEnum.MATERTYPE_OT.code().equals(
							vo.getMartype()))
					&& (!PlanQueryMaterTypeEnum.MATERTYPE_PR.code().equals(
							vo.getMartype()))
					&& (!PlanQueryMaterTypeEnum.MATERTYPE_ET.code().equals(
							vo.getMartype()))) {

				pk_materials.add(materialMap.get(material));
			}
		}

		if (notAllotMaterial.size() > 0) {
			String[] ids = (String[]) notAllotMaterial
					.toArray(new String[notAllotMaterial.size()]);

			Map<String, IBDData> map = MaterialAccessor.getDocMapByPks(ids);
			IBDData stock = StockOrgAccessor.getDocByPk(stockorg);
			StringBuffer materNotAllot = new StringBuffer();
			for (String m : notAllotMaterial) {
				String[] paras = { ((IBDData) map.get(m)).getCode(),
						stock.getCode().toString() };

				materNotAllot.append(
						NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"4007005_0", "04007005-0035", null, paras))
						.append("\r\n");
			}

			ExceptionUtils.wrappBusinessException(materNotAllot.toString());
		}

		String[] pk_stockorgs = new String[pk_materials.size()];
		for (int i = 0; i < pk_materials.size(); i++) {
			pk_stockorgs[i] = stockorg;
		}

		ISourceMMService mmService = (ISourceMMService) NCLocator.getInstance()
				.lookup(ISourceMMService.class);

		String[] stockorgs = mmService.queryStockOrgs(
				(String[]) pk_materials.toArray(new String[0]), pk_stockorgs);

		this.vstockorgsMap = OrgUnitPubService.getNewVIDSByOrgIDS(stockorgs);
		for (int i = 0; i < pk_materials.size(); i++) {
			nextOrgMap.put((String) pk_materials.get(i) + stockorg,
					stockorgs[i]);
		}

		return nextOrgMap;
	}

	private UFBoolean getParaValue(String pk_org) {
		if ((null == pk_org)
				|| (!OrgUnitPubService.isTypeOf(pk_org, "STOCKORGTYPE00000000"))) {
			return UFBoolean.FALSE;
		}

		String paraStr = InvpBalanceSysParamUtil.getINVP013NoCacheValue(pk_org);
		AggBalanceRuleVO ruleaggvo = AggBalanceRuleVO.valueOf(paraStr);
		this.rulebodys = ruleaggvo.getChildrenVO();
		this.rulehead = ruleaggvo.getParentVO();
		return UFBoolean.TRUE;
	}

	private Map<String, AtpVO> queryATPs(String[] pk_orgs, String[] pk_materials)
			throws BusinessException {
		InvpLogUtil.logout();
		UFDate[] date = new UFDate[pk_orgs.length];
		Map<String, AtpVO> atpMap = new HashMap();
		IAtpQuery service = (IAtpQuery) NCLocator.getInstance().lookup(
				IAtpQuery.class);
		AtpVO[] atps = service.queryAtpVOs(pk_orgs, pk_materials, date);
		/* XBX修改现存量 */
		for (int i = 0; i < atps.length; i++) {
			AtpVO atp = atps[i];
			String pk_org = atp.getPk_org();
			String pk_material = atp.getCmaterialoid();
			if (ifBalaByOrg(pk_org)) {
				atp.setNonhandnum(getNonhandnum(pk_org, pk_material));
			}
		}
		InvpLogUtil.logout();
		for (AtpVO vo : atps) {
			String key = vo.getPk_org() + vo.getCmaterialoid();
			atpMap.put(key, vo);
		}
		return atpMap;
	}

	/**
	 * 根据pk_org校验是否平衡数量
	 * 
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	private boolean ifBalaByOrg(String pk_org) throws BusinessException {
		String org = (String) new HYPubBO().findColValue("bd_defdoc", "code",
				"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
						+ "FROM BD_DEFDOCLIST WHERE CODE = 'BALANCEORG')"
						+ " and name = '" + pk_org + "'");// 流程节点名称
		if (org != null) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * 通过组织，物料 获取平衡库存内的现存量
	 * 
	 * @param pk_org
	 * @param pk_material
	 * @return
	 * @throws BusinessException
	 * @throws DAOException
	 */
	private UFDouble getNonhandnum(String pk_org, String pk_material)
			throws BusinessException {
		/* 按组织、物料、平衡仓库取总现存量 */
		StringBuffer queryOnhandSql = new StringBuffer();
		queryOnhandSql
				.append("select sum(zsl) xcl from v_ewm_material_kc where kczz = '");
		queryOnhandSql.append(pk_org + "'  and pk_material = '");
		queryOnhandSql
				.append(pk_material
						+ "' and pk_stordoc in (select pk_stordoc from bd_stordoc where pk_org = '");
		queryOnhandSql.append(pk_org + "' and def1 = 'Y')");
		/* 总现存量 */
		UFDouble onhandnum = UFDouble.ZERO_DBL;
		try {
			List<Object[]> result = (List<Object[]>) new BaseDAO()
					.executeQuery(queryOnhandSql.toString(),
							new ArrayListProcessor());
			for (Object[] array : result) {
				if (array != null && array.length > 0) {
					for (Object obj : array) {
						if (obj != null) {
							onhandnum = new UFDouble(
									result.get(0)[0].toString());
						}
					}
				}
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExceptionUtils.marsh(e);
		}

		return onhandnum;
	}

	private Map<String, UFDouble> queryOnhandNum(String[] pk_orgs,
			String[] pk_materials) throws BusinessException {
		Map<String, UFDouble> onhandMap = new HashMap();
		String[] groupfields = { "pk_org", "cmaterialoid", "cmaterialvid" };

		OnhandVO[] handvos = queryOnHandNum(pk_orgs, pk_materials, groupfields);

		if ((null == handvos) || (handvos.length == 0)) {
			return onhandMap;
		}
		for (OnhandVO vo : handvos) {
			String key = vo.getPk_org() + vo.getCmaterialoid();
			UFDouble onhandNum = null == onhandMap.get(key) ? UFDouble.ZERO_DBL
					: (UFDouble) onhandMap.get(key);

			onhandNum = MathTool.add(onhandNum, vo.getNonhandnum());
			/* XBX 按组织、物料、平衡仓库取总现存量 */
			if (ifBalaByOrg(vo.getPk_org())) {
				onhandNum = getNonhandnum(vo.getPk_org(), vo.getCmaterialoid());
			}
			onhandMap.put(key, onhandNum);
		}
		return onhandMap;
	}

	private OnhandVO[] queryOnHandNum(String[] pk_org, String[] cmaterialoid,
			String[] groupfields) {

		try {
			IOnhandQry srv = (IOnhandQry) NCLocator.getInstance().lookup(
					IOnhandQry.class);
			return srv.queryOnhandForInvp(pk_org, cmaterialoid, groupfields);
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);

			InvpLogUtil.logout();
		}
		return null;
	}

	private Map<String, UFDouble> queryStockReqPlanout(String[] pk_orgs,
			String[] pk_Materials) throws BusinessException {
		InvpLogUtil.logout();
		Map<String, UFDouble> reqMap = new HashMap();
		IStorereqQueryForInv9 reqService = (IStorereqQueryForInv9) NCLocator
				.getInstance().lookup(IStorereqQueryForInv9.class);

		reqMap = reqService.getPlanOut(pk_orgs, pk_Materials);
		InvpLogUtil.logout();
		return reqMap;
	}

	private Map<String, UFDouble> queryWorkOrderPlanout(String[] pk_orgs,
			String[] pk_Materials) throws BusinessException {
		InvpLogUtil.logout();
		Map<String, UFDouble> reqMap = new HashMap();
		if (SysInitGroupQuery.isEWMEnabled()) {
			IWorkOrderForInv reqService = (IWorkOrderForInv) NCLocator
					.getInstance().lookup(IWorkOrderForInv.class);

			reqMap = reqService.getReqWorkOrder(pk_orgs, pk_Materials);
		}
		InvpLogUtil.logout();
		return reqMap;
	}

	private void setAtpValue(BalanceResultVO[] treeDatas,
			Map<String, AtpVO> atpMap, Map<String, UFDouble> reqMap,
			Map<String, UFDouble> wkMap, Map<String, UFDouble> safeMap,
			Map<String, String> nextOrgMap, Map<String, UFDouble> onhandMap) {
		for (BalanceResultVO treevo : treeDatas) {
			String key = treevo.getPk_org() + treevo.getCmaterialoid();
			String okey = treevo.getCmaterialoid() + treevo.getPk_org();
			String nextOrg = (String) nextOrgMap.get(okey);

			if ((null != nextOrg)
					&& (treevo.getPk_parentid().equals(this.rootname))
					&& (!treevo.getPk_org().equals(nextOrg))) {
				String nextOrg_v = (String) this.vstockorgsMap.get(nextOrg);
				treevo.setPk_org_next(nextOrg);
				treevo.setPk_org_next_v(nextOrg_v);
			}

			if ((ReqTypeEnum.AFTERBALREQ.integerValue().equals(treevo
					.getFreqtype()))
					&& (treevo.getPk_org_req().equals(treevo.getPk_org()))) {
				if (treevo.getPk_parentid().equals(this.rootname)) {
					treevo.setNlacknum(treevo.getNnum());
					treevo.setNsugpunum(treevo.getNnum());
				}

			} else {
				treevo.setNsafestocknum(UFDouble.ZERO_DBL);
				treevo.setNpurchasenum(UFDouble.ZERO_DBL);
				treevo.setNpraynum(UFDouble.ZERO_DBL);
				treevo.setNtransinnum(UFDouble.ZERO_DBL);
				treevo.setNstockpreparenum(UFDouble.ZERO_DBL);
				treevo.setNtransoutnum(UFDouble.ZERO_DBL);
				treevo.setNstocknum(UFDouble.ZERO_DBL);
				treevo.setNreqnum(UFDouble.ZERO_DBL);
				treevo.setNworknum(UFDouble.ZERO_DBL);

				if ((!onhandMap.isEmpty()) && (null != onhandMap.get(key))) {
					treevo.setNstocknum((UFDouble) onhandMap.get(key));
				}
				if ((!atpMap.isEmpty()) && (null != atpMap.get(key))) {
					AtpVO atpvo = (AtpVO) atpMap.get(key);

					if (UFBoolean.TRUE.equals(this.rulehead.getBpo())) {
						treevo.setNpurchasenum(atpvo.getNonponum() != null ? atpvo
								.getNonponum() : UFDouble.ZERO_DBL);
					}

					if (UFBoolean.TRUE.equals(this.rulehead.getBpr())) {
						treevo.setNpraynum(atpvo.getNonrequirenum() != null ? atpvo
								.getNonrequirenum() : UFDouble.ZERO_DBL);
					}

					if (UFBoolean.TRUE.equals(this.rulehead.getBreqin())) {
						treevo.setNtransinnum(atpvo.getNtraninnum() != null ? atpvo
								.getNtraninnum() : UFDouble.ZERO_DBL);
					}

					if (UFBoolean.TRUE.equals(this.rulehead.getBmmplan())) {
						treevo.setNstockpreparenum(atpvo.getNpickmnum() != null ? atpvo
								.getNpickmnum() : UFDouble.ZERO_DBL);
					}

					if (UFBoolean.TRUE.equals(this.rulehead.getBreqout())) {
						treevo.setNtransoutnum(atpvo.getNtranoutnum() != null ? atpvo
								.getNtranoutnum() : UFDouble.ZERO_DBL);
					}
				}

				if ((!reqMap.isEmpty()) && (null != reqMap.get(key))) {
					UFDouble reqnum = (UFDouble) reqMap.get(key);
					treevo.setNreqnum(reqnum != null ? reqnum
							: UFDouble.ZERO_DBL);
				}

				if ((!wkMap.isEmpty()) && (null != wkMap.get(key))) {
					UFDouble worknum = (UFDouble) wkMap.get(key);
					treevo.setNworknum(worknum != null ? worknum
							: UFDouble.ZERO_DBL);
				}

				if ((!safeMap.isEmpty()) && (null != safeMap.get(key))) {
					UFDouble safenum = (UFDouble) safeMap.get(key);
					treevo.setNsafestocknum(safenum != null ? safenum
							: UFDouble.ZERO_DBL);
				}

				if ((null != treevo.getPk_balance_result())
						&& (treevo.getPk_parentid().equals(this.rootname))) {

					calNum(treevo);
				}
			}
		}
	}
}
