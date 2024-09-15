import dayjs from 'dayjs/esm';
import { ITask } from 'app/entities/task/task.model';

export interface ITaskLog {
  id: number;
  comments?: string | null;
  fromTime?: dayjs.Dayjs | null;
  toTime?: dayjs.Dayjs | null;
  task?: ITask | null;
}

export type NewTaskLog = Omit<ITaskLog, 'id'> & { id: null };
