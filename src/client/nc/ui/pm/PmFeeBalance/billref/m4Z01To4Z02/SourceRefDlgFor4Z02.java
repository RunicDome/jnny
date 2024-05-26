package nc.ui.pm.PmFeeBalance.billref.m4Z01To4Z02;

import java.awt.Container;

import nc.ui.pub.pf.BillSourceVar;
import nc.ui.pubapp.billref.src.view.SourceRefDlg;

public class SourceRefDlgFor4Z02 extends SourceRefDlg {
	private static final long serialVersionUID = 5811618879014228342L;

	public SourceRefDlgFor4Z02(Container parent, BillSourceVar bsVar) {
		super(parent, bsVar, true);
	}

	public String getRefBillInfoBeanPath() {
		return "nc/ui/pm/PmFeeBalance/billref/m4Z01To4Z02/M4Z01Ref4Z02Info.xml";
	}
}