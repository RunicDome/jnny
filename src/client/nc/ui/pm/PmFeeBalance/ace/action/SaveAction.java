/**
 *
 */
package nc.ui.pm.PmFeeBalance.ace.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.actions.pflow.SaveScriptAction;
import nc.ui.uif2.editor.IEditor;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pm.feebalance.PmFeebalanceBVO;
import nc.vo.pm.feebalance.PmFeebalanceHVO;

/**
 * ���ݱ���
 * 
 * @see
 * @author guodw
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2009-8-26 ����08:39:32
 */
@SuppressWarnings({ "restriction", "unused" })
public class SaveAction extends SaveScriptAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
		
	}
	
	public IEditor getEditor(){
		return super.editor;
	}
}