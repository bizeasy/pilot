import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IStatus } from 'app/entities/status/status.model';
import { IPartyType } from 'app/entities/party-type/party-type.model';

export interface IParty {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  displayName?: string | null;
  email?: string | null;
  dob?: dayjs.Dayjs | null;
  notes?: string | null;
  mobileNumber?: string | null;
  employeeId?: string | null;
  login?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  status?: IStatus | null;
  partyType?: IPartyType | null;
}

export type NewParty = Omit<IParty, 'id'> & { id: null };
