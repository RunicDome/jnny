package nc.itf.pcm.contract.pvt;
import nc.vo.pub.AggregatedValueObject;


public interface IContractImport {
	public void insertBillVO(AggregatedValueObject[] qdvos) throws Exception;
	//public String getPrimaryKey();
}
