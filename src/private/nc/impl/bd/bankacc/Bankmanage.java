package nc.impl.bd.bankacc;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.itf.bd.bankacc.cust.ICustBankaccService;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.bd.bankaccount.cust.CustBankaccUnionVO;
import nc.vo.bd.cust.CustbankVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.obm.log.ObmLog;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.system.utils.GetRequestJsonUtils;

@SuppressWarnings({ "restriction" })
public class Bankmanage implements IHttpServletAdaptor {

	private HYPubBO hyPubBO;

	@Override
	public void doAction(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		ISecurityTokenCallback sc = (ISecurityTokenCallback) NCLocator
				.getInstance().lookup(ISecurityTokenCallback.class);
		sc.token("NCSystem".getBytes(), "pfxx".getBytes());
		InvocationInfoProxy.getInstance().setUserDataSource("ORCL");
		// req.setCharacterEncoding("utf-8");
		String reqstr = null;
		JSONObject jsonObject = new JSONObject();
		String mes = "";
		setResponseContentType(resp);
		PrintWriter out = resp.getWriter();
		try {
			req.getCharacterEncoding();
			ObmLog.info("提取URL请求中的参数", getClass(), "doAction");
			// 接收的数据
			reqstr = GetRequestJsonUtils.getRequestJsonString(req);
			Logger.error("输出参数："+reqstr);
			System.out.println("输出参数："+reqstr);
			mes = this.distribute(reqstr);
		} catch (Exception e) {
			jsonObject.put("statusCode", 300);
			jsonObject.put("message", "操作异常："+e.getMessage());
			mes = jsonObject.toString();
			e.printStackTrace();
		}
		out.print(mes);
		out.close();
	}

	private void setResponseContentType(HttpServletResponse response) {
		response.setContentType("application/json; charset=utf-8");
	}

	// 校验参数格式
	private String distribute(String reqstr) throws Exception {
		JSONObject rtnjson = new JSONObject();
		ObmLog.info("接收到参数。。。" + reqstr, getClass(), "distribute");
		JSONObject jsonObject = (JSONObject) JSONObject.parse(reqstr);
		String billtype = jsonObject.getString("billtype");
		String bill = jsonObject.getString("bill");
		if (StringUtil.isEmpty(billtype)) {
			rtnjson.put("statusCode", 300);
			rtnjson.put("message", "异常：billtype为空！");
			return rtnjson.toString();
		}
		if (StringUtil.isEmpty(bill)) {
			rtnjson.put("statusCode", 300);
			rtnjson.put("message", "异常：bill为空！");
			return rtnjson.toString();
		}
		return doBankInfo(billtype, bill);
	}

	// 保存银行账户
	private String doBankInfo(String billtype, String bill) throws Exception {
		// TODO Auto-generated method stub
		JSONObject rtnjson = new JSONObject();
		JSONObject jsonObject = (JSONObject) JSONObject.parse(bill);
		if ("psnbankaccbas".equals(billtype)) {
			return doPsnBankInfo(jsonObject);
		} else if ("custsuppbankacc".equals(billtype)) {
			// 客户供应商
			return doCustomerBankInfo(jsonObject);
		} else {
			rtnjson.put("statusCode", 300);
			rtnjson.put("message", "billtype类型有误，请修改!");
			return rtnjson.toString();
		}
	}

	// 客户银行账户
	private String doCustomerBankInfo(JSONObject bill) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		JSONObject rtnjson = new JSONObject();
		JSONObject bankjson = bill.getJSONObject("bankaccbasVO");
		JSONObject custbankjson = bill.getJSONObject("custbankVO");
		if (StringUtils.isEmpty(custbankjson.getString("pk_cust"))) {
			rtnjson.put("statusCode", 300);
			rtnjson.put("message", "pk_cust客户/供应商不能为空!");
			return rtnjson.toString();
		}
		BankAccbasVO bVo = new BankAccbasVO();// 银行账户信息
		CustbankVO cusVo = new CustbankVO();// 银行账号
		BankAccSubVO bankaccsubvo = new BankAccSubVO();// 设置银行账户子户

		String result = checkIfSave(bankjson);// 校验银行账户信息是否完整
		if (StringUtils.isNotEmpty(result)) {
			return result;
		}
		result = "";
		try {
			int accclass = bankjson.getIntValue("accclass");// 账户分类
			CustBankaccUnionVO custBankaccUnionVO = new CustBankaccUnionVO();
			String pk_group_strwhere = " code = '"
					+ bankjson.getString("pk_group") + "'";
			String pk_group = getValueByCode("org_group", "pk_group",
					pk_group_strwhere);
			InvocationInfoProxy.getInstance().setGroupId(pk_group);
			bVo.setPk_group(pk_group);// 所属集团
			bVo.setAccopendate(new UFDate(bankjson.getDate("accopendate")));// 开户日期
			bVo.setEnablestate(2);// 1=未启用，2=已启用，3=已停用
			bVo.setOrgnumber(bankjson.getString("orgnumber"));// 机构号/分行号
			bVo.setAccnum(bankjson.getString("accnum"));// 账号
			// bankaccsubvo.setAccnum(bankjson.getString("accnum"));// 账号
			if (StringUtils.isNotEmpty(bankjson.getString("pk_netbankinftp"))) {
				// 网银接口类别 关联档案 bd_netbankinftp 网银信息模板
				String pk_netbankinftp_strwhere = " code = '"
						+ bankjson.getString("pk_netbankinftp") + "'";
				String pk_netbankinftp = getValueByCode("bd_netbankinftp",
						"pk_netbankinftp", pk_netbankinftp_strwhere);
				bVo.setPk_netbankinftp(pk_netbankinftp);
			}
			bVo.setCity(bankjson.getString("city"));// 城市
			bVo.setMemo(bankjson.getString("memo"));// 备注
			// 银行档案
			String pk_bankdoc_strwhere = " code = '"
					+ bankjson.getString("pk_bankdoc") + "'";
			String bd_bankdoc = getValueByCode("bd_bankdoc", "pk_bankdoc",
					pk_bankdoc_strwhere);
			bVo.setPk_bankdoc(bd_bankdoc);
			// 地区代码
			if (StringUtils.isNotEmpty(bankjson.getString("areacode"))) {
				// 地区代码关联档案 bd_defdoc 地区代码自定义档案
				String areacode_strwhere = " code = '"
						+ bankjson.getString("areacode")
						+ "' and pk_defdoclist in (select pk_defdoclist "
						+ "from bd_defdoclist where name = '地区代码')";
				String areacode = getValueByCode("bd_defdoc", "pk_defdoc",
						areacode_strwhere);
				bVo.setAreacode(areacode);
			}
			// 所属组织
			/*String pk_org_strwhere = " code = '" + bankjson.getString("pk_org")
					+ "' and isbusinessunit = 'Y' and enablestate = '2'";
			String pk_org = getValueByCode("org_orgs", "pk_org",
					pk_org_strwhere);*/
			bVo.setPk_org(pk_group);
			// 账户属性
			bVo.setAccattribute(bankjson.getIntValue("accattribute"));
			// 省份
			if (bankjson.getString("province") != null) {
				bVo.setProvince(bankjson.getString("province"));
			}
			// 联系电话
			if (bankjson.getString("tel") != null) {
				bVo.setTel(bankjson.getString("tel"));
			}
			Logger.error("输出户名参数："+bankjson.getString("accname"));
			System.out.println("输出户名参数："+bankjson.getString("accname"));
			bVo.setAccname(bankjson.getString("accname"));// 户名
			// bankaccsubvo.setAccname(bankjson.getString("accname"));// 户名
			bVo.setAccountproperty(bankjson.getIntValue("accountproperty"));// 账户性质
			// 银行类别
			String pk_banktype_strwhere = " code = '"
					+ bankjson.getString("pk_banktype") + "' ";
			String pk_banktype = getValueByCode("bd_banktype", "pk_banktype",
					pk_banktype_strwhere);
			bVo.setPk_banktype(pk_banktype);
			// 客户编号
			if (bankjson.getString("customernumber") != null) {
				bVo.setCustomernumber(bankjson.getString("customernumber"));
			}
			// 所在地
			if (bankjson.getString("address") != null) {
				bVo.setAddress(bankjson.getString("address"));
			}
			bVo.setAccstate(0); // 账户状态 "0=正常，1=冻结，2=部分冻结，3=销户，"
			bVo.setAccclass(accclass); // 账户分类 "0=个人，1=客户，2=公司，3=供应商，"
			// 联行号
			if (bankjson.getString("combinenum") != null) {
				bVo.setCombinenum(bankjson.getString("combinenum"));
			}
			bVo.setIssigned(UFBoolean.FALSE);// 签约
			// 联系人
			if (bankjson.getString("contactpsn") != null) {
				bVo.setContactpsn(bankjson.getString("contactpsn"));
			}
			// 开户地区
			if (bankjson.getString("bankarea") != null) {
				bVo.setBankarea(bankjson.getString("bankarea"));
			}
			// 币种
			String pk_currtype_strwhere = " code = 'CNY'";
			String pk_currtype = getValueByCode("bd_currtype", "pk_currtype",
					pk_currtype_strwhere);
			bankaccsubvo.setPk_currtype(pk_currtype);
			bankaccsubvo.setStatus(VOStatus.NEW);
			bVo.setStatus(VOStatus.NEW);
			bVo.setBankaccsub(new BankAccSubVO[] { bankaccsubvo });

			custBankaccUnionVO.setBankaccbasVO(bVo);

			/**
			 * cusVo银行账号VO
			 */
			String pk_cust = "";
			if (accclass == 1) {
				String pk_cust_strwhere = " code = '"
						+ custbankjson.getString("pk_cust") + "'";
				pk_cust = getValueByCode("bd_customer", "pk_customer",
						pk_cust_strwhere);
			} else if (accclass == 3) {
				String pk_cust_strwhere = " code = '"
						+ custbankjson.getString("pk_cust") + "'";
				pk_cust = getValueByCode("bd_supplier", "pk_supplier",
						pk_cust_strwhere);
			}
			cusVo.setPk_cust(pk_cust);
			cusVo.setAccclass(accclass); // 账户分类 "0=个人，1=客户，2=公司，3=供应商，"
			cusVo.setStatus(VOStatus.NEW);
			custBankaccUnionVO.setCustbankVO(cusVo);
			ICustBankaccService iCustBankaccService = (ICustBankaccService) NCLocator
					.getInstance().lookup(ICustBankaccService.class);
			iCustBankaccService.insertCustBankacc(custBankaccUnionVO);
			rtnjson.put("statusCode", 200);
			rtnjson.put("message", "操作成功！");
			return rtnjson.toString();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			rtnjson.put("statusCode", 300);
			rtnjson.put("message", "操作失败:" + e.getMessage());
			e.printStackTrace();
			return rtnjson.toString();
		}
	}

	private String checkIfSave(JSONObject bankjson) {
		// TODO Auto-generated method stub
		JSONObject rtnjson = new JSONObject();
		rtnjson.put("statusCode", 300);
		if (StringUtils.isEmpty(bankjson.getString("pk_group"))) {
			rtnjson.put("message", "pk_group集团不能为空!");
			return rtnjson.toString();
		}
		if (StringUtils.isEmpty(bankjson.getString("accopendate"))) {
			rtnjson.put("message", "accopendate开户日期不能为空!");
			return rtnjson.toString();
		}
		if (StringUtils.isEmpty(bankjson.getString("accnum"))) {
			rtnjson.put("message", "accnum账号不能为空!");
			return rtnjson.toString();
		}
		if (StringUtils.isEmpty(bankjson.getString("accnum"))) {
			rtnjson.put("message", "accnum账号不能为空!");
			return rtnjson.toString();
		}
		if (StringUtils.isEmpty(bankjson.getString("pk_bankdoc"))) {
			rtnjson.put("message", "pk_bankdoc开户银行不能为空!");
			return rtnjson.toString();
		}
		if (StringUtils.isEmpty(bankjson.getString("pk_org"))) {
			rtnjson.put("message", "pk_org所属组织不能为空!");
			return rtnjson.toString();
		}
		int accattribute = bankjson.getIntValue("accattribute");
		if (accattribute < 0 || accattribute > 3) {
			rtnjson.put("message", "accattribute账户属性参数错误!");
			return rtnjson.toString();
		}
		if (StringUtils.isEmpty(bankjson.getString("accname"))) {
			rtnjson.put("message", "accname户名不能为空!");
			return rtnjson.toString();
		}
		if (StringUtils.isEmpty(bankjson.getString("pk_banktype"))) {
			rtnjson.put("message", "pk_banktype银行类别不能为空!");
			return rtnjson.toString();
		}
		if (StringUtils.isEmpty(bankjson.getString("accountproperty"))) {
			rtnjson.put("message", "accountproperty账户性质不能为空!");
			return rtnjson.toString();
			
		}
		int accountproperty = bankjson.getIntValue("accountproperty");
		if (accountproperty != 0 && accountproperty != 1) {
			rtnjson.put("message", "accountproperty账户性质参数错误!");
			return rtnjson.toString();
		}
		if (StringUtils.isEmpty(bankjson.getString("accclass"))) {
			rtnjson.put("message", "accclass账户分类不能为空!");
			return rtnjson.toString();
		}
		int accclass = bankjson.getIntValue("accclass");
		if (accclass != 1 && accclass != 3) {
			rtnjson.put("message", "accclass账户分类参数错误!");
			return rtnjson.toString();
		}
		return null;
	}

	// 人员银行账户
	private String doPsnBankInfo(JSONObject bill) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param table_name
	 *            表名
	 * @param valuecode
	 *            查找的字段名
	 * @param strwhere
	 *            where条件语句
	 * @return
	 * @throws BusinessException
	 */
	private String getValueByCode(String table_name, String valuecode,
			String strwhere) throws BusinessException {
		String value = (String) getHyPubBO().findColValue(table_name,
				valuecode, "nvl(dr,0) = 0 and" + strwhere);
		if (value == null) {
			throw new BusinessException(strwhere + "翻译错误，请确认是否存在！");
		}
		return value;
	}

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}
}
