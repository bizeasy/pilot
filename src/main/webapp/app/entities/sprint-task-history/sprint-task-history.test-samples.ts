import dayjs from 'dayjs/esm';

import { ISprintTaskHistory, NewSprintTaskHistory } from './sprint-task-history.model';

export const sampleWithRequiredData: ISprintTaskHistory = {
  id: 259,
};

export const sampleWithPartialData: ISprintTaskHistory = {
  id: 2476,
  comments: 'whose criminal',
  toDate: dayjs('2024-09-15'),
};

export const sampleWithFullData: ISprintTaskHistory = {
  id: 6796,
  comments: 'although',
  fromDate: dayjs('2024-09-14'),
  toDate: dayjs('2024-09-15'),
};

export const sampleWithNewData: NewSprintTaskHistory = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
