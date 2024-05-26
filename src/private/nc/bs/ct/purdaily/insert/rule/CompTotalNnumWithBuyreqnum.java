package nc.bs.ct.purdaily.insert.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.pu.m20.entity.PraybillItemVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.pub.MathTool;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.vo.pubapp.pattern.data.IRowSet;

/**
 * 校验累计合同主数量是否超过请购单主数量
 * 
 * @author yechd5
 * @time 2017-8-21 上午10:09:17
 */
public class CompTotalNnumWithBuyreqnum implements IRule<AggCtPuVO> {
	
	// 交易类型编码
	List<String> listtrantypeid = new ArrayList<String>();
	// 请购单行id
	List<String> listpraybill_b = new ArrayList<String>();
	// <交易类型编码,是否控制合同数量>
	Map<String, String> transtypeInfo = new HashMap<String, String>();
	// <请购单行pk,累计合同主数量>
	Map<String, UFDouble[]> totalcontractnum = new HashMap<String, UFDouble[]>();

	@Override
	public void process(AggCtPuVO[] vos) {
		listtrantypeid.clear();
		transtypeInfo.clear();
		listpraybill_b.clear();

		for (AggCtPuVO vo : vos) {
			CtPuBVO[] bvos = vo.getCtPuBVO();
			// 1.获取所有合同行的请购单行id及请购单交易类型
			for (CtPuBVO bvo : bvos) {
				String vrstrantypecode = bvo.getVrstrantypecode();// 来源单据交易类型(编码)
				String pk_praybill_b = bvo.getPk_praybill_b();
				listpraybill_b.add(pk_praybill_b);
				listtrantypeid.add(vrstrantypecode);
			}
			if ((null != listpraybill_b && listpraybill_b.size() > 0)
					&& (null != listtrantypeid && listtrantypeid.size() > 0)) {
				transtypeInfo = this.qryTranstypeInfo(listtrantypeid
						.toArray(new String[0]));
				totalcontractnum = this.qryTotalContNum(listpraybill_b
						.toArray(new String[0]));
			} else {
				continue;
			}

			for (CtPuBVO body : bvos) {
				String pk_praybill_b = body.getPk_praybill_b();// 请购单行主键
				String vpraybillcode = body.getVpraybillcode();// 请购单号
				String prayRowno = body.getCpraybillrowno();// 请购单行号
				UFDouble nnum = body.getNnum();// 合同表体行主数量
				String rowno = body.getCrowno();// 合同行号
				String vrstrantypecode = body.getVrstrantypecode();// 来源单据交易类型(编码)
				if (null == pk_praybill_b || null == vpraybillcode) {
					continue;
				}
				UFDouble total = MathTool.add(
						totalcontractnum.get(pk_praybill_b)[0], nnum);
				String flag = transtypeInfo.get(vrstrantypecode);
				if (MathTool.compareTo(total,
						totalcontractnum.get(pk_praybill_b)[1]) > 0
						&& flag.equals("Y")) {
					ExceptionUtils.wrappBusinessException("合同第【" + rowno
							+ "】行存在问题：其来源请购单行【" + prayRowno
							+ "】的累计合同主数量超过该请购单行的主数量！");
				}
			}
		}
	}

	/**
	 * 获取<交易类型编码，是否控制合同数量>
	 * 
	 * @param ctrantypeid
	 *            请购单交易类型id
	 * @return map
	 */
	private Map<String, String> qryTranstypeInfo(String[] ctrantypeids) {
		String pk_group = AppContext.getInstance().getPkGroup();
		StringBuilder sql = new StringBuilder();
		sql.append("select ctrantypeid ,iscontrocontract from po_buyrtrantype where dr=0 and pk_group = '");
		sql.append(pk_group + "'");
		sql.append(" and ctrantypeid in(");
		for (String id : ctrantypeids) {
			sql.append("'");
			sql.append(id);
			sql.append("',");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");

		DataAccessUtils dao = new DataAccessUtils();
		IRowSet rowset = dao.query(sql.toString());
		Map<String, String> map = new HashMap<String, String>();
		while (rowset.next()) {
			map.put(rowset.getString(0), rowset.getString(1));
		}
		return map;
	}

	/**
	 * 获取<请购单行id，[累计合同主数量,请购主数量]>
	 * 
	 * @param ctrantypeid
	 *            请购单交易类型id
	 * @return map
	 */
	private Map<String, UFDouble[]> qryTotalContNum(String[] pk_praybill_bs) {
		Map<String, UFDouble[]> map = new HashMap<String, UFDouble[]>();
		VOQuery<PraybillItemVO> query = new VOQuery<PraybillItemVO>(
				PraybillItemVO.class, new String[] {
						PraybillItemVO.PK_PRAYBILL_B,
						PraybillItemVO.TOTALCONTRACTNUM, PraybillItemVO.NNUM });
		PraybillItemVO[] prayBVOs = query.query(pk_praybill_bs);
		if (null != prayBVOs && prayBVOs.length > 0) {
			for (PraybillItemVO bvo : prayBVOs) {
				UFDouble[] nums = { bvo.getTotalcontractnum(), bvo.getNnum() };
				map.put(bvo.getPk_praybill_b(), nums);
			}
		} else{
			return null;
		}
		return map;
	}

}