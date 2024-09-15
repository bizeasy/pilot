import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRetrospective, NewRetrospective } from '../retrospective.model';

export type PartialUpdateRetrospective = Partial<IRetrospective> & Pick<IRetrospective, 'id'>;

type RestOf<T extends IRetrospective | NewRetrospective> = Omit<T, 'dateCreated'> & {
  dateCreated?: string | null;
};

export type RestRetrospective = RestOf<IRetrospective>;

export type NewRestRetrospective = RestOf<NewRetrospective>;

export type PartialUpdateRestRetrospective = RestOf<PartialUpdateRetrospective>;

export type EntityResponseType = HttpResponse<IRetrospective>;
export type EntityArrayResponseType = HttpResponse<IRetrospective[]>;

@Injectable({ providedIn: 'root' })
export class RetrospectiveService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/retrospectives');

  create(retrospective: NewRetrospective): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(retrospective);
    return this.http
      .post<RestRetrospective>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(retrospective: IRetrospective): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(retrospective);
    return this.http
      .put<RestRetrospective>(`${this.resourceUrl}/${this.getRetrospectiveIdentifier(retrospective)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(retrospective: PartialUpdateRetrospective): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(retrospective);
    return this.http
      .patch<RestRetrospective>(`${this.resourceUrl}/${this.getRetrospectiveIdentifier(retrospective)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestRetrospective>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRetrospective[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getRetrospectiveIdentifier(retrospective: Pick<IRetrospective, 'id'>): number {
    return retrospective.id;
  }

  compareRetrospective(o1: Pick<IRetrospective, 'id'> | null, o2: Pick<IRetrospective, 'id'> | null): boolean {
    return o1 && o2 ? this.getRetrospectiveIdentifier(o1) === this.getRetrospectiveIdentifier(o2) : o1 === o2;
  }

  addRetrospectiveToCollectionIfMissing<Type extends Pick<IRetrospective, 'id'>>(
    retrospectiveCollection: Type[],
    ...retrospectivesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const retrospectives: Type[] = retrospectivesToCheck.filter(isPresent);
    if (retrospectives.length > 0) {
      const retrospectiveCollectionIdentifiers = retrospectiveCollection.map(retrospectiveItem =>
        this.getRetrospectiveIdentifier(retrospectiveItem),
      );
      const retrospectivesToAdd = retrospectives.filter(retrospectiveItem => {
        const retrospectiveIdentifier = this.getRetrospectiveIdentifier(retrospectiveItem);
        if (retrospectiveCollectionIdentifiers.includes(retrospectiveIdentifier)) {
          return false;
        }
        retrospectiveCollectionIdentifiers.push(retrospectiveIdentifier);
        return true;
      });
      return [...retrospectivesToAdd, ...retrospectiveCollection];
    }
    return retrospectiveCollection;
  }

  protected convertDateFromClient<T extends IRetrospective | NewRetrospective | PartialUpdateRetrospective>(retrospective: T): RestOf<T> {
    return {
      ...retrospective,
      dateCreated: retrospective.dateCreated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restRetrospective: RestRetrospective): IRetrospective {
    return {
      ...restRetrospective,
      dateCreated: restRetrospective.dateCreated ? dayjs(restRetrospective.dateCreated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestRetrospective>): HttpResponse<IRetrospective> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestRetrospective[]>): HttpResponse<IRetrospective[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
