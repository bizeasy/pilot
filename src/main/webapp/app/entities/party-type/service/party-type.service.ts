import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPartyType, NewPartyType } from '../party-type.model';

export type PartialUpdatePartyType = Partial<IPartyType> & Pick<IPartyType, 'id'>;

export type EntityResponseType = HttpResponse<IPartyType>;
export type EntityArrayResponseType = HttpResponse<IPartyType[]>;

@Injectable({ providedIn: 'root' })
export class PartyTypeService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/party-types');

  create(partyType: NewPartyType): Observable<EntityResponseType> {
    return this.http.post<IPartyType>(this.resourceUrl, partyType, { observe: 'response' });
  }

  update(partyType: IPartyType): Observable<EntityResponseType> {
    return this.http.put<IPartyType>(`${this.resourceUrl}/${this.getPartyTypeIdentifier(partyType)}`, partyType, { observe: 'response' });
  }

  partialUpdate(partyType: PartialUpdatePartyType): Observable<EntityResponseType> {
    return this.http.patch<IPartyType>(`${this.resourceUrl}/${this.getPartyTypeIdentifier(partyType)}`, partyType, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPartyType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPartyType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPartyTypeIdentifier(partyType: Pick<IPartyType, 'id'>): number {
    return partyType.id;
  }

  comparePartyType(o1: Pick<IPartyType, 'id'> | null, o2: Pick<IPartyType, 'id'> | null): boolean {
    return o1 && o2 ? this.getPartyTypeIdentifier(o1) === this.getPartyTypeIdentifier(o2) : o1 === o2;
  }

  addPartyTypeToCollectionIfMissing<Type extends Pick<IPartyType, 'id'>>(
    partyTypeCollection: Type[],
    ...partyTypesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const partyTypes: Type[] = partyTypesToCheck.filter(isPresent);
    if (partyTypes.length > 0) {
      const partyTypeCollectionIdentifiers = partyTypeCollection.map(partyTypeItem => this.getPartyTypeIdentifier(partyTypeItem));
      const partyTypesToAdd = partyTypes.filter(partyTypeItem => {
        const partyTypeIdentifier = this.getPartyTypeIdentifier(partyTypeItem);
        if (partyTypeCollectionIdentifiers.includes(partyTypeIdentifier)) {
          return false;
        }
        partyTypeCollectionIdentifiers.push(partyTypeIdentifier);
        return true;
      });
      return [...partyTypesToAdd, ...partyTypeCollection];
    }
    return partyTypeCollection;
  }
}
