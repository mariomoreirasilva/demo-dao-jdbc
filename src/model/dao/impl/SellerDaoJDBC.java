package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	
	private Connection conn;
	
	

	public SellerDaoJDBC(Connection conn) {
		
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES "
					+"(?, ?, ?, ?, ?)  ",
					Statement.RETURN_GENERATED_KEYS
					);
					
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartament().getId());
			int linhasInseridas =  st.executeUpdate();
			if (linhasInseridas > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			//não inseriu.
			else {
				throw new DbException("Erro inexperado. Não foi inserido linhas.");
				
			}			
				
			}catch(SQLException e) {
				throw new DbException(e.getMessage());
			}finally {
				DB.closeStatement(st);		
			}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ? "					
					);
					
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartament().getId());
			st.setInt(6, obj.getId());
			st.executeUpdate();
				
			}catch(SQLException e) {
				throw new DbException(e.getMessage());
			}finally {
				DB.closeStatement(st);		
			}
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
			st.setInt(1, id);
			st.executeUpdate();
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+" WHERE seller.Id = ? "
					);
					
			st.setInt(1, id);
			rs = st.executeQuery();
			//testar se o result set trouxe valores. se sair do if é semelhante ao recordcount = 0
			if (rs.next()) {
				//Department dep = new Department();
				//dep.setId(rs.getInt("DepartmentId"));
				//dep.setName(rs.getString("DepName"));
				//melhor colocar uma função que instancia os objetos. Imagina um result set grande ia ter muitos set...
				Department dep = instantiateDepartment(rs);
				/*
				Seller obj = new Seller();
				obj.setId(rs.getInt("Id"));
				obj.setName(rs.getString("Name"));
				obj.setEmail(rs.getString("Email"));
				obj.setbaseSalary(rs.getDouble("BaseSalary"));
				obj.setBirthDate(rs.getDate("BirthDate"));
				obj.setDepartament(dep);
				*/
				Seller obj = instantiateSeller(rs,dep);
				return obj;
				
			}
			else {
				return null;
			}
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setbaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartament(dep);
		return obj;
		
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		//não precisa tratar com try (deu erro antes do throws de propagação acima)
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					
					+ "ORDER BY Name");
					
			
			rs = st.executeQuery();
			//testar se o result set trouxe valores. se sair do if é semelhante ao recordcount = 0
			//este método retorna várias linhas. Então será uma lista. Percorrer o resutlset com while
			List<Seller> list = new ArrayList<>();
			//instanciar somente um departamento, do jeito correto no pdf. O resultset só tem 1 departamento(veja o parâmetro)
			//para isso usar o objeto Map. Ele não aceita repetição e guarda um de cada vez
			Map<Integer , Department> map = new HashMap<>();
			
			while (rs.next()) {	
				Department dep = map.get(rs.getInt("DepartmentId"));
				if(map != null) {
				   dep = instantiateDepartment(rs);
				//agora guarda a variavel no map para a proxima verificação
				map.put(rs.getInt("DepartmentId"), dep);
				}
				Seller obj = instantiateSeller(rs,dep);
				list.add(obj);				
			}
			return list;
						
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}
	

	@Override
	public List<Seller> findByDepartment(Department department) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name");
					
			st.setInt(1, department.getId());
			rs = st.executeQuery();
			//testar se o result set trouxe valores. se sair do if é semelhante ao recordcount = 0
			//este método retorna várias linhas. Então será uma lista. Percorrer o resutlset com while
			List<Seller> list = new ArrayList<>();
			//instanciar somente um departamento, do jeito correto no pdf. O resultset só tem 1 departamento(veja o parâmetro)
			//para isso usar o objeto Map. Ele não aceita repetição e guarda um de cada vez
			Map<Integer , Department> map = new HashMap<>();
			
			while (rs.next()) {	
				Department dep = map.get(rs.getInt("DepartmentId"));
				if(map != null) {
				   dep = instantiateDepartment(rs);
				//agora guarda a variavel no map para a proxima verificação
				map.put(rs.getInt("DepartmentId"), dep);
				}
				Seller obj = instantiateSeller(rs,dep);
				list.add(obj);				
			}
			return list;
						
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}
	

}
