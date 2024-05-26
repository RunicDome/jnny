package nc.ui.ct.purdaily.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.bc.pmpub.project.ProjectHeadVO;
import nc.vo.bd.currtype.CurrtypeVO;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.material.measdoc.MeasdocVO;
import nc.vo.bd.payment.PaymentVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.bd.taxcode.TaxcodeVO;
import nc.vo.ct.price.entity.CtPriceHeaderVO;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.ct.purdaily.entity.CtPuTermVO;
import nc.vo.ct.purdaily.entity.CtPuVO;
import nc.vo.org.DeptVO;
import nc.vo.org.FinanceOrgVO;
import nc.vo.org.StockOrgVO;
import nc.vo.pp.hqhp.qpschm.entity.QPSchmHeaderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.sm.UserVO;
import net.sf.json.JSONArray;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

@SuppressWarnings("restriction")
public class GetCGUtils {
	public static void getCGHTMsg(AggCtPuVO billVO) throws BusinessException {
		// TODO 自动生成的方法存根
		String mainTaleInfo = ""; // 主表信息
		// 取主表数据
		CtPuVO headVO = (CtPuVO) billVO.getParentVO();
		DefdocVO defVO = (DefdocVO) HYPubBO_Client.queryByPrimaryKey(
				DefdocVO.class, headVO.getVdef12() + ""); // 采购合同模板自定义档案
		if (defVO == null) {
			Logger.error("未选择采购合同模板！");
			MessageDialog.showErrorDlg(null, "提示", "未选择采购合同模板！");
			return;
		}
		if (!headVO.equals(null)) {
			mainTaleInfo = getMainInfo(headVO);
		}
		Logger.error("采购合同主表数据：" + mainTaleInfo);
		String[] ChildrenVOs = billVO.getTableCodes();

		String contractPub = ""; // 合同基本信息
		String ContractTerm = "";// 合同条款
		// 获取当前所编辑的VO
		// String[] tableCode = billVO.getTableCodes();

		// 取合同基本信息
		CircularlyAccessibleValueObject[] ChildrenCtPuBVOs = billVO
				.getTableVO("pk_ct_pu_b");

		if (ChildrenVOs != null) {
			contractPub = getContractPuBInfo(ChildrenCtPuBVOs);
		}

		Logger.error("采购合同基本信息：" + contractPub);

		// 合同条款ct_pu_term
		CircularlyAccessibleValueObject[] ChildrenTermVOs = billVO
				.getTableVO("pk_ct_pu_term");
		if (ChildrenTermVOs != null) {
			ContractTerm = getContractTermInfo(ChildrenTermVOs);
		}
		Logger.error("采购合同条款：" + ContractTerm);

		// 取单据的附件信息
		// String filePath = getPahtFile(headVO.getPk_ct_pu());
		Logger.error("采购合同附件信息：" + "");
		/* 下一步调用接口 */
		// String updateNoticeUrl =
		// "http://172.18.128.201:8090/zhuomaService/sendFormatDataToPlatfm";
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String chsql = "SELECT NAME FROM RL_CBSNAME WHERE ID = 33";
		List<Object[]> ls = (List<Object[]>) getDao.query(chsql);
		try {
			updateNotice(ls.get(0)[0] + "", defVO.getCode(), mainTaleInfo,
					contractPub, ContractTerm, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取主表信息
	private static String getMainInfo(CtPuVO headVO) throws UifException {
		JSONObject json = new JSONObject();

		json.put("Pk_ct_pu", headVO.getPk_ct_pu()); // 单据主键
		json.put("Vbillcode", headVO.getVbillcode()); // 合同编号

		json.put("Ctname", headVO.getCtname()); // 合同名称
		json.put("Ctrantypeid", headVO.getCtrantypeid()); // 合同类型
		BilltypeVO billtypeVO = (BilltypeVO) HYPubBO_Client.queryByPrimaryKey(
				BilltypeVO.class, headVO.getCtrantypeid());
		if (billtypeVO != null)
			json.put("CtrantypeName", billtypeVO.getBilltypename() + "");// 合同类型名称
		else
			json.put("CtrantypeName", "");// 合同类型名称

		json.put("Version", headVO.getVersion() + ""); // 版本号
		json.put("Subscribedate", headVO.getSubscribedate() + ""); // 签订日期

		json.put("Version", headVO.getVersion() + "");
		json.put("Valdate", headVO.getValdate() + ""); // 计划生效
		json.put("Invallidate", headVO.getInvallidate() + ""); // 计划终止
		json.put("Custunit", headVO.getCustunit() + ""); // 对方单位说明

		json.put("Cvendorid", headVO.getCvendorid() + "");// 供应商
		SupplierVO supergvo = (SupplierVO) HYPubBO_Client.queryByPrimaryKey(
				SupplierVO.class, headVO.getCvendorid() + "");
		if (supergvo != null)
			json.put("CvendorName", supergvo.getName() + ""); // 供应商名称
		else
			json.put("CvendorName", ""); // 供应商名称

		json.put("Personnelid", headVO.getPersonnelid() + ""); // 人员ID
		PsndocVO psndocVO = (PsndocVO) HYPubBO_Client.queryByPrimaryKey(
				PsndocVO.class, headVO.getPersonnelid() + ""); // 人员名称
		if (psndocVO != null)
			json.put("PersonnelName", psndocVO.getName() + "");// 人员名称
		else
			json.put("PersonnelName", "");

		json.put("Depid", headVO.getDepid() + ""); // 部门
		DeptVO deptVO = (DeptVO) HYPubBO_Client.queryByPrimaryKey(DeptVO.class,
				headVO.getDepid() + ""); // 采购部门名称
		if (deptVO != null)
			json.put("DepName", deptVO.getName() + "");
		else
			json.put("DepName", "");

		json.put("Deliaddr", headVO.getDeliaddr() + ""); // 交货地点
		json.put("Ccurrencyid", headVO.getCcurrencyid() + ""); // 币种
		CurrtypeVO currtypeVO = (CurrtypeVO) HYPubBO_Client.queryByPrimaryKey(
				CurrtypeVO.class, headVO.getCcurrencyid() + "");
		if (currtypeVO != null)
			json.put("CcurrencyName", currtypeVO.getName() + ""); // 币种名称
		else
			json.put("CcurrencyName", ""); // 币种名称
		json.put("Mexchangerate ", headVO.getNexchangerate() + ""); // 折本汇率

		json.put("Pk_payterm", headVO.getPk_payterm() + ""); // 付款协议
		PaymentVO paymentVO = (PaymentVO) HYPubBO_Client.queryByPrimaryKey(
				PaymentVO.class, headVO.getPk_payterm() + "");
		if (paymentVO != null)
			json.put("CcurrencyName", paymentVO.getName() + ""); // 付款协议名称
		else
			json.put("CcurrencyName", ""); // 付款协议名称

		json.put("Fstatusflag", headVO.getFstatusflag() + ""); // 单据状态
		// 0=自由，1=生效，2=审批中，3=审批通过，4=审批未通过，5=冻结，6=终止，7=提交，
		json.put("Bsc", headVO.getBsc() + ""); // 委外

		json.put("Bordernumexec", headVO.getBordernumexec() + ""); // 已生成订单量作为合同执行
		json.put("Ntotalgpamount", headVO.getNtotalgpamount() + ""); // 累积付款总额
		json.put("Ntotalastnum", headVO.getNtotalastnum() + ""); // 总数量
		json.put("Ntotalorigmny", headVO.getNtotalorigmny() + "");// 水价合计

		json.put("Dmakedate", headVO.getDmakedate() + ""); // 保证金比例
		json.put("Billmaker", headVO.getBillmaker() + ""); // 制单人ID号

		UserVO userVO = (UserVO) HYPubBO_Client.queryByPrimaryKey(UserVO.class,
				headVO.getBillmaker() + "");
		if (userVO != null)
			json.put("BillmakerName", userVO.getUser_name() + ""); // 制单人
		else
			json.put("BillmakerName", ""); // 制单人
		json.put("BillmakerTime", headVO.getDmakedate() + ""); // 制单时间

		json.put("vdef1", headVO.getVdef1() + "");
		json.put("vdef2", headVO.getVdef2() + "");
		json.put("vdef3", headVO.getVdef3() + ""); // 分管领导编码
		DefdocVO defdocVO = (DefdocVO) HYPubBO_Client.queryByPrimaryKey(
				DefdocVO.class, headVO.getVdef3() + "");
		if (defdocVO != null)
			json.put("vdef3Name", defdocVO.getName() + "");
		else
			json.put("vdef3Name", ""); // 分管领导名称

		// json.put("vdef4", headVO.getVdef4() + "");
		SupplierVO supplierVO = (SupplierVO) HYPubBO_Client.queryByPrimaryKey(
				SupplierVO.class, headVO.getVdef4() + ""); //
		if (supplierVO != null) {
			json.put("vdef4", headVO.getVdef4() + "");
			json.put("supplierName", supplierVO.getName() + "");
			json.put("supplierCode", supplierVO.getCode() + "");
		} else {
			json.put("vdef4", "");
			json.put("supplierName", "");
			json.put("supplierCode", "");
		}
		System.out.println("供应商：" + json.getString("vdef4") + "--"
				+ json.getString("supplierName") + "--"
				+ json.getString("supplierCode"));

		json.put("vdef5", headVO.getVdef5() + "");
		json.put("vdef6", headVO.getVdef6() + "");// 项目经理
		DefdocVO defdocVOXMJL = (DefdocVO) HYPubBO_Client.queryByPrimaryKey(
				DefdocVO.class, headVO.getVdef6() + "");
		if (defdocVOXMJL != null)
			json.put("vdef6Name", defdocVOXMJL.getName() + "");
		else
			json.put("vdef6Name", ""); // 项目经理名称
		System.out.println("项目经理名称为：" + json.get("vdef6Name") + "");
		json.put("vdef7", headVO.getVdef7() + "");
		json.put("vdef8", headVO.getVdef8() + "");
		json.put("vdef9", headVO.getVdef9() + "");
		json.put("vdef10", headVO.getVdef10() + "");

		DefdocVO defdocvdef10VO = (DefdocVO) HYPubBO_Client.queryByPrimaryKey(
				DefdocVO.class, headVO.getVdef10() + "");
		// if ("1".equals(headVO.getVdef10()))
		if (defdocvdef10VO != null)
			json.put("vdef10Name", defdocvdef10VO.getName()); // 是否特殊合同
		else
			json.put("vdef10Name", ""); // 特殊合同
		json.put("vdef11", headVO.getVdef11() + "");
		DefdocVO defdocvdef11VO = (DefdocVO) HYPubBO_Client.queryByPrimaryKey(
				DefdocVO.class, headVO.getVdef11() + "");
		if (defdocvdef11VO != null)
			// if ("1".equals(headVO.getVdef11()))
			json.put("vdef11Name", defdocvdef11VO.getName()); // 是否模板合同
		else
			json.put("vdef11Name", ""); // 模板合同
		json.put("vdef15", headVO.getVdef15() + ""); // 招标方式
		DefdocVO defdocvdef15VO = (DefdocVO) HYPubBO_Client.queryByPrimaryKey(
				DefdocVO.class, headVO.getVdef15() + "");
		if (defdocvdef15VO != null)
			json.put("vdef15Name", defdocvdef15VO.getName() + ""); // 招标方式
		else
			json.put("vdef15Name", ""); // 招标方式
		json.put("iprintcount", headVO.getIprintcount());// 送货时间（天）
		return JSONObject.toJSONString(json,
				SerializerFeature.WriteMapNullValue);

	}

	// 获取合同基本信息
	private static String getContractPuBInfo(
			CircularlyAccessibleValueObject[] childrenVOs) throws UifException {

		JSONArray arrjson = new JSONArray();
		for (int j = 0; j < childrenVOs.length; j++) {
			JSONObject jo = new JSONObject();
			CtPuBVO conWorkVO = (CtPuBVO) childrenVOs[j];

			jo.put("Pk_material", conWorkVO.getPk_material() + "");// 物料编码
			MaterialVO materialVO = (MaterialVO) HYPubBO_Client
					.queryByPrimaryKey(MaterialVO.class,
							conWorkVO.getPk_material() + "");
			if (materialVO != null) {
				jo.put("Pk_materialName", materialVO.getName() + "");// 物料名称
				jo.put("getMaterialmnecode", materialVO.getMaterialmnecode()
						+ "");
				jo.put("PK_materialspec", materialVO.getMaterialspec() + "");// 规格
				jo.put("PK_materialtype", materialVO.getFee() + "");// 型号
			} else {
				jo.put("Pk_materialName", "");// 物料名称
				jo.put("getMaterialmnecode", ""); // 物料编码
				jo.put("PK_materialspec", "");// 规格
				jo.put("PK_materialtype", "");// 型号
			}

			jo.put("Cbprojectid", conWorkVO.getCbprojectid() + "");// 项目信息
			ProjectHeadVO projectHeadVO = (ProjectHeadVO) HYPubBO_Client
					.queryByPrimaryKey(ProjectHeadVO.class,
							conWorkVO.getCbprojectid() + "");
			if (projectHeadVO != null) {
				jo.put("CbprojectName", projectHeadVO.getProject_name() + "");// 项目编号
				jo.put("CbprojectCode", projectHeadVO.getProject_code() + "");// 项目名称
			} else {
				jo.put("CbprojectName", "");
				jo.put("CbprojectCode", "");// 项目名称
			}

			jo.put("Castunitid", conWorkVO.getCastunitid() + "");// 单位
			MeasdocVO measureVO = (MeasdocVO) HYPubBO_Client.queryByPrimaryKey(
					MeasdocVO.class, conWorkVO.getCastunitid() + "");
			if (measureVO != null)
				jo.put("CastunitName", measureVO.getName() + "");// 单位名称
			else
				jo.put("CastunitName", "");// 单位名称

			jo.put("Nastnum", conWorkVO.getNastnum() + "");// 数量
			jo.put("Vchangerate", conWorkVO.getVchangerate() + "");// 换算率

			jo.put("norigprice", conWorkVO.getNorigprice() + "");// 无税单价

			jo.put("Norigtaxprice", conWorkVO.getNorigtaxprice() + "");// 含税单价
			jo.put("Ngprice", conWorkVO.getNgprice() + "");// 主本币无税单价
			jo.put("Ngtaxprice", conWorkVO.getNgtaxprice() + "");// 主本币含税单价
			jo.put("Norigmny", conWorkVO.getNorigmny() + "");// 无税金额
			jo.put("Ntaxrate", conWorkVO.getNtaxrate() + "");// 税率

			jo.put("Ftaxtypeflag", conWorkVO.getFtaxtypeflag() + "");// 扣税类别
			jo.put("Cqpbaseschemeid", conWorkVO.getCqpbaseschemeid() + "");// 优质优价方案

			QPSchmHeaderVO schmHeaderVO = (QPSchmHeaderVO) HYPubBO_Client
					.queryByPrimaryKey(QPSchmHeaderVO.class,
							conWorkVO.getCqpbaseschemeid() + "");
			if (schmHeaderVO != null)
				jo.put("Pk_measdocName", schmHeaderVO.getVschemename() + "");// 优质优价方案名称
			else
				jo.put("Pk_measdocName", "");

			jo.put("Norigtaxmny", conWorkVO.getNorigtaxmny() + "");// 价税合计
			jo.put("nmny", conWorkVO.getNmny() + "");// 本币无税金额

			// //////
			// jo.put("Norigtaxprice", conWorkVO.getNorigtaxprice());//本币价税金额
			jo.put("ctaxcodeid", conWorkVO.getCtaxcodeid() + "");// 税码编号

			TaxcodeVO taxcodeVO = (TaxcodeVO) HYPubBO_Client.queryByPrimaryKey(
					TaxcodeVO.class, conWorkVO.getCtaxcodeid() + "");
			if (taxcodeVO != null)
				jo.put("taxCode", taxcodeVO.getCode() + "");// 税码名称
			else
				jo.put("taxCode", "");// 税码名称

			jo.put("Nnosubtaxrate", conWorkVO.getNnosubtaxrate() + "");// 不可抵税税率
			jo.put("Nnosubtax", conWorkVO.getNnosubtax() + "");// 不可抵税金额

			jo.put("Ncalcostmny", conWorkVO.getNcalcostmny() + "");// 计成本金额

			jo.put("Nordnum", conWorkVO.getNordnum() + "");// 累计订单主数量
			jo.put("Nordsum", conWorkVO.getNordsum() + "");// 累计订单价税合计
			jo.put("Pk_financeorg", conWorkVO.getPk_financeorg() + "");// 财务组织
			FinanceOrgVO financeOrgVO = (FinanceOrgVO) HYPubBO_Client
					.queryByPrimaryKey(FinanceOrgVO.class,
							conWorkVO.getPk_financeorg() + "");
			if (financeOrgVO != null)
				jo.put("Pk_financeorgName", financeOrgVO.getName() + "");// 财务组织名称
			else
				jo.put("Pk_financeorgName", "");// 财务组织名称

			jo.put("Vmemo", conWorkVO.getVmemo() + "");// 备注

			jo.put("Pk_ct_price", conWorkVO.getPk_ct_price() + ""); // 合同价格ID
			CtPriceHeaderVO priceVO = (CtPriceHeaderVO) HYPubBO_Client
					.queryByPrimaryKey(CtPriceHeaderVO.class,
							conWorkVO.getPk_ct_price() + "");
			if (priceVO != null)
				jo.put("Pk_ct_priceName", priceVO.getVname() + "");// 合同价格名称
			else
				jo.put("Pk_ct_priceName", "");// 合同价格名称

			jo.put("Nschedulernum", conWorkVO.getNschedulernum() + ""); // 累积排程数量

			jo.put("Pk_arrvstock", conWorkVO.getPk_arrvstock() + ""); // 收货库存组织
			StockOrgVO stockOrgVO = (StockOrgVO) HYPubBO_Client
					.queryByPrimaryKey(StockOrgVO.class,
							conWorkVO.getPk_arrvstock() + "");
			if (stockOrgVO != null)
				jo.put("Pk_arrvstockName", stockOrgVO.getVname() + "");// 收货库存名称
			else
				jo.put("Pk_arrvstockName", "");// 合同价格名称
			jo.put("Vpraybillcode", conWorkVO.getVpraybillcode() + "");// 请购单号

			jo.put("vbdef1", conWorkVO.getVbdef1() + "");
			jo.put("vbdef2", conWorkVO.getVbdef2() + "");
			jo.put("vbdef3", conWorkVO.getVbdef3() + "");
			jo.put("vbdef4", conWorkVO.getVbdef4() + "");
			jo.put("vbdef5", conWorkVO.getVbdef5() + "");
			jo.put("vbdef6", conWorkVO.getVbdef6() + "");
			jo.put("vbdef7", conWorkVO.getVbdef7() + "");
			jo.put("vbdef8", conWorkVO.getVbdef8() + "");
			jo.put("vbdef9", conWorkVO.getVbdef9() + "");
			jo.put("vbdef10", conWorkVO.getVbdef10() + "");
			jo.put("ntax", conWorkVO.getNtax() + "");// 税额
			jo.put("Pk_ct_pu", conWorkVO.getPk_ct_pu() + "");// 合同主键
			jo.put("pk_ct_pu_b", conWorkVO.getPk_ct_pu_b());// 新增 合同明细主键
			jo.put("crowno", conWorkVO.getCrowno());// 新增 合同明细行号
			arrjson.add(jo);

		}
		return arrjson.toString();
	}

	// 合同条款pm_pu_term
	private static String getContractTermInfo(
			CircularlyAccessibleValueObject[] childrenVOs) {
		JSONArray arrjson = new JSONArray();

		for (int j = 0; j < childrenVOs.length; j++) {
			JSONObject jo = new JSONObject();
			CtPuTermVO conPuTermVO = (CtPuTermVO) childrenVOs[j];
			jo.put("Vtermcode", conPuTermVO.getVtermcode() + "");// 条款编码
			jo.put("Vtermname", conPuTermVO.getVtermname() + "");// 条款名称
			jo.put("Vtermtypename", conPuTermVO.getVtermtypename() + "");// 条款类型

			jo.put("Vtermcontent", conPuTermVO.getVtermcontent() + "");// 条款内容

			jo.put("Votherinfo", conPuTermVO.getVotherinfo() + "");// 其他信息
			jo.put("Vmemo", conPuTermVO.getVmemo() + "");// 备注
			jo.put("Pk_ct_pu", conPuTermVO.getPk_ct_pu() + "");// 合同主键
			arrjson.add(jo);
			// 无自定义字段

		}
		return arrjson.toString();
	}

	// 获取附件
	@SuppressWarnings("unchecked")
	public static String getPahtFile(String billCode) throws BusinessException {

		IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);

		String querySql = "select fp.filepath,fh.name, storepath from sm_pub_filesystem fp left join bap_fs_header fh on fp.pk_doc=fh.path"
				+ " left join bap_fs_body fb on fh.GUID = fb.headid"
				+ " where filepath like '" + billCode + "/%'";// 完整正确的sql语句
		List<Object[]> lms4 = (List<Object[]>) bs.executeQuery(querySql,
				new ArrayListProcessor());
		JSONArray json = new JSONArray();

		for (int i = 0; i < lms4.size(); i++) {
			JSONObject jsonObject = new JSONObject();
			Object[] item = lms4.get(i);
			jsonObject.put("FileName", item[1] + "");
			jsonObject.put("BaseDt", item[2] + "");
			jsonObject.put("Pk_ct_pu", billCode + "");
			json.add(jsonObject);

		}
		return json.toString();
	}

	// 调用HTTP接口
	private static void updateNotice(String url1, String templateNo,
			String ctDataBasic, String ctData, String ctItems,
			String base64AttachFile) throws Exception {
		// 设置通用的请求属性
		// Post请求的url，与get不同的是不需要带参数
		URL postUrl = new URL(url1);
		// 打开连接
		HttpURLConnection connection = (HttpURLConnection) postUrl
				.openConnection();
		// 设置是否向connection输出，因为这个是post请求，参数要放在
		// http正文内，因此需要设为true
		connection.setDoOutput(true);
		// Read from the connection. Default is true.
		connection.setDoInput(true);
		// 默认是 GET方式
		connection.setRequestMethod("POST");
		// Post 请求不能使用缓存
		connection.setUseCaches(false);
		// 设置本次连接是否自动重定向
		connection.setInstanceFollowRedirects(true);
		// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
		// 意思是正文是urlencoded编码过的form参数
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
		// 要注意的是connection.getOutputStream会隐含的进行connect。
		connection.connect();
		DataOutputStream out = new DataOutputStream(
				connection.getOutputStream());
		// 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
		String content = "templateNo=" + URLEncoder.encode(templateNo, "UTF-8");
		content += "&ctDataBasic=" + URLEncoder.encode(ctDataBasic, "UTF-8");
		content += "&ctData=" + URLEncoder.encode(ctData, "UTF-8");
		content += "&ctItems=" + URLEncoder.encode(ctItems, "UTF-8");
		content += "&base64AttachFile="
				+ URLEncoder.encode(base64AttachFile, "UTF-8");

		// DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写到流里面
		out.writeBytes(content);
		// 流用完记得关
		out.flush();
		out.close();
		// 获取响应
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream(),"UTF-8"));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println("返回结果：" + line);
			
			JSONObject rtdata = JSONObject.parseObject(line);
			String msg = rtdata.getString("msg");
			if ("true".equals(rtdata.getString("success"))) {
				MessageDialog.showOkCancelDlg(null, "提示", "传送电子合同系统成功！");
			} else {
				MessageDialog.showErrorDlg(null, "错误提示", msg);
			}
		}
		reader.close();
		// 结束,记得把连接断了
		connection.disconnect();
	}

	public static String getEncoding(String str) {
		String encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { // 判断是不是UTF-8编码
				String s2 = encode;
				return s2;
			}
		} catch (Exception e) {
		}
		encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { // 判断是不是GB2312
				String s = encode;
				return s; // 是的话，返回GB2312，以下代码同理
			}
		} catch (Exception e) {
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { // 判断是不是ISO-8859-1
				String s1 = encode;
				return s1;
			}
		} catch (Exception e) {
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { // 判断是不是GBK
				String s3 = encode;
				return s3;
			}
		} catch (Exception e) {
		}
		return ""; // 到这一步，你就应该检查是不是其他编码啦
	}
}
