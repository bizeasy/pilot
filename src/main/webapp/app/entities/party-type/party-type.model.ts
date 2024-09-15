export interface IPartyType {
  id: number;
  name?: string | null;
  description?: string | null;
}

export type NewPartyType = Omit<IPartyType, 'id'> & { id: null };
