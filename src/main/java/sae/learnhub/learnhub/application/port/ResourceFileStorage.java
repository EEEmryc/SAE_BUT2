package sae.learnhub.learnhub.application.port;

import java.io.InputStream;

public interface ResourceFileStorage {

    record FileUpload(String originalName, String contentType, long size, InputStream content) {
    }

    record StoredFile(String key, String url, String originalName, String contentType, long size) {
    }

    record FileContent(String originalName, String contentType, long size, InputStream content) {
    }

    StoredFile store(FileUpload upload);

    FileContent load(String key);

    void deleteByUrl(String url);
}
