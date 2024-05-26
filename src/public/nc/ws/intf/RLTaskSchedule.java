package nc.ws.intf;

import nc.vo.pub.SuperVO;

// 和工程推送项目进度
public class RLTaskSchedule extends SuperVO {

	private static final long serialVersionUID = -8154247745967708533L;
	public static final String PK_PROJECT = "pk_project";
	public static final String PK_ORG = "pk_org";
	public static final String ZZMC = "zzmc";
	public static final String PERCENT = "percent";
	public static final String START_DATE = "start_date";
	public static final String TASK_DATE = "task_date";
	public static final String PROJECT_CODE = "project_code";
	public static final String CHECK_DATE = "check_date";
	
	private String pk_project;
	public String getPk_project() {
		return pk_project;
	}

	public void setPk_project(String pk_project) {
		this.pk_project = pk_project;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getZzmc() {
		return zzmc;
	}

	public void setZzmc(String zzmc) {
		this.zzmc = zzmc;
	}

	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getTask_date() {
		return task_date;
	}

	public void setTask_date(String task_date) {
		this.task_date = task_date;
	}

	public String getProject_code() {
		return project_code;
	}

	public void setProject_code(String project_code) {
		this.project_code = project_code;
	}

	public String getCheck_date() {
		return check_date;
	}

	public void setCheck_date(String check_date) {
		this.check_date = check_date;
	}

	private String pk_org;
	private String zzmc;
	private String percent;
	private String start_date;
	private String task_date;
	private String project_code;
	private String check_date;

	public String getTableName() {
		return "RL_TASKSCHEDULE";
	}

}
