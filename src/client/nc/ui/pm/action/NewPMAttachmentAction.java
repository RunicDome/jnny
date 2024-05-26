package nc.ui.pm.action;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.bc.pmpub.project.ProjectHeadVO;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.ct.purdaily.entity.RlPmeFile;
import nc.vo.pcm.feebalance.FeeBalanceBillVO;
import nc.vo.pcm.feebalance.FeeBalanceBodyVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

@SuppressWarnings("restriction")
public class NewPMAttachmentAction extends PMAttachmentAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NewPMAttachmentAction() {
		super();
		ActionInitializer.initializeAction(this, IPMActionCode.FILE);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		/* step-1检查<项目>是否为空 */
		/*AbstractBill obj = (AbstractBill) getModel().getSelectedData();
		 step-2检查<项目>是否为空 
		String vtrantypecode = (String) obj.getParent().getAttributeValue(
				"transi_type");
		if (null == vtrantypecode) {
			vtrantypecode = (String) obj.getParent().getAttributeValue(
					"vtrantypecode");
		}
		Map<String, String> projectIds = new HashMap<String, String>();
		 采购合同 
		if (vtrantypecode.contains("Z2")) {
			CtPuBVO[] bvos = ((AggCtPuVO) obj).getCtPuBVO();
			for (CtPuBVO temp : bvos) {
				if (null != temp.getCbprojectid()) {
					projectIds
							.put(temp.getCbprojectid(), temp.getCbprojectid());
				}
			}
		}
		 多编码合同 
		if (vtrantypecode.contains("4Z01")) {

		}
		 费用结算单 
		if (vtrantypecode.contains("4D83")) {
			FeeBalanceBodyVO[] bvos = (FeeBalanceBodyVO[]) ((FeeBalanceBillVO) obj).getChildrenVO();
			for (FeeBalanceBodyVO temp : bvos) {
				if (null != temp.getPk_project()) {
					projectIds
							.put(temp.getPk_project(), temp.getPk_project());
				}
			}
		}
		 清单发包合同 
		if (vtrantypecode.contains("4D42")) {
			if(null != obj.getParent().getAttributeValue("pk_project")){
				projectIds
				.put(obj.getParent().getAttributeValue("pk_project").toString(), obj.getParent().getAttributeValue("pk_project").toString());
			}
		}
		 竣工验收 
		if (vtrantypecode.contains("4D36")) {
			if(null != obj.getParent().getAttributeValue("pk_project")){
				projectIds
				.put(obj.getParent().getAttributeValue("pk_project").toString(), obj.getParent().getAttributeValue("pk_project").toString());
			}
		}
		 结算单 
		if (vtrantypecode.contains("4D50")) {
			if(null != obj.getParent().getAttributeValue("pk_project")){
				projectIds
				.put(obj.getParent().getAttributeValue("pk_project").toString(), obj.getParent().getAttributeValue("pk_project").toString());
			}
		}
		 项目决算单 
		if (vtrantypecode.contains("4D64")) {
			if(null != obj.getParent().getAttributeValue("pk_project")){
				projectIds
				.put(obj.getParent().getAttributeValue("pk_project").toString(), obj.getParent().getAttributeValue("pk_project").toString());
			}
		}
		if (projectIds.size() > 0) {
			String pk_project = "";
			for (Map.Entry<String, String> entry : projectIds.entrySet()) {
				pk_project += "'" + entry.getKey() + "' ,";
			}
			pk_project = pk_project.substring(0, pk_project.length() - 2);
			String strWhere = " nvl(dr,0)=0 and project_id in (" + pk_project
					+ ")";
			RlPmeFile[] bvos = null;
			bvos = (RlPmeFile[]) HYPubBO_Client.queryByCondition(
					RlPmeFile.class, strWhere);
			for (RlPmeFile temp : bvos) {
				ProjectHeadVO projectHeadVO = (ProjectHeadVO) HYPubBO_Client
						.queryByPrimaryKey(ProjectHeadVO.class,
								temp.getProject_id());
				temp.setAttributeValue("project_code",
						projectHeadVO.getProject_code());
				temp.setAttributeValue("project_name",
						projectHeadVO.getProject_name());
			}
			 step-3查<项目>是否为空 
			if (null != bvos && bvos.length > 0) {

				ArtRequestDialog dialog = new ArtRequestDialog(getModel(), bvos);
				dialog.show();
			}
		}*/
		super.doAction(e);
	}
}