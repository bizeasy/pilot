import dayjs from 'dayjs/esm';
import { IProject } from 'app/entities/project/project.model';
import { IStatus } from 'app/entities/status/status.model';

export interface ISprint {
  id: number;
  name?: string | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  goal?: string | null;
  totalPoints?: number | null;
  project?: IProject | null;
  status?: IStatus | null;
}

export type NewSprint = Omit<ISprint, 'id'> & { id: null };
