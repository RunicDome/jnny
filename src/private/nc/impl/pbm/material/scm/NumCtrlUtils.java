package nc.impl.pbm.material.scm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.itf.pbm.materialnumctrl.pub.IMaterialnumctrlService;
import nc.itf.pbm.materialplan.pvt.IMaterialPlanCommonService;
import nc.pubitf.uapbd.IMaterialBaseClassPubService;
import nc.pubitf.uapbd.IMaterialPubService_C;
import nc.vo.bd.material.MaterialVO;
import nc.vo.pbm.materialnumctrl.ControlType;
import nc.vo.pbm.materialnumctrl.MaterialnumctrlVO;
import nc.vo.pbm.materialplan.MaterialPlanBillVO;
import nc.vo.pbm.materialplan.MaterialPlanBodyVO;
import nc.vo.pbm.materialplan.MaterialPlanHeadVO;
import nc.vo.pbm.materialstock.MaterialStockBodyFOrSCMVO;
import nc.vo.pbm.materialstock.MaterialStockVO;
import nc.vo.pbm.materialstocklog.MaterialNumCtlInfoMSG;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pm.constant.BillTypeConst;
import nc.vo.pm.util.ArrayUtil;
import nc.vo.pm.util.ListUtil;
import nc.vo.pm.util.StringUtil;
import nc.vo.pm.util.UFDoubleUtils;
import nc.vo.pm.util.app.BizContext;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.scale.ScaleUtils;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * 本类主要实现的功能：数量控制工具类
 * 
 * @version NC6.3_GH
 * @author yujian1
 * @time 2014-1-17 下午06:39:14
 */
public class NumCtrlUtils {
	public static MaterialStockVO[] numCtrl(MaterialStockBodyFOrSCMVO[] billVOs, 
			MaterialNumCtlInfoMSG returnMsg,MaterialStockVO[] finalMaterialStockVO) 
			throws BusinessException {
		// 出库单需要校验表体物料是否在项目下做过物资需求单
		if(StringUtils.equals(BillTypeConst.STOCKOUTBILL,
	            billVOs[0].getSrc_bill_type())){
			checkBodyMaterials(billVOs);
		}
		
		// 出库申请单需要进行数量控制
	    if (StringUtils.equals(BillTypeConst.STOCKOUTAPPLYBILL,
	        billVOs[0].getOri_bill_type())
	        ||StringUtils.equals(BillTypeConst.STOCKOUTAPPLYBILL,
	    	        billVOs[0].getSrc_bill_type())
	        || ( StringUtils.equals(BillTypeConst.STOCKOUTBILL,
	            billVOs[0].getSrc_bill_type()) && StringUtils.isEmpty(billVOs[0].getOri_pk_bill_b())  ) ) {
		    
//		    // 来源于出库申请单的出库单需要进行数量控制
//		    if (StringUtils.equals(BillTypeConst.STOCKOUTBILL,
//		        billVOs[0].getSrc_bill_type())
//		        && StringUtils.isNotEmpty(billVOs[0].getOri_pk_bill_b())
//		        && StringUtils.equals(billVOs[0].getOri_bill_type(),
//		            BillTypeConst.STOCKOUTAPPLYBILL)) {
		    	 // 查询数量控制方案
		        MaterialnumctrlVO[] ctrlVOs = queryMaterialNumCtrl(finalMaterialStockVO);
		        // 出库数量控制
		        checkMaterialStockOutNum(finalMaterialStockVO, ctrlVOs, returnMsg,
		            billVOs[0].getSrc_bill_type());
//		    }
	    }
	    // 物资需求申请单需要进行数量控制
	    else if (StringUtils.equals(BillTypeConst.APPLYBILL,billVOs[0].getSrc_bill_type() )) {
	    	// 查询数量控制方案
	        MaterialnumctrlVO[] ctrlVOs = queryMaterialNumCtrl(finalMaterialStockVO);
	        // 物资需求申请单数量控制
	        checkMaterialApplyNum(finalMaterialStockVO, ctrlVOs, returnMsg,
	            billVOs[0].getSrc_bill_type());
	    }
		return finalMaterialStockVO;
	}
	
	/**
	 * 检查出库单表体在项目下不存在物资需求单的物料行
	 * 
	 * @param billVOs
	 */
	private static void checkBodyMaterials(MaterialStockBodyFOrSCMVO[] billVOs) {
		if(billVOs == null || billVOs.length < 1){
			return;
		}
		
		// 获取表体所有项目
		Set<String> projectIds = new HashSet<>();
		for(MaterialStockBodyFOrSCMVO scmVO : billVOs){
			if(scmVO.getPk_project() != null){
				projectIds.add(scmVO.getPk_project());
			}
		}
		
		if(projectIds.size() == 0)
			return;
		
		/*// 查询项目下的物资需求单
		MaterialPlanBillVO[] materPlanBillVOs = NCLocator
				.getInstance()
				.lookup(IMaterialPlanCommonService.class)
				.queryMaterialPlanBillVOByPkProject(
						projectIds.toArray(new String[0]));
		
		if(materPlanBillVOs == null || materPlanBillVOs.length < 1)
			ExceptionUtils.wrappBusinessException("请先做物资需求单");
		
		// 项目主键和表体物料集合的映射
		Map<String, Set<String>> materPlanToMaters = new HashMap<>();
		for(MaterialPlanBillVO billVO : materPlanBillVOs){
			MaterialPlanHeadVO headVO = billVO.getParentVO();
			MaterialPlanBodyVO[] bodyVOs = billVO.getChildrenVO();
			if(!materPlanToMaters.containsKey(headVO.getPk_project())){
				materPlanToMaters.put(headVO.getPk_project(), new HashSet<String>());
			}
			
			if(bodyVOs == null || bodyVOs.length < 1)
				continue;
			
			for(MaterialPlanBodyVO bodyVO : bodyVOs){
				materPlanToMaters.get(headVO.getPk_project()).add(bodyVO.getPk_material());
			}	
		}
		
		StringBuffer errorMsg = new StringBuffer();
		for(MaterialStockBodyFOrSCMVO bodyVO : billVOs){
			String pkProject = bodyVO.getPk_project();
			// 当前行的物料存在于项目对应的物资需求单中，不处理。否则，抛出不能做出库的提示
			if(pkProject != null && materPlanToMaters.containsKey(pkProject) 
					&& materPlanToMaters.get(pkProject).contains(bodyVO.getPk_material()))
				continue;
			
			errorMsg.append(bodyVO.getSrc_rowno() + "、");
		}
		
		if(errorMsg.length() > 0){
			errorMsg.deleteCharAt(errorMsg.length() - 1);
			errorMsg.insert(0, "第");
			errorMsg.append("行的物料在项目下未做过物资及服务需求单，不允许出库。");
			
			ExceptionUtils.wrappBusinessException(errorMsg.toString());
		}*/
	}

	/**
	 * 
	 * 方法功能描述：物资需求申请数量控制,根据数量控制方案对备料表数据进行校验,并记录提示信息。
	 * <b>参数说明</b>
	 * @param finalMaterialStockVO
	 * @param ctrlVOs
	 * @param returnMsg
	 * @param src_bill_type
	 * @throws BusinessException
	 * <p>
	 * @author yujian1
	 * @time 2014-10-14 下午1:52:39
	 */
	private static  void checkMaterialApplyNum(MaterialStockVO[] finalMaterialStockVO,
		      MaterialnumctrlVO[] ctrlVOs, MaterialNumCtlInfoMSG returnMsg,
		      String src_bill_type) throws BusinessException {
			
	    List<MaterialStockVO> rigidityList = new ArrayList<MaterialStockVO>();// 需刚性控制的信息
	    List<MaterialStockVO> alertList = new ArrayList<MaterialStockVO>();// 预警控制的信息
	    for (int i = 0; i < finalMaterialStockVO.length; i++) {
	    	MaterialStockVO checkVO = finalMaterialStockVO[i];
	    	// 累计申请数
	    	UFDouble apply_num = checkVO.getApply_num();
	    	// 计划数量
	    	UFDouble planNum = checkVO.getNnum();
	    	// 物资计划主键
	    	String pk_mater_plan = checkVO.getPk_materialplan();
	    	if (null != ctrlVOs[i]) {
	    		// 如果 计划数量<累计申请数 则根据控制方案进行处理
	    		// 如果没有发布物资计划则不处理
	    		if (UFDoubleUtils.isLessThan(UFDoubleUtils.sub(planNum, apply_num),UFDouble.ZERO_DBL) 
	    				&& StringUtil.isNotEmpty(pk_mater_plan)) {
	    			// 控制类型
	    			int controlType = ctrlVOs[i].getControl_type();
	    			if (controlType == ControlType.rigidity_control) {// 刚性控制
	    				rigidityList.add(checkVO);
	    			}
	    			else if (controlType == ControlType.alert_control) {// 预警控制
	    				alertList.add(checkVO);
	    			}
	    		}
	    	}
	    }
	    if (!ListUtil.isEmpty(rigidityList)) {
	    	ExceptionUtils.wrappBusinessException(getReturnMsg(rigidityList,
	    			src_bill_type));
	    }
	    if (!ListUtil.isEmpty(alertList)) {
	    	returnMsg.setAlertMsg(getReturnMsg(alertList, src_bill_type));
	    }
	}

	/**
	 * 
	 * 方法功能描述：出库数量控制,根据数量控制方案对备料表数据进行校验,并记录提示信息。
	 * @param finalMaterialStockVO 备料数据
	 * @param ctrlVOs 数量控制方案
	 * @param returnMsg 返回信息类
	 * @param src_bill_type 单据类型
	 * @throws BusinessException
	 * @author yujian1
	 * @time 2014-1-17 下午06:48:45
	 */
	private static  void checkMaterialStockOutNum(MaterialStockVO[] finalMaterialStockVO,
	      MaterialnumctrlVO[] ctrlVOs, MaterialNumCtlInfoMSG returnMsg,
	      String src_bill_type) throws BusinessException {
		
	    List<MaterialStockVO> rigidityList = new ArrayList<MaterialStockVO>();// 需刚性控制的信息
	    List<MaterialStockVO> alertList = new ArrayList<MaterialStockVO>();// 预警控制的信息
	    for (int i = 0; i < finalMaterialStockVO.length; i++) {
	    	MaterialStockVO checkVO = finalMaterialStockVO[i];
	    	// 出库数
	    	UFDouble stockOutNum = checkVO.getStockout_num();
	    	// 预占量
	    	UFDouble stockApplyAdvNum = checkVO.getStock_apply_adv_num();
	    	// 计划数量
	    	UFDouble planNum = checkVO.getNnum();
	    	// 退库量
	    	UFDouble stockBackNum = checkVO.getStockback_num();
	    	// 物资计划主键
	    	String pk_mater_plan = checkVO.getPk_materialplan();
	    	if (null != ctrlVOs[i]) {
	    		// 如果 计划数量<预占量+出库量 则根据控制方案进行处理
	    		// 如果没有发布物资计划则不处理
	    		if (UFDoubleUtils.isLessThan(
	    				UFDoubleUtils.add(UFDoubleUtils.sub(planNum, stockOutNum, stockApplyAdvNum),stockBackNum),
	    				UFDouble.ZERO_DBL) && StringUtil.isNotEmpty(pk_mater_plan)) {
	    			// 控制类型
	    			int controlType = ctrlVOs[i].getControl_type();
	    			if (controlType == ControlType.rigidity_control) {// 刚性控制
	    				rigidityList.add(checkVO);
	    			}
	    			else if (controlType == ControlType.alert_control) {// 预警控制
	    				alertList.add(checkVO);
	    			}
	    		}
	    	}
	    }
	    if (!ListUtil.isEmpty(rigidityList)) {
	    	ExceptionUtils.wrappBusinessException(getReturnMsg(rigidityList,
	    			src_bill_type));
	    }
	    if (!ListUtil.isEmpty(alertList)) {
	    	returnMsg.setAlertMsg(getReturnMsg(alertList, src_bill_type));
	    }
	}
	
	
	/**
	 * 
	 * 方法功能描述：查询数量控制方案,返回的控制方案和备料数据的下标是对应的。
	 * @param finalMaterialStockVO
	 * @return
	 * @throws BusinessException
	 * @author yujian1
	 * @time 2014-1-17 下午06:43:25
	 */
	  private static MaterialnumctrlVO[] queryMaterialNumCtrl(
	      MaterialStockVO[] finalMaterialStockVO) throws BusinessException {
		
		  int length = finalMaterialStockVO.length;
		  String[] pk_org = new String[length];
		  String[] pk_materialtype = new String[length];
		  String[] pk_material = new String[length];
		  // 将项目组织和物料按照下标赋值
		  for (int i = 0; i < length; i++) {
			  pk_org[i] = finalMaterialStockVO[i].getPk_org();
			  pk_material[i] = finalMaterialStockVO[i].getPk_material();
		  }
		  // 物料对应物料分类的map key:物料pk value：物料分类pk
		  Map<String, String> materialTypeMap = new HashMap<String, String>();
		  // 查询物料对应的物料分类
		  materialTypeMap = queryMaterialTypeMap(pk_org[0], pk_material);
		  // 将物料分类按照下标赋值
		  for (int i = 0; i < length; i++) {
			  pk_materialtype[i] = materialTypeMap.get(pk_material[i]);
		  }
		  // 取组织
		  String pk_group = BizContext.getInstance().getGroupId();
		  MaterialnumctrlVO[] numCtrlVOS =
			  NCLocator
	            .getInstance()
	            .lookup(IMaterialnumctrlService.class)
	            .getStockOrgsFromItemStockRelationWithParentMaterialClass(pk_org,
	            		pk_materialtype, pk_material, pk_group);
		  return numCtrlVOS;
	  }
	  
	  /**
	   * 
	   * 方法功能描述：得到数量控制返回信息
	   * @param rigidityList
	   * @param src_bill_type
	   * @return
	   * @throws BusinessException
	   * @author yujian1
	   * @time 2014-1-17 下午06:49:21
	   */
	  private static String getReturnMsg(List<MaterialStockVO> rigidityList,
			  String src_bill_type) throws BusinessException {
		  // 项目pk对应项目名称
		  Map<String, String> projectMap = new HashMap<String, String>();
		  StringBuffer returnMsg = new StringBuffer();
		  String[] projectIDs = new String[rigidityList.size()];
		  String[] materialIDs=new String[rigidityList.size()];
		  for (int i = 0; i < rigidityList.size(); i++) {
			  projectIDs[i] = rigidityList.get(i).getPk_project();
			  materialIDs[i]= rigidityList.get(i).getPk_material_v();
		  }
		  ProjectHeadVO[] headVOs =
			  ArrayUtil.convertArrayType(LeachingUtils.getIProjectService()
					  .queryProjectHeadVOsByPK(projectIDs), ProjectHeadVO.class);
		  
		  Map<String, MaterialVO> materialMap=NCLocator.getInstance().
				  lookup(IMaterialPubService_C.class).queryMaterialBaseInfoByPks(materialIDs,new String[]{MaterialVO.CODE,MaterialVO.NAME});
		  
		  ScaleUtils scaleUtils =
			  new ScaleUtils(BizContext.getInstance().getGroupId());
		  for (ProjectHeadVO tempHeadVO : headVOs) {
			  if (!projectMap.containsKey(tempHeadVO.getPk_project())) {
				  projectMap
				  .put(tempHeadVO.getPk_project(), tempHeadVO.getProject_name());
			  }
		  }
		  for (MaterialStockVO tempStockVO : rigidityList) {
			  if (StringUtils.equals(BillTypeConst.STOCKOUTAPPLYBILL, src_bill_type)) {
				  // 项目名称
				  String projectName = projectMap.get(tempStockVO.getPk_project());
				  // 物料编码
				  String pk_material = materialMap.get(tempStockVO.getPk_material_v()).getCode();
				  // 物料名称
				  String materialname = materialMap.get(tempStockVO.getPk_material_v()).getName();
				  // 数量
				  UFDouble num =
					  UFDoubleUtils.getReverseNum(UFDoubleUtils.add(UFDoubleUtils.sub(tempStockVO.getNnum(),
							  tempStockVO.getStockout_num(), tempStockVO.getStock_apply_adv_num()),tempStockVO.getStockback_num(),
							  UFDouble.ZERO_DBL));
		       // 处理精度
				  String scaleNum =
					  scaleUtils.adjustNumScale(num, tempStockVO.getPk_measdoc())
					  .toString();
				  returnMsg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
						  "projectmaterial_0", "04815004-0116", null, new String[] {
								  projectName,pk_material+"_"+materialname, scaleNum
						  }));/*
						   * @res "[{0}]项目的出库申请单[{1}]出库申请数量超出当前可申请数量,超出的差值为[{2}]"
						   */
			  }
			  // 如果是物资需求申请单数量控制
			  else if(StringUtils.equals(BillTypeConst.APPLYBILL, src_bill_type)){
				  // 项目名称
				  String projectName = projectMap.get(tempStockVO.getPk_project());
				  // 物料编码
				  String pk_material = materialMap.get(tempStockVO.getPk_material_v()).getCode();
				  // 物料名称
				  String materialname = materialMap.get(tempStockVO.getPk_material_v()).getName();
				  // 数量
				  UFDouble num =
					  UFDoubleUtils.getReverseNum(UFDoubleUtils.sub(tempStockVO.getNnum(),
							  tempStockVO.getApply_num(),
							  UFDouble.ZERO_DBL));
				  
				  // 处理精度
				  String scaleNum =
					  scaleUtils.adjustNumScale(num, tempStockVO.getPk_measdoc())
					  .toString();
				  returnMsg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
						  "projectmaterial_0", "04815004-0184", null, new String[] {
								  projectName,pk_material+"_"+materialname, scaleNum
						  }));/*
						   * @res "[{0}]项目的物资需求申请单[{1}]申请数量超出当前需求量,超出的差值为[{2}]"
						   */
			  }
			  else {
				  // 项目名称
				  String projectName = projectMap.get(tempStockVO.getPk_project());
				  // 物料编码
				  String pk_material = materialMap.get(tempStockVO.getPk_material_v()).getCode();
				  // 物料名称
				  String materialname = materialMap.get(tempStockVO.getPk_material_v()).getName();
				  // 数量
				  UFDouble num =
					  UFDoubleUtils.getReverseNum(UFDoubleUtils.add(UFDoubleUtils.sub(tempStockVO.getNnum(),
							  tempStockVO.getStockout_num(), tempStockVO.getStock_apply_adv_num()),tempStockVO.getStockback_num(),
							  UFDouble.ZERO_DBL));
				  // 处理精度
				  String scaleNum =
					  scaleUtils.adjustNumScale(num, tempStockVO.getPk_measdoc())
					  .toString();
				  
				  returnMsg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
						  "projectmaterial_0", "04815004-0117", null, new String[] {
								  projectName,pk_material+"_"+materialname, scaleNum
						  }));/*
						   * @res "[{0}]项目的出库单[{1}]出库数量超出当前的可出库数量,超出的差值为[{2}]"
						   */
			  }
		  }
		  return returnMsg.toString();
	  }
	  
	  /**
	   * 
	   * 方法功能描述：查询物料对应物料分类
	   * @param pk_org
	   * @param pk_material
	   * @return
	   * @throws BusinessException
	   * @author yujian1
	   * @time 2014-1-17 下午06:44:15
	   */
	  private static Map<String, String> queryMaterialTypeMap(String pk_org,
	      String[] pk_material) throws BusinessException {
		  return getMaterialTypeService()
		  .queryMarBasClassIDByClassLevelAndMaterialOIDs(
				  getMaterialTypeService().getMaterialBaseClassMaxlevel(pk_org),
				  pk_material);
	  }
	  /**
	   * 
	   * 方法功能描述：得到物料分类服务
	   * @return
	   * @author yujian1
	   * @time 2014-1-17 下午06:44:08
	   */
	  private static IMaterialBaseClassPubService getMaterialTypeService() {
		  return NCLocator.getInstance().lookup(IMaterialBaseClassPubService.class);
	  }
}
