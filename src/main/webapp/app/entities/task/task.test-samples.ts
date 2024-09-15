import dayjs from 'dayjs/esm';

import { ITask, NewTask } from './task.model';

export const sampleWithRequiredData: ITask = {
  id: 13507,
  title: 'ballot invigilate elementary',
};

export const sampleWithPartialData: ITask = {
  id: 24355,
  title: 'oh graduate',
  priority: 'LOW',
  info: '../fake-data/blob/hipster.txt',
  storyPoints: 12092,
  startTime: dayjs('2024-09-15T03:31'),
  sequenceNo: 7398,
};

export const sampleWithFullData: ITask = {
  id: 31504,
  title: 'lively',
  description: 'drat amid rewarding',
  priority: 'MEDIUM',
  dueDate: dayjs('2024-09-15'),
  info: '../fake-data/blob/hipster.txt',
  storyPoints: 12887,
  startTime: dayjs('2024-09-14T10:51'),
  pauseTime: dayjs('2024-09-14T11:18'),
  endTime: dayjs('2024-09-15T01:57'),
  totalDuration: '4997',
  sequenceNo: 28885,
};

export const sampleWithNewData: NewTask = {
  title: 'confused through needily',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
