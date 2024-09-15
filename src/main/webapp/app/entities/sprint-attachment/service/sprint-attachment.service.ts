import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISprintAttachment, NewSprintAttachment } from '../sprint-attachment.model';

export type PartialUpdateSprintAttachment = Partial<ISprintAttachment> & Pick<ISprintAttachment, 'id'>;

export type EntityResponseType = HttpResponse<ISprintAttachment>;
export type EntityArrayResponseType = HttpResponse<ISprintAttachment[]>;

@Injectable({ providedIn: 'root' })
export class SprintAttachmentService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sprint-attachments');

  create(sprintAttachment: NewSprintAttachment): Observable<EntityResponseType> {
    return this.http.post<ISprintAttachment>(this.resourceUrl, sprintAttachment, { observe: 'response' });
  }

  update(sprintAttachment: ISprintAttachment): Observable<EntityResponseType> {
    return this.http.put<ISprintAttachment>(
      `${this.resourceUrl}/${this.getSprintAttachmentIdentifier(sprintAttachment)}`,
      sprintAttachment,
      { observe: 'response' },
    );
  }

  partialUpdate(sprintAttachment: PartialUpdateSprintAttachment): Observable<EntityResponseType> {
    return this.http.patch<ISprintAttachment>(
      `${this.resourceUrl}/${this.getSprintAttachmentIdentifier(sprintAttachment)}`,
      sprintAttachment,
      { observe: 'response' },
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISprintAttachment>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISprintAttachment[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSprintAttachmentIdentifier(sprintAttachment: Pick<ISprintAttachment, 'id'>): number {
    return sprintAttachment.id;
  }

  compareSprintAttachment(o1: Pick<ISprintAttachment, 'id'> | null, o2: Pick<ISprintAttachment, 'id'> | null): boolean {
    return o1 && o2 ? this.getSprintAttachmentIdentifier(o1) === this.getSprintAttachmentIdentifier(o2) : o1 === o2;
  }

  addSprintAttachmentToCollectionIfMissing<Type extends Pick<ISprintAttachment, 'id'>>(
    sprintAttachmentCollection: Type[],
    ...sprintAttachmentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sprintAttachments: Type[] = sprintAttachmentsToCheck.filter(isPresent);
    if (sprintAttachments.length > 0) {
      const sprintAttachmentCollectionIdentifiers = sprintAttachmentCollection.map(sprintAttachmentItem =>
        this.getSprintAttachmentIdentifier(sprintAttachmentItem),
      );
      const sprintAttachmentsToAdd = sprintAttachments.filter(sprintAttachmentItem => {
        const sprintAttachmentIdentifier = this.getSprintAttachmentIdentifier(sprintAttachmentItem);
        if (sprintAttachmentCollectionIdentifiers.includes(sprintAttachmentIdentifier)) {
          return false;
        }
        sprintAttachmentCollectionIdentifiers.push(sprintAttachmentIdentifier);
        return true;
      });
      return [...sprintAttachmentsToAdd, ...sprintAttachmentCollection];
    }
    return sprintAttachmentCollection;
  }
}
