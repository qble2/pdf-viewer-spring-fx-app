package qble2.pdf.viewer.business;

import java.time.ZonedDateTime;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity(name = "FileNote")
@Table(name = "FileNote")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Accessors(chain = true, fluent = false)
public class FileNote {

  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "filePath", nullable = false)
  private String filePath;

  /**
   * 0-based
   */
  @Column(name = "filePage", nullable = false)
  private int filePage;

  @Column(name = "title", nullable = true)
  private String title;

  @Column(name = "comment", nullable = true)
  @Lob
  @Basic(fetch = FetchType.EAGER)
  private String comment;

  @Column(name = "lastUpdatedAt", nullable = false)
  private ZonedDateTime lastUpdatedAt;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private ImageCapture imageCapture;

}
