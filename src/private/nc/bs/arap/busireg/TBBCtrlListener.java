package nc.bs.arap.busireg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.arap.paybp.PayBillDAO;
import nc.bs.arap.tbb.TbbCtrlBO;
import nc.bs.businessevent.BdUpdateEvent;
import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.itf.uap.sf.ICreateCorpQueryService;
import nc.itf.uap.sf.IProductVersionQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.ps.paybill.PayBillOpEnum;
import nc.pub.cmp.apply.ApplyOpEnum;
import nc.pubitf.cmp.apply.IApplyPubService;
import nc.pubitf.ps.IPlanPubService;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.basebill.BaseItemVO;
import nc.vo.arap.event.IArapBSEventType;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pub.ArapConstant;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.install.ProductVersionVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * 预算控制类
 * 说明：
 */
public class TBBCtrlListener  implements IBusinessListener {

	protected AggregatedValueObject[] getOriginBill(AggregatedValueObject[] bills) throws BusinessException {
		String[] pks = new String[bills.length];
		for (int index = 0; index < pks.length; index++) {
			pks[index] = bills[index].getParentVO().getPrimaryKey();
		}
		Collection<?> vo = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(bills[0].getClass(),
						pks, false);
		return vo.toArray(new AggregatedValueObject[0]);
	}
	public void doAction(final IBusinessEvent event) throws BusinessException {
		if(! (IArapBSEventType.TYPE_INSERT_BEFORE.equals(event.getEventType()) ||
				IArapBSEventType.TYPE_EFFECTION_AFTER.equals(event.getEventType()) ||
				IArapBSEventType.TYPE_UNEFFECTION_BEFORE.equals(event.getEventType()) ||
				IArapBSEventType.TYPE_DELETE_AFTER.equals(event.getEventType())||
				IArapBSEventType.TYPE_UPDATE_BEFORE.equals(event.getEventType()))){
			return;
		}

		AggregatedValueObject[] obills = this.getBills(event);
		if (ArrayUtils.isEmpty(obills)) {
			return;
		}
		boolean isCmpEnable = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).isEnabled(
				(String) obills[0].getParentVO().getAttributeValue(IBillFieldGet.PK_GROUP),
				IBillFieldGet.CMP);
		if(isCmpEnable){
			ApplyOpEnum add = null;
			if(IArapBSEventType.TYPE_INSERT_BEFORE.equals(event.getEventType())){
				add=ApplyOpEnum.add;
			}else if(IArapBSEventType.TYPE_EFFECTION_AFTER.equals(event.getEventType())){
				add=ApplyOpEnum.approve;
			}else if(IArapBSEventType.TYPE_UNEFFECTION_BEFORE.equals(event.getEventType())){
				add=ApplyOpEnum.unapprove;
			}else if(IArapBSEventType.TYPE_DELETE_AFTER.equals(event.getEventType())){
				add=ApplyOpEnum.delete;
			}else if(IArapBSEventType.TYPE_UPDATE_BEFORE.equals(event.getEventType())){
				add=ApplyOpEnum.add;
				do36D1(ApplyOpEnum.delete, getOriginBill(obills));
			}
			do36D1(add, obills);
			
			PayBillOpEnum add2 = null;
			if(IArapBSEventType.TYPE_INSERT_BEFORE.equals(event.getEventType())){
				add2=PayBillOpEnum.add;
			}else if(IArapBSEventType.TYPE_EFFECTION_AFTER.equals(event.getEventType())){
				add2=PayBillOpEnum.approve;
			}else if(IArapBSEventType.TYPE_UNEFFECTION_BEFORE.equals(event.getEventType())){
				add2=PayBillOpEnum.unapprove;
			}else if(IArapBSEventType.TYPE_DELETE_AFTER.equals(event.getEventType())){
				add2=PayBillOpEnum.delete;
			}else if(IArapBSEventType.TYPE_UPDATE_BEFORE.equals(event.getEventType())){
				add2=PayBillOpEnum.add;
				do36D7(PayBillOpEnum.delete, getOriginBill(obills));
			}
			do36D7(add2, obills);
		}

		ProductVersionVO[] ProductVersionVOs = NCLocator.getInstance().lookup(
				IProductVersionQueryService.class).queryByProductCode(ArapConstant.TBB_MODULE_ID);
		if (ProductVersionVOs == null || ProductVersionVOs.length == 0) {
			return;
		}

		String actionType = getActionTypeByEventType(event.getEventType());

		// 预算控制接口
		TbbCtrlBO tbbCtrlBO = new TbbCtrlBO();
		
		Map<String ,List<AggregatedValueObject> > orgMap =new HashMap<String, List<AggregatedValueObject>>();
		for(AggregatedValueObject destVo : obills){
			String pk_org = ((BaseAggVO) destVo).getHeadVO().getPk_org();
			if(orgMap.get(pk_org)==null){
				orgMap.put(pk_org, new ArrayList<AggregatedValueObject>());
			}
			orgMap.get(pk_org).add(destVo);
		}
		//根据pk_org批量处理调用预算
		for(String pk_org : orgMap.keySet()){
			tbbCtrlBO.budgetControl(orgMap.get(pk_org).toArray(new BaseAggVO[]{}), actionType, true);
		}
	}

	public static Map<String, UFDouble> get36D1Moneymap(ApplyOpEnum add,
			AggregatedValueObject[] obills) throws BusinessException {
		List<String> F3itemsList=new ArrayList<String>();
		Map<String, UFDouble> moneyMap = new HashMap<String, UFDouble>();
		Map<String, UFDouble> redMap = new HashMap<String, UFDouble>();
		getMoneyMap(obills, F3itemsList, moneyMap, redMap,"36D1",add,null);
		try {
			AggPayBillVO[] vos= new PayBillDAO().queryBillsBySourceRowIDs(F3itemsList.toArray(new String[]{}),(String) obills[0].getParentVO().getAttributeValue("pk_org"));
			getMoneyMap(vos, F3itemsList, moneyMap, redMap,"36D1",add,null);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		return moneyMap;
	}
	
	public static void do36D1(ApplyOpEnum add,
			AggregatedValueObject[] obills) throws BusinessException {
		Map<String, UFDouble> moneyMap = get36D1Moneymap(add,obills);
		if(moneyMap.size()!=0){
			NCLocator.getInstance().lookup(IApplyPubService.class).updateApplyMoney(moneyMap, add);
		}
	}
	
	

	public static Map<String, UFDouble> get36D7Moneymap(PayBillOpEnum add,
			AggregatedValueObject[] obills) throws BusinessException {
		List<String> F3itemsList=new ArrayList<String>();
		Map<String, UFDouble> moneyMap = new HashMap<String, UFDouble>();
		Map<String, UFDouble> redMap = new HashMap<String, UFDouble>();
		getMoneyMap(obills, F3itemsList, moneyMap, redMap,"36D7",null,add);
		try {
			AggPayBillVO[] vos= new PayBillDAO().queryBillsBySourceRowIDs(F3itemsList.toArray(new String[]{}),(String) obills[0].getParentVO().getAttributeValue("pk_org"));
			getMoneyMap(vos, F3itemsList, moneyMap, redMap,"36D7",null,add);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		return moneyMap;
	}
	public static void do36D7(PayBillOpEnum add,
			AggregatedValueObject[] obills) throws BusinessException {
		Map<String, UFDouble> moneyMap = get36D7Moneymap(add,obills);
		if(moneyMap.size()!=0){
			NCLocator.getInstance().lookup(IPlanPubService.class).updateApplyPlanMoney(moneyMap, add);
		}
	}
	
	private static void getMoneyMap(AggregatedValueObject[] obills,
			List<String> F3itemsList, Map<String, UFDouble> moneyMap, Map<String, UFDouble> redMap,String pk_billtype, ApplyOpEnum app, PayBillOpEnum ppp) {
		for(AggregatedValueObject destVo : obills){
			BaseAggVO aggvo = (BaseAggVO) destVo;
			BaseBillVO vo = aggvo.getHeadVO();
			String billclass = vo.getBillclass();
			if(!billclass.equals("fk")){
				continue;
			}
			for(BaseItemVO item:aggvo.getItems()){
				String topBilltype = item.getTop_billtype();
				
				if((app!=null && (app.equals(ApplyOpEnum.approve)||app.equals(ApplyOpEnum.unapprove)))
						|| 
					(ppp!=null && (ppp.equals(PayBillOpEnum.approve)||ppp.equals(PayBillOpEnum.unapprove)))){
					if(item.getIsdiscount()==null) {
						continue;
					}
				}
				if((app!=null && (app.equals(ApplyOpEnum.settle)||app.equals(ApplyOpEnum.unsettle)))
						|| 
					(ppp!=null && (ppp.equals(PayBillOpEnum.settle)||ppp.equals(PayBillOpEnum.unsettle)))){
					if(item.getIsdiscount()!=null && UFBoolean.TRUE.equals(item.getIsdiscount())) {
						continue;
					}
				}
				
				if(item.getTop_itemid()!=null){
					if(pk_billtype.equals(topBilltype)){
						String key = item.getTop_itemid();
						UFDouble money2 = item.getMoney_de();
						
						if(redMap.containsKey(item.getPrimaryKey())){
							money2=redMap.get(item.getPrimaryKey());
						}
						
						if(moneyMap.containsKey(key)){
							UFDouble money = moneyMap.get(key);
							moneyMap.put(key, money.add(money2));
						}else{
							moneyMap.put(key, money2);
						}
					}
					if("F3".equals(topBilltype)){
						F3itemsList.add(item.getTop_itemid());
						redMap.put(item.getTop_itemid(), item.getMoney_de());
					}
				}
			}
		}
	}


	protected AggregatedValueObject[] getBills(final IBusinessEvent event) {
		Object object = null;
		if (event instanceof BusinessEvent) {
			BusinessEvent e = (BusinessEvent) event;
			object = e.getObject();
		} else if (event instanceof BdUpdateEvent) {
			BdUpdateEvent e = (BdUpdateEvent) event;
			object = e.getNewObject();
		}

		AggregatedValueObject[] bills = null;
		if (null != object) {
			if (object.getClass().isArray()) {
				bills = (AggregatedValueObject[]) object;
			} else {
				bills = new AggregatedValueObject[1];
				bills[0] = (AggregatedValueObject) object;
			}
		}
		return bills;
	}

	  private String getActionTypeByEventType ( String eventType) {
		  
		  String actionType = null;
		  if(IArapBSEventType.TYPE_INSERT_BEFORE.equals(eventType)) {
			  actionType = ArapConstant.ARAP_NTB_SAVE_KEY;
		  } else if(IArapBSEventType.TYPE_EFFECTION_AFTER.equals(eventType)) {
			  actionType = ArapConstant.ARAP_NTB_EFFECT_KEY;
		  } else if(IArapBSEventType.TYPE_UNEFFECTION_BEFORE.equals(eventType)) {
			  actionType = ArapConstant.ARAP_NTB_UNEFFECT_KEY;
		  } else if(IArapBSEventType.TYPE_DELETE_AFTER.equals(eventType)) {
			  actionType = ArapConstant.ARAP_NTB_DELETE_KEY;
		  } else if(IArapBSEventType.TYPE_UPDATE_BEFORE.equals(eventType)) {
			  actionType = ArapConstant.ARAP_NTB_EDIT_KEY;
		  }
		  return actionType;
	  }

}