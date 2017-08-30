package com.shuishou.digitalmenu.common.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shuishou.digitalmenu.account.models.IUserDataAccessor;
import com.shuishou.digitalmenu.account.models.UserData;
import com.shuishou.digitalmenu.common.models.ConfirmCode;
import com.shuishou.digitalmenu.common.models.Desk;
import com.shuishou.digitalmenu.common.models.IConfirmCodeDataAccessor;
import com.shuishou.digitalmenu.common.models.IDeskDataAccessor;
import com.shuishou.digitalmenu.common.models.IPrinterDataAccessor;
import com.shuishou.digitalmenu.common.models.Printer;
import com.shuishou.digitalmenu.common.views.GetConfirmCodeResult;
import com.shuishou.digitalmenu.common.views.GetDeskResult;
import com.shuishou.digitalmenu.common.views.GetDeskWithIndentResult;
import com.shuishou.digitalmenu.common.views.GetPrinterResult;
import com.shuishou.digitalmenu.indent.models.IIndentDataAccessor;
import com.shuishou.digitalmenu.indent.models.Indent;
import com.shuishou.digitalmenu.log.models.LogData;
import com.shuishou.digitalmenu.log.services.ILogService;
import com.shuishou.digitalmenu.views.GridResult;
import com.shuishou.digitalmenu.views.Result;

@Service
public class CommonService implements ICommonService {
	@Autowired
	private IConfirmCodeDataAccessor confirmCodeDA;
	
	@Autowired
	private IDeskDataAccessor deskDA;
	
	@Autowired
	private IUserDataAccessor userDA;
	
	@Autowired
	private ILogService logService;
	
	@Autowired
	private IPrinterDataAccessor printerDA;
	
	@Autowired
	private IIndentDataAccessor indentDA;
	
	private DateFormat df = new SimpleDateFormat("HH:mm:ss");

	@Override
	@Transactional
	public GridResult checkConfirmCode(String code) {
		ConfirmCode cc = confirmCodeDA.getCode();
		if (code.equals(cc.getCode()))
			return new GridResult(Result.OK, true);
		return new GridResult(Result.FAIL, false);
	}

	@Override
	@Transactional
	public GetConfirmCodeResult getConfirmCode() {
		ConfirmCode cc = confirmCodeDA.getCode();
		String code = cc == null ? "" : cc.getCode();
		return new GetConfirmCodeResult(Result.OK, true, code);
	}

	@Override
	@Transactional
	public GridResult saveConfirmCode(long userId, String code) {
		ConfirmCode cc = new ConfirmCode();
		cc.setCode(code);
		confirmCodeDA.deleteCode();
		confirmCodeDA.saveCode(cc);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_CONFIRMCODE.toString(), "User "+ selfUser + " change confirm code " + code);

		return new GridResult(Result.OK, true);
	}

	@Override
	@Transactional
	public GetDeskResult getDesks() {
		List<Desk> desks = deskDA.queryDesks();
		GetDeskResult result = new GetDeskResult(Result.OK, true);
		result.desks = new ArrayList<GetDeskResult.Desk>(desks.size());
		for(int i = 0; i<desks.size(); i++){
			GetDeskResult.Desk d = new GetDeskResult.Desk(desks.get(i).getId(), desks.get(i).getName());
			result.desks.add(d);
		}
		return result;
	}

	@Override
	@Transactional
	public GridResult saveDesk(long userId, String deskname) {
		Desk desk = new Desk();
		desk.setName(deskname);
		deskDA.insertDesk(desk);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_DESK.toString(), "User "+ selfUser + " add desk "+ deskname);

		return new GridResult(Result.OK, true);
	}

	@Override
	@Transactional
	public GridResult updateDesk(long userId, int id, String name) {
		Desk desk = deskDA.getDeskById(id);
		String oldname = desk.getName();
		if (desk == null)
			return new GridResult("No desk, id = "+ id, false);
		desk.setName(name);
		deskDA.updateDesk(desk);
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_DESK.toString(), "User "+ selfUser + " update desk name from "+ oldname + " to "+ name);

		return new GridResult(Result.OK, true);
	}

	@Override
	@Transactional
	public GridResult deleteDesk(long userId, int id) {
		Desk desk = deskDA.getDeskById(id);
		if (desk == null)
			return new GridResult("No desk found, id = "+ id, false);
		deskDA.deleteDesk(desk);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_DESK.toString(), "User "+ selfUser + " delete desk " + desk.getName());

		return new GridResult(Result.OK, true);
	}

	@Override
	@Transactional
	public GetPrinterResult getPrinters() {
		List<Printer> printers = printerDA.queryPrinters();
		GetPrinterResult result = new GetPrinterResult(Result.OK, true);
		result.printers = new ArrayList<GetPrinterResult.Printer>();
		for (int i = 0; i < printers.size(); i++) {
			GetPrinterResult.Printer p = new GetPrinterResult.Printer();
			p.id = printers.get(i).getId();
			p.name = printers.get(i).getName();
			p.printerName = printers.get(i).getPrinterName();
			p.copy = printers.get(i).getCopy();
			p.printStyle = printers.get(i).getPrintStyle();
			result.printers.add(p);
		}
		return result;
	}

	@Override
	@Transactional
	public GridResult savePrinter(long userId, String name, String printerName, int copy, byte printStyle) {
		Printer p = new Printer();
		p.setName(name);
		p.setPrinterName(printerName);
		p.setCopy(copy);
		p.setPrintStyle(printStyle);
		printerDA.insertPrinter(p);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_PRINTER.toString(), "User "+ selfUser + " add printer "+ printerName);

		return new GridResult(Result.OK, true);
	}

	@Override
	@Transactional
	public GridResult deletePrinter(long userId, int id) {
		Printer p = printerDA.getPrinterById(id);
		if (p == null)
			return new GridResult("No printer found, id = "+ id, false);
		printerDA.deletePrinter(p);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CHANGE_PRINTER.toString(), "User "+ selfUser + " delete printer " + p.getName());

		return new GridResult(Result.OK, true);
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
		List<GetDeskWithIndentResult.Desk> deskinfos = new ArrayList<GetDeskWithIndentResult.Desk>();
		for (int i = 0; i < desks.size(); i++) {
			Desk desk = desks.get(i);
			GetDeskWithIndentResult.Desk deskinfo = new GetDeskWithIndentResult.Desk();
			deskinfo.id = desk.getId();
			deskinfo.name = desk.getName();
			if (desk.getMergeTo() != null)
				deskinfo.name = desk.getName() + " 并桌到 " + desk.getMergeTo().getName();
			for(Indent indent : indents){
				if (indent.getDeskName().equals(desk.getName())){
					deskinfo.indentId = indent.getId();
					deskinfo.price = indent.getTotalPrice();
					deskinfo.customerAmount = indent.getCustomerAmount();
					deskinfo.startTime = df.format(indent.getStartTime());
					break;
				}
			}
			deskinfos.add(deskinfo);
		}
		return new GetDeskWithIndentResult(Result.OK, true, deskinfos);
	}

}
