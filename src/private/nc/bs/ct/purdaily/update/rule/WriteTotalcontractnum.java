package nc.bs.ct.purdaily.update.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.dao.BaseDAO;
import nc.bs.logging.Logger;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.data.vo.VOUpdate;
import nc.impl.pubapp.pattern.rule.ICompareRule;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.pu.m20.entity.PraybillItemVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.pub.MathTool;

/**
 * 采购合同更新时回写请购单表体"累计合同主数量"
 * 
 * @param 无
 * @author yechd5
 * @time 2017-8-21 下午15:01:29
 */
public class WriteTotalcontractnum implements ICompareRule<AggCtPuVO> {

	// 交易类型编码
	List<String> listtrantypeid = new ArrayList<String>();
	// <交易类型编码,是否控制合同数量>
	Map<String, String> transtypeInfo = new HashMap<String, String>();

	@Override
	public void process(AggCtPuVO[] vos, AggCtPuVO[] originVOs) {

		transtypeInfo.clear();
		listtrantypeid.clear();

		// 请购单行id,原先合同行主数量
		Map<String, UFDouble> originBodyInfo = new HashMap<String, UFDouble>();
		// 构造合同行<来源请购单行id，来源单据交易类型（编码）>
		Map<String, String> bidAndTranstype = new HashMap<String, String>();

		// 请购单->采购合同主键对照
		Map<String, String> qghtmap = new HashMap<String, String>();
		// 1.获取修改前主数量信息
		for (AggCtPuVO vo : originVOs) {
			CtPuBVO[] bvos = vo.getCtPuBVO();
			for (CtPuBVO bvo : bvos) {
				String pk_praybill_b = bvo.getPk_praybill_b();// 请购单行主键
				UFDouble nnum = bvo.getNnum();// 本次合同行主数量
				if (null == pk_praybill_b) {
					continue;
				} else {
					originBodyInfo.put(pk_praybill_b, nnum);
					qghtmap.put(pk_praybill_b, bvo.getPrimaryKey());
				}
			}

		}

		// 2.过滤合同行是来源于请购单
		for (AggCtPuVO vo : vos) {
			CtPuBVO[] bvos = vo.getCtPuBVO();
			// 请购单行id,合同行主数量
			Map<String, UFDouble> willupdateBodyInfo = new HashMap<String, UFDouble>();
			// 合同行请购单行id,[合同行号,请购单行号]
			Map<String, String[]> rowInfo = new HashMap<String, String[]>();
			for (CtPuBVO bvo : bvos) {
				String crowno = bvo.getCrowno();// 合同行号
				String praybodyno = bvo.getCpraybillrowno();// 请购单行号
				String pk_praybill_b = bvo.getPk_praybill_b();// 请购单行主键
				UFDouble nnum = bvo.getNnum();// 本次合同行主数量
				if (null == pk_praybill_b) {
					continue;
				} else {
					willupdateBodyInfo.put(pk_praybill_b, nnum);
					// 合同行号,请购单行号
					String[] rowNOInfo = new String[] { crowno, praybodyno };
					rowInfo.put(pk_praybill_b, rowNOInfo);
				}
			}
			if (null != willupdateBodyInfo && willupdateBodyInfo.size() > 0) {
				// 3.开始更新/回写（先查询请购单行VO，再更新）
				try {
					this.updateTotalContNum(willupdateBodyInfo, originBodyInfo,
							rowInfo, bidAndTranstype,qghtmap);
				} catch (BusinessException e) {
					// TODO Auto-generated catch block
					Logger.error("查询请购单采购合同视图出错："+e.getMessage());
					e.printStackTrace();
				}
			} else {
				continue;
			}
		}
		
	}

	

	/**
	 * 根据合同行上的请购单行id更新请购单表体的累计合同主数量
	 * @param qghtmap 
	 * 
	 * @param contBodyInfo
	 * @throws BusinessException 
	 */
	private void updateTotalContNum(Map<String, UFDouble> willupdateBodyInfo,
			Map<String, UFDouble> originBodyInfo,
			Map<String, String[]> rowInfo, Map<String, String> bidAndTranstype, Map<String, String> qghtmap) throws BusinessException {
		List<String> praybillBids = new ArrayList<String>();
		// step1:取出所有的请购单行id(willupdateBodyInfo改成originBodyInfo)--add
		for (Map.Entry<String, UFDouble> entry : originBodyInfo.entrySet()) {
			praybillBids.add(entry.getKey());
		}

		// step2.根据请购单行id查询请购单行VO
		VOQuery<PraybillItemVO> query = new VOQuery<PraybillItemVO>(
				PraybillItemVO.class, new String[] {
						PraybillItemVO.PK_PRAYBILL_B,
						PraybillItemVO.TOTALCONTRACTNUM, PraybillItemVO.NNUM });
		PraybillItemVO[] prayBVOs = query.query(praybillBids
				.toArray(new String[0]));

		Map<String, UFDouble[]> map = new HashMap<String, UFDouble[]>();
		if (null != prayBVOs && prayBVOs.length > 0) {
			for (PraybillItemVO bvo : prayBVOs) {
				// 参数1表示累计合同主数量，参数2表示请购单行主数量
				UFDouble[] nums = { new UFDouble(bvo.getTotalcontractnum()), bvo.getNnum() };
				map.put(bvo.getPk_praybill_b(), nums);
			}
		} else {
			return;
		}

		// step3.更新请购单行VO
		List<PraybillItemVO> updatevos = new ArrayList<PraybillItemVO>();
		for (Map.Entry<String, UFDouble[]> entry : map.entrySet()) {
			UFDouble historynum = entry.getValue()[0];// 已累计合同数量——请购单表体
			UFDouble praybillNnum = entry.getValue()[1];// 请购单行主数量——请购单表体
			UFDouble originnum = originBodyInfo.get(entry.getKey());// 旧值
			UFDouble currentnum = willupdateBodyInfo.get(entry.getKey());// 修改后的本次合同主数量（新值）
			//currentnum为空，意味着这行被删行了--add
			if(currentnum==null) {
				currentnum=UFDouble.ZERO_DBL;
			}
			UFDouble diff = MathTool.sub(currentnum, originnum);// 新值—旧值
			// 获取请购单交易类型是否勾选“控制累计合同主数量”
//			String flag = transtypeInfo.get(bidAndTranstype.get(entry.getKey()));
			// XBX修改查询累计合同主数量
			String htmxpk = null;
			if(qghtmap.get(entry.getKey()) != null){
				htmxpk = qghtmap.get(entry.getKey());
			}
			UFDouble totalNum = getTotalNumByMX(htmxpk,entry.getKey());
//			UFDouble total = MathTool.add(historynum, diff);// 修改后的累计合同主数量
			UFDouble total = MathTool.add(totalNum, currentnum);// XBX修改后的累计合同主数量
			if (MathTool.compareTo(total, praybillNnum) > 0 
//					&& flag.equals("Y")
					) {
				ExceptionUtils.wrappBusinessException("修改后的合同第【"
						+ rowInfo.get(entry.getKey())[0] + "】行存在问题：其来源请购单第【"
						+ rowInfo.get(entry.getKey())[1]
						+ "】行的累计合同主数量超过该请购单行的主数量！");

			}

			PraybillItemVO bvo = new PraybillItemVO();
			bvo.setPk_praybill_b(entry.getKey());
			bvo.setTotalcontractnum(total);
			updatevos.add(bvo);
		}
		VOUpdate<PraybillItemVO> update = new VOUpdate<PraybillItemVO>();
		update.update(updatevos.toArray(new PraybillItemVO[0]),
				new String[] { PraybillItemVO.TOTALCONTRACTNUM });
	}

	/**
	 * 获取<交易类型编码，是否控制合同数量>
	 * 
	 * @param ctrantypeid
	 *            请购单交易类型id
	 * @return map
	 */
//	private Map<String, String> qryTranstypeInfo(String[] ctrantypeids) {
//		String pk_group = AppContext.getInstance().getPkGroup();
//		StringBuilder sql = new StringBuilder();
//		sql.append("select ctrantypeid ,iscontrocontract from po_buyrtrantype where dr=0 and pk_group = '");
//		sql.append(pk_group + "'");
//		sql.append(" and ctrantypeid in(");
//		for (String id : ctrantypeids) {
//			sql.append("'");
//			sql.append(id);
//			sql.append("',");
//		}
//		sql.deleteCharAt(sql.length() - 1);
//		sql.append(")");
//
//		DataAccessUtils dao = new DataAccessUtils();
//		IRowSet rowset = dao.query(sql.toString());
//		Map<String, String> map = new HashMap<String, String>();
//		while (rowset.next()) {
//			map.put(rowset.getString(0), rowset.getString(1));
//		}
//		return map;
//	}
	// 查询请购单明细当前累计合同数量，不包含本次合同
	private UFDouble getTotalNumByMX(String pk_ct_pu_b,String pk_praybill_b) throws BusinessException {
		UFDouble totalNum = UFDouble.ZERO_DBL;
		// 查询视图
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(nnum) nnum from v_qg_cg where 1 = 1 ");
		if(StringUtils.isNotEmpty(pk_ct_pu_b)){
			sql.append(" and pk_ct_pu_b <> '"+pk_ct_pu_b+"' ");
		}
		if(StringUtils.isNotEmpty(pk_praybill_b)){
			sql.append(" and pk_praybill_b = '"+pk_praybill_b+"' ");
		}
		List<Object[]> result = (List<Object[]>) new BaseDAO().executeQuery(sql.toString(),new ArrayListProcessor());
		if (result != null && result.size() > 0) {
			for (Object[] r : result) {
				if(r[0] != null){
					totalNum = new UFDouble(r[0].toString());
				}
			}
		}
		return totalNum;
	}	
	
}
