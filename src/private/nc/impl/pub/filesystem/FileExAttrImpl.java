/*     */ package nc.impl.pub.filesystem;
/*     */ 
/*     */ import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.filesystem.IFileTypeService;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pubapp.pattern.data.vo.VODelete;
import nc.impl.pubapp.pattern.data.vo.VOInsert;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.data.vo.VOUpdate;
import nc.impl.pubapp.pattern.database.DBTool;
import nc.jdbc.framework.exception.DbException;
import nc.md.persist.designer.vo.ClassVO;
import nc.uif.pub.exception.UifException;
import nc.vo.ct.purdaily.entity.RlPmeFile;
import nc.vo.pub.SuperVO;
import nc.vo.pub.filesystem.ExFileTypeVO;
import nc.vo.pub.filesystem.FileExAttrVO;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FileExAttrImpl implements nc.bs.pub.filesystem.IFileExAttrService
/*     */ {
/*     */   private String dsName;
int maxRows = 100000;
boolean addTimestamp = true;
/*     */   
/*     */   public FileExAttrImpl() {}
/*     */   
/*     */   private String getDsName()
/*     */   {
/*  28 */     if (this.dsName == null) {
/*  29 */       this.dsName = nc.bs.framework.common.InvocationInfoProxy.getInstance().getUserDataSource();
/*     */     }
/*  31 */     return this.dsName;
/*     */   }
/*     */   
/*     */   private String createPk() {
/*  35 */     DBTool dbTool = new DBTool();
/*  36 */     String[] ids = dbTool.getOIDs(1);
/*  37 */     return ids[0];
/*     */   }
/*     */   
/*     */   public String addExAttr(SuperVO exAttrVO)
/*     */   {
/*  42 */     VOInsert<SuperVO> service = new VOInsert();
/*  43 */     service.insert(new SuperVO[] { exAttrVO });
/*  44 */     return exAttrVO.getPrimaryKey();
/*     */   }
/*     */   
/*     */   public Boolean updateExAttr(SuperVO exAttrVO)
/*     */   {
/*  49 */     VOUpdate<SuperVO> service = new VOUpdate();
/*  50 */     service.update(new SuperVO[] { exAttrVO });

/*  51 */     return Boolean.valueOf(true);
/*     */   }
/*     */   
/*     */   public Boolean addFileExAttr(FileExAttrVO faVO)
/*     */   {
/*  56 */     FileExAttrDAO dao = new FileExAttrDAO(getDsName());
/*     */     try
/*     */     {
/*  59 */       faVO.setPk_fileexattr(createPk());
/*  60 */       dao.insertFileExAttr(faVO);
				//TODO create By zwh 创建文件目录表数据  start
				String beginning = (String) new HYPubBO().findColValue("sys_config", "config_value", " config_key = 'beginning'");
				if("true".equals(beginning)){
					new RlPmeFileUtile().beginning(); 
				}else{
					new RlPmeFileUtile().createRlPmeFile(faVO); 	
				}
				// TODO create By zwh 创建文件目录表数据  end
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  64 */       Logger.error(e);
/*  65 */       return Boolean.valueOf(false);
/*     */     }
/*  67 */     return Boolean.valueOf(true);
/*     */   }

/*     */   
/*     */   public void deleteFileExAttr(String pk_file, String pk_bill)
/*     */   {
/*  72 */     FileExAttrDAO dao = new FileExAttrDAO(getDsName());
/*     */     
/*  74 */     FileExAttrVO[] faVOs = queryFileExAttr(pk_file, pk_bill);
/*     */     try
/*     */     {
/*  77 */       for (FileExAttrVO faVO : faVOs) {
/*  78 */         ExFileTypeVO ftVO = ((IFileTypeService)NCLocator.getInstance().lookup(IFileTypeService.class)).queryFileTypeByPk(faVO.getPk_filetype());
/*     */         
/*     */ 
/*     */ 
/*  82 */         SuperVO[] exAttrVOs = queryExAttr(pk_file, ftVO, pk_bill);
/*  83 */         for (SuperVO exAttrVO : exAttrVOs) {
/*  84 */           VODelete<SuperVO> voDel = new VODelete();
/*  85 */           voDel.delete(new SuperVO[] { exAttrVO });
/*     */         }
/*     */       }
/*  88 */       dao.deleteFileExAttr(pk_file);
				//TODO create By zwh 创建文件目录表数据  start
				try {
					new HYPubBO().deleteByWhereClause(RlPmeFile.class, "nvl(dr,0) = 0 and file_id = '" + pk_file + "'");
				} catch (UifException e) {
					e.printStackTrace();
				} 
				// TODO create By zwh 创建文件目录表数据  end
/*     */     }
/*     */     catch (DbException e)
/*     */     {
/*  92 */       Logger.error(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public FileExAttrVO[] queryFileExAttr(String pk_file)
/*     */   {
/*  98 */     FileExAttrDAO dao = new FileExAttrDAO(getDsName());
/*  99 */     FileExAttrVO[] faVOs = dao.queryFileExAttr(pk_file);
/* 100 */     return faVOs;
/*     */   }
/*     */   
/*     */   public FileExAttrVO[] queryFileExAttrByPk(String[] pk_fileexattrs)
/*     */   {
/* 105 */     FileExAttrDAO dao = new FileExAttrDAO(getDsName());
/* 106 */     FileExAttrVO[] faVOs = dao.queryFileExAttrByPk(pk_fileexattrs);
/* 107 */     return faVOs;
/*     */   }
/*     */   
/*     */   public FileExAttrVO[] queryFileExAttr(String pk_file, String pk_bill)
/*     */   {
/* 112 */     FileExAttrDAO dao = new FileExAttrDAO(getDsName());
/* 113 */     FileExAttrVO[] faVOs = dao.queryFileExAttr(pk_file, pk_bill);
/* 114 */     return faVOs;
/*     */   }
/*     */   
/*     */   public SuperVO[] queryExAttr(String pk_file, ExFileTypeVO ftVO)
/*     */   {
/*     */     try
/*     */     {
/* 121 */       BaseDAO baseDao = new BaseDAO();
/* 122 */       Object classVO = baseDao.retrieveByPK(ClassVO.class, ftVO.getClassid());
/*     */       
/* 124 */       if (classVO == null) {
/* 125 */         return null;
/*     */       }
/* 127 */       String classPath = ((ClassVO)classVO).getFullClassName();
/*     */       
/* 129 */       FileExAttrVO[] faVOs = queryFileExAttr(pk_file);
/* 130 */       if (faVOs.length > 0) {
/* 131 */         String pk_exAttr = faVOs[0].getPk_exattr();
/* 132 */         VOQuery<SuperVO> service = new VOQuery(Class.forName(classPath));
/*     */         
/* 134 */         return (SuperVO[])service.query(new String[] { pk_exAttr });
/*     */       }
/*     */       
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/* 140 */       Logger.error(e);
/*     */     }
/*     */     catch (DAOException e)
/*     */     {
/* 144 */       Logger.error(e);
/*     */     }
/* 146 */     return null;
/*     */   }
/*     */   
/*     */   public SuperVO[] queryExAttr(String[] pk_exattrs, ExFileTypeVO ftVO)
/*     */   {
/* 151 */     SuperVO[] exAttrVOs = null;
/*     */     try {
/* 153 */       BaseDAO baseDao = new BaseDAO();
/* 154 */       Object classVO = baseDao.retrieveByPK(ClassVO.class, ftVO.getClassid());
/*     */       
/* 156 */       if (classVO == null) {
/* 157 */         return null;
/*     */       }
/* 159 */       String classPath = ((ClassVO)classVO).getFullClassName();
/* 160 */       VOQuery<SuperVO> service = new VOQuery(Class.forName(classPath));
/*     */       
/* 162 */       exAttrVOs = (SuperVO[])service.query(pk_exattrs);
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/* 166 */       Logger.error(e);
/*     */     }
/*     */     catch (DAOException e)
/*     */     {
/* 170 */       Logger.error(e);
/*     */     }
/* 172 */     return exAttrVOs;
/*     */   }
/*     */   
/*     */ 
/*     */   public SuperVO[] queryExAttr(String pk_file, ExFileTypeVO ftVO, String pk_bill)
/*     */   {
/*     */     try
/*     */     {
/* 180 */       BaseDAO baseDao = new BaseDAO();
/* 181 */       Object classVO = baseDao.retrieveByPK(ClassVO.class, ftVO.getClassid());
/*     */       
/* 183 */       if (classVO == null) {
/* 184 */         return null;
/*     */       }
/* 186 */       String classPath = ((ClassVO)classVO).getFullClassName();
/*     */       
/* 188 */       FileExAttrVO[] faVOs = queryFileExAttr(pk_file, pk_bill);
/* 189 */       if (faVOs.length > 0) {
/* 190 */         String pk_exAttr = faVOs[0].getPk_exattr();
/* 191 */         VOQuery<SuperVO> service = new VOQuery(Class.forName(classPath));
/*     */         
/* 193 */         return (SuperVO[])service.query(new String[] { pk_exAttr });
/*     */       }
/*     */       
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/* 199 */       Logger.error(e);
/*     */     }
/*     */     catch (DAOException e)
/*     */     {
/* 203 */       Logger.error(e);
/*     */     }
/* 205 */     return null;
/*     */   }
/*     */   
/*     */   public SuperVO[] queryExAttr(ExFileTypeVO ftVO, String whereSql)
/*     */   {
/* 210 */     SuperVO[] exAttrVOs = null;
/*     */     
/* 212 */     BaseDAO baseDao = new BaseDAO();
/*     */     try
/*     */     {
/* 215 */       Object classVO = baseDao.retrieveByPK(ClassVO.class, ftVO.getClassid());
/* 216 */       if (classVO == null) {
/* 217 */         return null;
/*     */       }
/* 219 */       String classPath = ((ClassVO)classVO).getFullClassName();
/* 220 */       VOQuery<SuperVO> service = new VOQuery(Class.forName(classPath));
/*     */       
/* 222 */       return (SuperVO[])service.query(" and " + whereSql, "");
/*     */ 
/*     */     }
/*     */     catch (DAOException e)
/*     */     {
/* 227 */       Logger.error(e);
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/* 231 */       Logger.error(e);
/*     */     }
/* 233 */     return null;
/*     */   }
/*     */   
/*     */   public FileExAttrVO[] queryFileExAttrByAttr(List<String> pk_exAttrVOs) {
/* 237 */     FileExAttrDAO dao = new FileExAttrDAO(getDsName());
/* 238 */     FileExAttrVO[] faVOs = dao.queryFileExAttr(pk_exAttrVOs);
/* 239 */     return faVOs;
/*     */   }
/*     */   
/*     */   public String[] queryFileByAttr(ExFileTypeVO ftVO, String[] pk_exattrs, String whereSql) {
/* 243 */     FileExAttrDAO dao = new FileExAttrDAO(getDsName());
/* 244 */     String[] pk_fileexattrs = dao.queryFileByAttr(pk_exattrs, whereSql);
/* 245 */     return pk_fileexattrs;
/*     */   }
/*     */ }
