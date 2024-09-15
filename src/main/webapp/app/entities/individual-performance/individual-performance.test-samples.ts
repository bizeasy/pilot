import { IIndividualPerformance, NewIndividualPerformance } from './individual-performance.model';

export const sampleWithRequiredData: IIndividualPerformance = {
  id: 12109,
};

export const sampleWithPartialData: IIndividualPerformance = {
  id: 7826,
  completedTasks: 1964,
  storyPointsCompleted: 32413,
};

export const sampleWithFullData: IIndividualPerformance = {
  id: 32421,
  completedTasks: 1225,
  velocity: 28289,
  storyPointsCompleted: 13023,
};

export const sampleWithNewData: NewIndividualPerformance = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
