package nc.ui.pmr.pm.action.validator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nc.bs.uif2.validation.IValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.bs.uif2.validation.ValidationFailure;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pm.util.ArrayUtil;
import nc.vo.pmbd.common.utils.ExceptionUtils;
import nc.vo.pub.ISuperVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

public class BodyNotNullValidator implements IValidationService {
	private Map<String, String> bodyC_E = new HashMap();

	private final String defaultTabName = "default";

	public BodyNotNullValidator() {
	}

	public void validate(Object obj) throws ValidationException {
		AbstractBill aggVO = (AbstractBill) obj;
		Iterator<String> keyIter = getBodyC_E().keySet().iterator();
		ValidationException vfE = new ValidationException();
		while (keyIter.hasNext()) {
			String bodyClassName = (String) keyIter.next();
			try {
				ISuperVO[] bodyVOS = aggVO.getChildren((Class<? extends ISuperVO>) Class
						.forName(bodyClassName));
				if (ArrayUtil.isEmpty(bodyVOS)) {
					ValidationFailure vf = new ValidationFailure();
					String tabName = (String) getBodyC_E().get(bodyClassName);
					if ("default".equals(tabName)) {
						vf.setMessage(NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("pmpub_0", "04801000-0000"));

					} else {

						vf.setMessage(tabName
								+ NCLangRes4VoTransl.getNCLangRes().getStrByID(
										"pmpub_0", "04801000-0001"));
					}

					vfE.addValidationFailure(vf);
				}
			} catch (ClassNotFoundException e) {
				ExceptionUtils.asBusinessRuntimeException(NCLangRes4VoTransl
						.getNCLangRes().getStrByID("pmpub_0", "04801000-0002"));
			}
		}

		if (vfE.getFailures().size() > 0) {
			throw vfE;
		}
	}

	public void setBodyC_E(Map<String, String> bodyC_E) {
		this.bodyC_E = bodyC_E;
	}

	public Map<String, String> getBodyC_E() {
		return this.bodyC_E;
	}
}