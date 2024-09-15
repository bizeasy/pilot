import dayjs from 'dayjs/esm';

import { ISprintTask, NewSprintTask } from './sprint-task.model';

export const sampleWithRequiredData: ISprintTask = {
  id: 13375,
};

export const sampleWithPartialData: ISprintTask = {
  id: 23518,
  sequenceNo: 724,
  thruTime: dayjs('2024-09-14T11:53'),
  assignedTime: dayjs('2024-09-14T18:01'),
};

export const sampleWithFullData: ISprintTask = {
  id: 10676,
  sequenceNo: 4054,
  storyPoints: 29675,
  fromTime: dayjs('2024-09-15T08:18'),
  thruTime: dayjs('2024-09-15T08:04'),
  assignedTime: dayjs('2024-09-15T09:55'),
  duration: '27985',
};

export const sampleWithNewData: NewSprintTask = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
