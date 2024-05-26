package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.cmp.pub.exception.ExceptionHandler;
import nc.data.param.CommonParam;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.ui.pcm.utils.GetDao;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.cmp.bill.BillAggVO;
import nc.vo.cmp.bill.BillDetailVO;
import nc.vo.cmp.bill.BillVO;
import nc.vo.cmp.bill.CommonContext;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 付款结算修改后保存
@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
public class N_F5_EDIT extends nc.bs.pub.compiler.AbstractCompiler2 {
	private Hashtable<String, Object> m_methodReturnHas = new Hashtable();
	private Hashtable<String, Object> m_keyHas = null;
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名

	public N_F5_EDIT() {
	}

	public Object runComClass(PfParameterVO paraVo) throws BusinessException {
		try {
			if (paraVo.m_preValueVos == null)
				return null;
			Object obj = null;
			this.m_tmpVo = paraVo;

			setParameter("context", paraVo.m_preValueVos);

			obj = runClass(
					"nc.bs.cmp.bill.actions.PayBillEditSaveBatchBSAction",
					"updateVOs", "&context:nc.vo.pub.AggregatedValueObject[]",
					paraVo, this.m_keyHas);

			if (obj != null) {
				this.m_methodReturnHas.put("updateVOs", obj);
				// XBX
				BillAggVO[] temps = (BillAggVO[]) obj;
				// for(BillAggVO temp:temps){
				try {
					for (int i = 0; i < temps.length; i++) {
						BillAggVO temp = temps[i];
						BillVO hVO = (BillVO) temp.getParentVO();
						OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(
								OrgVO.class, hVO.getPk_org());
						if ("4".equals(orgVO.getDef2())) {
							if (StringUtils.isEmpty(hVO.getDef6())) {
								String strWhere = " nvl(dr,0) = 0 and pk_bill ='"
										+ hVO.getPrimaryKey() + "'";
								WorkFlowBill[] workFlowBills = (WorkFlowBill[]) new HYPubBO()
										.queryByCondition(WorkFlowBill.class,
												strWhere);

								if (workFlowBills == null
										|| workFlowBills.length <= 0
										|| hVO.getDef3() == null

										|| "N".equals(hVO.getDef3())) {
									OAFlowVO oaVo = OaWorkFlowUtil
											.getOAFlowInfo(hVO.getPk_org(),
													"F5");
									if (oaVo != null && oaVo.getIsdr() == 0) {
										WorkFId = oaVo.getFlowid();
										TableName = oaVo.getTablename();
										BillDetailVO[] bvos = (BillDetailVO[]) temp
												.getChildren(BillDetailVO.class);
										// 获取主表数据
										JSONArray headData = getMainMap(hVO,
												bvos[0].getPk_supplier(),
												bvos[0].getPk_balatype(),
												bvos[0].getPk_account(),
												bvos[0].getPk_dept(),
												bvos[0].getPk_busiman(),
												bvos[0].getPk_currtype(),
												bvos[0].getPk_oppaccount());
										// 获取子表数据
										JSONArray bodyData = getDtaileDataMap(temp);
										// 构造workflow信息
										OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
										workFlowVO.setPrimaryKey(temp
												.getPrimaryKey());
										workFlowVO
												.setPkGroup(hVO.getPk_group());
										workFlowVO.setPkOrg(hVO.getPk_org());
										workFlowVO.setBillMaker(hVO
												.getBillmaker());
										workFlowVO.setCreator(hVO.getCreator());
										workFlowVO.setBillCode("F5");
										// 临时写死，后期改为接口获取
										workFlowVO.setWorkflowId(OaWorkFlowUtil
												.getOAFlowID(WorkFId));
										workFlowVO.setWorkflowName("付款结算");
										WorkFlowBill bill = OaWorkFlowUtil
												.getWorkFlowBill(workFlowVO);
										// WorkFlowBill的自定义项3 判断是否港华单据
										bill.setDef3("ZT");
										// 制单人身份证号
										UserVO userVO = (UserVO) getHyPubBO()
												.queryByPrimaryKey(
														UserVO.class,
														hVO.getBillmaker());
										String idCard = (String) getHyPubBO()
												.findColValue(
														"bd_psndoc",
														"id",
														"nvl(dr,0) = 0 and pk_psndoc='"
																+ userVO.getPk_psndoc()
																+ "'");
										bill.setDef5(idCard);
										// 调用OA工具类同步数据至OA
										OaWorkFlowUtil.sendOaData(headData,
												bodyData, bill);
										hVO.setDef3(UFBoolean.TRUE + "");
										hVO.setStatus(VOStatus.NEW);
										new HYPubBO().update(hVO);
									}
								}
							}
						}
						// 调用东港税务系统同步单据状态
						IArapForDGSWService util = (IArapForDGSWService) NCLocator.getInstance().lookup(
								IArapForDGSWService.class);
						// 单据号，组织主键，事件类型，单据类型
						JSONObject res = util.sendBillByNCBill(hVO.getPrimaryKey(),
								hVO.getPk_org(), CommonParam.COMMIT, hVO.getBill_type());
						if(!"Y".equals(res.getString("success"))){
							throw new BusinessException("提交税务系统报错："+res.getString("errinfo"));
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					throw ExceptionHandler.handleException(getClass(), e);
				}
			}
			return obj;
		} catch (Exception ex) {
			throw ExceptionHandler.handleException(getClass(), ex);
		}
	}

	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	private JSONArray getDtaileDataMap(BillAggVO temp) throws BusinessException {
		// 获取详细信息
		BillDetailVO[] bvos = (BillDetailVO[]) temp
				.getChildren(BillDetailVO.class);
		Map bodyMap = getBody(bvos);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(BillDetailVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (BillDetailVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 资金计划项目
			String zjjhxm = (String) getHyPubBO().findColValue(
					"bd_fundplan",
					"name",
					"nvl(dr,0) = 0 and pk_fundplan = '"
							+ temp.getBankroll_projet() + "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zjjhxm",
					zjjhxm));
			// 结算方式
			String jsfs = (String) getHyPubBO().findColValue(
					"bd_balatype",
					"name",
					"nvl(dr,0) = 0 and pk_balatype = '" + temp.getPk_balatype()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("jsfs",
					jsfs));
			// 付款银行账户
			if (temp.getPk_oppaccount() != null) {
				BankAccSubVO yhvo = (BankAccSubVO) getHyPubBO()
						.queryByPrimaryKey(BankAccSubVO.class,
								temp.getPk_oppaccount());
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fkyhzh", yhvo.getAccnum()));
			}
			// 资金类别
			if (temp.getDef10() != null) {
				String zjlb = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef10()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zjlb", zjlb));
			}

			// 票据类型
			if (temp.getNote_type() != null) {
				String pjlx = (String) getHyPubBO().findColValue(
						"bd_notetype",
						"name",
						"nvl(dr,0) = 0 and   pk_notetype  = '"
								+ temp.getNote_type() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"pjlx", pjlx));
			}
			/*
			 * //币种 if(null!= temp.getPk_currtype()){ String bz=(String)
			 * getHyPubBO().findColValue( "bd_currtype", "name",
			 * "nvl(dr,0) = 0 and  pk_currtype = '" + temp.getPk_currtype() +
			 * "'");
			 * workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz",
			 * bz)); }
			 */
			// 收款银行账号
			if (temp.getPk_account() != null) {
				String yhname = (String) getHyPubBO().findColValue(
						"bd_bankaccsub",
						"accnum",
						"nvl(dr,0) = 0 and pk_bankaccsub  = '"
								+ temp.getPk_account() + "'");

				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"skyhzh", yhname));
			}
			// 部门
			if (temp.getPk_dept() != null) {
				String bm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and    pk_dept   = '"
								+ temp.getPk_dept() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bm",
						bm));
			}
			GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
			String yhlhhh = "select a.code from bd_asslinenum a LEFT JOIN "
					+ "bd_bankdoc b on a.pk_asslinenum=b.pk_combine LEFT JOIN bd_bankaccbas c "
					+ "on b.pk_bankdoc=c.pk_bankdoc LEFT JOIN bd_bankaccsub d on c.pk_bankaccbas=d.pk_bankaccbas"
					+ " LEFT JOIN cmp_paybilldetail e on d.pk_bankaccsub= e.pk_account "
					+ "where e.pk_paybill_detail ='" + temp.getPrimaryKey()
					+ "'";

			try {
				List<Object[]> resultList = getDao.query(yhlhhh);
				String rhlhhh = "";
				for (int i = 0; i < resultList.size(); i++) {
					Object[] item = resultList.get(i);
					for (int j = 0; j < item.length; j++) {
						rhlhhh = item[j] + "";
					}
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"rhlhhh", rhlhhh));
			} catch (DAOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 现金账户
			if (temp.getMon_account() != null) {
				String xjzh = (String) getHyPubBO().findColValue(
						"bd_cashaccount",
						"name",
						"nvl(dr,0) = 0 and  pk_cashaccount   = '"
								+ temp.getMon_account() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xjzh", xjzh));
			}
			/* 现金流量项目 */
			if (null != temp.getCash_item()) {
				String xname = (String) getHyPubBO().findColValue(
						"bd_cashflow",
						"name",
						"nvl(dr,0) = 0 and pk_cashflow  = '"
								+ temp.getCash_item() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xjllxm", xname));
			}
			/* 支付人 */
			if (null != temp.getPayman()) {
				String zfr = (String) getHyPubBO().findColValue(
						"sm_user",
						"user_name",
						"nvl(dr,0) = 0 and cuserid   = '" + temp.getPayman()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zfr",
						zfr));
			}
			// 项目主键 objecttype
			if (null != temp.getObjecttype()) {
				int jydxlxint = temp.getObjecttype();
				String jydxlx = "";
				if (jydxlxint == 0) {
					jydxlx = "对象";
				} else if (jydxlxint == 1) {
					jydxlx = "供应商";
				} else if (jydxlxint == 2) {
					jydxlx = "部门";
				} else if (jydxlxint == 3) {
					jydxlx = "人员";
				} else if (jydxlxint == 4) {
					jydxlx = "散户";
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jydxlx", jydxlx + ""));
			}
			// 其他字段------end
			Map workflowRequestTableFieldsMap = new HashMap();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private JSONArray getMainMap(BillVO parentVO, String pk_supplier,
			String pk_balatype, String pk_account, String pk_dept,
			String pk_busiman, String pk_currtype, String pk_oppaccount)
			throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// 库存组织
		String stockName = (String) getHyPubBO().findColValue(
				"org_financeorg",
				"name",
				"nvl(dr,0) = 0 and pk_financeorg = '" + parentVO.getPk_org()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
		// 结算方式
		if (pk_balatype != null) {
			String jsfsbm = (String) getHyPubBO().findColValue("bd_balatype",
					"code",
					"nvl(dr,0) = 0 and pk_balatype = '" + pk_balatype + "'");
			list.add(OaWorkFlowUtil.listAddObj("jsfsbm", jsfsbm));
			String jsfs = (String) getHyPubBO().findColValue("bd_balatype",
					"name",
					"nvl(dr,0) = 0 and pk_balatype = '" + pk_balatype + "'");
			list.add(OaWorkFlowUtil.listAddObj("jsfs", jsfs));
		}
		// 供应商名称
		if (pk_supplier != null) {
			SupplierVO supplierVO = (SupplierVO) getHyPubBO()
					.queryByPrimaryKey(SupplierVO.class, pk_supplier);
			String name = supplierVO.getName();
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name", name));
		}
		// 收款银行账户
		if (pk_account != null) {
			String yhname = (String) getHyPubBO().findColValue("bd_bankaccsub",
					"accnum",
					"nvl(dr,0) = 0 and pk_bankaccsub  = '" + pk_account + "'");
			if (yhname != null) {
				list.add(OaWorkFlowUtil.listAddObj("skyhzh", yhname));
			} else {
				list.add(OaWorkFlowUtil.listAddObj("skyhzh", pk_account));
			}
		}
		// 部门

		if (pk_dept != null) {
			String sqbmName = (String) getHyPubBO().findColValue("org_dept",
					"name", "nvl(dr,0) = 0 and pk_dept = '" + pk_dept + "'");
			list.add(OaWorkFlowUtil.listAddObj("bm", sqbmName));
		}

		if (null != parentVO.getObjecttype()) {
			int jydxlxint = parentVO.getObjecttype();
			String jydxlx = "";
			if (jydxlxint == 0) {
				jydxlx = "对象";
			} else if (jydxlxint == 1) {
				jydxlx = "供应商";
			} else if (jydxlxint == 2) {
				jydxlx = "部门";
			} else if (jydxlxint == 3) {
				jydxlx = "人员";
			} else if (jydxlxint == 4) {
				jydxlx = "散户";
			}
			list.add(OaWorkFlowUtil.listAddObj("jydxlx", jydxlx + ""));
		}

		// 业务员
		if (pk_busiman != null) {
			String ywy = (String) getHyPubBO().findColValue("bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc = '" + pk_busiman + "'");
			list.add(OaWorkFlowUtil.listAddObj("ywy", ywy));
		}
		// 资金收付款类型
		if (parentVO.getDef1() != null) {
			String zjsfklxbm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef1()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("zjsfklxbm", zjsfklxbm));
			String zjsfklx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef1()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("zjsfklx", zjsfklx));
		}
		// 付款现金流量项目
		if (parentVO.getDef5() != null) {
			String fkxjllxmbm = (String) getHyPubBO().findColValue(
					"bd_cashflow",
					"code",
					"nvl(dr,0) = 0 and pk_cashflow = '" + parentVO.getDef5()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fkxjllxmbm", fkxjllxmbm));
			String fkxjllxm = (String) getHyPubBO().findColValue(
					"bd_cashflow",
					"name",
					"nvl(dr,0) = 0 and pk_cashflow = '" + parentVO.getDef5()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fkxjllxm", fkxjllxm));
		}
		// 费用类型
		if (parentVO.getDef2() != null) {
			String fylxbm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef2()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fylxbm", fylxbm));
			/*
			 * String fylx = (String) getHyPubBO().findColValue( "bd_defdoc",
			 * "name", "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef2() +
			 * "'");
			 */
			list.add(OaWorkFlowUtil.listAddObj("fylx", fylxbm));
		}
		// 工会报销类型
		if (parentVO.getDef19() != null) {
			String ghbxlxbm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef19()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("ghbxlxbm", ghbxlxbm));
			String ghbxlx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef19()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("ghbxlx", ghbxlx));
		}
		// 付款银行账户
		if (pk_oppaccount != null) {
			BankAccSubVO yhvo = (BankAccSubVO) getHyPubBO().queryByPrimaryKey(
					BankAccSubVO.class, pk_oppaccount);
			list.add(OaWorkFlowUtil.listAddObj("fkyhzh", yhvo.getAccnum()));
		}
		// 付款交易类型
		if (null != parentVO.getPk_tradetypeid()) {
			String fkjylx = (String) getHyPubBO().findColValue(
					"bd_billtype",
					"billtypename",
					"nvl(dr,0) = 0 and  pk_billtypeid = '"
							+ parentVO.getPk_tradetypeid() + "'");
			list.add(OaWorkFlowUtil.listAddObj("fkjylx", fkjylx));
		}
		// 单据状态
		if (null != parentVO.getBill_status()) {
			int djztint = parentVO.getBill_status();
			String djzt = "";
			if (djztint == -99) {
				djzt = "暂存";
			} else if (djztint == 0) {
				djzt = "审批失败";
			} else if (djztint == -1) {
				djzt = "保存";
			} else if (djztint == 1) {
				djzt = "审批通过";
			} else if (djztint == 8) {
				djzt = "签字";
			} else if (djztint == 2) {
				djzt = "审批中";
			} else if (djztint == 9) {
				djzt = "未确认";
			} else if (djztint == 10) {
				djzt = "关闭";
			} else if (djztint == 11) {
				djzt = "未保存";
			} else if (djztint == -10) {
				djzt = "自由态";
			}
			list.add(OaWorkFlowUtil.listAddObj("djzt", djzt + ""));
		}
		// 生效状态
		if (null != parentVO.getEffect_flag()) {
			int sxztint = parentVO.getEffect_flag();
			String sxzt = "";
			if (sxztint == 0) {
				sxzt = "未生效";
			} else if (sxztint == 5) {
				sxzt = "未生成凭证";
			} else if (sxztint == 10) {
				sxzt = "生效";
			}
			list.add(OaWorkFlowUtil.listAddObj("sxzt", sxzt + ""));
		}
		// 支付状态
		if (null != parentVO.getPaystatus()) {
			int zfztint = parentVO.getPaystatus();
			String zfzt = "";
			if (zfztint == 1) {
				zfzt = "无";
			} else if (zfztint == 2) {
				zfzt = "转账成功";
			} else if (zfztint == 3) {
				zfzt = "转账失败";
			} else if (zfztint == 4) {
				zfzt = "转账中";
			} else if (zfztint == 3) {
				zfzt = "部分成功";
			}
			list.add(OaWorkFlowUtil.listAddObj("zfzt", zfzt + ""));
		}
		// 币种
		if (null != pk_currtype) {
			String bz = (String) getHyPubBO().findColValue("bd_currtype",
					"name",
					"nvl(dr,0) = 0 and  pk_currtype = '" + pk_currtype + "'");
			list.add(OaWorkFlowUtil.listAddObj("bz", bz));
		}
		// 票据类型
		if (null != parentVO.getNote_type()) {
			String pjlx = (String) getHyPubBO().findColValue(
					"bd_notetype",
					"name",
					"nvl(dr,0) = 0 and  pk_notetype = '"
							+ parentVO.getNote_type() + "'");
			list.add(OaWorkFlowUtil.listAddObj("pjlx", pjlx));
		}
		// 票据号
		if (null != parentVO.getNote_no()) {
			String pjh = (String) getHyPubBO().findColValue(
					"fbm_register",
					"fbmbillno",
					"nvl(dr,0) = 0 and  pk_register = '"
							+ parentVO.getNote_no() + "'");
			list.add(OaWorkFlowUtil.listAddObj("pjh", pjh));
		}
		// 现金账户
		if (null != parentVO.getMon_account()) {
			String xjzh = (String) getHyPubBO().findColValue(
					"bd_cashaccount",
					"name",
					"nvl(dr,0) = 0 and    pk_cashaccount = '"
							+ parentVO.getMon_account() + "'");
			list.add(OaWorkFlowUtil.listAddObj("xjzh", xjzh));
		}
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());

		// 制单人用户编码
		list.add(OaWorkFlowUtil.listAddObj("usercode", userVO.getUser_code()));
		// 制单人名称
		list.add(OaWorkFlowUtil.listAddObj("username", userVO.getUser_name()));
		// 制单人身份证号
		String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == idCard) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		list.add(OaWorkFlowUtil.listAddObj("userid", idCard));

		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;

	}

	public String getCodeRemark() {
		return NCLangRes4VoTransl.getNCLangRes().getStrByID("3607mng_0",
				"03607mng-0360");
	}

	protected void setParameter(String key, Object val) {
		if (this.m_keyHas == null) {
			this.m_keyHas = new Hashtable();
		}
		if (val != null) {
			this.m_keyHas.put(key, val);
		}
	}

	protected void setContext(CommonContext context) throws BusinessException {
	}

}
