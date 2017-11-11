package com.shuishou.digitalmenu.indent.services;

import org.json.JSONArray;

import com.shuishou.digitalmenu.indent.views.GetIndentDetailResult;
import com.shuishou.digitalmenu.indent.views.GetIndentResult;
import com.shuishou.digitalmenu.indent.views.MakeOrderResult;
import com.shuishou.digitalmenu.indent.views.OperateIndentResult;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;

public interface IIndentService {
	MakeOrderResult saveIndent(String confirmCode, JSONArray jsonOrder, int deskid, int customerAmount, String comments);
	ObjectResult splitIndent(int userId, String confirmCode, JSONArray jsonOrder, int originIndentId, double paidPrice, String payWay, String memberCard);
	ObjectListResult queryIndent(int start, int limit, String sstarttime, String sendtime, String status, String deskname, String orderby, String orderbydesc);
	GetIndentDetailResult queryIndentDetail(int indentId);
	OperateIndentResult operateIndent(int userId, int indentId, byte operationType, double paidPrice, String payWay, String memberCard);
	OperateIndentResult operateIndentDetail(int userId, int indentId, int dishId, int indentDetailId, int amount, byte operateType);
	MakeOrderResult addDishToIndent(int deskId, JSONArray jsonOrder);
	ObjectResult printIndent(int userId, int indentId);
//	ObjectResult printIndentDetail(int userId, int indentDetailId);
	ObjectResult clearDesk(int userId, int deskId);
	ObjectResult changeDesks(int userId, int deskId1, int deskId2);
}
