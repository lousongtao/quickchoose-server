package com.shuishou.digitalmenu.indent.services;

import org.json.JSONArray;

import com.shuishou.digitalmenu.indent.views.GetIndentDetailResult;
import com.shuishou.digitalmenu.indent.views.GetIndentResult;
import com.shuishou.digitalmenu.indent.views.MakeOrderResult;
import com.shuishou.digitalmenu.indent.views.OperateIndentResult;
import com.shuishou.digitalmenu.views.GridResult;

public interface IIndentService {
	MakeOrderResult saveIndent(String confirmCode, JSONArray jsonOrder, int deskid, int customerAmount);
	GetIndentResult queryIndent(int start, int limit, String sstarttime, String sendtime, String status, String deskname, String orderby);
	GetIndentDetailResult queryIndentDetail(int indentId);
	OperateIndentResult operateIndent(int userId, int indentId, byte operationType, double paidPrice, byte payWay);
	OperateIndentResult operateIndentDetail(int userId, int indentId, int dishId, int indentDetailId, int amount, byte operateType);
	GridResult printIndent(int userId, int indentId);
	GridResult printIndentDetail(int userId, int indentDetailId);
}
