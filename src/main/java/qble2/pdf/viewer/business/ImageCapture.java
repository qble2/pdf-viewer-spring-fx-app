package qble2.pdf.viewer.business;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity(name = "ImageCapture")
@Table(name = "ImageCapture")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Accessors(chain = true, fluent = false)
public class ImageCapture {

  public ImageCapture(int width, int height, byte[] bytes) {
    this.width = width;
    this.height = height;
    this.bytes = bytes;
  }

  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "width", nullable = false, updatable = false)
  private int width;

  @Column(name = "height", nullable = false, updatable = false)
  private int height;

  @Lob
  @Column(name = "bytes", nullable = false, updatable = false)
  @ToString.Exclude
  private byte[] bytes;

}
