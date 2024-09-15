import { ISprint } from 'app/entities/sprint/sprint.model';
import { IAttachment } from 'app/entities/attachment/attachment.model';

export interface ISprintAttachment {
  id: number;
  sprint?: ISprint | null;
  attachment?: IAttachment | null;
}

export type NewSprintAttachment = Omit<ISprintAttachment, 'id'> & { id: null };
