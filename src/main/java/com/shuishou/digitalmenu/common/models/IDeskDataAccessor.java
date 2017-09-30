package com.shuishou.digitalmenu.common.models;

import java.io.Serializable;
import java.util.List;

public interface IDeskDataAccessor {

	List<Desk> queryDesks();
	
	Desk getDeskById(int id);
	
	Desk getDeskByName(String name);
	
	Serializable insertDesk(Desk desk);
	
	void updateDesk(Desk desk);
	
	void deleteDesk(Desk desk);
}
