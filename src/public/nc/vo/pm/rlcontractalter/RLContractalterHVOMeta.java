package nc.vo.pm.rlcontractalter;
/*    */ import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class RLContractalterHVOMeta extends AbstractBillMeta{
		
	 public RLContractalterHVOMeta()
	/*    */   {
		/* 14 */     init();
		/*    */   }
	 private void init() {
		 /* 18 */     setParent(RLContractalterHVO.class);
		 /* 19 */     addChildren(RLContractalterBVO.class);
		 /*    */   }
}
