import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: 'f6e38b27-451a-4837-835f-c84eb77dedf2',
};

export const sampleWithPartialData: IAuthority = {
  name: '601edc22-93c0-4348-b710-e127d8805ce2',
};

export const sampleWithFullData: IAuthority = {
  name: '0b9805c2-96be-438c-b8af-0e0800cf341c',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
