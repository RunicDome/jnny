package nc.vo.pcm.contract;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class ContrHeadVO extends SuperVO
{
  public static final String PK_CONTR = "pk_contr";
  public static final String PK_GROUP = "pk_group";
  public static final String PK_ORG = "pk_org";
  public static final String PK_ORG_V = "pk_org_v";
  public static final String BILL_TYPE = "bill_type";
  public static final String TRANSI_TYPE = "transi_type";
  public static final String PK_TRANSITYPE = "pk_transitype";
  public static final String BUSI_TYPE = "busi_type";
  public static final String BILL_CODE = "bill_code";
  public static final String BILL_NAME = "bill_name";
  public static final String PK_CONTRACTTYPE = "pk_contracttype";
  public static final String BILL_STATUS = "bill_status";
  public static final String PK_PROJECT = "pk_project";
  public static final String PK_WBS = "pk_wbs";
  public static final String PLAN_VALIDATE_TIME = "plan_validate_time";
  public static final String PLAN_TEMINATE_TIME = "plan_teminate_time";
  public static final String ACTU_VALIDATE_TIME = "actu_validate_time";
  public static final String ACTU_TEMINATE_TIME = "actu_teminate_time";
  public static final String PK_CURRTYPE = "pk_currtype";
  public static final String NEXCHANGERATE = "nexchangerate";
  public static final String CREATOR = "creator";
  public static final String CREATIONTIME = "creationtime";
  public static final String MODIFIER = "modifier";
  public static final String MODIFIEDTIME = "modifiedtime";
  public static final String AUDITOR = "auditor";
  public static final String AUDITTIME = "audittime";
  public static final String CHECK_OPINION = "check_opinion";
  public static final String BILLMAKER = "billmaker";
  public static final String BILLMAKETIME = "billmaketime";
  public static final String PROMISETIME = "promisetime";
  public static final String PK_PROMISEPSN = "pk_promisepsn";
  public static final String PK_PROMISE_DEPT = "pk_promise_dept";
  public static final String PK_PRO_DEPT_V = "pk_pro_dept_v";
  public static final String PROMISEPLACE = "promiseplace";
  public static final String PK_SUPPLIER = "pk_supplier";
  public static final String PAY_MODE = "pay_mode";
  public static final String NCURRENT_MNY = "ncurrent_mny";
  public static final String ORIG_CURRENT_MNY = "orig_current_mny";
  public static final String NPREPAY_MNY = "nprepay_mny";
  public static final String ORIG_PREPAY_MNY = "orig_prepay_mny";
  public static final String NTOT_EXPE_MNY = "ntot_expe_mny";
  public static final String ORIG_TOT_EXPE_MNY = "orig_tot_expe_mny";
  public static final String ORIG_SPARE_MNY = "orig_spare_mny";
  public static final String NSPARE_MNY = "nspare_mny";
  public static final String NTOT_SCHED_MNY = "ntot_sched_mny";
  public static final String ORIG_TOT_SCHED_MNY = "orig_tot_sched_mny";
  public static final String VERIFY_MNY = "verify_mny";
  public static final String ORIG_VERIFY_MNY = "orig_verify_mny";
  public static final String PK_FINANCEORG = "pk_financeorg";
  public static final String PK_FINANCEORG_V = "pk_financeorg_v";
  public static final String PREPAYFLAG = "prepayflag";
  public static final String BALAFLAG = "balaflag";
  public static final String PK_CURRTYPE_ORG = "pk_currtype_org";
  public static final String CURR_MNY_GROUP = "curr_mny_group";
  public static final String CURR_MNY_GLOBAL = "curr_mny_global";
  public static final String PREPAY_MNY_GROUP = "prepay_mny_group";
  public static final String PREPAY_MNY_GLOBAL = "prepay_mny_global";
  public static final String EXPE_MNY_GROUP = "expe_mny_group";
  public static final String EXPE_MNY_GLOBAL = "expe_mny_global";
  public static final String SPARE_MNY_GROUP = "spare_mny_group";
  public static final String SPARE_MNY_GLOBAL = "spare_mny_global";
  public static final String SCHED_MNY_GROUP = "sched_mny_group";
  public static final String SCHED_MNY_GLOBAL = "sched_mny_global";
  public static final String VERIFY_GROUP = "verify_group";
  public static final String VERIFY_GLOBAL = "verify_global";
  public static final String CONTR_VERSION = "contr_version";
  public static final String LAST_V_FLAG = "last_v_flag";
  public static final String PK_BILLOID = "pk_billoid";
  public static final String ORIG_TOT_SLD_MNY = "orig_tot_sld_mny";
  public static final String NTOT_SLD_MNY = "ntot_sld_mny";
  public static final String TOT_SLD_GROUP = "tot_sld_group";
  public static final String TOT_SLD_GLOBAL = "tot_sld_global";
  public static final String PAY_MNY = "pay_mny";
  public static final String PAY_ORIG = "pay_orig";
  public static final String PAY_GROUP = "pay_group";
  public static final String PAY_GLOBAL = "pay_global";
  public static final String SCHE_FLAG = "sche_flag";
  public static final String ALTER_FLAG = "alter_flag";
  public static final String MEMO = "memo";
  public static final String BUDGET_INFO = "budget_info";
  public static final String HDEF1 = "hdef1";
  public static final String HDEF2 = "hdef2";
  public static final String HDEF3 = "hdef3";
  public static final String HDEF4 = "hdef4";
  public static final String HDEF5 = "hdef5";
  public static final String HDEF6 = "hdef6";
  public static final String HDEF7 = "hdef7";
  public static final String HDEF8 = "hdef8";
  public static final String HDEF9 = "hdef9";
  public static final String HDEF10 = "hdef10";
  public static final String HDEF11 = "hdef11";
  public static final String HDEF12 = "hdef12";
  public static final String HDEF13 = "hdef13";
  public static final String HDEF14 = "hdef14";
  public static final String HDEF15 = "hdef15";
  public static final String HDEF16 = "hdef16";
  public static final String HDEF17 = "hdef17";
  public static final String HDEF18 = "hdef18";
  public static final String HDEF19 = "hdef19";
  public static final String HDEF20 = "hdef20";
  public static final String HDEF21 = "hdef21";
  public static final String HDEF22 = "hdef22";
  public static final String HDEF23 = "hdef23";
  public static final String HDEF24 = "hdef24";
  public static final String HDEF25 = "hdef25";
  public static final String HDEF26 = "hdef26";
  public static final String HDEF27 = "hdef27";
  public static final String HDEF28 = "hdef28";
  public static final String HDEF29 = "hdef29";
  public static final String HDEF30 = "hdef30";
  public static final String HDEF31 = "hdef31";
  public static final String HDEF32 = "hdef32";
  public static final String HDEF33 = "hdef33";
  public static final String HDEF34 = "hdef34";
  public static final String HDEF35 = "hdef35";
  public static final String HDEF36 = "hdef36";
  public static final String HDEF37 = "hdef37";
  public static final String HDEF38 = "hdef38";
  public static final String HDEF39 = "hdef39";
  public static final String HDEF40 = "hdef40";
  public static final String HDEF41 = "hdef41";
  public static final String HDEF42 = "hdef42";
  public static final String HDEF43 = "hdef43";
  public static final String HDEF44 = "hdef44";
  public static final String HDEF45 = "hdef45";
  public static final String HDEF46 = "hdef46";
  public static final String HDEF47 = "hdef47";
  public static final String HDEF48 = "hdef48";
  public static final String HDEF49 = "hdef49";
  public static final String HDEF50 = "hdef50";
  public static final String HDEF51 = "hdef51";
  public static final String HDEF52 = "hdef52";
  public static final String HDEF53 = "hdef53";
  public static final String HDEF54 = "hdef54";
  public static final String HDEF55 = "hdef55";
  public static final String HDEF56 = "hdef56";
  public static final String HDEF57 = "hdef57";
  public static final String HDEF58 = "hdef58";
  public static final String HDEF59 = "hdef59";
  public static final String HDEF60 = "hdef60";
  public static final String HDEF61 = "hdef61";
  public static final String HDEF62 = "hdef62";
  public static final String HDEF63 = "hdef63";
  public static final String HDEF64 = "hdef64";
  public static final String HDEF65 = "hdef65";
  public static final String HDEF66 = "hdef66";
  public static final String HDEF67 = "hdef67";
  public static final String HDEF68 = "hdef68";
  public static final String HDEF69 = "hdef69";
  public static final String HDEF70 = "hdef70";
  public static final String HDEF71 = "hdef71";
  public static final String HDEF72 = "hdef72";
  public static final String HDEF73 = "hdef73";
  public static final String HDEF74 = "hdef74";
  public static final String HDEF75 = "hdef75";
  public static final String HDEF76 = "hdef76";
  public static final String HDEF77 = "hdef77";
  public static final String HDEF78 = "hdef78";
  public static final String HDEF79 = "hdef79";
  public static final String HDEF80 = "hdef80";
  public static final String HDEF81 = "hdef81";
  public static final String HDEF82 = "hdef82";
  public static final String HDEF83 = "hdef83";
  public static final String HDEF84 = "hdef84";
  public static final String HDEF85 = "hdef85";
  public static final String HDEF86 = "hdef86";
  public static final String HDEF87 = "hdef87";
  public static final String HDEF88 = "hdef88";
  public static final String HDEF89 = "hdef89";
  public static final String HDEF90 = "hdef90";
  public static final String PK_STATUS_BEFORE = "pk_status_before";
  public static final String PK_STATUS_AFTER = "pk_status_after";
  public static final String STATUS_DATE_BEFORE = "status_date_before";
  public static final String STATUS_DATE_AFTER = "status_date_after";
  public static final String FLEXIBLE_FLAG = "flexible_flag";
  public static final String SRC_PK_BILL = "src_pk_bill";
  public static final String SRC_BILL_TYPE = "src_bill_type";
  public static final String SRC_TRANSI_TYPE = "src_transi_type";
  public static final String SRC_HEAD_TS = "src_head_ts";
  public static final String PREPAY_PROP = "prepay_prop";
  public static final String QUAL_MNY_PROP = "qual_mny_prop";
  public static final String QUAL_TIME = "qual_time";
  public static final String SCHE_MNY_PROP = "sche_mny_prop";
  public static final String QUAL_MNY_DEADLINE = "qual_mny_deadline";
  public static final String BEGIN_FLAG = "begin_flag";
  public static final String SERVICEBALRULE = "servicebalrule";

  public String getPk_contr()
  {
    return (String)getAttributeValue("pk_contr");
  }

  public void setPk_contr(String newPk_contr)
  {
    setAttributeValue("pk_contr", newPk_contr);
  }

  public String getPk_group()
  {
    return (String)getAttributeValue("pk_group");
  }

  public void setPk_group(String newPk_group)
  {
    setAttributeValue("pk_group", newPk_group);
  }

  public String getPk_org()
  {
    return (String)getAttributeValue("pk_org");
  }

  public void setPk_org(String newPk_org)
  {
    setAttributeValue("pk_org", newPk_org);
  }

  public String getPk_org_v()
  {
    return (String)getAttributeValue("pk_org_v");
  }

  public void setPk_org_v(String newPk_org_v)
  {
    setAttributeValue("pk_org_v", newPk_org_v);
  }

  public String getBill_type()
  {
    return (String)getAttributeValue("bill_type");
  }

  public void setBill_type(String newBill_type)
  {
    setAttributeValue("bill_type", newBill_type);
  }

  public String getTransi_type()
  {
    return (String)getAttributeValue("transi_type");
  }

  public void setTransi_type(String newTransi_type)
  {
    setAttributeValue("transi_type", newTransi_type);
  }

  public String getPk_transitype()
  {
    return (String)getAttributeValue("pk_transitype");
  }

  public void setPk_transitype(String newPk_transitype)
  {
    setAttributeValue("pk_transitype", newPk_transitype);
  }

  public String getBusi_type()
  {
    return (String)getAttributeValue("busi_type");
  }

  public void setBusi_type(String newBusi_type)
  {
    setAttributeValue("busi_type", newBusi_type);
  }

  public String getBill_code()
  {
    return (String)getAttributeValue("bill_code");
  }

  public void setBill_code(String newBill_code)
  {
    setAttributeValue("bill_code", newBill_code);
  }

  public String getBill_name()
  {
    return (String)getAttributeValue("bill_name");
  }

  public void setBill_name(String newBill_name)
  {
    setAttributeValue("bill_name", newBill_name);
  }

  public String getPk_contracttype()
  {
    return (String)getAttributeValue("pk_contracttype");
  }

  public void setPk_contracttype(String newPk_contracttype)
  {
    setAttributeValue("pk_contracttype", newPk_contracttype);
  }

  public Integer getBill_status()
  {
    return (Integer)getAttributeValue("bill_status");
  }

  public void setBill_status(Integer newBill_status)
  {
    setAttributeValue("bill_status", newBill_status);
  }

  public String getPk_project()
  {
    return (String)getAttributeValue("pk_project");
  }

  public void setPk_project(String newPk_project)
  {
    setAttributeValue("pk_project", newPk_project);
  }

  public String getPk_wbs()
  {
    return (String)getAttributeValue("pk_wbs");
  }

  public void setPk_wbs(String newPk_wbs)
  {
    setAttributeValue("pk_wbs", newPk_wbs);
  }

  public UFDate getPlan_validate_time()
  {
    return (UFDate)getAttributeValue("plan_validate_time");
  }

  public void setPlan_validate_time(UFDate newPlan_validate_time)
  {
    setAttributeValue("plan_validate_time", newPlan_validate_time);
  }

  public UFDate getPlan_teminate_time()
  {
    return (UFDate)getAttributeValue("plan_teminate_time");
  }

  public void setPlan_teminate_time(UFDate newPlan_teminate_time)
  {
    setAttributeValue("plan_teminate_time", newPlan_teminate_time);
  }

  public UFDate getActu_validate_time()
  {
    return (UFDate)getAttributeValue("actu_validate_time");
  }

  public void setActu_validate_time(UFDate newActu_validate_time)
  {
    setAttributeValue("actu_validate_time", newActu_validate_time);
  }

  public UFDate getActu_teminate_time()
  {
    return (UFDate)getAttributeValue("actu_teminate_time");
  }

  public void setActu_teminate_time(UFDate newActu_teminate_time)
  {
    setAttributeValue("actu_teminate_time", newActu_teminate_time);
  }

  public String getPk_currtype()
  {
    return (String)getAttributeValue("pk_currtype");
  }

  public void setPk_currtype(String newPk_currtype)
  {
    setAttributeValue("pk_currtype", newPk_currtype);
  }

  public UFDouble getNexchangerate()
  {
    return (UFDouble)getAttributeValue("nexchangerate");
  }

  public void setNexchangerate(UFDouble newNexchangerate)
  {
    setAttributeValue("nexchangerate", newNexchangerate);
  }

  public String getCreator()
  {
    return (String)getAttributeValue("creator");
  }

  public void setCreator(String newCreator)
  {
    setAttributeValue("creator", newCreator);
  }

  public UFDateTime getCreationtime()
  {
    return (UFDateTime)getAttributeValue("creationtime");
  }

  public void setCreationtime(UFDateTime newCreationtime)
  {
    setAttributeValue("creationtime", newCreationtime);
  }

  public String getModifier()
  {
    return (String)getAttributeValue("modifier");
  }

  public void setModifier(String newModifier)
  {
    setAttributeValue("modifier", newModifier);
  }

  public UFDateTime getModifiedtime()
  {
    return (UFDateTime)getAttributeValue("modifiedtime");
  }

  public void setModifiedtime(UFDateTime newModifiedtime)
  {
    setAttributeValue("modifiedtime", newModifiedtime);
  }

  public String getAuditor()
  {
    return (String)getAttributeValue("auditor");
  }

  public void setAuditor(String newAuditor)
  {
    setAttributeValue("auditor", newAuditor);
  }

  public UFDate getAudittime()
  {
    return (UFDate)getAttributeValue("audittime");
  }

  public void setAudittime(UFDate newAudittime)
  {
    setAttributeValue("audittime", newAudittime);
  }

  public String getCheck_opinion()
  {
    return (String)getAttributeValue("check_opinion");
  }

  public void setCheck_opinion(String newCheck_opinion)
  {
    setAttributeValue("check_opinion", newCheck_opinion);
  }

  public String getBillmaker()
  {
    return (String)getAttributeValue("billmaker");
  }

  public void setBillmaker(String newBillmaker)
  {
    setAttributeValue("billmaker", newBillmaker);
  }

  public UFDate getBillmaketime()
  {
    return (UFDate)getAttributeValue("billmaketime");
  }

  public void setBillmaketime(UFDate newBillmaketime)
  {
    setAttributeValue("billmaketime", newBillmaketime);
  }

  public UFDate getPromisetime()
  {
    return (UFDate)getAttributeValue("promisetime");
  }

  public void setPromisetime(UFDate newPromisetime)
  {
    setAttributeValue("promisetime", newPromisetime);
  }

  public String getPk_promisepsn()
  {
    return (String)getAttributeValue("pk_promisepsn");
  }

  public void setPk_promisepsn(String newPk_promisepsn)
  {
    setAttributeValue("pk_promisepsn", newPk_promisepsn);
  }

  public String getPk_promise_dept()
  {
    return (String)getAttributeValue("pk_promise_dept");
  }

  public void setPk_promise_dept(String newPk_promise_dept)
  {
    setAttributeValue("pk_promise_dept", newPk_promise_dept);
  }

  public String getPk_pro_dept_v()
  {
    return (String)getAttributeValue("pk_pro_dept_v");
  }

  public void setPk_pro_dept_v(String newPk_pro_dept_v)
  {
    setAttributeValue("pk_pro_dept_v", newPk_pro_dept_v);
  }

  public String getPromiseplace()
  {
    return (String)getAttributeValue("promiseplace");
  }

  public void setPromiseplace(String newPromiseplace)
  {
    setAttributeValue("promiseplace", newPromiseplace);
  }

  public String getPk_supplier()
  {
    return (String)getAttributeValue("pk_supplier");
  }

  public void setPk_supplier(String newPk_supplier)
  {
    setAttributeValue("pk_supplier", newPk_supplier);
  }

  public Integer getPay_mode()
  {
    return (Integer)getAttributeValue("pay_mode");
  }

  public void setPay_mode(Integer newPay_mode)
  {
    setAttributeValue("pay_mode", newPay_mode);
  }

  public UFDouble getNcurrent_mny()
  {
    return (UFDouble)getAttributeValue("ncurrent_mny");
  }

  public void setNcurrent_mny(UFDouble newNcurrent_mny)
  {
    setAttributeValue("ncurrent_mny", newNcurrent_mny);
  }

  public UFDouble getOrig_current_mny()
  {
    return (UFDouble)getAttributeValue("orig_current_mny");
  }

  public void setOrig_current_mny(UFDouble newOrig_current_mny)
  {
    setAttributeValue("orig_current_mny", newOrig_current_mny);
  }

  public UFDouble getNprepay_mny()
  {
    return (UFDouble)getAttributeValue("nprepay_mny");
  }

  public void setNprepay_mny(UFDouble newNprepay_mny)
  {
    setAttributeValue("nprepay_mny", newNprepay_mny);
  }

  public UFDouble getOrig_prepay_mny()
  {
    return (UFDouble)getAttributeValue("orig_prepay_mny");
  }

  public void setOrig_prepay_mny(UFDouble newOrig_prepay_mny)
  {
    setAttributeValue("orig_prepay_mny", newOrig_prepay_mny);
  }

  public UFDouble getNtot_expe_mny()
  {
    return (UFDouble)getAttributeValue("ntot_expe_mny");
  }

  public void setNtot_expe_mny(UFDouble newNtot_expe_mny)
  {
    setAttributeValue("ntot_expe_mny", newNtot_expe_mny);
  }

  public UFDouble getOrig_tot_expe_mny()
  {
    return (UFDouble)getAttributeValue("orig_tot_expe_mny");
  }

  public void setOrig_tot_expe_mny(UFDouble newOrig_tot_expe_mny)
  {
    setAttributeValue("orig_tot_expe_mny", newOrig_tot_expe_mny);
  }

  public UFDouble getOrig_spare_mny()
  {
    return (UFDouble)getAttributeValue("orig_spare_mny");
  }

  public void setOrig_spare_mny(UFDouble newOrig_spare_mny)
  {
    setAttributeValue("orig_spare_mny", newOrig_spare_mny);
  }

  public UFDouble getNspare_mny()
  {
    return (UFDouble)getAttributeValue("nspare_mny");
  }

  public void setNspare_mny(UFDouble newNspare_mny)
  {
    setAttributeValue("nspare_mny", newNspare_mny);
  }

  public UFDouble getNtot_sched_mny()
  {
    return (UFDouble)getAttributeValue("ntot_sched_mny");
  }

  public void setNtot_sched_mny(UFDouble newNtot_sched_mny)
  {
    setAttributeValue("ntot_sched_mny", newNtot_sched_mny);
  }

  public UFDouble getOrig_tot_sched_mny()
  {
    return (UFDouble)getAttributeValue("orig_tot_sched_mny");
  }

  public void setOrig_tot_sched_mny(UFDouble newOrig_tot_sched_mny)
  {
    setAttributeValue("orig_tot_sched_mny", newOrig_tot_sched_mny);
  }

  public UFDouble getVerify_mny()
  {
    return (UFDouble)getAttributeValue("verify_mny");
  }

  public void setVerify_mny(UFDouble newVerify_mny)
  {
    setAttributeValue("verify_mny", newVerify_mny);
  }

  public UFDouble getOrig_verify_mny()
  {
    return (UFDouble)getAttributeValue("orig_verify_mny");
  }

  public void setOrig_verify_mny(UFDouble newOrig_verify_mny)
  {
    setAttributeValue("orig_verify_mny", newOrig_verify_mny);
  }

  public String getPk_financeorg()
  {
    return (String)getAttributeValue("pk_financeorg");
  }

  public void setPk_financeorg(String newPk_financeorg)
  {
    setAttributeValue("pk_financeorg", newPk_financeorg);
  }

  public String getPk_financeorg_v()
  {
    return (String)getAttributeValue("pk_financeorg_v");
  }

  public void setPk_financeorg_v(String newPk_financeorg_v)
  {
    setAttributeValue("pk_financeorg_v", newPk_financeorg_v);
  }

  public UFBoolean getPrepayflag()
  {
    return (UFBoolean)getAttributeValue("prepayflag");
  }

  public void setPrepayflag(UFBoolean newPrepayflag)
  {
    setAttributeValue("prepayflag", newPrepayflag);
  }

  public Integer getBalaflag()
  {
    return (Integer)getAttributeValue("balaflag");
  }

  public void setBalaflag(Integer newBalaflag)
  {
    setAttributeValue("balaflag", newBalaflag);
  }

  public String getPk_currtype_org()
  {
    return (String)getAttributeValue("pk_currtype_org");
  }

  public void setPk_currtype_org(String newPk_currtype_org)
  {
    setAttributeValue("pk_currtype_org", newPk_currtype_org);
  }

  public UFDouble getCurr_mny_group()
  {
    return (UFDouble)getAttributeValue("curr_mny_group");
  }

  public void setCurr_mny_group(UFDouble newCurr_mny_group)
  {
    setAttributeValue("curr_mny_group", newCurr_mny_group);
  }

  public UFDouble getCurr_mny_global()
  {
    return (UFDouble)getAttributeValue("curr_mny_global");
  }

  public void setCurr_mny_global(UFDouble newCurr_mny_global)
  {
    setAttributeValue("curr_mny_global", newCurr_mny_global);
  }

  public UFDouble getPrepay_mny_group()
  {
    return (UFDouble)getAttributeValue("prepay_mny_group");
  }

  public void setPrepay_mny_group(UFDouble newPrepay_mny_group)
  {
    setAttributeValue("prepay_mny_group", newPrepay_mny_group);
  }

  public UFDouble getPrepay_mny_global()
  {
    return (UFDouble)getAttributeValue("prepay_mny_global");
  }

  public void setPrepay_mny_global(UFDouble newPrepay_mny_global)
  {
    setAttributeValue("prepay_mny_global", newPrepay_mny_global);
  }

  public UFDouble getExpe_mny_group()
  {
    return (UFDouble)getAttributeValue("expe_mny_group");
  }

  public void setExpe_mny_group(UFDouble newExpe_mny_group)
  {
    setAttributeValue("expe_mny_group", newExpe_mny_group);
  }

  public UFDouble getExpe_mny_global()
  {
    return (UFDouble)getAttributeValue("expe_mny_global");
  }

  public void setExpe_mny_global(UFDouble newExpe_mny_global)
  {
    setAttributeValue("expe_mny_global", newExpe_mny_global);
  }

  public UFDouble getSpare_mny_group()
  {
    return (UFDouble)getAttributeValue("spare_mny_group");
  }

  public void setSpare_mny_group(UFDouble newSpare_mny_group)
  {
    setAttributeValue("spare_mny_group", newSpare_mny_group);
  }

  public UFDouble getSpare_mny_global()
  {
    return (UFDouble)getAttributeValue("spare_mny_global");
  }

  public void setSpare_mny_global(UFDouble newSpare_mny_global)
  {
    setAttributeValue("spare_mny_global", newSpare_mny_global);
  }

  public UFDouble getSched_mny_group()
  {
    return (UFDouble)getAttributeValue("sched_mny_group");
  }

  public void setSched_mny_group(UFDouble newSched_mny_group)
  {
    setAttributeValue("sched_mny_group", newSched_mny_group);
  }

  public UFDouble getSched_mny_global()
  {
    return (UFDouble)getAttributeValue("sched_mny_global");
  }

  public void setSched_mny_global(UFDouble newSched_mny_global)
  {
    setAttributeValue("sched_mny_global", newSched_mny_global);
  }

  public UFDouble getVerify_group()
  {
    return (UFDouble)getAttributeValue("verify_group");
  }

  public void setVerify_group(UFDouble newVerify_group)
  {
    setAttributeValue("verify_group", newVerify_group);
  }

  public UFDouble getVerify_global()
  {
    return (UFDouble)getAttributeValue("verify_global");
  }

  public void setVerify_global(UFDouble newVerify_global)
  {
    setAttributeValue("verify_global", newVerify_global);
  }

  public Integer getContr_version()
  {
    return (Integer)getAttributeValue("contr_version");
  }

  public void setContr_version(Integer newContr_version)
  {
    setAttributeValue("contr_version", newContr_version);
  }

  public UFBoolean getLast_v_flag()
  {
    return (UFBoolean)getAttributeValue("last_v_flag");
  }

  public void setLast_v_flag(UFBoolean newLast_v_flag)
  {
    setAttributeValue("last_v_flag", newLast_v_flag);
  }

  public String getPk_billoid()
  {
    return (String)getAttributeValue("pk_billoid");
  }

  public void setPk_billoid(String newPk_billoid)
  {
    setAttributeValue("pk_billoid", newPk_billoid);
  }

  public UFDouble getOrig_tot_sld_mny()
  {
    return (UFDouble)getAttributeValue("orig_tot_sld_mny");
  }

  public void setOrig_tot_sld_mny(UFDouble newOrig_tot_sld_mny)
  {
    setAttributeValue("orig_tot_sld_mny", newOrig_tot_sld_mny);
  }

  public UFDouble getNtot_sld_mny()
  {
    return (UFDouble)getAttributeValue("ntot_sld_mny");
  }

  public void setNtot_sld_mny(UFDouble newNtot_sld_mny)
  {
    setAttributeValue("ntot_sld_mny", newNtot_sld_mny);
  }

  public UFDouble getTot_sld_group()
  {
    return (UFDouble)getAttributeValue("tot_sld_group");
  }

  public void setTot_sld_group(UFDouble newTot_sld_group)
  {
    setAttributeValue("tot_sld_group", newTot_sld_group);
  }

  public UFDouble getTot_sld_global()
  {
    return (UFDouble)getAttributeValue("tot_sld_global");
  }

  public void setTot_sld_global(UFDouble newTot_sld_global)
  {
    setAttributeValue("tot_sld_global", newTot_sld_global);
  }

  public UFDouble getPay_mny()
  {
    return (UFDouble)getAttributeValue("pay_mny");
  }

  public void setPay_mny(UFDouble newPay_mny)
  {
    setAttributeValue("pay_mny", newPay_mny);
  }

  public UFDouble getPay_orig()
  {
    return (UFDouble)getAttributeValue("pay_orig");
  }

  public void setPay_orig(UFDouble newPay_orig)
  {
    setAttributeValue("pay_orig", newPay_orig);
  }

  public UFDouble getPay_group()
  {
    return (UFDouble)getAttributeValue("pay_group");
  }

  public void setPay_group(UFDouble newPay_group)
  {
    setAttributeValue("pay_group", newPay_group);
  }

  public UFDouble getPay_global()
  {
    return (UFDouble)getAttributeValue("pay_global");
  }

  public void setPay_global(UFDouble newPay_global)
  {
    setAttributeValue("pay_global", newPay_global);
  }

  public UFBoolean getSche_flag()
  {
    return (UFBoolean)getAttributeValue("sche_flag");
  }

  public void setSche_flag(UFBoolean newSche_flag)
  {
    setAttributeValue("sche_flag", newSche_flag);
  }

  public UFBoolean getAlter_flag()
  {
    return (UFBoolean)getAttributeValue("alter_flag");
  }

  public void setAlter_flag(UFBoolean newAlter_flag)
  {
    setAttributeValue("alter_flag", newAlter_flag);
  }

  public String getMemo()
  {
    return (String)getAttributeValue("memo");
  }

  public void setMemo(String newMemo)
  {
    setAttributeValue("memo", newMemo);
  }

  public String getBudget_info()
  {
    return (String)getAttributeValue("budget_info");
  }

  public void setBudget_info(String newBudget_info)
  {
    setAttributeValue("budget_info", newBudget_info);
  }

  public String getHdef1()
  {
    return (String)getAttributeValue("hdef1");
  }

  public void setHdef1(String newHdef1)
  {
    setAttributeValue("hdef1", newHdef1);
  }

  public String getHdef2()
  {
    return (String)getAttributeValue("hdef2");
  }

  public void setHdef2(String newHdef2)
  {
    setAttributeValue("hdef2", newHdef2);
  }

  public String getHdef3()
  {
    return (String)getAttributeValue("hdef3");
  }

  public void setHdef3(String newHdef3)
  {
    setAttributeValue("hdef3", newHdef3);
  }

  public String getHdef4()
  {
    return (String)getAttributeValue("hdef4");
  }

  public void setHdef4(String newHdef4)
  {
    setAttributeValue("hdef4", newHdef4);
  }

  public String getHdef5()
  {
    return (String)getAttributeValue("hdef5");
  }

  public void setHdef5(String newHdef5)
  {
    setAttributeValue("hdef5", newHdef5);
  }

  public String getHdef6()
  {
    return (String)getAttributeValue("hdef6");
  }

  public void setHdef6(String newHdef6)
  {
    setAttributeValue("hdef6", newHdef6);
  }

  public String getHdef7()
  {
    return (String)getAttributeValue("hdef7");
  }

  public void setHdef7(String newHdef7)
  {
    setAttributeValue("hdef7", newHdef7);
  }

  public String getHdef8()
  {
    return (String)getAttributeValue("hdef8");
  }

  public void setHdef8(String newHdef8)
  {
    setAttributeValue("hdef8", newHdef8);
  }

  public String getHdef9()
  {
    return (String)getAttributeValue("hdef9");
  }

  public void setHdef9(String newHdef9)
  {
    setAttributeValue("hdef9", newHdef9);
  }

  public String getHdef10()
  {
    return (String)getAttributeValue("hdef10");
  }

  public void setHdef10(String newHdef10)
  {
    setAttributeValue("hdef10", newHdef10);
  }

  public String getHdef11()
  {
    return (String)getAttributeValue("hdef11");
  }

  public void setHdef11(String newHdef11)
  {
    setAttributeValue("hdef11", newHdef11);
  }

  public String getHdef12()
  {
    return (String)getAttributeValue("hdef12");
  }

  public void setHdef12(String newHdef12)
  {
    setAttributeValue("hdef12", newHdef12);
  }

  public String getHdef13()
  {
    return (String)getAttributeValue("hdef13");
  }

  public void setHdef13(String newHdef13)
  {
    setAttributeValue("hdef13", newHdef13);
  }

  public String getHdef14()
  {
    return (String)getAttributeValue("hdef14");
  }

  public void setHdef14(String newHdef14)
  {
    setAttributeValue("hdef14", newHdef14);
  }

  public String getHdef15()
  {
    return (String)getAttributeValue("hdef15");
  }

  public void setHdef15(String newHdef15)
  {
    setAttributeValue("hdef15", newHdef15);
  }

  public String getHdef16()
  {
    return (String)getAttributeValue("hdef16");
  }

  public void setHdef16(String newHdef16)
  {
    setAttributeValue("hdef16", newHdef16);
  }

  public String getHdef17()
  {
    return (String)getAttributeValue("hdef17");
  }

  public void setHdef17(String newHdef17)
  {
    setAttributeValue("hdef17", newHdef17);
  }

  public String getHdef18()
  {
    return (String)getAttributeValue("hdef18");
  }

  public void setHdef18(String newHdef18)
  {
    setAttributeValue("hdef18", newHdef18);
  }

  public String getHdef19()
  {
    return (String)getAttributeValue("hdef19");
  }

  public void setHdef19(String newHdef19)
  {
    setAttributeValue("hdef19", newHdef19);
  }

  public String getHdef20()
  {
    return (String)getAttributeValue("hdef20");
  }

  public void setHdef20(String newHdef20)
  {
    setAttributeValue("hdef20", newHdef20);
  }

  public String getHdef21()
  {
    return (String)getAttributeValue("hdef21");
  }

  public void setHdef21(String newHdef21)
  {
    setAttributeValue("hdef21", newHdef21);
  }

  public String getHdef22()
  {
    return (String)getAttributeValue("hdef22");
  }

  public void setHdef22(String newHdef22)
  {
    setAttributeValue("hdef22", newHdef22);
  }

  public String getHdef23()
  {
    return (String)getAttributeValue("hdef23");
  }

  public void setHdef23(String newHdef23)
  {
    setAttributeValue("hdef23", newHdef23);
  }

  public String getHdef24()
  {
    return (String)getAttributeValue("hdef24");
  }

  public void setHdef24(String newHdef24)
  {
    setAttributeValue("hdef24", newHdef24);
  }

  public String getHdef25()
  {
    return (String)getAttributeValue("hdef25");
  }

  public void setHdef25(String newHdef25)
  {
    setAttributeValue("hdef25", newHdef25);
  }

  public String getHdef26()
  {
    return (String)getAttributeValue("hdef26");
  }

  public void setHdef26(String newHdef26)
  {
    setAttributeValue("hdef26", newHdef26);
  }

  public String getHdef27()
  {
    return (String)getAttributeValue("hdef27");
  }

  public void setHdef27(String newHdef27)
  {
    setAttributeValue("hdef27", newHdef27);
  }

  public String getHdef28()
  {
    return (String)getAttributeValue("hdef28");
  }

  public void setHdef28(String newHdef28)
  {
    setAttributeValue("hdef28", newHdef28);
  }

  public String getHdef29()
  {
    return (String)getAttributeValue("hdef29");
  }

  public void setHdef29(String newHdef29)
  {
    setAttributeValue("hdef29", newHdef29);
  }

  public String getHdef30()
  {
    return (String)getAttributeValue("hdef30");
  }

  public void setHdef30(String newHdef30)
  {
    setAttributeValue("hdef30", newHdef30);
  }

  public String getHdef31()
  {
    return (String)getAttributeValue("hdef31");
  }

  public void setHdef31(String newHdef31)
  {
    setAttributeValue("hdef31", newHdef31);
  }

  public String getHdef32()
  {
    return (String)getAttributeValue("hdef32");
  }

  public void setHdef32(String newHdef32)
  {
    setAttributeValue("hdef32", newHdef32);
  }

  public String getHdef33()
  {
    return (String)getAttributeValue("hdef33");
  }

  public void setHdef33(String newHdef33)
  {
    setAttributeValue("hdef33", newHdef33);
  }

  public String getHdef34()
  {
    return (String)getAttributeValue("hdef34");
  }

  public void setHdef34(String newHdef34)
  {
    setAttributeValue("hdef34", newHdef34);
  }

  public String getHdef35()
  {
    return (String)getAttributeValue("hdef35");
  }

  public void setHdef35(String newHdef35)
  {
    setAttributeValue("hdef35", newHdef35);
  }

  public String getHdef36()
  {
    return (String)getAttributeValue("hdef36");
  }

  public void setHdef36(String newHdef36)
  {
    setAttributeValue("hdef36", newHdef36);
  }

  public String getHdef37()
  {
    return (String)getAttributeValue("hdef37");
  }

  public void setHdef37(String newHdef37)
  {
    setAttributeValue("hdef37", newHdef37);
  }

  public String getHdef38()
  {
    return (String)getAttributeValue("hdef38");
  }

  public void setHdef38(String newHdef38)
  {
    setAttributeValue("hdef38", newHdef38);
  }

  public String getHdef39()
  {
    return (String)getAttributeValue("hdef39");
  }

  public void setHdef39(String newHdef39)
  {
    setAttributeValue("hdef39", newHdef39);
  }

  public String getHdef40()
  {
    return (String)getAttributeValue("hdef40");
  }

  public void setHdef40(String newHdef40)
  {
    setAttributeValue("hdef40", newHdef40);
  }

  public String getHdef41() {
    return (String)getAttributeValue("hdef41");
  }

  public void setHdef41(String newHdef41) {
    setAttributeValue("hdef41", newHdef41);
  }

  public String getHdef42() {
    return (String)getAttributeValue("hdef42");
  }

  public void setHdef42(String newHdef42) {
    setAttributeValue("hdef42", newHdef42);
  }

  public String getHdef43() {
    return (String)getAttributeValue("hdef43");
  }

  public void setHdef43(String newHdef43) {
    setAttributeValue("hdef43", newHdef43);
  }

  public String getHdef44() {
    return (String)getAttributeValue("hdef44");
  }

  public void setHdef44(String newHdef43) {
    setAttributeValue("hdef44", newHdef43);
  }

  public String getHdef45() {
    return (String)getAttributeValue("hdef45");
  }

  public void setHdef45(String newHdef43) {
    setAttributeValue("hdef45", newHdef43);
  }

  public String gethdef46() {
    return (String)getAttributeValue("hdef46");
  }

  public void setHdef46(String newHdef46) {
    setAttributeValue("hdef46", newHdef46);
  }

  public String getHdef47() {
    return (String)getAttributeValue("hdef47");
  }

  public void setHdef47(String newHdef47) {
    setAttributeValue("hdef47", newHdef47);
  }

  public String getHdef48() {
    return (String)getAttributeValue("hdef48");
  }

  public void setHdef48(String newHdef48) {
    setAttributeValue("hdef48", newHdef48);
  }

  public String getHdef49() {
    return (String)getAttributeValue("hdef49");
  }

  public void setHdef49(String newHdef49) {
    setAttributeValue("hdef49", newHdef49);
  }

  public String getHdef50() {
    return (String)getAttributeValue("hdef50");
  }

  public void setHdef50(String newHdef50) {
    setAttributeValue("hdef50", newHdef50);
  }

  public String getHdef51() {
    return (String)getAttributeValue("hdef51");
  }

  public void setHdef51(String newHdef51) {
    setAttributeValue("hdef51", newHdef51);
  }

  public String getHdef52() {
    return (String)getAttributeValue("hdef52");
  }

  public void setHdef52(String newHdef52) {
    setAttributeValue("hdef52", newHdef52);
  }

  public String getHdef53() {
    return (String)getAttributeValue("hdef53");
  }

  public void setHdef53(String newHdef53) {
    setAttributeValue("hdef53", newHdef53);
  }

  public String getHdef54() {
    return (String)getAttributeValue("hdef54");
  }

  public void setHdef54(String newHdef54) {
    setAttributeValue("hdef54", newHdef54);
  }

  public String getHdef55() {
    return (String)getAttributeValue("hdef55");
  }

  public void setHdef55(String newHdef55) {
    setAttributeValue("hdef55", newHdef55);
  }

  public String getHdef56() {
    return (String)getAttributeValue("hdef56");
  }

  public void setHdef56(String newHdef56) {
    setAttributeValue("hdef56", newHdef56);
  }

  public String getHdef57() {
    return (String)getAttributeValue("hdef57");
  }

  public void setHdef57(String newHdef57) {
    setAttributeValue("hdef57", newHdef57);
  }

  public String getHdef58() {
    return (String)getAttributeValue("hdef58");
  }

  public void setHdef58(String newHdef58) {
    setAttributeValue("hdef58", newHdef58);
  }

  public String getHdef59() {
    return (String)getAttributeValue("hdef59");
  }

  public void setHdef59(String newHdef59) {
    setAttributeValue("hdef59", newHdef59);
  }

  public String getHdef60() {
    return (String)getAttributeValue("hdef60");
  }

  public void setHdef60(String newHdef60) {
    setAttributeValue("hdef60", newHdef60);
  }

  public String getHdef61() {
    return (String)getAttributeValue("hdef61");
  }

  public void setHdef61(String newHdef61) {
    setAttributeValue("hdef61", newHdef61);
  }

  public String getHdef62() {
    return (String)getAttributeValue("hdef62");
  }

  public void setHdef62(String newHdef62) {
    setAttributeValue("hdef62", newHdef62);
  }

  public String getHdef63() {
    return (String)getAttributeValue("hdef63");
  }

  public void setHdef63(String newHdef63) {
    setAttributeValue("hdef63", newHdef63);
  }

  public String getHdef64() {
    return (String)getAttributeValue("hdef64");
  }

  public void setHdef64(String newHdef64) {
    setAttributeValue("hdef64", newHdef64);
  }

  public String getHdef65() {
    return (String)getAttributeValue("hdef65");
  }

  public void setHdef65(String newHdef65) {
    setAttributeValue("hdef65", newHdef65);
  }

  public String getHdef66() {
    return (String)getAttributeValue("hdef66");
  }

  public void setHdef66(String newHdef66) {
    setAttributeValue("hdef66", newHdef66);
  }

  public String getHdef67() {
    return (String)getAttributeValue("hdef67");
  }

  public void setHdef67(String newHdef67) {
    setAttributeValue("hdef67", newHdef67);
  }

  public String getHdef68() {
    return (String)getAttributeValue("hdef68");
  }

  public void setHdef68(String newHdef68) {
    setAttributeValue("hdef68", newHdef68);
  }

  public String getHdef69() {
    return (String)getAttributeValue("hdef69");
  }

  public void setHdef69(String newHdef69) {
    setAttributeValue("hdef69", newHdef69);
  }

  public String getHdef70() {
    return (String)getAttributeValue("hdef70");
  }

  public void setHdef70(String newHdef70) {
    setAttributeValue("hdef70", newHdef70);
  }

  public String getHdef71() {
    return (String)getAttributeValue("hdef71");
  }

  public void setHdef71(String newHdef71) {
    setAttributeValue("hdef71", newHdef71);
  }

  public String getHdef72() {
    return (String)getAttributeValue("hdef72");
  }

  public void setHdef72(String newHdef72) {
    setAttributeValue("hdef72", newHdef72);
  }

  public String getHdef73() {
    return (String)getAttributeValue("hdef73");
  }

  public void setHdef73(String newHdef73) {
    setAttributeValue("hdef73", newHdef73);
  }

  public String getHdef74() {
    return (String)getAttributeValue("hdef74");
  }

  public void setHdef74(String newHdef74) {
    setAttributeValue("hdef74", newHdef74);
  }

  public String getHdef75() {
    return (String)getAttributeValue("hdef75");
  }

  public void setHdef75(String newHdef75) {
    setAttributeValue("hdef75", newHdef75);
  }

  public String getHdef76() {
    return (String)getAttributeValue("hdef76");
  }

  public void setHdef76(String newHdef76) {
    setAttributeValue("hdef76", newHdef76);
  }

  public String getHdef77() {
    return (String)getAttributeValue("hdef77");
  }

  public void setHdef77(String newHdef77) {
    setAttributeValue("hdef77", newHdef77);
  }

  public String getHdef78() {
    return (String)getAttributeValue("hdef78");
  }

  public void setHdef78(String newHdef78) {
    setAttributeValue("hdef78", newHdef78);
  }

  public String getHdef79() {
    return (String)getAttributeValue("hdef79");
  }

  public void setHdef79(String newHdef79) {
    setAttributeValue("hdef79", newHdef79);
  }

  public String getHdef80() {
    return (String)getAttributeValue("hdef80");
  }

  public void setHdef80(String newHdef80) {
    setAttributeValue("hdef80", newHdef80);
  }

  public String getHdef81() {
    return (String)getAttributeValue("hdef81");
  }

  public void setHdef81(String newHdef81) {
    setAttributeValue("hdef81", newHdef81);
  }

  public String getHdef82() {
    return (String)getAttributeValue("hdef82");
  }

  public void setHdef82(String newHdef82) {
    setAttributeValue("hdef82", newHdef82);
  }

  public String getHdef83() {
    return (String)getAttributeValue("hdef83");
  }

  public void setHdef83(String newHdef83) {
    setAttributeValue("hdef83", newHdef83);
  }

  public String getHdef84() {
    return (String)getAttributeValue("hdef84");
  }

  public void setHdef84(String newHdef84) {
    setAttributeValue("hdef84", newHdef84);
  }

  public String getHdef85() {
    return (String)getAttributeValue("hdef85");
  }

  public void setHdef85(String newHdef85) {
    setAttributeValue("hdef85", newHdef85);
  }

  public String getHdef86() {
    return (String)getAttributeValue("hdef86");
  }

  public void setHdef86(String newHdef86) {
    setAttributeValue("hdef86", newHdef86);
  }

  public String getHdef87() {
    return (String)getAttributeValue("hdef87");
  }

  public void setHdef87(String newHdef87) {
    setAttributeValue("hdef87", newHdef87);
  }

  public String getHdef88() {
    return (String)getAttributeValue("hdef88");
  }

  public void setHdef88(String newHdef88) {
    setAttributeValue("hdef88", newHdef88);
  }

  public String getHdef89() {
    return (String)getAttributeValue("hdef89");
  }

  public void setHdef89(String newHdef89) {
    setAttributeValue("hdef89", newHdef89);
  }

  public String getHdef90() {
    return (String)getAttributeValue("hdef90");
  }

  public void setHdef90(String newHdef90) {
    setAttributeValue("hdef90", newHdef90);
  }

  public String getPk_status_before()
  {
    return (String)getAttributeValue("pk_status_before");
  }

  public void setPk_status_before(String newPk_status_before)
  {
    setAttributeValue("pk_status_before", newPk_status_before);
  }

  public String getPk_status_after()
  {
    return (String)getAttributeValue("pk_status_after");
  }

  public void setPk_status_after(String newPk_status_after)
  {
    setAttributeValue("pk_status_after", newPk_status_after);
  }

  public UFDate getStatus_date_before()
  {
    return (UFDate)getAttributeValue("status_date_before");
  }

  public void setStatus_date_before(UFDate newStatus_date_before)
  {
    setAttributeValue("status_date_before", newStatus_date_before);
  }

  public UFDate getStatus_date_after()
  {
    return (UFDate)getAttributeValue("status_date_after");
  }

  public void setStatus_date_after(UFDate newStatus_date_after)
  {
    setAttributeValue("status_date_after", newStatus_date_after);
  }

  public UFBoolean getFlexible_flag()
  {
    return (UFBoolean)getAttributeValue("flexible_flag");
  }

  public void setFlexible_flag(UFBoolean newFlexible_flag)
  {
    setAttributeValue("flexible_flag", newFlexible_flag);
  }

  public String getSrc_pk_bill()
  {
    return (String)getAttributeValue("src_pk_bill");
  }

  public void setSrc_pk_bill(String newSrc_pk_bill)
  {
    setAttributeValue("src_pk_bill", newSrc_pk_bill);
  }

  public String getSrc_bill_type()
  {
    return (String)getAttributeValue("src_bill_type");
  }

  public void setSrc_bill_type(String newSrc_bill_type)
  {
    setAttributeValue("src_bill_type", newSrc_bill_type);
  }

  public String getSrc_transi_type()
  {
    return (String)getAttributeValue("src_transi_type");
  }

  public void setSrc_transi_type(String newSrc_transi_type)
  {
    setAttributeValue("src_transi_type", newSrc_transi_type);
  }

  public String getSrc_head_ts()
  {
    return (String)getAttributeValue("src_head_ts");
  }

  public void setSrc_head_ts(String newSrc_head_ts)
  {
    setAttributeValue("src_head_ts", newSrc_head_ts);
  }

  public UFDouble getPrepay_prop()
  {
    return (UFDouble)getAttributeValue("prepay_prop");
  }

  public void setPrepay_prop(UFDouble newPrepay_prop)
  {
    setAttributeValue("prepay_prop", newPrepay_prop);
  }

  public UFDouble getQual_mny_prop()
  {
    return (UFDouble)getAttributeValue("qual_mny_prop");
  }

  public void setQual_mny_prop(UFDouble newQual_mny_prop)
  {
    setAttributeValue("qual_mny_prop", newQual_mny_prop);
  }

  public Integer getQual_time()
  {
    return (Integer)getAttributeValue("qual_time");
  }

  public void setQual_time(Integer newQual_time)
  {
    setAttributeValue("qual_time", newQual_time);
  }

  public UFDouble getSche_mny_prop()
  {
    return (UFDouble)getAttributeValue("sche_mny_prop");
  }

  public void setSche_mny_prop(UFDouble newSche_mny_prop)
  {
    setAttributeValue("sche_mny_prop", newSche_mny_prop);
  }

  public UFDate getQual_mny_deadline()
  {
    return (UFDate)getAttributeValue("qual_mny_deadline");
  }

  public void setQual_mny_deadline(UFDate newQual_mny_deadline)
  {
    setAttributeValue("qual_mny_deadline", newQual_mny_deadline);
  }

  public UFBoolean getBegin_flag()
  {
    return (UFBoolean)getAttributeValue("begin_flag");
  }

  public void setBegin_flag(UFBoolean newBegin_flag)
  {
    setAttributeValue("begin_flag", newBegin_flag);
  }

  public Integer getServicebalrule()
  {
    return (Integer)getAttributeValue("servicebalrule");
  }

  public void setServicebalrule(Integer newServicebalrule)
  {
    setAttributeValue("servicebalrule", newServicebalrule);
  }

  public Integer getDr()
  {
    return (Integer)getAttributeValue("dr");
  }

  public void setDr(Integer newDr)
  {
    setAttributeValue("dr", newDr);
  }

  public UFDateTime getTs()
  {
    return (UFDateTime)getAttributeValue("ts");
  }

  public void setTs(UFDateTime newTs)
  {
    setAttributeValue("ts", newTs);
  }

  public String getParentPKFieldName()
  {
    return null;
  }

  public String getPKFieldName()
  {
    return "pk_contr";
  }

  public String getTableName()
  {
    return "pm_contr";
  }

  public IVOMeta getMetaData()
  {
    return VOMetaFactory.getInstance().getVOMeta("pcm.contractheadvo");
  }
}