package nc.ui.pcm.pcmproject.ref.model;

import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.IRefDocEdit;
import nc.ui.bd.ref.IRefMaintenanceHandler;
import nc.vo.pcm.pcmproject.PcmProject;

/**
 * 项目参照
 */
public class PcmProjectRefModel extends AbstractRefModel {

	public PcmProjectRefModel() {
		reset();
	}

	@Override
	public void reset() {

		setFieldCode(new String[] { PcmProject.PK_PCMROJECT, PcmProject.CODE,
				PcmProject.NAME });
		setFieldName(new String[] { "项目主键", "项目编码", "项目名称" });
		setHiddenFieldCode(new String[] { PcmProject.PK_PCMROJECT });
		setTableName("pcm_pcmproject");
		setPkFieldCode(PcmProject.PK_PCMROJECT);
		setRefCodeField(PcmProject.CODE);
		setRefNameField(PcmProject.NAME);
		// setHiddenFieldCode(new String[] { "pk_pcmproject" });
		// setFormulas(new String[][] { { StordocVO.STORADDR,"formataddress(" +
		// StordocVO.STORADDR + ");" } });

		// 使用启用条件
		// setAddEnableStateWherePart(true);

		// 数据权限
		// setResourceID(IBDResourceIDConst.STORDOC);

		// 维护
//		setRefMaintenanceHandler(new IRefMaintenanceHandler() {
//
//			@Override
//			public String[] getFucCodes() {
//				return new String[] { "4820020005457" };
//			}
//
//			@Override
//			public IRefDocEdit getRefDocEdit() {
//				return null;
//			}
//		});

		resetFieldName();
	}

	/**
	 * @return java.lang.String
	 */
	public String getRefTitle() {
		return "项目参照";
	}

	/**
	 * 设置不显示参照
	 */
	public String[] getHiddenFieldCode() {
		return new String[] { PcmProject.PK_PCMROJECT };
	}

	/**
	 * 设置参照表名
	 */
	public String getTableName() {
		return "pcm_pcmproject";
	}

	/**
	 * 设置参照查询规则
	 * 
	 */
	public String getWherePart() {
		return " isnull(dr, 0) = 0 ";
	}

	public boolean isMatchPkWithWherePart() {
		return true;
	}

	/**
	 * 获取主键字段，必要
	 */
	public String getPkFieldCode() {
		return PcmProject.PK_PCMROJECT;
	}

}
