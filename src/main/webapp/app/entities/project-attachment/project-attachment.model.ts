import { IProject } from 'app/entities/project/project.model';
import { IAttachment } from 'app/entities/attachment/attachment.model';

export interface IProjectAttachment {
  id: number;
  facility?: IProject | null;
  attachment?: IAttachment | null;
}

export type NewProjectAttachment = Omit<IProjectAttachment, 'id'> & { id: null };
