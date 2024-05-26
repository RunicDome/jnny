package nc.bs.scmpub.util;

import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.res.Variable;

public class SCMDataAccessUtils {
	private static final int NOT_LIMIT = -1;
	private DataAccessUtils utils;
	private int maxRow = 50000;

	public SCMDataAccessUtils() {
		this(Variable.getMaxQueryCount());
	}

	public SCMDataAccessUtils(int maxRow) {
		this.utils = new DataAccessUtils();
		// this.maxRow = maxRow;
		this.maxRow = 50000;
		if (maxRow == -1) {
			this.utils.setMaxRows(maxRow);
		} else {
			this.utils.setMaxRows(maxRow + 1);
		}
	}

	public IRowSet query(String sql) {
		IRowSet rowSet = this.utils.query(sql);

		if (this.maxRow >= 0) {
			checkResult(rowSet);
		}

		return rowSet;
	}

	private void checkResult(IRowSet rowSet) {
		if (rowSet.size() > this.maxRow) {
			String hint = NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"pubapp_0", "0pubapp-0268", null,
					new String[] { "" + Variable.getMaxQueryCount() });

			ExceptionUtils.wrappBusinessException(hint);
		}
	}
}
