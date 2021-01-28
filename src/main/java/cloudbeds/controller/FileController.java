package cloudbeds.controller;

import cloudbeds.fileUpload.StorageService;
import cloudbeds.fileUpload.exception.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller
@Service
public class FileController {

	private final StorageService storageService;

	@Autowired
	public FileController(StorageService storageService) {
		this.storageService = storageService;
	}

	@GetMapping("/files/upload")
	public String listUploadedFiles(Model model) throws IOException {
		model.addAttribute("files", storageService.loadAll().map(
				path -> MvcUriComponentsBuilder.fromMethodName(
						FileController.class,
						"serveFile",
						path.getFileName().toString()
				).build().toUri().toString()
		).collect(Collectors.toList()));

		return "uploadForm";
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		Resource file = storageService.loadAsResource(filename);

		return ResponseEntity.ok().header(
			HttpHeaders.CONTENT_DISPOSITION,
			"attachment; filename=\"" + file.getFilename() + "\""
		).body(file);
	}

	@PostMapping("/files/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		storageService.store(file);
		redirectAttributes.addFlashAttribute(
			"message",
			"You successfully uploaded " + file.getOriginalFilename() + "!"
		);

		return "redirect:/files/upload";
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}
