package br.com.senai1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import br.com.senai.Funcionario;

public class BaseDados {

	private Connection conexao;
	public BaseDados() throws Exception{
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
	public void inserirFuncionario(Funcionario funcionario) {
		PreparedStatement psInsert = null;
		try {
			psInsert = conexao.prepareStatement("INSERT INTO Funcionarios "
					+ "(nome, idade,email, genero, cpf, endereco, tipo, funcionarios_geridos) VALUES (?,?,?, ?,?,?,?,?) ");
			psInsert.setString(1, funcionario.getNome());
			psInsert.setInt(2, funcionario.getIdade());
			psInsert.setString(3,funcionario.getEmail());
			psInsert.setString(4,String.valueOf(funcionario.getGenero()));
			psInsert.setString(5,funcionario.getCPF());
			psInsert.setString(6,funcionario.getEndereco());
			psInsert.setString(7,String.valueOf(funcionario.getTipo()));
			psInsert.setInt(8,funcionario.getFuncionariosG());
			
			
			psInsert.executeUpdate();
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na inserção do Animal");
		}finally {
			this.liberar(psInsert);
		}
	}
	public void alterarFuncionario(Funcionario funcionario) {		
		PreparedStatement psUpdate = null;		
		try {
			this.conexao.setAutoCommit(false);
			psUpdate = conexao.prepareStatement("UPDATE Funcionarios SET "
					+ "nome = ?, idade = ?"
					+ "email = ?, genero =?"
					+ "cpf = ?, endereço = ?"
					+ "tipo = ?, funcionarioG = ?"
					+ "WHERE id = ? ");
			psUpdate.setString(1, funcionario.getNome());
			psUpdate.setInt(2, funcionario.getIdade());
			psUpdate.setString(3, funcionario.getEmail());
			psUpdate.setString(4, String.valueOf(funcionario.getGenero()));
			psUpdate.setString(5, funcionario.getCPF());
			psUpdate.setString(6, funcionario.getEndereco());
			psUpdate.setString(7, String.valueOf(funcionario.getTipo()));
			psUpdate.setInt(8, funcionario.getFuncionariosG());
			psUpdate.setInt(9, funcionario.getId());
			int qtdeDeLinhasAlteradas = psUpdate.executeUpdate();
			if (qtdeDeLinhasAlteradas <= 1) {
				this.conexao.commit();
			}else {
				
				this.conexao.rollback();
			}
			this.conexao.setAutoCommit(true);
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na alteração dos Funcionarios");
		}finally {
			this.liberar(psUpdate);
		}
	}
	public void excluirFuncionario(int id) {
		PreparedStatement psDelete = null;
		try {
			this.conexao.setAutoCommit(false);
			psDelete = conexao.prepareStatement("DELETE FROM Funcionarios WHERE id = ?");
			psDelete.setInt(1, id);
			int qtdeDeLinhasRemovidas = psDelete.executeUpdate();
			if (qtdeDeLinhasRemovidas <= 1) {
				this.conexao.commit();
			}else {
				
				this.conexao.rollback();
			}
			this.conexao.setAutoCommit(true);
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na exclusão do Funcionario");
		}finally {
			this.liberar(psDelete);
		}
	}
	
	public String listarFuncionario(int id) {
		String retorno = "";
		PreparedStatement psSelect = null;
		try {
			if (id > 0) { // 
				psSelect = conexao.prepareStatement("SELECT nome, idade, email, genero, cpf, endereço, tipo, FuncionarioG,  "
					+ "FROM Funcionarios where id = ? ");
				psSelect.setInt(1, id);
			} else { 
				psSelect = conexao.prepareStatement("SELECT id, nome, idade, email, genero, cpf, endereço, tipo, FuncionarioG "
						+ "FROM Funcionarios "
						+ "Order by nome");
			}
			ResultSet rs = psSelect.executeQuery();
			while (rs.next()) {
				retorno+= "id.: " + rs.getInt("id") + 
						" - Nome: " + rs.getString("nome") +
						" - idade: " + rs.getInt("idade") +
						" - email: " + rs.getString("email") +
						" - genero: " + rs.getString(String.valueOf("Genero")) +
						" - cpf: " + rs.getString("cpf") +
						" - endereço: " + rs.getString("endereço") +
						" - tipo: " + rs.getString(String.valueOf("Tipo")) +
						" - FuncionarioG: " + rs.getInt("idade") +
						"\n";
			}
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na busca pelo Funcionario!");
		}finally {
			this.liberar(psSelect);
		}
		return retorno;
	}

	
}
