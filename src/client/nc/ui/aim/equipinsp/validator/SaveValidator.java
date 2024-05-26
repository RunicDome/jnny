package nc.ui.aim.equipinsp.validator;

import nc.bs.uif2.validation.IValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.bs.uif2.validation.ValidationFailure;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

// 工单预案保存校验
@SuppressWarnings({ "unused" })
public class SaveValidator implements IValidationService {

	public SaveValidator() {
	}

	public void validate(Object obj) throws ValidationException {
		AbstractBill aggVO = (AbstractBill) obj;
		ValidationException vfE = new ValidationException();
		ValidationFailure vf = new ValidationFailure();
		vf.setMessage("测试保存校验");
		vfE.addValidationFailure(vf);
		if (vfE.getFailures().size() > 0) {
			// throw vfE;
		}
	}
}