package nc.imag.scan.action;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.imag.itf.service.IImageService;
import nc.imag.pub.uitl.ImagBasePubUtil;
import nc.imag.pub.util.ImageServiceUtil;
import nc.imag.util.ws.ImageFactoryConfigUtil;
import nc.itf.image.IImageScanQueryService;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.md.data.access.NCObject;
import nc.md.model.IBean;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pubapp.uif2app.AppUiState;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import uap.lfw.dbl.cpdoc.util.DocCommonUtil;
import uap.lfw.dbl.vo.MetaDataBaseAggVO;

// 影像扫描功能禁用
@SuppressWarnings({ "unused", "restriction" })
public class BaseImageScanAction extends NCAction {
	private static final long serialVersionUID = 1L;
	private AbstractUIAppModel model;
	private Object dataObj;
	private Object selDataObj;
	private String pk_billtype;
	private String pk_org;
	private String pk_orgForScan;
	private String checkscanway;
	private String ownmodule;

	public BaseImageScanAction() {
		setCode("BaseImageScan");
		setBtnName(NCLangRes4VoTransl.getNCLangRes().getStrByID("1054002_0",
				"01054002-0000"));
	}

	public void doAction(ActionEvent e) throws BusinessException {
		try {
			if (!ImagBasePubUtil.imageIsEnabled()) {
				ShowStatusBarMsgUtil.showErrorMsg(
						NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"1054002_0", "01054002-0001"),

						NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"1054002_0", "01054002-0002"),

						getModel().getContext());
				return;
			}
		} catch (BusinessException e2) {
			ExceptionUtils.wrappException(e2);
			return;
		}
		try {
			if (this.dataObj == null) {
				this.selDataObj = this.model.getSelectedData();
				if (this.model.getAppUiState().equals(AppUiState.ADD)) {
					throw new BusinessException("请先 <暂存> 单据再扫描影像");
				}
			} else {
				this.selDataObj = this.dataObj;
				this.dataObj = null;
			}
			if (this.selDataObj == null) {
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("1054002_0", "01054002-0003"));
			}

			if ((this.selDataObj instanceof MetaDataBaseAggVO)) {
				String docPK = (String) ((MetaDataBaseAggVO) this.selDataObj)
						.getParentVO().getAttributeValue("pk_doc");
				IBean bean = DocCommonUtil.getBeanByMD(docPK);
				((MetaDataBaseAggVO) this.selDataObj).getParentVO()
						.setAttributeValue("bean", bean);
			}
			NCObject ncObj = NCObject.newInstance(this.selDataObj);
			IFlowBizItf itf = (IFlowBizItf) ncObj
					.getBizInterface(IFlowBizItf.class);
			String billtype = itf.getTranstype();
			if ((billtype == null) || (billtype.equals(""))) {
				billtype = itf.getBilltype();
			}

			String billofpk_org = itf.getPkorg();
			String userid = InvocationInfoProxy.getInstance().getUserId();
			String factoryCode = ImageFactoryConfigUtil.factoryCode;
			if (factoryCode == null) {
				factoryCode = ImageServiceUtil
						.getImageFactoryCode(billofpk_org);
			}
			Map<String, String> kvMap = ImageFactoryConfigUtil
					.getAttr2ValueMapWithoutIFlowBizItf(factoryCode,
							this.selDataObj, billtype);
			String pk_org = ImageFactoryConfigUtil
					.getPk_org(kvMap, factoryCode);
			String billid = ImageFactoryConfigUtil.getPk(kvMap, factoryCode);
			String transType = ImageFactoryConfigUtil.getRealTransTypeCode(
					kvMap, factoryCode);

			if ("tchzt".equalsIgnoreCase(factoryCode)) {
				showModal((JComponent) e.getSource(), null);
			} else {
				ImageServiceUtil.imageScanCheck(this.selDataObj, transType,
						billid, userid, billofpk_org);
				if (!ImageServiceUtil.getIfScanInFlow(billofpk_org, billtype)) {
					int scanType = ((IImageScanQueryService) NCLocator
							.getInstance().lookup(IImageScanQueryService.class))
							.queryImageScan(billofpk_org, billtype);
					if ((scanType == 1) || (scanType == 2)) {
						((IImageService) NCLocator.getInstance().lookup(
								IImageService.class)).addScanTask(
								this.selDataObj, scanType, billtype,
								billofpk_org);
					}
				}
				String url = ImageServiceUtil.getImageScanURL(this.selDataObj,
						billtype, billid, userid, billofpk_org);
				if ("tchzt2".equalsIgnoreCase(factoryCode)) {
					showModal((JComponent) e.getSource(), url);
				} else {
					Runtime.getRuntime().exec(
							"rundll32 url.dll,FileProtocolHandler " + url);
				}
			}
		} catch (Exception e1) {
			Logger.error(e1.getMessage(), e1);
			ShowStatusBarMsgUtil.showErrorMsg(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("1054002_0", "01054002-0001"),

			e1.getMessage(), getModel().getContext());
		}
		// 角色下用户可使用
		String fg = "0";
		String user = InvocationInfoProxy.getInstance().getUserId();// 当前用户主键
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String querySql = "select c.cuserid, a.pk_role, a.role_name,c.user_name from sm_role a left join sm_user_role b  on a.pk_role=b.pk_role "
				+ " left join sm_user c on c.cuserid=b.cuserid  where a.role_code = 'YXSM'";
		List<Object[]> resultList = getDao.query(querySql);
		System.out.println("sql===" + querySql);
		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				Object[] item = resultList.get(i);
				if (user.equals(item[0])) {
					fg = "1";
				}
			}
		}
		System.out.println("当前登录用户主键：" + user);
		if (!"1".equals(fg)) {
			throw new BusinessException("功能关闭，请使用影像上传");
		}
	}

	private void showModal(JComponent parent, String url) throws Exception {
		if (!beforeAction()) {
			return;
		}

		NCObject ncObj = NCObject.newInstance(this.selDataObj);
		IFlowBizItf itf = (IFlowBizItf) ncObj
				.getBizInterface(IFlowBizItf.class);
		String factoryCode = ImageFactoryConfigUtil.factoryCode;
		if (factoryCode == null) {
			String billofpk_org = itf.getPkorg();
			factoryCode = ImageServiceUtil.getImageFactoryCode(billofpk_org);
		}
		Map<String, String> fieldMap = ImageFactoryConfigUtil.getAttr2ValueMap(
				factoryCode, this.selDataObj, this.pk_billtype);

		String pkfield = ((IImageService) NCLocator.getInstance().lookup(
				IImageService.class)).getPKField(factoryCode);

		String billID = (String) fieldMap.get(pkfield);

		if (factoryCode.equalsIgnoreCase("tchzt2")) {
			ImageScanBrowser browser = new ImageScanBrowser();
			browser.ScanBrowser(url);
		} else {
			ImageScanBrowser browser = new ImageScanBrowser(parent, fieldMap);
			browser.showBrowser("scan", url);
		}
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public AbstractUIAppModel getModel() {
		return this.model;
	}

	public String getPk_billtype() {
		return this.pk_billtype;
	}

	public void setPk_billtype(String pk_billtype) {
		this.pk_billtype = pk_billtype;
	}

	public String getCheckscanway() {
		return this.checkscanway;
	}

	public void setCheckscanway(String checkscanway) {
		this.checkscanway = checkscanway;
	}

	public Object getDataObj() {
		return this.selDataObj;
	}

	public void setDataObj(Object dataObj) {
		this.dataObj = dataObj;
	}

	protected boolean beforeAction() {
		return true;
	}
}
