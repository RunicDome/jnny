package nc.ui.arap.actions;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import nc.bs.arap.bill.ArapBillPubUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.trade.business.HYPubBO;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.fipub.framework.base.FIStringUtil;
import nc.itf.uap.pf.busiflow.PfButtonClickContext;
import nc.pubitf.setting.defaultdata.OrgSettingAccessor;
import nc.ui.arap.model.ArapBillManageModel;
import nc.ui.arap.pub.ArapUiUtil;
import nc.ui.arap.view.ArapBillCardForm;
import nc.ui.arap.view.ArapTransferBillDataLogic;
import nc.ui.dbcache.DBCacheFacade;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.pubapp.billref.dest.TransferBillViewProcessor;
import nc.ui.pubapp.uif2app.event.OrgChangedEvent;
import nc.ui.pubapp.uif2app.mediator.modelevent.IBillDefVaule;
import nc.ui.pubapp.uif2app.mediator.mutiltrans.NodekeyEvent;
import nc.ui.pubapp.uif2app.view.BillOrgPanel;
import nc.ui.pubapp.uif2app.view.MutilTransBillForm;
import nc.ui.pubapp.uif2app.view.ShowUpableBillListView;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.actions.AddAction;
import nc.ui.uif2.components.IAutoShowUpComponent;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.basebill.BaseItemVO;
import nc.vo.arap.pub.BillEnumCollection;
import nc.vo.arap.pub.BillEnumCollection.BillSatus;
import nc.vo.arap.pub.BillEnumCollection.FromSystem;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.org.GroupVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.PfAddInfo;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.pub.MapList;
import nc.vo.sm.UserVO;
import nc.vo.uif2.LoginContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

// 工程付款单、工程应付单自制控制
@SuppressWarnings({ "restriction", "unused" })
public class AddFlowAction extends NCAction {
	protected static final long serialVersionUID = 1L;
	protected PfAddInfo actinfo = null;

	protected BillForm billform = null;

	protected String nodekey = null;
	protected ArapAddFlowMenuAciton menuAction;
	protected boolean isNodeNull;
	protected AddAction addaction = null;
	private TransferBillViewProcessor transferBillViewProcessor;

	public String getNodekey() {
		return this.nodekey;
	}

	public void setNodekey(String nodekey) {
		this.nodekey = nodekey;
	}

	public AddFlowAction(PfAddInfo info, BillForm b, String curnodekey,
			ArapAddFlowMenuAciton menuAction) {
		if (null == info.getSrc_billtype()) {
			setBtnName(NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"2006pub_0", "02006pub-0613"));

			putValue("ShortDescription", getBtnName());
			putValue("Code", "Selfadd");
		} else {
			BilltypeVO srcbillTypevo = PfDataCache.getBillType(info
					.getSrc_billtype());

			setBtnName(info.getSrc_billtypename());
			putValue("ShortDescription", getBtnName());
			putValue("Code", "Selfadd" + srcbillTypevo.getBilltypename());
		}

		this.actinfo = info;
		this.billform = b;
		this.menuAction = menuAction;
		this.nodekey = curnodekey;
		this.addaction = new AddAction();
		addModelListener(b);
		this.addaction.setModel(this.billform.getModel());
	}

	private void addModelListener(BillForm b) {
		if ((b != null) && (b.getModel() != null))
			b.getModel().addAppEventListener(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doAction(ActionEvent e) throws Exception {
		// XBX新增自制加角色限制
		String billtype = this.billform.getNodekey();// 单据类型
		String act = this.menuAction.getCode();

		if (billtype != null &&("F3-Cxx-01".equals(billtype) || "F1-Cxx-01".equals(billtype))) {
			String fg = "0";
			String user = InvocationInfoProxy.getInstance().getUserId();// 当前用户主键
			UserVO userVO = (UserVO) HYPubBO_Client.queryByPrimaryKey(
					UserVO.class, user);
			Object conforg = (Object) HYPubBO_Client.findColValue("bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
							+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
							+ " and code = '" + userVO.getPk_org() + "'");
			if (conforg != null) {
				GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
				String querySql = "select c.cuserid, a.pk_role, a.role_name,c.user_name from sm_role a left join sm_user_role b  on a.pk_role=b.pk_role "
						+ " left join sm_user c on c.cuserid=b.cuserid  where a.role_code = 'FKD001'";
				List<Object[]> resultList = getDao.query(querySql);
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
					throw new BusinessException("不允许自制");
				}
			}			
		}
		// 结束
		String cuserid = WorkbenchEnvironment.getInstance().getLoginUser()
				.getCuserid();

		if (this.actinfo.isMakeflag()) {
			String currTradeType = this.billform.getNodekey();
			((IAutoShowUpComponent) this.billform).showMeUp();
			if ((currTradeType != null) && (this.billform.getNodekey() != null)
					&& (!currTradeType.equals(this.billform.getNodekey()))) {
				this.billform.getModel().fireEvent(
						new NodekeyEvent(this.billform.getNodekey(),
								currTradeType));
			}

			this.addaction.doAction(e);
			fireNodeKeyEvent();
			setDefaultValue(null);

			setHeadTailItemDefaultValue(this.billform.getBillCardPanel()
					.getBillData().getHeadTailItems());
		} else {
			String pk_group = WorkbenchEnvironment.getInstance().getGroupVO()
					.getPk_group();

			PfButtonClickContext context = new PfButtonClickContext();
			context.setUserObj(this.actinfo.getBusitypes());

			PfUtilClient.childButtonClickedWithBusi(
					this.actinfo.getSrc_billtype(), pk_group, cuserid,
					this.nodekey, this.billform, context, null,
					this.actinfo.getBusitypes());

			if (!PfUtilClient.isCloseOK())
				return;
			AggregatedValueObject[] oldVos = PfUtilClient.getRetOldVos();
			AggregatedValueObject[] vos = PfUtilClient.getRetVos(false);

			String billnostr = "";
			if (this.actinfo.getSrc_billtype().equals("36D1")) {
				for (AggregatedValueObject vo : oldVos) {
					Object opsrctype = vo.getParentVO().getAttributeValue(
							"opsrctype");

					if ((opsrctype == null)
							|| (!opsrctype.toString().equals("1")))
						continue;
					billnostr = billnostr
							+ vo.getParentVO().getAttributeValue("vbillno")
									.toString() + ",";
				}

			}

			if (FIStringUtil.isNotEmpty(billnostr)) {
				ExceptionUtils.wrappBusinessException("NC重量端拉单操作不能拉取轻量端数据："
						+ billnostr.substring(0, billnostr.length() - 1));
			}

			dealBills(vos, oldVos, null);
			if ((vos.length > 1) && (this.menuAction.getList() != null)) {
				this.menuAction.getList().showMeUp();
			}

			showbills(vos);

			controlFieldsEnable(vos);

			if (this.billform instanceof ArapBillCardForm) {
				BillOrgPanel billOrgPanel = ((ArapBillCardForm) this.billform)
						.getBillOrgPanel();

				billOrgPanel.setEnabled(true);
			}

			this.billform.getBillCardPanel().getHeadItem("rate")
					.setEnabled(true);

			ArapBillCardForm arapBillCardForm = (ArapBillCardForm) this.billform;
			arapBillCardForm.setTop_itemid(new MapList());
			arapBillCardForm.setTop_billid(new HashMap());
			arapBillCardForm.setTop_money(new HashMap());

			for (AggregatedValueObject vo : vos) {
				MapList top_itemid = ((BaseAggVO) vo).getHeadVO()
						.getTop_itemid();

				if (top_itemid != null) {
					for (Object k : top_itemid.keySet()) {
						arapBillCardForm.getTop_itemid().putAll(k.toString(),
								top_itemid.get(k));
					}
				}

				arapBillCardForm.getTop_billid().putAll(
						((BaseAggVO) vo).getHeadVO().getTop_billid());

				arapBillCardForm.getTop_money().putAll(
						((BaseAggVO) vo).getHeadVO().getTop_money());
			}
		}
	}

	private void controlFieldsEnable(AggregatedValueObject[] vos) {
		if (ArrayUtils.isEmpty(vos)) {
			return;
		}
		if (isNeedMoneyControl((BaseAggVO) vos[0])) {
			setMoneyItemEnabled(false);
		}
		if (isNeedCurrControl((BaseAggVO) vos[0])) {
			setItemEnble(new String[] { "pk_currtype", "rate" }, false);
		}

		if ((!"F3".equals(((BaseAggVO) vos[0]).getHeadVO().getPk_billtype()))
				|| (!"F0".equals(((BaseAggVO) vos[0]).getItems()[0]
						.getTop_billtype()))) {
			return;
		}

		setItemEnble(new String[] { "pk_currtype", "objtype" }, false);
	}

	private boolean isNeedMoneyControl(BaseAggVO vos) {
		BaseBillVO vo = (BaseBillVO) vos.getParentVO();

		return (vo.getSrc_syscode() != null)
				&& (vo.getSrc_syscode().intValue() != BillEnumCollection.FromSystem.AR.VALUE
						.intValue())
				&& (vo.getSrc_syscode().intValue() != BillEnumCollection.FromSystem.AP.VALUE
						.intValue())
				&& (vo.getSrc_syscode().intValue() != BillEnumCollection.FromSystem.CT.VALUE
						.intValue())
				&& (vo.getSrc_syscode().intValue() != BillEnumCollection.FromSystem.CMP.VALUE
						.intValue());
	}

	private boolean isNeedCurrControl(BaseAggVO baseAggVO) {
		String topBilltype = baseAggVO.getItems()[0].getTop_billtype();

		return (StringUtils.isNotEmpty(topBilltype))
				&& (topBilltype.trim().startsWith("4A"));
	}

	private void setMoneyItemEnabled(boolean flag) {
		String[] itemArray = { "money_de", "local_money_de", "local_notax_cr",
				"notax_de", "local_notax_de", "money_cr", "local_money_cr",
				"local_tax_cr", "notax_cr", "local_notax_cr" };

		for (String itemName : itemArray) {
			BillItem item = this.billform.getBillCardPanel().getBodyItem(
					itemName);

			if (item != null)
				item.setEnabled(flag);
		}
	}

	private void setItemEnble(String[] billItems, boolean flag) {
		for (String itemName : billItems) {
			BillItem item = this.billform.getBillCardPanel().getBodyItem(
					itemName);

			if (item != null) {
				item.setEnabled(flag);
			}
			item = this.billform.getBillCardPanel().getHeadItem(itemName);
			if (item != null)
				item.setEnabled(flag);
		}
	}

	private void setHeadTailItemDefaultValue(BillItem[] items) {
		if (items != null)
			for (int i = 0; i < items.length; ++i) {
				BillItem item = items[i];
				Object value = item.getDefaultValueObject();
				if (value != null)
					item.setValue(value);
			}
	}

	private void fireNodeKeyEvent() {
		if (this.menuAction.getBillform() instanceof MutilTransBillForm) {
			String defaultOrgUnit = null;
			try {
				defaultOrgUnit = OrgSettingAccessor.getDefaultOrgUnit();
			} catch (Exception e) {
				ExceptionHandler.consume(e);
			}

			String pk_org = (StringUtils.isNotEmpty(defaultOrgUnit)) ? defaultOrgUnit
					: (String) WorkbenchEnvironment.getInstance()
							.getClientCache(
									"ARAP_DEFAULT_ORG"
											+ PfDataCache.getBillType(
													getNodekey())
													.getParentbilltype());

			AggregatedValueObject aggvo = (AggregatedValueObject) this.menuAction
					.getModel().getSelectedData();
			try {
				String selectedOrg = ((aggvo == null) || (aggvo.getParentVO() == null)) ? null
						: (String) (String) aggvo.getParentVO()
								.getAttributeValue("pk_org");

				if ((StringUtils.isNotEmpty(pk_org))
						&& (StringUtils.isNotEmpty(selectedOrg))
						&& (pk_org.equals(selectedOrg))
						&& (Arrays.asList(ArapUiUtil
								.getPermissionOrgs(this.billform.getModel()
										.getContext())).contains(pk_org))) {
					MutilTransBillForm mutilTransBillForm = (MutilTransBillForm) this.menuAction
							.getBillform();

					String vidByOID = getVIDByOID(pk_org);
					if (vidByOID != null) {
						mutilTransBillForm.getBillOrgPanel().setPkOrg(vidByOID);

						this.menuAction.getModel().getContext()
								.setPk_org(pk_org);
						this.menuAction.getModel().fireEvent(
								new OrgChangedEvent(null, pk_org));

						if (mutilTransBillForm.getBillCardPanel() == null) {
							mutilTransBillForm.initUI();
						}
						if (!mutilTransBillForm.isEditable())
							mutilTransBillForm.setEditable(true);
					}
				}
			} catch (Exception e) {
				ExceptionHandler.handleRuntimeException(e);
			}
		}
	}

	void setDefaultValue(AggregatedValueObject[] vos) {
		MutilTransBillForm mutilTransBillForm = (MutilTransBillForm) this.menuAction
				.getBillform();

		if (mutilTransBillForm.getDefValueItf() != null) {
			if (mutilTransBillForm.getBillCardPanel() == null) {
				mutilTransBillForm.initUI();
			}
			AggregatedValueObject valueObject = mutilTransBillForm
					.getDefValueItf().getDefaultVO();

			if (vos != null) {
				String pk_busitype = (String) valueObject.getParentVO()
						.getAttributeValue("pk_busitype");

				for (AggregatedValueObject valObject : vos) {
					valObject.getParentVO().setAttributeValue("pk_busitype",
							pk_busitype);
				}
			}

			((ArapBillCardForm) this.billform).reloadBillTemplate(valueObject);
			mutilTransBillForm.getBillCardPanel().setBillValueVO(valueObject);
			BillForm arapBillCardForm = this.menuAction.getBillform();
			if (arapBillCardForm instanceof ArapBillCardForm)
				((ArapBillCardForm) arapBillCardForm).resetHeadDigit();
		}
	}

	@SuppressWarnings("rawtypes")
	private String getVIDByOID(String OID) {
		String sql = " select pk_vid from org_orgs_v where pk_org = '" + OID
				+ "' order by vno desc ";

		Vector vid = DBCacheFacade.getFromDBCache(sql);

		if ((!CollectionUtils.isEmpty(vid))
				&& (!CollectionUtils.isEmpty((Vector) vid.get(0)))) {
			return (String) ((Vector) vid.get(0)).get(0);
		}

		return null;
	}

	protected void dealBills(AggregatedValueObject[] vos,
			AggregatedValueObject[] oldVos, String busitype) throws Exception {
		String tradetype = this.billform.getNodekey();
		String pkBilltypeid = PfDataCache.getBillType(tradetype)
				.getPk_billtypeid();

		for (int i = 0; i < vos.length; ++i) {
			AggregatedValueObject vo = vos[i];
			CircularlyAccessibleValueObject[] items = vo.getChildrenVO();
			CircularlyAccessibleValueObject head = vo.getParentVO();

			head.setAttributeValue("pk_tradetype", tradetype);
			head.setAttributeValue("pk_tradetypeid", pkBilltypeid);
			head.setAttributeValue("billno", null);
			head.setAttributeValue("isflowbill", UFBoolean.TRUE);
			head.setAttributeValue("billmaker", InvocationInfoProxy
					.getInstance().getUserId());

			head.setAttributeValue("objtype",
					(items.length > 0) ? items[0].getAttributeValue("objtype")
							: null);

			for (int j = 0; j < items.length; ++j) {
				CircularlyAccessibleValueObject item = items[j];

				dealItem(items[j], head);
				item.setAttributeValue("pk_tradetype", tradetype);
				item.setAttributeValue("pk_tradetypeid", pkBilltypeid);

				item.setAttributeValue("billno", null);
				item.setAttributeValue("occupationmny", UFDouble.ZERO_DBL);
			}

			String top_billtype = (items.length == 0) ? null
					: (String) items[0].getAttributeValue("top_billtype");

			if ((StringUtils.isNotEmpty(top_billtype))
					&& (ArapBillPubUtil.isArapBilltype(top_billtype))) {
				head.setAttributeValue(
						"src_syscode",
						(ArapBillPubUtil.isARSysBilltype(top_billtype)) ? BillEnumCollection.FromSystem.AR.VALUE
								: BillEnumCollection.FromSystem.AP.VALUE);
			}

			head.setPrimaryKey(null);

			ArapBillPubUtil.processMoneyOnlySum(vo);

			setBillField(vo, busitype);
		}
	}

	protected CircularlyAccessibleValueObject dealItem(
			CircularlyAccessibleValueObject item,
			CircularlyAccessibleValueObject head) throws Exception {
		return item;
	}

	protected void setBillField(AggregatedValueObject vo, String busitype)
			throws Exception {
		CircularlyAccessibleValueObject head = vo.getParentVO();
		head.setAttributeValue("billstatus",
				BillEnumCollection.BillSatus.Save.VALUE);
		if ((null == busitype)
				|| (null != head.getAttributeValue("pk_busitype")))
			return;
		head.setAttributeValue("pk_busitype", busitype);
	}

	protected void showbills(AggregatedValueObject[] vos) throws Exception {
		ArapTransferBillDataLogic logic = new ArapTransferBillDataLogic();
		logic.setBillForm(getTransferBillViewProcessor().getBillForm());
		getTransferBillViewProcessor().setTransferLogic(logic);
		getTransferBillViewProcessor().processBillTransfer(vos);
	}

	public TransferBillViewProcessor getTransferBillViewProcessor() {
		return this.transferBillViewProcessor;
	}

	public void setTransferBillViewProcessor(
			TransferBillViewProcessor transferBillViewProcessor) {
		this.transferBillViewProcessor = transferBillViewProcessor;
	}

	protected void setAddaction(AddAction addaction) {
		this.addaction = addaction;
	}
}