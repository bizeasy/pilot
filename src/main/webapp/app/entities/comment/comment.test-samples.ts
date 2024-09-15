import { IComment, NewComment } from './comment.model';

export const sampleWithRequiredData: IComment = {
  id: 32538,
  text: 'transmogrify',
};

export const sampleWithPartialData: IComment = {
  id: 22620,
  text: 'amid',
};

export const sampleWithFullData: IComment = {
  id: 9653,
  text: 'excepting',
};

export const sampleWithNewData: NewComment = {
  text: 'what unexpectedly who',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
