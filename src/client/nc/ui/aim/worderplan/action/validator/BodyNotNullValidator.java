package nc.ui.aim.worderplan.action.validator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nc.bs.uif2.validation.IValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.bs.uif2.validation.ValidationFailure;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pm.util.ArrayUtil;
import nc.vo.pmbd.common.utils.ExceptionUtils;
import nc.vo.pub.ISuperVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

// 工单预案表体非空校验
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class BodyNotNullValidator implements IValidationService {

	private Map<String, String> bodyMap = new HashMap();

	private final String defaultTabName = "default";

	public BodyNotNullValidator() {
	}

	public void validate(Object obj) throws ValidationException {
		AbstractBill aggVO = (AbstractBill) obj;
		Iterator<String> keyIter = getbodyMap().keySet().iterator();
		ValidationException vfE = new ValidationException();
		while (keyIter.hasNext()) {
			String bodyClassName = (String) keyIter.next();
			try {
				ISuperVO[] bodyVOS = aggVO
						.getChildren((Class<? extends ISuperVO>) Class
								.forName(bodyClassName));
				if (ArrayUtil.isEmpty(bodyVOS)) {
					ValidationFailure vf = new ValidationFailure();
					String tabName = (String) getbodyMap().get(bodyClassName);
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

	public void setbodyMap(Map<String, String> bodyMap) {
		this.bodyMap = bodyMap;
	}

	public Map<String, String> getbodyMap() {
		return this.bodyMap;
	}
}