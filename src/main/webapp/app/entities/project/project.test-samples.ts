import dayjs from 'dayjs/esm';

import { IProject, NewProject } from './project.model';

export const sampleWithRequiredData: IProject = {
  id: 14715,
  name: 'fast cram',
};

export const sampleWithPartialData: IProject = {
  id: 21130,
  name: 'clear-cut',
  startDate: dayjs('2024-09-14'),
};

export const sampleWithFullData: IProject = {
  id: 5929,
  name: 'geez',
  description: 'during',
  startDate: dayjs('2024-09-15'),
  endDate: dayjs('2024-09-15'),
};

export const sampleWithNewData: NewProject = {
  name: 'upright flounce dogsled',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
