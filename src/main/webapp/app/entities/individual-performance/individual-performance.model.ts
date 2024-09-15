import { IParty } from 'app/entities/party/party.model';
import { ISprint } from 'app/entities/sprint/sprint.model';

export interface IIndividualPerformance {
  id: number;
  completedTasks?: number | null;
  velocity?: number | null;
  storyPointsCompleted?: number | null;
  party?: IParty | null;
  sprint?: ISprint | null;
}

export type NewIndividualPerformance = Omit<IIndividualPerformance, 'id'> & { id: null };
