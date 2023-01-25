package qble2.pdf.viewer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.Data;

@Entity(name = "Chapter")
@Table(name = "Chapter")
@Data
public class Chapter {

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Lob
  // @Basic(fetch=FetchType.LAZY)
  private String content;

}
