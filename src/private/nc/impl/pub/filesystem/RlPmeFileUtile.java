package nc.impl.pub.filesystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.trade.business.HYPubBO;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.uif.pub.exception.UifException;
import nc.vo.bc.pmpub.project.ProjectHeadVO;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.ct.purdaily.entity.CtPuVO;
import nc.vo.ct.purdaily.entity.RlPmeFile;
import nc.vo.hrss.pub.FileNodeVO;
import nc.vo.pbm.projectfinal.ProjectFinalHeadVO;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contractbalance.ContractBalanceHeadVO;
import nc.vo.pcm.contracttype.ContractTypeHeadVO;
import nc.vo.pcm.feebalance.FeeBalanceBodyVO;
import nc.vo.pcm.feebalance.FeeBalanceHeadVO;
import nc.vo.phm.projectproposal.ProjectProposalHeadVO;
import nc.vo.ppm.projectcheck.ProjectCheckHeadVO;
import nc.vo.pub.filesystem.FileExAttrVO;
import uap.pub.fs.client.FileStorageClient;

public class RlPmeFileUtile {

	private HYPubBO hyPubBO;

	public void createRlPmeFile(FileExAttrVO faVO) throws Exception {
		if (null != faVO.getPk_billtypecode()) {
			/* 费用结算单 */
			if (faVO.getPk_billtypecode().equals("4D83")) {
				createPmeFiles(getRlPmeByFeeBalance(faVO), faVO);
			}
			/* 清单发包合同 */
			if (faVO.getPk_billtypecode().equals("4D42")) {
				createPmeFile(getRlPmeByContrHeadVO(faVO), faVO);
			}
			/* 采购合同 */
			if (faVO.getPk_billtypecode().equals("Z2")) {
				createPmeFiles(getRlPmeByCtPu(faVO), faVO);

			}
			/* 项目建议书 */
			if (faVO.getPk_billtypecode().equals("4D15")) {
				createPmeFile(getRlPmeByProjectProposal(faVO), faVO);
			}
			/* 项目验收 */
			if (faVO.getPk_billtypecode().equals("4D36")) {
				createPmeFile(getRlPmeByProjectCheckHeadVO(faVO), faVO);
			}
			/* 结算单 */
			if (faVO.getPk_billtypecode().equals("4D50")) {
				createPmeFile(getRlPmeByContractBalanceHeadVO(faVO), faVO);
			}
			/* 项目决算单 */
			if (faVO.getPk_billtypecode().equals("4D64")) {
				createPmeFile(getRlPmeByProjectFinalHeadVO(faVO), faVO);
			}
		}
	}

	/**
	 * 采购合同处理
	 * 
	 * @param faVO
	 * @return
	 * @throws UifException
	 */
	public RlPmeFile[] getRlPmeByCtPu(FileExAttrVO faVO) throws UifException {
		/* step-1采购合同主键获取合同VO */
		CtPuVO hvo = (CtPuVO) getHyPubBO().queryByPrimaryKey(CtPuVO.class,
				faVO.getPk_bill());
		CtPuBVO[] bvo = (CtPuBVO[]) getHyPubBO().queryByCondition(CtPuBVO.class, "nvl(dr,0) = 0 and pk_ct_pu ='" + faVO.getPk_bill() + "'");
		/* step-2根据合同主键获取项目主键 */
		Map<String, RlPmeFile> rlPmeFilees = new HashMap<String, RlPmeFile>();
		for (CtPuBVO temp : bvo) {
			RlPmeFile rlPme = new RlPmeFile();
			rlPme.setProject_id(temp.getCbprojectid());
			queryEpsProject(rlPme);
			rlPme.setCatalog3("合同");
			rlPme.setBill_type(hvo.getVtrantypecode());
			if(null != hvo.getVdef12()){
				String strwhere = " nvl(dr,0) = 0 and pk_defdoc ='" + hvo.getVdef12() + "'";
				String templateName = (String) getHyPubBO().findColValue("bd_defdoc", "name", strwhere);
				rlPme.setCatalog4(templateName);
				DefdocVO defdocVO = (DefdocVO) getHyPubBO().queryByPrimaryKey(
						DefdocVO.class, hvo.getVdef12());
				rlPme.setCatalog5(defdocVO.getName());
			}
			rlPmeFilees.put(temp.getCbprojectid(), rlPme);
		}
		return getRlPmeFiles(rlPmeFilees);
	}

	private RlPmeFile[] getRlPmeFiles(Map<String, RlPmeFile> rlPmeFilees) {
		List<RlPmeFile> rlPmeFiles = new ArrayList<RlPmeFile>();
		for (Map.Entry<String, RlPmeFile> entry : rlPmeFilees.entrySet()) {
			rlPmeFiles.add(entry.getValue());
		}
		return rlPmeFiles.toArray(new RlPmeFile[0]);
	}

	private void queryEpsProject(RlPmeFile rlPmeFile)
			throws UifException {
		ProjectHeadVO projectHvo = (ProjectHeadVO) getHyPubBO()
				.queryByPrimaryKey(ProjectHeadVO.class, rlPmeFile.getProject_id());
		rlPmeFile.setEps_id(projectHvo.getPk_eps());
	}

	private String getName(String filePath) {
		int lastIndex = filePath.lastIndexOf("/");
		return filePath.substring(lastIndex + 1);
	}

	/* 费用结算单 */
	public RlPmeFile[] getRlPmeByFeeBalance(FileExAttrVO faVO)
			throws UifException {
		/* 获取单据交易类型 */
		FeeBalanceHeadVO feeBalanceHeadVO = (FeeBalanceHeadVO) getHyPubBO()
				.queryByPrimaryKey(FeeBalanceHeadVO.class, faVO.getPk_bill());
		FeeBalanceBodyVO[] feeBalanceBodyVO = (FeeBalanceBodyVO[]) getHyPubBO()
				.queryByCondition(
						FeeBalanceBodyVO.class,
						" nvl(dr,0) = 0 and pk_feebalance='"
								+ faVO.getPk_bill() + "'");
		Map<String, RlPmeFile> rlPmeFilees = new HashMap<String, RlPmeFile>();
		for (FeeBalanceBodyVO temp : feeBalanceBodyVO) {
			RlPmeFile rlPme = new RlPmeFile();
			rlPme.setBill_type(feeBalanceHeadVO.getTransi_type());
			rlPme.setProject_id(temp.getPk_project());
			queryEpsProject(rlPme);
			rlPmeFilees.put(temp.getPk_project(), rlPme);
		}
		List<RlPmeFile> rlPmeFiles = new ArrayList<RlPmeFile>();
		for (Map.Entry<String, RlPmeFile> entry : rlPmeFilees.entrySet()) {
			rlPmeFiles.add(entry.getValue());
		}
		switch (feeBalanceHeadVO.getTransi_type()) {
		/** 项目勘查资料 **/
		case "4D83-Cxx-05":
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				rlPmeFile.setCatalog3("前期手续资料");
				rlPmeFile.setCatalog4("项目勘查资料");
			}
			break;
		/** 项目申请报告（可研）资料 **/
		case "4D83-Cxx-12":
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				rlPmeFile.setCatalog3("前期手续资料");
				rlPmeFile.setCatalog4("项目申请报告（可研）资料");
			}
			break;
		/** 能评资料 **/
		case "4D83-Cxx-07":
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				rlPmeFile.setCatalog3("前期手续资料");
				rlPmeFile.setCatalog4("能评资料");
			}
			break;
		/** 环评资料 **/
		case "4D83-Cxx-09":
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				rlPmeFile.setCatalog3("前期手续资料");
				rlPmeFile.setCatalog4("环评资料");
			}
			break;
		/** 项目立项资料 **/
		case "4D83-Cxx-06":
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				rlPmeFile.setCatalog3("前期手续资料");
				rlPmeFile.setCatalog4("项目立项资料");
			}
			break;
		/** 招标类别 **/
		case "4D83-Cxx-04":
			/* 根据招标类别与合同类型处理业务逻辑 */
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				/* 根据招标类别 */
				if(null != feeBalanceHeadVO.getDef1()){
					rlPmeFile.setCatalog3("设计阶段资料");
					rlPmeFile.setCatalog4("设计招标资料");
					DefdocVO defdocVO = (nc.vo.bd.defdoc.DefdocVO) getHyPubBO().queryByPrimaryKey(DefdocVO.class, feeBalanceHeadVO.getDef1());
					if("001".equals(defdocVO.getCode())){
						rlPmeFile.setCatalog4(defdocVO.getName());
					}else if("002".equals(defdocVO.getCode())){
						rlPmeFile.setCatalog4(defdocVO.getName());
					}
				}
			}
			break;
		/** 图纸会审资料 **/
		case "4D83-Cxx-14":
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				rlPmeFile.setCatalog3("设计阶段资料");
				rlPmeFile.setCatalog4("图纸会审资料");
			}
			break;
		/** 正式图纸 **/
		case "4D83-Cxx-15":
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				rlPmeFile.setCatalog3("设计阶段资料");
				rlPmeFile.setCatalog4("正式图纸");
			}
			break;
		/** 土地占用手续 **/
		case "4D83-Cxx-16":
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				rlPmeFile.setCatalog3("施工手续资料");
				rlPmeFile.setCatalog4("土地占用手续");
			}
			break;
		/** 绿化占用手续 **/
		case "4D83-Cxx-18":
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				rlPmeFile.setCatalog3("施工手续资料");
				rlPmeFile.setCatalog4("绿化占用手续");
			}
			break;
		/** 绿化占用手续 **/
		case "4D83-Cxx-19":
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				rlPmeFile.setCatalog3("施工手续资料");
				rlPmeFile.setCatalog4("河道手续");
			}
			break;
		/** 涉铁施工手续 **/
		case "4D83-Cxx-20":
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				rlPmeFile.setCatalog3("施工手续资料");
				rlPmeFile.setCatalog4("涉铁施工手续");
			}
			break;
		/** 道路刨掘手续 **/
		case "4D83-Cxx-17":
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				rlPmeFile.setCatalog3("施工手续资料");
				rlPmeFile.setCatalog4("道路刨掘手续");
			}
			break;
		/** 其他手续 **/
		case "4D83-Cxx-28":
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				rlPmeFile.setCatalog3("施工手续资料");
				rlPmeFile.setCatalog4("其他手续");
			}
			break;
		/** 采购招标 **/
		case "4D83-Cxx-22":
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				rlPmeFile.setCatalog3("施工/材料招标及合同资料");
				rlPmeFile.setCatalog4("采购招标");
			}
			break;
		/** 采购招标 **/
		case "4D83-Cxx-02":
			/* 根据合同类型处理业务逻辑 */
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				/* 多编码合同 */
				setContractType(feeBalanceHeadVO.getDef10(), rlPmeFile);
			}
			break;
		/** 采购招标 **/
		case "4D83-Cxx-24":
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				rlPmeFile.setCatalog3("施工过程资料");
				rlPmeFile.setCatalog4("施工资料");
			}
			break;
		default:
			/**/
			for (RlPmeFile rlPmeFile : rlPmeFiles) {
				if ("4D83-Cxx-10".equals(feeBalanceHeadVO.getTransi_type())
						|| "4D83-Cxx-11".equals(feeBalanceHeadVO
								.getTransi_type())
						|| "4D83-Cxx-26".equals(feeBalanceHeadVO
								.getTransi_type())) {
					rlPmeFile.setCatalog3("前期手续资料");
					rlPmeFile.setCatalog4("项目规划资料");
				}
			}
			break;
		}
		return rlPmeFiles.toArray(new RlPmeFile[0]);
	}
	
	private void setContractType(String pk_contracttype, RlPmeFile rlPmeFile)
			throws UifException {
		ContractTypeHeadVO contractTypeHeadVO = (ContractTypeHeadVO) getHyPubBO()
				.queryByPrimaryKey(ContractTypeHeadVO.class, pk_contracttype);
		/* 0501 为采购设计类型 */
		if ("0501".equals(contractTypeHeadVO.getType_code())) {
			rlPmeFile.setCatalog3("设计阶段资料");
			rlPmeFile.setCatalog4("设计合同");
		} else {
			rlPmeFile.setCatalog3("施工/材料招标及合同资料");
			rlPmeFile.setCatalog4("合同");
		}
		String typeName = (String) getHyPubBO().findColValue(
				"pm_contracttype",
				"type_name",
				"nvl(dr,0) = 0 and pk_contracttype = '"
						+ contractTypeHeadVO.getPk_parent() + "'");
		rlPmeFile.setCatalog5(typeName);
		rlPmeFile.setCatalog6(contractTypeHeadVO.getType_name());
	}

	/* 清单发包合同 */
	public RlPmeFile getRlPmeByContrHeadVO(FileExAttrVO faVO)
			throws UifException {
		/*项目分包合同管理*/
		ContrHeadVO contrHeadVO = (ContrHeadVO) getHyPubBO().queryByPrimaryKey(
				ContrHeadVO.class, faVO.getPk_bill());
		/*合同类型*/
		ContractTypeHeadVO contractTypeHeadVO = (ContractTypeHeadVO) getHyPubBO()
				.queryByPrimaryKey(ContractTypeHeadVO.class,
						contrHeadVO.getPk_contracttype());
		RlPmeFile rlPmeFile = new RlPmeFile();
		rlPmeFile.setBill_type(contrHeadVO.getTransi_type());
		rlPmeFile.setProject_id(contrHeadVO.getPk_project());
		queryEpsProject(rlPmeFile);
		/* 0501 为采购设计类型 */
		if ("0501".equals(contractTypeHeadVO.getType_code())) {
			rlPmeFile.setCatalog3("设计阶段资料");
			rlPmeFile.setCatalog4("设计合同");
		} else {
			rlPmeFile.setCatalog3("施工/材料招标及合同资料");
			rlPmeFile.setCatalog4("合同");
		}
		String typeName = (String) getHyPubBO().findColValue(
				"pm_contracttype",
				"type_name",
				"nvl(dr,0) = 0 and pk_contracttype = '"
						+ contractTypeHeadVO.getPk_parent() + "'");
		rlPmeFile.setCatalog5(typeName);
		rlPmeFile.setCatalog6(contractTypeHeadVO.getType_name());
		return rlPmeFile;
	}

	/* 项目建议书 */
	public RlPmeFile getRlPmeByProjectProposal(FileExAttrVO faVO)
			throws UifException {
		RlPmeFile rlPmeFile = null;
		ProjectProposalHeadVO projectProposalHeadVO = (ProjectProposalHeadVO) getHyPubBO()
				.queryByPrimaryKey(ProjectProposalHeadVO.class,
						faVO.getPk_bill());
		if ("4D15-Cxx-008".equals(projectProposalHeadVO.getTransi_type())) {
			rlPmeFile = new RlPmeFile();
			rlPmeFile.setCatalog3("施工过程资料");
			rlPmeFile.setCatalog4("开工资料");
			rlPmeFile.setBill_type(projectProposalHeadVO.getTransi_type());
		}
		return rlPmeFile;
	}

	/* 项目验收 */
	public RlPmeFile getRlPmeByProjectCheckHeadVO(FileExAttrVO faVO)
			throws UifException {
		ProjectCheckHeadVO projectCheckHeadVO = (ProjectCheckHeadVO) getHyPubBO()
				.queryByPrimaryKey(ProjectCheckHeadVO.class, faVO.getPk_bill());
		RlPmeFile rlPmeFile = null;
		if ("4D36-01".equals(projectCheckHeadVO.getTransi_type())
				|| "4D36-02".equals(projectCheckHeadVO.getTransi_type())
				|| "4D36-03".equals(projectCheckHeadVO.getTransi_type())) {
			rlPmeFile = new RlPmeFile();
			rlPmeFile.setCatalog3("验收资料");
			rlPmeFile.setCatalog4("竣工资料");
			rlPmeFile.setBill_type(projectCheckHeadVO.getTransi_type());
		}
		return rlPmeFile;
	}

	/* 结算单 */
	public RlPmeFile getRlPmeByContractBalanceHeadVO(FileExAttrVO faVO)
			throws UifException {
		ContractBalanceHeadVO contractBalanceHeadVO = (ContractBalanceHeadVO) getHyPubBO()
				.queryByPrimaryKey(ContractBalanceHeadVO.class,
						faVO.getPk_bill());
		RlPmeFile rlPmeFile = null;
		if ("4D50-01".equals(contractBalanceHeadVO.getTransi_type())) {
			rlPmeFile = new RlPmeFile();
			rlPmeFile.setCatalog3("结算/决算资料");
			rlPmeFile.setCatalog4("结算资料");
			rlPmeFile.setBill_type(contractBalanceHeadVO.getTransi_type());
		}
		return rlPmeFile;
	}

	/* 项目决算单 */
	public RlPmeFile getRlPmeByProjectFinalHeadVO(FileExAttrVO faVO)
			throws UifException {
		ProjectFinalHeadVO projectFinalHeadVO = (ProjectFinalHeadVO) getHyPubBO()
				.queryByPrimaryKey(ProjectFinalHeadVO.class, faVO.getPk_bill());
		RlPmeFile rlPmeFile = null;
		if ("4D64_01".equals(projectFinalHeadVO.getTransi_type())) {
			rlPmeFile = new RlPmeFile();
			rlPmeFile.setCatalog3("结算/决算资料");
			rlPmeFile.setCatalog4("决算资料");
			rlPmeFile.setBill_type(projectFinalHeadVO.getTransi_type());
		}
		return rlPmeFile;
	}

	private void createPmeFile(RlPmeFile rlPmeFile, FileExAttrVO faVO)
			throws Exception {
		/* 组装公共字段 */
		String filePath = (String) getHyPubBO().findColValue(
				"sm_pub_filesystem", "pk_doc",
				" nvl(dr,0) = 0 and pk = '" + faVO.getPk_file() + "'");
		rlPmeFile.setFile_path(FileStorageClient.getInstance()
				.getViewURL(null, filePath + ""));
		rlPmeFile.setFile_down_path(FileStorageClient.getInstance()
				.getDownloadURL(null, filePath + ""));
		rlPmeFile.setFile_id(faVO.getPk_file());
		filePath = (String) getHyPubBO().findColValue(
				"sm_pub_filesystem", "filepath",
				" nvl(dr,0) = 0 and pk = '" + faVO.getPk_file() + "'");
		rlPmeFile.setDef1(getName(filePath));
		getHyPubBO().insert(rlPmeFile);
	}

	private void createPmeFiles(RlPmeFile[] rlPmeFile, FileExAttrVO faVO)
			throws Exception {
		/* 组装公共字段 */
		for (RlPmeFile temp : rlPmeFile) {
			String filePath = (String) getHyPubBO().findColValue(
					"sm_pub_filesystem", "pk_doc",
					" nvl(dr,0) = 0 and pk = '" + faVO.getPk_file() + "'");
			temp.setFile_path(FileStorageClient.getInstance()
					.getViewURL(null, filePath + ""));
			temp.setFile_down_path(FileStorageClient.getInstance()
					.getDownloadURL(null, filePath + ""));
			temp.setFile_id(faVO.getPk_file());
			filePath = (String) getHyPubBO().findColValue(
					"sm_pub_filesystem", "filepath",
					" nvl(dr,0) = 0 and pk = '" + faVO.getPk_file() + "'");
			temp.setDef1(getName(filePath));
		}
		getHyPubBO().insertAry(rlPmeFile);
	}

	public HYPubBO getHyPubBO() {
		if (null == this.hyPubBO) {
			this.hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}

	/**
	 * 期初导入
	 * @throws UifException 
	 */
	public void beginning() throws Exception {
		String counts = (String) new HYPubBO().findColValue("sys_config", "config_value", " config_key = 'count'");
		int i = 0;
		int count = Integer.valueOf(counts);
		/*其他期初导入*/
//		List<FileExAttrVO> fileExAttrVOs = (List<FileExAttrVO>) new BaseDAO().executeQuery("SELECT pk_bill, pk_billtypecode, pk_file FROM bd_fileexattr where nvl(dr,0) = 0 ORDER BY ts DESC", new BeanListProcessor(FileExAttrVO.class));
//		for(FileExAttrVO fileExAttrVO : fileExAttrVOs){
//			if(i < count){
//				RlPmeFile[] flPmeFile = (RlPmeFile[]) getHyPubBO().queryByCondition(RlPmeFile.class, "nvl(dr,0) = 0 and file_id = '" + fileExAttrVO.getPk_file() + "'");
//				if(null == flPmeFile || flPmeFile.length == 0){
//					createRlPmeFile(fileExAttrVO);	
//				}
//			}
//			i ++;
//		}
		i = 0;
		/*合同期初导入*/
		CtPuVO[] ctpus = (CtPuVO[]) getHyPubBO().queryByCondition(CtPuVO.class, "nvl(dr,0) = 0 and pk_org in (select pk_purchaseorg from org_purchaseorg where nvl(dr,0) = 0 and def1='1') order by ts desc");
		for(CtPuVO temp : ctpus){
			if(i < count){
				FileNodeVO[] fileNodeVOS = (FileNodeVO[]) getHyPubBO().queryByCondition(FileNodeVO.class, " isfolder = 'n' and nvl(dr,0) = 0 and filepath like '%" + temp.getPk_ct_pu() + "%'");
				if(null != fileNodeVOS && fileNodeVOS.length > 0){
					for(FileNodeVO fileNode : fileNodeVOS){
						String pk_file = (String) getHyPubBO().findColValue("sm_pub_filesystem", "pk", " nvl(dr,0) = 0 and pk_doc = '" + fileNode.getPk_doc() + "'");
						FileExAttrVO fileExAttrVO = new FileExAttrVO();
						fileExAttrVO.setPk_bill(temp.getPk_ct_pu());
						fileExAttrVO.setPk_file(pk_file);
						fileExAttrVO.setPk_billtypecode("Z2");
						RlPmeFile[] flPmeFile = (RlPmeFile[]) getHyPubBO().queryByCondition(RlPmeFile.class, "nvl(dr,0) = 0 and file_id = '" + pk_file + "'");
						if(null == flPmeFile || flPmeFile.length == 0){
							createRlPmeFile(fileExAttrVO);
						}
					}
				}
			}
			i ++;
		}
	}
}
