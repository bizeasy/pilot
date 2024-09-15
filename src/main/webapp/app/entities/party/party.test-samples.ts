import dayjs from 'dayjs/esm';

import { IParty, NewParty } from './party.model';

export const sampleWithRequiredData: IParty = {
  id: 24487,
  mobileNumber: 'victoriously',
};

export const sampleWithPartialData: IParty = {
  id: 18104,
  lastName: 'Lockman',
  displayName: 'merrily aside boastfully',
  email: 'Dewayne_Hermiston4@hotmail.com',
  mobileNumber: 'warmlyXXXX',
};

export const sampleWithFullData: IParty = {
  id: 12111,
  firstName: 'Gail',
  lastName: 'Dickens',
  displayName: 'usually concerning',
  email: 'Cole.Mayer87@gmail.com',
  dob: dayjs('2024-09-15'),
  notes: 'meow',
  mobileNumber: 'duff wound',
  employeeId: 'where meh',
  login: 'concerning promptly',
};

export const sampleWithNewData: NewParty = {
  mobileNumber: 'seldom provide',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
