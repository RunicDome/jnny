package nc.web.erm.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.pcm.utils.GetDao;
import nc.vo.bd.bankaccount.BankAccbasVO;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JKBXUtils {
	// 流程审批至某一节点，将数据存放至中间库
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	public String nc2cbs(String pk_primarykey, String djlxbm) throws Exception {
		Logger.error("单据主键：" + pk_primarykey + "交易类型编码：" + djlxbm);
		String mes = "";
		JSONArray arrjson = new JSONArray();
		String pk_billtype = djlxbm + "";// 单据类型
		String pk_primaryKey = pk_primarykey + "";// 单据主键
		JSONObject json = new JSONObject();
		String ispushsql = "SELECT NAME FROM RL_CBSNAME WHERE ID = '32'";
		List<Object[]> ispushls = getDao.query(ispushsql);
		if (ispushls == null || ispushls.size() == 0) {
			return "未配置中间库查询是否推送成功IP！";
		}
		if (ispushls.get(0)[0] == null || ispushls.get(0)[0] == "") {
			return "未配置中间库查询是否推送成功IP！";
		}
		if ("2641".equals(pk_billtype)
				|| "264X-Cxx-CNFTFSPD".equals(pk_billtype)
				|| "264X-Cxx-JBCFBX".equals(pk_billtype)
				|| "264X-Cxx-YGFYBXD".equals(pk_billtype)
				|| "264X-Cxx-PTFJRJLTF".equals(pk_billtype)) {
			String curbxsql = "";
			// 采暖费退费审批单，配套费及热计量退费审批单 收款方账户字段：custaccount
			if ("264X-Cxx-CNFTFSPD".equals(pk_billtype)
					|| "264X-Cxx-PTFJRJLTF".equals(pk_billtype)) {
				curbxsql = "SELECT zyx24,custaccount,zfbbje,zyx30,zyx20,djbh,zyx9,zyx14,zyx11 FROM ER_BXZB WHERE PK_JKBX = '"
						+ pk_primaryKey + "' AND nvl(dr,0) = 0 ";
			} else {
				curbxsql = "SELECT zyx24,skyhzh,zfbbje,zyx30,zyx20,djbh FROM ER_BXZB WHERE PK_JKBX = '"
						+ pk_primaryKey + "' AND nvl(dr,0) = 0 ";
			}
			List<Object[]> bxls = getDao.query(curbxsql);
			// 查询报销单据
			if (bxls == null || bxls.size() <= 0) {
				mes = "查询报销单据失败，单据PK：" + pk_primaryKey;
				Logger.error(mes);
				return mes;
			}
			String fg = isPush(ispushls.get(0)[0] + "",
					bxls.get(0)[5].toString());
			if (!"N".equals(fg)) {
				return "单据[" + bxls.get(0)[5].toString() + "]已经推送至CBS,请勿重新推送！";
			}
			Logger.error(bxls.get(0));

			if ("264X-Cxx-CNFTFSPD".equals(pk_billtype)
					|| "264X-Cxx-PTFJRJLTF".equals(pk_billtype)) {
				json = getCNPTBXJson(bxls, pk_primaryKey, pk_billtype);// 获取JSON字符串
			} else {
				json = getBXDJson(bxls, pk_primaryKey, pk_billtype);// 获取JSON字符串
			}
			arrjson.add(json);
		} else if ("263X-Cxx-YGJKD".equals(pk_billtype)
				|| "2631".equals(pk_billtype)) {// 员工借款单
			String curjksql = "SELECT zb.zyx24,zb.skyhzh,zb.total,mx.defitem30,zb.zyx20,zb.djbh FROM er_jkzb zb left join er_busitem mx on zb.PK_JKBX "
					+ "= mx.PK_JKBX where zb.PK_JKBX = '"
					+ pk_primaryKey
					+ "' AND nvl(zb.dr,0) = 0 AND nvl(mx.dr,0) = 0";
			List<Object[]> jkls = getDao.query(curjksql);
			// 查询借款单据
			if (jkls == null || jkls.size() <= 0) {
				mes = "查询借款单据失败，单据PK：" + pk_primaryKey;
				Logger.error(mes);
				return mes;
			}
			String fg = isPush(ispushls.get(0)[0] + "",
					jkls.get(0)[5].toString());
			if (!"N".equals(fg)) {
				return "单据[" + jkls.get(0)[5].toString() + "]已经推送至CBS,请勿重新推送！";
			}
			json = getJKDJson(jkls, pk_primaryKey, pk_billtype);// 获取JSON字符串
			arrjson.add(json);
		} else if ("F3-Cxx-FYFKSPD".equals(pk_billtype)
				|| "F3-Cxx-MTWGRFKSPD".equals(pk_billtype)
				|| "F3-Cxx-SDRFKSPD".equals(pk_billtype)
				|| "F3-Cxx-GDZC".equals(pk_billtype)) {
			// 付款审批
			String curfksql = "SELECT def24,def26,def2,billno,def20 FROM ap_paybill where pk_paybill = '"
					+ pk_primaryKey + "' AND nvl(dr,0) = 0";
			List<Object[]> fkls = getDao.query(curfksql);
			String skzhsql = "SELECT DISTINCT RECACCOUNT FROM AP_PAYITEM WHERE PK_PAYBILL = '"
					+ pk_primaryKey + "'";
			List<Object[]> skzhls = getDao.query(skzhsql);
			// 查询借款单据
			if (fkls == null || fkls.size() <= 0) {
				mes = "查询付款单据失败，单据PK：" + pk_primaryKey;
				Logger.error(mes);
				return mes;
			}
			String fg = isPush(ispushls.get(0)[0] + "",
					fkls.get(0)[3].toString());
			if (!"N".equals(fg)) {
				return "单据[" + fkls.get(0)[3].toString() + "]已经推送至CBS,请勿重新推送！";
			}
			String recaccount = "";// 收款银行账号
			if (skzhls != null && skzhls.size() > 0) {
				if (skzhls.get(0)[0] != null && skzhls.get(0)[0] != "'") {
					recaccount = skzhls.get(0)[0] + "";
				} else {
					return "未查到收款银行账号！";
				}
			} else {
				return "未查到收款银行账号！";
			}
			json = getFKDJson(fkls, pk_primaryKey, pk_billtype, recaccount);// 获取JSON字符串
			arrjson.add(json);
		} else if ("D3".equals(pk_billtype) || "F3-Cxx-01".equals(pk_billtype)) {
			// 付款单、工程付款单
			String curfksql = "SELECT def24,def26,def30,billno,def20 FROM ap_paybill where pk_paybill = '"
					+ pk_primaryKey + "' AND nvl(dr,0) = 0";
			List<Object[]> fkls = getDao.query(curfksql);
			String skzhsql = "SELECT DISTINCT RECACCOUNT FROM AP_PAYITEM WHERE PK_PAYBILL = '"
					+ pk_primaryKey + "'";
			List<Object[]> skzhls = getDao.query(skzhsql);
			// 查询借款单据
			if (fkls == null || fkls.size() <= 0) {
				mes = "查询付款单据失败，单据PK：" + pk_primaryKey;
				Logger.error(mes);
				return mes;
			}
			String fg = isPush(ispushls.get(0)[0] + "",
					fkls.get(0)[3].toString());
			if (!"N".equals(fg)) {
				return "单据[" + fkls.get(0)[3].toString() + "]已经推送至CBS,请勿重新推送！";
			}
			String recaccount = "";// 收款银行账号
			if (skzhls != null && skzhls.size() > 0) {
				if (skzhls.get(0)[0] != null && skzhls.get(0)[0] != "'") {
					recaccount = skzhls.get(0)[0] + "";
				} else {
					return "未查到收款银行账号！";
				}
			} else {
				return "未查到收款银行账号！";
			}
			json = getFKDJson(fkls, pk_primaryKey, pk_billtype, recaccount);// 获取JSON字符串
			arrjson.add(json);
		} else {
			return "暂未对交易类型[" + pk_billtype + "]设置推送CBS，请取消勾选！";
		}
		String urlsql = "SELECT NAME FROM RL_CBSNAME WHERE ID = '30'";
		List<Object[]> urls = getDao.query(urlsql);
		if (urls == null || urls.size() == 0) {
			return "未配置中间库系统IP！";
		}
		if (urls.get(0)[0] == null || urls.get(0)[0] == "") {
			return "未配置中间库系统IP！";
		}
		String updateNoticeUrl = urls.get(0)[0] + "";
		try {
			String flag = sendMidDataBase(updateNoticeUrl, arrjson.toString());
			if ("".equals(flag)) {
			} else {
				return flag;
			}
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return e.getMessage();
		}
		return mes;
	}

	// 批量推送CBS
	public String batch2cbs(List<Map<String, String>> ls) throws Exception {
		String mes = "";
		JSONArray arrjson = new JSONArray();
		String ispushsql = "SELECT NAME FROM RL_CBSNAME WHERE ID = '32'";
		List<Object[]> ispushls = getDao.query(ispushsql);
		if (ispushls == null || ispushls.size() == 0) {
			return "未配置中间库查询是否推送成功IP！";
		}
		if (ispushls.get(0)[0] == null || ispushls.get(0)[0] == "") {
			return "未配置中间库查询是否推送成功IP！";
		}
		for (int i = 0; i < ls.size(); i++) {
			Map<String, String> map = ls.get(i);
			String pk_primaryKey = map.get("pk_primarykey");// 单据主键
			String pk_billtype = map.get("djlxbm");// 交易类型
			JSONObject json = new JSONObject();
			if ("2641".equals(pk_billtype)
					|| "264X-Cxx-CNFTFSPD".equals(pk_billtype)
					|| "264X-Cxx-JBCFBX".equals(pk_billtype)
					|| "264X-Cxx-YGFYBXD".equals(pk_billtype)
					|| "264X-Cxx-PTFJRJLTF".equals(pk_billtype)) {
				String curbxsql = "";
				// 采暖费退费审批单，配套费及热计量退费审批单 收款方账户字段：custaccount
				if ("264X-Cxx-CNFTFSPD".equals(pk_billtype)
						|| "264X-Cxx-PTFJRJLTF".equals(pk_billtype)) {
					curbxsql = "SELECT zyx24,custaccount,zfbbje,zyx30,zyx20,djbh,zyx9,zyx14,zyx11 FROM ER_BXZB WHERE PK_JKBX = '"
							+ pk_primaryKey + "' AND nvl(dr,0) = 0 ";
				} else {
					curbxsql = "SELECT zyx24,skyhzh,zfbbje,zyx30,zyx20,djbh FROM ER_BXZB WHERE PK_JKBX = '"
							+ pk_primaryKey + "' AND nvl(dr,0) = 0 ";
				}
				List<Object[]> bxls = getDao.query(curbxsql);
				// 查询报销单据
				if (bxls == null || bxls.size() <= 0) {
					mes = "查询报销单据失败，单据PK：" + pk_primaryKey;
					Logger.error(mes);
					return mes;
				}
				String fg = isPush(ispushls.get(0)[0] + "",
						bxls.get(0)[5].toString());
				if (!"N".equals(fg)) {
					return "单据[" + bxls.get(0)[5].toString()
							+ "]已经推送至CBS,请勿重新推送！";
				}
				Logger.error(bxls.get(0));

				if ("264X-Cxx-CNFTFSPD".equals(pk_billtype)
						|| "264X-Cxx-PTFJRJLTF".equals(pk_billtype)) {
					json = getCNPTBXJson(bxls, pk_primaryKey, pk_billtype);// 获取JSON字符串
				} else {
					json = getBXDJson(bxls, pk_primaryKey, pk_billtype);// 获取JSON字符串
				}
				arrjson.add(json);
			} else if ("263X-Cxx-YGJKD".equals(pk_billtype)
					|| "2631".equals(pk_billtype)) {// 员工借款单
				String curjksql = "SELECT zb.zyx24,zb.skyhzh,zb.total,mx.defitem30,zb.zyx20,zb.djbh FROM er_jkzb zb left join er_busitem mx on zb.PK_JKBX "
						+ "= mx.PK_JKBX where zb.PK_JKBX = '"
						+ pk_primaryKey
						+ "' AND nvl(zb.dr,0) = 0 AND nvl(mx.dr,0) = 0";
				List<Object[]> jkls = getDao.query(curjksql);
				// 查询借款单据
				if (jkls == null || jkls.size() <= 0) {
					mes = "查询借款单据失败，单据PK：" + pk_primaryKey;
					Logger.error(mes);
					return mes;
				}
				String fg = isPush(ispushls.get(0)[0] + "",
						jkls.get(0)[5].toString());
				if (!"N".equals(fg)) {
					return "单据[" + jkls.get(0)[5].toString()
							+ "]已经推送至CBS,请勿重新推送！";
				}
				json = getJKDJson(jkls, pk_primaryKey, pk_billtype);// 获取JSON字符串
				arrjson.add(json);
			} else if ("F3-Cxx-FYFKSPD".equals(pk_billtype)
					|| "F3-Cxx-MTWGRFKSPD".equals(pk_billtype)
					|| "F3-Cxx-SDRFKSPD".equals(pk_billtype)) {// 付款审批
				String curfksql = "SELECT def24,def26,def2,billno,def20 FROM ap_paybill where pk_paybill = '"
						+ pk_primaryKey + "' AND nvl(dr,0) = 0";
				List<Object[]> fkls = getDao.query(curfksql);
				String skzhsql = "SELECT DISTINCT RECACCOUNT FROM AP_PAYITEM WHERE PK_PAYBILL = '"
						+ pk_primaryKey + "'";
				List<Object[]> skzhls = getDao.query(skzhsql);
				// 查询借款单据
				if (fkls == null || fkls.size() <= 0) {
					mes = "查询付款单据失败，单据PK：" + pk_primaryKey;
					Logger.error(mes);
					return mes;
				}
				String fg = isPush(ispushls.get(0)[0] + "",
						fkls.get(0)[3].toString());
				if (!"N".equals(fg)) {
					return "单据[" + fkls.get(0)[3].toString()
							+ "]已经推送至CBS,请勿重新推送！";
				}
				String recaccount = "";// 收款银行账号
				if (skzhls != null && skzhls.size() > 0) {
					if (skzhls.get(0)[0] != null && skzhls.get(0)[0] != "'") {
						recaccount = skzhls.get(0)[0] + "";
					} else {
						return "未查到收款银行账号！";
					}
				} else {
					return "未查到收款银行账号！";
				}
				json = getFKDJson(fkls, pk_primaryKey, pk_billtype, recaccount);// 获取JSON字符串
				arrjson.add(json);
			} else if ("D3".equals(pk_billtype)
					|| "F3-Cxx-01".equals(pk_billtype)) {
				// 付款单、工程付款单
				String curfksql = "SELECT def24,def26,def30,billno,def20 FROM ap_paybill where pk_paybill = '"
						+ pk_primaryKey + "' AND nvl(dr,0) = 0";
				List<Object[]> fkls = getDao.query(curfksql);
				String skzhsql = "SELECT DISTINCT RECACCOUNT FROM AP_PAYITEM WHERE PK_PAYBILL = '"
						+ pk_primaryKey + "'";
				List<Object[]> skzhls = getDao.query(skzhsql);
				// 查询借款单据
				if (fkls == null || fkls.size() <= 0) {
					mes = "查询付款单据失败，单据PK：" + pk_primaryKey;
					Logger.error(mes);
					return mes;
				}
				String fg = isPush(ispushls.get(0)[0] + "",
						fkls.get(0)[3].toString());
				if (!"N".equals(fg)) {
					return "单据[" + fkls.get(0)[3].toString()
							+ "]已经推送至CBS,请勿重新推送！";
				}
				String recaccount = "";// 收款银行账号
				if (skzhls != null && skzhls.size() > 0) {
					if (skzhls.get(0)[0] != null && skzhls.get(0)[0] != "'") {
						recaccount = skzhls.get(0)[0] + "";
					} else {
						return "未查到收款银行账号！";
					}
				} else {
					return "未查到收款银行账号！";
				}
				json = getFKDJson(fkls, pk_primaryKey, pk_billtype, recaccount);// 获取JSON字符串
				arrjson.add(json);
			} else {
				return "暂未对交易类型[" + pk_billtype + "]设置推送CBS，请取消勾选！";
			}
		}
		String urlsql = "SELECT NAME FROM RL_CBSNAME WHERE ID = '30'";
		List<Object[]> urls = getDao.query(urlsql);
		if (urls == null || urls.size() == 0) {
			return "未配置中间库系统IP！";
		}
		if (urls.get(0)[0] == null || urls.get(0)[0] == "") {
			return "未配置中间库系统IP！";
		}
		String updateNoticeUrl = urls.get(0)[0] + "";
		try {
			String flag = sendMidDataBase(updateNoticeUrl, arrjson.toString());
			if ("".equals(flag)) {
			} else {
				return flag;
			}

		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			mes = e.getMessage();
			e.printStackTrace();
			return mes;
		}
		return mes;
	}

	// 采暖配套报销
	public JSONObject getCNPTBXJson(List<Object[]> bxls, String pk_primaryKey,
			String pk_billtype) throws DAOException {
		UUID uuid = UUID.randomUUID();
		JSONObject json = new JSONObject();
		json.put("ERP_PAYMENT_ID", uuid.toString().substring(0, 30));// 主键
		json.put("RECORD_STATUS", "Available");// 指令状态
		json.put("PAYMENT_TYPE_ID", "202");// 支付类型 202，对外支付（默认值）
		json.put("PAYMENT_BUSTYPE_ID", "0");// 支付子类型 0 一般支付（默认值）
		json.put("PAYMENT_METHOD_TYPE_ID", "3");// 结算方式 3，电汇；
		String bankSql = "SELECT * FROM RL_BANK WHERE PK_DEFDOC = '"
				+ bxls.get(0)[0] + "'";
		List<Object[]> bankLs = getDao.query(bankSql);
		String PAYMENT_ACCOUNTS = "";// 付款方银行账号
		if (bankLs.size() > 0) {
			PAYMENT_ACCOUNTS = bankLs.get(0)[1] + "";
		}
		json.put("PAYMENT_ACCOUNTS", PAYMENT_ACCOUNTS);// 付款方银行账号
		json.put("CURRENCY_TYPE", "10");// 币种 10-人民币
		
		String DEPOSIT_BANK_NAME = "";// 收款方银行开户行
		String UNION_BANK_NUMBER = "";// 联行号
		String sql = "SELECT NAME,CODE FROM bd_asslinenum WHERE PK_asslinenum = '"+bxls.get(0)[6]+"' AND DR = 0";
		List<Object[]> skBank = getDao.query(sql);// 收款银行开户行
		if(skBank != null && skBank.size() > 0){
			DEPOSIT_BANK_NAME = skBank.get(0)[0]+"";
			UNION_BANK_NUMBER = skBank.get(0)[1]+"";
		}
		
		String DEPOSIT_ACCOUNTS_NAME = bxls.get(0)[8] + "";// 收款方银行账户名称
		String DEPOSIT_ACCOUNTS = bxls.get(0)[7] + "";// 收款方银行账号
		String DEPOSIT_BANK_TYPE = "";// 收款方银行类型
		String DEPOSIT_PROVINCE = "";// 收款方银行开户行省
		String DEPOSIT_CITY = "";// 收款方银行开户行市
		
		json.put("DEPOSIT_BANK_NAME", DEPOSIT_BANK_NAME);// 收款方银行开户行
		json.put("DEPOSIT_ACCOUNTS_NAME", DEPOSIT_ACCOUNTS_NAME);// 收款方银行账户名称
		json.put("DEPOSIT_ACCOUNTS", DEPOSIT_ACCOUNTS);// 收款方银行账号
		json.put("DEPOSIT_BANK_TYPE", DEPOSIT_BANK_TYPE);// 收款方银行类型
		json.put("DEPOSIT_PROVINCE", DEPOSIT_PROVINCE);// 收款方银行开户行省
		json.put("DEPOSIT_CITY", DEPOSIT_CITY);// 收收款方银行开户行市
		if("济南".equals(DEPOSIT_CITY) || "济南市".equals(DEPOSIT_CITY)){
			json.put("CITY_FLAG", "0");// 是否同城 =>应该只有木一些特定的银行需要填写
		}else{
			json.put("CITY_FLAG", "1");// 是否同城 =>应该只有木一些特定的银行需要填写
		}
		json.put("PRIORITY_FLAG", "Y");// 是否加急，默认加急 “Y”，加急；“N”：普通。
		json.put("OPERATION_TYPE", "3");// 支付渠道： “3”：银企直连支付（默认）
		json.put("UNION_BANK_NUMBER", UNION_BANK_NUMBER);// 联行号
		json.put("AMOUNT", bxls.get(0)[2]);// 表头合计金额 =》金额
		//json.put("PURPOSE", bxls.get(0)[3]);// 表头摘要 =》用途
		
		String gdSql = "SELECT NAME FROM BD_DEFDOC WHERE PK_DEFDOC = '"
				+ bxls.get(0)[0] + "'";
		List<Object[]> gdLs = getDao.query(gdSql);
		
		if (gdLs.size() > 0) {
			String zy = gdLs.get(0)[0] + "";
			System.out.println("zy======"+zy);
			if(zy.indexOf("光大")!=-1){
				json.put("PURPOSE", "财务报销");// 表头摘要 =》用途
			}else{
				json.put("PURPOSE", "退费");// 表头摘要 =》用途
			}
		}
		//json.put("PURPOSE", "退费");// 表头摘要 =》用途
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		json.put("NETBANK_EPT_DATE", sdf.format(new Date()));// 期望日期
		json.put("VERSION", "0");// 版本号
		String CHECK_CODE = getCheckCode(json.getString("ERP_PAYMENT_ID"),
				"Available", PAYMENT_ACCOUNTS, DEPOSIT_ACCOUNTS, bxls.get(0)[2]
						+ "")
				+ "";// 获取校验码
		json.put("CHECK_CODE", CHECK_CODE);// 校验码
		json.put("BILL_TYPE", "ER_BXZB");// 对应NC表
		json.put("PK_PRIMARYKEY", bxls.get(0)[5]);// 对应NC单据编号
		json.put("TRANSI_TYPE", pk_billtype);// 对应NC交易类型
		Logger.error("JSON字符串数据：" + json.toString());
		return json;
	}

	// 报销单通用JSON字符串
	@SuppressWarnings("unchecked")
	public JSONObject getBXDJson(List<Object[]> bxls, String pk_primaryKey,
			String pk_billtype) throws DAOException {
		UUID uuid = UUID.randomUUID();
		JSONObject json = new JSONObject();
		json.put("ERP_PAYMENT_ID", uuid.toString().substring(0, 30));// 主键
		json.put("RECORD_STATUS", "Available");// 指令状态
		json.put("PAYMENT_TYPE_ID", "202");// 支付类型 202，对外支付（默认值）
		json.put("PAYMENT_BUSTYPE_ID", "0");// 支付子类型 0 一般支付（默认值）
		json.put("PAYMENT_METHOD_TYPE_ID", "3");// 结算方式 3，电汇；
		String bankSql = "SELECT * FROM RL_BANK WHERE PK_DEFDOC = '"
				+ bxls.get(0)[0] + "'";
		List<Object[]> bankLs = getDao.query(bankSql);
		String PAYMENT_ACCOUNTS = "";// 付款方银行账号
		if (bankLs.size() > 0) {
			PAYMENT_ACCOUNTS = bankLs.get(0)[1] + "";
		}
		json.put("PAYMENT_ACCOUNTS", PAYMENT_ACCOUNTS);// 付款方银行账号
		json.put("CURRENCY_TYPE", "10");// 币种 10-人民币
		String DEPOSIT_BANK_NAME = "";// 收款方银行开户行
		String DEPOSIT_ACCOUNTS_NAME = "";// 收款方银行账户名称
		String DEPOSIT_ACCOUNTS = "";// 收款方银行账号
		String DEPOSIT_BANK_TYPE = "";// 收款方银行类型
		String DEPOSIT_PROVINCE = "";// 收款方银行开户行省
		String DEPOSIT_CITY = "";// 收款方银行开户行市
		String UNION_BANK_NUMBER = "";// 联行号
		String skBankSql = "SELECT pk_bankaccbas FROM bd_bankaccsub WHERE pk_bankaccsub = '"
				+ bxls.get(0)[1] + "' AND nvl(dr,0) = 0 ";
		List<Object[]> skBank = getDao.query(skBankSql);// 收款银行账户
		if (skBank.size() > 0) {
			List<BankAccbasVO> skgrBank = (List<BankAccbasVO>) getDao
					.executeQuery(
							"SELECT * FROM bd_bankaccbas WHERE pk_bankaccbas = '"
									+ skBank.get(0)[0] + "' AND nvl(dr,0) = 0 ",
							new BeanListProcessor(BankAccbasVO.class));// 收款个人银行账户
			if (skgrBank.size() > 0) {
				DEPOSIT_ACCOUNTS_NAME = skgrBank.get(0).getAccname();// 收款方银行账户名称
				DEPOSIT_ACCOUNTS = skgrBank.get(0).getAccnum();// 收款方银行账号
				DEPOSIT_PROVINCE = skgrBank.get(0).getProvince();// 收款方银行开户行省
				DEPOSIT_CITY = skgrBank.get(0).getCity();// 收款方银行开户行市
				UNION_BANK_NUMBER = skgrBank.get(0).getCombineaccnum();// 联行号
				List<Object[]> bankDoc = getDao
						.query("SELECT name,combinenum,pk_banktype FROM bd_bankdoc WHERE pk_bankdoc = '"
								+ skgrBank.get(0).getPk_bankdoc()
								+ "' AND nvl(dr,0) = 0 ");// 银行档案
				if (bankDoc.size() > 0) {
					if (bankDoc.get(0)[0] != null && bankDoc.get(0)[0] != "") {
						DEPOSIT_BANK_NAME = bankDoc.get(0)[0] + "";// 开户银行
					}
					/*if (bankDoc.get(0)[1] != null && bankDoc.get(0)[1] != "") {
						UNION_BANK_NUMBER = bankDoc.get(0)[1] + "";// 联行号
					}*/
					String dazjmsql = "SELECT MNECODE FROM bd_banktype WHERE pk_banktype = '"+bankDoc.get(0)[2]+"'";
					List<Object[]> banktp = getDao.query(dazjmsql);// 银行类别
					if (banktp != null && banktp.size() > 0) {
						DEPOSIT_BANK_TYPE = banktp.get(0)[0] + "";// 助记码作为银行类别
					}else{
						Logger.error("银行类别空！");
						return null;
					}
					/*
					 * List<Object[]> bankType = getDao .query(
					 * "SELECT mnecode FROM bd_bankaccbas WHERE pk_bankaccbas = '"
					 * + skBank.get(0)[1] + "' AND nvl(dr,0) = 0 ");// 银行类别 if
					 * (bankType.size() > 0) { DEPOSIT_BANK_TYPE =
					 * bankType.get(0)[0] + "";// 助记码 }
					 */
				}
			}
		}
		json.put("DEPOSIT_BANK_NAME", DEPOSIT_BANK_NAME);// 收款方银行开户行
		json.put("DEPOSIT_ACCOUNTS_NAME", DEPOSIT_ACCOUNTS_NAME);// 收款方银行账户名称
		json.put("DEPOSIT_ACCOUNTS", DEPOSIT_ACCOUNTS);// 收款方银行账号
		json.put("DEPOSIT_BANK_TYPE", DEPOSIT_BANK_TYPE);// 收款方银行类型
		json.put("DEPOSIT_PROVINCE", DEPOSIT_PROVINCE);// 收款方银行开户行省
		json.put("DEPOSIT_CITY", DEPOSIT_CITY);// 收收款方银行开户行市
		if("济南".equals(DEPOSIT_CITY) || "济南市".equals(DEPOSIT_CITY)){
			json.put("CITY_FLAG", "0");// 是否同城 =>应该只有木一些特定的银行需要填写
		}else{
			json.put("CITY_FLAG", "1");// 是否同城 =>应该只有木一些特定的银行需要填写
		}
		json.put("PRIORITY_FLAG", "Y");// 是否加急，默认加急 “Y”，加急；“N”：普通。
		json.put("OPERATION_TYPE", "3");// 支付渠道： “3”：银企直连支付（默认）
		json.put("UNION_BANK_NUMBER", UNION_BANK_NUMBER);// 联行号
		json.put("AMOUNT", bxls.get(0)[2]);// 表头合计金额 =》金额
		//2020 11 18 员工费用报销单固定摘要 -财务报销
		String gdSql = "SELECT NAME FROM BD_DEFDOC WHERE PK_DEFDOC = '"
				+ bxls.get(0)[0] + "'";
		List<Object[]> gdLs = getDao.query(gdSql);
		
		if (gdLs.size() > 0) {
			String zy = gdLs.get(0)[0] + "";
			System.out.println("zy======"+zy);
			if(zy.indexOf("光大")!=-1){
				json.put("PURPOSE", "财务报销");// 表头摘要 =》用途
			}else{
				if("264X-Cxx-YGFYBXD".equals(pk_billtype)){
					json.put("PURPOSE","财务报销");
				}else if("264X-Cxx-PTFJRJLTF".equals(pk_billtype)){
					json.put("PURPOSE", "退费");// 用途
				}else if("264X-Cxx-YGFYBXD".equals(pk_billtype) || "264X-Cxx-JBCFBX".equals(pk_billtype) || "2641".equals(pk_billtype)){
					json.put("PURPOSE", "财务报销");// 用途
				}else{
					json.put("PURPOSE", bxls.get(0)[3]);// 用途
				}
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		json.put("NETBANK_EPT_DATE", sdf.format(new Date()));// 期望日期
		json.put("VERSION", "0");// 版本号
		String CHECK_CODE = getCheckCode(json.getString("ERP_PAYMENT_ID"),
				"Available", PAYMENT_ACCOUNTS, DEPOSIT_ACCOUNTS, bxls.get(0)[2]
						+ "")
				+ "";// 获取校验码
		json.put("CHECK_CODE", CHECK_CODE);// 校验码
		json.put("BILL_TYPE", "ER_BXZB");// 对应NC表
		json.put("PK_PRIMARYKEY", bxls.get(0)[5]);// 对应NC单据编号
		json.put("TRANSI_TYPE", pk_billtype);// 对应NC交易类型
		Logger.error("JSON字符串数据：" + json.toString());
		return json;
	}

	// 借款单通用JSON字符串
	@SuppressWarnings("unchecked")
	public JSONObject getJKDJson(List<Object[]> bxls, String pk_primaryKey,
			String pk_billtype) throws DAOException {
		UUID uuid = UUID.randomUUID();
		JSONObject json = new JSONObject();
		json.put("ERP_PAYMENT_ID", uuid.toString().substring(0, 30));// 主键
		json.put("RECORD_STATUS", "Available");// 指令状态
		json.put("PAYMENT_TYPE_ID", "202");// 支付类型 202，对外支付（默认值）
		json.put("PAYMENT_BUSTYPE_ID", "0");// 支付子类型 0 一般支付（默认值）
		json.put("PAYMENT_METHOD_TYPE_ID", "3");// 结算方式 3，电汇；
		String bankSql = "SELECT * FROM RL_BANK WHERE PK_DEFDOC = '"
				+ bxls.get(0)[0] + "'";
		List<Object[]> bankLs = getDao.query(bankSql);
		String PAYMENT_ACCOUNTS = "";// 付款方银行账号
		if (bankLs != null && bankLs.size() > 0) {
			PAYMENT_ACCOUNTS = bankLs.get(0)[1] + "";
		}
		json.put("PAYMENT_ACCOUNTS", PAYMENT_ACCOUNTS);// 付款方银行账号
		json.put("CURRENCY_TYPE", "10");// 币种 10-人民币
		String DEPOSIT_BANK_NAME = "";// 收款方银行开户行
		String DEPOSIT_ACCOUNTS_NAME = "";// 收款方银行账户名称
		String DEPOSIT_ACCOUNTS = "";// 收款方银行账号
		String DEPOSIT_BANK_TYPE = "";// 收款方银行类型
		String DEPOSIT_PROVINCE = "";// 收款方银行开户行省
		String DEPOSIT_CITY = "";// 收款方银行开户行市
		String UNION_BANK_NUMBER = "";// 联行号
		String skBankSql = "SELECT pk_bankaccbas FROM bd_bankaccsub WHERE pk_bankaccsub = '"
				+ bxls.get(0)[1] + "' AND nvl(dr,0) = 0 ";
		List<Object[]> skBank = getDao.query(skBankSql);// 收款银行账户
		if (skBank != null && skBank.size() > 0) {
			List<BankAccbasVO> skgrBank = (List<BankAccbasVO>) getDao
					.executeQuery(
							"SELECT * FROM bd_bankaccbas WHERE pk_bankaccbas = '"
									+ skBank.get(0)[0] + "' AND nvl(dr,0) = 0 ",
							new BeanListProcessor(BankAccbasVO.class));// 收款个人银行账户
			if (skgrBank != null && skgrBank.size() > 0) {
				DEPOSIT_ACCOUNTS_NAME = skgrBank.get(0).getAccname();// 收款方银行账户名称
				DEPOSIT_ACCOUNTS = skgrBank.get(0).getAccnum();// 收款方银行账号
				DEPOSIT_PROVINCE = skgrBank.get(0).getProvince();// 收款方银行开户行省
				DEPOSIT_CITY = skgrBank.get(0).getCity();// 收款方银行开户行市
				UNION_BANK_NUMBER = skgrBank.get(0).getCombineaccnum();// 联行号
				List<Object[]> bankDoc = getDao
						.query("SELECT name,combinenum,pk_banktype FROM bd_bankdoc WHERE pk_bankdoc = '"
								+ skgrBank.get(0).getPk_bankdoc()
								+ "' AND nvl(dr,0) = 0 ");// 银行档案
				if (bankDoc != null && bankDoc.size() > 0) {
					if (bankDoc.get(0)[0] != null && bankDoc.get(0)[0] != "") {
						DEPOSIT_BANK_NAME = bankDoc.get(0)[0] + "";// 开户银行
					}
					/*if (bankDoc.get(0)[1] != null && bankDoc.get(0)[1] != "") {
						UNION_BANK_NUMBER = bankDoc.get(0)[1] + "";// 联行号
					}*/
					//DEPOSIT_BANK_TYPE = bankDoc.get(0)[2] + "";// 助记码作为银行类别
					String dazjmsql = "SELECT MNECODE FROM bd_banktype WHERE pk_banktype = '"+bankDoc.get(0)[2]+"'";
					List<Object[]> banktp = getDao.query(dazjmsql);// 银行类别
					if (banktp != null && banktp.size() > 0) {
						DEPOSIT_BANK_TYPE = banktp.get(0)[0] + "";// 助记码作为银行类别
					}else{
						Logger.error("银行类别空！");
						return null;
					}
					/*
					 * List<Object[]> bankType = getDao .query(
					 * "SELECT mnecode FROM bd_bankaccbas WHERE pk_bankaccbas = '"
					 * + skBank.get(0)[1] + "' AND nvl(dr,0) = 0 ");// 银行类别 if
					 * (bankType.size() > 0) { DEPOSIT_BANK_TYPE =
					 * bankType.get(0)[0] + "";// 助记码 }
					 */
				}
			}
		}
		json.put("DEPOSIT_BANK_NAME", DEPOSIT_BANK_NAME);// 收款方银行开户行
		json.put("DEPOSIT_ACCOUNTS_NAME", DEPOSIT_ACCOUNTS_NAME);// 收款方银行账户名称
		json.put("DEPOSIT_ACCOUNTS", DEPOSIT_ACCOUNTS);// 收款方银行账号
		json.put("DEPOSIT_BANK_TYPE", DEPOSIT_BANK_TYPE);// 收款方银行类型
		json.put("DEPOSIT_PROVINCE", DEPOSIT_PROVINCE);// 收款方银行开户行省
		json.put("DEPOSIT_CITY", DEPOSIT_CITY);// 收收款方银行开户行市
		if("济南".equals(DEPOSIT_CITY) || "济南市".equals(DEPOSIT_CITY)){
			json.put("CITY_FLAG", "0");// 是否同城 =>应该只有木一些特定的银行需要填写
		}else{
			json.put("CITY_FLAG", "1");// 是否同城 =>应该只有木一些特定的银行需要填写
		}
		json.put("PRIORITY_FLAG", "Y");// 是否加急，默认加急 “Y”，加急；“N”：普通。
		json.put("OPERATION_TYPE", "3");// 支付渠道： “3”：银企直连支付（默认）
		json.put("UNION_BANK_NUMBER", UNION_BANK_NUMBER);// 联行号
		json.put("AMOUNT", bxls.get(0)[2]);// 表头合计金额 =》金额
		String gdSql = "SELECT NAME FROM BD_DEFDOC WHERE PK_DEFDOC = '"
				+ bxls.get(0)[0] + "'";
		List<Object[]> gdLs = getDao.query(gdSql);
		
		if (gdLs.size() > 0) {
			String zy = gdLs.get(0)[0] + "";
			if(zy.indexOf("光大")!=-1){
				json.put("PURPOSE", "财务报销");// 表头摘要 =》用途
			}else{
				json.put("PURPOSE", bxls.get(0)[3]);// 表头摘要 =》用途
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		json.put("NETBANK_EPT_DATE", sdf.format(new Date()));// 期望日期
		json.put("VERSION", "0");// 版本号
		String CHECK_CODE = getCheckCode(json.getString("ERP_PAYMENT_ID"),
				"Available", PAYMENT_ACCOUNTS, DEPOSIT_ACCOUNTS, bxls.get(0)[2]
						+ "")
				+ "";// 获取校验码
		json.put("CHECK_CODE", CHECK_CODE);// 校验码
		json.put("BILL_TYPE", "ER_JKZB");// 对应NC表
		json.put("PK_PRIMARYKEY", bxls.get(0)[5]);// 对应NC单据编号
		json.put("TRANSI_TYPE", pk_billtype);// 对应NC交易类型
		Logger.error("JSON字符串数据：" + json.toString());
		return json;
	}

	// 付款单通用JSON字符串
	@SuppressWarnings("unchecked")
	public JSONObject getFKDJson(List<Object[]> bxls, String pk_primaryKey,
			String pk_billtype, String recaccount) throws DAOException {
		Logger.error("pk_primaryKey>>>" + pk_primaryKey + "pk_billtype>>>"
				+ pk_billtype + "recaccount>>>" + recaccount);
		UUID uuid = UUID.randomUUID();
		JSONObject json = new JSONObject();
		json.put("ERP_PAYMENT_ID", uuid.toString().substring(0, 30));// 主键
		json.put("RECORD_STATUS", "Available");// 指令状态
		json.put("PAYMENT_TYPE_ID", "202");// 支付类型 202，对外支付（默认值）
		json.put("PAYMENT_BUSTYPE_ID", "0");// 支付子类型 0 一般支付（默认值）
		json.put("PAYMENT_METHOD_TYPE_ID", "3");// 结算方式 3，电汇；
		String bankSql = "SELECT * FROM RL_BANK WHERE PK_DEFDOC = '"
				+ bxls.get(0)[0] + "'";
		List<Object[]> bankLs = getDao.query(bankSql);
		String PAYMENT_ACCOUNTS = "";// 付款方银行账号
		if (bankLs != null && bankLs.size() > 0) {
			PAYMENT_ACCOUNTS = bankLs.get(0)[1] + "";
		}
		json.put("PAYMENT_ACCOUNTS", PAYMENT_ACCOUNTS);// 付款方银行账号
		json.put("CURRENCY_TYPE", "10");// 币种 10-人民币
		String DEPOSIT_BANK_NAME = "";// 收款方银行开户行
		String DEPOSIT_ACCOUNTS_NAME = "";// 收款方银行账户名称
		String DEPOSIT_ACCOUNTS = "";// 收款方银行账号
		String DEPOSIT_BANK_TYPE = "";// 收款方银行类型
		String DEPOSIT_PROVINCE = "";// 收款方银行开户行省
		String DEPOSIT_CITY = "";// 收款方银行开户行市
		String UNION_BANK_NUMBER = "";// 联行号
		String skBankSql = "SELECT pk_bankaccbas FROM bd_bankaccsub WHERE pk_bankaccsub = '"
				+ recaccount + "' AND nvl(dr,0) = 0 ";
		List<Object[]> skBank = getDao.query(skBankSql);// 收款银行账户
		if (skBank != null && skBank.size() > 0) {
			List<BankAccbasVO> skgrBank = (List<BankAccbasVO>) getDao
					.executeQuery(
							"SELECT * FROM bd_bankaccbas WHERE pk_bankaccbas = '"
									+ skBank.get(0)[0] + "' AND nvl(dr,0) = 0 ",
							new BeanListProcessor(BankAccbasVO.class));// 收款个人银行账户
			if (skgrBank != null && skgrBank.size() > 0) {
				DEPOSIT_ACCOUNTS_NAME = skgrBank.get(0).getAccname();// 收款方银行账户名称
				DEPOSIT_ACCOUNTS = skgrBank.get(0).getAccnum();// 收款方银行账号
				DEPOSIT_PROVINCE = skgrBank.get(0).getProvince();// 收款方银行开户行省
				DEPOSIT_CITY = skgrBank.get(0).getCity();// 收款方银行开户行市
				UNION_BANK_NUMBER = skgrBank.get(0).getCombineaccnum();// 联行号
				List<Object[]> bankDoc = getDao
						.query("SELECT name,combinenum,pk_banktype FROM bd_bankdoc WHERE pk_bankdoc = '"
								+ skgrBank.get(0).getPk_bankdoc()
								+ "' AND nvl(dr,0) = 0 ");// 银行档案
				if (bankDoc != null && bankDoc.size() > 0) {
					if (bankDoc.get(0)[0] != null && bankDoc.get(0)[0] != "") {
						DEPOSIT_BANK_NAME = bankDoc.get(0)[0] + "";// 开户银行
					}
					/*if (bankDoc.get(0)[1] != null && bankDoc.get(0)[1] != "") {
						UNION_BANK_NUMBER = bankDoc.get(0)[1] + "";// 联行号
					}*/
					//DEPOSIT_BANK_TYPE = bankDoc.get(0)[2] + "";// 助记码作为银行类别
					String dazjmsql = "SELECT MNECODE FROM bd_banktype WHERE pk_banktype = '"+bankDoc.get(0)[2]+"'";
					List<Object[]> banktp = getDao.query(dazjmsql);// 银行类别
					if (banktp != null && banktp.size() > 0) {
						DEPOSIT_BANK_TYPE = banktp.get(0)[0] + "";// 助记码作为银行类别
					}else{
						Logger.error("银行类别空！");
						return null;
					}
				}
			}
		}
		json.put("DEPOSIT_BANK_NAME", DEPOSIT_BANK_NAME);// 收款方银行开户行
		json.put("DEPOSIT_ACCOUNTS_NAME", DEPOSIT_ACCOUNTS_NAME);// 收款方银行账户名称
		json.put("DEPOSIT_ACCOUNTS", DEPOSIT_ACCOUNTS);// 收款方银行账号
		json.put("DEPOSIT_BANK_TYPE", DEPOSIT_BANK_TYPE);// 收款方银行类型
		json.put("DEPOSIT_PROVINCE", DEPOSIT_PROVINCE);// 收款方银行开户行省
		json.put("DEPOSIT_CITY", DEPOSIT_CITY);// 收收款方银行开户行市
		if("济南".equals(DEPOSIT_CITY) || "济南市".equals(DEPOSIT_CITY)){
			json.put("CITY_FLAG", "0");// 是否同城 =>应该只有木一些特定的银行需要填写
		}else{
			json.put("CITY_FLAG", "1");// 是否同城 =>应该只有木一些特定的银行需要填写
		}
		json.put("PRIORITY_FLAG", "Y");// 是否加急，默认加急 “Y”，加急；“N”：普通。
		json.put("OPERATION_TYPE", "3");// 支付渠道： “3”：银企直连支付（默认）
		json.put("UNION_BANK_NUMBER", UNION_BANK_NUMBER);// 联行号
		json.put("AMOUNT", bxls.get(0)[1]);// 表头合计金额 =》金额
		//json.put("PURPOSE", bxls.get(0)[2]);// 表头摘要 =》用途
		String gdSql = "SELECT NAME FROM BD_DEFDOC WHERE PK_DEFDOC = '"
				+ bxls.get(0)[0] + "'";
		List<Object[]> gdLs = getDao.query(gdSql);
		
		if (gdLs.size() > 0) {
			String zy = gdLs.get(0)[0] + "";
			if(zy.indexOf("光大")!=-1){
				json.put("PURPOSE", "财务报销");// 表头摘要 =》用途
			}else{
				if ("D3".equals(pk_billtype)) {
					json.put("PURPOSE", "材料款");// 表头摘要 =》用途
				}else if("F3-Cxx-01".equals(pk_billtype)) {
					json.put("PURPOSE", "工程款");// 表头摘要 =》用途
				}else{
					json.put("PURPOSE", bxls.get(0)[2]);// 表头摘要 =》用途.
				}
			}
		}else{
			if ("D3".equals(pk_billtype)) {
				json.put("PURPOSE", "材料款");// 表头摘要 =》用途
			}else if("F3-Cxx-01".equals(pk_billtype)) {
				json.put("PURPOSE", "工程款");// 表头摘要 =》用途
			}else{
				json.put("PURPOSE", bxls.get(0)[2]);// 表头摘要 =》用途.
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		json.put("NETBANK_EPT_DATE", sdf.format(new Date()));// 期望日期
		json.put("VERSION", "0");// 版本号
		String CHECK_CODE = getCheckCode(json.getString("ERP_PAYMENT_ID"),
				"Available", PAYMENT_ACCOUNTS, DEPOSIT_ACCOUNTS, bxls.get(0)[1]
						+ "")
				+ "";// 获取校验码
		json.put("CHECK_CODE", CHECK_CODE);// 校验码
		json.put("BILL_TYPE", "AP_PAYBILL");// 对应NC表
		json.put("PK_PRIMARYKEY", bxls.get(0)[3]);// 对应NC单据编号
		json.put("TRANSI_TYPE", pk_billtype);// 对应NC交易类型
		Logger.error("JSON字符串数据：" + json.toString());
		return json;
	}

	// 修改单据未已推送
	public void updateBillStatus(String tableName, String fieldname,
			String pkname, String pkvalue) throws DAOException {
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String sql = "update " + tableName + " set " + fieldname
				+ " = 'Y' where " + pkname + " = '" + pkvalue + "' and dr = 0";
		Logger.error("更新状态SQL：" + sql);
		getDao.executeUpdate(sql);
	}

	// 获取校验码
	public int getCheckCode(String ERP_PAYMENT_ID, String RECORD_STATUS,
			String PAYMENT_ACCOUNTS, String DEPOSIT_ACCOUNTS, String AMOUNT) {
		int s = 0;
		for (int i1 = 0; i1 < ERP_PAYMENT_ID.length(); ++i1) {
			char ch1 = ERP_PAYMENT_ID.charAt(i1);
			int in1 = (int) ch1;
			s += in1;
		}
		s += 39;
		for (int i2 = 0; i2 < RECORD_STATUS.length(); ++i2) {
			char ch2 = RECORD_STATUS.charAt(i2);
			int in2 = (int) ch2;
			s += in2;
		}
		s += 39;
		for (int i3 = 0; i3 < PAYMENT_ACCOUNTS.length(); ++i3) {
			char ch3 = PAYMENT_ACCOUNTS.charAt(i3);
			int in3 = (int) ch3;
			s += in3;
		}
		s += 39;
		for (int i4 = 0; i4 < DEPOSIT_ACCOUNTS.length(); ++i4) {
			char ch4 = DEPOSIT_ACCOUNTS.charAt(i4);
			int in4 = (int) ch4;
			s += in4;
		}
		s += 39;
		if (AMOUNT.substring(AMOUNT.length() - 1, AMOUNT.length()).equals("0")
				&& AMOUNT.indexOf(".") > 0) {
			AMOUNT = AMOUNT.substring(0, AMOUNT.length() - 1);
		}
		if (AMOUNT.substring(AMOUNT.length() - 1, AMOUNT.length()).equals("0")
				&& AMOUNT.indexOf(".") > 0) {
			AMOUNT = AMOUNT.substring(0, AMOUNT.length() - 1);
		}
		if (AMOUNT.substring(AMOUNT.length() - 1, AMOUNT.length()).equals(".")
				&& AMOUNT.indexOf(".") > 0) {
			AMOUNT = AMOUNT.substring(0, AMOUNT.length() - 1);
		}
		for (int i5 = 0; i5 < AMOUNT.length(); ++i5) {
			char ch5 = AMOUNT.charAt(i5);
			int in5 = (int) ch5;
			s += in5;
		}
		s += 39;
		s += 39;
		s = ((s % 999) * (s % 2184)) % 9999;
		return s;
	}

	// 调用HTTP接口
	public String sendMidDataBase(String url1, String bxdData) throws Exception {
		String flag = "";
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
		// Logger.error("bxdData==" + bxdData);
		String content = "bxdData=" + URLEncoder.encode(bxdData, "UTF-8");
		// String content = "bxdData=" + bxdData;
		// DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写到流里面
		out.writeBytes(content);
		// 流用完记得关
		out.flush();
		out.close();
		// 获取响应
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), "UTF-8"));

		String line;
		while ((line = reader.readLine()) != null) {
			Logger.error("返回结果：" + line);
			if (!"OK".equals(line)) {
				return line;
			}
		}
		reader.close();
		// 结束,记得把连接断了
		connection.disconnect();
		return flag;
	}

	// 调用HTTP接口
	public String isPush(String url1, String pkPrimaryKey) throws Exception {
		String flag = "";
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
		// Logger.error("bxdData==" + bxdData);
		String content = "pk_primaryKey="
				+ URLEncoder.encode(pkPrimaryKey, "UTF-8");
		// String content = "bxdData=" + bxdData;
		// DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写到流里面
		out.writeBytes(content);
		// 流用完记得关
		out.flush();
		out.close();
		// 获取响应
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), "UTF-8"));

		String line;
		while ((line = reader.readLine()) != null) {
			Logger.error("返回结果：" + line);
			flag = line;
		}
		reader.close();
		// 结束,记得把连接断了
		connection.disconnect();
		return flag;
	}
}