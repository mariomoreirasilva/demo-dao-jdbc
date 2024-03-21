package model.dao;

import db.DB;
import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {
	
	//opera��es estaticas statics para instaciar os daos.
	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC(DB.getConnection());
	}

}
