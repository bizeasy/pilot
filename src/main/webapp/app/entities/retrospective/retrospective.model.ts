import dayjs from 'dayjs/esm';
import { ISprint } from 'app/entities/sprint/sprint.model';

export interface IRetrospective {
  id: number;
  summary?: string | null;
  actionItems?: string | null;
  dateCreated?: dayjs.Dayjs | null;
  sprint?: ISprint | null;
}

export type NewRetrospective = Omit<IRetrospective, 'id'> & { id: null };
