package nc.ui.arap.actions;

import nc.imag.scan.action.BaseImageScanAction;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;

// 应收应付影像补扫
@SuppressWarnings({ "restriction", "unused" })
public class ReceiptScanAction extends BaseImageScanAction {
	private static final long serialVersionUID = 1L;

	public ReceiptScanAction() {
		setCode("ReceiptScan");
		setBtnName(NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
				"arapcommonv6-0186"));
	}
}
