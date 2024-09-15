import { IPartyAttachment, NewPartyAttachment } from './party-attachment.model';

export const sampleWithRequiredData: IPartyAttachment = {
  id: 30561,
};

export const sampleWithPartialData: IPartyAttachment = {
  id: 26230,
};

export const sampleWithFullData: IPartyAttachment = {
  id: 13299,
};

export const sampleWithNewData: NewPartyAttachment = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
