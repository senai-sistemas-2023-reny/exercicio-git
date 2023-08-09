package br.com.senai1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import br.com.senai.Funcionario;

public class BaseDados {

	private Connection conexao;
	public BaseDeDados() throws Exception{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			this.conexao = DriverManager.getConnection("jdbc:mysql://localhost/pets","root","root");
		}catch (Exception e) {
			throw new Exception("Ocorreu um erro na conexão");
		}
	}
	public void fecharConexao() {
		try {
			this.conexao.close();
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro no encerramento da conexão");
		}
	}
	public void liberar(PreparedStatement ps) {
		try {
			if (ps != null) {
				ps.close();
			}
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na liberação do cursor");
		}
	}
	public void inserirAnimal(Funcionario funcionario) {
		PreparedStatement psInsert = null;
		try {
			psInsert = conexao.prepareStatement("INSERT INTO Animais "
					+ "(cdAnimal, nmAnimal, tpRaca) VALUES (?,?,?) ");
			psInsert.setInt(1,animal.getCdAnimal());
			psInsert.setString(2, animal.getNmAnimal());
			psInsert.setString(3, animal.getTpRaca());
			psInsert.executeUpdate();
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na inserção do Animal");
		}finally {
			this.liberar(psInsert);
		}
	}
	public void alterarAnimal(Funcionario funcionario) {		
		PreparedStatement psUpdate = null;		
		try {
			this.conexao.setAutoCommit(false);
			psUpdate = conexao.prepareStatement("UPDATE Animais SET "
					+ "nmAnimal = ?, tpRaca = ?"
					+ "WHERE cdAnimal = ? ");
			psUpdate.setString(1, animal.getNmAnimal());
			psUpdate.setString(2, animal.getTpRaca());
			psUpdate.setInt(3, animal.getCdAnimal());
			int qtdeDeLinhasAlteradas = psUpdate.executeUpdate();
			if (qtdeDeLinhasAlteradas <= 1) {
				this.conexao.commit();
			}else {
				//Mais de um animal alterado!!
				this.conexao.rollback();
			}
			this.conexao.setAutoCommit(true);
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na alteração do Animal");
		}finally {
			this.liberar(psUpdate);
		}
	}
	public void excluirAnimalPor(int codigoAnimal) {
		PreparedStatement psDelete = null;
		try {
			this.conexao.setAutoCommit(false);
			psDelete = conexao.prepareStatement("DELETE FROM Animais WHERE cdAnimal = ?");
			psDelete.setInt(1, codigoAnimal);
			int qtdeDeLinhasRemovidas = psDelete.executeUpdate();
			if (qtdeDeLinhasRemovidas <= 1) {
				this.conexao.commit();
			}else {
				// Mais de um animal eliminado!!
				this.conexao.rollback();
			}
			this.conexao.setAutoCommit(true);
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na exclusão do Animal");
		}finally {
			this.liberar(psDelete);
		}
	}
	public Animal localizarAnimalPor(int codigoAnimal) {
		Animal animal = new Animal();
		PreparedStatement psSelect = null;
		try {
			psSelect = conexao.prepareStatement("SELECT cdAnimal, nmAnimal, tpRaca "
					+ "FROM Animais where cdAnimal = ? ");
			psSelect.setInt(1, codigoAnimal);
			ResultSet rs = psSelect.executeQuery();
			while (rs.next()) {
				animal = new Animal(rs.getInt("cdAnimal"),
						rs.getString("nmAnimal"),
						rs.getString("tpRaca"));
			}
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na busca pelo animal!");
		}finally {
			this.liberar(psSelect);
		}
		return animal;
	}
	public String listarAnimais(int codigoAnimal) {
		String retorno = "";
		PreparedStatement psSelect = null;
		try {
			if (codigoAnimal > 0) { // lista 1 unico animal
				psSelect = conexao.prepareStatement("SELECT cdAnimal, nmAnimal, tpRaca "
					+ "FROM Animais where cdAnimal = ? ");
				psSelect.setInt(1, codigoAnimal);
			} else { // Lista todos os animais
				psSelect = conexao.prepareStatement("SELECT cdAnimal, nmAnimal, tpRaca "
						+ "FROM Animais "
						+ "Order by nmAnimal");
			}
			ResultSet rs = psSelect.executeQuery();
			while (rs.next()) {
				retorno+= "Cód.: " + rs.getInt("cdAnimal") + 
						" - Nome: " + rs.getString("nmAnimal") +
						" - Raça: " + rs.getString("tpRaca") +
						"\n";
			}
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na busca pelo animal!");
		}finally {
			this.liberar(psSelect);
		}
		return retorno;
	}

	public List<Animal> listarTodosAnimais(){
		List<Animal> animais = new ArrayList<Animal>();
		PreparedStatement psSelect = null;
		try {
			psSelect = conexao.prepareStatement("SELECT cdAnimal, nmAnimal, tpRaca "
					+ "FROM Animais "
					+ "Order by nmAnimal");
			ResultSet rs = psSelect.executeQuery();
			while (rs.next()) {
				animais.add(new Animal(rs.getInt("cdAnimal"),
										rs.getString("nmAnimal"),
										rs.getString("tpRaca")));
						
			}
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na listagem dos Animais");
		}finally {
			this.liberar(psSelect);
		}
		return animais;
	}
	

	// AGENDA
	public void inserirAgenda(Funcionario funcionario) {
		PreparedStatement psInsert = null;
		try {
			psInsert = conexao.prepareStatement("INSERT INTO Agendas "
					+ "(cdAgenda, dtAgenda, hrAgenda, cdAnimal) VALUES (?,?,?,?) ");
			psInsert.setInt(1, agenda.getCdAgenda());
			psInsert.setString(2, agenda.getDtAgenda());
			psInsert.setString(3, agenda.getHrAgenda());
			psInsert.setInt(4, agenda.getAnimal().getCdAnimal());
			psInsert.executeUpdate();
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na inserção da agenda");
		}finally {
			this.liberar(psInsert);
		}
	}
	public void alterarAgenda(Funcionario funcionario) {		
		PreparedStatement psUpdate = null;		
		try {
			this.conexao.setAutoCommit(false);
			psUpdate = conexao.prepareStatement("UPDATE Agendas SET dtAgenda = ?, "
					+ "hrAgenda = ?, cdAnimal = ? "
					+ "WHERE cdAgenda = ? ");
			psUpdate.setString(1, agenda.getDtAgenda());
			psUpdate.setString(2, agenda.getHrAgenda());
			psUpdate.setInt(3, agenda.getAnimal().getCdAnimal());
			psUpdate.setInt(4, agenda.getCdAgenda());
			int qtdeDeLinhasAlteradas = psUpdate.executeUpdate();
			if (qtdeDeLinhasAlteradas <= 1) {
				this.conexao.commit();
			}else {
				//Mais de uma agenda alterada!!
				this.conexao.rollback();
			}
			this.conexao.setAutoCommit(true);
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na alteração da Agenda!");
		}finally {
			this.liberar(psUpdate);
		}
	}
	
	public void excluirAgendaPor(int codigoAgenda) {
		PreparedStatement psDelete = null;
		try {
			this.conexao.setAutoCommit(false);
			psDelete = conexao.prepareStatement("DELETE FROM Agendas WHERE cdAgenda = ?");
			psDelete.setInt(1, codigoAgenda);
			int qtdeDeLinhasRemovidas = psDelete.executeUpdate();
			if (qtdeDeLinhasRemovidas <= 1) {
				this.conexao.commit();
			}else {
				// Mais de uma agenda eliminada!!
				this.conexao.rollback();
			}
			this.conexao.setAutoCommit(true);
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na exclusão da Agenda! ");
		}finally {
			this.liberar(psDelete);
		}
	}

	public Agenda localizarAgendaPorCodigo(int codigoAgenda) {
		Agenda agenda = new Agenda();
		PreparedStatement psSelect = null;
		try {
			psSelect = conexao.prepareStatement("SELECT cdAgenda, dtAgenda, hrAgenda, cdAnimal "
					+ "FROM Agenda where cdAgenda = ? ");
			psSelect.setInt(1, codigoAgenda);
			ResultSet rs = psSelect.executeQuery();
			while (rs.next()) {
				agenda = new Agenda(rs.getInt("cdAgenda"),
						rs.getString("dtAgenda"),
						rs.getString("hrAgenda"),
						localizarAnimalPor(rs.getInt("cdAnimal")));
			}
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na busca pela agenda!");
		}finally {
			this.liberar(psSelect);
		}
		return agenda;
	}
	public String listarAgendas(String dataAgenda) {
		String retorno = "";
		PreparedStatement psSelect = null;
		try {
			if (dataAgenda.equals("")) { // lista 1 unica data
				psSelect = conexao.prepareStatement("SELECT cdAgenda, dtAgenda, hrAgenda, cdAnimal "
						+ "FROM Agendas Order by dtAgenda, hrAgenda ");
			} else { // Lista todos as agendas
				psSelect = conexao.prepareStatement("SELECT cdAgenda, dtAgenda, hrAgenda, cdAnimal "
						+ "FROM Agendas where dtAgenda = ? "
						+ "Order by dtAgenda, hrAgenda ");
				psSelect.setString(1, dataAgenda);
			}
			ResultSet rs = psSelect.executeQuery();
			while (rs.next()) {
				retorno+= "Cód.: " + rs.getInt("cdAgenda") + 
						" - Data: " + rs.getString("dtAgenda") +
						" - Hora: " + rs.getString("hrAgenda") +
						" - Animal: " + localizarAnimalPor(rs.getInt("cdAnimal")).getNmAnimal() + 
						"\n";
			}
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na busca pela agenda!");
		}finally {
			this.liberar(psSelect);
		}
		return retorno;
	}
	
}
