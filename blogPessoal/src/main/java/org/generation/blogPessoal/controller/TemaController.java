package org.generation.blogPessoal.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.generation.blogPessoal.model.Tema;
import org.generation.blogPessoal.repository.TemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/temas")
public class TemaController {

	@Autowired
	private TemaRepository repository;

	@GetMapping("/todos")
	public ResponseEntity<List<Tema>> getAll() {
		List<Tema> objetoLista = repository.findAll();

		if (objetoLista.isEmpty()) {
			return ResponseEntity.status(204).build();
		} else {
			return ResponseEntity.status(200).body(objetoLista);
		}
	}

	@GetMapping("/{id_tema}")
	public ResponseEntity<Tema> pegarPorId(@PathVariable(value = "id_tema") Long idTema) {
		Optional<Tema> objetoOptional = repository.findById(idTema);

		if (objetoOptional.isPresent()) {
			return ResponseEntity.status(200).body(objetoOptional.get());
		} else {
			return ResponseEntity.status(204).build();
		}
	}

	@GetMapping("/nome/{nome}")
	public ResponseEntity<List<Tema>> getByName(@PathVariable String nome) {
		return ResponseEntity.ok(repository.findAllByDescricaoContainingIgnoreCase(nome));
	}

	@PostMapping("/salvar")
	public ResponseEntity<Tema> post(@Valid @RequestBody Tema novoTema) {
		return ResponseEntity.status(201).body(repository.save(novoTema));

	}

	@PutMapping("/atualizar")
	public ResponseEntity<Tema> put(@Valid @RequestBody Tema novoTema) {
		return ResponseEntity.ok(repository.save(novoTema));

	}

	@DeleteMapping("/deletar/{id_tema}")
	public ResponseEntity<Tema> deletar(@PathVariable(value = "id_tema") Long idTema) {
		Optional<Tema> objetoOptional = repository.findById(idTema);

		if (objetoOptional.isPresent()) {
			repository.deleteById(idTema);
			return ResponseEntity.status(204).build();
		} else {
			return ResponseEntity.status(400).build();
		}
	}
}
