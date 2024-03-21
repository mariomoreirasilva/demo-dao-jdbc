package application;

import java.util.Date;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		
		Department obj = new Department(1,"livros");
		
		Seller seller = new Seller(123, "mario", "mario@desban.org.br", new Date(), 3000.0, obj);
		
		System.out.println(seller);
		//para instaciar um SellerDaoJDB que são as implementações exemplo abaixo. Não tem new pois os procedimentos são estaticos.
		SellerDao sellerTeste =  DaoFactory.createSellerDao();
		
		

	}

}
