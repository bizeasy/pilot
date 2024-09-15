import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISprintTaskHistory, NewSprintTaskHistory } from '../sprint-task-history.model';

export type PartialUpdateSprintTaskHistory = Partial<ISprintTaskHistory> & Pick<ISprintTaskHistory, 'id'>;

type RestOf<T extends ISprintTaskHistory | NewSprintTaskHistory> = Omit<T, 'fromDate' | 'toDate'> & {
  fromDate?: string | null;
  toDate?: string | null;
};

export type RestSprintTaskHistory = RestOf<ISprintTaskHistory>;

export type NewRestSprintTaskHistory = RestOf<NewSprintTaskHistory>;

export type PartialUpdateRestSprintTaskHistory = RestOf<PartialUpdateSprintTaskHistory>;

export type EntityResponseType = HttpResponse<ISprintTaskHistory>;
export type EntityArrayResponseType = HttpResponse<ISprintTaskHistory[]>;

@Injectable({ providedIn: 'root' })
export class SprintTaskHistoryService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sprint-task-histories');

  create(sprintTaskHistory: NewSprintTaskHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sprintTaskHistory);
    return this.http
      .post<RestSprintTaskHistory>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(sprintTaskHistory: ISprintTaskHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sprintTaskHistory);
    return this.http
      .put<RestSprintTaskHistory>(`${this.resourceUrl}/${this.getSprintTaskHistoryIdentifier(sprintTaskHistory)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(sprintTaskHistory: PartialUpdateSprintTaskHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sprintTaskHistory);
    return this.http
      .patch<RestSprintTaskHistory>(`${this.resourceUrl}/${this.getSprintTaskHistoryIdentifier(sprintTaskHistory)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSprintTaskHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSprintTaskHistory[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSprintTaskHistoryIdentifier(sprintTaskHistory: Pick<ISprintTaskHistory, 'id'>): number {
    return sprintTaskHistory.id;
  }

  compareSprintTaskHistory(o1: Pick<ISprintTaskHistory, 'id'> | null, o2: Pick<ISprintTaskHistory, 'id'> | null): boolean {
    return o1 && o2 ? this.getSprintTaskHistoryIdentifier(o1) === this.getSprintTaskHistoryIdentifier(o2) : o1 === o2;
  }

  addSprintTaskHistoryToCollectionIfMissing<Type extends Pick<ISprintTaskHistory, 'id'>>(
    sprintTaskHistoryCollection: Type[],
    ...sprintTaskHistoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sprintTaskHistories: Type[] = sprintTaskHistoriesToCheck.filter(isPresent);
    if (sprintTaskHistories.length > 0) {
      const sprintTaskHistoryCollectionIdentifiers = sprintTaskHistoryCollection.map(sprintTaskHistoryItem =>
        this.getSprintTaskHistoryIdentifier(sprintTaskHistoryItem),
      );
      const sprintTaskHistoriesToAdd = sprintTaskHistories.filter(sprintTaskHistoryItem => {
        const sprintTaskHistoryIdentifier = this.getSprintTaskHistoryIdentifier(sprintTaskHistoryItem);
        if (sprintTaskHistoryCollectionIdentifiers.includes(sprintTaskHistoryIdentifier)) {
          return false;
        }
        sprintTaskHistoryCollectionIdentifiers.push(sprintTaskHistoryIdentifier);
        return true;
      });
      return [...sprintTaskHistoriesToAdd, ...sprintTaskHistoryCollection];
    }
    return sprintTaskHistoryCollection;
  }

  protected convertDateFromClient<T extends ISprintTaskHistory | NewSprintTaskHistory | PartialUpdateSprintTaskHistory>(
    sprintTaskHistory: T,
  ): RestOf<T> {
    return {
      ...sprintTaskHistory,
      fromDate: sprintTaskHistory.fromDate?.format(DATE_FORMAT) ?? null,
      toDate: sprintTaskHistory.toDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restSprintTaskHistory: RestSprintTaskHistory): ISprintTaskHistory {
    return {
      ...restSprintTaskHistory,
      fromDate: restSprintTaskHistory.fromDate ? dayjs(restSprintTaskHistory.fromDate) : undefined,
      toDate: restSprintTaskHistory.toDate ? dayjs(restSprintTaskHistory.toDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSprintTaskHistory>): HttpResponse<ISprintTaskHistory> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSprintTaskHistory[]>): HttpResponse<ISprintTaskHistory[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
