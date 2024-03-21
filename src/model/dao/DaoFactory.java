package model.dao;

import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {
	
	//operações estaticas statics para instaciar os daos.
	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC();
	}

}
