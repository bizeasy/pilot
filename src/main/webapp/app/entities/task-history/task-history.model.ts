import { IParty } from 'app/entities/party/party.model';
import { ISprint } from 'app/entities/sprint/sprint.model';

export interface ITaskHistory {
  id: number;
  type?: string | null;
  assignedTo?: IParty | null;
  sprint?: ISprint | null;
  assignedBy?: IParty | null;
}

export type NewTaskHistory = Omit<ITaskHistory, 'id'> & { id: null };
