package qble2.pdf.viewer.system;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.springframework.stereotype.Service;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.gui.exception.FailedToDeleteDirectoryException;
import qble2.pdf.viewer.gui.exception.FailedToLoadPdfFileException;
import qble2.pdf.viewer.gui.exception.FailedToSplitPdfFilesException;
import qble2.pdf.viewer.gui.exception.PdfFileBookmarksNotFoundException;
import qble2.pdf.viewer.gui.exception.PdfFileNotProvidedException;

@Service
@Slf4j
public class SplitPdfFileService {

  /**
   * 0-based
   */
  private static int SPLIT_OUTLINE_DEPTH_LIMIT_INCLUSIVE = 1;

  private static final String SPLIT_FILES_TARGET_FOLDER = "_SPLITS";
  private static final String SPLIT_FILE_NAME_FORMAT = "%s.%s";
  private static final String WINDOWS_FILE_NAME_FORBIDDEN_CHARACTERS_REGEX = "[\\/:*?\"<>|]";

  @Setter
  private Consumer<String> splitFilesTargetDirectoryConsumer;

  @Setter
  private Consumer<String> currentOperationConsumer;

  @Setter
  private Consumer<Integer> totalNumberOfPagesConsumer;

  @Setter
  private Consumer<String> currentEntryConsumer;

  @Setter
  private BiConsumer<Integer, Integer> progressUpdateConsumer;

  @Setter
  private Consumer<Integer> totalNumberOfSplitFilesCreatedConsumer;

  private int numberOfPages;
  private String previousItemTitle;
  private int previousOutlineStartingPageNumber;
  private int currentOutlineStartingPageNumber;
  private int totalSplitFilesCreated;

  public Path splitPdfFile(Path pdfFilePath) {
    Path targetDirectoryPath = null;

    if (pdfFilePath == null) {
      log.error("PDF file cannot be null");
      throw new PdfFileNotProvidedException("PDF file cannot be null");
    }

    totalSplitFilesCreated = 0;
    previousItemTitle = null;
    previousOutlineStartingPageNumber = 0;
    currentOutlineStartingPageNumber = 0;

    PDDocument document = null;
    try {
      totalNumberOfSplitFilesCreatedConsumer.accept(0);

      document = loadPdfFile(pdfFilePath);
      PDDocumentOutline bookmarks = loadBookmarks(document);
      targetDirectoryPath = prepareTargetDirectory(pdfFilePath);

      currentOperationConsumer.accept("Creating split files...");
      createSplitFiles(document, bookmarks, 0, "", targetDirectoryPath);
      // last outline
      createSplitFile(document, previousItemTitle, previousOutlineStartingPageNumber,
          document.getNumberOfPages(), targetDirectoryPath);
    } finally {
      if (document != null) {
        try {
          document.close();
        } catch (IOException e) {
          log.error("An error has occurred", e);
        }
      }
    }

    return targetDirectoryPath;
  }

  /////
  /////
  /////

  private Path prepareTargetDirectory(Path pdfFilePath) {
    currentOperationConsumer.accept("Preparing target folder...");

    Path targetDirectory = Path.of(pdfFilePath.getParent().toString(), SPLIT_FILES_TARGET_FOLDER);
    splitFilesTargetDirectoryConsumer.accept(targetDirectory.toString());
    log.info("Preparing target folder\t{}", targetDirectory.toString());

    if (targetDirectory.toFile().exists()) {
      try {
        FileUtils.deleteDirectory(targetDirectory.toFile());
      } catch (IOException e) {
        log.error("An error has occurred", e);
        throw new FailedToDeleteDirectoryException(
            String.format("Failed to delete existing target directory"));
      }
    }
    targetDirectory.toFile().mkdirs();

    return targetDirectory;
  }

  private PDDocument loadPdfFile(Path pdfFilePath) {
    currentOperationConsumer.accept("Loading PDF file...");

    log.info("Loading PDF file...");
    PDDocument document;
    try {
      document = PDDocument.load(pdfFilePath.toFile());
    } catch (IOException e) {
      log.error("An error has occurred", e);
      throw new FailedToLoadPdfFileException("Failed to load PDF file");
    }
    numberOfPages = document.getNumberOfPages();
    totalNumberOfPagesConsumer.accept(numberOfPages);

    return document;
  }

  private PDDocumentOutline loadBookmarks(PDDocument document) {
    currentOperationConsumer.accept("Loading bookmarks...");

    log.info("Loading bookmarks...");
    PDDocumentCatalog documentCatalog = document.getDocumentCatalog();
    PDDocumentOutline documentOutline = documentCatalog.getDocumentOutline();
    if (documentOutline == null) {
      log.warn("\t >>> Bookmarks not found");
      throw new PdfFileBookmarksNotFoundException("Bookmarks not found");
    }

    return documentOutline;
  }

  private void createSplitFiles(PDDocument document, PDOutlineNode outline, int depth,
      String indentation, Path targetDirectoryPath) {
    PDOutlineItem currentOutlineItem = outline.getFirstChild();
    while (currentOutlineItem != null && depth <= SPLIT_OUTLINE_DEPTH_LIMIT_INCLUSIVE) {
      String currentOutlineTitle = currentOutlineItem.getTitle();

      try {
        PDPage currentOutlinePage = currentOutlineItem.findDestinationPage(document);
        if (currentOutlinePage == null) {
          log.warn(
              "Skipped item ({}): does not point to anything or is an action that is not a GoTo action",
              currentOutlineItem.getTitle());
        } else {
          currentOutlineStartingPageNumber =
              document.getDocumentCatalog().getPages().indexOf(currentOutlinePage) + 1;

          if (previousItemTitle != null && currentOutlineStartingPageNumber >= 2) {
            createSplitFile(document, previousItemTitle, previousOutlineStartingPageNumber,
                currentOutlineStartingPageNumber - 1, targetDirectoryPath);
          }
        }
      } catch (IOException e) {
        log.error("An error has occurred", e);
        throw new FailedToSplitPdfFilesException("Failed to split PDF files");
      }

      //
      previousOutlineStartingPageNumber = currentOutlineStartingPageNumber;
      previousItemTitle = currentOutlineTitle;
      createSplitFiles(document, currentOutlineItem, depth + 1, indentation + "\t",
          targetDirectoryPath);
      currentOutlineItem = currentOutlineItem.getNextSibling();
    }
  }

  private void createSplitFile(PDDocument document, String title, int fromPage, int toPage,
      Path targetDirectoryPath) {
    currentEntryConsumer.accept(title);
    progressUpdateConsumer.accept(toPage, numberOfPages);

    String validFileName = title.replaceAll(WINDOWS_FILE_NAME_FORBIDDEN_CHARACTERS_REGEX, "_");
    String pdfSplitFileName = String.format(SPLIT_FILE_NAME_FORMAT, validFileName, "pdf");
    Path splitPdfFilePath = Paths.get(targetDirectoryPath.toString(), pdfSplitFileName);
    if (toPage < fromPage) {
      log.warn("Skipped item ({}): malformed page range [{}-{}]", title, fromPage, toPage);
      return;
    }

    Splitter splitter = new Splitter();
    splitter.setStartPage(fromPage);
    splitter.setEndPage(toPage);
    splitter.setSplitAtPage(toPage);

    try {
      List<PDDocument> splittedList = splitter.split(document);
      for (PDDocument doc : splittedList) {
        log.info("Creating split file for item ({}) - pages [{}-{}]", title, fromPage, toPage);
        if (splitPdfFilePath.toFile().exists()) {
          log.warn("\t >>> Skipped: split file already exists for item ({})", title);
        } else {
          doc.save(splitPdfFilePath.toString());
          log.info("\t >>> Created");
          totalSplitFilesCreated += 1;
          totalNumberOfSplitFilesCreatedConsumer.accept(totalSplitFilesCreated);
        }
        doc.close();
      }
    } catch (IOException e) {
      log.error("\t >>> Creation failed", e);
    }
  }

}
