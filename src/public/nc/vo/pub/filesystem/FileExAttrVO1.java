/*     */ package nc.vo.pub.filesystem;
import nc.vo.pub.SuperVO;
/*     */ 
/*     */ import nc.vo.pub.ValueObject;
/*     */ 
/*     */ public class FileExAttrVO1 extends SuperVO
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private String pk_fileexattr;
/*     */   private String pk_file;
/*     */   
/*     */   public FileExAttrVO1() {}
/*     */   
/*     */   public String getPKFieldName() {
/*  14 */     return "pk_fileexattr";
/*     */   }
/*     */   
/*     */   public String getTableName() {
/*  18 */     return "bd_fileexattr";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String pk_filetype;
/*     */   
/*     */ 
/*     */   private String pk_exattr;
/*     */   
/*     */   private String pk_bill;
/*     */   
/*     */   private String pk_billtypecode;
/*     */   
/*     */   private String location;
/*     */   
/*     */   public static final String PK_FILEEXATTR = "pk_fileexattr";
/*     */   
/*     */   public static final String PK_FILE = "pk_file";
/*     */   
/*     */   public static final String PK_FILETYPE = "pk_filetype";
/*     */   
/*     */   public static final String PK_EXATTR = "pk_exattr";
/*     */   
/*     */   public static final String PK_BILL = "pk_bill";
/*     */   
/*     */   public static final String PK_BILLTYPECODE = "pk_billtypecode";
/*     */   
/*     */   public static final String LOCATION = "location";
/*     */   
/*     */   public String getPk_fileexattr()
/*     */   {
/*  50 */     return this.pk_fileexattr;
/*     */   }
/*     */   
/*     */   public void setPk_fileexattr(String pk_fileexattr) {
/*  54 */     this.pk_fileexattr = pk_fileexattr;
/*     */   }
/*     */   
/*     */   public String getPk_file() {
/*  58 */     return this.pk_file;
/*     */   }
/*     */   
/*     */   public void setPk_file(String pk_file) {
/*  62 */     this.pk_file = pk_file;
/*     */   }
/*     */   
/*     */   public String getPk_filetype() {
/*  66 */     return this.pk_filetype;
/*     */   }
/*     */   
/*     */   public void setPk_filetype(String pk_filetype) {
/*  70 */     this.pk_filetype = pk_filetype;
/*     */   }
/*     */   
/*     */   public String getPk_exattr() {
/*  74 */     return this.pk_exattr;
/*     */   }
/*     */   
/*     */   public void setPk_exattr(String pk_exattr) {
/*  78 */     this.pk_exattr = pk_exattr;
/*     */   }
/*     */   
/*     */   public String getPk_bill() {
/*  82 */     return this.pk_bill;
/*     */   }
/*     */   
/*     */   public void setPk_bill(String pk_bill) {
/*  86 */     this.pk_bill = pk_bill;
/*     */   }
/*     */   
/*     */   public String getPk_billtypecode() {
/*  90 */     return this.pk_billtypecode;
/*     */   }
/*     */   
/*     */   public void setPk_billtypecode(String pk_billtypecode) {
/*  94 */     this.pk_billtypecode = pk_billtypecode;
/*     */   }
/*     */   
/*     */   public String getLocation() {
/*  98 */     return this.location;
/*     */   }
/*     */   
/*     */   public void setLocation(String location) {
/* 102 */     this.location = location;
/*     */   }
/*     */   

/*     */ }

/* Location:           D:\nchome\modules\pubapp\lib\pubpubapp_accessoryex.jar
 * Qualified Name:     nc.vo.pub.filesystem.FileExAttrVO
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.0.1
 */