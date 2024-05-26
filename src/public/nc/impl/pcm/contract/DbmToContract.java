package nc.impl.pcm.contract;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.trade.business.HYPubBO;
import nc.itf.pcm.contract.DbmSplitToContr;
import nc.itf.pcm.contract.pvt.IContract;
import nc.pubitf.pmr.pm.IPmFeebalanceCtMaintain;
import nc.ui.pcm.utils.GetDao;
import nc.vo.obm.log.ObmLog;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContrWorksVO;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pub.BusinessException;
import nc.vo.scmpub.api.rest.utils.RestUtils;

import org.apache.commons.lang.StringUtils;
import org.json.JSONString;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.system.utils.MessageResult;

// 多编码合同拆清单发包合同对外接口
@SuppressWarnings({ "restriction", "rawtypes", "unchecked" })
public class DbmToContract implements IHttpServletAdaptor {
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	@Override
	public void doAction(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// 模拟用户登录，不受单点登录控制
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");
		// InvocationInfoProxy.getInstance().setUserDataSource("RLJT");
		ISecurityTokenCallback sc = (ISecurityTokenCallback) NCLocator
				.getInstance().lookup(ISecurityTokenCallback.class);
		sc.token("NCSystem".getBytes(), "pfxx".getBytes());
		resp.setContentType("application/json; charset=utf-8");
		PrintWriter out = resp.getWriter();
		// 返回结果
		MessageResult result = new MessageResult();
		// 接收的数据
		String pm_feebalance = req.getParameter("pm_feebalance");// 多编码合同主键
		// 当前执行自定义拆分数量，传入多少个就拆分多少，不传则拆分全部
		int num = req.getParameter("num") == null ? 0 : Integer.parseInt(req.getParameter("num"));
		AggPmFeebalance[] aggvoS = null;
		Set bill_codes = new HashSet<>();// 记录已生成合同
		Set err_bill_codes = new HashSet<>();// 记录报错合同
		// 通过主键查询多编码合同
		try {
			String[] pm_feebalance_arrs = null;
			if (pm_feebalance != null) {
				pm_feebalance_arrs = pm_feebalance.split(",");
			} else {
				// 查询满足条件的多编码合同主键
				StringBuilder sql = new StringBuilder();
				sql.append("SELECT PM_FEEBALANCE FROM PM_FEEBALANCE_CT WHERE PK_ORG IN (SELECT CODE FROM BD_DEFDOC WHERE PK_DEFDOCLIST ");
				sql.append("IN (SELECT PK_DEFDOCLIST FROM BD_DEFDOCLIST WHERE CODE = 'DBM2QDORG' AND NVL(DR,0) = 0) AND NVL(DR,0) = 0) AND ");
				sql.append("NVL(DR,0) = 0 AND FSTATUSFLAG = 1 AND VDEF19 <> 'Y'");
				if(num != 0){
					sql.append("AND ROWNUM <= "+num+"");
				}
				sql.append(" ORDER BY BILLMAKETIME DESC");
				List<Object[]> fkls = getDao.query(sql.toString());
				pm_feebalance_arrs = new String[fkls.size()];
				// 如果有满足条件数据-->执行
				if (fkls != null && fkls.size() > 0 && fkls.get(0) != null) {
					for (int i = 0; i < fkls.size(); i++) {
						Object[] string = fkls.get(i);
						pm_feebalance_arrs[i] = string[0].toString();
					}
				}
			}
			if (pm_feebalance_arrs != null) {
				// 多编码合同AggVO
				aggvoS = (AggPmFeebalance[]) NCLocator.getInstance()
						.lookup(IPmFeebalanceCtMaintain.class)
						.queryObjectByPks(pm_feebalance_arrs);
				if(aggvoS != null && aggvoS.length > 0){
					for (AggPmFeebalance aggPmFeebalance : aggvoS) {
						// 调用多编码合同拆分接口，传入单个多编码合同
						DbmSplitToContr split = NCLocator.getInstance().lookup(DbmSplitToContr.class);
						String res = split.SplitToContr(aggPmFeebalance);
						if(StringUtils.isEmpty(res)){
							bill_codes.add(aggPmFeebalance.getParent().getBill_code()+"拆分成功！");
						}else{
							err_bill_codes.add(aggPmFeebalance.getParent().getBill_code() + res);
							IContract qditf = NCLocator.getInstance().lookup(
									IContract.class);
							String[] userObj = { ContractBillVO.class.getName(),
									ContrHeadVO.class.getName(), ContrWorksVO.class.getName() };
							String strWhere = " NVL(DR,0) = 0 AND HDEF53 = '"+aggPmFeebalance.getParent().getBill_code()+"'";
							ContractBillVO[] aggvos = (ContractBillVO[]) new HYPubBO().queryBillVOByCondition(userObj, strWhere);
							if(aggvos != null){
								try {
									qditf.deleteContr(aggvos);
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						}
					}
				}
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			result.setStatusCode(MessageResult.STATUS_ERROR);
			result.setMessage("根据主键获取多编码合同报错:" + getExceptionStr(e));
			e.printStackTrace();
		}
		if (err_bill_codes.size() <= 0) {
			result.setStatusCode(MessageResult.STATUS_SUCCESS);
			result.setMessage(bill_codes + "操作成功！");
		} else {
			result.setStatusCode(MessageResult.STATUS_ERROR);
			result.setMessage(err_bill_codes.toString() + "成功条数："
					+ bill_codes.size());
		}
		JSONString jsonString = RestUtils.toJSONString(result);
		ObmLog.info("返回的JSON", getClass(), "doAction");
		ObmLog.info(jsonString.toJSONString(), getClass(), "doAction");
		JSONObject parseObject = JSONObject.parseObject(jsonString
				.toJSONString());
		out.print(parseObject);
		out.close();
	}
	// 获取输出报错参数
	private static String getExceptionStr(Exception e) throws IOException {
		// 读取异常栈信息
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(arrayOutputStream));
		// 通过ByteArray转换输入输出流
		BufferedReader fr = new BufferedReader(new InputStreamReader(
				new ByteArrayInputStream(arrayOutputStream.toByteArray())));
		String str;
		StringBuilder exceptionStr = new StringBuilder();
		while ((str = fr.readLine()) != null) {
			exceptionStr.append(str);
		}
		// 关闭流
		fr.close();
		return exceptionStr.toString();
	}
}