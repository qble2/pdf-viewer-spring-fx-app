package qble2.pdf.viewer.gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.gui.event.EventBusFx;

@Service
@Slf4j
public class DirectoryService implements EventListener {

  private static final String PDF_EXTENSION = "pdf";

  public DirectoryService() {
    EventBusFx.getInstance().registerListener(this);
  }

  public List<Path> loadDirectory(Path directoryPath) {
    log.info("loading directory:\t{}", directoryPath.toString());

    try {
      return Files.walk(directoryPath).filter(this::isPdfFile).toList();
    } catch (IOException e) {
      log.error("an error has occured", e);
    }

    return Collections.emptyList();
  }

  private boolean isPdfFile(Path path) {
    return Files.isRegularFile(path)
        && PDF_EXTENSION.equals(FilenameUtils.getExtension(path.getFileName().toString()));
  }

}
