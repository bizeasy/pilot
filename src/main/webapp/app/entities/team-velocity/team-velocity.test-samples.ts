import { ITeamVelocity, NewTeamVelocity } from './team-velocity.model';

export const sampleWithRequiredData: ITeamVelocity = {
  id: 6527,
  sprintVelocity: 27369,
  averageVelocity: 14245,
};

export const sampleWithPartialData: ITeamVelocity = {
  id: 7086,
  sprintVelocity: 26820,
  averageVelocity: 19874,
};

export const sampleWithFullData: ITeamVelocity = {
  id: 27606,
  sprintVelocity: 20415,
  averageVelocity: 2747,
};

export const sampleWithNewData: NewTeamVelocity = {
  sprintVelocity: 7503,
  averageVelocity: 23624,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
