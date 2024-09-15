import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITaskAttachment, NewTaskAttachment } from '../task-attachment.model';

export type PartialUpdateTaskAttachment = Partial<ITaskAttachment> & Pick<ITaskAttachment, 'id'>;

export type EntityResponseType = HttpResponse<ITaskAttachment>;
export type EntityArrayResponseType = HttpResponse<ITaskAttachment[]>;

@Injectable({ providedIn: 'root' })
export class TaskAttachmentService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/task-attachments');

  create(taskAttachment: NewTaskAttachment): Observable<EntityResponseType> {
    return this.http.post<ITaskAttachment>(this.resourceUrl, taskAttachment, { observe: 'response' });
  }

  update(taskAttachment: ITaskAttachment): Observable<EntityResponseType> {
    return this.http.put<ITaskAttachment>(`${this.resourceUrl}/${this.getTaskAttachmentIdentifier(taskAttachment)}`, taskAttachment, {
      observe: 'response',
    });
  }

  partialUpdate(taskAttachment: PartialUpdateTaskAttachment): Observable<EntityResponseType> {
    return this.http.patch<ITaskAttachment>(`${this.resourceUrl}/${this.getTaskAttachmentIdentifier(taskAttachment)}`, taskAttachment, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITaskAttachment>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITaskAttachment[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTaskAttachmentIdentifier(taskAttachment: Pick<ITaskAttachment, 'id'>): number {
    return taskAttachment.id;
  }

  compareTaskAttachment(o1: Pick<ITaskAttachment, 'id'> | null, o2: Pick<ITaskAttachment, 'id'> | null): boolean {
    return o1 && o2 ? this.getTaskAttachmentIdentifier(o1) === this.getTaskAttachmentIdentifier(o2) : o1 === o2;
  }

  addTaskAttachmentToCollectionIfMissing<Type extends Pick<ITaskAttachment, 'id'>>(
    taskAttachmentCollection: Type[],
    ...taskAttachmentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const taskAttachments: Type[] = taskAttachmentsToCheck.filter(isPresent);
    if (taskAttachments.length > 0) {
      const taskAttachmentCollectionIdentifiers = taskAttachmentCollection.map(taskAttachmentItem =>
        this.getTaskAttachmentIdentifier(taskAttachmentItem),
      );
      const taskAttachmentsToAdd = taskAttachments.filter(taskAttachmentItem => {
        const taskAttachmentIdentifier = this.getTaskAttachmentIdentifier(taskAttachmentItem);
        if (taskAttachmentCollectionIdentifiers.includes(taskAttachmentIdentifier)) {
          return false;
        }
        taskAttachmentCollectionIdentifiers.push(taskAttachmentIdentifier);
        return true;
      });
      return [...taskAttachmentsToAdd, ...taskAttachmentCollection];
    }
    return taskAttachmentCollection;
  }
}
