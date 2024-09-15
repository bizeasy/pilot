import dayjs from 'dayjs/esm';
import { IStatus } from 'app/entities/status/status.model';

export interface ISprintTaskHistory {
  id: number;
  comments?: string | null;
  fromDate?: dayjs.Dayjs | null;
  toDate?: dayjs.Dayjs | null;
  fromStatus?: IStatus | null;
  toStatus?: IStatus | null;
}

export type NewSprintTaskHistory = Omit<ISprintTaskHistory, 'id'> & { id: null };
