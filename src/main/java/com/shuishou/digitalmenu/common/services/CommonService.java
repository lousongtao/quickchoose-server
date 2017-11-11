package com.shuishou.digitalmenu.common.services;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.PrinterName;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.account.models.IUserDataAccessor;
import com.shuishou.digitalmenu.account.models.UserData;
import com.shuishou.digitalmenu.common.models.Configs;
import com.shuishou.digitalmenu.common.models.Desk;
import com.shuishou.digitalmenu.common.models.DiscountTemplate;
import com.shuishou.digitalmenu.common.models.IConfigsDataAccessor;
import com.shuishou.digitalmenu.common.models.IDeskDataAccessor;
import com.shuishou.digitalmenu.common.models.IDiscountTemplateDataAccessor;
import com.shuishou.digitalmenu.common.models.IPayWayDataAccessor;
import com.shuishou.digitalmenu.common.models.IPrinterDataAccessor;
import com.shuishou.digitalmenu.common.models.PayWay;
import com.shuishou.digitalmenu.common.models.Printer;
import com.shuishou.digitalmenu.common.views.GetDeskResult;
import com.shuishou.digitalmenu.common.views.GetDeskWithIndentResult;
import com.shuishou.digitalmenu.indent.models.IIndentDataAccessor;
import com.shuishou.digitalmenu.indent.models.Indent;
import com.shuishou.digitalmenu.indent.models.IndentDetail;
import com.shuishou.digitalmenu.log.models.LogData;
import com.shuishou.digitalmenu.log.services.ILogService;
import com.shuishou.digitalmenu.menu.models.Category2;
import com.shuishou.digitalmenu.menu.models.Category2DataAccessor;
import com.shuishou.digitalmenu.menu.models.Category2Printer;
import com.shuishou.digitalmenu.menu.models.ICategory2DataAccessor;
import com.shuishou.digitalmenu.menu.models.ICategory2PrinterDataAccessor;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;
import com.shuishou.digitalmenu.views.Result;


@Service
public class CommonService implements ICommonService {
	private Logger logger = Logger.getLogger(CommonService.class);
	
	@Autowired
	private IConfigsDataAccessor configsDA;
	
	@Autowired
	private IDeskDataAccessor deskDA;
	
	@Autowired
	private IUserDataAccessor userDA;
	
	@Autowired
	private ILogService logService;
	
	@Autowired
	private IPrinterDataAccessor printerDA;
	
	@Autowired
	private IDiscountTemplateDataAccessor discountTemplateDA;
	
	@Autowired
	private IIndentDataAccessor indentDA;
	
	@Autowired
	private IPayWayDataAccessor payWayDA;
	
	@Autowired
	private ICategory2DataAccessor category2DA;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private ICategory2PrinterDataAccessor category2PrinterDA;
	
	@Override
	@Transactional
	public ObjectResult queryConfigMap(){
		List<Configs> configs = configsDA.queryConfigs();
		HashMap<String, String> maps = new HashMap<>();
		if (configs != null){
			for(Configs c : configs){
				maps.put(c.getName(), c.getValue());
			}
		}
		return new ObjectResult(Result.OK, true, maps);
	}
	
//	@Override
//	@Transactional
//	public CheckConfirmCodeResult checkConfirmCode(String code) {
//		ConfirmCode cc = confirmCodeDA.getCode();
//		if (code.equals(cc.getCode()))
//			return new CheckConfirmCodeResult(Result.OK, true, true);
//		return new CheckConfirmCodeResult(Result.FAIL, false, false);
//	}
//
//	@Override
//	@Transactional
//	public GetConfirmCodeResult getConfirmCode() {
//		ConfirmCode cc = confirmCodeDA.getCode();
//		String code = cc == null ? "" : cc.getCode();
//		return new GetConfirmCodeResult(Result.OK, true, code);
//	}

	@Override
	@Transactional
	public ObjectResult saveConfirmCode(long userId, String code) {
		Configs c = configsDA.getConfigsByName(ConstantValue.CONFIGS_CONFIRMCODE);
		if (c == null){
			c = new Configs();
			c.setName(ConstantValue.CONFIGS_CONFIRMCODE);
		}
		c.setValue(code);
		configsDA.saveConfigs(c);
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_CONFIRMCODE.toString(), "User "+ selfUser + " change confirm code " + code);

		return new ObjectResult(Result.OK, true);
	}
	
	@Override
	@Transactional
	public ObjectResult saveOpenCashdrawerCode(long userId, String code) {
		Configs c = configsDA.getConfigsByName(ConstantValue.CONFIGS_OPENCASHDRAWERCODE);
		if (c == null){
			c = new Configs();
			c.setName(ConstantValue.CONFIGS_OPENCASHDRAWERCODE);
		}
		c.setValue(code);
		configsDA.saveConfigs(c);
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_OPENCASHDRAWERCODE.toString(), "User "+ selfUser + " change open cashdrawer " + code);

		return new ObjectResult(Result.OK, true);
	}

	@Override
	@Transactional
	public GetDeskResult getDesks() {
		List<Desk> desks = deskDA.queryDesks();
		GetDeskResult result = new GetDeskResult(Result.OK, true);
		result.data = new ArrayList<GetDeskResult.Desk>(desks.size());
		for(int i = 0; i<desks.size(); i++){
			GetDeskResult.Desk d = new GetDeskResult.Desk();
			d.id = desks.get(i).getId();
			d.name = desks.get(i).getName();
			d.sequence = desks.get(i).getSequence();
			if (desks.get(i).getMergeTo() != null)
				d.mergeTo = desks.get(i).getMergeTo().getName();
			result.data.add(d);
		}
		return result;
	}

	@Override
	@Transactional
	public ObjectResult saveDesk(long userId, String deskname, int sequence) {
		Desk desk = new Desk();
		desk.setName(deskname);
		desk.setSequence(sequence);
		deskDA.insertDesk(desk);
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_DESK.toString(), "User "+ selfUser + " add desk "+ deskname);

		return new ObjectResult(Result.OK, true);
	}

	@Override
	@Transactional
	public ObjectResult updateDesk(long userId, int id, String name, int sequence) {
		Desk desk = deskDA.getDeskById(id);
		String oldname = desk.getName();
		if (desk == null)
			return new ObjectResult("No desk, id = "+ id, false);
		desk.setName(name);
		desk.setSequence(sequence);
		deskDA.updateDesk(desk);
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_DESK.toString(), "User "+ selfUser + " update desk name from "+ oldname + " to "+ name);

		return new ObjectResult(Result.OK, true);
	}

	@Override
	@Transactional
	public ObjectResult deleteDesk(long userId, int id) {
		Desk desk = deskDA.getDeskById(id);
		if (desk == null)
			return new ObjectResult("No desk found, id = "+ id, false);
		deskDA.deleteDesk(desk);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_DESK.toString(), "User "+ selfUser + " delete desk " + desk.getName());

		return new ObjectResult(Result.OK, true);
	}

	@Override
	@Transactional
	public ObjectListResult getPrinters() {
		List<Printer> printers = printerDA.queryPrinters();
		return new ObjectListResult(Result.OK, true, printers);
	}

	@Override
	@Transactional
	public ObjectResult savePrinter(long userId, String name, String printerName, int type) {
		Printer p = new Printer();
		p.setName(name);
		p.setPrinterName(printerName);
		p.setType(type);
//		p.setCopy(copy);
//		p.setPrintStyle(printStyle);
		printerDA.insertPrinter(p);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_PRINTER.toString(), "User "+ selfUser + " add printer "+ printerName);

		return new ObjectResult(Result.OK, true);
	}

	@Override
	@Transactional
	public ObjectResult deletePrinter(long userId, int id) {
		Printer p = printerDA.getPrinterById(id);
		if (p == null)
			return new ObjectResult("No printer found, id = "+ id, false);
		printerDA.deletePrinter(p);
		//clear the connection with Category2
		List<Category2> c2s = category2DA.getAllCategory2();
		for(Category2 c2 : c2s ){
			if (c2.getCategory2PrinterList()!= null){
				for(Category2Printer cp : c2.getCategory2PrinterList()){
					if (cp.getPrinter().getId() == id){
						category2PrinterDA.delete(cp);
						c2.getCategory2PrinterList().remove(cp);
						category2DA.save(c2);
					}
					
				}
				
			}
		}
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_PRINTER.toString(), "User "+ selfUser + " delete printer " + p.getName());

		return new ObjectResult(Result.OK, true);
	}
	
	@Override
	@Transactional
	public ObjectResult testPrinterConnection(int id) {
		Printer p = printerDA.getPrinterById(id);
		if (p == null)
			return new ObjectResult("No printer found, id = "+ id, false);
		PrinterJob pj = PrinterJob.getPrinterJob();

		HashAttributeSet hs = new HashAttributeSet();
		hs.add(new PrinterName(p.getPrinterName(), null));
		// 获取打印服务对象
		PrintService[] printService = PrintServiceLookup.lookupPrintServices(null, hs);
		if (printService.length > 0) {
			PrintService ps = printService[0];
			try {
				pj.setPrintService(ps);
				PageFormat pf = new PageFormat();
				Paper paper = new Paper();
				paper.setImageableArea(0, 0, 200, 200);
				pf.setPaper(paper);
				pj.setPrintable(new Printable(){

					@Override
					public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
						Graphics2D g2 = (Graphics2D) g;
						if (pageIndex > 0)
							return NO_SUCH_PAGE;
						String txt = ConstantValue.DFYMDHMS.format(new Date());
						
						g2.drawString("This is a print test", 0, 20);
						g2.drawString("Current time is " + txt, 0, 40);
						
						return PAGE_EXISTS;
					}
					
				}, pf);
				pj.print();
			} catch (PrinterException e) {
				logger.error("", e);
				e.printStackTrace();
				return new ObjectResult(e.getMessage(), false);
			}
		}
		return new ObjectResult(Result.OK, true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@Transactional
	public GetDeskWithIndentResult getDesksWithIndents() {
		List<Desk> desks = deskDA.queryDesks();
		Collections.sort(desks, new Comparator(){

			@Override
			public int compare(Object o1, Object o2) {
				return ((Desk)o1).getId() - ((Desk)o2).getId();
			}});
		List<Indent> indents = indentDA.getUnpaidIndent();
		List<GetDeskWithIndentResult.DeskWithIndent> deskinfos = new ArrayList<>();
		for (int i = 0; i < desks.size(); i++) {
			Desk desk = desks.get(i);
			GetDeskWithIndentResult.DeskWithIndent deskinfo = new GetDeskWithIndentResult.DeskWithIndent();
			deskinfo.id = desk.getId();
			deskinfo.name = desk.getName();
			if (desk.getMergeTo() != null)
				deskinfo.mergeTo =desk.getMergeTo().getName();
			for(Indent indent : indents){
				if (indent.getDeskName().equals(desk.getName())){
					Hibernate.initialize(indent);
					Hibernate.initialize(indent.getItems());
					deskinfo.indent = indent;
//					deskinfo.price = indent.getTotalPrice();
//					deskinfo.customerAmount = indent.getCustomerAmount();
//					deskinfo.startTime = ConstantValue.DFYMDHMS.format(indent.getStartTime());
					break;
				}
			}
			deskinfos.add(deskinfo);
		}
		return new GetDeskWithIndentResult(Result.OK, true, deskinfos);
	}

	@Override
	@Transactional
	public GetDeskWithIndentResult mergeDesks(int userId, int mainDeskId, String subDesksId) {
		String[] subDeskIds = subDesksId.split("/");
		String subDesksName = "";
		List<Desk> subDesks = new ArrayList<Desk>();
		for(String sid : subDeskIds){
			subDesks.add(deskDA.getDeskById(Integer.parseInt(sid)));
		}
		Desk mainDesk = deskDA.getDeskById(mainDeskId);
		List<Indent> mainIndents = indentDA.getIndents(0, 100, null, null, new Byte[]{ConstantValue.INDENT_STATUS_OPEN}, mainDesk.getName(), null, null);
		Indent mainIndent = null;
		if (!mainIndents.isEmpty()){
			mainIndent = mainIndents.get(0);
		}
		List<Indent> subDesksIndents = new ArrayList<Indent>();
		for(Desk desk : subDesks){
			subDesksIndents.addAll(indentDA.getIndents(0, 100, null, null, new Byte[]{ConstantValue.INDENT_STATUS_OPEN}, desk.getName(), null, null));
		}
		//flag the merge info for sub desks
		for(Desk desk : subDesks){
			if (subDesksName.length() > 0)
				subDesksName += ",";
			subDesksName += desk.getName();
			desk.setMergeTo(mainDesk);
			deskDA.updateDesk(desk);
		}
		//if there are not indents on sub tables, no need to merge indent
		if (subDesksIndents.isEmpty()){
			//do nothing
		} else if (!subDesksIndents.isEmpty()){
			if (mainIndent == null){
				mainIndent = new Indent();
				mainIndent.setDeskName(mainDesk.getName());
				mainIndent.setStartTime(Calendar.getInstance().getTime());
				mainIndent.setCustomerAmount(0);
				int sequence = indentDA.getMaxSequenceToday() + 1;
				mainIndent.setDailySequence(sequence);
			} 
			double totalprice = mainIndent.getTotalPrice();
			int customers = mainIndent.getCustomerAmount();
			for(Indent subIndent : subDesksIndents){
				List<IndentDetail> details = subIndent.getItems();
				for(IndentDetail detail : details){
					detail.setIndent(mainIndent);
					mainIndent.addItem(detail);
				}
				totalprice += subIndent.getTotalPrice();
				customers += subIndent.getCustomerAmount();
				subIndent.setItems(null);
				indentDA.delete(subIndent);
			}
			mainIndent.setTotalPrice(Double.parseDouble(new DecimalFormat("0.00").format(totalprice)));
			mainIndent.setCustomerAmount(customers);
			indentDA.save(mainIndent);
		}
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.MERGETABLE.toString(), "User "+ selfUser + " merge tables [ " + subDesksName + " ] to table [ "+ mainDesk.getName()+" ]");

		//prepare return data
		List<GetDeskWithIndentResult.DeskWithIndent> deskinfos = new ArrayList<>();
		GetDeskWithIndentResult.DeskWithIndent deskinfo = new GetDeskWithIndentResult.DeskWithIndent();
		deskinfo.id = mainDesk.getId();
		deskinfo.name = mainDesk.getName();
		if (mainIndent != null){
			Hibernate.initialize(mainIndent);
			Hibernate.initialize(mainIndent.getItems());
			deskinfo.indent = mainIndent;
//			deskinfo.price = mainIndent.getTotalPrice();
//			deskinfo.customerAmount = mainIndent.getCustomerAmount();
//			deskinfo.startTime = ConstantValue.DFYMDHMS.format(mainIndent.getStartTime());
		}
		
		deskinfos.add(deskinfo);
		for(Desk desk : subDesks){
			deskinfo = new GetDeskWithIndentResult.DeskWithIndent();
			deskinfo.id = desk.getId();
			deskinfo.name = desk.getName();
			deskinfo.mergeTo = mainDesk.getName();
			deskinfos.add(deskinfo);
		}
		return new GetDeskWithIndentResult(Result.OK, true, deskinfos);
	}

	@Override
	@Transactional
	public ObjectListResult getDiscountTemplates() {
		List<DiscountTemplate> templates = discountTemplateDA.queryDiscountTemplates();
		return new ObjectListResult(Result.OK, true, templates);
	}

	@Override
	@Transactional
	public ObjectResult saveDiscountTemplate(long userId, String name, double rate) {
		DiscountTemplate t = new DiscountTemplate();
		t.setName(name);
		t.setRate(rate);
		discountTemplateDA.insertDiscountTemplate(t);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_DISCOUNTTEMPLATE.toString(), 
				"User "+ selfUser + " add discount template "+ name);

		return new ObjectResult(Result.OK, true);
	}

	@Override
	@Transactional
	public ObjectResult deleteDiscountTemplate(long userId, int id) {
		DiscountTemplate t = discountTemplateDA.getDiscountTemplateById(id);
		if (t == null)
			return new ObjectResult("No Discount Template found, id = "+ id, false);
		discountTemplateDA.deleteDiscountTemplate(t);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_DISCOUNTTEMPLATE.toString(), "User "+ selfUser + " delete discount template " + t.getName());

		return new ObjectResult(Result.OK, true);
	}

	@Override
	public ObjectResult uploadErrorLog(String machineCode, MultipartFile logfile) {
		String fileName = logfile.getOriginalFilename();
		String pathName = request.getSession().getServletContext().getRealPath("/")+"../" + ConstantValue.CATEGORY_ERRORLOG;
		File path = new File(pathName);
		if (!path.exists())
			path.mkdirs();
		File f = new File(pathName + "/" + fileName);
		try {
			logfile.transferTo(f);
		} catch (IllegalStateException | IOException e) {
			return new ObjectResult(Result.FAIL, false);
		}
		return new ObjectResult(Result.OK, true);
	}

	@Override
	@Transactional
	public ObjectListResult getPayWays() {
		List<PayWay> listPayWay = payWayDA.queryPayWays();
		return new ObjectListResult(Result.OK, true, listPayWay);
	}

	@Override
	@Transactional
	public ObjectResult savePayWay(long userId, String name) {
		PayWay payWay = new PayWay();
		payWay.setName(name);
		payWayDA.insertPayWay(payWay);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_PAYWAY.toString(), 
				"User "+ selfUser + " add pay way "+ name);

		return new ObjectResult(Result.OK, true);
	}

	@Override
	@Transactional
	public ObjectResult deletePayWay(long userId, int id) {
		PayWay payWay = payWayDA.getPayWayById(id);
		if (payWay == null)
			return new ObjectResult("No Payway found, id = "+ id, false);
		payWayDA.deletePayWay(payWay);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_PAYWAY.toString(), "User "+ selfUser + " delete payway " + payWay.getName());

		return new ObjectResult(Result.OK, true);
	}

}
