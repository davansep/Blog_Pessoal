package org.generation.blogPessoal.service;

import java.nio.charset.Charset;
import java.util.Optional;
import org.apache.commons.codec.binary.Base64;
import org.generation.blogPessoal.model.Usuario;
import org.generation.blogPessoal.model.dtos.CredenciaisDTO;
import org.generation.blogPessoal.model.dtos.UsuarioLoginDTO;
import org.generation.blogPessoal.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

	private @Autowired UsuarioRepository repository;

	/**
	 * Metodo utilizado para cadastrar usuário validando duplicidade de email no
	 * banco
	 * 
	 * @param usuarioParaCadastrar do tipo Usuario
	 * @return Optional com Usuario cadastrado caso email não seja existente
	 * @author Priscila
	 * @since 1.0
	 */
	public Optional<Object> cadastrarUsuario(Usuario usuarioParaCadastrar) {
		return repository.findByEmail(usuarioParaCadastrar.getEmail()).map(usuarioExistente -> {
			return Optional.empty();
		}).orElseGet(() -> {
			usuarioParaCadastrar.setSenha(encriptadorDeSenha(usuarioParaCadastrar.getSenha()));
			return Optional.ofNullable(repository.save(usuarioParaCadastrar));
		});
	}

	/**
	 * Metodo utilizado para atualizar usuario no banco
	 * 
	 * @param usuarioParaAtualizar do tipo Usuario
	 * @return Optional com Usuario atualizado
	 * @author Priscila
	 * @since 1.0
	 */
	public Optional<Usuario> atualizarUsuario(Usuario usuarioParaAtualizar) {
		return repository.findById(usuarioParaAtualizar.getIdUsuario()).map(resp -> {
			resp.setNome(usuarioParaAtualizar.getNome());
			resp.setSenha(encriptadorDeSenha(usuarioParaAtualizar.getSenha()));
			return Optional.ofNullable(repository.save(resp));
		}).orElseGet(() -> {
			return Optional.empty();
		});
	}

	/**
	 * Metodo utilizado para pegar credenciais do usuario com Tokem (Formato Basic),
	 * este método sera utilizado para retornar ao front o token utilizado para ter
	 * acesso aos dados do usuario e mantelo logado no sistema
	 * 
	 * @param usuarioParaAutenticar do tipo UsuarioLoginDTO necessario email e senha
	 *                              para validar
	 * @return ResponseEntity com CredenciaisDTO preenchido com informações mais o
	 *         Token
	 * @since 1.0
	 * @author Priscila
	 */
	public ResponseEntity<CredenciaisDTO> pegarCredenciais(UsuarioLoginDTO usuarioParaAutenticar) {
		return repository.findByEmail(usuarioParaAutenticar.getEmail()).map(resp -> {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

			if (encoder.matches(usuarioParaAutenticar.getSenha(), resp.getSenha())) {

				CredenciaisDTO objetoCredenciaisDTO = new CredenciaisDTO();

				objetoCredenciaisDTO
						.setToken(gerarToken(usuarioParaAutenticar.getEmail(), usuarioParaAutenticar.getSenha()));
				objetoCredenciaisDTO.setIdUsuario(resp.getIdUsuario());
				objetoCredenciaisDTO.setNome(resp.getNome());
				objetoCredenciaisDTO.setEmail(resp.getEmail());
				objetoCredenciaisDTO.setSenha(resp.getSenha());

				return ResponseEntity.status(201).body(objetoCredenciaisDTO); // Usuario Credenciado
			} else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha Incorreta!"); // Senha incorreta
			}
		}).orElseGet(() -> {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email não existe!"); // Email não existe
		});
	}

	/**
	 * Metodo statico utilizado para gerar token
	 * 
	 * @param email
	 * @param senha
	 * @return Token no formato Basic para autenticação
	 * @since 1.0
	 * @author Priscila
	 */
	private static String gerarToken(String email, String senha) {
		String estruturaBasic = email + ":" + senha; // gustavoboaz@gmail.com:134652
		byte[] estruturaBase64 = Base64.encodeBase64(estruturaBasic.getBytes(Charset.forName("US-ASCII"))); // hHJyigo-o+i7%0ÍUG465sas=-
		return "Basic " + new String(estruturaBase64); // Basic hHJyigo-o+i7%0ÍUG465sas=-
	}

	/**
	 * Método estatico que recebe a senha do usuario o criptografa
	 * 
	 * @param senha
	 * @return String da senha criptografada
	 * @since 1.0
	 * @author Priscila
	 */
	private static String encriptadorDeSenha(String senha) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(senha);
	}
}
