package qble2.pdf.viewer.system;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DirectoryService {

  private static final String PDF_EXTENSION = "pdf";

  public DirectoryService() {}

  public List<Path> loadDirectory(Path directoryPath) {
    log.info("Loading directory:\t{}", directoryPath.toString());
    try {
      return Files.walk(directoryPath).filter(this::isPdfFile).sorted(new Comparator<Path>() {
        @Override
        public int compare(Path path1, Path path2) {
          return Long.compare(path1.toFile().lastModified(), path2.toFile().lastModified());
        }
      }).toList();
    } catch (IOException e) {
      log.error("An error has occured", e);
    }

    return Collections.emptyList();
  }

  private boolean isPdfFile(Path path) {
    return Files.isRegularFile(path)
        && PDF_EXTENSION.equals(FilenameUtils.getExtension(path.getFileName().toString()));
  }

}
