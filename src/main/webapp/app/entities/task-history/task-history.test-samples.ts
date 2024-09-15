import { ITaskHistory, NewTaskHistory } from './task-history.model';

export const sampleWithRequiredData: ITaskHistory = {
  id: 81,
};

export const sampleWithPartialData: ITaskHistory = {
  id: 390,
  type: 'who',
};

export const sampleWithFullData: ITaskHistory = {
  id: 5800,
  type: 'shower vogue dime',
};

export const sampleWithNewData: NewTaskHistory = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
