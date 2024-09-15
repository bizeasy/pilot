export interface IAttachment {
  id: number;
  name?: string | null;
  fileAttachment?: string | null;
  fileAttachmentContentType?: string | null;
  attachmentUrl?: string | null;
  mimeType?: string | null;
}

export type NewAttachment = Omit<IAttachment, 'id'> & { id: null };
