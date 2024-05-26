package nc.bs.ct.purdaily.delete.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.dao.BaseDAO;
import nc.bs.logging.Logger;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.data.vo.VOUpdate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.pu.m20.entity.PraybillItemVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.pub.MathTool;

/**
 * 采购合同删除时回写请购单表体"累计合同主数量"
 * 
 * @param 无
 * @author yechd5
 * @time 2017-8-21 下午15:01:29
 */
public class WriteTotalcontractnum implements IRule<AggCtPuVO> {

	@Override
	public void process(AggCtPuVO[] vos) {
		// 要判断该合同行是否来源于请购单
		for (AggCtPuVO vo : vos) {
			CtPuBVO[] bvos = vo.getCtPuBVO();
			// 请购单行id,合同行主数量
			Map<String, UFDouble> contBodyInfo = new HashMap<String, UFDouble>();
			for (CtPuBVO bvo : bvos) {
				String pk_praybill_b = bvo.getPk_praybill_b();// 请购单行主键
				UFDouble nnum = bvo.getNnum();// 本次合同行主数量
				if (null == pk_praybill_b) {
					continue;
				} else {
					contBodyInfo.put(pk_praybill_b, nnum);
				}
			}
			if (null != contBodyInfo && contBodyInfo.size() > 0) {
				try {
					this.updateTotalContNum(contBodyInfo);
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
	 * 根据请购单行id更新累计合同主数量
	 * 
	 * @param contBodyInfo
	 */
	private void updateTotalContNum(Map<String, UFDouble> contBodyInfo) throws BusinessException {
		List<String> praybillBids = new ArrayList<String>();
		// step1:取出所有的请购单行id
		for (Map.Entry<String, UFDouble> entry : contBodyInfo.entrySet()) {
			praybillBids.add(entry.getKey());
		}

		// step2.根据请购单行id查询请购单行VO
		VOQuery<PraybillItemVO> query = new VOQuery<PraybillItemVO>(
				PraybillItemVO.class, new String[] {
						PraybillItemVO.PK_PRAYBILL_B,
						PraybillItemVO.TOTALCONTRACTNUM });
		PraybillItemVO[] prayBVOs = query.query(praybillBids
				.toArray(new String[0]));

		Map<String, UFDouble> map = new HashMap<String, UFDouble>();
		if (null != prayBVOs && prayBVOs.length > 0) {
			for (PraybillItemVO bvo : prayBVOs) {
				map.put(bvo.getPk_praybill_b(), bvo.getTotalcontractnum());
				// XBX修改逻辑
				// map.put(bvo.getPk_praybill_b(), contBodyInfo.get(bvo.getPk_praybill_b()));
			}
		} else {
			return;
		}
		
		// step3.更新请购单行VO
		List<PraybillItemVO> updatevos = new ArrayList<PraybillItemVO>();
		for (Map.Entry<String, UFDouble> entry : map.entrySet()) {
			// UFDouble historynum = entry.getValue();// 已累计合同数量
			UFDouble currentnum = contBodyInfo.get(entry.getKey());// 本次合同主数量
			
			UFDouble totalNum = getTotalNumByMX(null,entry.getKey());// 查询时已删除
			// UFDouble total = MathTool.sub(totalNum, currentnum);
			PraybillItemVO bvo = new PraybillItemVO();
			bvo.setPk_praybill_b(entry.getKey());
//			bvo.setTotalcontractnum(total);
			bvo.setTotalcontractnum(totalNum);
			updatevos.add(bvo);
		}
		VOUpdate<PraybillItemVO> update = new VOUpdate<PraybillItemVO>();
		update.update(updatevos.toArray(new PraybillItemVO[0]),
				new String[] { PraybillItemVO.TOTALCONTRACTNUM });
	}
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
