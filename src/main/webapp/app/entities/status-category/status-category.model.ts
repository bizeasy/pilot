export interface IStatusCategory {
  id: number;
  name?: string | null;
  description?: string | null;
}

export type NewStatusCategory = Omit<IStatusCategory, 'id'> & { id: null };
