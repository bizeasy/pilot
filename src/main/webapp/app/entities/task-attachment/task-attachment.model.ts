import { ITask } from 'app/entities/task/task.model';
import { IAttachment } from 'app/entities/attachment/attachment.model';

export interface ITaskAttachment {
  id: number;
  task?: ITask | null;
  attachment?: IAttachment | null;
}

export type NewTaskAttachment = Omit<ITaskAttachment, 'id'> & { id: null };
