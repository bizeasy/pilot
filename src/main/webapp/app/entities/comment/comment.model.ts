import { ITask } from 'app/entities/task/task.model';

export interface IComment {
  id: number;
  text?: string | null;
  task?: ITask | null;
}

export type NewComment = Omit<IComment, 'id'> & { id: null };
