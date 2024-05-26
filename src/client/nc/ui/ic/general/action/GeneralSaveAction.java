package nc.ui.ic.general.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.bs.uif2.validation.IValidationService;
import nc.itf.pbm.materialplan.pvt.IMaterialPlanCommonService;
import nc.itf.pubapp.pub.exception.IResumeException;
import nc.itf.scmpub.reference.uap.group.SysInitGroupQuery;
import nc.ui.ic.general.model.ICGenBizEditorModel;
import nc.ui.ic.general.model.ICGenBizModel;
import nc.ui.ic.general.util.GenResumeExceptionHandle;
import nc.ui.ic.general.view.ICBizView;
import nc.ui.ic.pub.action.SaveAction;
import nc.ui.ic.pub.env.ICUIContext;
import nc.ui.ic.pub.model.ICBizEditorModel;
import nc.ui.ic.pub.model.ICBizModel;
import nc.ui.ic.pub.util.CardPanelWrapper;
import nc.ui.ic.pub.view.ICBizBillForm;
import nc.ui.ml.NCLangRes;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.pub.common.context.PFlowContext;
import nc.ui.pubapp.pub.power.PowerSaveValidateService;
import nc.ui.pubapp.uif2app.AppUiState;
import nc.ui.pubapp.uif2app.actions.IActionExecutable;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.validation.CompositeValidation;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.bc.pmpub.project.ProjectHeadVO;
import nc.vo.ia.mi6.entity.I6BillVO;
import nc.vo.ia.mi6.entity.I6ItemVO;
import nc.vo.ic.general.deal.ICBillValueSetter;
import nc.vo.ic.general.define.ICBillBodyVO;
import nc.vo.ic.general.define.ICBillHeadVO;
import nc.vo.ic.general.define.ICBillVO;
import nc.vo.ic.general.util.ICLocationUtil;
import nc.vo.ic.location.ICLocationVO;
import nc.vo.ic.m4d.entity.MaterialOutBodyVO;
import nc.vo.ic.m4d.entity.MaterialOutHeadVO;
import nc.vo.ic.m4d.entity.MaterialOutVO;
import nc.vo.ic.pub.define.BillOperator;
import nc.vo.ic.pub.pf.ICPFParameter;
import nc.vo.ic.pub.util.StringUtil;
import nc.vo.ic.pub.util.ValueCheckUtil;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.org.OrgVO;
import nc.vo.pbm.materialplan.MaterialPlanBillVO;
import nc.vo.pbm.materialplan.MaterialPlanBodyVO;
import nc.vo.pbm.materialplan.MaterialPlanHeadVO;
import nc.vo.pbm.materialstock.MaterialStockBodyFOrSCMVO;
import nc.vo.pcm.materialacc.AggMaterialaccVO;
import nc.vo.pcm.materialacc.MaterialaccBodyVO;
import nc.vo.pcm.materialacc.MaterialaccHeadVO;
import nc.vo.pcm.materialacc.SecondMaterialBodyVO;
import nc.vo.pmpub.project.ProjectBillVO;
import nc.vo.pmpub.projecttype.ProjectTypeHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.vorg.DeptVersionVO;
@SuppressWarnings("restriction")
public class GeneralSaveAction extends SaveAction implements IActionExecutable {
	private static final long serialVersionUID = 2010082721520001L;
	private boolean isExecuted;

	public GeneralSaveAction() {
		this.isExecuted = true;
	}

	public void doBeforAction() {
		super.doBeforAction();

		getEditorModel().getCardPanelWrapper().filterNullLine();

		int iRowCount = getEditorModel().getCardPanelWrapper()
				.getBillCardPanel().getRowCount();
		
		if (iRowCount <= 0) {
			ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
					.getNCLangRes().getStrByID("4008001_0", "04008001-0031"));
		}
		String flag = format();
		if(!"".equals(flag)){
			ExceptionUtils.wrappBusinessException(flag);
		}
		checkSnDuplicate();
	}

	public void setModel(BillManageModel model) {
		super.setModel(model);
		model.addAppEventListener(this);
	}

	public void setValidationService(IValidationService validationService) {
		if (validationService instanceof PowerSaveValidateService) {
			IValidationService validator = getValidationService();

			if (validator instanceof CompositeValidation) {
				List list = ((CompositeValidation) validator).getValidate();

				list.add(validationService);
				((CompositeValidation) validator).setValidators(list);
			} else {
				validator = validationService;
			}
			super.setValidationService(validator);
		} else if (validationService instanceof CompositeValidation) {
			List list = ((CompositeValidation) validationService).getValidate();

			if (getValidationService() != null) {
				list.add(getValidationService());
			}
			super.setValidationService(validationService);
		} else {
			super.setValidationService(validationService);
		}
	}

	private void afterSave() {
		getEditorModel().setTempBillPK(null);
		getEditorModel().clearAllBodyDetailData();
		((ICGenBizEditorModel) getEditorModel()).getICBizModel()
				.clearLocationVOsAtSelectedRow();

		getModel().setAppUiState(AppUiState.NOT_EDIT);
		((ICBizModel) getModel()).getIcUIContext().showStatusBarMessage(
				NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("common", "UCH005"));
	}

	private void checkSnDuplicate() {
		ICBizView view = (ICBizView) getEditorModel().getICBizView();
		if (view == null) {
			return;
		}
		ICBillVO bill = (ICBillVO) getEditorModel().getICBizView().getValue();
		ICBillBodyVO[] bodys = bill.getBodys();
		Map sns = new HashMap();
		Map dupsnRowNoMap = new LinkedHashMap();

		boolean isSNEndabled = SysInitGroupQuery.isSNEnabled();

		for (int i = 0; i < bodys.length; ++i) {
			String rowno = getEditorModel().getCardPanelWrapper()
					.getBodyValueAt_String(i, "crowno");

			ICLocationVO[] locs = getEditorModel().getBodyEditDetailData(i);
			if (ValueCheckUtil.isNullORZeroLength(locs)) {
				continue;
			}
			for (int j = 0; j < locs.length; ++j) {
				String sn = locs[j].getVserialcode();
				if (StringUtil.isSEmptyOrNull(sn)) {
					continue;
				}
				String key = (isSNEndabled) ? new StringBuilder()
						.append(bodys[i].getCmaterialvid()).append(sn)
						.toString() : sn;

				if (sns.containsKey(key)) {
					if (dupsnRowNoMap.get(sn) == null) {
						Set dupset = new HashSet();
						dupset.add(sns.get(key));
						dupset.add(rowno);
						dupsnRowNoMap.put(sn, dupset);
					} else {
						((Set) dupsnRowNoMap.get(sn)).add(sns.get(key));
						((Set) dupsnRowNoMap.get(sn)).add(rowno);
					}
				}
				sns.put(key, rowno);
			}
		}
		if (ValueCheckUtil.isNullORZeroLength(dupsnRowNoMap)) {
			return;
		}
		showErrMsg(dupsnRowNoMap);
	}

	private void showAutoBalancedHint(Object[] retObj) {
		if (!(retObj instanceof ICBillVO[])) {
			return;
		}
		ICBillVO[] bills = (ICBillVO[]) (ICBillVO[]) retObj;
		for (ICBillVO bill : bills) {
			if (bill.getHead().getHasbalanced() == null)
				continue;
			if (!(bill.getHead().getHasbalanced().booleanValue())) {
				continue;
			}

			MessageDialog.showWarningDlg(getEditorModel().getContext()
					.getContainUI(), null, NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("4008001_0", "04008001-0032"));
		}
	}

	private void showErrMsg(Map<String, Set<String>> dupsnRowNoMap) {
		StringBuilder errMsg = new StringBuilder();
		errMsg.append(NCLangRes4VoTransl.getNCLangRes().getStrByID("4008001_0",
				"04008001-0033"));

		for (Map.Entry snent : dupsnRowNoMap.entrySet()) {
			StringBuffer rownostr = new StringBuffer();
			// for (String rowno : (Set) snent.getValue()) {
			for (Object rowno : (Set) snent.getValue()) {
				rownostr.append(rowno);
				rownostr.append(",");
			}
			rownostr.deleteCharAt(rownostr.length() - 1);
			errMsg.append(NCLangRes.getInstance().getStrByID("4008001_0",
					"04008001-0669", null,
					new String[]{(String) snent.getKey(), rownostr.toString()}));

			errMsg.append("\n");
		}
		errMsg.deleteCharAt(errMsg.length() - 1);
		ExceptionUtils.wrappBusinessException(errMsg.toString());
	}

	protected void fillUpContext(PFlowContext context) {
		ICPFParameter pfparam = (ICPFParameter) context.getUserObj();
		if (context.getUserObj() == null) {
			pfparam = new ICPFParameter();
		}
		if (getModel().getAppUiState() == AppUiState.ADD) {
			pfparam.setBillaction(BillOperator.New);
		} else if (getModel().getAppUiState() == AppUiState.EDIT) {
			pfparam.setBillaction(BillOperator.Edit);
		}
		context.setUserObj(pfparam);
		super.fillUpContext(context);
	}

	protected boolean isResume(IResumeException resumeInfo) {
		boolean executed = new GenResumeExceptionHandle(getEditorModel(),
				getFlowContext()).isResume(resumeInfo);

		setExecuted(executed);
		return executed;
	}

	protected Object[] processBefore(Object[] vos) {
		ICBillVO icBillVO = null;
		ICUIContext context = ((ICBizModel) getModel()).getIcUIContext();
		ICBillValueSetter setter = new ICBillValueSetter();

		for (Object vo : vos) {
			icBillVO = (ICBillVO) vo;
			try {
				setter.setBillInitData(icBillVO, context);

				((ICGenBizEditorModel) getEditorModel())
						.setLocationVOToBodyForSave(icBillVO);

				icBillVO.setTempBillPK(getEditorModel().getTempBillPK());
			} catch (Exception ex) {
				ExceptionUtils.wrappException(ex);
			}
		}
		
		return super.processBefore(vos);
	}

	protected void processReturnObj(Object[] retObj) throws Exception {
		super.processReturnObj(retObj);
		afterSave();
		showAutoBalancedHint(retObj);
	}

	protected AbstractBill[] produceLightVO(AbstractBill[] newVO) {
		Map beforeUpdatedVOMap = ICLocationUtil
				.getLocationVO((ICBillVO[]) (ICBillVO[]) newVO);

		AbstractBill[] lightVOs = super.produceLightVO(newVO);
		fillBatchInfoAfterLight(lightVOs, newVO);
		ICLocationUtil.setLocationVO((ICBillVO[]) (ICBillVO[]) lightVOs,
				beforeUpdatedVOMap);

		return ((AbstractBill[]) lightVOs);
	}

	private void fillBatchInfoAfterLight(AbstractBill[] lightVOs,
			AbstractBill[] newVO) {
		if (AppUiState.EDIT != this.model.getAppUiState()) {
			return;
		}

		Map newmap = getValueMap(newVO);
		if (ValueCheckUtil.isNullORZeroLength(newmap)) {
			return;
		}

		String[] fillFields = {"cqualitylevelid", "dproducedate", "dvalidate",
				"csnunitid"};
		try {
			for (AbstractBill lightVO : lightVOs) {
				fillBatchInfo(newmap, fillFields, lightVO);
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}
	}

	private void fillBatchInfo(
			Map<String, CircularlyAccessibleValueObject> newmap,
			String[] fillFields, AbstractBill lightVO) throws BusinessException {
		CircularlyAccessibleValueObject[] bodys = lightVO.getAllChildrenVO();
		if (ValueCheckUtil.isNullORZeroLength(bodys)) {
			return;
		}
		for (CircularlyAccessibleValueObject body : bodys) {
			String primarykey = body.getPrimaryKey();
			CircularlyAccessibleValueObject newbody = (CircularlyAccessibleValueObject) newmap
					.get(primarykey);
			if (StringUtil.isSEmptyOrNull(primarykey))
				continue;
			if (newbody == null) {
				continue;
			}
			for (String field : fillFields) {
				if (body.getAttributeValue(field) != null) {
					continue;
				}
				body.setAttributeValue(field, newbody.getAttributeValue(field));
			}
		}
	}

	private Map<String, CircularlyAccessibleValueObject> getValueMap(
			AbstractBill[] oldVOs) {
		Map oldmap = new HashMap();
		try {
			if (ValueCheckUtil.isNullORZeroLength(oldVOs)) {
				return null;
			}
			for (AbstractBill old : oldVOs) {
				CircularlyAccessibleValueObject[] bodys = old
						.getAllChildrenVO();
				for (CircularlyAccessibleValueObject body : bodys) {
					String primarykey = body.getPrimaryKey();
					if (StringUtil.isSEmptyOrNull(primarykey)) {
						continue;
					}
					oldmap.put(primarykey, body);
				}
			}
		} catch (BusinessException ex) {
			ExceptionUtils.wrappException(ex);
		}
		return oldmap;
	}

	public boolean isExecuted() {
		return this.isExecuted;
	}

	public void setExecuted(boolean isExecuted) {
		this.isExecuted = isExecuted;
	}
	public String format(){
		String flag = "";
		System.out.println("this.getBillType()=="+this.getBillType());
		if("4D".equals(this.getBillType())){
			GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
			MaterialOutVO billVO = (MaterialOutVO) this.editor.getValue();//BILLVO
			MaterialOutHeadVO headVO = (MaterialOutHeadVO) billVO.getParentVO();
			MaterialOutBodyVO[] bodyVO = (MaterialOutBodyVO[]) billVO.getChildrenVO();//表体VO
			checkBodyMate(bodyVO);
			
			for(int i=0;i<bodyVO.length;i++){
				MaterialOutBodyVO itemVO = bodyVO[i];
				String pk_project = itemVO.getCprojectid();//项目主键
				if(pk_project != null){
					try {
						ProjectHeadVO projectHeadVO = (ProjectHeadVO) HYPubBO_Client
								.queryByPrimaryKey(ProjectHeadVO.class, pk_project+ "");//项目VO
						String project_code = projectHeadVO.getProject_code();//项目名称
						if (projectHeadVO != null) {
							//20221012 材料结算  施工单位+项目是否有 组织为03和0308
								if("0001A21000000001ZBF8".equals(headVO.getPk_org()) || "0001A11000000007P8FS".equals(headVO.getPk_org()) ){
								MaterialaccHeadVO[] cljsvo = (MaterialaccHeadVO[]) HYPubBO_Client.queryByCondition(MaterialaccHeadVO.class,
										"pk_project = '"+pk_project+"' and pk_supplier = '"+headVO.getCconstructvendorid()+"' and dr = 0 and bill_status = 1");
								if(cljsvo != null && cljsvo.length > 0 && cljsvo[0] != null && cljsvo[0].getBill_code() != null){
									System.out.println("第"+(i+1)+"行项目名称为"+project_code+"已做材料结算["+cljsvo[0].getBill_code()+"]");
									// MessageDialog.showErrorDlg(null, "提示", "第"+(i+1)+"行项目名称为"+project_code+"已做材料结算["+cljsvo[0].getParentVO().getBill_code()+"]");
									flag = "第"+(i+1)+"行项目名称为"+project_code+"已做材料结算["+cljsvo[0].getBill_code()+"]";
								}
							}
							// 材料结算  施工单位+项目是否有结束
							String pk_projectclass = projectHeadVO.getPk_projectclass()+"";//项目类型主键
							
							String fg = "";
							if (pk_projectclass != null
									&& ("1001A21000000005SCHW".equals(pk_projectclass) || "1001A21000000005NM9B"
											.equals(pk_projectclass))) {
								String getndjhsql = "SELECT COUNT(ZB.PK_YEARPLAN) AS SL FROM PM_YEARPLAN ZB LEFT JOIN PM_YEARPLAN_B MX ON ZB.PK_YEARPLAN = MX.PK_YEARPLAN WHERE ZB.DR = 0 AND MX.DR = 0 AND ZB.PLAN_YEAR > '2020' AND MX.PK_PROJECT = '"
										+ pk_project + "'";
								System.out.println("查询年度投资计划SQL：" + getndjhsql);
								List<Object[]> tzls = getDao.query(getndjhsql);
								int sl = Integer.parseInt(tzls.get(0)[0] + "");
								if (sl > 0) {
									fg = "1";
								}
							} else {
								System.out.println("项目类型：" + pk_projectclass);
							}
							if ("1".equals(fg)) {
								// 校验是否能投或下属子公司
								int ifcheck = 0;
								String pk_org = billVO.getParentVO().getPk_org();
								OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
										pk_org);
								if("0001A21000000003U6L6".equals(pk_org) || "0001A21000000003U6L6".equals(orgVO.getPk_fatherorg())
										|| "0001A110000000000HYQ".equals(pk_org) ){
									ifcheck = 1;
								}
								if(ifcheck == 0 && StringUtils.isNotEmpty(orgVO.getPk_fatherorg())){
									OrgVO fatherorgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
											pk_org);// 上级组织
									if(StringUtils.isNotEmpty(fatherorgVO.getPk_fatherorg()) && ("0001A21000000003U6L6".equals(fatherorgVO.getPk_fatherorg()) || 
											"0001A110000000000HYQ".equals(fatherorgVO.getPk_fatherorg()))){
										ifcheck = 1;
									}
								}
								//String project_name = projectHeadVO.getProject_name();//项目名称
								
								ProjectTypeHeadVO typeVO = (ProjectTypeHeadVO) HYPubBO_Client
										.queryByPrimaryKey(ProjectTypeHeadVO.class, pk_projectclass+ "");//项目类型VO
								if(typeVO!=null && ifcheck == 1){
									String kgrqsql = "SELECT COUNT(PK_PROJECTPROPOSAL) AS SL FROM PM_PROJECTPROPOSAL WHERE BILL_STATUS = '1' AND PK_GROUP = '0001A1100000000001QS' "
											+ "AND TRANSI_TYPE = '4D15-Cxx-011' AND DR = 0 AND PK_PROJECT = '"
											+ pk_project + "'";
									List<Object[]> rqls = getDao.query(kgrqsql);
									System.out.println("查询开工容缺受理单sql："+kgrqsql);
									if(Integer.parseInt(rqls.get(0)[0] + "") <= 0 ){
										String type_name = typeVO.getType_name();//项目类型名称
										String sql = "SELECT NAME FROM RL_CBSNAME WHERE NAME LIKE '%"
												+ type_name + "%' AND TYPE = 'XMTYPE'";
										System.out.println("sql==="+sql);
										List<Object[]> ls = getDao.query(sql);
										if (ls != null && ls.size() > 0) {
											String pgdsql = "SELECT PK_PROJECTPROPOSAL FROM PM_PROJECTPROPOSAL WHERE BILL_STATUS = '1' AND PK_GROUP = '0001A1100000000001QS' "
													+ "AND TRANSI_TYPE = '4D15-Cxx-008' AND DR = 0 AND PK_PROJECT = '"
													+ pk_project + "'";
											List<Object[]> pgdls = getDao.query(pgdsql);
											if (pgdls == null || pgdls.size() <= 0) {
												System.out.println("第"+(i+1)+"行项目名称为"+project_code+"未完成开工单！");
												// MessageDialog.showErrorDlg(null, "提示", "第"+(i+1)+"行项目编码为"+project_code+"未完成开工单！");
												flag = "第"+(i+1)+"行项目编码为"+project_code+"未完成开工单！";
											}
										}
									}	
								}
							}
						}
					} catch (UifException e1) {
						// TODO 自动生成的 catch 块
						e1.printStackTrace();
					} catch (DAOException e1) {
						// TODO 自动生成的 catch 块
						e1.printStackTrace();
					}
				}
			}
		}
		return flag;
	}
	//检查出库单表体在项目下不存在物资需求单的物料行
	private static void checkBodyMate(MaterialOutBodyVO[] billVOs) {
		if(billVOs == null || billVOs.length < 1){
			return;
		}
		// 获取表体所有项目
		//Set<String> projectIds = new HashSet<>();
		for(MaterialOutBodyVO scmVO : billVOs){
			try {
				if(scmVO.getCprojectid() != null){
					ProjectHeadVO xmvo = (ProjectHeadVO) HYPubBO_Client.queryByPrimaryKey(ProjectHeadVO.class,
						scmVO.getCprojectid() + ""); //项目档案VO
					String def5 = xmvo.getDef5()+"";//是否完工=====新增
					if(def5==null||"".equals(def5)||"~".equals(def5)||"null".equals(def5)){
						
					}else{
						if("1001A2100000000B68C1".equals(def5)){
							//如果为是，则不允许保存
							ExceptionUtils.wrappBusinessException("行号为"+scmVO.getCrowno()+"行项目档案已完工，不允许保存！");
						}
						/*ProjectTypeHeadVO typeVO = (ProjectTypeHeadVO) HYPubBO_Client
								.queryByPrimaryKey(ProjectTypeHeadVO.class, xmvo.getPk_projectclass()+ "");//项目类型VO
						String type_name = typeVO.getType_name();//项目类型名称
						String sql = "SELECT NAME FROM RL_CBSNAME WHERE NAME LIKE '%"
								+ type_name + "%' AND TYPE = 'CLCK'";
						System.out.println("sql==="+sql);
						GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
						List<Object[]> ls = getDao.query(sql);
						if (ls != null && ls.size() > 0) {
							MaterialPlanBillVO wzvo = NCLocator.getInstance()
									.lookup(IMaterialPlanCommonService.class).queryMaterialPlanBillVOByPkProject(scmVO.getCprojectid()+ "");//物资及服务需求单VO
							if(wzvo == null){
								ExceptionUtils.wrappBusinessException("行号为"+scmVO.getCrowno()+"行请先做物资需求单！");
							}
							//projectIds.add(scmVO.getCprojectid());
						}*/
					}
				}
				return;
			} catch (UifException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			
		}
		
		/*if(projectIds.size() == 0)
			return;
		// 查询项目下的物资需求单
		MaterialPlanBillVO[] materPlanBillVOs = NCLocator
				.getInstance()
				.lookup(IMaterialPlanCommonService.class)
				.queryMaterialPlanBillVOByPkProject(
						projectIds.toArray(new String[0]));
		
		if(materPlanBillVOs == null || materPlanBillVOs.length < 1){
			ExceptionUtils.wrappBusinessException("请先做物资需求单");
		}*/
		
	
	}
}