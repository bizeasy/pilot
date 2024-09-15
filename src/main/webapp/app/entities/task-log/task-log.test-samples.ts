import dayjs from 'dayjs/esm';

import { ITaskLog, NewTaskLog } from './task-log.model';

export const sampleWithRequiredData: ITaskLog = {
  id: 7741,
};

export const sampleWithPartialData: ITaskLog = {
  id: 4674,
  comments: 'that',
  fromTime: dayjs('2024-09-15T02:02'),
  toTime: dayjs('2024-09-15T07:37'),
};

export const sampleWithFullData: ITaskLog = {
  id: 25246,
  comments: 'occasional',
  fromTime: dayjs('2024-09-15T05:21'),
  toTime: dayjs('2024-09-15T05:15'),
};

export const sampleWithNewData: NewTaskLog = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
