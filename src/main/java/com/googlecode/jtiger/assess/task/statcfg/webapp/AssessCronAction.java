package com.googlecode.jtiger.assess.task.statcfg.webapp;

import java.util.Date;

import org.apache.commons.lang.xwork.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.googlecode.jtiger.assess.AssessConstants;
import com.googlecode.jtiger.modules.quartz.model.Cron;
import com.googlecode.jtiger.modules.quartz.service.CronManager;
import com.googlecode.jtiger.modules.quartz.webapp.CronAction;
import com.ibm.icu.util.Calendar;

/**
 * <pre>
 * 用于完成考核时间Cron的Action
 * 默认拟定每月第一天的零点零分零秒执行统计
 * 用户可以通过日期控件选择月日时分秒来设定.
 * cron表达式: 0 0 0 1 * ?
 * 位置  时间域名  允许值 允许的特殊字符
 * 1   秒                  0-59    , - * /
 * 2   分钟             0-59    , - * /
 * 3   小时            0-23    , - * /
 * 4   日期           1-31    , - * ? / L W C
 * 5   月份           1-12    , - * /
 * 6   星期           1-7     , - * ? / L C #
 * 7   年(可选) 空值1970-2099 , - * /
 * @author DELPHI
 *</pre>
 */
@SuppressWarnings("serial")
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AssessCronAction extends CronAction {
	/**用户选定的时间*/
	private Date cronDate;
	/**Cron实体*/
	private Cron assessCron = new Cron();
	
	@Autowired
	private CronManager cronManager;

	/**
	 * 按月考核的时间设置
	 * 
	 * @return
	 */
	public String indexMonthly() {
		assessCron = cronManager.getCronByMarker(AssessConstants.CRON_ASSESS);
		StringBuffer buf = new StringBuffer();
		if (assessCron != null && StringUtils.isNotBlank(assessCron.getCron())) {
			String[] ary = assessCron.getCron().split(" ");
			buf.append("每月 ").append(ary[3] + "日    ").append(ary[2] + "点")
					.append(ary[1] + "分").append(ary[0] + "秒");
		}

		getRequest().setAttribute("cronStr", buf.toString());

		return "indexMonthly";
	}

	/**
	 * 每天考核的时间
	 * 
	 * @return
	 */
	public String indexDaily() {
		assessCron = cronManager
				.getCronByMarker(AssessConstants.CRON_ASSESS_DAILY);
		StringBuffer buf = new StringBuffer();
		if (assessCron != null && StringUtils.isNotBlank(assessCron.getCron())) {
			String[] ary = assessCron.getCron().split(" ");
			buf.append("每天 ").append(ary[2] + "点").append(ary[1] + "分").append(
					ary[0] + "秒");
		}

		getRequest().setAttribute("cronStr", buf.toString());

		return "indexDaily";
	}
	/**
	 * 保存操作
	 */
	public String save() {
		// SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		assessCron = cronManager.get(assessCron.getId());
		//设定用户选择的时间
		Calendar c = Calendar.getInstance();
		c.setTime(cronDate);
		String ss = String.valueOf(c.get(Calendar.SECOND));
		String mm = String.valueOf(c.get(Calendar.MINUTE));
		String hh = String.valueOf(c.get(Calendar.HOUR_OF_DAY) - 1);
		String dd = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		
		//根据用户选择的时间,构建cron表达式
		StringBuffer buf = new StringBuffer();
		String cronType = getRequest().getParameter("cronType");
		if (AssessConstants.CRON_ASSESS.equals(cronType)) {
			buf.append(ss).append(" ").append(mm).append(" ").append(hh)
					.append(" ").append(dd).append(" ").append("*").append(" ")
					.append("?");
		} else {
			buf.append(ss).append(" ").append(mm).append(" ").append(hh)
					.append(" ").append("*").append(" ").append("*")
					.append(" ").append("?");
		}

		assessCron.setCron(buf.toString());
		// assessCron.setMarker(AssessConstants.CRON_ASSESS);
		assessCron.setName(AssessConstants.CRON_ASSESS);

		try {
			cronManager.save(assessCron);
			render("success", "text/plain");
		} catch (Exception e) {
			e.printStackTrace();
			render(e.getMessage(), "text/plain");
		}

		if (AssessConstants.CRON_ASSESS.equals(cronType)) {
			return "monthlySuccess";
		} else {
			return "dailySuccess";
		}
	}

	public Cron getAssessCron() {
		return assessCron;
	}

	public void setAssessCron(Cron assessCron) {
		this.assessCron = assessCron;
	}

	public Date getCronDate() {
		return cronDate;
	}

	public void setCronDate(Date cronDate) {
		this.cronDate = cronDate;
	}
}
