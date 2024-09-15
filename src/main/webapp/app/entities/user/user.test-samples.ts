import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 21384,
  login: 'do',
};

export const sampleWithPartialData: IUser = {
  id: 21216,
  login: 'Bp',
};

export const sampleWithFullData: IUser = {
  id: 23754,
  login: 'VM',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
