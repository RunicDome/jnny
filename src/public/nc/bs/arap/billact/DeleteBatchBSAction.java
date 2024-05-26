package nc.bs.arap.billact;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.framework.common.NCLocator;
import nc.data.param.CommonParam;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IBean;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

// ARAP调用东港税务批量删除
public abstract class DeleteBatchBSAction extends BillBaseBSAction {
	public DeleteBatchBSAction() {
	}

	public void deleteVOs(AggregatedValueObject[] bills)
			throws BusinessException {
		if (null == bills) {
			throw new BusinessException("parameter is null!");
		}

		long t1 = System.currentTimeMillis();
		setbillsLock(bills);
		LogTime.debug("单据加动态锁", t1);

		t1 = System.currentTimeMillis();
		checkBillsTs(bills);
		LogTime.debug("单据校验TS", t1);

		IBean bean = MDBaseQueryFacade.getInstance().getBeanByFullClassName(
				bills[0].getClass().getName());

		String id = bean.getID();
		this.tmpMap.put("beanid", id);

		t1 = System.currentTimeMillis();
		setBillStatus(bills);
		LogTime.debug("设置单据状态", t1);

		t1 = System.currentTimeMillis();
		doBeforeDelete(bills);
		LogTime.debug("删除前处理", t1);

		t1 = System.currentTimeMillis();
		validateWithPermission(bills);
		LogTime.debug("单据校验", t1);

		t1 = System.currentTimeMillis();
		doBeforeFireEvent(bills);
		LogTime.debug("单据删除前通知监听者", t1);

		t1 = System.currentTimeMillis();
		doDelete(bills);
		LogTime.debug("在数据库中删除单据", t1);

		t1 = System.currentTimeMillis();
		doAfterDelete(bills);
		LogTime.debug("单据删除后处理", t1);

		t1 = System.currentTimeMillis();
		doAfterFireEvent(bills);
		LogTime.debug("单据删除后通知监听者", t1);

		t1 = System.currentTimeMillis();
		returnbillsCode(bills);
		LogTime.debug("回收单据号", t1);

		for (AggregatedValueObject vo : bills) {
			// 如果为ARAP单据则调用东港税务接口
			String pk_billtype = (String) vo.getParentVO().getAttributeValue(
					"pk_billtype");
			if (StringUtils.isNotEmpty(pk_billtype)
					&& ("F0".equals(pk_billtype) || "F1".equals(pk_billtype)
							|| "F2".equals(pk_billtype) || "F3"
								.equals(pk_billtype))) {
				// 调用东港税务系统同步单据状态
				IArapForDGSWService util = (IArapForDGSWService) NCLocator.getInstance().lookup(
						IArapForDGSWService.class);
				BaseBillVO hvo = (BaseBillVO) vo.getParentVO();
				JSONObject res = util.sendBillByNCBill(hvo.getPrimaryKey(),
						hvo.getPk_org(), CommonParam.DELETE,
						hvo.getPk_billtype());
				if (!"Y".equals(res.getString("success"))) {
					throw new BusinessException(res.getString("errinfo"));
				}
			}
		}
		this.tmpMap.clear();
	}

	protected abstract void doAfterDelete(
			AggregatedValueObject[] paramArrayOfAggregatedValueObject)
			throws BusinessException;

	protected void doAfterFireEvent(AggregatedValueObject[] bills)
			throws BusinessException {
		EventDispatcher.fireEvent(new BusinessEvent((String) this.tmpMap
				.get("beanid"), "1006", bills));
	}

	protected abstract void doBeforeDelete(
			AggregatedValueObject[] paramArrayOfAggregatedValueObject)
			throws BusinessException;

	protected void doBeforeFireEvent(AggregatedValueObject[] bills)
			throws BusinessException {
		EventDispatcher.fireEvent(new BusinessEvent((String) this.tmpMap
				.get("beanid"), "1005", bills));
	}

	protected void doDelete(AggregatedValueObject[] bills)
			throws BusinessException {
		if ((bills instanceof IBill[])) {
			getBilloperator().delete((IBill[]) bills);
		} else if ((null != bills) && (bills.length > 0)
				&& ((bills[0] instanceof IBill))) {
			IBill[] ibills = new IBill[bills.length];
			System.arraycopy(bills, 0, ibills, 0, bills.length);

			getBilloperator().delete(ibills);
		}
	}

	protected void returnbillsCode(AggregatedValueObject[] bills)
			throws BusinessException {
	}

	protected void setBillStatus(AggregatedValueObject[] bills)
			throws BusinessException {
		for (AggregatedValueObject bill : bills) {
			bill.getParentVO().setStatus(3);
		}
	}
}
