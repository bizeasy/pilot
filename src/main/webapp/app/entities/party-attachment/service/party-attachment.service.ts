import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPartyAttachment, NewPartyAttachment } from '../party-attachment.model';

export type PartialUpdatePartyAttachment = Partial<IPartyAttachment> & Pick<IPartyAttachment, 'id'>;

export type EntityResponseType = HttpResponse<IPartyAttachment>;
export type EntityArrayResponseType = HttpResponse<IPartyAttachment[]>;

@Injectable({ providedIn: 'root' })
export class PartyAttachmentService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/party-attachments');

  create(partyAttachment: NewPartyAttachment): Observable<EntityResponseType> {
    return this.http.post<IPartyAttachment>(this.resourceUrl, partyAttachment, { observe: 'response' });
  }

  update(partyAttachment: IPartyAttachment): Observable<EntityResponseType> {
    return this.http.put<IPartyAttachment>(`${this.resourceUrl}/${this.getPartyAttachmentIdentifier(partyAttachment)}`, partyAttachment, {
      observe: 'response',
    });
  }

  partialUpdate(partyAttachment: PartialUpdatePartyAttachment): Observable<EntityResponseType> {
    return this.http.patch<IPartyAttachment>(`${this.resourceUrl}/${this.getPartyAttachmentIdentifier(partyAttachment)}`, partyAttachment, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPartyAttachment>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPartyAttachment[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPartyAttachmentIdentifier(partyAttachment: Pick<IPartyAttachment, 'id'>): number {
    return partyAttachment.id;
  }

  comparePartyAttachment(o1: Pick<IPartyAttachment, 'id'> | null, o2: Pick<IPartyAttachment, 'id'> | null): boolean {
    return o1 && o2 ? this.getPartyAttachmentIdentifier(o1) === this.getPartyAttachmentIdentifier(o2) : o1 === o2;
  }

  addPartyAttachmentToCollectionIfMissing<Type extends Pick<IPartyAttachment, 'id'>>(
    partyAttachmentCollection: Type[],
    ...partyAttachmentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const partyAttachments: Type[] = partyAttachmentsToCheck.filter(isPresent);
    if (partyAttachments.length > 0) {
      const partyAttachmentCollectionIdentifiers = partyAttachmentCollection.map(partyAttachmentItem =>
        this.getPartyAttachmentIdentifier(partyAttachmentItem),
      );
      const partyAttachmentsToAdd = partyAttachments.filter(partyAttachmentItem => {
        const partyAttachmentIdentifier = this.getPartyAttachmentIdentifier(partyAttachmentItem);
        if (partyAttachmentCollectionIdentifiers.includes(partyAttachmentIdentifier)) {
          return false;
        }
        partyAttachmentCollectionIdentifiers.push(partyAttachmentIdentifier);
        return true;
      });
      return [...partyAttachmentsToAdd, ...partyAttachmentCollection];
    }
    return partyAttachmentCollection;
  }
}
