package qble2.pdf.viewer.business;

import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileNoteService {

  @Autowired
  private FileNoteRepository fileNoteRepository;

  public List<FileNote> getFileNotes(String filePath) {
    return this.fileNoteRepository.findByFilePathOrderByLastUpdatedAtDesc(filePath);
  }

  public FileNote createFileNote(FileNote fileNote) {
    log.info("creating file note: {}", fileNote);
    fileNote.setLastUpdatedAt(ZonedDateTime.now());

    return this.fileNoteRepository.save(fileNote);
  }

  public FileNote updateFileNote(FileNote fileNote) {
    log.info("updating file note (id: {})", fileNote.getId());

    if (!this.fileNoteRepository.existsById(fileNote.getId())) {
      throw new FileNoteNotFoundException();
    }

    fileNote.setLastUpdatedAt(ZonedDateTime.now());

    return this.fileNoteRepository.save(fileNote);
  }

  public void deleteFileNote(Long id) {
    log.info("deleting file note (id: {})", id);
    this.fileNoteRepository.deleteById(id);
  }

}
