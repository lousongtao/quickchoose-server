package com.shuishou.digitalmenu.common.views;

import java.util.List;

import com.shuishou.digitalmenu.views.GridResult;

public class GetPrinterResult extends GridResult{

	public List<Printer> printers;
	
	public final static class Printer{
		public int id;
		public String name;
		public String printerName;
		public int copy;
		public byte printStyle;
	}
	public GetPrinterResult(String result, boolean success) {
		super(result, success);
	}

}
