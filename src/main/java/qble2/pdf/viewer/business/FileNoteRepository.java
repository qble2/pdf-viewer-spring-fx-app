package qble2.pdf.viewer.business;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileNoteRepository extends JpaRepository<FileNote, Long> {

  List<FileNote> findByFilePath(String filePath);

  List<FileNote> findByFilePathOrderByLastUpdatedAtDesc(String filePath);

}
