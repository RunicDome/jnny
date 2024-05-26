package nc.ui.pbm.budget.CEAPrint;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import nc.ui.pm.action.ActionInitializer;
import nc.ui.pub.print.IDataSource;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.actions.PrintAction;
import nc.vo.pbm.budget.BudgetBillVO;
import nc.vo.pbm.budget.BudgetHeadVO;
import nc.vo.pmbd.common.consts.FuncCodeConst;

/**
 * 类描述： CEA-C打印动作按钮
 * @date 2018-3-23
 *
 */
public class CEA_CPrintAction extends PrintAction {

	/**
	 * 版本号
	 */
	private static final long serialVersionUID = 551349680633153863L;
	
	public CEA_CPrintAction() {
		super();
		setBtnName("CEA-A");
		ActionInitializer.initializeAction(this, "CEA-CPrint");
	}

	@Override
	public void doAction(ActionEvent e) {
		JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class,
				this.getModel().getContext().getEntranceUI());
		PrintEntry entry = new PrintEntry(frame);
		// pub_print_template表主键
		BudgetBillVO billVO = (BudgetBillVO) this.getModel().getSelectedData();
		BudgetHeadVO headVo=billVO.getParentVO();
		entry.setTemplateID(headVo.getPk_group(),headVo.getPk_org(), FuncCodeConst.BUDGET,  this.getModel().getContext().getPk_loginUser(), null, CEAPrintConst.CEA_C_TEMPLET,null);
		entry.selectTemplate();
		entry.setDataSource(getDataSource());
		entry.preview();
	}
	
	/**
	 * 方法描述：获取数据进行字段的映射
	 * @return
	 */
	private IDataSource getDataSource() {
		
		BudgetBillVO billVO = (BudgetBillVO) this.getModel().getSelectedData();
		return new CEA_CDataSource(billVO);
	}
	
}
