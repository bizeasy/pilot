import { ITaskAttachment, NewTaskAttachment } from './task-attachment.model';

export const sampleWithRequiredData: ITaskAttachment = {
  id: 6781,
};

export const sampleWithPartialData: ITaskAttachment = {
  id: 15613,
};

export const sampleWithFullData: ITaskAttachment = {
  id: 15072,
};

export const sampleWithNewData: NewTaskAttachment = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
