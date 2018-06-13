package com.shuishou.digitalmenu.indent.services;

import java.util.Date;

import org.json.JSONArray;

import com.shuishou.digitalmenu.DataCheckException;
import com.shuishou.digitalmenu.indent.views.MakeOrderResult;
import com.shuishou.digitalmenu.indent.views.OperateIndentResult;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;

public interface IIndentService {
	MakeOrderResult saveIndent(String confirmCode, JSONArray jsonOrder, int deskid, int customerAmount, String comments) throws DataCheckException;
	ObjectResult splitIndent(int userId, String confirmCode, JSONArray jsonOrder, int originIndentId, double paidPrice, 
			double paidCash, String payWay, String discountTemplate, String memberCard, String memberPassword) throws DataCheckException;
	ObjectListResult queryIndent(int start, int limit, String sstarttime, String sendtime, String status, String deskname, 
			String orderby, String orderbydesc);
//	GetIndentDetailResult queryIndentDetail(int indentId);
//	OperateIndentResult operateIndent(int userId, int indentId, byte operationType, double paidPrice, double paidCash, String payWay, 
//			String memberCard, String memberPassword) throws DataCheckException;
	OperateIndentResult doPayIndent(int userId, int indentId, double paidPrice, double paidCash, String payWay, String discountTemplate, String memberCard, String memberPassword) throws DataCheckException;
	OperateIndentResult doCancelIndent(int userId, int indentId);
	OperateIndentResult doRefundIndent(int userId, int indentId);
	OperateIndentResult operateIndentDetail(int userId, int indentId, int dishId, int indentDetailId, int amount, byte operateType) throws DataCheckException;
	MakeOrderResult addDishToIndent(int deskId, JSONArray jsonOrder) throws DataCheckException;
	ObjectResult printIndent(int userId, int indentId);
//	ObjectResult printIndentDetail(int userId, int indentDetailId);
	ObjectResult clearDesk(int userId, int deskId);
	ObjectResult changeDesks(int userId, int deskId1, int deskId2);
}
