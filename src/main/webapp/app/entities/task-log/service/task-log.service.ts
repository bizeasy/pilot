import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITaskLog, NewTaskLog } from '../task-log.model';

export type PartialUpdateTaskLog = Partial<ITaskLog> & Pick<ITaskLog, 'id'>;

type RestOf<T extends ITaskLog | NewTaskLog> = Omit<T, 'fromTime' | 'toTime'> & {
  fromTime?: string | null;
  toTime?: string | null;
};

export type RestTaskLog = RestOf<ITaskLog>;

export type NewRestTaskLog = RestOf<NewTaskLog>;

export type PartialUpdateRestTaskLog = RestOf<PartialUpdateTaskLog>;

export type EntityResponseType = HttpResponse<ITaskLog>;
export type EntityArrayResponseType = HttpResponse<ITaskLog[]>;

@Injectable({ providedIn: 'root' })
export class TaskLogService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/task-logs');

  create(taskLog: NewTaskLog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(taskLog);
    return this.http
      .post<RestTaskLog>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(taskLog: ITaskLog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(taskLog);
    return this.http
      .put<RestTaskLog>(`${this.resourceUrl}/${this.getTaskLogIdentifier(taskLog)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(taskLog: PartialUpdateTaskLog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(taskLog);
    return this.http
      .patch<RestTaskLog>(`${this.resourceUrl}/${this.getTaskLogIdentifier(taskLog)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTaskLog>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTaskLog[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTaskLogIdentifier(taskLog: Pick<ITaskLog, 'id'>): number {
    return taskLog.id;
  }

  compareTaskLog(o1: Pick<ITaskLog, 'id'> | null, o2: Pick<ITaskLog, 'id'> | null): boolean {
    return o1 && o2 ? this.getTaskLogIdentifier(o1) === this.getTaskLogIdentifier(o2) : o1 === o2;
  }

  addTaskLogToCollectionIfMissing<Type extends Pick<ITaskLog, 'id'>>(
    taskLogCollection: Type[],
    ...taskLogsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const taskLogs: Type[] = taskLogsToCheck.filter(isPresent);
    if (taskLogs.length > 0) {
      const taskLogCollectionIdentifiers = taskLogCollection.map(taskLogItem => this.getTaskLogIdentifier(taskLogItem));
      const taskLogsToAdd = taskLogs.filter(taskLogItem => {
        const taskLogIdentifier = this.getTaskLogIdentifier(taskLogItem);
        if (taskLogCollectionIdentifiers.includes(taskLogIdentifier)) {
          return false;
        }
        taskLogCollectionIdentifiers.push(taskLogIdentifier);
        return true;
      });
      return [...taskLogsToAdd, ...taskLogCollection];
    }
    return taskLogCollection;
  }

  protected convertDateFromClient<T extends ITaskLog | NewTaskLog | PartialUpdateTaskLog>(taskLog: T): RestOf<T> {
    return {
      ...taskLog,
      fromTime: taskLog.fromTime?.toJSON() ?? null,
      toTime: taskLog.toTime?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTaskLog: RestTaskLog): ITaskLog {
    return {
      ...restTaskLog,
      fromTime: restTaskLog.fromTime ? dayjs(restTaskLog.fromTime) : undefined,
      toTime: restTaskLog.toTime ? dayjs(restTaskLog.toTime) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTaskLog>): HttpResponse<ITaskLog> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTaskLog[]>): HttpResponse<ITaskLog[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
