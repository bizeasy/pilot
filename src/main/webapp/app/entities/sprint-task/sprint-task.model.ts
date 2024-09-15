import dayjs from 'dayjs/esm';
import { ITask } from 'app/entities/task/task.model';
import { ISprint } from 'app/entities/sprint/sprint.model';
import { IParty } from 'app/entities/party/party.model';
import { IStatus } from 'app/entities/status/status.model';

export interface ISprintTask {
  id: number;
  sequenceNo?: number | null;
  storyPoints?: number | null;
  fromTime?: dayjs.Dayjs | null;
  thruTime?: dayjs.Dayjs | null;
  assignedTime?: dayjs.Dayjs | null;
  duration?: string | null;
  task?: ITask | null;
  sprint?: ISprint | null;
  assignedTo?: IParty | null;
  assignedBy?: IParty | null;
  qa?: IParty | null;
  reviewedBy?: IParty | null;
  status?: IStatus | null;
}

export type NewSprintTask = Omit<ISprintTask, 'id'> & { id: null };
