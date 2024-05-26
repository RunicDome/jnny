package nc.impl.bd.material.assign;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import nc.bs.bd.assignservice.AssignBaseService;
import nc.bs.bd.assignservice.multiorg.MultiOrgReturnValueCombUtil;
import nc.bs.bd.material.marorg.IMarOrgService;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfo;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.component.RemoteProcessComponetFactory;
import nc.bs.framework.execute.ThreadFactoryManager;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.itf.bd.material.assign.IMaterialAssignService;
import nc.itf.bd.pub.assign.AssignStatus;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.md.model.IBean;
import nc.vo.bd.assign.AssignStatusVO;
import nc.vo.bd.errorlog.ErrLogResult;
import nc.vo.bd.errorlog.ErrLogReturnValue;
import nc.vo.bd.errorlog.ErrorLogUtil;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.pub.sqlutil.BDSqlInUtil;
import nc.vo.corg.CostRegionVO;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.util.BDPKLockUtil;
import nc.vo.util.SqlWhereUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
public class MaterialAssignServiceImpl extends AssignBaseService implements
		IMaterialAssignService {
	private BaseDAO baseDAO = null;

	private static final int MAX_NUM_MAR = 300000;

	private static final int MAX_NUM_ORG = 100;
	private IMarOrgService marOrgService;

	public MaterialAssignServiceImpl() {
		super("c7dc0ccd-8872-4eee-8882-160e8f49dfad", "pk_marbasclass");
	}

	public ErrLogReturnValue assignMaterialByCondition(
			final String[] funcPermissionOrgIDs, String whereCondition,
			final AssignStatus assignStatus, final String[] targets)
			throws BusinessException {
		final String wherePart = appendFuncPermissionCondition(whereCondition,
				funcPermissionOrgIDs);

		final InvocationInfo info = getInvocationInfo();
		final String logUtilPK = generateNewPk();

		ExecutorService executorService = Executors.newFixedThreadPool(2,
				ThreadFactoryManager.newThreadFactory());

		CompletionService<ErrLogReturnValue> service = new ExecutorCompletionService(
				executorService);

		service.submit(new Callable() {
			public ErrLogReturnValue call() throws Exception {
				RemoteProcessComponetFactory factory = (RemoteProcessComponetFactory) NCLocator
						.getInstance().lookup("RemoteProcessComponetFactory");

				try {
					MaterialAssignServiceImpl.setInvocationInfoWithLogUtilPK(
							info, logUtilPK);
					if (factory != null) {
						factory.preProcess();
					}

					ErrLogReturnValue multiorgValue = MaterialAssignServiceImpl.this
							.marOrgAssignByCondition(funcPermissionOrgIDs,
									wherePart, assignStatus, targets);

					if (factory != null)
						factory.postProcess();
					RemoteProcessComponetFactory newFactory;
					return multiorgValue;
				} catch (Exception ex) {
					RemoteProcessComponetFactory newFactory = (RemoteProcessComponetFactory) NCLocator
							.getInstance().lookup(
									"RemoteProcessComponetFactory");

					if (newFactory != null) {
						newFactory.postErrorProcess(ex);
					}
					Logger.error(ex.getMessage(), ex);
					throw ex;
				} finally {
					RemoteProcessComponetFactory newFactory = (RemoteProcessComponetFactory) NCLocator
							.getInstance().lookup(
									"RemoteProcessComponetFactory");

					if (newFactory != null) {
						newFactory.clearThreadScopePostProcess();
					}
				}
			}
		});
		service.submit(new Callable() {
			public ErrLogReturnValue call() throws Exception {
				RemoteProcessComponetFactory factory = (RemoteProcessComponetFactory) NCLocator
						.getInstance().lookup("RemoteProcessComponetFactory");

				try {
					MaterialAssignServiceImpl.setInvocationInfoWithLogUtilPK(
							info, logUtilPK);
					if (factory != null) {
						factory.preProcess();
					}
					ErrLogReturnValue multiorgValue = ((IMaterialAssignService) NCLocator
							.getInstance().lookup(IMaterialAssignService.class))
							.assignByCondition(wherePart, assignStatus, targets);

					if (factory != null)
						factory.postProcess();
					RemoteProcessComponetFactory newFactory;
					return multiorgValue;
				} catch (Exception ex) {
					RemoteProcessComponetFactory newFactory = (RemoteProcessComponetFactory) NCLocator
							.getInstance().lookup(
									"RemoteProcessComponetFactory");

					if (newFactory != null) {
						newFactory.postErrorProcess(ex);
					}
					Logger.error(ex.getMessage(), ex);
					throw ex;
				} finally {
					RemoteProcessComponetFactory newFactory = (RemoteProcessComponetFactory) NCLocator
							.getInstance().lookup(
									"RemoteProcessComponetFactory");

					if (newFactory != null)
						newFactory.clearThreadScopePostProcess();
				}
			}
		});
		int taskCount = 0;
		Future<ErrLogReturnValue> f = null;
		List<ErrLogReturnValue> list = new ArrayList();
		List<Exception> exceptionList = new ArrayList();
		while (taskCount < 2) {
			try {
				f = service.take();
				Object obj = f.get();
				if ((obj instanceof ErrLogReturnValue)) {
					list.add((ErrLogReturnValue) obj);
				}
			} catch (Exception e) {
				Logger.error(e);
				if (f != null) {
					f.cancel(true);
				}
				exceptionList.add(e);
			}
			taskCount++;
		}
		executorService.shutdownNow();
		if (!CollectionUtils.isEmpty(exceptionList)) {
			Exception exception = (Exception) exceptionList.get(0);
			BusinessException e = new BusinessException(exception.getMessage());
			e.setStackTrace(exception.getStackTrace());
			RemoteProcessComponetFactory factory = (RemoteProcessComponetFactory) NCLocator
					.getInstance().lookup("RemoteProcessComponetFactory");

			if (factory != null) {
				factory.postErrorProcess(exception);
				factory.clearThreadScopePostProcess();
			}
			throw e;
		}
		return MultiOrgReturnValueCombUtil.combineReturnValue(
				(ErrLogReturnValue) list.get(0),
				(ErrLogReturnValue) list.get(1), logUtilPK);
	}

	private ErrLogReturnValue marOrgAssignByCondition(
			String[] funcPermissionOrgIDs, String whereCondition,
			AssignStatus assignStatus, String[] targets)
			throws BusinessException {
		String countSql = "select  count(1) from "
				+ MaterialVO.getDefaultTableName() + " where " + whereCondition;

		Integer marterialCount = (Integer) getBaseDAO().executeQuery(countSql,
				new ColumnProcessor());
		if ((marterialCount.intValue() <= 300000) && (targets.length <= 100)) {
			return getMarOrgService()
					.assignMaterialByCondition(funcPermissionOrgIDs,
							whereCondition, assignStatus, targets);
		}

		List<ErrLogReturnValue> totalRrrLogReturnValues = new ArrayList();
		int count = targets.length / 100;
		int remain = targets.length % 100;
		for (int i = 0; i < count; i++) {
			int start = i * 100;
			int end = (i + 1) * 100;
			String[] subtargets = getPKOrgsByIndex(start, end, targets);
			ErrLogReturnValue errLogReturnValue = getMarOrgService()
					.assignMaterialByCondition_RequiresNew(
							funcPermissionOrgIDs, whereCondition, assignStatus,
							subtargets);

			totalRrrLogReturnValues.add(errLogReturnValue);
		}
		if (remain != 0) {
			int start = count * 100;
			int end = targets.length;
			String[] subtargets = getPKOrgsByIndex(start, end, targets);
			ErrLogReturnValue errLogReturnValue = getMarOrgService()
					.assignMaterialByCondition_RequiresNew(
							funcPermissionOrgIDs, whereCondition, assignStatus,
							subtargets);

			totalRrrLogReturnValues.add(errLogReturnValue);
		}
		return MultiOrgReturnValueCombUtil
				.combineListReturnValue(totalRrrLogReturnValues);
	}

	public ErrLogReturnValue assignMaterialByPks(String[] pks,
			final String[] targets, final String[] funcPermissionOrgIDs)
			throws BusinessException {
		ErrorLogUtil util = new ErrorLogUtil(getBaseBean().getID(),
				InvocationInfoProxy.getInstance().getUserId(),
				NCLangRes4VoTransl.getNCLangRes().getStrByID("10180advcg",
						"010180advcg0000"), true);

		String[] filterOrgPks = null;
		if (ArrayUtils.isEmpty(funcPermissionOrgIDs)) {
			filterOrgPks = pks;
		} else {
			filterOrgPks = filterFuncPermission(pks, funcPermissionOrgIDs, util);
		}
		final InvocationInfo info = getInvocationInfo();
		final String[] filterPks = filterOrgPks;
		final String logUtilPK = generateNewPk();

		ExecutorService executorService = Executors.newFixedThreadPool(2,
				ThreadFactoryManager.newThreadFactory());

		CompletionService<ErrLogReturnValue> service = new ExecutorCompletionService(
				executorService);

		service.submit(new Callable() {
			public ErrLogReturnValue call() throws Exception {
				RemoteProcessComponetFactory factory = (RemoteProcessComponetFactory) NCLocator
						.getInstance().lookup("RemoteProcessComponetFactory");

				try {
					MaterialAssignServiceImpl.setInvocationInfoWithLogUtilPK(
							info, logUtilPK);
					if (factory != null) {
						factory.preProcess();
					}
					ErrLogReturnValue multiorgValue = MaterialAssignServiceImpl.this
							.marOrgAssignByPks(filterPks, targets,
									funcPermissionOrgIDs);

					if (factory != null)
						factory.postProcess();
					RemoteProcessComponetFactory newFactory;
					return multiorgValue;
				} catch (Exception ex) {
					RemoteProcessComponetFactory newFactory = (RemoteProcessComponetFactory) NCLocator
							.getInstance().lookup(
									"RemoteProcessComponetFactory");

					if (newFactory != null) {
						newFactory.postErrorProcess(ex);
					}
					Logger.error(ex.getMessage(), ex);
					throw ex;
				} finally {
					RemoteProcessComponetFactory newFactory = (RemoteProcessComponetFactory) NCLocator
							.getInstance().lookup(
									"RemoteProcessComponetFactory");

					if (newFactory != null) {
						newFactory.clearThreadScopePostProcess();
					}
				}
			}
		});
		service.submit(new Callable() {
			public ErrLogReturnValue call() throws Exception {
				RemoteProcessComponetFactory factory = (RemoteProcessComponetFactory) NCLocator
						.getInstance().lookup("RemoteProcessComponetFactory");

				try {
					MaterialAssignServiceImpl.setInvocationInfoWithLogUtilPK(
							info, logUtilPK);
					if (factory != null) {
						factory.preProcess();
					}
					ErrLogReturnValue multiorgValue = ((IMaterialAssignService) NCLocator
							.getInstance().lookup(IMaterialAssignService.class))
							.assignByPks(filterPks, targets, true);

					if (factory != null)
						factory.postProcess();
					RemoteProcessComponetFactory newFactory;
					return multiorgValue;
				} catch (Exception ex) {
					RemoteProcessComponetFactory newFactory = (RemoteProcessComponetFactory) NCLocator
							.getInstance().lookup(
									"RemoteProcessComponetFactory");

					if (newFactory != null) {
						newFactory.postErrorProcess(ex);
					}
					Logger.error(ex.getMessage(), ex);
					throw ex;
				} finally {
					RemoteProcessComponetFactory newFactory = (RemoteProcessComponetFactory) NCLocator
							.getInstance().lookup(
									"RemoteProcessComponetFactory");

					if (newFactory != null)
						newFactory.clearThreadScopePostProcess();
				}
			}
		});
		int taskCount = 0;
		Future<ErrLogReturnValue> f = null;
		List<ErrLogReturnValue> list = new ArrayList();
		List<Exception> exceptionList = new ArrayList();
		while (taskCount < 2) {
			try {
				f = service.take();
				Object obj = f.get();
				if ((obj instanceof ErrLogReturnValue)) {
					list.add((ErrLogReturnValue) obj);
				}
			} catch (Exception e) {
				Logger.error(e);
				if (f != null) {
					f.cancel(true);
				}
				exceptionList.add(e);
			}
			taskCount++;
		}
		executorService.shutdownNow();
		if (!CollectionUtils.isEmpty(exceptionList)) {
			Exception exception = (Exception) exceptionList.get(0);
			BusinessException e = new BusinessException(exception.getMessage());
			e.setStackTrace(exception.getStackTrace());
			RemoteProcessComponetFactory factory = (RemoteProcessComponetFactory) NCLocator
					.getInstance().lookup("RemoteProcessComponetFactory");

			if (factory != null) {
				factory.postErrorProcess(exception);
				factory.clearThreadScopePostProcess();
			}
			throw e;
		}
		return MultiOrgReturnValueCombUtil.combineReturnValue(
				(ErrLogReturnValue) list.get(0),
				(ErrLogReturnValue) list.get(1), logUtilPK);
	}

	private ErrLogReturnValue marOrgAssignByPks(String[] filterPks,
			String[] targets, String[] funcPermissionOrgIDs)
			throws BusinessException {
		if ((filterPks.length <= 300000) && (targets.length <= 100)) {
			return getMarOrgService().assignMaterialByPks(filterPks, targets,
					funcPermissionOrgIDs);
		}
		List<ErrLogReturnValue> totalRrrLogReturnValues = new ArrayList();
		int count = targets.length / 100;
		int remain = targets.length % 100;
		for (int i = 0; i < count; i++) {
			int start = i * 100;
			int end = (i + 1) * 100;
			String[] subtargets = getPKOrgsByIndex(start, end, targets);
			ErrLogReturnValue errLogReturnValue = getMarOrgService()
					.assignMaterialByPks_RequiresNew(filterPks, subtargets,
							funcPermissionOrgIDs);

			totalRrrLogReturnValues.add(errLogReturnValue);
		}
		if (remain != 0) {
			int start = count * 100;
			int end = targets.length;
			String[] subtargets = getPKOrgsByIndex(start, end, targets);
			ErrLogReturnValue errLogReturnValue = getMarOrgService()
					.assignMaterialByPks_RequiresNew(filterPks, subtargets,
							funcPermissionOrgIDs);

			totalRrrLogReturnValues.add(errLogReturnValue);
		}
		return MultiOrgReturnValueCombUtil
				.combineListReturnValue(totalRrrLogReturnValues);
	}

	private String[] getPKOrgsByIndex(int start, int end, String[] pk_orgs) {
		List<String> list = new ArrayList();
		for (int i = start; i < end; i++) {
			list.add(pk_orgs[i]);
		}
		return (String[]) list.toArray(new String[0]);
	}

	public void assignMaterialToSelfOrg(MaterialVO vo) throws BusinessException {
		((IMarOrgService) NCLocator.getInstance().lookup(IMarOrgService.class))
				.assignMaterialToSelfOrg(vo);

		ErrorLogUtil util = new ErrorLogUtil(getBaseBean().getID(),
				InvocationInfoProxy.getInstance().getUserId(),
				NCLangRes4VoTransl.getNCLangRes().getStrByID("10180advcg",
						"010180advcg0000"), false);

		assignByPks(new String[] { vo.getPrimaryKey() },
				groupTargetPks(new String[] { vo.getPk_org() }, false), util);

		ErrLogResult result = util.getErrLogResult();
		if (result.getErrorMessagegNum() > 0) {
			throw new BusinessException(result.toString());
		}
	}

	public void assignMaterialForPf(String pk_material, String pk_org,
			String[] funcPermissionOrgIDs) throws BusinessException {
		((IMarOrgService) NCLocator.getInstance().lookup(IMarOrgService.class))
				.assignMaterialByPks(new String[] { pk_material },
						new String[] { pk_org }, funcPermissionOrgIDs);

		ErrorLogUtil util = new ErrorLogUtil(getBaseBean().getID(),
				InvocationInfoProxy.getInstance().getUserId(),
				NCLangRes4VoTransl.getNCLangRes().getStrByID("10180advcg",
						"010180advcg0000"), false);

		assignByPks(new String[] { pk_material },
				groupTargetPks(new String[] { pk_org }, false), util);

		ErrLogResult result = util.getErrLogResult();
		if (result.getErrorMessagegNum() > 0) {
			throw new BusinessException(result.toString());
		}
	}

	public void cancelAssignMaterial(MaterialVO vo) throws BusinessException {
		((IMarOrgService) NCLocator.getInstance().lookup(IMarOrgService.class))
				.cancelAssignMaterial(vo);
		ErrorLogUtil util = new ErrorLogUtil(getBaseBean().getID(),
				InvocationInfoProxy.getInstance().getUserId(),
				NCLangRes4VoTransl.getNCLangRes().getStrByID("10180advcg",
						"010180advcg0003"), true);

		cancelAssignByPks(new String[] { vo.getPrimaryKey() },
				groupTargetPks(null, false), util);

		ErrLogResult result = util.getErrLogResult();
		if (result.getErrorMessagegNum() > 0) {
			throw new BusinessException(result.toString());
		}
	}

	public ErrLogReturnValue cancelAssignMaterialByCondition(
			final String[] funcPermissionOrgIDs, String whereCondition,
			final AssignStatus assignStatus, final String[] targets)
			throws BusinessException {
		final String wherePart = appendFuncPermissionCondition(whereCondition,
				funcPermissionOrgIDs);

		final InvocationInfo info = getInvocationInfo();
		final String logUtilPK = generateNewPk();

		ExecutorService executorService = Executors.newFixedThreadPool(2,
				ThreadFactoryManager.newThreadFactory());

		CompletionService<ErrLogReturnValue> service = new ExecutorCompletionService(
				executorService);

		service.submit(new Callable() {
			public ErrLogReturnValue call() throws Exception {
				RemoteProcessComponetFactory factory = (RemoteProcessComponetFactory) NCLocator
						.getInstance().lookup("RemoteProcessComponetFactory");

				try {
					MaterialAssignServiceImpl.setInvocationInfoWithLogUtilPK(
							info, logUtilPK);
					if (factory != null) {
						factory.preProcess();
					}

					ErrLogReturnValue multiorgValue = ((IMarOrgService) NCLocator
							.getInstance().lookup(IMarOrgService.class))
							.cancelAssignMaterialByCondition(
									funcPermissionOrgIDs, wherePart,
									assignStatus, targets);

					if (factory != null)
						factory.postProcess();
					RemoteProcessComponetFactory newFactory;
					return multiorgValue;
				} catch (Exception ex) {
					RemoteProcessComponetFactory newFactory = (RemoteProcessComponetFactory) NCLocator
							.getInstance().lookup(
									"RemoteProcessComponetFactory");

					if (newFactory != null) {
						newFactory.postErrorProcess(ex);
					}
					Logger.error(ex.getMessage(), ex);
					throw ex;
				} finally {
					RemoteProcessComponetFactory newFactory = (RemoteProcessComponetFactory) NCLocator
							.getInstance().lookup(
									"RemoteProcessComponetFactory");

					if (newFactory != null) {
						newFactory.clearThreadScopePostProcess();
					}
				}
			}
		});
		service.submit(new Callable() {
			public ErrLogReturnValue call() throws Exception {
				RemoteProcessComponetFactory factory = (RemoteProcessComponetFactory) NCLocator
						.getInstance().lookup("RemoteProcessComponetFactory");

				try {
					MaterialAssignServiceImpl.setInvocationInfoWithLogUtilPK(
							info, logUtilPK);
					if (factory != null) {
						factory.preProcess();
					}
					ErrLogReturnValue returnValue = ((IMaterialAssignService) NCLocator
							.getInstance().lookup(IMaterialAssignService.class))
							.cancelAssignByCondition(wherePart, assignStatus,
									targets);

					if (factory != null)
						factory.postProcess();
					RemoteProcessComponetFactory newFactory;
					return returnValue;
				} catch (Exception ex) {
					RemoteProcessComponetFactory newFactory = (RemoteProcessComponetFactory) NCLocator
							.getInstance().lookup(
									"RemoteProcessComponetFactory");

					if (newFactory != null) {
						newFactory.postErrorProcess(ex);
					}
					Logger.error(ex.getMessage(), ex);
					throw ex;
				} finally {
					RemoteProcessComponetFactory newFactory = (RemoteProcessComponetFactory) NCLocator
							.getInstance().lookup(
									"RemoteProcessComponetFactory");

					if (newFactory != null)
						newFactory.clearThreadScopePostProcess();
				}
			}
		});
		int taskCount = 0;
		Future<ErrLogReturnValue> f = null;
		List<ErrLogReturnValue> list = new ArrayList();
		List<Exception> exceptionList = new ArrayList();
		while (taskCount < 2) {
			try {
				f = service.take();
				Object obj = f.get();
				if ((obj instanceof ErrLogReturnValue)) {
					list.add((ErrLogReturnValue) obj);
				}
			} catch (Exception e) {
				Logger.error(e);
				if (f != null) {
					f.cancel(true);
				}
				exceptionList.add(e);
			}
			taskCount++;
		}
		executorService.shutdownNow();
		if (!CollectionUtils.isEmpty(exceptionList)) {
			Exception exception = (Exception) exceptionList.get(0);
			BusinessException e = new BusinessException(exception.getMessage());
			e.setStackTrace(exception.getStackTrace());
			RemoteProcessComponetFactory factory = (RemoteProcessComponetFactory) NCLocator
					.getInstance().lookup("RemoteProcessComponetFactory");

			if (factory != null) {
				factory.postErrorProcess(exception);
				factory.clearThreadScopePostProcess();
			}
			throw e;
		}
		return MultiOrgReturnValueCombUtil.combineReturnValue(
				(ErrLogReturnValue) list.get(0),
				(ErrLogReturnValue) list.get(1), logUtilPK);
	}

	public ErrLogReturnValue cancelAssignMaterialByPks(String[] pks,
			final String[] targets, final String[] funcPermissionOrgIDs)
			throws BusinessException {
		ErrorLogUtil util = new ErrorLogUtil(getBaseBean().getID(),
				InvocationInfoProxy.getInstance().getUserId(),
				NCLangRes4VoTransl.getNCLangRes().getStrByID("10180advcg",
						"010180advcg0000"), true);

		String[] filterOrgPks = null;
		if (ArrayUtils.isEmpty(funcPermissionOrgIDs)) {
			filterOrgPks = pks;
		} else {
			filterOrgPks = filterFuncPermission(pks, funcPermissionOrgIDs, util);
		}
		final InvocationInfo info = getInvocationInfo();
		final String[] filterPks = filterOrgPks;
		final String logUtilPK = generateNewPk();

		ExecutorService executorService = Executors.newFixedThreadPool(2,
				ThreadFactoryManager.newThreadFactory());

		CompletionService<ErrLogReturnValue> service = new ExecutorCompletionService(
				executorService);

		service.submit(new Callable() {
			public ErrLogReturnValue call() throws Exception {
				RemoteProcessComponetFactory factory = (RemoteProcessComponetFactory) NCLocator
						.getInstance().lookup("RemoteProcessComponetFactory");

				try {
					MaterialAssignServiceImpl.setInvocationInfoWithLogUtilPK(
							info, logUtilPK);
					if (factory != null) {
						factory.preProcess();
					}

					ErrLogReturnValue multiorgValue = ((IMarOrgService) NCLocator
							.getInstance().lookup(IMarOrgService.class))
							.cancelAssignMaterialByPks(filterPks, targets,
									funcPermissionOrgIDs);

					if (factory != null)
						factory.postProcess();
					RemoteProcessComponetFactory newFactory;
					return multiorgValue;
				} catch (Exception ex) {
					RemoteProcessComponetFactory newFactory = (RemoteProcessComponetFactory) NCLocator
							.getInstance().lookup(
									"RemoteProcessComponetFactory");

					if (newFactory != null) {
						newFactory.postErrorProcess(ex);
					}
					Logger.error(ex.getMessage(), ex);
					throw ex;
				} finally {
					RemoteProcessComponetFactory newFactory = (RemoteProcessComponetFactory) NCLocator
							.getInstance().lookup(
									"RemoteProcessComponetFactory");

					if (newFactory != null) {
						factory.clearThreadScopePostProcess();
					}
				}
			}
		});
		service.submit(new Callable() {
			public ErrLogReturnValue call() throws Exception {
				RemoteProcessComponetFactory factory = (RemoteProcessComponetFactory) NCLocator
						.getInstance().lookup("RemoteProcessComponetFactory");

				try {
					MaterialAssignServiceImpl.setInvocationInfoWithLogUtilPK(
							info, logUtilPK);
					if (factory != null) {
						factory.preProcess();
					}
					ErrLogReturnValue returnValue = ((IMaterialAssignService) NCLocator
							.getInstance().lookup(IMaterialAssignService.class))
							.cancelAssignByPks(filterPks, targets, true);

					if (factory != null)
						factory.postProcess();
					RemoteProcessComponetFactory newFactory;
					return returnValue;
				} catch (Exception ex) {
					RemoteProcessComponetFactory newFactory = (RemoteProcessComponetFactory) NCLocator
							.getInstance().lookup(
									"RemoteProcessComponetFactory");

					if (newFactory != null) {
						newFactory.postErrorProcess(ex);
					}
					Logger.error(ex.getMessage(), ex);
					throw ex;
				} finally {
					RemoteProcessComponetFactory newFactory = (RemoteProcessComponetFactory) NCLocator
							.getInstance().lookup(
									"RemoteProcessComponetFactory");

					if (newFactory != null)
						factory.clearThreadScopePostProcess();
				}
			}
		});
		int taskCount = 0;
		Future<ErrLogReturnValue> f = null;
		List<ErrLogReturnValue> list = new ArrayList();
		List<Exception> exceptionList = new ArrayList();
		while (taskCount < 2) {
			try {
				f = service.take();
				Object obj = f.get();
				if ((obj instanceof ErrLogReturnValue)) {
					list.add((ErrLogReturnValue) obj);
				}
			} catch (Exception e) {
				Logger.error(e);
				if (f != null) {
					f.cancel(true);
				}
				exceptionList.add(e);
			}
			taskCount++;
		}
		executorService.shutdownNow();
		if (!CollectionUtils.isEmpty(exceptionList)) {
			Exception exception = (Exception) exceptionList.get(0);
			BusinessException e = new BusinessException(exception.getMessage());
			e.setStackTrace(exception.getStackTrace());
			RemoteProcessComponetFactory factory = (RemoteProcessComponetFactory) NCLocator
					.getInstance().lookup("RemoteProcessComponetFactory");

			if (factory != null) {
				factory.postErrorProcess(exception);
				factory.clearThreadScopePostProcess();
			}
			throw e;
		}
		return MultiOrgReturnValueCombUtil.combineReturnValue(
				(ErrLogReturnValue) list.get(0),
				(ErrLogReturnValue) list.get(1), logUtilPK);
	}

	public ErrLogReturnValue copyAssignMaterialByPk(MaterialVO vo,
			String pk_source, boolean assignToSelf) throws BusinessException {
		((IMarOrgService) NCLocator.getInstance().lookup(IMarOrgService.class))
				.copyAssignMaterialByPk(vo, pk_source, assignToSelf);

		BDPKLockUtil.lockString(new String[] { vo.getPrimaryKey(), pk_source });
		ErrorLogUtil util = new ErrorLogUtil(getBaseBean().getID(),
				InvocationInfoProxy.getInstance().getUserId(),
				NCLangRes4VoTransl.getNCLangRes().getStrByID("10180advcg",
						"010180advcg0037"), false);

		copyAssignByPk(vo, pk_source, util);
		if (assignToSelf) {
			assignByPks(new String[] { vo.getPrimaryKey() },
					groupTargetPks(new String[] { vo.getPk_org() }, false),
					util);
		}

		return util.getErrLogReturnValue(vo, -1);
	}

	public AssignStatusVO queryAssignStatusVO(String[] pk_relations)
			throws BusinessException {
		return queryAssignStatus(pk_relations);
	}

	public String queryAssignVOByOrg(String[] pk_orgs, String tablename)
			throws BusinessException {
		return queryAssignDataByOrg(pk_orgs, tablename);
	}

	public String[] queryMaterialPksByCondition(String[] funcPermissionOrgIDs,
			String whereCondition, AssignStatus assignStatus, String[] targets)
			throws BusinessException {
		String wherePart = appendFuncPermissionCondition(whereCondition,
				funcPermissionOrgIDs);
		return queryPksByCondition(wherePart, assignStatus, targets);
	}

	private String appendFuncPermissionCondition(String condition,
			String[] funcPermissionOrgIDs) {
		String visibleCondtion = BDSqlInUtil.formInSQLWithoutAnd("pk_org",
				funcPermissionOrgIDs, false);

		return new SqlWhereUtil(condition).and(visibleCondtion).getSQLWhere();
	}

	private String[] filterFuncPermission(String[] pks,
			String[] funcPermissionOrgIDs, ErrorLogUtil util)
			throws BusinessException {
		String cond = BDSqlInUtil
				.formInSQLWithoutAnd("pk_material", pks, false);
		cond = cond + " "
				+ BDSqlInUtil.formInSQL("pk_org", funcPermissionOrgIDs, false);
		String sql = "select pk_material from "
				+ MaterialVO.getDefaultTableName() + " where " + cond;

		List<String> col = (List) getBaseDAO().executeQuery(sql,
				new ColumnListProcessor());
		return (String[]) col.toArray(new String[0]);
	}

	private BaseDAO getBaseDAO() {
		if (this.baseDAO == null) {
			this.baseDAO = new BaseDAO();
		}
		return this.baseDAO;
	}

	private String getFuncPermissionErrorMsg(MaterialVO vo) {
		return NCLangRes4VoTransl.getNCLangRes().getStrByID("10140mag",
				"010140mag0125", null, new String[] { vo.getCode() });
	}

	public IMarOrgService getMarOrgService() {
		if (this.marOrgService == null) {
			this.marOrgService = ((IMarOrgService) NCLocator.getInstance()
					.lookup(IMarOrgService.class));
		}
		return this.marOrgService;
	}
}