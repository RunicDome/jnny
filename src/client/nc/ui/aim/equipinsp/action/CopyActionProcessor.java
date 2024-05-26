package nc.ui.aim.equipinsp.action;

import nc.ui.pubapp.uif2app.actions.intf.ICopyActionProcessor;
import nc.vo.aim.equipinsp.AggEquipinsp;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.uif2.LoginContext;

public class CopyActionProcessor implements ICopyActionProcessor<AggEquipinsp> {

	@Override
	public void processVOAfterCopy(AggEquipinsp paramT,
			LoginContext paramLoginContext) {
		paramT.getParent().setPrimaryKey(null);
		paramT.getParent().setModifier(null);
		paramT.getParent().setModifiedtime(null);
		paramT.getParent().setCreator(null);
		paramT.getParent().setCreationtime(null);
		paramT.getParent().setBillno(null);
		paramT.getParent().setApprover(null);
		paramT.getParent().setApprovedate(null);
		// TODO 根据需要业务自己补充处理清空
		String[] codes = paramT.getTableCodes();
		if (codes != null && codes.length > 0) {
			for (int i = 0; i < codes.length; i++) {
				String tableCode = codes[i];
				CircularlyAccessibleValueObject[] childVOs = paramT
						.getTableVO(tableCode);
				for (CircularlyAccessibleValueObject childVO : childVOs) {
					try {
						childVO.setPrimaryKey(null);
					} catch (BusinessException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
		}
	}
}
