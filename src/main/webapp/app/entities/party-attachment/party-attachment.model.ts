import { IParty } from 'app/entities/party/party.model';
import { IAttachment } from 'app/entities/attachment/attachment.model';

export interface IPartyAttachment {
  id: number;
  party?: IParty | null;
  attachment?: IAttachment | null;
}

export type NewPartyAttachment = Omit<IPartyAttachment, 'id'> & { id: null };
