import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITaskHistory, NewTaskHistory } from '../task-history.model';

export type PartialUpdateTaskHistory = Partial<ITaskHistory> & Pick<ITaskHistory, 'id'>;

export type EntityResponseType = HttpResponse<ITaskHistory>;
export type EntityArrayResponseType = HttpResponse<ITaskHistory[]>;

@Injectable({ providedIn: 'root' })
export class TaskHistoryService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/task-histories');

  create(taskHistory: NewTaskHistory): Observable<EntityResponseType> {
    return this.http.post<ITaskHistory>(this.resourceUrl, taskHistory, { observe: 'response' });
  }

  update(taskHistory: ITaskHistory): Observable<EntityResponseType> {
    return this.http.put<ITaskHistory>(`${this.resourceUrl}/${this.getTaskHistoryIdentifier(taskHistory)}`, taskHistory, {
      observe: 'response',
    });
  }

  partialUpdate(taskHistory: PartialUpdateTaskHistory): Observable<EntityResponseType> {
    return this.http.patch<ITaskHistory>(`${this.resourceUrl}/${this.getTaskHistoryIdentifier(taskHistory)}`, taskHistory, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITaskHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITaskHistory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTaskHistoryIdentifier(taskHistory: Pick<ITaskHistory, 'id'>): number {
    return taskHistory.id;
  }

  compareTaskHistory(o1: Pick<ITaskHistory, 'id'> | null, o2: Pick<ITaskHistory, 'id'> | null): boolean {
    return o1 && o2 ? this.getTaskHistoryIdentifier(o1) === this.getTaskHistoryIdentifier(o2) : o1 === o2;
  }

  addTaskHistoryToCollectionIfMissing<Type extends Pick<ITaskHistory, 'id'>>(
    taskHistoryCollection: Type[],
    ...taskHistoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const taskHistories: Type[] = taskHistoriesToCheck.filter(isPresent);
    if (taskHistories.length > 0) {
      const taskHistoryCollectionIdentifiers = taskHistoryCollection.map(taskHistoryItem => this.getTaskHistoryIdentifier(taskHistoryItem));
      const taskHistoriesToAdd = taskHistories.filter(taskHistoryItem => {
        const taskHistoryIdentifier = this.getTaskHistoryIdentifier(taskHistoryItem);
        if (taskHistoryCollectionIdentifiers.includes(taskHistoryIdentifier)) {
          return false;
        }
        taskHistoryCollectionIdentifiers.push(taskHistoryIdentifier);
        return true;
      });
      return [...taskHistoriesToAdd, ...taskHistoryCollection];
    }
    return taskHistoryCollection;
  }
}
