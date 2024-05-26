package nc.ui.fct.ap.action;

import java.awt.event.ActionEvent;

import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.oa.web.FilePathManage;
import nc.oa.web.NCService;
import nc.oa.web.NCServiceSoap;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.ct.term.entity.TermVO;
import nc.vo.fct.ap.entity.AggCtApVO;
import nc.vo.fct.ap.entity.CtApBVO;
import nc.vo.fct.ap.entity.CtApTermVO;
import nc.vo.fct.ap.entity.CtApVO;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.sm.UserVO;
import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
//import nc.vo.bd.currtype.CurrtypeVO;
//import nc.vo.org.CorpVO;

@SuppressWarnings("restriction")
public class FKHTApCommitScriptAction extends ApCommitScriptAction {
	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO 自动生成的方法存根
		super.doAction(e);
		AggCtApVO fkhtVO = (AggCtApVO) this.getModel().getSelectedData();
		if (fkhtVO != null) {
			CtApVO headVO = fkhtVO.getParentVO();
			OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
					headVO.getPk_org());
			if (orgVO.getDef2() == null) {
				if (!StringUtils.equals("0001A21000000001H7J3", headVO.getPk_org())) {
					String mainTaleInfo = getMainInfo(headVO);// 主表数据
					String ifblht = headVO.getVdef15();// 是否补录
					if ("1001A2100000000B68C1".equals(ifblht)) {

					} else {
						Logger.error("----------主表数据：------------" + mainTaleInfo);
						String contractPub = ""; // 合同基本信息
						String ContractExp = ""; // 取合同费用
						CircularlyAccessibleValueObject[] htjbVOs = fkhtVO
								.getTableVO("pk_fct_ap_b");
						if (htjbVOs != null) {
							contractPub = getHTJBInfo(htjbVOs);
						}
						Logger.error("----------合同基本：------------" + contractPub);
						CircularlyAccessibleValueObject[] httkVOs = fkhtVO
								.getTableVO("pk_fct_ap_term");
						if (httkVOs != null) {
							ContractExp = getHTTKInfo(httkVOs);
						}
						Logger.error("----------合同条款：------------" + ContractExp);
						// 取单据的附件信息
						FilePathManage fileManager = new FilePathManage();
						String filePath = fileManager.getPahtFile(headVO
								.getPrimaryKey());
						Logger.error("--------------附件信息---------------" + filePath);
						// 调用OA的webservice
						Logger.error("-----------开始调用webservic--------------");
						// 合同基本信息 //取合同费用
						NCService service = new NCService();
						NCServiceSoap serviceSoap = service.getNCServiceSoap();
						String webRtn = serviceSoap.savencworks("30", mainTaleInfo,
								contractPub, ContractExp, "", "", "", filePath);
						// 返回结果
						Logger.error("----返回结果-----------" + webRtn);
						if (!"0".equals(webRtn)) {
							MessageDialog.showErrorDlg(null, "错误提示", "提交失败！");
						}
						Logger.error("----调用webservic结束-----------");
					}
				} else {
					Logger.error("未获取到付款合同VO-----");
				}
			}
		}
	}

	// 获取主表信息
	public static String getMainInfo(CtApVO headVO) throws UifException {
		JSONObject json = new JSONObject();
		String pk_fct_ap = headVO.getPrimaryKey();// 单据主键
		String billMaker = headVO.getBillmaker() + "";// 制单人主键
		String billMakerName = "";// 制单人名称
		UserVO userVO = (UserVO) HYPubBO_Client.queryByPrimaryKey(UserVO.class,
				headVO.getBillmaker() + "");
		if (userVO != null) {
			billMakerName = userVO.getUser_name() + ""; // 制单人
		}
		String billMakerTime = headVO.getDbilldate() + "";// 制单时间

		String pk_org = headVO.getPk_org();// 组织PK
		String pk_orgname = "";// 组织名称
		OrgVO zzVO = (OrgVO) HYPubBO_Client.queryByPrimaryKey(OrgVO.class,
				pk_org);// 组织VO
		if (zzVO != null) {
			pk_orgname = zzVO.getName() + "";
		}
		String vbillcode = headVO.getVbillcode() + "";// 合同编码
		String ctname = headVO.getCtname() + "";// 合同名称
		String htlb = headVO.getVdef18() + "";// 合同类别主键
		String htlbmc = "";// 合同类别名称
		DefdocVO htlbvo = (DefdocVO) HYPubBO_Client.queryByPrimaryKey(
				DefdocVO.class, htlb + "");
		if (htlbvo != null) {
			htlbmc = htlbvo.getName();
		}
		String vdef3 = headVO.getVdef3();// 招标方式PK
		String vdef3name = "";// 招标方式
		DefdocVO zbfsvo = (DefdocVO) HYPubBO_Client.queryByPrimaryKey(
				DefdocVO.class, vdef3 + "");
		if (zbfsvo != null) {
			vdef3name = zbfsvo.getName();
		}
		String subscribedate = headVO.getSubscribedate() + "";// 建立日期
		String valdate = headVO.getValdate() + "";// 起始日期
		String invallidate = headVO.getInvallidate() + "";// 终止日期
		String personnelid = headVO.getPersonnelid() + "";// 承办人员主键
		String personnelname = "";// 承办人员名称
		PsndocVO psndocVO = (PsndocVO) HYPubBO_Client.queryByPrimaryKey(
				PsndocVO.class, headVO.getPersonnelid() + "");
		if (psndocVO != null) {
			personnelname = psndocVO.getName();
		}
		String depid_v = headVO.getDepid() + "";// 承办部门版本主键
		String depid_vname = "";// 承办部门版本
		DeptVO deptVO = (DeptVO) HYPubBO_Client.queryByPrimaryKey(DeptVO.class,
				headVO.getDepid() + "");
		if (deptVO != null) {
			depid_vname = deptVO.getName();
		}
		String vdef2 = headVO.getVdef2();// 分管领导PK
		String vdef2name = "";// 分管领导
		DefdocVO fgldvo = (DefdocVO) HYPubBO_Client.queryByPrimaryKey(
				DefdocVO.class, vdef2 + "");
		if (fgldvo != null) {
			vdef2name = fgldvo.getName();
		}
		String ntotalorigmny = headVO.getNtotalorigmny() + "";// 合同金额
		String vdef16 = headVO.getVdef16() + "";// 金额大写
		String cvendorid = headVO.getCvendorid() + "";// 供应商主键
		String cvendorname = "";// 供应商名称
		SupplierVO supergvo = (SupplierVO) HYPubBO_Client.queryByPrimaryKey(
				SupplierVO.class, cvendorid);
		if (supergvo != null) {
			cvendorname = supergvo.getName();
		}
		String bankaccount = headVO.getBankaccount() + "";// 对方银行账号主键
		String bankaccountno = "";// 对方银行账号
		BankAccSubVO bankvo = (BankAccSubVO) HYPubBO_Client.queryByPrimaryKey(
				BankAccSubVO.class, headVO.getBankaccount() + "");
		if (bankvo != null) {
			bankaccountno = bankvo.getAccnum();
		}
		String vdef17 = headVO.getVdef17();// 合同变更PK
		String vdef17name = "";// 合同变更
		DefdocVO htbgvo = (DefdocVO) HYPubBO_Client.queryByPrimaryKey(
				DefdocVO.class, vdef17 + "");
		if (htbgvo != null) {
			vdef17name = htbgvo.getName();
		}
		String norigpshamount = headVO.getNorigpshamount() + "";// 累计原币付款金额
		String norigcopamount = headVO.getNorigcopamount() + "";// 累计原币开票金额
		String vdef20 = headVO.getVdef20() + "";// 备注
		String ctrantypeid = headVO.getCtrantypeid() + "";// 合同类型主键
		String ctrantypename = "";// 合同类型名称
		BilltypeVO billtypeVO = (BilltypeVO) HYPubBO_Client.queryByPrimaryKey(
				BilltypeVO.class, headVO.getCtrantypeid());
		if (billtypeVO != null) {
			ctrantypename = billtypeVO.getBilltypename() + "";
		}

		// 徐文军新增是否特殊合同 2021.4.8
		String vdef1 = headVO.getVdef1();// 是否特殊合同
		if (StringUtils.isNotBlank(vdef1)) {
			DefdocVO defdocVO = (DefdocVO) HYPubBO_Client.queryByPrimaryKey(
					DefdocVO.class, vdef1);
			json.put("vdef1", defdocVO == null ? "" : defdocVO.getName());
		}
		json.put("vdef12", headVO.getVdef12());
		json.put("pk_fct_ap", pk_fct_ap);
		json.put("billMaker", billMaker);
		json.put("billMakerName", billMakerName);
		json.put("billMakerTime", billMakerTime);
		json.put("pk_org", pk_org);
		json.put("pk_orgname", pk_orgname);
		json.put("vbillcode", vbillcode);
		json.put("ctname", ctname);
		json.put("subscribedate", subscribedate);
		json.put("valdate", valdate);
		json.put("invallidate", invallidate);
		json.put("cvendorid", cvendorid);
		json.put("cvendorname", cvendorname);
		json.put("personnelid", personnelid);
		json.put("personnelname", personnelname);
		json.put("depid_v", depid_v);
		json.put("depid_vname", depid_vname);
		json.put("norigpshamount", norigpshamount);
		json.put("norigcopamount", norigcopamount);
		json.put("ntotalorigmny", ntotalorigmny);
		json.put("bankaccount", bankaccount);
		json.put("bankaccountno", bankaccountno);
		json.put("vdef2", vdef2);
		json.put("vdef2name", vdef2name);
		json.put("vdef3", vdef3);
		json.put("vdef3name", vdef3name);
		json.put("transi_type", headVO.getVtrantypecode());// 交易类型
		// 新增
		json.put("htlb", htlb);// 合同类别PK
		json.put("htlbmc", htlbmc);// 合同类别名称
		json.put("vdef16", vdef16);// 金额大写 vdef17
		json.put("vdef17", vdef17);// 合同变更PK
		json.put("vdef17name", vdef17name);// 合同变更名称
		json.put("vdef20", vdef20);// 备注
		json.put("ctrantypeid", ctrantypeid);
		json.put("ctrantypename", ctrantypename);
		return JSONObject.toJSONString(json,
				SerializerFeature.WriteMapNullValue);
	}

	// 获取合同基本信息
	private static String getHTJBInfo(
			CircularlyAccessibleValueObject[] childrenVOs) throws UifException {
		JSONArray arrjson = new JSONArray();
		for (int j = 0; j < childrenVOs.length; j++) {
			JSONObject jo = new JSONObject();
			CtApBVO jbVO = (CtApBVO) childrenVOs[j];
			String crowno = jbVO.getCrowno();// 行号
			String project = "";// 项目编码
			String projectname = "";// 项目名称
			ProjectHeadVO xmvo = (ProjectHeadVO) HYPubBO_Client
					.queryByPrimaryKey(ProjectHeadVO.class, project + "");
			if (xmvo != null) {
				projectname = xmvo.getProject_name();
				project = xmvo.getProject_code();
			}
			/*
			 * String inoutcome = jbVO.getInoutcome() + "";// 收支项目主键 String
			 * inoutcomename = "";// 收支项目名称 InoutBusiClassVO szxmvo =
			 * (InoutBusiClassVO) HYPubBO_Client
			 * .queryByPrimaryKey(InoutBusiClassVO.class, inoutcome + ""); if
			 * (szxmvo != null) { inoutcomename = szxmvo.getName(); } String
			 * norigtaxmny = jbVO.getNorigtaxmny() + "";// 原币价税合计 String
			 * noriplangpmny = jbVO.getNoriplangpmny() + "";// 累计原币预付款金额 String
			 * noritotalgpmny = jbVO.getNoritotalgpmny() + "";// 累计原币付款金额 String
			 * noricopegpmny = jbVO.getNoricopegpmny() + "";// 累计原币开票金额
			 */
			String vbdef3 = jbVO.getVbdef3() + "";// 物料名称
			String vbdef4 = jbVO.getVbdef4() + "";// 规格
			String vbdef1 = jbVO.getVbdef5() + "";// 名称
			String nnum = jbVO.getNnum() + "";// 数量
			String ngtaxprice = jbVO.getNgtaxprice() + "";// 单价
			String ntaxmny = jbVO.getNtaxmny() + "";// 金额
			String slpk = jbVO.getVbdef17() + "";// 税率PK
			String sl = "";// 税率
			DefdocVO slvo = (DefdocVO) HYPubBO_Client.queryByPrimaryKey(
					DefdocVO.class, slpk + "");
			if (slvo != null) {
				sl = slvo.getName();
			}
			String vbdef19 = jbVO.getVbdef19() + "";// 税额
			String vbdef15 = jbVO.getVbdef15() + "";// 不含税金额
			String ntotalgpmny = jbVO.getNtotalgpmny() + "";// 累计付款金额
			String ncopegpmny = jbVO.getNcopegpmny() + "";// 累计开票金额
			String vmemo = jbVO.getVmemo() + "";// 备注
			jo.put("crowno", crowno);
			jo.put("project", project);
			jo.put("projectname", projectname);
			jo.put("vbdef1", vbdef1);
			jo.put("nnum", nnum);
			jo.put("ngtaxprice", ngtaxprice);
			jo.put("ntaxmny", ntaxmny);
			jo.put("vbdef3", vbdef3);
			jo.put("vbdef4", vbdef4);
			jo.put("sl", sl);
			jo.put("vbdef19", vbdef19);
			jo.put("vbdef15", vbdef15);
			jo.put("ntotalgpmny", ntotalgpmny);
			jo.put("ncopegpmny", ncopegpmny);
			jo.put("vmemo", vmemo);

			arrjson.add(jo);
		}
		return arrjson.toString();
	}

	// 获取合同条款信息
	private static String getHTTKInfo(
			CircularlyAccessibleValueObject[] childrenVOs) throws UifException {
		JSONArray arrjson = new JSONArray();
		for (int j = 0; j < childrenVOs.length; j++) {
			JSONObject jo = new JSONObject();
			CtApTermVO jbVO = (CtApTermVO) childrenVOs[j];
			String vtermcode = "";// 条款编码
			String vtermname = "";// 条款名称
			String vtermtypename = jbVO.getVtermtypename() + "";// 条款类型
			String vtermcontent = "";// 条款内容
			TermVO tkvo = (TermVO) HYPubBO_Client.queryByPrimaryKey(
					TermVO.class, jbVO.getVtermcode() + "");
			if (tkvo != null) {
				vtermcode = tkvo.getVtermcode();
				vtermname = tkvo.getVtermname();
				vtermcontent = tkvo.getVtermcontent();
			}
			String votherinfo = jbVO.getVotherinfo();// 其他信息
			String vmemo = jbVO.getVmemo();// 备注
			jo.put("vtermcode", vtermcode);
			jo.put("vtermname", vtermname);
			jo.put("vtermtypename", vtermtypename);
			jo.put("vtermcontent", vtermcontent);
			jo.put("votherinfo", votherinfo);
			jo.put("vmemo", vmemo);
			arrjson.add(jo);
		}
		return arrjson.toString();
	}
}