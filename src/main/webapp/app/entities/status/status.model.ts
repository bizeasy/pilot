import { IStatusCategory } from 'app/entities/status-category/status-category.model';

export interface IStatus {
  id: number;
  name?: string | null;
  sequenceNo?: number | null;
  description?: string | null;
  type?: string | null;
  category?: IStatusCategory | null;
}

export type NewStatus = Omit<IStatus, 'id'> & { id: null };
