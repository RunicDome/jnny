package nc.bs.arap.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.arap.bill.ArapBillDAO;
import nc.bs.arap.busireg.TBBCtrlListener;
import nc.bs.dao.BaseDAO;
import nc.bs.logging.Log;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.pub.cmp.apply.ApplyOpEnum;
import nc.vo.arap.basebill.ArapVOFactory;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.basebill.BaseItemVO;
import nc.vo.arap.global.ArapBillVOConsts;
import nc.vo.arap.pub.BillEnumCollection;
import nc.vo.arap.pub.BillEnumCollection.PayStatus;
import nc.vo.arap.pub.BillEnumCollection.SettlementState;
import nc.vo.arap.pub.BillEnumCollection.SettlementType;
import nc.vo.cmp.CMPExecStatus;
import nc.vo.cmp.NetPayExecInfo;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.billtype.BilltypeVO;
/**
 * 网银支付状态发生了变化，回写单据支付状态
 * @see
 * @author guodw
 * @version V6.0
 * @since V6.0 创建时间：2009-7-15 下午03:11:40
 */
public class NetPayBSAction {

	private NetPayExecInfo payInfo = null;
	private BaseDAO baseDao = null;


	protected AggregatedValueObject doBusiness(AggregatedValueObject bill)
			throws BusinessException {
		if(null == bill)return null;
		BaseBillVO parent = (BaseBillVO) bill.getParentVO();
		BaseItemVO[] children = (BaseItemVO[]) bill.getChildrenVO();
		updateBillParentInfo(parent,new String[]{IBillFieldGet.SETTLETYPE,IBillFieldGet.SETTLEFLAG,IBillFieldGet.PAYMAN,IBillFieldGet.PAYDATE});
		updateBillChildrenInfo(children,new String[]{IBillFieldGet.PAYFLAG,IBillFieldGet.PAYMAN,IBillFieldGet.PAYDATE});
		return bill;
	}
	/**
	 * 根据支付信息创建主表VO
	 * @param isSucc
	 * @param isFail
	 * @param isGoing
	 * @param isPart
	 * @return
	 * @see
	 * @since V6.0
	 */
	private BaseBillVO getParentInstance(){

		BaseBillVO parent = ArapVOFactory.getParentVOInstance(payInfo.getBilltype());
		parent.setSettletype(ArapBillVOConsts.m_intJSZXZF_NetBank);
		parent.setPrimaryKey(payInfo.getBillid());
		parent.setPaydate(payInfo.getOperateDate());
		parent.setPayman(payInfo.getOperator());
		parent.setSettlenum(payInfo.getSettleno());
		Map<String, CMPExecStatus> map = payInfo.getExecStatusMap();


		int settleState =SettlementState.DEFAULT.VALUE;
		CMPExecStatus cestatus = map.get(parent.getPrimaryKey());

		if(CMPExecStatus.PayFail.equals(cestatus)){
			settleState =SettlementState.TRANSFAIL.VALUE;
		}else if(CMPExecStatus.SomePayFinish.equals(cestatus)){
			settleState =SettlementState.SomePayFinish.VALUE;
		}else if(CMPExecStatus.Paying.equals(cestatus)){
			settleState =SettlementState.TRANSING.VALUE;
		}else if(CMPExecStatus.UNPayed.equals(cestatus)){
			settleState =SettlementState.DEFAULT.VALUE;
		}else if(CMPExecStatus.PayFinish.equals(cestatus)){
			settleState =SettlementState.TRANSSUCCED.VALUE;
		}else if(CMPExecStatus.Reciving.equals(cestatus)){
			settleState =SettlementState.TRANSING.VALUE;
		}else if(CMPExecStatus.ReciveFinish.equals(cestatus)){
			settleState =SettlementState.TRANSSUCCED.VALUE;
		}else {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("20062006v61013_0","020062006v61013-0003")/*@res "支付状态不对"*/+cestatus);
		}
		parent.setSettleflag(settleState);
		parent.setSettletype(SettlementType.NETBANK.VALUE);
		

		return parent;
	}

	/**
	 * 根据支付信息创建子表VO
	 * @param isSucc
	 * @param isFail
	 * @param isGoing
	 * @param isPart
	 * @return
	 * @see
	 * @since V6.0
	 */
	private BaseItemVO[] getChildrenInstance (){

		List<BaseItemVO> children = new ArrayList<BaseItemVO>();
		Map<String, CMPExecStatus> map = payInfo.getExecStatusMap();
		for(String key : map.keySet()){
			Log.getInstance(this.getClass()).debug(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0297")/*@res "debug#####################LIAOBX#############################单据类型"*/+payInfo.getBilltype(),getClass(),"getChildrenInstance");
			BaseItemVO item = ArapVOFactory.getChildrenVOInstance(payInfo.getBilltype());
			item.setPrimaryKey(key);
			item.setPayflag(PayStatus.valueof(map.get(key).getStatus()).VALUE);
			item.setParentPK(payInfo.getBillid());
			item.setPaydate(payInfo.getOperateDate());
			item.setPayman(payInfo.getOperator());
			children.add(item);
		}

		return (BaseItemVO[])children.toArray(new BaseItemVO[0]);
	}

	/**
	 * 处理网银支付状态
	 * @param payInfo
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	public void doNetPay(NetPayExecInfo payInfo) throws BusinessException {

		this.payInfo = payInfo;
		BaseBillVO parent = getParentInstance();
		BaseItemVO[] children = getChildrenInstance();
		BaseAggVO vo = ArapVOFactory.getAggVOInstance(payInfo.getBilltype());
		vo.setParentVO(parent);
		vo.setChildrenVO(children);
		doBusiness(vo);
		BilltypeVO billTypevo = PfDataCache.getBillType(payInfo.getBilltype());
		BaseAggVO updatedVO = new ArapBillDAO().queryBillByPrimaryKey(parent.getPrimaryKey(), billTypevo.getParentbilltype());
		BaseBillVO parentEffect = (BaseBillVO) updatedVO.getParentVO();
		
		
		Integer settleflag = parentEffect.getSettleflag();
		Integer effectstatus = parentEffect.getEffectstatus();
		if((SettlementState.SomePayFinish.VALUE.equals(settleflag) || SettlementState.TRANSSUCCED.VALUE.equals(settleflag))&& !BillEnumCollection.InureSign.OKINURE.VALUE.equals(effectstatus) ){
			parentEffect.setEffectstatus(BillEnumCollection.InureSign.OKINURE.VALUE);
			parentEffect.setEffectuser(this.payInfo.getOperator());
			parentEffect.setEffectdate(this.payInfo.getOperateDate());
			new BillEffectBatchBSAction().updateVO(new AggregatedValueObject[] { updatedVO });
		}
		
		// BEGIN_TM_WANGJIAS_20190510_网银支付单据不回写付款申请金额
		if (SettlementState.TRANSSUCCED.VALUE.equals(settleflag)) {
			TBBCtrlListener.do36D1(ApplyOpEnum.settle, new AggregatedValueObject[] { updatedVO });
		}
		// END_TM_WANGJIAS_20190510

		// ---------------------部分成功生成红冲单-------------------------------------------//
		// 没用
		generateWriteBack(payInfo);
	}
	private void generateWriteBack(NetPayExecInfo payInfo) {
		List<String> writeBackItemPKs = new ArrayList<String>();
		Map<String, CMPExecStatus> map = payInfo.getExecStatusMap();
		for(String key : map.keySet()){
			if((PayStatus.valueof(map.get(key).getStatus()).VALUE).equals(PayStatus.SomePayFinish.VALUE)){
				writeBackItemPKs.add(key);
			}
		}	
	}
	/**
	 * 更新单据主表信息
	 * @param parent
	 * @param fieldNames
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	private void updateBillParentInfo(BaseBillVO parent,String[] fieldNames) throws BusinessException{
		if(null == parent)
			return ;
		 getBaseDao().updateVO(parent, fieldNames);
	}
	/**
	 * 更新单据子表信息
	 * @param childrens
	 * @param fieldNames
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	private void updateBillChildrenInfo(BaseItemVO[] childrens ,String[] fieldNames) throws BusinessException{
		if(null == childrens || childrens.length < 0)
			return;
		getBaseDao().updateVOArray(childrens, fieldNames);
	}


	private BaseDAO getBaseDao(){
		if(null == baseDao){
			baseDao = new BaseDAO();
		}
		return baseDao;
	}
	
	
}