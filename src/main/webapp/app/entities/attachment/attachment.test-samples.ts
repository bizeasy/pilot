import { IAttachment, NewAttachment } from './attachment.model';

export const sampleWithRequiredData: IAttachment = {
  id: 10684,
};

export const sampleWithPartialData: IAttachment = {
  id: 7328,
  name: 'valiantly',
  attachmentUrl: 'parody marketer yum',
  mimeType: 'hasty apprentice',
};

export const sampleWithFullData: IAttachment = {
  id: 1686,
  name: 'academic qua',
  fileAttachment: '../fake-data/blob/hipster.png',
  fileAttachmentContentType: 'unknown',
  attachmentUrl: 'oddly yet seriously',
  mimeType: 'outmanoeuvre er gah',
};

export const sampleWithNewData: NewAttachment = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
