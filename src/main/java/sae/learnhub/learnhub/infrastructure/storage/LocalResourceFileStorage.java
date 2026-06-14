package sae.learnhub.learnhub.infrastructure.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.application.exception.BusinessRuleException;
import sae.learnhub.learnhub.application.exception.ResourceNotFoundException;
import sae.learnhub.learnhub.application.port.ResourceFileStorage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Component
public class LocalResourceFileStorage implements ResourceFileStorage {

    private final Path storageRoot;

    public LocalResourceFileStorage(
            @Value("${app.storage.resources-dir:uploads/resources}") String storageDirectory) {
        try {
            storageRoot = Path.of(storageDirectory).toAbsolutePath().normalize();
            Files.createDirectories(storageRoot);
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible d'initialiser le stockage des ressources", exception);
        }
    }

    @Override
    public StoredFile store(FileUpload upload) {
        String extension = extensionOf(upload.originalName());
        String key = UUID.randomUUID() + extension;
        Path target = resolveKey(key);

        try (InputStream content = upload.content()) {
            Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING);
            return new StoredFile(
                    key,
                    "/api/files/resources/" + key,
                    upload.originalName(),
                    upload.contentType(),
                    upload.size());
        } catch (IOException exception) {
            throw new BusinessRuleException("Le fichier n'a pas pu être enregistré");
        }
    }

    @Override
    public FileContent load(String key) {
        Path file = resolveKey(key);
        if (!Files.isRegularFile(file)) {
            throw new ResourceNotFoundException("Fichier introuvable");
        }

        try {
            String contentType = Files.probeContentType(file);
            return new FileContent(
                    key,
                    contentType == null ? "application/octet-stream" : contentType,
                    Files.size(file),
                    Files.newInputStream(file));
        } catch (IOException exception) {
            throw new ResourceNotFoundException("Fichier introuvable");
        }
    }

    @Override
    public void deleteByUrl(String url) {
        if (url == null || !url.startsWith("/api/files/resources/")) {
            return;
        }
        String key = url.substring(url.lastIndexOf('/') + 1);
        try {
            Files.deleteIfExists(resolveKey(key));
        } catch (IOException ignored) {
            // La suppression de la donnée métier ne doit pas échouer pour un fichier déjà absent.
        }
    }

    private Path resolveKey(String key) {
        Path resolved = storageRoot.resolve(key).normalize();
        if (!resolved.startsWith(storageRoot)) {
            throw new BusinessRuleException("Nom de fichier invalide");
        }
        return resolved;
    }

    private String extensionOf(String originalName) {
        if (originalName == null) {
            return "";
        }
        int separator = originalName.lastIndexOf('.');
        return separator < 0
                ? ""
                : originalName.substring(separator).toLowerCase(Locale.ROOT);
    }
}
