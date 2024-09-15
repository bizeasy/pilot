import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISprintTask, NewSprintTask } from '../sprint-task.model';

export type PartialUpdateSprintTask = Partial<ISprintTask> & Pick<ISprintTask, 'id'>;

type RestOf<T extends ISprintTask | NewSprintTask> = Omit<T, 'fromTime' | 'thruTime' | 'assignedTime'> & {
  fromTime?: string | null;
  thruTime?: string | null;
  assignedTime?: string | null;
};

export type RestSprintTask = RestOf<ISprintTask>;

export type NewRestSprintTask = RestOf<NewSprintTask>;

export type PartialUpdateRestSprintTask = RestOf<PartialUpdateSprintTask>;

export type EntityResponseType = HttpResponse<ISprintTask>;
export type EntityArrayResponseType = HttpResponse<ISprintTask[]>;

@Injectable({ providedIn: 'root' })
export class SprintTaskService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sprint-tasks');

  create(sprintTask: NewSprintTask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sprintTask);
    return this.http
      .post<RestSprintTask>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(sprintTask: ISprintTask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sprintTask);
    return this.http
      .put<RestSprintTask>(`${this.resourceUrl}/${this.getSprintTaskIdentifier(sprintTask)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(sprintTask: PartialUpdateSprintTask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sprintTask);
    return this.http
      .patch<RestSprintTask>(`${this.resourceUrl}/${this.getSprintTaskIdentifier(sprintTask)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSprintTask>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSprintTask[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSprintTaskIdentifier(sprintTask: Pick<ISprintTask, 'id'>): number {
    return sprintTask.id;
  }

  compareSprintTask(o1: Pick<ISprintTask, 'id'> | null, o2: Pick<ISprintTask, 'id'> | null): boolean {
    return o1 && o2 ? this.getSprintTaskIdentifier(o1) === this.getSprintTaskIdentifier(o2) : o1 === o2;
  }

  addSprintTaskToCollectionIfMissing<Type extends Pick<ISprintTask, 'id'>>(
    sprintTaskCollection: Type[],
    ...sprintTasksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sprintTasks: Type[] = sprintTasksToCheck.filter(isPresent);
    if (sprintTasks.length > 0) {
      const sprintTaskCollectionIdentifiers = sprintTaskCollection.map(sprintTaskItem => this.getSprintTaskIdentifier(sprintTaskItem));
      const sprintTasksToAdd = sprintTasks.filter(sprintTaskItem => {
        const sprintTaskIdentifier = this.getSprintTaskIdentifier(sprintTaskItem);
        if (sprintTaskCollectionIdentifiers.includes(sprintTaskIdentifier)) {
          return false;
        }
        sprintTaskCollectionIdentifiers.push(sprintTaskIdentifier);
        return true;
      });
      return [...sprintTasksToAdd, ...sprintTaskCollection];
    }
    return sprintTaskCollection;
  }

  protected convertDateFromClient<T extends ISprintTask | NewSprintTask | PartialUpdateSprintTask>(sprintTask: T): RestOf<T> {
    return {
      ...sprintTask,
      fromTime: sprintTask.fromTime?.toJSON() ?? null,
      thruTime: sprintTask.thruTime?.toJSON() ?? null,
      assignedTime: sprintTask.assignedTime?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restSprintTask: RestSprintTask): ISprintTask {
    return {
      ...restSprintTask,
      fromTime: restSprintTask.fromTime ? dayjs(restSprintTask.fromTime) : undefined,
      thruTime: restSprintTask.thruTime ? dayjs(restSprintTask.thruTime) : undefined,
      assignedTime: restSprintTask.assignedTime ? dayjs(restSprintTask.assignedTime) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSprintTask>): HttpResponse<ISprintTask> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSprintTask[]>): HttpResponse<ISprintTask[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
