import { IStatusCategory, NewStatusCategory } from './status-category.model';

export const sampleWithRequiredData: IStatusCategory = {
  id: 17960,
};

export const sampleWithPartialData: IStatusCategory = {
  id: 231,
};

export const sampleWithFullData: IStatusCategory = {
  id: 4256,
  name: 'majestically until',
  description: 'supposing helpful',
};

export const sampleWithNewData: NewStatusCategory = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
