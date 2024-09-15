import { IStatus, NewStatus } from './status.model';

export const sampleWithRequiredData: IStatus = {
  id: 28406,
};

export const sampleWithPartialData: IStatus = {
  id: 21788,
  name: 'naughty apud',
  description: 'rarely steam',
};

export const sampleWithFullData: IStatus = {
  id: 22337,
  name: 'meh canalise bagel',
  sequenceNo: 24278,
  description: 'lest lest though',
  type: 'long-term',
};

export const sampleWithNewData: NewStatus = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
