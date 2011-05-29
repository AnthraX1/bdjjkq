package com.googlecode.jtiger.assess.transgress.stat.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.jtiger.assess.transgress.stat.StatCondition;
import com.googlecode.jtiger.assess.transgress.stat.dao.StatDao;
import com.googlecode.jtiger.assess.transgress.stat.model.TransgressStat;
import com.googlecode.jtiger.assess.transgress.stat.model.TransgressStatDetail;
import com.googlecode.jtiger.core.service.BaseGenericsManager;

@Service
public class TransgressStatManager extends BaseGenericsManager<TransgressStat> {
	@Autowired
	private StatDao statDao;

	@SuppressWarnings("unchecked")
	public void stat() {
		List<Map<String, Object>> list = statDao.stat1();
		if (CollectionUtils.isNotEmpty(list)) {
			TransgressStat ts = new TransgressStat();
			save(ts);
			for (@SuppressWarnings("unused")
			Map map : list) {
				TransgressStatDetail td = new TransgressStatDetail();
				td.setTransgressStat(ts);
				getDao().save(td);
			}
		}

	}

	public List<Map<String, Object>> stat(StatCondition condition) {

		return statDao.stat(condition);
	}
}