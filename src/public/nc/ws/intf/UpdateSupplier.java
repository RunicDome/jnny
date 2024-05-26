package nc.ws.intf;

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import nc.bs.trade.business.HYPubBO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;

public class UpdateSupplier {
	public static String update(Map<String,String> gysmap){
		String pk_supplier = gysmap.get("pk_supplier");// 供应商主键
		String oldsupplier_name = gysmap.get("oldname");// 修改前供应商名称
		String newsupplier_name = gysmap.get("newname");// 修改前供应商名称
		try {
			// 供应商档案信息
			SupplierVO hvo = (SupplierVO) new HYPubBO().queryByPrimaryKey(
					SupplierVO.class, pk_supplier);
			if(StringUtils.isEmpty(hvo.getDef1())){
				hvo.setDef1(oldsupplier_name);	
			}else if(StringUtils.isEmpty(hvo.getDef2())){
				hvo.setDef2(oldsupplier_name);	
			}else if(StringUtils.isEmpty(hvo.getDef3())){
				hvo.setDef3(oldsupplier_name);	
			}else{
				return "供应商变更次数已达最大，请检查！";
			}
			hvo.setName(newsupplier_name);
			hvo.setStatus(VOStatus.UPDATED);
			new HYPubBO().update(hvo);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
		return "";
	}
}
