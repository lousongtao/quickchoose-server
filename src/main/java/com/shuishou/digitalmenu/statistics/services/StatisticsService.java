package com.shuishou.digitalmenu.statistics.services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.indent.models.IIndentDataAccessor;
import com.shuishou.digitalmenu.indent.models.Indent;
import com.shuishou.digitalmenu.indent.models.IndentDetail;
import com.shuishou.digitalmenu.menu.models.Dish;
import com.shuishou.digitalmenu.menu.models.IDishDataAccessor;
import com.shuishou.digitalmenu.statistics.views.StatItem;
import com.shuishou.digitalmenu.views.ObjectResult;
import com.shuishou.digitalmenu.views.Result;

@Service
public class StatisticsService implements IStatisticsService{
	private final static Logger logger = LoggerFactory.getLogger(StatisticsService.class);
	
	@Autowired
	private IDishDataAccessor dishDA;
	
	@Autowired
	private IIndentDataAccessor indentDA;
	
	private DecimalFormat doubleFormat = new DecimalFormat("0.00");
	
	@Override
	@Transactional
	public ObjectResult statistics(int userId, Date startDate, Date endDate, int dimension, int sellGranularity,
			int sellByPeriod) {
		List<Indent> indents = indentDA.getIndentsByPaidTime(startDate, endDate);
		if (indents == null || indents.isEmpty())
			return new ObjectResult("No order paid in this period", false);
		ObjectResult result = new ObjectResult(Result.OK, true);
		if (dimension == ConstantValue.STATISTICS_DIMENSTION_PAYWAY){
			ArrayList<StatItem> stats = statisticsPayway(indents);
			result.data = stats;
		} else if (dimension == ConstantValue.STATISTICS_DIMENSTION_SELL){
			if (sellGranularity != ConstantValue.STATISTICS_SELLGRANULARITY_BYDISH
					&& sellGranularity != ConstantValue.STATISTICS_SELLGRANULARITY_BYCATEGORY2
					&& sellGranularity != ConstantValue.STATISTICS_SELLGRANULARITY_BYCATEGORY1){
				return new ObjectResult("Wrong param of Sell Granularity.", false);
			}
			ArrayList<StatItem> stats = statisticsSell(indents, sellGranularity);
			result.data = stats;
		} else if (dimension == ConstantValue.STATISTICS_DIMENSTION_PERIODSELL){
			if (sellByPeriod != ConstantValue.STATISTICS_PERIODSELL_PERDAY
					&& sellByPeriod != ConstantValue.STATISTICS_PERIODSELL_PERHOUR){
				return new ObjectResult("Wrong param of Sell By Period.", false);
			}
			ArrayList<StatItem> stats = statisticsSellByPeriod(indents, sellByPeriod, startDate, endDate);
			result.data = stats;
		}
		//format double value
		if (result.data != null){
			for(StatItem si : (ArrayList<StatItem>)result.data){
				si.paidPrice = Double.parseDouble(doubleFormat.format(si.paidPrice));
				si.totalPrice = Double.parseDouble(doubleFormat.format(si.totalPrice));
				si.weight = Double.parseDouble(doubleFormat.format(si.weight));
			}
		}
		return result;
	}
	
	/**
	 * 首先根据sellByPeriod把时间段按粒度划分开, 然后遍历indent的列表, 找到对应的时间段的数据, 添加进去
	 * @param indents
	 * @param sellByPeriod
	 * @return
	 */
	@Transactional
	private ArrayList<StatItem> statisticsSellByPeriod(List<Indent> indents, int sellByPeriod, Date startDate, Date endDate){
		ArrayList<StatItem> stats = new ArrayList<>();
		//initial time period into map
		HashMap<String, StatItem> mapPeriod = new HashMap<>();
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		if (sellByPeriod == ConstantValue.STATISTICS_PERIODSELL_PERDAY){
			do{
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				Date time1 = c.getTime();
				StatItem si = new StatItem(ConstantValue.DFYMD.format(time1) + " - " + ConstantValue.DFWEEK.format(time1));
				mapPeriod.put(si.itemName, si);
				c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
			} while(c.getTime().getTime() <= endDate.getTime());
		} else if (sellByPeriod == ConstantValue.STATISTICS_PERIODSELL_PERHOUR){
			c.set(Calendar.HOUR_OF_DAY, 0);
			do{
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				Date time1 = c.getTime();
				c.set(Calendar.MINUTE, 59);
				c.set(Calendar.SECOND, 59);
				Date time2 = c.getTime();
				StatItem si = new StatItem(ConstantValue.DFYMDHMS.format(time1) + " - " + ConstantValue.DFHMS.format(time2) + " - " + ConstantValue.DFWEEK.format(time1));
				mapPeriod.put(si.itemName, si);
				c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + 1);
			} while (c.getTime().getTime() <= endDate.getTime());
		}
		//start loop indents list
		for(Indent indent : indents){
			if (indent.getEndTime() == null)
				continue;
			double weight = 0;
			for(IndentDetail detail : indent.getItems()){
				weight += detail.getWeight();
			}
			if (sellByPeriod == ConstantValue.STATISTICS_PERIODSELL_PERDAY){
				c.setTime(indent.getEndTime());
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				Date time1 = c.getTime();
				StatItem si = mapPeriod.get(ConstantValue.DFYMD.format(time1) + " - " + ConstantValue.DFWEEK.format(time1));
				si.weight += weight;
				si.soldAmount += 1;
				si.totalPrice += indent.getTotalPrice();
				si.paidPrice += indent.getPaidPrice();
			} else if (sellByPeriod == ConstantValue.STATISTICS_PERIODSELL_PERHOUR){
				c.setTime(indent.getEndTime());
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				Date time1 = c.getTime();
				c.set(Calendar.MINUTE, 59);
				c.set(Calendar.SECOND, 59);
				Date time2 = c.getTime();
				StatItem si = mapPeriod.get(ConstantValue.DFYMDHMS.format(time1) + " - " + ConstantValue.DFHMS.format(time2) + " - " + ConstantValue.DFWEEK.format(time1));
				si.weight += weight;
				si.soldAmount += 1;
				si.totalPrice += indent.getTotalPrice();
				si.paidPrice += indent.getPaidPrice();
			}
		}
		Iterator<StatItem> its = mapPeriod.values().iterator();
		while(its.hasNext()){
			stats.add(its.next());
		}
		return stats;
	}
	
	/**
	 * 根据统计粒度, 讲每个indent的ditail下对应的dish/category进行分类统计, 如果对应的dish/category已经删除, 则记录为UNFOUND
	 * @param indents
	 * @param sellGranularity
	 * @return
	 */
	@Transactional
	private ArrayList<StatItem> statisticsSell(List<Indent> indents, int sellGranularity){
		ArrayList<StatItem> stats = new ArrayList<>();
		HashMap<String, StatItem> mapSell = new HashMap<>();
		//first define one UNFOUND for those dish/category1/category2 cannot be found
		String sUnfound = "UNFOUND";
		StatItem ssUnfound = new StatItem(sUnfound);
		mapSell.put(sUnfound, ssUnfound);
		for(Indent indent : indents){
			List<IndentDetail> details = indent.getItems();
			for(IndentDetail detail : details){
				Dish dish = dishDA.getDishById(detail.getDishId());
				if (sellGranularity == ConstantValue.STATISTICS_SELLGRANULARITY_BYDISH){
					if (dish == null){
						ssUnfound.soldAmount += detail.getAmount();
						ssUnfound.totalPrice += detail.getDishPrice();
						ssUnfound.weight += detail.getWeight();
					} else {
						StatItem ss = mapSell.get(dish.getFirstLanguageName());
						if (ss == null){
							ss = new StatItem(dish.getFirstLanguageName());
							mapSell.put(dish.getFirstLanguageName(), ss);
						}
						accumulateIndentDetailInfo(ss, dish, detail);
					}
				} else if (sellGranularity == ConstantValue.STATISTICS_SELLGRANULARITY_BYCATEGORY2){
					if (dish == null || dish.getCategory2() == null){
						ssUnfound.soldAmount += detail.getAmount();
						ssUnfound.totalPrice += detail.getDishPrice();
						ssUnfound.weight += detail.getWeight();
					} else {
						StatItem ss = mapSell.get(dish.getCategory2().getFirstLanguageName());
						if (ss == null){
							ss = new StatItem(dish.getCategory2().getFirstLanguageName());
							mapSell.put(dish.getCategory2().getFirstLanguageName(), ss);
						}
						accumulateIndentDetailInfo(ss, dish, detail);
					}
				} else if (sellGranularity == ConstantValue.STATISTICS_SELLGRANULARITY_BYCATEGORY1){
					if (dish == null || dish.getCategory2() == null || dish.getCategory2().getCategory1() == null){
						ssUnfound.soldAmount += detail.getAmount();
						ssUnfound.totalPrice += detail.getDishPrice();
						ssUnfound.weight += detail.getWeight();
					} else {
						StatItem ss = mapSell.get(dish.getCategory2().getCategory1().getFirstLanguageName());
						if (ss == null){
							ss = new StatItem(dish.getCategory2().getCategory1().getFirstLanguageName());
							mapSell.put(dish.getCategory2().getCategory1().getFirstLanguageName(), ss);
						}
						accumulateIndentDetailInfo(ss, dish, detail);
					}
				} 
			}
		}
		//remove unfound if it is 0
		if (ssUnfound.soldAmount == 0){
			mapSell.remove(sUnfound);
		}
		Iterator<StatItem> its = mapSell.values().iterator();
		while(its.hasNext()){
			stats.add(its.next());
		}
		return stats;
	}
	
	@Transactional
	private void accumulateIndentDetailInfo(StatItem ss, Dish dish, IndentDetail detail){
		ss.soldAmount += detail.getAmount();
		ss.weight += detail.getWeight();
		if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT){
			ss.totalPrice += detail.getDishPrice();
		} else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT){
			ss.totalPrice += detail.getDishPrice() * detail.getWeight();
		}
	}
	
	@Transactional
	private ArrayList<StatItem> statisticsPayway(List<Indent> indents){
		ArrayList<StatItem> stats = new ArrayList<>();
		HashMap<String, StatItem> mapPayway = new HashMap<>();
		for(Indent indent : indents){
			if (indent.getPayWay() == null)
				continue;
			StatItem sp = mapPayway.get(indent.getPayWay());
			if (sp == null){
				sp = new StatItem(indent.getPayWay());
				mapPayway.put(indent.getPayWay(), sp);
			}
			sp.paidPrice += indent.getPaidPrice();
			sp.soldAmount++;
		}
		Iterator<StatItem> its = mapPayway.values().iterator();
		while(its.hasNext()){
			stats.add(its.next());
		}
		return stats;
	}
}
