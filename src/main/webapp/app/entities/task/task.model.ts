import dayjs from 'dayjs/esm';
import { IProject } from 'app/entities/project/project.model';
import { IStatus } from 'app/entities/status/status.model';
import { IParty } from 'app/entities/party/party.model';
import { TaskPriority } from 'app/entities/enumerations/task-priority.model';

export interface ITask {
  id: number;
  title?: string | null;
  description?: string | null;
  priority?: keyof typeof TaskPriority | null;
  dueDate?: dayjs.Dayjs | null;
  info?: string | null;
  storyPoints?: number | null;
  startTime?: dayjs.Dayjs | null;
  pauseTime?: dayjs.Dayjs | null;
  endTime?: dayjs.Dayjs | null;
  totalDuration?: string | null;
  sequenceNo?: number | null;
  project?: IProject | null;
  status?: IStatus | null;
  assignee?: IParty | null;
}

export type NewTask = Omit<ITask, 'id'> & { id: null };
