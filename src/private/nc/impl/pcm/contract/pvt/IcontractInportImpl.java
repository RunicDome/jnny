package nc.impl.pcm.contract.pvt;

import nc.bp.impl.uap.oid.OidBaseAlgorithm;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.uap.oid.OidGenerator;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.itf.pcm.contract.pvt.IContractImport;
import nc.itf.uap.pf.IPFBusiAction;
import nc.jdbc.framework.MockDataSource;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

@SuppressWarnings("unused")
public class IcontractInportImpl extends BillBaseImpl<ContractBillVO> implements
		IContractImport {

	@Override
	public void insertBillVO(AggregatedValueObject[] qdvos) throws Exception {
		// TODO Auto-generated method stub
		IPFBusiAction ipf = (IPFBusiAction) NCLocator.getInstance().lookup(
				IPFBusiAction.class);
		try {
			
			Object[] ss =  ipf.processBatch("SAVEBASE", "4D42", qdvos, null, null, null);
			String sq = ss[0]+ "";
			System.out.println(sq);
		} catch (Exception e) {
			System.out.println("异常信息："+e.getMessage());
			e.printStackTrace();
			throw new BusinessException("导入失败: "+e.getMessage());
		}
	}

	/*@Override
	public String getPrimaryKey() {
		// TODO Auto-generated method stub
		long OID_BASE_INITIAL_VAL = 19000000000000L;
		//IdGenerator ss = new SequenceGenerator();
		String[] newOids = new String[1];
		if (MockDataSource.isMockDataBase()) {
			for (int i = 0; i < 0; i++) {
				OID_BASE_INITIAL_VAL += 1L;
				newOids[i] = OidBaseAlgorithm.getInstance(
						String.valueOf(OID_BASE_INITIAL_VAL)).nextOidBase();
			}
			return newOids[0];
		}
		String groupNumber = InvocationInfoProxy.getInstance().getGroupNumber();
		if ((groupNumber == null) || (groupNumber.isEmpty())) {
			groupNumber = "001";
		}
		String ds = InvocationInfoProxy.getInstance().getUserDataSource();
		for (int i = 0; i < 0; i++) {
			newOids[i] = OidGenerator.getInstance().nextOid(ds, groupNumber);
		}
		return newOids[0];
	}*/

}
