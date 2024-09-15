import { IProjectAttachment, NewProjectAttachment } from './project-attachment.model';

export const sampleWithRequiredData: IProjectAttachment = {
  id: 27980,
};

export const sampleWithPartialData: IProjectAttachment = {
  id: 18344,
};

export const sampleWithFullData: IProjectAttachment = {
  id: 32431,
};

export const sampleWithNewData: NewProjectAttachment = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
