import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProjectAttachment, NewProjectAttachment } from '../project-attachment.model';

export type PartialUpdateProjectAttachment = Partial<IProjectAttachment> & Pick<IProjectAttachment, 'id'>;

export type EntityResponseType = HttpResponse<IProjectAttachment>;
export type EntityArrayResponseType = HttpResponse<IProjectAttachment[]>;

@Injectable({ providedIn: 'root' })
export class ProjectAttachmentService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/project-attachments');

  create(projectAttachment: NewProjectAttachment): Observable<EntityResponseType> {
    return this.http.post<IProjectAttachment>(this.resourceUrl, projectAttachment, { observe: 'response' });
  }

  update(projectAttachment: IProjectAttachment): Observable<EntityResponseType> {
    return this.http.put<IProjectAttachment>(
      `${this.resourceUrl}/${this.getProjectAttachmentIdentifier(projectAttachment)}`,
      projectAttachment,
      { observe: 'response' },
    );
  }

  partialUpdate(projectAttachment: PartialUpdateProjectAttachment): Observable<EntityResponseType> {
    return this.http.patch<IProjectAttachment>(
      `${this.resourceUrl}/${this.getProjectAttachmentIdentifier(projectAttachment)}`,
      projectAttachment,
      { observe: 'response' },
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProjectAttachment>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProjectAttachment[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProjectAttachmentIdentifier(projectAttachment: Pick<IProjectAttachment, 'id'>): number {
    return projectAttachment.id;
  }

  compareProjectAttachment(o1: Pick<IProjectAttachment, 'id'> | null, o2: Pick<IProjectAttachment, 'id'> | null): boolean {
    return o1 && o2 ? this.getProjectAttachmentIdentifier(o1) === this.getProjectAttachmentIdentifier(o2) : o1 === o2;
  }

  addProjectAttachmentToCollectionIfMissing<Type extends Pick<IProjectAttachment, 'id'>>(
    projectAttachmentCollection: Type[],
    ...projectAttachmentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const projectAttachments: Type[] = projectAttachmentsToCheck.filter(isPresent);
    if (projectAttachments.length > 0) {
      const projectAttachmentCollectionIdentifiers = projectAttachmentCollection.map(projectAttachmentItem =>
        this.getProjectAttachmentIdentifier(projectAttachmentItem),
      );
      const projectAttachmentsToAdd = projectAttachments.filter(projectAttachmentItem => {
        const projectAttachmentIdentifier = this.getProjectAttachmentIdentifier(projectAttachmentItem);
        if (projectAttachmentCollectionIdentifiers.includes(projectAttachmentIdentifier)) {
          return false;
        }
        projectAttachmentCollectionIdentifiers.push(projectAttachmentIdentifier);
        return true;
      });
      return [...projectAttachmentsToAdd, ...projectAttachmentCollection];
    }
    return projectAttachmentCollection;
  }
}
