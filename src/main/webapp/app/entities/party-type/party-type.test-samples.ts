import { IPartyType, NewPartyType } from './party-type.model';

export const sampleWithRequiredData: IPartyType = {
  id: 24214,
};

export const sampleWithPartialData: IPartyType = {
  id: 16598,
  name: 'insistent after',
};

export const sampleWithFullData: IPartyType = {
  id: 16377,
  name: 'usefully whether',
  description: 'in',
};

export const sampleWithNewData: NewPartyType = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
