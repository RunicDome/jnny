package nc.impl.uap.pf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.core.service.TimeService;
import nc.bs.pub.wfengine.impl.ActionEnvironment;
import nc.bs.wfengine.engine.ext.PfParticipantHandlerContext;
import nc.jdbc.framework.processor.ArrayProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.bd.psn.util.GetBusiDateUtil;
import nc.vo.general.rule.IGeneralRule;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.sm.UserVO;
/**
 * 根据制单人找到部门负责人
* @ClassName: GeneralRuleImpl 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author zzz 
* @date 2015-5-28 下午1:08:36 
*
 */
public class GeneralRuleImpl implements IGeneralRule {

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = 1L;

	@Override
	public ArrayList<String> getuser(PfParticipantHandlerContext context)
			throws BusinessException {
		PfParameterVO paraVo = ActionEnvironment.getInstance().getParaVo(context.getWftask().getBillID());
		String makeman =  paraVo.m_makeBillOperator;//制单人
		if (null == makeman) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("pfworkflow", "generalrule-0006")/* 没有获取导制单人*/);	
		}
		BaseDAO dao = new BaseDAO();
		//根据制单人获得用户表信息
		Collection<?> c =((Collection<?>) dao.retrieveByClause(UserVO.class, "CUSERID = '" +makeman+"'"));
		if(c== null || c.size()==0){
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "generalrule-0007")/* 没有找到制单人在用户表中的信息*/);				
		}
		//人员信息pk
		String psn = c.toArray(new UserVO[c.size()])[0].getPk_psndoc();
		//用户表中人员信息pk为空，不用找往下进行
		if("".equals(psn) || null == psn){
			throw	new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "generalrule-0008")/* 当前操作人员在人员信息表中不存在*/);	
		}
		
		//根据人员信息获得部门负责人信息
		/**
		 * 1.制单人在用户表中存在
		 * 2.用户表关联人员表找到他的负责人
		 * 3.负责人在用户表中存在
		 */ 
		UFLiteralDate nowDate=new GetBusiDateUtil().getNowDate();
        String sqlstr = "select s.CUSERID ,c.PK_FATHERORG from BD_PSNDOC a, BD_PSNJOB b,ORG_DEPT c, SM_USER s "+
        " where a.PK_PSNDOC = '"+psn+"' and a.PK_PSNDOC = b.PK_PSNDOC "+
        " and b.PK_DEPT = c.PK_DEPT and c.PRINCIPAL is not null and c.PRINCIPAL != '~' " +
        " and c.PRINCIPAL = s.PK_PSNDOC and b.ISMAINJOB='Y' and b.indutydate <='" +nowDate
			+ "' and (b.enddutydate >='" + nowDate + "' or b.enddutydate ='~'"
			+ " or b.enddutydate is null)" ;

        ArrayList<String> result = new  ArrayList<String>();//返回结果
        //obj[0]返回当前操作员的负责人，如果操作员就是当前部门负责人obj[1]返回操作员上级部门pk
        Object[] obj = (Object[]) dao.executeQuery(sqlstr,  new ArrayProcessor());
		if( null == obj || null == obj[0] || "".equals(obj[0])){
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "generalrule-0014")/*制单人所在部门的负责人不存在或者只是人员不是用户*/);			
		}
		if(makeman.equals(obj[0]) && null == obj[1]){//部门负责人是自己并且上级部门为空
			//kejl 进行  部门负责人是自己并且上级部门为空 特殊处理
			  result.add(makeman);//得到负责人
			  return result;
		//	throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "generalrule-0015")/*制单人是本部门负责人且没有上级部门*/);			
		}
		
		if(makeman.equals(obj[0])){//制单人是负责人
			//sql保证上级部门的负责人必须是用户，并且返回这个
			 result.add(makeman);
//			String sql = " select s.CUSERID from ORG_DEPT c , SM_USER s where c.PK_DEPT = '"+obj[1].toString()+"'  and  c.PRINCIPAL = s.PK_PSNDOC  and c.PRINCIPAL !='~'"; 
//			//返回上级部门负责人
//	        String sj = (String) dao.executeQuery(sql, new ColumnProcessor());
//	        if("".equals(sj) || null == sj){
//	        	throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "generalrule-0016")/*制单人是本部门负责人且有上级部门，但是上级部门不存在负责人*/);	
//	        	
//	        }
//	        result.add(sj);
			 
			 //得到负责人
		}else{//存在当前部门负责人且不是本人			
			result.add((String)obj[0]);//得到负责人			
		}
		return result;
	}
	
	/**
	 * 获得当前时间
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private UFDateTime getCurrentTime() {
		return new UFDateTime(new Date(TimeService.getInstance().getTime()));
	}

}
