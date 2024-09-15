import dayjs from 'dayjs/esm';

import { IRetrospective, NewRetrospective } from './retrospective.model';

export const sampleWithRequiredData: IRetrospective = {
  id: 30077,
  dateCreated: dayjs('2024-09-14T13:58'),
};

export const sampleWithPartialData: IRetrospective = {
  id: 19697,
  actionItems: 'so poorly by',
  dateCreated: dayjs('2024-09-14T14:48'),
};

export const sampleWithFullData: IRetrospective = {
  id: 3274,
  summary: 'finger homeschool',
  actionItems: 'outmaneuver joyfully at',
  dateCreated: dayjs('2024-09-14T17:30'),
};

export const sampleWithNewData: NewRetrospective = {
  dateCreated: dayjs('2024-09-14T19:20'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
