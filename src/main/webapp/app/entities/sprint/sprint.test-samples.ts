import dayjs from 'dayjs/esm';

import { ISprint, NewSprint } from './sprint.model';

export const sampleWithRequiredData: ISprint = {
  id: 18305,
  name: 'given whether',
  startDate: dayjs('2024-09-15'),
  endDate: dayjs('2024-09-14'),
};

export const sampleWithPartialData: ISprint = {
  id: 7638,
  name: 'woot chicken finally',
  startDate: dayjs('2024-09-15'),
  endDate: dayjs('2024-09-14'),
};

export const sampleWithFullData: ISprint = {
  id: 10333,
  name: 'hastily manner map',
  startDate: dayjs('2024-09-14'),
  endDate: dayjs('2024-09-14'),
  goal: 'across worth soon',
  totalPoints: 11806,
};

export const sampleWithNewData: NewSprint = {
  name: 'spout athwart',
  startDate: dayjs('2024-09-15'),
  endDate: dayjs('2024-09-14'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
