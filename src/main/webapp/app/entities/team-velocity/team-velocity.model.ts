import { ISprint } from 'app/entities/sprint/sprint.model';

export interface ITeamVelocity {
  id: number;
  sprintVelocity?: number | null;
  averageVelocity?: number | null;
  sprint?: ISprint | null;
}

export type NewTeamVelocity = Omit<ITeamVelocity, 'id'> & { id: null };
