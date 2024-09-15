import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IParty, NewParty } from '../party.model';

export type PartialUpdateParty = Partial<IParty> & Pick<IParty, 'id'>;

type RestOf<T extends IParty | NewParty> = Omit<T, 'dob'> & {
  dob?: string | null;
};

export type RestParty = RestOf<IParty>;

export type NewRestParty = RestOf<NewParty>;

export type PartialUpdateRestParty = RestOf<PartialUpdateParty>;

export type EntityResponseType = HttpResponse<IParty>;
export type EntityArrayResponseType = HttpResponse<IParty[]>;

@Injectable({ providedIn: 'root' })
export class PartyService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/parties');

  create(party: NewParty): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(party);
    return this.http.post<RestParty>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(party: IParty): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(party);
    return this.http
      .put<RestParty>(`${this.resourceUrl}/${this.getPartyIdentifier(party)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(party: PartialUpdateParty): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(party);
    return this.http
      .patch<RestParty>(`${this.resourceUrl}/${this.getPartyIdentifier(party)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestParty>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestParty[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPartyIdentifier(party: Pick<IParty, 'id'>): number {
    return party.id;
  }

  compareParty(o1: Pick<IParty, 'id'> | null, o2: Pick<IParty, 'id'> | null): boolean {
    return o1 && o2 ? this.getPartyIdentifier(o1) === this.getPartyIdentifier(o2) : o1 === o2;
  }

  addPartyToCollectionIfMissing<Type extends Pick<IParty, 'id'>>(
    partyCollection: Type[],
    ...partiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const parties: Type[] = partiesToCheck.filter(isPresent);
    if (parties.length > 0) {
      const partyCollectionIdentifiers = partyCollection.map(partyItem => this.getPartyIdentifier(partyItem));
      const partiesToAdd = parties.filter(partyItem => {
        const partyIdentifier = this.getPartyIdentifier(partyItem);
        if (partyCollectionIdentifiers.includes(partyIdentifier)) {
          return false;
        }
        partyCollectionIdentifiers.push(partyIdentifier);
        return true;
      });
      return [...partiesToAdd, ...partyCollection];
    }
    return partyCollection;
  }

  protected convertDateFromClient<T extends IParty | NewParty | PartialUpdateParty>(party: T): RestOf<T> {
    return {
      ...party,
      dob: party.dob?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restParty: RestParty): IParty {
    return {
      ...restParty,
      dob: restParty.dob ? dayjs(restParty.dob) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestParty>): HttpResponse<IParty> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestParty[]>): HttpResponse<IParty[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
