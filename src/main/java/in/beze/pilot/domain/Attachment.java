package in.beze.pilot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Attachment.
 */
@Entity
@Table(name = "attachment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Attachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 25)
    @Column(name = "name", length = 25)
    private String name;

    @Lob
    @Column(name = "file_attachment")
    private byte[] fileAttachment;

    @Column(name = "file_attachment_content_type")
    private String fileAttachmentContentType;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "mime_type")
    private String mimeType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Attachment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Attachment name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getFileAttachment() {
        return this.fileAttachment;
    }

    public Attachment fileAttachment(byte[] fileAttachment) {
        this.setFileAttachment(fileAttachment);
        return this;
    }

    public void setFileAttachment(byte[] fileAttachment) {
        this.fileAttachment = fileAttachment;
    }

    public String getFileAttachmentContentType() {
        return this.fileAttachmentContentType;
    }

    public Attachment fileAttachmentContentType(String fileAttachmentContentType) {
        this.fileAttachmentContentType = fileAttachmentContentType;
        return this;
    }

    public void setFileAttachmentContentType(String fileAttachmentContentType) {
        this.fileAttachmentContentType = fileAttachmentContentType;
    }

    public String getAttachmentUrl() {
        return this.attachmentUrl;
    }

    public Attachment attachmentUrl(String attachmentUrl) {
        this.setAttachmentUrl(attachmentUrl);
        return this;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public Attachment mimeType(String mimeType) {
        this.setMimeType(mimeType);
        return this;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attachment)) {
            return false;
        }
        return getId() != null && getId().equals(((Attachment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Attachment{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", fileAttachment='" + getFileAttachment() + "'" +
            ", fileAttachmentContentType='" + getFileAttachmentContentType() + "'" +
            ", attachmentUrl='" + getAttachmentUrl() + "'" +
            ", mimeType='" + getMimeType() + "'" +
            "}";
    }
}
