package nc.ui.pcm.feebalance.ref;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import nc.bs.framework.common.NCLocator;
import nc.itf.ct.purdaily.IPurdailyMaintain;
import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.ct.purdaily.entity.CtPubillViewVO;
import nc.vo.ct.uitl.ArrayUtil;
import nc.vo.ct.uitl.StringUtil;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.pubapp.scale.ScaleUtils;

public class FeeBalanceHeadForCtPuRefModel extends AbstractRefModel {
	private static final long serialVersionUID = -4564062033976587230L;
	private String[] m_sFieldCodes = {"balance_date", "money","def17","def18","pk_supplier","def14"};

	private String[] m_sFieldNames = {"单据日期","中标金额","招标文号","招标名称","中标单位","招标方式"};

	public FeeBalanceHeadForCtPuRefModel() {
		setPKMatch(true);
	}

	public Vector<Vector<Object>> getData() {
		Vector<Vector<Object>> vector = super.getData();
		if (vector.size() < 1) {
			return vector;
		}
		Vector<Object> firstObject = (Vector) vector.firstElement();
		String pk_group = firstObject.get(15).toString();
		ScaleUtils scale = new ScaleUtils(pk_group);

		Map<String, CtPubillViewVO> paramViewMap = getParamViewMap(vector);

		Map<String, CtPubillViewVO> refViewMap = queryMutiPriceViewMap(paramViewMap);

		for (Vector<Object> vrow : vector) {
			CtPubillViewVO view = (CtPubillViewVO) refViewMap.get(vrow.get(13));
			if (null != view) {

				UFDouble norigprice = scale.adjustSoPuPriceScale(
						view.getNorigprice(), "corigcurrencyid");

				UFDouble norigtaxprice = scale.adjustSoPuPriceScale(
						view.getNorigtaxprice(), "corigcurrencyid");

				UFDouble nqtorigprice = scale.adjustSoPuPriceScale(
						view.getNqtorigprice(), "corigcurrencyid");

				UFDouble nqtorigtaxprice = scale.adjustSoPuPriceScale(
						view.getNqtorigtaxprice(), "corigcurrencyid");

				vrow.set(8, nqtorigprice);
				vrow.set(9, nqtorigtaxprice);
				vrow.set(10, norigprice);
				vrow.set(11, norigtaxprice);
			}
		}
		return vector;
	}

	public int getDefaultFieldCount() {
		return this.m_sFieldNames.length;
	}

	public String[] getFieldCode() {
		return this.m_sFieldCodes;
	}

	public String[] getFieldName() {
		return this.m_sFieldNames;
	}

	public String getPkFieldCode() {
		return "pk_feebalance";
	}

	public String[] getPkValues() {
		Object[] oDatas = getValues(getPkFieldCode());
		String[] sDatas = objs2Strs(oDatas);

		return dealRepeatValues(sDatas);
	}

	public String getRefTitle() {
		return "采购招标结果登记";
	}

	public String getTableName() {
		SqlBuilder fromsql = new SqlBuilder();
		fromsql.append("pm_feebalance");
		return fromsql.toString();
	}

	public String getWherePart() {
//		a.pk_group= '" + super.getPk_group() + "' ");
//		addWherePart.append(" and a.dr = 0 and b.dr = 0");
//		addWherePart.append(" and a.bbracketorder = 'N'");
//		if (StringUtil.isEmptyTrimSpace(super.getWherePart())) {
//			return addWherePart.toString();
//		}
		return "";
	}

	public boolean isMatchPkWithWherePart() {
		return true;
	}

	private String[] dealRepeatValues(String[] datas) {
		if (ArrayUtil.isEmpty(datas)) {
			return null;
		}
		Set<String> set = new HashSet();
		for (String data : datas) {
			set.add(data);
		}
		return (String[]) set.toArray(new String[0]);
	}

	private Map<String, CtPubillViewVO> getParamViewMap(
			Vector<Vector<Object>> vector) {
		Map<String, CtPubillViewVO> paramViewMap = new HashMap();

		String pk_ct_pu_b = null;

		String pk_org = getPara1();

		UFDate date = new UFDate(getPara2());
		for (Vector<Object> vrow : vector) {
			pk_ct_pu_b = vrow.get(13).toString();
			CtPubillViewVO view = new CtPubillViewVO();
			view.setPk_ct_pu_b(pk_ct_pu_b);
			view.setPk_org(pk_org);
			view.setActualvalidate(date);
			paramViewMap.put(pk_ct_pu_b, view);
		}
		return paramViewMap;
	}

	private Map<String, CtPubillViewVO> queryMutiPriceViewMap(
			Map<String, CtPubillViewVO> paramMap) {
		IPurdailyMaintain service = (IPurdailyMaintain) NCLocator.getInstance()
				.lookup(IPurdailyMaintain.class);
		try {
			return service.getMutiPriceViewMap(paramMap);
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}
		return null;
	}
}