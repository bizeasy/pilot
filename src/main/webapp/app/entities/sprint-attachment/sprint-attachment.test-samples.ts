import { ISprintAttachment, NewSprintAttachment } from './sprint-attachment.model';

export const sampleWithRequiredData: ISprintAttachment = {
  id: 24868,
};

export const sampleWithPartialData: ISprintAttachment = {
  id: 32323,
};

export const sampleWithFullData: ISprintAttachment = {
  id: 19511,
};

export const sampleWithNewData: NewSprintAttachment = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
