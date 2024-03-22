package application;

import java.util.Date;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		
		//Department obj = new Department(1,"livros");
		
		//Seller seller = new Seller(123, "mario", "mario@desban.org.br", new Date(), 3000.0, obj);
		
		//System.out.println(seller);
		//para instaciar um SellerDaoJDB que são as implementações exemplo abaixo. Não tem new pois os procedimentos são estaticos.
		SellerDao sellerTeste =  DaoFactory.createSellerDao();
		System.err.println("=====Teste 1: seller findById======");
		Seller seller = sellerTeste.findById(3);
		System.out.println(seller);
		System.err.println("\n=====Teste 2: seller findByDepartment======");
		Department dep = new Department(2, null);
		List<Seller> list = sellerTeste.findByDepartment(dep); 
		//expressão labda abaixo. 
		list.forEach(System.out::println);
		System.err.println("\n=====Teste 3: seller findAll======");
		
		List<Seller> list2 = sellerTeste.findAll();
		list2.forEach(System.out::println);
		
		System.err.println("\n=====Teste 4: inserir seller======");
		Seller novoEmpregado = new Seller(null, "Mário Fonseca", "mario@desban.org.br", new Date(), 4000.0, dep);
		sellerTeste.insert(novoEmpregado);
		System.out.println("Novo id do empregado = "+novoEmpregado.getId());
		
		System.err.println("\n=====Teste 5: update seller======");
		seller = sellerTeste.findById(1);
		seller.setName("Maria teste");
		sellerTeste.update(seller);
		System.out.println("Atualizado");
		
		System.err.println("\n=====Teste 6: delete seller======");
		sellerTeste.deleteById(2);
		System.out.println("apagou");
		
		

	}

}
